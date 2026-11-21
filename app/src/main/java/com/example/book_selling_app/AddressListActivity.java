package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.AddressAdapter;
import com.example.book_selling_app.databinding.ActivityAddressListBinding;
import com.example.book_selling_app.models.Address;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.AddressViewModel;

import java.util.List;

public class AddressListActivity extends AppCompatActivity implements AddressAdapter.OnAddressActionListener {

    public static final String EXTRA_SELECT_MODE = "EXTRA_SELECT_MODE";
    public static final String RESULT_SELECTED_ADDRESS = "RESULT_SELECTED_ADDRESS";

    private ActivityAddressListBinding binding;
    private AddressViewModel addressViewModel;
    private SessionManager sessionManager;
    private AddressAdapter adapter;
    private boolean isSelectMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isSelectMode = getIntent().getBooleanExtra(EXTRA_SELECT_MODE, false);
        if (isSelectMode) {
            binding.tvAddressTitle.setText("Chọn địa chỉ nhận hàng");
        }

        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        sessionManager = new SessionManager(this);

        adapter = new AddressAdapter(isSelectMode, this);
        binding.rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAddresses.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressListActivity.this, AddEditAddressActivity.class);
            startActivity(intent);
        });

        loadAddresses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        int userId = sessionManager.getUserId();
        addressViewModel.getAddressesForUser(userId).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                List<Address> list = resource.data;
                if (list != null && !list.isEmpty()) {
                    binding.rvAddresses.setVisibility(View.VISIBLE);
                    binding.layoutEmptyAddress.setVisibility(View.GONE);
                    adapter.setAddresses(list);
                } else {
                    binding.rvAddresses.setVisibility(View.GONE);
                    binding.layoutEmptyAddress.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onSelect(Address address) {
        if (isSelectMode) {
            Intent data = new Intent();
            data.putExtra(RESULT_SELECTED_ADDRESS, address);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onEdit(Address address) {
        Intent intent = new Intent(this, AddEditAddressActivity.class);
        intent.putExtra("EXTRA_ADDRESS", address);
        startActivity(intent);
    }

    @Override
    public void onDelete(Address address) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int userId = sessionManager.getUserId();
                    addressViewModel.deleteAddress(address.getId(), userId).observe(this, resource -> {
                        if (resource != null && resource.isSuccess()) {
                            Toast.makeText(this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                            loadAddresses();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onSetDefault(Address address) {
        int userId = sessionManager.getUserId();
        addressViewModel.setDefaultAddress(address.getId(), userId).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                Toast.makeText(this, "Đã đặt làm địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                loadAddresses();
            }
        });
    }
}

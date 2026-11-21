package com.example.book_selling_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.databinding.ActivityAddEditAddressBinding;
import com.example.book_selling_app.models.Address;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.AddressViewModel;

public class AddEditAddressActivity extends AppCompatActivity {

    private ActivityAddEditAddressBinding binding;
    private AddressViewModel addressViewModel;
    private SessionManager sessionManager;
    private Address editingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        sessionManager = new SessionManager(this);

        if (getIntent().hasExtra("EXTRA_ADDRESS")) {
            editingAddress = (Address) getIntent().getSerializableExtra("EXTRA_ADDRESS");
        }

        setupUI();
        setupEvents();
    }

    private void setupUI() {
        if (editingAddress != null) {
            binding.tvAddEditTitle.setText("Chỉnh sửa địa chỉ");
            binding.edtRecipientName.setText(editingAddress.getRecipientName());
            binding.edtRecipientPhone.setText(editingAddress.getPhone());
            binding.edtStreetAddress.setText(editingAddress.getStreetAddress());
            binding.switchDefaultAddress.setChecked(editingAddress.isDefault());

            if ("Văn phòng".equalsIgnoreCase(editingAddress.getLabel())) {
                binding.rbOffice.setChecked(true);
            } else if ("Khác".equalsIgnoreCase(editingAddress.getLabel())) {
                binding.rbOther.setChecked(true);
            } else {
                binding.rbHome.setChecked(true);
            }
        } else {
            binding.tvAddEditTitle.setText("Thêm địa chỉ mới");
            // Prefill user's registered name and phone if available
            binding.edtRecipientName.setText(sessionManager.getUserName());
        }
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSaveAddress.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String name = binding.edtRecipientName.getText() != null
                ? binding.edtRecipientName.getText().toString().trim() : "";
        String phone = binding.edtRecipientPhone.getText() != null
                ? binding.edtRecipientPhone.getText().toString().trim() : "";
        String street = binding.edtStreetAddress.getText() != null
                ? binding.edtStreetAddress.getText().toString().trim() : "";

        binding.tilRecipientName.setError(null);
        binding.tilRecipientPhone.setError(null);
        binding.tilStreetAddress.setError(null);

        if (TextUtils.isEmpty(name)) {
            binding.tilRecipientName.setError("Vui lòng nhập tên người nhận");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            binding.tilRecipientPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        if (TextUtils.isEmpty(street)) {
            binding.tilStreetAddress.setError("Vui lòng nhập địa chỉ nhận hàng chi tiết");
            return;
        }

        String label = "Nhà riêng";
        if (binding.rbOffice.isChecked()) {
            label = "Văn phòng";
        } else if (binding.rbOther.isChecked()) {
            label = "Khác";
        }

        boolean isDefault = binding.switchDefaultAddress.isChecked();
        int userId = sessionManager.getUserId();

        binding.btnSaveAddress.setEnabled(false);

        if (editingAddress != null) {
            addressViewModel.updateAddress(editingAddress.getId(), userId, name, phone, street, label, isDefault)
                    .observe(this, resource -> {
                        binding.btnSaveAddress.setEnabled(true);
                        if (resource != null && resource.isSuccess()) {
                            Toast.makeText(this, "Đã cập nhật địa chỉ!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else if (resource != null && resource.isError()) {
                            Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            addressViewModel.insertAddress(userId, name, phone, street, label, isDefault)
                    .observe(this, resource -> {
                        binding.btnSaveAddress.setEnabled(true);
                        if (resource != null && resource.isSuccess()) {
                            Toast.makeText(this, "Thêm địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else if (resource != null && resource.isError()) {
                            Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

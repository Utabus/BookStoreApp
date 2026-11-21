package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.AdminOrderAdapter;
import com.example.book_selling_app.databinding.ActivityAdminOrdersBinding;
import com.example.book_selling_app.models.Order;
import com.example.book_selling_app.viewmodels.AdminOrderViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity {

    private ActivityAdminOrdersBinding binding;
    private AdminOrderViewModel orderViewModel;
    private AdminOrderAdapter adapter;
    private String currentFilter = "ALL";
    private List<Order> currentOrdersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderViewModel = new ViewModelProvider(this).get(AdminOrderViewModel.class);

        setupRecyclerView();
        setupFilterTabs();
        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders(currentFilter);
    }

    private void setupRecyclerView() {
        binding.rvAdminOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(this, new ArrayList<>(), order -> {
            Intent intent = new Intent(AdminOrdersActivity.this, AdminOrderDetailActivity.class);
            intent.putExtra("ORDER_EXTRA", order);
            startActivity(intent);
        });
        binding.rvAdminOrders.setAdapter(adapter);
    }

    private void setupFilterTabs() {
        binding.tabAll.setOnClickListener(v -> selectTab(binding.tabAll, "ALL"));
        binding.tabPending.setOnClickListener(v -> selectTab(binding.tabPending, "PENDING"));
        binding.tabConfirmed.setOnClickListener(v -> selectTab(binding.tabConfirmed, "CONFIRMED"));
        binding.tabShipping.setOnClickListener(v -> selectTab(binding.tabShipping, "SHIPPING"));
        binding.tabDelivered.setOnClickListener(v -> selectTab(binding.tabDelivered, "DELIVERED"));
        binding.tabCancelled.setOnClickListener(v -> selectTab(binding.tabCancelled, "CANCELLED"));
    }

    private void selectTab(TextView selectedView, String filter) {
        currentFilter = filter;
        TextView[] tabs = {binding.tabAll, binding.tabPending, binding.tabConfirmed, binding.tabShipping, binding.tabDelivered, binding.tabCancelled};
        for (TextView t : tabs) {
            if (t == selectedView) {
                t.setBackgroundResource(R.drawable.bg_chip_selected);
                t.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                t.setBackgroundResource(R.drawable.bg_chip_unselected);
                t.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            }
        }
        loadOrders(filter);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.edtSearchOrder.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterOrders(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.updateData(currentOrdersList);
            binding.tvAdminOrderCount.setText(currentOrdersList.size() + " đơn");
            return;
        }

        String lower = query.toLowerCase().trim();
        List<Order> filtered = new ArrayList<>();
        for (Order o : currentOrdersList) {
            String ordIdStr = String.valueOf(o.getId());
            String ordFullStr = "ord-" + ordIdStr;
            String customer = o.getCustomerName() != null ? o.getCustomerName().toLowerCase() : "";
            String phone = o.getCustomerPhone() != null ? o.getCustomerPhone() : "";
            String address = o.getShippingAddress() != null ? o.getShippingAddress().toLowerCase() : "";

            if (ordIdStr.contains(lower) || ordFullStr.contains(lower) || customer.contains(lower)
                    || phone.contains(lower) || address.contains(lower)) {
                filtered.add(o);
            }
        }
        adapter.updateData(filtered);
        binding.tvAdminOrderCount.setText(filtered.size() + " đơn");
    }

    private void loadOrders(String filter) {
        orderViewModel.getAllOrdersForAdmin(filter).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                currentOrdersList = resource.data;
                filterOrders(binding.edtSearchOrder.getText() != null ? binding.edtSearchOrder.getText().toString() : "");
            }
        });
    }
}

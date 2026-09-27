package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.databinding.ActivityAdminDashboardBinding;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.AdminDashboardViewModel;

import java.text.DecimalFormat;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private AdminDashboardViewModel dashboardViewModel;
    private SessionManager sessionManager;
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dashboardViewModel = new ViewModelProvider(this).get(AdminDashboardViewModel.class);
        sessionManager = new SessionManager(this);

        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        binding.tvAdminGreeting.setText("Xin chào, " + sessionManager.getUserName());
        dashboardViewModel.getAdminStats().observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                DatabaseHelper.AdminStats stats = resource.data;
                binding.tvStatRevenue.setText(formatter.format(stats.totalRevenue));
                binding.tvStatOrders.setText(String.valueOf(stats.totalOrders));
                binding.tvStatPendingOrders.setText(String.valueOf(stats.pendingOrders));
                binding.tvStatBooks.setText(String.valueOf(stats.totalBooks));
                binding.tvStatUsers.setText(String.valueOf(stats.totalUsers));
            }
        });
    }

    private void setupEvents() {
        binding.btnSwitchToUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            startActivity(intent);
        });

        binding.cardManageBooks.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminBooksActivity.class));
        });

        binding.cardManageCategories.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminCategoriesActivity.class));
        });

        binding.cardManageOrders.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class));
        });

        binding.cardManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class));
        });

        binding.btnAdminLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất quản trị")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi trang quản trị?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }
}

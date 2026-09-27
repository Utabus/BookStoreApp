package com.example.book_selling_app;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.AdminUserAdapter;
import com.example.book_selling_app.databinding.ActivityAdminUsersBinding;
import com.example.book_selling_app.models.User;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.AdminUserViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    private ActivityAdminUsersBinding binding;
    private AdminUserViewModel userViewModel;
    private SessionManager sessionManager;
    private AdminUserAdapter adapter;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(AdminUserViewModel.class);
        sessionManager = new SessionManager(this);

        setupRecyclerView();
        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void setupRecyclerView() {
        binding.rvAdminUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(this, new ArrayList<>(), sessionManager.getUserId(), user -> {
            confirmToggleStatus(user);
        });
        binding.rvAdminUsers.setAdapter(adapter);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void loadUsers() {
        userViewModel.getAllUsers().observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                userList = resource.data;
                adapter.updateData(userList);
                binding.tvAdminUserCount.setText(userList.size() + " người dùng");
            }
        });
    }

    private void confirmToggleStatus(User user) {
        String action = user.isActive() ? "khóa tài khoản" : "mở khóa tài khoản";
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thao tác")
                .setMessage("Bạn có chắc chắn muốn " + action + " của \"" + user.getFullName() + "\" (" + user.getEmail() + ")?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    boolean newStatus = !user.isActive();
                    userViewModel.toggleUserStatus(user.getId(), newStatus).observe(this, resource -> {
                        if (resource != null && resource.isSuccess()) {
                            user.setActive(newStatus);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "Đã cập nhật trạng thái người dùng thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

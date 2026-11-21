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

        binding.edtSearchUser.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.updateData(userList);
            binding.tvAdminUserCount.setText(userList.size() + " người dùng");
            return;
        }

        String lower = query.toLowerCase().trim();
        List<User> filtered = new ArrayList<>();
        for (User u : userList) {
            if ((u.getFullName() != null && u.getFullName().toLowerCase().contains(lower))
                    || (u.getEmail() != null && u.getEmail().toLowerCase().contains(lower))
                    || (u.getPhone() != null && u.getPhone().contains(lower))) {
                filtered.add(u);
            }
        }
        adapter.updateData(filtered);
        binding.tvAdminUserCount.setText(filtered.size() + " người dùng");
    }

    private void loadUsers() {
        userViewModel.getAllUsers().observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                userList = resource.data;
                filterUsers(binding.edtSearchUser.getText() != null ? binding.edtSearchUser.getText().toString() : "");
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

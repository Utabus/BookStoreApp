package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.book_selling_app.databinding.FragmentProfileBinding;
import com.example.book_selling_app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        setupUI();
        setupEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && sessionManager != null) {
            setupUI();
        }
    }

    private void setupUI() {
        binding.tvProfileName.setText(sessionManager.getUserName());
        binding.tvProfileEmail.setText(sessionManager.getUserEmail());

        boolean isAdmin = "ADMIN".equalsIgnoreCase(sessionManager.getUserRole());
        if (isAdmin) {
            binding.tvProfileRole.setText("Quản trị viên (Admin)");
            binding.btnMenuAdminPanel.setVisibility(View.VISIBLE);
            binding.dividerAdmin.setVisibility(View.VISIBLE);
        } else {
            binding.tvProfileRole.setText("Thành viên thân thiết");
            binding.btnMenuAdminPanel.setVisibility(View.GONE);
            binding.dividerAdmin.setVisibility(View.GONE);
        }
    }

    private void setupEvents() {
        binding.btnMenuEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        binding.btnMenuOrders.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_orders);
            }
        });

        binding.btnMenuPassword.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        binding.btnMenuAdminPanel.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AdminDashboardActivity.class);
            startActivity(intent);
        });

        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    sessionManager.logout();
                    Toast.makeText(getContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

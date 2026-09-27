package com.example.book_selling_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.databinding.ActivityEditProfileBinding;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.AuthViewModel;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        sessionManager = authViewModel.getSessionManager();

        loadUserData();
        setupEvents();
    }

    private void loadUserData() {
        binding.edtEmail.setText(sessionManager.getUserEmail());
        binding.edtFullName.setText(sessionManager.getUserName());
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSaveProfile.setOnClickListener(v -> handleSaveProfile());
    }

    private void handleSaveProfile() {
        String fullName = binding.edtFullName.getText() != null ? binding.edtFullName.getText().toString().trim() : "";
        String phone = binding.edtPhone.getText() != null ? binding.edtPhone.getText().toString().trim() : "";
        String address = binding.edtAddress.getText() != null ? binding.edtAddress.getText().toString().trim() : "";

        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError("Vui lòng nhập họ và tên");
            return;
        } else {
            binding.tilFullName.setError(null);
        }

        binding.btnSaveProfile.setEnabled(false);
        authViewModel.updateProfile(sessionManager.getUserId(), fullName, phone, address).observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    binding.btnSaveProfile.setEnabled(true);
                    sessionManager.createLoginSession(sessionManager.getUserId(), fullName, sessionManager.getUserEmail(), sessionManager.getUserRole());
                    Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ERROR:
                    binding.btnSaveProfile.setEnabled(true);
                    Toast.makeText(this, resource.message != null ? resource.message : "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}

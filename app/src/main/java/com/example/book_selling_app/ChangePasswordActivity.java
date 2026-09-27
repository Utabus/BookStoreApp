package com.example.book_selling_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.databinding.ActivityChangePasswordBinding;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.AuthViewModel;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        sessionManager = authViewModel.getSessionManager();

        setupEvents();
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnChangePassword.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String oldPass = binding.edtOldPassword.getText() != null ? binding.edtOldPassword.getText().toString().trim() : "";
        String newPass = binding.edtNewPassword.getText() != null ? binding.edtNewPassword.getText().toString().trim() : "";
        String confirmNewPass = binding.edtConfirmNewPassword.getText() != null ? binding.edtConfirmNewPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(oldPass)) {
            binding.tilOldPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        } else {
            binding.tilOldPassword.setError(null);
        }

        if (TextUtils.isEmpty(newPass)) {
            binding.tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        } else if (newPass.length() < 6) {
            binding.tilNewPassword.setError("Mật khẩu mới phải từ 6 ký tự trở lên");
            return;
        } else {
            binding.tilNewPassword.setError(null);
        }

        if (!newPass.equals(confirmNewPass)) {
            binding.tilConfirmNewPassword.setError("Mật khẩu xác nhận không trùng khớp");
            return;
        } else {
            binding.tilConfirmNewPassword.setError(null);
        }

        binding.btnChangePassword.setEnabled(false);
        authViewModel.changePassword(sessionManager.getUserId(), oldPass, newPass).observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    binding.btnChangePassword.setEnabled(true);
                    Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ERROR:
                    binding.btnChangePassword.setEnabled(true);
                    binding.tilOldPassword.setError(resource.message != null ? resource.message : "Mật khẩu hiện tại không chính xác");
                    break;
            }
        });
    }
}

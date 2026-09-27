package com.example.book_selling_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.databinding.ActivityRegisterBinding;
import com.example.book_selling_app.viewmodels.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvLoginNow.setOnClickListener(v -> finish());
        binding.btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String fullName = binding.edtFullName.getText() != null ? binding.edtFullName.getText().toString().trim() : "";
        String email = binding.edtEmail.getText() != null ? binding.edtEmail.getText().toString().trim() : "";
        String phone = binding.edtPhone.getText() != null ? binding.edtPhone.getText().toString().trim() : "";
        String password = binding.edtPassword.getText() != null ? binding.edtPassword.getText().toString().trim() : "";
        String confirmPassword = binding.edtConfirmPassword.getText() != null ? binding.edtConfirmPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError("Vui lòng nhập họ và tên");
            return;
        } else {
            binding.tilFullName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Vui lòng nhập Email");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            binding.tilPhone.setError("Vui lòng nhập số điện thoại");
            return;
        } else {
            binding.tilPhone.setError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.tilPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        binding.btnRegister.setEnabled(false);
        authViewModel.register(fullName, email, password, phone, "").observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    binding.btnRegister.setEnabled(true);
                    Toast.makeText(this, "Đăng ký tài khoản thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case ERROR:
                    binding.btnRegister.setEnabled(true);
                    Toast.makeText(this, resource.message != null ? resource.message : "Đăng ký thất bại!", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}

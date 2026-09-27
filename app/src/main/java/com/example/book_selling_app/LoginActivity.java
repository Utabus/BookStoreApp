package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.databinding.ActivityLoginBinding;
import com.example.book_selling_app.models.User;
import com.example.book_selling_app.viewmodels.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupEvents();
    }

    private void setupEvents() {
        binding.btnLogin.setOnClickListener(v -> handleLogin());

        binding.tvRegisterNow.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        binding.btnGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Đăng nhập Google đang được kết nối", Toast.LENGTH_SHORT).show();
        });

        binding.btnFacebookLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Đăng nhập Facebook đang được kết nối", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String email = binding.edtEmail.getText() != null ? binding.edtEmail.getText().toString().trim() : "";
        String password = binding.edtPassword.getText() != null ? binding.edtPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Vui lòng nhập Email hoặc Tài khoản");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Vui lòng nhập Mật khẩu");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        binding.btnLogin.setEnabled(false);
        authViewModel.login(email, password).observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    binding.btnLogin.setEnabled(true);
                    User user = resource.data;
                    if (user != null) {
                        Toast.makeText(this, "Xin chào " + user.getFullName() + "!", Toast.LENGTH_SHORT).show();
                        Intent intent;
                        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                    break;
                case ERROR:
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(this, resource.message != null ? resource.message : "Đăng nhập thất bại!", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }
}

package com.example.book_selling_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.book_selling_app.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvBackToLogin.setOnClickListener(v -> finish());

        binding.btnSubmit.setOnClickListener(v -> {
            String email = binding.edtEmail.getText() != null ? binding.edtEmail.getText().toString().trim() : "";
            if (TextUtils.isEmpty(email)) {
                binding.tilEmail.setError("Vui lòng nhập Email đã đăng ký");
                return;
            }
            binding.tilEmail.setError(null);
            Toast.makeText(this, "Đã gửi hướng dẫn khôi phục mật khẩu vào " + email, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}

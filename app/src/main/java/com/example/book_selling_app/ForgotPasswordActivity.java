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

            com.example.book_selling_app.database.DatabaseHelper dbHelper =
                    new com.example.book_selling_app.database.DatabaseHelper(this);
            boolean resetSuccess = dbHelper.resetPassword(email, "123456");
            if (resetSuccess) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Khôi phục mật khẩu thành công")
                        .setMessage("Mật khẩu cho tài khoản \"" + email + "\" đã được đặt lại về mặc định là: 123456\n\nVui lòng đăng nhập và đổi mật khẩu mới.")
                        .setPositiveButton("Đăng nhập ngay", (dialog, which) -> finish())
                        .setCancelable(false)
                        .show();
            } else {
                binding.tilEmail.setError("Email không tồn tại trong hệ thống BookStore");
            }
        });
    }
}

package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.book_selling_app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SessionManager session = new SessionManager(this);
        AppCompatDelegate.setDefaultNightMode(
            session.isDarkMode() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (session.isLoggedIn()) {
                if ("ADMIN".equalsIgnoreCase(session.getUserRole())) {
                    intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1800);
    }
}

package com.example.book_selling_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.book_selling_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if intent requests a specific tab
        int openTab = getIntent().getIntExtra("OPEN_TAB", -1);
        if (openTab != -1) {
            binding.bottomNavigation.setSelectedItemId(openTab);
        } else if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_categories) {
                replaceFragment(new CategoriesFragment());
                return true;
            } else if (id == R.id.nav_cart) {
                replaceFragment(new CartFragment());
                return true;
            } else if (id == R.id.nav_orders) {
                replaceFragment(new OrdersFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int openTab = intent.getIntExtra("OPEN_TAB", -1);
        if (openTab != -1) {
            binding.bottomNavigation.setSelectedItemId(openTab);
        }
    }

    public void navigateToTab(int menuItemId) {
        binding.bottomNavigation.setSelectedItemId(menuItemId);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.book_selling_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int pendingCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if intent requests a specific tab
        int openTab = getIntent().getIntExtra("OPEN_TAB", -1);
        int categoryId = getIntent().getIntExtra("OPEN_CATEGORY_ID", -1);

        if (categoryId != -1) {
            pendingCategoryId = categoryId;
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else if (openTab != -1) {
            binding.bottomNavigation.setSelectedItemId(openTab);
        } else if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                int catId = pendingCategoryId;
                pendingCategoryId = -1; // reset after consumption
                replaceFragment(HomeFragment.newInstance(catId));
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int categoryId = intent.getIntExtra("OPEN_CATEGORY_ID", -1);
        if (categoryId != -1) {
            navigateToCategory(categoryId);
            return;
        }
        int openTab = intent.getIntExtra("OPEN_TAB", -1);
        if (openTab != -1) {
            binding.bottomNavigation.setSelectedItemId(openTab);
        }
    }

    public void navigateToTab(int menuItemId) {
        binding.bottomNavigation.setSelectedItemId(menuItemId);
    }

    public void navigateToCategory(int categoryId) {
        this.pendingCategoryId = categoryId;
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}

package com.example.book_selling_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.AdminCategoryAdapter;
import com.example.book_selling_app.databinding.ActivityAdminCategoriesBinding;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.viewmodels.AdminCategoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoriesActivity extends AppCompatActivity {

    private ActivityAdminCategoriesBinding binding;
    private AdminCategoryViewModel categoryViewModel;
    private AdminCategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryViewModel = new ViewModelProvider(this).get(AdminCategoryViewModel.class);

        setupRecyclerView();
        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void setupRecyclerView() {
        binding.rvAdminCategories.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminCategoryAdapter(new ArrayList<>(), new AdminCategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showEditCategoryDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                confirmDeleteCategory(category);
            }
        });
        binding.rvAdminCategories.setAdapter(adapter);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadCategories() {
        categoryViewModel.getAllCategories().observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                categoryList = resource.data;
                adapter.updateData(categoryList);
                binding.tvAdminCategoryCount.setText(categoryList.size() + " thể loại");
            }
        });
    }

    private void showAddCategoryDialog() {
        final EditText input = new EditText(this);
        input.setHint("Nhập tên thể loại mới...");
        input.setPadding(40, 30, 40, 30);

        new AlertDialog.Builder(this)
                .setTitle("Thêm thể loại mới")
                .setView(input)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(name)) {
                        categoryViewModel.insertCategory(name).observe(this, resource -> {
                            if (resource != null && resource.isSuccess()) {
                                Toast.makeText(this, "Đã thêm thể loại thành công", Toast.LENGTH_SHORT).show();
                                loadCategories();
                            } else if (resource != null && resource.isError()) {
                                Toast.makeText(this, resource.message != null ? resource.message : "Thể loại này đã tồn tại!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Tên thể loại không được để trống", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditCategoryDialog(Category category) {
        final EditText input = new EditText(this);
        input.setText(category.getName());
        input.setSelection(category.getName().length());
        input.setPadding(40, 30, 40, 30);

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa thể loại")
                .setView(input)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(newName)) {
                        categoryViewModel.updateCategory(category.getId(), newName).observe(this, resource -> {
                            if (resource != null && resource.isSuccess()) {
                                Toast.makeText(this, "Cập nhật thể loại thành công", Toast.LENGTH_SHORT).show();
                                loadCategories();
                            } else if (resource != null && resource.isError()) {
                                Toast.makeText(this, resource.message != null ? resource.message : "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void confirmDeleteCategory(Category category) {
        categoryViewModel.deleteCategory(category.getId()).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                Toast.makeText(this, "Đã xóa thể loại", Toast.LENGTH_SHORT).show();
                loadCategories();
            } else if (resource != null && resource.isError()) {
                new AlertDialog.Builder(this)
                        .setTitle("Không thể xóa")
                        .setMessage(resource.message != null ? resource.message : "Không thể xóa thể loại này!")
                        .setPositiveButton("Đã hiểu", null)
                        .show();
            }
        });
    }
}

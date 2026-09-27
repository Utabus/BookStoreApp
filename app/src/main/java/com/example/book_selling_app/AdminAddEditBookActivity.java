package com.example.book_selling_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.databinding.ActivityAdminAddEditBookBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.viewmodels.AdminBookViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminAddEditBookActivity extends AppCompatActivity {

    private ActivityAdminAddEditBookBinding binding;
    private AdminBookViewModel bookViewModel;
    private Book editingBook;
    private final List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddEditBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookViewModel = new ViewModelProvider(this).get(AdminBookViewModel.class);

        if (getIntent().hasExtra("BOOK_EXTRA")) {
            editingBook = (Book) getIntent().getSerializableExtra("BOOK_EXTRA");
        }

        setupCategoriesSpinner();
        setupViewData();
        setupEvents();
    }

    private void setupCategoriesSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        binding.spBookCategory.setAdapter(spinnerAdapter);

        bookViewModel.getCategories().observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                categoryList.clear();
                categoryList.addAll(resource.data);

                List<String> names = new ArrayList<>();
                for (Category c : categoryList) {
                    names.add(c.getName());
                }

                spinnerAdapter.clear();
                spinnerAdapter.addAll(names);
                spinnerAdapter.notifyDataSetChanged();

                if (editingBook != null) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getId() == editingBook.getCategoryId()) {
                            binding.spBookCategory.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setupViewData() {
        if (editingBook != null) {
            binding.tvHeaderTitle.setText("Chỉnh sửa sách");
            binding.edtBookTitle.setText(editingBook.getTitle());
            binding.edtBookAuthor.setText(editingBook.getAuthor());
            binding.edtBookPrice.setText(String.valueOf((long) editingBook.getPrice()));
            binding.edtBookOriginalPrice.setText(String.valueOf((long) editingBook.getOriginalPrice()));
            binding.edtBookStock.setText(String.valueOf(editingBook.getStock()));
            binding.edtBookDiscount.setText(String.valueOf(editingBook.getDiscount()));
            binding.edtBookImage.setText(editingBook.getImageUrl());
            binding.edtBookDesc.setText(editingBook.getDescription());
            binding.btnSaveBook.setText("CẬP NHẬT SÁCH");
        } else {
            binding.tvHeaderTitle.setText("Thêm sách mới");
            binding.btnSaveBook.setText("THÊM VÀO KHO SÁCH");
        }
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSaveBook.setOnClickListener(v -> handleSaveBook());
    }

    private void handleSaveBook() {
        String title = binding.edtBookTitle.getText() != null ? binding.edtBookTitle.getText().toString().trim() : "";
        String author = binding.edtBookAuthor.getText() != null ? binding.edtBookAuthor.getText().toString().trim() : "";
        String priceStr = binding.edtBookPrice.getText() != null ? binding.edtBookPrice.getText().toString().trim() : "";
        String origPriceStr = binding.edtBookOriginalPrice.getText() != null ? binding.edtBookOriginalPrice.getText().toString().trim() : "";
        String stockStr = binding.edtBookStock.getText() != null ? binding.edtBookStock.getText().toString().trim() : "";
        String discountStr = binding.edtBookDiscount.getText() != null ? binding.edtBookDiscount.getText().toString().trim() : "";
        String image = binding.edtBookImage.getText() != null ? binding.edtBookImage.getText().toString().trim() : "";
        String desc = binding.edtBookDesc.getText() != null ? binding.edtBookDesc.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) {
            binding.tilBookTitle.setError("Vui lòng nhập tên sách");
            return;
        } else {
            binding.tilBookTitle.setError(null);
        }

        if (TextUtils.isEmpty(author)) {
            binding.tilBookAuthor.setError("Vui lòng nhập tác giả");
            return;
        } else {
            binding.tilBookAuthor.setError(null);
        }

        if (TextUtils.isEmpty(priceStr)) {
            binding.tilBookPrice.setError("Vui lòng nhập giá bán");
            return;
        } else {
            binding.tilBookPrice.setError(null);
        }

        double price = Double.parseDouble(priceStr);
        double origPrice = TextUtils.isEmpty(origPriceStr) ? price : Double.parseDouble(origPriceStr);
        int stock = TextUtils.isEmpty(stockStr) ? 10 : Integer.parseInt(stockStr);
        int discount = TextUtils.isEmpty(discountStr) ? 0 : Integer.parseInt(discountStr);

        if (categoryList.isEmpty()) {
            Toast.makeText(this, "Chưa có thể loại sách nào trong hệ thống!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex = binding.spBookCategory.getSelectedItemPosition();
        int catId = categoryList.get(selectedIndex >= 0 ? selectedIndex : 0).getId();

        if (TextUtils.isEmpty(image)) {
            image = "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400";
        }

        binding.btnSaveBook.setEnabled(false);
        if (editingBook == null) {
            bookViewModel.insertBook(catId, title, author, price, origPrice, discount, image, desc, stock).observe(this, resource -> {
                if (resource == null) return;
                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        binding.btnSaveBook.setEnabled(true);
                        Toast.makeText(this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case ERROR:
                        binding.btnSaveBook.setEnabled(true);
                        Toast.makeText(this, resource.message != null ? resource.message : "Có lỗi khi thêm sách", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        } else {
            bookViewModel.updateBook(editingBook.getId(), catId, title, author, price, origPrice, discount, image, desc, stock).observe(this, resource -> {
                if (resource == null) return;
                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        binding.btnSaveBook.setEnabled(true);
                        Toast.makeText(this, "Cập nhật sách thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case ERROR:
                        binding.btnSaveBook.setEnabled(true);
                        Toast.makeText(this, resource.message != null ? resource.message : "Có lỗi khi cập nhật sách", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        }
    }
}

package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.AdminBookAdapter;
import com.example.book_selling_app.databinding.ActivityAdminBooksBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.viewmodels.AdminBookViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminBooksActivity extends AppCompatActivity {

    private ActivityAdminBooksBinding binding;
    private AdminBookViewModel bookViewModel;
    private AdminBookAdapter adapter;
    private List<Book> fullBookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookViewModel = new ViewModelProvider(this).get(AdminBookViewModel.class);

        setupRecyclerView();
        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }

    private void setupRecyclerView() {
        binding.rvAdminBooks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminBookAdapter(this, new ArrayList<>(), new AdminBookAdapter.OnBookActionListener() {
            @Override
            public void onEdit(Book book) {
                Intent intent = new Intent(AdminBooksActivity.this, AdminAddEditBookActivity.class);
                intent.putExtra("BOOK_EXTRA", book);
                startActivity(intent);
            }

            @Override
            public void onDelete(Book book) {
                confirmDeleteBook(book);
            }
        });
        binding.rvAdminBooks.setAdapter(adapter);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.fabAddBook.setOnClickListener(v -> {
            startActivity(new Intent(AdminBooksActivity.this, AdminAddEditBookActivity.class));
        });

        binding.edtSearchBook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadBooks() {
        bookViewModel.getAllBooks().observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                fullBookList = resource.data;
                adapter.updateData(fullBookList);
                binding.tvAdminBookCount.setText(fullBookList.size() + " sách");
            }
        });
    }

    private void filterBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.updateData(fullBookList);
            return;
        }

        String lower = query.toLowerCase().trim();
        List<Book> filtered = new ArrayList<>();
        for (Book b : fullBookList) {
            if (b.getTitle().toLowerCase().contains(lower) || b.getAuthor().toLowerCase().contains(lower)) {
                filtered.add(b);
            }
        }
        adapter.updateData(filtered);
    }

    private void confirmDeleteBook(Book book) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa sách")
                .setMessage("Bạn có chắc chắn muốn xóa cuốn sách \"" + book.getTitle() + "\" khỏi kho không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    bookViewModel.deleteBook(book.getId()).observe(this, resource -> {
                        if (resource != null && resource.isSuccess()) {
                            Toast.makeText(this, "Đã xóa sách thành công", Toast.LENGTH_SHORT).show();
                            loadBooks();
                        } else {
                            Toast.makeText(this, "Không thể xóa sách", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

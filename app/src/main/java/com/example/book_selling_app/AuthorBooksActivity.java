package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.book_selling_app.adapters.BookAdapter;
import com.example.book_selling_app.databinding.ActivityAuthorBooksBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.viewmodels.BookViewModel;

import java.util.ArrayList;
import java.util.List;

public class AuthorBooksActivity extends AppCompatActivity {

    public static final String EXTRA_AUTHOR_NAME = "EXTRA_AUTHOR_NAME";

    private ActivityAuthorBooksBinding binding;
    private BookViewModel bookViewModel;
    private BookAdapter bookAdapter;
    private String authorName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthorBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authorName = getIntent().getStringExtra(EXTRA_AUTHOR_NAME);
        if (authorName == null || authorName.trim().isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin tác giả", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        setupUI();
        setupEvents();
        loadBooksByAuthor();
    }

    private void setupUI() {
        binding.tvAuthorTopTitle.setText("Tác giả: " + authorName);
        binding.tvAuthorName.setText(authorName);

        bookAdapter = new BookAdapter(this, new ArrayList<>(), book -> {
            Intent intent = new Intent(AuthorBooksActivity.this, BookDetailActivity.class);
            intent.putExtra("BOOK", book);
            startActivity(intent);
        });

        binding.rvAuthorBooks.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvAuthorBooks.setAdapter(bookAdapter);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void loadBooksByAuthor() {
        bookViewModel.getAllBooksByAuthor(authorName).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                List<Book> books = resource.data;
                if (books != null && !books.isEmpty()) {
                    binding.rvAuthorBooks.setVisibility(View.VISIBLE);
                    binding.layoutEmptyAuthorBooks.setVisibility(View.GONE);
                    binding.tvAuthorBookCount.setText(books.size() + " tác phẩm");
                    bookAdapter.updateData(books);
                } else {
                    binding.rvAuthorBooks.setVisibility(View.GONE);
                    binding.layoutEmptyAuthorBooks.setVisibility(View.VISIBLE);
                    binding.tvAuthorBookCount.setText("0 tác phẩm");
                }
            }
        });
    }
}

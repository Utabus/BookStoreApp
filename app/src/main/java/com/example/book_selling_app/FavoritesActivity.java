package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.book_selling_app.adapters.BookAdapter;
import com.example.book_selling_app.databinding.ActivityFavoritesBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.BookViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private ActivityFavoritesBinding binding;
    private BookViewModel bookViewModel;
    private SessionManager sessionManager;
    private BookAdapter bookAdapter;
    private final List<Book> favoriteBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        sessionManager = new SessionManager(this);

        binding.btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(this, favoriteBooks, book -> {
            Intent intent = new Intent(FavoritesActivity.this, BookDetailActivity.class);
            intent.putExtra("BOOK", book);
            startActivity(intent);
        });

        binding.rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvFavorites.setAdapter(bookAdapter);
    }

    private void loadFavorites() {
        int userId = sessionManager.getUserId();
        binding.progressBarFavorites.setVisibility(View.VISIBLE);

        bookViewModel.getFavoriteBooks(userId).observe(this, resource -> {
            binding.progressBarFavorites.setVisibility(View.GONE);
            if (resource != null && resource.isSuccess() && resource.data != null) {
                favoriteBooks.clear();
                favoriteBooks.addAll(resource.data);
                bookAdapter.notifyDataSetChanged();

                if (favoriteBooks.isEmpty()) {
                    binding.layoutEmptyFavorites.setVisibility(View.VISIBLE);
                    binding.rvFavorites.setVisibility(View.GONE);
                } else {
                    binding.layoutEmptyFavorites.setVisibility(View.GONE);
                    binding.rvFavorites.setVisibility(View.VISIBLE);
                }
            } else if (resource != null && resource.isError()) {
                binding.layoutEmptyFavorites.setVisibility(View.VISIBLE);
                binding.rvFavorites.setVisibility(View.GONE);
            }
        });
    }
}

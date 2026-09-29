package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.book_selling_app.adapters.BookAdapter;
import com.example.book_selling_app.databinding.ActivitySearchBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.viewmodels.BookViewModel;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private BookViewModel bookViewModel;
    private BookAdapter bookAdapter;
    private final List<Book> searchResults = new ArrayList<>();
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnClearSearch.setOnClickListener(v -> binding.edtSearchQuery.setText(""));

        binding.rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        bookAdapter = new BookAdapter(this, searchResults, book -> {
            Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
            intent.putExtra("BOOK", book);
            startActivity(intent);
        });
        binding.rvSearchResults.setAdapter(bookAdapter);

        // Barcode Scanner Launcher
        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String barcode = result.getContents().trim();
                binding.edtSearchQuery.setText(barcode);
                Toast.makeText(this, "Đã quét: " + barcode, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnBarcodeScan.setOnClickListener(v -> launchBarcodeScanner());

        binding.edtSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                binding.btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                searchBooks(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Check if auto scan requested
        if (getIntent().getBooleanExtra("AUTO_SCAN", false)) {
            launchBarcodeScanner();
        }

        // Initial search to display all books
        searchBooks("");
    }

    private void launchBarcodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Hướng camera về phía mã vạch hoặc mã QR trên sách");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        barcodeLauncher.launch(options);
    }

    private void searchBooks(String query) {
        bookViewModel.searchBooks(query, null, null, null, null, null).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                searchResults.clear();
                searchResults.addAll(resource.data);
                bookAdapter.notifyDataSetChanged();

                if (searchResults.isEmpty()) {
                    binding.layoutSearchEmpty.setVisibility(View.VISIBLE);
                    binding.rvSearchResults.setVisibility(View.GONE);
                } else {
                    binding.layoutSearchEmpty.setVisibility(View.GONE);
                    binding.rvSearchResults.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}

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

    public static final String EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID";
    public static final String EXTRA_CATEGORY_NAME = "EXTRA_CATEGORY_NAME";

    private ActivitySearchBinding binding;
    private BookViewModel bookViewModel;
    private BookAdapter bookAdapter;
    private final List<Book> searchResults = new ArrayList<>();
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;
    private Integer selectedCategoryId = null;

    private Double selectedMinPrice = null;
    private Double selectedMaxPrice = null;
    private String selectedSortBy = null;
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        int catId = getIntent().getIntExtra(EXTRA_CATEGORY_ID, -1);
        if (catId != -1) {
            selectedCategoryId = catId;
            String catName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
            if (catName != null && !catName.isEmpty()) {
                binding.edtSearchQuery.setHint("Tìm kiếm trong " + catName + "...");
            }
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnClearSearch.setOnClickListener(v -> binding.edtSearchQuery.setText(""));
        binding.btnFilter.setOnClickListener(v -> showFilterBottomSheet());

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
                currentKeyword = s.toString().trim();
                binding.btnClearSearch.setVisibility(currentKeyword.isEmpty() ? View.GONE : View.VISIBLE);
                searchBooks();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Check if auto scan requested
        if (getIntent().getBooleanExtra("AUTO_SCAN", false)) {
            launchBarcodeScanner();
        }

        // Initial search to display books
        searchBooks();
    }

    private void showFilterBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        com.example.book_selling_app.databinding.BottomSheetFilterSearchBinding filterBinding =
                com.example.book_selling_app.databinding.BottomSheetFilterSearchBinding.inflate(getLayoutInflater());
        dialog.setContentView(filterBinding.getRoot());

        // Pre-fill current state
        if ("PRICE_ASC".equals(selectedSortBy)) {
            filterBinding.rbSortPriceAsc.setChecked(true);
        } else if ("PRICE_DESC".equals(selectedSortBy)) {
            filterBinding.rbSortPriceDesc.setChecked(true);
        } else if ("RATING".equals(selectedSortBy)) {
            filterBinding.rbSortRating.setChecked(true);
        } else {
            filterBinding.rbSortDefault.setChecked(true);
        }

        if (selectedMinPrice == null && selectedMaxPrice != null && selectedMaxPrice == 100000.0) {
            filterBinding.rbPriceUnder100k.setChecked(true);
        } else if (selectedMinPrice != null && selectedMinPrice == 100000.0 && selectedMaxPrice != null && selectedMaxPrice == 200000.0) {
            filterBinding.rbPrice100kTo200k.setChecked(true);
        } else if (selectedMinPrice != null && selectedMinPrice == 200000.0 && selectedMaxPrice == null) {
            filterBinding.rbPriceAbove200k.setChecked(true);
        } else {
            filterBinding.rbPriceAll.setChecked(true);
        }

        filterBinding.btnCloseFilter.setOnClickListener(v -> dialog.dismiss());

        filterBinding.btnResetFilter.setOnClickListener(v -> {
            selectedSortBy = null;
            selectedMinPrice = null;
            selectedMaxPrice = null;
            dialog.dismiss();
            searchBooks();
            Toast.makeText(this, "Đã đặt lại bộ lọc", Toast.LENGTH_SHORT).show();
        });

        filterBinding.btnApplyFilter.setOnClickListener(v -> {
            // Read sort
            int sortId = filterBinding.rgSort.getCheckedRadioButtonId();
            if (sortId == R.id.rbSortPriceAsc) {
                selectedSortBy = "PRICE_ASC";
            } else if (sortId == R.id.rbSortPriceDesc) {
                selectedSortBy = "PRICE_DESC";
            } else if (sortId == R.id.rbSortRating) {
                selectedSortBy = "RATING";
            } else {
                selectedSortBy = null;
            }

            // Read price range
            int priceId = filterBinding.rgPriceRange.getCheckedRadioButtonId();
            if (priceId == R.id.rbPriceUnder100k) {
                selectedMinPrice = null;
                selectedMaxPrice = 100000.0;
            } else if (priceId == R.id.rbPrice100kTo200k) {
                selectedMinPrice = 100000.0;
                selectedMaxPrice = 200000.0;
            } else if (priceId == R.id.rbPriceAbove200k) {
                selectedMinPrice = 200000.0;
                selectedMaxPrice = null;
            } else {
                selectedMinPrice = null;
                selectedMaxPrice = null;
            }

            dialog.dismiss();
            searchBooks();
        });

        dialog.show();
    }

    private void launchBarcodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Hướng camera về phía mã vạch hoặc mã QR trên sách");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        barcodeLauncher.launch(options);
    }

    private void searchBooks() {
        bookViewModel.searchBooks(currentKeyword, selectedCategoryId, selectedMinPrice, selectedMaxPrice, null, selectedSortBy).observe(this, resource -> {
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

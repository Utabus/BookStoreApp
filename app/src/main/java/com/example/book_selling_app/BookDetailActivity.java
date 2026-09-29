package com.example.book_selling_app;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.book_selling_app.databinding.ActivityBookDetailBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.BookViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    private ActivityBookDetailBinding binding;
    private BookViewModel bookViewModel;
    private SessionManager sessionManager;
    private Book currentBook;
    private int quantity = 1;
    private boolean isFavorite = false;
    private final NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        sessionManager = new SessionManager(this);

        currentBook = (Book) getIntent().getSerializableExtra("BOOK");
        if (currentBook == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu sách!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindBookData();
        setupEvents();
        checkFavoriteStatus();
    }

    private void checkFavoriteStatus() {
        int userId = sessionManager.getUserId();
        bookViewModel.isBookFavorite(userId, currentBook.getId()).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                isFavorite = resource.data;
                updateFavoriteIcon();
            }
        });
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            binding.btnFavorite.setColorFilter(getColor(R.color.status_discount));
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            binding.btnFavorite.setColorFilter(getColor(R.color.text_primary));
        }
    }

    private void bindBookData() {
        binding.tvDetailTitle.setText(currentBook.getTitle());
        binding.tvDetailAuthor.setText("Tác giả: " + currentBook.getAuthor());
        binding.tvDetailRating.setText(String.valueOf(currentBook.getRating()));
        binding.tvDetailReviews.setText("(" + currentBook.getReviewCount() + " đánh giá)");
        binding.tvDetailPrice.setText(currencyFormatter.format(currentBook.getPrice()) + " đ");
        binding.tvDetailDescription.setText(currentBook.getDescription());

        if (currentBook.getOriginalPrice() > currentBook.getPrice()) {
            binding.tvDetailOriginalPrice.setText(currencyFormatter.format(currentBook.getOriginalPrice()) + " đ");
            binding.tvDetailOriginalPrice.setPaintFlags(
                    binding.tvDetailOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvDetailDiscount.setText("-" + currentBook.getDiscount() + "%");
        } else {
            binding.tvDetailOriginalPrice.setVisibility(View.GONE);
            binding.tvDetailDiscount.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(currentBook.getImageUrl())
                .placeholder(R.drawable.ic_book)
                .error(R.drawable.ic_book)
                .centerCrop()
                .into(binding.imgBookDetailCover);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnFavorite.setOnClickListener(v -> {
            int userId = sessionManager.getUserId();
            bookViewModel.toggleFavorite(userId, currentBook.getId()).observe(this, resource -> {
                if (resource != null && resource.isSuccess() && resource.data != null) {
                    isFavorite = resource.data;
                    updateFavoriteIcon();
                    if (isFavorite) {
                        Toast.makeText(this, "Đã thêm vào danh sách yêu thích ❤️", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        binding.btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
            intent.putExtra("OPEN_TAB", R.id.nav_cart);
            startActivity(intent);
        });

        binding.btnDetailPlus.setOnClickListener(v -> {
            quantity++;
            binding.tvDetailQuantity.setText(String.valueOf(quantity));
        });

        binding.btnDetailMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvDetailQuantity.setText(String.valueOf(quantity));
            }
        });

        binding.btnAddToCart.setOnClickListener(v -> addToCart(false));
        binding.btnBuyNow.setOnClickListener(v -> addToCart(true));
    }

    private void addToCart(boolean openCheckout) {
        int userId = sessionManager.getUserId();
        bookViewModel.addToCart(userId, currentBook.getId(), quantity).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                if (openCheckout) {
                    startActivity(new Intent(BookDetailActivity.this, CheckoutActivity.class));
                } else {
                    Toast.makeText(this, "Đã thêm " + quantity + " cuốn vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

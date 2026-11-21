package com.example.book_selling_app;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.book_selling_app.adapters.BookAdapter;
import com.example.book_selling_app.adapters.ReviewAdapter;
import com.example.book_selling_app.databinding.ActivityBookDetailBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Review;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.BookViewModel;
import com.example.book_selling_app.viewmodels.ReviewViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    private ActivityBookDetailBinding binding;
    private BookViewModel bookViewModel;
    private ReviewViewModel reviewViewModel;
    private SessionManager sessionManager;
    private Book currentBook;
    private int quantity = 1;
    private boolean isFavorite = false;
    private final NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    private ReviewAdapter reviewAdapter;
    private BookAdapter authorBooksAdapter;

    private final ActivityResultLauncher<Intent> writeReviewLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadReviews();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        sessionManager = new SessionManager(this);

        currentBook = (Book) getIntent().getSerializableExtra("BOOK");
        if (currentBook == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu sách!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindBookData();
        setupAdapters();
        setupEvents();
        checkFavoriteStatus();
        loadReviews();
        loadAuthorBooks();
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

        // Overview rating
        binding.tvOverviewRatingScore.setText(String.format(Locale.getDefault(), "%.1f", currentBook.getRating()));
        binding.ratingBarOverview.setRating((float) currentBook.getRating());
        binding.tvOverviewReviewCount.setText("Dựa trên " + currentBook.getReviewCount() + " nhận xét từ độc giả");

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

    private void setupAdapters() {
        // Reviews Adapter
        reviewAdapter = new ReviewAdapter();
        binding.rvBookReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBookReviews.setAdapter(reviewAdapter);

        // Author Books Adapter (Horizontal)
        authorBooksAdapter = new BookAdapter(this, new ArrayList<>(), book -> {
            Intent intent = new Intent(BookDetailActivity.this, BookDetailActivity.class);
            intent.putExtra("BOOK", book);
            startActivity(intent);
        });
        binding.rvAuthorRelatedBooks.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvAuthorRelatedBooks.setAdapter(authorBooksAdapter);
    }

    private void loadReviews() {
        reviewViewModel.getReviewsForBook(currentBook.getId()).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                List<Review> reviews = resource.data;
                if (reviews != null && !reviews.isEmpty()) {
                    binding.rvBookReviews.setVisibility(View.VISIBLE);
                    binding.tvEmptyReviews.setVisibility(View.GONE);
                    reviewAdapter.setReviews(reviews);

                    // Recalculate average and update overview
                    float totalStars = 0;
                    for (Review r : reviews) {
                        totalStars += r.getRating();
                    }
                    float avg = Math.round((totalStars / reviews.size()) * 10.0f) / 10.0f;
                    binding.tvDetailRating.setText(String.valueOf(avg));
                    binding.tvDetailReviews.setText("(" + reviews.size() + " đánh giá)");
                    binding.tvOverviewRatingScore.setText(String.format(Locale.getDefault(), "%.1f", avg));
                    binding.ratingBarOverview.setRating(avg);
                    binding.tvOverviewReviewCount.setText("Dựa trên " + reviews.size() + " nhận xét từ độc giả");
                } else {
                    binding.rvBookReviews.setVisibility(View.GONE);
                    binding.tvEmptyReviews.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadAuthorBooks() {
        if (currentBook.getAuthor() == null || currentBook.getAuthor().trim().isEmpty()) {
            binding.layoutAuthorBooksSection.setVisibility(View.GONE);
            return;
        }

        bookViewModel.getBooksByAuthor(currentBook.getAuthor(), currentBook.getId()).observe(this, resource -> {
            if (resource != null && resource.isSuccess()) {
                List<Book> books = resource.data;
                if (books != null && !books.isEmpty()) {
                    binding.layoutAuthorBooksSection.setVisibility(View.VISIBLE);
                    authorBooksAdapter.updateData(books);
                } else {
                    binding.layoutAuthorBooksSection.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Click on author to view all author's books
        View.OnClickListener authorClickListener = v -> {
            Intent intent = new Intent(BookDetailActivity.this, AuthorBooksActivity.class);
            intent.putExtra(AuthorBooksActivity.EXTRA_AUTHOR_NAME, currentBook.getAuthor());
            startActivity(intent);
        };
        binding.layoutAuthorClick.setOnClickListener(authorClickListener);
        binding.btnViewAllAuthorBooks.setOnClickListener(authorClickListener);

        // Click Write Review
        binding.btnWriteReview.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để viết nhận xét", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            Intent intent = new Intent(BookDetailActivity.this, WriteReviewActivity.class);
            intent.putExtra("BOOK_ID", currentBook.getId());
            intent.putExtra("BOOK_TITLE", currentBook.getTitle());
            intent.putExtra("BOOK_AUTHOR", currentBook.getAuthor());
            intent.putExtra("BOOK_IMAGE", currentBook.getImageUrl());
            writeReviewLauncher.launch(intent);
        });

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

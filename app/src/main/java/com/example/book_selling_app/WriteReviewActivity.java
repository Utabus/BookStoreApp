package com.example.book_selling_app;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.book_selling_app.databinding.ActivityWriteReviewBinding;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.ReviewViewModel;

public class WriteReviewActivity extends AppCompatActivity {

    private ActivityWriteReviewBinding binding;
    private ReviewViewModel reviewViewModel;
    private SessionManager sessionManager;

    private int bookId = 1;
    private String bookTitle = "";
    private String bookAuthor = "";
    private String bookImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        sessionManager = new SessionManager(this);

        bookId = getIntent().getIntExtra("BOOK_ID", 1);
        bookTitle = getIntent().getStringExtra("BOOK_TITLE");
        bookAuthor = getIntent().getStringExtra("BOOK_AUTHOR");
        bookImage = getIntent().getStringExtra("BOOK_IMAGE");

        if (bookTitle != null) {
            binding.tvReviewBookTitle.setText(bookTitle);
        }
        if (bookAuthor != null) {
            binding.tvReviewBookAuthor.setText(bookAuthor);
        }
        if (bookImage != null && !bookImage.isEmpty()) {
            Glide.with(this)
                    .load(bookImage)
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .centerCrop()
                    .into(binding.imgReviewBookCover);
        }

        setupEvents();
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.ratingBarReview.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            int r = Math.round(rating);
            switch (r) {
                case 1:
                    binding.tvRatingDescription.setText("Rất không hài lòng");
                    break;
                case 2:
                    binding.tvRatingDescription.setText("Không hài lòng");
                    break;
                case 3:
                    binding.tvRatingDescription.setText("Bình thường");
                    break;
                case 4:
                    binding.tvRatingDescription.setText("Hài lòng");
                    break;
                case 5:
                default:
                    binding.tvRatingDescription.setText("Tuyệt vời");
                    break;
            }
        });

        binding.btnSubmitReview.setOnClickListener(v -> {
            float rating = binding.ratingBarReview.getRating();
            if (rating < 1.0f) {
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá (tối thiểu 1 sao)", Toast.LENGTH_SHORT).show();
                return;
            }

            String comment = binding.edtReviewComment.getText() != null ?
                    binding.edtReviewComment.getText().toString().trim() : "";

            if (comment.isEmpty()) {
                comment = "Sản phẩm rất tốt, giao hàng nhanh chóng!";
            }

            int userId = sessionManager.getUserId();
            binding.btnSubmitReview.setEnabled(false);
            reviewViewModel.insertReview(userId, bookId, rating, comment).observe(this, resource -> {
                if (resource == null) return;
                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        binding.btnSubmitReview.setEnabled(true);
                        Toast.makeText(this, "Cảm ơn bạn đã gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case ERROR:
                        binding.btnSubmitReview.setEnabled(true);
                        Toast.makeText(this, resource.message != null ? resource.message : "Lỗi lưu đánh giá!", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });
    }
}

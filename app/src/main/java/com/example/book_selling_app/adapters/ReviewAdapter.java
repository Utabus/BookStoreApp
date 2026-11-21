package com.example.book_selling_app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.databinding.ItemReviewBinding;
import com.example.book_selling_app.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews = new ArrayList<>();

    public void setReviews(List<Review> list) {
        this.reviews = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding binding = ItemReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ItemReviewBinding binding;

        public ReviewViewHolder(@NonNull ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Review review) {
            String name = review.getUserName();
            if (name == null || name.trim().isEmpty()) {
                name = "Người dùng ẩn danh";
            }
            binding.tvReviewUserName.setText(name);

            // Set initial letter
            String initial = name.substring(0, 1).toUpperCase();
            binding.tvUserAvatarLetter.setText(initial);

            binding.tvReviewDate.setText(review.getReviewDate() != null ? review.getReviewDate() : "");
            binding.ratingBarItem.setRating(review.getRating());
            binding.tvReviewComment.setText(review.getComment() != null ? review.getComment() : "");
        }
    }
}

package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.Review;
import com.example.book_selling_app.repositories.ReviewRepository;

import java.util.List;

public class ReviewViewModel extends AndroidViewModel {

    private final ReviewRepository reviewRepository;

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        this.reviewRepository = new ReviewRepository(application);
    }

    public LiveData<Resource<Long>> insertReview(int userId, int bookId, float rating, String comment) {
        return reviewRepository.insertReview(userId, bookId, rating, comment);
    }

    public LiveData<Resource<List<Review>>> getReviewsForBook(int bookId) {
        return reviewRepository.getReviewsForBook(bookId);
    }
}

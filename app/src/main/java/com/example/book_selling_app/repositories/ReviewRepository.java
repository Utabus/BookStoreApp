package com.example.book_selling_app.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.book_selling_app.data.utils.AppExecutors;
import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.models.Review;

import java.util.List;

public class ReviewRepository {

    private final DatabaseHelper dbHelper;
    private final AppExecutors appExecutors;

    public ReviewRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        this.appExecutors = AppExecutors.getInstance();
    }

    public LiveData<Resource<List<Review>>> getReviewsForBook(int bookId) {
        MutableLiveData<Resource<List<Review>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                List<Review> reviews = dbHelper.getReviewsForBook(bookId);
                result.postValue(Resource.success(reviews));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tải đánh giá: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Long>> insertReview(int userId, int bookId, float rating, String comment) {
        MutableLiveData<Resource<Long>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                long revId = dbHelper.insertReview(userId, bookId, rating, comment);
                if (revId > 0) {
                    result.postValue(Resource.success(revId));
                } else {
                    result.postValue(Resource.error("Không thể lưu đánh giá", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), null));
            }
        });

        return result;
    }
}

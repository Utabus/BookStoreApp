package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.repositories.BookRepository;
import com.example.book_selling_app.repositories.CartRepository;
import com.example.book_selling_app.repositories.CategoryRepository;

import java.util.List;

public class BookViewModel extends AndroidViewModel {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;

    public BookViewModel(@NonNull Application application) {
        super(application);
        this.bookRepository = new BookRepository(application);
        this.categoryRepository = new CategoryRepository(application);
        this.cartRepository = new CartRepository(application);
    }

    public LiveData<Resource<List<Book>>> searchBooks(String keyword, Integer categoryId, Double minPrice, Double maxPrice, Float minRating, String sortBy) {
        return bookRepository.searchBooks(keyword, categoryId, minPrice, maxPrice, minRating, sortBy);
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return categoryRepository.getAllCategories();
    }

    public LiveData<Resource<Boolean>> addToCart(int userId, int bookId, int quantity) {
        return cartRepository.addToCart(userId, bookId, quantity);
    }
}

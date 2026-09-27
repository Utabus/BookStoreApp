package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.repositories.BookRepository;
import com.example.book_selling_app.repositories.CategoryRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.bookRepository = new BookRepository(application);
        this.categoryRepository = new CategoryRepository(application);
    }

    public LiveData<Resource<List<Book>>> getFeaturedBooks() {
        return bookRepository.getFeaturedBooks();
    }

    public LiveData<Resource<List<Book>>> getBooksByCategory(int categoryId) {
        return bookRepository.getBooksByCategory(categoryId);
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return categoryRepository.getAllCategories();
    }
}

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

public class AdminBookViewModel extends AndroidViewModel {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public AdminBookViewModel(@NonNull Application application) {
        super(application);
        this.bookRepository = new BookRepository(application);
        this.categoryRepository = new CategoryRepository(application);
    }

    public LiveData<Resource<List<Book>>> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return categoryRepository.getAllCategories();
    }

    public LiveData<Resource<Long>> insertBook(int catId, String title, String author, double price, double origPrice, int discount, String image, String desc, int stock) {
        return bookRepository.insertBook(catId, title, author, price, origPrice, discount, image, desc, stock);
    }

    public LiveData<Resource<Boolean>> updateBook(int id, int catId, String title, String author, double price, double origPrice, int discount, String image, String desc, int stock) {
        return bookRepository.updateBook(id, catId, title, author, price, origPrice, discount, image, desc, stock);
    }

    public LiveData<Resource<Boolean>> deleteBook(int id) {
        return bookRepository.deleteBook(id);
    }
}

package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.repositories.CategoryRepository;

import java.util.List;

public class AdminCategoryViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;

    public AdminCategoryViewModel(@NonNull Application application) {
        super(application);
        this.categoryRepository = new CategoryRepository(application);
    }

    public LiveData<Resource<List<Category>>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    public LiveData<Resource<Long>> insertCategory(String name) {
        return categoryRepository.insertCategory(name);
    }

    public LiveData<Resource<Boolean>> updateCategory(int id, String name) {
        return categoryRepository.updateCategory(id, name);
    }

    public LiveData<Resource<Boolean>> deleteCategory(int id) {
        return categoryRepository.deleteCategory(id);
    }
}

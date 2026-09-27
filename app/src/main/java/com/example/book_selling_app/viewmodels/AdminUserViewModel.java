package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.User;
import com.example.book_selling_app.repositories.AdminRepository;

import java.util.List;

public class AdminUserViewModel extends AndroidViewModel {

    private final AdminRepository adminRepository;

    public AdminUserViewModel(@NonNull Application application) {
        super(application);
        this.adminRepository = new AdminRepository(application);
    }

    public LiveData<Resource<List<User>>> getAllUsers() {
        return adminRepository.getAllUsers();
    }

    public LiveData<Resource<Boolean>> toggleUserStatus(int userId, boolean currentStatus) {
        return adminRepository.toggleUserStatus(userId, currentStatus);
    }
}

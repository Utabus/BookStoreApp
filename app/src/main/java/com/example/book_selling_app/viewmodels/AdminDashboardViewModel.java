package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.repositories.AdminRepository;

public class AdminDashboardViewModel extends AndroidViewModel {

    private final AdminRepository adminRepository;

    public AdminDashboardViewModel(@NonNull Application application) {
        super(application);
        this.adminRepository = new AdminRepository(application);
    }

    public LiveData<Resource<DatabaseHelper.AdminStats>> getAdminStats() {
        return adminRepository.getAdminStats();
    }
}

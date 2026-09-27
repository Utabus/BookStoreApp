package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.User;
import com.example.book_selling_app.repositories.AuthRepository;
import com.example.book_selling_app.utils.SessionManager;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<Resource<User>> login(String email, String password) {
        return authRepository.login(email, password);
    }

    public LiveData<Resource<Long>> register(String fullName, String email, String password, String phone, String address) {
        return authRepository.register(fullName, email, password, phone, address);
    }

    public LiveData<Resource<Boolean>> updateProfile(int userId, String fullName, String phone, String address) {
        return authRepository.updateProfile(userId, fullName, phone, address);
    }

    public LiveData<Resource<Boolean>> changePassword(int userId, String oldPassword, String newPassword) {
        return authRepository.changePassword(userId, oldPassword, newPassword);
    }

    public void logout() {
        authRepository.logout();
    }

    public SessionManager getSessionManager() {
        return authRepository.getSessionManager();
    }
}

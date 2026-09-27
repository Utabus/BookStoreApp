package com.example.book_selling_app.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.book_selling_app.data.utils.AppExecutors;
import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminRepository {

    private final DatabaseHelper dbHelper;
    private final AppExecutors appExecutors;
    private final FirebaseFirestore mFirestore;

    public AdminRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        this.appExecutors = AppExecutors.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Resource<DatabaseHelper.AdminStats>> getAdminStats() {
        MutableLiveData<Resource<DatabaseHelper.AdminStats>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                DatabaseHelper.AdminStats stats = dbHelper.getAdminStats();
                result.postValue(Resource.success(stats));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tải thống kê: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<User>>> getAllUsers() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                // Return local cached users first for instant UI response
                List<User> localUsers = dbHelper.getAllUsers();
                result.postValue(Resource.success(localUsers));

                // Fetch latest users from Firestore
                mFirestore.collection("users").get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                appExecutors.diskIO().execute(() -> {
                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                        String email = doc.getString("email");
                                        if (email != null && !email.trim().isEmpty()) {
                                            String fullName = doc.getString("fullName");
                                            String phone = doc.getString("phone");
                                            String address = doc.getString("address");
                                            String role = doc.getString("role");
                                            Boolean isActive = doc.getBoolean("isActive");
                                            dbHelper.insertOrUpdateUserFromFirebase(fullName, email, phone, address, role, isActive != null ? isActive : true);
                                        }
                                    }
                                    List<User> updatedUsers = dbHelper.getAllUsers();
                                    result.postValue(Resource.success(updatedUsers));
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Firestore failed, continue with local users
                        });
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tải danh sách người dùng: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> toggleUserStatus(int userId, boolean currentStatus) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.toggleUserStatus(userId, currentStatus);
                if (rows > 0) {
                    // Sync status to Firestore
                    List<User> users = dbHelper.getAllUsers();
                    for (User u : users) {
                        if (u.getId() == userId) {
                            mFirestore.collection("users")
                                    .whereEqualTo("email", u.getEmail())
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (querySnapshot != null) {
                                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                                doc.getReference().update("isActive", currentStatus);
                                            }
                                        }
                                    });
                            break;
                        }
                    }
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Không thể cập nhật trạng thái người dùng", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi cập nhật trạng thái người dùng: " + e.getMessage(), false));
            }
        });

        return result;
    }
}

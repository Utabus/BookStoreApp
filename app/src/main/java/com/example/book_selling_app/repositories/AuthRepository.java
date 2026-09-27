package com.example.book_selling_app.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.book_selling_app.data.utils.AppExecutors;
import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.models.User;
import com.example.book_selling_app.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    private final DatabaseHelper dbHelper;
    private final SessionManager sessionManager;
    private final AppExecutors appExecutors;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mFirestore;

    public AuthRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        this.sessionManager = new SessionManager(context.getApplicationContext());
        this.appExecutors = AppExecutors.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Resource<User>> login(String email, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        FirebaseUser fbUser = task.getResult().getUser();
                        String uid = fbUser.getUid();

                        mFirestore.collection("users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    String fullName = documentSnapshot.getString("fullName");
                                    String phone = documentSnapshot.getString("phone");
                                    String address = documentSnapshot.getString("address");
                                    String role = documentSnapshot.getString("role");
                                    Boolean isActive = documentSnapshot.getBoolean("isActive");

                                    if (role == null) {
                                        role = email.equalsIgnoreCase("admin@gmail.com") ? "ADMIN" : "USER";
                                    }
                                    if (isActive == null) {
                                        isActive = true;
                                    }
                                    if (fullName == null || fullName.trim().isEmpty()) {
                                        fullName = (fbUser.getDisplayName() != null && !fbUser.getDisplayName().isEmpty())
                                                ? fbUser.getDisplayName()
                                                : (role.equals("ADMIN") ? "Quản trị viên" : "Người dùng");
                                    }

                                    if (!isActive) {
                                        mAuth.signOut();
                                        result.setValue(Resource.error("Tài khoản của bạn đã bị khóa bởi Quản trị viên.", null));
                                        return;
                                    }

                                    if (!documentSnapshot.exists()) {
                                        Map<String, Object> newProfile = new HashMap<>();
                                        newProfile.put("uid", uid);
                                        newProfile.put("fullName", fullName);
                                        newProfile.put("email", email);
                                        newProfile.put("phone", phone != null ? phone : "");
                                        newProfile.put("address", address != null ? address : "");
                                        newProfile.put("role", role);
                                        newProfile.put("isActive", true);
                                        newProfile.put("createdAt", FieldValue.serverTimestamp());
                                        mFirestore.collection("users").document(uid).set(newProfile);
                                    }

                                    final String finalFullName = fullName;
                                    final String finalRole = role;
                                    final String finalPhone = phone != null ? phone : "";
                                    final String finalAddress = address != null ? address : "";
                                    final boolean finalActive = isActive;

                                    appExecutors.diskIO().execute(() -> {
                                        long localId = dbHelper.insertOrUpdateUserFromFirebase(finalFullName, email, finalPhone, finalAddress, finalRole, finalActive);
                                        User localUser = dbHelper.getUserByEmail(email);
                                        if (localUser != null) {
                                            sessionManager.createLoginSession((int) localId, finalFullName, email, finalRole);
                                            result.postValue(Resource.success(localUser));
                                        } else {
                                            User u = new User((int) localId, finalFullName, email, finalPhone, finalAddress, finalRole, finalActive);
                                            sessionManager.createLoginSession((int) localId, finalFullName, email, finalRole);
                                            result.postValue(Resource.success(u));
                                        }
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    appExecutors.diskIO().execute(() -> {
                                        User localUser = dbHelper.getUserByEmail(email);
                                        if (localUser != null) {
                                            sessionManager.createLoginSession(localUser.getId(), localUser.getFullName(), localUser.getEmail(), localUser.getRole());
                                            result.postValue(Resource.success(localUser));
                                        } else {
                                            result.postValue(Resource.error("Không thể tải thông tin tài khoản: " + e.getMessage(), null));
                                        }
                                    });
                                });
                    } else {
                        // Fallback: Check local SQLite for initial accounts or offline login
                        appExecutors.diskIO().execute(() -> {
                            User localUser = dbHelper.loginUser(email, password);
                            if (localUser != null) {
                                if (!localUser.isActive()) {
                                    result.postValue(Resource.error("Tài khoản của bạn đã bị khóa bởi Quản trị viên.", null));
                                    return;
                                }

                                // Auto-register existing local account to Firebase Auth & Firestore in background
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createTask -> {
                                    if (createTask.isSuccessful() && createTask.getResult() != null && createTask.getResult().getUser() != null) {
                                        String newUid = createTask.getResult().getUser().getUid();
                                        Map<String, Object> newProfile = new HashMap<>();
                                        newProfile.put("uid", newUid);
                                        newProfile.put("fullName", localUser.getFullName());
                                        newProfile.put("email", localUser.getEmail());
                                        newProfile.put("phone", localUser.getPhone());
                                        newProfile.put("address", localUser.getAddress());
                                        newProfile.put("role", localUser.getRole());
                                        newProfile.put("isActive", localUser.isActive());
                                        newProfile.put("createdAt", FieldValue.serverTimestamp());
                                        mFirestore.collection("users").document(newUid).set(newProfile);
                                    }
                                });

                                sessionManager.createLoginSession(localUser.getId(), localUser.getFullName(), localUser.getEmail(), localUser.getRole());
                                result.postValue(Resource.success(localUser));
                            } else {
                                String errorMsg = "Email hoặc mật khẩu không chính xác!";
                                if (task.getException() != null && task.getException().getMessage() != null) {
                                    String raw = task.getException().getMessage();
                                    if (raw.contains("network") || raw.contains("INTERNET")) {
                                        errorMsg = "Lỗi kết nối mạng, vui lòng thử lại!";
                                    }
                                }
                                result.postValue(Resource.error(errorMsg, null));
                            }
                        });
                    }
                });

        return result;
    }

    public LiveData<Resource<Long>> register(String fullName, String email, String password, String phone, String address) {
        MutableLiveData<Resource<Long>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        String uid = task.getResult().getUser().getUid();

                        Map<String, Object> profile = new HashMap<>();
                        profile.put("uid", uid);
                        profile.put("fullName", fullName);
                        profile.put("email", email);
                        profile.put("phone", phone);
                        profile.put("address", address);
                        profile.put("role", "USER");
                        profile.put("isActive", true);
                        profile.put("createdAt", FieldValue.serverTimestamp());

                        mFirestore.collection("users").document(uid).set(profile);

                        appExecutors.diskIO().execute(() -> {
                            long userId = dbHelper.registerUser(fullName, email, password, phone, address);
                            result.postValue(Resource.success(userId));
                        });
                    } else {
                        appExecutors.diskIO().execute(() -> {
                            if (dbHelper.checkEmailExists(email)) {
                                result.postValue(Resource.error("Email này đã được đăng ký tài khoản khác!", null));
                                return;
                            }
                            String err = "Đăng ký không thành công!";
                            if (task.getException() != null && task.getException().getMessage() != null) {
                                String raw = task.getException().getMessage();
                                if (raw.contains("email address is already in use")) {
                                    err = "Email này đã được đăng ký tài khoản khác!";
                                } else if (raw.contains("password")) {
                                    err = "Mật khẩu phải có ít nhất 6 ký tự!";
                                } else if (raw.contains("badly formatted")) {
                                    err = "Định dạng email không hợp lệ!";
                                }
                            }
                            result.postValue(Resource.error(err, null));
                        });
                    }
                });

        return result;
    }

    public LiveData<Resource<Boolean>> updateProfile(int userId, String fullName, String phone, String address) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.updateUserProfile(userId, fullName, phone, address);
                if (rows > 0) {
                    sessionManager.updateUserName(fullName);

                    // Update Firestore if user is signed in
                    FirebaseUser fbUser = mAuth.getCurrentUser();
                    if (fbUser != null) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("fullName", fullName);
                        updates.put("phone", phone);
                        updates.put("address", address);
                        mFirestore.collection("users").document(fbUser.getUid()).update(updates);
                    }

                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Cập nhật thông tin thất bại", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> changePassword(int userId, String oldPass, String newPass) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                boolean success = dbHelper.changePassword(userId, oldPass, newPass);
                if (success) {
                    FirebaseUser fbUser = mAuth.getCurrentUser();
                    if (fbUser != null) {
                        fbUser.updatePassword(newPass);
                    }
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Mật khẩu hiện tại không chính xác!", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public void logout() {
        if (mAuth != null) {
            mAuth.signOut();
        }
        sessionManager.logout();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}

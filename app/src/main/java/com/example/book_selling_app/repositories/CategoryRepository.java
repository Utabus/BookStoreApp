package com.example.book_selling_app.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.book_selling_app.data.utils.AppExecutors;
import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.models.Category;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryRepository {

    private final DatabaseHelper dbHelper;
    private final AppExecutors appExecutors;
    private final FirebaseFirestore mFirestore;

    public CategoryRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        this.appExecutors = AppExecutors.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Resource<List<Category>>> getAllCategories() {
        MutableLiveData<Resource<List<Category>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                // 1. Immediately return local categories for zero latency
                List<Category> localCategories = dbHelper.getAllCategories();
                result.postValue(Resource.success(localCategories));

                // 2. Fetch fresh categories from Cloud Firestore
                mFirestore.collection("categories").get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                appExecutors.diskIO().execute(() -> {
                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                        try {
                                            int id = doc.contains("id") && doc.getLong("id") != null 
                                                    ? doc.getLong("id").intValue() 
                                                    : Integer.parseInt(doc.getId());
                                            String name = doc.getString("name");
                                            if (name != null) {
                                                dbHelper.upsertCategoryFromFirebase(id, name);
                                            }
                                        } catch (Exception ignored) {
                                        }
                                    }
                                    List<Category> updatedCategories = dbHelper.getAllCategories();
                                    result.postValue(Resource.success(updatedCategories));
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // If Firestore fails, local data is already emitted
                        });
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tải danh mục: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Long>> insertCategory(String name) {
        MutableLiveData<Resource<Long>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                long id = dbHelper.insertCategory(name);
                if (id > 0) {
                    // Sync to Firestore
                    Map<String, Object> catMap = new HashMap<>();
                    catMap.put("id", id);
                    catMap.put("name", name);
                    mFirestore.collection("categories").document(String.valueOf(id)).set(catMap);

                    result.postValue(Resource.success(id));
                } else {
                    result.postValue(Resource.error("Không thể thêm thể loại", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> updateCategory(int id, String name) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.updateCategory(id, name);
                if (rows > 0) {
                    // Sync update to Firestore
                    mFirestore.collection("categories").document(String.valueOf(id)).update("name", name);
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Cập nhật thể loại không thành công", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> deleteCategory(int id) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                if (dbHelper.isCategoryInUse(id)) {
                    result.postValue(Resource.error("Không thể xóa: Vẫn còn sách thuộc thể loại này trong kho!", false));
                    return;
                }
                int rows = dbHelper.deleteCategory(id);
                if (rows > 0) {
                    // Sync delete to Firestore
                    mFirestore.collection("categories").document(String.valueOf(id)).delete();
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Không thể xóa thể loại", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }
}

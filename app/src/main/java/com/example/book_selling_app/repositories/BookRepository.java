package com.example.book_selling_app.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.book_selling_app.data.utils.AppExecutors;
import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.models.Book;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookRepository {

    private final DatabaseHelper dbHelper;
    private final AppExecutors appExecutors;
    private final FirebaseFirestore mFirestore;

    public BookRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        this.appExecutors = AppExecutors.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Resource<List<Book>>> getAllBooks() {
        MutableLiveData<Resource<List<Book>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                // 1. Return local cache immediately
                List<Book> localBooks = dbHelper.getAllBooks(false);
                result.postValue(Resource.success(localBooks));

                // 2. Fetch remote books from Firestore
                fetchBooksFromFirestore(() -> {
                    List<Book> updated = dbHelper.getAllBooks(false);
                    result.postValue(Resource.success(updated));
                });
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tải danh sách sách: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Book>>> getFeaturedBooks() {
        MutableLiveData<Resource<List<Book>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                List<Book> localBooks = dbHelper.getAllBooks(true);
                result.postValue(Resource.success(localBooks));

                fetchBooksFromFirestore(() -> {
                    List<Book> updated = dbHelper.getAllBooks(true);
                    result.postValue(Resource.success(updated));
                });
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi tải sách nổi bật: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Book>>> getBooksByCategory(int categoryId) {
        MutableLiveData<Resource<List<Book>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                List<Book> books = dbHelper.getBooksByCategory(categoryId);
                result.postValue(Resource.success(books));

                fetchBooksFromFirestore(() -> {
                    List<Book> updated = dbHelper.getBooksByCategory(categoryId);
                    result.postValue(Resource.success(updated));
                });
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi tải sách theo danh mục: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Book>>> searchBooks(String keyword, Integer categoryId, Double minPrice, Double maxPrice, Float minRating, String sortBy) {
        MutableLiveData<Resource<List<Book>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                List<Book> books = dbHelper.searchBooks(keyword, categoryId, minPrice, maxPrice, minRating, sortBy);
                result.postValue(Resource.success(books));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi tìm kiếm sách: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Long>> insertBook(int catId, String title, String author, double price, double origPrice, int discount, String image, String desc, int stock) {
        MutableLiveData<Resource<Long>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                long id = dbHelper.insertBook(catId, title, author, price, origPrice, discount, image, desc, stock);
                if (id > 0) {
                    // Sync to Firestore
                    Map<String, Object> bookMap = new HashMap<>();
                    bookMap.put("id", id);
                    bookMap.put("categoryId", catId);
                    bookMap.put("title", title);
                    bookMap.put("author", author);
                    bookMap.put("price", price);
                    bookMap.put("originalPrice", origPrice);
                    bookMap.put("discount", discount);
                    bookMap.put("rating", 5.0);
                    bookMap.put("reviewsCount", 0);
                    bookMap.put("image", image);
                    bookMap.put("description", desc);
                    bookMap.put("stock", stock);
                    bookMap.put("isActive", true);

                    mFirestore.collection("books").document(String.valueOf(id)).set(bookMap);

                    result.postValue(Resource.success(id));
                } else {
                    result.postValue(Resource.error("Không thể thêm sách", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> updateBook(int id, int catId, String title, String author, double price, double origPrice, int discount, String image, String desc, int stock) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.updateBook(id, catId, title, author, price, origPrice, discount, image, desc, stock);
                if (rows > 0) {
                    // Sync update to Firestore
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("categoryId", catId);
                    updates.put("title", title);
                    updates.put("author", author);
                    updates.put("price", price);
                    updates.put("originalPrice", origPrice);
                    updates.put("discount", discount);
                    updates.put("image", image);
                    updates.put("description", desc);
                    updates.put("stock", stock);

                    mFirestore.collection("books").document(String.valueOf(id)).update(updates);

                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Cập nhật sách không thành công", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> deleteBook(int id) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.deleteBook(id);
                if (rows > 0) {
                    mFirestore.collection("books").document(String.valueOf(id)).delete();
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Không thể xóa sách", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi: " + e.getMessage(), false));
            }
        });

        return result;
    }

    private void fetchBooksFromFirestore(Runnable onSynced) {
        mFirestore.collection("books").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        appExecutors.diskIO().execute(() -> {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                try {
                                    int id = doc.contains("id") && doc.getLong("id") != null 
                                            ? doc.getLong("id").intValue() 
                                            : Integer.parseInt(doc.getId());
                                    int catId = doc.getLong("categoryId") != null ? doc.getLong("categoryId").intValue() : 1;
                                    String title = doc.getString("title");
                                    String author = doc.getString("author");
                                    double price = doc.getDouble("price") != null ? doc.getDouble("price") : 0.0;
                                    double origPrice = doc.getDouble("originalPrice") != null ? doc.getDouble("originalPrice") : price;
                                    int discount = doc.getLong("discount") != null ? doc.getLong("discount").intValue() : 0;
                                    double rating = doc.getDouble("rating") != null ? doc.getDouble("rating") : 5.0;
                                    int reviews = doc.getLong("reviewsCount") != null ? doc.getLong("reviewsCount").intValue() : 0;
                                    String image = doc.getString("image");
                                    String desc = doc.getString("description");
                                    int stock = doc.getLong("stock") != null ? doc.getLong("stock").intValue() : 10;
                                    boolean isActive = doc.getBoolean("isActive") != null ? Boolean.TRUE.equals(doc.getBoolean("isActive")) : true;

                                    if (title != null) {
                                        dbHelper.upsertBookFromFirebase(id, catId, title, author, price, origPrice, discount, rating, reviews, image, desc, stock, isActive);
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                            if (onSynced != null) {
                                onSynced.run();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Ignore offline
                });
    }

    public LiveData<Resource<Boolean>> isBookFavorite(int userId, int bookId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> {
            try {
                boolean fav = dbHelper.isBookFavorite(userId, bookId);
                result.postValue(Resource.success(fav));
            } catch (Exception e) {
                result.postValue(Resource.error(e.getMessage(), false));
            }
        });
        return result;
    }

    public LiveData<Resource<Boolean>> toggleFavorite(int userId, int bookId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        appExecutors.diskIO().execute(() -> {
            try {
                boolean newState = dbHelper.toggleFavorite(userId, bookId);
                result.postValue(Resource.success(newState));
            } catch (Exception e) {
                result.postValue(Resource.error(e.getMessage(), false));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Book>>> getFavoriteBooks(int userId) {
        MutableLiveData<Resource<List<Book>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());
        appExecutors.diskIO().execute(() -> {
            try {
                List<Book> favs = dbHelper.getFavoriteBooks(userId);
                result.postValue(Resource.success(favs));
            } catch (Exception e) {
                result.postValue(Resource.error(e.getMessage(), null));
            }
        });
        return result;
    }
}

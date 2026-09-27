package com.example.book_selling_app.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.book_selling_app.data.utils.AppExecutors;
import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    private final DatabaseHelper dbHelper;
    private final AppExecutors appExecutors;

    public CartRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        this.appExecutors = AppExecutors.getInstance();
    }

    public LiveData<Resource<List<CartItem>>> getCartItems(int userId) {
        MutableLiveData<Resource<List<CartItem>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                List<CartItem> list = new ArrayList<>();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT c.id, c.quantity, b.id, b.title, b.author, b.price, b.original_price, b.image_url "
                        + "FROM " + DatabaseHelper.TABLE_CART + " c "
                        + "INNER JOIN " + DatabaseHelper.TABLE_BOOKS + " b ON c.book_id = b.id "
                        + "WHERE c.user_id = ?";
                Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int cartId = cursor.getInt(0);
                        int qty = cursor.getInt(1);

                        Book book = new Book();
                        book.setId(cursor.getInt(2));
                        book.setTitle(cursor.getString(3));
                        book.setAuthor(cursor.getString(4));
                        book.setPrice(cursor.getDouble(5));
                        book.setOriginalPrice(cursor.getDouble(6));
                        book.setImageUrl(cursor.getString(7));

                        list.add(new CartItem(cartId, book, qty));
                    } while (cursor.moveToNext());
                    cursor.close();
                }

                result.postValue(Resource.success(list));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tải giỏ hàng: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> addToCart(int userId, int bookId, int quantity) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT id, quantity FROM " + DatabaseHelper.TABLE_CART
                                + " WHERE user_id = ? AND book_id = ?",
                        new String[]{String.valueOf(userId), String.valueOf(bookId)});

                if (cursor != null && cursor.moveToFirst()) {
                    int cartId = cursor.getInt(0);
                    int currentQty = cursor.getInt(1);
                    cursor.close();

                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseHelper.COLUMN_CART_QUANTITY, currentQty + quantity);
                    db.update(DatabaseHelper.TABLE_CART, cv, "id = ?", new String[]{String.valueOf(cartId)});
                } else {
                    if (cursor != null) cursor.close();
                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseHelper.COLUMN_CART_USER_ID, userId);
                    cv.put(DatabaseHelper.COLUMN_CART_BOOK_ID, bookId);
                    cv.put(DatabaseHelper.COLUMN_CART_QUANTITY, quantity);
                    db.insert(DatabaseHelper.TABLE_CART, null, cv);
                }

                result.postValue(Resource.success(true));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi thêm vào giỏ: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> updateQuantity(int cartId, int newQuantity) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_CART_QUANTITY, newQuantity);
                int rows = db.update(DatabaseHelper.TABLE_CART, cv, "id = ?", new String[]{String.valueOf(cartId)});
                result.postValue(Resource.success(rows > 0));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi cập nhật số lượng: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> removeFromCart(int cartId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int rows = db.delete(DatabaseHelper.TABLE_CART, "id = ?", new String[]{String.valueOf(cartId)});
                result.postValue(Resource.success(rows > 0));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi xóa khỏi giỏ: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> clearCart(int userId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(DatabaseHelper.TABLE_CART, "user_id = ?", new String[]{String.valueOf(userId)});
                result.postValue(Resource.success(true));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi dọn giỏ hàng: " + e.getMessage(), false));
            }
        });

        return result;
    }
}

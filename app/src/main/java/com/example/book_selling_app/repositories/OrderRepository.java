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
import com.example.book_selling_app.models.Order;
import com.example.book_selling_app.models.OrderItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderRepository {

    private final DatabaseHelper dbHelper;
    private final AppExecutors appExecutors;
    private final FirebaseFirestore mFirestore;

    public OrderRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
        this.appExecutors = AppExecutors.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Resource<Long>> createOrder(int userId, String address, String phone, String payment, double totalAmount) {
        MutableLiveData<Resource<Long>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String dateStr = sdf.format(new Date());

                ContentValues orderValues = new ContentValues();
                orderValues.put(DatabaseHelper.COLUMN_ORDER_USER_ID, userId);
                orderValues.put(DatabaseHelper.COLUMN_ORDER_DATE, dateStr);
                orderValues.put(DatabaseHelper.COLUMN_ORDER_STATUS, "CHỜ XỬ LÝ");
                orderValues.put(DatabaseHelper.COLUMN_ORDER_TOTAL, totalAmount);
                orderValues.put(DatabaseHelper.COLUMN_ORDER_ADDRESS, address + " (SĐT: " + phone + ")");
                orderValues.put(DatabaseHelper.COLUMN_ORDER_PAYMENT, payment);

                long orderId = db.insert(DatabaseHelper.TABLE_ORDERS, null, orderValues);

                if (orderId != -1) {
                    List<Map<String, Object>> itemsList = new ArrayList<>();

                    // Copy items from cart to order_items with price & details
                    String cartQuery = "SELECT c.book_id, c.quantity, b.title, b.author, b.price, b.image_url "
                            + " FROM " + DatabaseHelper.TABLE_CART + " c "
                            + " LEFT JOIN " + DatabaseHelper.TABLE_BOOKS + " b ON c.book_id = b.id "
                            + " WHERE c.user_id = ?";
                    Cursor c = db.rawQuery(cartQuery, new String[]{String.valueOf(userId)});
                    if (c != null && c.moveToFirst()) {
                        do {
                            int bookId = c.getInt(0);
                            int qty = c.getInt(1);
                            String bTitle = c.getString(2) != null ? c.getString(2) : "Sách #" + bookId;
                            String bAuthor = c.getString(3) != null ? c.getString(3) : "";
                            double bPrice = c.getDouble(4);
                            String bImage = c.getString(5) != null ? c.getString(5) : "";

                            ContentValues itemValues = new ContentValues();
                            itemValues.put(DatabaseHelper.COLUMN_ITEM_ORDER_ID, orderId);
                            itemValues.put(DatabaseHelper.COLUMN_ITEM_BOOK_ID, bookId);
                            itemValues.put(DatabaseHelper.COLUMN_ITEM_QUANTITY, qty);
                            itemValues.put(DatabaseHelper.COLUMN_ITEM_PRICE, bPrice);
                            db.insert(DatabaseHelper.TABLE_ORDER_ITEMS, null, itemValues);

                            Map<String, Object> itMap = new HashMap<>();
                            itMap.put("bookId", bookId);
                            itMap.put("title", bTitle);
                            itMap.put("author", bAuthor);
                            itMap.put("price", bPrice);
                            itMap.put("quantity", qty);
                            itMap.put("image", bImage);
                            itemsList.add(itMap);
                        } while (c.moveToNext());
                        c.close();
                    }

                    // Clear cart
                    db.delete(DatabaseHelper.TABLE_CART, "user_id = ?", new String[]{String.valueOf(userId)});
                    db.setTransactionSuccessful();

                    // Sync to Cloud Firestore with full items list
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", orderId);
                    orderMap.put("userId", String.valueOf(userId));
                    orderMap.put("orderDate", dateStr);
                    orderMap.put("status", "CHỜ XỬ LÝ");
                    orderMap.put("total", totalAmount);
                    orderMap.put("shippingAddress", address + " (SĐT: " + phone + ")");
                    orderMap.put("paymentMethod", payment);
                    orderMap.put("items", itemsList);

                    mFirestore.collection("orders").document(String.valueOf(orderId)).set(orderMap);

                    result.postValue(Resource.success(orderId));
                } else {
                    result.postValue(Resource.error("Không thể tạo đơn hàng", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi khi tạo đơn hàng: " + e.getMessage(), null));
            } finally {
                db.endTransaction();
            }
        });

        return result;
    }

    public LiveData<Resource<List<Order>>> getUserOrders(int userId) {
        MutableLiveData<Resource<List<Order>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                // 1. Return local orders immediately
                List<Order> list = getLocalUserOrders(userId);
                result.postValue(Resource.success(list));

                // 2. Sync from Firestore
                mFirestore.collection("orders")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                appExecutors.diskIO().execute(() -> {
                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                        syncOrderDocToSqlite(doc, userId);
                                    }
                                    List<Order> updated = getLocalUserOrders(userId);
                                    result.postValue(Resource.success(updated));
                                });
                            }
                        });
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi tải đơn hàng: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<OrderItem>>> getOrderItems(int orderId) {
        MutableLiveData<Resource<List<OrderItem>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                // 1. Return local cached items
                List<OrderItem> localItems = dbHelper.getOrderItems(orderId);
                result.postValue(Resource.success(localItems));

                // 2. Fetch fresh items from Firestore
                mFirestore.collection("orders").document(String.valueOf(orderId)).get()
                        .addOnSuccessListener(doc -> {
                            if (doc != null && doc.exists() && doc.contains("items")) {
                                List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                                if (items != null) {
                                    appExecutors.diskIO().execute(() -> {
                                        for (Map<String, Object> it : items) {
                                            try {
                                                int bId = it.get("bookId") instanceof Number ? ((Number) it.get("bookId")).intValue() : 1;
                                                String title = (String) it.get("title");
                                                String author = (String) it.get("author");
                                                String image = (String) it.get("image");
                                                double price = it.get("price") instanceof Number ? ((Number) it.get("price")).doubleValue() : 0.0;
                                                int qty = it.get("quantity") instanceof Number ? ((Number) it.get("quantity")).intValue() : 1;
                                                dbHelper.upsertOrderItemFromFirebase(orderId, bId, title, author, image, price, qty);
                                            } catch (Exception ignored) {
                                            }
                                        }
                                        List<OrderItem> updated = dbHelper.getOrderItems(orderId);
                                        result.postValue(Resource.success(updated));
                                    });
                                }
                            }
                        });
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi tải chi tiết sản phẩm đơn hàng: " + e.getMessage(), null));
            }
        });

        return result;
    }

    private void syncOrderDocToSqlite(DocumentSnapshot doc, int defaultUserId) {
        try {
            int orderId = doc.contains("id") && doc.getLong("id") != null 
                    ? doc.getLong("id").intValue() 
                    : Integer.parseInt(doc.getId());
            String uidStr = doc.getString("userId");
            int oUserId = uidStr != null && uidStr.matches("\\d+") ? Integer.parseInt(uidStr) : defaultUserId;
            String oDate = doc.getString("orderDate") != null ? doc.getString("orderDate") : "";
            String status = doc.getString("status") != null ? doc.getString("status") : "PENDING";
            double total = doc.getDouble("total") != null ? doc.getDouble("total") : 0.0;
            String addr = doc.getString("shippingAddress") != null ? doc.getString("shippingAddress") : "";
            String pay = doc.getString("paymentMethod") != null ? doc.getString("paymentMethod") : "";

            dbHelper.upsertOrderFromFirebase(orderId, oUserId, oDate, status, total, addr, pay);

            // Sync items if present
            if (doc.contains("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
                if (items != null) {
                    for (Map<String, Object> it : items) {
                        int bId = it.get("bookId") instanceof Number ? ((Number) it.get("bookId")).intValue() : 1;
                        String title = (String) it.get("title");
                        String author = (String) it.get("author");
                        String image = (String) it.get("image");
                        double price = it.get("price") instanceof Number ? ((Number) it.get("price")).doubleValue() : 0.0;
                        int qty = it.get("quantity") instanceof Number ? ((Number) it.get("quantity")).intValue() : 1;
                        dbHelper.upsertOrderItemFromFirebase(orderId, bId, title, author, image, price, qty);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private List<Order> getLocalUserOrders(int userId) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, user_id, order_date, status, total_amount, shipping_address, payment_method FROM "
                + DatabaseHelper.TABLE_ORDERS + " WHERE user_id = ? ORDER BY id DESC", new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Order order = new Order(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getString(5),
                        cursor.getString(6)
                );
                list.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public LiveData<Resource<Boolean>> cancelOrder(int orderId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.cancelOrder(orderId);
                if (rows > 0) {
                    mFirestore.collection("orders").document(String.valueOf(orderId)).update("status", "CANCELLED");
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Không thể hủy đơn hàng", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi hủy đơn hàng: " + e.getMessage(), false));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Book>>> getBooksInOrder(int orderId) {
        MutableLiveData<Resource<List<Book>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                List<Book> books = dbHelper.getBooksInOrder(orderId);
                result.postValue(Resource.success(books));
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi tải sách trong đơn: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<Order>>> getAllOrdersForAdmin(String statusFilter) {
        MutableLiveData<Resource<List<Order>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                List<Order> orders = dbHelper.getAllOrdersForAdmin(statusFilter);
                result.postValue(Resource.success(orders));

                // Fetch remote orders from Firestore
                mFirestore.collection("orders").get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                appExecutors.diskIO().execute(() -> {
                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                        syncOrderDocToSqlite(doc, 2);
                                    }
                                    List<Order> updated = dbHelper.getAllOrdersForAdmin(statusFilter);
                                    result.postValue(Resource.success(updated));
                                });
                            }
                        });
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi tải đơn hàng: " + e.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> updateOrderStatus(int orderId, String newStatus) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        appExecutors.diskIO().execute(() -> {
            try {
                int rows = dbHelper.updateOrderStatus(orderId, newStatus);
                if (rows > 0) {
                    mFirestore.collection("orders").document(String.valueOf(orderId)).update("status", newStatus);
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("Không thể cập nhật trạng thái đơn", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("Lỗi cập nhật trạng thái đơn: " + e.getMessage(), false));
            }
        });

        return result;
    }
}

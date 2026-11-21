package com.example.book_selling_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.book_selling_app.models.Address;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.models.Order;
import com.example.book_selling_app.models.OrderItem;
import com.example.book_selling_app.models.Review;
import com.example.book_selling_app.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 4;

    // Table: Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "full_name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ADDRESS = "address";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role"; // "USER" or "ADMIN"
    public static final String COLUMN_USER_ACTIVE = "is_active"; // 1 active, 0 disabled

    // Table: Categories
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CAT_ID = "id";
    public static final String COLUMN_CAT_NAME = "name";

    // Table: Books
    public static final String TABLE_BOOKS = "books";
    public static final String COLUMN_BOOK_ID = "id";
    public static final String COLUMN_BOOK_CAT_ID = "category_id";
    public static final String COLUMN_BOOK_TITLE = "title";
    public static final String COLUMN_BOOK_AUTHOR = "author";
    public static final String COLUMN_BOOK_PRICE = "price";
    public static final String COLUMN_BOOK_ORIGINAL_PRICE = "original_price";
    public static final String COLUMN_BOOK_DISCOUNT = "discount";
    public static final String COLUMN_BOOK_RATING = "rating";
    public static final String COLUMN_BOOK_REVIEWS = "review_count";
    public static final String COLUMN_BOOK_IMAGE = "image_url";
    public static final String COLUMN_BOOK_DESC = "description";
    public static final String COLUMN_BOOK_STOCK = "stock";
    public static final String COLUMN_BOOK_ACTIVE = "is_active";

    // Table: Cart
    public static final String TABLE_CART = "cart";
    public static final String COLUMN_CART_ID = "id";
    public static final String COLUMN_CART_USER_ID = "user_id";
    public static final String COLUMN_CART_BOOK_ID = "book_id";
    public static final String COLUMN_CART_QUANTITY = "quantity";

    // Table: Orders
    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "id";
    public static final String COLUMN_ORDER_USER_ID = "user_id";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_ORDER_STATUS = "status"; // PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED
    public static final String COLUMN_ORDER_TOTAL = "total_amount";
    public static final String COLUMN_ORDER_ADDRESS = "shipping_address";
    public static final String COLUMN_ORDER_PAYMENT = "payment_method";

    // Table: Order Items
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String COLUMN_ITEM_ID = "id";
    public static final String COLUMN_ITEM_ORDER_ID = "order_id";
    public static final String COLUMN_ITEM_BOOK_ID = "book_id";
    public static final String COLUMN_ITEM_QUANTITY = "quantity";
    public static final String COLUMN_ITEM_PRICE = "price";

    // Table: Reviews
    public static final String TABLE_REVIEWS = "reviews";
    public static final String COLUMN_REV_ID = "id";
    public static final String COLUMN_REV_USER_ID = "user_id";
    public static final String COLUMN_REV_BOOK_ID = "book_id";
    public static final String COLUMN_REV_RATING = "rating";
    public static final String COLUMN_REV_COMMENT = "comment";
    public static final String COLUMN_REV_DATE = "review_date";

    // Table: Favorites
    public static final String TABLE_FAVORITES = "favorites";
    public static final String COLUMN_FAV_ID = "id";
    public static final String COLUMN_FAV_USER_ID = "user_id";
    public static final String COLUMN_FAV_BOOK_ID = "book_id";
    public static final String COLUMN_FAV_DATE = "created_at";

    // Table: Addresses
    public static final String TABLE_ADDRESSES = "addresses";
    public static final String COLUMN_ADDR_ID = "id";
    public static final String COLUMN_ADDR_USER_ID = "user_id";
    public static final String COLUMN_ADDR_NAME = "recipient_name";
    public static final String COLUMN_ADDR_PHONE = "phone";
    public static final String COLUMN_ADDR_STREET = "street_address";
    public static final String COLUMN_ADDR_LABEL = "label";
    public static final String COLUMN_ADDR_IS_DEFAULT = "is_default";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_EMAIL + " TEXT UNIQUE, "
                + COLUMN_USER_PHONE + " TEXT, "
                + COLUMN_USER_ADDRESS + " TEXT, "
                + COLUMN_USER_PASSWORD + " TEXT, "
                + COLUMN_USER_ROLE + " TEXT DEFAULT 'USER', "
                + COLUMN_USER_ACTIVE + " INTEGER DEFAULT 1)";
        db.execSQL(createUsersTable);

        // Create Categories Table
        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " ("
                + COLUMN_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CAT_NAME + " TEXT UNIQUE)";
        db.execSQL(createCategoriesTable);

        // Create Books Table
        String createBooksTable = "CREATE TABLE " + TABLE_BOOKS + " ("
                + COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BOOK_CAT_ID + " INTEGER, "
                + COLUMN_BOOK_TITLE + " TEXT, "
                + COLUMN_BOOK_AUTHOR + " TEXT, "
                + COLUMN_BOOK_PRICE + " REAL, "
                + COLUMN_BOOK_ORIGINAL_PRICE + " REAL, "
                + COLUMN_BOOK_DISCOUNT + " INTEGER, "
                + COLUMN_BOOK_RATING + " REAL, "
                + COLUMN_BOOK_REVIEWS + " INTEGER, "
                + COLUMN_BOOK_IMAGE + " TEXT, "
                + COLUMN_BOOK_DESC + " TEXT, "
                + COLUMN_BOOK_STOCK + " INTEGER, "
                + COLUMN_BOOK_ACTIVE + " INTEGER DEFAULT 1, "
                + "FOREIGN KEY(" + COLUMN_BOOK_CAT_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CAT_ID + "))";
        db.execSQL(createBooksTable);

        // Create Cart Table
        String createCartTable = "CREATE TABLE " + TABLE_CART + " ("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CART_USER_ID + " INTEGER, "
                + COLUMN_CART_BOOK_ID + " INTEGER, "
                + COLUMN_CART_QUANTITY + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_CART_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "))";
        db.execSQL(createCartTable);

        // Create Orders Table
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " ("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ORDER_USER_ID + " INTEGER, "
                + COLUMN_ORDER_DATE + " TEXT, "
                + COLUMN_ORDER_STATUS + " TEXT DEFAULT 'PENDING', "
                + COLUMN_ORDER_TOTAL + " REAL, "
                + COLUMN_ORDER_ADDRESS + " TEXT, "
                + COLUMN_ORDER_PAYMENT + " TEXT)";
        db.execSQL(createOrdersTable);

        // Create Order Items Table
        String createOrderItemsTable = "CREATE TABLE " + TABLE_ORDER_ITEMS + " ("
                + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ITEM_ORDER_ID + " INTEGER, "
                + COLUMN_ITEM_BOOK_ID + " INTEGER, "
                + COLUMN_ITEM_QUANTITY + " INTEGER, "
                + COLUMN_ITEM_PRICE + " REAL, "
                + "FOREIGN KEY(" + COLUMN_ITEM_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_ITEM_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "))";
        db.execSQL(createOrderItemsTable);

        // Create Reviews Table
        String createReviewsTable = "CREATE TABLE " + TABLE_REVIEWS + " ("
                + COLUMN_REV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_REV_USER_ID + " INTEGER, "
                + COLUMN_REV_BOOK_ID + " INTEGER, "
                + COLUMN_REV_RATING + " REAL, "
                + COLUMN_REV_COMMENT + " TEXT, "
                + COLUMN_REV_DATE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_REV_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_REV_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "))";
        db.execSQL(createReviewsTable);

        // Create Favorites Table
        String createFavoritesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + " ("
                + COLUMN_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FAV_USER_ID + " INTEGER, "
                + COLUMN_FAV_BOOK_ID + " INTEGER, "
                + COLUMN_FAV_DATE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_FAV_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_FAV_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "), "
                + "UNIQUE(" + COLUMN_FAV_USER_ID + ", " + COLUMN_FAV_BOOK_ID + "))";
        db.execSQL(createFavoritesTable);

        // Create Addresses Table
        String createAddressesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESSES + " ("
                + COLUMN_ADDR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADDR_USER_ID + " INTEGER, "
                + COLUMN_ADDR_NAME + " TEXT, "
                + COLUMN_ADDR_PHONE + " TEXT, "
                + COLUMN_ADDR_STREET + " TEXT, "
                + COLUMN_ADDR_LABEL + " TEXT DEFAULT 'Nhà riêng', "
                + COLUMN_ADDR_IS_DEFAULT + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COLUMN_ADDR_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createAddressesTable);

        // Seed Sample Data
        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        ensureRichSampleData(db);
    }

    private int getUserIdByEmail(SQLiteDatabase db, String email) {
        Cursor c = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
        int id = -1;
        if (c != null && c.moveToFirst()) {
            id = c.getInt(0);
            c.close();
        } else if (c != null) {
            c.close();
        }
        return id;
    }

    private int getBookIdByTitle(SQLiteDatabase db, String title) {
        Cursor c = db.query(TABLE_BOOKS, new String[]{COLUMN_BOOK_ID}, COLUMN_BOOK_TITLE + " = ?", new String[]{title}, null, null, null);
        int id = -1;
        if (c != null && c.moveToFirst()) {
            id = c.getInt(0);
            c.close();
        } else if (c != null) {
            c.close();
        }
        return id;
    }

    private boolean hasUser(SQLiteDatabase db, String email) {
        Cursor c = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
        boolean exists = (c != null && c.getCount() > 0);
        if (c != null) c.close();
        return exists;
    }

    private int getOrCreateCategory(SQLiteDatabase db, String catName) {
        Cursor c = db.query(TABLE_CATEGORIES, new String[]{COLUMN_CAT_ID}, COLUMN_CAT_NAME + " = ?", new String[]{catName}, null, null, null);
        if (c != null && c.moveToFirst()) {
            int id = c.getInt(0);
            c.close();
            return id;
        }
        if (c != null) c.close();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CAT_NAME, catName);
        return (int) db.insert(TABLE_CATEGORIES, null, cv);
    }

    private void insertBookIfMissing(SQLiteDatabase db, int catId, String title, String author, double price,
                                    double origPrice, int discount, double rating, int reviews, String image, String desc, int stock) {
        Cursor c = db.query(TABLE_BOOKS, new String[]{COLUMN_BOOK_ID}, COLUMN_BOOK_TITLE + " = ?", new String[]{title}, null, null, null);
        if (c != null && c.moveToFirst()) {
            int existingId = c.getInt(0);
            c.close();
            ContentValues upd = new ContentValues();
            upd.put(COLUMN_BOOK_CAT_ID, catId);
            upd.put(COLUMN_BOOK_AUTHOR, author);
            upd.put(COLUMN_BOOK_PRICE, price);
            upd.put(COLUMN_BOOK_ORIGINAL_PRICE, origPrice);
            upd.put(COLUMN_BOOK_DISCOUNT, discount);
            db.update(TABLE_BOOKS, upd, COLUMN_BOOK_ID + " = ?", new String[]{String.valueOf(existingId)});
            return;
        }
        if (c != null) c.close();
        insertBookInternal(db, catId, title, author, price, origPrice, discount, rating, reviews, image, desc, stock);
    }

    private void insertBookInternal(SQLiteDatabase db, int catId, String title, String author, double price,
                                    double origPrice, int discount, double rating, int reviews, String image, String desc, int stock) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BOOK_CAT_ID, catId);
        cv.put(COLUMN_BOOK_TITLE, title);
        cv.put(COLUMN_BOOK_AUTHOR, author);
        cv.put(COLUMN_BOOK_PRICE, price);
        cv.put(COLUMN_BOOK_ORIGINAL_PRICE, origPrice);
        cv.put(COLUMN_BOOK_DISCOUNT, discount);
        cv.put(COLUMN_BOOK_RATING, rating);
        cv.put(COLUMN_BOOK_REVIEWS, reviews);
        cv.put(COLUMN_BOOK_IMAGE, image);
        cv.put(COLUMN_BOOK_DESC, desc);
        cv.put(COLUMN_BOOK_STOCK, stock);
        cv.put(COLUMN_BOOK_ACTIVE, 1);
        db.insert(TABLE_BOOKS, null, cv);
    }

    private void insertAddressIfMissing(SQLiteDatabase db, int userId, String name, String phone, String street, String label, int isDefault) {
        Cursor c = db.query(TABLE_ADDRESSES, new String[]{COLUMN_ADDR_ID},
                COLUMN_ADDR_USER_ID + " = ? AND " + COLUMN_ADDR_STREET + " = ?",
                new String[]{String.valueOf(userId), street}, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            return;
        }
        if (c != null) c.close();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDR_USER_ID, userId);
        cv.put(COLUMN_ADDR_NAME, name);
        cv.put(COLUMN_ADDR_PHONE, phone);
        cv.put(COLUMN_ADDR_STREET, street);
        cv.put(COLUMN_ADDR_LABEL, label);
        cv.put(COLUMN_ADDR_IS_DEFAULT, isDefault);
        db.insert(TABLE_ADDRESSES, null, cv);
    }

    private void insertFavoriteIfMissing(SQLiteDatabase db, int userId, int bookId) {
        if (bookId <= 0 || userId <= 0) return;
        Cursor c = db.query(TABLE_FAVORITES, new String[]{COLUMN_FAV_ID},
                COLUMN_FAV_USER_ID + " = ? AND " + COLUMN_FAV_BOOK_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(bookId)}, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            return;
        }
        if (c != null) c.close();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FAV_USER_ID, userId);
        cv.put(COLUMN_FAV_BOOK_ID, bookId);
        cv.put(COLUMN_FAV_DATE, "26/09/2026 10:00");
        db.insert(TABLE_FAVORITES, null, cv);
    }

    private void insertReviewIfMissing(SQLiteDatabase db, int userId, int bookId, float rating, String comment, String date) {
        if (bookId <= 0 || userId <= 0) return;
        Cursor c = db.query(TABLE_REVIEWS, new String[]{COLUMN_REV_ID},
                COLUMN_REV_USER_ID + " = ? AND " + COLUMN_REV_BOOK_ID + " = ? AND " + COLUMN_REV_COMMENT + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(bookId), comment}, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            return;
        }
        if (c != null) c.close();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REV_USER_ID, userId);
        cv.put(COLUMN_REV_BOOK_ID, bookId);
        cv.put(COLUMN_REV_RATING, rating);
        cv.put(COLUMN_REV_COMMENT, comment);
        cv.put(COLUMN_REV_DATE, date);
        db.insert(TABLE_REVIEWS, null, cv);
        updateBookRatingAndCount(db, bookId);
    }

    private void insertOrderWithItemsIfMissing(SQLiteDatabase db, int userId, String date, String status, double total,
                                              String address, String payment, int[] bookIds, int[] quantities, double[] prices) {
        Cursor c = db.query(TABLE_ORDERS, new String[]{COLUMN_ORDER_ID},
                COLUMN_ORDER_USER_ID + " = ? AND " + COLUMN_ORDER_DATE + " = ? AND " + COLUMN_ORDER_STATUS + " = ?",
                new String[]{String.valueOf(userId), date, status}, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            return;
        }
        if (c != null) c.close();
        ContentValues orderCv = new ContentValues();
        orderCv.put(COLUMN_ORDER_USER_ID, userId);
        orderCv.put(COLUMN_ORDER_DATE, date);
        orderCv.put(COLUMN_ORDER_STATUS, status);
        orderCv.put(COLUMN_ORDER_TOTAL, total);
        orderCv.put(COLUMN_ORDER_ADDRESS, address);
        orderCv.put(COLUMN_ORDER_PAYMENT, payment);
        long orderId = db.insert(TABLE_ORDERS, null, orderCv);
        if (orderId > 0 && bookIds != null) {
            for (int i = 0; i < bookIds.length; i++) {
                if (bookIds[i] > 0) {
                    ContentValues itemCv = new ContentValues();
                    itemCv.put(COLUMN_ITEM_ORDER_ID, orderId);
                    itemCv.put(COLUMN_ITEM_BOOK_ID, bookIds[i]);
                    itemCv.put(COLUMN_ITEM_QUANTITY, quantities[i]);
                    itemCv.put(COLUMN_ITEM_PRICE, prices[i]);
                    db.insert(TABLE_ORDER_ITEMS, null, itemCv);
                }
            }
        }
    }

    public void ensureRichSampleData(SQLiteDatabase db) {
        // 1. Users
        if (!hasUser(db, "admin@bookstore.vn")) {
            ContentValues admin = new ContentValues();
            admin.put(COLUMN_USER_NAME, "Quản Trị Viên");
            admin.put(COLUMN_USER_EMAIL, "admin@bookstore.vn");
            admin.put(COLUMN_USER_PHONE, "0901234567");
            admin.put(COLUMN_USER_ADDRESS, "Phòng Quản Trị Hệ Thống, BookStore");
            admin.put(COLUMN_USER_PASSWORD, "admin123");
            admin.put(COLUMN_USER_ROLE, "ADMIN");
            admin.put(COLUMN_USER_ACTIVE, 1);
            db.insert(TABLE_USERS, null, admin);
        }

        if (!hasUser(db, "user@gmail.com")) {
            ContentValues user = new ContentValues();
            user.put(COLUMN_USER_NAME, "Nguyễn Văn A");
            user.put(COLUMN_USER_EMAIL, "user@gmail.com");
            user.put(COLUMN_USER_PHONE, "0987654321");
            user.put(COLUMN_USER_ADDRESS, "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức");
            user.put(COLUMN_USER_PASSWORD, "123456");
            user.put(COLUMN_USER_ROLE, "USER");
            user.put(COLUMN_USER_ACTIVE, 1);
            db.insert(TABLE_USERS, null, user);
        }

        if (!hasUser(db, "mai.tran@gmail.com")) {
            ContentValues user2 = new ContentValues();
            user2.put(COLUMN_USER_NAME, "Trần Thị Mai");
            user2.put(COLUMN_USER_EMAIL, "mai.tran@gmail.com");
            user2.put(COLUMN_USER_PHONE, "0912345678");
            user2.put(COLUMN_USER_ADDRESS, "123 Nguyễn Huệ, Quận 1, TP. HCM");
            user2.put(COLUMN_USER_PASSWORD, "123456");
            user2.put(COLUMN_USER_ROLE, "USER");
            user2.put(COLUMN_USER_ACTIVE, 1);
            db.insert(TABLE_USERS, null, user2);
        }

        if (!hasUser(db, "nam.le@gmail.com")) {
            ContentValues user3 = new ContentValues();
            user3.put(COLUMN_USER_NAME, "Lê Hoàng Nam");
            user3.put(COLUMN_USER_EMAIL, "nam.le@gmail.com");
            user3.put(COLUMN_USER_PHONE, "0934567890");
            user3.put(COLUMN_USER_ADDRESS, "45 Lê Duẩn, Quận 1, TP. HCM");
            user3.put(COLUMN_USER_PASSWORD, "123456");
            user3.put(COLUMN_USER_ROLE, "USER");
            user3.put(COLUMN_USER_ACTIVE, 1);
            db.insert(TABLE_USERS, null, user3);
        }

        if (!hasUser(db, "ha.pham@gmail.com")) {
            ContentValues user4 = new ContentValues();
            user4.put(COLUMN_USER_NAME, "Phạm Thu Hà");
            user4.put(COLUMN_USER_EMAIL, "ha.pham@gmail.com");
            user4.put(COLUMN_USER_PHONE, "0977889900");
            user4.put(COLUMN_USER_ADDRESS, "88 Hai Bà Trưng, Quận 3, TP. HCM");
            user4.put(COLUMN_USER_PASSWORD, "123456");
            user4.put(COLUMN_USER_ROLE, "USER");
            user4.put(COLUMN_USER_ACTIVE, 1);
            db.insert(TABLE_USERS, null, user4);
        }

        // 2. Categories
        int catKinhTe = getOrCreateCategory(db, "Kinh Tế");
        int catVanHoc = getOrCreateCategory(db, "Văn Học");
        int catKyNang = getOrCreateCategory(db, "Kỹ Năng Sống");
        int catCongNghe = getOrCreateCategory(db, "Công Nghệ");
        int catTamLy = getOrCreateCategory(db, "Tâm Lý Học");
        int catThieuNhi = getOrCreateCategory(db, "Thiếu Nhi");

        // 3. Books (35 rich titles)
        // --- Danh mục Kinh Tế ---
        insertBookIfMissing(db, catKinhTe, "Cha Giàu Cha Nghèo (Rich Dad Poor Dad)", "Robert Kiyosaki", 115000, 145000, 21, 4.9, 2800,
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400",
                "Bí quyết quản lý tài chính cá nhân và tư duy làm giàu kinh điển giúp bạn đạt được tự do tài chính vững bền.", 85);

        insertBookIfMissing(db, catKinhTe, "Dạy Con Làm Giàu - Tập 2: Sử Dụng Đồng Vốn", "Robert Kiyosaki", 105000, 130000, 19, 4.8, 1450,
                "https://images.unsplash.com/photo-1553729459-efe14ef6055d?w=400",
                "Phân loại nhóm người làm việc theo Kim Tứ Đồ và chiến lược dịch chuyển sang nhóm chủ doanh nghiệp và nhà đầu tư.", 60);

        insertBookIfMissing(db, catKinhTe, "Nghĩ Giàu Và Làm Giàu (Think and Grow Rich)", "Napoleon Hill", 95000, 120000, 21, 4.7, 1980,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400",
                "13 nguyên tắc vàng về làm giàu được đúc kết qua 25 năm nghiên cứu hàng trăm tỷ phú lừng danh thế giới.", 70);

        insertBookIfMissing(db, catKinhTe, "Chiến Tranh Tiền Tệ", "Song Hongbing", 168000, 210000, 20, 4.6, 890,
                "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=400",
                "Khám phá bức màn bí mật phía sau các gia tộc tài phiệt và lịch sử các cuộc khủng hoảng tài chính toàn cầu.", 40);

        insertBookIfMissing(db, catKinhTe, "Kinh Tế Học Hài Hước (Freakonomics)", "Steven D. Levitt", 125000, 155000, 19, 4.8, 640,
                "https://images.unsplash.com/photo-1592496431122-2349e0fbc666?w=400",
                "Góc nhìn dí dỏm, độc đáo giải mã bản chất các hiện tượng xã hội đời thường thông qua lăng kính kinh tế học.", 55);

        // --- Danh mục Văn Học ---
        insertBookIfMissing(db, catVanHoc, "Nhà Giả Kim", "Paulo Coelho", 69000, 89000, 22, 4.8, 3200,
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400",
                "Kiệt tác văn học kinh điển được dịch ra nhiều thứ tiếng nhất hành tinh, truyền cảm hứng mạnh mẽ theo đuổi vận mệnh.", 95);

        insertBookIfMissing(db, catVanHoc, "Phút Chốc Tinh Khôi", "Paulo Coelho", 92000, 115000, 20, 4.7, 540,
                "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400",
                "Những mẩu chuyện chiêm nghiệm đầy chất thơ về tình yêu, sự bao dung và hành trình lắng nghe tiếng gọi trái tim.", 50);

        insertBookIfMissing(db, catVanHoc, "Chiến Binh Ánh Sáng", "Paulo Coelho", 85000, 105000, 19, 4.8, 720,
                "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400",
                "Cẩm nang tinh thần bất hủ dẫn dắt mỗi người trở thành chiến binh can đảm theo đuổi ước mơ giữa giông bão cuộc đời.", 45);

        insertBookIfMissing(db, catVanHoc, "Cây Cam Ngọt Của Tôi", "José Mauro de Vasconcelos", 85000, 108000, 21, 4.9, 2100,
                "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400",
                "Câu chuyện cảm động rơi nước mắt về tuổi thơ đầy mất mát nhưng ngập tràn tình yêu thương của chú bé Zezé.", 80);

        insertBookIfMissing(db, catVanHoc, "Cho Tôi Xin Một Vé Đi Tuổi Thơ", "Nguyễn Nhật Ánh", 78000, 98000, 20, 4.9, 1850,
                "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=400",
                "Chuyến tàu ký ức đưa người đọc trở về với miền tuổi thơ ngộ nghĩnh, tinh nghịch và vô tư lự.", 90);

        insertBookIfMissing(db, catVanHoc, "Tôi Thấy Hoa Vàng Trên Cỏ Xanh", "Nguyễn Nhật Ánh", 99000, 125000, 21, 4.8, 2300,
                "https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=400",
                "Bức tranh đồng quê thanh bình, tình anh em gắn bó và những rung động đầu đời trong sáng, ngọt ngào.", 85);

        insertBookIfMissing(db, catVanHoc, "Mắt Biếc", "Nguyễn Nhật Ánh", 88000, 110000, 20, 4.8, 2750,
                "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400",
                "Mối tình đơn phương da diết của chàng trai Ngạn dành cho cô bạn thanh mai trúc mã qua bao thăng trầm năm tháng.", 75);

        insertBookIfMissing(db, catVanHoc, "Rừng Na Uy (Norwegian Wood)", "Haruki Murakami", 125000, 150000, 17, 4.7, 1600,
                "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=400",
                "Tiểu thuyết lãng mạn pha lẫn nỗi u uất của tuổi trẻ Nhật Bản những năm 1960 với sự cô đơn và khát khao đồng điệu.", 60);

        insertBookIfMissing(db, catVanHoc, "Bố Già (The Godfather)", "Mario Puzo", 135000, 170000, 21, 4.9, 1950,
                "https://images.unsplash.com/photo-1519682577862-22b62b24e493?w=400",
                "Kiệt tác tiểu thuyết kinh điển khắc họa thế giới ngầm mafia và những giá trị tình thân sắt đá của gia tộc Corleone.", 65);

        // --- Danh mục Kỹ Năng Sống ---
        insertBookIfMissing(db, catKyNang, "Đắc Nhân Tâm", "Dale Carnegie", 78000, 98000, 20, 4.9, 3400,
                "https://images.unsplash.com/photo-1491841573634-28140fc7ced7?w=400",
                "Cuốn sách nghệ thuật thu phục lòng người và dẫn lối đến thành công trong giao tiếp và các mối quan hệ xã hội.", 100);

        insertBookIfMissing(db, catKyNang, "Quẳng Gánh Lo Đi Và Vui Sống", "Dale Carnegie", 82000, 105000, 22, 4.8, 1750,
                "https://images.unsplash.com/photo-1528459801416-a9e53bbf4e17?w=400",
                "Cẩm nang thiết thực giúp bạn xóa bỏ những lo âu, căng thẳng thường nhật để sống an nhiên và hạnh phúc trọn vẹn.", 70);

        insertBookIfMissing(db, catKyNang, "Thói Quen Nguyên Tử (Atomic Habits)", "James Clear", 149000, 189000, 21, 4.9, 3800,
                "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400",
                "Thay đổi tí hon, hiệu quả bất ngờ. Chiến lược từng bước khoa học để xây dựng thói quen tốt và từ bỏ thói quen xấu.", 90);

        insertBookIfMissing(db, catKyNang, "Sức Mạnh Của Thói Quen (The Power of Habit)", "Charles Duhigg", 110000, 140000, 21, 4.7, 1200,
                "https://images.unsplash.com/photo-1510172951991-856a654063f9?w=400",
                "Khám phá cơ chế vòng lặp thói quen trong não bộ con người và phương pháp tái định hình chúng để thành công.", 55);

        insertBookIfMissing(db, catKyNang, "Đi Tìm Lẽ Sống (Man's Search for Meaning)", "Viktor E. Frankl", 95000, 119000, 20, 4.9, 1650,
                "https://images.unsplash.com/photo-1521587760476-6c12a4b040da?w=400",
                "Hồi ký xúc động về trải nghiệm vượt qua trại tập trung Đức Quốc Xã và liệu pháp ý nghĩa cuộc sống bất diệt.", 60);

        insertBookIfMissing(db, catKyNang, "Giận (Anger)", "Thích Nhất Hạnh", 86000, 108000, 20, 4.8, 1400,
                "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=400",
                "Những bài học thiền định nhẹ nhàng giúp chuyển hóa cơn giận dữ thành sự thấu hiểu, yêu thương và bình yên tâm hồn.", 65);

        // --- Danh mục Công Nghệ ---
        insertBookIfMissing(db, catCongNghe, "Lập Trình Di Động Nâng Cao", "TS. Nguyễn Văn Hùng", 120000, 150000, 20, 4.7, 420,
                "https://images.unsplash.com/photo-1532012164546-f432f2e3edd3?w=400",
                "Giáo trình lập trình di động thực chiến với kiến trúc MVVM hiện đại, SQLite và xử lý giao diện chuyên sâu.", 40);

        insertBookIfMissing(db, catCongNghe, "Giáo Trình Cấu Trúc Dữ Liệu & Giải Thuật", "TS. Nguyễn Văn Hùng", 98000, 125000, 22, 4.6, 380,
                "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400",
                "Nền tảng cấu trúc dữ liệu mảng, danh sách liên kết, cây, đồ thị cùng các giải thuật sắp xếp, tìm kiếm tối ưu.", 50);

        insertBookIfMissing(db, catCongNghe, "Clean Code (Mã Sạch)", "Robert C. Martin", 210000, 260000, 19, 4.9, 2150,
                "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400",
                "Quy tắc vàng của lập trình viên chuyên nghiệp: viết mã nguồn rõ ràng, dễ bảo trì, dễ mở rộng và ít lỗi nhất.", 55);

        insertBookIfMissing(db, catCongNghe, "Clean Architecture (Kiến Trúc Sạch)", "Robert C. Martin", 235000, 290000, 19, 4.8, 1350,
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400",
                "Hướng dẫn phân tách kiến trúc phần mềm độc lập với framework, cơ sở dữ liệu và UI, tạo nền tảng vững bền cho hệ thống lớn.", 45);

        insertBookIfMissing(db, catCongNghe, "Thiết Kế Hệ Thống Quy Mô Lớn (System Design)", "Alex Xu", 280000, 350000, 20, 4.9, 1820,
                "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400",
                "Bí quyết vượt qua phỏng vấn thiết kế hệ thống và xây dựng các dịch vụ phục vụ hàng triệu người dùng đồng thời.", 35);

        // --- Danh mục Tâm Lý Học ---
        insertBookIfMissing(db, catTamLy, "Tư Duy Nhanh Và Chậm (Thinking, Fast and Slow)", "Daniel Kahneman", 175000, 220000, 20, 4.7, 980,
                "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400",
                "Khám phá hai hệ thống tư duy chi phối mọi quyết định của con người: Hệ thống 1 trực giác và Hệ thống 2 lý tính.", 45);

        insertBookIfMissing(db, catTamLy, "Nghệ Thuật Tinh Tế Của Việc Đếch Quan Tâm", "Mark Manson", 92000, 115000, 20, 4.7, 2200,
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400",
                "Tiếp cận cuộc sống thực tế, không ảo tưởng: biết chọn lọc điều gì thực sự quan trọng để bớt áp lực và sống tự do.", 80);

        insertBookIfMissing(db, catTamLy, "Tâm Lý Học Đám Đông", "Gustave Le Bon", 89000, 110000, 19, 4.6, 750,
                "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400",
                "Khảo cứu sâu sắc và kinh điển về quy luật hành vi, cảm xúc và cách tư duy của các tập thể và đám đông.", 50);

        insertBookIfMissing(db, catTamLy, "Bẫy Thao Túng Tâm Lý", "Jackson MacKenzie", 105000, 135000, 22, 4.7, 860,
                "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400",
                "Nhận diện các mối quan hệ độc hại, cơ chế thao túng tâm lý và phương thức chữa lành cảm xúc bản thân.", 50);

        insertBookIfMissing(db, catTamLy, "Hiểu Về Trái Tim", "Thích Minh Niệm", 118000, 148000, 20, 4.9, 2500,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400",
                "Cuốn sách chữa lành tâm hồn, soi rọi nội tâm và tìm về bình yên đích thực trong tình yêu và các mối quan hệ đời thường.", 70);

        // --- Danh mục Thiếu Nhi ---
        insertBookIfMissing(db, catThieuNhi, "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", 65000, 85000, 24, 4.9, 1950,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400",
                "Tác phẩm thiếu nhi kinh điển của văn học Việt Nam kể về hành trình chu du, trưởng thành và bài học tình bạn của chú Dế Mèn.", 90);

        insertBookIfMissing(db, catThieuNhi, "Hoàng Tử Bé (Le Petit Prince)", "Antoine de Saint-Exupéry", 58000, 75000, 23, 4.9, 2600,
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400",
                "Câu chuyện ngụ ngôn giàu chất thơ về tình bạn, tình yêu và những bài học nhân văn sâu sắc chỉ có thể nhìn thấy bằng trái tim.", 85);

        insertBookIfMissing(db, catThieuNhi, "Không Gia Đình (Sans Famille)", "Hector Malot", 110000, 145000, 24, 4.8, 1150,
                "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400",
                "Hành trình lưu lạc đầy nghị lực và phẩm chất cao đẹp của cậu bé mồ côi Rémi cùng gánh xiếc rong cụ Vitalis.", 60);

        insertBookIfMissing(db, catThieuNhi, "Chuyện Con Mèo Dạy Hải Âu Bay", "Luis Sepúlveda", 62000, 79000, 22, 4.9, 1680,
                "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400",
                "Câu chuyện ấm áp, cảm động về lời hứa danh dự của chú mèo Zorba với chim hải âu mẹ và bài học chấp nhận sự khác biệt.", 75);

        insertBookIfMissing(db, catThieuNhi, "Harry Potter Và Hòn Đá Phù Thủy", "J.K. Rowling", 185000, 230000, 20, 4.9, 4200,
                "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400",
                "Mở đầu huyền thoại thế giới phù thủy Hogwarts cùng cậu bé Harry Potter và hai người bạn thân Ron, Hermione.", 100);

        insertBookIfMissing(db, catThieuNhi, "Harry Potter Và Phòng Chứa Bí Mật", "J.K. Rowling", 195000, 245000, 20, 4.9, 3100,
                "https://images.unsplash.com/photo-1519682577862-22b62b24e493?w=400",
                "Năm học thứ hai đầy kịch tính tại Hogwarts với những lời thì thầm bí ẩn trên tường và quái vật cổ xưa trỗi dậy.", 80);

        // 4. Addresses for User A (user@gmail.com)
        int uIdUser = getUserIdByEmail(db, "user@gmail.com");
        if (uIdUser > 0) {
            insertAddressIfMissing(db, uIdUser, "Nguyễn Văn A", "0987654321", "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức, TP. Hồ Chí Minh", "Trường ĐH SPKT (Nhà riêng)", 1);
            insertAddressIfMissing(db, uIdUser, "Nguyễn Văn A", "0987654321", "Tầng 12, Tòa nhà Bitexco, 2 Hải Triều, Bến Nghé, Quận 1, TP. HCM", "Văn phòng công ty", 0);
            insertAddressIfMissing(db, uIdUser, "Nguyễn Văn A", "0912987654", "Số 45 Đường 3/2, Phường 11, Quận 10, TP. Hồ Chí Minh", "Nhà bố mẹ", 0);
        }

        // 5. Reviews
        int uIdMai = getUserIdByEmail(db, "mai.tran@gmail.com");
        int uIdNam = getUserIdByEmail(db, "nam.le@gmail.com");
        int uIdHa = getUserIdByEmail(db, "ha.pham@gmail.com");

        int bNhaGiaKim = getBookIdByTitle(db, "Nhà Giả Kim");
        int bDacNhanTam = getBookIdByTitle(db, "Đắc Nhân Tâm");
        int bAtomicHabits = getBookIdByTitle(db, "Thói Quen Nguyên Tử (Atomic Habits)");
        int bCleanCode = getBookIdByTitle(db, "Clean Code (Mã Sạch)");
        int bCayCamNgot = getBookIdByTitle(db, "Cây Cam Ngọt Của Tôi");
        int bDeMen = getBookIdByTitle(db, "Dế Mèn Phiêu Lưu Ký");
        int bHarryPotter1 = getBookIdByTitle(db, "Harry Potter Và Hòn Đá Phù Thủy");
        int bToiThayHoaVang = getBookIdByTitle(db, "Tôi Thấy Hoa Vàng Trên Cỏ Xanh");
        int bChaGiauChaNgheo = getBookIdByTitle(db, "Cha Giàu Cha Nghèo (Rich Dad Poor Dad)");

        if (bNhaGiaKim > 0) {
            insertReviewIfMissing(db, uIdUser, bNhaGiaKim, 5.0f, "Cuốn sách làm thay đổi nhân sinh quan của mình. Ai cũng nên đọc ít nhất một lần!", "15/09/2026 10:20");
            insertReviewIfMissing(db, uIdMai, bNhaGiaKim, 5.0f, "Văn phong giản dị nhưng chứa đựng triết lý sâu sắc về việc theo đuổi ước mơ.", "18/09/2026 14:15");
            insertReviewIfMissing(db, uIdNam, bNhaGiaKim, 4.5f, "Bìa sách đẹp, giấy xịn, giao hàng đóng hộp rất cẩn thận.", "20/09/2026 09:30");
        }

        if (bDacNhanTam > 0) {
            insertReviewIfMissing(db, uIdMai, bDacNhanTam, 5.0f, "Bài học giao tiếp kinh điển, đọc đi đọc lại vẫn thấy thấm thía.", "10/09/2026 16:40");
            insertReviewIfMissing(db, uIdHa, bDacNhanTam, 4.8f, "Sách hay và có nhiều ví dụ thực tế gần gũi trong đời sống.", "12/09/2026 11:05");
        }

        if (bAtomicHabits > 0) {
            insertReviewIfMissing(db, uIdNam, bAtomicHabits, 5.0f, "Cực kỳ thực chiến! Mình đã áp dụng quy tắc 2 phút và thấy thói quen đọc sách cải thiện rõ rệt.", "22/09/2026 08:45");
            insertReviewIfMissing(db, uIdUser, bAtomicHabits, 5.0f, "Cuốn sách phát triển bản thân hay nhất mình từng đọc trong năm nay.", "25/09/2026 21:10");
        }

        if (bCleanCode > 0) {
            insertReviewIfMissing(db, uIdNam, bCleanCode, 5.0f, "Kinh thánh của anh em lập trình viên. Đọc xong viết code sạch và tự tin hơn hẳn.", "19/09/2026 17:30");
            insertReviewIfMissing(db, uIdUser, bCleanCode, 4.8f, "Ví dụ Java rất chi tiết, giải thích cặn kẽ vì sao không nên viết hàm quá dài.", "24/09/2026 15:20");
        }

        if (bCayCamNgot > 0) {
            insertReviewIfMissing(db, uIdMai, bCayCamNgot, 5.0f, "Đoạn cuối khóc sưng cả mắt, thương chú bé Zezé vô cùng. Một cuốn sách tuyệt vời!", "14/09/2026 20:00");
            insertReviewIfMissing(db, uIdHa, bCayCamNgot, 5.0f, "Gợi nhớ về tình yêu thương gia đình và tuổi thơ, nên đọc để trân trọng những người bên cạnh.", "16/09/2026 19:10");
        }

        if (bDeMen > 0) {
            insertReviewIfMissing(db, uIdHa, bDeMen, 5.0f, "Mua cho con mà mẹ cũng mê mẩn đọc lại. Hình minh họa màu rất đẹp mắt.", "21/09/2026 13:50");
        }

        if (bHarryPotter1 > 0) {
            insertReviewIfMissing(db, uIdUser, bHarryPotter1, 5.0f, "Tuổi thơ ùa về, bản in mới giấy xốp nhẹ tay, cầm đọc không bị mỏi.", "23/09/2026 10:00");
            insertReviewIfMissing(db, uIdMai, bHarryPotter1, 5.0f, "Cốt truyện kỳ ảo lôi cuốn từ trang đầu đến trang cuối. 10/10 điểm!", "25/09/2026 18:25");
        }

        if (bToiThayHoaVang > 0) {
            insertReviewIfMissing(db, uIdNam, bToiThayHoaVang, 4.8f, "Ngòi bút của bác Ánh lúc nào cũng mộc mạc mà đong đầy cảm xúc làng quê.", "17/09/2026 14:00");
        }

        // 6. Favorites for User A
        if (uIdUser > 0) {
            insertFavoriteIfMissing(db, uIdUser, bNhaGiaKim);
            insertFavoriteIfMissing(db, uIdUser, bAtomicHabits);
            insertFavoriteIfMissing(db, uIdUser, bCayCamNgot);
            insertFavoriteIfMissing(db, uIdUser, bCleanCode);
            insertFavoriteIfMissing(db, uIdUser, bHarryPotter1);
        }

        // 7. Orders with Items for User A
        if (uIdUser > 0) {
            insertOrderWithItemsIfMissing(db, uIdUser, "20/09/2026 10:15", "DELIVERED", 147000,
                    "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức", "Thanh toán khi nhận hàng (COD)",
                    new int[]{bNhaGiaKim, bDacNhanTam}, new int[]{1, 1}, new double[]{69000, 78000});

            insertOrderWithItemsIfMissing(db, uIdUser, "25/09/2026 09:30", "SHIPPING", 234000,
                    "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức", "Thanh toán khi nhận hàng (COD)",
                    new int[]{bAtomicHabits, bCayCamNgot}, new int[]{1, 1}, new double[]{149000, 85000});

            insertOrderWithItemsIfMissing(db, uIdUser, "26/09/2026 14:00", "CONFIRMED", 210000,
                    "Tầng 12, Tòa nhà Bitexco, 2 Hải Triều, Bến Nghé, Quận 1, TP. HCM", "Chuyển khoản ngân hàng",
                    new int[]{bCleanCode}, new int[]{1}, new double[]{210000});

            insertOrderWithItemsIfMissing(db, uIdUser, "26/09/2026 18:20", "PENDING", 185000,
                    "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức", "Ví điện tử MoMo",
                    new int[]{bHarryPotter1}, new int[]{1}, new double[]{185000});

            insertOrderWithItemsIfMissing(db, uIdUser, "15/09/2026 11:00", "CANCELLED", 115000,
                    "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức", "Thanh toán khi nhận hàng (COD)",
                    new int[]{bChaGiauChaNgheo}, new int[]{1}, new double[]{115000});
        }
    }

    // ==========================================
    // ADMIN DASHBOARD STATS
    // ==========================================

    public static class AdminStats {
        public double totalRevenue;
        public int totalOrders;
        public int pendingOrders;
        public int totalBooks;
        public int totalUsers;
        public Map<String, Integer> categoryDistribution = new LinkedHashMap<>();
        public Map<String, Double> dailyRevenue = new LinkedHashMap<>();
    }

    public AdminStats getAdminStats() {
        AdminStats stats = new AdminStats();
        SQLiteDatabase db = this.getReadableDatabase();

        // Total Revenue (From DELIVERED orders)
        Cursor cRev = db.rawQuery("SELECT SUM(" + COLUMN_ORDER_TOTAL + ") FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDER_STATUS + " = 'DELIVERED'", null);
        if (cRev != null && cRev.moveToFirst()) {
            stats.totalRevenue = cRev.getDouble(0);
            cRev.close();
        }

        // Total Orders
        Cursor cOrders = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ORDERS, null);
        if (cOrders != null && cOrders.moveToFirst()) {
            stats.totalOrders = cOrders.getInt(0);
            cOrders.close();
        }

        // Pending Orders
        Cursor cPending = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDER_STATUS + " = 'PENDING'", null);
        if (cPending != null && cPending.moveToFirst()) {
            stats.pendingOrders = cPending.getInt(0);
            cPending.close();
        }

        // Total Books
        Cursor cBooks = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKS, null);
        if (cBooks != null && cBooks.moveToFirst()) {
            stats.totalBooks = cBooks.getInt(0);
            cBooks.close();
        }

        // Total Users
        Cursor cUsers = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ROLE + " = 'USER'", null);
        if (cUsers != null && cUsers.moveToFirst()) {
            stats.totalUsers = cUsers.getInt(0);
            cUsers.close();
        }

        // Category Distribution
        String catQuery = "SELECT c.name, COUNT(b.id) FROM " + TABLE_CATEGORIES + " c "
                + " LEFT JOIN " + TABLE_BOOKS + " b ON c.id = b.category_id "
                + " GROUP BY c.id ORDER BY c.id ASC";
        Cursor cCat = db.rawQuery(catQuery, null);
        if (cCat != null && cCat.moveToFirst()) {
            do {
                String catName = cCat.getString(0);
                int count = cCat.getInt(1);
                if (catName != null) {
                    stats.categoryDistribution.put(catName, count);
                }
            } while (cCat.moveToNext());
            cCat.close();
        }

        // Daily Revenue from recent orders
        String revQuery = "SELECT substr(" + COLUMN_ORDER_DATE + ", 1, 5) as day, SUM(" + COLUMN_ORDER_TOTAL + ") "
                + " FROM " + TABLE_ORDERS
                + " GROUP BY day ORDER BY " + COLUMN_ORDER_ID + " ASC LIMIT 7";
        Cursor cDay = db.rawQuery(revQuery, null);
        if (cDay != null && cDay.moveToFirst()) {
            do {
                String day = cDay.getString(0);
                double amt = cDay.getDouble(1);
                if (day != null && !day.isEmpty()) {
                    stats.dailyRevenue.put(day, amt);
                }
            } while (cDay.moveToNext());
            cDay.close();
        }

        if (stats.dailyRevenue.isEmpty()) {
            stats.dailyRevenue.put("T2", 120000.0);
            stats.dailyRevenue.put("T3", 250000.0);
            stats.dailyRevenue.put("T4", 180000.0);
            stats.dailyRevenue.put("T5", 340000.0);
            stats.dailyRevenue.put("T6", 290000.0);
            stats.dailyRevenue.put("T7", 450000.0);
            stats.dailyRevenue.put("CN", stats.totalRevenue > 0 ? stats.totalRevenue : 520000.0);
        }

        return stats;
    }

    // ==========================================
    // BOOK CRUD (Admin & User)
    // ==========================================

    public List<Book> getAllBooks(boolean onlyActive) {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.id, b.title, b.author, b.price, b.original_price, b.discount, b.rating, b.review_count, b.image_url, b.description, b.stock, c.name, b.category_id, b.is_active "
                + " FROM " + TABLE_BOOKS + " b LEFT JOIN " + TABLE_CATEGORIES + " c ON b.category_id = c.id";
        if (onlyActive) {
            query += " WHERE b.is_active = 1";
        }
        query += " ORDER BY b.id DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(0));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setPrice(cursor.getDouble(3));
                book.setOriginalPrice(cursor.getDouble(4));
                book.setDiscount(cursor.getInt(5));
                book.setRating(cursor.getDouble(6));
                book.setReviewCount(cursor.getInt(7));
                book.setImageUrl(cursor.getString(8));
                book.setDescription(cursor.getString(9));
                book.setStock(cursor.getInt(10));
                book.setCategoryName(cursor.getString(11));
                book.setCategoryId(cursor.getInt(12));
                list.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Book> getBooksByCategory(int catId) {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT b.id, b.title, b.author, b.price, b.original_price, b.discount, b.rating, b.review_count, b.image_url, b.description, b.stock, c.name, b.category_id FROM "
                + TABLE_BOOKS + " b LEFT JOIN " + TABLE_CATEGORIES + " c ON b.category_id = c.id WHERE b.category_id = ? AND b.is_active = 1", new String[]{String.valueOf(catId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(0));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setPrice(cursor.getDouble(3));
                book.setOriginalPrice(cursor.getDouble(4));
                book.setDiscount(cursor.getInt(5));
                book.setRating(cursor.getDouble(6));
                book.setReviewCount(cursor.getInt(7));
                book.setImageUrl(cursor.getString(8));
                book.setDescription(cursor.getString(9));
                book.setStock(cursor.getInt(10));
                book.setCategoryName(cursor.getString(11));
                book.setCategoryId(cursor.getInt(12));
                list.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Book> searchBooks(String keyword, Integer categoryId, Double minPrice, Double maxPrice, Float minRating, String sortBy) {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT b.id, b.title, b.author, b.price, b.original_price, b.discount, b.rating, b.review_count, b.image_url, b.description, b.stock, c.name, b.category_id FROM "
                + TABLE_BOOKS + " b LEFT JOIN " + TABLE_CATEGORIES + " c ON b.category_id = c.id WHERE b.is_active = 1");
        List<String> args = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" AND (b.title LIKE ? OR b.author LIKE ?)");
            args.add("%" + keyword.trim() + "%");
            args.add("%" + keyword.trim() + "%");
        }

        if (categoryId != null && categoryId > 0) {
            query.append(" AND b.category_id = ?");
            args.add(String.valueOf(categoryId));
        }

        if (minPrice != null && minPrice > 0) {
            query.append(" AND b.price >= ?");
            args.add(String.valueOf(minPrice));
        }

        if (maxPrice != null && maxPrice > 0) {
            query.append(" AND b.price <= ?");
            args.add(String.valueOf(maxPrice));
        }

        if (minRating != null && minRating > 0) {
            query.append(" AND b.rating >= ?");
            args.add(String.valueOf(minRating));
        }

        if ("PRICE_ASC".equalsIgnoreCase(sortBy)) {
            query.append(" ORDER BY b.price ASC");
        } else if ("PRICE_DESC".equalsIgnoreCase(sortBy)) {
            query.append(" ORDER BY b.price DESC");
        } else if ("RATING".equalsIgnoreCase(sortBy)) {
            query.append(" ORDER BY b.rating DESC");
        } else {
            query.append(" ORDER BY b.id DESC");
        }

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(0));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setPrice(cursor.getDouble(3));
                book.setOriginalPrice(cursor.getDouble(4));
                book.setDiscount(cursor.getInt(5));
                book.setRating(cursor.getDouble(6));
                book.setReviewCount(cursor.getInt(7));
                book.setImageUrl(cursor.getString(8));
                book.setDescription(cursor.getString(9));
                book.setStock(cursor.getInt(10));
                book.setCategoryName(cursor.getString(11));
                book.setCategoryId(cursor.getInt(12));
                list.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public long insertBook(int catId, String title, String author, double price, double origPrice, int discount, String image, String desc, int stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BOOK_CAT_ID, catId);
        cv.put(COLUMN_BOOK_TITLE, title);
        cv.put(COLUMN_BOOK_AUTHOR, author);
        cv.put(COLUMN_BOOK_PRICE, price);
        cv.put(COLUMN_BOOK_ORIGINAL_PRICE, origPrice);
        cv.put(COLUMN_BOOK_DISCOUNT, discount);
        cv.put(COLUMN_BOOK_RATING, 5.0);
        cv.put(COLUMN_BOOK_REVIEWS, 0);
        cv.put(COLUMN_BOOK_IMAGE, image);
        cv.put(COLUMN_BOOK_DESC, desc);
        cv.put(COLUMN_BOOK_STOCK, stock);
        cv.put(COLUMN_BOOK_ACTIVE, 1);
        return db.insert(TABLE_BOOKS, null, cv);
    }

    public int updateBook(int id, int catId, String title, String author, double price, double origPrice, int discount, String image, String desc, int stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BOOK_CAT_ID, catId);
        cv.put(COLUMN_BOOK_TITLE, title);
        cv.put(COLUMN_BOOK_AUTHOR, author);
        cv.put(COLUMN_BOOK_PRICE, price);
        cv.put(COLUMN_BOOK_ORIGINAL_PRICE, origPrice);
        cv.put(COLUMN_BOOK_DISCOUNT, discount);
        cv.put(COLUMN_BOOK_IMAGE, image);
        cv.put(COLUMN_BOOK_DESC, desc);
        cv.put(COLUMN_BOOK_STOCK, stock);
        return db.update(TABLE_BOOKS, cv, COLUMN_BOOK_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKS, COLUMN_BOOK_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ==========================================
    // CATEGORY CRUD
    // ==========================================

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT c.id, c.name, (SELECT COUNT(*) FROM " + TABLE_BOOKS + " b WHERE b.category_id = c.id) as book_count FROM " + TABLE_CATEGORIES + " c", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(cursor.getInt(0));
                cat.setName(cursor.getString(1));
                cat.setBookCount(cursor.getInt(2));
                list.add(cat);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public long insertCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CAT_NAME, name);
        return db.insert(TABLE_CATEGORIES, null, cv);
    }

    public int updateCategory(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CAT_NAME, name);
        return db.update(TABLE_CATEGORIES, cv, COLUMN_CAT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public boolean isCategoryInUse(int catId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKS + " WHERE " + COLUMN_BOOK_CAT_ID + " = ?", new String[]{String.valueOf(catId)});
        boolean inUse = false;
        if (cursor != null && cursor.moveToFirst()) {
            inUse = cursor.getInt(0) > 0;
            cursor.close();
        }
        return inUse;
    }

    public int deleteCategory(int catId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CATEGORIES, COLUMN_CAT_ID + " = ?", new String[]{String.valueOf(catId)});
    }

    // ==========================================
    // ORDER MANAGEMENT (Admin & User)
    // ==========================================

    public List<Order> getAllOrdersForAdmin(String statusFilter) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o.id, o.user_id, o.order_date, o.status, o.total_amount, o.shipping_address, o.payment_method, u.full_name, u.phone "
                + " FROM " + TABLE_ORDERS + " o LEFT JOIN " + TABLE_USERS + " u ON o.user_id = u.id";
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equalsIgnoreCase("ALL")) {
            query += " WHERE o.status = '" + statusFilter + "'";
        }
        query += " ORDER BY o.id DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(0));
                order.setUserId(cursor.getInt(1));
                order.setOrderDate(cursor.getString(2));
                order.setStatus(cursor.getString(3));
                order.setTotalAmount(cursor.getDouble(4));
                order.setShippingAddress(cursor.getString(5));
                order.setPaymentMethod(cursor.getString(6));
                order.setCustomerName(cursor.getString(7));
                order.setCustomerPhone(cursor.getString(8));
                list.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public int updateOrderStatus(int orderId, String newStatus) {
        if ("CANCELLED".equalsIgnoreCase(newStatus) || "ĐÃ HỦY".equalsIgnoreCase(newStatus)) {
            return cancelOrder(orderId);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDER_STATUS, newStatus);
        return db.update(TABLE_ORDERS, cv, COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
    }

    public int cancelOrder(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Check current status
            Cursor checkCursor = db.rawQuery("SELECT status FROM " + TABLE_ORDERS + " WHERE id = ?", new String[]{String.valueOf(orderId)});
            String currentStatus = "";
            if (checkCursor != null && checkCursor.moveToFirst()) {
                currentStatus = checkCursor.getString(0);
                checkCursor.close();
            }
            if ("CANCELLED".equalsIgnoreCase(currentStatus) || "ĐÃ HỦY".equalsIgnoreCase(currentStatus)) {
                db.setTransactionSuccessful();
                return 1; // already cancelled
            }

            // Restore book stock from order_items
            Cursor itemsCursor = db.rawQuery("SELECT book_id, quantity FROM " + TABLE_ORDER_ITEMS + " WHERE order_id = ?", new String[]{String.valueOf(orderId)});
            if (itemsCursor != null && itemsCursor.moveToFirst()) {
                do {
                    int bookId = itemsCursor.getInt(0);
                    int qty = itemsCursor.getInt(1);
                    db.execSQL("UPDATE " + TABLE_BOOKS + " SET " + COLUMN_BOOK_STOCK + " = " + COLUMN_BOOK_STOCK + " + ? WHERE " + COLUMN_BOOK_ID + " = ?", new Object[]{qty, bookId});
                } while (itemsCursor.moveToNext());
                itemsCursor.close();
            }

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ORDER_STATUS, "CANCELLED");
            int rows = db.update(TABLE_ORDERS, cv, COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
            db.setTransactionSuccessful();
            return rows;
        } finally {
            db.endTransaction();
        }
    }

    // ==========================================
    // USER MANAGEMENT & PROFILE
    // ==========================================

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, full_name, email, phone, address, role, is_active FROM " + TABLE_USERS + " ORDER BY id DESC", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setFullName(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setPhone(cursor.getString(3));
                user.setAddress(cursor.getString(4));
                user.setRole(cursor.getString(5));
                user.setActive(cursor.getInt(6) == 1);
                list.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public int toggleUserStatus(int userId, boolean isActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_ACTIVE, isActive ? 1 : 0);
        return db.update(TABLE_USERS, cv, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public int updateUserProfile(int userId, String fullName, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_NAME, fullName);
        cv.put(COLUMN_USER_PHONE, phone);
        cv.put(COLUMN_USER_ADDRESS, address);
        return db.update(TABLE_USERS, cv, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE id = ? AND password = ?", new String[]{String.valueOf(userId), oldPassword});
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USER_PASSWORD, newPassword);
            int updated = db.update(TABLE_USERS, cv, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            return updated > 0;
        }
        if (cursor != null) cursor.close();
        return false;
    }

    public boolean resetPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_PASSWORD, newPassword);
        int rows = db.update(TABLE_USERS, cv, COLUMN_USER_EMAIL + " = ?", new String[]{email});
        return rows > 0;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, full_name, email, phone, address, role, is_active FROM " + TABLE_USERS
                + " WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 1
            );
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, full_name, email, phone, address, role, is_active FROM " + TABLE_USERS
                + " WHERE email = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 1
            );
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public long insertOrUpdateUserFromFirebase(String fullName, String email, String phone, String address, String role, boolean isActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_NAME, fullName != null ? fullName : "Người dùng");
        cv.put(COLUMN_USER_EMAIL, email);
        cv.put(COLUMN_USER_PHONE, phone != null ? phone : "");
        cv.put(COLUMN_USER_ADDRESS, address != null ? address : "");
        cv.put(COLUMN_USER_ROLE, role != null ? role : "USER");
        cv.put(COLUMN_USER_ACTIVE, isActive ? 1 : 0);

        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            db.update(TABLE_USERS, cv, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(id)});
            return id;
        }
        if (cursor != null) cursor.close();
        cv.put(COLUMN_USER_PASSWORD, "firebase_synced");
        return db.insert(TABLE_USERS, null, cv);
    }

    public long registerUser(String fullName, String email, String password, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_NAME, fullName);
        cv.put(COLUMN_USER_EMAIL, email);
        cv.put(COLUMN_USER_PASSWORD, password);
        cv.put(COLUMN_USER_PHONE, phone);
        cv.put(COLUMN_USER_ADDRESS, address);
        cv.put(COLUMN_USER_ROLE, "USER");
        cv.put(COLUMN_USER_ACTIVE, 1);
        return db.insert(TABLE_USERS, null, cv);
    }

    // ==========================================
    // REVIEWS
    // ==========================================

    public long insertReview(int userId, int bookId, float rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REV_USER_ID, userId);
        cv.put(COLUMN_REV_BOOK_ID, bookId);
        cv.put(COLUMN_REV_RATING, rating);
        cv.put(COLUMN_REV_COMMENT, comment);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        cv.put(COLUMN_REV_DATE, sdf.format(new Date()));
        long revId = db.insert(TABLE_REVIEWS, null, cv);
        if (revId > 0) {
            updateBookRatingAndCount(db, bookId);
        }
        return revId;
    }

    private void updateBookRatingAndCount(SQLiteDatabase db, int bookId) {
        try {
            Cursor cursor = db.rawQuery("SELECT AVG(" + COLUMN_REV_RATING + "), COUNT(*) FROM " + TABLE_REVIEWS + " WHERE " + COLUMN_REV_BOOK_ID + " = ?",
                    new String[]{String.valueOf(bookId)});
            if (cursor != null && cursor.moveToFirst()) {
                double avg = cursor.getDouble(0);
                int count = cursor.getInt(1);
                cursor.close();
                avg = Math.round(avg * 10.0) / 10.0;
                ContentValues bCv = new ContentValues();
                bCv.put(COLUMN_BOOK_RATING, avg);
                bCv.put(COLUMN_BOOK_REVIEWS, count);
                db.update(TABLE_BOOKS, bCv, COLUMN_BOOK_ID + " = ?", new String[]{String.valueOf(bookId)});
            }
        } catch (Exception ignored) {
        }
    }

        public List<Review> getReviewsForBook(int bookId) {
        List<Review> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.id, r.user_id, u.full_name, r.book_id, r.rating, r.comment, r.review_date "
                + " FROM " + TABLE_REVIEWS + " r LEFT JOIN " + TABLE_USERS + " u ON r.user_id = u.id WHERE r.book_id = ? ORDER BY r.id DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(bookId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Review r = new Review();
                r.setId(cursor.getInt(0));
                r.setUserId(cursor.getInt(1));
                r.setUserName(cursor.getString(2) != null ? cursor.getString(2) : "Người dùng");
                r.setBookId(cursor.getInt(3));
                r.setRating(cursor.getFloat(4));
                r.setComment(cursor.getString(5));
                r.setReviewDate(cursor.getString(6));
                list.add(r);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Book> getBooksInOrder(int orderId) {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.id, b.title, b.author, b.price, b.original_price, b.discount, b.rating, b.review_count, b.description, b.image_url, b.category_id "
                + " FROM " + TABLE_ORDER_ITEMS + " oi "
                + " JOIN " + TABLE_BOOKS + " b ON oi.book_id = b.id "
                + " WHERE oi.order_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book b = new Book();
                b.setId(cursor.getInt(0));
                b.setTitle(cursor.getString(1));
                b.setAuthor(cursor.getString(2));
                b.setPrice(cursor.getDouble(3));
                b.setOriginalPrice(cursor.getDouble(4));
                b.setDiscount(cursor.getInt(5));
                b.setRating(cursor.getDouble(6));
                b.setReviewCount(cursor.getInt(7));
                b.setDescription(cursor.getString(8));
                b.setImageUrl(cursor.getString(9));
                b.setCategoryId(cursor.getInt(10));
                list.add(b);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT oi.id, oi.order_id, oi.book_id, b.title, b.author, b.image_url, oi.price, oi.quantity "
                + " FROM " + TABLE_ORDER_ITEMS + " oi "
                + " LEFT JOIN " + TABLE_BOOKS + " b ON oi.book_id = b.id "
                + " WHERE oi.order_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double price = cursor.getDouble(6);
                OrderItem item = new OrderItem(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3) != null ? cursor.getString(3) : "Sách #" + cursor.getInt(2),
                        cursor.getString(4) != null ? cursor.getString(4) : "",
                        cursor.getString(5) != null ? cursor.getString(5) : "",
                        price,
                        cursor.getInt(7)
                );
                list.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void upsertOrderItemFromFirebase(int orderId, int bookId, String title, String author, String image, double price, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Ensure book exists in TABLE_BOOKS so foreign key or query succeeds
        Cursor bCheck = db.rawQuery("SELECT id FROM " + TABLE_BOOKS + " WHERE id = ?", new String[]{String.valueOf(bookId)});
        if (bCheck == null || !bCheck.moveToFirst()) {
            ContentValues bCv = new ContentValues();
            bCv.put(COLUMN_BOOK_ID, bookId);
            bCv.put(COLUMN_BOOK_TITLE, title != null ? title : "Sách #" + bookId);
            bCv.put(COLUMN_BOOK_AUTHOR, author != null ? author : "");
            bCv.put(COLUMN_BOOK_IMAGE, image != null ? image : "");
            bCv.put(COLUMN_BOOK_PRICE, price);
            bCv.put(COLUMN_BOOK_STOCK, 100);
            bCv.put(COLUMN_BOOK_ACTIVE, 1);
            db.insertWithOnConflict(TABLE_BOOKS, null, bCv, SQLiteDatabase.CONFLICT_IGNORE);
        }
        if (bCheck != null) bCheck.close();

        // Check if item already exists in TABLE_ORDER_ITEMS
        Cursor c = db.rawQuery("SELECT id FROM " + TABLE_ORDER_ITEMS + " WHERE order_id = ? AND book_id = ?",
                new String[]{String.valueOf(orderId), String.valueOf(bookId)});
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ITEM_ORDER_ID, orderId);
        cv.put(COLUMN_ITEM_BOOK_ID, bookId);
        cv.put(COLUMN_ITEM_PRICE, price);
        cv.put(COLUMN_ITEM_QUANTITY, quantity);

        if (c != null && c.moveToFirst()) {
            int id = c.getInt(0);
            c.close();
            db.update(TABLE_ORDER_ITEMS, cv, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(id)});
        } else {
            if (c != null) c.close();
            db.insert(TABLE_ORDER_ITEMS, null, cv);
        }
    }

    public void upsertCategoryFromFirebase(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CAT_ID, id);
        cv.put(COLUMN_CAT_NAME, name);
        db.insertWithOnConflict(TABLE_CATEGORIES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void upsertBookFromFirebase(int id, int catId, String title, String author, double price, double origPrice, int discount, double rating, int reviews, String image, String desc, int stock, boolean isActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BOOK_ID, id);
        cv.put(COLUMN_BOOK_CAT_ID, catId);
        cv.put(COLUMN_BOOK_TITLE, title);
        cv.put(COLUMN_BOOK_AUTHOR, author);
        cv.put(COLUMN_BOOK_PRICE, price);
        cv.put(COLUMN_BOOK_ORIGINAL_PRICE, origPrice);
        cv.put(COLUMN_BOOK_DISCOUNT, discount);
        cv.put(COLUMN_BOOK_RATING, rating);
        cv.put(COLUMN_BOOK_REVIEWS, reviews);
        cv.put(COLUMN_BOOK_IMAGE, image);
        cv.put(COLUMN_BOOK_DESC, desc);
        cv.put(COLUMN_BOOK_STOCK, stock);
        cv.put(COLUMN_BOOK_ACTIVE, isActive ? 1 : 0);
        db.insertWithOnConflict(TABLE_BOOKS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void upsertOrderFromFirebase(int orderId, int userId, String orderDate, String status, double total, String address, String payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDER_ID, orderId);
        cv.put(COLUMN_ORDER_USER_ID, userId);
        cv.put(COLUMN_ORDER_DATE, orderDate);
        cv.put(COLUMN_ORDER_STATUS, status);
        cv.put(COLUMN_ORDER_TOTAL, total);
        cv.put(COLUMN_ORDER_ADDRESS, address);
        cv.put(COLUMN_ORDER_PAYMENT, payment);
        db.insertWithOnConflict(TABLE_ORDERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void upsertReviewFromFirebase(int id, int userId, int bookId, float rating, String comment, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REV_ID, id);
        cv.put(COLUMN_REV_USER_ID, userId);
        cv.put(COLUMN_REV_BOOK_ID, bookId);
        cv.put(COLUMN_REV_RATING, rating);
        cv.put(COLUMN_REV_COMMENT, comment);
        cv.put(COLUMN_REV_DATE, date);
        db.insertWithOnConflict(TABLE_REVIEWS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        updateBookRatingAndCount(db, bookId);
    }

        // ==========================================
    // FAVORITES / WISHLIST OPERATIONS
    // ==========================================

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + " ("
                + COLUMN_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FAV_USER_ID + " INTEGER, "
                + COLUMN_FAV_BOOK_ID + " INTEGER, "
                + COLUMN_FAV_DATE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_FAV_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), "
                + "FOREIGN KEY(" + COLUMN_FAV_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "), "
                + "UNIQUE(" + COLUMN_FAV_USER_ID + ", " + COLUMN_FAV_BOOK_ID + "))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESSES + " ("
                + COLUMN_ADDR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADDR_USER_ID + " INTEGER, "
                + COLUMN_ADDR_NAME + " TEXT, "
                + COLUMN_ADDR_PHONE + " TEXT, "
                + COLUMN_ADDR_STREET + " TEXT, "
                + COLUMN_ADDR_LABEL + " TEXT DEFAULT 'Nhà riêng', "
                + COLUMN_ADDR_IS_DEFAULT + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COLUMN_ADDR_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))");

        ensureRichSampleData(db);
    }

    public boolean isBookFavorite(int userId, int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES,
                new String[]{COLUMN_FAV_ID},
                COLUMN_FAV_USER_ID + " = ? AND " + COLUMN_FAV_BOOK_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(bookId)},
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public boolean toggleFavorite(int userId, int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (isBookFavorite(userId, bookId)) {
            db.delete(TABLE_FAVORITES,
                    COLUMN_FAV_USER_ID + " = ? AND " + COLUMN_FAV_BOOK_ID + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(bookId)});
            return false; // unfavorited
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_FAV_USER_ID, userId);
            cv.put(COLUMN_FAV_BOOK_ID, bookId);
            cv.put(COLUMN_FAV_DATE, new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
            db.insertWithOnConflict(TABLE_FAVORITES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return true; // favorited
        }
    }

    public List<Book> getFavoriteBooks(int userId) {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.id, b.title, b.author, b.price, b.original_price, b.discount, b.rating, b.review_count, b.image_url, b.description, b.stock, c.name, b.category_id "
                + "FROM " + TABLE_BOOKS + " b "
                + "INNER JOIN " + TABLE_FAVORITES + " f ON b.id = f." + COLUMN_FAV_BOOK_ID + " "
                + "LEFT JOIN " + TABLE_CATEGORIES + " c ON b.category_id = c.id "
                + "WHERE f." + COLUMN_FAV_USER_ID + " = ? "
                + "ORDER BY f." + COLUMN_FAV_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(0));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setPrice(cursor.getDouble(3));
                book.setOriginalPrice(cursor.getDouble(4));
                book.setDiscount(cursor.getInt(5));
                book.setRating(cursor.getDouble(6));
                book.setReviewCount(cursor.getInt(7));
                book.setImageUrl(cursor.getString(8));
                book.setDescription(cursor.getString(9));
                book.setStock(cursor.getInt(10));
                book.setCategoryName(cursor.getString(11));
                book.setCategoryId(cursor.getInt(12));
                list.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }


    // ==========================================
    // ADDRESS BOOK OPERATIONS
    // ==========================================

    public long insertAddress(int userId, String recipientName, String phone, String streetAddress, String label, boolean isDefault) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor countCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ADDRESSES + " WHERE " + COLUMN_ADDR_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        boolean isFirst = true;
        if (countCursor != null) {
            if (countCursor.moveToFirst()) {
                isFirst = countCursor.getInt(0) == 0;
            }
            countCursor.close();
        }

        if (isFirst || isDefault) {
            ContentValues resetCv = new ContentValues();
            resetCv.put(COLUMN_ADDR_IS_DEFAULT, 0);
            db.update(TABLE_ADDRESSES, resetCv, COLUMN_ADDR_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            isDefault = true;
        }

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDR_USER_ID, userId);
        cv.put(COLUMN_ADDR_NAME, recipientName);
        cv.put(COLUMN_ADDR_PHONE, phone);
        cv.put(COLUMN_ADDR_STREET, streetAddress);
        cv.put(COLUMN_ADDR_LABEL, (label != null && !label.isEmpty()) ? label : "Nhà riêng");
        cv.put(COLUMN_ADDR_IS_DEFAULT, isDefault ? 1 : 0);

        return db.insert(TABLE_ADDRESSES, null, cv);
    }

    public int updateAddress(int addressId, int userId, String recipientName, String phone, String streetAddress, String label, boolean isDefault) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isDefault) {
            ContentValues resetCv = new ContentValues();
            resetCv.put(COLUMN_ADDR_IS_DEFAULT, 0);
            db.update(TABLE_ADDRESSES, resetCv, COLUMN_ADDR_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        }

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDR_NAME, recipientName);
        cv.put(COLUMN_ADDR_PHONE, phone);
        cv.put(COLUMN_ADDR_STREET, streetAddress);
        cv.put(COLUMN_ADDR_LABEL, (label != null && !label.isEmpty()) ? label : "Nhà riêng");
        cv.put(COLUMN_ADDR_IS_DEFAULT, isDefault ? 1 : 0);

        return db.update(TABLE_ADDRESSES, cv, COLUMN_ADDR_ID + " = ? AND " + COLUMN_ADDR_USER_ID + " = ?",
                new String[]{String.valueOf(addressId), String.valueOf(userId)});
    }

    public int deleteAddress(int addressId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ADDR_IS_DEFAULT + " FROM " + TABLE_ADDRESSES + " WHERE " + COLUMN_ADDR_ID + " = ? AND " + COLUMN_ADDR_USER_ID + " = ?",
                new String[]{String.valueOf(addressId), String.valueOf(userId)});
        boolean wasDefault = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                wasDefault = cursor.getInt(0) == 1;
            }
            cursor.close();
        }

        int rows = db.delete(TABLE_ADDRESSES, COLUMN_ADDR_ID + " = ? AND " + COLUMN_ADDR_USER_ID + " = ?",
                new String[]{String.valueOf(addressId), String.valueOf(userId)});

        if (wasDefault && rows > 0) {
            Cursor nextCursor = db.rawQuery("SELECT " + COLUMN_ADDR_ID + " FROM " + TABLE_ADDRESSES + " WHERE " + COLUMN_ADDR_USER_ID + " = ? ORDER BY " + COLUMN_ADDR_ID + " DESC LIMIT 1",
                    new String[]{String.valueOf(userId)});
            if (nextCursor != null) {
                if (nextCursor.moveToFirst()) {
                    int nextId = nextCursor.getInt(0);
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_ADDR_IS_DEFAULT, 1);
                    db.update(TABLE_ADDRESSES, cv, COLUMN_ADDR_ID + " = ?", new String[]{String.valueOf(nextId)});
                }
                nextCursor.close();
            }
        }
        return rows;
    }

    public List<Address> getAddressesForUser(int userId) {
        List<Address> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ADDR_ID + ", " + COLUMN_ADDR_USER_ID + ", " + COLUMN_ADDR_NAME + ", "
                + COLUMN_ADDR_PHONE + ", " + COLUMN_ADDR_STREET + ", " + COLUMN_ADDR_LABEL + ", " + COLUMN_ADDR_IS_DEFAULT
                + " FROM " + TABLE_ADDRESSES + " WHERE " + COLUMN_ADDR_USER_ID + " = ? ORDER BY " + COLUMN_ADDR_IS_DEFAULT + " DESC, " + COLUMN_ADDR_ID + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Address a = new Address();
                a.setId(cursor.getInt(0));
                a.setUserId(cursor.getInt(1));
                a.setRecipientName(cursor.getString(2));
                a.setPhone(cursor.getString(3));
                a.setStreetAddress(cursor.getString(4));
                a.setLabel(cursor.getString(5));
                a.setDefault(cursor.getInt(6) == 1);
                list.add(a);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Address getDefaultAddress(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ADDR_ID + ", " + COLUMN_ADDR_USER_ID + ", " + COLUMN_ADDR_NAME + ", "
                + COLUMN_ADDR_PHONE + ", " + COLUMN_ADDR_STREET + ", " + COLUMN_ADDR_LABEL + ", " + COLUMN_ADDR_IS_DEFAULT
                + " FROM " + TABLE_ADDRESSES + " WHERE " + COLUMN_ADDR_USER_ID + " = ? ORDER BY " + COLUMN_ADDR_IS_DEFAULT + " DESC, " + COLUMN_ADDR_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        Address a = null;
        if (cursor != null && cursor.moveToFirst()) {
            a = new Address();
            a.setId(cursor.getInt(0));
            a.setUserId(cursor.getInt(1));
            a.setRecipientName(cursor.getString(2));
            a.setPhone(cursor.getString(3));
            a.setStreetAddress(cursor.getString(4));
            a.setLabel(cursor.getString(5));
            a.setDefault(cursor.getInt(6) == 1);
            cursor.close();
        }
        return a;
    }

    public boolean setDefaultAddress(int addressId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues resetCv = new ContentValues();
        resetCv.put(COLUMN_ADDR_IS_DEFAULT, 0);
        db.update(TABLE_ADDRESSES, resetCv, COLUMN_ADDR_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDR_IS_DEFAULT, 1);
        int rows = db.update(TABLE_ADDRESSES, cv, COLUMN_ADDR_ID + " = ? AND " + COLUMN_ADDR_USER_ID + " = ?",
                new String[]{String.valueOf(addressId), String.valueOf(userId)});
        return rows > 0;
    }

    // ==========================================
    // BOOKS BY AUTHOR OPERATIONS
    // ==========================================

    public List<Book> getBooksByAuthor(String author, int excludeBookId) {
        List<Book> list = new ArrayList<>();
        if (author == null || author.trim().isEmpty()) {
            return list;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.id, b.title, b.author, b.price, b.original_price, b.discount, b.rating, b.review_count, b.image_url, b.description, b.stock, c.name, b.category_id "
                + "FROM " + TABLE_BOOKS + " b "
                + "LEFT JOIN " + TABLE_CATEGORIES + " c ON b.category_id = c.id "
                + "WHERE b.is_active = 1 AND LOWER(b.author) = LOWER(?) AND b.id != ? "
                + "ORDER BY b.id DESC";
        Cursor cursor = db.rawQuery(query, new String[]{author.trim(), String.valueOf(excludeBookId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(0));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setPrice(cursor.getDouble(3));
                book.setOriginalPrice(cursor.getDouble(4));
                book.setDiscount(cursor.getInt(5));
                book.setRating(cursor.getDouble(6));
                book.setReviewCount(cursor.getInt(7));
                book.setImageUrl(cursor.getString(8));
                book.setDescription(cursor.getString(9));
                book.setStock(cursor.getInt(10));
                book.setCategoryName(cursor.getString(11));
                book.setCategoryId(cursor.getInt(12));
                list.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<Book> getAllBooksByAuthor(String author) {
        return getBooksByAuthor(author, -1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}

package com.example.book_selling_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.models.Order;
import com.example.book_selling_app.models.OrderItem;
import com.example.book_selling_app.models.Review;
import com.example.book_selling_app.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 2;

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

        // Seed Sample Data
        seedData(db);
    }

    private void seedData(SQLiteDatabase db) {
        // Default Admin & User
        ContentValues admin = new ContentValues();
        admin.put(COLUMN_USER_NAME, "Quản Trị Viên");
        admin.put(COLUMN_USER_EMAIL, "admin@bookstore.vn");
        admin.put(COLUMN_USER_PHONE, "0901234567");
        admin.put(COLUMN_USER_ADDRESS, "Phòng Quản Trị Hệ Thống, BookStore");
        admin.put(COLUMN_USER_PASSWORD, "admin123");
        admin.put(COLUMN_USER_ROLE, "ADMIN");
        admin.put(COLUMN_USER_ACTIVE, 1);
        db.insert(TABLE_USERS, null, admin);

        ContentValues user = new ContentValues();
        user.put(COLUMN_USER_NAME, "Nguyễn Văn A");
        user.put(COLUMN_USER_EMAIL, "user@gmail.com");
        user.put(COLUMN_USER_PHONE, "0987654321");
        user.put(COLUMN_USER_ADDRESS, "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức");
        user.put(COLUMN_USER_PASSWORD, "123456");
        user.put(COLUMN_USER_ROLE, "USER");
        user.put(COLUMN_USER_ACTIVE, 1);
        db.insert(TABLE_USERS, null, user);

        ContentValues user2 = new ContentValues();
        user2.put(COLUMN_USER_NAME, "Trần Thị Mai");
        user2.put(COLUMN_USER_EMAIL, "mai.tran@gmail.com");
        user2.put(COLUMN_USER_PHONE, "0912345678");
        user2.put(COLUMN_USER_ADDRESS, "123 Nguyễn Huệ, Quận 1, TP. HCM");
        user2.put(COLUMN_USER_PASSWORD, "123456");
        user2.put(COLUMN_USER_ROLE, "USER");
        user2.put(COLUMN_USER_ACTIVE, 1);
        db.insert(TABLE_USERS, null, user2);

        // Categories
        String[] categories = {"Kinh Tế", "Văn Học", "Kỹ Năng Sống", "Công Nghệ", "Tâm Lý Học", "Thiếu Nhi"};
        for (String cat : categories) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_CAT_NAME, cat);
            db.insert(TABLE_CATEGORIES, null, cv);
        }

        // Sample Books
        insertBookInternal(db, 1, "Nhà Giả Kim", "Paulo Coelho", 69000, 89000, 22, 4.8, 1250,
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400",
                "Tác phẩm văn học kinh điển được dịch ra nhiều thứ tiếng nhất trên thế giới, truyền cảm hứng theo đuổi vận mệnh.", 50);

        insertBookInternal(db, 1, "Đắc Nhân Tâm", "Dale Carnegie", 78000, 98000, 20, 4.9, 2400,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400",
                "Cuốn sách nghệ thuật thu phục lòng người và dẫn lối đến thành công trong giao tiếp và cuộc sống.", 80);

        insertBookInternal(db, 3, "Thói Quen Nguyên Tử (Atomic Habits)", "James Clear", 149000, 189000, 21, 4.9, 3100,
                "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400",
                "Thay đổi tí hon, hiệu quả bất ngờ. Chiến lược từng bước để xây dựng thói quen tốt và từ bỏ thói quen xấu.", 65);

        insertBookInternal(db, 4, "Lập Trình Di Động Nâng Cao", "TS. Nguyễn Văn Hùng", 120000, 150000, 20, 4.7, 340,
                "https://images.unsplash.com/photo-1532012164546-f432f2e3edd3?w=400",
                "Giáo trình lập trình di động thực chiến với kiến trúc hiện đại, kết nối CSDL và xử lý giao diện chuyên sâu.", 40);

        insertBookInternal(db, 2, "Cây Cam Ngọt Của Tôi", "José Mauro", 85000, 108000, 21, 4.8, 980,
                "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400",
                "Câu chuyện cảm động rơi nước mắt về tuổi thơ đầy mất mát nhưng ngập tràn tình yêu thương.", 90);

        insertBookInternal(db, 5, "Tư Duy Nhanh Và Chậm", "Daniel Kahneman", 175000, 220000, 20, 4.6, 610,
                "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400",
                "Kiệt tác về tâm lý học hành vi và kinh tế học nhận thức của nhà kinh tế học đoạt giải Nobel.", 35);

        // Seed Sample Orders
        ContentValues order1 = new ContentValues();
        order1.put(COLUMN_ORDER_USER_ID, 2);
        order1.put(COLUMN_ORDER_DATE, "26/09/2026 14:30");
        order1.put(COLUMN_ORDER_STATUS, "DELIVERED");
        order1.put(COLUMN_ORDER_TOTAL, 147000);
        order1.put(COLUMN_ORDER_ADDRESS, "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức");
        order1.put(COLUMN_ORDER_PAYMENT, "Thanh toán khi nhận hàng (COD)");
        long oId1 = db.insert(TABLE_ORDERS, null, order1);

        ContentValues item1 = new ContentValues();
        item1.put(COLUMN_ITEM_ORDER_ID, oId1);
        item1.put(COLUMN_ITEM_BOOK_ID, 1);
        item1.put(COLUMN_ITEM_QUANTITY, 1);
        item1.put(COLUMN_ITEM_PRICE, 69000);
        db.insert(TABLE_ORDER_ITEMS, null, item1);

        ContentValues item2 = new ContentValues();
        item2.put(COLUMN_ITEM_ORDER_ID, oId1);
        item2.put(COLUMN_ITEM_BOOK_ID, 2);
        item2.put(COLUMN_ITEM_QUANTITY, 1);
        item2.put(COLUMN_ITEM_PRICE, 78000);
        db.insert(TABLE_ORDER_ITEMS, null, item2);

        ContentValues order2 = new ContentValues();
        order2.put(COLUMN_ORDER_USER_ID, 2);
        order2.put(COLUMN_ORDER_DATE, "26/09/2026 18:15");
        order2.put(COLUMN_ORDER_STATUS, "PENDING");
        order2.put(COLUMN_ORDER_TOTAL, 149000);
        order2.put(COLUMN_ORDER_ADDRESS, "Số 1 Võ Văn Ngân, Phường Linh Chiểu, TP. Thủ Đức");
        order2.put(COLUMN_ORDER_PAYMENT, "Thanh toán khi nhận hàng (COD)");
        long oId2 = db.insert(TABLE_ORDERS, null, order2);

        ContentValues item3 = new ContentValues();
        item3.put(COLUMN_ITEM_ORDER_ID, oId2);
        item3.put(COLUMN_ITEM_BOOK_ID, 3);
        item3.put(COLUMN_ITEM_QUANTITY, 1);
        item3.put(COLUMN_ITEM_PRICE, 149000);
        db.insert(TABLE_ORDER_ITEMS, null, item3);

        // Seed Sample Review
        ContentValues rev = new ContentValues();
        rev.put(COLUMN_REV_USER_ID, 2);
        rev.put(COLUMN_REV_BOOK_ID, 1);
        rev.put(COLUMN_REV_RATING, 5.0f);
        rev.put(COLUMN_REV_COMMENT, "Sách rất hay, đóng gói cẩn thận và giao hàng nhanh chóng!");
        rev.put(COLUMN_REV_DATE, "26/09/2026 16:00");
        db.insert(TABLE_REVIEWS, null, rev);
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

    // ==========================================
    // ADMIN DASHBOARD STATS
    // ==========================================

    public static class AdminStats {
        public double totalRevenue;
        public int totalOrders;
        public int pendingOrders;
        public int totalBooks;
        public int totalUsers;
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDER_STATUS, newStatus);
        return db.update(TABLE_ORDERS, cv, COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
    }

    public int cancelOrder(int orderId) {
        return updateOrderStatus(orderId, "CANCELLED");
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
        cv.put(COLUMN_REV_DATE, "Hôm nay");
        return db.insert(TABLE_REVIEWS, null, cv);
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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

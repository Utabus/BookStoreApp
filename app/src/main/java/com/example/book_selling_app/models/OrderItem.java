package com.example.book_selling_app.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int id;
    private int orderId;
    private int bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookImage;
    private double price;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(int id, int orderId, int bookId, String bookTitle, String bookAuthor, String bookImage, double price, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookImage = bookImage;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle != null ? bookTitle : "Sách #" + bookId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor != null ? bookAuthor : "";
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookImage() {
        return bookImage != null ? bookImage : "";
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return price * quantity;
    }
}

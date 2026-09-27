package com.example.book_selling_app.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private Book book;
    private int quantity;

    public CartItem(int id, Book book, int quantity) {
        this.id = id;
        this.book = book;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() {
        return (book != null) ? book.getPrice() * quantity : 0;
    }
}

package com.example.book_selling_app.models;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private int bookCount;
    private boolean isSelected;

    public Category() {
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        this.isSelected = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getBookCount() { return bookCount; }
    public void setBookCount(int bookCount) { this.bookCount = bookCount; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}

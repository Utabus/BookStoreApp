package com.example.book_selling_app.models;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String id;
    private int senderId;
    private String senderName;
    private String senderRole; // "USER" or "ADMIN"
    private String message;
    private String imageUrl;
    private long timestamp;
    private boolean read;

    public ChatMessage() {
        // Needed for Firebase Firestore
    }

    public ChatMessage(String id, int senderId, String senderName, String senderRole, String message, String imageUrl, long timestamp, boolean read) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.message = message;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}

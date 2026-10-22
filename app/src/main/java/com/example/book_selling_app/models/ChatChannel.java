package com.example.book_selling_app.models;

import java.io.Serializable;

public class ChatChannel implements Serializable {

    private String channelId; // Usually String.valueOf(userId)
    private int userId;
    private String userName;
    private String userEmail;
    private String lastMessage;
    private long lastTimestamp;
    private int unreadAdmin;
    private int unreadUser;

    public ChatChannel() {
        // Needed for Firebase Firestore
    }

    public ChatChannel(String channelId, int userId, String userName, String userEmail, String lastMessage, long lastTimestamp, int unreadAdmin, int unreadUser) {
        this.channelId = channelId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
        this.unreadAdmin = unreadAdmin;
        this.unreadUser = unreadUser;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public int getUnreadAdmin() {
        return unreadAdmin;
    }

    public void setUnreadAdmin(int unreadAdmin) {
        this.unreadAdmin = unreadAdmin;
    }

    public int getUnreadUser() {
        return unreadUser;
    }

    public void setUnreadUser(int unreadUser) {
        this.unreadUser = unreadUser;
    }
}

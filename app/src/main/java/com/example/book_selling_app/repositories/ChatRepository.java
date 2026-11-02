package com.example.book_selling_app.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.book_selling_app.data.utils.AppExecutors;
import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.ChatChannel;
import com.example.book_selling_app.models.ChatMessage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository {

    public interface OnMessagesListener {
        void onMessages(List<ChatMessage> messages);
        void onError(String error);
    }

    public interface OnChannelsListener {
        void onChannels(List<ChatChannel> channels);
        void onError(String error);
    }

    private final FirebaseFirestore mFirestore;
    private final AppExecutors appExecutors;

    public ChatRepository(Context context) {
        this.mFirestore = FirebaseFirestore.getInstance();
        this.appExecutors = AppExecutors.getInstance();
    }

    public LiveData<Resource<Boolean>> sendMessage(String channelId, ChatMessage message, String userName, String userEmail) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        DocumentReference channelRef = mFirestore.collection("chats").document(channelId);
        DocumentReference newMsgRef = channelRef.collection("messages").document();
        message.setId(newMsgRef.getId());

        // Prepare message map
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("id", message.getId());
        msgMap.put("senderId", message.getSenderId());
        msgMap.put("senderName", message.getSenderName());
        msgMap.put("senderRole", message.getSenderRole());
        msgMap.put("message", message.getMessage() != null ? message.getMessage() : "");
        msgMap.put("imageUrl", message.getImageUrl() != null ? message.getImageUrl() : "");
        msgMap.put("timestamp", message.getTimestamp());
        msgMap.put("read", message.isRead());

        // Save message to subcollection
        newMsgRef.set(msgMap)
                .addOnSuccessListener(aVoid -> {
                    // Update channel summary
                    Map<String, Object> channelMap = new HashMap<>();
                    channelMap.put("channelId", channelId);
                    try {
                        channelMap.put("userId", Integer.parseInt(channelId));
                    } catch (Exception ignored) {
                        channelMap.put("userId", message.getSenderId());
                    }
                    if (userName != null && !userName.isEmpty()) {
                        channelMap.put("userName", userName);
                    }
                    if (userEmail != null && !userEmail.isEmpty()) {
                        channelMap.put("userEmail", userEmail);
                    }
                    String snippet = (message.getMessage() != null && !message.getMessage().isEmpty())
                            ? message.getMessage()
                            : "";
                    channelMap.put("lastMessage", snippet);
                    channelMap.put("lastTimestamp", message.getTimestamp());

                    // Increment unread counter for recipient
                    if ("USER".equalsIgnoreCase(message.getSenderRole())) {
                        channelMap.put("unreadAdmin", com.google.firebase.firestore.FieldValue.increment(1));
                    } else {
                        channelMap.put("unreadUser", com.google.firebase.firestore.FieldValue.increment(1));
                    }

                    channelRef.set(channelMap, SetOptions.merge())
                            .addOnSuccessListener(v -> result.postValue(Resource.success(true)))
                            .addOnFailureListener(e -> result.postValue(Resource.error("Lỗi cập nhật hội thoại: " + e.getMessage(), false)));
                })
                .addOnFailureListener(e -> result.postValue(Resource.error("Không thể gửi tin nhắn: " + e.getMessage(), false)));

        return result;
    }

    public ListenerRegistration listenMessages(String channelId, OnMessagesListener listener) {
        return mFirestore.collection("chats")
                .document(channelId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) listener.onError(error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<ChatMessage> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            try {
                                ChatMessage msg = new ChatMessage();
                                msg.setId(doc.getString("id"));
                                Long sId = doc.getLong("senderId");
                                msg.setSenderId(sId != null ? sId.intValue() : 0);
                                msg.setSenderName(doc.getString("senderName"));
                                msg.setSenderRole(doc.getString("senderRole"));
                                msg.setMessage(doc.getString("message"));
                                msg.setImageUrl(doc.getString("imageUrl"));
                                Long ts = doc.getLong("timestamp");
                                msg.setTimestamp(ts != null ? ts : 0);
                                Boolean rd = doc.getBoolean("read");
                                msg.setRead(rd != null && rd);
                                list.add(msg);
                            } catch (Exception ignored) {
                            }
                        }
                        if (listener != null) listener.onMessages(list);
                    }
                });
    }

    public ListenerRegistration listenChannels(OnChannelsListener listener) {
        return mFirestore.collection("chats")
                .orderBy("lastTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        if (listener != null) listener.onError(error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<ChatChannel> channels = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            try {
                                ChatChannel ch = new ChatChannel();
                                ch.setChannelId(doc.getString("channelId") != null ? doc.getString("channelId") : doc.getId());
                                Long uId = doc.getLong("userId");
                                ch.setUserId(uId != null ? uId.intValue() : 0);
                                ch.setUserName(doc.getString("userName") != null ? doc.getString("userName") : "Khách hàng");
                                ch.setUserEmail(doc.getString("userEmail") != null ? doc.getString("userEmail") : "");
                                ch.setLastMessage(doc.getString("lastMessage") != null ? doc.getString("lastMessage") : "");
                                Long lt = doc.getLong("lastTimestamp");
                                ch.setLastTimestamp(lt != null ? lt : 0);
                                Long ua = doc.getLong("unreadAdmin");
                                ch.setUnreadAdmin(ua != null ? ua.intValue() : 0);
                                Long uu = doc.getLong("unreadUser");
                                ch.setUnreadUser(uu != null ? uu.intValue() : 0);
                                channels.add(ch);
                            } catch (Exception ignored) {
                            }
                        }
                        if (listener != null) listener.onChannels(channels);
                    }
                });
    }

    public void markMessagesAsRead(String channelId, String currentRole) {
        DocumentReference channelRef = mFirestore.collection("chats").document(channelId);
        Map<String, Object> resetMap = new HashMap<>();
        if ("ADMIN".equalsIgnoreCase(currentRole)) {
            resetMap.put("unreadAdmin", 0);
        } else {
            resetMap.put("unreadUser", 0);
        }
        channelRef.set(resetMap, SetOptions.merge());
    }
}

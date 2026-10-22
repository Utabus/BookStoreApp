package com.example.book_selling_app.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.ChatMessage;
import com.example.book_selling_app.repositories.ChatRepository;
import com.google.firebase.firestore.ListenerRegistration;

public class ChatViewModel extends AndroidViewModel {

    private final ChatRepository chatRepository;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        this.chatRepository = new ChatRepository(application);
    }

    public LiveData<Resource<Boolean>> sendMessage(String channelId, ChatMessage message, String userName, String userEmail) {
        return chatRepository.sendMessage(channelId, message, userName, userEmail);
    }

    public ListenerRegistration listenMessages(String channelId, ChatRepository.OnMessagesListener listener) {
        return chatRepository.listenMessages(channelId, listener);
    }

    public ListenerRegistration listenChannels(ChatRepository.OnChannelsListener listener) {
        return chatRepository.listenChannels(listener);
    }

    public void markMessagesAsRead(String channelId, String currentRole) {
        chatRepository.markMessagesAsRead(channelId, currentRole);
    }

    public void uploadChatImage(Uri imageUri, ChatRepository.OnImageUploadedListener listener) {
        chatRepository.uploadChatImage(imageUri, listener);
    }
}

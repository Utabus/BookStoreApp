package com.example.book_selling_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.ChatMessageAdapter;
import com.example.book_selling_app.databinding.ActivityChatBinding;
import com.example.book_selling_app.models.ChatMessage;
import com.example.book_selling_app.repositories.ChatRepository;
import com.example.book_selling_app.utils.ChatNotificationHelper;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.ChatViewModel;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatViewModel chatViewModel;
    private SessionManager sessionManager;

    private String channelId;
    private int targetUserId;
    private String targetUserName;
    private String currentUserRole;
    private int currentUserId;

    private ChatMessageAdapter messageAdapter;
    private final List<ChatMessage> messageList = new ArrayList<>();
    private ListenerRegistration messageListenerRegistration;
    private boolean isInitialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        currentUserRole = sessionManager.getUserRole();
        currentUserId = sessionManager.getUserId();

        initIntentData();
        setupUI();
        setupRecyclerView();
        setupEvents();
        listenToMessages();
    }

    private void initIntentData() {
        channelId = getIntent().getStringExtra("CHANNEL_ID");
        targetUserId = getIntent().getIntExtra("TARGET_USER_ID", 0);
        targetUserName = getIntent().getStringExtra("TARGET_USER_NAME");

        if (TextUtils.isEmpty(channelId)) {
            // Customer is chatting with Admin
            channelId = String.valueOf(currentUserId);
            targetUserName = "CSKH BookStore";
        }

        if (TextUtils.isEmpty(targetUserName)) {
            targetUserName = "ADMIN".equalsIgnoreCase(currentUserRole) ? "Khách hàng #" + channelId : "CSKH BookStore";
        }
    }

    private void setupUI() {
        binding.tvChatTitle.setText(targetUserName);
        binding.tvChatStatus.setText("🟢 Đang hoạt động");
    }

    private void setupRecyclerView() {
        messageAdapter = new ChatMessageAdapter(this, messageList, currentUserId, currentUserRole);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(messageAdapter);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void listenToMessages() {
        binding.progressBarChat.setVisibility(View.VISIBLE);

        messageListenerRegistration = chatViewModel.listenMessages(channelId, new ChatRepository.OnMessagesListener() {
            @Override
            public void onMessages(List<ChatMessage> messages) {
                binding.progressBarChat.setVisibility(View.GONE);

                // Check for new incoming message to trigger notification if app wasn't initial load
                if (!isInitialLoad && !messages.isEmpty() && messages.size() > messageList.size()) {
                    ChatMessage latest = messages.get(messages.size() - 1);
                    boolean isFromOther;
                    if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
                        isFromOther = "USER".equalsIgnoreCase(latest.getSenderRole());
                    } else {
                        isFromOther = "ADMIN".equalsIgnoreCase(latest.getSenderRole());
                    }

                    if (isFromOther) {
                        String snippet = !TextUtils.isEmpty(latest.getMessage()) ? latest.getMessage() : "Bạn có tin nhắn mới";
                        ChatNotificationHelper.showChatNotification(
                                ChatActivity.this,
                                targetUserName,
                                snippet,
                                channelId,
                                targetUserId,
                                targetUserName
                        );
                    }
                }
                isInitialLoad = false;

                messageList.clear();
                messageList.addAll(messages);
                messageAdapter.notifyDataSetChanged();

                if (!messageList.isEmpty()) {
                    binding.rvMessages.scrollToPosition(messageList.size() - 1);
                }

                // Mark messages as read
                chatViewModel.markMessagesAsRead(channelId, currentUserRole);
            }

            @Override
            public void onError(String error) {
                binding.progressBarChat.setVisibility(View.GONE);
            }
        });
    }

    private void sendMessage() {
        String text = binding.edtChatMessage.getText() != null
                ? binding.edtChatMessage.getText().toString().trim()
                : "";

        if (TextUtils.isEmpty(text)) {
            return;
        }

        binding.btnSendMessage.setEnabled(false);

        ChatMessage message = new ChatMessage();
        message.setSenderId(currentUserId);
        message.setSenderName(sessionManager.getUserName());
        message.setSenderRole(currentUserRole);
        message.setMessage(text);
        message.setImageUrl(null);
        message.setTimestamp(System.currentTimeMillis());
        message.setRead(false);

        String senderName = sessionManager.getUserName();
        String senderEmail = sessionManager.getUserEmail();

        chatViewModel.sendMessage(channelId, message, senderName, senderEmail).observe(this, resource -> {
            binding.progressBarChat.setVisibility(View.GONE);
            binding.btnSendMessage.setEnabled(true);

            if (resource != null && resource.isSuccess()) {
                binding.edtChatMessage.setText("");
            } else if (resource != null && resource.isError()) {
                Toast.makeText(this, resource.message != null ? resource.message : "Gửi tin nhắn thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chatViewModel != null && !TextUtils.isEmpty(channelId)) {
            chatViewModel.markMessagesAsRead(channelId, currentUserRole);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListenerRegistration != null) {
            messageListenerRegistration.remove();
        }
    }
}

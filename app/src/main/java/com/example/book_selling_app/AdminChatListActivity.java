package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.ChatChannelAdapter;
import com.example.book_selling_app.databinding.ActivityAdminChatListBinding;
import com.example.book_selling_app.models.ChatChannel;
import com.example.book_selling_app.repositories.ChatRepository;
import com.example.book_selling_app.utils.ChatNotificationHelper;
import com.example.book_selling_app.viewmodels.ChatViewModel;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class AdminChatListActivity extends AppCompatActivity {

    private ActivityAdminChatListBinding binding;
    private ChatViewModel chatViewModel;
    private ChatChannelAdapter channelAdapter;
    private final List<ChatChannel> channelList = new ArrayList<>();
    private ListenerRegistration channelListenerRegistration;
    private boolean isInitialLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        listenToChannels();
    }

    private void setupRecyclerView() {
        channelAdapter = new ChatChannelAdapter(this, channelList, channel -> {
            Intent intent = new Intent(AdminChatListActivity.this, ChatActivity.class);
            intent.putExtra("CHANNEL_ID", channel.getChannelId());
            intent.putExtra("TARGET_USER_ID", channel.getUserId());
            intent.putExtra("TARGET_USER_NAME", channel.getUserName());
            startActivity(intent);
        });

        binding.rvAdminChatChannels.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdminChatChannels.setAdapter(channelAdapter);
    }

    private void listenToChannels() {
        binding.progressBarChannels.setVisibility(View.VISIBLE);

        channelListenerRegistration = chatViewModel.listenChannels(new ChatRepository.OnChannelsListener() {
            @Override
            public void onChannels(List<ChatChannel> channels) {
                binding.progressBarChannels.setVisibility(View.GONE);

                // Check for incoming customer messages to trigger notification
                if (!isInitialLoad && !channels.isEmpty()) {
                    for (ChatChannel ch : channels) {
                        if (ch.getUnreadAdmin() > 0) {
                            ChatNotificationHelper.showChatNotification(
                                    AdminChatListActivity.this,
                                    "Tin nhắn mới từ " + ch.getUserName(),
                                    ch.getLastMessage(),
                                    ch.getChannelId(),
                                    ch.getUserId(),
                                    ch.getUserName()
                            );
                            break;
                        }
                    }
                }
                isInitialLoad = false;

                channelList.clear();
                channelList.addAll(channels);
                channelAdapter.notifyDataSetChanged();

                if (channelList.isEmpty()) {
                    binding.layoutEmptyChannels.setVisibility(View.VISIBLE);
                    binding.rvAdminChatChannels.setVisibility(View.GONE);
                } else {
                    binding.layoutEmptyChannels.setVisibility(View.GONE);
                    binding.rvAdminChatChannels.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.progressBarChannels.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (channelListenerRegistration != null) {
            channelListenerRegistration.remove();
        }
    }
}

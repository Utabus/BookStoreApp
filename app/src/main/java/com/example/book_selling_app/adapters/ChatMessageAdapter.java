package com.example.book_selling_app.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.book_selling_app.R;
import com.example.book_selling_app.databinding.ItemChatMessageBinding;
import com.example.book_selling_app.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private final Context context;
    private final List<ChatMessage> messageList;
    private final int currentUserId;
    private final String currentRole; // "USER" or "ADMIN"
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatMessageAdapter(Context context, List<ChatMessage> messageList, int currentUserId, String currentRole) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.currentRole = currentRole;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        boolean isMe;
        if ("ADMIN".equalsIgnoreCase(currentRole)) {
            isMe = "ADMIN".equalsIgnoreCase(message.getSenderRole());
        } else {
            isMe = (message.getSenderId() == currentUserId) && "USER".equalsIgnoreCase(message.getSenderRole());
        }

        String formattedTime = timeFormat.format(new Date(message.getTimestamp()));

        if (isMe) {
            holder.binding.layoutSent.setVisibility(View.VISIBLE);
            holder.binding.layoutReceived.setVisibility(View.GONE);

            // Message text
            if (TextUtils.isEmpty(message.getMessage())) {
                holder.binding.tvSentMessage.setVisibility(View.GONE);
            } else {
                holder.binding.tvSentMessage.setVisibility(View.VISIBLE);
                holder.binding.tvSentMessage.setText(message.getMessage());
            }

            // Image attachment
            if (!TextUtils.isEmpty(message.getImageUrl())) {
                holder.binding.imgSentAttachment.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.ic_book)
                        .into(holder.binding.imgSentAttachment);
            } else {
                holder.binding.imgSentAttachment.setVisibility(View.GONE);
            }

            holder.binding.tvSentTime.setText(formattedTime);

        } else {
            holder.binding.layoutSent.setVisibility(View.GONE);
            holder.binding.layoutReceived.setVisibility(View.VISIBLE);

            // Sender Name
            String senderLabel = !TextUtils.isEmpty(message.getSenderName()) ? message.getSenderName() : "Hỗ trợ";
            if ("ADMIN".equalsIgnoreCase(message.getSenderRole())) {
                senderLabel = "CSKH BookStore";
            }
            holder.binding.tvReceivedSenderName.setText(senderLabel);

            // Message text
            if (TextUtils.isEmpty(message.getMessage())) {
                holder.binding.tvReceivedMessage.setVisibility(View.GONE);
            } else {
                holder.binding.tvReceivedMessage.setVisibility(View.VISIBLE);
                holder.binding.tvReceivedMessage.setText(message.getMessage());
            }

            // Image attachment
            if (!TextUtils.isEmpty(message.getImageUrl())) {
                holder.binding.imgReceivedAttachment.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.ic_book)
                        .into(holder.binding.imgReceivedAttachment);
            } else {
                holder.binding.imgReceivedAttachment.setVisibility(View.GONE);
            }

            holder.binding.tvReceivedTime.setText(formattedTime);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        final ItemChatMessageBinding binding;

        MessageViewHolder(ItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

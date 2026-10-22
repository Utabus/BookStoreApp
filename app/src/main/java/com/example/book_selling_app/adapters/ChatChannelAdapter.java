package com.example.book_selling_app.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.databinding.ItemChatChannelBinding;
import com.example.book_selling_app.models.ChatChannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatChannelAdapter extends RecyclerView.Adapter<ChatChannelAdapter.ChannelViewHolder> {

    public interface OnChannelClickListener {
        void onChannelClick(ChatChannel channel);
    }

    private final Context context;
    private final List<ChatChannel> channelList;
    private final OnChannelClickListener listener;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());

    public ChatChannelAdapter(Context context, List<ChatChannel> channelList, OnChannelClickListener listener) {
        this.context = context;
        this.channelList = channelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatChannelBinding binding = ItemChatChannelBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ChannelViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        ChatChannel channel = channelList.get(position);

        String name = !TextUtils.isEmpty(channel.getUserName()) ? channel.getUserName() : "Khách hàng #" + channel.getUserId();
        holder.binding.tvChannelUserName.setText(name);

        holder.binding.tvChannelLastMessage.setText(
                !TextUtils.isEmpty(channel.getLastMessage()) ? channel.getLastMessage() : "[Cuộc hội thoại mới]"
        );

        if (channel.getLastTimestamp() > 0) {
            holder.binding.tvChannelLastTime.setText(timeFormat.format(new Date(channel.getLastTimestamp())));
        } else {
            holder.binding.tvChannelLastTime.setText("");
        }

        if (channel.getUnreadAdmin() > 0) {
            holder.binding.tvChannelUnreadBadge.setVisibility(View.VISIBLE);
            holder.binding.tvChannelUnreadBadge.setText(String.valueOf(channel.getUnreadAdmin()));
            holder.binding.tvChannelLastMessage.setTextColor(context.getColor(com.example.book_selling_app.R.color.text_primary));
            holder.binding.tvChannelLastMessage.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.binding.tvChannelUnreadBadge.setVisibility(View.GONE);
            holder.binding.tvChannelLastMessage.setTextColor(context.getColor(com.example.book_selling_app.R.color.text_secondary));
            holder.binding.tvChannelLastMessage.setTypeface(null, android.graphics.Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChannelClick(channel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    static class ChannelViewHolder extends RecyclerView.ViewHolder {
        final ItemChatChannelBinding binding;

        ChannelViewHolder(ItemChatChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

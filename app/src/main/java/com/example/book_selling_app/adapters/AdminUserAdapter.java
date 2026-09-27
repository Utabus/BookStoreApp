package com.example.book_selling_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.R;
import com.example.book_selling_app.databinding.ItemAdminUserBinding;
import com.example.book_selling_app.models.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    public interface OnUserStatusToggleListener {
        void onToggle(User user);
    }

    private Context context;
    private List<User> userList;
    private int currentAdminId;
    private OnUserStatusToggleListener listener;

    public AdminUserAdapter(Context context, List<User> userList, int currentAdminId, OnUserStatusToggleListener listener) {
        this.context = context;
        this.userList = userList;
        this.currentAdminId = currentAdminId;
        this.listener = listener;
    }

    public void updateData(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminUserBinding binding = ItemAdminUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.binding.tvAdminUserName.setText(user.getFullName());
        holder.binding.tvAdminUserEmail.setText(user.getEmail());
        holder.binding.tvAdminUserPhone.setText("SĐT: " + (user.getPhone() != null ? user.getPhone() : "Chưa có"));
        holder.binding.tvAdminUserRole.setText(user.getRole());

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            holder.binding.tvAdminUserRole.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.primary));
        } else {
            holder.binding.tvAdminUserRole.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.secondary));
        }

        // Disable self-lock
        if (user.getId() == currentAdminId) {
            holder.binding.btnToggleUserStatus.setVisibility(View.GONE);
        } else {
            holder.binding.btnToggleUserStatus.setVisibility(View.VISIBLE);
            if (user.isActive()) {
                holder.binding.btnToggleUserStatus.setText("Khóa tài khoản");
                holder.binding.btnToggleUserStatus.setTextColor(ContextCompat.getColor(context, R.color.status_error));
                holder.binding.btnToggleUserStatus.setStrokeColor(ContextCompat.getColorStateList(context, R.color.status_error));
            } else {
                holder.binding.btnToggleUserStatus.setText("Mở khóa");
                holder.binding.btnToggleUserStatus.setTextColor(ContextCompat.getColor(context, R.color.status_success));
                holder.binding.btnToggleUserStatus.setStrokeColor(ContextCompat.getColorStateList(context, R.color.status_success));
            }
        }

        holder.binding.btnToggleUserStatus.setOnClickListener(v -> {
            if (listener != null) listener.onToggle(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ItemAdminUserBinding binding;

        public UserViewHolder(@NonNull ItemAdminUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

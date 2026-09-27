package com.example.book_selling_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.book_selling_app.R;
import com.example.book_selling_app.databinding.ItemOrderDetailBookBinding;
import com.example.book_selling_app.models.OrderItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private final List<OrderItem> items = new ArrayList<>();
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    public void setItems(List<OrderItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderDetailBookBinding binding = ItemOrderDetailBookBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.binding.tvOrderItemTitle.setText(item.getBookTitle());
        holder.binding.tvOrderItemAuthor.setText(item.getBookAuthor());
        holder.binding.tvOrderItemPrice.setText(formatter.format(item.getPrice()));
        holder.binding.tvOrderItemQuantity.setText("x" + item.getQuantity());
        holder.binding.tvOrderItemSubtotal.setText(formatter.format(item.getSubtotal()));

        if (item.getBookImage() != null && !item.getBookImage().isEmpty()) {
            Glide.with(context)
                    .load(item.getBookImage())
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(holder.binding.imgOrderItem);
        } else {
            holder.binding.imgOrderItem.setImageResource(R.drawable.ic_book);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        final ItemOrderDetailBookBinding binding;

        public OrderItemViewHolder(@NonNull ItemOrderDetailBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

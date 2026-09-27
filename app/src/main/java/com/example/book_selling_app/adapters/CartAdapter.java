package com.example.book_selling_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.book_selling_app.R;
import com.example.book_selling_app.databinding.ItemCartBinding;
import com.example.book_selling_app.models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartItemChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }

    private final Context context;
    private final List<CartItem> cartItems;
    private final OnCartItemChangeListener listener;
    private final NumberFormat currencyFormatter;

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
        this.currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        if (item.getBook() != null) {
            holder.binding.tvCartBookTitle.setText(item.getBook().getTitle());
            holder.binding.tvCartBookPrice.setText(currencyFormatter.format(item.getBook().getPrice()) + " đ");

            Glide.with(context)
                    .load(item.getBook().getImageUrl())
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .centerCrop()
                    .into(holder.binding.imgCartBook);
        }

        holder.binding.tvQuantity.setText(String.valueOf(item.getQuantity()));

        holder.binding.btnPlus.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            item.setQuantity(newQty);
            holder.binding.tvQuantity.setText(String.valueOf(newQty));
            if (listener != null) {
                listener.onQuantityChanged(item, newQty);
            }
        });

        holder.binding.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQty = item.getQuantity() - 1;
                item.setQuantity(newQty);
                holder.binding.tvQuantity.setText(String.valueOf(newQty));
                if (listener != null) {
                    listener.onQuantityChanged(item, newQty);
                }
            }
        });

        holder.binding.btnRemoveItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        final ItemCartBinding binding;

        CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

package com.example.book_selling_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.R;
import com.example.book_selling_app.databinding.ItemCategoryChipBinding;
import com.example.book_selling_app.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    private final Context context;
    private final List<Category> categories;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < getItemCount()) {
            int oldPos = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
        }
    }

    public void setSelectedCategoryId(int categoryId) {
        if (categories == null) return;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == categoryId) {
                setSelectedPosition(i);
                break;
            }
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryChipBinding binding = ItemCategoryChipBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.binding.tvCategoryName.setText(category.getName());

        if (position == selectedPosition) {
            holder.binding.tvCategoryName.setBackgroundResource(R.drawable.bg_chip_selected);
            holder.binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.binding.tvCategoryName.setBackgroundResource(R.drawable.bg_chip_unselected);
            holder.binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && pos < categories.size()) {
                int oldPos = selectedPosition;
                selectedPosition = pos;
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onCategoryClick(categories.get(pos), selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final ItemCategoryChipBinding binding;

        CategoryViewHolder(ItemCategoryChipBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

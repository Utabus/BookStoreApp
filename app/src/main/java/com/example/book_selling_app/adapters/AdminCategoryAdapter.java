package com.example.book_selling_app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.databinding.ItemAdminCategoryBinding;
import com.example.book_selling_app.models.Category;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryActionListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    private List<Category> categoryList;
    private OnCategoryActionListener listener;

    public AdminCategoryAdapter(List<Category> categoryList, OnCategoryActionListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public void updateData(List<Category> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminCategoryBinding binding = ItemAdminCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.binding.tvAdminCategoryName.setText(category.getName());
        holder.binding.tvAdminCategoryCount.setText(category.getBookCount() + " đầu sách trong thể loại");

        holder.binding.btnEditCategory.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(category);
        });

        holder.binding.btnDeleteCategory.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(category);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemAdminCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemAdminCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

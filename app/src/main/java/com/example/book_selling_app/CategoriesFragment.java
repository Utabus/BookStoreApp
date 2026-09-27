package com.example.book_selling_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.databinding.FragmentCategoriesBinding;
import com.example.book_selling_app.databinding.ItemCategoryRowBinding;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.viewmodels.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private CategoryViewModel categoryViewModel;
    private final List<Category> categoryList = new ArrayList<>();
    private RecyclerView.Adapter<CategoryRowViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        binding.rvCategoriesList.setLayoutManager(new LinearLayoutManager(getContext()));

        setupAdapter();
        observeCategories();
    }

    private void setupAdapter() {
        adapter = new RecyclerView.Adapter<CategoryRowViewHolder>() {
            @NonNull
            @Override
            public CategoryRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemCategoryRowBinding b = ItemCategoryRowBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
                return new CategoryRowViewHolder(b);
            }

            @Override
            public void onBindViewHolder(@NonNull CategoryRowViewHolder holder, int position) {
                Category cat = categoryList.get(position);
                holder.binding.tvCategoryTitle.setText(cat.getName());
                holder.binding.tvCategoryBookCount.setText(cat.getBookCount() + " cuốn sách đang có");

                holder.itemView.setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateToTab(R.id.nav_home);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return categoryList.size();
            }
        };
        binding.rvCategoriesList.setAdapter(adapter);
    }

    private void observeCategories() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                categoryList.clear();
                categoryList.addAll(resource.data);
                adapter.notifyDataSetChanged();
            }
        });
    }

    static class CategoryRowViewHolder extends RecyclerView.ViewHolder {
        final ItemCategoryRowBinding binding;
        CategoryRowViewHolder(ItemCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

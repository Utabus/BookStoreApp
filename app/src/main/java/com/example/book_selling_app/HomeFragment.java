package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.BookAdapter;
import com.example.book_selling_app.adapters.CategoryAdapter;
import com.example.book_selling_app.databinding.FragmentHomeBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Category;
import com.example.book_selling_app.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private BookAdapter bookAdapter;
    private CategoryAdapter categoryAdapter;
    private final List<Book> allBooks = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerViews();
        observeCategories();
        loadBooks(-1); // -1 = All books
        setupEvents();
    }

    private void setupRecyclerViews() {
        // Horizontal Categories
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList, (category, position) -> {
            if (position == 0) {
                loadBooks(-1);
            } else {
                loadBooks(category.getId());
            }
        });
        binding.rvCategories.setAdapter(categoryAdapter);

        // 2-Column Grid Books
        binding.rvBooks.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bookAdapter = new BookAdapter(requireContext(), allBooks, book -> {
            Intent intent = new Intent(getContext(), BookDetailActivity.class);
            intent.putExtra("BOOK", book);
            startActivity(intent);
        });
        binding.rvBooks.setAdapter(bookAdapter);
    }

    private void observeCategories() {
        homeViewModel.getCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                categoryList.clear();
                categoryList.add(new Category(-1, "Tất cả"));
                categoryList.addAll(resource.data);
                categoryAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadBooks(int categoryId) {
        if (categoryId == -1) {
            homeViewModel.getFeaturedBooks().observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.isSuccess() && resource.data != null) {
                    allBooks.clear();
                    allBooks.addAll(resource.data);
                    bookAdapter.updateData(allBooks);
                    binding.tvBookCount.setText(allBooks.size() + " tựa sách");
                }
            });
        } else {
            homeViewModel.getBooksByCategory(categoryId).observe(getViewLifecycleOwner(), resource -> {
                if (resource != null && resource.isSuccess() && resource.data != null) {
                    allBooks.clear();
                    allBooks.addAll(resource.data);
                    bookAdapter.updateData(allBooks);
                    binding.tvBookCount.setText(allBooks.size() + " tựa sách");
                }
            });
        }
    }

    private void setupEvents() {
        binding.layoutSearchBar.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SearchActivity.class));
        });

        binding.btnHomeBarcodeScan.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("AUTO_SCAN", true);
            startActivity(intent);
        });

        binding.btnHeaderCart.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_cart);
            }
        });

        binding.tvSeeAllCategories.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_categories);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

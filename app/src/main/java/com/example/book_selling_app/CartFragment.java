package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.CartAdapter;
import com.example.book_selling_app.databinding.FragmentCartBinding;
import com.example.book_selling_app.models.CartItem;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.CartViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private SessionManager sessionManager;
    private CartAdapter cartAdapter;
    private final List<CartItem> cartItems = new ArrayList<>();
    private final NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        sessionManager = new SessionManager(requireContext());

        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(requireContext(), cartItems, new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                cartViewModel.updateQuantity(item.getId(), newQuantity).observe(getViewLifecycleOwner(), res -> {
                    if (res != null && res.isSuccess()) {
                        item.setQuantity(newQuantity);
                        updateTotalPrice();
                    }
                });
            }

            @Override
            public void onItemRemoved(CartItem item) {
                cartViewModel.removeFromCart(item.getId()).observe(getViewLifecycleOwner(), res -> {
                    if (res != null && res.isSuccess()) {
                        cartItems.remove(item);
                        cartAdapter.notifyDataSetChanged();
                        checkEmptyState();
                        updateTotalPrice();
                        Toast.makeText(getContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        binding.rvCartItems.setAdapter(cartAdapter);

        binding.btnShopNow.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_home);
            }
        });

        binding.tvClearCart.setOnClickListener(v -> clearAllCart());

        binding.btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(getContext(), CheckoutActivity.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartData();
    }

    private void loadCartData() {
        int userId = sessionManager.getUserId();
        cartViewModel.getCartItems(userId).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                cartItems.clear();
                cartItems.addAll(resource.data);
                cartAdapter.notifyDataSetChanged();
                checkEmptyState();
                updateTotalPrice();
            }
        });
    }

    private void clearAllCart() {
        int userId = sessionManager.getUserId();
        cartViewModel.clearCart(userId).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess()) {
                cartItems.clear();
                cartAdapter.notifyDataSetChanged();
                checkEmptyState();
                updateTotalPrice();
            }
        });
    }

    private void checkEmptyState() {
        if (cartItems.isEmpty()) {
            binding.layoutEmptyCart.setVisibility(View.VISIBLE);
            binding.rvCartItems.setVisibility(View.GONE);
            binding.tvClearCart.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyCart.setVisibility(View.GONE);
            binding.rvCartItems.setVisibility(View.VISIBLE);
            binding.tvClearCart.setVisibility(View.VISIBLE);
        }
    }

    private void updateTotalPrice() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getSubtotal();
        }

        binding.tvSubtotal.setText(currencyFormatter.format(subtotal) + " đ");
        binding.tvTotalAmount.setText(currencyFormatter.format(subtotal) + " đ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

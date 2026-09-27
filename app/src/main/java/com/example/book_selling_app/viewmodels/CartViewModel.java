package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.CartItem;
import com.example.book_selling_app.repositories.CartRepository;

import java.util.List;

public class CartViewModel extends AndroidViewModel {

    private final CartRepository cartRepository;

    public CartViewModel(@NonNull Application application) {
        super(application);
        this.cartRepository = new CartRepository(application);
    }

    public LiveData<Resource<List<CartItem>>> getCartItems(int userId) {
        return cartRepository.getCartItems(userId);
    }

    public LiveData<Resource<Boolean>> updateQuantity(int cartId, int newQuantity) {
        return cartRepository.updateQuantity(cartId, newQuantity);
    }

    public LiveData<Resource<Boolean>> removeFromCart(int cartId) {
        return cartRepository.removeFromCart(cartId);
    }

    public LiveData<Resource<Boolean>> clearCart(int userId) {
        return cartRepository.clearCart(userId);
    }
}

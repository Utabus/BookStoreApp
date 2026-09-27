package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.repositories.OrderRepository;

public class CheckoutViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        this.orderRepository = new OrderRepository(application);
    }

    public LiveData<Resource<Long>> createOrder(int userId, String address, String phone, String payment, double totalAmount) {
        return orderRepository.createOrder(userId, address, phone, payment, totalAmount);
    }
}

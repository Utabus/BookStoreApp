package com.example.book_selling_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.book_selling_app.data.utils.Resource;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Order;
import com.example.book_selling_app.models.OrderItem;
import com.example.book_selling_app.repositories.OrderRepository;

import java.util.List;

public class OrderViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        this.orderRepository = new OrderRepository(application);
    }

    public LiveData<Resource<List<Order>>> getUserOrders(int userId) {
        return orderRepository.getUserOrders(userId);
    }

    public LiveData<Resource<Boolean>> cancelOrder(int orderId) {
        return orderRepository.cancelOrder(orderId);
    }

    public LiveData<Resource<List<Book>>> getBooksInOrder(int orderId) {
        return orderRepository.getBooksInOrder(orderId);
    }

    public LiveData<Resource<List<OrderItem>>> getOrderItems(int orderId) {
        return orderRepository.getOrderItems(orderId);
    }
}

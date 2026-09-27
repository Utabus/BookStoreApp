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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.databinding.FragmentOrdersBinding;
import com.example.book_selling_app.databinding.ItemOrderCardBinding;
import com.example.book_selling_app.models.Order;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.OrderViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private OrderViewModel orderViewModel;
    private SessionManager sessionManager;
    private final List<Order> orderList = new ArrayList<>();
    private final NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    private RecyclerView.Adapter<OrderViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        sessionManager = new SessionManager(requireContext());

        binding.rvOrdersList.setLayoutManager(new LinearLayoutManager(getContext()));
        setupAdapter();
        loadOrders();
    }

    private void setupAdapter() {
        adapter = new RecyclerView.Adapter<OrderViewHolder>() {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemOrderCardBinding b = ItemOrderCardBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
                return new OrderViewHolder(b);
            }

            @Override
            public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
                Order order = orderList.get(position);
                holder.binding.tvOrderId.setText("Mã đơn: #ORD-" + order.getId());
                holder.binding.tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
                holder.binding.tvOrderAddress.setText("Địa chỉ: " + order.getShippingAddress());
                holder.binding.tvOrderStatus.setText(order.getStatus());
                holder.binding.tvOrderTotal.setText(currencyFormatter.format(order.getTotalAmount()) + " đ");

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                    intent.putExtra("ORDER_EXTRA", order);
                    startActivity(intent);
                });
            }

            @Override
            public int getItemCount() {
                return orderList.size();
            }
        };
        binding.rvOrdersList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        int userId = sessionManager.getUserId();
        orderViewModel.getUserOrders(userId).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                orderList.clear();
                orderList.addAll(resource.data);
                adapter.notifyDataSetChanged();

                if (orderList.isEmpty()) {
                    binding.layoutEmptyOrders.setVisibility(View.VISIBLE);
                    binding.rvOrdersList.setVisibility(View.GONE);
                } else {
                    binding.layoutEmptyOrders.setVisibility(View.GONE);
                    binding.rvOrdersList.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final ItemOrderCardBinding binding;
        OrderViewHolder(ItemOrderCardBinding binding) {
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

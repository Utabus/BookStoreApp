package com.example.book_selling_app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_selling_app.R;
import com.example.book_selling_app.databinding.ItemAdminOrderBinding;
import com.example.book_selling_app.models.Order;

import java.text.DecimalFormat;
import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener;
    private DecimalFormat formatter = new DecimalFormat("#,### đ");

    public AdminOrderAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    public void updateData(List<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminOrderBinding binding = ItemAdminOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.binding.tvAdminOrderId.setText("Mã đơn: #ORD-" + order.getId());
        holder.binding.tvAdminCustomer.setText("Khách hàng: " + (order.getCustomerName() != null ? order.getCustomerName() : "Khách vãng lai") + " (" + (order.getCustomerPhone() != null ? order.getCustomerPhone() : "") + ")");
        holder.binding.tvAdminOrderDate.setText("Ngày đặt: " + order.getOrderDate());
        holder.binding.tvAdminOrderAddress.setText("Địa chỉ: " + order.getShippingAddress());
        holder.binding.tvAdminOrderTotal.setText(formatter.format(order.getTotalAmount()));

        String status = order.getStatus();
        holder.binding.tvAdminOrderStatus.setText(status);

        if ("PENDING".equalsIgnoreCase(status)) {
            holder.binding.tvAdminOrderStatus.setText("CHỜ DUYỆT");
            holder.binding.tvAdminOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_warning));
        } else if ("CONFIRMED".equalsIgnoreCase(status)) {
            holder.binding.tvAdminOrderStatus.setText("ĐÃ DUYỆT");
            holder.binding.tvAdminOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
        } else if ("SHIPPING".equalsIgnoreCase(status)) {
            holder.binding.tvAdminOrderStatus.setText("ĐANG GIAO");
            holder.binding.tvAdminOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.secondary));
        } else if ("DELIVERED".equalsIgnoreCase(status)) {
            holder.binding.tvAdminOrderStatus.setText("ĐÃ GIAO");
            holder.binding.tvAdminOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_success));
        } else if ("CANCELLED".equalsIgnoreCase(status)) {
            holder.binding.tvAdminOrderStatus.setText("ĐÃ HỦY");
            holder.binding.tvAdminOrderStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_error));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemAdminOrderBinding binding;

        public OrderViewHolder(@NonNull ItemAdminOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

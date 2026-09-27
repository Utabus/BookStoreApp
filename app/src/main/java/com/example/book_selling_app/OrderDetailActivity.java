package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.OrderItemAdapter;
import com.example.book_selling_app.databinding.ActivityOrderDetailBinding;
import com.example.book_selling_app.models.Book;
import com.example.book_selling_app.models.Order;
import com.example.book_selling_app.viewmodels.OrderViewModel;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private ActivityOrderDetailBinding binding;
    private OrderViewModel orderViewModel;
    private Order order;
    private OrderItemAdapter itemAdapter;
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        itemAdapter = new OrderItemAdapter();
        binding.rvUserOrderDetailItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUserOrderDetailItems.setAdapter(itemAdapter);

        if (getIntent().hasExtra("ORDER_EXTRA")) {
            order = (Order) getIntent().getSerializableExtra("ORDER_EXTRA");
        }

        if (order != null) {
            displayDetails();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupEvents();
    }

    private void displayDetails() {
        binding.tvUserOrderDetailTitle.setText("Đơn hàng #ORD-" + order.getId());
        binding.tvUserDetailOrderId.setText("Mã đơn hàng: #ORD-" + order.getId());
        binding.tvUserDetailOrderDate.setText("Thời gian đặt: " + order.getOrderDate());
        binding.tvUserDetailAddress.setText("Địa chỉ: " + order.getShippingAddress());
        binding.tvUserDetailPayment.setText("Phương thức: " + order.getPaymentMethod());
        binding.tvUserDetailTotal.setText(formatter.format(order.getTotalAmount()));

        updateStatusUI(order.getStatus());

        // Load items in this order
        orderViewModel.getOrderItems(order.getId()).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                itemAdapter.setItems(resource.data);
            }
        });
    }

    private void updateStatusUI(String status) {
        binding.btnUserCancelOrder.setVisibility(View.GONE);
        binding.btnUserReviewOrder.setVisibility(View.GONE);
        binding.layoutUserOrderActions.setVisibility(View.GONE);

        if ("PENDING".equalsIgnoreCase(status) || "CHỜ XỬ LÝ".equalsIgnoreCase(status) || "CHỜ DUYỆT".equalsIgnoreCase(status)) {
            binding.tvUserDetailStatusBadge.setText("CHỜ XỬ LÝ");
            binding.tvUserDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_warning));
            binding.btnUserCancelOrder.setVisibility(View.VISIBLE);
            binding.layoutUserOrderActions.setVisibility(View.VISIBLE);
        } else if ("CONFIRMED".equalsIgnoreCase(status) || "ĐÃ DUYỆT".equalsIgnoreCase(status)) {
            binding.tvUserDetailStatusBadge.setText("ĐÃ DUYỆT");
            binding.tvUserDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
        } else if ("SHIPPING".equalsIgnoreCase(status) || "ĐANG GIAO".equalsIgnoreCase(status)) {
            binding.tvUserDetailStatusBadge.setText("ĐANG GIAO");
            binding.tvUserDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary));
        } else if ("DELIVERED".equalsIgnoreCase(status) || "ĐÃ GIAO".equalsIgnoreCase(status) || "ĐÃ GIAO HÀNG".equalsIgnoreCase(status)) {
            binding.tvUserDetailStatusBadge.setText("ĐÃ GIAO HÀNG");
            binding.tvUserDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_success));
            binding.btnUserReviewOrder.setVisibility(View.VISIBLE);
            binding.layoutUserOrderActions.setVisibility(View.VISIBLE);
        } else if ("CANCELLED".equalsIgnoreCase(status) || "ĐÃ HỦY".equalsIgnoreCase(status) || "ĐÃ HỦY ĐƠN".equalsIgnoreCase(status)) {
            binding.tvUserDetailStatusBadge.setText("ĐÃ HỦY ĐƠN");
            binding.tvUserDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_error));
        }
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnUserCancelOrder.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hủy đơn hàng")
                    .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không?")
                    .setPositiveButton("Hủy đơn", (dialog, which) -> {
                        orderViewModel.cancelOrder(order.getId()).observe(this, resource -> {
                            if (resource != null && resource.isSuccess()) {
                                order.setStatus("CANCELLED");
                                updateStatusUI("CANCELLED");
                                Toast.makeText(this, "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        });

        binding.btnUserReviewOrder.setOnClickListener(v -> {
            orderViewModel.getBooksInOrder(order.getId()).observe(this, resource -> {
                if (resource != null && resource.isSuccess() && resource.data != null) {
                    List<Book> booksInOrder = resource.data;
                    Intent reviewIntent = new Intent(OrderDetailActivity.this, WriteReviewActivity.class);
                    if (!booksInOrder.isEmpty()) {
                        Book book = booksInOrder.get(0);
                        reviewIntent.putExtra("BOOK_ID", book.getId());
                        reviewIntent.putExtra("BOOK_TITLE", book.getTitle());
                        reviewIntent.putExtra("BOOK_AUTHOR", book.getAuthor());
                        reviewIntent.putExtra("BOOK_IMAGE", book.getImageUrl());
                    } else {
                        reviewIntent.putExtra("BOOK_ID", 1);
                        reviewIntent.putExtra("BOOK_TITLE", "Sách trong đơn hàng #" + order.getId());
                        reviewIntent.putExtra("BOOK_AUTHOR", "Nhiều tác giả");
                    }
                    startActivity(reviewIntent);
                }
            });
        });
    }
}

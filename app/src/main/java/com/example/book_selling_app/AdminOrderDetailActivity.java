package com.example.book_selling_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.book_selling_app.adapters.OrderItemAdapter;
import com.example.book_selling_app.databinding.ActivityAdminOrderDetailBinding;
import com.example.book_selling_app.models.Order;
import com.example.book_selling_app.viewmodels.AdminOrderViewModel;

import java.text.DecimalFormat;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private ActivityAdminOrderDetailBinding binding;
    private AdminOrderViewModel orderViewModel;
    private Order order;
    private OrderItemAdapter itemAdapter;
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderViewModel = new ViewModelProvider(this).get(AdminOrderViewModel.class);

        itemAdapter = new OrderItemAdapter();
        binding.rvAdminOrderDetailItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdminOrderDetailItems.setAdapter(itemAdapter);

        if (getIntent().hasExtra("ORDER_EXTRA")) {
            order = (Order) getIntent().getSerializableExtra("ORDER_EXTRA");
        }

        if (order != null) {
            displayOrderDetails();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupEvents();
    }

    private void displayOrderDetails() {
        binding.tvAdminOrderDetailTitle.setText("Đơn hàng #ORD-" + order.getId());
        binding.tvDetailOrderId.setText("Mã đơn hàng: #ORD-" + order.getId());
        binding.tvDetailOrderDate.setText("Thời gian đặt: " + order.getOrderDate());
        binding.tvDetailCustomerName.setText("Người nhận: " + (order.getCustomerName() != null ? order.getCustomerName() : "Khách hàng"));
        binding.tvDetailPhone.setText("Số điện thoại: " + (order.getCustomerPhone() != null ? order.getCustomerPhone() : "Chưa cập nhật"));
        binding.tvDetailAddress.setText("Địa chỉ: " + order.getShippingAddress());
        binding.tvDetailPayment.setText("Hình thức: " + order.getPaymentMethod());
        binding.tvDetailTotal.setText(formatter.format(order.getTotalAmount()));

        updateStatusUI(order.getStatus());

        // Load items in this order
        orderViewModel.getOrderItems(order.getId()).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                itemAdapter.setItems(resource.data);
            }
        });
    }

    private void updateStatusUI(String status) {
        binding.tvDetailStatusBadge.setText(status);

        if ("PENDING".equalsIgnoreCase(status) || "CHỜ XỬ LÝ".equalsIgnoreCase(status) || "CHỜ DUYỆT".equalsIgnoreCase(status)) {
            binding.tvDetailStatusBadge.setText("CHỜ DUYỆT");
            binding.tvDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_warning));

            binding.btnCancelOrder.setVisibility(View.VISIBLE);
            binding.btnAdvanceStatus.setVisibility(View.VISIBLE);
            binding.btnAdvanceStatus.setText("DUYỆT ĐƠN HÀNG");
            binding.btnAdvanceStatus.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
        } else if ("CONFIRMED".equalsIgnoreCase(status) || "ĐÃ DUYỆT".equalsIgnoreCase(status)) {
            binding.tvDetailStatusBadge.setText("ĐÃ DUYỆT");
            binding.tvDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));

            binding.btnCancelOrder.setVisibility(View.GONE);
            binding.btnAdvanceStatus.setVisibility(View.VISIBLE);
            binding.btnAdvanceStatus.setText("CHUYỂN SANG ĐANG GIAO");
            binding.btnAdvanceStatus.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.secondary));
        } else if ("SHIPPING".equalsIgnoreCase(status) || "ĐANG GIAO".equalsIgnoreCase(status)) {
            binding.tvDetailStatusBadge.setText("ĐANG GIAO");
            binding.tvDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary));

            binding.btnCancelOrder.setVisibility(View.GONE);
            binding.btnAdvanceStatus.setVisibility(View.VISIBLE);
            binding.btnAdvanceStatus.setText("XÁC NHẬN ĐÃ GIAO THÀNH CÔNG");
            binding.btnAdvanceStatus.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.status_success));
        } else if ("DELIVERED".equalsIgnoreCase(status) || "ĐÃ GIAO".equalsIgnoreCase(status) || "ĐÃ GIAO HÀNG".equalsIgnoreCase(status)) {
            binding.tvDetailStatusBadge.setText("ĐÃ GIAO THÀNH CÔNG");
            binding.tvDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_success));

            binding.btnCancelOrder.setVisibility(View.GONE);
            binding.btnAdvanceStatus.setVisibility(View.GONE);
        } else if ("CANCELLED".equalsIgnoreCase(status) || "ĐÃ HỦY".equalsIgnoreCase(status) || "ĐÃ HỦY ĐƠN".equalsIgnoreCase(status)) {
            binding.tvDetailStatusBadge.setText("ĐÃ HỦY ĐƠN");
            binding.tvDetailStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.status_error));

            binding.btnCancelOrder.setVisibility(View.GONE);
            binding.btnAdvanceStatus.setVisibility(View.GONE);
        }
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnCancelOrder.setOnClickListener(v -> {
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

        binding.btnAdvanceStatus.setOnClickListener(v -> {
            String current = order.getStatus();
            String nextStatus = "";
            if ("PENDING".equalsIgnoreCase(current) || "CHỜ XỬ LÝ".equalsIgnoreCase(current) || "CHỜ DUYỆT".equalsIgnoreCase(current)) {
                nextStatus = "CONFIRMED";
            } else if ("CONFIRMED".equalsIgnoreCase(current) || "ĐÃ DUYỆT".equalsIgnoreCase(current)) {
                nextStatus = "SHIPPING";
            } else if ("SHIPPING".equalsIgnoreCase(current) || "ĐANG GIAO".equalsIgnoreCase(current)) {
                nextStatus = "DELIVERED";
            }

            if (!nextStatus.isEmpty()) {
                final String targetStatus = nextStatus;
                orderViewModel.updateOrderStatus(order.getId(), targetStatus).observe(this, resource -> {
                    if (resource != null && resource.isSuccess()) {
                        order.setStatus(targetStatus);
                        updateStatusUI(targetStatus);
                        Toast.makeText(this, "Đã cập nhật trạng thái đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

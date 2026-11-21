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

        updateStepperUI(status);
    }

    private void updateStepperUI(String status) {
        if ("CANCELLED".equalsIgnoreCase(status) || "ĐÃ HỦY".equalsIgnoreCase(status) || "ĐÃ HỦY ĐƠN".equalsIgnoreCase(status)) {
            binding.stepperView.layoutStepperTimeline.setVisibility(View.GONE);
            binding.stepperView.layoutStepperCancelled.setVisibility(View.VISIBLE);
            return;
        }

        binding.stepperView.layoutStepperTimeline.setVisibility(View.VISIBLE);
        binding.stepperView.layoutStepperCancelled.setVisibility(View.GONE);

        int colorPrimary = ContextCompat.getColor(this, R.color.primary);
        int colorSecondary = ContextCompat.getColor(this, R.color.secondary);
        int colorSuccess = ContextCompat.getColor(this, R.color.status_success);
        int colorMuted = ContextCompat.getColor(this, R.color.text_muted);
        int colorSecondaryText = ContextCompat.getColor(this, R.color.text_secondary);
        int colorWhite = ContextCompat.getColor(this, R.color.white);
        int colorInactive = ContextCompat.getColor(this, R.color.surface_variant);
        int colorBorder = ContextCompat.getColor(this, R.color.border_subtle);

        boolean isConfirmed = "CONFIRMED".equalsIgnoreCase(status) || "ĐÃ DUYỆT".equalsIgnoreCase(status)
                || "SHIPPING".equalsIgnoreCase(status) || "ĐANG GIAO".equalsIgnoreCase(status)
                || "DELIVERED".equalsIgnoreCase(status) || "ĐÃ GIAO".equalsIgnoreCase(status) || "ĐÃ GIAO HÀNG".equalsIgnoreCase(status);

        boolean isShipping = "SHIPPING".equalsIgnoreCase(status) || "ĐANG GIAO".equalsIgnoreCase(status)
                || "DELIVERED".equalsIgnoreCase(status) || "ĐÃ GIAO".equalsIgnoreCase(status) || "ĐÃ GIAO HÀNG".equalsIgnoreCase(status);

        boolean isDelivered = "DELIVERED".equalsIgnoreCase(status) || "ĐÃ GIAO".equalsIgnoreCase(status) || "ĐÃ GIAO HÀNG".equalsIgnoreCase(status);

        // Step 1: Placed (always done)
        binding.stepperView.cardStep1.setCardBackgroundColor(colorPrimary);
        binding.stepperView.imgStep1.setColorFilter(colorWhite);
        binding.stepperView.tvStep1Label.setTextColor(colorPrimary);

        // Line 1 -> 2
        binding.stepperView.lineStep1To2.setBackgroundColor(isConfirmed ? colorPrimary : colorBorder);

        // Step 2: Confirmed
        binding.stepperView.cardStep2.setCardBackgroundColor(isConfirmed ? colorPrimary : colorInactive);
        binding.stepperView.imgStep2.setColorFilter(isConfirmed ? colorWhite : colorMuted);
        binding.stepperView.tvStep2Label.setTextColor(isConfirmed ? colorPrimary : colorSecondaryText);

        // Line 2 -> 3
        binding.stepperView.lineStep2To3.setBackgroundColor(isShipping ? colorSecondary : colorBorder);

        // Step 3: Shipping
        binding.stepperView.cardStep3.setCardBackgroundColor(isShipping ? colorSecondary : colorInactive);
        binding.stepperView.imgStep3.setColorFilter(isShipping ? colorWhite : colorMuted);
        binding.stepperView.tvStep3Label.setTextColor(isShipping ? colorSecondary : colorSecondaryText);

        // Line 3 -> 4
        binding.stepperView.lineStep3To4.setBackgroundColor(isDelivered ? colorSuccess : colorBorder);

        // Step 4: Delivered
        binding.stepperView.cardStep4.setCardBackgroundColor(isDelivered ? colorSuccess : colorInactive);
        binding.stepperView.imgStep4.setColorFilter(isDelivered ? colorWhite : colorMuted);
        binding.stepperView.tvStep4Label.setTextColor(isDelivered ? colorSuccess : colorSecondaryText);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnShareOrderInvoice.setOnClickListener(v -> shareOrderInvoice());

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
                    if (booksInOrder.isEmpty()) {
                        launchWriteReview(1, "Sách trong đơn hàng #" + order.getId(), "Nhiều tác giả", "");
                    } else if (booksInOrder.size() == 1) {
                        Book b = booksInOrder.get(0);
                        launchWriteReview(b.getId(), b.getTitle(), b.getAuthor(), b.getImageUrl());
                    } else {
                        String[] titles = new String[booksInOrder.size()];
                        for (int i = 0; i < booksInOrder.size(); i++) {
                            titles[i] = (i + 1) + ". " + booksInOrder.get(i).getTitle();
                        }
                        new AlertDialog.Builder(this)
                                .setTitle("Chọn sách bạn muốn nhận xét")
                                .setItems(titles, (dialog, which) -> {
                                    Book b = booksInOrder.get(which);
                                    launchWriteReview(b.getId(), b.getTitle(), b.getAuthor(), b.getImageUrl());
                                })
                                .setNegativeButton("Đóng", null)
                                .show();
                    }
                }
            });
        });
    }

    private void launchWriteReview(int bookId, String title, String author, String image) {
        Intent reviewIntent = new Intent(OrderDetailActivity.this, WriteReviewActivity.class);
        reviewIntent.putExtra("BOOK_ID", bookId);
        reviewIntent.putExtra("BOOK_TITLE", title);
        reviewIntent.putExtra("BOOK_AUTHOR", author);
        reviewIntent.putExtra("BOOK_IMAGE", image);
        startActivity(reviewIntent);
    }

    private void shareOrderInvoice() {
        StringBuilder invoice = new StringBuilder();
        invoice.append("=========================================\n");
        invoice.append("       HÓA ĐƠN MUA HÀNG - BOOKSTORE       \n");
        invoice.append("=========================================\n");
        invoice.append("Mã đơn hàng : #ORD-").append(order.getId()).append("\n");
        invoice.append("Thời gian   : ").append(order.getOrderDate()).append("\n");
        invoice.append("Trạng thái  : ").append(order.getStatus()).append("\n");
        invoice.append("Địa chỉ nhận: ").append(order.getShippingAddress()).append("\n");
        invoice.append("Phương thức : ").append(order.getPaymentMethod()).append("\n");
        invoice.append("-----------------------------------------\n");
        invoice.append("TỔNG TIỀN   : ").append(formatter.format(order.getTotalAmount())).append("\n");
        invoice.append("=========================================\n");
        invoice.append("Cảm ơn quý khách đã mua sắm tại BookStore!\n");

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, invoice.toString());
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Hóa đơn mua sách #ORD-" + order.getId());
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, "Chia sẻ hóa đơn đơn hàng"));
    }
}

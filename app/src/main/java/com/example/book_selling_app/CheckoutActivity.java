package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.databinding.ActivityCheckoutBinding;
import com.example.book_selling_app.models.CartItem;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.CartViewModel;
import com.example.book_selling_app.viewmodels.CheckoutViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private CheckoutViewModel checkoutViewModel;
    private CartViewModel cartViewModel;
    private SessionManager sessionManager;
    private double totalAmount = 0;
    private final NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkoutViewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        sessionManager = new SessionManager(this);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnConfirmOrder.setOnClickListener(v -> placeOrder());

        calculateTotal();
    }

    private void calculateTotal() {
        int userId = sessionManager.getUserId();
        cartViewModel.getCartItems(userId).observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                totalAmount = 0;
                for (CartItem item : resource.data) {
                    totalAmount += item.getSubtotal();
                }
                binding.tvCheckoutSubtotal.setText(currencyFormatter.format(totalAmount) + " đ");
                binding.tvCheckoutTotal.setText(currencyFormatter.format(totalAmount) + " đ");
            }
        });
    }

    private void placeOrder() {
        String address = binding.edtShippingAddress.getText() != null ? binding.edtShippingAddress.getText().toString().trim() : "";
        String phone = binding.edtReceiverPhone.getText() != null ? binding.edtReceiverPhone.getText().toString().trim() : "";

        if (TextUtils.isEmpty(address)) {
            binding.tilShippingAddress.setError("Vui lòng nhập địa chỉ nhận hàng");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            binding.tilReceiverPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        int userId = sessionManager.getUserId();
        String payment = binding.rbCOD.isChecked() ? "COD (Tiền mặt)" : "Chuyển khoản / QR";

        binding.btnConfirmOrder.setEnabled(false);
        checkoutViewModel.createOrder(userId, address, phone, payment, totalAmount).observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    binding.btnConfirmOrder.setEnabled(true);
                    long orderId = resource.data != null ? resource.data : -1;
                    Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                    intent.putExtra("ORDER_ID", orderId);
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    binding.btnConfirmOrder.setEnabled(true);
                    Toast.makeText(this, resource.message != null ? resource.message : "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}

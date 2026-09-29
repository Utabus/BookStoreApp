package com.example.book_selling_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.book_selling_app.database.DatabaseHelper;
import com.example.book_selling_app.databinding.ActivityAdminDashboardBinding;
import com.example.book_selling_app.utils.SessionManager;
import com.example.book_selling_app.viewmodels.AdminDashboardViewModel;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private AdminDashboardViewModel dashboardViewModel;
    private SessionManager sessionManager;
    private final DecimalFormat formatter = new DecimalFormat("#,### đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dashboardViewModel = new ViewModelProvider(this).get(AdminDashboardViewModel.class);
        sessionManager = new SessionManager(this);

        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        binding.tvAdminGreeting.setText("Xin chào, " + sessionManager.getUserName());
        dashboardViewModel.getAdminStats().observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                DatabaseHelper.AdminStats stats = resource.data;
                binding.tvStatRevenue.setText(formatter.format(stats.totalRevenue));
                binding.tvStatOrders.setText(String.valueOf(stats.totalOrders));
                binding.tvStatPendingOrders.setText(String.valueOf(stats.pendingOrders));
                binding.tvStatBooks.setText(String.valueOf(stats.totalBooks));
                binding.tvStatUsers.setText(String.valueOf(stats.totalUsers));

                // Populate Visual Charts
                setupBarChart(stats.dailyRevenue);
                setupPieChart(stats.categoryDistribution);
            }
        });
    }

    private void setupBarChart(Map<String, Double> dailyRevenue) {
        if (dailyRevenue == null || dailyRevenue.isEmpty()) return;

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Double> entry : dailyRevenue.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColor(ContextCompat.getColor(this, R.color.primary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.text_primary));
        dataSet.setValueTextSize(9f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        binding.barChartRevenue.setData(barData);
        binding.barChartRevenue.getDescription().setEnabled(false);
        binding.barChartRevenue.getLegend().setEnabled(false);
        binding.barChartRevenue.setDrawGridBackground(false);
        binding.barChartRevenue.setFitBars(true);

        XAxis xAxis = binding.barChartRevenue.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        binding.barChartRevenue.getAxisLeft().setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        binding.barChartRevenue.getAxisLeft().setDrawGridLines(true);
        binding.barChartRevenue.getAxisRight().setEnabled(false);

        binding.barChartRevenue.animateY(900);
        binding.barChartRevenue.invalidate();
    }

    private void setupPieChart(Map<String, Integer> categoryDistribution) {
        if (categoryDistribution == null || categoryDistribution.isEmpty()) return;

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryDistribution.entrySet()) {
            if (entry.getValue() > 0) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "Chưa có sách"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.primary));
        colors.add(ContextCompat.getColor(this, R.color.secondary));
        colors.add(ContextCompat.getColor(this, R.color.status_warning));
        colors.add(ContextCompat.getColor(this, R.color.status_success));
        colors.add(ContextCompat.getColor(this, R.color.status_discount));
        colors.add(ContextCompat.getColor(this, R.color.primary_light));
        for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
        dataSet.setColors(colors);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));

        PieData pieData = new PieData(dataSet);
        binding.pieChartCategories.setData(pieData);
        binding.pieChartCategories.getDescription().setEnabled(false);
        binding.pieChartCategories.setDrawHoleEnabled(true);
        binding.pieChartCategories.setHoleColor(ContextCompat.getColor(this, R.color.surface));
        binding.pieChartCategories.setHoleRadius(40f);
        binding.pieChartCategories.setTransparentCircleRadius(45f);
        binding.pieChartCategories.setEntryLabelColor(ContextCompat.getColor(this, R.color.text_primary));
        binding.pieChartCategories.setEntryLabelTextSize(10f);
        binding.pieChartCategories.getLegend().setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        binding.pieChartCategories.getLegend().setWordWrapEnabled(true);

        binding.pieChartCategories.animateXY(800, 800);
        binding.pieChartCategories.invalidate();
    }

    private void setupEvents() {
        binding.btnSwitchToUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            startActivity(intent);
        });

        binding.cardManageBooks.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminBooksActivity.class));
        });

        binding.cardManageCategories.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminCategoriesActivity.class));
        });

        binding.cardManageOrders.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminOrdersActivity.class));
        });

        binding.cardManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class));
        });

        binding.btnAdminLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất quản trị")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi trang quản trị?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }
}

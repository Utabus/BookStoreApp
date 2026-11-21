package com.example.book_selling_app.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.book_selling_app.OrderDetailActivity;
import com.example.book_selling_app.R;
import com.example.book_selling_app.models.Order;

public class OrderNotificationHelper {

    private static final String CHANNEL_ID = "bookstore_orders_channel";
    private static final String CHANNEL_NAME = "Thông báo trạng thái đơn hàng";
    private static final String CHANNEL_DESC = "Cập nhật tiến trình xử lý và vận chuyển đơn hàng";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showOrderStatusNotification(Context context, Order order, String newStatus) {
        if (order == null) return;
        createNotificationChannel(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        String title;
        String message;

        if ("CONFIRMED".equalsIgnoreCase(newStatus) || "ĐÃ DUYỆT".equalsIgnoreCase(newStatus)) {
            title = "Đơn hàng #ORD-" + order.getId() + " đã được xác nhận! 📦";
            message = "Người bán đang chuẩn bị sách để bàn giao cho đơn vị vận chuyển.";
        } else if ("SHIPPING".equalsIgnoreCase(statusOrUpper(newStatus)) || "ĐANG GIAO".equalsIgnoreCase(newStatus)) {
            title = "Đơn hàng #ORD-" + order.getId() + " đang được giao! 🚚";
            message = "Tài xế đang trên đường giao sách đến địa chỉ của bạn.";
        } else if ("DELIVERED".equalsIgnoreCase(statusOrUpper(newStatus)) || "ĐÃ GIAO".equalsIgnoreCase(newStatus) || "ĐÃ GIAO HÀNG".equalsIgnoreCase(newStatus)) {
            title = "Giao hàng thành công! 🎉";
            message = "Đơn hàng #ORD-" + order.getId() + " đã giao thành công. Hãy vào viết nhận xét cho sách nhé!";
        } else if ("CANCELLED".equalsIgnoreCase(statusOrUpper(newStatus)) || "ĐÃ HỦY".equalsIgnoreCase(newStatus)) {
            title = "Đơn hàng #ORD-" + order.getId() + " đã bị hủy";
            message = "Đơn hàng đã được hủy bỏ và số lượng sách đã được hoàn trả lại kho.";
        } else {
            title = "Cập nhật đơn hàng #ORD-" + order.getId();
            message = "Trạng thái đơn hàng của bạn đã chuyển sang: " + newStatus;
        }

        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("ORDER_EXTRA", order);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                order.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(order.getId() + 1000, builder.build());
    }

    private static String statusOrUpper(String s) {
        return s != null ? s.toUpperCase() : "";
    }
}

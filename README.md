# 📚 BookStore App - Ứng Dụng Bán Sách Trực Tuyến (Android Java)

Ứng dụng thương mại điện tử bán sách trực tuyến hoàn chỉnh trên nền tảng **Android Native (Java 11)**, tuân thủ kiến trúc phân tầng chuẩn **MVVM (Model-View-ViewModel) + Repository Pattern**, kết nối đám mây **Firebase (Authentication, Cloud Firestore)** và hỗ trợ đệm ngoại tuyến với **SQLite (Offline-first & Dual-readiness)**.

Dự án đồ án môn học: **Lập trình thiết bị di động (Android Java)** - Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh (HCMUTE).

---

## 🌟 Tổng Quan Tính Năng Hệ Thống

### 👤 1. Phân hệ Khách hàng (User Shopping Experience)
- **Xác thực & Bảo mật**: Đăng ký, Đăng nhập, Đổi mật khẩu và Khôi phục mật khẩu thông minh; quản trị phiên đăng nhập an toàn bằng `SessionManager`.
- **Đăng nhập một chạm (One-Tap Demo)**: Nút chọn nhanh tài khoản Khách hàng và Quản trị viên để demo trực tiếp không cần nhập phím.
- **Khám phá sách & Trang chủ**: Banner khuyến mãi, danh mục thể loại cuộn ngang, danh sách sách nổi bật và sách giảm giá, huy hiệu "Hết hàng" tự động.
- **Tìm kiếm, Quét Barcode & Bộ lọc nâng cao**:
  - Tìm kiếm thời gian thực theo tên sách, tác giả.
  - Quét mã vạch ISBN / QR Code bìa sách thật bằng camera (ZXing Embedded).
  - BottomSheet lọc đa tiêu chí: Sắp xếp theo giá tăng dần, giá giảm dần, đánh giá sao cao nhất; lọc theo khoảng giá (< 100k, 100k - 200k, > 200k).
- **Trang chi tiết sách đa năng**:
  - Thông tin xuất bản, điểm đánh giá sao, bìa sách HD, giá gốc và % chiết khấu.
  - Tồn kho thời gian thực: Cảnh báo số lượng trong kho, tự động khóa nút mua khi sách hết hàng (`stock <= 0`).
  - Danh sách cuộn ngang tác phẩm cùng tác giả & nút bấm chuyển sang toàn bộ sách của tác giả.
  - Thẻ tổng quan đánh giá độc giả (Rating Overview) & danh sách bình luận chi tiết.
  - Nút tim lưu vào **Sách yêu thích (Wishlist)**.
- **Giỏ hàng & Đặt hàng (Checkout)**:
  - Tăng/giảm số lượng có kiểm tra giới hạn tồn kho, xóa sản phẩm, dọn sạch giỏ hàng.
  - Áp dụng mã giảm giá Voucher (`GIAM10`, `GIAM30K`, `BOOKLOVER`, `WELCOME50`) tự động tính chiết khấu.
  - Tích hợp **Sổ địa chỉ nhận hàng**: Chọn nhanh địa chỉ đã lưu hoặc nạp địa chỉ mặc định.
  - Đa dạng phương thức thanh toán: **COD (Tiền mặt khi nhận)** và **Chuyển khoản VietQR** (Popup hiển thị mã QR ngân hàng động, STK, số tiền và cú pháp chuyển khoản).
- **Quản lý đơn hàng & Đánh giá**:
  - Theo dõi tiến trình đơn hàng qua các trạng thái (`CHỜ XỬ LÝ` $\rightarrow$ `ĐÃ DUYỆT` $\rightarrow$ `ĐANG GIAO` $\rightarrow$ `ĐÃ GIAO` hoặc `ĐÃ HỦY`).
  - Khách hàng có quyền hủy đơn khi đơn còn ở trạng thái chờ duyệt (hệ thống tự động hoàn lại tồn kho).
  - Viết đánh giá & bình luận 5 sao cho từng cuốn sách trong đơn hàng đã nhận.
  - **In & Chia sẻ hóa đơn**: Xuất tóm tắt hóa đơn mua hàng chi tiết qua Zalo, Messenger, Email,...
- **Hỗ trợ & Trực tuyến**:
  - Chat trực tiếp theo thời gian thực (Real-time Live Chat) với Admin/CSKH kèm thông báo thanh trạng thái (Status Bar Notification).
  - Quản lý Sổ địa chỉ nhận hàng (CRUD địa chỉ, đặt địa chỉ mặc định).

---

### 👑 2. Phân hệ Quản trị viên (Admin Management Suite)
- **Bảng điều khiển & Biểu đồ trực quan (Dashboard & Analytics)**:
  - Thống kê doanh thu bán hàng, tổng số đơn, đơn chờ xử lý, số đầu sách và thành viên.
  - **Biểu đồ cột (BarChart - MPAndroidChart)**: Doanh thu theo mốc thời gian.
  - **Biểu đồ tròn (PieChart - MPAndroidChart)**: Cơ cấu phân bổ thể loại sách.
- **Quản lý Kho sách (Book Inventory Management)**:
  - Xem danh sách sách kèm số lượng tồn kho (`Kho: X cuốn`).
  - Tìm kiếm nhanh sách theo tên hoặc tác giả.
  - Thêm mới, chỉnh sửa chi tiết sách (giá bán, giá gốc, tỷ lệ giảm giá, mô tả, link ảnh bìa, số lượng nhập kho).
  - Xóa sách khỏi hệ thống có hộp thoại xác nhận an toàn.
- **Kiểm soát Tồn kho tự động (Automatic Inventory Engine)**:
  - Tự động trừ tồn kho khi đơn hàng được tạo thành công.
  - Tự động cộng hoàn lại kho sách khi đơn hàng bị hủy (khách hủy hoặc admin hủy).
- **Quản lý Thể loại (Categories Management)**:
  - Thêm, sửa tên danh mục.
  - Xóa danh mục an toàn (ngăn chặn xóa khi vẫn còn sách thuộc thể loại đó).
- **Quy trình Duyệt đơn hàng (Order State Machine)**:
  - Lọc đơn hàng theo từng tab trạng thái hoặc tìm kiếm nhanh theo mã đơn (`#ORD-xxx`), tên khách hàng, số điện thoại.
  - Duyệt tuần tự: [Duyệt đơn] $\rightarrow$ [Bắt đầu giao] $\rightarrow$ [Đã giao thành công] hoặc [Hủy đơn hàng].
  - Đồng bộ trạng thái tức thì lên Cloud Firestore.
- **Quản lý Người dùng & Phân quyền (User Security & Access Control)**:
  - Danh sách tài khoản người dùng, vai trò (`USER` / `ADMIN`).
  - Thanh tìm kiếm người dùng theo Họ tên, Email, SĐT.
  - Công tắc bật/tắt khóa tài khoản tức thì (chặn đăng nhập khi tài khoản bị vô hiệu hóa).
- **Trung tâm Hỗ trợ Chat (Admin Chat Desk)**:
  - Quản lý danh sách các hội thoại từ khách hàng, phản hồi realtime cho từng độc giả.

---

## 🏗 Kiến Trúc Hệ Thống (Architecture)

```mermaid
graph TD
    subgraph UI["1. View Layer (Giao diện UI)"]
        Activity["20 Activities & 5 Fragments (ViewBinding)"]
    end

    subgraph ViewModel["2. Presentation Layer"]
        VM["ViewModels (AndroidX Lifecycle & LiveData)"]
        Res["Resource<T> (LOADING, SUCCESS, ERROR)"]
    end

    subgraph Repository["3. Repository Layer"]
        AuthRepo["AuthRepository"]
        BookRepo["BookRepository"]
        CatRepo["CategoryRepository"]
        OrderRepo["OrderRepository"]
        CartRepo["CartRepository"]
        AdminRepo["AdminRepository"]
        ReviewRepo["ReviewRepository"]
        AddrRepo["AddressRepository"]
        ChatRepo["ChatRepository"]
    end

    subgraph Remote["4. Cloud Data Source (Firebase)"]
        FB_Auth["Firebase Authentication"]
        FB_Store["Cloud Firestore (Realtime Sync)"]
    end

    subgraph Local["5. Local Data Source (SQLite)"]
        DB["SQLite DatabaseHelper (Offline-first Cache & Sample Data)"]
    end

    UI <--> VM
    VM <--> Repository
    Repository <--> Remote
    Repository <--> Local
```

---

## 🔑 Tài Khoản Trải Nghiệm & Demo Nhanh

Tại màn hình đăng nhập, bạn có thể bấm **1 chạm vào nút Demo** hoặc nhập thông tin sau:

| Vai trò | Email đăng nhập | Mật khẩu | Quyền hạn |
|---|---|---|---|
| **Quản trị viên (Admin)** | `admin@bookstore.vn` | `admin123` | Quản trị toàn quyền hệ thống |
| **Khách hàng mẫu 1** | `user@gmail.com` | `123456` | Khách mua hàng (đã có sẵn đơn & địa chỉ) |
| **Khách hàng mẫu 2** | `mai.tran@gmail.com` | `123456` | Khách hàng thân thiết |
| **Khách hàng mẫu 3** | `nam.le@gmail.com` | `123456` | Khách hàng |

---

## 🎟 Danh Sách Mã Giảm Giá (Voucher) Thử Nghiệm

Tại bước Checkout, nhập một trong các mã sau:
- `GIAM10`: Giảm 10% tổng đơn hàng (đơn từ 100.000 đ)
- `GIAM30K`: Giảm trực tiếp 30.000 đ (đơn từ 200.000 đ)
- `BOOKLOVER`: Giảm 15% tổng đơn (đơn từ 150.000 đ)
- `WELCOME50`: Giảm ngay 50.000 đ (đơn từ 300.000 đ)

---

## 🛠 Công Nghệ & Thư Viện Sử Dụng

- **Ngôn ngữ**: Java 11 (Android SDK 34, Min SDK 26)
- **Giao diện**: XML Layouts, Material Design Components, ViewBinding
- **Xử lý hình ảnh**: Bumptech Glide 4.16.0
- **Biểu đồ thống kê**: MPAndroidChart 3.1.0
- **Quét mã vạch**: ZXing Android Embedded 4.3.0
- **Kiến trúc**: AndroidX Lifecycle (ViewModel 2.6.2, LiveData 2.6.2), Repository Pattern, AppExecutors
- **Cơ sở dữ liệu cục bộ**: SQLite (`DatabaseHelper` v4)
- **Đám mây**: Firebase BoM 33.7.0 (`firebase-auth`, `firebase-firestore`, `firebase-storage`)

---

## 🚀 Hướng Dẫn Biên Dịch & Chạy Ứng Dụng

1. **Khởi động Android Studio**:
   - Mở thư mục dự án `Book_selling_app`.
2. **Kiểm tra biên dịch bằng lệnh Terminal**:
   ```powershell
   cd Book_selling_app
   .\gradlew assembleDebug
   ```
3. **Chạy ứng dụng**:
   - Chọn máy ảo Android (Pixel 7/8, API 26 - 34) hoặc cắm điện thoại Android thật.
   - Bấm nút **Run** (hoặc tổ hợp phím `Shift + F10`).

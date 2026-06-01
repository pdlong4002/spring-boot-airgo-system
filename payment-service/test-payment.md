# HƯỚNG DẪN KIỂM THỬ AIRGO SYSTEM (PAYMENT & SWAGGER UI)

Hồ sơ này hướng dẫn bạn cách kiểm thử toàn diện luồng thanh toán và cách truy cập tài liệu Swagger UI của **payment-service**.

---

## 1. KIỂM THỬ SWAGGER UI (TÀI LIỆU DỊCH VỤ APIs)

Chúng tôi đã tích hợp thành công thư viện **Springdoc OpenAPI** vào `payment-service`. Bạn có thể truy cập trực tiếp tài liệu và thực hiện kiểm thử gọi thử nghiệm các endpoint API của `payment-service` thông qua:

* **Đường dẫn truy cập Swagger UI**:
  👉 **[http://localhost:8085/docs](http://localhost:8085/docs)** (nó sẽ tự động chuyển hướng bạn tới giao diện tài liệu `/swagger-ui/index.html`).
* **Đường dẫn OpenAPI JSON**:
  👉 **[http://localhost:8085/v3/api-docs](http://localhost:8085/v3/api-docs)**

---

## 2. KIỂM THỬ LUỒNG THANH TOÁN (END-TO-END E2E FLOW)

### Bước 1: Khởi tạo mã đặt vé (PNR) thử nghiệm trong DB
Bạn hãy mở công cụ quản lý Database MySQL (như DBeaver, Navicat) và chạy câu lệnh SQL sau để tạo một booking mẫu ở trạng thái chờ thanh toán (`PENDING`):

```sql
-- 1. Thêm một Booking mới với mã đặt vé (PNR) là 'AG9999' ở trạng thái PENDING
INSERT INTO bookings (id, booking_code, user_id, flight_id, total_amount, status) 
VALUES (9999, 'AG9999', 1, 1, 150.00, 'PENDING');

-- 2. Thêm Passenger (hành khách) thuộc Booking trên
INSERT INTO passengers (booking_id, seat_id, flight_class_id, full_name, identity_number, ticket_price) 
VALUES (9999, 10, 5, 'NGUYEN VAN A', '0123456789', 150.00);
```

### Bước 2: Thực hiện thanh toán

#### Cách 1: Sử dụng Giao diện Web (API Gateway)
1. **Mở trình duyệt** và truy cập: **[http://localhost:8080/api/v1/vnpay/](http://localhost:8080/api/v1/vnpay/)** (truy cập qua API Gateway).
2. **Điền thông tin thanh toán**:
   * **Họ và tên khách hàng**: `NGUYEN VAN A`
   * **Email nhận vé**: Nhập **địa chỉ Gmail thật của bạn** (Ví dụ: `your-email@gmail.com`) để kiểm tra email gửi về.
   * **Mã đặt vé (Mã PNR)**: Nhập chính xác mã: **`AG9999`**
   * **Số tiền**: Nhập `150` (USD).
3. **Chọn Phương thức Thanh toán**:
   * **VNPAY** (Dẫn tới cổng VNPAY Sandbox thật).
4. Hệ thống sẽ tự động đưa bạn về trang thông báo **Thành Công (Success Page)** màu xanh ngọc lục bảo rất bắt mắt với đầy đủ thông tin vé.

#### Cách 2: Gọi trực tiếp qua REST API (Postman / Thunder Client)
Bạn có thể gọi trực tiếp API khởi tạo thanh toán qua API Gateway bằng phương thức **`POST`** với cấu trúc sau:

* **HTTP Method**: `POST`
* **URL**: `http://localhost:8080/api/v1/vnpay/create-payment`
* **Headers**: `Content-Type: application/json`
* **Body (JSON)**:
  ```json
  {
    "fullName": "NGUYEN VAN A",
    "email": "longphamk2@gmail.com",
    "txnRef": "AG9999",
    "amount": 150.00,
    "paymentMethod": "VNPAY" // Hoặc MOMO, ZALOPAY
  }
  ```

* **Phản hồi trả về (JSON)**:
  ```json
  {
    "status": "SUCCESS",
    "message": "Payment request generated successfully",
    "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=..." 
  }
  ```
  👉 **Kiểm thử**: Bạn chỉ cần copy giá trị `paymentUrl` trong kết quả trả về, dán vào thanh địa chỉ trình duyệt để tiến hành thanh toán trực tiếp!

### Bước 3: Đối soát và Kiểm tra kết quả
1. **Kiểm tra trạng thái Booking trong Database**:
   Chạy câu lệnh SQL:
   ```sql
   SELECT * FROM bookings WHERE booking_code = 'AG9999';
   ```
   ➔ Cột **`status`** của booking `AG9999` đã tự động chuyển sang **`CONFIRMED`**!

2. **Kiểm tra Hòm thư Email**:
   ➔ Bạn sẽ nhận được một email từ **AirGo System** chứa **Vé Máy Bay Điện Tử** HTML cực kỳ bóng bẩy gửi thẳng về hòm thư Gmail của bạn!

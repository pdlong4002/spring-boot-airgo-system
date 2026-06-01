# ✈️ Flight Booking DB – Field Description

## 🧑‍💻 1. users

| Field      | Kiểu      | Ý nghĩa                    |
| ---------- | --------- | -------------------------- |
| id         | BIGINT    | ID người dùng              |
| username   | VARCHAR   | Tên hiển thị               |
| email      | VARCHAR   | Email đăng nhập (duy nhất) |
| password   | VARCHAR   | Mật khẩu (đã hash)         |
| role       | ENUM      | Vai trò: USER / ADMIN      |
| created_at | TIMESTAMP | Thời điểm tạo              |
| updated_at | TIMESTAMP | Thời điểm cập nhật cuối    |

---

## 🛫 2. airports

| Field     | Kiểu    | Ý nghĩa                   |
| --------- | ------- | ------------------------- |
| id        | INT     | ID sân bay                |
| iata_code | VARCHAR | Mã sân bay (VD: SGN, HAN) |
| name      | VARCHAR | Tên sân bay               |
| city      | VARCHAR | Thành phố                 |
| country   | VARCHAR | Quốc gia                  |

---

## ✈️ 3. flights

| Field                | Kiểu     | Ý nghĩa                                     |
| -------------------- | -------- | ------------------------------------------- |
| id                   | BIGINT   | ID chuyến bay                               |
| flight_number        | VARCHAR  | Mã chuyến bay (VD: VN123)                   |
| departure_airport_id | INT      | Sân bay đi                                  |
| arrival_airport_id   | INT      | Sân bay đến                                 |
| departure_time       | DATETIME | Thời gian cất cánh                          |
| arrival_time         | DATETIME | Thời gian hạ cánh                           |
| status               | ENUM     | Trạng thái: SCHEDULED / DELAYED / CANCELLED |

---

## 💺 4. flight_classes

| Field           | Kiểu    | Ý nghĩa                          |
| --------------- | ------- | -------------------------------- |
| id              | BIGINT  | ID hạng vé                       |
| flight_id       | BIGINT  | Thuộc chuyến bay nào             |
| class_type      | ENUM    | ECONOMY / BUSINESS / FIRST_CLASS |
| price           | DECIMAL | Giá vé                           |
| available_seats | INT     | Số ghế còn lại (optional)        |
| version         | INT     | Version dùng cho Optimistic Lock |

---

## 🪑 5. seats

| Field       | Kiểu    | Ý nghĩa            |
| ----------- | ------- | ------------------ |
| id          | BIGINT  | ID ghế             |
| flight_id   | BIGINT  | Thuộc chuyến bay   |
| seat_number | VARCHAR | Số ghế (A1, B2...) |
| class_type  | ENUM    | Hạng ghế           |
| is_booked   | BOOLEAN | Đã được đặt chưa   |
| version     | INT     | Version chống double booking |

---

## 📖 6. bookings

| Field        | Kiểu      | Ý nghĩa                         |
| ------------ | --------- | ------------------------------- |
| id           | BIGINT    | ID booking                      |
| booking_code | VARCHAR   | Mã PNR (Ví dụ: VJ8X2B)          |
| user_id      | BIGINT    | Người đặt (Soft Reference)      |
| flight_id    | BIGINT    | Chuyến bay (Soft Reference)     |
| total_amount | DECIMAL   | Tổng tiền                       |
| status       | ENUM      | PENDING / CONFIRMED / CANCELLED |
| created_at   | TIMESTAMP | Thời điểm đặt                   |
| updated_at   | TIMESTAMP | Thời điểm cập nhật cuối         |

---

## 👨‍👩‍👧 7. passengers

| Field           | Kiểu    | Ý nghĩa              |
| --------------- | ------- | -------------------- |
| id              | BIGINT  | ID hành khách        |
| booking_id      | BIGINT  | Thuộc booking        |
| seat_id         | BIGINT  | Ghế đã chọn (Soft Ref)|
| flight_class_id | BIGINT  | Hạng vé (Soft Ref)   |
| full_name       | VARCHAR | Tên hành khách       |
| identity_number | VARCHAR | CMND/CCCD            |
| ticket_price    | DECIMAL | Giá vé của người này |

---

## 🔥 Ghi nhớ nhanh (để đi phỏng vấn)

* `booking` = đơn đặt vé
* `passenger` = người đi (1 booking nhiều người)
* `seat` = ghế cụ thể
* `flight_class` = loại vé (giá + hạng)

👉 Quan hệ quan trọng nhất:

* **booking → passengers (1-N)**
* **passenger → seat (1-1)**
* **flight → seats (1-N)**

---

## ⚠️ Note quan trọng (Kiến trúc Microservices)

* **Không dùng Khóa ngoại (Hard Foreign Key) chéo:** Các trường `user_id`, `flight_id`, `seat_id` ở các service khác nhau chỉ là "Soft Reference" (lưu ID), bắt buộc phải đánh INDEX trong CSDL để truy xuất nhanh.
* **Chống Double Booking:** Cột `version` trong `seats` và `flight_classes` dùng cho cơ chế **Optimistic Locking** (JPA `@Version`). Nếu 2 người cùng gọi API đặt 1 ghế, người thứ 2 sẽ bị throw lỗi thay vì ghi đè.
* `available_seats` mang tính chất tham khảo hiển thị, kết quả cuối cùng phải dựa trên trạng thái `is_booked` của `seats`.

---
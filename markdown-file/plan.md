# ✈️ Flight Booking Microservices - Implementation Plan

## 🎯 Goal

Xây dựng hệ thống đặt vé máy bay theo kiến trúc **Microservices** chuẩn, đảm bảo:

* Dễ mở rộng (Scalable)
* Tránh coupling (Loose coupling)
* Xử lý concurrency (Double booking)
* Code production-style, Clean Architecture

---

# 🧱 1. Architecture Overview

> **🚨 Microservices Data Rule:** Mỗi service sở hữu database độc lập. **KHÔNG DÙNG Foreign Key (khóa ngoại) chéo giữa các services**. Ta dùng "Soft References" (lưu ID và đánh Index) để liên kết.

## Services

### 1. API Gateway & Infrastructure
* **API Gateway:** Routing, Authentication filter (Validate JWT), Rate Limiting.
* **Eureka Server:** Service Discovery.
* **Config Server:** Centralized Configuration.

### 2. User/Auth Service (Đã hoàn thành)
* **Responsibility:** Authentication (JWT), User management.
* **Database (`user_db`):** `users`, `roles`

### 3. Flight Service
* **Responsibility:** Quản lý và tìm kiếm chuyến bay, sân bay, pricing.
* **Database (`flight_db`):** `airports`, `flights`, `flight_classes`

### 4. Seat (Inventory) Service
* **Responsibility:** Quản lý kho ghế, Seat availability, Seat locking (Giữ chỗ tạm thời bằng Redis TTL), Ngăn chặn double booking (Optimistic Locking).
* **Database (`seat_db`):** `seats` (Thêm cột `version` cho Optimistic Locking).
* **Cache/Lock:** Redis (Lưu trạng thái giữ chỗ tạm thời trong 15p).

### 5. Booking Service
* **Responsibility:** Tạo booking, quản lý hành khách, vòng đời của booking (PENDING, CONFIRMED, CANCELLED).
* **Database (`booking_db`):** `bookings` (có `pnr_code`), `passengers`.

### 6. Payment Service
* **Responsibility:** Xử lý thanh toán, callback từ payment gateway.
* **Database (`payment_db`):** `payments`.

### 7. Notification Service
* **Responsibility:** Gửi E-ticket, email xác nhận, SMS.
* **Database:** Không cần thiết (hoặc `notification_db` để log lịch sử gửi).

---

# 🔄 2. Booking Flow (Core Logic)

Sử dụng kết hợp **Synchronous (OpenFeign)** cho luồng cần data tức thời và **Asynchronous (Kafka)** cho luồng không cần chờ kết quả ngay (Saga Pattern - Choreography).

### 1. Search Flight
* Client → API Gateway → **Flight Service** (Có thể dùng Redis để cache kết quả search).

### 2. Select Seat
* Client → API Gateway → **Seat Service** → Check availability.

### 3. Create Booking (Synchronous)
**Booking Service:**
1. Nhận request tạo Booking.
2. Call **Flight Service** (OpenFeign) để xác thực giá và thông tin chuyến bay.
3. Call **Seat Service** (OpenFeign) để **Lock seat** (Giữ chỗ trong 15 phút).
4. Lưu DB: Booking = `PENDING`.
5. Publish event `BookingCreatedEvent` lên **Kafka**.

### 4. Process Payment & Confirm (Asynchronous)
1. **Payment Service** nhận thanh toán thành công → Publish event `PaymentCompletedEvent` lên Kafka.
2. **Booking Service** consume `PaymentCompletedEvent` → Update Booking = `CONFIRMED`.
3. **Seat Service** consume `PaymentCompletedEvent` → Update Seat = `BOOKED` (Mua đứt).
4. **Notification Service** consume cả 2 event trên để gửi email Confirm / E-ticket.

### 5. Cancel Booking (Timeout/Failed Payment)
* Nếu quá 15 phút không có `PaymentCompletedEvent`, Seat tự động release lock. Booking chuyển `CANCELLED`.

---

# ⚠️ 3. Critical Problems & Solutions

## ❌ Double Booking (Cùng lúc nhiều người mua 1 ghế)
### Solution:
* **Optimistic Locking (`@Version`)**: Ở table `seats`. Đơn giản, hiệu quả cao, chuẩn JPA.
* **Redis Distributed Lock**: Dành cho scale cực lớn (Dùng Redisson khoá theo `seat_id`).

## 🛡️ API Protection: Rate Limiting (Chống tấn công Brute-force/Spam)
Để bảo vệ hệ thống khỏi việc bị quá tải bởi các request spam (đặc biệt là API Search và Booking), ta áp dụng **Rate Limiting** tại API Gateway.

### Strategy:
1. **Algorithm:** Sử dụng **Token Bucket Algorithm** (Mặc định của Spring Cloud Gateway).
2. **Implementation:**
   * **Spring Cloud Gateway + Redis:** Lưu trữ số lượng token còn lại của mỗi client vào Redis để đảm bảo hiệu năng cao và hỗ trợ scale ngang Gateway.
   * **Key Resolver:** Xác định client dựa trên:
     * **IP Address:** Chặn spam từ một địa chỉ IP cụ thể (Phù hợp cho Public API).
     * **User ID:** Hạn chế số lượng request của một user đã login (Phù hợp cho API Booking/Payment).
     * **API Key/Header:** Dành cho các đối tác tích hợp.

### Configuration (Example):
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: booking-service
          uri: lb://booking-service
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10   # Số lượng request tối đa mỗi giây
                redis-rate-limiter.burstCapacity: 20   # Dung lượng tối đa của "xô" token
                key-resolver: "#{@userKeyResolver}"    # Bean định nghĩa cách nhận diện user
```

## ❌ Distributed Transaction & Cascade Failure
### Solution:
* **Saga Pattern (Choreography):** Dùng Kafka cho Event-Driven như flow ở trên, tránh 2PC (Two-Phase Commit).
* **Resilience4j:** Áp dụng Circuit Breaker, Retry, Fallback cho các call OpenFeign (VD: Seat Service sập thì Booking Service trả về lỗi tử tế, không bị treo theo).

---

# 🧰 4. Tech Stack

* **Backend:** Spring Boot, Spring Cloud (Eureka, Config, Gateway, OpenFeign)
* **Database:** MySQL (Database-per-service), Redis (Cache, Lock)
* **Message Broker:** Kafka (Event-Driven, Async Communication)
* **Resilience:** Resilience4j
* **Security:** Spring Security, JWT
* **Deployment:** Docker, docker-compose

---

# 🗂️ 5. Clean Architecture & Project Structure

Áp dụng cho từng service:

```
src/main/java/com/airgo/[service_name]/
├── config/             # Cấu hình Feign, Kafka, Redis, Security
├── controller/         # REST APIs
├── dto/                # Request/Response DTOs (Data Transfer Object)
├── entity/             # JPA Entities
├── exception/          # GlobalExceptionHandler (@RestControllerAdvice), Custom Exceptions
├── mapper/             # MapStruct / ModelMapper interfaces
├── repository/         # Spring Data JPA
├── service/            # Business interfaces
│   └── impl/           # Business Logic Implementations
└── client/             # OpenFeign Clients (chỉ có ở service cần gọi ra ngoài)
```

---

# 🚀 6. Development Roadmap

## 🟢 Phase 1: Infrastructure & Auth (Hiện tại)
* [x] Auth / User Service
* [ ] Setup Eureka Server & Config Server
* [ ] Setup API Gateway (tích hợp check JWT filter và Rate Limiting)

## 🟡 Phase 2: Core Data Services
* [ ] **Flight Service**: CRUD, Search, Cache với Redis.
* [ ] **Seat Service**: Entity với `@Version`, API lock ghế và release ghế.
* *Chỉ setup OpenFeign và test gọi chéo giữa các service.*

## 🔴 Phase 3: Synchronous Booking Flow
* [ ] **Booking Service**: Tạo API Make Booking.
* [ ] Implement OpenFeign từ Booking gọi sang Flight và Seat.
* [ ] Implement Resilience4j (Circuit Breaker / Retry) cho các external calls.
* [ ] Hoàn thiện luồng Booking ở trạng thái `PENDING`.

## 🔵 Phase 4: Asynchronous & Event-Driven (Pro Level)
* [ ] Setup Kafka broker (Docker).
* [ ] **Payment Service**: Mô phỏng thanh toán, Publish sự kiện lên Kafka.
* [ ] **Notification Service**: Consume sự kiện gửi Email.
* [ ] Implement Saga Pattern để complete / rollback flow dựa trên Kafka events.

---

# 🛡️ 7. Best Practices & Anti-Patterns Cần Tránh

### ✅ Best Practices
1. **Global Exception Handler**: Bắt toàn bộ exception bằng `@RestControllerAdvice` và trả về một ErrorResponse chung (code, message, timestamp).
2. **Luôn dùng DTO và Mapper**: Tuyệt đối không expose Entity ra ngoài Controller. Dùng MapStruct để map Entity <-> DTO.
3. **Idempotency (Tính lũy đẳng)**: Khi consume Kafka event, phải check `eventId` trong database để đảm bảo không xử lý 1 event 2 lần.

### ❌ Anti-Patterns
1. **Foreign Keys qua các Database**: Đừng cố nối bảng (JOIN) giữa `booking_db` và `user_db`.
2. **Chain HTTP Calls quá dài**: Gateway -> Booking -> Seat -> Flight. Dễ bị timeout. Dùng Async Kafka ở những bước không cần user chờ.
3. **God Class/Service**: Đừng nhét logic thanh toán vào Booking Service.

---

# 📊 8. What to Show in CV / Portfolio / KLTN

* **System Architecture Diagram** (rõ ràng các components, Kafka, Redis).
* **Database-per-service pattern** (chứng minh loose coupling).
* **Distributed Concurrency Handling** (cách xử lý Double Booking bằng Optimistic Lock / Redis Lock).
* **Event-Driven Architecture** (Saga Pattern giải quyết bài toán Distributed Transaction).
* **Circuit Breaker** (chống lỗi dây chuyền bằng Resilience4j).

---
**END OF PLAN**
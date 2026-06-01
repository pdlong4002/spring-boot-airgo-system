-- ==================================================
-- MICROSERVICES DATABASE SCHEMA
-- Note: Trong kiến trúc Microservices thực tế, các bảng này
-- sẽ nằm ở các database vật lý/logical khác nhau.
-- Do đó, các Foreign Key (khóa ngoại) chéo giữa các service 
-- ĐÃ BỊ LOẠI BỎ để tránh tight coupling.
-- ==================================================

-- Drop the database if it already exists
DROP DATABASE IF EXISTS microservice;
-- Create database
CREATE DATABASE IF NOT EXISTS microservice;
USE microservice;

-- DROP TABLES
DROP TABLE IF EXISTS passengers;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS flight_classes;
DROP TABLE IF EXISTS flights;
DROP TABLE IF EXISTS airports;
DROP TABLE IF EXISTS users;

-- ==================================================
-- 1. USER SERVICE (DB: users) // auth-service(done)
-- ==================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    firstname VARCHAR(100),
    lastname VARCHAR(100),
    email VARCHAR(250) UNIQUE NOT NULL,
    password VARCHAR(150),
    enabled BOOLEAN DEFAULT FALSE,
    otp_code VARCHAR(10),
    otp_expiry DATETIME,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    role ENUM('ADMIN', 'USER', 'MANAGER') DEFAULT 'USER',
    access_token VARCHAR(500),
    refresh_token VARCHAR(500),
    provider ENUM('local', 'facebook', 'google', 'github') DEFAULT 'local',
    provider_id VARCHAR(150),
    image_url VARCHAR(500)
);

-- ==================================================
-- 2. FLIGHT SERVICE (DB: flights_db)
-- ==================================================
CREATE TABLE airports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    iata_code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL
);

CREATE TABLE flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,

    departure_airport_id INT,
    arrival_airport_id INT,

    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,

    status ENUM('SCHEDULED', 'DELAYED', 'CANCELLED') DEFAULT 'SCHEDULED',

    -- Internal FKs trong cùng Flight Service là HỢP LỆ
    FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
);

CREATE TABLE flight_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_id BIGINT,
    class_type ENUM('ECONOMY', 'BUSINESS', 'FIRST_CLASS') NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    available_seats INT,
    version INT DEFAULT 0, -- Optimistic locking để xử lý concurrency

    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE
);

-- ==================================================
-- 3. SEAT SERVICE (DB: seats_db)
-- ==================================================
CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_id BIGINT NOT NULL, -- Soft reference tới bảng flights

    seat_number VARCHAR(10) NOT NULL,
    class_type ENUM('ECONOMY', 'BUSINESS', 'FIRST_CLASS'),

    is_booked BOOLEAN DEFAULT FALSE,
    version INT DEFAULT 0, -- Optimistic locking chống double booking

    UNIQUE KEY (flight_id, seat_number)
    -- ĐÃ BỎ: FOREIGN KEY (flight_id) REFERENCES flights(id)
);

-- ==================================================
-- 4. BOOKING SERVICE (DB: bookings_db)
-- ==================================================
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(10) UNIQUE NOT NULL, -- Mã PNR (VD: VJ8X2B) để khách tra cứu
    user_id BIGINT NOT NULL, -- Soft reference tới bảng users
    flight_id BIGINT NOT NULL, -- Soft reference tới bảng flights

    total_amount DECIMAL(10,2),

    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

    -- ĐÃ BỎ: FOREIGN KEY (user_id) REFERENCES users(id)
    -- ĐÃ BỎ: FOREIGN KEY (flight_id) REFERENCES flights(id)
);

CREATE TABLE passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    seat_id BIGINT, -- Soft reference tới bảng seats
    flight_class_id BIGINT, -- Soft reference tới bảng flight_classes

    full_name VARCHAR(100) NOT NULL,
    identity_number VARCHAR(50),

    ticket_price DECIMAL(10,2),

    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
    -- ĐÃ BỎ: FOREIGN KEY (seat_id) REFERENCES seats(id)
    -- ĐÃ BỎ: FOREIGN KEY (flight_class_id) REFERENCES flight_classes(id)
);

-- ==================================================
-- 5. PAYMENT SERVICE (DB: payments_db)
-- ==================================================
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(10) NOT NULL, -- Soft reference tới bảng bookings (Mã PNR)
    user_id BIGINT NOT NULL,           -- Soft reference tới bảng users
    
    amount DECIMAL(10,2) NOT NULL,     -- Số tiền thanh toán
    payment_method VARCHAR(50),        -- 'VNPAY', 'MOMO', 'STRIPE'
    transaction_id VARCHAR(100),       -- Mã giao dịch do VNPay trả về để đối soát
    
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_booking_code ON payments(booking_code);


-- ==================================================
-- INDEXES FOR SOFT REFERENCES (Cực kỳ quan trọng về hiệu năng)
-- ==================================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_flights_route ON flights(departure_airport_id, arrival_airport_id);

-- Đánh Index cho các Soft FK (do đã bỏ hard FK, bắt buộc phải có Index để join ở tầng code nhanh hơn)
CREATE INDEX idx_seats_flight_id ON seats(flight_id);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_flight_id ON bookings(flight_id);
CREATE INDEX idx_passengers_seat_id ON passengers(seat_id);
CREATE INDEX idx_passengers_flight_class_id ON passengers(flight_class_id);

-- ==================================================
-- SAMPLE DATA FOR TESTING
-- ==================================================
INSERT INTO airports (iata_code, name, city, country) VALUES 
('HAN', 'Noi Bai International Airport', 'Ha Noi', 'Vietnam'),
('SGN', 'Tan Son Nhat International Airport', 'Ho Chi Minh City', 'Vietnam');

INSERT INTO flights (flight_number, departure_airport_id, arrival_airport_id, departure_time, arrival_time, status) VALUES 
('VN123', 1, 2, '2026-06-01 08:00:00', '2026-06-01 10:00:00', 'SCHEDULED');

INSERT INTO flight_classes (flight_id, class_type, price, available_seats) VALUES 
(1, 'ECONOMY', 100.00, 50),
(1, 'BUSINESS', 250.00, 10);

INSERT INTO seats (flight_id, seat_number, class_type, is_booked) VALUES 
(1, '1A', 'BUSINESS', FALSE),
(1, '1B', 'BUSINESS', FALSE),
(1, '10A', 'ECONOMY', FALSE),
(1, '10B', 'ECONOMY', FALSE);


-- test payment-service
-- 1. Thêm một Booking mới với mã đặt vé (PNR) là 'AG9999' ở trạng thái PENDING
INSERT INTO bookings (id, booking_code, user_id, flight_id, total_amount, status) 
VALUES (9999, 'AG9999', 1, 1, 150.00, 'PENDING');

-- 2. Thêm Passenger (hành khách) thuộc Booking trên
INSERT INTO passengers (booking_id, seat_id, flight_class_id, full_name, identity_number, ticket_price) 
VALUES (9999, 10, 5, 'NGUYEN VAN A', '0123456789', 150.00);

-- Các booking mẫu khác để test thanh toán mà không bị trùng
INSERT INTO bookings (id, booking_code, user_id, flight_id, total_amount, status) 
VALUES (9998, 'AG9998', 1, 1, 150.00, 'PENDING');
INSERT INTO passengers (booking_id, seat_id, flight_class_id, full_name, identity_number, ticket_price) 
VALUES (9998, 11, 5, 'NGUYEN VAN B', '0123456788', 150.00);

INSERT INTO bookings (id, booking_code, user_id, flight_id, total_amount, status) 
VALUES (9997, 'AG9997', 1, 1, 150.00, 'PENDING');
INSERT INTO passengers (booking_id, seat_id, flight_class_id, full_name, identity_number, ticket_price) 
VALUES (9997, 12, 5, 'NGUYEN VAN C', '0123456787', 150.00);

INSERT INTO bookings (id, booking_code, user_id, flight_id, total_amount, status) 
VALUES (9996, 'AG9996', 1, 1, 150.00, 'PENDING');
INSERT INTO passengers (booking_id, seat_id, flight_class_id, full_name, identity_number, ticket_price) 
VALUES (9996, 13, 5, 'NGUYEN VAN D', '0123456786', 150.00);


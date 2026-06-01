-- ==================================================
-- MICROSERVICES DATABASE SCHEMA
-- Note: Trong kiến trúc Microservices thực tế, các bảng này
-- sẽ nằm ở các database vật lý/logical khác nhau.
-- Do đó, các Foreign Key (khóa ngoại) chéo giữa các service 
-- ĐÃ BỊ LOẠI BỎ để tránh tight coupling.
-- ==================================================

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
    role ENUM('USER', 'ADMIN', 'MANAGER') DEFAULT 'USER',
    access_token VARCHAR(500),
    refresh_token VARCHAR(500),
    provider ENUM('local', 'facebook', 'google', 'github') DEFAULT 'local',
    provider_id VARCHAR(150),
    image_url VARCHAR(500),
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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
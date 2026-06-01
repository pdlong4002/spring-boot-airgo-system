# AirGo System Test Guide

This guide helps you test the end-to-end flight booking flow.

## 1. Prerequisites
- Start all services using Docker: `docker-compose up -d`
- Ensure MySQL is running and databases are initialized.

## 2. Testing Flow

### Step A: Authentication (Auth Service)
- Port: `8081`
- Swagger: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **Register** a new user.
- **Login** to get the `access_token`.

### Step B: Explore Flights (Flight Service)
- Port: `8082`
- Swagger: [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
- *Note: You may need to manually insert some flight data into the `flights` and `flight_classes` tables in `flights_db` or `microservice` DB to test.*

### Step C: Create Booking (Booking Service)
- Port: `8084`
- Swagger: [http://localhost:8084/swagger-ui/index.html](http://localhost:8084/swagger-ui/index.html)
- Use the **Create Booking** endpoint with a valid `userId` and `flightId`.
- Copy the `bookingCode` from the response.

### Step D: Verify Booking
- Use the **Get Booking by Code** endpoint in Booking Service to verify the data was saved correctly.

## 3. Database Verification
You can check the data directly in MySQL:
- `SELECT * FROM microservice.users;`
- `SELECT * FROM bookings_db.bookings;`
- `SELECT * FROM bookings_db.passengers;`

### Step E: Test Rate Limiting (API Gateway)
- API Gateway acts as a shield. You can test the limits by sending multiple requests to any service via port `8080`.
- Detailed guide: [api-gateway/ratelimit-test.md](file:///d:/SpringBoot-Microservce/spring-boot-microservices-airgo-system/api-gateway/ratelimit-test.md)

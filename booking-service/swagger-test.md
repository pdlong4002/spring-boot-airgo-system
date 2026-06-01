# Booking Service Swagger Test Guide

Link Swagger UI: [http://localhost:8084/swagger-ui/index.html](http://localhost:8084/swagger-ui/index.html)

## 1. Create a Booking
**Endpoint:** `POST /api/v1/bookings`

**JSON Payload:**
```json
{
  "userId": 1,
  "flightId": 101,
  "passengers": [
    {
      "fullName": "Nguyen Van A",
      "identityNumber": "123456789",
      "seatId": 5,
      "flightClassId": 2,
      "ticketPrice": 150.0
    },
    {
      "fullName": "Tran Thi B",
      "identityNumber": "987654321",
      "seatId": 6,
      "flightClassId": 2,
      "ticketPrice": 150.0
    }
  ]
}
```

## 2. Get Booking by Code
**Endpoint:** `GET /api/v1/bookings/{code}`

**Instructions:**
- Replace `{code}` with the `bookingCode` returned from the Create Booking response (e.g., `A1B2C3`).
- Example: `http://localhost:8084/api/v1/bookings/A1B2C3`

# Tài liệu Hướng dẫn Kiểm thử API Tìm kiếm Chuyến bay (Flight Search API)

Tài liệu này chứa các cURL command mẫu và định dạng JSON dữ liệu trả về để bạn dễ dàng nhập vào Postman hoặc gọi trực tiếp từ terminal để kiểm thử tính năng vừa thêm.

---

## 1. Các trường hợp kiểm thử (Test Cases)

### Case 1: Lấy toàn bộ danh sách chuyến bay (Không bộ lọc, phân trang mặc định)
* **Phương thức:** `GET`
* **URL:** `http://localhost:8082/api/v1/flights/search` (hoặc qua API Gateway: `http://localhost:8080/api/v1/flights/search`)
* **cURL:**
```bash
curl -X GET "http://localhost:8082/api/v1/flights/search"
```

### Case 2: Tìm kiếm theo chặng bay (HAN -> SGN)
* **Phương thức:** `GET`
* **URL:** `http://localhost:8082/api/v1/flights/search?departureIata=HAN&arrivalIata=SGN`
* **cURL:**
```bash
curl -X GET "http://localhost:8082/api/v1/flights/search?departureIata=HAN&arrivalIata=SGN"
```

### Case 3: Tìm kiếm theo chặng bay và Ngày khởi hành
* **Phương thức:** `GET`
* **URL:** `http://localhost:8082/api/v1/flights/search?departureIata=HAN&arrivalIata=SGN&departureDate=2026-06-01`
* **cURL:**
```bash
curl -X GET "http://localhost:8082/api/v1/flights/search?departureIata=HAN&arrivalIata=SGN&departureDate=2026-06-01"
```

### Case 4: Lọc theo Hạng ghế và số lượng Ghế trống mong muốn (Ví dụ: Cần 2 ghế BUSINESS trống)
* **Phương thức:** `GET`
* **URL:** `http://localhost:8082/api/v1/flights/search?classType=BUSINESS&passengers=2`
* **cURL:**
```bash
curl -X GET "http://localhost:8082/api/v1/flights/search?classType=BUSINESS&passengers=2"
```

### Case 5: Lọc theo khoảng giá vé (Ví dụ: Từ 100 USD đến 500 USD)
* **Phương thức:** `GET`
* **URL:** `http://localhost:8082/api/v1/flights/search?minPrice=100&maxPrice=500`
* **cURL:**
```bash
curl -X GET "http://localhost:8082/api/v1/flights/search?minPrice=100&maxPrice=500"
```

### Case 6: Kết hợp phân trang và sắp xếp theo Giá (Giảm dần)
* **Phương thức:** `GET`
* **URL:** `http://localhost:8082/api/v1/flights/search?page=0&size=5&sortBy=flightClasses.price&sortDir=DESC`
* **cURL:**
```bash
curl -X GET "http://localhost:8082/api/v1/flights/search?page=0&size=5&sortBy=flightClasses.price&sortDir=DESC"
```

---

## 2. Định dạng JSON Phản hồi mẫu (Sample JSON Response)

Khi API chạy thành công, nó sẽ trả về kết quả phân trang theo chuẩn cấu trúc Page của Spring Data JPA như sau:

```json
{
  "content": [
    {
      "id": 1,
      "flightNumber": "VN123",
      "departureAirportName": "Noi Bai International Airport",
      "departureAirportIata": "HAN",
      "arrivalAirportName": "Tan Son Nhat International Airport",
      "arrivalAirportIata": "SGN",
      "departureTime": "2026-06-01T08:00:00",
      "arrivalTime": "2026-06-01T10:00:00",
      "flightClasses": [
        {
          "id": 1,
          "classType": "ECONOMY",
          "price": 120.00,
          "availableSeats": 50
        },
        {
          "id": 2,
          "classType": "BUSINESS",
          "price": 250.00,
          "availableSeats": 12
        }
      ]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "unpaged": false,
    "paged": true
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

# 🛡️ Spring Boot Auth Full Service
> **JWT + OAuth2 + MySQL + Spring Security 6**

Dự án này là một hệ thống xác thực và phân quyền toàn diện, kết hợp giữa phương thức truyền thống (Email/Password) và hiện đại (Social Login via Google/GitHub). 

---

## 🏗️ Kiến trúc & Công nghệ
- **Backend**: Java 21, Spring Boot 3.4.x
- **Bảo mật**: Spring Security 6 (Stateless), JJWT 0.11.5
- **OAuth2**: Google & GitHub Integration
- **Database**: MySQL 8.x, Spring Data JPA
- **Tài liệu**: OpenAPI 3 (Swagger UI) tại `/docs`
- **Tiện ích**: Lombok, ModelMapper, Dotenv

---

## 🔄 Workflow (Luồng hoạt động)

### 1. Luồng Xác thực Truyền thống (Traditional Auth)
1. **Request**: Client gửi Email & Password tới `/api/v1/auth/login`.
2. **Process**: Backend kiểm tra thông tin trong MySQL -> Mã hóa Password bằng BCrypt.
3. **Token**: Nếu đúng, `JwtService` tạo **Access Token** & **Refresh Token**.
4. **Response**: Trả về JSON chứa Token cho Client.

### 2. Luồng Xác thực OAuth2 (Social Login)
1. **Authorize**: Người dùng truy cập link `/oauth2/authorize/{provider}`.
2. **Redirect**: Backend chuyển hướng người dùng sang trang đăng nhập của Google/GitHub.
3. **Callback**: Sau khi đăng nhập xong, Provider gửi **Authorization Code** về Backend.
4. **Process**: 
   - `CustomOAuth2UserService` lấy thông tin từ Provider.
   - Kiểm tra Database: Nếu là User mới -> Tự động đăng ký (Người đầu tiên là **ADMIN**, còn lại là **USER**).
   - Nếu User cũ -> Cập nhật thông tin (Avatar, Name).
5. **Success**: `OAuth2SuccessHandler` tạo JWT và Redirect về API thành công kèm theo Token.

---

## 🛠️ Hướng dẫn Test nhanh (Demo Links)

Vì dự án chạy ở chế độ Stateless (không dùng giao diện), bạn có thể test trực tiếp bằng trình duyệt:

### 🔗 Link đăng nhập Social (Click để test)
*   **Đăng nhập Google**: [http://localhost:8080/oauth2/authorize/google](http://localhost:8080/oauth2/authorize/google)
*   **Đăng nhập GitHub**: [http://localhost:8080/oauth2/authorize/github](http://localhost:8080/oauth2/authorize/github)

### 🔗 Tài liệu API
*   **Swagger UI**: [http://localhost:8080/docs](http://localhost:8080/docs)

---

## 🔑 Cách sử dụng Token (Usage)
Sau khi nhận được `token` từ quy trình OAuth2 hoặc Login:
1. Copy chuỗi token đó.
2. Sử dụng công cụ như **Postman** hoặc **Swagger**.
3. Thêm vào Header: `Authorization: Bearer <your_token>`.
4. Bây giờ bạn có thể truy cập các API yêu cầu xác thực như `/api/v1/users/me`.

---

## 📂 Danh sách API chính

| Method | Endpoint | Mô tả | Quyền truy cập |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Đăng ký tài khoản mới | Public |
| `POST` | `/api/v1/auth/login` | Đăng nhập lấy JWT | Public |
| `GET` | `/api/v1/users/me` | Lấy thông tin cá nhân | Yêu cầu JWT |
| `PUT` | `/api/v1/users/password` | Thay đổi mật khẩu | Yêu cầu JWT |
| `DELETE` | `/api/v1/users/me` | Xóa tài khoản (Trừ Admin cuối) | Yêu cầu JWT |

---

## ⚙️ Cài đặt môi trường (.env)

Để chạy dự án, bạn cần tạo file `.env` tại thư mục gốc với các thông số sau:

```env
# Database
DATABASE_USERNAME=csdl_longsama
DATABASE_PASSWORD=12345

# OAuth2 Google
GOOGLE_CLIENT_ID=your_id_here
GOOGLE_CLIENT_SECRET=your_secret_here

# OAuth2 GitHub
GITHUB_CLIENT_ID=your_id_here
GITHUB_CLIENT_SECRET=your_secret_here
```

---

## 🛠️ Xử lý sự cố (Troubleshooting)
- **Lỗi `redirect_uri_mismatch`**: Kiểm tra lại Dashboard của Google/GitHub. Đảm bảo URI được cấu hình đúng là: `http://localhost:8080/oauth2/callback/google` (hoặc github).
- **Lỗi `401 Unauthorized`**: Đảm bảo token của bạn còn hạn (mặc định 24h) và có tiền tố `Bearer ` chính xác.

---

## 🌟 Tính năng bảo mật đặc biệt
- **Self-Deletion Protection**: Admin duy nhất trong hệ thống không được phép tự xóa chính mình để tránh mất quyền quản trị.
- **Smart Role Assignment**: Hệ thống tự nhận diện: User đăng ký đầu tiên là ADMIN, các User sau là USER.
- **Stateless**: Không lưu Session trên Server, bảo mật tuyệt đối bằng JWT.

---
Phát triển bởi **LongSama** 🚀

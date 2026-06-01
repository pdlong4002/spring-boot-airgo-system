# 🎙️ AirGo System - Cẩm nang Chuẩn bị Phỏng vấn Senior

Tài liệu này mô phỏng một buổi phỏng vấn kỹ thuật từ góc nhìn của một Kỹ sư Backend Cấp cao (Senior Backend Engineer) phỏng vấn chủ sở hữu dự án AirGo System. Tài liệu cung cấp các câu hỏi chuyên sâu, giải thích ý đồ của người phỏng vấn, câu trả lời mẫu xuất sắc và các câu hỏi mở rộng.

---

## 1. Câu hỏi Nhân sự (HR) & Động lực Dự án

### Q: Động lực nào khiến bạn xây dựng AirGo System và phần nào là khó khăn nhất đối với bạn?
* **Ý đồ của người phỏng vấn:** Đánh giá niềm đam mê thực sự đối với kiến trúc phần mềm (hay chỉ là copy code từ các video hướng dẫn) và đo lường khả năng kiên trì giải quyết vấn đề của bạn.
* **Câu trả lời xuất sắc:** "Tôi muốn hiểu sâu sắc về hệ thống phân tán. Việc dựng một ứng dụng Monolith CRUD thông thường thì khá đơn giản, nhưng tôi muốn đối mặt với các bài toán thực tế như giao dịch phân tán (distributed transactions) và tính nhất quán của dữ liệu. Khó khăn lớn nhất đối với tôi là cơ chế khóa ghế để tránh đặt trùng. Ban đầu tôi dùng khóa trực tiếp trong MySQL nhưng độ trễ quá cao, sau đó tôi đã chuyển dịch sang dùng khóa phân tán Redis kết hợp với TTL. Đó thực sự là một quá trình học hỏi và thử nghiệm rất lớn."
* **Câu hỏi mở rộng:** "Làm thế nào bạn đảm bảo khóa Redis được giải phóng an toàn nếu dịch vụ đang chạy bị sập đột ngột?"

## 2. Câu hỏi Giới thiệu Dự án

### Q: Hãy tóm tắt kiến trúc hệ thống của bạn trong vòng 2 phút.
* **Ý đồ của người phỏng vấn:** Đánh giá kỹ năng giao tiếp và khả năng bao quát toàn bộ hệ thống của bạn.
* **Câu trả lời xuất sắc:** "AirGo System là một nền tảng đặt vé máy bay xây dựng trên kiến trúc Microservices. Mọi request từ Client đi qua cổng Spring Cloud API Gateway để kiểm tra phân quyền JWT. Gateway định tuyến request đến 6 service nghiệp vụ độc lập (Auth, Flight, Seat, Booking, Payment, Notification) đăng ký tập trung tại Eureka Server. Giao tiếp đồng bộ giữa các service được xử lý qua OpenFeign. Đối với các tác vụ tranh chấp cao như giữ chỗ, chúng tôi dùng Redis Distributed Lock. Còn với các tác vụ không đồng bộ như gửi mail thông báo, chúng tôi dùng Kafka để tách biệt hoàn toàn Notification Service khỏi luồng đặt vé chính."
* **Câu hỏi mở rộng:** "Tại sao bạn không thực hiện xác thực JWT ở từng microservice riêng lẻ mà lại làm tập trung tại API Gateway?"

## 3. Câu hỏi Kiến trúc & Lựa chọn Đánh đổi (Trade-off)

### Q: Tại sao bạn lại chọn Microservices thay vì kiến trúc Monolith?
* **Ý đồ của người phỏng vấn:** Đảm bảo bạn đưa ra quyết định dựa trên bài toán thực tế chứ không phải chỉ chạy theo xu hướng công nghệ.
* **Câu trả lời xuất sắc:** "Kiến trúc Monolith chắc chắn sẽ giúp phát triển và triển khai nhanh hơn ở giai đoạn đầu. Tuy nhiên, với một hệ thống đặt vé máy bay, các phân vùng nghiệp vụ có nhu cầu scale rất khác nhau. Vào các dịp khuyến mãi lớn, `seat-service` và `booking-service` sẽ phải gánh lượng tải cực lớn, trong khi `auth-service` thì không. Microservices cho phép chúng tôi scale độc lập các service chịu tải cao này, cô lập các lỗi hệ thống (ví dụ: nếu mail server bị lỗi làm Notification Service sập, khách hàng vẫn có thể mua vé bình thường)."
* **Câu hỏi mở rộng:** "Khó khăn lớn nhất mà bạn thực tế đã gặp phải khi vận hành hệ thống microservices này là gì?"

### Q: Tại sao lại chọn Kafka thay vì RabbitMQ?
* **Ý đồ của người phỏng vấn:** Kiểm tra sự hiểu biết sâu sắc về các hệ thống Message Broker và ngữ cảnh sử dụng tối ưu của từng loại.
* **Câu trả lời xuất sắc:** "RabbitMQ rất tốt trong việc định tuyến tin nhắn phức tạp, nhưng Kafka vượt trội hoàn toàn về khả năng chịu tải cao và lưu trữ sự kiện để đọc lại (event replayability). Trong hệ thống đặt vé, các sự kiện như `BookingCreated` trong tương lai có thể được tiêu thụ bởi rất nhiều service mới (như hệ thống phân tích hành vi, tích điểm thành viên). Cấu trúc lưu trữ dạng log của Kafka cho phép các nhóm Consumer mới đọc lại toàn bộ dữ liệu lịch sử từ đầu, điều mà RabbitMQ không hỗ trợ một cách tự nhiên."
* **Câu hỏi mở rộng:** "Bạn xử lý thế nào nếu một Consumer trong Kafka bị lỗi khi đang xử lý tin nhắn?"

## 4. Câu hỏi về Microservices & Spring Cloud

### Q: Eureka Service Discovery sẽ xử lý thế nào khi một instance của microservice bị sập đột ngột?
* **Ý đồ của người phỏng vấn:** Đánh giá hiểu biết về tính chịu lỗi của mạng (network resiliency) và cơ chế cập nhật trạng thái trong hệ thống.
* **Câu trả lời xuất sắc:** "Mỗi instance định kỳ gửi tín hiệu nhịp tim (heartbeat) lên Eureka sau mỗi 30 giây. Nếu Eureka không nhận được nhịp tim sau một khoảng thời gian quy định, nó sẽ gạch tên instance đó khỏi danh sách đăng ký. Đồng thời, API Gateway và các client Feign cũng lưu cache danh sách này cục bộ. Khi cache được làm mới, các service sẽ lập tức ngừng gửi yêu cầu đến instance đã sập."
* **Câu hỏi mở rộng:** "Nếu chính Eureka Server bị sập, toàn bộ hệ thống có ngừng hoạt động ngay lập tức không?" (Trả lời: Không, các service vẫn liên lạc được với nhau nhờ cache cục bộ).

## 5. Câu hỏi về Database & Xử lý Đồng thời

### Q: Làm thế nào bạn ngăn chặn việc 2 khách hàng đặt trùng 1 chiếc ghế ở cùng một thời điểm?
* **Ý đồ của người phỏng vấn:** Đây là bài toán cốt lõi của hệ thống đặt vé. Kiểm tra cách bạn xử lý xung đột dữ liệu (concurrency) và các chiến lược khóa.
* **Câu trả lời xuất sắc:** "Khi khách chọn ghế, `seat-service` sẽ thực hiện đặt một khóa phân tán trên Redis bằng câu lệnh nguyên tử `SETNX` (Set if Not Exists) với thời gian hết hạn (TTL) là 15 phút. Vì Redis xử lý đơn luồng, lệnh `SETNX` này được đảm bảo diễn ra tuần tự và nguyên tử. Người gửi request trước sẽ tạo được khóa thành công và tiến hành thanh toán, request đến sau lập tức thất bại và nhận thông báo ghế đã có người giữ. Nếu quá 15 phút người đầu tiên không hoàn tất thanh toán, khóa tự động hết hạn và ghế được nhả ra."
* **Câu hỏi mở rộng:** "Điều gì xảy ra nếu khách hàng đã thanh toán thành công nhưng mạng bị ngắt ngay trước khi Redis kịp xóa khóa giữ chỗ?"

## 6. Câu hỏi Bảo mật (Security)

### Q: Bạn thực hiện xác thực và giải mã JWT ở đâu và như thế nào?
* **Ý đồ của người phỏng vấn:** Đánh giá tư duy thiết kế bảo mật vòng ngoài (perimeter security) đối chiếu với mô hình không tin cậy (zero-trust network).
* **Câu trả lời xuất sắc:** "Tôi áp dụng bảo mật vòng ngoài. Token JWT được chặn và kiểm tra tính hợp lệ hoàn toàn tại API Gateway thông qua một Global Filter. Nếu token hợp lệ, Gateway mới cho phép request đi tiếp vào mạng nội bộ. Việc này giúp các microservices nghiệp vụ bên trong không cần phải chứa các đoạn mã xử lý bảo mật trùng lặp, đảm bảo nguyên tắc Single Responsibility."
* **Câu hỏi mở rộng:** "Nếu các service bên trong không tự xác thực lại, làm sao để ngăn chặn một kẻ tấn công xâm nhập mạng nội bộ gọi trực tiếp tới các service này mà không đi qua Gateway?"

---

# 🛡️ Bảo vệ Dự án Trước Nhà Tuyển Dụng

## Lời khuyên khi giải thích các quyết định kiến trúc
1. **Không bao giờ trả lời "Vì công nghệ đó đang hot":** Luôn gắn lựa chọn công nghệ với một bài toán thực tế. (Ví dụ: "Tôi dùng Redis *vì* việc lock dòng dữ liệu trong MySQL gây ra tình trạng khóa hàng đợi và giảm hiệu năng nghiêm trọng dưới tải lớn.")
2. **Thừa nhận các điểm hạn chế (Trade-offs):** Một kỹ sư có kinh nghiệm luôn biết không có công nghệ nào là hoàn hảo. (Ví dụ: "Việc chia nhỏ thành Microservices làm quy trình triển khai phức tạp hơn nhiều, đó là lý do tôi buộc phải xây dựng CI/CD qua GitHub Actions để tự động hóa.")
3. **Mô tả cụ thể nghiệp vụ:** Đừng chỉ nói chung chung "Tôi dùng Kafka để gửi tin nhắn". Hãy nói "Tôi dùng Kafka để tách biệt nghiệp vụ Đặt vé (`Booking`) khỏi nghiệp vụ Gửi thông báo (`Notification`) nhằm tối ưu tốc độ phản hồi API cho người dùng."

## Những câu trả lời yếu cần tránh 🚫
* *"Tôi dùng Eureka vì video hướng dẫn trên mạng làm thế."*
* *"Kiến trúc Microservices luôn tốt hơn Monolith."* (Sai, monolith vẫn tốt nhất cho các dự án quy mô nhỏ/vừa).
* *"Hệ thống của tôi an toàn 100% không thể bị hack."* (Không có hệ thống nào an toàn tuyệt đối).

## Danh sách 50 Câu hỏi Phỏng vấn Cốt lõi (Checklist)
*Hãy chuẩn bị sẵn sàng để trả lời ngắn gọn tất cả các câu hỏi này.*

**Tổng quan & Kiến trúc**
1. Tại sao chọn microservices thay vì monolith cho dự án này?
2. Bạn xử lý giao dịch phân tán thế nào? (Ví dụ: Thanh toán thành công nhưng Booking bị lỗi hệ thống).
3. Làm thế nào để trace (theo dấu) một request đi qua nhiều microservices?
4. API Gateway hoạt động thế nào và tại sao chọn Spring Cloud Gateway?
5. Eureka Server xử lý thế nào khi xảy ra phân mảnh mạng (network partition)?
6. Tại sao cần tách cấu hình ra Config Server riêng biệt?
7. Điều gì xảy ra nếu MySQL database chính bị sập?
8. Dựa vào tiêu chí nào để bạn phân chia các microservices?

**Xử lý Đồng thời & Redis**
9. Cơ chế hoạt động của lệnh `SETNX` trong Redis là gì?
10. Lỗi Cache Stampede là gì và cách bạn phòng ngừa trong dự án?
11. Tại sao không dùng lệnh `SELECT ... FOR UPDATE` của MySQL thay cho Redis để khóa ghế?
12. Điều gì xảy ra nếu Redis bị sập đột ngột khi đang giữ khóa đặt ghế?
13. Làm sao đảm bảo tính duy nhất (idempotency) khi mở khóa ghế?

**Kafka & Giao tiếp Bất đồng bộ**
14. Mô tả chi tiết đường đi của sự kiện từ `booking-service` đến `notification-service`.
15. Làm thế nào để đảm bảo mail xác nhận không bị gửi 2 lần nếu Kafka gửi trùng tin nhắn?
16. Consumer Group trong Kafka hoạt động thế nào?
17. Tại sao Kafka lại đạt được tốc độ ghi dữ liệu cực nhanh so với các hàng đợi truyền thống?
18. Bạn định dạng và tuần tự hóa (serialize) dữ liệu truyền qua Kafka ra sao?

**Spring Boot & Java**
19. Cơ chế hoạt động ngầm của Spring Boot Auto-configuration là gì?
20. Những tính năng nào của Java 21 được bạn áp dụng trực tiếp vào dự án?
21. Tại sao Constructor Injection lại được khuyên dùng hơn Field Injection?
22. Cách hoạt động của annotation `@Transactional` trong Spring?
23. Phân biệt `@Component`, `@Service`, và `@Repository`.

**Bảo mật & JWT**
24. Cấu trúc của một chuỗi JWT gồm những thành phần nào?
25. Cách xử lý khi người dùng ấn nút Logout (thu hồi JWT trước thời gian hết hạn)?
26. Mật khẩu người dùng được mã hóa và lưu trữ thế nào trong database?
27. Lỗi XSS là gì và hệ thống của bạn phòng chống nó ra sao?
28. API Gateway xác thực chữ ký JWT như thế nào?

**Docker & CI/CD**
29. Lợi ích của việc chạy container Docker so với việc chạy file `.jar` trực tiếp trên server?
30. Cấu trúc mạng nội bộ trong file `docker-compose.yml` của bạn thế nào?
31. GitHub Actions làm cách nào để xác thực và deploy code lên server thật?
32. Lợi ích của multi-stage Docker build?
33. Bạn quản lý việc cập nhật cấu trúc database (migrations) thế nào?

**Database & JPA**
34. Lỗi N+1 select trong Hibernate là gì và cách bạn khắc phục?
35. Bạn thiết kế quan hệ giữa các bảng Flight, Seat, và Booking như thế nào?
36. Bạn có tạo Index nào trong MySQL không? Tại sao?
37. Isolation level (mức độ cô lập giao dịch) mặc định của database bạn dùng là gì?

**Hiệu năng & Độ bền bỉ**
38. Bạn có sử dụng Circuit Breaker (như Resilience4j) không? Tại sao?
39. Cách giới hạn lượt gọi (Rate Limiting) để tránh hệ thống bị quá tải?
40. Các chỉ số (metrics) quan trọng nào cần giám sát khi hệ thống chạy thực tế?
41. Bạn sẽ scale hệ thống thế nào nếu lượng người dùng tăng gấp 100 lần?

**Xử lý Tình huống nghiệp vụ**
42. Nếu làm lại dự án này từ đầu, bạn sẽ thay đổi hoặc cải tiến điều gì?
43. Lỗi khó nhất bạn từng gặp trong quá trình làm dự án này là gì và giải quyết ra sao?
44. Bạn đã viết những loại kiểm thử (test) nào cho hệ thống?
45. Tại sao dùng OpenFeign thay vì RestTemplate hay WebClient?
46. Cách bạn quản lý phiên bản (versioning) cho các API?
47. Chiến lược xử lý khi API của VNPay gặp sự cố hoặc phản hồi chậm?
48. Quản lý các thông tin nhạy cảm (mật khẩu, khóa bí mật) trong code thế nào?
49. Hệ thống xử lý lỗi cấu hình CORS ra sao?
50. Luồng chạy chi tiết của code từ lúc người dùng ấn nút "Đặt vé" cho đến khi nhận được mail?

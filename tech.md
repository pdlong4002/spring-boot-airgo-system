# AirGo System - Cẩm nang Học tập Công nghệ (Technology Study Guide)

Tài liệu này là một hướng dẫn học tập và ôn tập toàn diện về các công nghệ chính được sử dụng trong dự án AirGo System. Hướng dẫn này được thiết kế để giúp bạn củng cố kiến thức, chuẩn bị cho các buổi phỏng vấn và hiểu rõ cách từng công nghệ được triển khai thực tế trong mã nguồn của dự án này.

---

## 1. Java 21

### Nó là gì?
Java 21 là phiên bản hỗ trợ dài hạn (Long-Term Support - LTS) của ngôn ngữ lập trình Java. Nó giới thiệu nhiều tính năng quan trọng như Virtual Threads, Sequenced Collections, và Record Patterns.

### Tại sao nó được sử dụng?
Java cung cấp khả năng độc lập nền tảng, hệ sinh thái thư viện khổng lồ, kiểm soát kiểu dữ liệu mạnh mẽ (strong typing) và hiệu năng tuyệt vời cho các ứng dụng backend doanh nghiệp.

### Tại sao nó được dùng trong dự án NÀY?
Java 21 được chọn vì Spring Boot 3.x được tối ưu hóa cực tốt cho Java 17 và 21. Các tính năng Java hiện đại (như Records và Pattern Matching) giúp giảm thiểu mã nguồn lặp lại (boilerplate), trong khi Virtual Threads (Project Loom) giúp xử lý lượng truy cập đồng thời cực lớn mà không tốn tài nguyên hệ điều hành cho native threads.

### Các Khái niệm Cốt lõi
- **Virtual Threads (Luồng ảo):** Các luồng siêu nhẹ do JVM quản lý thay vì hệ điều hành, cho phép tạo ra hàng triệu luồng đồng thời mà không bị nghẽn.
- **Records:** Lớp đặc biệt dùng để chứa dữ liệu bất biến, tự động tạo các hàm getter/setter, equals, hashCode, và toString mà không cần thư viện Lombok hay viết code tay.
- **Pattern Matching (Khớp mẫu):** Đơn giản hóa việc kiểm tra kiểu dữ liệu (`instanceof`) và mệnh đề `switch`.

### Câu hỏi Phỏng vấn Thường gặp & Câu trả lời Mẫu
1. **Q:** Virtual Threads trong Java 21 là gì và khác gì so với Platform Threads thông thường?
   **A:** Virtual Threads là các luồng siêu nhẹ do JVM quản lý, có chi phí tạo rất thấp, giúp ứng dụng có thể chạy hàng triệu luồng cùng lúc. Platform Threads là luồng hệ điều hành truyền thống, mỗi luồng ánh xạ 1:1 với luồng OS nên rất tốn tài nguyên phần cứng.
2. **Q:** Records trong Java 21 hỗ trợ như thế nào trong Spring Boot?
   **A:** Records cung cấp cú pháp cực ngắn gọn để tạo ra các DTO (Data Transfer Object) bất biến. Trong Spring Boot, chúng rất phù hợp làm Request/Response payload truyền nhận dữ liệu giữa các microservices.
3. **Q:** Khác biệt giữa `var` và khai báo kiểu dữ liệu tường minh trong Java?
   **A:** `var` cho phép trình biên dịch tự động suy luận kiểu dữ liệu tại thời điểm biên dịch. Nó giúp code ngắn gọn, dễ đọc hơn mà không làm mất đi tính an toàn kiểu dữ liệu (strong typing).
4. **Q:** Garbage Collection (Bộ dọn rác) của Java ảnh hưởng thế nào đến độ trễ của microservice?
   **A:** Các đợt tạm dừng dọn rác (GC pause) có thể gây ra hiện tượng tăng độ trễ đột ngột (latency spikes). Trong Java 21, các GC hiện đại như ZGC hoạt động cực kỳ hiệu quả với độ trễ dưới 1 mili-giây, rất quan trọng đối với các dịch vụ thời gian thực như `seat-service`.
5. **Q:** Hãy giải thích về Sequenced Collections trong Java 21.
   **A:** Java 21 giới thiệu các interface mới (`SequencedCollection`, `SequencedSet`, `SequencedMap`) đại diện cho các tập hợp có thứ tự truy cập được định nghĩa rõ ràng, giúp dễ dàng lấy ra phần tử đầu tiên/cuối cùng hoặc đảo ngược tập hợp.
6. **Q:** Pattern Matching cho `switch` là gì?
   **A:** Cho phép một khối lệnh `switch` kiểm tra một giá trị dựa trên một loạt các mẫu (bao gồm khớp kiểu dữ liệu) thay vì chỉ khớp các giá trị chính xác, giúp code clean hơn nhiều.
7. **Q:** Tại sao chọn Java thay vì Node.js cho dự án này?
   **A:** Java hỗ trợ đa luồng tốt hơn, kiểm soát kiểu dữ liệu an toàn hơn và có hệ sinh thái Spring cực mạnh để phát triển các hệ thống doanh nghiệp lớn, phức tạp và cần độ ổn định cao như hệ thống đặt vé máy bay.
8. **Q:** Bạn xử lý lỗi NullPointerException như thế nào trong Java hiện đại?
   **A:** Sử dụng `Optional<T>` cho các hàm trả về có thể rỗng và tận dụng tính năng "Helpful NullPointerExceptions" của Java để chỉ ra chính xác biến nào bị null trong log lỗi.
9. **Q:** Java là tham trị (pass-by-value) hay tham chiếu (pass-by-reference)?
   **A:** Java hoàn toàn là tham trị. Đối với Object, giá trị được truyền đi chính là giá trị của địa chỉ tham chiếu đến đối tượng đó.
10. **Q:** Sự khác biệt giữa Abstract Class và Interface là gì?
    **A:** Interface định nghĩa một tập hợp các hành vi (contract) mà các class khác phải tuân theo (có thể có default method từ Java 8), trong khi Abstract Class có thể chứa cả trạng thái (biến instance) và constructor. Một class có thể implement nhiều interface nhưng chỉ extend được một abstract class.

### Các Lỗi Phổ biến
- Nhầm lẫn Virtual Threads với lập trình bất đồng bộ (Reactive Programming). Virtual threads giúp viết code tuần tự dễ hiểu nhưng vẫn chạy bất đồng bộ ngầm bên dưới.
- Lạm dụng từ khóa `var` khiến code khó đọc (ví dụ: `var x = readData()`).

### Thực hành Tốt nhất (Best Practices)
- Luôn sử dụng Records để định nghĩa các DTO chuyển đổi dữ liệu.
- Tránh sử dụng khóa (`synchronized` blocks) trên các tác vụ I/O tốn thời gian khi chạy Virtual Threads vì có thể gây nghẽn luồng vật lý bên dưới (carrier thread).

### Cách Dùng Thực tế Trong Dự Án
Java 21 được cấu hình làm phiên bản SDK mặc định trong file `pom.xml` của tất cả các service (ví dụ: `<java.version>21</java.version>` trong `auth-service`, `flight-service`).

---

## 2. Spring Boot 3.x

### Nó là gì?
Spring Boot là một phần mở rộng của Spring Framework giúp đơn giản hóa việc khởi tạo và phát triển ứng dụng Spring thông qua cơ chế tự động cấu hình (auto-configuration) và các cấu hình mặc định sẵn có.

### Tại sao nó được sử dụng?
Nó giúp loại bỏ hầu hết các cấu hình rườm rà (file XML hoặc Java Config phức tạp), cung cấp các máy chủ nhúng (Tomcat) và các dependencies dạng "starter" tích hợp sẵn.

### Tại sao nó được dùng trong dự án NÀY?
Giúp phát triển nhanh các microservices (`auth-service`, `booking-service`, v.v.) dưới dạng các ứng dụng độc lập, sẵn sàng chạy trên môi trường thực tế, dễ dàng đóng gói và deploy trong container Docker.

### Các Khái niệm Cốt lõi
- **Auto-Configuration:** Spring Boot tự đoán và cấu hình các Bean bạn cần dựa trên các thư viện có trong classpath.
- **Starters:** Các bộ dependency (ví dụ: `spring-boot-starter-web`) nhóm các thư viện liên quan để dễ quản lý.
- **Actuator:** Cung cấp các tính năng giám sát hệ thống như kiểm tra trạng thái hoạt động (health check), xem số liệu thống kê (metrics).

### Câu hỏi Phỏng vấn Thường gặp & Câu trả lời Mẫu
1. **Q:** Annotation `@SpringBootApplication` hoạt động như thế nào?
   **A:** Nó là sự kết hợp của 3 annotation: `@Configuration` (đánh dấu class cung cấp Bean), `@EnableAutoConfiguration` (kích hoạt tự động cấu hình), và `@ComponentScan` (quét toàn bộ các Component nằm trong package hiện tại).
2. **Q:** Sự khác biệt giữa Spring và Spring Boot?
   **A:** Spring là một framework tiêm phụ thuộc (Dependency Injection) yêu cầu cấu hình thủ công rất nhiều. Spring Boot là bản mở rộng của Spring, tự động cấu hình và tích hợp sẵn server để chạy ngay lập tức.
3. **Q:** Vai trò của Spring Boot Actuator là gì?
   **A:** Nó cung cấp các endpoint HTTP (như `/actuator/health`) để giám sát sức khỏe, tài nguyên phần cứng và các tham số môi trường của app. Rất quan trọng khi chạy trong Kubernetes hoặc Eureka để check tình trạng container.
4. **Q:** Làm thế nào để tắt một cấu hình tự động (auto-configuration) cụ thể?
   **A:** Sử dụng thuộc tính `exclude` trong annotation: `@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})`.
5. **Q:** Khác biệt giữa `@RestController` và `@Controller`?
   **A:** `@RestController` là sự kết hợp của `@Controller` và `@ResponseBody`. Nó tự động chuyển đổi đối tượng trả về từ hàm thành định dạng JSON/XML thay vì tìm kiếm và hiển thị một trang HTML (view template).
6. **Q:** Spring Boot quản lý cấu hình từ bên ngoài (externalized configuration) thế nào?
   **A:** Qua file `application.properties`/`application.yml`, biến môi trường hoặc tham số dòng lệnh. Trong hệ thống này, các cấu hình được tải tập trung từ `config-service`.
7. **Q:** Máy chủ nhúng (embedded server) mặc định trong Spring Boot là gì?
   **A:** Apache Tomcat, chạy trên cổng 8080 mặc định.
8. **Q:** Cơ chế Tiêm phụ thuộc (Dependency Injection - DI) hoạt động thế nào trong Spring?
   **A:** Container IoC của Spring quản lý toàn bộ vòng đời của các Bean và tự động tiêm (inject) các bean phụ thuộc vào đối tượng cần dùng (thông qua Constructor, Setter hoặc Field).
9. **Q:** Tại sao nên dùng Constructor Injection thay vì Field Injection (`@Autowired` trên biến)?
   **A:** Constructor Injection đảm bảo các đối tượng phụ thuộc không thể bị thay đổi (immutability), giúp dễ dàng viết Unit Test (mock dễ dàng) và phát hiện lỗi tham chiếu vòng (circular dependency) ngay khi khởi động app.
10. **Q:** Spring Profiles dùng để làm gì?
    **A:** Phân chia các cấu hình ứng dụng cho các môi trường chạy khác nhau (ví dụ: `dev`, `test`, `prod`) giúp dễ dàng quản lý.

### Các Lỗi Phổ biến
- Sử dụng Field Injection (`@Autowired`) thay vì Constructor Injection.
- Đặt class chứa `@SpringBootApplication` sai vị trí, khiến `@ComponentScan` không thể quét được các Bean nằm ở các package cha hoặc package ngang hàng.

### Thực hành Tốt nhất (Best Practices)
- Luôn ưu tiên dùng Constructor Injection (có thể kết hợp với `@RequiredArgsConstructor` của Lombok).
- Giữ Controller mỏng (thin controllers), toàn bộ logic nghiệp vụ phải nằm trong lớp `@Service`.

### Cách Dùng Thực tế Trong Dự Án
Mỗi microservice đều là một app Spring Boot độc lập. Ví dụ, `auth-service/pom.xml` khai báo các starter như `spring-boot-starter-webmvc` hay `spring-boot-starter-data-jpa`. Endpoint Actuator `/actuator/health` được dùng trong file `docker-compose.yml` để kiểm tra trạng thái container.

---

## 3. Spring Cloud (Eureka, Gateway, Config)

### Nó là gì?
Một bộ công cụ hỗ trợ xây dựng hệ thống phân tán (microservices), cung cấp các dịch vụ phát hiện dịch vụ (Eureka), định tuyến API (Gateway) và quản lý cấu hình tập trung (Config).

### Tại sao nó được sử dụng?
Việc quản lý địa chỉ IP, điều hướng cuộc gọi và cấu hình cho từng microservice một cách thủ công là cực kỳ khó khăn. Spring Cloud tự động hóa toàn bộ quá trình này.

### Tại sao nó được dùng trong dự án NÀY?
Để `booking-service` có thể gọi `flight-service` thông qua tên dịch vụ mà không cần quan tâm IP chạy ở đâu, tập trung toàn bộ cấu hình vào một mối và tạo ra một cổng truy cập an toàn duy nhất (API Gateway) cho người dùng.

### Các Khái niệm Cốt lõi
- **Service Discovery (Eureka):** Đóng vai trò như danh bạ điện thoại. Các microservice đăng ký IP/Port của mình lên đây. Các service khác sẽ truy vấn danh bạ này để tìm và gọi đối tác.
- **API Gateway:** Cổng đón nhận tất cả các request từ client, thực hiện phân quyền JWT, chống DDoS/rate limit và điều hướng request tới service tương ứng.
- **Config Server:** Lưu trữ và cung cấp toàn bộ file cấu hình `.yml` cho các service khi khởi động.

### Câu hỏi Phỏng vấn Thường gặp & Câu trả lời Mẫu
1. **Q:** Service Discovery là gì và tại sao chúng ta cần Eureka?
   **A:** Trong hệ thống Microservices, các container có thể được tắt đi, bật lên hoặc scale tự động khiến địa chỉ IP thay đổi liên tục. Eureka hoạt động như một danh bạ động, giúp các service tự động đăng ký và tìm kiếm nhau bằng tên dịch vụ (ví dụ: `flight-service`) thay vì IP cố định.
2. **Q:** Hãy giải thích cách Spring Cloud Gateway hoạt động trong dự án này.
   **A:** Gateway chạy ở cổng `8080`, chặn mọi request từ Client. Nó áp dụng một filter để giải mã và kiểm tra tính hợp lệ của token JWT. Nếu hợp lệ, nó sẽ tra cứu Eureka để tìm địa chỉ của service đích (ví dụ: `booking-service`) và chuyển tiếp request đi.
3. **Q:** Mục đích của Spring Cloud Config Server là gì?
   **A:** Tập trung toàn bộ thông số cấu hình (tài khoản database, API key, JWT secret) về một server duy nhất. Các service khác chỉ cần trỏ về đây để lấy cấu hình khi khởi động. Khi cần đổi mật khẩu database, ta chỉ cần chỉnh sửa trên Config Server thay vì phải cập nhật và rebuild từng microservice.
4. **Q:** Điều gì xảy ra nếu Config Server bị sập?
   **A:** Nếu Config Server sập khi các microservice đang khởi động, chúng sẽ không thể khởi động thành công. Nếu nó sập khi hệ thống đang chạy, các service vẫn hoạt động bình thường nhờ sử dụng cấu hình đã lưu trong cache, nhưng sẽ không thể cập nhật cấu hình mới.
5. **Q:** API Gateway xử lý bảo mật như thế nào?
   **A:** Thông qua các Global Filter. Gateway sẽ kiểm tra header `Authorization` để xác thực chữ ký JWT trước khi cho phép chuyển tiếp request tới các dịch vụ nghiệp vụ phía sau.
6. **Q:** Phân biệt API Gateway và Load Balancer?
   **A:** API Gateway hoạt động ở tầng ứng dụng (Lớp 7), xử lý routing, auth, logging, rate limiting. Load Balancer phân phối đều lưu lượng request tới các instance khác nhau của một service. Gateway thường gọi Load Balancer bên trong nó.
7. **Q:** Tại sao cấu hình `fetch-registry: false` trên Eureka Server?
   **A:** Vì Eureka Server đóng vai trò là máy chủ danh bạ, nó không cần phải đăng ký chính mình hay tải danh sách các service khác về làm gì (trừ khi thiết lập cụm Eureka Server chạy song song).
8. **Q:** Làm sao các microservice biết Config Server nằm ở đâu?
   **A:** Khai báo thông tin trong file cấu hình khởi động `application.yml` thông qua thuộc tính `spring.config.import=optional:configserver:http://config-service:8888`.
9. **Q:** Client-Side Load Balancing là gì?
   **A:** Là cơ chế tự cân bằng tải từ phía client gọi. Ví dụ Service A muốn gọi Service B, nó sẽ tự hỏi Eureka danh sách IP của B, sau đó tự chọn 1 IP theo thuật toán (ví dụ Round Robin) để gửi request trực tiếp, tránh đi qua một Load Balancer tập trung.
10. **Q:** Cách triển khai Rate Limiting trên Gateway?
    **A:** Sử dụng bộ lọc RequestRateLimiter kết hợp với Redis để giới hạn số lượng request tối đa trên giây dựa trên IP hoặc thông tin User ID.

### Các Lỗi Phổ biến
- Thiếu annotation `@EnableEurekaClient` hoặc cấu hình sai địa chỉ Eureka Server khiến dịch vụ không thể đăng ký.
- Khai báo cứng URL của service đích khi gọi qua HTTP thay vì gọi bằng tên dịch vụ trên Eureka.

### Thực hành Tốt nhất (Best Practices)
- Bảo mật tuyệt đối cổng API Gateway. Các service phía sau chỉ nên mở kết nối nội bộ hoặc áp dụng kiểm tra nghiêm ngặt để chỉ nhận request từ Gateway.

### Cách Dùng Thực tế Trong Dự Án
- `eureka-server` chạy ở cổng 8761.
- `config-service` chạy ở cổng 8888, cung cấp cấu hình từ thư mục `classpath:/configurations` (ví dụ: `auth-service.yml`).
- `api-gateway` tự động định tuyến các API và kiểm tra JWT.

---

## 4. Apache Kafka

### Nó là gì?
Là một nền tảng truyền truyền tin phân tán (event streaming platform) được thiết kế cho việc truyền dữ liệu tốc độ cao, khả năng chịu lỗi tốt và lưu trữ tin nhắn lâu dài.

### Tại sao nó được sử dụng?
Để giao tiếp bất đồng bộ giữa các microservices, giúp loại bỏ sự phụ thuộc trực tiếp giữa các service và tăng hiệu năng toàn hệ thống.

### Tại sao nó được dùng trong dự án NÀY?
Được sử dụng cho `notification-service`. Khi một vé được đặt thành công trong `booking-service`, service này chỉ cần đẩy một sự kiện `BookingCreatedEvent` vào Kafka rồi trả kết quả ngay cho người dùng. `notification-service` sẽ lắng nghe Kafka và gửi email xác nhận sau đó, giúp quy trình đặt vé không bị trễ vì việc gửi email.

### Các Khái niệm Cốt lõi
- **Topic:** Kênh/chủ đề nơi các tin nhắn được gửi đến.
- **Producer (Người sản xuất):** Ứng dụng/dịch vụ gửi tin nhắn lên Kafka.
- **Consumer (Người tiêu dùng):** Ứng dụng/dịch vụ đọc tin nhắn từ Kafka.
- **Broker:** Một server Kafka chạy trong cụm.
- **Zookeeper:** Quản lý và điều phối trạng thái của cụm Kafka Broker.

### Câu hỏi Phỏng vấn Thường gặp & Câu trả lời Mẫu
1. **Q:** Tại sao dùng Kafka thay vì REST API để gửi thông báo?
   **A:** Gửi email là tác vụ chậm và có thể thất bại nếu server gửi mail gặp sự cố. Nếu dùng REST, người dùng phải chờ gửi mail xong mới được xác nhận đặt vé, hoặc booking sẽ thất bại nếu mail lỗi. Dùng Kafka giúp tách biệt hai luồng: đặt vé thành công ngay lập tức và email được gửi bất đồng bộ khi Notification Service rảnh (Đảm bảo tính nhất quán sau - Eventual Consistency).
2. **Q:** Partition trong Kafka Topic là gì?
   **A:** Mỗi Topic được chia thành nhiều phần gọi là Partition để tăng khả năng xử lý song song. Nhiều Consumer trong cùng một nhóm có thể đọc dữ liệu đồng thời từ các partition khác nhau.
3. **Q:** Consumer Group là gì?
   **A:** Nhóm các Consumer hợp tác cùng nhau để đọc tin nhắn từ một Topic. Kafka đảm bảo mỗi partition chỉ được đọc bởi tối đa một consumer trong nhóm, tránh trùng lặp dữ liệu.
4. **Q:** Kafka đảm bảo thứ tự gửi tin nhắn như thế nào?
   **A:** Kafka **chỉ đảm bảo thứ tự tin nhắn trong phạm vi một Partition duy nhất**. Nếu muốn duy trì thứ tự cho các sự kiện liên quan (ví dụ: đặt vé -> thanh toán), bạn phải dùng chung một khóa (Message Key) như `bookingId` để chúng luôn rơi vào cùng một partition.
5. **Q:** Điều gì xảy ra với tin nhắn sau khi đã được Consumer đọc?
   **A:** Khác với RabbitMQ (tự xóa tin nhắn khi đọc xong), Kafka lưu trữ tin nhắn trên ổ đĩa trong một khoảng thời gian được cấu hình trước (mặc định 7 ngày).
6. **Q:** Vai trò của Zookeeper đối với Kafka?
   **A:** Quản lý siêu dữ liệu (metadata), điều phối bầu chọn Leader cho các Broker và lưu trữ thông tin cấu hình của cụm. (Các bản Kafka mới đang dần thay thế Zookeeper bằng KRaft, nhưng Zookeeper vẫn được dùng trong file docker-compose của dự án này).
7. **Q:** Làm thế nào để xử lý một Consumer bị lỗi khi đang đọc tin nhắn?
   **A:** Vị trí đọc (offset) của tin nhắn lỗi sẽ không được ghi nhận (commit). Khi consumer khởi động lại, nó sẽ tự động đọc lại từ vị trí chưa commit trước đó. Vì thế, code xử lý consumer cần có tính phản xạ để tránh lỗi lặp lại vô hạn.
8. **Q:** Tính Idempotent (Lũy đẳng) trong truyền nhận tin nhắn là gì?
   **A:** Là khả năng một Consumer nhận và xử lý cùng một tin nhắn nhiều lần mà không làm thay đổi trạng thái cuối cùng của hệ thống (ví dụ: kiểm tra xem email đã được gửi cho bookingId này chưa trước khi gửi lại).
9. **Q:** Giải thích cơ chế truyền tin "At-least-once" (Tối thiểu một lần).
   **A:** Kafka đảm bảo tin nhắn chắc chắn sẽ được gửi đi, nhưng có khả năng bị trùng lặp trong một số điều kiện mạng. Đây là cơ chế mặc định của Kafka.
10. **Q:** Tại sao dùng Kafka thay vì RabbitMQ?
    **A:** Kafka tối ưu cho việc xử lý dòng dữ liệu lớn (log-based event streaming) và cho phép đọc lại dữ liệu lịch sử. RabbitMQ tối ưu cho việc định tuyến tin nhắn phức tạp. Cho nhu cầu phát sự kiện (event-driven) trong microservices, Kafka mang lại khả năng mở rộng tốt hơn.

### Các Lỗi Phổ biến
- Lầm tưởng Kafka cam kết thứ tự tin nhắn trên toàn bộ Topic (thực tế chỉ cam kết trên từng Partition).
- Không cam kết offset (nếu cấu hình thủ công) dẫn đến việc đọc lặp lại tin nhắn cũ vô hạn khi service restart.

### Thực hành Tốt nhất (Best Practices)
- Luôn gán Message Key thích hợp nếu thứ tự của dữ liệu là quan trọng.
- Viết code Consumer đảm bảo tính lũy đẳng (idempotent).

### Cách Dùng Thực tế Trong Dự Án
Kafka chạy ở cổng 9092 thông qua Docker. `booking-service` hoạt động như một Producer gửi tin nhắn, còn `notification-service` lắng nghe và thực hiện gửi mail.

---

## 5. Redis

### Nó là gì?
Redis là một cơ sở dữ liệu lưu trữ cấu trúc dữ liệu dưới dạng key-value trực tiếp trong bộ nhớ RAM, cho tốc độ truy xuất cực kỳ nhanh.

### Tại sao nó được sử dụng?
Do dữ liệu lưu hoàn toàn trên RAM nên độ trễ cực thấp (dưới mili-giây). Redis hỗ trợ các kiểu dữ liệu nâng cao và các thao tác nguyên tử (atomic operations).

### Tại sao nó được dùng trong dự án NÀY?
Được sử dụng trong `seat-service` cho chức năng **Khóa ghế tạm thời (Seat Locking)**. Khi khách chọn ghế, hệ thống phải giữ chỗ tạm trong 15 phút để họ tiến hành thanh toán. Nếu không thanh toán, ghế sẽ tự động nhả ra. Cơ chế TTL (Time-To-Live) của Redis giúp giải quyết bài toán này cực kỳ mượt mà, đồng thời tính chất nguyên tử ngăn chặn hoàn toàn việc 2 người đặt trùng 1 ghế (double-booking).

### Các Khái niệm Cốt lõi
- **Key-Value Store:** Dữ liệu dạng khóa - giá trị.
- **TTL (Time to Live):** Thời gian sống của một khóa. Khi hết thời gian, Redis tự động xóa khóa đó.
- **Distributed Lock (Khóa phân tán):** Kỹ thuật dùng các câu lệnh như `SETNX` (Set if Not Exists) để đảm bảo tại một thời điểm chỉ một tiến trình được thao tác trên tài nguyên.

### Câu hỏi Phỏng vấn Thường gặp & Câu trả lời Mẫu
1. **Q:** Cơ chế khóa ghế bằng Redis hoạt động thế nào trong dự án này?
   **A:** Khi User A chọn ghế số 12, `seat-service` chạy câu lệnh nguyên tử lên Redis: `SET seat:12 "UserA" NX EX 900`. `NX` bảo đảm chỉ lưu khóa nếu khóa này chưa tồn tại. `EX 900` đặt TTL 15 phút. Nếu User B cũng muốn chọn ghế 12, lệnh `SETNX` sẽ trả về thất bại và User B nhận được thông báo ghế đã bị khóa. Sau 15 phút, nếu User A không thanh toán, Redis tự xóa khóa để nhả ghế ra.
2. **Q:** Tại sao không dùng cột `status` trong MySQL để khóa ghế cho đơn giản?
   **A:** Cập nhật trạng thái liên tục trong MySQL dưới lượng truy cập lớn sẽ làm nghẽn DB (table/row lock, deadlock) và giảm hiệu năng nghiêm trọng. Redis chạy trên RAM nên xử lý hàng chục nghìn lượt đặt cùng lúc rất dễ dàng. Hơn nữa, tự viết code quét và giải phóng các ghế quá hạn trong MySQL cần các tác vụ cron job chạy nền liên tục, rất phức tạp so với việc để Redis tự xóa qua TTL.
3. **Q:** Lỗi Cache Penetration (Bộ lọc bộ nhớ đệm bị xuyên thủng) là gì?
   **A:** Là khi client liên tục gửi request tìm các khóa không tồn tại cả trong Redis lẫn DB MySQL. Request luôn đi xuyên qua Redis để truy vấn trực tiếp vào DB, gây quá tải DB. Cách khắc phục: Lưu các giá trị rỗng (null) vào Redis với TTL ngắn hoặc dùng Bloom Filter.
4. **Q:** Hiện tượng Cache Stampede (Dogpile effect) là gì?
   **A:** Khi một khóa hot-key (được truy cập rất nhiều) hết hạn, hàng ngàn request cùng lúc nhận thấy cache trống và đồng loạt truy vấn xuống DB để ghi lại vào cache, gây sập DB. Cách khắc phục: Dùng khóa mutex để chỉ cho phép 1 thread đi xuống DB tải lại dữ liệu lên cache.
5. **Q:** Có đúng là Redis chỉ chạy đơn luồng (single-threaded)?
   **A:** Đúng, luồng xử lý các lệnh đọc/ghi chính của Redis là đơn luồng. Điều này loại bỏ hoàn toàn các vấn đề tranh chấp tài nguyên (race conditions), đảm bảo các lệnh chạy tuần tự và có tính nguyên tử cao.
6. **Q:** Redis lưu trữ dữ liệu thế nào nếu bị mất điện/restart?
   **A:** Redis hỗ trợ hai cơ chế lưu trữ xuống đĩa cứng: RDB (chụp ảnh dữ liệu định kỳ) và AOF (ghi log lại mọi lệnh ghi).
7. **Q:** Nếu Redis bị sập trong lúc khách đang khóa ghế thì sao?
   **A:** Tùy thuộc vào cơ chế lưu trữ đĩa cứng (RDB/AOF) mà thông tin khóa có thể bị mất. Với hệ thống lớn, người ta thường triển khai cụm Redis Cluster hoặc thuật toán Redlock để tăng tính chịu lỗi.
8. **Q:** Thuật toán Redlock là gì?
   **A:** Là thuật toán quản lý khóa phân tán chạy trên nhiều node Redis độc lập, đảm bảo an toàn cho khóa ngay cả khi một vài node Master bị sập.
9. **Q:** Làm thế nào để xóa bỏ cache khi dữ liệu gốc thay đổi?
   **A:** Thiết lập thời gian sống (TTL) phù hợp, hoặc viết code chủ động xóa/ghi đè khóa trong Redis khi có thao tác chỉnh sửa dữ liệu tương ứng trong MySQL (mô hình Cache-Aside).
10. **Q:** Redis hỗ trợ các kiểu dữ liệu nào?
    **A:** Strings, Lists, Sets, Sorted Sets, Hashes, Bitmaps, HyperLogLogs.

### Các Lỗi Phổ biến
- Set khóa phân tán nhưng quên gán TTL. Khi service giữ khóa bị crash đột ngột, khóa sẽ kẹt mãi mãi khiến ghế bị khóa vĩnh viễn.
- Lưu trữ các Object JSON quá lớn vào Redis, làm hao phí bộ nhớ RAM không cần thiết.

### Thực hành Tốt nhất (Best Practices)
- Luôn luôn gán thời gian hết hạn (TTL) cho mọi loại khóa phân tán.
- Đảm bảo việc giải phóng khóa chỉ được thực hiện bởi chính User đã tạo ra khóa đó (bằng cách kiểm tra token hoặc UUID duy nhất đi kèm).

### Cách Dùng Thực tế Trong Dự Án
Redis chạy ở cổng `6379`. Dịch vụ `seat-service` kết nối tới Redis qua thư viện `spring-boot-starter-data-redis` để thực hiện khóa và quản lý trạng thái sơ đồ ghế.

---

## 6. Docker & Docker Compose

### Nó là gì?
Docker là nền tảng giúp đóng gói ứng dụng cùng toàn bộ môi trường chạy vào một chiếc hộp gọi là Container. Docker Compose là công cụ để định nghĩa và khởi chạy hệ thống gồm nhiều container cùng một lúc.

### Tại sao nó được sử dụng?
Giúp giải quyết triệt để lỗi "chạy được trên máy tôi nhưng lỗi trên máy production". Mọi cấu hình hệ điều hành, thư viện hay phiên bản Java đều được cố định trong Docker Image.

### Tại sao nó được dùng trong dự án NÀY?
Để nhanh chóng thiết lập toàn bộ môi trường phức tạp của AirGo bao gồm MySQL, Redis, Zookeeper, Kafka cùng 7 microservices chỉ bằng duy nhất một câu lệnh (`docker-compose up`).

### Các Khái niệm Cốt lõi
- **Image:** Bản thiết kế đọc-ghi dùng để tạo ra Container (như một Class trong lập trình).
- **Container:** Một instance chạy độc lập được tạo ra từ Image (như một Object).
- **Dockerfile:** File chứa tuần tự các dòng lệnh để xây dựng nên một Image.
- **Volume:** Cơ chế gắn thư mục từ máy thật vào trong Container để lưu trữ dữ liệu lâu dài (tránh mất mát khi container bị xóa).
- **Network:** Mạng ảo biệt lập giúp các container giao tiếp nội bộ an toàn với nhau.

### Câu hỏi Phỏng vấn Thường gặp & Câu trả lời Mẫu
1. **Q:** Sự khác biệt giữa Docker Container và Máy ảo (Virtual Machine - VM)?
   **A:** Máy ảo chứa toàn bộ một hệ điều hành khách (Guest OS) riêng biệt nên rất nặng. Docker Container sử dụng chung nhân hệ điều hành (Kernel) của máy chủ và chỉ chứa các file ứng dụng cần thiết nên cực kỳ nhẹ và khởi động rất nhanh.
2. **Q:** Tại sao cần ánh xạ cổng (Port mapping, ví dụ `8080:8080`)?
   **A:** Các container chạy trong mạng nội bộ biệt lập của Docker. Ánh xạ cổng giúp chuyển hướng lưu lượng từ cổng của máy vật lý vào cổng tương ứng của container để chúng ta truy cập được từ bên ngoài.
3. **Q:** Thuộc tính `depends_on` hoạt động thế nào trong Docker Compose?
   **A:** Nó quy định thứ tự khởi động của các container. Ví dụ `auth-service` phụ thuộc vào `mysql` thì container `mysql` sẽ được chạy trước. Tuy nhiên, nó chỉ đảm bảo container đích được bật lên chứ không đợi phần mềm bên trong khởi động xong.
4. **Q:** Làm sao để chắc chắn một service chỉ chạy khi database đã hoàn toàn sẵn sàng nhận kết nối?
   **A:** Thiết lập `healthcheck` cho container database. Ở service phụ thuộc, cấu hình `condition: service_healthy` thay vì chỉ dùng `service_started`.
5. **Q:** Docker Volume là gì và tại sao nó lại bắt buộc đối với MySQL?
   **A:** Container có tính chất tạm thời, nếu xóa container đi thì toàn bộ dữ liệu mới tạo bên trong nó cũng biến mất. Volume giúp liên kết thư mục chứa dữ liệu của MySQL trong container ra ổ đĩa máy thật, bảo vệ dữ liệu không bị mất khi restart hay cập nhật phiên bản DB.
6. **Q:** Hãy giải thích khái niệm Docker build context.
   **A:** Là tập hợp toàn bộ các file nằm trong thư mục được truyền vào lệnh `docker build`. Khi chạy, Docker CLI sẽ gửi toàn bộ file này lên Docker daemon để tiến hành build.
7. **Q:** Làm cách nào các microservice gọi được nhau trong môi trường Docker Compose?
   **A:** Chúng cùng tham gia chung một mạng ảo (ví dụ: `spring-microservice`). Nhờ DNS nội bộ của Docker, các service có thể trực tiếp gọi nhau bằng tên container thay vì IP (ví dụ: `http://config-service:8888`).
8. **Q:** Kỹ thuật Multi-stage build trong Docker là gì?
   **A:** Là việc chia quá trình build Dockerfile làm nhiều bước. Bước đầu dùng các công cụ nặng để compile (như Maven), bước sau chỉ copy file jar đã compile sang một môi trường JRE siêu nhẹ (như Alpine). Giúp giảm dung lượng image cuối cùng rất nhiều.
9. **Q:** Làm thế nào để xem log của một container đang chạy trong Compose?
   **A:** Dùng lệnh: `docker-compose logs -f <tên_container>`.
10. **Q:** Điều gì xảy ra nếu hai container cùng ánh xạ ra một cổng trên máy thật?
    **A:** Docker sẽ báo lỗi xung đột cổng và không cho phép container thứ hai khởi chạy.

### Cách Dùng Thực tế Trong Dự Án
File `docker-compose.yml` định nghĩa toàn bộ hạ tầng. Các microservice kết nối với DB thông qua URL `jdbc:mysql://mysql:3306/microservice` bằng cách sử dụng chính tên dịch vụ làm hostname.

---

## 7. GitHub Actions (CI/CD)

### Nó là gì?
Là công cụ tự động hóa quy trình phát triển phần mềm (tích hợp và triển khai liên tục) được tích hợp trực tiếp bên trong hệ thống GitHub.

### Tại sao nó được sử dụng?
Tự động biên dịch, chạy unit test và đóng gói mã nguồn mỗi khi có lập trình viên đẩy code mới lên GitHub, giúp phát hiện lỗi sớm.

### Tại sao nó được dùng trong dự án NÀY?
Để tự động kiểm thử 7 microservices. Dự án áp dụng mô hình "Reusable Workflows" (workflow dùng chung) để tránh viết lặp lại các bước cài đặt Java, chạy Maven cho từng service riêng lẻ.

### Các Khái niệm Cốt lõi
- **Workflow:** Quy trình tự động hóa được thiết lập bằng file YAML.
- **Event:** Sự kiện kích hoạt workflow (ví dụ: hành động `push` hoặc `pull_request` vào nhánh `main`).
- **Job:** Nhóm các công việc (steps) chạy trên cùng một máy ảo (Runner).
- **Runner:** Máy chủ (Linux/Windows) chịu trách nhiệm chạy các lệnh do workflow yêu cầu.

### Câu hỏi Phỏng vấn Thường gặp & Câu trả lời Mẫu
1. **Q:** CI/CD là gì?
   **A:** CI (Continuous Integration - Tích hợp liên tục): Tự động build và test code khi có thay đổi. CD (Continuous Delivery/Deployment - Triển khai liên tục): Tự động phát hành và đưa ứng dụng lên các server chạy thực tế.
2. **Q:** Reusable Workflow trong GitHub Actions là gì?
   **A:** Là một file workflow mẫu chứa các bước chạy dùng chung. Các workflow khác có thể gọi lại file này để tái sử dụng, giúp giảm thiểu trùng lặp code cấu hình.
3. **Q:** Làm thế nào để chỉ kích hoạt workflow khi có thay đổi code ở một thư mục nhất định?
   **A:** Sử dụng thuộc tính lọc đường dẫn `paths` trong sự kiện kích hoạt (ví dụ: `paths: ['auth-service/**']`).
4. **Q:** Làm cách nào bảo mật các thông tin nhạy cảm (như mật khẩu MySQL) trong GitHub Actions?
   **A:** Sử dụng GitHub Secrets của Repository và truy xuất trong file YAML qua cú pháp `${{ secrets.TEN_BIEN }}`.
5. **Q:** Artifact trong GitHub Actions là gì?
   **A:** Là sản phẩm đầu ra được tạo ra trong quá trình chạy (ví dụ: file `.jar` sau khi build), có thể tải về hoặc chuyển tiếp cho các Job tiếp theo sử dụng.
6. **Q:** Các Job trong một workflow có chạy song song không?
   **A:** Mặc định các Job chạy song song độc lập. Nếu muốn Job này phải đợi Job kia hoàn thành trước, dùng từ khóa `needs`.
7. **Q:** Đoạn lệnh `actions/checkout@v4` dùng để làm gì?
   **A:** Dùng để tải mã nguồn từ repository trên GitHub về máy ảo đang chạy của GitHub Actions để có code chạy biên dịch.
8. **Q:** Điểm khác biệt giữa GitHub Actions và Jenkins?
   **A:** Jenkins yêu cầu bạn tự cài đặt và quản trị máy chủ CI. GitHub Actions là dịch vụ cloud được quản lý hoàn toàn và tích hợp sâu sắc với hệ sinh thái của GitHub.
9. **Q:** Làm sao chạy test cho dự án Spring Boot trên GitHub Actions?
   **A:** Thêm bước chạy lệnh `mvn test` sau khi đã cấu hình xong môi trường JDK.
10. **Q:** Điều gì xảy ra nếu một bước (step) trong Job bị lỗi?
    **A:** Mặc định Job sẽ lập tức dừng lại và được đánh dấu là thất bại (Failed), trừ khi có cấu hình `continue-on-error: true`.

### Cách Dùng Thực tế Trong Dự Án
Thư mục `.github/workflows/` chứa file `build-template.yml` chứa toàn bộ logic biên dịch chung của Maven. Các file YAML riêng của từng service sẽ gọi đến template này mỗi khi phát hiện thay đổi trong thư mục tương ứng.

---
*Kết thúc cẩm nang ôn tập. Hãy đọc thật kỹ trước buổi phỏng vấn của bạn!*

# 🌐 Social Network API

## 📌 Giới thiệu
**Social Network API** là một ứng dụng backend được xây dựng bằng **Spring Boot 3.5.4** và **Java 17**.  
Ứng dụng cung cấp các tính năng cơ bản của một mạng xã hội thu nhỏ: xác thực bằng JWT, đăng kí người dùng, đăng bài viết, like/comment bài viết,
theo dõi người dùng, chat realtime qua WebSocket, thông báo realtime, gửi mail và caching với Redis.  

---

## ✨ Tính năng chính
- 🔑 Đăng ký & Đăng nhập (Spring Security + JWT)
- 💬 Đăng bài viết
- 💬 Like bài viết
- 💬 Comment bài viết
- 👥 Follow / Unfollow người dùng  
- 💬 Chat realtime (WebSocket + STOMP)  
- 🔔 Thông báo realtime (WebSocket)
- 📧 Gửi email qua Gmail SMTP  
- 🗄️ Cache với Redis
  
---

## 🛠️ Công nghệ sử dụng
- **Ngôn ngữ:** Java 17  
- **Framework:** Spring Boot 3.5.4  
- **Bảo mật:** Spring Security, JWT (jjwt 0.11.5)  
- **CSDL:** MySQL 8 (MySQL Connector/J)  
- **Cache:** Redis  
- **Realtime:** WebSocket  
- **ORM:** Spring Data JPA, Hibernate  
- **Validation:** Spring Boot Validation  
- **Template Engine:** Thymeleaf  
- **Mapper:** MapStruct 1.5.5.Final  
- **Thư viện khác:** Lombok, Jackson, Mail  
- **Build Tool:** Maven  

---

## ⚙️ Cài đặt & Chạy ứng dụng

### Yêu cầu môi trường
- Java 17+  
- Maven 3.8+  
- MySQL 8+  
- Redis  

### Các bước cài đặt
```bash
# Clone repo
git clone https://github.com/minhtien213/social-network-api.git
cd social-network-api

# Cấu hình CSDL trong src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/social_network_api
spring.datasource.username=minhtien213
spring.datasource.password=minhtien213

# Chạy ứng dụng bằng Maven
mvn spring-boot:run

---

**📖 Cấu hình chính**
CSDL (MySQL)
Database: social_network_api
Username: minhtien213
Password: minhtien213

JWT
Secret key: thisis-my-super-secret-long-key-1234567890
Redis
Host: localhost
Port: 6379
Timeout: 60000ms

Mail (SMTP Gmail)
Host: smtp.gmail.com
Port: 587
Username: minhtien213@gmail.com
Password: (app password)

**Cấu trúc thư mục**
src/
├── main/
│   ├── java/com/example/social_network_api
│   │   ├── controller/    # REST API controllers
│   │   ├── service/       # Business logic
│   │   ├── repository/    # JPA repositories
│   │   ├── entity/        # Entity models
│   │   ├── dto/           # File dtos
│   │   ├── mapper/        # File mapper
│   │   ├── exception/     # File exceptions
│   │   ├── security/      # Security & JWT config
│   │   └── config/        # Cấu hình (Redis, WebSocket, Mail...)
│   │   └── utils/         # File utils
│   │   └── common/        # File common
│   └── resources/
│       ├── application.properties
│       └── static/
└── test/                  # Unit & Integration tests

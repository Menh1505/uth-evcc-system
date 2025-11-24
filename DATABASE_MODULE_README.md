# PostgreSQL Database Module for EVCC Application

## Mô tả

Module này cung cấp kết nối và quản lý PostgreSQL database cho ứng dụng EVCC.

## Cấu trúc Module

### 1. Configuration

- `DatabaseConfig.java` - Cấu hình kết nối database
- `application.properties` - Thông số kết nối

### 2. Entity

- `User.java` - Entity mẫu để test database

### 3. Repository

- `UserRepository.java` - Interface để thao tác với User entity

### 4. Service

- `DatabaseService.java` - Service quản lý các thao tác database

### 5. Controller

- `DatabaseController.java` - REST API endpoints để test database

### 6. Component

- `DatabaseInitializer.java` - Component khởi tạo và test kết nối khi app start

## Cài đặt và Sử dụng

### 1. Cài đặt PostgreSQL

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib

# CentOS/RHEL
sudo yum install postgresql-server postgresql-contrib

# macOS (với Homebrew)
brew install postgresql
```

### 2. Tạo Database

```bash
# Đăng nhập PostgreSQL
sudo -u postgres psql

# Tạo database
CREATE DATABASE evcc_db;

# Tạo user (tùy chọn)
CREATE USER evcc_user WITH PASSWORD 'evcc_password';
GRANT ALL PRIVILEGES ON DATABASE evcc_db TO evcc_user;

# Thoát
\q
```

### 3. Cấu hình Database

Chỉnh sửa file `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/evcc_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 4. Chạy Application

```bash
# Compile và chạy
./mvnw spring-boot:run

# Hoặc
mvn clean spring-boot:run
```

## API Endpoints

### Test Connection

```bash
# Test kết nối database
curl -X GET http://localhost:8080/api/database/test-connection

# Response:
{
  "connected": true,
  "info": "Connected to PostgreSQL version: ...",
  "timestamp": 1697123456789
}
```

### Database Info

```bash
# Thông tin database
curl -X GET http://localhost:8080/api/database/info
```

### User Management

```bash
# Lấy tất cả users
curl -X GET http://localhost:8080/api/database/users

# Lấy user theo username
curl -X GET http://localhost:8080/api/database/users/admin

# Tạo user mới
curl -X POST http://localhost:8080/api/database/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@evcc.com",
    "fullName": "New User"
  }'

# Cập nhật user
curl -X PUT http://localhost:8080/api/database/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updateduser",
    "email": "updated@evcc.com",
    "fullName": "Updated User"
  }'

# Xóa user
curl -X DELETE http://localhost:8080/api/database/users/1

# Đếm số lượng users
curl -X GET http://localhost:8080/api/database/users/count
```

## Troubleshooting

### Lỗi kết nối database

1. Kiểm tra PostgreSQL đang chạy:

```bash
sudo systemctl status postgresql
```

2. Kiểm tra port và host:

```bash
sudo netstat -tulnp | grep 5432
```

3. Kiểm tra authentication trong `/etc/postgresql/*/main/pg_hba.conf`

### Lỗi permission

```bash
# Cấp quyền cho user
sudo -u postgres psql
GRANT ALL PRIVILEGES ON DATABASE evcc_db TO your_username;
```

### Reset password PostgreSQL

```bash
sudo -u postgres psql
ALTER USER postgres PASSWORD 'new_password';
```

## Logs và Monitoring

- Application logs sẽ hiển thị thông tin kết nối database khi start
- Hibernate sẽ log SQL queries (có thể tắt bằng cách set `spring.jpa.show-sql=false`)
- Connection pool metrics có thể monitor qua Actuator endpoints

## Bảo mật

- Không commit password vào git
- Sử dụng environment variables cho production:

```bash
export DB_PASSWORD=your_secure_password
```

- Trong application.properties:

```properties
spring.datasource.password=${DB_PASSWORD:default_password}
```

# 🚀 HƯỚNG DẪN KHỞI ĐỘNG HỆ THỐNG EVCC

## 📋 Tổng quan

Hệ thống EVCC (Electric Vehicle Car Sharing) tự động khởi tạo tài khoản admin khi chạy lần đầu.

## 🔧 Khởi động hệ thống

### 1. Khởi động Database (PostgreSQL)

```bash
# Từ thư mục gốc của project
./db.sh start
```

### 2. Khởi động Backend

```bash
cd evcc
./mvnw spring-boot:run
```

### 3. Khởi động Frontend

```bash
cd fe
./mvnw spring-boot:run
```

## 👨‍💼 Tài khoản Admin mặc định

Khi backend khởi động lần đầu, hệ thống sẽ tự động tạo tài khoản admin:

- **Username**: `admin`
- **Password**: `admin`
- **Roles**: `ADMIN`, `USER`
- **Status**: Đã xác minh (verified)

### 📱 Truy cập Admin

- **Backend API**: http://localhost:3000/api
- **Frontend Admin**: http://localhost:8081/admin
- **Frontend User**: http://localhost:8081

## 🔑 Quyền Admin

Admin có thể thực hiện các chức năng sau:

### 👥 Quản lý người dùng

- `GET /api/users` - Xem danh sách tất cả user
- `GET /api/users/unverified` - Xem user chưa xác minh
- `PUT /api/users/{userId}/verify` - Xác minh người dùng
- `GET /api/users/stats` - Thống kê user

### 🚗 Quản lý xe

- `POST /api/vehicles` - Thêm xe mới
- `PUT /api/vehicles/{id}` - Sửa thông tin xe
- `DELETE /api/vehicles/{id}` - Xóa xe
- `GET /api/vehicles/available` - Xem xe chưa được sử dụng

### 📊 Quản lý hợp đồng

- `PUT /api/contracts/{id}/status` - Thay đổi trạng thái hợp đồng
- `DELETE /api/contracts/{id}` - Xóa hợp đồng (chỉ DRAFT)

### 🔧 Quản trị hệ thống

- `GET /api/users/admin/status` - Kiểm tra tình trạng admin
- `POST /api/users/admin/reset-password` - Reset mật khẩu admin

## 📝 Log khởi động

Khi backend khởi động, bạn sẽ thấy log như sau:

```
=== KHỞI ĐỘNG HỆ THỐNG EVCC ===
Kiểm tra và khởi tạo dữ liệu admin...
Kiểm tra và tạo roles mặc định...
Đã tạo role: ADMIN
Đã tạo role: USER
Kiểm tra và tạo admin user...
Tạo admin user mới...
Đã tạo admin user: username='admin', password='admin', verified=true
⚠️  QUAN TRỌNG: Hãy đổi mật khẩu admin sau khi đăng nhập lần đầu!
✅ Admin account đã sẵn sàng
📋 Thông tin đăng nhập admin:
   Username: admin
   Password: admin
   URL Admin: http://localhost:3000/admin
=== HỆ THỐNG EVCC ĐÃ KHỞI ĐỘNG HOÀN TẤT ===
```

## 🔒 Bảo mật

### ⚠️ Quan trọng

- **Đổi mật khẩu admin** ngay sau khi đăng nhập lần đầu
- Mật khẩu mặc định chỉ dành cho development
- Trong production, nên sử dụng environment variables cho mật khẩu

### 🔄 Reset mật khẩu admin

Nếu quên mật khẩu admin, có thể reset bằng API:

```bash
curl -X POST http://localhost:3000/api/users/admin/reset-password \
  -H "Authorization: Bearer <admin-token>"
```

## 🛠️ Development

### Kiểm tra admin status

```bash
curl -X GET http://localhost:3000/api/users/admin/status \
  -H "Authorization: Bearer <admin-token>"
```

### Đăng nhập admin

```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin"}'
```

## 🚀 Workflow đầy đủ

1. **Khởi động database**: `./db.sh start`
2. **Khởi động backend**: `cd evcc && ./mvnw spring-boot:run`
3. **Đăng nhập admin**: POST `/api/auth/login` với `admin/admin`
4. **Truy cập admin panel**: Sử dụng JWT token
5. **Thêm xe**: POST `/api/vehicles` với role ADMIN
6. **Xác minh user**: PUT `/api/users/{userId}/verify`
7. **Quản lý hợp đồng**: Theo dõi và quản lý các hợp đồng mua xe

## 📞 Hỗ trợ

Nếu gặp vấn đề:

1. Kiểm tra log console khi startup
2. Kiểm tra database đã chạy chưa: `./db.sh status`
3. Kiểm tra admin status: `GET /api/users/admin/status`
4. Reset admin nếu cần: `POST /api/users/admin/reset-password`

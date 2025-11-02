# Authentication API Documentation

## Overview

API đăng nhập và đăng ký đơn giản với username và password (không mã hóa). User mới sẽ được gán role USER mặc định.

## Base URL

```
http://localhost:8080/api/auth
```

## Endpoints

### 1. Đăng ký user mới

**POST** `/api/auth/register`

**Request Body:**

```json
{
  "username": "test_user",
  "password": "123456"
}
```

**Response Success (201 Created):**

```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "test_user",
  "roles": ["USER"]
}
```

**Response Error (400 Bad Request):**

```json
{
  "success": false,
  "message": "Username đã tồn tại",
  "userId": null,
  "username": null,
  "roles": null
}
```

### 2. Đăng nhập

**POST** `/api/auth/login`

**Request Body:**

```json
{
  "username": "test_user",
  "password": "123456"
}
```

**Response Success (200 OK):**

```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "test_user",
  "roles": ["USER"]
}
```

**Response Error (401 Unauthorized):**

```json
{
  "success": false,
  "message": "Username hoặc password không đúng",
  "userId": null,
  "username": null,
  "roles": null
}
```

### 3. Kiểm tra username có tồn tại

**GET** `/api/auth/check-username?username=test_user`

**Response - Username đã tồn tại:**

```json
{
  "success": false,
  "message": "Username đã tồn tại"
}
```

**Response - Username có thể sử dụng:**

```json
{
  "success": true,
  "message": "Username có thể sử dụng"
}
```

### 4. Health Check

**GET** `/api/auth/health`

**Response:**

```json
{
  "success": true,
  "message": "Auth service is running"
}
```

## Validation Rules

### Username:

- Không được để trống
- Phải là duy nhất trong hệ thống
- Tối đa 100 ký tự

### Password:

- Không được để trống
- Không có mã hóa (lưu trữ plain text)
- Tối đa 255 ký tự

## Default Role

- User mới đăng ký sẽ tự động được gán role "USER"
- Role "USER" sẽ được tạo tự động nếu chưa tồn tại

## Error Codes

- **400 Bad Request**: Dữ liệu đầu vào không hợp lệ
- **401 Unauthorized**: Đăng nhập thất bại
- **500 Internal Server Error**: Lỗi hệ thống

## Testing với cURL

### Đăng ký:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "test_user", "password": "123456"}'
```

### Đăng nhập:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test_user", "password": "123456"}'
```

### Kiểm tra username:

```bash
curl -X GET "http://localhost:8080/api/auth/check-username?username=test_user"
```

## Notes

- **Bảo mật**: API này không sử dụng mã hóa password và không có JWT token
- **CORS**: API cho phép tất cả origins (`*`)
- **Logging**: Tất cả request đều được log với thông tin user
- **Transaction**: Sử dụng `@Transactional` để đảm bảo tính nhất quán dữ liệu

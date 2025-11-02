# User Profile Management API

## Mô tả

API quản lý thông tin profile người dùng với các trường bổ sung: số căn cước công dân, số bằng lái xe, và trạng thái xác minh.

## Các trường mới trong User Entity

### 1. `citizenId` (String)

- **Mô tả**: Số căn cước công dân
- **Kiểu dữ liệu**: String (tối đa 20 ký tự)
- **Validation**: Chỉ chứa số (0-9)
- **Nullable**: Có (không bắt buộc)
- **Database column**: `citizen_id`

### 2. `driverLicense` (String)

- **Mô tả**: Số bằng lái xe
- **Kiểu dữ liệu**: String (tối đa 20 ký tự)
- **Validation**: Chỉ chứa chữ và số (A-Z, a-z, 0-9)
- **Nullable**: Có (không bắt buộc)
- **Database column**: `driver_license`

### 3. `isVerified` (Boolean)

- **Mô tả**: Trạng thái xác minh tài khoản
- **Kiểu dữ liệu**: Boolean
- **Giá trị mặc định**: `false`
- **Nullable**: Không (bắt buộc)
- **Database column**: `is_verified`

## API Endpoints

### Base URL: `/api/users`

### 1. Lấy thông tin profile cá nhân

**GET** `/api/users/profile`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "admin",
  "citizenId": "123456789012",
  "driverLicense": "B2123456789",
  "isVerified": false,
  "roles": ["USER"],
  "createdAt": "2024-11-03T10:00:00",
  "updatedAt": "2024-11-03T10:30:00"
}
```

### 2. Cập nhật thông tin profile cá nhân

**PUT** `/api/users/profile`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**

```json
{
  "citizenId": "123456789012",
  "driverLicense": "B2123456789"
}
```

**Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "admin",
  "citizenId": "123456789012",
  "driverLicense": "B2123456789",
  "isVerified": false,
  "roles": ["USER"],
  "createdAt": "2024-11-03T10:00:00",
  "updatedAt": "2024-11-03T10:35:00"
}
```

### 3. Xác minh tài khoản user (Admin only)

**PUT** `/api/users/{userId}/verify`

**Headers:**

```
Authorization: Bearer <ADMIN_JWT_TOKEN>
```

**Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "user1",
  "citizenId": "123456789012",
  "driverLicense": "B2123456789",
  "isVerified": true,
  "roles": ["USER"],
  "createdAt": "2024-11-03T10:00:00",
  "updatedAt": "2024-11-03T10:40:00"
}
```

## Validation Rules

### Số căn cước công dân (`citizenId`)

- Tối đa 20 ký tự
- Chỉ được chứa số (0-9)
- Có thể để trống

### Số bằng lái xe (`driverLicense`)

- Tối đa 20 ký tự
- Chỉ được chứa chữ cái và số (A-Z, a-z, 0-9)
- Có thể để trống

### Trạng thái xác minh (`isVerified`)

- Mặc định là `false` khi tạo tài khoản
- Chỉ admin mới có quyền set thành `true`
- Không thể tự cập nhật trường này qua API profile

## Error Responses

### Validation Error (400 Bad Request)

```json
{
  "timestamp": "2024-11-03T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Dữ liệu đầu vào không hợp lệ",
  "fieldErrors": {
    "citizenId": "Số căn cước công dân chỉ được chứa số",
    "driverLicense": "Số bằng lái xe không được vượt quá 20 ký tự"
  }
}
```

### User Not Found (400 Bad Request)

```json
{
  "timestamp": "2024-11-03T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Không tìm thấy user với ID: 550e8400-e29b-41d4-a716-446655440000"
}
```

### Unauthorized (403 Forbidden)

```json
{
  "timestamp": "2024-11-03T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "User chưa đăng nhập"
}
```

## Testing với cURL

### 1. Đăng nhập để lấy JWT token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'
```

### 2. Lấy thông tin profile

```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Cập nhật thông tin profile

```bash
curl -X PUT http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "citizenId": "123456789012",
    "driverLicense": "B2123456789"
  }'
```

### 4. Xác minh user (Admin)

```bash
curl -X PUT http://localhost:8080/api/users/USER_UUID/verify \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## Database Migration

Khi chạy ứng dụng, Hibernate sẽ tự động tạo các cột mới:

```sql
-- Các cột sẽ được thêm vào bảng users
ALTER TABLE users ADD COLUMN citizen_id VARCHAR(20);
ALTER TABLE users ADD COLUMN driver_license VARCHAR(20);
ALTER TABLE users ADD COLUMN is_verified BOOLEAN NOT NULL DEFAULT false;

-- Indexes để tối ưu hiệu suất (tùy chọn)
CREATE INDEX idx_users_citizen_id ON users(citizen_id);
CREATE INDEX idx_users_driver_license ON users(driver_license);
CREATE INDEX idx_users_is_verified ON users(is_verified);
```

## Use Cases

1. **User tự cập nhật thông tin**: User có thể cập nhật số CCCD và bằng lái để chuẩn bị cho việc xác minh.

2. **Admin xác minh tài khoản**: Admin kiểm tra thông tin và xác minh tài khoản user.

3. **Hệ thống kiểm tra trạng thái xác minh**: Các tính năng khác có thể kiểm tra `isVerified` để quyết định cho phép user sử dụng hay không.

4. **Báo cáo và thống kê**: Có thể thống kê số lượng user đã xác minh vs chưa xác minh.

## Lưu ý bảo mật

1. **Không trả về password**: DTO response không bao gồm password.
2. **JWT Authentication**: Tất cả endpoints đều yêu cầu JWT token hợp lệ.
3. **User isolation**: User chỉ có thể xem/cập nhật profile của chính mình.
4. **Admin verification**: Chỉ admin mới có quyền xác minh tài khoản.

# Group Management API Documentation

## Mô tả

Tính năng quản lý nhóm (Group Management) cho phép người dùng tạo nhóm, thêm/xóa thành viên với hệ thống phân quyền ADMIN/MEMBER.

## Kiến trúc thiết kế

### Entities:

1. **Group** (`groups` table):

   - `id` (Long) - Primary key
   - `name` (String) - Tên nhóm (unique)
   - `description` (String) - Mô tả nhóm
   - `created_at`, `updated_at` - Timestamps

2. **GroupMembership** (`group_memberships` table) - Entity trung gian:

   - `id` (Long) - Primary key
   - `user_id` (UUID) - Foreign key tới User
   - `group_id` (Long) - Foreign key tới Group
   - `role` (ENUM) - ADMIN hoặc MEMBER
   - `joined_at` (LocalDateTime) - Thời điểm tham gia (@PrePersist)

3. **GroupRole** (Enum):
   - `ADMIN` - Trưởng nhóm (có quyền thêm/xóa thành viên)
   - `MEMBER` - Thành viên (chỉ có quyền xem)

### Repositories:

- **GroupRepository**: JPA Repository cho Group
- **GroupMembershipRepository**: JPA Repository cho GroupMembership với method đặc biệt:
  - `findByGroup_IdAndUser_Id(Long groupId, UUID userId)` - Tìm membership theo groupId và userId

## API Endpoints

### Base URL: `/api/groups`

### 1. Tạo nhóm mới

**POST** `/api/groups`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**

```json
{
  "name": "Nhóm xe điện VinFast",
  "description": "Nhóm chia sẻ chi phí xe điện VinFast VF8"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "name": "Nhóm xe điện VinFast",
  "description": "Nhóm chia sẻ chi phí xe điện VinFast VF8",
  "createdAt": "2024-11-03T10:00:00",
  "updatedAt": "2024-11-03T10:00:00",
  "memberCount": 1,
  "members": [
    {
      "membershipId": 1,
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "username": "admin",
      "role": "ADMIN",
      "joinedAt": "2024-11-03T10:00:00"
    }
  ]
}
```

### 2. Thêm thành viên vào nhóm

**POST** `/api/groups/{groupId}/members`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**

```json
{
  "userId": "650e8400-e29b-41d4-a716-446655440001"
}
```

**Response (201 Created):**

```json
{
  "membershipId": 2,
  "userId": "650e8400-e29b-41d4-a716-446655440001",
  "username": "user1",
  "role": "MEMBER",
  "joinedAt": "2024-11-03T10:05:00"
}
```

### 3. Xóa thành viên khỏi nhóm

**DELETE** `/api/groups/{groupId}/members/{memberId}`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (204 No Content)**

### 4. Lấy thông tin chi tiết nhóm

**GET** `/api/groups/{groupId}`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**

```json
{
  "id": 1,
  "name": "Nhóm xe điện VinFast",
  "description": "Nhóm chia sẻ chi phí xe điện VinFast VF8",
  "createdAt": "2024-11-03T10:00:00",
  "updatedAt": "2024-11-03T10:00:00",
  "memberCount": 2,
  "members": [
    {
      "membershipId": 1,
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "username": "admin",
      "role": "ADMIN",
      "joinedAt": "2024-11-03T10:00:00"
    },
    {
      "membershipId": 2,
      "userId": "650e8400-e29b-41d4-a716-446655440001",
      "username": "user1",
      "role": "MEMBER",
      "joinedAt": "2024-11-03T10:05:00"
    }
  ]
}
```

### 5. Lấy danh sách nhóm của user hiện tại

**GET** `/api/groups/my-groups`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "name": "Nhóm xe điện VinFast",
    "description": "Nhóm chia sẻ chi phí xe điện VinFast VF8",
    "createdAt": "2024-11-03T10:00:00",
    "updatedAt": "2024-11-03T10:00:00",
    "memberCount": 2,
    "members": [...]
  }
]
```

## Business Logic

### Security & Permissions

1. **Tạo nhóm**: Bất kỳ user đã đăng nhập nào cũng có thể tạo nhóm và tự động trở thành ADMIN.

2. **Thêm thành viên**: Chỉ ADMIN mới có quyền thêm thành viên. Thành viên mới sẽ có role MEMBER.

3. **Xóa thành viên**: Chỉ ADMIN mới có quyền xóa thành viên. ADMIN không thể tự xóa mình.

4. **Authentication**: Tất cả endpoints đều yêu cầu JWT token hợp lệ.

### Validation Rules

- **Tên nhóm**: Bắt buộc, tối đa 255 ký tự, không trùng lặp (case-insensitive)
- **Mô tả**: Tùy chọn, tối đa 1000 ký tự
- **User ID**: Bắt buộc khi thêm thành viên, phải tồn tại trong hệ thống

### Key Method: `checkAdminPermission`

```java
private void checkAdminPermission(Long groupId, UUID userId) {
    GroupMembership membership = membershipRepository
            .findByGroup_IdAndUser_Id(groupId, userId)
            .orElseThrow(() -> new SecurityException("Bạn không phải là thành viên của nhóm này"));

    if (membership.getRole() != GroupRole.ADMIN) {
        throw new SecurityException("Bạn không có quyền admin trong nhóm này");
    }
}
```

## Error Handling

### HTTP Status Codes

- **400 Bad Request**: Dữ liệu không hợp lệ, validation error
- **403 Forbidden**: Không có quyền thực hiện hành động
- **404 Not Found**: Không tìm thấy resource
- **500 Internal Server Error**: Lỗi hệ thống

### Error Response Format

```json
{
  "timestamp": "2024-11-03T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Bạn không có quyền admin trong nhóm này"
}
```

### Validation Error Response

```json
{
  "timestamp": "2024-11-03T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Dữ liệu đầu vào không hợp lệ",
  "fieldErrors": {
    "name": "Tên nhóm không được để trống",
    "description": "Mô tả không được vượt quá 1000 ký tự"
  }
}
```

## Testing với cURL

### 1. Đăng nhập để lấy JWT token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'
```

### 2. Tạo nhóm

```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nhóm xe điện VinFast",
    "description": "Nhóm chia sẻ chi phí xe điện"
  }'
```

### 3. Thêm thành viên

```bash
curl -X POST http://localhost:8080/api/groups/1/members \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER_UUID_TO_ADD"
  }'
```

### 4. Lấy thông tin nhóm

```bash
curl -X GET http://localhost:8080/api/groups/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Xóa thành viên

```bash
curl -X DELETE http://localhost:8080/api/groups/1/members/USER_UUID_TO_REMOVE \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Database Schema

```sql
-- Bảng groups
CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Bảng group_memberships (entity trung gian)
CREATE TABLE group_memberships (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id BIGINT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MEMBER')),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, group_id)
);

-- Indexes for performance
CREATE INDEX idx_group_memberships_user_id ON group_memberships(user_id);
CREATE INDEX idx_group_memberships_group_id ON group_memberships(group_id);
CREATE INDEX idx_group_memberships_role ON group_memberships(role);
```

## Lưu ý quan trọng

1. **Entity trung gian**: GroupMembership là mấu chốt của thiết kế, cho phép quản lý quan hệ Many-to-Many phức tạp với thông tin bổ sung (role, joinedAt).

2. **Security**: User ID được lấy từ JWT token đã xác thực, không tin tưởng vào request body.

3. **Transactions**: Sử dụng `@Transactional` để đảm bảo tính nhất quán dữ liệu.

4. **JSON Serialization**: Sử dụng `@JsonIgnore` để tránh infinite loop khi serialize entity relationships.

5. **Repository Query Methods**: Sử dụng Spring Data JPA naming convention để tự động generate queries.

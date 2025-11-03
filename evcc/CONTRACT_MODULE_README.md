# MODULE HỢP ĐỒNG MUA XE (CONTRACT MODULE)

## Tổng quan
Module hợp đồng mua xe cho phép các nhóm tạo và quản lý hợp đồng mua xe điện, trong đó các thành viên của nhóm có thể góp tiền với tỉ lệ sở hữu khác nhau. Tỉ lệ sở hữu này sẽ quyết định quyền ưu tiên sử dụng xe của mỗi thành viên.

## Tính năng chính

### 1. Quản lý hợp đồng
- **Tạo hợp đồng mới**: Nhóm có thể tạo hợp đồng mua xe với thông tin chi tiết
- **Cập nhật hợp đồng**: Sửa đổi thông tin hợp đồng (chỉ khi ở trạng thái phù hợp)
- **Quản lý trạng thái**: DRAFT → PENDING → ACTIVE → COMPLETED
- **Gán xe**: Liên kết hợp đồng với xe cụ thể
- **Xóa hợp đồng**: Chỉ cho phép xóa khi ở trạng thái DRAFT

### 2. Quản lý quyền sở hữu
- **Tỉ lệ sở hữu**: Mỗi thành viên có tỉ lệ sở hữu từ 0.01% đến 100%
- **Đóng góp tài chính**: Theo dõi số tiền mỗi thành viên đã đóng góp
- **Trạng thái thanh toán**: PENDING, PARTIAL, COMPLETED
- **Quyền sử dụng**: Dựa trên tỉ lệ sở hữu và trạng thái thanh toán

### 3. Hệ thống ưu tiên sử dụng xe
- **Ưu tiên dựa trên tỉ lệ sở hữu**: Thành viên có tỉ lệ cao hơn được ưu tiên
- **Điểm ưu tiên**: Tính toán từ 0-100 dựa trên tỉ lệ sở hữu
- **Danh sách ưu tiên**: API trả về danh sách sắp xếp theo độ ưu tiên

## Cấu trúc dữ liệu

### Contract (Hợp đồng)
```sql
CREATE TABLE contracts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    contract_number VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    group_id BIGINT NOT NULL,
    vehicle_id BIGINT,
    agreed_price DECIMAL(19,2) NOT NULL,
    signing_date DATE,
    effective_date DATE,
    expiry_date DATE,
    status VARCHAR(20) NOT NULL,
    terms_and_conditions TEXT,
    notes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);
```

### ContractOwnership (Quyền sở hữu)
```sql
CREATE TABLE contract_ownerships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    contract_id BIGINT NOT NULL,
    user_id UUID NOT NULL,
    ownership_percentage DECIMAL(5,2) NOT NULL,
    contribution_amount DECIMAL(19,2) NOT NULL,
    contribution_date DATETIME,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    usage_eligible BOOLEAN DEFAULT TRUE,
    notes TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (contract_id) REFERENCES contracts(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_contract_user (contract_id, user_id)
);
```

## API Endpoints

### Quản lý hợp đồng
- `POST /api/contracts` - Tạo hợp đồng mới
- `PUT /api/contracts/{id}` - Cập nhật hợp đồng
- `GET /api/contracts/{id}` - Xem chi tiết hợp đồng
- `GET /api/contracts/by-number/{contractNumber}` - Tìm hợp đồng theo mã
- `DELETE /api/contracts/{id}` - Xóa hợp đồng (ADMIN)

### Danh sách và tìm kiếm
- `GET /api/contracts/group/{groupId}` - Hợp đồng của nhóm (có phân trang)
- `GET /api/contracts/user/{userId}` - Hợp đồng của user (có phân trang)
- `GET /api/contracts/search?title={title}` - Tìm kiếm theo tiêu đề

### Quản lý trạng thái và xe
- `PUT /api/contracts/{id}/status?status={status}` - Thay đổi trạng thái (ADMIN)
- `PUT /api/contracts/{id}/vehicle?vehicleId={vehicleId}` - Gán xe

### Ưu tiên sử dụng xe
- `GET /api/contracts/{id}/usage-priority` - Danh sách ưu tiên sử dụng
- `GET /api/contracts/{id}/can-use-vehicle?userId={userId}` - Kiểm tra quyền sử dụng
- `GET /api/contracts/{id}/user-priority?userId={userId}` - Tính điểm ưu tiên

### Thống kê và validation
- `GET /api/contracts/{id}/validate` - Kiểm tra tính hợp lệ
- `GET /api/contracts/{id}/contribution-percentage` - Tỉ lệ đóng góp đã hoàn thành

## Business Logic

### Quy tắc hợp đồng
1. **Tổng tỉ lệ sở hữu phải = 100%**
2. **Một nhóm có thể có nhiều hợp đồng nhưng chỉ một hợp đồng ACTIVE tại một thời điểm**
3. **Chỉ có thể xóa hợp đồng ở trạng thái DRAFT**
4. **Hợp đồng phải có xe được gán mới có thể chuyển sang ACTIVE**

### Quy tắc chuyển đổi trạng thái
- DRAFT → PENDING, CANCELLED
- PENDING → ACTIVE, CANCELLED  
- ACTIVE → COMPLETED
- COMPLETED/CANCELLED → Không thể thay đổi

### Tính toán ưu tiên sử dụng xe
1. **Điểm cơ bản**: Bằng tỉ lệ sở hữu (%)
2. **Điều kiện tham gia**: 
   - `usageEligible = true`
   - `paymentStatus != 'PENDING'` hoặc có đóng góp > 0
3. **Sắp xếp**: Theo tỉ lệ sở hữu giảm dần, sau đó theo thời gian đóng góp

## Ví dụ sử dụng

### Tạo hợp đồng mới
```json
POST /api/contracts
{
    "title": "Hợp đồng mua xe Tesla Model 3",
    "description": "Mua xe điện Tesla Model 3 cho nhóm ABC",
    "groupId": 1,
    "agreedPrice": 1500000000,
    "signingDate": "2024-01-15",
    "effectiveDate": "2024-02-01",
    "ownerships": [
        {
            "userId": "uuid-user-1",
            "ownershipPercentage": 40.00,
            "contributionAmount": 600000000,
            "notes": "Góp 40% - 600 triệu"
        },
        {
            "userId": "uuid-user-2", 
            "ownershipPercentage": 35.00,
            "contributionAmount": 525000000,
            "notes": "Góp 35% - 525 triệu"
        },
        {
            "userId": "uuid-user-3",
            "ownershipPercentage": 25.00,
            "contributionAmount": 375000000,
            "notes": "Góp 25% - 375 triệu"
        }
    ]
}
```

### Lấy danh sách ưu tiên sử dụng
```json
GET /api/contracts/1/usage-priority

Response:
[
    {
        "id": 1,
        "user": {
            "id": "uuid-user-1",
            "username": "user1"
        },
        "ownershipPercentage": 40.00,
        "paymentStatus": "COMPLETED",
        "usageEligible": true
    },
    {
        "id": 2,
        "user": {
            "id": "uuid-user-2", 
            "username": "user2"
        },
        "ownershipPercentage": 35.00,
        "paymentStatus": "COMPLETED",
        "usageEligible": true
    }
]
```

## Tích hợp với các module khác

### Với Group Module
- Hợp đồng thuộc về một nhóm
- Chỉ thành viên nhóm mới có thể tham gia hợp đồng

### Với Vehicle Module  
- Mỗi hợp đồng có thể liên kết với một xe
- Xe chỉ có thể thuộc về một hợp đồng ACTIVE

### Với User Module
- Theo dõi quyền sở hữu và đóng góp của từng user
- Tính toán quyền ưu tiên sử dụng xe

### Với Booking Module (tương lai)
- Sử dụng thông tin ưu tiên từ hợp đồng để xếp hạng đặt lịch
- Kiểm tra quyền đặt lịch dựa trên `usageEligible`

## Security

### Quyền truy cập
- **USER**: Có thể xem, tạo và cập nhật hợp đồng của nhóm mình
- **ADMIN**: Có thể thay đổi trạng thái và xóa hợp đồng

### Validation
- Kiểm tra quyền thành viên nhóm khi tạo/sửa hợp đồng
- Validate tổng tỉ lệ sở hữu = 100%
- Kiểm tra trạng thái hợp lệ khi chuyển đổi

## Mở rộng tương lai

1. **Lịch sử sử dụng xe**: Tích hợp với module booking để theo dõi usage history
2. **Đánh giá hiệu suất**: Thêm metrics về việc sử dụng xe của từng thành viên  
3. **Hợp đồng phụ**: Cho phép chuyển nhượng quyền sở hữu
4. **Thông báo**: Tự động thông báo khi có thay đổi trong hợp đồng
5. **Báo cáo tài chính**: Thống kê đóng góp và chi phí vận hành
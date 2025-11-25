# DEMO GUIDE - Hệ thống Contract với Vehicle và Voting

## Tổng quan

Hệ thống contract đã được hoàn thiện với các tính năng:

- Tạo hợp đồng mua xe cho nhóm
- Chọn xe điện khi tạo hợp đồng
- Hệ thống voting để các thành viên chấp nhận/từ chối hợp đồng
- Quản lý ownership (quyền sở hữu) của các thành viên

## Luồng Demo

### 1. Tạo hợp đồng mới

**Đường dẫn:** `/contracts`

**Các bước:**

1. Điền thông tin hợp đồng:

   - **Tiêu đề**: Ví dụ "Hợp đồng mua Tesla Model 3"
   - **Nhóm**: Chọn nhóm từ dropdown
   - **Xe điện**: Chọn xe từ danh sách có sẵn (Tesla Model 3, VinFast VF8, BYD Tang)
   - **Giá thỏa thuận**: Ví dụ 1200000000 (VND)

2. Điền quyền sở hữu (ít nhất 2 thành viên):

   - **User ID**: Sử dụng UUID demo:
     - `11111111-1111-1111-1111-111111111111` (user1@example.com)
     - `22222222-2222-2222-2222-222222222222` (user2@example.com)
     - `33333333-3333-3333-3333-333333333333` (user3@example.com)
   - **Tỉ lệ %**: Tổng phải bằng 100% (ví dụ: 50%, 30%, 20%)
   - **Đóng góp**: Số tiền đóng góp của từng thành viên

3. Nhấn "Tạo hợp đồng" → Hệ thống tự động tạo voting session

### 2. Xem trạng thái voting

**Đường dẫn:** `/contracts/voting`

- Hiển thị các hợp đồng đang chờ bỏ phiếu
- Thống kê số vote chấp nhận/từ chối
- Tiến độ voting (cần 60% thành viên chấp nhận)

### 3. Bỏ phiếu cho hợp đồng

**Đường dẫn:** `/contracts/{id}`

**Các bước:**

1. Xem chi tiết hợp đồng
2. Kiểm tra thông tin xe điện
3. Xem danh sách quyền sở hữu
4. Bỏ phiếu:
   - **Chấp nhận**: Đồng ý với hợp đồng
   - **Từ chối**: Không đồng ý
   - **Lý do**: Ghi chú tùy chọn

### 4. Theo dõi kết quả

- Hệ thống tự động cập nhật trạng thái khi đủ votes
- **APPROVED**: Khi đạt đủ 60% vote chấp nhận
- **REJECTED**: Khi không thể đạt đủ vote cần thiết
- **PENDING_VOTES**: Đang chờ vote

## Dữ liệu Demo

### Xe điện có sẵn:

1. **Tesla Model 3** (ID: 1)

   - Biển số: 30A-12345
   - Giá: 1,200,000,000 VND

2. **VinFast VF8** (ID: 2)

   - Biển số: 30A-67890
   - Giá: 1,500,000,000 VND

3. **BYD Tang** (ID: 3)
   - Biển số: 30A-11111
   - Giá: 1,000,000,000 VND

### User ID để test:

- `11111111-1111-1111-1111-111111111111` → user1@example.com
- `22222222-2222-2222-2222-222222222222` → user2@example.com
- `33333333-3333-3333-3333-333333333333` → user3@example.com
- `44444444-4444-4444-4444-444444444444` → user4@example.com

## Tính năng chính

### 1. Contract Management

- ✅ Tạo hợp đồng với vehicle
- ✅ Quản lý ownership
- ✅ Trạng thái hợp đồng (DRAFT, PENDING_VOTES, APPROVED, REJECTED)

### 2. Vehicle Integration

- ✅ Chọn xe khi tạo hợp đồng
- ✅ Hiển thị thông tin xe trong contract detail
- ✅ Kiểm tra xe đã được sử dụng

### 3. Voting System

- ✅ Tự động tạo voting session khi tạo contract
- ✅ Bỏ phiếu chấp nhận/từ chối
- ✅ Theo dõi tiến độ voting
- ✅ Tự động kết thúc khi đạt kết quả

### 4. User Interface

- ✅ Form tạo hợp đồng với dropdown chọn xe
- ✅ Trang danh sách phiếu bầu chờ xử lý
- ✅ Chi tiết hợp đồng với thông tin xe và voting
- ✅ Navigation menu với link hợp đồng

## Cách test đầy đủ

1. **Login** vào hệ thống với tài khoản bất kỳ
2. **Tạo hợp đồng** tại `/contracts`
3. **Kiểm tra voting** tại `/contracts/voting`
4. **Vote cho hợp đồng** tại `/contracts/{id}`
5. **Xem kết quả** sau khi đủ votes

## Lưu ý

- Dữ liệu được lưu in-memory, sẽ mất khi restart ứng dụng
- Hệ thống đã có sẵn 3 xe demo và 4 user demo
- Voting cần 60% thành viên chấp nhận để approve
- Mỗi xe chỉ có thể được sử dụng trong 1 hợp đồng

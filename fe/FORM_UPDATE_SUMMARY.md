# Cập nhật Form Contract - Hiển thị thành viên group

## Những thay đổi đã thực hiện:

### 1. ✅ Cập nhật Controller

**File:** `ContractController.java`

**Thay đổi:**

- Thêm `Map<UUID, String> demoUsers` vào model để hiển thị danh sách thành viên
- Thêm parameter `selectedUsers` trong method `createContract()`
- Cập nhật logic xử lý ownership để chỉ xử lý user được chọn qua checkbox
- Import thêm `Map` class

### 2. ✅ Cập nhật Template

**File:** `contracts/list.html`

**Thay đổi:**

- Thay đổi bảng "Quyền sở hữu" từ input UUID thành danh sách thành viên với checkbox
- Mỗi thành viên hiển thị:
  - ✅ Checkbox để chọn thành viên
  - ✅ Tên email của thành viên
  - ✅ UUID (hiển thị nhỏ phía dưới)
  - ✅ Input tỉ lệ %, đóng góp, ghi chú (disabled khi chưa chọn)

### 3. ✅ Thêm JavaScript xử lý dynamic

**Tính năng JavaScript:**

- ✅ Enable/disable input fields khi check/uncheck thành viên
- ✅ Tính toán real-time tổng tỉ lệ sở hữu
- ✅ Hiển thị indicator màu: xanh (100%), đỏ (>100%), vàng (<100%)
- ✅ Validation form khi submit:
  - Phải chọn ít nhất 1 thành viên
  - Tỉ lệ sở hữu không được để trống
  - Tổng tỉ lệ phải bằng đúng 100%
- ✅ Visual feedback: highlight hàng được chọn

### 4. ✅ Cải thiện UX

- ✅ Hiển thị tên thành viên rõ ràng thay vì chỉ UUID
- ✅ Form validation thân thiện với người dùng
- ✅ Real-time feedback khi nhập liệu
- ✅ Không cần nhập UUID thủ công nữa

## Luồng sử dụng mới:

### Trước đây:

1. ❌ Phải nhập UUID thủ công của từng thành viên
2. ❌ Dễ nhập sai UUID
3. ❌ Không biết UUID nào tương ứng với user nào

### Bây giờ:

1. ✅ Chọn checkbox của thành viên muốn thêm vào hợp đồng
2. ✅ Điền tỉ lệ sở hữu và đóng góp cho từng thành viên được chọn
3. ✅ Hệ thống tự động kiểm tra tổng = 100%
4. ✅ Submit form để tạo hợp đồng

## Demo flow hoàn chỉnh:

### 1. Tạo hợp đồng mới:

- Vào `/contracts`
- Chọn nhóm, xe điện, điền giá
- **Chọn thành viên** từ danh sách (user1, user2, user3, user4)
- **Điền tỉ lệ sở hữu** cho từng thành viên được chọn
- Xem real-time tổng tỉ lệ = 100% ✅
- Submit → Tạo hợp đồng + voting session

### 2. Thành viên vote:

- Vào `/contracts/voting` → Xem phiếu bầu chờ xử lý
- Vào `/contracts/{id}` → Vote chấp nhận/từ chối
- Hệ thống tự động approve khi đủ 60% vote

## Dữ liệu demo có sẵn:

```
user1@example.com (ID: 11111111-1111-1111-1111-111111111111)
user2@example.com (ID: 22222222-2222-2222-2222-222222222222)
user3@example.com (ID: 33333333-3333-3333-3333-333333333333)
user4@example.com (ID: 44444444-4444-4444-4444-444444444444)
```

Form hiện tại đã user-friendly và không cần nhập UUID thủ công nữa! 🎉

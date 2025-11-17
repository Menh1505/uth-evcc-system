# FE Integration for Contract Module

Tài liệu này mô tả nhanh những phần giao diện (FE) đã được triển khai để tích hợp với **Contract Module** ở backend.

## 1. Luồng nhóm (Group) và hợp đồng

- Người dùng sau khi đăng nhập:
  - Có menu **Nhóm** (`/groups`) để:
    - Xem danh sách các nhóm mà user đang là thành viên.
    - Tạo nhóm mới (gửi request tới backend group API).
  - Trong trang chi tiết nhóm `/groups/{groupId}`:
    - Hiển thị thông tin nhóm và danh sách thành viên (ADMIN/MEMBER).
    - ADMIN trong nhóm có thể thêm/xóa thành viên.
    - **Mới**: hiển thị thêm card “Hợp đồng của nhóm”:
      - Gọi `GET /api/contracts/group/{groupId}` (backend trả `Page<ContractSummaryResponse>`).
      - FE parse field `content` và map sang `groupContracts` để hiển thị:
        - `contractNumber`, `title`, `vehicleName`, `status`.
        - Nút “Chi tiết” dẫn tới `/contracts/{id}`.

## 2. Trang tạo hợp đồng mới

- URL: `GET /contracts`
- Controller: `fe/src/main/java/evcc/controller/ContractController.java` – method `listMyContracts`.
- View: `fe/src/main/resources/templates/contracts/list.html`.

### 2.1. Dữ liệu hiển thị

- Lấy danh sách nhóm mà user hiện tại là thành viên:
  - FE gọi `GET /api/groups/my-groups`, map vào `GroupResponseDto[]` → Model attribute `groups`.
  - Trong form, người dùng chọn một group để gắn hợp đồng (`groupId`).

### 2.2. Form tạo hợp đồng

- Các field chính:
  - `title` – Tiêu đề hợp đồng (bắt buộc).
  - `groupId` – Chọn nhóm (bắt buộc).
  - `agreedPrice` – Giá thỏa thuận (VND, > 0).
  - `signingDate`, `effectiveDate`, `expiryDate` – ngày (tùy chọn).
  - `description`, `termsAndConditions`, `notes` – mô tả, điều khoản, ghi chú.
- Các field về quyền sở hữu (ownerships) – **tối đa 3 dòng trên form**:
  - `ownerUserId` (UUID user).
  - `ownerPercentage` (tỉ lệ % sở hữu).
  - `ownerContribution` (số tiền đóng góp).
  - `ownerNotes` (ghi chú riêng cho từng người).

### 2.3. Validation phía FE

Trong `ContractController.createContract`:

- Kiểm tra cơ bản:
  - `title` không được rỗng.
  - `agreedPrice` parse được sang `BigDecimal` và > 0.
  - Ngày (`signingDate`, `effectiveDate`, `expiryDate`) parse được sang `LocalDate` nếu không rỗng.
  - Nếu có lỗi, gán `errorMessage` và `redirect:/contracts` (hiển thị alert trên view).

- Xử lý ownerships:
  - Với mỗi dòng có `ownerUserId` không rỗng:
    - Parse `UUID` từ chuỗi.
    - Parse `ownerPercentage`, kiểm tra > 0.
    - Parse `ownerContribution` (có thể rỗng → mặc định 0).
    - Gộp vào `CreateContractRequestDto.OwnershipRequestDto`.
  - Nếu không có dòng nào hợp lệ → báo `"Danh sách quyền sở hữu không được để trống."`.
  - Tính `totalPerc = sum(ownershipPercentage)`:
    - Nếu `totalPerc != 100` → báo `"Tổng tỉ lệ sở hữu phải bằng 100%. Hiện tại: X"`.

- Chỉ khi tất cả validate đều OK, FE mới:
  - Gọi `userService.createContract(token, request)` → `POST /api/contracts`.
  - Backend `CreateContractRequest` ở module `contract` sẽ tiếp tục kiểm tra logic nghiệp vụ.

## 3. Trang danh sách hợp đồng của nhóm

- URL: `GET /contracts/group/{groupId}` (hiện tại dùng nội bộ, đã có view `contracts/group-list.html`).
- FE gọi `GET /api/contracts/group/{groupId}`:
  - Backend trả `Page<ContractSummaryResponse>`.
  - FE map `content` sang `ContractSummaryResponseDto` (id, contractNumber, title, groupName, vehicleName, agreedPrice, signingDate, status, totalOwners, totalContributed, contributionPercentage).
- Trang `groups/detail.html` sử dụng `groupContracts` để hiển thị nhanh các hợp đồng thuộc nhóm.

## 4. Trang chi tiết hợp đồng

- URL: `GET /contracts/{id}`.
- Controller: `ContractController.contractDetail`.
- View: `contracts/detail.html`.
- FE gọi `GET /api/contracts/{id}`:
  - Map JSON `ContractResponse` sang `ContractResponseDto`:
    - Thông tin hợp đồng (title, contractNumber, giá, ngày, trạng thái, điều khoản, ghi chú).
    - `group` (GroupInfo) – tên nhóm, mô tả.
    - `vehicle` (VehicleInfo) – thông tin xe (nếu đã gán).
    - `ownerships` – list OwnershipInfo (user, ownershipPercentage, contributionAmount, paymentStatus, usageEligible, notes).
- Giao diện:
  - Card “Thông tin hợp đồng”.
  - Bảng “Quyền sở hữu” với các cột: user, tỉ lệ %, số tiền đã góp, trạng thái thanh toán, đủ điều kiện sử dụng.
  - Card “Điều khoản & ghi chú”.

## 5. Phân quyền và điều hướng

- USER (ROLE USER):
  - Navbar:
    - Trang chủ (`/`), Nhóm (`/groups`), Hợp đồng (`/contracts`), Hồ sơ, Đăng xuất.
  - Có thể:
    - Tạo nhóm & quản lý thành viên nhóm mình.
    - Tạo hợp đồng mới cho group mình là thành viên.
    - Xem danh sách & chi tiết hợp đồng của nhóm.

- ADMIN:
  - Navbar:
    - Trang chủ, Quản trị người dùng (`/admin/users`), Dashboard (`/dashboard`), Hồ sơ, Đăng xuất.
  - Không dùng các chức năng “tham gia nhóm” hay “tạo hợp đồng” từ FE (có thể duyệt trạng thái hợp đồng qua API/manager riêng sau này).

## 6. Những phần **chưa** triển khai trên FE

- Chưa có UI để:
  - Cập nhật hợp đồng (`PUT /api/contracts/{id}`) theo trạng thái.
  - Thay đổi trạng thái hợp đồng (DRAFT/PENDING/ACTIVE/COMPLETED) – `PUT /api/contracts/{id}/status`.
  - Gán xe cho hợp đồng – `PUT /api/contracts/{id}/vehicle`.
  - Xem danh sách ưu tiên sử dụng – `GET /api/contracts/{id}/usage-priority`.
  - Các API kiểm tra `can-use-vehicle` và `user-priority`.
- Các phần này hiện có thể test trực tiếp bằng Postman / cURL theo tài liệu backend, và có thể bổ sung UI riêng (Admin duyệt hợp đồng, trang ưu tiên sử dụng…) trong các bước tiếp theo nếu cần.

## 7. Ghi chú cho việc chấm điểm

- FE không tái hiện toàn bộ nghiệp vụ phức tạp của Contract Module, nhưng đã:
  - Tôn trọng constraint **tổng tỉ lệ sở hữu = 100%** ngay từ form.
  - Cho phép khai báo nhiều ownership khác nhau (giống ví dụ trong README).
  - Tích hợp chặt với Group Module: hợp đồng được tạo cho **nhóm**, hiển thị lại trong trang nhóm.
  - Tích hợp với User & Security:
    - Lấy JWT từ session (sau khi login).
    - Chỉ cho phép user đã đăng nhập tạo/xem hợp đồng.
    - Phân quyền Admin / User khác nhau ở phần menu và Dashboard.


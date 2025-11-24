package com.evcc.vehicle.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.group.entity.Group;
import com.evcc.group.entity.GroupMembership;
import com.evcc.group.enums.GroupRole;
import com.evcc.group.repository.GroupRepository;
import com.evcc.group.repository.GroupMembershipRepository;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;
import com.evcc.vehicle.dto.VehicleCreateRequest;
import com.evcc.vehicle.dto.VehicleResponse;
import com.evcc.vehicle.dto.VehiclePurchaseProposalRequest;
import com.evcc.vehicle.entity.Vehicle;
import com.evcc.vehicle.enums.VehicleStatus;
import com.evcc.vehicle.repository.VehicleRepository;
import com.evcc.voting.dto.request.CreateVoteRequest;
import com.evcc.voting.dto.response.VoteResponse;
import com.evcc.voting.enums.VoteType;
import com.evcc.voting.service.VotingService;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final UserRepository userRepository;
    private final VotingService votingService;

    public VehicleService(VehicleRepository vehicleRepository,
            GroupRepository groupRepository,
            GroupMembershipRepository groupMembershipRepository,
            UserRepository userRepository,
            VotingService votingService) {
        this.vehicleRepository = vehicleRepository;
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.userRepository = userRepository;
        this.votingService = votingService;
    }

    public VehicleResponse createVehicle(VehicleCreateRequest req) {
        if (vehicleRepository.existsByLicensePlate(req.getLicensePlate())) {
            throw new IllegalArgumentException("License plate already exists: " + req.getLicensePlate());
        }

        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + req.getGroupId()));

        Vehicle v = Vehicle.builder()
                .name(req.getName())
                .licensePlate(req.getLicensePlate())
                .make(req.getMake())
                .model(req.getModel())
                .year(req.getYear())
                .group(group)
                .purchasePrice(req.getPurchasePrice())
                .purchaseDate(req.getPurchaseDate())
                .batteryCapacity(req.getBatteryCapacity())
                .initialOdometer(req.getInitialOdometer())
                .status(parseStatus(req.getStatus()))
                .build();

        Vehicle saved = vehicleRepository.save(v);
        return toResponse(saved);
    }

    /**
     * Tạo proposal mua xe mới với voting (chỉ GROUP ADMIN)
     */
    public VoteResponse proposeVehiclePurchase(VehiclePurchaseProposalRequest req) {
        UUID currentUserId = getCurrentUserId();

        // 1. Kiểm tra user có phải GROUP ADMIN không
        checkGroupAdminPermission(req.getGroupId(), currentUserId);

        // 2. Validate vehicle data
        if (vehicleRepository.existsByLicensePlate(req.getLicensePlate())) {
            throw new IllegalArgumentException("Biển số xe đã tồn tại: " + req.getLicensePlate());
        }

        // 3. Tạo vote cho proposal
        CreateVoteRequest voteRequest = CreateVoteRequest.builder()
                .title("Đề xuất mua xe: " + req.getName())
                .description(buildVehiclePurchaseDescription(req))
                .voteType(VoteType.VEHICLE_PURCHASE)
                .groupId(req.getGroupId())
                .startTime(LocalDateTime.now().plusMinutes(5)) // Bắt đầu sau 5 phút
                .endTime(req.getVoteEndTime() != null ? req.getVoteEndTime() : LocalDateTime.now().plusDays(7))
                .minimumVotes(1)
                .requiredPercentage(req.getRequiredApprovalPercentage() != null ? req.getRequiredApprovalPercentage() : new BigDecimal("75.00"))
                .allowVoteChange(true)
                .anonymous(false)
                .relatedAmount(req.getPurchasePrice())
                .relatedEntityType("VEHICLE_PURCHASE")
                .notes("Proposal mua xe mới cho nhóm")
                .options(createVehiclePurchaseOptions())
                .build();

        return votingService.createVote(voteRequest, currentUserId);
    }

    /**
     * Kiểm tra user có phải GROUP ADMIN không
     */
    private void checkGroupAdminPermission(Long groupId, UUID userId) {
        Optional<GroupMembership> membership = groupMembershipRepository
                .findByGroup_IdAndUser_Id(groupId, userId);

        if (membership.isEmpty()) {
            throw new IllegalArgumentException("Bạn không phải thành viên của nhóm này");
        }

        if (membership.get().getRole() != GroupRole.ADMIN) {
            throw new IllegalArgumentException("Chỉ trưởng nhóm mới có thể đề xuất mua xe");
        }
    }

    /**
     * Lấy user ID hiện tại từ Spring Security
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User chưa đăng nhập");
        }

        String username = authentication.getName();
        try {
            return UUID.fromString(username);
        } catch (IllegalArgumentException e) {
            // Nếu username không phải UUID, cần lookup user
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + username));
            return user.getId();
        }
    }

    /**
     * Tạo mô tả chi tiết cho proposal mua xe
     */
    private String buildVehiclePurchaseDescription(VehiclePurchaseProposalRequest req) {
        StringBuilder desc = new StringBuilder();
        desc.append("Đề xuất mua xe mới cho nhóm:\n\n");
        desc.append("• Tên xe: ").append(req.getName()).append("\n");
        desc.append("• Biển số: ").append(req.getLicensePlate()).append("\n");
        desc.append("• Hãng/Dòng: ").append(req.getMake()).append(" ").append(req.getModel()).append("\n");
        desc.append("• Năm sản xuất: ").append(req.getYear()).append("\n");
        desc.append("• Giá mua: ").append(formatCurrency(req.getPurchasePrice())).append("\n");

        if (req.getBatteryCapacity() != null) {
            desc.append("• Dung lượng pin: ").append(req.getBatteryCapacity()).append(" kWh\n");
        }

        if (req.getDealerInfo() != null && !req.getDealerInfo().isEmpty()) {
            desc.append("• Đại lý/Người bán: ").append(req.getDealerInfo()).append("\n");
        }

        if (req.getProposalDescription() != null && !req.getProposalDescription().isEmpty()) {
            desc.append("\nLý do đề xuất:\n").append(req.getProposalDescription()).append("\n");
        }

        desc.append("\nNếu được phê duyệt, tất cả thành viên sẽ góp tiền theo tỷ lệ sở hữu để mua xe này.");

        return desc.toString();
    }

    /**
     * Tạo các lựa chọn vote mặc định cho vehicle purchase
     */
    private List<CreateVoteRequest.VoteOptionRequest> createVehiclePurchaseOptions() {
        return List.of(
                CreateVoteRequest.VoteOptionRequest.builder()
                        .optionText("Đồng ý mua xe")
                        .description("Đồng ý mua xe theo đề xuất và sẵn sàng đóng góp")
                        .displayOrder(1)
                        .build(),
                CreateVoteRequest.VoteOptionRequest.builder()
                        .optionText("Không đồng ý")
                        .description("Không đồng ý mua xe này")
                        .displayOrder(2)
                        .build(),
                CreateVoteRequest.VoteOptionRequest.builder()
                        .optionText("Cần thảo luận thêm")
                        .description("Cần thêm thông tin hoặc thảo luận trước khi quyết định")
                        .displayOrder(3)
                        .build()
        );
    }

    /**
     * Format currency cho hiển thị
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        return String.format("%,.0f VNĐ", amount);
    }

    public VehicleResponse getVehicleById(Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));
        return toResponse(v);
    }

    public List<VehicleResponse> listAll() {
        return vehicleRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<VehicleResponse> listByGroup(Long groupId) {
        Group g = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));
        return vehicleRepository.findByGroup(g).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<VehicleResponse> listAvailableVehicles() {
        return vehicleRepository.findAvailableVehicles().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Vehicle not found: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public VehicleResponse updateVehicle(Long id, VehicleCreateRequest req) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));

        if (req.getLicensePlate() != null && !req.getLicensePlate().equals(v.getLicensePlate())
                && vehicleRepository.existsByLicensePlate(req.getLicensePlate())) {
            throw new IllegalArgumentException("License plate already exists: " + req.getLicensePlate());
        }

        if (req.getGroupId() != null) {
            Group g = groupRepository.findById(req.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("Group not found: " + req.getGroupId()));
            v.setGroup(g);
        }

        // update fields if present
        if (req.getName() != null) {
            v.setName(req.getName());
        }
        if (req.getLicensePlate() != null) {
            v.setLicensePlate(req.getLicensePlate());
        }
        if (req.getMake() != null) {
            v.setMake(req.getMake());
        }
        if (req.getModel() != null) {
            v.setModel(req.getModel());
        }
        if (req.getYear() != null) {
            v.setYear(req.getYear());
        }
        if (req.getPurchasePrice() != null) {
            v.setPurchasePrice(req.getPurchasePrice());
        }
        if (req.getPurchaseDate() != null) {
            v.setPurchaseDate(req.getPurchaseDate());
        }
        if (req.getBatteryCapacity() != null) {
            v.setBatteryCapacity(req.getBatteryCapacity());
        }
        if (req.getInitialOdometer() != null) {
            v.setInitialOdometer(req.getInitialOdometer());
        }
        if (req.getStatus() != null) {
            v.setStatus(parseStatus(req.getStatus()));
        }

        Vehicle saved = vehicleRepository.save(v);
        return toResponse(saved);
    }

    private VehicleStatus parseStatus(String s) {
        if (s == null) {
            return VehicleStatus.AVAILABLE;
        }
        try {
            return VehicleStatus.valueOf(s.toUpperCase());
        } catch (Exception ex) {
            return VehicleStatus.AVAILABLE;
        }
    }

    private VehicleResponse toResponse(Vehicle v) {
        VehicleResponse r = new VehicleResponse();
        r.setId(v.getId());
        r.setName(v.getName());
        r.setLicensePlate(v.getLicensePlate());
        r.setMake(v.getMake());
        r.setModel(v.getModel());
        r.setYear(v.getYear());
        r.setGroupId(Optional.ofNullable(v.getGroup()).map(Group::getId).orElse(null));
        r.setPurchasePrice(v.getPurchasePrice());
        r.setPurchaseDate(v.getPurchaseDate());
        r.setBatteryCapacity(v.getBatteryCapacity());
        r.setInitialOdometer(v.getInitialOdometer());
        r.setStatus(v.getStatus());
        r.setCreatedAt(v.getCreatedAt());
        r.setUpdatedAt(v.getUpdatedAt());
        return r;
    }
}

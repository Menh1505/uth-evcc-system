package com.evcc.document.service;

import com.evcc.document.dto.ContractCreateRequest;
import com.evcc.document.dto.ParticipantInviteRequest;
import com.evcc.document.entity.Contract;
import com.evcc.document.entity.ContractParticipant;
import com.evcc.document.enums.ContractStatus;
import com.evcc.document.enums.ParticipantStatus;
import com.evcc.document.repository.ContractParticipantRepository;
import com.evcc.document.repository.ContractRepository;
import com.evcc.user.entity.User;
import com.evcc.user.service.UserService; // <-- thêm: load user theo userId
// import com.evcc.notification.service.NotificationService;
// import com.evcc.notification.enums.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Formatter;

@Service
@Transactional
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractParticipantRepository participantRepository;

    @Autowired
    private UserService userService;

    // @Autowired
    // private NotificationService notificationService;

    /**
     * 1) Người dùng tạo Hợp đồng (Bước 1: Soạn thảo)
     */
    public Contract createContract(ContractCreateRequest request, User creator) {
        // TODO: dùng template thật: fillTemplate(request.getTemplateId(), request.getFormData())
        String content = "Đây là nội dung hợp đồng được tạo từ form : " + request.getFormData();
        String contentHash = generateSHA256(content);

        Contract contract = Contract.builder()
                .title(request.getTitle())
                .content(content)
                .contentHash(contentHash) // Lưu hash để đảm bảo bất biến
                .creator(creator)
                .status(ContractStatus.DRAFT) // Trạng thái ban đầu là NHÁP
                .build();

        return contractRepository.save(contract);
    }

    /**
     * 2) Mời người khác vào Hợp đồng (Bước 2: Thêm người)
     *    - Mời bằng userId + name hiển thị
     */
    public ContractParticipant addParticipant(Long contractId, ParticipantInviteRequest request, User creator) {
        Contract contract = findContractById(contractId);

        // Chỉ cho phép thêm khi hợp đồng còn ở trạng thái NHÁP
        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Không thể thêm người tham gia khi hợp đồng đã gửi.");
        }

        // Chỉ người tạo hợp đồng mới được thêm
        if (!contract.getCreator().getId().equals(creator.getId())) {
            throw new SecurityException("Bạn không có quyền sửa hợp đồng này.");
        }

        // Lấy user được mời theo userId
        User invitee = userService.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + request.getUserId()));

        // Không cho mời chính mình
        if (invitee.getId().equals(creator.getId())) {
            throw new IllegalStateException("Không thể mời chính bạn vào hợp đồng.");
        }

        // Chống mời trùng trong cùng 1 hợp đồng
        boolean existed = participantRepository.findByContractAndUser(contract, invitee).isPresent();
        if (existed) {
            throw new IllegalStateException("Người dùng này đã có trong hợp đồng.");
        }

        // Tạo participant: gán user & tên hiển thị tại thời điểm mời
        String displayName = (request.getName() != null && !request.getName().isBlank())
                ? request.getName()
                : (invitee.getFullName() != null ? invitee.getFullName() : invitee.getUsername());

        ContractParticipant participant = ContractParticipant.builder()
                .contract(contract)
                .user(invitee)
                .inviteeName(displayName)
                .status(ParticipantStatus.PENDING)
                .build();

        ContractParticipant saved = participantRepository.save(participant);

        // // Gửi thông báo cho người được mời (khi có NotificationService)
        // if (notificationService != null) {
        //     notificationService.createNotification(
        //         invitee.getId(),
        //         "Bạn được mời tham gia hợp đồng: " + contract.getTitle(),
        //         NotificationType.CONTRACT,
        //         String.valueOf(contractId)
        //     );
        // }

        return saved;
    }

    /**
     * 3) Gửi Hợp đồng đi (Next - Khóa hợp đồng)
     */
    public Contract submitContract(Long contractId, User creator) {
        Contract contract = findContractById(contractId);

        if (!contract.getCreator().getId().equals(creator.getId())) {
            throw new SecurityException("Bạn không có quyền gửi hợp đồng này.");
        }

        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Hợp đồng đã được gửi đi trước đó.");
        }

        // Khóa hợp đồng: từ đây không chỉnh sửa nội dung/participant
        contract.setStatus(ContractStatus.PENDING_PARTICIPANT_APPROVAL);
        return contractRepository.save(contract);
    }

    /**
     * 4) Người nhận xác thực Hợp đồng (Các bên ký)
     */
    public ContractParticipant acceptContract(Long contractId, User participantUser) {
        Contract contract = findContractById(contractId);

        // Phải ở trạng thái chờ ký
        if (contract.getStatus() != ContractStatus.PENDING_PARTICIPANT_APPROVAL) {
            throw new IllegalStateException("Hợp đồng không ở trạng thái chờ ký.");
        }

        // Kiểm tra toàn vẹn nội dung
        String currentHash = generateSHA256(contract.getContent());
        if (!currentHash.equals(contract.getContentHash())) {
            throw new SecurityException("Nội dung hợp đồng đã bị thay đổi. Không thể ký.");
        }

        // Tìm participant theo (contract, user) — vì giờ đã mời bằng userId nên chắc chắn có
        ContractParticipant participant = participantRepository
                .findByContractAndUser(contract, participantUser)
                .orElseThrow(() -> new SecurityException("Bạn không phải là người tham gia hợp đồng này."));

        participant.setStatus(ParticipantStatus.ACCEPTED);
        participant.setAcceptedAt(LocalDateTime.now());
        participantRepository.save(participant);

        // Nếu tất cả đã ký → chờ Admin duyệt
        long pendingCount = participantRepository.countByContractAndStatus(contract, ParticipantStatus.PENDING);
        if (pendingCount == 0) {
            contract.setStatus(ContractStatus.PENDING_ADMIN_APPROVAL);
            contractRepository.save(contract);
            // // notify admin nếu cần
            // notificationService.notifyAdmins("Hợp đồng " + contract.getId() + " chờ duyệt");
        }

        return participant;
    }

    /**
     * 5) Admin duyệt Hợp đồng
     */
    public Contract approveContract(Long contractId, User adminUser) {
        // TODO: kiểm tra role ADMIN cho adminUser

        Contract contract = findContractById(contractId);

        if (contract.getStatus() != ContractStatus.PENDING_ADMIN_APPROVAL) {
            throw new IllegalStateException("Hợp đồng không ở trạng thái chờ duyệt.");
        }

        contract.setStatus(ContractStatus.ACTIVE); // Kích hoạt
        contract.setAdminApprover(adminUser);
        contract.setApprovedAt(LocalDateTime.now());

        return contractRepository.save(contract);
    }

    /**
     * Lấy chi tiết hợp đồng
     */
    public Contract getContractById(Long contractId) {
        return findContractById(contractId);
    }

    // --- Helper ---

    private Contract findContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng " + contractId));
    }

    // Hàm tạo hash (Mô phỏng Smart Contract)
    private String generateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            try (Formatter formatter = new Formatter()) {
                for (byte b : hash) {
                    formatter.format("%02x", b);
                }
                return formatter.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Không thể tạo hash", e);
        }
    }
}

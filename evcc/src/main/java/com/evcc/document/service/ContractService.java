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
import com.evcc.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Formatter;

@Slf4j
@Service
@Transactional
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractParticipantRepository participantRepository;

    @Autowired
    private UserService userService;

    /**
     * 1) Người dùng tạo Hợp đồng (Bước 1: Soạn thảo)
     */
    public Contract createContract(ContractCreateRequest request, User creator) {
        log.info("[CONTRACT][CREATE] byUser={}, title={}, templateId={}", creator.getId(), request.getTitle(), request.getTemplateId());

        // TODO: dùng template thật: fillTemplate(request.getTemplateId(), request.getFormData())
        String content = "Đây là nội dung hợp đồng được tạo từ form : " + request.getFormData();
        String contentHash = generateSHA256(content);

        Contract contract = Contract.builder()
                .title(request.getTitle())
                .content(content)
                .contentHash(contentHash)
                .creator(creator)
                .status(ContractStatus.DRAFT)
                .build();

        Contract saved = contractRepository.save(contract);
        log.info("[CONTRACT][CREATE] saved id={}, status={}", saved.getId(), saved.getStatus());
        return saved;
    }

    /**
     * 2) Mời người khác vào Hợp đồng (Bước 2: Thêm người)
     *    - Mời bằng userId + name hiển thị
     */
    public ContractParticipant addParticipant(Long contractId, ParticipantInviteRequest request, User creator) {
        log.info("[INVITE][BEGIN] contractId={}, creatorId={}, inviteeUserId={}, inviteeName={}",
                contractId, creator.getId(), request.getUserId(), request.getName());

        Contract contract = findContractById(contractId);

        // Chỉ cho phép thêm khi hợp đồng còn ở trạng thái NHÁP
        if (contract.getStatus() != ContractStatus.DRAFT) {
            log.warn("[INVITE][BLOCK] contractId={} status={} != DRAFT", contractId, contract.getStatus());
            throw new IllegalStateException("Không thể thêm người tham gia khi hợp đồng đã gửi.");
        }

        // Chỉ người tạo hợp đồng mới được thêm
        if (!contract.getCreator().getId().equals(creator.getId())) {
            log.warn("[INVITE][BLOCK] creatorMismatch contract.creatorId={} vs currentUserId={}",
                    contract.getCreator().getId(), creator.getId());
            throw new SecurityException("Bạn không có quyền sửa hợp đồng này.");
        }

        // Lấy user được mời theo userId
        User invitee = userService.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("[INVITE][BLOCK] inviteeNotFound userId={}", request.getUserId());
                    return new RuntimeException("Không tìm thấy user: " + request.getUserId());
                });

        // Không cho mời chính mình
        if (invitee.getId().equals(creator.getId())) {
            log.warn("[INVITE][BLOCK] selfInvite userId={}", invitee.getId());
            throw new IllegalStateException("Không thể mời chính bạn vào hợp đồng.");
        }

        // Chống mời trùng trong cùng 1 hợp đồng
        boolean existed = participantRepository.findByContractAndUser(contract, invitee).isPresent();
        log.info("[INVITE] existsInContract={}", existed);
        if (existed) {
            throw new IllegalStateException("Người dùng này đã có trong hợp đồng.");
        }

        // Tạo participant: gán user & tên hiển thị tại thời điểm mời
        String displayName = (request.getName() != null && !request.getName().isBlank())
                ? request.getName()
                : invitee.getUsername();

        ContractParticipant participant = ContractParticipant.builder()
                .contract(contract)
                .user(invitee)
                .inviteeName(displayName)
                .status(ParticipantStatus.PENDING)
                .build();

        ContractParticipant saved = participantRepository.save(participant);
        log.info("[INVITE][SUCCESS] contractId={}, participantId={}, inviteeId={}, inviteeName={}",
                contractId, saved.getId(), invitee.getId(), displayName);

        return saved;
    }

    /**
     * 3) Gửi Hợp đồng đi (Next - Khóa hợp đồng)
     */
    public Contract submitContract(Long contractId, User creator) {
        log.info("[SUBMIT][BEGIN] contractId={}, byUserId={}", contractId, creator.getId());

        Contract contract = findContractById(contractId);

        if (!contract.getCreator().getId().equals(creator.getId())) {
            log.warn("[SUBMIT][BLOCK] creatorMismatch contract.creatorId={} vs currentUserId={}",
                    contract.getCreator().getId(), creator.getId());
            throw new SecurityException("Bạn không có quyền gửi hợp đồng này.");
        }

        if (contract.getStatus() != ContractStatus.DRAFT) {
            log.warn("[SUBMIT][BLOCK] contractId={} alreadySubmitted status={}", contractId, contract.getStatus());
            throw new IllegalStateException("Hợp đồng đã được gửi đi trước đó.");
        }

        contract.setStatus(ContractStatus.PENDING_PARTICIPANT_APPROVAL);
        Contract saved = contractRepository.save(contract);
        log.info("[SUBMIT][SUCCESS] contractId={}, newStatus={}", contractId, saved.getStatus());
        return saved;
    }

    /**
     * 4) Người nhận xác thực Hợp đồng (Các bên ký)
     */
    public ContractParticipant acceptContract(Long contractId, User participantUser) {
        log.info("[ACCEPT][BEGIN] contractId={}, userId={}", contractId, participantUser.getId());

        Contract contract = findContractById(contractId);

        if (contract.getStatus() != ContractStatus.PENDING_PARTICIPANT_APPROVAL) {
            log.warn("[ACCEPT][BLOCK] contractId={} status={} != PENDING_PARTICIPANT_APPROVAL", contractId, contract.getStatus());
            throw new IllegalStateException("Hợp đồng không ở trạng thái chờ ký.");
        }

        // Kiểm tra toàn vẹn nội dung
        String currentHash = generateSHA256(contract.getContent());
        if (!currentHash.equals(contract.getContentHash())) {
            log.error("[ACCEPT][BLOCK] hashMismatch contractId={}, currentHash={}, expectedHash={}",
                    contractId, currentHash, contract.getContentHash());
            throw new SecurityException("Nội dung hợp đồng đã bị thay đổi. Không thể ký.");
        }

        // Tìm participant theo (contract, user)
        ContractParticipant participant = participantRepository
                .findByContractAndUser(contract, participantUser)
                .orElseThrow(() -> {
                    log.warn("[ACCEPT][BLOCK] participantNotFound contractId={}, userId={}", contractId, participantUser.getId());
                    return new SecurityException("Bạn không phải là người tham gia hợp đồng này.");
                });

        participant.setStatus(ParticipantStatus.ACCEPTED);
        participant.setAcceptedAt(LocalDateTime.now());
        participantRepository.save(participant);
        log.info("[ACCEPT][SUCCESS] participantId={}, contractId={}", participant.getId(), contractId);

        // Nếu tất cả đã ký → chờ Admin duyệt
        long pendingCount = participantRepository.countByContractAndStatus(contract, ParticipantStatus.PENDING);
        log.info("[ACCEPT] pendingCount={} for contractId={}", pendingCount, contractId);
        if (pendingCount == 0) {
            contract.setStatus(ContractStatus.PENDING_ADMIN_APPROVAL);
            contractRepository.save(contract);
            log.info("[ACCEPT] All signed. contractId={} -> status={}", contractId, ContractStatus.PENDING_ADMIN_APPROVAL);
        }

        return participant;
    }

    /**
     * 5) Admin duyệt Hợp đồng
     */
    public Contract approveContract(Long contractId, User adminUser) {
        log.info("[APPROVE][BEGIN] contractId={}, byUserId={}", contractId, adminUser.getId());
        // TODO: kiểm tra role ADMIN cho adminUser

        Contract contract = findContractById(contractId);

        if (contract.getStatus() != ContractStatus.PENDING_ADMIN_APPROVAL) {
            log.warn("[APPROVE][BLOCK] contractId={} status={} != PENDING_ADMIN_APPROVAL",
                    contractId, contract.getStatus());
            throw new IllegalStateException("Hợp đồng không ở trạng thái chờ duyệt.");
        }

        contract.setStatus(ContractStatus.ACTIVE);
        contract.setAdminApprover(adminUser);
        contract.setApprovedAt(LocalDateTime.now());

        Contract saved = contractRepository.save(contract);
        log.info("[APPROVE][SUCCESS] contractId={}, status={}, approverId={}",
                contractId, saved.getStatus(), adminUser.getId());
        return saved;
    }

    /**
     * Lấy chi tiết hợp đồng
     */
    @Transactional(readOnly = true)
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

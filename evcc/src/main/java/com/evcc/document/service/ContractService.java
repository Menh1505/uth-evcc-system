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
// import com.evcc.notification.service.NotificationService; // Sẽ cần để gửi mời
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
    // @Autowired
    // private ContractTemplateRepository templateRepository; // (Bạn sẽ cần tạo cái này)
    // @Autowired
    // private NotificationService notificationService; // (Bạn sẽ cần tạo cáiT này)

    /**
     * 1. Người dùng tạo Hợp đồng (Bước 1: Soạn thảo)
     */
    public Contract createContract(ContractCreateRequest request, User creator) {
        
        // Giả sử chúng ta có hàm lấy mẫu và điền form
        // String content = fillTemplate(request.getTemplateId(), request.getFormData());
        String content = "Đây là nội dung hợp đồng được tạo từ form : " + request.getFormData().toString();

        String contentHash = generateSHA256(content);

        Contract contract = Contract.builder()
                .title(request.getTitle())
                .content(content)
                .contentHash(contentHash) // Lưu hash để đảm bảo bất biến
                .creator(creator)
                .status(ContractStatus.DRAFT) // Trạng thái ban đầu là Nháp
                .build();
        
        return contractRepository.save(contract);
    }

    /**
     * 2. Mời người khác vào Hợp đồng (Bước 2: Thêm người)
     */
    public ContractParticipant addParticipant(Long contractId, ParticipantInviteRequest request, User creator) {
        Contract contract = findContractById(contractId);
        
        // *** KIỂM TRA BẤT BIẾN ***
        // Chỉ cho phép thêm người khi hợp đồng còn ở trạng thái NHÁP
        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Không thể thêm người tham gia khi hợp đồng đã gửi.");
        }
        
        // (Kiểm tra xem creator có phải là người tạo hợp đồng không)
        if (!contract.getCreator().getId().equals(creator.getId())) {
             throw new SecurityException("Bạn không có quyền sửa hợp đồng này .");
        }

        ContractParticipant participant = ContractParticipant.builder()
                .contract(contract)
                .inviteeName(request.getInviteeName())
                .inviteePhone(request.getInviteePhone())
                .status(ParticipantStatus.PENDING)
                .build();
        
        // notificationService.sendContractInvite(request.getInviteePhone());
        return participantRepository.save(participant);
    }
    
    /**
     * 3. Gửi Hợp đồng đi (Next - Khóa hợp đồng)
     */
    public Contract submitContract(Long contractId, User creator) {
         Contract contract = findContractById(contractId);
         
         if (!contract.getCreator().getId().equals(creator.getId())) {
             throw new SecurityException("Bạn không có quyền gửi hợp đồng này.");
         }
         
         // *** KIỂM TRA BẤT BIẾN ***
         if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new IllegalStateException("Hợp đồng đã được gửi đi trước đó.");
         }
         
         // *** KHÓA HỢP ĐỒNG ***
         // Chuyển trạng thái, từ giờ không ai (kể cả người tạo) được sửa nội dung
         contract.setStatus(ContractStatus.PENDING_PARTICIPANT_APPROVAL);
         return contractRepository.save(contract);
    }

    /**
     * 4. Người nhận xác thực Hợp đồng (Các bên ký)
     */
    public ContractParticipant acceptContract(Long contractId, User participantUser) {
        Contract contract = findContractById(contractId);
        
        // *** KIỂM TRA BẤT BIẾN (Quan trọng) ***
        // Đảm bảo hợp đồng đang ở trạng thái chờ ký VÀ nội dung không bị thay đổi
        if (contract.getStatus() != ContractStatus.PENDING_PARTICIPANT_APPROVAL) {
             throw new IllegalStateException("Hợp đồng không ở trạng thái chờ ký.");
        }
        
        // Kiểm tra lại hash xem nội dung có bị sửa (ví dụ: ai đó sửa DB) hay không
        String currentHash = generateSHA256(contract.getContent());
        if (!currentHash.equals(contract.getContentHash())) {
            // LỖI NGHIÊM TRỌNG - Hợp đồng đã bị thay đổi
            // Bạn có thể set status thành REJECTED hoặc báo động cho Admin
             throw new SecurityException("Nội dung hợp đồng đã bị thay đổi. Không thể ký.");
        }

        // Tìm xem user này có phải là người tham gia không
        // (Lưu ý: Logic này giả định 'participantUser' đã được liên kết)
        // Bạn sẽ cần logic phức tạp hơn để liên kết SĐT với tài khoản User khi họ xác thực
        ContractParticipant participant = participantRepository
                .findByContractAndUser(contract, participantUser)
                .orElseThrow(() -> new SecurityException("Bạn không phải là người tham gia hợp đồng này."));
        
        participant.setStatus(ParticipantStatus.ACCEPTED);
        participant.setAcceptedAt(LocalDateTime.now());
        participantRepository.save(participant);

        // Kiểm tra xem tất cả mọi người đã ký chưa
        long pendingCount = participantRepository.countByContractAndStatus(contract, ParticipantStatus.PENDING);
        if (pendingCount == 0) {
            // Tất cả đã ký -> Chuyển cho Admin duyệt
            contract.setStatus(ContractStatus.PENDING_ADMIN_APPROVAL);
            contractRepository.save(contract);
            // notificationService.notifyAdmins("Hợp đồng " + contract.getId() + " chờ duyệt");
        }
        
        return participant;
    }
    
    /**
     * 5. Admin duyệt Hợp đồng
     */
    public Contract approveContract(Long contractId, User adminUser) {
        // (Bạn nên có logic kiểm tra 'adminUser' có Role là ADMIN)
        
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


    // --- Hàm Helper ---
    
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
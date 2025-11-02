package com.evcc.document.controller;

import com.evcc.document.dto.ContractCreateRequest;
import com.evcc.document.dto.ParticipantInviteRequest;
import com.evcc.document.entity.Contract;
import com.evcc.document.service.ContractService;
import com.evcc.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    // 1. Tạo hợp đồng (chủ sở hữu tạo)
    @PostMapping("/")
    public ResponseEntity<Contract> createContract(
            @RequestBody ContractCreateRequest request,
            @AuthenticationPrincipal User currentUser) { // Lấy user đang đăng nhập

        Contract contract = contractService.createContract(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(contract);
    }

    // 2. Mời người khác (chủ sở hữu mời)
    @PostMapping("/{contractId}/invite")
    public ResponseEntity<?> addParticipant(
            @PathVariable Long contractId,
            @RequestBody ParticipantInviteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(contractService.addParticipant(contractId, request, currentUser));
    }

    // 3. Gửi hợp đồng đi (chủ sở hữu gửi, khóa hợp đồng)
    @PostMapping("/{contractId}/submit")
    public ResponseEntity<?> submitContract(
            @PathVariable Long contractId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(contractService.submitContract(contractId, currentUser));
    }

    // 4. Người tham gia xác thực (người được mời ký)
    @PostMapping("/{contractId}/accept")
    public ResponseEntity<?> acceptContract(
            @PathVariable Long contractId,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(contractService.acceptContract(contractId, currentUser));
    }

    // 5. Admin duyệt (Admin ký)
    @PostMapping("/{contractId}/approve")
    public ResponseEntity<?> approveContract(
            @PathVariable Long contractId,
            @AuthenticationPrincipal User adminUser) {

        // (Nên có @PreAuthorize("hasRole('ADMIN')") ở đây)
        return ResponseEntity.ok(contractService.approveContract(contractId, adminUser));
    }

    // Lấy chi tiết hợp đồng
    @GetMapping("/{contractId}")
    public ResponseEntity<Contract> getContract(@PathVariable Long contractId) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }
}
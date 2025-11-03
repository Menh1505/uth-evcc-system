package com.evcc.contract.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.contract.dto.ContractResponse;
import com.evcc.contract.dto.ContractSummaryResponse;
import com.evcc.contract.dto.CreateContractRequest;
import com.evcc.contract.dto.UpdateContractRequest;
import com.evcc.contract.enums.ContractStatus;
import com.evcc.contract.service.ContractService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller cho quản lý hợp đồng mua xe
 */
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    /**
     * Tạo mới hợp đồng
     * POST /api/contracts
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ContractResponse> createContract(@Valid @RequestBody CreateContractRequest request) {
        try {
            ContractResponse response = contractService.createContract(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cập nhật hợp đồng
     * PUT /api/contracts/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ContractResponse> updateContract(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContractRequest request) {
        try {
            ContractResponse response = contractService.updateContract(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy thông tin chi tiết hợp đồng
     * GET /api/contracts/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ContractResponse> getContract(@PathVariable Long id) {
        try {
            ContractResponse response = contractService.getContractById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy hợp đồng theo mã số
     * GET /api/contracts/by-number/{contractNumber}
     */
    @GetMapping("/by-number/{contractNumber}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ContractResponse> getContractByNumber(@PathVariable String contractNumber) {
        try {
            ContractResponse response = contractService.getContractByNumber(contractNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy danh sách hợp đồng của nhóm
     * GET /api/contracts/group/{groupId}
     */
    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ContractSummaryResponse>> getContractsByGroup(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractSummaryResponse> contracts = contractService.getContractsByGroup(groupId, pageable);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy danh sách hợp đồng mà user tham gia
     * GET /api/contracts/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ContractSummaryResponse>> getContractsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ContractSummaryResponse> contracts = contractService.getContractsByUser(userId, pageable);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Tìm kiếm hợp đồng theo tiêu đề
     * GET /api/contracts/search?title={title}
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ContractSummaryResponse>> searchContracts(
            @RequestParam String title) {
        try {
            List<ContractSummaryResponse> contracts = contractService.searchContractsByTitle(title);
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Thay đổi trạng thái hợp đồng
     * PUT /api/contracts/{id}/status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContractResponse> changeContractStatus(
            @PathVariable Long id,
            @RequestParam ContractStatus status) {
        try {
            ContractResponse response = contractService.changeContractStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gán xe cho hợp đồng
     * PUT /api/contracts/{id}/vehicle
     */
    @PutMapping("/{id}/vehicle")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ContractResponse> assignVehicleToContract(
            @PathVariable Long id,
            @RequestParam Long vehicleId) {
        try {
            ContractResponse response = contractService.assignVehicleToContract(id, vehicleId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Xóa hợp đồng (chỉ khi ở trạng thái DRAFT)
     * DELETE /api/contracts/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        try {
            contractService.deleteContract(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Kiểm tra tính hợp lệ của hợp đồng
     * GET /api/contracts/{id}/validate
     */
    @GetMapping("/{id}/validate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> validateContract(@PathVariable Long id) {
        try {
            boolean isValid = contractService.validateContract(id);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy tỉ lệ đóng góp đã hoàn thành
     * GET /api/contracts/{id}/contribution-percentage
     */
    @GetMapping("/{id}/contribution-percentage")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BigDecimal> getContributionPercentage(@PathVariable Long id) {
        try {
            BigDecimal percentage = contractService.calculateContributionPercentage(id);
            return ResponseEntity.ok(percentage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy danh sách ưu tiên sử dụng xe
     * GET /api/contracts/{id}/usage-priority
     */
    @GetMapping("/{id}/usage-priority")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ContractResponse.OwnershipInfo>> getUsagePriority(@PathVariable Long id) {
        try {
            List<ContractResponse.OwnershipInfo> priority = contractService.getVehicleUsagePriority(id);
            return ResponseEntity.ok(priority);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Kiểm tra quyền sử dụng xe của user
     * GET /api/contracts/{id}/can-use-vehicle?userId={userId}
     */
    @GetMapping("/{id}/can-use-vehicle")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> canUserUseVehicle(
            @PathVariable Long id,
            @RequestParam UUID userId) {
        try {
            boolean canUse = contractService.canUserUseVehicle(id, userId);
            return ResponseEntity.ok(canUse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Tính toán ưu tiên sử dụng của user
     * GET /api/contracts/{id}/user-priority?userId={userId}
     */
    @GetMapping("/{id}/user-priority")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Integer> getUserPriority(
            @PathVariable Long id,
            @RequestParam UUID userId) {
        try {
            int priority = contractService.calculateUserPriority(id, userId);
            return ResponseEntity.ok(priority);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
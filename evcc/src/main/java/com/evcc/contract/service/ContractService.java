package com.evcc.contract.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.evcc.contract.dto.ContractResponse;
import com.evcc.contract.dto.ContractSummaryResponse;
import com.evcc.contract.dto.CreateContractRequest;
import com.evcc.contract.dto.UpdateContractRequest;
import com.evcc.contract.enums.ContractStatus;

/**
 * Interface cho Contract Service
 */
public interface ContractService {

    /**
     * Tạo mới hợp đồng
     */
    ContractResponse createContract(CreateContractRequest request);

    /**
     * Cập nhật hợp đồng
     */
    ContractResponse updateContract(Long contractId, UpdateContractRequest request);

    /**
     * Lấy thông tin chi tiết hợp đồng
     */
    ContractResponse getContractById(Long contractId);

    /**
     * Lấy hợp đồng theo mã số
     */
    ContractResponse getContractByNumber(String contractNumber);

    /**
     * Lấy danh sách hợp đồng của nhóm
     */
    List<ContractSummaryResponse> getContractsByGroup(Long groupId);

    /**
     * Lấy danh sách hợp đồng của nhóm với phân trang
     */
    Page<ContractSummaryResponse> getContractsByGroup(Long groupId, Pageable pageable);

    /**
     * Lấy danh sách hợp đồng mà user tham gia
     */
    List<ContractSummaryResponse> getContractsByUser(UUID userId);

    /**
     * Lấy danh sách hợp đồng mà user tham gia với phân trang
     */
    Page<ContractSummaryResponse> getContractsByUser(UUID userId, Pageable pageable);

    /**
     * Tìm kiếm hợp đồng theo tiêu đề
     */
    List<ContractSummaryResponse> searchContractsByTitle(String title);

    /**
     * Thay đổi trạng thái hợp đồng
     */
    ContractResponse changeContractStatus(Long contractId, ContractStatus newStatus);

    /**
     * Gán xe cho hợp đồng
     */
    ContractResponse assignVehicleToContract(Long contractId, Long vehicleId);

    /**
     * Xóa hợp đồng (chỉ khi ở trạng thái DRAFT)
     */
    void deleteContract(Long contractId);

    /**
     * Kiểm tra tính hợp lệ của hợp đồng
     */
    boolean validateContract(Long contractId);

    /**
     * Tính toán tỉ lệ đóng góp đã hoàn thành
     */
    BigDecimal calculateContributionPercentage(Long contractId);

    /**
     * Lấy danh sách ưu tiên sử dụng xe dựa trên tỉ lệ sở hữu
     */
    List<ContractResponse.OwnershipInfo> getVehicleUsagePriority(Long contractId);

    /**
     * Kiểm tra user có quyền sử dụng xe không
     */
    boolean canUserUseVehicle(Long contractId, UUID userId);

    /**
     * Tính toán ưu tiên sử dụng của user (số từ 0-100)
     */
    int calculateUserPriority(Long contractId, UUID userId);
}
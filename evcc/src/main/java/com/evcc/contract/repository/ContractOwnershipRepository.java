package com.evcc.contract.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.contract.entity.ContractOwnership;

/**
 * Repository cho ContractOwnership entity
 */
@Repository
public interface ContractOwnershipRepository extends JpaRepository<ContractOwnership, Long> {

    /**
     * Tìm tất cả quyền sở hữu trong một hợp đồng
     */
    List<ContractOwnership> findByContractId(Long contractId);

    /**
     * Tìm quyền sở hữu của một user trong một hợp đồng
     */
    Optional<ContractOwnership> findByContractIdAndUserId(Long contractId, UUID userId);

    /**
     * Tìm tất cả quyền sở hữu của một user
     */
    List<ContractOwnership> findByUserId(UUID userId);

    /**
     * Tìm quyền sở hữu theo trạng thái thanh toán
     */
    List<ContractOwnership> findByPaymentStatus(String paymentStatus);

    /**
     * Tìm quyền sở hữu có quyền sử dụng xe
     */
    List<ContractOwnership> findByUsageEligible(Boolean usageEligible);

    /**
     * Tính tổng tỉ lệ sở hữu trong một hợp đồng
     */
    @Query("SELECT SUM(o.ownershipPercentage) FROM ContractOwnership o WHERE o.contract.id = :contractId")
    BigDecimal sumOwnershipPercentageByContractId(@Param("contractId") Long contractId);

    /**
     * Tính tổng số tiền đóng góp trong một hợp đồng
     */
    @Query("SELECT SUM(o.contributionAmount) FROM ContractOwnership o WHERE o.contract.id = :contractId")
    BigDecimal sumContributionAmountByContractId(@Param("contractId") Long contractId);

    /**
     * Tính tổng số tiền đã thanh toán đầy đủ trong một hợp đồng
     */
    @Query("SELECT SUM(o.contributionAmount) FROM ContractOwnership o " +
           "WHERE o.contract.id = :contractId AND o.paymentStatus = 'COMPLETED'")
    BigDecimal sumCompletedContributionsByContractId(@Param("contractId") Long contractId);

    /**
     * Đếm số lượng owner trong một hợp đồng
     */
    long countByContractId(Long contractId);

    /**
     * Đếm số lượng owner đã thanh toán đầy đủ
     */
    long countByContractIdAndPaymentStatus(Long contractId, String paymentStatus);

    /**
     * Đếm số lượng owner có quyền sử dụng xe
     */
    long countByContractIdAndUsageEligible(Long contractId, Boolean usageEligible);

    /**
     * Tìm owner có tỉ lệ sở hữu cao nhất trong hợp đồng
     */
    @Query("SELECT o FROM ContractOwnership o " +
           "WHERE o.contract.id = :contractId " +
           "ORDER BY o.ownershipPercentage DESC")
    List<ContractOwnership> findByContractIdOrderByOwnershipPercentageDesc(@Param("contractId") Long contractId);

    /**
     * Tìm ownership theo hợp đồng và sắp xếp theo tỉ lệ sở hữu giảm dần
     * Dùng cho việc ưu tiên sử dụng xe
     */
    @Query("SELECT o FROM ContractOwnership o " +
           "WHERE o.contract.id = :contractId AND o.usageEligible = true " +
           "ORDER BY o.ownershipPercentage DESC, o.contributionDate ASC")
    List<ContractOwnership> findEligibleOwnershipsByContractIdOrderedByPriority(@Param("contractId") Long contractId);

    /**
     * Kiểm tra xem user đã có ownership trong hợp đồng chưa
     */
    boolean existsByContractIdAndUserId(Long contractId, UUID userId);

    /**
     * Xóa ownership theo contractId và userId
     */
    void deleteByContractIdAndUserId(Long contractId, UUID userId);
}
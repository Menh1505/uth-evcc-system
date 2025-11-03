package com.evcc.contract.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.contract.entity.Contract;
import com.evcc.contract.enums.ContractStatus;

/**
 * Repository cho Contract entity
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * Tìm hợp đồng theo mã số
     */
    Optional<Contract> findByContractNumber(String contractNumber);

    /**
     * Tìm tất cả hợp đồng của một nhóm
     */
    List<Contract> findByGroupId(Long groupId);

    /**
     * Tìm hợp đồng của nhóm với phân trang
     */
    Page<Contract> findByGroupId(Long groupId, Pageable pageable);

    /**
     * Tìm hợp đồng theo trạng thái
     */
    List<Contract> findByStatus(ContractStatus status);

    /**
     * Tìm hợp đồng theo xe
     */
    Optional<Contract> findByVehicleId(Long vehicleId);

    /**
     * Tìm hợp đồng mà user tham gia
     */
    @Query("SELECT DISTINCT c FROM Contract c " +
           "JOIN c.ownerships o " +
           "WHERE o.user.id = :userId")
    List<Contract> findContractsByUserId(@Param("userId") UUID userId);

    /**
     * Tìm hợp đồng mà user tham gia với phân trang
     */
    @Query("SELECT DISTINCT c FROM Contract c " +
           "JOIN c.ownerships o " +
           "WHERE o.user.id = :userId")
    Page<Contract> findContractsByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Tìm hợp đồng theo trạng thái và nhóm
     */
    List<Contract> findByGroupIdAndStatus(Long groupId, ContractStatus status);

    /**
     * Kiểm tra xem nhóm đã có hợp đồng ACTIVE nào chưa
     */
    boolean existsByGroupIdAndStatus(Long groupId, ContractStatus status);

    /**
     * Tìm hợp đồng theo tiêu đề (tìm kiếm tương đối)
     */
    List<Contract> findByTitleContainingIgnoreCase(String title);

    /**
     * Tìm hợp đồng theo tiêu đề và nhóm
     */
    List<Contract> findByTitleContainingIgnoreCaseAndGroupId(String title, Long groupId);

    /**
     * Đếm số hợp đồng của một nhóm
     */
    long countByGroupId(Long groupId);

    /**
     * Đếm số hợp đồng theo trạng thái của một nhóm
     */
    long countByGroupIdAndStatus(Long groupId, ContractStatus status);
}
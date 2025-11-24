package com.evcc.expense.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.expense.entity.VehicleExpense;

/**
 * Repository for VehicleExpense entity
 */
@Repository
public interface VehicleExpenseRepository extends JpaRepository<VehicleExpense, Long> {

    /**
     * Find expenses by contract
     */
    List<VehicleExpense> findByContract_IdOrderByCreatedAtDesc(Long contractId);

    /**
     * Find expenses by status
     */
    List<VehicleExpense> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Find expenses by expense type
     */
    List<VehicleExpense> findByExpenseTypeOrderByCreatedAtDesc(String expenseType);

    /**
     * Find pending expenses (not approved/rejected)
     */
    @Query("SELECT e FROM VehicleExpense e WHERE e.status IN ('PENDING', 'DRAFT') ORDER BY e.createdAt DESC")
    List<VehicleExpense> findPendingExpenses();

    /**
     * Find expenses due for payment
     */
    @Query("SELECT e FROM VehicleExpense e WHERE e.dueDate <= :date AND e.status = 'APPROVED' ORDER BY e.dueDate ASC")
    List<VehicleExpense> findExpensesDueByDate(@Param("date") LocalDate date);

    /**
     * Find expenses created by user
     */
    List<VehicleExpense> findByCreatedBy_IdOrderByCreatedAtDesc(UUID createdById);

    /**
     * Count expenses by status
     */
    long countByStatus(String status);

    /**
     * Sum total amount by contract
     */
    @Query("SELECT COALESCE(SUM(e.totalAmount), 0) FROM VehicleExpense e WHERE e.contract.id = :contractId")
    java.math.BigDecimal sumTotalAmountByContract(@Param("contractId") Long contractId);
}

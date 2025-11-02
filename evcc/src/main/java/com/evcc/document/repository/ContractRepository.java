package com.evcc.document.repository;

import com.evcc.document.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    // Spring Data JPA tự động tạo các hàm save, findById...
}
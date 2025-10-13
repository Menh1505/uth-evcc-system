package com.evcc.booking.repository;

import com.evcc.booking.entity.Contract;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ContractRepository extends JpaRepository<Contract, UUID> {
}

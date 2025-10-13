package com.evcc.booking.repository;

import com.evcc.booking.entity.ContractAcceptance;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ContractAcceptanceRepository extends JpaRepository<ContractAcceptance, UUID> {
}

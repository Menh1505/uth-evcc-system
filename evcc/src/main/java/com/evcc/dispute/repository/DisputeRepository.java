package com.evcc.dispute.repository;

import com.evcc.dispute.entity.Dispute;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
}

package com.evcc.evcc.repository;

import com.evcc.evcc.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
}
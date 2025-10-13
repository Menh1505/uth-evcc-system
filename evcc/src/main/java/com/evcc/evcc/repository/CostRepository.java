package com.evcc.evcc.repository;

import com.evcc.evcc.entity.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CostRepository extends JpaRepository<Cost, UUID> {
}
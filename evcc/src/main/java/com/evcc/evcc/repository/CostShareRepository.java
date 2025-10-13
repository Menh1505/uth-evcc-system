package com.evcc.evcc.repository;

import com.evcc.evcc.entity.CostShare;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CostShareRepository extends JpaRepository<CostShare, UUID> {
}
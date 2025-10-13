package com.evcc.cost.repository;

import com.evcc.cost.entity.CostShare;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CostShareRepository extends JpaRepository<CostShare, UUID> {
}

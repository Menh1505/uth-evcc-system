package com.evcc.cost.repository;

import com.evcc.cost.entity.Cost;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CostRepository extends JpaRepository<Cost, UUID> {
}

package com.evcc.vehicle.repository;

import com.evcc.vehicle.entity.Usage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UsageRepository extends JpaRepository<Usage, UUID> {
}

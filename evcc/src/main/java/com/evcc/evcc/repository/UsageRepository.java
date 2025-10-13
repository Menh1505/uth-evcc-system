package com.evcc.evcc.repository;

import com.evcc.evcc.entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UsageRepository extends JpaRepository<Usage, UUID> {
}
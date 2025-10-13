package com.evcc.evcc.repository;

import com.evcc.evcc.entity.IntegrationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface IntegrationConfigRepository extends JpaRepository<IntegrationConfig, UUID> {
}
package com.evcc.integration.repository;

import com.evcc.integration.entity.IntegrationConfig;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface IntegrationConfigRepository extends JpaRepository<IntegrationConfig, UUID> {
}

package com.evcc.integration.service;

import com.evcc.integration.entity.IntegrationConfig;
import com.evcc.integration.repository.IntegrationConfigRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class IntegrationConfigService {
    @Autowired
    private IntegrationConfigRepository integrationConfigRepository;

    public List<IntegrationConfig> getAllIntegrationConfigs() {
        return integrationConfigRepository.findAll();
    }

    public IntegrationConfig saveIntegrationConfig(IntegrationConfig integrationConfig) {
        return integrationConfigRepository.save(integrationConfig);
    }

    public IntegrationConfig getIntegrationConfigById(UUID id) {
        return integrationConfigRepository.findById(id).orElse(null);
    }
}

package com.evcc.evcc.service;

import com.evcc.evcc.entity.IntegrationConfig;
import com.evcc.evcc.repository.IntegrationConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
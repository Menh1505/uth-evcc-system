package com.evcc.integration.controller;

import com.evcc.integration.entity.IntegrationConfig;
import com.evcc.integration.service.IntegrationConfigService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/integration-configs")
public class IntegrationConfigController {
    @Autowired
    private IntegrationConfigService integrationConfigService;

    @GetMapping
    public List<IntegrationConfig> getAllIntegrationConfigs() {
        return integrationConfigService.getAllIntegrationConfigs();
    }

    @PostMapping
    public IntegrationConfig saveIntegrationConfig(@RequestBody IntegrationConfig integrationConfig) {
        return integrationConfigService.saveIntegrationConfig(integrationConfig);
    }

    @GetMapping("/{id}")
    public IntegrationConfig getIntegrationConfigById(@PathVariable UUID id) {
        return integrationConfigService.getIntegrationConfigById(id);
    }
}

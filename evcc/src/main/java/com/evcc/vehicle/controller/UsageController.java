package com.evcc.vehicle.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evcc.vehicle.entity.Usage;
import com.evcc.vehicle.service.UsageService;

@RestController
@RequestMapping("/usages")
public class UsageController {
    private final UsageService usageService;

    public UsageController(UsageService usageService) {
        this.usageService = usageService;
    }

    @GetMapping
    public List<Usage> getAllUsages() {
        return usageService.getAllUsages();
    }

    @PostMapping
    public Usage saveUsage(@RequestBody Usage usage) {
        return usageService.saveUsage(usage);
    }

    @GetMapping("/{id}")
    public Usage getUsageById(@PathVariable UUID id) {
        return usageService.getUsageById(id);
    }
}

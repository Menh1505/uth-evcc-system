package com.evcc.evcc.controller;

import com.evcc.evcc.entity.Usage;
import com.evcc.evcc.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usages")
public class UsageController {
    @Autowired
    private UsageService usageService;

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
package com.evcc.vehicle.controller;

import com.evcc.vehicle.entity.Usage;
import com.evcc.vehicle.service.UsageService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

package com.evcc.evcc.controller;

import com.evcc.evcc.entity.Handover;
import com.evcc.evcc.service.HandoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/handovers")
public class HandoverController {
    @Autowired
    private HandoverService handoverService;

    @GetMapping
    public List<Handover> getAllHandovers() {
        return handoverService.getAllHandovers();
    }

    @PostMapping
    public Handover saveHandover(@RequestBody Handover handover) {
        return handoverService.saveHandover(handover);
    }

    @GetMapping("/{id}")
    public Handover getHandoverById(@PathVariable UUID id) {
        return handoverService.getHandoverById(id);
    }
}
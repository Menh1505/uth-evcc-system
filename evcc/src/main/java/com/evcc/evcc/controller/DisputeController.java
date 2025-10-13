package com.evcc.evcc.controller;

import com.evcc.evcc.entity.Dispute;
import com.evcc.evcc.service.DisputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/disputes")
public class DisputeController {
    @Autowired
    private DisputeService disputeService;

    @GetMapping
    public List<Dispute> getAllDisputes() {
        return disputeService.getAllDisputes();
    }

    @PostMapping
    public Dispute saveDispute(@RequestBody Dispute dispute) {
        return disputeService.saveDispute(dispute);
    }

    @GetMapping("/{id}")
    public Dispute getDisputeById(@PathVariable UUID id) {
        return disputeService.getDisputeById(id);
    }
}
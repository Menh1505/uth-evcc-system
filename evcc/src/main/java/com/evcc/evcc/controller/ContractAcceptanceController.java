package com.evcc.evcc.controller;

import com.evcc.evcc.entity.ContractAcceptance;
import com.evcc.evcc.service.ContractAcceptanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contract-acceptances")
public class ContractAcceptanceController {
    @Autowired
    private ContractAcceptanceService contractAcceptanceService;

    @GetMapping
    public List<ContractAcceptance> getAllContractAcceptances() {
        return contractAcceptanceService.getAllContractAcceptances();
    }

    @PostMapping
    public ContractAcceptance saveContractAcceptance(@RequestBody ContractAcceptance contractAcceptance) {
        return contractAcceptanceService.saveContractAcceptance(contractAcceptance);
    }

    @GetMapping("/{id}")
    public ContractAcceptance getContractAcceptanceById(@PathVariable UUID id) {
        return contractAcceptanceService.getContractAcceptanceById(id);
    }
}
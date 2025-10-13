package com.evcc.booking.controller;

import com.evcc.booking.entity.ContractAcceptance;
import com.evcc.booking.service.ContractAcceptanceService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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

package com.evcc.booking.controller;

import com.evcc.booking.entity.Contract;
import com.evcc.booking.service.ContractService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/contracts")
public class ContractController {
    @Autowired
    private ContractService contractService;

    @GetMapping
    public List<Contract> getAllContracts() {
        return contractService.getAllContracts();
    }

    @PostMapping
    public Contract saveContract(@RequestBody Contract contract) {
        return contractService.saveContract(contract);
    }

    @GetMapping("/{id}")
    public Contract getContractById(@PathVariable UUID id) {
        return contractService.getContractById(id);
    }
}

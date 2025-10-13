package com.evcc.evcc.controller;

import com.evcc.evcc.entity.CostShare;
import com.evcc.evcc.service.CostShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cost-shares")
public class CostShareController {
    @Autowired
    private CostShareService costShareService;

    @GetMapping
    public List<CostShare> getAllCostShares() {
        return costShareService.getAllCostShares();
    }

    @PostMapping
    public CostShare saveCostShare(@RequestBody CostShare costShare) {
        return costShareService.saveCostShare(costShare);
    }

    @GetMapping("/{id}")
    public CostShare getCostShareById(@PathVariable UUID id) {
        return costShareService.getCostShareById(id);
    }
}
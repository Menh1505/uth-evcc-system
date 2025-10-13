package com.evcc.evcc.controller;

import com.evcc.evcc.entity.Cost;
import com.evcc.evcc.service.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/costs")
public class CostController {
    @Autowired
    private CostService costService;

    @GetMapping
    public List<Cost> getAllCosts() {
        return costService.getAllCosts();
    }

    @PostMapping
    public Cost saveCost(@RequestBody Cost cost) {
        return costService.saveCost(cost);
    }

    @GetMapping("/{id}")
    public Cost getCostById(@PathVariable UUID id) {
        return costService.getCostById(id);
    }
}
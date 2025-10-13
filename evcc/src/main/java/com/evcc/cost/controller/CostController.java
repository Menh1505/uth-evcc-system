package com.evcc.cost.controller;

import com.evcc.cost.entity.Cost;
import com.evcc.cost.service.CostService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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

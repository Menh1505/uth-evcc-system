package com.evcc.cost.controller;

import com.evcc.cost.entity.CostShare;
import com.evcc.cost.service.CostShareService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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

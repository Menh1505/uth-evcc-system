package com.evcc.cost.service;

import com.evcc.cost.entity.Cost;
import com.evcc.cost.repository.CostRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class CostService {
    @Autowired
    private CostRepository costRepository;

    public List<Cost> getAllCosts() {
        return costRepository.findAll();
    }

    public Cost saveCost(Cost cost) {
        return costRepository.save(cost);
    }

    public Cost getCostById(UUID id) {
        return costRepository.findById(id).orElse(null);
    }
}

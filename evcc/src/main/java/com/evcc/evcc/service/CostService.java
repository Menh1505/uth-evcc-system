package com.evcc.evcc.service;

import com.evcc.evcc.entity.Cost;
import com.evcc.evcc.repository.CostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
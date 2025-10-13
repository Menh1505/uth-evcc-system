package com.evcc.evcc.service;

import com.evcc.evcc.entity.CostShare;
import com.evcc.evcc.repository.CostShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CostShareService {
    @Autowired
    private CostShareRepository costShareRepository;

    public List<CostShare> getAllCostShares() {
        return costShareRepository.findAll();
    }

    public CostShare saveCostShare(CostShare costShare) {
        return costShareRepository.save(costShare);
    }

    public CostShare getCostShareById(UUID id) {
        return costShareRepository.findById(id).orElse(null);
    }
}
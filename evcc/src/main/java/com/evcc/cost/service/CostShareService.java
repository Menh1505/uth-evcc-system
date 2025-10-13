package com.evcc.cost.service;

import com.evcc.cost.entity.CostShare;
import com.evcc.cost.repository.CostShareRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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

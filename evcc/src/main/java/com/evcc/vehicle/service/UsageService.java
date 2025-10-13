package com.evcc.vehicle.service;

import com.evcc.vehicle.entity.Usage;
import com.evcc.vehicle.repository.UsageRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class UsageService {
    @Autowired
    private UsageRepository usageRepository;

    public List<Usage> getAllUsages() {
        return usageRepository.findAll();
    }

    public Usage saveUsage(Usage usage) {
        return usageRepository.save(usage);
    }

    public Usage getUsageById(UUID id) {
        return usageRepository.findById(id).orElse(null);
    }
}

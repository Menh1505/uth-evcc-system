package com.evcc.vehicle.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.evcc.vehicle.entity.Usage;
import com.evcc.vehicle.repository.UsageRepository;




@Service
public class UsageService {
    private final UsageRepository usageRepository;

    public UsageService(UsageRepository usageRepository) {
        this.usageRepository = usageRepository;
    }

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

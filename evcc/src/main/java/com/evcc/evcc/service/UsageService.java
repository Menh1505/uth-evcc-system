package com.evcc.evcc.service;

import com.evcc.evcc.entity.Usage;
import com.evcc.evcc.repository.UsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
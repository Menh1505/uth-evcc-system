package com.evcc.evcc.service;

import com.evcc.evcc.entity.Handover;
import com.evcc.evcc.repository.HandoverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HandoverService {
    @Autowired
    private HandoverRepository handoverRepository;

    public List<Handover> getAllHandovers() {
        return handoverRepository.findAll();
    }

    public Handover saveHandover(Handover handover) {
        return handoverRepository.save(handover);
    }

    public Handover getHandoverById(UUID id) {
        return handoverRepository.findById(id).orElse(null);
    }
}
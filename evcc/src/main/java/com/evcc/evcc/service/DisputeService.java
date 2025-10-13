package com.evcc.evcc.service;

import com.evcc.evcc.entity.Dispute;
import com.evcc.evcc.repository.DisputeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DisputeService {
    @Autowired
    private DisputeRepository disputeRepository;

    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAll();
    }

    public Dispute saveDispute(Dispute dispute) {
        return disputeRepository.save(dispute);
    }

    public Dispute getDisputeById(UUID id) {
        return disputeRepository.findById(id).orElse(null);
    }
}
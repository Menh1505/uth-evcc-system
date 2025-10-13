package com.evcc.dispute.service;

import com.evcc.dispute.entity.Dispute;
import com.evcc.dispute.repository.DisputeRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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

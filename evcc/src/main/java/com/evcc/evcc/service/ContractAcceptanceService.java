package com.evcc.evcc.service;

import com.evcc.evcc.entity.ContractAcceptance;
import com.evcc.evcc.repository.ContractAcceptanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContractAcceptanceService {
    @Autowired
    private ContractAcceptanceRepository contractAcceptanceRepository;

    public List<ContractAcceptance> getAllContractAcceptances() {
        return contractAcceptanceRepository.findAll();
    }

    public ContractAcceptance saveContractAcceptance(ContractAcceptance contractAcceptance) {
        return contractAcceptanceRepository.save(contractAcceptance);
    }

    public ContractAcceptance getContractAcceptanceById(UUID id) {
        return contractAcceptanceRepository.findById(id).orElse(null);
    }
}
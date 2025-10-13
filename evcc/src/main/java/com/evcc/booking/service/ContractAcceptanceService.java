package com.evcc.booking.service;

import com.evcc.booking.entity.ContractAcceptance;
import com.evcc.booking.repository.ContractAcceptanceRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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

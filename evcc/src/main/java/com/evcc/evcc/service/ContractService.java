package com.evcc.evcc.service;

import com.evcc.evcc.entity.Contract;
import com.evcc.evcc.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Contract saveContract(Contract contract) {
        return contractRepository.save(contract);
    }

    public Contract getContractById(UUID id) {
        return contractRepository.findById(id).orElse(null);
    }
}
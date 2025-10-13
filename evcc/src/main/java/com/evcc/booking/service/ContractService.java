package com.evcc.booking.service;

import com.evcc.booking.entity.Contract;
import com.evcc.booking.repository.ContractRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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

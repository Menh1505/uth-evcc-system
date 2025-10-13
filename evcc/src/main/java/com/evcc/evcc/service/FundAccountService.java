package com.evcc.evcc.service;

import com.evcc.evcc.entity.FundAccount;
import com.evcc.evcc.repository.FundAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FundAccountService {
    @Autowired
    private FundAccountRepository fundAccountRepository;

    public List<FundAccount> getAllFundAccounts() {
        return fundAccountRepository.findAll();
    }

    public FundAccount saveFundAccount(FundAccount fundAccount) {
        return fundAccountRepository.save(fundAccount);
    }

    public FundAccount getFundAccountById(UUID id) {
        return fundAccountRepository.findById(id).orElse(null);
    }
}
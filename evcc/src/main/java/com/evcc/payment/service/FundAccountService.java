package com.evcc.payment.service;

import com.evcc.payment.entity.FundAccount;
import com.evcc.payment.repository.FundAccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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

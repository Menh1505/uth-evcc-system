package com.evcc.evcc.service;

import com.evcc.evcc.entity.FundTxn;
import com.evcc.evcc.repository.FundTxnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FundTxnService {
    @Autowired
    private FundTxnRepository fundTxnRepository;

    public List<FundTxn> getAllFundTxns() {
        return fundTxnRepository.findAll();
    }

    public FundTxn saveFundTxn(FundTxn fundTxn) {
        return fundTxnRepository.save(fundTxn);
    }

    public FundTxn getFundTxnById(UUID id) {
        return fundTxnRepository.findById(id).orElse(null);
    }
}
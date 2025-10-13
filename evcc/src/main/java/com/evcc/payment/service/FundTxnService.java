package com.evcc.payment.service;

import com.evcc.payment.entity.FundTxn;
import com.evcc.payment.repository.FundTxnRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




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

package com.evcc.evcc.controller;

import com.evcc.evcc.entity.FundTxn;
import com.evcc.evcc.service.FundTxnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/fund-txns")
public class FundTxnController {
    @Autowired
    private FundTxnService fundTxnService;

    @GetMapping
    public List<FundTxn> getAllFundTxns() {
        return fundTxnService.getAllFundTxns();
    }

    @PostMapping
    public FundTxn saveFundTxn(@RequestBody FundTxn fundTxn) {
        return fundTxnService.saveFundTxn(fundTxn);
    }

    @GetMapping("/{id}")
    public FundTxn getFundTxnById(@PathVariable UUID id) {
        return fundTxnService.getFundTxnById(id);
    }
}
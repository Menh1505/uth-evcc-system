package com.evcc.payment.controller;

import com.evcc.payment.entity.FundTxn;
import com.evcc.payment.service.FundTxnService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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

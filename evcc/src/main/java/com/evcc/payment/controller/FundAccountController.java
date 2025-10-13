package com.evcc.payment.controller;

import com.evcc.payment.entity.FundAccount;
import com.evcc.payment.service.FundAccountService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/fund-accounts")
public class FundAccountController {
    @Autowired
    private FundAccountService fundAccountService;

    @GetMapping
    public List<FundAccount> getAllFundAccounts() {
        return fundAccountService.getAllFundAccounts();
    }

    @PostMapping
    public FundAccount saveFundAccount(@RequestBody FundAccount fundAccount) {
        return fundAccountService.saveFundAccount(fundAccount);
    }

    @GetMapping("/{id}")
    public FundAccount getFundAccountById(@PathVariable UUID id) {
        return fundAccountService.getFundAccountById(id);
    }
}

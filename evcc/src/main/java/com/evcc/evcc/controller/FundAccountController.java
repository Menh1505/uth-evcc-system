package com.evcc.evcc.controller;

import com.evcc.evcc.entity.FundAccount;
import com.evcc.evcc.service.FundAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
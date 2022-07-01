package com.example.paydaystock.service;

import com.example.paydaystock.resource.BalanceRequest;

public interface DepositService {
    void addBalanceForUser(BalanceRequest balanceRequest, Integer id);
}

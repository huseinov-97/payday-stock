package com.example.paydaystock.service;

import com.example.paydaystock.resource.BalanceRequest;

public interface BalanceService {
    void addBalanceForUser(BalanceRequest balanceRequest, Integer id);
}

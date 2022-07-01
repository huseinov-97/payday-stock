package com.example.paydaystock.controller;


import com.example.paydaystock.config.UserContext;
import com.example.paydaystock.resource.BalanceRequest;
import com.example.paydaystock.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService service;

    @PutMapping
    public ResponseEntity<Void> addBalance(@RequestBody @Valid BalanceRequest balanceRequest) {
        service.addBalanceForUser(balanceRequest, UserContext.getUser().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

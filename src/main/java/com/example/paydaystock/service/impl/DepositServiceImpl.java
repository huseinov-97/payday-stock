package com.example.paydaystock.service.impl;

import com.example.paydaystock.exception.NotFoundException;
import com.example.paydaystock.model.Balance;
import com.example.paydaystock.model.User;
import com.example.paydaystock.repository.BalanceRepository;
import com.example.paydaystock.repository.UserRepository;
import com.example.paydaystock.resource.BalanceRequest;
import com.example.paydaystock.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final BalanceRepository balanceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addBalanceForUser(BalanceRequest balanceRequest, Integer id) {

        Balance balance = new Balance();
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getBalance() == null) {
            user.setBalance(balance);
            userRepository.save(user);
        }

        if (user.getBalance().getCurrentBalance() == null) {
            balance.setCurrentBalance(BigDecimal.valueOf(0));
            balanceRepository.save(balance);
        }

        BigDecimal balanceUser = user.getBalance().getCurrentBalance().add(balanceRequest.getAddBalance());
        user.getBalance().setCurrentBalance(balanceUser);
        userRepository.save(user);
    }
}

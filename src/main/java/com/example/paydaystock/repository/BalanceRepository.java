package com.example.paydaystock.repository;

import com.example.paydaystock.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Integer> {
}

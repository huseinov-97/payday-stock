package com.example.paydaystock.repository;

import com.example.paydaystock.enums.Status;
import com.example.paydaystock.model.Stock;
import com.example.paydaystock.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    List<Stock> findByStatus(Status waitingForBuy);

    Optional<Stock> findByUser(User user);
}

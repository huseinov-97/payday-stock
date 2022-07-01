package com.example.paydaystock.repository;


import com.example.paydaystock.model.Balance;
import com.example.paydaystock.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    User findByBalance(Balance balance);

    User findByVerificationCode(String code);

    Optional<User> findByUsername(String name);
}

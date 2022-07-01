package com.example.paydaystock.repository;


import com.example.paydaystock.enums.RoleEnum;
import com.example.paydaystock.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(RoleEnum name);
}

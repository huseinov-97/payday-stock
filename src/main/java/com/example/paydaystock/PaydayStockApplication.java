package com.example.paydaystock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaydayStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaydayStockApplication.class, args);
    }

}

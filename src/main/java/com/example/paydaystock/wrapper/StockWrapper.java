package com.example.paydaystock.wrapper;

import lombok.RequiredArgsConstructor;
import yahoofinance.Stock;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class StockWrapper {

    //    @Autowired
//    private ModelMapper modelMapper;
    private final Stock stock;
    private final LocalDateTime lastAccess;

    public StockWrapper(Stock stock) {
        this.stock = stock;
        this.lastAccess = LocalDateTime.now();
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public Stock getStock() {
        return stock;
    }

//    public Ticker getStocks(){
//        return modelMapper.map(stock, Ticker.class);
//    }
}
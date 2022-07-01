package com.example.paydaystock.service;

import com.example.paydaystock.resource.BuySellStockRequest;
import com.example.paydaystock.resource.Ticker;
import com.example.paydaystock.wrapper.StockWrapper;

import java.io.IOException;
import java.util.List;

public interface StockService {

    StockWrapper findStock(String symbol);

    List<Ticker> findStocks(List<String> symblos) throws IOException;

    String sellStock(BuySellStockRequest buySellStockRequest);

    String buyStock(BuySellStockRequest buySellStockRequest);
}

package com.example.paydaystock.service.impl;

import com.example.paydaystock.service.RefreshService;
import com.example.paydaystock.wrapper.StockWrapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

@Service
public class RefreshServiceImpl implements RefreshService {

//    private final Map<StockWrapper, Boolean> stocksToRefresh;
//
//    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//    private static final Duration refreshPeriod = Duration.ofSeconds(15);
//
//    public RefreshServiceImpl(){
//        stocksToRefresh = new HashMap<>();
//        setRefresh15Minutes();
//    }
//
//    public boolean shouldRefresh(final StockWrapper stockWrapper){
//        if (!stocksToRefresh.containsKey(stockWrapper)){
//            stocksToRefresh.put(stockWrapper, false);
//            return true;
//        }
//        return stocksToRefresh.get(stockWrapper);
//    }
//
//    private void setRefresh15Minutes(){
//        scheduler.scheduleAtFixedRate(() ->
//                stocksToRefresh.forEach((stock, value) -> {
//                    if (stock.getLastAccess().isBefore(LocalDateTime.now().minus(refreshPeriod))){
//                        System.out.println("Setting should refresh" + stock.getStock().getSymbol());
//                        stocksToRefresh.remove(stock);
//                        stocksToRefresh.put(stock, true);
//                    }
//                }),0, 15, SECONDS);
//    }
}

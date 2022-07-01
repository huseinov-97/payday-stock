package com.example.paydaystock.controller;

import com.example.paydaystock.resource.BuySellStockRequest;
import com.example.paydaystock.resource.Ticker;
import com.example.paydaystock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService service;

    @PostMapping
    BigDecimal getBySymbol() {
        return service.findStock("TSLA").getStock().getQuote().getPrice();
    }

    @PostMapping("/all")
    List<Ticker> findAll() throws IOException {
        List<String> stocks = new ArrayList<>();
        stocks.add("TSLA");
        stocks.add("GOOG");
        stocks.add("AMZN");
        stocks.add("ABNB");
        stocks.add("ADBE");
        stocks.add("AAPL");
        return service.findStocks(stocks);
    }

    @PostMapping("/buy")
    public String buyStock(@RequestBody @Valid BuySellStockRequest buySellStockRequest){
        return service.buyStock(buySellStockRequest);
    }

    @PostMapping("/sell")
    public String sellStock(@RequestBody @Valid BuySellStockRequest buySellStockRequest){
        return service.sellStock(buySellStockRequest);
    }

}

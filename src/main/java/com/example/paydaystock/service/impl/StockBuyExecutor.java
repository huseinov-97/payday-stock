package com.example.paydaystock.service.impl;

import com.example.paydaystock.enums.Status;
import com.example.paydaystock.model.Stock;
import com.example.paydaystock.repository.StockRepository;
import com.example.paydaystock.service.MailService;
import com.example.paydaystock.service.StockService;
import com.example.paydaystock.wrapper.StockWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StockBuyExecutor {

    private final StockRepository stockRepository;
    private final StockService stockService;
    private final MailService mailService;


    @Scheduled(initialDelay = 300,fixedDelay = 60000)
    public void executor() {
        List<Stock> stockList = stockRepository.findByStatus(Status.WAITING_FOR_BUY);
        if (stockList.isEmpty()){
            return;
        }
        for (Stock stock : stockList){
            StockWrapper stockWrapper = stockService.findStock(stock.getSymbol());

            if (stockWrapper.getStock().getQuote().getPrice().compareTo(stock.getTargetPrice()) < 0 ||
                    stockWrapper.getStock().getQuote().getPrice().compareTo(stock.getTargetPrice()) == 0 ) {
                stock.setStatus(Status.BOUGHT);
                BigDecimal i = stock.getUser().getBalance().getCurrentBalance().subtract(stock.getTargetPrice());
                stock.getUser().getBalance().setCurrentBalance(i);
                stockRepository.save(stock);
                mailService.sendWithoutAttachment(stock.getUser().getEmail(),
                        "mhuseinov7@gmail.com",
                        "your stock buying process is done.");
            }
        }
    }

    @Scheduled(initialDelay = 300, fixedDelay = 15000)
    public void sellExecutor() {
        List<Stock> stockList = stockRepository.findByStatus(Status.WAITING_FOR_SELL);
        if (stockList.isEmpty()){
            return;
        }
        for (Stock stock : stockList){
            StockWrapper stockWrapper = stockService.findStock(stock.getSymbol());

            BigDecimal yahooPrice = stockWrapper.getStock().getQuote().getPrice();
            BigDecimal targetPrice = stock.getTargetPrice();


            if (yahooPrice.compareTo(targetPrice) > 0 ||
                    yahooPrice.compareTo(targetPrice) == 0 ) {
                stock.setStatus(Status.SOLD);

                BigDecimal currentBalance = stock.getUser().getBalance().getCurrentBalance();

                BigDecimal amount = BigDecimal.valueOf(stock.getAmount());

                BigDecimal add = currentBalance.add(yahooPrice.multiply(amount));

                stock.getUser().getBalance().setCurrentBalance(add);
                stock.setAmount(0);
                stockRepository.save(stock);
                mailService
                        .sendWithoutAttachment(stock.getUser().getEmail(),
                                "mhuseinov7@gmail.com",
                                "your stock selling process is done.");
            }
        }
    }
}

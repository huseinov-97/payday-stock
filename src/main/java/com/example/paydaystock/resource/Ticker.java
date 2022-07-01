package com.example.paydaystock.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticker {
    private String symbol;
    private String name;
    private TickerQuote quote;

}

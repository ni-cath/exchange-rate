package com.ni.cath.exchange_rate_service.web.controller;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class TestControllerCommonUtils {
    static String getUrl(int port) {
        return "http://localhost:" + port;
    }

    static URI buildUriForStatistic(int port) {
        return UriComponentsBuilder.fromUriString(getUrl(port)).path("/currencies")
                .build().toUri();
    }

    static URI buildUriForExchange(int port, String baseCurrency, Double amount, String targetCurrency) {
        return UriComponentsBuilder.fromUriString(getUrl(port)).path("/exchange")
                .queryParam("from", baseCurrency)
                .queryParam("amount", amount)
                .queryParam("to",targetCurrency)
                .build().toUri();
    }

    static URI buildUriForQuote(int port, String baseCurrency, String targetCurrency) {
        return UriComponentsBuilder.fromUriString(getUrl(port)).path("/quote")
                .queryParam("from", baseCurrency)
                .queryParam("to",targetCurrency)
                .build().toUri();
    }
}

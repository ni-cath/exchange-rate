package com.ni.cath.exchange_rate_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.ni.cath.exchange_rate_service.exception.InvalidCurrencyRateException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class QuotesServiceTest {
    @InjectMocks
    private QuotesService quotesService;

    @Test
    void getQuote_GivenEmptyRateList_ExpectException() {
        assertThrows(InvalidCurrencyRateException.class, () -> quotesService.getQuoteForTheCurrency("EUR"));
    }

    @Test
    void getQuote_GivenInvalidCurrencyCode_ExpectException() {
        assertThrows(InvalidCurrencyRateException.class, () -> quotesService.getQuoteForTheCurrency("ER"));
    }

    @Test
    void getQuote_GivenValidCurrency_ExpectRate() {
        Map<String, BigDecimal> quotes = new HashMap<>();
        quotes.putIfAbsent("USD", BigDecimal.valueOf(1.0396));
        quotes.putIfAbsent("GBR", BigDecimal.valueOf(0.83723));
        ReflectionTestUtils.setField(quotesService, "quotesEUR", quotes);

        quotesService.getQuoteForTheCurrency("USD");
        assertEquals(0, BigDecimal.valueOf(1.0396).compareTo(quotesService.getQuoteForTheCurrency("USD")));
    }
}
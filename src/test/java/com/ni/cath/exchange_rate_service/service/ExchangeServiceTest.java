package com.ni.cath.exchange_rate_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static com.ni.cath.exchange_rate_service.service.TestRoundCommonUtils.getMathContext;
import static com.ni.cath.exchange_rate_service.service.TestRoundCommonUtils.round;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceTest {

    @InjectMocks
    ExchangingService exchangingService;

    @Mock
    QuotesService quotesService;

    @Test
    void getQuote_GivenEURAndNonEURCurrency_ExpectOriginalRate() {
        BigDecimal quote = BigDecimal.valueOf(1.0396);
        when(quotesService.getQuoteForTheCurrency("USD")).thenReturn(quote);
        BigDecimal resultQuote = exchangingService.getQuote("EUR", "USD");

        assertEquals(0, quote.compareTo(resultQuote));
    }

    @Test
    void getQuote_GivenNonEURCurrencies_ExpectCalculatedRate() {
        BigDecimal usdQuote = BigDecimal.valueOf(1.0396);
        BigDecimal gbpQuote = BigDecimal.valueOf(0.83723);
        BigDecimal quote = round(gbpQuote.divide(usdQuote, getMathContext()));
        when(quotesService.getQuoteForTheCurrency("USD")).thenReturn(usdQuote);
        when(quotesService.getQuoteForTheCurrency("GBP")).thenReturn(gbpQuote);
        BigDecimal result = exchangingService.getQuote("USD", "GBP");

        // usd to eur 1 / 1.0396 -> convert to gbr * 0.83723
        assertEquals(0, quote.compareTo(result));
    }
}

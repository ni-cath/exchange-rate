package com.ni.cath.exchange_rate_service.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ni.cath.exchange_rate_service.service.ExchangingService;
import com.ni.cath.exchange_rate_service.web.dto.ConversionDto;
import com.ni.cath.exchange_rate_service.web.dto.CurrencyStatisticDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static com.ni.cath.exchange_rate_service.service.TestRoundCommonUtils.getMathContext;
import static com.ni.cath.exchange_rate_service.service.TestRoundCommonUtils.round;

@ExtendWith(MockitoExtension.class)
class ExchangeRateControllerUnitTest {

    @InjectMocks
    private ExchangeRateController controller;

    @Mock
    private ExchangingService exchangingService;

    @Test
    void getSupportedCurrencies_GivenNonEmptyCurrencyStatMap_ExpectDataFromTheMap() {
        Map<String, Integer> statisticMap = new HashMap<>();
        statisticMap.put("USD", 0);
        statisticMap.put("JPY", 0);
        statisticMap.put("BGN", 0);
        when(exchangingService.getQuotesStatistic()).thenReturn(statisticMap);

        Set<CurrencyStatisticDto> supportedCurrencies = controller.getSupportedCurrencies().getSupportedCurrencies();
        assertEquals(3, supportedCurrencies.size());
        assertTrue(supportedCurrencies.contains(new CurrencyStatisticDto("USD", 0)));
        assertTrue(supportedCurrencies.contains(new CurrencyStatisticDto("JPY", 0)));
        assertTrue(supportedCurrencies.contains(new CurrencyStatisticDto("BGN", 0)));
    }

    @Test
    void getQuote_GivenValidNonEurCurrencies_ExpectCalculatedValue() {
        BigDecimal eurToJpy = new BigDecimal("161.50");
        BigDecimal eurToUsd = new BigDecimal("1.0396");
        BigDecimal jpyToUsd = eurToUsd.divide(eurToJpy, getMathContext());

        // JPY to eur -> 1/161.50, eur to usd * 1.0396
        when(exchangingService.getQuote( "JPY", "USD")).thenReturn(jpyToUsd);

        assertEquals(jpyToUsd.doubleValue(), controller.getQuote("JPY", "USD"));
    }

    @Test
    void getQuote_GivenValidEurAndNonEurCurrency_ExpectCalculatedValue() {
        BigDecimal usdQuote = BigDecimal.valueOf(1.0396);
        BigDecimal eurQuote = BigDecimal.ONE.divide(BigDecimal.valueOf(1.0396), getMathContext());

        when(exchangingService.getQuote( "EUR", "USD"))
                .thenReturn(usdQuote);
        when(exchangingService.getQuote( "USD", "EUR"))
                .thenReturn(eurQuote);

        assertEquals(usdQuote.doubleValue(), controller.getQuote("EUR", "USD"));
        assertEquals(eurQuote.doubleValue(), controller.getQuote("USD", "EUR"));
    }

    @Test
    void exchange_GivenZeroAmount_ExpectZeroConvertedAmount() {
        when(exchangingService.exchange("EUR", BigDecimal.ZERO, "USD"))
                .thenReturn(BigDecimal.ZERO);

        ConversionDto request = new ConversionDto("EUR", 0, "USD", 123);
        ConversionDto response = new ConversionDto("EUR", 0, "USD", 0);
        assertEquals(response, controller.exchange(request.getBaseCurrency(), request.getAmountToConvert(), request.getTargetCurrency()));
    }

    @Test
    void exchange_GivenNonZeroAmount_ExpectValidConvertedAmount() {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal result = BigDecimal.valueOf(100).multiply(new BigDecimal("1.0396"));
        when(exchangingService.exchange("EUR", amount, "USD"))
                .thenReturn(round(result));

        ConversionDto response = new ConversionDto("EUR", 100, "USD", result.doubleValue());
        assertEquals(response, controller.exchange("EUR", amount.doubleValue(), "USD"));
    }
}
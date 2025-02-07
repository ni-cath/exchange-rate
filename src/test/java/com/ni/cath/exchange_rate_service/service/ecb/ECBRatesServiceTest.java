package com.ni.cath.exchange_rate_service.service.ecb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ni.cath.exchange_rate_service.configuration.ECBQuotesConfiguration;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ECBRatesServiceTest {

    @InjectMocks
    ECBRatesService ecbRatesService;

    @Mock
    ECBQuotesConfiguration configuration;

    @Test
    void getQuotes_GivenInvalidUrl_ExpectEmptyList() {
        when(configuration.getTimeoutMs()).thenReturn(10000);
        when(configuration.getRetryNumber()).thenReturn(3);
        when(configuration.getEcbUrl()).thenReturn("http://somehost:8080/");

        List<ExchangeRate> exchangeRates = ecbRatesService.getQuotes();
        Assertions.assertTrue(exchangeRates.isEmpty());
    }

    @Test
    void getQuotes_GivenValidUrl_ExpectValidQuotes() {
        //todo: use mockserver to mock the response from ECB or change the structure of class to mock httpclient
    }
}
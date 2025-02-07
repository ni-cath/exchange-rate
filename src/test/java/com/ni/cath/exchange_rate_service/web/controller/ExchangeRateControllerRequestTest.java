package com.ni.cath.exchange_rate_service.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.ni.cath.exchange_rate_service.web.dto.ConversionDto;
import com.ni.cath.exchange_rate_service.web.dto.CurrencyStatisticSetDto;

import static org.assertj.core.api.Assertions.assertThat;
import static com.ni.cath.exchange_rate_service.web.controller.TestControllerCommonUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeRateControllerRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    @Test
    void getQuote_GivenValidCurrencies_ShouldReturnDoubleNonZeroValue() {
        Double quote = template.getForObject(buildUriForQuote(port, "USD", "EUR"), Double.class);
        assertThat(quote.doubleValue()).isGreaterThan(0.);
    }

    @Test
    void getQuote_GivenValidCurrenciesLowCase_ShouldReturnDoubleNonZeroValue() {
        Double quote = template.getForObject(buildUriForQuote(port, "usd", "eur"), Double.class);
        assertThat(quote.doubleValue()).isGreaterThan(0.);
    }

    @Test
    void getQuote_GivenInvalidCurrencies_ShouldReturnErrorDescription() {
        String errorDesc = template.getForObject(buildUriForQuote(port, "USSSD", "EUR"), String.class);
        assertThat(errorDesc).contains("Currency code USSSD not supported or have invalid rate");
    }

    @Test
    void getQuote_EmptyParams_ShouldReturnErrorDescription() {
        String errorDesc = template.getForObject(buildUriForQuote(port, "", ""), String.class);
        assertThat(errorDesc).contains("Bad request: Currency codes must be presented");
    }

    @Test
    void getSupportedCurrencies_ShouldReturnCurrencyListWithStatistic() {
        CurrencyStatisticSetDto resultSet = template.getForObject(buildUriForStatistic(port), CurrencyStatisticSetDto.class);
        assertThat(resultSet.getSupportedCurrencies().size()).isGreaterThan(0);
    }

    @Test
    void exchange_GivenEmptyParam_ShouldReturnErrorDesc() {
        String errorDesc = template.getForObject(buildUriForExchange(port, "", 100., ""), String.class);
        assertThat(errorDesc).contains("Bad request: Currency codes must be presented");
    }

    @Test
    void exchange_GivenValidCurrencies_ShouldReturnExchangeRate() {
        ConversionDto response = template.getForObject(buildUriForExchange(port, "EUR", 10., "USD"), ConversionDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getBaseCurrency()).isEqualTo("EUR");
        assertThat(response.getTargetCurrency()).isEqualTo("USD");
        assertThat(response.getAmountToConvert()).isEqualTo(10);
        assertThat(response.getConvertedAmount()).isGreaterThan(0);
    }

    @Test
    void exchange_GivenValidSpacesInBody_ShouldReturnExchangeRate() {
        ConversionDto response = template.getForObject(buildUriForExchange(port, "  eur ", 10., "uSd  "), ConversionDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getBaseCurrency()).isEqualTo("EUR");
        assertThat(response.getTargetCurrency()).isEqualTo("USD");
        assertThat(response.getAmountToConvert()).isEqualTo(10);
        assertThat(response.getConvertedAmount()).isGreaterThan(0);
    }

    @Test
    void exchange_GivenValidCurrenciesAndZeroAmount_ShouldReturnZeroConvertedAmount() {
        ConversionDto response = template.getForObject(buildUriForExchange(port, "EUR", 0., "USD"), ConversionDto.class);

        assertThat(response.getBaseCurrency()).isEqualTo("EUR");
        assertThat(response.getTargetCurrency()).isEqualTo("USD");
        assertThat(response.getAmountToConvert()).isEqualTo(0);
        assertThat(response.getConvertedAmount()).isEqualTo(0);
    }

    @Test
    void exchange_GivenInvalidCurrency_ShouldReturnErrorDesc() {
        String errorDesc = template.getForObject(buildUriForExchange(port, "EUUR", 0., "USD"), String.class);

        assertThat(errorDesc).contains("Currency code EUUR not supported or have invalid rate");
    }
}

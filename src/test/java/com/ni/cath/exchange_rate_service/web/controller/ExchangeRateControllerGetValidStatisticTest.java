package com.ni.cath.exchange_rate_service.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.ni.cath.exchange_rate_service.web.dto.CurrencyStatisticDto;
import com.ni.cath.exchange_rate_service.web.dto.CurrencyStatisticSetDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static com.ni.cath.exchange_rate_service.web.controller.TestControllerCommonUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeRateControllerGetValidStatisticTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    @Test
    void getSupportedCurrencies_GivenPreviousCalls_ShouldReturnCurrencyListWithValidStatistic() {
        template.getForObject(buildUriForQuote(port, "USD", "GBP"), Double.class);
        template.getForObject(buildUriForQuote(port, "JPY", "USD"), Double.class);

        CurrencyStatisticSetDto resultSet = template.getForObject(buildUriForStatistic(port), CurrencyStatisticSetDto.class);
        Optional<CurrencyStatisticDto> nok = resultSet.getSupportedCurrencies()
                .stream()
                .filter(it -> it.getCurrencyCode().equals("NOK"))
                .findAny();
        Optional<CurrencyStatisticDto> usd = resultSet.getSupportedCurrencies()
                .stream()
                .filter(it -> it.getCurrencyCode().equals("USD"))
                .findAny();
        Optional<CurrencyStatisticDto> gbp = resultSet.getSupportedCurrencies()
                .stream()
                .filter(it -> it.getCurrencyCode().equals("GBP"))
                .findAny();
        Optional<CurrencyStatisticDto> jpy = resultSet.getSupportedCurrencies()
                .stream()
                .filter(it -> it.getCurrencyCode().equals("JPY"))
                .findAny();

        assertThat(nok.isPresent()).isTrue();
        assertThat(nok.get().getNumberOfCall()).isEqualTo(0);

        assertThat(usd.isPresent()).isTrue();
        assertThat(usd.get().getNumberOfCall()).isEqualTo(2);

        assertThat(gbp.isPresent()).isTrue();
        assertThat(gbp.get().getNumberOfCall()).isEqualTo(1);

        assertThat(jpy.isPresent()).isTrue();
        assertThat(jpy.get().getNumberOfCall()).isEqualTo(1);
    }
}

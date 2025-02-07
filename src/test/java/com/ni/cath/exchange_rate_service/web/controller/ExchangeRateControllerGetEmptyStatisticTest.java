package com.ni.cath.exchange_rate_service.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import com.ni.cath.exchange_rate_service.web.dto.CurrencyStatisticSetDto;

import static org.assertj.core.api.Assertions.assertThat;
import static com.ni.cath.exchange_rate_service.web.controller.TestControllerCommonUtils.buildUriForStatistic;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExchangeRateControllerGetEmptyStatisticTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    @Test
    void getSupportedCurrencies_GivenNoPreviousCalls_ShouldReturnCurrencyListWithZeroStatistic() {
        CurrencyStatisticSetDto resultSet = template.getForObject(buildUriForStatistic(port), CurrencyStatisticSetDto.class);

        assertThat(resultSet.getSupportedCurrencies().size())
                .isGreaterThan(0);
        assertThat(resultSet.getSupportedCurrencies()
                .stream()
                .allMatch(it -> it.getNumberOfCall() == 0)).isTrue();
    }
}

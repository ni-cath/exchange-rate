package com.ni.cath.exchange_rate_service.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ni.cath.exchange_rate_service.service.ExchangingService;
import com.ni.cath.exchange_rate_service.web.dto.ConversionDto;
import com.ni.cath.exchange_rate_service.web.dto.CurrencyStatisticDto;
import com.ni.cath.exchange_rate_service.web.dto.CurrencyStatisticSetDto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@Tag(name = "Exchange Rate API", description = "API for currency exchange rate services")
@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(examples = {@ExampleObject(value = "Internal server error: {message description}")}))
public class ExchangeRateController {

    @Autowired
    private final ExchangingService exchangingService;

    public ExchangeRateController(ExchangingService exchangingService) {
        this.exchangingService = exchangingService;
    }

    @Operation(summary = "Get currencies statistic", description = "Retrieves the list of supported currencies and their call statistics")
    @ApiResponse(responseCode = "200", description = "Successful retrieval",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyStatisticSetDto.class)))
    @GetMapping("/currencies")
    @ResponseBody
    public CurrencyStatisticSetDto getSupportedCurrencies() {
        Map<String, Integer> quotesStatistic = exchangingService.getQuotesStatistic();

        Set<CurrencyStatisticDto> response = new HashSet<>(quotesStatistic.size());
        for(Map.Entry<String, Integer> entry: quotesStatistic.entrySet()) {
            response.add(new CurrencyStatisticDto(entry.getKey(), entry.getValue()));
        }

        return new CurrencyStatisticSetDto(response);
    }

    @Operation(summary = "Get exchange quote", description = "Retrieves the exchange rate between two currencies")
    @ApiResponse(responseCode = "200", description = "Successful retrieval",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class, example = "0.9987")))
    @ApiResponse(responseCode = "400", description = "Invalid input parameters: currency codes in request params",
            content = @Content(examples = {@ExampleObject(value = "Bad request: Currency code EUUR not supported or have invalid rate")}))
    @GetMapping("/quote")
    @ResponseBody
    public Double getQuote(
            @RequestParam @Parameter(description = "base currency code", example = "USD") String from,
            @RequestParam @Parameter(description = "target currency code", example = "EUR") String to) {
        if (from.isBlank() || to.isBlank()) {
            throw new IllegalArgumentException("Currency codes must be presented");
        }

        String baseCurrency = from.trim().toUpperCase();
        String targetCurrency = to.trim().toUpperCase();

        return exchangingService.getQuote(baseCurrency, targetCurrency).doubleValue();
    }

    @Operation(summary = "Exchange currency", description = "Converts an amount from a base currency to a target currency")
    @ApiResponse(responseCode = "200", description = "Successful conversion",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConversionDto.class),
                    examples = {@ExampleObject(name="Convert 10.5 usd to euros", value = "{\"baseCurrency\":\"USD\",\"amountToConvert\":10.5,\"targetCurrency\":\"EUR\",\"convertedAmount\":10.1029}")}))
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(examples = {@ExampleObject(value = "Bad request: Currency code EUUR not supported or have invalid rate")}))
    @GetMapping("/exchange")
    @ResponseBody
    public ConversionDto exchange(
            @RequestParam @Parameter(description = "base currency code", example = "USD") String from,
            @RequestParam @Parameter(description = "amount to convert", example = "10.5") Double amount,
            @RequestParam @Parameter(description = "target currency code", example = "EUR") String to) {
        if (from.isBlank() || to.isBlank()) {
            throw new IllegalArgumentException("Currency codes must be presented");
        }
        String baseCurrency = from.trim().toUpperCase();
        String targetCurrency = to.trim().toUpperCase();

        return new ConversionDto(
                baseCurrency, amount, targetCurrency,
                exchangingService.exchange(baseCurrency, new BigDecimal(amount), targetCurrency).doubleValue()
        );
    }
}

package com.ni.cath.exchange_rate_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Service
public class ExchangingService {

    //todo: check if it works with HashMap()
    Map<String, LongAdder> quotesStatistic = new ConcurrentHashMap<>();

    //todo: add the property for configuring
    private static final int PRECISION = 4;

    @Autowired
    private final QuotesService quotesService;

    public ExchangingService(QuotesService quotesService) {
        this.quotesService = quotesService;
    }

    /**
     * Getting a rate for currencies
     * @param baseCurrencyCode - base currency code
     * @param targetCurrencyCode - target currency code
     * @return a quote for two currencies
     */
    public BigDecimal getQuote(String baseCurrencyCode, String targetCurrencyCode) {
        return exchange(baseCurrencyCode, BigDecimal.ONE, targetCurrencyCode);
    }

    /**
     * Exchange given amount in base currency to a new one in target
     * @param baseCurrencyCode - base currency code
     * @param amount - amount to change
     * @param targetCurrencyCode - target currency code
     * @return converted amount in target currency
     */
    //todo: add different rounding for quotes and for converted money
    public BigDecimal exchange(String baseCurrencyCode, BigDecimal amount, String targetCurrencyCode) {
        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            updateStatistic(targetCurrencyCode);
            return round(amount);
        }

        if (isEUR(baseCurrencyCode)) {
            updateStatistic(targetCurrencyCode);
            BigDecimal result = amount.multiply(quotesService.getQuoteForTheCurrency(targetCurrencyCode));
            return round(result);
        }

        if (isEUR(targetCurrencyCode)) {
            updateStatistic(baseCurrencyCode);
            BigDecimal result = amount.divide(quotesService.getQuoteForTheCurrency(baseCurrencyCode), getMathContext());
            return round(result);
        }

        updateStatistic(baseCurrencyCode);
        updateStatistic(targetCurrencyCode);

        BigDecimal result = quotesService.getQuoteForTheCurrency(targetCurrencyCode)
                .multiply(amount)
                .divide(quotesService.getQuoteForTheCurrency(baseCurrencyCode), getMathContext());

        return round(result);
    }

    /**
     * Get a statistic with a data with number of calls by each available currency
     * @return map with currency code and number of calls for the currency
     */
    // todo: providing sorting option based on currency statistic
    public Map<String, Integer> getQuotesStatistic() {
        Set<String> supportedCurrencies = quotesService.getAllCurrencyCodes();

        Map<String, Integer> supportedQuotesStatistic = new HashMap<>(supportedCurrencies.size());

        for(Map.Entry<String, LongAdder> entry: quotesStatistic.entrySet()) {
            supportedQuotesStatistic.putIfAbsent(entry.getKey(), entry.getValue().intValue());
        }

        for(String currencyCode: supportedCurrencies) {
            supportedQuotesStatistic.putIfAbsent(
                    currencyCode,
                    quotesStatistic.getOrDefault(currencyCode, new LongAdder()).intValue()
            );
        }

        return supportedQuotesStatistic;
    }

    private boolean isEUR(String code) {
        return Objects.equals(code.toUpperCase(), "EUR");
    }

    private void updateStatistic(String currencyCode) {
        quotesStatistic.computeIfAbsent(currencyCode, k -> new LongAdder()).increment();
    }

    private BigDecimal round(BigDecimal num) {
        return num.setScale(PRECISION, RoundingMode.HALF_EVEN);
    }

    private MathContext getMathContext() {
        return MathContext.DECIMAL128;
    }
}

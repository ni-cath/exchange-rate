package com.ni.cath.exchange_rate_service.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ni.cath.exchange_rate_service.exception.InvalidCurrencyRateException;
import com.ni.cath.exchange_rate_service.service.ecb.ECBRatesService;
import com.ni.cath.exchange_rate_service.service.ecb.ExchangeRate;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class QuotesService {

    private final Map<String, BigDecimal> quotesEUR = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(QuotesService.class);

    @Autowired
    private final ECBRatesService ecbRatesService;

    public QuotesService(ECBRatesService ecbRatesService) {
        this.ecbRatesService = ecbRatesService;
    }

    /**
     * Try to update rate map with ECB service or use default values
     */
    @PostConstruct
    public void loadQuotes() {
        List<ExchangeRate> exchangeRates = ecbRatesService.getQuotes();
        for (ExchangeRate rate : exchangeRates) {
            quotesEUR.putIfAbsent(rate.getCurrency(), BigDecimal.valueOf(rate.getRate()));
        }

        if (quotesEUR.isEmpty()) {
            logger.warn("Quotes are empty after loading new values from ECB, default values was used");
            setCurrenciesWithZeroQuotes();
        }
    }


    //todo: save the previous values from ECB and use it when the service is not available
    private void setCurrenciesWithZeroQuotes() {
        Set<Currency> currencies = Currency.getAvailableCurrencies();
        for (Currency currency : currencies) {
            quotesEUR.putIfAbsent(currency.getCurrencyCode(), BigDecimal.ZERO);
        }
    }

    /**
     * Getting exchange rate for the currency from map
     * @param currencyCode - currency code
     * @return exchange rate
     * @see QuotesService#quotesEUR
     */
    public BigDecimal getQuoteForTheCurrency(String currencyCode) {
        BigDecimal rate = quotesEUR.getOrDefault(currencyCode, BigDecimal.ZERO);

        if (rate.equals(BigDecimal.ZERO)) {
            throw new InvalidCurrencyRateException(MessageFormat.format("Currency code {0} not supported or have invalid rate", currencyCode));
        }

        return rate;
    }

    public Set<String> getAllCurrencyCodes() {
        return quotesEUR.keySet();
    }
}

package com.ni.cath.exchange_rate_service.service.ecb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ni.cath.exchange_rate_service.configuration.ECBQuotesConfiguration;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for getting actual ECB rates, loads quotes during application startup
 */
@Service
public class ECBRatesService {

    private final Logger logger = LoggerFactory.getLogger(ECBRatesService.class);

    @Autowired
    private final ECBQuotesConfiguration configuration;

    public ECBRatesService(ECBQuotesConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Loads quotes from ECB
     * @return list with pairs of currency code and rate
     * @see ECBQuotesConfiguration#getEcbUrl() ECB url
     */
    public List<ExchangeRate> getQuotes() {
        try(CloseableHttpClient httpClient = getHttpClient()) {
            HttpGet request = new HttpGet(URI.create(configuration.getEcbUrl()));
            CloseableHttpResponse response = httpClient.execute(request);

            String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return parseResponse(responseString);

        } catch (IOException e) {
            logger.error("Error while getting response from ECB service {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClientBuilder
                .create()
                .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(configuration.getTimeoutMs()).build())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(configuration.getRetryNumber(), true))
                .build();
    }

    private List<ExchangeRate> parseResponse(String responseString) {
        XmlMapper xmlMapper = new XmlMapper();
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        try {
            ECBResponse parsedResponse = xmlMapper.readValue(responseString, ECBResponse.class);
            ECBResponse.ExchangeRates exchangeRateInfo = parsedResponse.getExchangeRatesWrapper().getExchangeRates().getFirst();

            if (exchangeRateInfo == null || exchangeRateInfo.getTime() == null) {
                logger.error("Couldn't parse the response from ECB service, unexpected response body");
                return exchangeRates;
            }

            logger.info("Response received from ECB service {} with {} rates", exchangeRateInfo.getTime(), exchangeRateInfo.getRates().size());
            exchangeRates = exchangeRateInfo.getRates();

        } catch (JsonProcessingException e) {
            logger.error("Couldn't parse the response from ECB service, quotes wasn't received");
        }

        return exchangeRates;
    }
}

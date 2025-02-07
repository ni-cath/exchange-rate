package com.ni.cath.exchange_rate_service.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ECBQuotesConfiguration {

    @Value("${ecb.request.url}")
    private String ecbUrl;

    @Value("${ecb.request.retry.attempts}")
    private int retryNumber;

    @Value("${ecb.request.retry.timeout.milliseconds}")
    private int timeoutMs;
}

package com.ni.cath.exchange_rate_service.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for /currencies endpoint
 */
@Data
@AllArgsConstructor
public class CurrencyStatisticDto {
    private String currencyCode;
    private Integer numberOfCall;
}

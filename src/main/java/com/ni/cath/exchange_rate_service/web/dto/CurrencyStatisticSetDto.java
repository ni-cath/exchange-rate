package com.ni.cath.exchange_rate_service.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * DTO for /currencies endpoint
 */
@Data
@AllArgsConstructor
public class CurrencyStatisticSetDto {
    Set<CurrencyStatisticDto> supportedCurrencies;
}

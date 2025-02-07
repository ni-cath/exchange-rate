package com.ni.cath.exchange_rate_service.web.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DTO for /exchange endpoint
 */
@Data
public class ConversionDto {

    @NotBlank(message = "Base currency can not be empty")
//    @Schema(name = "Base currency code", example = "USD", requiredMode = Schema.RequiredMode.AUTO)
    private String baseCurrency;

    @NotNull(message = "Amount to convert can not be empty")
//    @Schema(name = "Amount to convert", example = "10.56", requiredMode = Schema.RequiredMode.AUTO)
    private double amountToConvert;

    @NotBlank(message = "Target currency can not be empty")
//    @Schema(name = "Target currency code", example = "EUR", requiredMode = Schema.RequiredMode.AUTO)
    private String targetCurrency;

    private double convertedAmount;

    public ConversionDto(String baseCurrency, double amountToConvert, String targetCurrency, double convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.amountToConvert = amountToConvert;
        this.targetCurrency = targetCurrency;
        this.convertedAmount = convertedAmount;
    }
}
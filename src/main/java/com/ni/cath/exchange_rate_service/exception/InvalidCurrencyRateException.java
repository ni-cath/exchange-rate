package com.ni.cath.exchange_rate_service.exception;

public class InvalidCurrencyRateException extends RuntimeException {
    public InvalidCurrencyRateException(String message) {
        super(message);
    }
}

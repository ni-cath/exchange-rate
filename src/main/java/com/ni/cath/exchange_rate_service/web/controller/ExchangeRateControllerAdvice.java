package com.ni.cath.exchange_rate_service.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ni.cath.exchange_rate_service.exception.InvalidCurrencyRateException;

@RestControllerAdvice
public class ExchangeRateControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidCurrencyRateException.class)
    public String handleUnsupportedException(Exception e) {
        return "Bad request: " + e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArguments(Exception e) {
        return "Bad request: " + e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeExceptionException(Exception e) {
        return "Internal server error: " + e.getMessage();
    }
}

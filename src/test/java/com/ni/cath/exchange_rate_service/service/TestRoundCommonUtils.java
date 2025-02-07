package com.ni.cath.exchange_rate_service.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class TestRoundCommonUtils {
    //todo: synchronize with actual value
    private static final int PRECISION = 4;

    public static BigDecimal round(BigDecimal num) {
        return num.setScale(PRECISION, RoundingMode.HALF_EVEN);
    }

    public static MathContext getMathContext() {
        return MathContext.DECIMAL128;
    }
}

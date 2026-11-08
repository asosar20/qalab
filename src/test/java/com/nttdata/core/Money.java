package com.nttdata.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {
    public static BigDecimal parse(String text) {
        String clean = text.replaceAll("[^0-9,.]", "");
        if (clean.contains(",") && clean.contains(".")) {
            clean = clean.replace(",", "");
        } else {
            clean = clean.replace(",", ".");
        }
        return new BigDecimal(clean).setScale(2, RoundingMode.HALF_UP);
    }
}
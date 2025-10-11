package com.example.bankcards.util;

public class CardMaskUtil {

    public static String applyMask(String cardNumber) {
        return "**** ".repeat(3)
               + cardNumber.substring(cardNumber.length() - 4);
    }
}

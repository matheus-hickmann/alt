package com.alt.card.service;

import com.alt.card.config.CardConstants;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates card numbers and masked representations.
 */
public final class CardNumberGenerator {

    private CardNumberGenerator() {}

    public static String generate() {
        StringBuilder sb = new StringBuilder(CardConstants.CARD_NUMBER_LENGTH);
        sb.append(CardConstants.BIN_EXAMPLE);
        for (int i = 0; i < CardConstants.RANDOM_DIGITS; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        return sb.toString();
    }

    public static String mask(String number) {
        if (number == null || number.length() < CardConstants.MASK_MIN_LENGTH) {
            return "****";
        }
        return number.substring(0, CardConstants.BIN_LENGTH)
                + CardConstants.MASK_PLACEHOLDER
                + number.substring(number.length() - CardConstants.BIN_LENGTH);
    }
}

package com.alt.card.exception;

/**
 * Thrown when a card is not found by identifier.
 */
public class CardNotFoundException extends IllegalArgumentException {

    public CardNotFoundException(String message) {
        super(message);
    }
}

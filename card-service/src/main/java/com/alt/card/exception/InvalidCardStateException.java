package com.alt.card.exception;

/**
 * Thrown when card is in invalid state for the requested operation (e.g. activating card not in DELIVERED status).
 */
public class InvalidCardStateException extends IllegalStateException {

    public InvalidCardStateException(String message) {
        super(message);
    }
}

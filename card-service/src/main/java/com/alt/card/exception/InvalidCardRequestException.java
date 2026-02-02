package com.alt.card.exception;

/**
 * Thrown when card request validation fails (e.g. missing or invalid card_id).
 */
public class InvalidCardRequestException extends IllegalArgumentException {

    public InvalidCardRequestException(String message) {
        super(message);
    }
}

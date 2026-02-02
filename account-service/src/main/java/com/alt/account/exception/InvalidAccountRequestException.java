package com.alt.account.exception;

/**
 * Thrown when account request validation fails (e.g. missing or invalid account_id).
 */
public class InvalidAccountRequestException extends IllegalArgumentException {

    public InvalidAccountRequestException(String message) {
        super(message);
    }
}

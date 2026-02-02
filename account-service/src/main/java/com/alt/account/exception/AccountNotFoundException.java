package com.alt.account.exception;

/**
 * Thrown when an account is not found by identifier.
 */
public class AccountNotFoundException extends IllegalArgumentException {

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String accountId, Throwable cause) {
        super("Account not found: " + accountId, cause);
    }
}

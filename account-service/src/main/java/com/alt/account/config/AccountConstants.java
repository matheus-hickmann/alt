package com.alt.account.config;

/**
 * Constants for account domain.
 */
public final class AccountConstants {

    private AccountConstants() {}

    public static final String ACCOUNT_ID_PREFIX = "acc_";
    public static final int ACCOUNT_NUMBER_LENGTH = 8;
    public static final int ACCOUNT_NUMBER_MAX = 10_000_000;
    public static final int ACCOUNT_NUMBER_MIN = 1;
    public static final int UNIQUE_NUMBER_MAX_ATTEMPTS = 100;
}

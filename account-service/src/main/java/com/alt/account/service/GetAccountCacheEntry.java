package com.alt.account.service;

import java.util.List;

/**
 * Cache value for getAccount (Redis, TTL 15 min).
 * Serializable to JSON for Redis backend.
 */
public record GetAccountCacheEntry(
        boolean found,
        GetAccountCacheValue value
) {
    public static GetAccountCacheEntry notFound() {
        return new GetAccountCacheEntry(false, null);
    }

    public static GetAccountCacheEntry of(GetAccountCacheValue value) {
        return new GetAccountCacheEntry(true, value);
    }

    public record GetAccountCacheValue(
            String accountId,
            String accountNumber,
            String name,
            String email,
            String document,
            String status,
            List<CardSummaryCache> cards
    ) {
    }

    public record CardSummaryCache(
            String id,
            String maskedNumber,
            String brand,
            String type
    ) {
    }
}

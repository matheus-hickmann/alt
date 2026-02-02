package com.alt.card.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

/**
 * Publishes to Kafka for account-service to invalidate getAccount cache (when a card is cancelled).
 */
@ApplicationScoped
public class AccountCacheInvalidationProducer {

    @Channel("invalidate-account-cache")
    Emitter<String> invalidateAccountCacheEmitter;

    /**
     * Sends account_id to the alt.invalidate-account-cache topic.
     * The account-service consumes and invalidates the getAccount cache for that account.
     */
    public void requestInvalidateAccountCache(String accountId) {
        if (accountId != null && !accountId.isBlank()) {
            invalidateAccountCacheEmitter.send(accountId);
        }
    }
}

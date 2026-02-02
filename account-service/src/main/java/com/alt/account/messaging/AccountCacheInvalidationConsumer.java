package com.alt.account.messaging;

import com.alt.account.service.AccountService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

/**
 * Kafka consumer: invalidates getAccount cache when card-service cancels a card (topic alt.invalidate-account-cache).
 */
@ApplicationScoped
public class AccountCacheInvalidationConsumer {

    private static final Logger log = LoggerFactory.getLogger(AccountCacheInvalidationConsumer.class);

    private final AccountService accountService;

    public AccountCacheInvalidationConsumer(AccountService accountService) {
        this.accountService = accountService;
    }

    @Incoming("invalidate-account-cache")
    public CompletionStage<Void> onInvalidateAccountCache(Message<String> message) {
        String accountId = message.getPayload();
        log.info("Kafka: invalidating getAccount cache for account {}", accountId);
        try {
            accountService.invalidateGetAccountCache(accountId);
            return message.ack();
        } catch (Exception e) {
            log.error("Error invalidating cache for account {}: {}", accountId, e.getMessage());
            return message.nack(e);
        }
    }
}

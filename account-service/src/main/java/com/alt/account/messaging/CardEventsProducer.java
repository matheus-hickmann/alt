package com.alt.account.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

/**
 * Publishes events for card-service to process asynchronously via Kafka.
 */
@ApplicationScoped
public class CardEventsProducer {

    @Channel("create-physical-card")
    Emitter<String> createPhysicalCardEmitter;

    @Channel("cancel-cards-by-account")
    Emitter<String> cancelCardsByAccountEmitter;

    /**
     * Sends request to create physical card (topic alt.create-physical-card).
     * The card-service consumes and creates the card in the database.
     */
    public void requestCreatePhysicalCard(String accountId) {
        createPhysicalCardEmitter.send(accountId);
    }

    /**
     * Sends request to cancel all cards for the account (topic alt.cancel-cards-by-account).
     * The card-service consumes and cancels the cards in the database.
     */
    public void requestCancelCardsByAccountId(String accountId) {
        cancelCardsByAccountEmitter.send(accountId);
    }
}

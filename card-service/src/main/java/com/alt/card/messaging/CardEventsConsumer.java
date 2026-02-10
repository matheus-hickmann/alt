package com.alt.card.messaging;

import com.alt.card.service.CardService;
import com.alt.proto.card.CancelCardsByAccountIdRpcRequest;
import com.alt.proto.card.CreatePhysicalCardRpcRequest;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

/**
 * Kafka consumer: creates physical card and cancels cards by account (events sent by account-service).
 */
@ApplicationScoped
public class CardEventsConsumer {

    private static final Logger log = LoggerFactory.getLogger(CardEventsConsumer.class);

    private final CardService cardService;

    public CardEventsConsumer(CardService cardService) {
        this.cardService = cardService;
    }

    @Incoming("create-physical-card")
    @Blocking
    public CompletionStage<Void> onCreatePhysicalCard(Message<String> message) {
        String accountId = message.getPayload();
        log.info("Kafka: creating physical card for account {}", accountId);
        try {
            cardService.createPhysicalCard(
                    CreatePhysicalCardRpcRequest.newBuilder().setAccountId(accountId).build());
            return message.ack();
        } catch (Exception e) {
            log.error("Error creating physical card for account {}: {}", accountId, e.getMessage());
            return message.nack(e);
        }
    }

    @Incoming("cancel-cards-by-account")
    @Blocking
    public CompletionStage<Void> onCancelCardsByAccountId(Message<String> message) {
        String accountId = message.getPayload();
        log.info("Kafka: cancelling cards for account {}", accountId);
        try {
            cardService.cancelCardsByAccountId(
                    CancelCardsByAccountIdRpcRequest.newBuilder().setAccountId(accountId).build());
            return message.ack();
        } catch (Exception e) {
            log.error("Error cancelling cards for account {}: {}", accountId, e.getMessage());
            return message.nack(e);
        }
    }
}

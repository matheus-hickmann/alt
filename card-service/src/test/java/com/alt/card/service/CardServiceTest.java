package com.alt.card.service;

import com.alt.card.entity.Card;
import com.alt.card.entity.CardStatus;
import com.alt.card.entity.CardType;
import com.alt.card.repository.CardRepository;
import com.alt.proto.card.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@QuarkusTest
@DisplayName("CardService")
class CardServiceTest {

    @Inject
    CardService cardService;

    @Inject
    CardRepository cardRepository;

    @Inject
    CardTestDataHelper testData;

    @InjectMock
    com.alt.card.messaging.AccountCacheInvalidationProducer accountCacheInvalidationProducer;

    @BeforeEach
    void setUp() {
        testData.deleteAll();
    }

    @Nested
    @DisplayName("createPhysicalCard")
    class CreatePhysicalCard {

        @Test
        void should_create_card_with_pending_delivery_status() {
            var request = CreatePhysicalCardRpcRequest.newBuilder()
                    .setAccountId("acc_123")
                    .build();

            var response = cardService.createPhysicalCard(request);

            assertThat(response.getId()).startsWith("card_");
            assertThat(response.getNumber()).hasSize(16);

            var cards = cardRepository.findByAccountId("acc_123");
            assertThat(cards).hasSize(1);
            assertThat(cards.get(0).status).isEqualTo(CardStatus.PENDING_DELIVERY);
            assertThat(cards.get(0).type).isEqualTo(CardType.PHYSICAL);
        }
    }

    @Nested
    @DisplayName("listCardsByAccountId")
    class ListCardsByAccountId {

        @Test
        void should_return_empty_when_account_id_blank() {
            var request = ListCardsByAccountIdRpcRequest.newBuilder().setAccountId("").build();

            var response = cardService.listCardsByAccountId(request);

            assertThat(response.getCardsList()).isEmpty();
        }

        @Test
        void should_return_only_active_cards() {
            var activeCard = new Card();
            activeCard.cardId = "card_active1";
            activeCard.accountId = "acc_list1";
            activeCard.maskedNumber = "4532********1111";
            activeCard.brand = "VISA";
            activeCard.type = CardType.PHYSICAL;
            activeCard.status = CardStatus.ACTIVE;
            activeCard.createdAt = Instant.now();
            activeCard.updatedAt = Instant.now();
            testData.persist(activeCard);

            var cancelledCard = new Card();
            cancelledCard.cardId = "card_cancelled1";
            cancelledCard.accountId = "acc_list1";
            cancelledCard.maskedNumber = "4532********2222";
            cancelledCard.brand = "VISA";
            cancelledCard.type = CardType.PHYSICAL;
            cancelledCard.status = CardStatus.CANCELLED;
            cancelledCard.createdAt = Instant.now();
            cancelledCard.updatedAt = Instant.now();
            testData.persist(cancelledCard);

            var request = ListCardsByAccountIdRpcRequest.newBuilder().setAccountId("acc_list1").build();
            var response = cardService.listCardsByAccountId(request);

            assertThat(response.getCardsList()).hasSize(1);
            assertThat(response.getCardsList().get(0).getId()).isEqualTo("card_active1");
        }
    }

    @Nested
    @DisplayName("cancelCard")
    class CancelCard {

        @Test
        void should_cancel_card_and_publish_cache_invalidation() {
            var card = new Card();
            card.cardId = "card_cancel1";
            card.accountId = "acc_cancel1";
            card.maskedNumber = "4532********3333";
            card.brand = "VISA";
            card.type = CardType.PHYSICAL;
            card.status = CardStatus.ACTIVE;
            card.createdAt = Instant.now();
            card.updatedAt = Instant.now();
            testData.persist(card);

            var request = CancelCardRpcRequest.newBuilder().setCardId("card_cancel1").build();
            cardService.cancelCard(request);

            var updated = cardRepository.findByCardId("card_cancel1").orElseThrow();
            assertThat(updated.status).isEqualTo(CardStatus.CANCELLED);

            verify(accountCacheInvalidationProducer).requestInvalidateAccountCache("acc_cancel1");
        }

        @Test
        void should_throw_when_card_id_blank() {
            var request = CancelCardRpcRequest.newBuilder().setCardId("").build();

            assertThatThrownBy(() -> cardService.cancelCard(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("card_id");
        }

        @Test
        void should_throw_when_card_not_found() {
            var request = CancelCardRpcRequest.newBuilder().setCardId("card_nonexistent").build();

            assertThatThrownBy(() -> cardService.cancelCard(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("activateCard")
    class ActivateCard {

        @Test
        void should_activate_card_when_delivered() {
            var card = new Card();
            card.cardId = "card_activate1";
            card.accountId = "acc_activate1";
            card.maskedNumber = "4532********4444";
            card.brand = "VISA";
            card.type = CardType.PHYSICAL;
            card.status = CardStatus.DELIVERED;
            card.createdAt = Instant.now();
            card.updatedAt = Instant.now();
            testData.persist(card);

            var request = ActivateCardRpcRequest.newBuilder().setCardId("card_activate1").build();
            cardService.activateCard(request);

            var updated = cardRepository.findByCardId("card_activate1").orElseThrow();
            assertThat(updated.status).isEqualTo(CardStatus.ACTIVE);

            verify(accountCacheInvalidationProducer).requestInvalidateAccountCache("acc_activate1");
        }

        @Test
        void should_throw_when_card_not_delivered() {
            var card = new Card();
            card.cardId = "card_pending1";
            card.accountId = "acc_pending1";
            card.maskedNumber = "4532********5555";
            card.brand = "VISA";
            card.type = CardType.PHYSICAL;
            card.status = CardStatus.PENDING_DELIVERY;
            card.createdAt = Instant.now();
            card.updatedAt = Instant.now();
            testData.persist(card);

            var request = ActivateCardRpcRequest.newBuilder().setCardId("card_pending1").build();

            assertThatThrownBy(() -> cardService.activateCard(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("DELIVERED");
        }
    }
}

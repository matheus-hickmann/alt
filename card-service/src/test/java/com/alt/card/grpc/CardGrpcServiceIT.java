package com.alt.card.grpc;

import com.alt.card.entity.Card;
import com.alt.card.entity.CardStatus;
import com.alt.card.entity.CardType;
import com.alt.card.repository.CardRepository;
import com.alt.proto.card.*;
import io.quarkus.grpc.GrpcClient;
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

@QuarkusTest
@DisplayName("CardGrpcService (integration)")
class CardGrpcServiceIT {

    @GrpcClient("self")
    CardServiceGrpc.CardServiceBlockingStub cardStub;

    @Inject
    CardRepository cardRepository;

    @InjectMock
    com.alt.card.messaging.AccountCacheInvalidationProducer accountCacheInvalidationProducer;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
    }

    @Nested
    @DisplayName("createPhysicalCard")
    class CreatePhysicalCard {

        @Test
        void should_create_card_via_grpc() {
            var request = CreatePhysicalCardRpcRequest.newBuilder()
                    .setAccountId("acc_grpc1")
                    .build();

            var response = cardStub.createPhysicalCard(request);

            assertThat(response.getId()).startsWith("card_");
            assertThat(response.getNumber()).hasSize(16);
            assertThat(cardRepository.findByAccountId("acc_grpc1")).hasSize(1);
        }
    }

    @Nested
    @DisplayName("listCardsByAccountId")
    class ListCardsByAccountId {

        @Test
        void should_return_cards_via_grpc() {
            var card = new Card();
            card.cardId = "card_grpc_list1";
            card.accountId = "acc_grpc_list1";
            card.maskedNumber = "4532********9999";
            card.brand = "VISA";
            card.type = CardType.PHYSICAL;
            card.status = CardStatus.ACTIVE;
            card.createdAt = Instant.now();
            card.updatedAt = Instant.now();
            card.persist();

            var request = ListCardsByAccountIdRpcRequest.newBuilder()
                    .setAccountId("acc_grpc_list1")
                    .build();
            var response = cardStub.listCardsByAccountId(request);

            assertThat(response.getCardsList()).hasSize(1);
            assertThat(response.getCardsList().get(0).getId()).isEqualTo("card_grpc_list1");
        }
    }

    @Nested
    @DisplayName("cancelCard")
    class CancelCard {

        @Test
        void should_cancel_card_via_grpc() {
            var card = new Card();
            card.cardId = "card_grpc_cancel1";
            card.accountId = "acc_grpc_cancel1";
            card.maskedNumber = "4532********8888";
            card.brand = "VISA";
            card.type = CardType.PHYSICAL;
            card.status = CardStatus.ACTIVE;
            card.createdAt = Instant.now();
            card.updatedAt = Instant.now();
            card.persist();

            var request = CancelCardRpcRequest.newBuilder().setCardId("card_grpc_cancel1").build();
            cardStub.cancelCard(request);

            var updated = cardRepository.findByCardId("card_grpc_cancel1").orElseThrow();
            assertThat(updated.status).isEqualTo(CardStatus.CANCELLED);
        }

        @Test
        void should_return_not_found_when_card_missing() {
            var request = CancelCardRpcRequest.newBuilder().setCardId("card_nonexistent").build();

            assertThatThrownBy(() -> cardStub.cancelCard(request))
                    .hasMessageContaining("NOT_FOUND");
        }
    }

    @Nested
    @DisplayName("activateCard")
    class ActivateCard {

        @Test
        void should_activate_card_via_grpc() {
            var card = new Card();
            card.cardId = "card_grpc_activate1";
            card.accountId = "acc_grpc_activate1";
            card.maskedNumber = "4532********7777";
            card.brand = "VISA";
            card.type = CardType.PHYSICAL;
            card.status = CardStatus.DELIVERED;
            card.createdAt = Instant.now();
            card.updatedAt = Instant.now();
            card.persist();

            var request = ActivateCardRpcRequest.newBuilder().setCardId("card_grpc_activate1").build();
            cardStub.activateCard(request);

            var updated = cardRepository.findByCardId("card_grpc_activate1").orElseThrow();
            assertThat(updated.status).isEqualTo(CardStatus.ACTIVE);
        }

        @Test
        void should_return_failed_precondition_when_card_not_delivered() {
            var card = new Card();
            card.cardId = "card_grpc_pending1";
            card.accountId = "acc_grpc_pending1";
            card.maskedNumber = "4532********6666";
            card.brand = "VISA";
            card.type = CardType.PHYSICAL;
            card.status = CardStatus.PENDING_DELIVERY;
            card.createdAt = Instant.now();
            card.updatedAt = Instant.now();
            card.persist();

            var request = ActivateCardRpcRequest.newBuilder().setCardId("card_grpc_pending1").build();

            assertThatThrownBy(() -> cardStub.activateCard(request))
                    .hasMessageContaining("FAILED_PRECONDITION");
        }
    }
}

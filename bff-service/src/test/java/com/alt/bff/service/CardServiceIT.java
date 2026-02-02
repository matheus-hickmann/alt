package com.alt.bff.service;

import com.alt.bff.client.CardClient;
import com.alt.proto.card.*;
import com.alt.bff.resource.dto.card.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("CardService (integration)")
class CardServiceIT {

    @Inject
    CardService cardService;

    @InjectMock
    CardClient cardClient;

    @Nested
    @DisplayName("issueVirtualCard")
    class IssueVirtualCard {

        @Test
        void should_integrate_mapper_and_client_returning_virtual_card() {
            var request = new VirtualCardRequest("acc_it_1");
            var rpcResponse = IssueVirtualCardRpcResponse.newBuilder()
                    .setId("virt-it-1")
                    .setNumber("4532999988887777")
                    .setMaskedNumber("4532********7777")
                    .setBrand("VISA")
                    .setExpirationDate("06/2029")
                    .setCardholderName("MARIA SANTOS")
                    .setCvv("456")
                    .build();
            when(cardClient.issueVirtualCard(any(IssueVirtualCardRpcRequest.class))).thenReturn(rpcResponse);

            var response = cardService.issueVirtualCard(request);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo("virt-it-1");
            assertThat(response.maskedNumber()).isEqualTo("4532********7777");
            assertThat(response.expirationDate()).isEqualTo("06/2029");
            assertThat(response.cvv()).isEqualTo("456");
            verify(cardClient).issueVirtualCard(any(IssueVirtualCardRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("reissueCard")
    class ReissueCard {

        @Test
        void should_integrate_mapper_and_client_returning_reissue_response() {
            var request = new ReissueCardRequest(ReissueReason.THEFT);
            var rpcResponse = ReissueCardRpcResponse.newBuilder()
                    .setId("card-reissue-it")
                    .setMaskedNumber("5502********8888")
                    .setBrand("MASTERCARD")
                    .build();
            when(cardClient.reissueCard(any(ReissueCardRpcRequest.class))).thenReturn(rpcResponse);

            var response = cardService.reissueCard("card_it_1", request);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo("card-reissue-it");
            assertThat(response.brand()).isEqualTo("MASTERCARD");
            verify(cardClient).reissueCard(any(ReissueCardRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("getFullCardData")
    class GetFullCardData {

        @Test
        void should_integrate_mapper_and_client_returning_card_full() {
            var cardId = "card_full_it";
            var rpcResponse = GetFullCardRpcResponse.newBuilder()
                    .setCardId(cardId)
                    .setBrand("VISA")
                    .setNumber("4532015112830366")
                    .setExpirationDate("12/2028")
                    .setCardholderName("JOAO SILVA")
                    .setCvv("789")
                    .build();
            when(cardClient.getFullCard(any(GetFullCardRpcRequest.class))).thenReturn(rpcResponse);

            var response = cardService.getFullCardData(cardId);

            assertThat(response).isNotNull();
            assertThat(response.cardId()).isEqualTo(cardId);
            assertThat(response.number()).isEqualTo("4532015112830366");
            assertThat(response.cardholderName()).isEqualTo("JOAO SILVA");
            verify(cardClient).getFullCard(any(GetFullCardRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("cancelCard")
    class CancelCard {

        @Test
        void should_call_client_with_no_return() {
            var cardId = "card_cancel_it";
            when(cardClient.cancelCard(any(CancelCardRpcRequest.class)))
                    .thenReturn(CancelCardRpcResponse.newBuilder().build());

            cardService.cancelCard(cardId);

            verify(cardClient).cancelCard(any(CancelCardRpcRequest.class));
        }
    }
}

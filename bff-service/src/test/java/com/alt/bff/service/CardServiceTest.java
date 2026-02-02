package com.alt.bff.service;

import com.alt.bff.client.CardClient;
import com.alt.bff.mapper.CardRpcMapper;
import com.alt.proto.card.*;
import com.alt.bff.resource.dto.card.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardService")
class CardServiceTest {

    @Mock
    CardClient cardClient;

    CardRpcMapper mapper = new CardRpcMapper();

    CardService cardService;

    @BeforeEach
    void setUp() {
        cardService = new CardService(cardClient, mapper);
    }

    @Nested
    @DisplayName("issueVirtualCard")
    class IssueVirtualCard {

        @Test
        void should_call_client_and_return_mapped_response() {
            var request = new VirtualCardRequest("acc_123");
            var rpcResponse = IssueVirtualCardRpcResponse.newBuilder()
                    .setId("virt-1")
                    .setNumber("4532015112830366")
                    .setMaskedNumber("4532********0366")
                    .setBrand("VISA")
                    .setExpirationDate("12/2028")
                    .setCardholderName("JOAO SILVA")
                    .setCvv("123")
                    .build();
            when(cardClient.issueVirtualCard(any(IssueVirtualCardRpcRequest.class))).thenReturn(rpcResponse);

            var response = cardService.issueVirtualCard(request);

            assertThat(response.id()).isEqualTo("virt-1");
            assertThat(response.number()).isEqualTo("4532015112830366");
            assertThat(response.brand()).isEqualTo("VISA");
            verify(cardClient).issueVirtualCard(any(IssueVirtualCardRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("reissueCard")
    class ReissueCard {

        @Test
        void should_call_client_and_return_mapped_response() {
            var request = new ReissueCardRequest(ReissueReason.LOSS);
            var rpcResponse = ReissueCardRpcResponse.newBuilder()
                    .setId("card-new-1")
                    .setMaskedNumber("4532********9999")
                    .setBrand("VISA")
                    .build();
            when(cardClient.reissueCard(any(ReissueCardRpcRequest.class))).thenReturn(rpcResponse);

            var response = cardService.reissueCard("card_1", request);

            assertThat(response.id()).isEqualTo("card-new-1");
            assertThat(response.maskedNumber()).isEqualTo("4532********9999");
            verify(cardClient).reissueCard(any(ReissueCardRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("getFullCardData")
    class GetFullCardData {

        @Test
        void should_call_client_and_return_mapped_card_full() {
            var cardId = "card_1";
            var rpcResponse = GetFullCardRpcResponse.newBuilder()
                    .setCardId(cardId)
                    .setBrand("VISA")
                    .setNumber("4532015112830366")
                    .setExpirationDate("12/2028")
                    .setCardholderName("JOAO SILVA")
                    .setCvv("123")
                    .build();
            when(cardClient.getFullCard(any(GetFullCardRpcRequest.class))).thenReturn(rpcResponse);

            var response = cardService.getFullCardData(cardId);

            assertThat(response.cardId()).isEqualTo(cardId);
            assertThat(response.brand()).isEqualTo("VISA");
            assertThat(response.number()).isEqualTo("4532015112830366");
            assertThat(response.cvv()).isEqualTo("123");
            verify(cardClient).getFullCard(any(GetFullCardRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("cancelCard")
    class CancelCard {

        @Test
        void should_call_client_cancel_card() {
            var cardId = "card_456";
            when(cardClient.cancelCard(any(CancelCardRpcRequest.class)))
                    .thenReturn(CancelCardRpcResponse.newBuilder().build());

            cardService.cancelCard(cardId);

            verify(cardClient).cancelCard(any(CancelCardRpcRequest.class));
        }
    }
}

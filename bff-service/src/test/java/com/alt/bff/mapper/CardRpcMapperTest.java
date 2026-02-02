package com.alt.bff.mapper;

import com.alt.proto.card.*;
import com.alt.bff.resource.dto.card.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CardRpcMapper")
class CardRpcMapperTest {

    CardRpcMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CardRpcMapper();
    }

    @Nested
    @DisplayName("toIssueVirtualCardRpcRequest")
    class ToIssueVirtualCardRpcRequest {

        @Test
        void should_map_account_id() {
            var request = new VirtualCardRequest("acc_123");

            var rpc = mapper.toIssueVirtualCardRpcRequest(request);

            assertThat(rpc.getAccountId()).isEqualTo("acc_123");
        }
    }

    @Nested
    @DisplayName("toReissueCardRpcRequest")
    class ToReissueCardRpcRequest {

        @Test
        void should_map_card_id_and_reason_LOSS() {
            var request = new ReissueCardRequest(ReissueReason.LOSS);

            var rpc = mapper.toReissueCardRpcRequest("card_1", request);

            assertThat(rpc.getCardId()).isEqualTo("card_1");
            assertThat(rpc.getReason()).isEqualTo(ReissueReasonRpc.LOSS);
        }

        @Test
        void should_map_reason_THEFT() {
            var request = new ReissueCardRequest(ReissueReason.THEFT);

            var rpc = mapper.toReissueCardRpcRequest("card_2", request);

            assertThat(rpc.getReason()).isEqualTo(ReissueReasonRpc.THEFT);
        }

        @Test
        void should_map_reason_DAMAGE() {
            var request = new ReissueCardRequest(ReissueReason.DAMAGE);

            var rpc = mapper.toReissueCardRpcRequest("card_3", request);

            assertThat(rpc.getReason()).isEqualTo(ReissueReasonRpc.DAMAGE);
        }
    }

    @Nested
    @DisplayName("toGetFullCardRpcRequest / toCancelCardRpcRequest")
    class ToGetFullAndCancelRpcRequest {

        @Test
        void should_map_card_id_for_get_full() {
            var rpc = mapper.toGetFullCardRpcRequest("card_full_1");

            assertThat(rpc.getCardId()).isEqualTo("card_full_1");
        }

        @Test
        void should_map_card_id_for_cancel() {
            var rpc = mapper.toCancelCardRpcRequest("card_cancel_1");

            assertThat(rpc.getCardId()).isEqualTo("card_cancel_1");
        }
    }

    @Nested
    @DisplayName("toVirtualCardResponse")
    class ToVirtualCardResponse {

        @Test
        void should_map_all_fields() {
            var rpc = IssueVirtualCardRpcResponse.newBuilder()
                    .setId("virt-1")
                    .setNumber("4532015112830366")
                    .setMaskedNumber("4532********0366")
                    .setBrand("VISA")
                    .setExpirationDate("12/2028")
                    .setCardholderName("JOAO SILVA")
                    .setCvv("123")
                    .build();

            var response = mapper.toVirtualCardResponse(rpc);

            assertThat(response.id()).isEqualTo("virt-1");
            assertThat(response.number()).isEqualTo("4532015112830366");
            assertThat(response.maskedNumber()).isEqualTo("4532********0366");
            assertThat(response.brand()).isEqualTo("VISA");
            assertThat(response.expirationDate()).isEqualTo("12/2028");
            assertThat(response.cardholderName()).isEqualTo("JOAO SILVA");
            assertThat(response.cvv()).isEqualTo("123");
        }
    }

    @Nested
    @DisplayName("toReissueCardResponse")
    class ToReissueCardResponse {

        @Test
        void should_map_id_masked_number_and_brand() {
            var rpc = ReissueCardRpcResponse.newBuilder()
                    .setId("card-new-1")
                    .setMaskedNumber("4532********9999")
                    .setBrand("VISA")
                    .build();

            var response = mapper.toReissueCardResponse(rpc);

            assertThat(response.id()).isEqualTo("card-new-1");
            assertThat(response.maskedNumber()).isEqualTo("4532********9999");
            assertThat(response.brand()).isEqualTo("VISA");
        }
    }

    @Nested
    @DisplayName("toCardFullResponse")
    class ToCardFullResponse {

        @Test
        void should_map_all_fields() {
            var rpc = GetFullCardRpcResponse.newBuilder()
                    .setCardId("card_1")
                    .setBrand("VISA")
                    .setNumber("4532015112830366")
                    .setExpirationDate("12/2028")
                    .setCardholderName("JOAO SILVA")
                    .setCvv("123")
                    .build();

            var response = mapper.toCardFullResponse(rpc);

            assertThat(response.cardId()).isEqualTo("card_1");
            assertThat(response.brand()).isEqualTo("VISA");
            assertThat(response.number()).isEqualTo("4532015112830366");
            assertThat(response.expirationDate()).isEqualTo("12/2028");
            assertThat(response.cardholderName()).isEqualTo("JOAO SILVA");
            assertThat(response.cvv()).isEqualTo("123");
        }
    }
}

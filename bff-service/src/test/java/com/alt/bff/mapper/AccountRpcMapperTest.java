package com.alt.bff.mapper;

import com.alt.proto.account.*;
import com.alt.bff.resource.dto.account.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccountRpcMapper")
class AccountRpcMapperTest {

    AccountRpcMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AccountRpcMapper();
    }

    @Nested
    @DisplayName("toCreateAccountRpcRequest")
    class ToCreateAccountRpcRequest {

        @Test
        void should_map_all_fields() {
            var request = new CreateAccountRequest("João Silva", "joao@email.com", "12345678901", "11999999999");

            var rpc = mapper.toCreateAccountRpcRequest(request);

            assertThat(rpc.getName()).isEqualTo("João Silva");
            assertThat(rpc.getEmail()).isEqualTo("joao@email.com");
            assertThat(rpc.getDocument()).isEqualTo("12345678901");
            assertThat(rpc.getPhone()).isEqualTo("11999999999");
        }

        @Test
        void should_use_empty_string_when_phone_is_null() {
            var request = new CreateAccountRequest("Maria", "maria@email.com", "12345678901", null);

            var rpc = mapper.toCreateAccountRpcRequest(request);

            assertThat(rpc.getPhone()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toGetAccountRpcRequest")
    class ToGetAccountRpcRequest {

        @Test
        void should_map_account_id() {
            var rpc = mapper.toGetAccountRpcRequest("acc_123");

            assertThat(rpc.getAccountId()).isEqualTo("acc_123");
        }
    }

    @Nested
    @DisplayName("toCancelAccountRpcRequest")
    class ToCancelAccountRpcRequest {

        @Test
        void should_map_account_id() {
            var rpc = mapper.toCancelAccountRpcRequest("acc_456");

            assertThat(rpc.getAccountId()).isEqualTo("acc_456");
        }
    }

    @Nested
    @DisplayName("toCreateAccountResponse")
    class ToCreateAccountResponse {

        @Test
        void should_map_account_number_and_card_number() {
            var rpc = CreateAccountRpcResponse.newBuilder()
                    .setAccountNumber("12345678")
                    .setCardNumber("4532015112830366")
                    .build();

            var response = mapper.toCreateAccountResponse(rpc);

            assertThat(response.accountNumber()).isEqualTo("12345678");
            assertThat(response.cardNumber()).isEqualTo("4532015112830366");
        }
    }

    @Nested
    @DisplayName("toAccountResponse")
    class ToAccountResponse {

        @Test
        void should_map_account_and_cards_list() {
            var cardRpc = CardSummaryRpc.newBuilder()
                    .setId("card-1")
                    .setMaskedNumber("4532********0366")
                    .setBrand("VISA")
                    .setType("PHYSICAL")
                    .build();
            var rpc = GetAccountRpcResponse.newBuilder()
                    .setAccountId("acc_789")
                    .setAccountNumber("12345678")
                    .setName("João Silva")
                    .setEmail("joao@email.com")
                    .setDocument("12345678901")
                    .setStatus("ACTIVE")
                    .addCards(cardRpc)
                    .build();

            var response = mapper.toAccountResponse(rpc);

            assertThat(response.accountId()).isEqualTo("acc_789");
            assertThat(response.accountNumber()).isEqualTo("12345678");
            assertThat(response.name()).isEqualTo("João Silva");
            assertThat(response.email()).isEqualTo("joao@email.com");
            assertThat(response.document()).isEqualTo("12345678901");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.cards()).hasSize(1);
            assertThat(response.cards().get(0).id()).isEqualTo("card-1");
            assertThat(response.cards().get(0).maskedNumber()).isEqualTo("4532********0366");
            assertThat(response.cards().get(0).brand()).isEqualTo("VISA");
            assertThat(response.cards().get(0).type()).isEqualTo("PHYSICAL");
        }

        @Test
        void should_map_empty_cards_list() {
            var rpc = GetAccountRpcResponse.newBuilder()
                    .setAccountId("acc_1")
                    .setAccountNumber("11111111")
                    .setName("Nome")
                    .setEmail("e@e.com")
                    .setDocument("12345678901")
                    .setStatus("ACTIVE")
                    .build();

            var response = mapper.toAccountResponse(rpc);

            assertThat(response.cards()).isEmpty();
        }
    }
}

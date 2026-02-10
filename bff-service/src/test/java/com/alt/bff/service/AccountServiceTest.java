package com.alt.bff.service;

import com.alt.bff.client.AccountClient;
import com.alt.bff.client.CardClient;
import com.alt.bff.mapper.AccountRpcMapper;
import com.alt.bff.resource.dto.account.CreateAccountRequest;
import com.alt.bff.resource.dto.account.CreateAccountResponse;
import com.alt.proto.account.CancelAccountRpcRequest;
import com.alt.proto.account.CancelAccountRpcResponse;
import com.alt.proto.account.CardSummaryRpc;
import com.alt.proto.account.CreateAccountRpcRequest;
import com.alt.proto.account.CreateAccountRpcResponse;
import com.alt.proto.account.GetAccountRpcRequest;
import com.alt.proto.account.GetAccountRpcResponse;
import com.alt.proto.account.GetAccountRpcResponseOrBuilder;
import com.alt.proto.card.ListCardsByAccountIdRpcResponse;
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
@DisplayName("AccountService")
class AccountServiceTest {

    @Mock
    AccountClient accountClient;

    @Mock
    CardClient cardClient;

    AccountRpcMapper mapper = new AccountRpcMapper();

    AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountClient, cardClient, mapper);
    }

    @Nested
    @DisplayName("createAccount")
    class CreateAccount {

        @Test
        void should_call_client_and_return_mapped_response() {
            var request = new CreateAccountRequest("João", "joao@email.com", "12345678901", "11999999999");
            var rpcResponse = CreateAccountRpcResponse.newBuilder()
                    .setAccountNumber("12345678")
                    .setCardNumber("4532015112830366")
                    .build();
            when(accountClient.createAccount(any(CreateAccountRpcRequest.class))).thenReturn(rpcResponse);

            var response = accountService.createAccount(request);

            assertThat(response.accountNumber()).isEqualTo("12345678");
            assertThat(response.cardNumber()).isEqualTo("4532015112830366");
            verify(accountClient).createAccount(any(CreateAccountRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("getAccountData")
    class GetAccountData {

        @Test
        void should_call_clients_and_return_mapped_account_with_cards_from_card_service() {
            var accountId = "acc_123";
            var accountRpc = GetAccountRpcResponse.newBuilder()
                    .setAccountId(accountId)
                    .setAccountNumber("12345678")
                    .setName("João Silva")
                    .setEmail("joao@email.com")
                    .setDocument("12345678901")
                    .setStatus("ACTIVE")
                    .build();
            var cardRpc = com.alt.proto.card.CardSummaryRpc.newBuilder()
                    .setId("card-1")
                    .setMaskedNumber("4532********0366")
                    .setBrand("VISA")
                    .setType("PHYSICAL")
                    .build();
            var cardsResponse = ListCardsByAccountIdRpcResponse.newBuilder()
                    .addCards(cardRpc)
                    .build();

            when(accountClient.getAccount(any(GetAccountRpcRequest.class))).thenReturn(accountRpc);
            when(cardClient.listCardsByAccountId(accountId)).thenReturn(cardsResponse);

            var response = accountService.getAccountData(accountId);

            assertThat(response.accountId()).isEqualTo(accountId);
            assertThat(response.accountNumber()).isEqualTo("12345678");
            assertThat(response.name()).isEqualTo("João Silva");
            assertThat(response.cards()).hasSize(1);
            assertThat(response.cards().get(0).id()).isEqualTo("card-1");
            verify(accountClient).getAccount(any(GetAccountRpcRequest.class));
            verify(cardClient).listCardsByAccountId(accountId);
        }
    }

    @Nested
    @DisplayName("cancelAccount")
    class CancelAccount {

        @Test
        void should_call_client_cancel_account() {
            var accountId = "acc_456";
            when(accountClient.cancelAccount(any(CancelAccountRpcRequest.class)))
                    .thenReturn(CancelAccountRpcResponse.newBuilder().build());

            accountService.cancelAccount(accountId);

            verify(accountClient).cancelAccount(any(CancelAccountRpcRequest.class));
        }
    }
}

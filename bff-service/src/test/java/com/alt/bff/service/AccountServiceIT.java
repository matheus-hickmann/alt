package com.alt.bff.service;

import com.alt.bff.client.AccountClient;
import com.alt.proto.account.*;
import com.alt.bff.resource.dto.account.*;
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
@DisplayName("AccountService (integration)")
class AccountServiceIT {

    @Inject
    AccountService accountService;

    @InjectMock
    AccountClient accountClient;

    @Nested
    @DisplayName("createAccount")
    class CreateAccount {

        @Test
        void should_integrate_mapper_and_client_returning_response() {
            var request = new CreateAccountRequest("Maria", "maria@email.com", "98765432100", null);
            var rpcResponse = CreateAccountRpcResponse.newBuilder()
                    .setAccountNumber("87654321")
                    .setCardNumber("5502123456789012")
                    .build();
            when(accountClient.createAccount(any(CreateAccountRpcRequest.class))).thenReturn(rpcResponse);

            var response = accountService.createAccount(request);

            assertThat(response).isNotNull();
            assertThat(response.accountNumber()).isEqualTo("87654321");
            assertThat(response.cardNumber()).isEqualTo("5502123456789012");
            verify(accountClient).createAccount(any(CreateAccountRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("getAccountData")
    class GetAccountData {

        @Test
        void should_integrate_mapper_and_client_returning_account_with_cards() {
            var accountId = "acc_it_1";
            var cardRpc = CardSummaryRpc.newBuilder()
                    .setId("card-it-1")
                    .setMaskedNumber("4532********1111")
                    .setBrand("MASTERCARD")
                    .setType("VIRTUAL")
                    .build();
            var rpcResponse = GetAccountRpcResponse.newBuilder()
                    .setAccountId(accountId)
                    .setAccountNumber("11111111")
                    .setName("Maria Santos")
                    .setEmail("maria@email.com")
                    .setDocument("98765432100")
                    .setStatus("ACTIVE")
                    .addCards(cardRpc)
                    .build();
            when(accountClient.getAccount(any(GetAccountRpcRequest.class))).thenReturn(rpcResponse);

            var response = accountService.getAccountData(accountId);

            assertThat(response).isNotNull();
            assertThat(response.accountId()).isEqualTo(accountId);
            assertThat(response.name()).isEqualTo("Maria Santos");
            assertThat(response.cards()).hasSize(1);
            assertThat(response.cards().get(0).brand()).isEqualTo("MASTERCARD");
            verify(accountClient).getAccount(any(GetAccountRpcRequest.class));
        }
    }

    @Nested
    @DisplayName("cancelAccount")
    class CancelAccount {

        @Test
        void should_call_client_with_no_return() {
            var accountId = "acc_cancel_it";
            when(accountClient.cancelAccount(any(CancelAccountRpcRequest.class)))
                    .thenReturn(CancelAccountRpcResponse.newBuilder().build());

            accountService.cancelAccount(accountId);

            verify(accountClient).cancelAccount(any(CancelAccountRpcRequest.class));
        }
    }
}

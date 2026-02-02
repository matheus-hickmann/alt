package com.alt.account.service;

import com.alt.account.entity.Account;
import com.alt.account.entity.AccountStatus;
import com.alt.account.repository.AccountRepository;
import com.alt.proto.account.*;
import com.alt.proto.card.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("AccountService")
class AccountServiceTest {

    @Inject
    AccountService accountService;

    @Inject
    AccountRepository accountRepository;

    @InjectMock
    com.alt.account.client.CardClient cardClient;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Nested
    @DisplayName("createAccount")
    class CreateAccount {

        @Test
        void should_create_account_and_publish_card_event() {
            var request = CreateAccountRpcRequest.newBuilder()
                    .setName("John Doe")
                    .setEmail("john@email.com")
                    .setDocument("12345678901")
                    .setPhone("11999999999")
                    .build();

            var response = accountService.createAccount(request);

            assertThat(response.getAccountNumber()).hasSize(8);
            assertThat(response.getCardNumber()).isEmpty();

            var accounts = accountRepository.listAll();
            assertThat(accounts).hasSize(1);
            assertThat(accounts.get(0).name).isEqualTo("John Doe");
            assertThat(accounts.get(0).status).isEqualTo(AccountStatus.ACTIVE);
        }

        @Test
        void should_use_empty_phone_when_null() {
            var request = CreateAccountRpcRequest.newBuilder()
                    .setName("Jane Doe")
                    .setEmail("jane@email.com")
                    .setDocument("98765432100")
                    .setPhone("")
                    .build();

            var response = accountService.createAccount(request);

            var account = accountRepository.listAll().get(0);
            assertThat(account.phone).isNull();
        }
    }

    @Nested
    @DisplayName("cancelAccount")
    class CancelAccount {

        @Test
        void should_cancel_account_and_publish_event() {
            var account = new Account();
            account.accountId = "acc_test123";
            account.accountNumber = "12345678";
            account.name = "Test User";
            account.email = "test@email.com";
            account.document = "12345678901";
            account.status = AccountStatus.ACTIVE;
            account.createdAt = Instant.now();
            account.updatedAt = Instant.now();
            account.persist();

            when(cardClient.listCardsByAccountId(any())).thenReturn(ListCardsByAccountIdRpcResponse.newBuilder().build());

            var request = CancelAccountRpcRequest.newBuilder().setAccountId("acc_test123").build();
            var response = accountService.cancelAccount(request);

            assertThat(response).isNotNull();

            var updated = accountRepository.findByAccountId("acc_test123").orElseThrow();
            assertThat(updated.status).isEqualTo(AccountStatus.CANCELLED);
        }

        @Test
        void should_throw_when_account_id_blank() {
            var request = CancelAccountRpcRequest.newBuilder().setAccountId("").build();

            assertThatThrownBy(() -> accountService.cancelAccount(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("account_id");
        }

        @Test
        void should_throw_when_account_not_found() {
            var request = CancelAccountRpcRequest.newBuilder().setAccountId("acc_nonexistent").build();

            assertThatThrownBy(() -> accountService.cancelAccount(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("getAccount")
    class GetAccount {

        @Test
        void should_return_empty_when_account_not_found() {
            var request = GetAccountRpcRequest.newBuilder().setAccountId("acc_nonexistent").build();

            var result = accountService.getAccount(request);

            assertThat(result).isEmpty();
        }

        @Test
        void should_return_account_with_cards() {
            var account = new Account();
            account.accountId = "acc_get1";
            account.accountNumber = "87654321";
            account.name = "Get User";
            account.email = "get@email.com";
            account.document = "11111111111";
            account.status = AccountStatus.ACTIVE;
            account.createdAt = Instant.now();
            account.updatedAt = Instant.now();
            account.persist();

            var cardRpc = com.alt.proto.card.CardSummaryRpc.newBuilder()
                    .setId("card_1")
                    .setMaskedNumber("4532********1234")
                    .setBrand("VISA")
                    .setType("PHYSICAL")
                    .build();
            when(cardClient.listCardsByAccountId("acc_get1"))
                    .thenReturn(ListCardsByAccountIdRpcResponse.newBuilder().addCards(cardRpc).build());

            var request = GetAccountRpcRequest.newBuilder().setAccountId("acc_get1").build();
            var result = accountService.getAccount(request);

            assertThat(result).isPresent();
            assertThat(result.get().getAccountId()).isEqualTo("acc_get1");
            assertThat(result.get().getCardsList()).hasSize(1);
            assertThat(result.get().getCardsList().get(0).getId()).isEqualTo("card_1");
        }
    }
}

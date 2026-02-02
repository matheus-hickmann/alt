package com.alt.account.grpc;

import com.alt.account.entity.Account;
import com.alt.account.entity.AccountStatus;
import com.alt.account.repository.AccountRepository;
import com.alt.proto.account.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("AccountGrpcService (integration)")
class AccountGrpcServiceIT {

    @GrpcClient("account-service")
    AccountServiceGrpc.AccountServiceBlockingStub accountStub;

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
        void should_create_account_via_grpc() {
            var request = CreateAccountRpcRequest.newBuilder()
                    .setName("Grpc User")
                    .setEmail("grpc@email.com")
                    .setDocument("22222222222")
                    .setPhone("11888888888")
                    .build();

            var response = accountStub.createAccount(request);

            assertThat(response.getAccountNumber()).hasSize(8);
            assertThat(response.getCardNumber()).isEmpty();
            assertThat(accountRepository.listAll()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getAccount")
    class GetAccount {

        @Test
        void should_return_not_found_when_account_missing() {
            var request = GetAccountRpcRequest.newBuilder().setAccountId("acc_nonexistent").build();

            assertThatThrownBy(() -> accountStub.getAccount(request))
                    .hasMessageContaining("NOT_FOUND");
        }

        @Test
        void should_return_account_with_cards() {
            var account = new Account();
            account.accountId = "acc_grpc1";
            account.accountNumber = "11111111";
            account.name = "Grpc Get User";
            account.email = "grpcget@email.com";
            account.document = "33333333333";
            account.status = AccountStatus.ACTIVE;
            account.createdAt = Instant.now();
            account.updatedAt = Instant.now();
            account.persist();

            when(cardClient.listCardsByAccountId("acc_grpc1"))
                    .thenReturn(ListCardsByAccountIdRpcResponse.newBuilder()
                            .addCards(com.alt.proto.card.CardSummaryRpc.newBuilder()
                                    .setId("card_grpc")
                                    .setMaskedNumber("4532********9999")
                                    .setBrand("VISA")
                                    .setType("PHYSICAL")
                                    .build())
                            .build());

            var request = GetAccountRpcRequest.newBuilder().setAccountId("acc_grpc1").build();
            var response = accountStub.getAccount(request);

            assertThat(response.getAccountId()).isEqualTo("acc_grpc1");
            assertThat(response.getCardsList()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("cancelAccount")
    class CancelAccount {

        @Test
        void should_cancel_account_via_grpc() {
            var account = new Account();
            account.accountId = "acc_cancel_grpc";
            account.accountNumber = "22222222";
            account.name = "Cancel Grpc User";
            account.email = "cancelgrpc@email.com";
            account.document = "44444444444";
            account.status = AccountStatus.ACTIVE;
            account.createdAt = Instant.now();
            account.updatedAt = Instant.now();
            account.persist();

            var request = CancelAccountRpcRequest.newBuilder().setAccountId("acc_cancel_grpc").build();
            accountStub.cancelAccount(request);

            var updated = accountRepository.findByAccountId("acc_cancel_grpc").orElseThrow();
            assertThat(updated.status).isEqualTo(AccountStatus.CANCELLED);
        }
    }
}

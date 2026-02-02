package com.alt.account.client;

import com.alt.proto.card.*;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * gRPC client for card-service (e.g. create physical card when creating account).
 */
@ApplicationScoped
public class CardClient {

    @GrpcClient("card-service")
    CardServiceGrpc.CardServiceBlockingStub stub;

    /**
     * Lists cards for the account by account_id (card-service database).
     */
    public ListCardsByAccountIdRpcResponse listCardsByAccountId(String accountId) {
        return stub.listCardsByAccountId(
                ListCardsByAccountIdRpcRequest.newBuilder().setAccountId(accountId).build());
    }
}

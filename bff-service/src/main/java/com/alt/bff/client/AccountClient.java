package com.alt.bff.client;

import com.alt.proto.account.*;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Cliente gRPC para o account-service.
 */
@ApplicationScoped
public class AccountClient {

    @GrpcClient("account-service")
    AccountServiceGrpc.AccountServiceBlockingStub stub;

    public CreateAccountRpcResponse createAccount(CreateAccountRpcRequest request) {
        return stub.createAccount(request);
    }

    public GetAccountRpcResponse getAccount(GetAccountRpcRequest request) {
        return stub.getAccount(request);
    }

    public CancelAccountRpcResponse cancelAccount(CancelAccountRpcRequest request) {
        return stub.cancelAccount(request);
    }
}

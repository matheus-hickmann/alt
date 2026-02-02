package com.alt.account.grpc;

import com.alt.account.exception.AccountNotFoundException;
import com.alt.account.exception.InvalidAccountRequestException;
import com.alt.account.service.AccountService;
import com.alt.proto.account.*;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import jakarta.inject.Inject;

import java.util.NoSuchElementException;

/**
 * gRPC service for account operations. Delegates to {@link AccountService}.
 */
@GrpcService
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    @Inject
    AccountService accountService;

    @Override
    public void createAccount(CreateAccountRpcRequest request, StreamObserver<CreateAccountRpcResponse> responseObserver) {
        GrpcResponseHandler.handle(responseObserver,
                () -> accountService.createAccount(request),
                e -> io.grpc.Status.INTERNAL.withDescription(e.getMessage()));
    }

    @Override
    public void getAccount(GetAccountRpcRequest request, StreamObserver<GetAccountRpcResponse> responseObserver) {
        GrpcResponseHandler.handle(responseObserver,
                () -> accountService.getAccount(request)
                        .orElseThrow(() -> new NoSuchElementException("Account not found: " + request.getAccountId())),
                e -> {
                    if (e instanceof NoSuchElementException || e instanceof AccountNotFoundException) {
                        return io.grpc.Status.NOT_FOUND.withDescription(e.getMessage());
                    }
                    return io.grpc.Status.INTERNAL.withDescription(e.getMessage());
                });
    }

    @Override
    public void cancelAccount(CancelAccountRpcRequest request, StreamObserver<CancelAccountRpcResponse> responseObserver) {
        GrpcResponseHandler.handle(responseObserver,
                () -> accountService.cancelAccount(request),
                e -> {
                    if (e instanceof InvalidAccountRequestException) {
                        return io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage());
                    }
                    if (e instanceof AccountNotFoundException) {
                        return io.grpc.Status.NOT_FOUND.withDescription(e.getMessage());
                    }
                    if (e instanceof IllegalStateException) {
                        return io.grpc.Status.FAILED_PRECONDITION.withDescription(e.getMessage());
                    }
                    return io.grpc.Status.INTERNAL.withDescription(e.getMessage());
                });
    }
}

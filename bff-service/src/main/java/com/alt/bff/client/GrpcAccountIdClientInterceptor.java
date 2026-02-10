package com.alt.bff.client;

import com.alt.bff.auth.GrpcAccountIdPropagationContext;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.quarkus.grpc.GlobalInterceptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Interceptor gRPC que adiciona o header x-account-id da requisição HTTP atual
 * ao metadata de todas as chamadas gRPC (account-service, card-service).
 */
@GlobalInterceptor
@ApplicationScoped
public class GrpcAccountIdClientInterceptor implements ClientInterceptor {

    public static final String ACCOUNT_ID_HEADER = "x-account-id";
    private static final Metadata.Key<String> ACCOUNT_ID =
            Metadata.Key.of(ACCOUNT_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    @Inject
    GrpcAccountIdPropagationContext propagationContext;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {
        ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);
        String accountId = propagationContext != null ? propagationContext.getAccountId() : null;
        if (accountId == null || accountId.isBlank()) {
            return call;
        }
        return new io.grpc.ForwardingClientCall.SimpleForwardingClientCall<>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(ACCOUNT_ID, accountId);
                super.start(responseListener, headers);
            }
        };
    }
}

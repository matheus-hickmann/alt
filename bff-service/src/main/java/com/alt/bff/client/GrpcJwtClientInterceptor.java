package com.alt.bff.client;

import com.alt.bff.auth.GrpcJwtPropagationContext;
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
 * Interceptor gRPC que adiciona o header Authorization (JWT) da requisição HTTP atual
 * ao metadata de todas as chamadas gRPC (account-service, card-service).
 */
@GlobalInterceptor
@ApplicationScoped
public class GrpcJwtClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Inject
    GrpcJwtPropagationContext propagationContext;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {
        ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);
        String auth = propagationContext != null ? propagationContext.getAuthorizationHeader() : null;
        if (auth == null || auth.isBlank()) {
            return call;
        }
        return new io.grpc.ForwardingClientCall.SimpleForwardingClientCall<>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(AUTHORIZATION, auth);
                super.start(responseListener, headers);
            }
        };
    }
}

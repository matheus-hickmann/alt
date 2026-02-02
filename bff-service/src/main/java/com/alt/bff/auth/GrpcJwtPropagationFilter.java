package com.alt.bff.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * Filtro que captura o header Authorization da requisição HTTP e armazena no
 * {@link GrpcJwtPropagationContext} para que o {@link GrpcJwtClientInterceptor}
 * propague o JWT nas chamadas gRPC.
 */
@Provider
public class GrpcJwtPropagationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";

    @Inject
    GrpcJwtPropagationContext propagationContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String auth = requestContext.getHeaderString(AUTHORIZATION);
        if (auth != null && !auth.isBlank()) {
            propagationContext.setAuthorizationHeader(auth);
        }
    }
}

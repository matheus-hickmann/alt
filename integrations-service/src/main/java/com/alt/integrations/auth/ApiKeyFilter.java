package com.alt.integrations.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * Filter to validate API Key in webhooks received from external integrations.
 * Uses {@link ApiKeyValidator} for extensible validation.
 */
@Provider
@WebhookSecured
public class ApiKeyFilter implements ContainerRequestFilter {

    @Inject
    ApiKeyValidator apiKeyValidator;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String apiKey = requestContext.getHeaderString("X-API-Key");

        if (apiKey == null || apiKey.isBlank()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"Missing X-API-Key header\"}")
                            .build()
            );
            return;
        }

        if (!apiKeyValidator.isValid(apiKey)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"error\":\"Invalid API Key\"}")
                            .build()
            );
        }
    }
}

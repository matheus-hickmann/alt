package com.alt.bff.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.List;

/**
 * Filtro que exige o header X-Account-Id nos endpoints /api/* exceto health, OpenAPI (q/)
 * e o endpoint de criação de conta (POST /api/accounts).
 */
@Provider
public class AccountIdInterceptor implements ContainerRequestFilter {

    public static final String ACCOUNT_ID_HEADER = "X-Account-Id";

    /** Paths que não exigem X-Account-Id. */
    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/health",
            "/q/"
    );

    private static final String CREATE_ACCOUNT_PATH = "/api/accounts";

    @Inject
    AccountIdContext accountIdContext;

    @Inject
    GrpcAccountIdPropagationContext propagationContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String fullPath = "/" + path;
        String method = requestContext.getMethod();

        if (isPublicPath(fullPath)) {
            return;
        }

        if (isCreateAccountEndpoint(method, fullPath)) {
            return;
        }

        if (!fullPath.startsWith("/api/")) {
            return;
        }

        String accountId = requestContext.getHeaderString(ACCOUNT_ID_HEADER);
        if (accountId == null || accountId.isBlank()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .type(MediaType.APPLICATION_JSON_TYPE)
                            .entity(new UnauthorizedMessage("Missing or invalid " + ACCOUNT_ID_HEADER + " header"))
                            .build()
            );
            return;
        }

        accountId = accountId.trim();
        accountIdContext.setAccountId(accountId);
        propagationContext.setAccountId(accountId);
    }

    private static boolean isPublicPath(String fullPath) {
        return PUBLIC_PREFIXES.stream().anyMatch(fullPath::startsWith);
    }

    private static boolean isCreateAccountEndpoint(String method, String fullPath) {
        return "POST".equalsIgnoreCase(method) && CREATE_ACCOUNT_PATH.equals(fullPath);
    }

    public record UnauthorizedMessage(String message) {
    }
}

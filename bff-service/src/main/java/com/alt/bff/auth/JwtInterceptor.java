package com.alt.bff.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.List;

/**
 * Interceptor que exige JWT Bearer nos endpoints /api/* (exceto /api/health).
 * Retorna 401 se o header Authorization: Bearer &lt;token&gt; estiver ausente.
 * A validação do token (assinatura, exp, iss) é feita pelo Quarkus SmallRye JWT.
 */
@Provider
public class JwtInterceptor implements ContainerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    /** Paths que não exigem JWT. */
    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/health",
            "/q/"
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String fullPath = "/" + path;

        if (isPublicPath(fullPath)) {
            return;
        }

        if (!fullPath.startsWith("/api/")) {
            return;
        }

        String auth = requestContext.getHeaderString(AUTHORIZATION);
        if (auth == null || !auth.startsWith(BEARER) || auth.substring(BEARER.length()).isBlank()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .type(MediaType.APPLICATION_JSON_TYPE)
                            .entity(new UnauthorizedMessage("Missing or invalid Authorization: Bearer token"))
                            .build()
            );
        }
    }

    private static boolean isPublicPath(String fullPath) {
        return PUBLIC_PREFIXES.stream().anyMatch(fullPath::startsWith);
    }

    public record UnauthorizedMessage(String message) {
    }
}

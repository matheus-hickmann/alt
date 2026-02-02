package com.alt.bff.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Payload do JWT emitido pelo altbank-auth-service.
 * Corresponde ao body do token com: name, account_id, iss, iat, exp, roles, scope.
 */
public record JwtClaims(
        String name,
        @JsonProperty("account_id") String accountId,
        String iss,
        Long iat,
        Long exp,
        List<String> roles,
        String scope
) {
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasScope(String scopePart) {
        return scope != null && scope.contains(scopePart);
    }
}

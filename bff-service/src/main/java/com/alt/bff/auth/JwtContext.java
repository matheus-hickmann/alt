package com.alt.bff.auth;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Contexto do JWT da requisição atual. Injete em resources para acessar os claims do token.
 */
@RequestScoped
public class JwtContext {

    @Inject
    JsonWebToken jwt;

    /**
     * Retorna os claims do JWT atual (name, account_id, iss, iat, exp, roles, scope).
     * Retorna null se não houver token ou se não estiver autenticado.
     */
    public JwtClaims getClaims() {
        if (jwt == null || jwt.getSubject() == null) {
            return null;
        }
        return new JwtClaims(
                getClaimString("name"),
                getClaimString("account_id"),
                jwt.getIssuer(),
                jwt.getIssuedAtTime(),
                jwt.getExpirationTime(),
                getClaimRoles("roles"),
                getClaimString("scope")
        );
    }

    public JsonWebToken getToken() {
        return jwt;
    }

    private String getClaimString(String name) {
        Object value = jwt.getClaim(name);
        return value != null ? value.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getClaimRoles(String name) {
        Object value = jwt.getClaim(name);
        if (value == null) {
            return List.of();
        }
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(o -> o != null ? o.toString() : null)
                    .filter(o -> o != null)
                    .collect(Collectors.toList());
        }
        if (value instanceof Iterable<?> iterable) {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(o -> o != null ? o.toString() : null)
                    .filter(o -> o != null)
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(value.toString());
    }
}

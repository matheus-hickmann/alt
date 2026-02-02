package com.alt.bff.auth;

import jakarta.enterprise.context.RequestScoped;

/**
 * Contexto que guarda o header Authorization da requisição HTTP atual para propagar
 * nas chamadas gRPC (account-service, card-service).
 */
@RequestScoped
public class GrpcJwtPropagationContext {

    private String authorizationHeader;

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    /**
     * Retorna o valor do header Authorization (ex: "Bearer &lt;token&gt;") ou null se não houver.
     */
    public String getAuthorizationHeader() {
        return authorizationHeader;
    }
}

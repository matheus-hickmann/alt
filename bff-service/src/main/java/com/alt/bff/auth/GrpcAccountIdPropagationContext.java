package com.alt.bff.auth;

import jakarta.enterprise.context.RequestScoped;

/**
 * Contexto que guarda o accountId da requisição HTTP atual para propagar
 * nas chamadas gRPC (account-service, card-service) via header x-account-id.
 */
@RequestScoped
public class GrpcAccountIdPropagationContext {

    private String accountId;

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Retorna o accountId da requisição atual ou null se não houver.
     */
    public String getAccountId() {
        return accountId;
    }
}

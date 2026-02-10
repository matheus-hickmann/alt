package com.alt.bff.auth;

import jakarta.enterprise.context.RequestScoped;

/**
 * Contexto com o accountId da requisição atual (header X-Account-Id).
 * Preenchido pelo {@link AccountIdInterceptor} em rotas que exigem o header.
 */
@RequestScoped
public class AccountIdContext {

    private String accountId;

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Retorna o accountId da requisição atual ou null se não houver (ex: rota pública).
     */
    public String getAccountId() {
        return accountId;
    }
}

package com.alt.account.service;

import com.alt.account.entity.Account;
import com.alt.account.entity.AccountStatus;
import com.alt.proto.account.CreateAccountRpcRequest;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Factory for creating Account entities from RPC requests.
 */
@ApplicationScoped
public class AccountFactory {

    private final IdGenerator idGenerator;

    public AccountFactory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Account createFromRequest(CreateAccountRpcRequest request) {
        Account account = new Account();
        account.accountId = idGenerator.generateAccountId();
        account.accountNumber = idGenerator.generateUniqueAccountNumber();
        account.name = request.getName();
        account.email = request.getEmail();
        account.document = request.getDocument();
        account.phone = blankToNull(request.getPhone());
        account.status = AccountStatus.ACTIVE;
        return account;
    }

    private static String blankToNull(String value) {
        return value != null && !value.isBlank() ? value : null;
    }
}

package com.alt.bff.service;

import com.alt.bff.client.AccountClient;
import com.alt.bff.mapper.AccountRpcMapper;
import com.alt.bff.resource.dto.account.*;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * BFF account service: receives DTOs from REST, maps to RPC, calls account-service and returns DTOs.
 */
@ApplicationScoped
public class AccountService {

    private final AccountClient accountClient;
    private final AccountRpcMapper mapper;

    public AccountService(AccountClient accountClient, AccountRpcMapper mapper) {
        this.accountClient = accountClient;
        this.mapper = mapper;
    }

    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        var rpcRequest = mapper.toCreateAccountRpcRequest(request);
        var rpcResponse = accountClient.createAccount(rpcRequest);
        return mapper.toCreateAccountResponse(rpcResponse);
    }

    public AccountResponse getAccountData(String accountId) {
        var rpcRequest = mapper.toGetAccountRpcRequest(accountId);
        var rpcResponse = accountClient.getAccount(rpcRequest);
        return mapper.toAccountResponse(rpcResponse);
    }

    public void cancelAccount(String accountId) {
        var rpcRequest = mapper.toCancelAccountRpcRequest(accountId);
        accountClient.cancelAccount(rpcRequest);
    }
}

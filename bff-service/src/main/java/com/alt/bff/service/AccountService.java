package com.alt.bff.service;

import com.alt.bff.client.AccountClient;
import com.alt.bff.client.CardClient;
import com.alt.bff.mapper.AccountRpcMapper;
import com.alt.bff.resource.dto.account.AccountResponse;
import com.alt.bff.resource.dto.account.CreateAccountRequest;
import com.alt.bff.resource.dto.account.CreateAccountResponse;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * BFF account service: receives DTOs from REST, maps to RPC, calls account-service and card-service
 * and returns DTOs.
 */
@ApplicationScoped
public class AccountService {

    private final AccountClient accountClient;
    private final CardClient cardClient;
    private final AccountRpcMapper mapper;

    public AccountService(AccountClient accountClient, CardClient cardClient, AccountRpcMapper mapper) {
        this.accountClient = accountClient;
        this.cardClient = cardClient;
        this.mapper = mapper;
    }

    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        var rpcRequest = mapper.toCreateAccountRpcRequest(request);
        var rpcResponse = accountClient.createAccount(rpcRequest);
        return mapper.toCreateAccountResponse(rpcResponse);
    }

    /**
     * Returns account data along with its cards, fetching cards directly from card-service
     * instead of relying on the account-service response for the card list.
     */
    public AccountResponse getAccountData(String accountId) {
        var rpcRequest = mapper.toGetAccountRpcRequest(accountId);
        var accountRpc = accountClient.getAccount(rpcRequest);
        var cardsRpc = cardClient.listCardsByAccountId(accountId);
        return mapper.toAccountResponse(accountRpc, cardsRpc);
    }

    public void cancelAccount(String accountId) {
        var rpcRequest = mapper.toCancelAccountRpcRequest(accountId);
        accountClient.cancelAccount(rpcRequest);
    }
}

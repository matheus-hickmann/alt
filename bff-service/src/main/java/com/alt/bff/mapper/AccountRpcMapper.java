package com.alt.bff.mapper;

import com.alt.bff.resource.dto.account.AccountResponse;
import com.alt.bff.resource.dto.account.CardSummaryDto;
import com.alt.bff.resource.dto.account.CreateAccountRequest;
import com.alt.bff.resource.dto.account.CreateAccountResponse;
import com.alt.proto.account.CancelAccountRpcRequest;
import com.alt.proto.account.CreateAccountRpcRequest;
import com.alt.proto.account.CreateAccountRpcResponse;
import com.alt.proto.account.GetAccountRpcRequest;
import com.alt.proto.account.GetAccountRpcResponse;
import com.alt.proto.card.CardSummaryRpc;
import com.alt.proto.card.ListCardsByAccountIdRpcResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Maps BFF DTOs to RPC messages (request) and RPC to DTOs (response).
 */
@ApplicationScoped
public class AccountRpcMapper {

    // Request: BFF DTO -> RPC
    public CreateAccountRpcRequest toCreateAccountRpcRequest(CreateAccountRequest request) {
        return CreateAccountRpcRequest.newBuilder()
                .setName(request.name())
                .setEmail(request.email())
                .setDocument(request.document())
                .setPhone(request.phone() != null ? request.phone() : "")
                .build();
    }

    public GetAccountRpcRequest toGetAccountRpcRequest(String accountId) {
        return GetAccountRpcRequest.newBuilder().setAccountId(accountId).build();
    }

    public CancelAccountRpcRequest toCancelAccountRpcRequest(String accountId) {
        return CancelAccountRpcRequest.newBuilder().setAccountId(accountId).build();
    }

    // Response: RPC -> BFF DTO
    public CreateAccountResponse toCreateAccountResponse(CreateAccountRpcResponse rpc) {
        return new CreateAccountResponse(rpc.getAccountNumber(), rpc.getCardNumber());
    }

    /**
     * Maps account RPC (including cards) to DTO. Used when cards come from account-service.
     */
    public AccountResponse toAccountResponse(GetAccountRpcResponse rpc) {
        List<CardSummaryDto> cards = mapCardSummaries(rpc.getCardsList());
        return new AccountResponse(
                rpc.getAccountId(),
                rpc.getAccountNumber(),
                rpc.getName(),
                rpc.getEmail(),
                rpc.getDocument(),
                rpc.getStatus(),
                cards
        );
    }

    /**
     * Maps account RPC and a separate card-service response to DTO.
     * Used by the BFF when fetching cards directly from card-service.
     */
    public AccountResponse toAccountResponse(GetAccountRpcResponse accountRpc,
                                             ListCardsByAccountIdRpcResponse cardsRpc) {
        List<CardSummaryDto> cards = mapCardSummaries(cardsRpc.getCardsList());
        return new AccountResponse(
                accountRpc.getAccountId(),
                accountRpc.getAccountNumber(),
                accountRpc.getName(),
                accountRpc.getEmail(),
                accountRpc.getDocument(),
                accountRpc.getStatus(),
                cards
        );
    }

    private static List<CardSummaryDto> mapCardSummaries(List<CardSummaryRpc> rpcs) {
        return rpcs.stream()
                .map(c -> new CardSummaryDto(c.getId(), c.getMaskedNumber(), c.getBrand(), c.getType()))
                .toList();
    }
}

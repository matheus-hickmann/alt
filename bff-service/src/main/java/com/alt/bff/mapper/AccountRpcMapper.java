package com.alt.bff.mapper;

import com.alt.proto.account.*;
import com.alt.bff.resource.dto.account.*;
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

    public AccountResponse toAccountResponse(GetAccountRpcResponse rpc) {
        List<CardSummaryDto> cards = rpc.getCardsList().stream()
                .map(c -> new CardSummaryDto(c.getId(), c.getMaskedNumber(), c.getBrand(), c.getType()))
                .toList();
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
}

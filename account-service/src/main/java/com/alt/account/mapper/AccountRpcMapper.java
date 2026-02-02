package com.alt.account.mapper;

import com.alt.account.service.GetAccountCacheEntry;
import com.alt.proto.account.CardSummaryRpc;
import com.alt.proto.account.GetAccountRpcResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps domain entities and cache entries to RPC response messages.
 */
@ApplicationScoped
public class AccountRpcMapper {

    public GetAccountRpcResponse toGetAccountRpcResponse(GetAccountCacheEntry.GetAccountCacheValue value) {
        List<CardSummaryRpc> cardsRpc = value.cards().stream()
                .map(this::toCardSummaryRpc)
                .collect(Collectors.toList());
        return GetAccountRpcResponse.newBuilder()
                .setAccountId(value.accountId())
                .setAccountNumber(value.accountNumber())
                .setName(value.name())
                .setEmail(value.email())
                .setDocument(value.document())
                .setStatus(value.status())
                .addAllCards(cardsRpc)
                .build();
    }

    private CardSummaryRpc toCardSummaryRpc(GetAccountCacheEntry.CardSummaryCache c) {
        return CardSummaryRpc.newBuilder()
                .setId(c.id())
                .setMaskedNumber(c.maskedNumber())
                .setBrand(c.brand())
                .setType(c.type())
                .build();
    }
}

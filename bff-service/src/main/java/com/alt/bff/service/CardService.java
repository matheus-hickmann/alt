package com.alt.bff.service;

import com.alt.bff.client.CardClient;
import com.alt.bff.mapper.CardRpcMapper;
import com.alt.bff.resource.dto.card.*;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * BFF card service: receives DTOs from REST, maps to RPC, calls card-service and returns DTOs.
 */
@ApplicationScoped
public class CardService {

    private final CardClient cardClient;
    private final CardRpcMapper mapper;

    public CardService(CardClient cardClient, CardRpcMapper mapper) {
        this.cardClient = cardClient;
        this.mapper = mapper;
    }

    public VirtualCardResponse issueVirtualCard(VirtualCardRequest request) {
        var rpcRequest = mapper.toIssueVirtualCardRpcRequest(request);
        var rpcResponse = cardClient.issueVirtualCard(rpcRequest);
        return mapper.toVirtualCardResponse(rpcResponse);
    }

    public ReissueCardResponse reissueCard(String cardId, ReissueCardRequest request) {
        var rpcRequest = mapper.toReissueCardRpcRequest(cardId, request);
        var rpcResponse = cardClient.reissueCard(rpcRequest);
        return mapper.toReissueCardResponse(rpcResponse);
    }

    public CardFullResponse getFullCardData(String cardId) {
        var rpcRequest = mapper.toGetFullCardRpcRequest(cardId);
        var rpcResponse = cardClient.getFullCard(rpcRequest);
        return mapper.toCardFullResponse(rpcResponse);
    }

    public void cancelCard(String cardId) {
        var rpcRequest = mapper.toCancelCardRpcRequest(cardId);
        cardClient.cancelCard(rpcRequest);
    }

    public void activateCard(String cardId) {
        var rpcRequest = mapper.toActivateCardRpcRequest(cardId);
        cardClient.activateCard(rpcRequest);
    }
}

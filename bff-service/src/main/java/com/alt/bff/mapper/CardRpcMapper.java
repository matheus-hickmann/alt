package com.alt.bff.mapper;

import com.alt.proto.card.*;
import com.alt.bff.resource.dto.card.*;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Maps BFF DTOs to RPC messages (request) and RPC to DTOs (response).
 */
@ApplicationScoped
public class CardRpcMapper {

    // Request: BFF DTO -> RPC
    public IssueVirtualCardRpcRequest toIssueVirtualCardRpcRequest(VirtualCardRequest request) {
        return IssueVirtualCardRpcRequest.newBuilder().setAccountId(request.accountId()).build();
    }

    public ReissueCardRpcRequest toReissueCardRpcRequest(String cardId, ReissueCardRequest request) {
        return ReissueCardRpcRequest.newBuilder()
                .setCardId(cardId)
                .setReason(toReissueReasonRpc(request.reason()))
                .build();
    }

    public GetFullCardRpcRequest toGetFullCardRpcRequest(String cardId) {
        return GetFullCardRpcRequest.newBuilder().setCardId(cardId).build();
    }

    public CancelCardRpcRequest toCancelCardRpcRequest(String cardId) {
        return CancelCardRpcRequest.newBuilder().setCardId(cardId).build();
    }

    public ActivateCardRpcRequest toActivateCardRpcRequest(String cardId) {
        return ActivateCardRpcRequest.newBuilder().setCardId(cardId).build();
    }

    private static ReissueReasonRpc toReissueReasonRpc(ReissueReason reason) {
        return switch (reason) {
            case LOSS -> ReissueReasonRpc.LOSS;
            case THEFT -> ReissueReasonRpc.THEFT;
            case DAMAGE -> ReissueReasonRpc.DAMAGE;
        };
    }

    // Response: RPC -> BFF DTO
    public VirtualCardResponse toVirtualCardResponse(IssueVirtualCardRpcResponse rpc) {
        return new VirtualCardResponse(
                rpc.getId(),
                rpc.getNumber(),
                rpc.getMaskedNumber(),
                rpc.getBrand(),
                rpc.getExpirationDate(),
                rpc.getCardholderName(),
                rpc.getCvv()
        );
    }

    public ReissueCardResponse toReissueCardResponse(ReissueCardRpcResponse rpc) {
        return new ReissueCardResponse(rpc.getId(), rpc.getMaskedNumber(), rpc.getBrand());
    }

    public CardFullResponse toCardFullResponse(GetFullCardRpcResponse rpc) {
        return new CardFullResponse(
                rpc.getCardId(),
                rpc.getBrand(),
                rpc.getNumber(),
                rpc.getExpirationDate(),
                rpc.getCardholderName(),
                rpc.getCvv()
        );
    }
}

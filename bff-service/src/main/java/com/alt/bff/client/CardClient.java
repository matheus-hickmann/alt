package com.alt.bff.client;

import com.alt.proto.card.ActivateCardRpcRequest;
import com.alt.proto.card.ActivateCardRpcResponse;
import com.alt.proto.card.CancelCardRpcRequest;
import com.alt.proto.card.CancelCardRpcResponse;
import com.alt.proto.card.CardServiceGrpc;
import com.alt.proto.card.GetFullCardRpcRequest;
import com.alt.proto.card.GetFullCardRpcResponse;
import com.alt.proto.card.IssueVirtualCardRpcRequest;
import com.alt.proto.card.IssueVirtualCardRpcResponse;
import com.alt.proto.card.ListCardsByAccountIdRpcRequest;
import com.alt.proto.card.ListCardsByAccountIdRpcResponse;
import com.alt.proto.card.ReissueCardRpcRequest;
import com.alt.proto.card.ReissueCardRpcResponse;
import io.quarkus.grpc.GrpcClient;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Cliente gRPC para o card-service.
 */
@ApplicationScoped
public class CardClient {

    @GrpcClient("card-service")
    CardServiceGrpc.CardServiceBlockingStub stub;

    public IssueVirtualCardRpcResponse issueVirtualCard(IssueVirtualCardRpcRequest request) {
        return stub.issueVirtualCard(request);
    }

    public ReissueCardRpcResponse reissueCard(ReissueCardRpcRequest request) {
        return stub.reissueCard(request);
    }

    public GetFullCardRpcResponse getFullCard(GetFullCardRpcRequest request) {
        return stub.getFullCard(request);
    }

    public CancelCardRpcResponse cancelCard(CancelCardRpcRequest request) {
        return stub.cancelCard(request);
    }

    public ActivateCardRpcResponse activateCard(ActivateCardRpcRequest request) {
        return stub.activateCard(request);
    }

    /**
     * Lists cards for a given account by calling card-service directly.
     */
    public ListCardsByAccountIdRpcResponse listCardsByAccountId(String accountId) {
        return stub.listCardsByAccountId(
                ListCardsByAccountIdRpcRequest.newBuilder().setAccountId(accountId).build());
    }
}

package com.alt.bff.client;

import com.alt.proto.card.*;
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
}

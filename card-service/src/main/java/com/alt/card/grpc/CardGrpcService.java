package com.alt.card.grpc;

import com.alt.card.exception.CardNotFoundException;
import com.alt.card.exception.InvalidCardRequestException;
import com.alt.card.exception.InvalidCardStateException;
import com.alt.card.service.CardService;
import com.alt.proto.card.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;

/**
 * gRPC service for card operations. Delegates to {@link CardService}.
 */
@GrpcService
public class CardGrpcService extends CardServiceGrpc.CardServiceImplBase {

    @Inject
    CardService cardService;

    @Override
    @Blocking
    public void createPhysicalCard(CreatePhysicalCardRpcRequest request, StreamObserver<CreatePhysicalCardRpcResponse> responseObserver) {
        GrpcResponseHandler.handle(responseObserver,
                () -> cardService.createPhysicalCard(request),
                e -> Status.INTERNAL.withDescription(e.getMessage()));
    }

    @Override
    @Blocking
    public void issueVirtualCard(IssueVirtualCardRpcRequest request, StreamObserver<IssueVirtualCardRpcResponse> responseObserver) {
        // TODO: delegate to cardService.issueVirtualCard when implemented
        var response = IssueVirtualCardRpcResponse.newBuilder()
                .setId("virt-1")
                .setNumber("4532015112830366")
                .setMaskedNumber("4532********0366")
                .setBrand("VISA")
                .setExpirationDate("12/2028")
                .setCardholderName("JOAO SILVA")
                .setCvv("123")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Blocking
    public void reissueCard(ReissueCardRpcRequest request, StreamObserver<ReissueCardRpcResponse> responseObserver) {
        // TODO: delegate to cardService.reissueCard when implemented
        var response = ReissueCardRpcResponse.newBuilder()
                .setId("card-new-1")
                .setMaskedNumber("4532********9999")
                .setBrand("VISA")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Blocking
    public void getFullCard(GetFullCardRpcRequest request, StreamObserver<GetFullCardRpcResponse> responseObserver) {
        // TODO: delegate to cardService.getFullCard when implemented
        var response = GetFullCardRpcResponse.newBuilder()
                .setCardId(request.getCardId())
                .setBrand("VISA")
                .setNumber("4532015112830366")
                .setExpirationDate("12/2028")
                .setCardholderName("JOAO SILVA")
                .setCvv("123")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Blocking
    public void cancelCard(CancelCardRpcRequest request, StreamObserver<CancelCardRpcResponse> responseObserver) {
        GrpcResponseHandler.handle(responseObserver,
                () -> cardService.cancelCard(request),
                e -> mapCardException(e));
    }

    @Override
    @Blocking
    public void activateCard(ActivateCardRpcRequest request, StreamObserver<ActivateCardRpcResponse> responseObserver) {
        GrpcResponseHandler.handle(responseObserver,
                () -> cardService.activateCard(request),
                e -> mapCardException(e));
    }

    @Override
    @Blocking
    public void cancelCardsByAccountId(CancelCardsByAccountIdRpcRequest request, StreamObserver<CancelCardsByAccountIdRpcResponse> responseObserver) {
        GrpcResponseHandler.handle(responseObserver,
                () -> cardService.cancelCardsByAccountId(request),
                e -> Status.INTERNAL.withDescription(e.getMessage()));
    }

    @Override
    @Blocking
    public void listCardsByAccountId(ListCardsByAccountIdRpcRequest request, StreamObserver<ListCardsByAccountIdRpcResponse> responseObserver) {
        GrpcResponseHandler.handle(responseObserver,
                () -> cardService.listCardsByAccountId(request),
                e -> Status.INTERNAL.withDescription(e.getMessage()));
    }

    private static Status mapCardException(Exception e) {
        String desc = e.getMessage();
        if (e instanceof InvalidCardRequestException || e instanceof CardNotFoundException) {
            return Status.NOT_FOUND.withDescription(desc);
        }
        if (e instanceof InvalidCardStateException) {
            return Status.FAILED_PRECONDITION.withDescription(desc);
        }
        return Status.INTERNAL.withDescription(desc);
    }
}

package com.alt.integrations.grpc;

import com.alt.proto.integrations.CardDataServiceGrpc;
import com.alt.proto.integrations.GetFullCardDataRpcRequest;
import com.alt.proto.integrations.GetFullCardDataRpcResponse;
import io.grpc.stub.StreamObserver;
import io.quarkus.cache.CacheResult;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Singleton;

import java.util.concurrent.ThreadLocalRandom;

/**
 * gRPC service for card-service to get full card data via integration.
 * Stub implementation: later card-service will call this service to enrich GetFullCard.
 */
@GrpcService
@Singleton
public class CardDataGrpcService extends CardDataServiceGrpc.CardDataServiceImplBase {

    @Override
    @Blocking
    public void getFullCardData(GetFullCardDataRpcRequest request, StreamObserver<GetFullCardDataRpcResponse> responseObserver) {
        GetFullCardDataRpcResponse response = getFullCardDataCached(request.getCardId());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * TEMPORARY: Returns random card data for testing purposes.
     * TODO: Replace with actual integration to fetch real card data from external processor.
     */
    @CacheResult(cacheName = "full-card-data")
    GetFullCardDataRpcResponse getFullCardDataCached(String cardId) {
        // TEMPORARY: Generate random CVV and expiration for testing
        int randomCvv = ThreadLocalRandom.current().nextInt(100, 1000);
        int randomMonth = ThreadLocalRandom.current().nextInt(1, 13);
        int randomYear = ThreadLocalRandom.current().nextInt(2025, 2031);
        String expirationDate = String.format("%02d/%d", randomMonth, randomYear);

        return GetFullCardDataRpcResponse.newBuilder()
                .setCardId(cardId)
                .setNumber("4532015112830366") // TEMPORARY: mock card number
                .setBrand("VISA")
                .setExpirationDate(expirationDate)
                .setCardholderName("JOHN DOE") // TEMPORARY: mock name
                .setCvv(String.valueOf(randomCvv))
                .build();
    }
}

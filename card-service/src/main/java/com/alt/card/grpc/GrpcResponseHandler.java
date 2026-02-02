package com.alt.card.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility to handle gRPC response flow: execute supplier, map exceptions to Status, complete or error.
 */
public final class GrpcResponseHandler {

    private GrpcResponseHandler() {}

    public static <T> void handle(StreamObserver<T> responseObserver,
                                  Supplier<T> supplier,
                                  Function<Exception, Status> errorMapper) {
        try {
            T response = supplier.get();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(errorMapper.apply(e).withDescription(e.getMessage()).asException());
        }
    }
}

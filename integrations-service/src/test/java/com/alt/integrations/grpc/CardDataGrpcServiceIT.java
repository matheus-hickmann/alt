package com.alt.integrations.grpc;

import com.alt.proto.integrations.CardDataServiceGrpc;
import com.alt.proto.integrations.GetFullCardDataRpcRequest;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("CardDataGrpcService (integration)")
class CardDataGrpcServiceIT {

    @GrpcClient("card-data")
    CardDataServiceGrpc.CardDataServiceBlockingStub cardDataStub;

    @Test
    void should_return_card_data_via_grpc() {
        var request = GetFullCardDataRpcRequest.newBuilder()
                .setCardId("card_test_123")
                .build();

        var response = cardDataStub.getFullCardData(request);

        assertThat(response.getCardId()).isEqualTo("card_test_123");
    }
}

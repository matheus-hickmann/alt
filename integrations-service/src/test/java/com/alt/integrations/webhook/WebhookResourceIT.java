package com.alt.integrations.webhook;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@DisplayName("WebhookResource (integration)")
class WebhookResourceIT {

    @Test
    @DisplayName("card-delivery: full flow with carrier API key")
    void should_process_delivery_webhook_end_to_end() {
        var payload = """
                {
                    "tracking_id": "TRACK-IT-001",
                    "delivery_status": "DELIVERED",
                    "delivery_date": "2026-02-01T14:30:00",
                    "delivery_address": "789 Integration St"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .header("X-API-Key", "carrier-api-key-secret")
                .body(payload)
        .when()
                .post("/webhooks/card-delivery")
        .then()
                .statusCode(202)
                .body("message", containsString("received successfully"));
    }

    @Test
    @DisplayName("card-cvv-change: full flow with processor API key")
    void should_process_cvv_change_webhook_end_to_end() {
        var payload = """
                {
                    "account_id": "proc-acc-it-1",
                    "card_id": "proc-card-it-1",
                    "next_cvv": 456,
                    "expiration_date": "2026-05-01T12:00:00"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .header("X-API-Key", "processor-api-key-secret")
                .body(payload)
        .when()
                .post("/webhooks/card-cvv-change")
        .then()
                .statusCode(202)
                .body("message", containsString("received successfully"));
    }
}

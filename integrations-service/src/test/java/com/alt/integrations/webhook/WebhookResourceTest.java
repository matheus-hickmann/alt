package com.alt.integrations.webhook;

import com.alt.integrations.webhook.dto.CvvChangeWebhookPayload;
import com.alt.integrations.webhook.dto.DeliveryWebhookPayload;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@DisplayName("WebhookResource")
class WebhookResourceTest {

    private static final String CARRIER_API_KEY = "carrier-api-key-secret";
    private static final String PROCESSOR_API_KEY = "processor-api-key-secret";

    @Nested
    @DisplayName("card-delivery")
    class CardDelivery {

        @Test
        void should_accept_delivery_webhook_with_valid_api_key() {
            var payload = new DeliveryWebhookPayload(
                    "TRACK-12345",
                    "DELIVERED",
                    "2026-02-01T10:30:00",
                    null,
                    "123 Main St"
            );

            given()
                    .contentType(ContentType.JSON)
                    .header("X-API-Key", CARRIER_API_KEY)
                    .body(payload)
            .when()
                    .post("/webhooks/card-delivery")
            .then()
                    .statusCode(202)
                    .body(containsString("received successfully"));
        }

        @Test
        void should_reject_when_api_key_missing() {
            var payload = new DeliveryWebhookPayload(
                    "TRACK-123",
                    "DELIVERED",
                    "2026-02-01T10:00:00",
                    null,
                    "456 Test Ave"
            );

            given()
                    .contentType(ContentType.JSON)
                    .body(payload)
            .when()
                    .post("/webhooks/card-delivery")
            .then()
                    .statusCode(401)
                    .body(containsString("Missing"));
        }

        @Test
        void should_reject_when_api_key_invalid() {
            var payload = new DeliveryWebhookPayload(
                    "TRACK-999",
                    "FAILED",
                    "2026-02-01T09:00:00",
                    "Recipient absent",
                    null
            );

            given()
                    .contentType(ContentType.JSON)
                    .header("X-API-Key", "invalid-key")
                    .body(payload)
            .when()
                    .post("/webhooks/card-delivery")
            .then()
                    .statusCode(403)
                    .body(containsString("Invalid"));
        }
    }

    @Nested
    @DisplayName("card-cvv-change")
    class CardCvvChange {

        @Test
        void should_accept_cvv_change_webhook_with_valid_api_key() {
            var payload = new CvvChangeWebhookPayload(
                    "proc-acc-123",
                    "proc-card-456",
                    789,
                    "2026-03-01T10:30:00"
            );

            given()
                    .contentType(ContentType.JSON)
                    .header("X-API-Key", PROCESSOR_API_KEY)
                    .body(payload)
            .when()
                    .post("/webhooks/card-cvv-change")
            .then()
                    .statusCode(202)
                    .body(containsString("received successfully"));
        }

        @Test
        void should_reject_when_api_key_missing() {
            var payload = new CvvChangeWebhookPayload(
                    "proc-acc-999",
                    "proc-card-888",
                    123,
                    "2026-04-01T00:00:00"
            );

            given()
                    .contentType(ContentType.JSON)
                    .body(payload)
            .when()
                    .post("/webhooks/card-cvv-change")
            .then()
                    .statusCode(401);
        }
    }
}

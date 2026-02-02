package com.alt.integrations.mock;

import com.alt.integrations.webhook.dto.CvvChangeWebhookPayload;
import com.alt.integrations.webhook.dto.DeliveryWebhookPayload;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mock endpoints to simulate calls from external systems (carrier and processor).
 * Useful for testing: these endpoints simulate external integrations calling our webhooks.
 */
@Path("/mock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MockExternalResource {

    private static final Logger LOG = Logger.getLogger(MockExternalResource.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @ConfigProperty(name = "integrations.api-key.carrier", defaultValue = "carrier-api-key-secret")
    String carrierApiKey;

    @ConfigProperty(name = "integrations.api-key.processor", defaultValue = "processor-api-key-secret")
    String processorApiKey;

    @ConfigProperty(name = "integrations.webhook.base-url", defaultValue = "http://localhost:8081")
    String webhookBaseUrl;

    private final jakarta.ws.rs.client.Client client = jakarta.ws.rs.client.ClientBuilder.newClient();

    /**
     * Mock: simulates the carrier sending a successful delivery webhook.
     * 
     * @param trackingId Card tracking ID
     * @param deliveryAddress Delivery address
     */
    @POST
    @Path("/carrier/notify-delivery")
    public Response mockCarrierDelivery(
            @QueryParam("trackingId") @DefaultValue("TRACK-12345") String trackingId,
            @QueryParam("deliveryAddress") @DefaultValue("Rua Exemplo, 123") String deliveryAddress) {
        
        LOG.infof("Mock: Carrier notifying delivery - trackingId=%s", trackingId);

        DeliveryWebhookPayload payload = new DeliveryWebhookPayload(
            trackingId,
            "DELIVERED",
            LocalDateTime.now().format(FORMATTER),
            null,
            deliveryAddress
        );

        try {
            Response response = client.target(webhookBaseUrl + "/webhooks/card-delivery")
                .request(MediaType.APPLICATION_JSON)
                .header("X-API-Key", carrierApiKey)
                .post(jakarta.ws.rs.client.Entity.json(payload));

            String body = response.readEntity(String.class);
            LOG.infof("Mock: Webhook response status=%d, body=%s", response.getStatus(), body);

            return Response.ok()
                .entity("{\"message\":\"Mock delivery notification sent\",\"webhookStatus\":" + response.getStatus() + "}")
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Mock: Error sending delivery webhook");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    /**
     * Mock: simulates the carrier sending a delivery failure webhook.
     * 
     * @param trackingId Card tracking ID
     * @param returnReason Failure reason
     */
    @POST
    @Path("/carrier/notify-delivery-failed")
    public Response mockCarrierDeliveryFailed(
            @QueryParam("trackingId") @DefaultValue("TRACK-12345") String trackingId,
            @QueryParam("returnReason") @DefaultValue("Destinatário ausente") String returnReason) {
        
        LOG.infof("Mock: Carrier notifying delivery failure - trackingId=%s, reason=%s", trackingId, returnReason);

        DeliveryWebhookPayload payload = new DeliveryWebhookPayload(
            trackingId,
            "FAILED",
            LocalDateTime.now().format(FORMATTER),
            returnReason,
            null
        );

        try {
            Response response = client.target(webhookBaseUrl + "/webhooks/card-delivery")
                .request(MediaType.APPLICATION_JSON)
                .header("X-API-Key", carrierApiKey)
                .post(jakarta.ws.rs.client.Entity.json(payload));

            String body = response.readEntity(String.class);
            LOG.infof("Mock: Webhook response status=%d, body=%s", response.getStatus(), body);

            return Response.ok()
                .entity("{\"message\":\"Mock delivery failed notification sent\",\"webhookStatus\":" + response.getStatus() + "}")
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Mock: Error sending delivery failed webhook");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }

    /**
     * Mock: simulates the processor sending a CVV change webhook.
     * 
     * @param accountId Account ID in the processor
     * @param cardId Card ID in the processor
     * @param nextCvv New CVV (default: 456)
     */
    @POST
    @Path("/processor/notify-cvv-change")
    public Response mockProcessorCvvChange(
            @QueryParam("accountId") @DefaultValue("proc-acc-123") String accountId,
            @QueryParam("cardId") @DefaultValue("proc-card-456") String cardId,
            @QueryParam("nextCvv") @DefaultValue("456") Integer nextCvv) {
        
        LOG.infof("Mock: Processor notifying CVV change - accountId=%s, cardId=%s, nextCvv=%d", 
            accountId, cardId, nextCvv);

        CvvChangeWebhookPayload payload = new CvvChangeWebhookPayload(
            accountId,
            cardId,
            nextCvv,
            LocalDateTime.now().plusDays(30).format(FORMATTER)
        );

        try {
            Response response = client.target(webhookBaseUrl + "/webhooks/card-cvv-change")
                .request(MediaType.APPLICATION_JSON)
                .header("X-API-Key", processorApiKey)
                .post(jakarta.ws.rs.client.Entity.json(payload));

            String body = response.readEntity(String.class);
            LOG.infof("Mock: Webhook response status=%d, body=%s", response.getStatus(), body);

            return Response.ok()
                .entity("{\"message\":\"Mock CVV change notification sent\",\"webhookStatus\":" + response.getStatus() + "}")
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Mock: Error sending CVV change webhook");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
}

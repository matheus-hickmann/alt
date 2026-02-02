package com.alt.integrations.webhook;

import com.alt.integrations.auth.WebhookSecured;
import com.alt.integrations.webhook.dto.CvvChangeWebhookPayload;
import com.alt.integrations.webhook.dto.DeliveryWebhookPayload;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

/**
 * Webhook endpoints for external integrations: physical card delivery and virtual CVV change.
 * Requires authentication via X-API-Key header.
 */
@Path("/webhooks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WebhookResource {

    private static final Logger LOG = Logger.getLogger(WebhookResource.class);

    @POST
    @Path("/card-delivery")
    @WebhookSecured
    public Response cardDelivery(DeliveryWebhookPayload payload) {
        LOG.infof("Received card delivery webhook: trackingId=%s, status=%s, date=%s", 
            payload.trackingId(), payload.deliveryStatus(), payload.deliveryDate());
        
        // TODO: publish event to card-service to update status to DELIVERED
        // Example: if deliveryStatus == "DELIVERED", publish to Kafka for card-service to mark card as DELIVERED
        
        return Response.accepted()
            .entity("{\"message\":\"Delivery webhook received successfully\"}")
            .build();
    }

    @POST
    @Path("/card-cvv-change")
    @WebhookSecured
    public Response cardCvvChange(CvvChangeWebhookPayload payload) {
        LOG.infof("Received CVV change webhook: accountId=%s, cardId=%s, nextCvv=%d, expirationDate=%s", 
            payload.accountId(), payload.cardId(), payload.nextCvv(), payload.expirationDate());
        
        // TODO: persist/notify new CVV for the virtual card in card-service
        // Example: publish to Kafka for card-service to update virtual card CVV
        
        return Response.accepted()
            .entity("{\"message\":\"CVV change webhook received successfully\"}")
            .build();
    }
}

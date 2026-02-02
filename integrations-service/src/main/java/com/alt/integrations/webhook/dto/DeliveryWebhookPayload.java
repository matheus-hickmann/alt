package com.alt.integrations.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload sent by the carrier when delivering the physical card.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryWebhookPayload(
        @JsonProperty("tracking_id") String trackingId,
        @JsonProperty("delivery_status") String deliveryStatus,
        @JsonProperty("delivery_date") String deliveryDate,
        @JsonProperty("delivery_return_reason") String deliveryReturnReason,
        @JsonProperty("delivery_address") String deliveryAddress
) {}

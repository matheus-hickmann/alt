package com.alt.integrations.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Payload do webhook de mapeamento de entrega do cartão físico.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CardDeliveryWebhookRequest(
        String cardId,
        String accountId,
        String trackingCode,
        String status
) {}

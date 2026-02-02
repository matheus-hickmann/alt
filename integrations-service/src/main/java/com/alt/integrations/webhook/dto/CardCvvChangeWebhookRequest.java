package com.alt.integrations.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Payload do webhook para alteração de CVV do cartão virtual.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CardCvvChangeWebhookRequest(
        String cardId,
        String newCvv
) {}

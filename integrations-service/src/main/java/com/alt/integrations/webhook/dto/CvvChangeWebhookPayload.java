package com.alt.integrations.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload sent by the card processor when the virtual card CVV changes periodically.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CvvChangeWebhookPayload(
        @JsonProperty("account_id") String accountId,
        @JsonProperty("card_id") String cardId,
        @JsonProperty("next_cvv") Integer nextCvv,
        @JsonProperty("expiration_date") String expirationDate
) {}

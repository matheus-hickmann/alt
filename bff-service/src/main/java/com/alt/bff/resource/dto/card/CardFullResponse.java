package com.alt.bff.resource.dto.card;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Full card data (brand, number, expiration, name, CVV)")
public record CardFullResponse(
        @Schema(description = "Card identifier", required = true)
        String cardId,

        @Schema(description = "Card brand", example = "VISA", required = true)
        String brand,

        @Schema(description = "Full card number", example = "4532015112830366", required = true)
        String number,

        @Schema(description = "Expiration date (MM/YYYY)", example = "12/2028", required = true)
        String expirationDate,

        @Schema(description = "Name on card", example = "JOHN DOE", required = true)
        String cardholderName,

        @Schema(description = "Security code (CVV)", example = "123", required = true)
        String cvv
) {}

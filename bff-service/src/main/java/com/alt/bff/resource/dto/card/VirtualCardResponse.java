package com.alt.bff.resource.dto.card;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Virtual card issuance response with card data")
public record VirtualCardResponse(
        @Schema(description = "Virtual card identifier", required = true)
        String id,

        @Schema(description = "Full card number", example = "4532015112830366", required = true)
        String number,

        @Schema(description = "Masked number (first 4 and last 4 digits)", example = "4532********0366", required = true)
        String maskedNumber,

        @Schema(description = "Card brand", example = "VISA", required = true)
        String brand,

        @Schema(description = "Expiration date (MM/YYYY)", example = "12/2028", required = true)
        String expirationDate,

        @Schema(description = "Name on card", example = "JOHN DOE", required = true)
        String cardholderName,

        @Schema(description = "Security code (CVV)", example = "123", required = true)
        String cvv
) {}

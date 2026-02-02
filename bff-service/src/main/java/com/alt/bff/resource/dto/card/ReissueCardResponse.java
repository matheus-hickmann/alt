package com.alt.bff.resource.dto.card;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Card reissuance response with new card data (masked number)")
public record ReissueCardResponse(
        @Schema(description = "New card identifier", required = true)
        String id,

        @Schema(description = "Masked number (first 4 and last 4 digits)", example = "4532********9999", required = true)
        String maskedNumber,

        @Schema(description = "Card brand", example = "VISA", required = true)
        String brand
) {}

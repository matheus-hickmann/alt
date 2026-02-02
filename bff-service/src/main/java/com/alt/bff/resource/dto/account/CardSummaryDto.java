package com.alt.bff.resource.dto.account;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Card summary (first 4 and last 4 digits)")
public record CardSummaryDto(
        @Schema(description = "Card identifier")
        String id,
        @Schema(description = "Masked number (e.g.: 4532********0366)", example = "4532********0366")
        String maskedNumber,
        @Schema(description = "Card brand", example = "VISA")
        String brand,
        @Schema(description = "Card type", example = "PHYSICAL", enumeration = {"PHYSICAL", "VIRTUAL"})
        String type
) {}

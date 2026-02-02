package com.alt.bff.resource.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "API error response")
public record ErrorResponse(
        @Schema(description = "Error code", example = "CARD_001")
        String code,
        @Schema(description = "Descriptive error message", example = "No physical cards enabled")
        String message
) {}

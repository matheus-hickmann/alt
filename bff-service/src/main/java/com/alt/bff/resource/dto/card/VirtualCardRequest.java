package com.alt.bff.resource.dto.card;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Virtual card issuance request")
public record VirtualCardRequest(
        @Schema(description = "Account identifier", required = true)
        @NotBlank(message = "Account ID is required")
        String accountId
) {}

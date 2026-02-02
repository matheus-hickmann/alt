package com.alt.bff.resource.dto.card;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Physical card reissuance request")
public record ReissueCardRequest(
        @Schema(description = "Reissuance reason", required = true, enumeration = {"LOSS", "THEFT", "DAMAGE"})
        @NotNull(message = "Reason is required")
        ReissueReason reason
) {}

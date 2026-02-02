package com.alt.bff.resource.dto.account;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "Account data with card summary")
public record AccountResponse(
        @Schema(description = "Account identifier", required = true)
        String accountId,

        @Schema(description = "Account number", example = "12345678", required = true)
        String accountNumber,

        @Schema(description = "Account holder name", example = "John Doe", required = true)
        String name,

        @Schema(description = "Account holder email", example = "john@email.com", required = true)
        String email,

        @Schema(description = "Account holder document (CPF or CNPJ)", example = "12345678901", required = true)
        String document,

        @Schema(description = "Account status", example = "ACTIVE", enumeration = {"ACTIVE", "CANCELLED"})
        String status,

        @Schema(description = "List of account cards (masked number)")
        List<CardSummaryDto> cards
) {}

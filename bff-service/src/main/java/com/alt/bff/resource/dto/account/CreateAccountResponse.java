package com.alt.bff.resource.dto.account;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Account creation response with account number; physical card is created asynchronously (use getAccount to list cards)")
public record CreateAccountResponse(
        @Schema(description = "Account number", example = "12345678", required = true)
        String accountNumber,

        @Schema(description = "Physical card number (empty at creation; card is created asynchronously via Kafka)")
        String cardNumber
) {}

package com.alt.bff.resource.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Data for user account creation")
public record CreateAccountRequest(
        @Schema(description = "Account holder full name", example = "John Doe", required = true, maxLength = 200)
        @NotBlank(message = "Name is required")
        @Size(max = 200)
        String name,

        @Schema(description = "Account holder email", example = "john@email.com", required = true)
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @Schema(description = "CPF or CNPJ (numbers only)", example = "12345678901", required = true, minLength = 11, maxLength = 14)
        @NotBlank(message = "Document is required")
        @Size(min = 11, max = 14)
        String document,

        @Schema(description = "Contact phone number", example = "11999999999", maxLength = 20)
        @Size(max = 20)
        String phone
) {}

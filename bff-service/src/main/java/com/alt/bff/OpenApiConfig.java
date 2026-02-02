package com.alt.bff;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "BFF Service API",
                version = "1.0.0",
                description = "Backend for Frontend API - Accounts and Cards",
                contact = @Contact(name = "Alt")
        ),
        servers = {
                @Server(url = "/", description = "Local server")
        }
)
public class OpenApiConfig {
}

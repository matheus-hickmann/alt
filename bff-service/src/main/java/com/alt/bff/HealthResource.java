package com.alt.bff;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health", description = "Verificação de saúde do serviço")
public class HealthResource {

    @GET
    @Operation(summary = "Health check", description = "Retorna o status do serviço")
    public Response health() {
        return Response.ok(Map.of(
                "status", "UP",
                "service", "bff-service"
        )).build();
    }
}

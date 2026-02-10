package com.alt.bff.resource;

import com.alt.bff.resource.dto.card.*;
import com.alt.bff.service.CardService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/cards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cards", description = "Operations related to cards")
public class CardResource {

    private final CardService cardService;

    public CardResource(CardService cardService) {
        this.cardService = cardService;
    }

    @POST
    @Path("/virtual")
    @Operation(summary = "Issue virtual card", description = "Receives a request to issue a virtual card. Returns virtual card data or error if no physical cards are enabled.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Virtual card issued successfully"),
            @APIResponse(responseCode = "400", description = "Invalid data"),
            @APIResponse(responseCode = "422", description = "No physical cards enabled")
    })
    public Response issueVirtualCard(@Valid VirtualCardRequest request) {
        var response = cardService.issueVirtualCard(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/{cardId}/reissue")
    @Operation(summary = "Card reissuance", description = "Physical cards can be reissued for reasons such as loss, theft or damage. Returns success with new card data (first 4 and last 4 digits) or error.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Card reissued successfully"),
            @APIResponse(responseCode = "400", description = "Invalid data"),
            @APIResponse(responseCode = "404", description = "Card not found")
    })
    public Response reissueCard(
            @Parameter(description = "Card identifier to be reissued", required = true)
            @PathParam("cardId") String cardId,
            @Valid ReissueCardRequest request) {
        var response = cardService.reissueCard(cardId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{cardId}/cancel")
    @Operation(summary = "Cancel card", description = "Receives the card id to perform its cancellation.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Card cancelled successfully"),
            @APIResponse(responseCode = "404", description = "Card not found")
    })
    public Response cancelCard(
            @Parameter(description = "Card identifier to be cancelled", required = true)
            @PathParam("cardId") String cardId) {
        cardService.cancelCard(cardId);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{cardId}/activate")
    @Operation(summary = "Activate card", description = "Activates a physical card. Can only activate if card is in DELIVERED status; after activation it changes to ACTIVE and account cache is invalidated.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Card activated successfully"),
            @APIResponse(responseCode = "404", description = "Card not found"),
            @APIResponse(responseCode = "409", description = "Card is not in DELIVERED status")
    })
    public Response activateCard(
            @Parameter(description = "Card identifier to be activated", required = true)
            @PathParam("cardId") String cardId) {
        cardService.activateCard(cardId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{cardId}/full")
    @Operation(summary = "Get full card data", description = "Returns all card data: brand, number, expiration date, cardholder name and CVV.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Full card data"),
            @APIResponse(responseCode = "404", description = "Card not found")
    })
    public Response getFullCardData(
            @Parameter(description = "Card identifier", required = true)
            @PathParam("cardId") String cardId) {
        var response = cardService.getFullCardData(cardId);
        return Response.ok(response).build();
    }
}

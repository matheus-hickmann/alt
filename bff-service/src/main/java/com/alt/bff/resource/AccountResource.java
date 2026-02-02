package com.alt.bff.resource;

import com.alt.bff.resource.dto.account.AccountResponse;
import com.alt.bff.resource.dto.account.CreateAccountRequest;
import com.alt.bff.resource.dto.account.CreateAccountResponse;
import com.alt.bff.service.AccountService;
import io.quarkus.security.Authenticated;
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

@Path("/api/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Accounts", description = "Operations related to user accounts")
@Authenticated
public class AccountResource {

    private final AccountService accountService;

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Operation(summary = "Create account", description = "Inserts basic data for user creation. Returns account number and card number.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Account created successfully"),
            @APIResponse(responseCode = "400", description = "Invalid data")
    })
    public Response createAccount(@Valid CreateAccountRequest request) {
        var response = accountService.createAccount(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{accountId}")
    @Operation(summary = "Get account data", description = "Returns account data along with its cards (showing only first 4 and last 4 digits).")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Account data returned successfully"),
            @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response getAccountData(
            @Parameter(description = "Account identifier", required = true)
            @PathParam("accountId") String accountId) {
        var response = accountService.getAccountData(accountId);
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{accountId}/cancel")
    @Operation(summary = "Cancel account", description = "Cancels the specified account.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Account cancelled successfully"),
            @APIResponse(responseCode = "404", description = "Account not found")
    })
    public Response cancelAccount(
            @Parameter(description = "Account identifier to be cancelled", required = true)
            @PathParam("accountId") String accountId) {
        accountService.cancelAccount(accountId);
        return Response.noContent().build();
    }
}

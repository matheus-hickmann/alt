package com.alt.bff.exception;

import com.alt.bff.resource.dto.ErrorResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Maps gRPC StatusRuntimeException to HTTP responses.
 */
@Provider
public class GrpcStatusExceptionMapper implements ExceptionMapper<StatusRuntimeException> {

    private static final Logger LOG = Logger.getLogger(GrpcStatusExceptionMapper.class);

    @Override
    public Response toResponse(StatusRuntimeException exception) {
        Status status = exception.getStatus();
        Status.Code code = status.getCode();
        String description = status.getDescription() != null ? status.getDescription() : exception.getMessage();

        LOG.debugf("gRPC error mapped: code=%s, description=%s", code, description);

        int httpStatus = switch (code) {
            case NOT_FOUND -> Response.Status.NOT_FOUND.getStatusCode();
            case INVALID_ARGUMENT -> Response.Status.BAD_REQUEST.getStatusCode();
            case FAILED_PRECONDITION -> Response.Status.CONFLICT.getStatusCode(); // e.g. card not in DELIVERED status
            case PERMISSION_DENIED -> Response.Status.FORBIDDEN.getStatusCode();
            case UNAUTHENTICATED -> Response.Status.UNAUTHORIZED.getStatusCode();
            case ALREADY_EXISTS -> Response.Status.CONFLICT.getStatusCode();
            case RESOURCE_EXHAUSTED -> Response.Status.SERVICE_UNAVAILABLE.getStatusCode();
            case UNAVAILABLE -> Response.Status.SERVICE_UNAVAILABLE.getStatusCode();
            default -> Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        };

        ErrorResponse error = new ErrorResponse("GRPC_" + code.name(), description);
        return Response.status(httpStatus).entity(error).build();
    }
}

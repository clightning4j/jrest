package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.types.CLightningPing;

public class NetworkServices {

    private static final String SERVICE = "Network";

    @OpenApi(
            path = "/network/ping",            // only necessary to include when using static method references
            method = HttpMethod.GET,    // only necessary to include when using static method references
            summary = "Ping the node and return the result",
            operationId = SERVICE,
            tags = {SERVICE},
            pathParams = {
                    @OpenApiParam(name = "nodeId", description = "Lightning node identifier", required = true),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = CLightningPing.class)}),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = ErrorMessage.class)})
            }
    )
    public static void ping(Context context) {
        String nodeId = context.pathParam("nodeId");
        try {
            CLightningPing ping = CLightningRPC.getInstance().ping(nodeId);
            UtilsService.makeSuccessResponse(context, ping);
        }catch (CLightningException exception) {
            UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
        }
    }
}

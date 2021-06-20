package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import jrpc.clightning.model.CLightningListInvoices;

public class PluginServices {

    private static final String SERVICE = "Plugin Services";

    @OpenApi(
            path = "/plugin/diagnostic",            // only necessary to include when using static method references
            method = HttpMethod.GET,    // only necessary to include when using static method references
            summary = "Get the diagnostic by metrics ID",
            operationId = SERVICE,
            tags = {SERVICE},
            pathParams = {
                    @OpenApiParam(name = "metric_id", type = Integer.class, required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = CLightningListInvoices.class)}),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = ErrorMessage.class)}),
            }
    )
    public static void diagnostic(Context context) {
        Integer spent = context.formParam("metric_id", Integer.class).getOrNull();
        //TODO make the wrapper method if the plugin is registered
        UtilsService.makeErrorResponse(context, "Not implemented yet");
    }
}

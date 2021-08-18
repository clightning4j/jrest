package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.utils.rpc.CLightningCommand;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.exceptions.CommandException;
import jrpc.clightning.model.CLightningListInvoices;

import java.util.HashMap;

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
        Integer metricId = context.formParam("metric_id", Integer.class).getOrNull();
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("metric_id", metricId);
        CLightningDiagnostic result = CLightningRPC.getInstance().runRegisterCommand(CLightningCommand.DIAGNOSTIC, payload);
        try {
            UtilsService.makeSuccessResponse(context, result);
        } catch (CLightningException | CommandException exception) {
            UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
        }
    }
}

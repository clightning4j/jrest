package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.utils.rpc.CLightningCommand;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.exceptions.CommandException;

import java.util.HashMap;

public class PluginServices {

  private static final String SERVICE = "Plugin Services";

  @OpenApi(
      path = "/plugin/diagnostic", // only necessary to include when using static method references
      method = HttpMethod.GET, // only necessary to include when using static method references
      summary = "Get the diagnostic by metrics ID",
      operationId = SERVICE,
      tags = {SERVICE},
      formParams = {
        @OpenApiFormParam(name = "metric_id", type = Integer.class, required = false),
        @OpenApiFormParam(name = "metrics_id", type = String.class, required = false)
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningDiagnostic.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = ErrorMessage.class)}),
      })
  public static void diagnostic(Context context) {
    Integer metricId = context.formParam("metric_id", Integer.class).getOrNull();
    String metricsId = context.formParam("metrics_id", String.class).getOrNull();
    HashMap<String, Object> payload = new HashMap<>();
    if ((metricsId != null || !metricsId.isEmpty()) && metricId != null)
      UtilsService.makeErrorResponse(context, "Specified metric_id or metrics_id not both");
    if (metricId != null) payload.put("metric_id", metricId);
    if (metricsId != null && !metricsId.isEmpty()) payload.put("metrics_id", metricsId);
    CLightningDiagnostic result =
        CLightningRPC.getInstance().runRegisterCommand(CLightningCommand.DIAGNOSTIC, payload);
    try {
      UtilsService.makeSuccessResponse(context, result);
    } catch (CLightningException | CommandException exception) {
      UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
    }
  }
}

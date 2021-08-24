package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.utils.rpc.CLightningCommand;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.plugins.CLightningPlugin;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.PluginLog;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;
import org.reflections.util.Utils;

import java.util.HashMap;

public class PluginServices {

  private static final String SERVICE = "Plugin Services";
  private static final Class TAG = PluginServices.class;

  @OpenApi(
      path = "/plugin/diagnostic", // only necessary to include when using static method references
      method = HttpMethod.GET, // only necessary to include when using static method references
      summary = "Get the diagnostic by metrics ID or list of IDs",
      operationId = SERVICE,
      tags = {SERVICE},
      queryParams = {
        @OpenApiParam(name = "metric_id", type = Integer.class, required = false),
        @OpenApiParam(name = "metrics_id", type = String.class, required = false)
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningDiagnostic.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = ErrorMessage.class)}),
      })
  public static void diagnostic(Context context, ICLightningPlugin plugin) {
    try {
      Integer metricId = context.queryParam("metric_id", Integer.class).getOrNull();
      String metricsId = context.queryParam("metrics_id", String.class).getOrNull();
      HashMap<String, Object> payload = new HashMap<>();
      if ((metricsId != null || !metricsId.isEmpty()) && metricId != null)
        UtilsService.makeErrorResponse(context, "Specified metric_id or metrics_id not both");
      if (metricId != null) {
        plugin.log(PluginLog.INFO, "Metric id requested " + metricId);
        payload.put("metric_id", metricId);
      }
      if (metricsId != null && !metricsId.isEmpty()) {
        plugin.log(PluginLog.INFO, "Metrics id requested " + metricsId);
        payload.put("metrics_id", metricsId);
      }
      if (payload.isEmpty())
        UtilsService.makeErrorResponse(context, plugin, "payload to call diagnostic method empty");
      CLightningDiagnostic result = CLightningRPC.getInstance().runRegisterCommand(CLightningCommand.DIAGNOSTIC, payload);
      if (result == null)
        UtilsService.makeErrorResponse(context, plugin, "Result from diagnostic is null");
      UtilsService.makeSuccessResponse(context, result);
    } catch (Exception ex) {
      UtilsService.makeErrorResponse(context, plugin, ex.toString());
    }
  }
}

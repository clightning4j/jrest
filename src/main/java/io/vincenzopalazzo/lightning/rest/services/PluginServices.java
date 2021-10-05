package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.utils.UtilsService;
import io.vincenzopalazzo.lightning.rest.utils.rpc.CLightningCommand;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.PluginLog;

public class PluginServices {

  static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private static final String SERVICE = "Plugin Services";

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
    Integer metricId = context.queryParamAsClass("metric_id", Integer.class).getOrDefault(null);
    String metricsId = context.queryParamAsClass("metrics_id", String.class).getOrDefault(null);
    HashMap<String, Object> payload = new HashMap<>();
    if ((metricsId != null && !metricsId.isEmpty()) && metricId != null)
      throw new InternalServerErrorResponse("Specified metric_id or metrics_id not both");
    if (metricId != null) {
      plugin.log(PluginLog.INFO, "Metric id requested " + metricId);
      payload.put("metric_id", metricId);
    }
    if (metricsId != null && !metricsId.isEmpty()) {
      plugin.log(PluginLog.INFO, "Metrics id requested " + metricsId);
      payload.put("metrics_id", metricsId);
    }
    if (payload.isEmpty())
      throw new InternalServerErrorResponse("payload to call diagnostic method empty");

    context.future(
        runCommandDiagnostic(payload),
        result -> {
          if (result != null) {
            UtilsService.makeSuccessResponse(context, result);
          }
        });
  }

  public static CompletableFuture<CLightningDiagnostic> runCommandDiagnostic(
      HashMap<String, Object> payload) {
    var future = new CompletableFuture<CLightningDiagnostic>();
    executor.schedule(
        () ->
            future.complete(
                CLightningRPC.getInstance()
                    .runRegisterCommand(CLightningCommand.DIAGNOSTIC, payload)),
        1,
        TimeUnit.MILLISECONDS);
    return future;
  }
}

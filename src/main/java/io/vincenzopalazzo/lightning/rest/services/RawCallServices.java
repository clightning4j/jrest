package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.vincenzopalazzo.lightning.rest.utils.UtilsService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jrpc.clightning.CLightningRPC;

public class RawCallServices {
  private static final String SERVICE = "JSON RPC 2.0 result";

  static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  @OpenApi(
      path = "/raw/listchannels", // only necessary to include when using static method references
      method = HttpMethod.GET, // only necessary to include when using static method references
      summary = "Return all channels open in the backend",
      operationId = SERVICE,
      tags = {SERVICE},
      responses = {@OpenApiResponse(status = "200")})
  public static void rawListChannels(Context ctx) {
    ctx.future(
        getChannelsRaw(ctx),
        listChannels -> UtilsService.makeSuccessRawResponse(ctx, (String) listChannels));
  }

  public static CompletableFuture<String> getChannelsRaw(Context ctx) {
    var future = new CompletableFuture<String>();
    executor.schedule(
        () -> future.complete(CLightningRPC.getInstance().rawCommand("listchannels")),
        1,
        TimeUnit.MILLISECONDS);
    return future;
  }
}

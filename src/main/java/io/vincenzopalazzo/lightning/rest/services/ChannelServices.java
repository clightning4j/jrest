package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.utils.UtilsService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.model.CLightningListChannels;

public class ChannelServices {

  private static final String SERVICE = "Channel Services";

  static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  @OpenApi(
      path =
          "/channel/listchannels", // only necessary to include when using static method references
      method = HttpMethod.GET, // only necessary to include when using static method references
      summary = "Return all channels open in the backend",
      operationId = SERVICE,
      tags = {SERVICE},
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningListChannels.class)})
      })
  public static void listChannels(Context ctx) {
    ctx.future(
        getChannels(ctx), listChannels -> UtilsService.makeSuccessResponse(ctx, listChannels));
  }

  public static CompletableFuture<CLightningListChannels> getChannels(Context ctx) {
    var future = new CompletableFuture<CLightningListChannels>();
    executor.schedule(
        () -> future.complete(CLightningRPC.getInstance().listChannels()),
        1,
        TimeUnit.MILLISECONDS);
    return future;
  }
}

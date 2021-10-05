package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import io.vincenzopalazzo.lightning.rest.utils.UtilsService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.model.CLightningListNodes;
import jrpc.clightning.model.types.CLightningNode;
import jrpc.clightning.model.types.CLightningPing;

public class NetworkServices {

  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor();

  private static final String SERVICE = "Network";

  @OpenApi(
      path = "/network/ping", // only necessary to include when using static method references
      method = HttpMethod.GET, // only necessary to include when using static method references
      summary = "Ping the node and return the result",
      operationId = SERVICE,
      tags = {SERVICE},
      pathParams = {
        @OpenApiParam(name = "nodeId", description = "Lightning node identifier", required = true),
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningPing.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = ErrorMessage.class)})
      })
  public static void ping(Context ctx) {
    String nodeId = ctx.pathParam("nodeId");
    ctx.future(
        pingNode(nodeId),
        result -> {
          if (result != null) UtilsService.makeSuccessResponse(ctx, result);
        });
  }

  @OpenApi(
      path = "/network/listnodes", // only necessary to include when using static method references
      method = HttpMethod.GET, // only necessary to include when using static method references
      summary = "Return all the nodes connected to the actual ln node",
      operationId = SERVICE,
      tags = {SERVICE},
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningListNodes.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = InternalServerErrorResponse.class)})
      })
  public static void listNodes(Context ctx) {
    ctx.future(
        getListNodes(null),
        result -> {
          if (result != null) UtilsService.makeSuccessResponse(ctx, result);
        });
  }

  @OpenApi(
      path = "/network/listnodes", // only necessary to include when using static method references
      method = HttpMethod.POST, // only necessary to include when using static method references
      summary = "Return all the nodes connected to the actual ln node",
      operationId = SERVICE,
      tags = {SERVICE},
      formParams = {
        @OpenApiFormParam(name = "nodeId", required = true),
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningNode.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = InternalServerErrorResponse.class)})
      }) // TODO move it in the GET request
  public static void listNodesWithId(Context ctx) {
    String nodeId = ctx.formParam("nodeId");
    ctx.future(
        getListNodes(nodeId),
        result -> {
          if (result != null) UtilsService.makeSuccessResponse(ctx, result);
        });
  }

  public static CompletableFuture<CLightningListNodes> getListNodes(String nodeId) {
    var future = new CompletableFuture<CLightningListNodes>();
    executor.schedule(
        () -> {
          CLightningListNodes listNodes;
          if (nodeId != null) listNodes = CLightningRPC.getInstance().listNodes(nodeId);
          else listNodes = CLightningRPC.getInstance().listNodes();
          future.complete(listNodes);
        },
        1,
        TimeUnit.MILLISECONDS);
    return future;
  }

  public static CompletableFuture<CLightningPing> pingNode(String nodeId) {
    var future = new CompletableFuture<CLightningPing>();
    executor.schedule(
        () -> future.complete(CLightningRPC.getInstance().ping(nodeId)), 1, TimeUnit.MILLISECONDS);
    return future;
  }
}

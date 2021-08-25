package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningListNodes;
import jrpc.clightning.model.types.CLightningNode;
import jrpc.clightning.model.types.CLightningPing;

public class NetworkServices {

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
  public static void ping(Context context) {
    String nodeId = context.pathParam("nodeId");
    try {
      CLightningPing ping = CLightningRPC.getInstance().ping(nodeId);
      UtilsService.makeSuccessResponse(context, ping);
    } catch (CLightningException exception) {
      UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
    }
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
    try {
      CLightningListNodes nodes = CLightningRPC.getInstance().listNodes();
      UtilsService.makeSuccessResponse(ctx, nodes);
    } catch (CLightningException ex) {
      throw new InternalServerErrorResponse(ex.getLocalizedMessage());
    }
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
    try {
      String nodeId = ctx.formParam("nodeId");
      CLightningListNodes nodes = CLightningRPC.getInstance().listNodes(nodeId);
      UtilsService.makeSuccessResponse(ctx, nodes);
    } catch (CLightningException ex) {
      throw new InternalServerErrorResponse(ex.getLocalizedMessage());
    }
  }
}

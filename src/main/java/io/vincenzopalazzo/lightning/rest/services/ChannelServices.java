package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.exceptions.CommandException;
import jrpc.clightning.model.CLightningListChannels;

public class ChannelServices {

  private static final String SERVICE = "Channel Services";

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
    try {
      CLightningListChannels listChannels = CLightningRPC.getInstance().listChannels();
      UtilsService.makeSuccessResponse(ctx, listChannels);
    } catch (CLightningException | CommandException ex) {
      UtilsService.makeErrorResponse(ctx, ex.getLocalizedMessage());
    }
  }
}

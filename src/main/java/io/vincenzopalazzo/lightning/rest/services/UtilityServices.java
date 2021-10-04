package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.exceptions.CommandException;
import jrpc.clightning.model.CLightningGetInfo;
import jrpc.clightning.model.CLightningListFunds;

public class UtilityServices {

  private static final String SERVICE = "Utility";

  @OpenApi(
      path = "/utility/getinfo", // only necessary to include when using static method references
      method = HttpMethod.GET, // only necessary to include when using static method references
      summary = "Receive c-lightning node info",
      operationId = SERVICE,
      tags = {SERVICE},
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningGetInfo.class)})
      })
  public static void getInfo(Context context) {
    try {
      var result = CLightningRPC.getInstance().getInfo();
      UtilsService.makeSuccessResponse(context, result);
    } catch (CLightningException | CommandException exception) {
      UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
    }
  }

  @OpenApi(
      path = "/utility/listfounds",
      method = HttpMethod.GET,
      summary =
          "displays all funds available, either in unspent outputs (UTXOs) in the internal wallet or funds locked in currently open channels",
      operationId = SERVICE,
      tags = {SERVICE},
      pathParams = {
        @OpenApiParam(
            name = "spent",
            description =
                "if true, then the outputs will include spent outputs in addition to the unspent ones",
            type = Boolean.class)
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningListFunds.class)})
      })
  public static void listFunds(Context context) {
    Boolean spent = context.formParamAsClass("spent", Boolean.class).getOrDefault(null);
    if (spent == null) spent = false;
    try {
      // TODO add method in the list funds
      var result = CLightningRPC.getInstance().listFunds();
      UtilsService.makeSuccessResponse(context, result);
    } catch (CLightningException | CommandException exception) {
      UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
    }
  }
}

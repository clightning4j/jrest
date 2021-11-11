package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.CLightningVerifyMessage;
import io.vincenzopalazzo.lightning.rest.utils.UtilsService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningGetInfo;
import jrpc.clightning.model.CLightningListFunds;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.PluginLog;
import jrpc.service.converters.JsonConverter;
import jrpc.wrapper.response.RPCResponseWrapper;

public class UtilityServices {

  private static final String SERVICE = "Utility";
  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor();

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
    var result = CLightningRPC.getInstance().getInfo();
    UtilsService.makeSuccessResponse(context, result);
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
    Boolean spent = context.formParamAsClass("spent", Boolean.class).getOrDefault(false);
    // TODO add method in the list funds
    var result = CLightningRPC.getInstance().listFunds();
    UtilsService.makeSuccessResponse(context, result);
  }

  @OpenApi(
      path = "/utility/checkmessage",
      method = HttpMethod.POST,
      summary = "Verify a digital signature {zbase} of {message} signed with {pubkey}\n",
      operationId = SERVICE,
      tags = {SERVICE},
      formParams = {
        @OpenApiFormParam(name = "message", required = true),
        @OpenApiFormParam(name = "zbase", required = true),
        @OpenApiFormParam(name = "pubkey"),
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningListFunds.class)})
      })
  public static void checkMessage(Context context, ICLightningPlugin plugin) {
    HashMap<String, Object> payload = new HashMap<>();
    payload.put("message", context.formParamAsClass("message", String.class));
    payload.put("zbase", context.formParamAsClass("zbase", String.class));
    if (context.formParamMap().containsKey("pubkey"))
      payload.put("message", context.formParamAsClass("pubkey", String.class));
    payload.put("message", context.formParamAsClass("message", String.class));
    context.future(
        verifyMessage(context, plugin, payload),
        result -> {
          if (result != null) {
            UtilsService.makeSuccessResponse(context, result);
          }
        });
  }

  public static CompletableFuture<CLightningVerifyMessage> verifyMessage(
      Context ctx, ICLightningPlugin plugin, Map<String, Object> payload) {
    var future = new CompletableFuture<CLightningVerifyMessage>();
    executor.schedule(
        () -> {
          String response = null;
          try {
            response = CLightningRPC.getInstance().rawCommand("checkmessage", payload);
          } catch (IOException e) {
            plugin.log(PluginLog.ERROR, e.getLocalizedMessage());
            future.completeExceptionally(e);
          }
          RPCResponseWrapper<CLightningVerifyMessage> verifyMessage;
          JsonConverter converter = new JsonConverter();
          verifyMessage =
              (RPCResponseWrapper<CLightningVerifyMessage>)
                  converter.deserialization(response, converter.getClass());
          if (verifyMessage.getError() == null) future.complete(verifyMessage.getResult());
          else
            future.completeExceptionally(
                new CLightningException(verifyMessage.getError().getMessage()));
        },
        1,
        TimeUnit.MILLISECONDS);
    return future;
  }
}

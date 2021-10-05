package io.vincenzopalazzo.lightning.rest.utils;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.PluginLog;

public class UtilsService {
  public static void makeErrorResponse(
      Context context, ICLightningPlugin plugin, String exception) {
    plugin.log(
        PluginLog.ERROR,
        String.format(
            "Request performed with URI %s throws the following exception %s: ",
            context.fullUrl(), exception));
    throw new InternalServerErrorResponse(exception);
  }

  public static void makeErrorResponse(Context context, String exception) {
    context.status(500);
    throw new InternalServerErrorResponse(exception);
  }

  public static <T> void makeSuccessResponse(Context context, T response) {
    // context.json(new SuccessMessage<T>(response));
    context.status(200);
    context.json(response);
  }

  static <T> void makeSuccessResponse(Context context, ICLightningPlugin plugin, T response) {
    // context.json(new SuccessMessage<T>(response));
    plugin.log(
        PluginLog.DEBUG,
        String.format("Request to URI %s performed with success", context.fullUrl()));
    context.json(response);
    context.status(200);
  }
}

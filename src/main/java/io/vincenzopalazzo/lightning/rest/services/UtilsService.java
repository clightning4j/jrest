package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.PluginLog;

class UtilsService {
    static void makeErrorResponse(Context context, ICLightningPlugin plugin, String exception) {
        plugin.log(PluginLog.ERROR, String.format("Request performed with URI %s throws the following exception %s: ", context.fullUrl(), exception));
        throw new InternalServerErrorResponse(exception);
    }

    static void makeErrorResponse(Context context, String exception) {
        throw new InternalServerErrorResponse(exception);
    }

    static <T> void makeSuccessResponse(Context context, T response) {
        //context.json(new SuccessMessage<T>(response));
        context.json(response);
        context.status(200);
    }

    static <T> void makeSuccessResponse(Context context, ICLightningPlugin plugin, T response) {
        //context.json(new SuccessMessage<T>(response));
        plugin.log(PluginLog.DEBUG, String.format("Request to URI %s performed with success", context.fullUrl()));
        context.json(response);
        context.status(200);
    }
}

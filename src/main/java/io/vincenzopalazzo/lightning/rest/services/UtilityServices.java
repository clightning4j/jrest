package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.model.CLightningGetInfo;

public class UtilityServices {

    private static final String SERVICE = "Utility";

    @OpenApi(
            path = "/utility/getinfo",            // only necessary to include when using static method references
            method = HttpMethod.GET,    // only necessary to include when using static method references
            summary = "Receive c-lightning node info",
            operationId = SERVICE,
            tags = {SERVICE},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = CLightningGetInfo.class)})
            }
    )
    public static void getInfo(Context context){
        context.json(CLightningRPC.getInstance().getInfo());
    }
}

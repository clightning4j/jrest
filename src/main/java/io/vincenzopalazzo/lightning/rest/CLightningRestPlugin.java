package io.vincenzopalazzo.lightning.rest;

import com.google.gson.JsonArray;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import io.vincenzopalazzo.lightning.rest.services.BitcoinServices;
import io.vincenzopalazzo.lightning.rest.services.UtilityServices;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.commands.Command;
import jrpc.clightning.plugins.CLightningPlugin;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.CLightningLevelLog;
import jrpc.clightning.plugins.rpcmethods.RPCMethod;
import jrpc.service.converters.JsonConverter;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;
import jrpc.wrapper.response.ErrorResponse;

public class CLightningRestPlugin extends CLightningPlugin {

    private static final String BITCOIN_SECTION = "/bitcoin/";

    @Override
    protected void registerMethod() {
        super.registerMethod();
        super.addRPCMethod(new CommandServerRestMethod("restserver", "[operation]", "This plugin help to introduce the rest server on C-lightning node."));
    }

    public class CommandServerRestMethod extends RPCMethod {

        private Javalin serverInstance;
        private JsonConverter converter = new JsonConverter();

        public CommandServerRestMethod(String name, String usage, String description) {
            super(name, usage, description);
            Info info = new Info().version("1.0").description("User API");
            OpenApiOptions options = new OpenApiOptions(info)
                    .activateAnnotationScanningFor("io.vincenzopalazzo.lightning.rest.services")
                    .path("/swagger-docs") // endpoint for OpenAPI json
                    .swagger(new SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
                    .reDoc(new ReDocOptions("/redoc")) // endpoint for redoc
                    .defaultDocumentation(doc -> {
                        doc.json("500", ErrorResponse.class);
                        doc.json("503", ErrorResponse.class);
                    });
            serverInstance = Javalin.create(config -> {
                config.registerPlugin(new OpenApiPlugin(options));
                config.defaultContentType = "application/json";
            });
        }


        @Override
        public void doRun(ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
            plugin.log(CLightningLevelLog.DEBUG, "Request from server:\n" + request.toString());
            if (request != null) {
                JsonArray params = request.get("params").getAsJsonArray();
                String operation = params.get(0).getAsString();

                if (operation.equalsIgnoreCase("start")) {
                    if (serverInstance != null) {
                        serverInstance.start(7003);

                        setBitcoinServices(serverInstance);
                        setUtilityServices(serverInstance);
                        serverInstance.get("/listinvoice", ctx -> ctx.result(converter.serialization(CLightningRPC.getInstance().getListInvoices())));
                        serverInstance.get("/decodepay", ctx -> ctx.result(converter.serialization(CLightningRPC.getInstance().decodePay(
                                ctx.queryParam("bolt11")
                        ))));

                        response.add("status", "running");
                        response.add("port", serverInstance.port());
                    }
                } else if (operation.equalsIgnoreCase("stop")) {
                    if (serverInstance != null) {
                        serverInstance.stop();
                        response.add("status", "shutdown");
                        response.add("port", serverInstance.port());
                        Info info = new Info().version("0.1").description("C-lightning REST API");
                        OpenApiOptions options = new OpenApiOptions(info)
                                .activateAnnotationScanningFor("io.vincenzopalazzo.lightning.rest.services")
                                .path("/swagger-docs") // endpoint for OpenAPI json
                                .swagger(new SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
                                .reDoc(new ReDocOptions("/redoc")) // endpoint for redoc
                                .defaultDocumentation(doc -> {
                                    doc.json("500", ErrorResponse.class);
                                    doc.json("503", ErrorResponse.class);
                                });
                        serverInstance = Javalin.create(config -> {
                            config.registerPlugin(new OpenApiPlugin(options));
                            config.defaultContentType = "application/json";
                        });
                    }
                }
            }
        }

        private void setBitcoinServices(Javalin serverInstance) {
            serverInstance.get(BITCOIN_SECTION + Command.NEWADDR.getCommandKey().toLowerCase(),
                    ctx -> BitcoinServices.newAddr(ctx)
            );
            serverInstance.post(BITCOIN_SECTION + Command.WITHDRAW.getCommandKey().toLowerCase(),
                    ctx -> log(CLightningLevelLog.DEBUG, ctx.formParamMap().toString())
            );
        }

        private void setUtilityServices(Javalin serverInstance){
            serverInstance.get("/utility/" + Command.GETINFO.getCommandKey().toLowerCase(),
                    ctx -> UtilityServices.getInfo(ctx)
            );
        }
    }
}

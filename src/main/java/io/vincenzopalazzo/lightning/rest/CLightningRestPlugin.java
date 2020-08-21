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
import jrpc.clightning.plugins.annotation.Hook;
import jrpc.clightning.plugins.annotation.PluginOption;
import jrpc.clightning.plugins.annotation.RPCMethod;
import jrpc.clightning.plugins.annotation.Subscription;
import jrpc.clightning.plugins.log.CLightningLevelLog;
import jrpc.clightning.plugins.rpcmethods.AbstractRPCMethod;
import jrpc.service.converters.JsonConverter;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;
import jrpc.wrapper.response.ErrorResponse;

public class CLightningRestPlugin extends CLightningPlugin {

    private static final String BITCOIN_SECTION = "/bitcoin/";
    private static final String LIGHTNING_SECTION = "/lightning/";
    private static final String UTILITY_SECTION = "/utility/";

    @PluginOption(
            name = "jrest-port",
            defValue = "7000",
            typeValue = "int",
            description = "The port where the rest server will listen"
    )
    private int port = 7001;

    protected void readPort() {
        String portPar = getParameter("jrest-port");
        log(CLightningLevelLog.WARNING, portPar);
        if (portPar != null) {
            port = Integer.parseInt(portPar);
        }
    }

    @Override
    protected void registerMethod() {
        super.registerMethod();
        super.addRPCMethod(new CommandServerRestMethod("restserver", "[operation]", "This plugin help to introduce the rest server on C-lightning node."));
    }

    @Subscription(notification = "invoice_creation")
    public void doInvoiceCreation(CLightningJsonObject data) {
        log(CLightningLevelLog.WARNING, "Notification invoice_creation received inside the plugin lightning rest");
    }

    @Hook(hook = "rpc_command")
    public void logAllRPCCommand(CLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
        log(CLightningLevelLog.WARNING, request.toString());
        response.add("result", "continue");
    }

    public class CommandServerRestMethod extends AbstractRPCMethod {

        private Javalin serverInstance;
        private JsonConverter converter = new JsonConverter();

        public CommandServerRestMethod(String name, String usage, String description) {
            super(name, usage, description);
            serverInstance = buildServerInstance();
        }


        @Override
        public void doRun(ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
            plugin.log(CLightningLevelLog.DEBUG, "Request from server:\n" + request.toString());
            if (request != null) {
                JsonArray params = request.get("params").getAsJsonArray();
                String operation = params.get(0).getAsString();

                if(serverInstance == null) serverInstance = buildServerInstance();

                if (operation.equalsIgnoreCase("start")) {
                    readPort();
                    log(CLightningLevelLog.WARNING, "Server on port: " + port);
                    serverInstance.start(port);

                    setBitcoinServices(serverInstance);
                    setUtilityServices(serverInstance);
                    setLightningServices(serverInstance);

                    response.add("status", "running");
                    response.add("port", serverInstance.port());
                } else if (operation.equalsIgnoreCase("stop")) {
                    serverInstance.stop();
                    response.add("status", "shutdown");
                    response.add("port", serverInstance.port());
                    serverInstance = null;
                }
            }
        }

        private Javalin buildServerInstance(){
            Info info = new Info().version("0.1").description("C-lightning REST API");
            OpenApiOptions options = new OpenApiOptions(info)
                    .activateAnnotationScanningFor("io.vincenzopalazzo.lightning.rest.services")
                    .path("/jrest-docs") // endpoint for OpenAPI json
                    .swagger(new SwaggerOptions("/jrest-ui")) // endpoint for swagger-ui
                    .reDoc(new ReDocOptions("/redoc")) // endpoint for redoc
                    .defaultDocumentation(doc -> {
                        doc.json("500", ErrorResponse.class);
                        doc.json("503", ErrorResponse.class);
                    });
            return Javalin.create(config -> {
                config.registerPlugin(new OpenApiPlugin(options));
                config.defaultContentType = "application/json";
            });
        }

        private void setLightningServices(Javalin serverInstance) {
            serverInstance.get(LIGHTNING_SECTION + Command.LISTINVOICE.getCommandKey(), ctx ->
                    ctx.result(converter.serialization(CLightningRPC.getInstance().getListInvoices()
                    )));
            serverInstance.get(LIGHTNING_SECTION + Command.DECODEPAY.getCommandKey(), ctx ->
                    ctx.result(converter.serialization(CLightningRPC.getInstance().decodePay(
                            ctx.queryParam("bolt11")
                    ))));
        }

        private void setBitcoinServices(Javalin serverInstance) {
            serverInstance.get(BITCOIN_SECTION + Command.NEWADDR.getCommandKey().toLowerCase(),
                    ctx -> BitcoinServices.newAddr(ctx)
            );
            serverInstance.post(BITCOIN_SECTION + Command.WITHDRAW.getCommandKey().toLowerCase(),
                    ctx -> log(CLightningLevelLog.DEBUG, ctx.formParamMap().toString())
            );
        }

        private void setUtilityServices(Javalin serverInstance) {
            serverInstance.get(UTILITY_SECTION + Command.GETINFO.getCommandKey().toLowerCase(),
                    ctx -> UtilityServices.getInfo(ctx)
            );
        }
    }
}

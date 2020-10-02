package io.vincenzopalazzo.lightning.rest;

import com.google.gson.JsonArray;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import io.vincenzopalazzo.lightning.rest.services.BitcoinServices;
import io.vincenzopalazzo.lightning.rest.services.PaymentService;
import io.vincenzopalazzo.lightning.rest.services.UtilityServices;
import jrpc.clightning.annotation.Hook;
import jrpc.clightning.annotation.PluginOption;
import jrpc.clightning.annotation.Subscription;
import jrpc.clightning.commands.Command;
import jrpc.clightning.plugins.CLightningPlugin;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.PluginLog;
import jrpc.clightning.plugins.rpcmethods.AbstractRPCMethod;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;
import jrpc.wrapper.response.ErrorResponse;

public class CLightningRestPlugin extends CLightningPlugin {

    private static final String BITCOIN_SECTION = "/bitcoin";
    private static final String PAYMENT_SECTION = "/payment";
    private static final String UTILITY_SECTION = "/utility";
    protected boolean testMode = false;

    @PluginOption(
            name = "jrest-port",
            defValue = "7000",
            typeValue = "int",
            description = "The port where the rest server will listen"
    )
    private int port = 7001;

    protected void readPort() {
        if(testMode) return;
        Object portPar = getParameter("jrest-port");
        logRestPlugin(PluginLog.WARNING, String.valueOf(portPar));
        if (portPar != null) {
            if (portPar instanceof String){
                //With allow-deprecated-api
                port = Integer.parseInt((String)portPar);
            }else{
                port = (Integer) portPar;
            }
        }
    }


    @Override
    protected void registerMethod() {
        super.registerMethod();
        super.addRPCMethod(new CommandServerRestMethod("restserver", "[operation]", "This plugin help to introduce the rest server on C-lightning node."));
    }

    @Subscription(notification = "invoice_creation")
    public void doInvoiceCreation(CLightningJsonObject data) {
        logRestPlugin(PluginLog.WARNING, "Notification invoice_creation received inside the plugin lightning rest");
    }

    @Hook(hook = "rpc_command")
    public void logAllRPCCommand(CLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
       logRestPlugin(PluginLog.WARNING, request.toString());
        response.add("result", "continue");
    }

    public void testModeOne() {
        this.registerMethod();
        testMode = true;
        getRpcMethods().forEach(it -> {
            if(it.getName().equals("restserver")){
                CLightningJsonObject request = new CLightningJsonObject();
                request.add("id", 1);
                request.add("jsonrpc", "2.0");
                request.add("jsonrpc", "2.0");
                JsonArray params = new JsonArray();
                params.add("start");
                request.add("params", params);
                it.doRun(this, request, new CLightningJsonObject());
            }
        });
    }

    public void testModeOff() {
        getRpcMethods().forEach(it -> {
            if(it.getName().equals("restserver")){
                CLightningJsonObject request = new CLightningJsonObject();
                request.add("id", 1);
                request.add("jsonrpc", "2.0");
                JsonArray params = new JsonArray();
                params.add("stop");
                request.add("params", params);
                it.doRun(this, request, new CLightningJsonObject());
            }
        });
        testMode = false;
    }

    protected void logRestPlugin(PluginLog level, String message){
        if(!testMode){
            log(PluginLog.WARNING, message);
        }
    }


    public class CommandServerRestMethod extends AbstractRPCMethod {

        private Javalin serverInstance;

        public CommandServerRestMethod(String name, String usage, String description) {
            super(name, usage, description);
            serverInstance = buildServerInstance();
        }


        @Override
        public void doRun(ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
            logRestPlugin(PluginLog.DEBUG, "Request from server:\n" + request.toString());
            if (request != null) {
                JsonArray params = request.get("params").getAsJsonArray();
                String operation = params.get(0).getAsString();

                if (serverInstance == null) serverInstance = buildServerInstance();

                if (operation.equalsIgnoreCase("start")) {
                    readPort();
                    logRestPlugin(PluginLog.WARNING, "Server on port: " + port);
                    serverInstance.start(port);

                    setBitcoinServices(serverInstance);
                    setUtilityServices(serverInstance);
                    setPaymentServices(serverInstance);

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

        private Javalin buildServerInstance() {
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

        private void setPaymentServices(Javalin serverInstance) {
            String url = String.format("%s/%s", PAYMENT_SECTION, Command.LISTINVOICE.getCommandKey());
            serverInstance.get(url, PaymentService::listInvoice);

            url = String.format("%s/%s", PAYMENT_SECTION, Command.DECODEPAY.getCommandKey());
            serverInstance.post(url, PaymentService::decodePay);

            url = String.format("%s/%s", PAYMENT_SECTION, Command.DELINVOICE.getCommandKey());
            serverInstance.delete(url, PaymentService::delInvoice);

            url = String.format("%s/%s", PAYMENT_SECTION, Command.INVOICE.getCommandKey());
            serverInstance.post(url, PaymentService::invoice);
        }

        private void setBitcoinServices(Javalin serverInstance) {
            String url = String.format("%s/%s", BITCOIN_SECTION, Command.NEWADDR.getCommandKey());
            serverInstance.get(url, BitcoinServices::newAddr);

            url = String.format("%s/%s", BITCOIN_SECTION, Command.WITHDRAW.getCommandKey());
            serverInstance.post(url, BitcoinServices::withdraw);
        }

        private void setUtilityServices(Javalin serverInstance) {
            String url = String.format("%s/%s", UTILITY_SECTION, Command.GETINFO.getCommandKey());
            serverInstance.get(url, UtilityServices::getInfo);
        }
    }
}

package io.vincenzopalazzo.lightning.rest;

import com.google.gson.JsonArray;
import io.javalin.Javalin;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.plugins.CLightningPlugin;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.CLightningLevelLog;
import jrpc.clightning.plugins.rpcmethods.RPCMethod;
import jrpc.service.converters.JsonConverter;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;

public class CLightningRestPlugin extends CLightningPlugin {

    @Override
    protected void registerMethod() {
        super.registerMethod();
        super.addRPCMethod(new CommandServerRestMethod("restserver", "[operation]", "This plugin help to introduce the rest server on C-lightning node."));
    }

    public class CommandServerRestMethod extends RPCMethod {

        private Javalin serverInstance;

        public CommandServerRestMethod(String name, String usage, String description) {
            super(name, usage, description);
            serverInstance = Javalin.create();
        }

        @Override
        public void doRun(ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
            plugin.log(CLightningLevelLog.DEBUG, "Request from server:\n" + request.toString());
            if (request != null) {
                JsonArray params = request.get("params").getAsJsonArray();
                String operation = params.get(0).getAsString();

                if (operation.equalsIgnoreCase("start")) {
                    if (serverInstance != null) {
                        JsonConverter converter = new JsonConverter();
                        serverInstance.start(7001);
                        serverInstance.get("/getinfo", ctx -> ctx.result(converter.serialization(CLightningRPC.getInstance().getInfo())));
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
                        serverInstance = Javalin.create();
                    }
                }
            }

        }
    }
}

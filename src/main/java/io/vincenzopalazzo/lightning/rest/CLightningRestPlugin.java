package io.vincenzopalazzo.lightning.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.vincenzopalazzo.lightning.rest.utils.ServerUtils;
import jrpc.clightning.annotation.Hook;
import jrpc.clightning.annotation.PluginOption;
import jrpc.clightning.annotation.RPCMethod;
import jrpc.clightning.annotation.Subscription;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.plugins.CLightningPlugin;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.log.PluginLog;
import jrpc.clightning.plugins.rpcmethods.AbstractRPCMethod;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;

public class CLightningRestPlugin extends CLightningPlugin {

    protected boolean testMode = false;

    @PluginOption(
            name = "jrest-port",
            defValue = "7000",
            typeValue = "int",
            description = "The port where the rest server will listen"
    )
    private int port;

    private Javalin serverInstance;

    @RPCMethod(
            name = "restserver",
            parameter = "[operation]",
            description = "This plugin help to introduce the rest server on C-lightning node."
    )
    public void runRestServer(ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
        if (serverInstance == null)
            serverInstance = ServerUtils.buildServerInstance();
        plugin.log(PluginLog.DEBUG, "Request from server:\n" + request.toString());
        JsonArray params = request.get("params").getAsJsonArray();
        String operation = params.get(0).getAsString();
        if (operation.equalsIgnoreCase("start")) {
            plugin.log(PluginLog.WARNING, "Server on port: " + port);
            if(!serverInstance.server().getStarted())
                serverInstance.start(port);
            response.add("status", "running");
            response.add("port", serverInstance.port());
        } else if (operation.equalsIgnoreCase("stop")) {
            if(serverInstance.server().getStarted())
                serverInstance.stop();
            response.add("status", "shutdown");
            response.add("port", serverInstance.port());
            serverInstance = null;
        }
    }

    @Subscription(notification = "invoice_creation")
    public void doInvoiceCreation(CLightningJsonObject data) {
        this.log(PluginLog.DEBUG, "Notification invoice_creation received inside the plugin lightning rest");
    }

    @Hook(hook = "rpc_command")
    public void interceptorRPCCommands(CLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
        JsonObject params = request.get("params").getAsJsonObject();
        if (params.has("rpc_command")) {
            JsonObject rpcMethod = params.get("rpc_command").getAsJsonObject();
            if (rpcMethod.get("method").getAsString().equals("stop") && this.serverInstance != null) {
                this.serverInstance.stop();
                plugin.log(PluginLog.INFO, "Stopping Server Instance");
            }
        }
        response.add("result", "continue");
    }


    public void testModeOne() {
        this.port = 7010;
        this.addRPCMethod(new AbstractRPCMethod("restserver", "[operation]", "only for test") {
            @Override
            public void doRun(ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) throws CLightningException {
                runRestServer(plugin, request, response);
            }
        });
        this.registerMethod();
        testMode = true;
        getRpcMethods().forEach(it -> {
            if(it.getName().equals("restserver")){
                CLightningJsonObject request = new CLightningJsonObject();
                request.add("id", 1);
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
}

package io.vincenzopalazzo.lightning.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.vincenzopalazzo.lightning.rest.utils.ServerUtils;
import jrpc.clightning.annotation.Hook;
import jrpc.clightning.annotation.PluginOption;
import jrpc.clightning.annotation.RPCMethod;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.plugins.CLightningPlugin;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.exceptions.CLightningPluginException;
import jrpc.clightning.plugins.log.PluginLog;
import jrpc.clightning.plugins.rpcmethods.AbstractRPCMethod;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CLightningRestPlugin extends CLightningPlugin {

  protected boolean testMode = false;

  @PluginOption(
      name = "jrest-port",
      defValue = "7000",
      typeValue = "int",
      description = "The port where the rest server will listen")
  private int port;

  @PluginOption(
      name = "jrest-on-startup",
      defValue = "false",
      typeValue = "flag",
      description = "Startup the rest server when the node call the method init.")
  private boolean onStartup;

  private Boolean onStartupCalled;

  private Javalin serverInstance;

  public CLightningRestPlugin() {
    // The boolean here have three value, one of these is null
    // is mean that onStartup is not specified
    this.onStartupCalled = null;
  }

  @Override
  public void onInit(
      ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
    super.onInit(plugin, request, response);
    if (onStartup) {
      this.onStartupCalled = false;
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
                @Override
                public void run() {
                  serverInstance = ServerUtils.buildServerInstance(plugin);
                  serverInstance.start();
                  onStartupCalled = true;
                  timer.cancel();
                }
              },
              2000);
    }
  }

  @RPCMethod(
      name = "restserver",
      parameter = "[operation]",
      description = "This plugin help to introduce the rest server on C-lightning node.")
  public void runRestServer(
      ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
    if (onStartupCalled != null && !onStartupCalled) {
        response.add("message", "Waiting first initialization by c-lightning");
    } else {
      if (serverInstance == null) serverInstance = ServerUtils.buildServerInstance(plugin);
      if (!request.get("params").isJsonArray())
        throw new CLightningPluginException(-1, "Params is not a JSON array");
      JsonArray params = request.get("params").getAsJsonArray();
      if (params.isEmpty()) {
        response.add("error", "JSON array is empty we need to know the operation, start or stop");
        return;
      }
      String operation = params.get(0).getAsString();
      switch (operation) {
        case "start":
          {
            plugin.log(PluginLog.INFO, "Server on port: " + port);
            if (!Objects.requireNonNull(serverInstance.server()).getStarted()) {
              serverInstance.start(port);
              response.add("status", "running");
              response.add("port", serverInstance.port());
            } else {
              response.add("message", "Rest server already running");
            }
            break;
          }
        case "stop":
          {
            if (Objects.requireNonNull(serverInstance.server()).getStarted()) {
              serverInstance.stop();
              response.add("status", "shutdown");
              response.add("port", serverInstance.port());
              serverInstance = null;
            } else {
              response.add("message", "Rest server is already stopped");
            }
            break;
          }
        default:
          throw new CLightningPluginException(
              -1, String.format("Command %s not found: ", operation));
      }
    }
  }

  @Hook(hook = "rpc_command")
  public void interceptorRPCCommands(
      CLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response) {
    if (!request.get("params").isJsonObject()) {
      plugin.log(PluginLog.ERROR, "rpc_command object need to be a JSON Object, we receive the following response \n" + request.getAsString());
      response.add("result", "continue");
      return;
    }
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
    this.addRPCMethod(
        new AbstractRPCMethod("restserver", "[operation]", "only for test") {
          @Override
          public void doRun(
              ICLightningPlugin plugin, CLightningJsonObject request, CLightningJsonObject response)
              throws CLightningException {
            runRestServer(plugin, request, response);
          }
        });
    this.registerMethod();
    testMode = true;
    getRpcMethods()
        .forEach(
            it -> {
              if (it.getName().equals("restserver")) {
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
    getRpcMethods()
        .forEach(
            it -> {
              if (it.getName().equals("restserver")) {
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

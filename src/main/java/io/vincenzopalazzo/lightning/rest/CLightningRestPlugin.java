package io.vincenzopalazzo.lightning.rest;

import com.google.gson.JsonArray;
import io.javalin.Javalin;
import io.vincenzopalazzo.lightning.rest.utils.ServerUtils;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import jrpc.clightning.annotation.PluginOption;
import jrpc.clightning.annotation.RPCMethod;
import jrpc.clightning.annotation.Subscription;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.plugins.CLightningPlugin;
import jrpc.clightning.plugins.ICLightningPlugin;
import jrpc.clightning.plugins.exceptions.CLightningPluginException;
import jrpc.clightning.plugins.log.PluginLog;
import jrpc.clightning.plugins.rpcmethods.AbstractRPCMethod;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;

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
    if (onStartup && serverInstance == null) {
      this.onStartupCalled = false;
      Timer timer = new Timer();
      timer.schedule(
          new TimerTask() {
            @Override
            public void run() {
              int retryMax = 4;
              int retryTime = 0;
              while (retryTime < retryMax && !onStartupCalled) {
                try {
                  serverInstance = ServerUtils.buildServerInstance(plugin);
                  serverInstance.start(port);
                  onStartupCalled = true;
                } catch (Exception exception) {
                  plugin.log(
                      PluginLog.ERROR,
                      String.format(
                          "Error during the init phase of jrest, retry time %d over %d",
                          retryTime, retryMax));
                  try {
                    Thread.sleep(3000);
                  } catch (InterruptedException e) {
                    plugin.log(
                        PluginLog.ERROR,
                        String.format(
                            "Java thread exception with message: %s",
                            exception.getLocalizedMessage()));
                  }
                  retryTime++;
                }
              }
              plugin.log(PluginLog.INFO, "jrest: init completed");
              timer.cancel();
            }
          },
          2000);
      plugin.log(
          PluginLog.INFO, "Auto init jrest init function calla after a delay of 2000 millisecond");
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
      if (serverInstance == null)
        serverInstance = ServerUtils.buildServerInstance(plugin);
      JsonArray params = request.get("params").getAsJsonArray();
      if (params.isEmpty()) {
        response.add("error", "JSON array is empty we need to know the operation, start or stop");
        return;
      }
      String operation = params.get(0).getAsString();
      plugin.log(PluginLog.INFO, "Operation from rpc method " + operation);
      switch (operation) {
        case "start":
          {
            plugin.log(PluginLog.INFO, "Server on port: " + port);
            if (!Objects.requireNonNull(serverInstance.jettyServer()).started) {
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
            if (Objects.requireNonNull(serverInstance.jettyServer()).started) {
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

  @Subscription(notification = "shutdown")
  public void shutdown(CLightningJsonObject data) {
    if (this.serverInstance != null) this.serverInstance.stop();
    System.exit(0);
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

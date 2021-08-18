package io.vincenzopalazzo.lightning.rest.utils;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import io.vincenzopalazzo.lightning.rest.services.*;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.commands.Command;
import jrpc.clightning.model.CLightningHelp;
import jrpc.clightning.model.CLightningListConfigs;
import jrpc.wrapper.response.ErrorResponse;

public class ServerUtils {

  private static final String BITCOIN_SECTION = "/bitcoin";
  private static final String PAYMENT_SECTION = "/payment";
  private static final String UTILITY_SECTION = "/utility";
  private static final String NETWORK_SECTION = "/network";
  private static final String CHANNEL_SECTION = "/channel";
  private static final String PLUGIN_SECTION = "/plugin";

  public static Javalin buildServerInstance() {
    Info info = new Info().version("0.1").description("C-lightning REST API");
    OpenApiOptions options =
        new OpenApiOptions(info)
            .activateAnnotationScanningFor("io.vincenzopalazzo.lightning.rest.services")
            .path("/docs") // endpoint for OpenAPI json
            .swagger(new SwaggerOptions("/ui")) // endpoint for swagger-ui
            .reDoc(new ReDocOptions("/redoc")) // endpoint for redoc
            .defaultDocumentation(
                doc -> {
                  doc.json("500", ErrorResponse.class);
                  doc.json("503", ErrorResponse.class);
                });
    Javalin serverInstance =
        Javalin.create(
            config -> {
              config.registerPlugin(new OpenApiPlugin(options));
              config.defaultContentType = "application/json";
            });

    setBitcoinServices(serverInstance);
    setUtilityServices(serverInstance);
    setPaymentServices(serverInstance);
    setNetworkServices(serverInstance);
    setChannelServices(serverInstance);
    setPluginServices(serverInstance);
    return serverInstance;
  }

  private static void setPaymentServices(Javalin serverInstance) {
    String url = String.format("%s/%s", PAYMENT_SECTION, Command.LISTINVOICE.getCommandKey());
    serverInstance.get(url, PaymentService::listInvoice);
    serverInstance.post(url, PaymentService::listInvoiceFiltered);

    url = String.format("%s/%s", PAYMENT_SECTION, Command.DECODEPAY.getCommandKey());
    serverInstance.post(url, PaymentService::decodePay);

    url = String.format("%s/%s/:label", PAYMENT_SECTION, Command.DELINVOICE.getCommandKey());
    serverInstance.delete(url, PaymentService::delInvoice);

    url = String.format("%s/%s", PAYMENT_SECTION, Command.INVOICE.getCommandKey());
    serverInstance.post(url, PaymentService::invoice);
  }

  private static void setBitcoinServices(Javalin serverInstance) {
    String url = String.format("%s/%s/:type", BITCOIN_SECTION, Command.NEWADDR.getCommandKey());
    serverInstance.get(url, BitcoinServices::newAddr);

    url = String.format("%s/%s", BITCOIN_SECTION, Command.WITHDRAW.getCommandKey());
    serverInstance.post(url, BitcoinServices::withdraw);
  }

  private static void setUtilityServices(Javalin serverInstance) {
    String url = String.format("%s/%s", UTILITY_SECTION, Command.GETINFO.getCommandKey());
    serverInstance.get(url, UtilityServices::getInfo);

    url = String.format("%s/%s", UTILITY_SECTION, Command.LISTFOUNDS.getCommandKey());
    serverInstance.get(url, UtilityServices::listFunds);
  }

  private static void setNetworkServices(Javalin servicesInstance) {
    String url = String.format("%s/%s/:nodeId", NETWORK_SECTION, Command.PING.getCommandKey());
    servicesInstance.get(url, NetworkServices::ping);

    url = String.format("%s/%s", NETWORK_SECTION, Command.LISTNODES.getCommandKey());
    servicesInstance.get(url, NetworkServices::listNodes);

    url = String.format("%s/%s", NETWORK_SECTION, Command.LISTNODES.getCommandKey());
    servicesInstance.post(url, NetworkServices::listNodesWithId);
  }

  private static void setChannelServices(Javalin serverInstance) {
    String url = String.format("%s/%s", CHANNEL_SECTION, Command.LISTCHANNELS.getCommandKey());
    serverInstance.get(url, ChannelServices::listChannels);
  }

  private static void setPluginServices(Javalin serverInstance) {
    CLightningHelp help = CLightningRPC.getInstance().help();
    help.getHelpItems().parallelStream()
        .forEach(
            info -> {
              if (info.getCommand().contains("diagnostic")) {
                String url = String.format("%s/%s", PLUGIN_SECTION, "diagnostic");
                serverInstance.get(url, PluginServices::diagnostic);
              }
            });
  }
}

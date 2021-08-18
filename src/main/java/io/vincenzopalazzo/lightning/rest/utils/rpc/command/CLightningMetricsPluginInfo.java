package io.vincenzopalazzo.lightning.rest.utils.rpc.command;

import com.google.gson.reflect.TypeToken;
import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.MetricsPluginInfo;
import jrpc.clightning.annotation.RPCCommand;
import jrpc.clightning.commands.AbstractRPCCommand;
import jrpc.wrapper.response.RPCResponseWrapper;

import java.lang.reflect.Type;

@RPCCommand(name = "lnmetrics-reporter")
public class CLightningMetricsPluginInfo extends AbstractRPCCommand<MetricsPluginInfo> {
  private static final String COMMAND_NAME = "lnmetrics-reporter";

    public CLightningMetricsPluginInfo() {
        super(COMMAND_NAME);
    }

    @Override
    protected Type toTypeFromClass() {
        return new TypeToken<RPCResponseWrapper<MetricsPluginInfo>>() {}.getType();
    }
}

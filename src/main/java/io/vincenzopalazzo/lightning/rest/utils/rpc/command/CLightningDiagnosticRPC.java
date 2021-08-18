package io.vincenzopalazzo.lightning.rest.utils.rpc.command;

import com.google.gson.reflect.TypeToken;
import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import jrpc.clightning.annotation.RPCCommand;
import jrpc.clightning.commands.AbstractRPCCommand;
import jrpc.wrapper.response.RPCResponseWrapper;

import java.lang.reflect.Type;

@RPCCommand(name = "diagnostic")
public class CLightningDiagnosticRPC extends AbstractRPCCommand<CLightningDiagnostic> {
    private static final String COMMAND_NAME = "diagnostic";

    public CLightningDiagnosticRPC() {
        super(COMMAND_NAME);
    }

    @Override
    protected Type toTypeFromClass() {
        return new TypeToken<RPCResponseWrapper<CLightningDiagnostic>>() {}.getType();
    }
}

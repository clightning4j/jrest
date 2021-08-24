package io.vincenzopalazzo.lightning.rest.utils.rpc.command;

import com.google.gson.reflect.TypeToken;
import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.utils.rpc.CLightningCommand;
import jrpc.clightning.commands.AbstractRPCCommand;
import jrpc.wrapper.response.RPCResponseWrapper;

import java.lang.reflect.Type;

public class CLightningDiagnosticRPC extends AbstractRPCCommand<CLightningDiagnostic> {

    public CLightningDiagnosticRPC() {
        super(CLightningCommand.DIAGNOSTIC.getCommandKey());
    }

    @Override
    protected Type toTypeFromClass() {
        return new TypeToken<RPCResponseWrapper<CLightningDiagnostic>>() {}.getType();
    }
}

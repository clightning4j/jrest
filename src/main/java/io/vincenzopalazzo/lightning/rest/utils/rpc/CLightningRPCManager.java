package io.vincenzopalazzo.lightning.rest.utils.rpc;

import io.vincenzopalazzo.lightning.rest.utils.rpc.command.CLightningDiagnosticRPC;
import jrpc.clightning.CLightningRPC;

public class CLightningRPCManager {

    public static void registerMethods() {
        CLightningRPCManager.registerMetricsPluginMethods();
    }

    private static void registerMetricsPluginMethods() {
        //TODO it can be register by annotation?
        CLightningRPC.getInstance().registerCommand(CLightningCommand.DIAGNOSTIC, new CLightningDiagnosticRPC());
    }
}

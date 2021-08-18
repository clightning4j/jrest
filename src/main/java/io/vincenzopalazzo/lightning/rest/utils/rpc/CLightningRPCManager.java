package io.vincenzopalazzo.lightning.rest.utils.rpc;

import jrpc.clightning.CLightningRPC;

public class CLightningRPCManager {

    public static void registerMethods() { }

    private static void registerMetricsPluginMethods() {
        //TODO it is register by annotation?
        // CLightningRPC.getInstance().registerCommand(CLightningCommand.DIAGNOSTIC, );
    }
}

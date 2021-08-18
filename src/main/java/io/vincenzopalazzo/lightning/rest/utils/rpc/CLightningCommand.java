package io.vincenzopalazzo.lightning.rest.utils.rpc;

import jrpc.clightning.commands.ICommandKey;

public enum CLightningCommand implements ICommandKey {
    DIAGNOSTIC("diagnostic");

    private String commandKey;

    CLightningCommand(String commandKey) {
        this.commandKey = commandKey;
    }

    @Override
    public String getCommandKey() {
        return commandKey;
    }
}

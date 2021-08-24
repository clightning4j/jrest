package io.vincenzopalazzo.lightning.rest.utils.rpc;

import jrpc.clightning.commands.ICommandKey;

import java.util.Locale;

public enum CLightningCommand implements ICommandKey {
    DIAGNOSTIC("diagnostic");

    private String commandKey;

    CLightningCommand(String commandKey) {
        this.commandKey = commandKey.toLowerCase(Locale.ROOT);
    }

    @Override
    public String getCommandKey() {
        return commandKey;
    }
}

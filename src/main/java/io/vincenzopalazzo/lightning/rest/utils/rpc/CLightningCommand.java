package io.vincenzopalazzo.lightning.rest.utils.rpc;

import java.util.Locale;
import jrpc.clightning.commands.ICommandKey;

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

package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.Expose;

public class ClightningSignMessage {
  @Expose private String signature;
  @Expose private String recid;
  @Expose private String zbase;

  public String getSignature() {
    return signature;
  }

  public String getRecid() {
    return recid;
  }

  public String getZbase() {
    return zbase;
  }
}

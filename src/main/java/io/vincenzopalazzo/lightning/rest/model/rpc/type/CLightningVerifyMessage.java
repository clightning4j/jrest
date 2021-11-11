package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CLightningVerifyMessage {
  @Expose private Boolean verified;

  @Expose
  @SerializedName("pubkey")
  private String pubKey;

  public Boolean getVerified() {
    return verified;
  }

  public String getPubKey() {
    return pubKey;
  }
}

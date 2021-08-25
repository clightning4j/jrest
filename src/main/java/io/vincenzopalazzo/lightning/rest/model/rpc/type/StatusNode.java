package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.math.BigInteger;

public class StatusNode {

  @Expose
  @SerializedName("channels")
  private ChannelsInfo channels;

  @Expose private String event;
  @Expose private PaymentsInfo forwards;
  @Expose private Long timestamp;

  public ChannelsInfo getChannels() {
    return channels;
  }

  public String getEvent() {
    return event;
  }

  public PaymentsInfo getForwards() {
    return forwards;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public static class ChannelsInfo {
    @SerializedName("tot_channels")
    private Long totChannels;

    private ChannelSummary summary;

    public ChannelSummary getSummary() {
      return summary;
    }

    public static class ChannelSummary {
      @SerializedName("node_id")
      private String nodeId;

      private String alias;
      private String color;

      @SerializedName("channel_id")
      private String channelId;

      private String state;

      public String getNodeId() {
        return nodeId;
      }

      public String getAlias() {
        return alias;
      }

      public String getColor() {
        return color;
      }

      public String getChannelId() {
        return channelId;
      }

      public String getState() {
        return state;
      }
    }
  }

  public static class PaymentsInfo {
    private BigInteger completed;
    private BigInteger failed;

    public BigInteger getCompleted() {
      return completed;
    }

    public BigInteger getFailed() {
      return failed;
    }
  }
}

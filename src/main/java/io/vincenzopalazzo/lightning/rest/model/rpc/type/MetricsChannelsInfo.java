package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.List;

public class MetricsChannelsInfo {
    @SerializedName("channel_id")
    private String channelId;
    @SerializedName("node_alias")
    private String nodeAlias;
    @SerializedName("node_id")
    private String nodeId;
    private String color;
    private String direction;
    private BigInteger capacity;
    @SerializedName("last_update")
    private BigInteger lastUpdate;
    private List<PaymentInfo> forwards;
    private Boolean online;
    @SerializedName("public")
    private Boolean publicChannel;
    @SerializedName("up_times")
    private List<UptimeChannel> upTimes;

    public String getChannelId() {
        return channelId;
    }

    public String getNodeAlias() {
        return nodeAlias;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getColor() {
        return color;
    }

    public String getDirection() {
        return direction;
    }

    public BigInteger getCapacity() {
        return capacity;
    }

    public BigInteger getLastUpdate() {
        return lastUpdate;
    }

    public List<PaymentInfo> getForwards() {
        return forwards;
    }

    public Boolean getOnline() {
        return online;
    }

    public Boolean getPublicChannel() {
        return publicChannel;
    }

    public List<UptimeChannel> getUpTimes() {
        return upTimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricsChannelsInfo)) return false;
        MetricsChannelsInfo that = (MetricsChannelsInfo) o;
        return channelId.equals(that.channelId);
    }

    @Override
    public int hashCode() {
        return channelId.hashCode();
    }

    public static class UptimeChannel {
        private String status;
        private BigInteger timestamp;

        public String getStatus() {
            return status;
        }

        public BigInteger getTimestamp() {
            return timestamp;
        }
    }

    public static class PaymentInfo {
        private String direction;
        @SerializedName("failure_code")
        private Integer failureCode;
        @SerializedName("failure_reason")
        private String failureReason;
        private String status;

        public String getDirection() {
            return direction;
        }

        public Integer getFailureCode() {
            return failureCode;
        }

        public String getFailureReason() {
            return failureReason;
        }

        public String getStatus() {
            return status;
        }
    }
}

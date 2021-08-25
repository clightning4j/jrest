package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MetricsChannelsInfo {
    @Expose
    @SerializedName("channel_id")
    private String channelId;
    @Expose
    @SerializedName("node_alias")
    private String nodeAlias;
    @Expose
    @SerializedName("node_id")
    private String nodeId;
    @Expose
    private String color;
    @Expose
    private String direction;
    @Expose
    private Long capacity;
    @Expose
    @SerializedName("last_update")
    private Long lastUpdate;
    @Expose
    private List<PaymentInfo> forwards;
    @Expose
    private Boolean online;
    @Expose
    @SerializedName("public")
    private Boolean publicChannel;
    @Expose
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

    public Long getCapacity() {
        return capacity;
    }

    public Long getLastUpdate() {
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
        @Expose
        private String status;
        @Expose
        private Long timestamp;

        public String getStatus() {
            return status;
        }

        public Long getTimestamp() {
            return timestamp;
        }
    }

    public static class PaymentInfo {
        @Expose
        private String direction;
        @Expose
        @SerializedName("failure_code")
        private Integer failureCode;
        @Expose
        @SerializedName("failure_reason")
        private String failureReason;
        @Expose
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

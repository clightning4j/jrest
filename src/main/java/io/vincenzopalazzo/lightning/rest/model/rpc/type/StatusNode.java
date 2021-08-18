package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.Date;

public class StatusNode {

    @SerializedName("channels")
    private ChannelsInfo channels;
    private String event;
    private PaymentsInfo forwards;
    private Date timestamp;

    public ChannelsInfo getChannels() {
        return channels;
    }

    public String getEvent() {
        return event;
    }

    public PaymentsInfo getForwards() {
        return forwards;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public static class ChannelsInfo {
        private ChannelSummary summary;

        public static class ChannelSummary {

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

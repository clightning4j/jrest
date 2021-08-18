package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.SerializedName;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.MetricsChannelsInfo;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.MetricsOsInfo;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.StatusNode;

import java.util.List;
import java.util.Set;

public class CLightningMetricOne {
    @SerializedName("metric_name")
    private String metricName;
    private String color;
    @SerializedName("node_id")
    private String nodeId;
    @SerializedName("os_info")
    private MetricsOsInfo osInfo;
    private String timezone;
    @SerializedName("up_time")
    private List<StatusNode> upTime;
    @SerializedName("channels_info")
    private Set<MetricsChannelsInfo> channelsInfo;

    public String getMetricName() {
        return metricName;
    }

    public String getColor() {
        return color;
    }

    public String getNodeId() {
        return nodeId;
    }

    public MetricsOsInfo getOsInfo() {
        return osInfo;
    }

    public String getTimezone() {
        return timezone;
    }

    public List<StatusNode> getUpTime() {
        return upTime;
    }

    public Set<MetricsChannelsInfo> getChannelsInfo() {
        return channelsInfo;
    }
}

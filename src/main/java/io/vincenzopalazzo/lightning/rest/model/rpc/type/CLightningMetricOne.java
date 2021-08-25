package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Set;

public class CLightningMetricOne {
  @Expose
  @SerializedName("metric_name")
  private String metricName;

  @Expose private String color;

  @Expose
  @SerializedName("node_id")
  private String nodeId;

  @Expose
  @SerializedName("os_info")
  private MetricsOsInfo osInfo;

  @Expose private String timezone;

  @Expose
  @SerializedName("up_time")
  private List<StatusNode> upTime;

  @Expose
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

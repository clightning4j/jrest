package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.SerializedName;

public class MetricsPluginInfo {
    private String name;
    private String version;
    @SerializedName("lang_version")
    private String langVersion;
    private String architecture;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getLangVersion() {
        return langVersion;
    }

    public String getArchitecture() {
        return architecture;
    }
}

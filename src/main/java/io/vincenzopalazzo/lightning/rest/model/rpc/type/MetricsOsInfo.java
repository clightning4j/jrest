package io.vincenzopalazzo.lightning.rest.model.rpc.type;

import com.google.gson.annotations.Expose;

public class MetricsOsInfo {
    @Expose
    private String architecture;
    @Expose
    private String os;
    @Expose
    private String version;

    public String getArchitecture() {
        return architecture;
    }

    public String getOs() {
        return os;
    }

    public String getVersion() {
        return version;
    }
}

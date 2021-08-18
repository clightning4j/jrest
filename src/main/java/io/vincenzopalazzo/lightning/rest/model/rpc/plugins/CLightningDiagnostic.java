package io.vincenzopalazzo.lightning.rest.model.rpc.plugins;

import io.vincenzopalazzo.lightning.rest.model.rpc.type.CLightningMetricOne;

import java.util.HashMap;
import java.util.Map;

public class CLightningDiagnostic {
    private Map<String, Object> metrics;

    public CLightningDiagnostic() {
        metrics = new HashMap<>();
    }

    public CLightningMetricOne getMetricOne() {
        if (!metrics.containsKey("metric_one"))
            throw new IllegalArgumentException("No metric contained in the plugin response with key  metric_one");
        var value = metrics.get("metric_one");
        return (CLightningMetricOne) value;
    }

    public <T> T getMetricWithKey(String key) {
        if (!metrics.containsKey(key))
            throw new IllegalArgumentException("No metric contained in the plugin response with key " + key);
        var value = metrics.get(key);
        return (T) value;
    }
}

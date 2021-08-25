package io.vincenzopalazzo.lightning.rest.model.rpc.plugins;

import com.google.gson.annotations.Expose;
import jrpc.service.converters.JsonConverter;

import java.lang.reflect.Type;
import java.util.HashMap;

public class CLightningDiagnostic {

    @Expose
    private HashMap<String, Object> metrics;

    public HashMap<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(HashMap<String, Object> metrics) {
        this.metrics = metrics;
    }

    public <T> T getMetricWithImplementation(String key, Type implementation) {
        if (metrics == null)
            return null;
        if (!metrics.containsKey(key))
            throw new IllegalArgumentException("No metric contained in the plugin response with key " + key);
        JsonConverter jsonConverter = new JsonConverter();
        var jsonObject = metrics.get(key);
        String jsonString = jsonConverter.serialization(jsonObject);
        return (T) jsonConverter.deserialization(jsonString, implementation);
    }

    public boolean allMetrics() {
        return metrics != null;
    }

    public boolean singleMetrics() {
        return false;
    }
}

package io.vincenzopalazzo.lightning.rest.model.rpc.plugins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import jrpc.service.converters.JsonConverter;

import java.lang.reflect.Type;
import java.util.HashMap;

public class CLightningDiagnostic {
    @Expose
    private HashMap<String, JsonElement> metrics;

    public <T> T getMetricWithImplementation(String key, Type implementation) {
        if (metrics == null)
            return null;
        if (!metrics.containsKey(key))
            throw new IllegalArgumentException("No metric contained in the plugin response with key " + key);
        JsonConverter jsonConverter = new JsonConverter();
        var jsonObject = metrics.get(key);
        if (jsonObject == null || jsonObject.isJsonNull())
            throw new IllegalArgumentException("Value with key " + key + " null");
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

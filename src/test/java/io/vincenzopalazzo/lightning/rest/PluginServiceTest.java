package io.vincenzopalazzo.lightning.rest;

import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.CLightningMetricOne;
import io.vincenzopalazzo.lightning.rest.utils.rpc.CLightningCommand;
import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningGetInfo;
import jrpc.clightning.model.CLightningHelp;
import jrpc.service.converters.JsonConverter;
import junit.framework.TestCase;
import kong.unirest.Unirest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

public class PluginServiceTest extends AbstractServiceTest {

    private AtomicBoolean enable;

    @Before
    public void init() {
        enable = new AtomicBoolean(false);
        var help = CLightningRPC.getInstance().help();
        help.getHelpItems()
                .parallelStream()
                .forEach(it -> {
                    if (it.getCommand().contains("diagnostic")) {
                        enable.set(true);
                    }
                });
    }

    @Test
    public void GET_diagnostic() {
        assumeThat(enable.get(), is(true));
        try {
            TestCase.assertTrue(enable.get());
            var response = Unirest.get("/plugin/diagnostic")
                    .queryString("metrics_id", "1,")
                    .asString();
            var body = response.getBody();
            LOGGER.debug("GET_diagnostic response: " + body);
            LOGGER.debug("GET_diagnostic status: " + response.getStatusText());
            TestCase.assertTrue(response.isSuccess());
            JsonConverter converter = new JsonConverter();
            HashMap<String, Object> params = new HashMap<>();
            params.put("metrics_id", "1");
            CLightningDiagnostic expectedDiagnostic = CLightningRPC.getInstance().runRegisterCommand(CLightningCommand.DIAGNOSTIC, params);
            var expectedString = converter.serialization(expectedDiagnostic);
            TestCase.assertEquals(expectedString, body);
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }

    @Test
    public void GET_metricsOneFromMap() {
        assumeThat(enable.get(), is(true));
        try {
            var response = Unirest.get("/plugin/diagnostic"+"?metrics_id=1").asString();
            var body = response.getBody();
            LOGGER.debug("GET_diagnostic response: " + body);
            LOGGER.debug("GET_diagnostic status: " + response.getStatusText());
            TestCase.assertTrue(response.isSuccess());
            JsonConverter converter = new JsonConverter();
            TestCase.fail(body);
            CLightningDiagnostic diagnostic = (CLightningDiagnostic) converter.deserialization(body, CLightningDiagnostic.class);
            TestCase.assertFalse(diagnostic.singleMetrics());
            TestCase.assertTrue(diagnostic.allMetrics());
            CLightningMetricOne metricOne = diagnostic.getMetricWithImplementation("metric_one", CLightningMetricOne.class);
            TestCase.assertNotNull(metricOne);
            CLightningGetInfo getInfo = CLightningRPC.getInstance().getInfo();
            TestCase.assertEquals(getInfo.getId(), metricOne.getNodeId());
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }
}

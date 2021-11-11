package io.vincenzopalazzo.lightning.rest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.CLightningMetricOne;
import io.vincenzopalazzo.lightning.rest.utils.rpc.CLightningCommand;
import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningGetInfo;
import jrpc.service.converters.JsonConverter;
import junit.framework.TestCase;
import kong.unirest.Unirest;
import org.junit.Test;

public class PluginServiceTest extends AbstractServiceTest {

  private AtomicBoolean enable;

  @Override
  public void init() {
    super.init();
    enable = new AtomicBoolean(false);
    var help = rpc.help();
    help.getHelpItems().parallelStream()
        .forEach(
            it -> {
              if (it.getCommand().contains("diagnostic")) {
                enable.set(true);
              }
            });
  }

  @Test
  public void GET_diagnostic() {
    assumeThat(enable.get(), is(true));
    try {
      var response = Unirest.get("/plugin/diagnostic").queryString("metrics_id", "1").asString();
      var body = response.getBody();
      LOGGER.debug("GET_diagnostic response: " + body);
      LOGGER.debug("GET_diagnostic status: " + response.getStatusText());
      TestCase.assertTrue(response.isSuccess());
      JsonConverter converter = new JsonConverter();
      HashMap<String, Object> params = new HashMap<>();
      params.put("metrics_id", "1");
      CLightningDiagnostic expectedDiagnostic =
          rpc.runRegisterCommand(CLightningCommand.DIAGNOSTIC, params);
      var expectedString = converter.serialization(expectedDiagnostic);
      // The number in JSON are floating point, we need to make a cast to make sure that it is a
      // correct
      // type in the body. We use the JsonConvert of JRPCLightning that implement all these stuff
      // for us.
      CLightningDiagnostic restResponse =
          (CLightningDiagnostic) converter.deserialization(body, CLightningDiagnostic.class);
      TestCase.assertEquals(expectedString, converter.serialization(restResponse));
    } catch (CLightningException exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }

  @Test
  public void GET_metricsOneFromMap() {
    assumeThat(enable.get(), is(true));
    try {
      var response = Unirest.get("/plugin/diagnostic").queryString("metrics_id", "1").asString();
      var body = response.getBody();
      LOGGER.debug("GET_diagnostic response: " + body);
      LOGGER.debug("GET_diagnostic status: " + response.getStatusText());
      TestCase.assertTrue(response.isSuccess());
      JsonConverter converter = new JsonConverter();
      CLightningDiagnostic diagnostic =
          (CLightningDiagnostic) converter.deserialization(body, CLightningDiagnostic.class);
      TestCase.assertFalse(diagnostic.singleMetrics());
      TestCase.assertTrue(diagnostic.allMetrics());
      CLightningMetricOne metricOne =
          diagnostic.getMetricWithImplementation("metric_one", CLightningMetricOne.class);
      TestCase.assertNotNull(metricOne);
      CLightningGetInfo getInfo = rpc.getInfo();
      TestCase.assertEquals(getInfo.getId(), metricOne.getNodeId());
    } catch (CLightningException exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }
}

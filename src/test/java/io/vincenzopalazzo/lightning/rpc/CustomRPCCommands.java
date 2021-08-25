package io.vincenzopalazzo.lightning.rpc;

import io.vincenzopalazzo.lightning.rest.model.rpc.plugins.CLightningDiagnostic;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.CLightningMetricOne;
import io.vincenzopalazzo.lightning.rest.utils.rpc.CLightningCommand;
import io.vincenzopalazzo.lightning.rest.utils.rpc.command.CLightningDiagnosticRPC;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import jrpc.clightning.CLightningRPC;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

public class CustomRPCCommands {

  private AtomicBoolean enable;

  @Before
  public void init() {
    enable = new AtomicBoolean(false);
    var help = CLightningRPC.getInstance().help();
    help.getHelpItems().parallelStream()
            .forEach(
                    it -> {
                      if (it.getCommand().contains("diagnostic")) {
                        enable.set(true);
                      }
                    });
    CLightningRPC.getInstance()
        .registerCommand(CLightningCommand.DIAGNOSTIC, new CLightningDiagnosticRPC());
  }

  @Test
  public void testMetricsPluginDiagnosticCmdOne() {
    assumeThat(enable.get(), is(true));
    HashMap<String, Object> params = new HashMap<>();
    params.put("metrics_id", "1");
    CLightningDiagnostic diagnostic =
        CLightningRPC.getInstance().runRegisterCommand(CLightningCommand.DIAGNOSTIC, params);
    TestCase.assertTrue(diagnostic.allMetrics());
  }

  @Test
  public void testMetricsPluginDiagnosticCmdTwo() {
    assumeThat(enable.get(), is(true));
    HashMap<String, Object> params = new HashMap<>();
    params.put("metrics_id", "1");
    CLightningDiagnostic diagnostic =
        CLightningRPC.getInstance().runRegisterCommand(CLightningCommand.DIAGNOSTIC, params);
    TestCase.assertTrue(diagnostic.allMetrics());
    var getInfo = CLightningRPC.getInstance().getInfo();
    CLightningMetricOne metricOne =
        diagnostic.getMetricWithImplementation("metric_one", CLightningMetricOne.class);
    TestCase.assertEquals(getInfo.getId(), metricOne.getNodeId());
  }

  @Test
  public void testMetricsPluginDiagnosticCmdThree() {
    assumeThat(enable.get(), is(true));
    HashMap<String, Object> params = new HashMap<>();
    params.put("metrics_id", "1,");
    CLightningDiagnostic diagnostic =
        CLightningRPC.getInstance().runRegisterCommand(CLightningCommand.DIAGNOSTIC, params);
    TestCase.assertTrue(diagnostic.allMetrics());
    var getInfo = CLightningRPC.getInstance().getInfo();
    CLightningMetricOne metricOne =
        diagnostic.getMetricWithImplementation("metric_one", CLightningMetricOne.class);
    TestCase.assertEquals(getInfo.getId(), metricOne.getNodeId());
  }
}

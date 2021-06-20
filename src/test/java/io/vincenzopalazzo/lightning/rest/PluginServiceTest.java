package io.vincenzopalazzo.lightning.rest;

import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningHelp;
import junit.framework.TestCase;
import kong.unirest.Unirest;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

public class PluginServiceTest extends AbstractServiceTest {

    @Test
    public void GET_diagnostic() {
        //assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
        try {
            CLightningHelp help = CLightningRPC.getInstance().help();
            AtomicBoolean enable = new AtomicBoolean(false);
            help.getHelpItems()
                    .parallelStream()
                    .forEach(it -> {
                if (it.getCommand().contains("diagnostic")) {
                    enable.set(true);
                }
            });
            TestCase.assertTrue(enable.get());
            var response = Unirest.get("/plugin/diagnostic").asString();
            LOGGER.debug("GET_diagnostic response: " + response.getBody());
            LOGGER.debug("GET_diagnostic status: " + response.getStatusText());
            TestCase.assertTrue(response.isSuccess());
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }
}

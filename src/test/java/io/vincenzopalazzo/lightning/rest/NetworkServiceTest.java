package io.vincenzopalazzo.lightning.rest;

import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import jrpc.clightning.CLightningRPC;
import junit.framework.TestCase;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

public class NetworkServiceTest extends AbstractServiceTest {

    @Test
    public void GET_ping() {
        assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
        try {
            var nodeInfo = CLightningRPC.getInstance().getInfo();
            HttpResponse response = Unirest.get("/network/ping/" + nodeInfo.getId())
                    .asString();
            LOGGER.debug("POST_invoice response: " + response.getBody().toString());
            assertThat(response.getStatus()).isEqualTo(500);
            TestCase.assertTrue(response.getBody().toString().contains("Unknown peer"));
        } catch (Exception exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }
}

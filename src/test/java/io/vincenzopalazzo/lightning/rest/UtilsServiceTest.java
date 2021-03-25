package io.vincenzopalazzo.lightning.rest;

import io.javalin.plugin.json.JavalinJson;
import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningGetInfo;
import junit.framework.TestCase;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsServiceTest extends AbstractServiceTest {

    @Test
    public void GET_getInfoNode() {
        try {
            CLightningGetInfo getInfo = rpc.getInfo();
            String jsonResult = JavalinJson.toJson(getInfo);
            HttpResponse response = Unirest.get("/utility/getinfo").asString();

            LOGGER.debug("GET_getInfoNode response: " + response.getBody().toString());
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(jsonResult);
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }
}

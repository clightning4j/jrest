package io.vincenzopalazzo.lightning.rest;

import static org.assertj.core.api.Assertions.assertThat;

import io.javalin.plugin.json.JavalinJson;
import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import jrpc.clightning.exceptions.CLightningException;
import junit.framework.TestCase;
import kong.unirest.Unirest;
import org.junit.Test;

public class UtilsServiceTest extends AbstractServiceTest {

  @Test
  public void GET_getInfoNode() {
    try {
      var getInfo = rpc.getInfo();
      var jsonResult = JavalinJson.toJson(getInfo);
      var response = Unirest.get("/utility/getinfo").asString();

      LOGGER.debug("GET_getInfoNode response: " + response.getBody());
      assertThat(response.getStatus()).isEqualTo(200);
      assertThat(response.getBody()).isEqualTo(jsonResult);
    } catch (CLightningException exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }

  @Test
  public void GET_listFunds() {
    try {
      var getInfo = rpc.listFunds();
      var jsonResult = JavalinJson.toJson(getInfo);
      var response = Unirest.get("/utility/listfounds").asString();

      LOGGER.debug("GET_listFunds response: " + response.getBody());
      assertThat(response.getStatus()).isEqualTo(200);
      assertThat(response.getBody()).isEqualTo(jsonResult);
    } catch (CLightningException exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }
}

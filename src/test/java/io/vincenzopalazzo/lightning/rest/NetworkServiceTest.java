package io.vincenzopalazzo.lightning.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

import io.javalin.plugin.json.JavalinJackson;
import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import junit.framework.TestCase;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.Test;

public class NetworkServiceTest extends AbstractServiceTest {

  @Test
  public void GET_ping() {
    assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
    try {
      var nodeInfo = rpc.getInfo();
      HttpResponse response = Unirest.get("/network/ping/" + nodeInfo.getId()).asString();
      LOGGER.debug("POST_invoice response: " + response.getBody().toString());
      assertThat(response.getStatus()).isEqualTo(500);
      TestCase.assertTrue(response.getBody().toString().contains("Unknown peer"));
    } catch (Exception exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }

  @Test
  public void GET_listNodes() {
    assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
    try {
      var allNodes = rpc.listNodes();
      String asString = new JavalinJackson().toJsonString(allNodes);
      HttpResponse response = Unirest.get("/network/listnodes").asString();
      LOGGER.debug("POST_invoice response: " + response.getBody().toString());
      assertThat(response.getStatus()).isEqualTo(200);
      TestCase.assertEquals(asString, response.getBody().toString());
    } catch (Exception exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }
}

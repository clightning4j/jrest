package io.vincenzopalazzo.lightning.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.reflect.TypeToken;
import io.javalin.plugin.json.JavalinJackson;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.CLightningVerifyMessage;
import io.vincenzopalazzo.lightning.rest.model.rpc.type.ClightningSignMessage;
import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.wrapper.response.RPCResponseWrapper;
import junit.framework.TestCase;
import kong.unirest.Unirest;
import org.junit.Test;

public class UtilsServiceTest extends AbstractServiceTest {

  @Test
  public void GET_getInfoNode() {
    try {
      var getInfo = rpc.getInfo();
      var jsonResult = new JavalinJackson().toJsonString(getInfo);
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
      var jsonResult = new JavalinJackson().toJsonString(getInfo);
      var response = Unirest.get("/utility/listfounds").asString();

      LOGGER.debug("GET_listFunds response: " + response.getBody());
      assertThat(response.getStatus()).isEqualTo(200);
      assertThat(response.getBody()).isEqualTo(jsonResult);
    } catch (CLightningException exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }

  @Test
  public void POST_verifyMessage() throws IOException {
    try {
      var getInfo = rpc.getInfo();
      var payload = new HashMap<String, Object>();
      payload.put("message", "Hello from jrest");
      var signRaw = rpc.rawCommand("signmessage", payload);
      LOGGER.debug("POST_verifyMessage mock signmessage: " + signRaw);
      Type type = new TypeToken<RPCResponseWrapper<ClightningSignMessage>>() {}.getType();
      RPCResponseWrapper<ClightningSignMessage> rawResponse =
          (RPCResponseWrapper<ClightningSignMessage>) converter.deserialization(signRaw, type);
      assertThat(rawResponse.getError()).isNull();
      ClightningSignMessage signMessage = rawResponse.getResult();
      var response =
          Unirest.post("/utility/checkmessage")
              .field("message", "Hello from jrest")
              .field("zbase", signMessage.getZbase())
              .field("pubkey", getInfo.getId())
              .asString();

      LOGGER.debug("POST_verifyMessage response: " + response.getBody());

      assertThat(response.getStatus()).isEqualTo(200);
      CLightningVerifyMessage verifyMessage =
          (CLightningVerifyMessage)
              converter.deserialization(response.getBody(), CLightningVerifyMessage.class);
      assertThat(verifyMessage.getVerified()).isEqualTo(true);
    } catch (CLightningException exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }
}

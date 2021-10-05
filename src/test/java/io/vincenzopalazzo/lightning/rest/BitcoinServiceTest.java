package io.vincenzopalazzo.lightning.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.model.types.AddressType;
import junit.framework.TestCase;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.Test;

public class BitcoinServiceTest extends AbstractServiceTest {

  @Test
  public void GET_newAddressP2sh() {
    assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
    try {
      var address = CLightningRPC.getInstance().newAddress(AddressType.P2SH_SEGWIT);
      HttpResponse response = Unirest.get("/bitcoin/newaddr/p2sh-segwit").asString();
      var toTest = response.getBody().toString();
      LOGGER.debug("POST_invoice response: " + toTest);
      LOGGER.debug("POST_invoice address expected length: " + address);
      assertThat(response.getStatus()).isEqualTo(200);
      TestCase.assertEquals(toTest.length(), address.length());
    } catch (Exception exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }

  @Test
  public void GET_newAddressBech32() {
    assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
    try {
      var address = CLightningRPC.getInstance().newAddress(AddressType.BECH32);
      HttpResponse response = Unirest.get("/bitcoin/newaddr/bech32").asString();
      var toTest = response.getBody().toString();
      LOGGER.debug("POST_invoice response: " + toTest);
      LOGGER.debug("POST_invoice address expected length: " + address);
      assertThat(response.getStatus()).isEqualTo(200);
      TestCase.assertEquals(toTest.length(), address.length());
    } catch (Exception exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }

  @Test
  public void GET_withdraw() {
    assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
    try {
      var address = CLightningRPC.getInstance().newAddress(AddressType.BECH32);
      HttpResponse response =
          Unirest.post(String.format("/bitcoin/withdraw"))
              .field("destination", address)
              .field("satoshi", "10000000")
              .asString();
      LOGGER.debug("POST_invoice response: " + response.getBody().toString());
      assertThat(response.getStatus()).isEqualTo(501);
      TestCase.assertTrue(response.getBody().toString().contains("Could not afford"));

    } catch (Exception exception) {
      TestCase.fail(exception.getLocalizedMessage());
    }
  }
}

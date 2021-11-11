package io.vincenzopalazzo.lightning.testutil;

import io.vincenzopalazzo.lightning.rest.CLightningRestPlugin;
import io.vincenzopalazzo.lightning.rest.PaymentServiceTest;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.LiteCLightningRPC;
import jrpc.service.converters.JsonConverter;
import kong.unirest.Unirest;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractServiceTest {

  protected static final String BASE_URL = "http://localhost:7010";
  protected static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceTest.class);

  protected CLightningRestPlugin app = new CLightningRestPlugin();
  protected CLightningRPC rpc = new CLightningRPC();
  protected LiteCLightningRPC liteRpc = new LiteCLightningRPC();
  protected JsonConverter converter = new JsonConverter();

  public AbstractServiceTest() {
    Unirest.config().defaultBaseUrl(BASE_URL);
  }

  @Before
  public void init() {
    app.testModeOne();
    rpc.listInvoices()
        .getListInvoice()
        .forEach(it -> rpc.delInvoice(it.getLabel(), it.getStatus()));
  }

  @After
  public void tearDown() throws InterruptedException {
    app.testModeOff();
    Thread.sleep(1000);
  }
}

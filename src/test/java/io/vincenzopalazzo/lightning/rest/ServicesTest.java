package io.vincenzopalazzo.lightning.rest;

import io.javalin.plugin.json.JavalinJson;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningGetInfo;
import jrpc.clightning.model.CLightningInvoice;
import junit.framework.TestCase;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.assertj.core.error.ShouldNotBeEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

public class ServicesTest {

    private CLightningRestPlugin app = new CLightningRestPlugin();
    private CLightningRPC rpc = CLightningRPC.getInstance();


    @Before
    public void init() {
        app.testModeOne();
        rpc.listInvoices().getListInvoice().forEach(it -> {
            rpc.delInvoice(it.getLabel(), it.getStatus());
        });
    }

    @After
    public void tearDown() throws InterruptedException {
        app.testModeOff();
    }

    @Test
    public void GET_getInfoNode() {
        try {
            CLightningGetInfo getInfo = rpc.getInfo();
            String jsonResult = JavalinJson.toJson(getInfo);
            HttpResponse response = Unirest.get("http://localhost:7001/utility/getinfo").asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(jsonResult);
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }

    @Test
    public void GET_listInvoice() {
        // payment/listinvoice
        try {
            var listInvoices = rpc.listInvoices();
            String jsonResult = JavalinJson.toJson(listInvoices.getListInvoice());

            HttpResponse response = Unirest.get("http://localhost:7001/payment/listinvoice").asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(jsonResult);
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }

    @Test
    public void POST_decodePay() {
        // payment/listinvoice
        try {
            var invoice = "lnbc1pvjluezpp5qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqypqdpl2pkx2ctnv5sxxmmwwd5kgetjypeh2ursdae8g6twvus8g6rfwvs8qun0dfjkxaq8rkx3yf5tcsyz3d73gafnh3cax9rn449d9p5uxz9ezhhypd0elx87sjle52x86fux2ypatgddc6k63n7erqz25le42c4u4ecky03ylcqca784w";
            var aspected = rpc.decodePay(invoice);
            String jsonResult = JavalinJson.toJson(aspected);

            HttpResponse response = Unirest.post("http://localhost:7001/payment/decodepay")
                    .field("bolt11", invoice)
                    .asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(jsonResult);
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }

    @Test
    public void POST_delInvoice() {
        assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
        try {
            var invoice = rpc.invoice("1000", "test-invoice", "test");
            String jsonResult = JavalinJson.toJson(invoice);

            HttpResponse response = Unirest.post("http://localhost:7001/payment/delInvoice")
                    .field("label", invoice.getLabel())
                    .field("status", invoice.getStatus())
                    .asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(jsonResult);
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }

    @Test
    public void POST_invoice() {
        assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
        var invoice = rpc.invoice("1000", "test-invoice", "test");
        try {
            HttpResponse response = Unirest.post("http://localhost:7001/payment/invoice")
                    .field("msat", "1000")
                    .field("label", "test-invoice")
                    .field("description", "test")
                    .asString();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isIn("bolt11");
            assertThat(response.getBody()).isIn("label");
            rpc.delInvoice("test-invoice", "unpaid");
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }


}

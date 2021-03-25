package io.vincenzopalazzo.lightning.rest;

import io.javalin.plugin.json.JavalinJson;
import io.vincenzopalazzo.lightning.testutil.AbstractServiceTest;
import jrpc.clightning.exceptions.CLightningException;
import junit.framework.TestCase;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;

public class PaymentServiceTest extends AbstractServiceTest {


    @Test
    public void GET_listInvoice() {
        // payment/listinvoice
        try {
            var listInvoices = rpc.listInvoices();
            String jsonResult = JavalinJson.toJson(listInvoices);
            HttpResponse response = Unirest.get("/payment/listinvoice")
                    .asString();
            LOGGER.debug("GET_listInvoice response: " + response.getBody().toString());
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(jsonResult);
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }

    @Test
    public void POST_listInvoice() {
        // payment/listinvoice
        try {
            var invoice = this.rpc.invoice("1000", "test", "test");
            var listInvoice = rpc.listInvoices().getListInvoice();
            TestCase.assertFalse(listInvoice.isEmpty());
            invoice = listInvoice.get(0);
            TestCase.assertNotNull(invoice.getBolt11());
            String jsonResult = JavalinJson.toJson(invoice);
            HttpResponse response = Unirest.post("/payment/listinvoice")
                    .field("label", invoice.getLabel())
                    .asString();
            LOGGER.debug("GET_listInvoice response: " + response.getBody().toString());
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
            var invoice = this.rpc.invoice("1000", "test", "test");
            var expected = rpc.decodePay(invoice.getBolt11());
            String jsonResult = JavalinJson.toJson(expected);

            HttpResponse response = Unirest.post("/payment/decodepay")
                    .field("bolt11", invoice.getBolt11())
                    .asString();
            LOGGER.debug("POST_decodePay response: " + response.getBody().toString());
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
            var num = Math.random();
            var invoice = rpc.invoice("1000", "test-invoice-" + num, "test");
            invoice = rpc.listInvoices("test-invoice-" + num).getListInvoice().get(0);
            String jsonResult = JavalinJson.toJson(invoice);
            TestCase.assertNotNull(invoice.getStatus());
            TestCase.assertNotNull(invoice.getLabel());

            LOGGER.debug("POST_delInvoice expected response: " + jsonResult);
            HttpResponse response = Unirest.delete("/payment/delinvoice/" + invoice.getLabel())
                    .asString();
            LOGGER.debug("POST_delInvoice response: " + response.getBody().toString());
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isEqualTo(jsonResult);
        } catch (CLightningException exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }

    @Test
    public void POST_invoice() {
        assumeThat(rpc.getInfo().getNetwork(), is("testnet"));
        var num = Math.random();
        try {
            var listInvoice = rpc.listInvoices("test-invoice-" + num).getListInvoice();
            TestCase.assertTrue(listInvoice.isEmpty());
            HttpResponse response = Unirest.post("/payment/invoice")
                    .field("msat","1000")
                    .field("label", "test-invoice-" + num)
                    .field("description", "test")
                    .asString();
            LOGGER.debug("POST_invoice response: " + response.getBody().toString());
            assertThat(response.getStatus()).isEqualTo(200);
            TestCase.assertTrue(response.getBody().toString().contains("bolt11"));
            TestCase.assertTrue(response.getBody().toString().contains("label"));
            listInvoice = rpc.listInvoices("test-invoice-" + num).getListInvoice();
            TestCase.assertFalse(listInvoice.isEmpty());
        } catch (Exception exception) {
            TestCase.fail(exception.getLocalizedMessage());
        }
    }
}

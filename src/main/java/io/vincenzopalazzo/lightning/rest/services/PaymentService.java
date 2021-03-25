package io.vincenzopalazzo.lightning.rest.services;

import java.util.Objects;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningDecodePay;
import jrpc.clightning.model.CLightningInvoice;
import jrpc.clightning.model.CLightningListInvoices;

public class PaymentService {

    private static final String SERVICE = "Payment";

    @OpenApi(
            path = "/payment/listinvoice",            // only necessary to include when using static method references
            method = HttpMethod.GET,    // only necessary to include when using static method references
            summary = "Get list invoice stored inside the node",
            operationId = SERVICE,
            tags = {SERVICE},
            pathParams = {
                @OpenApiParam(name="bolt11", required = false, description = "Filter by bolt11, it is null no filtering happen")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = UtilsService.class)})
            }
    )
    public static void listInvoice(Context context) {
        try {
            String bolt11 = context.pathParam("bolt11");
            CLightningListInvoices invoices;
            if (bolt11 != null) {
                invoices = CLightningRPC.getInstance().listInvoices(bolt11);
            } else {
                invoices = CLightningRPC.getInstance().listInvoices();
            }
            UtilsService.makeSuccessResponse(context, invoices);
        } catch (CLightningException exception) {
            UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
        }
    }

    @OpenApi(
            path = "/payment/decodepay", // only necessary to include when using static method references
            method = HttpMethod.POST,    // only necessary to include when using static method references
            description = "This API cal permit to caller to decode pay from a bolt11",
            summary = "Decode pay from a bolt11",
            operationId = SERVICE,
            tags = {SERVICE},
            formParams = {
              @OpenApiFormParam(name = "bolt11", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = CLightningDecodePay.class)})
            }
    )
    public static void decodePay(Context context){
        String bolt11 = context.formParam("bolt11", String.class)
                .check(i -> !i.isEmpty())
                .get();
        if(bolt11.isEmpty()){
           UtilsService.makeErrorResponse(context, "bolt11 not valid");
            return;
        }
        try {
            CLightningDecodePay decodePay = CLightningRPC.getInstance().decodePay(bolt11);
            UtilsService.makeSuccessResponse(context, decodePay);
        }catch (CLightningException ex) {
            UtilsService.makeErrorResponse(context, ex.getLocalizedMessage());
        }
    }

    @OpenApi(
            path = "/payment/decodepay",            // only necessary to include when using static method references
            method = HttpMethod.DELETE,    // only necessary to include when using static method references
            description = "This API cal permit to caller to decode pay from a bolt11",
            summary = "Decode pay from a bolt11",
            operationId = SERVICE,
            tags = {SERVICE},
            formParams = {
                    @OpenApiFormParam(name = "label", required = true),
                    @OpenApiFormParam(name = "status", required = true),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = CLightningInvoice.class)})
            }
    )
    public static void delInvoice(Context context){
        String label = context.formParam("label", String.class)
                .check(i -> !i.isEmpty())
                .get();
        String status = context.formParam("status", String.class)
                .check(i -> !i.isEmpty())
                .get();

        try {
            CLightningInvoice invoice = CLightningRPC.getInstance().delInvoice(label, status);
            UtilsService.makeSuccessResponse(context, invoice);
        }catch (CLightningException ex) {
            UtilsService.makeErrorResponse(context, ex.getLocalizedMessage());
        }
    }

    @OpenApi(
            path = "/payment/invoice",            // only necessary to include when using static method references
            method = HttpMethod.POST,    // only necessary to include when using static method references
            description = "This API cal permit to caller to create an invoice with the following parameter {TODO}",
            summary = "Make an invoice",
            operationId = SERVICE,
            tags = {SERVICE},
            formParams = {
                @OpenApiFormParam(name = "label", required = true),
                @OpenApiFormParam(name = "msat", required = true),
                @OpenApiFormParam(name = "description"),
                @OpenApiFormParam(name = "expiry"),
                @OpenApiFormParam(name = "expiry"),
                @OpenApiFormParam(name = "preImage"),
                @OpenApiFormParam(name = "exposePrivateChannels"),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = CLightningInvoice.class)})
            }
    ) //TODO complete the documentation
    public static void invoice(Context context){
        String milliSatoshi = context.formParam("msat", String.class)
                .check(i -> !i.isEmpty())
                .get();
        String label = context.formParam("label", String.class)
                .check(i -> !i.isEmpty())
                .get();
        String description = context.formParam("description", String.class)
                .check(Objects::nonNull)
                .check(i -> !i.isEmpty())
                .get();
        String expiry = context.formParam("expiry");
        if(expiry == null || expiry.isEmpty()){
            expiry = "";
        }
        String preImage = context.formParam("preImage");
        if(preImage == null || preImage.isEmpty()){
            preImage = "";
        }
        String exposePrivateChannelsString = context.formParam("exposePrivateChannels");
        boolean exposePrivateChannels = false;
        if(exposePrivateChannelsString != null && !exposePrivateChannelsString.isEmpty()){
            exposePrivateChannels = Boolean.getBoolean(exposePrivateChannelsString);
        }
        // TODO: support fallbacks
        //String[] fallbacks = context.queryParam("fallbacks", List<String>.class).get();

        try {
            CLightningInvoice invoice = CLightningRPC.getInstance()
                    .invoice(milliSatoshi, label, description, expiry, new String[]{}, preImage, exposePrivateChannels);
            UtilsService.makeSuccessResponse(context, invoice);
        }catch (CLightningException ex) {
            UtilsService.makeErrorResponse(context, ex.getLocalizedMessage());
        }
    }

}

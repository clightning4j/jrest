package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.annotations.*;
import io.vincenzopalazzo.lightning.rest.model.ErrorMessage;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.model.CLightningDecodePay;
import jrpc.clightning.model.CLightningInvoice;
import jrpc.clightning.model.CLightningListInvoices;

public class PaymentService {

  private static final String SERVICE = "Payment Services";

  @OpenApi(
      path =
          "/payment/listinvoice", // only necessary to include when using static method references
      method = HttpMethod.GET, // only necessary to include when using static method references
      summary = "Get list invoice stored inside the node",
      operationId = SERVICE,
      tags = {SERVICE},
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningListInvoices.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = ErrorMessage.class)}),
      })
  public static void listInvoice(Context context) {
    try {
      CLightningListInvoices invoices;
      invoices = CLightningRPC.getInstance().listInvoices();
      UtilsService.makeSuccessResponse(context, invoices);
    } catch (CLightningException exception) {
      UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
    }
  }

  @OpenApi(
      path =
          "/payment/listinvoice", // only necessary to include when using static method references
      method = HttpMethod.POST, // only necessary to include when using static method references
      summary = "Filter by label all the invoices of the node",
      operationId = SERVICE,
      tags = {SERVICE},
      formParams = {@OpenApiFormParam(name = "label", required = true)},
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningInvoice.class)}),
        @OpenApiResponse(
            status = "400",
            content = {@OpenApiContent(from = ErrorMessage.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = ErrorMessage.class)}),
      })
  public static void listInvoiceFiltered(Context context) {
    try {
      String bolt11 = context.formParam("label");
      CLightningListInvoices invoices = CLightningRPC.getInstance().listInvoices(bolt11);
      if (invoices.getListInvoice().isEmpty()) {
        context.status(404);
        context.json(new ErrorMessage(404, "Invoice not found"));
        return;
      }
      UtilsService.makeSuccessResponse(context, invoices.getListInvoice().get(0));
    } catch (CLightningException exception) {
      UtilsService.makeErrorResponse(context, exception.getLocalizedMessage());
    }
  }

  @OpenApi(
      path = "/payment/decodepay", // only necessary to include when using static method references
      method = HttpMethod.POST, // only necessary to include when using static method references
      description = "This API cal permit to caller to decode pay from a bolt11",
      summary = "Decode pay from a bolt11",
      operationId = SERVICE,
      tags = {SERVICE},
      formParams = {@OpenApiFormParam(name = "bolt11", required = true)},
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningDecodePay.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = ErrorMessage.class)})
      })
  public static void decodePay(Context context) {
    String bolt11 = context.formParam("bolt11");
    if (bolt11.isEmpty()) {
      UtilsService.makeErrorResponse(context, "bolt11 not valid");
      return;
    }
    try {
      CLightningDecodePay decodePay = CLightningRPC.getInstance().decodePay(bolt11);
      UtilsService.makeSuccessResponse(context, decodePay);
    } catch (CLightningException ex) {
      UtilsService.makeErrorResponse(context, ex.getLocalizedMessage());
    }
  }

  @OpenApi(
      path = "/payment/delinvoice", // only necessary to include when using static method references
      method = HttpMethod.DELETE, // only necessary to include when using static method references
      description = "This API call give the possibility to delete a invoice with a specified label",
      summary = "delete an invoice with the a specified label",
      operationId = SERVICE,
      tags = {SERVICE},
      pathParams = {
        @OpenApiParam(name = "label", required = true, description = "Unique invoice label"),
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningInvoice.class)}),
        @OpenApiResponse(
            status = "404",
            content = {@OpenApiContent(from = NotFoundResponse.class)}),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = InternalServerErrorResponse.class)}),
      })
  public static void delInvoice(Context context) {
    String label = context.pathParam("label");

    try {
      CLightningListInvoices listInvoices = CLightningRPC.getInstance().listInvoices(label);
      if (listInvoices.getListInvoice().isEmpty()) {
        throw new NotFoundResponse("Invoice not found");
      }
      String status = listInvoices.getListInvoice().get(0).getStatus();
      CLightningInvoice invoice = CLightningRPC.getInstance().delInvoice(label, status);
      UtilsService.makeSuccessResponse(context, invoice);
    } catch (CLightningException ex) {
      throw new InternalServerErrorResponse(ex.getLocalizedMessage());
    }
  }

  @OpenApi(
      path = "/payment/invoice", // only necessary to include when using static method references
      method = HttpMethod.POST, // only necessary to include when using static method references
      description =
          "This API cal permit to caller to create an invoice with the following parameter {TODO}",
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
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = CLightningInvoice.class)})
      }) // TODO complete the documentation
  public static void invoice(Context context) {
    String milliSatoshi =
        context
            .formParamAsClass("msat", String.class)
            .check(i -> !i.isEmpty(), "msat value is not empty")
            .get();
    String label =
        context
            .formParamAsClass("label", String.class)
            .check(i -> !i.isEmpty(), "label value is not empty")
            .get();
    String description =
        context
            .formParamAsClass("description", String.class)
            .check(i -> !i.isEmpty(), "description is not empty")
            .get();
    String expiry = context.formParam("expiry");
    if (expiry == null || expiry.isEmpty()) {
      expiry = "";
    }
    String preImage = context.formParam("preImage");
    if (preImage == null || preImage.isEmpty()) {
      preImage = "";
    }
    String exposePrivateChannelsString = context.formParam("exposePrivateChannels");
    boolean exposePrivateChannels = false;
    if (exposePrivateChannelsString != null && !exposePrivateChannelsString.isEmpty()) {
      exposePrivateChannels = Boolean.getBoolean(exposePrivateChannelsString);
    }
    // TODO: support fallbacks
    // String[] fallbacks = context.queryParam("fallbacks", List<String>.class).get();

    try {
      CLightningInvoice invoice =
          CLightningRPC.getInstance()
              .invoice(
                  milliSatoshi,
                  label,
                  description,
                  expiry,
                  new String[] {},
                  preImage,
                  exposePrivateChannels);
      UtilsService.makeSuccessResponse(context, invoice);
    } catch (CLightningException ex) {
      UtilsService.makeErrorResponse(context, ex.getLocalizedMessage());
    }
  }
}

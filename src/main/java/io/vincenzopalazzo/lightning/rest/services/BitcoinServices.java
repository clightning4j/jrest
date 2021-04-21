package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CLightningException;
import jrpc.clightning.exceptions.CommandException;
import jrpc.clightning.model.CLightningBitcoinTx;
import jrpc.clightning.model.types.AddressType;

public class BitcoinServices {

    private static final String SERVICE = "Bitcoin Services";

    @OpenApi(
            path = "/bitcoin/newaddr",            // only necessary to include when using static method references
            method = HttpMethod.GET,    // only necessary to include when using static method references
            summary = "Generate new address with a type",
            operationId = SERVICE,
            tags = {SERVICE},
            pathParams = {
                    @OpenApiParam(name = "type", description = "Type bitcoin address", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = String.class)})
            }
    )
    public static void newAddr(Context ctx) {
        String addressType = ctx.pathParam("type");
        AddressType type = null;
        if (addressType.equals("p2sh-segwit")) {
            type = AddressType.P2SH_SEGWIT;
        } else if (addressType.equals("bech32")) {
            type = AddressType.BECH32;
        }
        try {
            String result = CLightningRPC.getInstance().newAddress(type);
            UtilsService.makeSuccessResponse(ctx, result);
        } catch (CLightningException | CommandException exception) {
            UtilsService.makeErrorResponse(ctx, exception.getLocalizedMessage());
        }
    }

    @OpenApi(
            path = "/bitcoin/withdraw",            // only necessary to include when using static method references
            method = HttpMethod.POST,    // only necessary to include when using static method references
            summary = "Move satoshi from off chain to on chain",
            operationId = SERVICE,
            tags = {SERVICE},
            formParams = {
                    @OpenApiFormParam(name = "destination", required = true),
                    @OpenApiFormParam(name = "satoshi", required = true),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = CLightningBitcoinTx.class)})
            }
    )
    public static void withdraw(Context ctx) {
        String destination = ctx.formParam("destination");
        String satoshi = ctx.formParam("satoshi");
        try {
            CLightningBitcoinTx bitcoinTx = CLightningRPC.getInstance().withdraw(
                    destination,
                    satoshi
            );
            UtilsService.makeSuccessResponse(ctx, bitcoinTx);
        } catch (CLightningException | CommandException commandException) {
            UtilsService.makeErrorResponse(ctx, commandException.getLocalizedMessage());
        }
    }
}

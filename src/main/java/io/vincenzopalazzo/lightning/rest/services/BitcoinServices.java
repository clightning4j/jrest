package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.openapi.annotations.*;
import jrpc.clightning.CLightningRPC;
import jrpc.clightning.exceptions.CommandException;
import jrpc.clightning.model.CLightningBitcoinTx;
import jrpc.clightning.model.types.AddressType;
import jrpc.service.converters.jsonwrapper.CLightningJsonObject;

public class BitcoinServices {

    private static final String SERVICE = "Bitcoin";

    @OpenApi(
            path = "/bitcoin/newaddr",            // only necessary to include when using static method references
            method = HttpMethod.GET,    // only necessary to include when using static method references
            summary = "Generate new address with a type",
            operationId = SERVICE,
                tags = {SERVICE},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = String.class)})
            }
    )
    public static void newAddr(Context ctx) {
        String addressType = ctx.queryParam("type");
        AddressType type = null;
        if (addressType.equals("p2sh-segwit")) {
            type = AddressType.P2SH_SEGWIT;
        } else if (addressType.equals("betch32")) {
            type = AddressType.BECH32;
        }
        String result = "";
        CLightningJsonObject response = new CLightningJsonObject();
        if (type == null) {
            result = "Type address " + addressType + " wrong";
            response.add("error", result);
        } else {
            result = CLightningRPC.getInstance().getNewAddress(type);
            response.add("address", result);
            response.add(addressType, result);
        }
        ctx.json(response.entrySet());
    }

    @OpenApi(
            path = "/bitcoin/withdraw",            // only necessary to include when using static method references
            method = HttpMethod.POST,    // only necessary to include when using static method references
            summary = "Move satoshi from off chain to on chain",
            operationId = SERVICE,
            tags = {SERVICE},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = CLightningBitcoinTx.class)})
            }
    )
    public static void withdraw(Context ctx) {
        String destination = ctx.formParam("destination");
        String satoshi = ctx.formParam("satoshi");
        try {
            ctx.json(CLightningRPC.getInstance().withDraw(
                    destination,
                    satoshi
            ));
        } catch (CommandException commandException) {
            throw new NotFoundResponse(commandException.getMessage());
        }
    }
}

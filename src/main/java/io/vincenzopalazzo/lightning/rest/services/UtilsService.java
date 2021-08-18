package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;

class UtilsService {
    static void makeErrorResponse(Context context, String exception) {
        throw new InternalServerErrorResponse(exception);
    }

    static <T> void makeSuccessResponse(Context context, T response) {
        //context.json(new SuccessMessage<T>(response));
        context.json(response);
        context.status(200);
    }
}

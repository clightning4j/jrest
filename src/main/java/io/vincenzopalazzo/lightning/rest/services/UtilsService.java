package io.vincenzopalazzo.lightning.rest.services;

import io.javalin.http.Context;

class UtilsService {
    static void makeErrorResponse(Context context, String exception) {
        context.json(exception);
        context.status(500);
    }

    static <T> void makeSuccessResponse(Context context, T response) {
        //context.json(new SuccessMessage<T>(response));
        context.json(response);
        context.status(200);
    }
}

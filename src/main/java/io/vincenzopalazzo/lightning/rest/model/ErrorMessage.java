package io.vincenzopalazzo.lightning.rest.model;

public class ErrorMessage {

    private int code;
    private String message;

    public ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

package com.stripe.android.exception;

public class InvalidRequestException extends StripeException {
    private final String param;

    public InvalidRequestException(String message, String param2, String requestId, Integer statusCode, Throwable e) {
        super(message, requestId, statusCode, e);
        this.param = param2;
    }

    public String getParam() {
        return this.param;
    }
}

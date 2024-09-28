package com.stripe.android.exception;

public class APIConnectionException extends StripeException {
    public APIConnectionException(String message) {
        super(message, (String) null, 0);
    }

    public APIConnectionException(String message, Throwable e) {
        super(message, (String) null, 0, e);
    }
}

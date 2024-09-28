package com.stripe.android.exception;

public abstract class StripeException extends Exception {
    protected static final long serialVersionUID = 1;
    private String requestId;
    private Integer statusCode;

    public StripeException(String message, String requestId2, Integer statusCode2) {
        super(message, (Throwable) null);
        this.requestId = requestId2;
        this.statusCode = statusCode2;
    }

    public StripeException(String message, String requestId2, Integer statusCode2, Throwable e) {
        super(message, e);
        this.statusCode = statusCode2;
        this.requestId = requestId2;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public String toString() {
        String reqIdStr = "";
        if (this.requestId != null) {
            reqIdStr = "; request-id: " + this.requestId;
        }
        return super.toString() + reqIdStr;
    }
}

package io.reactivex.exceptions;

public final class OnErrorNotImplementedException extends RuntimeException {
    private static final long serialVersionUID = -6298857009889503852L;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public OnErrorNotImplementedException(String message, Throwable e) {
        super(message, e != null ? e : new NullPointerException());
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public OnErrorNotImplementedException(Throwable e) {
        super(e != null ? e.getMessage() : null, e != null ? e : new NullPointerException());
    }
}

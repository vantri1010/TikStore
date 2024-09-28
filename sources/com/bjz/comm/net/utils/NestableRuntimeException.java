package com.bjz.comm.net.utils;

public class NestableRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1;
    private Throwable cause = null;

    public NestableRuntimeException() {
    }

    public NestableRuntimeException(String msg) {
        super(msg);
    }

    public NestableRuntimeException(Throwable cause2) {
        this.cause = cause2;
    }

    public NestableRuntimeException(String msg, Throwable cause2) {
        super(msg);
        this.cause = cause2;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        }
        Throwable th = this.cause;
        if (th != null) {
            return th.toString();
        }
        return null;
    }
}

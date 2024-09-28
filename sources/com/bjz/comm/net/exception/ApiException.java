package com.bjz.comm.net.exception;

import androidx.annotation.Nullable;
import java.io.IOException;

public class ApiException extends IOException {
    private int code;
    private final String msg;

    public ApiException(int code2, String msg2) {
        this.code = code2;
        this.msg = msg2;
    }

    @Nullable
    public String getMessage() {
        String str = this.msg;
        if (str == null || str.equals("")) {
            return super.getMessage();
        }
        return this.msg;
    }

    public int getCode() {
        return this.code;
    }
}

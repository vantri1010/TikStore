package im.bclpbkiauv.tgnet;

import java.util.List;

public class TLApiModel<T> {
    public static final transient String PARSE_FAILED = "-9999";
    public String code;
    public String data;
    public String message;
    public transient T model;
    public transient List<T> modelList;
    private transient String successCode = "0";

    public boolean isSuccess() {
        return this.successCode.equals(this.code);
    }
}

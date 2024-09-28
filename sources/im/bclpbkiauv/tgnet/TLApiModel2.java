package im.bclpbkiauv.tgnet;

import java.util.List;

public class TLApiModel2<T> {
    public static final transient String PARSE_FAILED = "-9999";
    public String business_key;
    public transient T model;
    public transient List<T> modelList;
    public String result_code = "0";
    public String result_data;
    public String result_desc;
    private transient String successCode = "0";

    public boolean isSuccess() {
        return this.successCode.equals(this.result_code);
    }
}

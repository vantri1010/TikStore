package io.openinstall.sdk;

import com.fm.openinstall.model.Error;

public class bf {
    private String a;
    private Error b;

    public enum a {
        NOT_INIT(-8, "未调用初始化"),
        INIT_ERROR(-12, "初始化时错误"),
        REQUEST_FAIL(-1, "请求失败"),
        REQUEST_EXCEPTION(-1, "请求异常"),
        REQUEST_ERROR(-2, "请求错误"),
        REQUEST_TIMEOUT(-4, "超时返回，请重试"),
        INVALID_DATA(-7, "data 不匹配"),
        INVALID_URI(-7, "uri 不匹配");
        
        public final int i;
        public final String j;

        private a(int i2, String str) {
            this.i = i2;
            this.j = str;
        }

        public bf a() {
            return new bf(this.i, this.j);
        }

        public bf a(String str) {
            int i2 = this.i;
            return new bf(i2, this.j + "：" + str);
        }
    }

    private bf(int i, String str) {
        this.b = new Error(i, str);
    }

    private bf(String str) {
        this.a = str;
    }

    public static bf a() {
        return a("");
    }

    public static bf a(bb bbVar) {
        if (!(bbVar instanceof ay)) {
            return bbVar instanceof az ? a.REQUEST_EXCEPTION.a(bbVar.f()) : bbVar instanceof ba ? a.REQUEST_FAIL.a(bbVar.f()) : a();
        }
        ay ayVar = (ay) bbVar;
        return ayVar.a() == 0 ? a(ayVar.c()) : a.REQUEST_ERROR.a(bbVar.f());
    }

    public static bf a(String str) {
        return new bf(str);
    }

    public String b() {
        return this.a;
    }

    public Error c() {
        return this.b;
    }
}

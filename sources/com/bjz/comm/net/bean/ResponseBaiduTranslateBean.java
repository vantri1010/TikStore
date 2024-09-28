package com.bjz.comm.net.bean;

import java.util.Arrays;

public class ResponseBaiduTranslateBean {
    private String corpus_no;
    private String err_msg;
    private int err_no;
    private String[] result;
    private String sn;

    public String getCorpus_no() {
        return this.corpus_no;
    }

    public void setCorpus_no(String corpus_no2) {
        this.corpus_no = corpus_no2;
    }

    public String getErr_msg() {
        return this.err_msg;
    }

    public void setErr_msg(String err_msg2) {
        this.err_msg = err_msg2;
    }

    public int getErr_no() {
        return this.err_no;
    }

    public void setErr_no(int err_no2) {
        this.err_no = err_no2;
    }

    public String[] getResult() {
        return this.result;
    }

    public void setResult(String[] result2) {
        this.result = result2;
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String sn2) {
        this.sn = sn2;
    }

    public String toString() {
        return "ResponseBaiduTranslateBean{corpus_no='" + this.corpus_no + '\'' + ", err_msg='" + this.err_msg + '\'' + ", err_no=" + this.err_no + ", result=" + Arrays.toString(this.result) + ", sn='" + this.sn + '\'' + '}';
    }
}

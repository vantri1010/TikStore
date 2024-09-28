package im.bclpbkiauv.ui.wallet.model;

import java.util.ArrayList;

public class BillRecordsReqBean {
    private String businessKey;
    private String date;
    private ArrayList<Integer> orderTypes = new ArrayList<>();
    private int pageNum;
    private int pageSize;
    private int userId;

    public String getBusinessKey() {
        return this.businessKey;
    }

    public void setBusinessKey(String businessKey2) {
        this.businessKey = businessKey2;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId2) {
        this.userId = userId2;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date2) {
        this.date = date2;
    }

    public ArrayList<Integer> getOrderTypes() {
        return this.orderTypes;
    }

    public void setOrderTypes(ArrayList<Integer> orderTypes2) {
        this.orderTypes = orderTypes2;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum2) {
        this.pageNum = pageNum2;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize2) {
        this.pageSize = pageSize2;
    }
}

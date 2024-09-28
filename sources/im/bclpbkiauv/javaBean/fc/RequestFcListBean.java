package im.bclpbkiauv.javaBean.fc;

import java.io.Serializable;

public class RequestFcListBean implements Serializable {
    private int LimitCount;
    private int OffsetNo;
    private int OrderID = 1;
    private long UserID;

    public RequestFcListBean() {
    }

    public RequestFcListBean(long userID, int offsetNo, int limitCount) {
        this.UserID = userID;
        this.OffsetNo = offsetNo;
        this.LimitCount = limitCount;
    }

    public long getUserID() {
        return this.UserID;
    }

    public void setUserID(long userID) {
        this.UserID = userID;
    }

    public int getOffsetNo() {
        return this.OffsetNo;
    }

    public void setOffsetNo(int offsetNo) {
        this.OffsetNo = offsetNo;
    }

    public int getLimitCount() {
        return this.LimitCount;
    }

    public void setLimitCount(int limitCount) {
        this.LimitCount = limitCount;
    }

    public int getOrderID() {
        return this.OrderID;
    }

    public void setOrderID(int orderID) {
        this.OrderID = orderID;
    }
}

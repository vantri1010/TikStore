package im.bclpbkiauv.ui.wallet.model;

import java.util.ArrayList;

public class BillRecordsResBean {
    private ArrayList<BillRecordResBillListBean> billList;
    private String dateTime;
    private BillRecordResStatisticsBean statistics;

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime2) {
        this.dateTime = dateTime2;
    }

    public ArrayList<BillRecordResBillListBean> getBillList() {
        return this.billList;
    }

    public void setBillList(ArrayList<BillRecordResBillListBean> billList2) {
        this.billList = billList2;
    }

    public BillRecordResStatisticsBean getStatistics() {
        return this.statistics;
    }

    public void setStatistics(BillRecordResStatisticsBean statistics2) {
        this.statistics = statistics2;
    }
}

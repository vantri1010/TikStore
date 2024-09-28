package im.bclpbkiauv.ui.wallet.model;

public class StickyBean {
    private BillRecordResBillListBean bean;
    private String dateTime;
    private BillRecordResStatisticsBean statistics;
    private int type;

    public StickyBean(String dateTime2, BillRecordResStatisticsBean statistics2, int type2) {
        this.dateTime = dateTime2;
        this.type = type2;
        this.statistics = statistics2;
    }

    public StickyBean(BillRecordResBillListBean bean2, int type2) {
        this.bean = bean2;
        this.type = type2;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime2) {
        this.dateTime = dateTime2;
    }

    public BillRecordResBillListBean getBean() {
        return this.bean;
    }

    public void setBean(BillRecordResBillListBean bean2) {
        this.bean = bean2;
    }

    public BillRecordResStatisticsBean getStatistics() {
        return this.statistics;
    }

    public void setStatistics(BillRecordResStatisticsBean statistics2) {
        this.statistics = statistics2;
    }

    public int getType() {
        return this.type;
    }
}

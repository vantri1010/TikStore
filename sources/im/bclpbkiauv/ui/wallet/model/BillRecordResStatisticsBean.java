package im.bclpbkiauv.ui.wallet.model;

public class BillRecordResStatisticsBean {
    private String dateTime;
    private int expenditureAmount;
    private int incomeAmount;

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime2) {
        this.dateTime = dateTime2;
    }

    public int getIncomeAmount() {
        return this.incomeAmount;
    }

    public void setIncomeAmount(int incomeAmount2) {
        this.incomeAmount = incomeAmount2;
    }

    public int getExpenditureAmount() {
        return this.expenditureAmount;
    }

    public void setExpenditureAmount(int expenditureAmount2) {
        this.expenditureAmount = expenditureAmount2;
    }
}

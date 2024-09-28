package im.bclpbkiauv.ui.hui.packet.bean;

import java.util.ArrayList;

public class RecordResponse {
    private ArrayList<RecordBean> record;
    private RecordsInfo redCount;

    public RecordsInfo getRedCount() {
        return this.redCount;
    }

    public void setRedCount(RecordsInfo redCount2) {
        this.redCount = redCount2;
    }

    public ArrayList<RecordBean> getRecord() {
        return this.record;
    }

    public void setRecord(ArrayList<RecordBean> record2) {
        this.record = record2;
    }
}

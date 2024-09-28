package im.bclpbkiauv.ui.hui.packet.bean;

import java.util.ArrayList;

public class RedpacketDetailResponse {
    private ArrayList<RedpacketDetailRecord> record;
    private RedpacketBean red;

    public RedpacketBean getRed() {
        return this.red;
    }

    public ArrayList<RedpacketDetailRecord> getRecord() {
        return this.record;
    }
}

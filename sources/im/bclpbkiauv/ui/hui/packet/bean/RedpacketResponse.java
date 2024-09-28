package im.bclpbkiauv.ui.hui.packet.bean;

import im.bclpbkiauv.messenger.UserConfig;
import java.util.ArrayList;
import java.util.Iterator;

public class RedpacketResponse {
    private ArrayList<RedpacketDetailRecord> records;
    private RedpacketBean red;
    private int selfStatus = -1;
    private int statusRecv = -1;
    private ArrayList<Integer> userList;

    public RedpacketBean getRed() {
        return this.red;
    }

    public void setRed(RedpacketBean red2) {
        this.red = red2;
    }

    public boolean isReceived() {
        ArrayList<RedpacketDetailRecord> arrayList;
        ArrayList<Integer> arrayList2 = this.userList;
        if ((arrayList2 == null || arrayList2.size() == 0) && ((arrayList = this.records) == null || arrayList.size() == 0)) {
            return false;
        }
        boolean finish = this.red.getStatusInt() == 1 || this.red.getStatusInt() == 2;
        if (finish) {
            int i = this.selfStatus;
            if (i == 1) {
                return true;
            }
            if (i == 0) {
                return false;
            }
        }
        ArrayList<Integer> arrayList3 = this.userList;
        if (arrayList3 != null && arrayList3.size() > 0) {
            Iterator<Integer> it = this.userList.iterator();
            while (it.hasNext()) {
                if (it.next().intValue() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                    if (finish) {
                        this.selfStatus = 1;
                    }
                    return true;
                }
            }
        }
        ArrayList<RedpacketDetailRecord> arrayList4 = this.records;
        if (arrayList4 != null && arrayList4.size() > 0) {
            Iterator<RedpacketDetailRecord> it2 = this.records.iterator();
            while (it2.hasNext()) {
                if (it2.next().getUserIdInt() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                    if (finish) {
                        this.selfStatus = 1;
                    }
                    return true;
                }
            }
        }
        if (finish) {
            this.selfStatus = 0;
        }
        return false;
    }
}

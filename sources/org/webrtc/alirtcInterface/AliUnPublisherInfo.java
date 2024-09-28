package org.webrtc.alirtcInterface;

public class AliUnPublisherInfo {
    public String call_id;
    public String session;
    public String user_id;

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id2) {
        this.user_id = user_id2;
    }

    public String getSession() {
        return this.session;
    }

    public void setSession(String session2) {
        this.session = session2;
    }

    public String getCall_id() {
        return this.call_id;
    }

    public void setCall_id(String call_id2) {
        this.call_id = call_id2;
    }
}

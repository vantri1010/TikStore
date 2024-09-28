package org.webrtc.alirtcInterface;

public class AliSubscriberInfo {
    public String session;
    public String stream_type;
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

    public String getStream_type() {
        return this.stream_type;
    }

    public void setStream_type(String stream_type2) {
        this.stream_type = stream_type2;
    }
}

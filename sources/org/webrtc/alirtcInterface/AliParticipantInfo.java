package org.webrtc.alirtcInterface;

public class AliParticipantInfo {
    public String session;
    public String user_id;
    public String user_name;

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

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name2) {
        this.user_name = user_name2;
    }
}

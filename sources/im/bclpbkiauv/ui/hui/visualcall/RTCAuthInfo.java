package im.bclpbkiauv.ui.hui.visualcall;

import java.io.Serializable;

public class RTCAuthInfo implements Serializable {
    public int code;
    public RTCAuthInfo_Data data;
    public int server;

    public static class RTCAuthInfo_Data implements Serializable {
        public String appid;
        public String[] gslb;
        public String key;
        public String nonce;
        public long timestamp;
        public String token;
        public RTCAuthInfo_Data_Turn turn;
        public String userid;

        public String getAppid() {
            return this.appid;
        }

        public void setAppid(String appid2) {
            this.appid = appid2;
        }

        public String getUserid() {
            return this.userid;
        }

        public void setUserid(String userid2) {
            this.userid = userid2;
        }

        public String getNonce() {
            return this.nonce;
        }

        public void setNonce(String nonce2) {
            this.nonce = nonce2;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public void setTimestamp(long timestamp2) {
            this.timestamp = timestamp2;
        }

        public String getToken() {
            return this.token;
        }

        public void setToken(String token2) {
            this.token = token2;
        }

        public RTCAuthInfo_Data_Turn getTurn() {
            return this.turn;
        }

        public void setTurn(RTCAuthInfo_Data_Turn turn2) {
            this.turn = turn2;
        }

        public String[] getGslb() {
            return this.gslb;
        }

        public void setGslb(String[] gslb2) {
            this.gslb = gslb2;
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key2) {
            this.key = key2;
        }

        public static class RTCAuthInfo_Data_Turn implements Serializable {
            public String password;
            public String username;

            public String getUsername() {
                return this.username;
            }

            public void setUsername(String username2) {
                this.username = username2;
            }

            public String getPassword() {
                return this.password;
            }

            public void setPassword(String password2) {
                this.password = password2;
            }
        }
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code2) {
        this.code = code2;
    }

    public RTCAuthInfo_Data getData() {
        return this.data;
    }

    public void setData(RTCAuthInfo_Data data2) {
        this.data = data2;
    }

    public int getServer() {
        return this.server;
    }

    public void setServer(int server2) {
        this.server = server2;
    }
}

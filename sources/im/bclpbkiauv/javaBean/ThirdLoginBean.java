package im.bclpbkiauv.javaBean;

import java.io.Serializable;

public class ThirdLoginBean implements Serializable {
    private int code;
    private String msg;
    private UserinfoBean userinfo;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code2) {
        this.code = code2;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg2) {
        this.msg = msg2;
    }

    public UserinfoBean getUserinfo() {
        return this.userinfo;
    }

    public void setUserinfo(UserinfoBean userinfo2) {
        this.userinfo = userinfo2;
    }

    public static class UserinfoBean {
        private String avatar;
        private String nickname;
        private String username;

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username2) {
            this.username = username2;
        }

        public String getAvatar() {
            return this.avatar;
        }

        public void setAvatar(String avatar2) {
            this.avatar = avatar2;
        }

        public String getNickname() {
            return this.nickname;
        }

        public void setNickname(String nickname2) {
            this.nickname = nickname2;
        }
    }
}

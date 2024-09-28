package im.bclpbkiauv.javaBean.fc;

import com.bjz.comm.net.bean.RespFcListBean;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import java.io.Serializable;

@Table("fclist_user")
public class ResponseFclistBeanUser implements Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    private long ForumID;
    private RespFcListBean data;
    private boolean isUser;

    public RespFcListBean getData() {
        return this.data;
    }

    public long getForumID() {
        return this.ForumID;
    }

    public void setForumID(long forumID) {
        this.ForumID = forumID;
    }

    public void setData(RespFcListBean data2) {
        this.data = data2;
    }

    public boolean isUser() {
        return this.isUser;
    }

    public void setUser(boolean user) {
        this.isUser = user;
    }
}

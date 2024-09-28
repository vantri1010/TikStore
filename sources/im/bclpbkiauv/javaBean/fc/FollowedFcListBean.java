package im.bclpbkiauv.javaBean.fc;

import com.bjz.comm.net.bean.RespFcListBean;
import com.litesuits.orm.db.annotation.Table;

@Table("fc_list_followed")
public class FollowedFcListBean extends RespFcListBean {
    public FollowedFcListBean() {
    }

    public FollowedFcListBean(RespFcListBean response) {
        super(response);
    }
}

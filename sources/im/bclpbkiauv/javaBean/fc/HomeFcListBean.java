package im.bclpbkiauv.javaBean.fc;

import com.bjz.comm.net.bean.RespFcListBean;
import com.litesuits.orm.db.annotation.Table;

@Table("fc_list_home")
public class HomeFcListBean extends RespFcListBean {
    public HomeFcListBean() {
    }

    public HomeFcListBean(RespFcListBean response) {
        super(response);
    }
}

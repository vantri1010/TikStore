package im.bclpbkiauv.javaBean.fc;

import com.bjz.comm.net.bean.RespFcListBean;
import com.litesuits.orm.db.annotation.Table;

@Table("fc_list_recommend")
public class RecommendFcListBean extends RespFcListBean {
    public RecommendFcListBean() {
    }

    public RecommendFcListBean(RespFcListBean response) {
        super(response);
    }
}

package im.bclpbkiauv.ui.hui.friendscircle_v1.listener;

import android.view.View;
import com.bjz.comm.net.bean.RespFcListBean;
import im.bclpbkiauv.ui.actionbar.BaseFragment;

public interface FcItemActionClickListener {
    void onAction(View view, int i, int i2, Object obj);

    void onPresentFragment(BaseFragment baseFragment);

    void onReplyClick(View view, String str, RespFcListBean respFcListBean, int i, int i2, boolean z);
}

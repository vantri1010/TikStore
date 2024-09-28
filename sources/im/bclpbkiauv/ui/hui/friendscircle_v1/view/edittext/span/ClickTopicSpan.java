package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span;

import android.view.View;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.bean.TopicBean;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanTopicCallBack;

public class ClickTopicSpan extends ClickAtUserSpan {
    private SpanTopicCallBack spanTopicCallBack;
    private TopicBean topicModel;

    public ClickTopicSpan(TopicBean topicModel2, int color, SpanTopicCallBack spanTopicCallBack2) {
        super((FCEntitysResponse) null, color, (SpanAtUserCallBack) null);
        this.topicModel = topicModel2;
        this.spanTopicCallBack = spanTopicCallBack2;
    }

    public void onClick(View view) {
        super.onClick(view);
        SpanTopicCallBack spanTopicCallBack2 = this.spanTopicCallBack;
        if (spanTopicCallBack2 != null) {
            spanTopicCallBack2.onClick(view, this.topicModel);
        }
    }
}

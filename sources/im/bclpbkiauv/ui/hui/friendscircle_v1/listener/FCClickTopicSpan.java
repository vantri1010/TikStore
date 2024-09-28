package im.bclpbkiauv.ui.hui.friendscircle_v1.listener;

import android.text.TextPaint;
import com.bjz.comm.net.bean.TopicBean;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanTopicCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickTopicSpan;

public class FCClickTopicSpan extends ClickTopicSpan {
    public FCClickTopicSpan(TopicBean topicBean, int color, SpanTopicCallBack spanTopicCallBack) {
        super(topicBean, color, spanTopicCallBack);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
    }
}

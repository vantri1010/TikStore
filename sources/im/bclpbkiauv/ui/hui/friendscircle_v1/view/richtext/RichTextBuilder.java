package im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext;

import android.content.Context;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.widget.TextView;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.bean.TopicBean;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.ITextViewShow;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanCreateListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanTopicCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanUrlCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickAtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickTopicSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.LinkSpan;
import java.util.List;

public class RichTextBuilder {
    private int atColor = -16776961;
    private String content = "";
    private Context context;
    /* access modifiers changed from: private */
    public int emojiSize = 0;
    private int linkColor = -16776961;
    private List<TopicBean> listTopic;
    private List<FCEntitysResponse> listUser;
    private boolean needNum = false;
    private boolean needUrl = false;
    private SpanAtUserCallBack spanAtUserCallBack;
    /* access modifiers changed from: private */
    public SpanCreateListener spanCreateListener;
    private SpanTopicCallBack spanTopicCallBack;
    private SpanUrlCallBack spanUrlCallBack;
    /* access modifiers changed from: private */
    public TextView textView;
    private int topicColor = -16776961;
    /* access modifiers changed from: private */
    public int verticalAlignment = 0;

    public RichTextBuilder(Context context2) {
        this.context = context2;
    }

    public RichTextBuilder setContent(String content2) {
        this.content = content2;
        return this;
    }

    public RichTextBuilder setListUser(List<FCEntitysResponse> listUser2) {
        this.listUser = listUser2;
        return this;
    }

    public RichTextBuilder setListTopic(List<TopicBean> listTopic2) {
        this.listTopic = listTopic2;
        return this;
    }

    public RichTextBuilder setTextView(TextView textView2) {
        this.textView = textView2;
        return this;
    }

    public RichTextBuilder setAtColor(int atColor2) {
        this.atColor = atColor2;
        return this;
    }

    public RichTextBuilder setTopicColor(int topicColor2) {
        this.topicColor = topicColor2;
        return this;
    }

    public RichTextBuilder setLinkColor(int linkColor2) {
        this.linkColor = linkColor2;
        return this;
    }

    public RichTextBuilder setNeedNum(boolean needNum2) {
        this.needNum = needNum2;
        return this;
    }

    public RichTextBuilder setNeedUrl(boolean needUrl2) {
        this.needUrl = needUrl2;
        return this;
    }

    public RichTextBuilder setSpanAtUserCallBack(SpanAtUserCallBack spanAtUserCallBack2) {
        this.spanAtUserCallBack = spanAtUserCallBack2;
        return this;
    }

    public RichTextBuilder setSpanUrlCallBack(SpanUrlCallBack spanUrlCallBack2) {
        this.spanUrlCallBack = spanUrlCallBack2;
        return this;
    }

    public RichTextBuilder setSpanTopicCallBack(SpanTopicCallBack spanTopicCallBack2) {
        this.spanTopicCallBack = spanTopicCallBack2;
        return this;
    }

    public RichTextBuilder setEmojiSize(int emojiSize2) {
        this.emojiSize = emojiSize2;
        return this;
    }

    public RichTextBuilder setVerticalAlignment(int verticalAlignment2) {
        this.verticalAlignment = verticalAlignment2;
        return this;
    }

    public RichTextBuilder setSpanCreateListener(SpanCreateListener spanCreateListener2) {
        this.spanCreateListener = spanCreateListener2;
        return this;
    }

    public Spannable buildSpan(ITextViewShow iTextViewShow) {
        Context context2 = this.context;
        if (context2 != null) {
            return TextCommonUtils.getAllSpanText(context2, this.content, this.listUser, this.listTopic, iTextViewShow, this.atColor, this.linkColor, this.topicColor, this.needNum, this.needUrl, this.spanAtUserCallBack, this.spanUrlCallBack, this.spanTopicCallBack);
        }
        throw new IllegalStateException("context could not be null.");
    }

    public void build() {
        if (this.context == null) {
            throw new IllegalStateException("context could not be null.");
        } else if (this.textView != null) {
            this.textView.setText(TextCommonUtils.getAllSpanText(this.context, this.content, this.listUser, this.listTopic, new ITextViewShow() {
                public void setText(CharSequence charSequence) {
                    RichTextBuilder.this.textView.setText(charSequence);
                }

                public CharSequence getText() {
                    return RichTextBuilder.this.textView.getText();
                }

                public void setMovementMethod(MovementMethod movementMethod) {
                    RichTextBuilder.this.textView.setMovementMethod(movementMethod);
                }

                public void setAutoLinkMask(int flag) {
                    RichTextBuilder.this.textView.setAutoLinkMask(flag);
                }

                public ClickAtUserSpan getCustomClickAtUserSpan(Context context, FCEntitysResponse userModel, int color, SpanAtUserCallBack spanClickCallBack) {
                    if (RichTextBuilder.this.spanCreateListener != null) {
                        return RichTextBuilder.this.spanCreateListener.getCustomClickAtUserSpan(context, userModel, color, spanClickCallBack);
                    }
                    return null;
                }

                public ClickTopicSpan getCustomClickTopicSpan(Context context, TopicBean topicModel, int color, SpanTopicCallBack spanTopicCallBack) {
                    if (RichTextBuilder.this.spanCreateListener != null) {
                        return RichTextBuilder.this.spanCreateListener.getCustomClickTopicSpan(context, topicModel, color, spanTopicCallBack);
                    }
                    return null;
                }

                public LinkSpan getCustomLinkSpan(Context context, String url, int color, SpanUrlCallBack spanUrlCallBack) {
                    if (RichTextBuilder.this.spanCreateListener != null) {
                        return RichTextBuilder.this.spanCreateListener.getCustomLinkSpan(context, url, color, spanUrlCallBack);
                    }
                    return null;
                }

                public int emojiSize() {
                    return RichTextBuilder.this.emojiSize;
                }

                public int verticalAlignment() {
                    return RichTextBuilder.this.verticalAlignment;
                }
            }, this.atColor, this.linkColor, this.topicColor, this.needNum, this.needUrl, this.spanAtUserCallBack, this.spanUrlCallBack, this.spanTopicCallBack));
        } else {
            throw new IllegalStateException("textView could not be null.");
        }
    }
}

package im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.bean.TopicBean;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanCreateListener;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanTopicCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanUrlCallBack;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RichTextView extends ColorTextView {
    private int atColor = -16776961;
    private long downTime;
    private int emojiSize = 0;
    private int emojiVerticalAlignment = 0;
    private int linkColor = -16776961;
    private List<FCEntitysResponse> nameList = new ArrayList();
    private boolean needNumberShow = true;
    private boolean needUrlShow = true;
    private SpanAtUserCallBack spanAtUserCallBack = new SpanAtUserCallBack() {
        public void onPresentFragment(BaseFragment baseFragment) {
            if (RichTextView.this.spanAtUserCallBackListener != null) {
                RichTextView.this.spanAtUserCallBackListener.onPresentFragment(baseFragment);
            }
        }
    };
    /* access modifiers changed from: private */
    public SpanAtUserCallBack spanAtUserCallBackListener;
    private SpanCreateListener spanCreateListener;
    private SpanTopicCallBack spanTopicCallBack = new SpanTopicCallBack() {
        public void onClick(View view, TopicBean TopicBean) {
            if (RichTextView.this.spanTopicCallBackListener != null) {
                RichTextView.this.spanTopicCallBackListener.onClick(view, TopicBean);
            }
        }
    };
    /* access modifiers changed from: private */
    public SpanTopicCallBack spanTopicCallBackListener;
    private SpanUrlCallBack spanUrlCallBack = new SpanUrlCallBack() {
        public void phone(View view, String phone) {
            if (RichTextView.this.spanUrlCallBackListener != null) {
                RichTextView.this.spanUrlCallBackListener.phone(view, phone);
            }
        }

        public void url(View view, String url) {
            if (RichTextView.this.spanUrlCallBackListener != null) {
                RichTextView.this.spanUrlCallBackListener.url(view, url);
            }
        }
    };
    /* access modifiers changed from: private */
    public SpanUrlCallBack spanUrlCallBackListener;
    private int topicColor = -16776961;
    private List<TopicBean> topicList = new ArrayList();

    public RichTextView(Context context) {
        super(context);
        init(context, (AttributeSet) null);
    }

    public RichTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (!isInEditMode() && attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RichTextView);
            this.needNumberShow = array.getBoolean(5, false);
            this.needUrlShow = array.getBoolean(6, false);
            this.atColor = array.getColor(0, -16776961);
            this.topicColor = array.getColor(12, -16776961);
            this.linkColor = array.getColor(3, -16776961);
            this.emojiSize = array.getInteger(1, 0);
            this.emojiVerticalAlignment = array.getInteger(2, 0);
            array.recycle();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        StaticLayout layout = null;
        Field field = null;
        try {
            Field staticField = DynamicLayout.class.getDeclaredField("sStaticLayout");
            staticField.setAccessible(true);
            layout = (StaticLayout) staticField.get(DynamicLayout.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        if (layout != null) {
            try {
                field = StaticLayout.class.getDeclaredField("mMaximumVisibleLineCount");
                field.setAccessible(true);
                field.setInt(layout, getMaxLines());
            } catch (NoSuchFieldException e3) {
                e3.printStackTrace();
            } catch (IllegalAccessException e4) {
                e4.printStackTrace();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (layout != null && field != null) {
            try {
                field.setInt(layout, Integer.MAX_VALUE);
            } catch (IllegalAccessException e5) {
                e5.printStackTrace();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            this.downTime = System.currentTimeMillis();
        }
        if (action == 1) {
            long interval = System.currentTimeMillis() - this.downTime;
            int x = ((int) event.getX()) - getTotalPaddingLeft();
            int y = ((int) event.getY()) - getTotalPaddingTop();
            int x2 = x + getScrollX();
            int y2 = y + getScrollY();
            Layout layout = getLayout();
            int line = layout.getLineForVertical(y2);
            int off = layout.getOffsetForHorizontal(line, (float) x2);
            if (getText() instanceof Spannable) {
                ClickableSpan[] link = (ClickableSpan[]) ((Spannable) getText()).getSpans(off, off, ClickableSpan.class);
                if (link.length == 0) {
                } else if (interval < ((long) ViewConfiguration.getLongPressTimeout())) {
                    int i = action;
                    if (((float) x2) <= getPaint().measureText(getText().subSequence(layout.getLineStart(line), layout.getLineEnd(line)).toString())) {
                        link[0].onClick(this);
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void resolveRichShow(String content) {
        new RichTextBuilder(getContext()).setContent(content).setAtColor(this.atColor).setLinkColor(this.linkColor).setTopicColor(this.topicColor).setListUser(this.nameList).setListTopic(this.topicList).setNeedNum(this.needNumberShow).setNeedUrl(this.needUrlShow).setTextView(this).setEmojiSize(this.emojiSize).setSpanAtUserCallBack(this.spanAtUserCallBack).setSpanUrlCallBack(this.spanUrlCallBack).setSpanTopicCallBack(this.spanTopicCallBack).setVerticalAlignment(this.emojiVerticalAlignment).setSpanCreateListener(this.spanCreateListener).build();
    }

    public void setRichTextUser(String text, List<FCEntitysResponse> nameList2) {
        setRichText(text, nameList2, this.topicList);
    }

    public void setRichTextTopic(String text, List<TopicBean> topicList2) {
        setRichText(text, this.nameList, topicList2);
    }

    public void setRichText(String text, List<FCEntitysResponse> nameList2, List<TopicBean> topicList2) {
        if (nameList2 != null) {
            this.nameList = nameList2;
        }
        if (topicList2 != null) {
            this.topicList = topicList2;
        }
        resolveRichShow(text);
    }

    public void setRichText(String text) {
        setRichText(text, this.nameList, this.topicList);
    }

    public boolean isNeedNumberShow() {
        return this.needNumberShow;
    }

    public List<TopicBean> getTopicList() {
        return this.topicList;
    }

    public void setTopicList(List<TopicBean> topicList2) {
        this.topicList = topicList2;
    }

    public List<FCEntitysResponse> getNameList() {
        return this.nameList;
    }

    public void setNameList(List<FCEntitysResponse> nameList2) {
        this.nameList = nameList2;
    }

    public void setNeedNumberShow(boolean needNumberShow2) {
        this.needNumberShow = needNumberShow2;
    }

    public boolean isNeedUrlShow() {
        return this.needUrlShow;
    }

    public void setNeedUrlShow(boolean needUrlShow2) {
        this.needUrlShow = needUrlShow2;
    }

    public void setSpanUrlCallBackListener(SpanUrlCallBack spanUrlCallBackListener2) {
        this.spanUrlCallBackListener = spanUrlCallBackListener2;
    }

    public void setSpanAtUserCallBackListener(SpanAtUserCallBack spanAtUserCallBackListener2) {
        this.spanAtUserCallBackListener = spanAtUserCallBackListener2;
    }

    public void setSpanCreateListener(SpanCreateListener spanCreateListener2) {
        this.spanCreateListener = spanCreateListener2;
    }

    public void setSpanTopicCallBackListener(SpanTopicCallBack spanTopicCallBackListener2) {
        this.spanTopicCallBackListener = spanTopicCallBackListener2;
    }

    public int getAtColor() {
        return this.atColor;
    }

    public void setAtColor(int atColor2) {
        this.atColor = atColor2;
    }

    public int getTopicColor() {
        return this.topicColor;
    }

    public void setTopicColor(int topicColor2) {
        this.topicColor = topicColor2;
    }

    public int getLinkColor() {
        return this.linkColor;
    }

    public void setLinkColor(int linkColor2) {
        this.linkColor = linkColor2;
    }

    public void setEmojiSize(int emojiSize2) {
        this.emojiSize = emojiSize2;
    }

    public void setEmojiVerticalAlignment(int emojiVerticalAlignment2) {
        this.emojiVerticalAlignment = emojiVerticalAlignment2;
    }

    public int getEmojiVerticalAlignment() {
        return this.emojiVerticalAlignment;
    }
}

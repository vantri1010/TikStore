package im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.expandViewModel.ExpandableStatusFix;
import com.bjz.comm.net.expandViewModel.LinkType;
import com.bjz.comm.net.expandViewModel.StatusType;
import com.google.android.exoplayer2.C;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView.model.FormatData;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView.model.UUIDUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpandableTextView extends AppCompatTextView {
    public static final String DEFAULT_CONTENT = "                                                                                                                                                                                                                                                                                                                           ";
    private static final int DEF_MAX_LINE = 4;
    public static final String IMAGE_TARGET = "图";
    public static final String Space = " ";
    public static final String TARGET = (IMAGE_TARGET + TEXT_TARGET);
    public static String TEXT_CONTRACT = "\n\n收起";
    public static String TEXT_EXPEND = "\n\n展开";
    public static String TEXT_TARGET = "网页链接";
    public static final String regexp_mention = "@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}";
    private static int retryTime = 0;
    public static final String self_regex = "\\[([^\\[]*)\\]\\(([^\\(]*)\\)";
    public static final String urlPatternStr = "((http|ftp|https|rtsp)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})?(/[a-zA-Z0-9\\&\\%_\\./-~-]*)?(\\?([一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\=[一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\&?)+)?";
    /* access modifiers changed from: private */
    public int currentLines;
    boolean dontConsumeNonUrlClicks;
    private ArrayList<FCEntitysResponse> entitys;
    /* access modifiers changed from: private */
    public OnExpandOrContractClickListener expandOrContractClickListener;
    /* access modifiers changed from: private */
    public boolean isAttached;
    /* access modifiers changed from: private */
    public OnLinkClickListener linkClickListener;
    boolean linkHit;
    /* access modifiers changed from: private */
    public CharSequence mContent;
    /* access modifiers changed from: private */
    public Context mContext;
    private String mContractString;
    /* access modifiers changed from: private */
    public int mContractTextColor;
    private DynamicLayout mDynamicLayout;
    private String mEndExpandContent;
    private int mEndExpandTextColor;
    private String mExpandString;
    /* access modifiers changed from: private */
    public int mExpandTextColor;
    private FormatData mFormatData;
    /* access modifiers changed from: private */
    public int mLimitLines;
    /* access modifiers changed from: private */
    public int mLineCount;
    /* access modifiers changed from: private */
    public int mLinkTextColor;
    /* access modifiers changed from: private */
    public int mMentionTextColor;
    /* access modifiers changed from: private */
    public ExpandableStatusFix mModel;
    private boolean mNeedAlwaysShowRight;
    private boolean mNeedAnimation;
    /* access modifiers changed from: private */
    public boolean mNeedContract;
    private boolean mNeedConvertUrl;
    private boolean mNeedExpend;
    private boolean mNeedLink;
    private boolean mNeedMention;
    private boolean mNeedSelf;
    private TextPaint mPaint;
    /* access modifiers changed from: private */
    public int mSelfTextColor;
    private int mWidth;
    /* access modifiers changed from: private */
    public boolean needRealExpandOrContract;
    private OnGetLineCountListener onGetLineCountListener;

    public interface OnExpandOrContractClickListener {
        void onClick(StatusType statusType);
    }

    public interface OnGetLineCountListener {
        void onGetLineCount(int i, boolean z);
    }

    public interface OnLinkClickListener {
        void onLinkClickListener(LinkType linkType, String str, String str2, FCEntitysResponse fCEntitysResponse);
    }

    static /* synthetic */ int access$208() {
        int i = retryTime;
        retryTime = i + 1;
        return i;
    }

    public ExpandableTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.needRealExpandOrContract = true;
        this.mNeedContract = true;
        this.mNeedExpend = true;
        this.mNeedConvertUrl = true;
        this.mNeedMention = true;
        this.mNeedLink = true;
        this.mNeedSelf = false;
        this.mNeedAlwaysShowRight = false;
        this.mNeedAnimation = true;
        this.dontConsumeNonUrlClicks = true;
        init(context, attrs, defStyleAttr);
        setMovementMethod(LocalLinkMovementMethod.getInstance());
        addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View v) {
                if (!ExpandableTextView.this.isAttached) {
                    ExpandableTextView.this.doSetContent();
                }
                boolean unused = ExpandableTextView.this.isAttached = true;
            }

            public void onViewDetachedFromWindow(View v) {
            }
        });
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TEXT_CONTRACT = "\n\n收起";
        TEXT_EXPEND = "\n\n展开";
        TEXT_TARGET = "网页链接";
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyleAttr, 0);
            this.mLimitLines = a.getInt(7, 4);
            this.mNeedExpend = a.getBoolean(13, true);
            this.mNeedContract = a.getBoolean(11, false);
            this.mNeedAnimation = a.getBoolean(10, true);
            this.mNeedSelf = a.getBoolean(16, false);
            this.mNeedMention = a.getBoolean(15, true);
            this.mNeedLink = a.getBoolean(14, true);
            this.mNeedAlwaysShowRight = a.getBoolean(9, false);
            this.mNeedConvertUrl = a.getBoolean(12, true);
            this.mContractString = a.getString(1);
            String string = a.getString(4);
            this.mExpandString = string;
            if (TextUtils.isEmpty(string)) {
                this.mExpandString = TEXT_EXPEND;
            }
            if (TextUtils.isEmpty(this.mContractString)) {
                this.mContractString = TEXT_CONTRACT;
            }
            this.mExpandTextColor = a.getColor(3, Color.parseColor("#FF09A4C9"));
            this.mEndExpandTextColor = a.getColor(3, Color.parseColor("#333333"));
            this.mContractTextColor = a.getColor(0, Color.parseColor("#FF09A4C9"));
            this.mLinkTextColor = a.getColor(5, Color.parseColor("#FF09A4C9"));
            this.mSelfTextColor = a.getColor(17, Color.parseColor("#FF09A4C9"));
            this.mMentionTextColor = a.getColor(8, Color.parseColor("#FF09A4C9"));
            this.currentLines = this.mLimitLines;
            a.recycle();
        }
        this.mContext = context;
        TextPaint paint = getPaint();
        this.mPaint = paint;
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /* access modifiers changed from: private */
    public SpannableStringBuilder setRealContent(CharSequence content) {
        this.mFormatData = formatData(content);
        try {
            this.mDynamicLayout = new DynamicLayout(this.mFormatData.getFormatedContent(), this.mPaint, this.mWidth, Layout.Alignment.ALIGN_NORMAL, 1.2f, 0.0f, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int lineCount = this.mDynamicLayout.getLineCount();
        this.mLineCount = lineCount;
        OnGetLineCountListener onGetLineCountListener2 = this.onGetLineCountListener;
        if (onGetLineCountListener2 != null) {
            onGetLineCountListener2.onGetLineCount(lineCount, lineCount > this.mLimitLines);
        }
        if (!this.mNeedExpend || this.mLineCount <= this.mLimitLines) {
            return dealLink(this.mFormatData, false);
        }
        return dealLink(this.mFormatData, true);
    }

    public void setEndExpendContent(String endExpendContent) {
        this.mEndExpandContent = endExpendContent;
    }

    public void setContent(String content) {
        this.mContent = content;
        if (this.isAttached) {
            doSetContent();
        }
    }

    /* access modifiers changed from: private */
    public void doSetContent() {
        if (this.mContent != null) {
            this.currentLines = this.mLimitLines;
            if (this.mWidth <= 0 && getWidth() > 0) {
                this.mWidth = (getWidth() - getPaddingLeft()) - getPaddingRight();
            }
            if (this.mWidth <= 0) {
                if (retryTime > 10) {
                    setText(DEFAULT_CONTENT);
                }
                post(new Runnable() {
                    public void run() {
                        ExpandableTextView.access$208();
                        ExpandableTextView expandableTextView = ExpandableTextView.this;
                        expandableTextView.setContent(expandableTextView.mContent.toString());
                    }
                });
                return;
            }
            setRealContent(this.mContent.toString());
        }
    }

    private String getExpandEndContent() {
        if (TextUtils.isEmpty(this.mEndExpandContent)) {
            return String.format(Locale.getDefault(), "  %s", new Object[]{this.mContractString});
        }
        return String.format(Locale.getDefault(), "  %s  %s", new Object[]{this.mEndExpandContent, this.mContractString});
    }

    private String getHideEndContent() {
        if (TextUtils.isEmpty(this.mEndExpandContent)) {
            Locale locale = Locale.getDefault();
            boolean z = this.mNeedAlwaysShowRight;
            return String.format(locale, "  %s", new Object[]{this.mExpandString});
        }
        Locale locale2 = Locale.getDefault();
        boolean z2 = this.mNeedAlwaysShowRight;
        return String.format(locale2, "  %s  %s", new Object[]{this.mEndExpandContent, this.mExpandString});
    }

    private SpannableStringBuilder dealLink(FormatData formatData, boolean ignoreMore) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ExpandableStatusFix expandableStatusFix = this.mModel;
        if (!(expandableStatusFix == null || expandableStatusFix.getStatusType() == null)) {
            boolean isHide = false;
            if (this.mModel.getStatusType() != null) {
                if (this.mModel.getStatusType().equals(StatusType.STATUS_CONTRACT)) {
                    isHide = true;
                } else {
                    isHide = false;
                }
            }
            if (isHide) {
                int i = this.mLimitLines;
                this.currentLines = i + (this.mLineCount - i);
            } else if (this.mNeedContract) {
                this.currentLines = this.mLimitLines;
            }
        }
        if (ignoreMore) {
            int i2 = this.currentLines;
            if (i2 < this.mLineCount) {
                int index = i2 - 1;
                int endPosition = this.mDynamicLayout.getLineEnd(index);
                int startPosition = this.mDynamicLayout.getLineStart(index);
                float lineWidth = this.mDynamicLayout.getLineWidth(index);
                String endString = getHideEndContent();
                String endString2 = endString;
                String substring = formatData.getFormatedContent().substring(0, getFitPosition(endString, endPosition, startPosition, lineWidth, this.mPaint.measureText(endString), 0.0f));
                if (substring.endsWith("\n\n")) {
                    substring = substring.substring(0, substring.length() - "\n\n".length());
                } else if (substring.endsWith("\n")) {
                    substring = substring.substring(0, substring.length() - "\n".length());
                }
                ssb.append(substring);
                if (this.mNeedAlwaysShowRight) {
                    float lastLineWidth = 0.0f;
                    for (int i3 = 0; i3 < index; i3++) {
                        lastLineWidth += this.mDynamicLayout.getLineWidth(i3);
                    }
                    float emptyWidth = ((lastLineWidth / ((float) index)) - lineWidth) - this.mPaint.measureText(endString2);
                    if (emptyWidth > 0.0f) {
                        int count = 0;
                        while (((float) count) * this.mPaint.measureText(" ") < emptyWidth) {
                            count++;
                        }
                        int count2 = count - 1;
                        for (int i4 = 0; i4 < count2; i4++) {
                            ssb.append(" ");
                        }
                    }
                }
                ssb.append(endString2);
                ssb.setSpan(new ClickableSpan() {
                    public void onClick(View widget) {
                        if (ExpandableTextView.this.needRealExpandOrContract) {
                            if (ExpandableTextView.this.mModel != null) {
                                ExpandableTextView.this.mModel.setStatusType(StatusType.STATUS_CONTRACT);
                                ExpandableTextView expandableTextView = ExpandableTextView.this;
                                expandableTextView.action(expandableTextView.mModel.getStatusType());
                            } else {
                                ExpandableTextView.this.action();
                            }
                        }
                        if (ExpandableTextView.this.expandOrContractClickListener != null) {
                            ExpandableTextView.this.expandOrContractClickListener.onClick(StatusType.STATUS_EXPAND);
                        }
                    }

                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ExpandableTextView.this.mExpandTextColor);
                        ds.setUnderlineText(false);
                    }
                }, (ssb.length() - this.mExpandString.length()) - (TextUtils.isEmpty(this.mEndExpandContent) ? 0 : this.mEndExpandContent.length() + 2), ssb.length(), 17);
            } else {
                ssb.append(formatData.getFormatedContent());
                if (this.mNeedContract) {
                    String endString3 = getExpandEndContent();
                    if (this.mNeedAlwaysShowRight) {
                        int index2 = this.mDynamicLayout.getLineCount() - 1;
                        float lineWidth2 = this.mDynamicLayout.getLineWidth(index2);
                        float lastLineWidth2 = 0.0f;
                        for (int i5 = 0; i5 < index2; i5++) {
                            lastLineWidth2 += this.mDynamicLayout.getLineWidth(i5);
                        }
                        float emptyWidth2 = ((lastLineWidth2 / ((float) index2)) - lineWidth2) - this.mPaint.measureText(endString3);
                        if (emptyWidth2 > 0.0f) {
                            int count3 = 0;
                            while (((float) count3) * this.mPaint.measureText(" ") < emptyWidth2) {
                                count3++;
                            }
                            int count4 = count3 - 1;
                            for (int i6 = 0; i6 < count4; i6++) {
                                ssb.append(" ");
                            }
                        }
                    }
                    ssb.append(endString3);
                    ssb.setSpan(new ClickableSpan() {
                        public void onClick(View widget) {
                            if (ExpandableTextView.this.mModel != null) {
                                ExpandableTextView.this.mModel.setStatusType(StatusType.STATUS_EXPAND);
                                ExpandableTextView expandableTextView = ExpandableTextView.this;
                                expandableTextView.action(expandableTextView.mModel.getStatusType());
                            } else {
                                ExpandableTextView.this.action();
                            }
                            if (ExpandableTextView.this.expandOrContractClickListener != null) {
                                ExpandableTextView.this.expandOrContractClickListener.onClick(StatusType.STATUS_CONTRACT);
                            }
                        }

                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(ExpandableTextView.this.mContractTextColor);
                            ds.setUnderlineText(false);
                        }
                    }, (ssb.length() - this.mContractString.length()) - (TextUtils.isEmpty(this.mEndExpandContent) ? 0 : this.mEndExpandContent.length() + 2), ssb.length(), 17);
                } else if (!TextUtils.isEmpty(this.mEndExpandContent)) {
                    ssb.append(this.mEndExpandContent);
                    ssb.setSpan(new ForegroundColorSpan(this.mEndExpandTextColor), ssb.length() - this.mEndExpandContent.length(), ssb.length(), 17);
                }
            }
        } else {
            ssb.append(formatData.getFormatedContent());
            if (!TextUtils.isEmpty(this.mEndExpandContent)) {
                ssb.append(this.mEndExpandContent);
                ssb.setSpan(new ForegroundColorSpan(this.mEndExpandTextColor), ssb.length() - this.mEndExpandContent.length(), ssb.length(), 17);
            }
        }
        for (FormatData.PositionData data : formatData.getPositionDatas()) {
            if (ssb.length() >= data.getEnd()) {
                if (data.getType().equals(LinkType.LINK_TYPE)) {
                    if (!this.mNeedExpend || !ignoreMore) {
                        addUrl(ssb, data, data.getEnd());
                    } else {
                        int fitPosition = ssb.length() - getHideEndContent().length();
                        if (data.getStart() < fitPosition) {
                            int endPosition2 = data.getEnd();
                            if (this.currentLines < this.mLineCount && fitPosition > data.getStart() + 1 && fitPosition < data.getEnd()) {
                                endPosition2 = fitPosition;
                            }
                            if (data.getStart() + 1 < fitPosition) {
                                addUrl(ssb, data, endPosition2);
                            }
                        }
                    }
                } else if (data.getType().equals(LinkType.MENTION_TYPE)) {
                    if (!this.mNeedExpend || !ignoreMore) {
                        addMention(ssb, data, data.getEnd());
                    } else {
                        int fitPosition2 = ssb.length() - getHideEndContent().length();
                        if (data.getStart() < fitPosition2) {
                            int endPosition3 = data.getEnd();
                            if (this.currentLines < this.mLineCount && fitPosition2 < data.getEnd()) {
                                endPosition3 = fitPosition2;
                            }
                            addMention(ssb, data, endPosition3);
                        }
                    }
                } else if (data.getType().equals(LinkType.SELF)) {
                    if (!this.mNeedExpend || !ignoreMore) {
                        addSelf(ssb, data, data.getEnd());
                    } else {
                        int fitPosition3 = ssb.length() - getHideEndContent().length();
                        if (data.getStart() < fitPosition3) {
                            int endPosition4 = data.getEnd();
                            if (this.currentLines < this.mLineCount && fitPosition3 < data.getEnd()) {
                                endPosition4 = fitPosition3;
                            }
                            addSelf(ssb, data, endPosition4);
                        }
                    }
                }
            }
        }
        setHighlightColor(0);
        setText(ssb);
        return ssb;
    }

    private int getFitSpaceCount(float emptyWidth, float endStringWidth) {
        int count = 0;
        while ((((float) count) * this.mPaint.measureText(" ")) + endStringWidth < emptyWidth) {
            count++;
        }
        return count - 1;
    }

    private void addSelf(SpannableStringBuilder ssb, final FormatData.PositionData data, int endPosition) {
        ssb.setSpan(new ClickableSpan() {
            public void onClick(View widget) {
                if (ExpandableTextView.this.linkClickListener != null) {
                    ExpandableTextView.this.linkClickListener.onLinkClickListener(LinkType.SELF, data.getSelfAim(), data.getSelfContent(), data.getFcEntitysResponse());
                }
            }

            public void updateDrawState(TextPaint ds) {
                ds.setColor(ExpandableTextView.this.mSelfTextColor);
                ds.setUnderlineText(false);
            }
        }, data.getStart(), endPosition, 17);
    }

    private void addMention(SpannableStringBuilder ssb, final FormatData.PositionData data, int endPosition) {
        ssb.setSpan(new ClickableSpan() {
            public void onClick(View widget) {
                if (ExpandableTextView.this.linkClickListener != null) {
                    ExpandableTextView.this.linkClickListener.onLinkClickListener(LinkType.MENTION_TYPE, data.getUrl(), (String) null, data.getFcEntitysResponse());
                }
            }

            public void updateDrawState(TextPaint ds) {
                ds.setColor(ExpandableTextView.this.mMentionTextColor);
                ds.setUnderlineText(false);
            }
        }, data.getStart(), endPosition, 17);
    }

    private void addUrl(SpannableStringBuilder ssb, final FormatData.PositionData data, int endPosition) {
        ssb.setSpan(new ClickableSpan() {
            public void onClick(View widget) {
                if (ExpandableTextView.this.linkClickListener != null) {
                    ExpandableTextView.this.linkClickListener.onLinkClickListener(LinkType.LINK_TYPE, data.getUrl(), (String) null, data.getFcEntitysResponse());
                    return;
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setFlags(C.ENCODING_PCM_MU_LAW);
                intent.setData(Uri.parse(data.getUrl()));
                ExpandableTextView.this.mContext.startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                ds.setColor(ExpandableTextView.this.mLinkTextColor);
                ds.setUnderlineText(false);
            }
        }, data.getStart(), endPosition, 17);
    }

    public void setCurrStatus(StatusType type) {
        action(type);
    }

    /* access modifiers changed from: private */
    public void action() {
        action((StatusType) null);
    }

    /* access modifiers changed from: private */
    public void action(StatusType type) {
        boolean isHide = this.currentLines < this.mLineCount;
        if (type != null) {
            this.mNeedAnimation = false;
        }
        if (this.mNeedAnimation) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            final boolean finalIsHide = isHide;
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float value = (Float) animation.getAnimatedValue();
                    if (finalIsHide) {
                        ExpandableTextView expandableTextView = ExpandableTextView.this;
                        int unused = expandableTextView.currentLines = expandableTextView.mLimitLines + ((int) (((float) (ExpandableTextView.this.mLineCount - ExpandableTextView.this.mLimitLines)) * value.floatValue()));
                    } else if (ExpandableTextView.this.mNeedContract) {
                        ExpandableTextView expandableTextView2 = ExpandableTextView.this;
                        int unused2 = expandableTextView2.currentLines = expandableTextView2.mLimitLines + ((int) (((float) (ExpandableTextView.this.mLineCount - ExpandableTextView.this.mLimitLines)) * (1.0f - value.floatValue())));
                    }
                    ExpandableTextView expandableTextView3 = ExpandableTextView.this;
                    expandableTextView3.setText(expandableTextView3.setRealContent(expandableTextView3.mContent));
                }
            });
            valueAnimator.setDuration(100);
            valueAnimator.start();
            return;
        }
        if (isHide) {
            int i = this.mLimitLines;
            this.currentLines = i + (this.mLineCount - i);
        } else if (this.mNeedContract) {
            this.currentLines = this.mLimitLines;
        }
        setText(setRealContent(this.mContent));
    }

    private int getFitPosition(String endString, int endPosition, int startPosition, float lineWidth, float endStringWith, float offset) {
        int i = startPosition;
        int position = (int) (((lineWidth - (endStringWith + offset)) * ((float) (endPosition - i))) / lineWidth);
        if (position <= endString.length()) {
            return endPosition;
        }
        if (this.mPaint.measureText(this.mFormatData.getFormatedContent().substring(startPosition, i + position)) <= lineWidth - endStringWith) {
            return i + position;
        }
        return getFitPosition(endString, endPosition, startPosition, lineWidth, endStringWith, offset + this.mPaint.measureText(" "));
    }

    private FormatData formatData(CharSequence content) {
        Matcher matcher;
        Pattern pattern;
        Matcher matcher2;
        Pattern pattern2;
        int length;
        ExpandableTextView expandableTextView = this;
        CharSequence charSequence = content;
        FormatData formatData = new FormatData();
        List<FormatData.PositionData> datas = new ArrayList<>();
        ArrayList<FCEntitysResponse> arrayList = expandableTextView.entitys;
        if (arrayList == null || arrayList.size() <= 0) {
            Pattern pattern3 = Pattern.compile(self_regex, 2);
            Matcher matcher3 = pattern3.matcher(charSequence);
            StringBuffer newResult = new StringBuffer();
            int end = 0;
            int temp = 0;
            Map<String, String> convert = new HashMap<>();
            if (expandableTextView.mNeedSelf) {
                List<FormatData.PositionData> datasMention = new ArrayList<>();
                while (matcher3.find()) {
                    int start = matcher3.start();
                    end = matcher3.end();
                    newResult.append(content.toString().substring(temp, start));
                    String result = matcher3.group();
                    if (!TextUtils.isEmpty(result)) {
                        String aimSrt = result.substring(result.indexOf("[") + 1, result.indexOf("]"));
                        pattern2 = pattern3;
                        String contentSrt = result.substring(result.indexOf(SQLBuilder.PARENTHESES_LEFT) + 1, result.indexOf(SQLBuilder.PARENTHESES_RIGHT));
                        String key = UUIDUtils.getUuid(aimSrt.length());
                        matcher2 = matcher3;
                        datasMention.add(new FormatData.PositionData(newResult.length() + 1, newResult.length() + 2 + aimSrt.length(), aimSrt, contentSrt, LinkType.SELF));
                        convert.put(key, aimSrt);
                        newResult.append(" " + key + " ");
                        temp = end;
                    } else {
                        pattern2 = pattern3;
                        matcher2 = matcher3;
                    }
                    CharSequence charSequence2 = content;
                    pattern3 = pattern2;
                    matcher3 = matcher2;
                }
                matcher = matcher3;
                datas.addAll(datasMention);
            } else {
                matcher = matcher3;
            }
            newResult.append(content.toString().substring(end, content.toString().length()));
            CharSequence content2 = newResult.toString();
            StringBuffer newResult2 = new StringBuffer();
            int start2 = 0;
            int end2 = 0;
            int temp2 = 0;
            if (expandableTextView.mNeedLink) {
                Pattern pattern4 = Pattern.compile("((http|ftp|https|rtsp)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})?(/[a-zA-Z0-9\\&\\%_\\./-~-]*)?(\\?([一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\=[一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\&?)+)?");
                Matcher matcher4 = pattern4.matcher(content2);
                while (matcher4.find()) {
                    start2 = matcher4.start();
                    end2 = matcher4.end();
                    newResult2.append(content2.toString().substring(temp2, start2));
                    if (expandableTextView.mNeedConvertUrl) {
                        datas.add(new FormatData.PositionData(newResult2.length() + 1, newResult2.length() + 2 + TARGET.length(), matcher4.group(), LinkType.LINK_TYPE));
                        newResult2.append(" " + TARGET + " ");
                        pattern = pattern4;
                    } else {
                        String result2 = matcher4.group();
                        String key2 = UUIDUtils.getUuid(result2.length());
                        pattern = pattern4;
                        datas.add(new FormatData.PositionData(newResult2.length(), newResult2.length() + 2 + key2.length(), result2, LinkType.LINK_TYPE));
                        convert.put(key2, result2);
                        newResult2.append(" " + key2 + " ");
                    }
                    temp2 = end2;
                    expandableTextView = this;
                    pattern4 = pattern;
                }
                Pattern pattern5 = pattern4;
                int i = start2;
                Matcher matcher5 = matcher4;
            } else {
                Matcher matcher6 = matcher;
            }
            newResult2.append(content2.toString().substring(end2, content2.toString().length()));
            if (!convert.isEmpty()) {
                String resultData = newResult2.toString();
                for (Map.Entry<String, String> entry : convert.entrySet()) {
                    resultData = resultData.replaceAll(entry.getKey(), entry.getValue());
                }
                newResult2 = new StringBuffer(resultData);
            }
            formatData.setFormatedContent(newResult2.toString());
            formatData.setPositionDatas(datas);
        } else {
            int length2 = 0;
            if (!TextUtils.isEmpty(content)) {
                length2 = content.length();
            }
            int i2 = 0;
            while (i2 < expandableTextView.entitys.size()) {
                FCEntitysResponse fcEntitysResponse = expandableTextView.entitys.get(i2);
                if (fcEntitysResponse != null) {
                    String userName = fcEntitysResponse.getUserName();
                    int offset = fcEntitysResponse.getUOffset();
                    int limit = offset + fcEntitysResponse.getULimit();
                    if (TextUtils.isEmpty(userName)) {
                        int limit2 = limit;
                        String str = userName;
                        int offset2 = offset;
                        if (fcEntitysResponse.getType() != 2) {
                            length = length2;
                        } else if (offset2 < 0 || limit2 <= 0 || offset2 + limit2 > length2) {
                            length = length2;
                        } else {
                            length = length2;
                            FormatData.PositionData positionData = r9;
                            FormatData.PositionData positionData2 = new FormatData.PositionData(offset2, limit2, content.toString().substring(offset2, offset2 + limit2), LinkType.LINK_TYPE, fcEntitysResponse);
                            datas.add(positionData);
                        }
                    } else if (offset < 0 || limit <= offset || limit > length2) {
                        String str2 = userName;
                        int i3 = offset;
                        length = length2;
                    } else {
                        FormatData.PositionData positionData3 = r9;
                        int i4 = limit;
                        String str3 = userName;
                        int i5 = offset;
                        FormatData.PositionData positionData4 = new FormatData.PositionData(offset, limit, userName, LinkType.MENTION_TYPE, fcEntitysResponse);
                        datas.add(positionData3);
                        length = length2;
                    }
                } else {
                    length = length2;
                }
                i2++;
                length2 = length;
            }
            if (expandableTextView.mNeedLink != 0) {
                Matcher matcher7 = Pattern.compile("((http|ftp|https|rtsp)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})?(/[a-zA-Z0-9\\&\\%_\\./-~-]*)?(\\?([一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\=[一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\&?)+)?").matcher(charSequence);
                while (matcher7.find()) {
                    datas.add(new FormatData.PositionData(matcher7.start(), matcher7.end(), matcher7.group(), LinkType.LINK_TYPE));
                }
            }
            formatData.setFormatedContent(content.toString());
            formatData.setPositionDatas(datas);
        }
        return formatData;
    }

    class SelfImageSpan extends ImageSpan {
        private Drawable drawable;

        public SelfImageSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
            this.drawable = d;
        }

        public Drawable getDrawable() {
            return this.drawable;
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable b = getDrawable();
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            canvas.save();
            canvas.translate(x, (float) (((((fm.descent + y) + y) + fm.ascent) / 2) - (b.getBounds().bottom / 2)));
            b.draw(canvas);
            canvas.restore();
        }
    }

    public void bind(ExpandableStatusFix model) {
        this.mModel = model;
    }

    public static class LocalLinkMovementMethod extends LinkMovementMethod {
        static LocalLinkMovementMethod sInstance;

        public static LocalLinkMovementMethod getInstance() {
            if (sInstance == null) {
                sInstance = new LocalLinkMovementMethod();
            }
            return sInstance;
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();
            if (action != 1 && action != 0) {
                return Touch.onTouchEvent(widget, buffer, event);
            }
            int x = ((int) event.getX()) - widget.getTotalPaddingLeft();
            int y = ((int) event.getY()) - widget.getTotalPaddingTop();
            int x2 = x + widget.getScrollX();
            int y2 = y + widget.getScrollY();
            Layout layout = widget.getLayout();
            int off = layout.getOffsetForHorizontal(layout.getLineForVertical(y2), (float) x2);
            ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                if (action == 1) {
                    link[0].onClick(widget);
                } else if (action == 0) {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                }
                if (widget instanceof ExpandableTextView) {
                    ((ExpandableTextView) widget).linkHit = true;
                }
                return true;
            }
            Selection.removeSelection(buffer);
            Touch.onTouchEvent(widget, buffer, event);
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        this.linkHit = false;
        boolean res = super.onTouchEvent(event);
        if (this.dontConsumeNonUrlClicks) {
            return this.linkHit;
        }
        if (action == 1) {
            setTextIsSelectable(false);
        }
        return res;
    }

    public OnGetLineCountListener getOnGetLineCountListener() {
        return this.onGetLineCountListener;
    }

    public void setOnGetLineCountListener(OnGetLineCountListener onGetLineCountListener2) {
        this.onGetLineCountListener = onGetLineCountListener2;
    }

    public OnLinkClickListener getLinkClickListener() {
        return this.linkClickListener;
    }

    public void setLinkClickListener(OnLinkClickListener linkClickListener2) {
        this.linkClickListener = linkClickListener2;
    }

    public boolean ismNeedMention() {
        return this.mNeedMention;
    }

    public void setNeedMention(boolean mNeedMention2) {
        this.mNeedMention = mNeedMention2;
    }

    public boolean isNeedContract() {
        return this.mNeedContract;
    }

    public void setNeedContract(boolean mNeedContract2) {
        this.mNeedContract = mNeedContract2;
    }

    public boolean isNeedExpend() {
        return this.mNeedExpend;
    }

    public void setNeedExpend(boolean mNeedExpend2) {
        this.mNeedExpend = mNeedExpend2;
    }

    public boolean isNeedAnimation() {
        return this.mNeedAnimation;
    }

    public void setNeedAnimation(boolean mNeedAnimation2) {
        this.mNeedAnimation = mNeedAnimation2;
    }

    public int getExpandableLineCount() {
        return this.mLineCount;
    }

    public void setExpandableLineCount(int mLineCount2) {
        this.mLineCount = mLineCount2;
    }

    public int getExpandTextColor() {
        return this.mExpandTextColor;
    }

    public void setExpandTextColor(int mExpandTextColor2) {
        this.mExpandTextColor = mExpandTextColor2;
    }

    public int getExpandableLinkTextColor() {
        return this.mLinkTextColor;
    }

    public void setExpandableLinkTextColor(int mLinkTextColor2) {
        this.mLinkTextColor = mLinkTextColor2;
    }

    public int getContractTextColor() {
        return this.mContractTextColor;
    }

    public void setContractTextColor(int mContractTextColor2) {
        this.mContractTextColor = mContractTextColor2;
    }

    public String getExpandString() {
        return this.mExpandString;
    }

    public void setExpandString(String mExpandString2) {
        this.mExpandString = mExpandString2;
    }

    public String getContractString() {
        return this.mContractString;
    }

    public void setContractString(String mContractString2) {
        this.mContractString = mContractString2;
    }

    public int getEndExpandTextColor() {
        return this.mEndExpandTextColor;
    }

    public void setEndExpandTextColor(int mEndExpandTextColor2) {
        this.mEndExpandTextColor = mEndExpandTextColor2;
    }

    public boolean isNeedLink() {
        return this.mNeedLink;
    }

    public void setNeedLink(boolean mNeedLink2) {
        this.mNeedLink = mNeedLink2;
    }

    public int getSelfTextColor() {
        return this.mSelfTextColor;
    }

    public void setSelfTextColor(int mSelfTextColor2) {
        this.mSelfTextColor = mSelfTextColor2;
    }

    public boolean isNeedSelf() {
        return this.mNeedSelf;
    }

    public void setNeedSelf(boolean mNeedSelf2) {
        this.mNeedSelf = mNeedSelf2;
    }

    public boolean isNeedAlwaysShowRight() {
        return this.mNeedAlwaysShowRight;
    }

    public void setNeedAlwaysShowRight(boolean mNeedAlwaysShowRight2) {
        this.mNeedAlwaysShowRight = mNeedAlwaysShowRight2;
    }

    public ArrayList<FCEntitysResponse> getEntitys() {
        return this.entitys;
    }

    public void setEntitys(ArrayList<FCEntitysResponse> entitys2) {
        this.entitys = entitys2;
    }

    public OnExpandOrContractClickListener getExpandOrContractClickListener() {
        return this.expandOrContractClickListener;
    }

    public void setExpandOrContractClickListener(OnExpandOrContractClickListener expandOrContractClickListener2) {
        this.expandOrContractClickListener = expandOrContractClickListener2;
    }

    public void setExpandOrContractClickListener(OnExpandOrContractClickListener expandOrContractClickListener2, boolean needRealExpandOrContract2) {
        this.expandOrContractClickListener = expandOrContractClickListener2;
        this.needRealExpandOrContract = needRealExpandOrContract2;
    }
}

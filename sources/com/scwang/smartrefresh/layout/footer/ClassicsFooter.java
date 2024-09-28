package com.scwang.smartrefresh.layout.footer;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ArrowDrawable;
import com.scwang.smartrefresh.layout.internal.InternalClassics;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.util.SmartUtil;

public class ClassicsFooter extends InternalClassics<ClassicsFooter> implements RefreshFooter {
    public static String REFRESH_FOOTER_FAILED = null;
    public static String REFRESH_FOOTER_FINISH = null;
    public static String REFRESH_FOOTER_LOADING = null;
    public static String REFRESH_FOOTER_NOTHING = null;
    public static String REFRESH_FOOTER_PULLING = null;
    public static String REFRESH_FOOTER_REFRESHING = null;
    public static String REFRESH_FOOTER_RELEASE = null;
    protected boolean mNoMoreData;
    protected String mTextFailed;
    protected String mTextFinish;
    protected String mTextLoading;
    protected String mTextNothing;
    protected String mTextPulling;
    protected String mTextRefreshing;
    protected String mTextRelease;

    public ClassicsFooter(Context context) {
        this(context, (AttributeSet) null);
    }

    public ClassicsFooter(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mNoMoreData = false;
        View.inflate(context, R.layout.srl_classics_footer, this);
        ImageView imageView = (ImageView) findViewById(R.id.srl_classics_arrow);
        this.mArrowView = imageView;
        ImageView imageView2 = (ImageView) findViewById(R.id.srl_classics_progress);
        this.mProgressView = imageView2;
        this.mTitleText = (TextView) findViewById(R.id.srl_classics_title);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsFooter);
        RelativeLayout.LayoutParams lpArrow = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        RelativeLayout.LayoutParams lpProgress = (RelativeLayout.LayoutParams) imageView2.getLayoutParams();
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, SmartUtil.dp2px(20.0f));
        lpArrow.rightMargin = lpProgress.rightMargin;
        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.height);
        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.height);
        this.mFinishDuration = ta.getInt(R.styleable.ClassicsFooter_srlFinishDuration, this.mFinishDuration);
        this.mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.ClassicsFooter_srlClassicsSpinnerStyle, this.mSpinnerStyle.ordinal)];
        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableArrow)) {
            this.mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableArrow));
        } else if (this.mArrowView.getDrawable() == null) {
            this.mArrowDrawable = new ArrowDrawable();
            this.mArrowDrawable.setColor(-10066330);
            this.mArrowView.setImageDrawable(this.mArrowDrawable);
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableProgress)) {
            this.mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableProgress));
        } else if (this.mProgressView.getDrawable() == null) {
            this.mProgressDrawable = new ProgressDrawable();
            this.mProgressDrawable.setColor(-10066330);
            this.mProgressView.setImageDrawable(this.mProgressDrawable);
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextSizeTitle)) {
            this.mTitleText.setTextSize(0, (float) ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlTextSizeTitle, SmartUtil.dp2px(16.0f)));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
            super.setAccentColor(ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextPulling)) {
            this.mTextPulling = ta.getString(R.styleable.ClassicsFooter_srlTextPulling);
        } else {
            String str = REFRESH_FOOTER_PULLING;
            if (str != null) {
                this.mTextPulling = str;
            } else {
                this.mTextPulling = context.getString(R.string.srl_footer_pulling);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextRelease)) {
            this.mTextRelease = ta.getString(R.styleable.ClassicsFooter_srlTextRelease);
        } else {
            String str2 = REFRESH_FOOTER_RELEASE;
            if (str2 != null) {
                this.mTextRelease = str2;
            } else {
                this.mTextRelease = context.getString(R.string.srl_footer_release);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextLoading)) {
            this.mTextLoading = ta.getString(R.styleable.ClassicsFooter_srlTextLoading);
        } else {
            String str3 = REFRESH_FOOTER_LOADING;
            if (str3 != null) {
                this.mTextLoading = str3;
            } else {
                this.mTextLoading = context.getString(R.string.srl_footer_loading);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextRefreshing)) {
            this.mTextRefreshing = ta.getString(R.styleable.ClassicsFooter_srlTextRefreshing);
        } else {
            String str4 = REFRESH_FOOTER_REFRESHING;
            if (str4 != null) {
                this.mTextRefreshing = str4;
            } else {
                this.mTextRefreshing = context.getString(R.string.srl_footer_refreshing);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextFinish)) {
            this.mTextFinish = ta.getString(R.styleable.ClassicsFooter_srlTextFinish);
        } else {
            String str5 = REFRESH_FOOTER_FINISH;
            if (str5 != null) {
                this.mTextFinish = str5;
            } else {
                this.mTextFinish = context.getString(R.string.srl_footer_finish);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextFailed)) {
            this.mTextFailed = ta.getString(R.styleable.ClassicsFooter_srlTextFailed);
        } else {
            String str6 = REFRESH_FOOTER_FAILED;
            if (str6 != null) {
                this.mTextFailed = str6;
            } else {
                this.mTextFailed = context.getString(R.string.srl_footer_failed);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextNothing)) {
            this.mTextNothing = ta.getString(R.styleable.ClassicsFooter_srlTextNothing);
        } else {
            String str7 = REFRESH_FOOTER_NOTHING;
            if (str7 != null) {
                this.mTextNothing = str7;
            } else {
                this.mTextNothing = context.getString(R.string.srl_footer_nothing);
            }
        }
        ta.recycle();
        imageView2.animate().setInterpolator((TimeInterpolator) null);
        this.mTitleText.setText(isInEditMode() ? this.mTextLoading : this.mTextPulling);
        if (isInEditMode()) {
            imageView.setVisibility(8);
        } else {
            imageView2.setVisibility(8);
        }
    }

    public void onStartAnimator(RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (!this.mNoMoreData) {
            super.onStartAnimator(refreshLayout, height, maxDragHeight);
        }
    }

    public int onFinish(RefreshLayout layout, boolean success) {
        if (this.mNoMoreData) {
            return 0;
        }
        this.mTitleText.setText(success ? this.mTextFinish : this.mTextFailed);
        return super.onFinish(layout, success);
    }

    @Deprecated
    public void setPrimaryColors(int... colors) {
        if (this.mSpinnerStyle == SpinnerStyle.FixedBehind) {
            super.setPrimaryColors(colors);
        }
    }

    public boolean setNoMoreData(boolean noMoreData) {
        if (this.mNoMoreData == noMoreData) {
            return true;
        }
        this.mNoMoreData = noMoreData;
        View arrowView = this.mArrowView;
        if (noMoreData) {
            this.mTitleText.setText(this.mTextNothing);
            arrowView.setVisibility(8);
            return true;
        }
        this.mTitleText.setText(this.mTextPulling);
        arrowView.setVisibility(0);
        return true;
    }

    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        View arrowView = this.mArrowView;
        if (!this.mNoMoreData) {
            switch (AnonymousClass1.$SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[newState.ordinal()]) {
                case 1:
                    arrowView.setVisibility(0);
                    break;
                case 2:
                    break;
                case 3:
                case 4:
                    arrowView.setVisibility(8);
                    this.mTitleText.setText(this.mTextLoading);
                    return;
                case 5:
                    this.mTitleText.setText(this.mTextRelease);
                    arrowView.animate().rotation(0.0f);
                    return;
                case 6:
                    this.mTitleText.setText(this.mTextRefreshing);
                    arrowView.setVisibility(8);
                    return;
                default:
                    return;
            }
            this.mTitleText.setText(this.mTextPulling);
            arrowView.animate().rotation(180.0f);
        }
    }

    /* renamed from: com.scwang.smartrefresh.layout.footer.ClassicsFooter$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState;

        static {
            int[] iArr = new int[RefreshState.values().length];
            $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState = iArr;
            try {
                iArr[RefreshState.None.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.PullUpToLoad.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.Loading.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.LoadReleased.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.ReleaseToLoad.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.Refreshing.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }
}

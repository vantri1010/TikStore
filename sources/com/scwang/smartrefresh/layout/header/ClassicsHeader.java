package com.scwang.smartrefresh.layout.header;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ArrowDrawable;
import com.scwang.smartrefresh.layout.internal.InternalClassics;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.util.SmartUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClassicsHeader extends InternalClassics<ClassicsHeader> implements RefreshHeader {
    public static final int ID_TEXT_UPDATE = R.id.srl_classics_update;
    public static String REFRESH_HEADER_FAILED = null;
    public static String REFRESH_HEADER_FINISH = null;
    public static String REFRESH_HEADER_LOADING = null;
    public static String REFRESH_HEADER_PULLING = null;
    public static String REFRESH_HEADER_REFRESHING = null;
    public static String REFRESH_HEADER_RELEASE = null;
    public static String REFRESH_HEADER_SECONDARY = null;
    public static String REFRESH_HEADER_UPDATE = null;
    protected String KEY_LAST_UPDATE_TIME;
    protected boolean mEnableLastTime;
    protected Date mLastTime;
    protected DateFormat mLastUpdateFormat;
    protected TextView mLastUpdateText;
    protected SharedPreferences mShared;
    protected String mTextFailed;
    protected String mTextFinish;
    protected String mTextLoading;
    protected String mTextPulling;
    protected String mTextRefreshing;
    protected String mTextRelease;
    protected String mTextSecondary;
    protected String mTextUpdate;

    public ClassicsHeader(Context context) {
        this(context, (AttributeSet) null);
    }

    public ClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        FragmentManager manager;
        this.KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";
        this.mEnableLastTime = true;
        View.inflate(context, R.layout.srl_classics_header, this);
        ImageView imageView = (ImageView) findViewById(R.id.srl_classics_arrow);
        this.mArrowView = imageView;
        TextView textView = (TextView) findViewById(R.id.srl_classics_update);
        this.mLastUpdateText = textView;
        ImageView imageView2 = (ImageView) findViewById(R.id.srl_classics_progress);
        this.mProgressView = imageView2;
        this.mTitleText = (TextView) findViewById(R.id.srl_classics_title);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader);
        RelativeLayout.LayoutParams lpArrow = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        RelativeLayout.LayoutParams lpProgress = (RelativeLayout.LayoutParams) imageView2.getLayoutParams();
        new LinearLayout.LayoutParams(-2, -2).topMargin = ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextTimeMarginTop, SmartUtil.dp2px(0.0f));
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, SmartUtil.dp2px(20.0f));
        lpArrow.rightMargin = lpProgress.rightMargin;
        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.height);
        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.height);
        this.mFinishDuration = ta.getInt(R.styleable.ClassicsHeader_srlFinishDuration, this.mFinishDuration);
        this.mEnableLastTime = ta.getBoolean(R.styleable.ClassicsHeader_srlEnableLastTime, this.mEnableLastTime);
        this.mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.ClassicsHeader_srlClassicsSpinnerStyle, this.mSpinnerStyle.ordinal)];
        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableArrow)) {
            this.mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableArrow));
        } else if (this.mArrowView.getDrawable() == null) {
            this.mArrowDrawable = new ArrowDrawable();
            this.mArrowDrawable.setColor(-10066330);
            this.mArrowView.setImageDrawable(this.mArrowDrawable);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableProgress)) {
            this.mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableProgress));
        } else if (this.mProgressView.getDrawable() == null) {
            this.mProgressDrawable = new ProgressDrawable();
            this.mProgressDrawable.setColor(-10066330);
            this.mProgressView.setImageDrawable(this.mProgressDrawable);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTitle)) {
            this.mTitleText.setTextSize(0, (float) ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTitle, SmartUtil.dp2px(16.0f)));
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTime)) {
            this.mLastUpdateText.setTextSize(0, (float) ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTime, SmartUtil.dp2px(12.0f)));
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.ClassicsHeader_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.ClassicsHeader_srlAccentColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextPulling)) {
            this.mTextPulling = ta.getString(R.styleable.ClassicsHeader_srlTextPulling);
        } else {
            String str = REFRESH_HEADER_PULLING;
            if (str != null) {
                this.mTextPulling = str;
            } else {
                this.mTextPulling = context.getString(R.string.srl_header_pulling);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextLoading)) {
            this.mTextLoading = ta.getString(R.styleable.ClassicsHeader_srlTextLoading);
        } else {
            String str2 = REFRESH_HEADER_LOADING;
            if (str2 != null) {
                this.mTextLoading = str2;
            } else {
                this.mTextLoading = context.getString(R.string.srl_header_loading);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextRelease)) {
            this.mTextRelease = ta.getString(R.styleable.ClassicsHeader_srlTextRelease);
        } else {
            String str3 = REFRESH_HEADER_RELEASE;
            if (str3 != null) {
                this.mTextRelease = str3;
            } else {
                this.mTextRelease = context.getString(R.string.srl_header_release);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextFinish)) {
            this.mTextFinish = ta.getString(R.styleable.ClassicsHeader_srlTextFinish);
        } else {
            String str4 = REFRESH_HEADER_FINISH;
            if (str4 != null) {
                this.mTextFinish = str4;
            } else {
                this.mTextFinish = context.getString(R.string.srl_header_finish);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextFailed)) {
            this.mTextFailed = ta.getString(R.styleable.ClassicsHeader_srlTextFailed);
        } else {
            String str5 = REFRESH_HEADER_FAILED;
            if (str5 != null) {
                this.mTextFailed = str5;
            } else {
                this.mTextFailed = context.getString(R.string.srl_header_failed);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSecondary)) {
            this.mTextSecondary = ta.getString(R.styleable.ClassicsHeader_srlTextSecondary);
        } else {
            String str6 = REFRESH_HEADER_SECONDARY;
            if (str6 != null) {
                this.mTextSecondary = str6;
            } else {
                this.mTextSecondary = context.getString(R.string.srl_header_secondary);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextRefreshing)) {
            this.mTextRefreshing = ta.getString(R.styleable.ClassicsHeader_srlTextRefreshing);
        } else {
            String str7 = REFRESH_HEADER_REFRESHING;
            if (str7 != null) {
                this.mTextRefreshing = str7;
            } else {
                this.mTextRefreshing = context.getString(R.string.srl_header_refreshing);
            }
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextUpdate)) {
            this.mTextUpdate = ta.getString(R.styleable.ClassicsHeader_srlTextUpdate);
        } else {
            String str8 = REFRESH_HEADER_UPDATE;
            if (str8 != null) {
                this.mTextUpdate = str8;
            } else {
                this.mTextUpdate = context.getString(R.string.srl_header_update);
            }
        }
        this.mLastUpdateFormat = new SimpleDateFormat(this.mTextUpdate, Locale.getDefault());
        ta.recycle();
        imageView2.animate().setInterpolator((TimeInterpolator) null);
        textView.setVisibility(this.mEnableLastTime ? 0 : 8);
        this.mTitleText.setText(isInEditMode() ? this.mTextRefreshing : this.mTextPulling);
        if (isInEditMode()) {
            imageView.setVisibility(8);
        } else {
            imageView2.setVisibility(8);
        }
        try {
            if ((context instanceof FragmentActivity) && (manager = ((FragmentActivity) context).getSupportFragmentManager()) != null && manager.getFragments().size() > 0) {
                setLastUpdateTime(new Date());
                return;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.KEY_LAST_UPDATE_TIME += context.getClass().getName();
        this.mShared = context.getSharedPreferences("ClassicsHeader", 0);
        setLastUpdateTime(new Date(this.mShared.getLong(this.KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));
    }

    public int onFinish(RefreshLayout layout, boolean success) {
        if (success) {
            this.mTitleText.setText(this.mTextFinish);
            if (this.mLastTime != null) {
                setLastUpdateTime(new Date());
            }
        } else {
            this.mTitleText.setText(this.mTextFailed);
        }
        return super.onFinish(layout, success);
    }

    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        View arrowView = this.mArrowView;
        View updateView = this.mLastUpdateText;
        int i = 8;
        switch (AnonymousClass1.$SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[newState.ordinal()]) {
            case 1:
                if (this.mEnableLastTime) {
                    i = 0;
                }
                updateView.setVisibility(i);
                break;
            case 2:
                break;
            case 3:
            case 4:
                this.mTitleText.setText(this.mTextRefreshing);
                arrowView.setVisibility(8);
                return;
            case 5:
                this.mTitleText.setText(this.mTextRelease);
                arrowView.animate().rotation(180.0f);
                return;
            case 6:
                this.mTitleText.setText(this.mTextSecondary);
                arrowView.animate().rotation(0.0f);
                return;
            case 7:
                arrowView.setVisibility(8);
                if (this.mEnableLastTime) {
                    i = 4;
                }
                updateView.setVisibility(i);
                this.mTitleText.setText(this.mTextLoading);
                return;
            default:
                return;
        }
        this.mTitleText.setText(this.mTextPulling);
        arrowView.setVisibility(0);
        arrowView.animate().rotation(0.0f);
    }

    /* renamed from: com.scwang.smartrefresh.layout.header.ClassicsHeader$1  reason: invalid class name */
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
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.PullDownToRefresh.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.Refreshing.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.RefreshReleased.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.ReleaseToRefresh.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.ReleaseToTwoLevel.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.Loading.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public ClassicsHeader setLastUpdateTime(Date time) {
        this.mLastTime = time;
        this.mLastUpdateText.setText(this.mLastUpdateFormat.format(time));
        if (this.mShared != null && !isInEditMode()) {
            this.mShared.edit().putLong(this.KEY_LAST_UPDATE_TIME, time.getTime()).apply();
        }
        return this;
    }

    public ClassicsHeader setTimeFormat(DateFormat format) {
        this.mLastUpdateFormat = format;
        Date date = this.mLastTime;
        if (date != null) {
            this.mLastUpdateText.setText(format.format(date));
        }
        return this;
    }

    public ClassicsHeader setLastUpdateText(CharSequence text) {
        this.mLastTime = null;
        this.mLastUpdateText.setText(text);
        return this;
    }

    public ClassicsHeader setAccentColor(int accentColor) {
        this.mLastUpdateText.setTextColor((16777215 & accentColor) | -872415232);
        return (ClassicsHeader) super.setAccentColor(accentColor);
    }

    public ClassicsHeader setEnableLastTime(boolean enable) {
        View updateView = this.mLastUpdateText;
        this.mEnableLastTime = enable;
        updateView.setVisibility(enable ? 0 : 8);
        if (this.mRefreshKernel != null) {
            this.mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public ClassicsHeader setTextSizeTime(float size) {
        this.mLastUpdateText.setTextSize(size);
        if (this.mRefreshKernel != null) {
            this.mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return this;
    }

    public ClassicsHeader setTextTimeMarginTop(float dp) {
        View updateView = this.mLastUpdateText;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) updateView.getLayoutParams();
        lp.topMargin = SmartUtil.dp2px(dp);
        updateView.setLayoutParams(lp);
        return this;
    }
}

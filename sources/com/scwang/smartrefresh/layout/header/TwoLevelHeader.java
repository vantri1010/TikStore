package com.scwang.smartrefresh.layout.header;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.OnTwoLevelListener;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;

public class TwoLevelHeader extends InternalAbstract implements RefreshHeader {
    protected boolean mEnablePullToCloseTwoLevel;
    protected boolean mEnableTwoLevel;
    protected int mFloorDuration;
    protected float mFloorRage;
    protected int mHeaderHeight;
    protected float mMaxRage;
    protected float mPercent;
    protected RefreshInternal mRefreshHeader;
    protected RefreshKernel mRefreshKernel;
    protected float mRefreshRage;
    protected int mSpinner;
    protected OnTwoLevelListener mTwoLevelListener;

    public TwoLevelHeader(Context context) {
        this(context, (AttributeSet) null);
    }

    public TwoLevelHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mPercent = 0.0f;
        this.mMaxRage = 2.5f;
        this.mFloorRage = 1.9f;
        this.mRefreshRage = 1.0f;
        this.mEnableTwoLevel = true;
        this.mEnablePullToCloseTwoLevel = true;
        this.mFloorDuration = 1000;
        this.mSpinnerStyle = SpinnerStyle.FixedBehind;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TwoLevelHeader);
        this.mMaxRage = ta.getFloat(R.styleable.TwoLevelHeader_srlMaxRage, this.mMaxRage);
        this.mFloorRage = ta.getFloat(R.styleable.TwoLevelHeader_srlFloorRage, this.mFloorRage);
        this.mRefreshRage = ta.getFloat(R.styleable.TwoLevelHeader_srlRefreshRage, this.mRefreshRage);
        this.mFloorDuration = ta.getInt(R.styleable.TwoLevelHeader_srlFloorDuration, this.mFloorDuration);
        this.mEnableTwoLevel = ta.getBoolean(R.styleable.TwoLevelHeader_srlEnableTwoLevel, this.mEnableTwoLevel);
        this.mEnablePullToCloseTwoLevel = ta.getBoolean(R.styleable.TwoLevelHeader_srlEnablePullToCloseTwoLevel, this.mEnablePullToCloseTwoLevel);
        ta.recycle();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        int i = 0;
        int len = getChildCount();
        while (true) {
            if (i >= len) {
                break;
            }
            View childAt = getChildAt(i);
            if (childAt instanceof RefreshHeader) {
                this.mRefreshHeader = (RefreshHeader) childAt;
                this.mWrappedInternal = (RefreshInternal) childAt;
                bringChildToFront(childAt);
                break;
            }
            i++;
        }
        if (this.mRefreshHeader == null) {
            setRefreshHeader(new ClassicsHeader(getContext()));
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSpinnerStyle = SpinnerStyle.MatchLayout;
        if (this.mRefreshHeader == null) {
            setRefreshHeader(new ClassicsHeader(getContext()));
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mSpinnerStyle = SpinnerStyle.FixedBehind;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        RefreshInternal refreshHeader = this.mRefreshHeader;
        if (refreshHeader == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (View.MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE) {
            refreshHeader.getView().measure(widthMeasureSpec, heightMeasureSpec);
            super.setMeasuredDimension(View.resolveSize(super.getSuggestedMinimumWidth(), widthMeasureSpec), refreshHeader.getView().getMeasuredHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public boolean equals(Object obj) {
        Object header = this.mRefreshHeader;
        return (header != null && header.equals(obj)) || super.equals(obj);
    }

    public void onInitialized(RefreshKernel kernel, int height, int maxDragHeight) {
        RefreshInternal refreshHeader = this.mRefreshHeader;
        if (refreshHeader != null) {
            if ((((float) (maxDragHeight + height)) * 1.0f) / ((float) height) != this.mMaxRage && this.mHeaderHeight == 0) {
                this.mHeaderHeight = height;
                this.mRefreshHeader = null;
                kernel.getRefreshLayout().setHeaderMaxDragRate(this.mMaxRage);
                this.mRefreshHeader = refreshHeader;
            }
            if (this.mRefreshKernel == null && refreshHeader.getSpinnerStyle() == SpinnerStyle.Translate && !isInEditMode()) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) refreshHeader.getView().getLayoutParams();
                params.topMargin -= height;
                refreshHeader.getView().setLayoutParams(params);
            }
            this.mHeaderHeight = height;
            this.mRefreshKernel = kernel;
            kernel.requestFloorDuration(this.mFloorDuration);
            kernel.requestNeedTouchEventFor(this, !this.mEnablePullToCloseTwoLevel);
            refreshHeader.onInitialized(kernel, height, maxDragHeight);
        }
    }

    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        RefreshInternal refreshHeader = this.mRefreshHeader;
        if (refreshHeader != null) {
            this.mRefreshHeader.onStateChanged(refreshLayout, oldState, newState);
            int i = AnonymousClass1.$SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[newState.ordinal()];
            boolean z = true;
            if (i == 1) {
                if (refreshHeader.getView() != this) {
                    refreshHeader.getView().animate().alpha(0.0f).setDuration((long) (this.mFloorDuration / 2));
                }
                RefreshKernel refreshKernel = this.mRefreshKernel;
                if (refreshKernel != null) {
                    OnTwoLevelListener twoLevelListener = this.mTwoLevelListener;
                    if (twoLevelListener != null && !twoLevelListener.onTwoLevel(refreshLayout)) {
                        z = false;
                    }
                    refreshKernel.startTwoLevel(z);
                }
            } else if (i != 3) {
                if (i == 4 && refreshHeader.getView().getAlpha() == 0.0f && refreshHeader.getView() != this) {
                    refreshHeader.getView().setAlpha(1.0f);
                }
            } else if (refreshHeader.getView() != this) {
                refreshHeader.getView().animate().alpha(1.0f).setDuration((long) (this.mFloorDuration / 2));
            }
        }
    }

    /* renamed from: com.scwang.smartrefresh.layout.header.TwoLevelHeader$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState;

        static {
            int[] iArr = new int[RefreshState.values().length];
            $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState = iArr;
            try {
                iArr[RefreshState.TwoLevelReleased.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.TwoLevel.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.TwoLevelFinish.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.PullDownToRefresh.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        moveSpinner(offset);
        RefreshInternal refreshHeader = this.mRefreshHeader;
        RefreshKernel refreshKernel = this.mRefreshKernel;
        if (refreshHeader != null) {
            refreshHeader.onMoving(isDragging, percent, offset, height, maxDragHeight);
        }
        if (isDragging) {
            float f = this.mPercent;
            float f2 = this.mFloorRage;
            if (f < f2 && percent >= f2 && this.mEnableTwoLevel) {
                refreshKernel.setState(RefreshState.ReleaseToTwoLevel);
            } else if (this.mPercent < this.mFloorRage || percent >= this.mRefreshRage) {
                float f3 = this.mPercent;
                float f4 = this.mFloorRage;
                if (f3 >= f4 && percent < f4) {
                    refreshKernel.setState(RefreshState.ReleaseToRefresh);
                }
            } else {
                refreshKernel.setState(RefreshState.PullDownToRefresh);
            }
            this.mPercent = percent;
        }
    }

    /* access modifiers changed from: protected */
    public void moveSpinner(int spinner) {
        RefreshInternal refreshHeader = this.mRefreshHeader;
        if (this.mSpinner != spinner && refreshHeader != null) {
            this.mSpinner = spinner;
            SpinnerStyle style = refreshHeader.getSpinnerStyle();
            if (style == SpinnerStyle.Translate) {
                refreshHeader.getView().setTranslationY((float) spinner);
            } else if (style.scale) {
                View view = refreshHeader.getView();
                view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getTop() + Math.max(0, spinner));
            }
        }
    }

    public TwoLevelHeader setRefreshHeader(RefreshHeader header) {
        return setRefreshHeader(header, -1, -2);
    }

    public TwoLevelHeader setRefreshHeader(RefreshHeader header, int width, int height) {
        if (header != null) {
            RefreshInternal refreshHeader = this.mRefreshHeader;
            if (refreshHeader != null) {
                removeView(refreshHeader.getView());
            }
            RefreshInternal refreshHeader2 = header;
            if (refreshHeader2.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                addView(refreshHeader2.getView(), 0, new RelativeLayout.LayoutParams(width, height));
            } else {
                addView(refreshHeader2.getView(), getChildCount(), new RelativeLayout.LayoutParams(width, height));
            }
            this.mRefreshHeader = header;
            this.mWrappedInternal = header;
        }
        return this;
    }

    public TwoLevelHeader setMaxRage(float rate) {
        if (this.mMaxRage != rate) {
            this.mMaxRage = rate;
            RefreshKernel refreshKernel = this.mRefreshKernel;
            if (refreshKernel != null) {
                this.mHeaderHeight = 0;
                refreshKernel.getRefreshLayout().setHeaderMaxDragRate(this.mMaxRage);
            }
        }
        return this;
    }

    public TwoLevelHeader setEnablePullToCloseTwoLevel(boolean enabled) {
        RefreshKernel refreshKernel = this.mRefreshKernel;
        this.mEnablePullToCloseTwoLevel = enabled;
        if (refreshKernel != null) {
            refreshKernel.requestNeedTouchEventFor(this, !enabled);
        }
        return this;
    }

    public TwoLevelHeader setFloorRage(float rate) {
        this.mFloorRage = rate;
        return this;
    }

    public TwoLevelHeader setRefreshRage(float rate) {
        this.mRefreshRage = rate;
        return this;
    }

    public TwoLevelHeader setEnableTwoLevel(boolean enabled) {
        this.mEnableTwoLevel = enabled;
        return this;
    }

    public TwoLevelHeader setFloorDuration(int duration) {
        this.mFloorDuration = duration;
        return this;
    }

    public TwoLevelHeader setOnTwoLevelListener(OnTwoLevelListener listener) {
        this.mTwoLevelListener = listener;
        return this;
    }

    public TwoLevelHeader finishTwoLevel() {
        RefreshKernel refreshKernel = this.mRefreshKernel;
        if (refreshKernel != null) {
            refreshKernel.finishTwoLevel();
        }
        return this;
    }

    public TwoLevelHeader openTwoLevel(boolean widthOnTwoLevelListener) {
        RefreshKernel refreshKernel = this.mRefreshKernel;
        if (refreshKernel != null) {
            OnTwoLevelListener twoLevelListener = this.mTwoLevelListener;
            refreshKernel.startTwoLevel(!widthOnTwoLevelListener || twoLevelListener == null || twoLevelListener.onTwoLevel(refreshKernel.getRefreshLayout()));
        }
        return this;
    }
}

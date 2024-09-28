package im.bclpbkiauv.ui.hui.friendscircle_v1.view.panel;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import cn.dreamtobe.kpswitch.IPanelConflictLayout;
import cn.dreamtobe.kpswitch.R;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.ViewUtil;

public class KPSwitchPanelLayoutHandler implements IPanelConflictLayout {
    private boolean mIgnoreRecommendHeight = false;
    private boolean mIsHide = false;
    private boolean mIsKeyboardShowing = false;
    private final View panelLayout;
    private final int[] processedMeasureWHSpec = new int[2];

    public KPSwitchPanelLayoutHandler(View panelLayout2, AttributeSet attrs) {
        this.panelLayout = panelLayout2;
        if (attrs != null) {
            TypedArray typedArray = null;
            try {
                typedArray = panelLayout2.getContext().obtainStyledAttributes(attrs, R.styleable.KPSwitchPanelLayout);
                this.mIgnoreRecommendHeight = typedArray.getBoolean(0, false);
            } finally {
                if (typedArray != null) {
                    typedArray.recycle();
                }
            }
        }
    }

    public boolean filterSetVisibility(int visibility) {
        if (visibility == 0) {
            this.mIsHide = false;
        }
        if (visibility == this.panelLayout.getVisibility()) {
            return true;
        }
        if (!isKeyboardShowing() || visibility != 0) {
            return false;
        }
        return true;
    }

    public int[] processOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mIsHide) {
            this.panelLayout.setVisibility(8);
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 1073741824);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 1073741824);
        }
        int[] iArr = this.processedMeasureWHSpec;
        iArr[0] = widthMeasureSpec;
        iArr[1] = heightMeasureSpec;
        return iArr;
    }

    public void setIsKeyboardShowing(boolean isKeyboardShowing) {
        this.mIsKeyboardShowing = isKeyboardShowing;
    }

    public boolean isKeyboardShowing() {
        return this.mIsKeyboardShowing;
    }

    public boolean isVisible() {
        return !this.mIsHide;
    }

    public void handleShow() {
        throw new IllegalAccessError("You can't invoke handle show in handler, please instead of handling in the panel layout, maybe just need invoke super.setVisibility(View.VISIBLE)");
    }

    public void handleHide() {
        this.mIsHide = true;
    }

    public void resetToRecommendPanelHeight(int recommendPanelHeight) {
        if (!this.mIgnoreRecommendHeight) {
            ViewUtil.refreshHeight(this.panelLayout, recommendPanelHeight);
        }
    }

    public void setIgnoreRecommendHeight(boolean ignoreRecommendHeight) {
        this.mIgnoreRecommendHeight = ignoreRecommendHeight;
    }
}

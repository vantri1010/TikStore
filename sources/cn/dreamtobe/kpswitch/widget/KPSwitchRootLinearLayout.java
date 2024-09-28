package cn.dreamtobe.kpswitch.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import cn.dreamtobe.kpswitch.handler.KPSwitchRootLayoutHandler;

public class KPSwitchRootLinearLayout extends LinearLayout {
    private KPSwitchRootLayoutHandler conflictHandler;

    public KPSwitchRootLinearLayout(Context context) {
        super(context);
        init();
    }

    public KPSwitchRootLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KPSwitchRootLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public KPSwitchRootLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.conflictHandler = new KPSwitchRootLayoutHandler(this);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.conflictHandler.handleBeforeMeasure(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

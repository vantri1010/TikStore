package cn.dreamtobe.kpswitch.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import cn.dreamtobe.kpswitch.handler.KPSwitchRootLayoutHandler;

public class KPSwitchRootRelativeLayout extends RelativeLayout {
    private KPSwitchRootLayoutHandler conflictHandler;

    public KPSwitchRootRelativeLayout(Context context) {
        super(context);
        init();
    }

    public KPSwitchRootRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KPSwitchRootRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public KPSwitchRootRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

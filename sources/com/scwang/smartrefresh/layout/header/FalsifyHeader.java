package com.scwang.smartrefresh.layout.header;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.SmartUtil;

public class FalsifyHeader extends InternalAbstract implements RefreshHeader {
    protected RefreshKernel mRefreshKernel;

    public FalsifyHeader(Context context) {
        this(context, (AttributeSet) null);
    }

    public FalsifyHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isInEditMode()) {
            int d = SmartUtil.dp2px(5.0f);
            Context context = getContext();
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(-858993460);
            paint.setStrokeWidth((float) SmartUtil.dp2px(1.0f));
            paint.setPathEffect(new DashPathEffect(new float[]{(float) d, (float) d, (float) d, (float) d}, 1.0f));
            Canvas canvas2 = canvas;
            canvas2.drawRect((float) d, (float) d, (float) (getWidth() - d), (float) (getBottom() - d), paint);
            TextView textView = new TextView(context);
            textView.setText(context.getString(R.string.srl_component_falsify, new Object[]{getClass().getSimpleName(), Float.valueOf(SmartUtil.px2dp(getHeight()))}));
            textView.setTextColor(-858993460);
            textView.setGravity(17);
            View view = textView;
            view.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getHeight(), 1073741824));
            view.layout(0, 0, getWidth(), getHeight());
            view.draw(canvas);
        }
    }

    public void onInitialized(RefreshKernel kernel, int height, int maxDragHeight) {
        this.mRefreshKernel = kernel;
    }

    public void onReleased(RefreshLayout layout, int height, int maxDragHeight) {
        RefreshKernel refreshKernel = this.mRefreshKernel;
        if (refreshKernel != null) {
            refreshKernel.setState(RefreshState.None);
            this.mRefreshKernel.setState(RefreshState.RefreshFinish);
        }
    }
}

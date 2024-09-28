package com.scwang.smartrefresh.layout.util;

import android.view.View;
import android.view.ViewGroup;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.listener.CoordinatorLayoutListener;

public class DesignUtil {
    public static void checkCoordinatorLayout(View content, RefreshKernel kernel, final CoordinatorLayoutListener listener) {
        try {
            if (content instanceof CoordinatorLayout) {
                kernel.getRefreshLayout().setEnableNestedScroll(false);
                ViewGroup layout = (ViewGroup) content;
                for (int i = layout.getChildCount() - 1; i >= 0; i--) {
                    View view = layout.getChildAt(i);
                    if (view instanceof AppBarLayout) {
                        ((AppBarLayout) view).addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) new AppBarLayout.OnOffsetChangedListener() {
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                CoordinatorLayoutListener coordinatorLayoutListener = listener;
                                boolean z = true;
                                boolean z2 = verticalOffset >= 0;
                                if (appBarLayout.getTotalScrollRange() + verticalOffset > 0) {
                                    z = false;
                                }
                                coordinatorLayoutListener.onCoordinatorUpdate(z2, z);
                            }
                        });
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

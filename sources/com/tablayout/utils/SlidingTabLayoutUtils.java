package com.tablayout.utils;

import android.graphics.Bitmap;
import android.view.View;

public class SlidingTabLayoutUtils {
    public static Bitmap generateViewCacheBitmap(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return Bitmap.createBitmap(view.getDrawingCache());
    }

    /* JADX WARNING: type inference failed for: r3v2, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.view.View findBrotherView(android.view.View r4, int r5, int r6) {
        /*
            r0 = 0
            r1 = r4
        L_0x0002:
            if (r0 >= r6) goto L_0x001d
            android.view.View r2 = r1.findViewById(r5)
            if (r2 == 0) goto L_0x000b
            return r2
        L_0x000b:
            int r0 = r0 + 1
            android.view.ViewParent r3 = r1.getParent()
            boolean r3 = r3 instanceof android.view.View
            if (r3 == 0) goto L_0x001d
            android.view.ViewParent r3 = r1.getParent()
            r1 = r3
            android.view.View r1 = (android.view.View) r1
            goto L_0x0002
        L_0x001d:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tablayout.utils.SlidingTabLayoutUtils.findBrotherView(android.view.View, int, int):android.view.View");
    }
}

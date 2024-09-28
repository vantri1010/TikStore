package com.baidu.mapapi.map;

import android.animation.ValueAnimator;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class SwipeDismissTouchListener implements View.OnTouchListener {
    private int a;
    private int b;
    private int c;
    private long d;
    /* access modifiers changed from: private */
    public View e;
    /* access modifiers changed from: private */
    public DismissCallbacks f;
    private int g = 1;
    private float h;
    private float i;
    private boolean j;
    private int k;
    /* access modifiers changed from: private */
    public Object l;
    private VelocityTracker m;
    private float n;
    private boolean o;
    private boolean p;

    public interface DismissCallbacks {
        boolean canDismiss(Object obj);

        void onDismiss(View view, Object obj);

        void onNotify();
    }

    public SwipeDismissTouchListener(View view, Object obj, DismissCallbacks dismissCallbacks) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(view.getContext());
        this.a = viewConfiguration.getScaledTouchSlop();
        this.b = viewConfiguration.getScaledMinimumFlingVelocity();
        this.c = viewConfiguration.getScaledMaximumFlingVelocity();
        this.d = (long) view.getContext().getResources().getInteger(17694720);
        this.e = view;
        view.getContext();
        this.l = obj;
        this.f = dismissCallbacks;
    }

    /* access modifiers changed from: private */
    public void a() {
        ViewGroup.LayoutParams layoutParams = this.e.getLayoutParams();
        int height = this.e.getHeight();
        ValueAnimator duration = ValueAnimator.ofInt(new int[]{height, 1}).setDuration(this.d);
        duration.addListener(new r(this, layoutParams, height));
        duration.addUpdateListener(new s(this, layoutParams));
        duration.start();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0027, code lost:
        if (r10.m == null) goto L_0x018f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x018b, code lost:
        if (r10.j != false) goto L_0x002b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.View r11, android.view.MotionEvent r12) {
        /*
            r10 = this;
            float r11 = r10.n
            r0 = 0
            r12.offsetLocation(r11, r0)
            int r11 = r10.g
            r1 = 2
            if (r11 >= r1) goto L_0x0013
            android.view.View r11 = r10.e
            int r11 = r11.getWidth()
            r10.g = r11
        L_0x0013:
            int r11 = r12.getActionMasked()
            r2 = 0
            r3 = 1
            if (r11 == 0) goto L_0x0190
            r4 = 0
            r5 = 3
            if (r11 == r3) goto L_0x00e2
            if (r11 == r1) goto L_0x004f
            if (r11 == r5) goto L_0x0025
            goto L_0x018f
        L_0x0025:
            android.view.VelocityTracker r11 = r10.m
            if (r11 != 0) goto L_0x002b
            goto L_0x018f
        L_0x002b:
            android.view.View r11 = r10.e
            android.view.ViewPropertyAnimator r11 = r11.animate()
            android.view.ViewPropertyAnimator r11 = r11.translationX(r0)
            long r5 = r10.d
            android.view.ViewPropertyAnimator r11 = r11.setDuration(r5)
            r11.setListener(r4)
        L_0x003e:
            android.view.VelocityTracker r11 = r10.m
            r11.recycle()
            r10.m = r4
            r10.n = r0
            r10.h = r0
            r10.i = r0
            r10.j = r2
            goto L_0x018f
        L_0x004f:
            android.view.VelocityTracker r11 = r10.m
            if (r11 != 0) goto L_0x0055
            goto L_0x018f
        L_0x0055:
            r11.addMovement(r12)
            float r11 = r12.getRawX()
            float r1 = r10.h
            float r11 = r11 - r1
            float r1 = r12.getRawY()
            float r4 = r10.i
            float r1 = r1 - r4
            float r4 = java.lang.Math.abs(r11)
            int r6 = r10.a
            float r6 = (float) r6
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x00d2
            float r1 = java.lang.Math.abs(r1)
            float r4 = java.lang.Math.abs(r11)
            r6 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r6
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 >= 0) goto L_0x00d2
            r10.j = r3
            int r0 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0089
            int r0 = r10.a
            goto L_0x008c
        L_0x0089:
            int r0 = r10.a
            int r0 = -r0
        L_0x008c:
            r10.k = r0
            android.view.View r0 = r10.e
            android.view.ViewParent r0 = r0.getParent()
            r0.requestDisallowInterceptTouchEvent(r3)
            boolean r0 = r10.o
            if (r0 != 0) goto L_0x00a2
            r10.o = r3
            com.baidu.mapapi.map.SwipeDismissTouchListener$DismissCallbacks r0 = r10.f
            r0.onNotify()
        L_0x00a2:
            float r0 = java.lang.Math.abs(r11)
            int r1 = r10.g
            int r1 = r1 / r5
            float r1 = (float) r1
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x00ba
            boolean r0 = r10.p
            if (r0 != 0) goto L_0x00bc
            r10.p = r3
            com.baidu.mapapi.map.SwipeDismissTouchListener$DismissCallbacks r0 = r10.f
            r0.onNotify()
            goto L_0x00bc
        L_0x00ba:
            r10.p = r2
        L_0x00bc:
            android.view.MotionEvent r0 = android.view.MotionEvent.obtain(r12)
            int r12 = r12.getActionIndex()
            int r12 = r12 << 8
            r12 = r12 | r5
            r0.setAction(r12)
            android.view.View r12 = r10.e
            r12.onTouchEvent(r0)
            r0.recycle()
        L_0x00d2:
            boolean r12 = r10.j
            if (r12 == 0) goto L_0x018f
            r10.n = r11
            android.view.View r12 = r10.e
            int r0 = r10.k
            float r0 = (float) r0
            float r11 = r11 - r0
            r12.setTranslationX(r11)
            return r3
        L_0x00e2:
            android.view.VelocityTracker r11 = r10.m
            if (r11 != 0) goto L_0x00e8
            goto L_0x018f
        L_0x00e8:
            float r11 = r12.getRawX()
            float r1 = r10.h
            float r11 = r11 - r1
            android.view.VelocityTracker r1 = r10.m
            r1.addMovement(r12)
            android.view.VelocityTracker r12 = r10.m
            r1 = 1000(0x3e8, float:1.401E-42)
            r12.computeCurrentVelocity(r1)
            android.view.VelocityTracker r12 = r10.m
            float r12 = r12.getXVelocity()
            float r1 = java.lang.Math.abs(r12)
            android.view.VelocityTracker r6 = r10.m
            float r6 = r6.getYVelocity()
            float r6 = java.lang.Math.abs(r6)
            float r7 = java.lang.Math.abs(r11)
            int r8 = r10.g
            int r8 = r8 / r5
            float r5 = (float) r8
            int r5 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0127
            boolean r5 = r10.j
            if (r5 == 0) goto L_0x0127
            int r11 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
            if (r11 <= 0) goto L_0x0125
            r11 = 1
            goto L_0x0164
        L_0x0125:
            r11 = 0
            goto L_0x0164
        L_0x0127:
            int r5 = r10.b
            float r5 = (float) r5
            int r5 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r5 > 0) goto L_0x0162
            int r5 = r10.c
            float r5 = (float) r5
            int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r5 > 0) goto L_0x0162
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x0162
            if (r1 >= 0) goto L_0x0162
            boolean r1 = r10.j
            if (r1 == 0) goto L_0x0162
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 >= 0) goto L_0x0145
            r12 = 1
            goto L_0x0146
        L_0x0145:
            r12 = 0
        L_0x0146:
            int r11 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
            if (r11 >= 0) goto L_0x014c
            r11 = 1
            goto L_0x014d
        L_0x014c:
            r11 = 0
        L_0x014d:
            if (r12 != r11) goto L_0x0151
            r11 = 1
            goto L_0x0152
        L_0x0151:
            r11 = 0
        L_0x0152:
            android.view.VelocityTracker r12 = r10.m
            float r12 = r12.getXVelocity()
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 <= 0) goto L_0x015d
            goto L_0x015e
        L_0x015d:
            r3 = 0
        L_0x015e:
            r9 = r3
            r3 = r11
            r11 = r9
            goto L_0x0164
        L_0x0162:
            r11 = 0
            r3 = 0
        L_0x0164:
            if (r3 == 0) goto L_0x0189
            android.view.View r12 = r10.e
            android.view.ViewPropertyAnimator r12 = r12.animate()
            if (r11 == 0) goto L_0x0171
            int r11 = r10.g
            goto L_0x0174
        L_0x0171:
            int r11 = r10.g
            int r11 = -r11
        L_0x0174:
            float r11 = (float) r11
            android.view.ViewPropertyAnimator r11 = r12.translationX(r11)
            long r5 = r10.d
            android.view.ViewPropertyAnimator r11 = r11.setDuration(r5)
            com.baidu.mapapi.map.q r12 = new com.baidu.mapapi.map.q
            r12.<init>(r10)
            r11.setListener(r12)
            goto L_0x003e
        L_0x0189:
            boolean r11 = r10.j
            if (r11 == 0) goto L_0x003e
            goto L_0x002b
        L_0x018f:
            return r2
        L_0x0190:
            float r11 = r12.getRawX()
            r10.h = r11
            float r11 = r12.getRawY()
            r10.i = r11
            com.baidu.mapapi.map.SwipeDismissTouchListener$DismissCallbacks r11 = r10.f
            java.lang.Object r0 = r10.l
            boolean r11 = r11.canDismiss(r0)
            if (r11 == 0) goto L_0x01b1
            r10.o = r2
            android.view.VelocityTracker r11 = android.view.VelocityTracker.obtain()
            r10.m = r11
            r11.addMovement(r12)
        L_0x01b1:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.map.SwipeDismissTouchListener.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }
}

package com.baidu.mapapi.map;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapsdkplatform.comapi.map.ab;
import com.baidu.mapsdkplatform.comapi.map.am;
import com.baidu.mapsdkplatform.comapi.map.h;
import com.baidu.mapsdkplatform.comapi.map.k;
import com.baidu.mapsdkplatform.comapi.map.l;
import com.baidu.mapsdkplatform.comapi.map.n;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class WearMapView extends ViewGroup implements View.OnApplyWindowInsetsListener {
    public static final int BT_INVIEW = 1;
    private static final String b = MapView.class.getSimpleName();
    private static String c;
    private static int d = 0;
    private static int e = 0;
    private static int s = 0;
    private static int t = 0;
    private static int u = 10;
    /* access modifiers changed from: private */
    public static final SparseArray<Integer> x;
    /* access modifiers changed from: private */
    public float A;
    private n B;
    private int C;
    private int D;
    private int E;
    private int F;
    private int G;
    private int H;
    ScreenShape a = ScreenShape.ROUND;
    /* access modifiers changed from: private */
    public l f;
    private BaiduMap g;
    private ImageView h;
    private Bitmap i;
    /* access modifiers changed from: private */
    public am j;
    private boolean k = true;
    private Point l;
    private Point m;
    public AnimationTask mTask;
    public Timer mTimer;
    public a mTimerHandler;
    private RelativeLayout n;
    private SwipeDismissView o;
    /* access modifiers changed from: private */
    public TextView p;
    /* access modifiers changed from: private */
    public TextView q;
    /* access modifiers changed from: private */
    public ImageView r;
    private boolean v = true;
    private Context w;
    private boolean y = true;
    private boolean z = true;

    public class AnimationTask extends TimerTask {
        public AnimationTask() {
        }

        public void run() {
            Message message = new Message();
            message.what = 1;
            WearMapView.this.mTimerHandler.sendMessage(message);
        }
    }

    public interface OnDismissCallback {
        void onDismiss();

        void onNotify();
    }

    public enum ScreenShape {
        ROUND,
        RECTANGLE,
        UNDETECTED
    }

    private class a extends Handler {
        private final WeakReference<Context> b;

        public a(Context context) {
            this.b = new WeakReference<>(context);
        }

        public void handleMessage(Message message) {
            if (((Context) this.b.get()) != null) {
                super.handleMessage(message);
                if (message.what == 1 && WearMapView.this.j != null) {
                    WearMapView.this.a(true);
                }
            }
        }
    }

    static {
        SparseArray<Integer> sparseArray = new SparseArray<>();
        x = sparseArray;
        sparseArray.append(3, 2000000);
        x.append(4, Integer.valueOf(EditInputFilter.MAX_VALUE));
        x.append(5, 500000);
        x.append(6, 200000);
        x.append(7, Integer.valueOf(DefaultOggSeeker.MATCH_BYTE_RANGE));
        x.append(8, 50000);
        x.append(9, 25000);
        x.append(10, 20000);
        x.append(11, 10000);
        x.append(12, 5000);
        x.append(13, 2000);
        x.append(14, 1000);
        x.append(15, 500);
        x.append(16, Integer.valueOf(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION));
        x.append(17, 100);
        x.append(18, 50);
        x.append(19, 20);
        x.append(20, 10);
        x.append(21, 5);
        x.append(22, 2);
    }

    public WearMapView(Context context) {
        super(context);
        a(context, (BaiduMapOptions) null);
    }

    public WearMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, (BaiduMapOptions) null);
    }

    public WearMapView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        a(context, (BaiduMapOptions) null);
    }

    public WearMapView(Context context, BaiduMapOptions baiduMapOptions) {
        super(context);
        a(context, baiduMapOptions);
    }

    private int a(int i2, int i3) {
        return i2 - ((int) Math.sqrt(Math.pow((double) i2, 2.0d) - Math.pow((double) i3, 2.0d)));
    }

    private void a(int i2) {
        l lVar = this.f;
        if (lVar != null) {
            if (i2 == 0) {
                lVar.onPause();
                b();
            } else if (i2 == 1) {
                lVar.onResume();
                c();
            }
        }
    }

    private static void a(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        s = point.x;
        t = point.y;
    }

    private void a(Context context, BaiduMapOptions baiduMapOptions) {
        AnimationTask animationTask;
        a(context);
        setOnApplyWindowInsetsListener(this);
        this.w = context;
        this.mTimerHandler = new a(context);
        Timer timer = new Timer();
        this.mTimer = timer;
        if (!(timer == null || (animationTask = this.mTask) == null)) {
            animationTask.cancel();
        }
        AnimationTask animationTask2 = new AnimationTask();
        this.mTask = animationTask2;
        this.mTimer.schedule(animationTask2, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        k.a();
        BMapManager.init();
        a(context, baiduMapOptions, c);
        this.g = new BaiduMap(this.f);
        this.f.a().u(false);
        this.f.a().t(false);
        c(context);
        d(context);
        b(context);
        if (baiduMapOptions != null && !baiduMapOptions.h) {
            this.j.setVisibility(4);
        }
        e(context);
        if (baiduMapOptions != null && !baiduMapOptions.i) {
            this.n.setVisibility(4);
        }
        if (!(baiduMapOptions == null || baiduMapOptions.l == null)) {
            this.m = baiduMapOptions.l;
        }
        if (baiduMapOptions != null && baiduMapOptions.k != null) {
            this.l = baiduMapOptions.k;
        }
    }

    private void a(Context context, BaiduMapOptions baiduMapOptions, String str) {
        if (baiduMapOptions == null) {
            this.f = new l(context, (ab) null, str, e);
        } else {
            this.f = new l(context, baiduMapOptions.a(), str, e);
        }
        addView(this.f);
        this.B = new aa(this);
        this.f.a().a(this.B);
    }

    private void a(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(-2, -2);
        }
        int i2 = layoutParams.width;
        int makeMeasureSpec = i2 > 0 ? View.MeasureSpec.makeMeasureSpec(i2, 1073741824) : View.MeasureSpec.makeMeasureSpec(0, 0);
        int i3 = layoutParams.height;
        view.measure(makeMeasureSpec, i3 > 0 ? View.MeasureSpec.makeMeasureSpec(i3, 1073741824) : View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    private void a(View view, boolean z2) {
        AnimatorSet animatorSet;
        if (z2) {
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "TranslationY", new float[]{0.0f, -50.0f}), ObjectAnimator.ofFloat(view, "alpha", new float[]{1.0f, 0.0f})});
            animatorSet.addListener(new ad(this, view));
        } else {
            view.setVisibility(0);
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "TranslationY", new float[]{-50.0f, 0.0f}), ObjectAnimator.ofFloat(view, "alpha", new float[]{0.0f, 1.0f})});
        }
        animatorSet.setDuration(1200);
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    public void a(String str, int i2) {
        if (this.f != null) {
            if (i2 != 0 && 1 != i2) {
                throw new RuntimeException("BDMapSDKException: loadCustomStyleFileMode is illegal. Only support 0-local, 1-server");
            } else if (TextUtils.isEmpty(str)) {
                throw new RuntimeException("BDMapSDKException: customMapStyleFilePath String is illegal");
            } else if (new File(str).exists()) {
                this.f.a().a(str, i2);
            } else {
                throw new RuntimeException("BDMapSDKException: please check whether the customMapStylePath file exits");
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z2) {
        if (this.k) {
            a((View) this.j, z2);
        }
    }

    private void b() {
        if (this.f != null && !this.v) {
            d();
            this.v = true;
        }
    }

    private void b(Context context) {
        this.o = new SwipeDismissView(context, this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((int) ((context.getResources().getDisplayMetrics().density * 34.0f) + 0.5f), t);
        this.o.setBackgroundColor(Color.argb(0, 0, 0, 0));
        this.o.setLayoutParams(layoutParams);
        addView(this.o);
    }

    private void c() {
        if (this.f != null && this.v) {
            e();
            this.v = false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c(android.content.Context r10) {
        /*
            r9 = this;
            int r0 = com.baidu.mapapi.common.SysOSUtil.getDensityDpi()
            r1 = 180(0xb4, float:2.52E-43)
            if (r0 >= r1) goto L_0x000b
            java.lang.String r1 = "logo_l.png"
            goto L_0x000d
        L_0x000b:
            java.lang.String r1 = "logo_h.png"
        L_0x000d:
            android.graphics.Bitmap r2 = com.baidu.mapsdkplatform.comapi.commonutils.a.a((java.lang.String) r1, (android.content.Context) r10)
            r1 = 480(0x1e0, float:6.73E-43)
            if (r0 <= r1) goto L_0x0031
            android.graphics.Matrix r7 = new android.graphics.Matrix
            r7.<init>()
            r0 = 1073741824(0x40000000, float:2.0)
        L_0x001c:
            r7.postScale(r0, r0)
            r3 = 0
            r4 = 0
            int r5 = r2.getWidth()
            int r6 = r2.getHeight()
            r8 = 1
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r2, r3, r4, r5, r6, r7, r8)
            r9.i = r0
            goto L_0x0041
        L_0x0031:
            r3 = 320(0x140, float:4.48E-43)
            if (r0 <= r3) goto L_0x003f
            if (r0 > r1) goto L_0x003f
            android.graphics.Matrix r7 = new android.graphics.Matrix
            r7.<init>()
            r0 = 1069547520(0x3fc00000, float:1.5)
            goto L_0x001c
        L_0x003f:
            r9.i = r2
        L_0x0041:
            android.graphics.Bitmap r0 = r9.i
            if (r0 == 0) goto L_0x0056
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r10)
            r9.h = r0
            android.graphics.Bitmap r10 = r9.i
            r0.setImageBitmap(r10)
            android.widget.ImageView r10 = r9.h
            r9.addView(r10)
        L_0x0056:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.map.WearMapView.c(android.content.Context):void");
    }

    private void d() {
        l lVar = this.f;
        if (lVar != null) {
            lVar.b();
        }
    }

    private void d(Context context) {
        am amVar = new am(context, true);
        this.j = amVar;
        if (amVar.a()) {
            this.j.b((View.OnClickListener) new ab(this));
            this.j.a((View.OnClickListener) new ac(this));
            addView(this.j);
        }
    }

    private void e() {
        l lVar = this.f;
        if (lVar != null) {
            lVar.c();
        }
    }

    private void e(Context context) {
        this.n = new RelativeLayout(context);
        this.n.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        this.p = new TextView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(14);
        this.p.setTextColor(Color.parseColor("#FFFFFF"));
        this.p.setTextSize(2, 11.0f);
        TextView textView = this.p;
        textView.setTypeface(textView.getTypeface(), 1);
        this.p.setLayoutParams(layoutParams);
        this.p.setId(Integer.MAX_VALUE);
        this.n.addView(this.p);
        this.q = new TextView(context);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.addRule(14);
        this.q.setTextColor(Color.parseColor("#000000"));
        this.q.setTextSize(2, 11.0f);
        this.q.setLayoutParams(layoutParams2);
        this.n.addView(this.q);
        this.r = new ImageView(context);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.addRule(14);
        layoutParams3.addRule(3, this.p.getId());
        this.r.setLayoutParams(layoutParams3);
        Bitmap a2 = com.baidu.mapsdkplatform.comapi.commonutils.a.a("icon_scale.9.png", context);
        byte[] ninePatchChunk = a2.getNinePatchChunk();
        NinePatch.isNinePatchChunk(ninePatchChunk);
        this.r.setBackgroundDrawable(new NinePatchDrawable(a2, ninePatchChunk, new Rect(), (String) null));
        this.n.addView(this.r);
        addView(this.n);
    }

    @Deprecated
    public static void setCustomMapStylePath(String str) {
        if (str == null || str.length() == 0) {
            throw new RuntimeException("BDMapSDKException: customMapStylePath String is illegal");
        } else if (new File(str).exists()) {
            c = str;
        } else {
            throw new RuntimeException("BDMapSDKException: please check whether the customMapStylePath file exits");
        }
    }

    @Deprecated
    public static void setIconCustom(int i2) {
        e = i2;
    }

    @Deprecated
    public static void setLoadCustomMapStyleFileMode(int i2) {
        d = i2;
    }

    @Deprecated
    public static void setMapCustomEnable(boolean z2) {
        k.a(z2);
    }

    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof MapViewLayoutParams) {
            super.addView(view, layoutParams);
        }
    }

    public final BaiduMap getMap() {
        this.g.c = this;
        return this.g;
    }

    public final int getMapLevel() {
        return x.get((int) this.f.a().E().a).intValue();
    }

    public int getScaleControlViewHeight() {
        return this.G;
    }

    public int getScaleControlViewWidth() {
        return this.H;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        this.a = windowInsets.isRound() ? ScreenShape.ROUND : ScreenShape.RECTANGLE;
        return windowInsets;
    }

    public void onCreate(Context context, Bundle bundle) {
        if (bundle != null) {
            MapStatus mapStatus = (MapStatus) bundle.getParcelable("mapstatus");
            if (this.l != null) {
                this.l = (Point) bundle.getParcelable("scalePosition");
            }
            if (this.m != null) {
                this.m = (Point) bundle.getParcelable("zoomPosition");
            }
            this.y = bundle.getBoolean("mZoomControlEnabled");
            this.z = bundle.getBoolean("mScaleControlEnabled");
            setPadding(bundle.getInt("paddingLeft"), bundle.getInt("paddingTop"), bundle.getInt("paddingRight"), bundle.getInt("paddingBottom"));
            a(context, new BaiduMapOptions().mapStatus(mapStatus));
        }
    }

    public final void onDestroy() {
        Context context = this.w;
        if (context != null) {
            this.f.b(context.hashCode());
        }
        Bitmap bitmap = this.i;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.i.recycle();
            this.i = null;
        }
        this.j.b();
        BMapManager.destroy();
        k.b();
        AnimationTask animationTask = this.mTask;
        if (animationTask != null) {
            animationTask.cancel();
        }
        this.w = null;
    }

    public final void onDismiss() {
        removeAllViews();
    }

    public final void onEnterAmbient(Bundle bundle) {
        a(0);
    }

    public void onExitAmbient() {
        a(1);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        AnimationTask animationTask;
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                Timer timer = new Timer();
                this.mTimer = timer;
                if (!(timer == null || (animationTask = this.mTask) == null)) {
                    animationTask.cancel();
                }
                AnimationTask animationTask2 = new AnimationTask();
                this.mTask = animationTask2;
                this.mTimer.schedule(animationTask2, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
        } else if (this.j.getVisibility() == 0) {
            Timer timer2 = this.mTimer;
            if (timer2 != null) {
                if (this.mTask != null) {
                    timer2.cancel();
                    this.mTask.cancel();
                }
                this.mTimer = null;
                this.mTask = null;
            }
        } else if (this.j.getVisibility() == 4) {
            if (this.mTimer != null) {
                AnimationTask animationTask3 = this.mTask;
                if (animationTask3 != null) {
                    animationTask3.cancel();
                }
                this.mTimer.cancel();
                this.mTask = null;
                this.mTimer = null;
            }
            a(false);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public final void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
        float f2;
        int i6;
        int i7;
        int i8;
        int i9;
        int childCount = getChildCount();
        a((View) this.h);
        float f3 = 1.0f;
        if (((getWidth() - this.C) - this.D) - this.h.getMeasuredWidth() <= 0 || ((getHeight() - this.E) - this.F) - this.h.getMeasuredHeight() <= 0) {
            this.C = 0;
            this.D = 0;
            this.F = 0;
            this.E = 0;
            f2 = 1.0f;
        } else {
            float width = ((float) ((getWidth() - this.C) - this.D)) / ((float) getWidth());
            f3 = ((float) ((getHeight() - this.E) - this.F)) / ((float) getHeight());
            f2 = width;
        }
        for (int i10 = 0; i10 < childCount; i10++) {
            View childAt = getChildAt(i10);
            l lVar = this.f;
            if (childAt == lVar) {
                lVar.layout(0, 0, getWidth(), getHeight());
            } else if (childAt == this.h) {
                int i11 = (int) (((float) this.F) + (12.0f * f3));
                if (this.a == ScreenShape.ROUND) {
                    a((View) this.j);
                    int i12 = s / 2;
                    i9 = a(i12, this.j.getMeasuredWidth() / 2);
                    i8 = ((s / 2) - a(i12, i12 - i9)) + u;
                } else {
                    i9 = 0;
                    i8 = 0;
                }
                int i13 = (t - i9) - i11;
                int i14 = s - i8;
                this.h.layout(i14 - this.h.getMeasuredWidth(), i13 - this.h.getMeasuredHeight(), i14, i13);
            } else {
                am amVar = this.j;
                if (childAt == amVar) {
                    if (amVar.a()) {
                        a((View) this.j);
                        Point point = this.m;
                        if (point == null) {
                            int a2 = (int) ((12.0f * f3) + ((float) this.E) + ((float) (this.a == ScreenShape.ROUND ? a(t / 2, this.j.getMeasuredWidth() / 2) : 0)));
                            int measuredWidth = (s - this.j.getMeasuredWidth()) / 2;
                            this.j.layout(measuredWidth, a2, this.j.getMeasuredWidth() + measuredWidth, this.j.getMeasuredHeight() + a2);
                        } else {
                            this.j.layout(point.x, this.m.y, this.m.x + this.j.getMeasuredWidth(), this.m.y + this.j.getMeasuredHeight());
                        }
                    }
                } else if (childAt == this.n) {
                    if (this.a == ScreenShape.ROUND) {
                        a((View) this.j);
                        int i15 = s / 2;
                        i7 = a(i15, this.j.getMeasuredWidth() / 2);
                        i6 = ((s / 2) - a(i15, i15 - i7)) + u;
                    } else {
                        i7 = 0;
                        i6 = 0;
                    }
                    a((View) this.n);
                    Point point2 = this.l;
                    if (point2 == null) {
                        this.H = this.n.getMeasuredWidth();
                        this.G = this.n.getMeasuredHeight();
                        int i16 = (int) (((float) this.C) + (5.0f * f2) + ((float) i6));
                        int i17 = (t - ((int) (((float) this.F) + (12.0f * f3)))) - i7;
                        this.n.layout(i16, i17 - this.n.getMeasuredHeight(), this.H + i16, i17);
                    } else {
                        this.n.layout(point2.x, this.l.y, this.l.x + this.n.getMeasuredWidth(), this.l.y + this.n.getMeasuredHeight());
                    }
                } else {
                    SwipeDismissView swipeDismissView = this.o;
                    if (childAt == swipeDismissView) {
                        a((View) swipeDismissView);
                        this.o.layout(0, 0, this.o.getMeasuredWidth(), t);
                    } else {
                        ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                        if (layoutParams instanceof MapViewLayoutParams) {
                            MapViewLayoutParams mapViewLayoutParams = (MapViewLayoutParams) layoutParams;
                            Point a3 = mapViewLayoutParams.c == MapViewLayoutParams.ELayoutMode.absoluteMode ? mapViewLayoutParams.b : this.f.a().a(CoordUtil.ll2mc(mapViewLayoutParams.a));
                            a(childAt);
                            int measuredWidth2 = childAt.getMeasuredWidth();
                            int measuredHeight = childAt.getMeasuredHeight();
                            float f4 = mapViewLayoutParams.d;
                            float f5 = mapViewLayoutParams.e;
                            int i18 = (int) (((float) a3.x) - (f4 * ((float) measuredWidth2)));
                            int i19 = ((int) (((float) a3.y) - (f5 * ((float) measuredHeight)))) + mapViewLayoutParams.f;
                            childAt.layout(i18, i19, measuredWidth2 + i18, measuredHeight + i19);
                        }
                    }
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        BaiduMap baiduMap;
        if (bundle != null && (baiduMap = this.g) != null) {
            bundle.putParcelable("mapstatus", baiduMap.getMapStatus());
            Point point = this.l;
            if (point != null) {
                bundle.putParcelable("scalePosition", point);
            }
            Point point2 = this.m;
            if (point2 != null) {
                bundle.putParcelable("zoomPosition", point2);
            }
            bundle.putBoolean("mZoomControlEnabled", this.y);
            bundle.putBoolean("mScaleControlEnabled", this.z);
            bundle.putInt("paddingLeft", this.C);
            bundle.putInt("paddingTop", this.E);
            bundle.putInt("paddingRight", this.D);
            bundle.putInt("paddingBottom", this.F);
        }
    }

    public void removeView(View view) {
        if (view != this.h) {
            super.removeView(view);
        }
    }

    public void setCustomStyleFilePathAndMode(String str, int i2) {
        a(str, i2);
    }

    public void setMapCustomStyle(MapCustomStyleOptions mapCustomStyleOptions, MapView.CustomMapStyleCallBack customMapStyleCallBack) {
        if (mapCustomStyleOptions != null) {
            String customMapStyleId = mapCustomStyleOptions.getCustomMapStyleId();
            if (customMapStyleId == null || customMapStyleId.isEmpty()) {
                String localCustomStyleFilePath = mapCustomStyleOptions.getLocalCustomStyleFilePath();
                if (localCustomStyleFilePath != null && !localCustomStyleFilePath.isEmpty()) {
                    a(localCustomStyleFilePath, 0);
                    return;
                }
                return;
            }
            h.a().a(this.w, customMapStyleId, (h.a) new z(this, customMapStyleCallBack, mapCustomStyleOptions));
        }
    }

    public void setMapCustomStyleEnable(boolean z2) {
        l lVar = this.f;
        if (lVar != null) {
            lVar.a().n(z2);
        }
    }

    public void setMapCustomStylePath(String str) {
        a(str, 0);
    }

    public void setOnDismissCallbackListener(OnDismissCallback onDismissCallback) {
        SwipeDismissView swipeDismissView = this.o;
        if (swipeDismissView != null) {
            swipeDismissView.setCallback(onDismissCallback);
        }
    }

    public void setPadding(int i2, int i3, int i4, int i5) {
        this.C = i2;
        this.E = i3;
        this.D = i4;
        this.F = i5;
    }

    public void setScaleControlPosition(Point point) {
        if (point != null && point.x >= 0 && point.y >= 0 && point.x <= getWidth() && point.y <= getHeight()) {
            this.l = point;
            requestLayout();
        }
    }

    public void setShape(ScreenShape screenShape) {
        this.a = screenShape;
    }

    public void setViewAnimitionEnable(boolean z2) {
        this.k = z2;
    }

    public void setZoomControlsPosition(Point point) {
        if (point != null && point.x >= 0 && point.y >= 0 && point.x <= getWidth() && point.y <= getHeight()) {
            this.m = point;
            requestLayout();
        }
    }

    public void showScaleControl(boolean z2) {
        this.n.setVisibility(z2 ? 0 : 8);
        this.z = z2;
    }

    public void showZoomControls(boolean z2) {
        if (this.j.a()) {
            this.j.setVisibility(z2 ? 0 : 8);
            this.y = z2;
        }
    }
}

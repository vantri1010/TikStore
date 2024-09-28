package com.baidu.mapapi.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapsdkplatform.comapi.commonutils.a;
import com.baidu.mapsdkplatform.comapi.map.ab;
import com.baidu.mapsdkplatform.comapi.map.am;
import com.baidu.mapsdkplatform.comapi.map.e;
import com.baidu.mapsdkplatform.comapi.map.h;
import com.baidu.mapsdkplatform.comapi.map.k;
import com.baidu.mapsdkplatform.comapi.map.l;
import com.baidu.mapsdkplatform.comapi.map.n;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import java.io.File;

public final class MapView extends ViewGroup {
    private static final String a = MapView.class.getSimpleName();
    private static String b;
    private static int c = 0;
    private static int d = 0;
    /* access modifiers changed from: private */
    public static final SparseIntArray q;
    private int A;
    /* access modifiers changed from: private */
    public l e;
    private BaiduMap f;
    private ImageView g;
    private Bitmap h;
    private am i;
    private Point j;
    private Point k;
    private RelativeLayout l;
    /* access modifiers changed from: private */
    public TextView m;
    /* access modifiers changed from: private */
    public TextView n;
    /* access modifiers changed from: private */
    public ImageView o;
    private Context p;
    private int r = LogoPosition.logoPostionleftBottom.ordinal();
    private boolean s = true;
    private boolean t = true;
    /* access modifiers changed from: private */
    public float u;
    private int v;
    private int w;
    private int x;
    private int y;
    private int z;

    public interface CustomMapStyleCallBack {
        boolean onCustomMapStyleLoadFailed(int i, String str, String str2);

        boolean onCustomMapStyleLoadSuccess(boolean z, String str);

        boolean onPreLoadLastCustomMapStyle(String str);
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        q = sparseIntArray;
        sparseIntArray.append(3, 2000000);
        q.append(4, EditInputFilter.MAX_VALUE);
        q.append(5, 500000);
        q.append(6, 200000);
        q.append(7, DefaultOggSeeker.MATCH_BYTE_RANGE);
        q.append(8, 50000);
        q.append(9, 25000);
        q.append(10, 20000);
        q.append(11, 10000);
        q.append(12, 5000);
        q.append(13, 2000);
        q.append(14, 1000);
        q.append(15, 500);
        q.append(16, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        q.append(17, 100);
        q.append(18, 50);
        q.append(19, 20);
        q.append(20, 10);
        q.append(21, 5);
        q.append(22, 2);
        q.append(23, 2);
        q.append(24, 2);
        q.append(25, 2);
        q.append(26, 2);
    }

    public MapView(Context context) {
        super(context);
        a(context, (BaiduMapOptions) null);
    }

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, (BaiduMapOptions) null);
    }

    public MapView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        a(context, (BaiduMapOptions) null);
    }

    public MapView(Context context, BaiduMapOptions baiduMapOptions) {
        super(context);
        a(context, baiduMapOptions);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.content.Context r10) {
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
            if (r2 != 0) goto L_0x0014
            return
        L_0x0014:
            r1 = 480(0x1e0, float:6.73E-43)
            if (r0 <= r1) goto L_0x0034
            android.graphics.Matrix r7 = new android.graphics.Matrix
            r7.<init>()
            r0 = 1073741824(0x40000000, float:2.0)
        L_0x001f:
            r7.postScale(r0, r0)
            r3 = 0
            r4 = 0
            int r5 = r2.getWidth()
            int r6 = r2.getHeight()
            r8 = 1
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r2, r3, r4, r5, r6, r7, r8)
            r9.h = r0
            goto L_0x0042
        L_0x0034:
            r1 = 320(0x140, float:4.48E-43)
            if (r0 <= r1) goto L_0x0040
            android.graphics.Matrix r7 = new android.graphics.Matrix
            r7.<init>()
            r0 = 1069547520(0x3fc00000, float:1.5)
            goto L_0x001f
        L_0x0040:
            r9.h = r2
        L_0x0042:
            android.graphics.Bitmap r0 = r9.h
            if (r0 == 0) goto L_0x0057
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r10)
            r9.g = r0
            android.graphics.Bitmap r10 = r9.h
            r0.setImageBitmap(r10)
            android.widget.ImageView r10 = r9.g
            r9.addView(r10)
        L_0x0057:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.map.MapView.a(android.content.Context):void");
    }

    private void a(Context context, BaiduMapOptions baiduMapOptions) {
        this.p = context;
        k.a();
        BMapManager.init();
        a(context, baiduMapOptions, b, c);
        this.f = new BaiduMap(this.e);
        a(context);
        b(context);
        if (baiduMapOptions != null && !baiduMapOptions.h) {
            this.i.setVisibility(4);
        }
        c(context);
        if (baiduMapOptions != null && !baiduMapOptions.i) {
            this.l.setVisibility(4);
        }
        if (!(baiduMapOptions == null || baiduMapOptions.j == null)) {
            this.r = baiduMapOptions.j.ordinal();
        }
        if (!(baiduMapOptions == null || baiduMapOptions.l == null)) {
            this.k = baiduMapOptions.l;
        }
        if (baiduMapOptions != null && baiduMapOptions.k != null) {
            this.j = baiduMapOptions.k;
        }
    }

    private void a(Context context, BaiduMapOptions baiduMapOptions, String str, int i2) {
        if (baiduMapOptions == null) {
            this.e = new l(context, (ab) null, str, i2);
        } else {
            this.e = new l(context, baiduMapOptions.a(), str, i2);
        }
        addView(this.e);
        this.e.a().a((n) new m(this));
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

    /* access modifiers changed from: private */
    public void a(String str, int i2) {
        if (this.e != null) {
            if (i2 != 0 && 1 != i2) {
                throw new RuntimeException("BDMapSDKException: loadCustomStyleFileMode is illegal. Only support 0-local, 1-server");
            } else if (TextUtils.isEmpty(str)) {
                throw new RuntimeException("BDMapSDKException: customMapStyleFilePath String is illegal");
            } else if (new File(str).exists()) {
                this.e.a().a(str, i2);
            } else {
                throw new RuntimeException("BDMapSDKException: please check whether the customMapStylePath file exits");
            }
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        if (this.i.a()) {
            float f2 = this.e.a().E().a;
            boolean z2 = true;
            this.i.b(f2 > this.e.a().b);
            am amVar = this.i;
            if (f2 >= this.e.a().a) {
                z2 = false;
            }
            amVar.a(z2);
        }
    }

    private void b(Context context) {
        am amVar = new am(context, false);
        this.i = amVar;
        if (amVar.a()) {
            this.i.b((View.OnClickListener) new n(this));
            this.i.a((View.OnClickListener) new o(this));
            addView(this.i);
        }
    }

    private void c(Context context) {
        this.l = new RelativeLayout(context);
        this.l.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        this.m = new TextView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(14);
        this.m.setTextColor(Color.parseColor("#FFFFFF"));
        this.m.setTextSize(2, 11.0f);
        TextView textView = this.m;
        textView.setTypeface(textView.getTypeface(), 1);
        this.m.setLayoutParams(layoutParams);
        this.m.setId(Integer.MAX_VALUE);
        this.l.addView(this.m);
        this.n = new TextView(context);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.addRule(14);
        this.n.setTextColor(Color.parseColor("#000000"));
        this.n.setTextSize(2, 11.0f);
        this.n.setLayoutParams(layoutParams2);
        this.l.addView(this.n);
        this.o = new ImageView(context);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.addRule(14);
        layoutParams3.addRule(3, this.m.getId());
        this.o.setLayoutParams(layoutParams3);
        Bitmap a2 = a.a("icon_scale.9.png", context);
        if (a2 != null) {
            byte[] ninePatchChunk = a2.getNinePatchChunk();
            if (NinePatch.isNinePatchChunk(ninePatchChunk)) {
                this.o.setBackgroundDrawable(new NinePatchDrawable(a2, ninePatchChunk, new Rect(), (String) null));
            }
        }
        this.l.addView(this.o);
        addView(this.l);
    }

    @Deprecated
    public static void setCustomMapStylePath(String str) {
        if (str == null || str.length() == 0) {
            throw new RuntimeException("BDMapSDKException: customMapStylePath String is illegal");
        } else if (new File(str).exists()) {
            b = str;
        } else {
            throw new RuntimeException("BDMapSDKException: please check whether the customMapStylePath file exits");
        }
    }

    @Deprecated
    public static void setIconCustom(int i2) {
        d = i2;
    }

    @Deprecated
    public static void setLoadCustomMapStyleFileMode(int i2) {
        c = i2;
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

    public void cancelRenderMap() {
        this.e.a().x(false);
        this.e.a().P().clear();
    }

    public final LogoPosition getLogoPosition() {
        int i2 = this.r;
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? i2 != 5 ? LogoPosition.logoPostionleftBottom : LogoPosition.logoPostionRightTop : LogoPosition.logoPostionRightBottom : LogoPosition.logoPostionCenterTop : LogoPosition.logoPostionCenterBottom : LogoPosition.logoPostionleftTop;
    }

    public final BaiduMap getMap() {
        this.f.a = this;
        return this.f;
    }

    public final int getMapLevel() {
        return q.get(Math.round(this.e.a().E().a));
    }

    public Point getScaleControlPosition() {
        return this.j;
    }

    public int getScaleControlViewHeight() {
        return this.z;
    }

    public int getScaleControlViewWidth() {
        return this.A;
    }

    public Point getZoomControlsPosition() {
        return this.k;
    }

    public boolean handleMultiTouch(float f2, float f3, float f4, float f5) {
        l lVar = this.e;
        return lVar != null && lVar.a(f2, f3, f4, f5);
    }

    public void handleTouchDown(float f2, float f3) {
        l lVar = this.e;
        if (lVar != null) {
            lVar.a(f2, f3);
        }
    }

    public boolean handleTouchMove(float f2, float f3) {
        l lVar = this.e;
        return lVar != null && lVar.c(f2, f3);
    }

    public boolean handleTouchUp(float f2, float f3) {
        l lVar = this.e;
        if (lVar == null) {
            return false;
        }
        return lVar.b(f2, f3);
    }

    public boolean inRangeOfView(float f2, float f3) {
        l lVar = this.e;
        return lVar != null && lVar.d(f2, f3);
    }

    public void onCreate(Context context, Bundle bundle) {
        if (bundle != null) {
            MapStatus mapStatus = (MapStatus) bundle.getParcelable("mapstatus");
            if (this.j != null) {
                this.j = (Point) bundle.getParcelable("scalePosition");
            }
            if (this.k != null) {
                this.k = (Point) bundle.getParcelable("zoomPosition");
            }
            this.s = bundle.getBoolean("mZoomControlEnabled");
            this.t = bundle.getBoolean("mScaleControlEnabled");
            this.r = bundle.getInt("logoPosition");
            setPadding(bundle.getInt("paddingLeft"), bundle.getInt("paddingTop"), bundle.getInt("paddingRight"), bundle.getInt("paddingBottom"));
            a(context, new BaiduMapOptions().mapStatus(mapStatus));
        }
    }

    public final void onDestroy() {
        Context context = this.p;
        if (context != null) {
            this.e.b(context.hashCode());
        }
        Bitmap bitmap = this.h;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.h.recycle();
            this.h = null;
        }
        if (b != null) {
            b = null;
        }
        this.i.b();
        BMapManager.destroy();
        k.b();
        this.p = null;
    }

    /* access modifiers changed from: protected */
    public final void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
        float f2;
        Point point;
        int i6;
        int i7;
        int height;
        int childCount = getChildCount();
        a((View) this.g);
        float f3 = 1.0f;
        if (((getWidth() - this.v) - this.w) - this.g.getMeasuredWidth() <= 0 || ((getHeight() - this.x) - this.y) - this.g.getMeasuredHeight() <= 0) {
            this.v = 0;
            this.w = 0;
            this.y = 0;
            this.x = 0;
            f2 = 1.0f;
        } else {
            f3 = ((float) ((getWidth() - this.v) - this.w)) / ((float) getWidth());
            f2 = ((float) ((getHeight() - this.x) - this.y)) / ((float) getHeight());
        }
        for (int i8 = 0; i8 < childCount; i8++) {
            View childAt = getChildAt(i8);
            if (childAt != null) {
                l lVar = this.e;
                if (childAt == lVar) {
                    lVar.layout(0, 0, getWidth(), getHeight());
                } else {
                    ImageView imageView = this.g;
                    if (childAt == imageView) {
                        float f4 = f3 * 5.0f;
                        int i9 = (int) (((float) this.v) + f4);
                        int i10 = (int) (((float) this.w) + f4);
                        float f5 = 5.0f * f2;
                        int i11 = (int) (((float) this.x) + f5);
                        int i12 = (int) (((float) this.y) + f5);
                        int i13 = this.r;
                        if (i13 != 1) {
                            if (i13 == 2) {
                                height = getHeight() - i12;
                                i11 = height - this.g.getMeasuredHeight();
                            } else if (i13 != 3) {
                                if (i13 == 4) {
                                    i7 = getHeight() - i12;
                                    i11 = i7 - this.g.getMeasuredHeight();
                                } else if (i13 != 5) {
                                    i7 = getHeight() - i12;
                                    i6 = this.g.getMeasuredWidth() + i9;
                                    i11 = i7 - this.g.getMeasuredHeight();
                                } else {
                                    i7 = i11 + imageView.getMeasuredHeight();
                                }
                                i6 = getWidth() - i10;
                                i9 = i6 - this.g.getMeasuredWidth();
                            } else {
                                height = i11 + imageView.getMeasuredHeight();
                            }
                            i9 = (((getWidth() - this.g.getMeasuredWidth()) + this.v) - this.w) / 2;
                            i6 = (((getWidth() + this.g.getMeasuredWidth()) + this.v) - this.w) / 2;
                        } else {
                            i7 = imageView.getMeasuredHeight() + i11;
                            i6 = this.g.getMeasuredWidth() + i9;
                        }
                        this.g.layout(i9, i11, i6, i7);
                    } else {
                        am amVar = this.i;
                        if (childAt != amVar) {
                            RelativeLayout relativeLayout = this.l;
                            if (childAt == relativeLayout) {
                                a((View) relativeLayout);
                                Point point2 = this.j;
                                if (point2 == null) {
                                    this.A = this.l.getMeasuredWidth();
                                    this.z = this.l.getMeasuredHeight();
                                    int i14 = (int) (((float) this.v) + (5.0f * f3));
                                    int height2 = (getHeight() - ((int) ((((float) this.y) + (f2 * 5.0f)) + 56.0f))) - this.g.getMeasuredHeight();
                                    this.l.layout(i14, height2, this.A + i14, this.z + height2);
                                } else {
                                    this.l.layout(point2.x, this.j.y, this.j.x + this.l.getMeasuredWidth(), this.j.y + this.l.getMeasuredHeight());
                                }
                            } else {
                                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                                if (layoutParams instanceof MapViewLayoutParams) {
                                    MapViewLayoutParams mapViewLayoutParams = (MapViewLayoutParams) layoutParams;
                                    if (mapViewLayoutParams.c == MapViewLayoutParams.ELayoutMode.absoluteMode) {
                                        point = mapViewLayoutParams.b;
                                    } else {
                                        point = this.e.a().a(CoordUtil.ll2mc(mapViewLayoutParams.a));
                                    }
                                    a(childAt);
                                    int measuredWidth = childAt.getMeasuredWidth();
                                    int measuredHeight = childAt.getMeasuredHeight();
                                    float f6 = mapViewLayoutParams.d;
                                    float f7 = mapViewLayoutParams.e;
                                    int i15 = (int) (((float) point.x) - (f6 * ((float) measuredWidth)));
                                    int i16 = ((int) (((float) point.y) - (f7 * ((float) measuredHeight)))) + mapViewLayoutParams.f;
                                    childAt.layout(i15, i16, measuredWidth + i15, measuredHeight + i16);
                                }
                            }
                        } else if (amVar.a()) {
                            a((View) this.i);
                            Point point3 = this.k;
                            if (point3 == null) {
                                int height3 = (int) ((((float) (getHeight() - 15)) * f2) + ((float) this.x));
                                int width = (int) ((((float) (getWidth() - 15)) * f3) + ((float) this.v));
                                int measuredWidth2 = width - this.i.getMeasuredWidth();
                                int measuredHeight2 = height3 - this.i.getMeasuredHeight();
                                if (this.r == 4) {
                                    height3 -= this.g.getMeasuredHeight();
                                    measuredHeight2 -= this.g.getMeasuredHeight();
                                }
                                this.i.layout(measuredWidth2, measuredHeight2, width, height3);
                            } else {
                                this.i.layout(point3.x, this.k.y, this.k.x + this.i.getMeasuredWidth(), this.k.y + this.i.getMeasuredHeight());
                            }
                        }
                    }
                }
            }
        }
    }

    public final void onPause() {
        this.e.onPause();
    }

    public final void onResume() {
        this.e.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        BaiduMap baiduMap;
        if (bundle != null && (baiduMap = this.f) != null) {
            bundle.putParcelable("mapstatus", baiduMap.getMapStatus());
            Point point = this.j;
            if (point != null) {
                bundle.putParcelable("scalePosition", point);
            }
            Point point2 = this.k;
            if (point2 != null) {
                bundle.putParcelable("zoomPosition", point2);
            }
            bundle.putBoolean("mZoomControlEnabled", this.s);
            bundle.putBoolean("mScaleControlEnabled", this.t);
            bundle.putInt("logoPosition", this.r);
            bundle.putInt("paddingLeft", this.v);
            bundle.putInt("paddingTop", this.x);
            bundle.putInt("paddingRight", this.w);
            bundle.putInt("paddingBottom", this.y);
        }
    }

    public void removeView(View view) {
        if (view != this.g) {
            super.removeView(view);
        }
    }

    public void renderMap() {
        e a2 = this.e.a();
        a2.x(true);
        a2.Q();
    }

    public void setCustomStyleFilePathAndMode(String str, int i2) {
        a(str, i2);
    }

    public final void setLogoPosition(LogoPosition logoPosition) {
        if (logoPosition == null) {
            logoPosition = LogoPosition.logoPostionleftBottom;
        }
        this.r = logoPosition.ordinal();
        requestLayout();
    }

    public void setMapCustomStyle(MapCustomStyleOptions mapCustomStyleOptions, CustomMapStyleCallBack customMapStyleCallBack) {
        if (mapCustomStyleOptions != null) {
            String customMapStyleId = mapCustomStyleOptions.getCustomMapStyleId();
            if (customMapStyleId == null || customMapStyleId.isEmpty()) {
                String localCustomStyleFilePath = mapCustomStyleOptions.getLocalCustomStyleFilePath();
                if (localCustomStyleFilePath != null && !localCustomStyleFilePath.isEmpty()) {
                    a(localCustomStyleFilePath, 0);
                    setMapCustomStyleEnable(true);
                    return;
                }
                return;
            }
            h.a().a(this.p, customMapStyleId, (h.a) new l(this, customMapStyleCallBack, mapCustomStyleOptions));
        }
    }

    public void setMapCustomStyleEnable(boolean z2) {
        l lVar = this.e;
        if (lVar != null) {
            lVar.a().n(z2);
        }
    }

    public void setMapCustomStylePath(String str) {
        a(str, 0);
    }

    public void setPadding(int i2, int i3, int i4, int i5) {
        this.v = i2;
        this.x = i3;
        this.w = i4;
        this.y = i5;
    }

    public void setScaleControlPosition(Point point) {
        if (point != null && point.x >= 0 && point.y >= 0 && point.x <= getWidth() && point.y <= getHeight()) {
            this.j = point;
            requestLayout();
        }
    }

    public void setUpViewEventToMapView(MotionEvent motionEvent) {
        this.e.onTouchEvent(motionEvent);
    }

    public final void setZOrderMediaOverlay(boolean z2) {
        l lVar = this.e;
        if (lVar != null) {
            lVar.setZOrderMediaOverlay(z2);
        }
    }

    public void setZoomControlsPosition(Point point) {
        if (point != null && point.x >= 0 && point.y >= 0 && point.x <= getWidth() && point.y <= getHeight()) {
            this.k = point;
            requestLayout();
        }
    }

    public void showScaleControl(boolean z2) {
        this.l.setVisibility(z2 ? 0 : 8);
        this.t = z2;
    }

    public void showZoomControls(boolean z2) {
        if (this.i.a()) {
            this.i.setVisibility(z2 ? 0 : 8);
            this.s = z2;
        }
    }
}

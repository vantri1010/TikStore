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
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapsdkplatform.comapi.commonutils.a;
import com.baidu.mapsdkplatform.comapi.map.ab;
import com.baidu.mapsdkplatform.comapi.map.ae;
import com.baidu.mapsdkplatform.comapi.map.am;
import com.baidu.mapsdkplatform.comapi.map.h;
import com.baidu.mapsdkplatform.comapi.map.k;
import com.baidu.mapsdkplatform.comapi.map.n;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import java.io.File;

public final class TextureMapView extends ViewGroup {
    private static final String a = TextureMapView.class.getSimpleName();
    private static String i;
    private static int j = 0;
    private static int k = 0;
    /* access modifiers changed from: private */
    public static final SparseArray<Integer> q;
    private int A;
    private int B;
    /* access modifiers changed from: private */
    public ae b;
    private BaiduMap c;
    private ImageView d;
    private Bitmap e;
    private am f;
    private Point g;
    private Point h;
    private RelativeLayout l;
    /* access modifiers changed from: private */
    public TextView m;
    /* access modifiers changed from: private */
    public TextView n;
    /* access modifiers changed from: private */
    public ImageView o;
    private Context p;
    /* access modifiers changed from: private */
    public float r;
    private n s;
    private int t = LogoPosition.logoPostionleftBottom.ordinal();
    private boolean u = true;
    private boolean v = true;
    private int w;
    private int x;
    private int y;
    private int z;

    static {
        SparseArray<Integer> sparseArray = new SparseArray<>();
        q = sparseArray;
        sparseArray.append(3, 2000000);
        q.append(4, Integer.valueOf(EditInputFilter.MAX_VALUE));
        q.append(5, 500000);
        q.append(6, 200000);
        q.append(7, Integer.valueOf(DefaultOggSeeker.MATCH_BYTE_RANGE));
        q.append(8, 50000);
        q.append(9, 25000);
        q.append(10, 20000);
        q.append(11, 10000);
        q.append(12, 5000);
        q.append(13, 2000);
        q.append(14, 1000);
        q.append(15, 500);
        q.append(16, Integer.valueOf(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION));
        q.append(17, 100);
        q.append(18, 50);
        q.append(19, 20);
        q.append(20, 10);
        q.append(21, 5);
        q.append(22, 2);
    }

    public TextureMapView(Context context) {
        super(context);
        a(context, (BaiduMapOptions) null);
    }

    public TextureMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, (BaiduMapOptions) null);
    }

    public TextureMapView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        a(context, (BaiduMapOptions) null);
    }

    public TextureMapView(Context context, BaiduMapOptions baiduMapOptions) {
        super(context);
        a(context, baiduMapOptions);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
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
            r9.e = r0
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
            r9.e = r2
        L_0x0041:
            android.graphics.Bitmap r0 = r9.e
            if (r0 == 0) goto L_0x0056
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r10)
            r9.d = r0
            android.graphics.Bitmap r10 = r9.e
            r0.setImageBitmap(r10)
            android.widget.ImageView r10 = r9.d
            r9.addView(r10)
        L_0x0056:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.map.TextureMapView.a(android.content.Context):void");
    }

    private void a(Context context, BaiduMapOptions baiduMapOptions) {
        setBackgroundColor(-1);
        this.p = context;
        k.a();
        BMapManager.init();
        a(context, baiduMapOptions, i, k);
        this.c = new BaiduMap(this.b);
        a(context);
        b(context);
        if (baiduMapOptions != null && !baiduMapOptions.h) {
            this.f.setVisibility(4);
        }
        c(context);
        if (baiduMapOptions != null && !baiduMapOptions.i) {
            this.l.setVisibility(4);
        }
        if (!(baiduMapOptions == null || baiduMapOptions.j == null)) {
            this.t = baiduMapOptions.j.ordinal();
        }
        if (!(baiduMapOptions == null || baiduMapOptions.l == null)) {
            this.h = baiduMapOptions.l;
        }
        if (baiduMapOptions != null && baiduMapOptions.k != null) {
            this.g = baiduMapOptions.k;
        }
    }

    private void a(Context context, BaiduMapOptions baiduMapOptions, String str, int i2) {
        if (baiduMapOptions == null) {
            this.b = new ae(context, (ab) null, str, i2);
        } else {
            this.b = new ae(context, baiduMapOptions.a(), str, i2);
        }
        addView(this.b);
        this.s = new v(this);
        this.b.b().a(this.s);
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
        if (this.b != null) {
            if (i2 != 0 && 1 != i2) {
                throw new RuntimeException("BDMapSDKException: loadCustomStyleFileMode is illegal. Only support 0-local, 1-server");
            } else if (TextUtils.isEmpty(str)) {
                throw new RuntimeException("BDMapSDKException: customMapStyleFilePath String is illegal");
            } else if (new File(str).exists()) {
                this.b.b().a(str, i2);
            } else {
                throw new RuntimeException("BDMapSDKException: please check whether the customMapStylePath file exits");
            }
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        if (this.f.a()) {
            float f2 = this.b.b().E().a;
            boolean z2 = true;
            this.f.b(f2 > this.b.b().b);
            am amVar = this.f;
            if (f2 >= this.b.b().a) {
                z2 = false;
            }
            amVar.a(z2);
        }
    }

    private void b(Context context) {
        am amVar = new am(context);
        this.f = amVar;
        if (amVar.a()) {
            this.f.b((View.OnClickListener) new w(this));
            this.f.a((View.OnClickListener) new x(this));
            addView(this.f);
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
        byte[] ninePatchChunk = a2.getNinePatchChunk();
        NinePatch.isNinePatchChunk(ninePatchChunk);
        this.o.setBackgroundDrawable(new NinePatchDrawable(a2, ninePatchChunk, new Rect(), (String) null));
        this.l.addView(this.o);
        addView(this.l);
    }

    @Deprecated
    public static void setCustomMapStylePath(String str) {
        if (str == null || str.length() == 0) {
            throw new RuntimeException("BDMapSDKException: customMapStylePath String is illegal");
        } else if (new File(str).exists()) {
            i = str;
        } else {
            throw new RuntimeException("BDMapSDKException: please check whether the customMapStylePath file exits");
        }
    }

    @Deprecated
    public static void setIconCustom(int i2) {
        k = i2;
    }

    @Deprecated
    public static void setLoadCustomMapStyleFileMode(int i2) {
        j = i2;
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

    public final LogoPosition getLogoPosition() {
        int i2 = this.t;
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? i2 != 5 ? LogoPosition.logoPostionleftBottom : LogoPosition.logoPostionRightTop : LogoPosition.logoPostionRightBottom : LogoPosition.logoPostionCenterTop : LogoPosition.logoPostionCenterBottom : LogoPosition.logoPostionleftTop;
    }

    public final BaiduMap getMap() {
        this.c.b = this;
        return this.c;
    }

    public final int getMapLevel() {
        return q.get((int) this.b.b().E().a).intValue();
    }

    public int getScaleControlViewHeight() {
        return this.B;
    }

    public int getScaleControlViewWidth() {
        return this.B;
    }

    public void onCreate(Context context, Bundle bundle) {
        if (bundle != null) {
            MapStatus mapStatus = (MapStatus) bundle.getParcelable("mapstatus");
            if (this.g != null) {
                this.g = (Point) bundle.getParcelable("scalePosition");
            }
            if (this.h != null) {
                this.h = (Point) bundle.getParcelable("zoomPosition");
            }
            this.u = bundle.getBoolean("mZoomControlEnabled");
            this.v = bundle.getBoolean("mScaleControlEnabled");
            this.t = bundle.getInt("logoPosition");
            setPadding(bundle.getInt("paddingLeft"), bundle.getInt("paddingTop"), bundle.getInt("paddingRight"), bundle.getInt("paddingBottom"));
            a(context, new BaiduMapOptions().mapStatus(mapStatus));
        }
    }

    public final void onDestroy() {
        Context context = this.p;
        if (context != null) {
            this.b.a(context.hashCode());
        }
        Bitmap bitmap = this.e;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.e.recycle();
        }
        this.f.b();
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
        a((View) this.d);
        float f3 = 1.0f;
        if (((getWidth() - this.w) - this.x) - this.d.getMeasuredWidth() <= 0 || ((getHeight() - this.y) - this.z) - this.d.getMeasuredHeight() <= 0) {
            this.w = 0;
            this.x = 0;
            this.z = 0;
            this.y = 0;
            f2 = 1.0f;
        } else {
            f3 = ((float) ((getWidth() - this.w) - this.x)) / ((float) getWidth());
            f2 = ((float) ((getHeight() - this.y) - this.z)) / ((float) getHeight());
        }
        for (int i8 = 0; i8 < childCount; i8++) {
            View childAt = getChildAt(i8);
            ae aeVar = this.b;
            if (childAt == aeVar) {
                aeVar.layout(0, 0, getWidth(), getHeight());
            } else {
                ImageView imageView = this.d;
                if (childAt == imageView) {
                    float f4 = f3 * 5.0f;
                    int i9 = (int) (((float) this.w) + f4);
                    int i10 = (int) (((float) this.x) + f4);
                    float f5 = 5.0f * f2;
                    int i11 = (int) (((float) this.y) + f5);
                    int i12 = (int) (((float) this.z) + f5);
                    int i13 = this.t;
                    if (i13 != 1) {
                        if (i13 == 2) {
                            height = getHeight() - i12;
                            i11 = height - this.d.getMeasuredHeight();
                        } else if (i13 != 3) {
                            if (i13 == 4) {
                                i7 = getHeight() - i12;
                                i11 = i7 - this.d.getMeasuredHeight();
                            } else if (i13 != 5) {
                                i7 = getHeight() - i12;
                                i6 = this.d.getMeasuredWidth() + i9;
                                i11 = i7 - this.d.getMeasuredHeight();
                            } else {
                                i7 = i11 + imageView.getMeasuredHeight();
                            }
                            i6 = getWidth() - i10;
                            i9 = i6 - this.d.getMeasuredWidth();
                        } else {
                            height = i11 + imageView.getMeasuredHeight();
                        }
                        i9 = (((getWidth() - this.d.getMeasuredWidth()) + this.w) - this.x) / 2;
                        i6 = (((getWidth() + this.d.getMeasuredWidth()) + this.w) - this.x) / 2;
                    } else {
                        i7 = imageView.getMeasuredHeight() + i11;
                        i6 = this.d.getMeasuredWidth() + i9;
                    }
                    this.d.layout(i9, i11, i6, i7);
                } else {
                    am amVar = this.f;
                    if (childAt != amVar) {
                        RelativeLayout relativeLayout = this.l;
                        if (childAt == relativeLayout) {
                            a((View) relativeLayout);
                            Point point2 = this.g;
                            if (point2 == null) {
                                this.B = this.l.getMeasuredWidth();
                                this.A = this.l.getMeasuredHeight();
                                int i14 = (int) (((float) this.w) + (5.0f * f3));
                                int height2 = (getHeight() - ((int) ((((float) this.z) + (f2 * 5.0f)) + 56.0f))) - this.d.getMeasuredHeight();
                                this.l.layout(i14, height2, this.B + i14, this.A + height2);
                            } else {
                                this.l.layout(point2.x, this.g.y, this.g.x + this.l.getMeasuredWidth(), this.g.y + this.l.getMeasuredHeight());
                            }
                        } else {
                            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                            if (layoutParams instanceof MapViewLayoutParams) {
                                MapViewLayoutParams mapViewLayoutParams = (MapViewLayoutParams) layoutParams;
                                if (mapViewLayoutParams.c == MapViewLayoutParams.ELayoutMode.absoluteMode) {
                                    point = mapViewLayoutParams.b;
                                } else {
                                    point = this.b.b().a(CoordUtil.ll2mc(mapViewLayoutParams.a));
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
                        a((View) this.f);
                        Point point3 = this.h;
                        if (point3 == null) {
                            int height3 = (int) ((((float) (getHeight() - 15)) * f2) + ((float) this.y));
                            int width = (int) ((((float) (getWidth() - 15)) * f3) + ((float) this.w));
                            int measuredWidth2 = width - this.f.getMeasuredWidth();
                            int measuredHeight2 = height3 - this.f.getMeasuredHeight();
                            if (this.t == 4) {
                                height3 -= this.d.getMeasuredHeight();
                                measuredHeight2 -= this.d.getMeasuredHeight();
                            }
                            this.f.layout(measuredWidth2, measuredHeight2, width, height3);
                        } else {
                            this.f.layout(point3.x, this.h.y, this.h.x + this.f.getMeasuredWidth(), this.h.y + this.f.getMeasuredHeight());
                        }
                    }
                }
            }
        }
    }

    public final void onPause() {
        this.b.d();
    }

    public final void onResume() {
        this.b.c();
    }

    public void onSaveInstanceState(Bundle bundle) {
        BaiduMap baiduMap;
        if (bundle != null && (baiduMap = this.c) != null) {
            bundle.putParcelable("mapstatus", baiduMap.getMapStatus());
            Point point = this.g;
            if (point != null) {
                bundle.putParcelable("scalePosition", point);
            }
            Point point2 = this.h;
            if (point2 != null) {
                bundle.putParcelable("zoomPosition", point2);
            }
            bundle.putBoolean("mZoomControlEnabled", this.u);
            bundle.putBoolean("mScaleControlEnabled", this.v);
            bundle.putInt("logoPosition", this.t);
            bundle.putInt("paddingLeft", this.w);
            bundle.putInt("paddingTop", this.y);
            bundle.putInt("paddingRight", this.x);
            bundle.putInt("paddingBottom", this.z);
        }
    }

    public void removeView(View view) {
        if (view != this.d) {
            super.removeView(view);
        }
    }

    public void setCustomStyleFilePathAndMode(String str, int i2) {
        a(str, i2);
    }

    public final void setLogoPosition(LogoPosition logoPosition) {
        if (logoPosition == null) {
            logoPosition = LogoPosition.logoPostionleftBottom;
        }
        this.t = logoPosition.ordinal();
        requestLayout();
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
            h.a().a(this.p, customMapStyleId, (h.a) new u(this, customMapStyleCallBack, mapCustomStyleOptions));
        }
    }

    public void setMapCustomStyleEnable(boolean z2) {
        ae aeVar = this.b;
        if (aeVar != null) {
            aeVar.b().n(z2);
        }
    }

    public void setMapCustomStylePath(String str) {
        a(str, 0);
    }

    public void setPadding(int i2, int i3, int i4, int i5) {
        this.w = i2;
        this.y = i3;
        this.x = i4;
        this.z = i5;
    }

    public void setScaleControlPosition(Point point) {
        if (point != null && point.x >= 0 && point.y >= 0 && point.x <= getWidth() && point.y <= getHeight()) {
            this.g = point;
            requestLayout();
        }
    }

    public void setZoomControlsPosition(Point point) {
        if (point != null && point.x >= 0 && point.y >= 0 && point.x <= getWidth() && point.y <= getHeight()) {
            this.h = point;
            requestLayout();
        }
    }

    public void showScaleControl(boolean z2) {
        this.l.setVisibility(z2 ? 0 : 8);
        this.v = z2;
    }

    public void showZoomControls(boolean z2) {
        if (this.f.a()) {
            this.f.setVisibility(z2 ? 0 : 8);
            this.u = z2;
        }
    }
}

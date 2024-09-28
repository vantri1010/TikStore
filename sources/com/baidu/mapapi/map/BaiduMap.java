package com.baidu.mapapi.map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.PointerIconCompat;
import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapsdkplatform.comapi.map.ac;
import com.baidu.mapsdkplatform.comapi.map.ad;
import com.baidu.mapsdkplatform.comapi.map.ae;
import com.baidu.mapsdkplatform.comapi.map.ak;
import com.baidu.mapsdkplatform.comapi.map.e;
import com.baidu.mapsdkplatform.comapi.map.l;
import com.baidu.mapsdkplatform.comapi.map.n;
import com.baidu.mapsdkplatform.comapi.map.q;
import com.king.zxing.util.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.microedition.khronos.opengles.GL10;

public class BaiduMap {
    public static final int MAP_TYPE_NONE = 3;
    public static final int MAP_TYPE_NORMAL = 1;
    public static final int MAP_TYPE_SATELLITE = 2;
    private static final String e = BaiduMap.class.getSimpleName();
    public static int mapStatusReason = 0;
    /* access modifiers changed from: private */
    public OnMyLocationClickListener A;
    /* access modifiers changed from: private */
    public SnapshotReadyCallback B;
    /* access modifiers changed from: private */
    public OnMapDrawFrameCallback C;
    /* access modifiers changed from: private */
    public OnBaseIndoorMapListener D;
    /* access modifiers changed from: private */
    public OnMapRenderValidDataListener E;
    /* access modifiers changed from: private */
    public OnSynchronizationListener F;
    /* access modifiers changed from: private */
    public TileOverlay G;
    /* access modifiers changed from: private */
    public HeatMap H;
    /* access modifiers changed from: private */
    public Lock I = new ReentrantLock();
    /* access modifiers changed from: private */
    public Lock J = new ReentrantLock();
    /* access modifiers changed from: private */
    public Map<String, InfoWindow> K;
    private Map<InfoWindow, Marker> L;
    /* access modifiers changed from: private */
    public Marker M;
    private MyLocationData N;
    private MyLocationConfiguration O;
    private boolean P;
    private boolean Q;
    private boolean R;
    /* access modifiers changed from: private */
    public boolean S;
    private Point T;
    MapView a;
    TextureMapView b;
    WearMapView c;
    ac d;
    /* access modifiers changed from: private */
    public Projection f;
    private UiSettings g;
    private l h;
    /* access modifiers changed from: private */
    public e i;
    private ae j;
    /* access modifiers changed from: private */
    public List<Overlay> k;
    /* access modifiers changed from: private */
    public List<Marker> l;
    /* access modifiers changed from: private */
    public List<Marker> m;
    private List<InfoWindow> n;
    private Overlay.a o;
    private InfoWindow.a p;
    /* access modifiers changed from: private */
    public OnMapStatusChangeListener q;
    /* access modifiers changed from: private */
    public OnMapTouchListener r;
    /* access modifiers changed from: private */
    public OnMapClickListener s;
    /* access modifiers changed from: private */
    public OnMapLoadedCallback t;
    /* access modifiers changed from: private */
    public OnMapRenderCallback u;
    /* access modifiers changed from: private */
    public OnMapDoubleClickListener v;
    /* access modifiers changed from: private */
    public OnMapLongClickListener w;
    /* access modifiers changed from: private */
    public CopyOnWriteArrayList<OnMarkerClickListener> x = new CopyOnWriteArrayList<>();
    /* access modifiers changed from: private */
    public CopyOnWriteArrayList<OnPolylineClickListener> y = new CopyOnWriteArrayList<>();
    /* access modifiers changed from: private */
    public OnMarkerDragListener z;

    public interface OnBaseIndoorMapListener {
        void onBaseIndoorMapMode(boolean z, MapBaseIndoorMapInfo mapBaseIndoorMapInfo);
    }

    public interface OnMapClickListener {
        void onMapClick(LatLng latLng);

        void onMapPoiClick(MapPoi mapPoi);
    }

    public interface OnMapDoubleClickListener {
        void onMapDoubleClick(LatLng latLng);
    }

    public interface OnMapDrawFrameCallback {
        void onMapDrawFrame(MapStatus mapStatus);

        @Deprecated
        void onMapDrawFrame(GL10 gl10, MapStatus mapStatus);
    }

    public interface OnMapLoadedCallback {
        void onMapLoaded();
    }

    public interface OnMapLongClickListener {
        void onMapLongClick(LatLng latLng);
    }

    public interface OnMapRenderCallback {
        void onMapRenderFinished();
    }

    public interface OnMapRenderValidDataListener {
        void onMapRenderValidData(boolean z, int i, String str);
    }

    public interface OnMapStatusChangeListener {
        public static final int REASON_API_ANIMATION = 2;
        public static final int REASON_DEVELOPER_ANIMATION = 3;
        public static final int REASON_GESTURE = 1;

        void onMapStatusChange(MapStatus mapStatus);

        void onMapStatusChangeFinish(MapStatus mapStatus);

        void onMapStatusChangeStart(MapStatus mapStatus);

        void onMapStatusChangeStart(MapStatus mapStatus, int i);
    }

    public interface OnMapTouchListener {
        void onTouch(MotionEvent motionEvent);
    }

    public interface OnMarkerClickListener {
        boolean onMarkerClick(Marker marker);
    }

    public interface OnMarkerDragListener {
        void onMarkerDrag(Marker marker);

        void onMarkerDragEnd(Marker marker);

        void onMarkerDragStart(Marker marker);
    }

    public interface OnMyLocationClickListener {
        boolean onMyLocationClick();
    }

    public interface OnPolylineClickListener {
        boolean onPolylineClick(Polyline polyline);
    }

    public interface OnSynchronizationListener {
        void onMapStatusChangeReason(int i);
    }

    public interface SnapshotReadyCallback {
        void onSnapshotReady(Bitmap bitmap);
    }

    BaiduMap(ae aeVar) {
        this.j = aeVar;
        this.i = aeVar.b();
        this.d = ac.TextureView;
        c();
    }

    BaiduMap(l lVar) {
        this.h = lVar;
        this.i = lVar.a();
        this.d = ac.GLSurfaceView;
        c();
    }

    private Point a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        int i2 = 0;
        int i3 = 0;
        for (String replaceAll : str.replaceAll("^\\{", "").replaceAll("\\}$", "").split(",")) {
            String[] split = replaceAll.replaceAll("\"", "").split(LogUtils.COLON);
            if ("x".equals(split[0])) {
                i2 = Integer.valueOf(split[1]).intValue();
            }
            if ("y".equals(split[0])) {
                i3 = Integer.valueOf(split[1]).intValue();
            }
        }
        return new Point(i2, i3);
    }

    private ad a(MapStatusUpdate mapStatusUpdate) {
        e eVar = this.i;
        if (eVar == null) {
            return null;
        }
        ad E2 = eVar.E();
        MapStatus a2 = mapStatusUpdate.a(this.i, getMapStatus());
        if (a2 == null) {
            return null;
        }
        return a2.b(E2);
    }

    /* access modifiers changed from: private */
    public String a(int i2) {
        if (i2 == 0) {
            return "数据请求成功";
        }
        switch (i2) {
            case PointerIconCompat.TYPE_WAIT:
                return "网络连接错误";
            case 1005:
                return "请求发送错误";
            case PointerIconCompat.TYPE_CELL:
                return "响应数据读取失败";
            case PointerIconCompat.TYPE_CROSSHAIR:
                return "返回响应数据过大，数据溢出";
            case PointerIconCompat.TYPE_TEXT:
                return "当前网络类型有问题";
            case PointerIconCompat.TYPE_VERTICAL_TEXT:
                return "数据不一致";
            case PointerIconCompat.TYPE_ALIAS:
                return "请求取消";
            case PointerIconCompat.TYPE_COPY:
                return "网络超时错误";
            case PointerIconCompat.TYPE_NO_DROP:
                return "网络连接超时";
            case PointerIconCompat.TYPE_ALL_SCROLL:
                return "网络发送超时";
            case PointerIconCompat.TYPE_HORIZONTAL_DOUBLE_ARROW:
                return "网络接收超时";
            case PointerIconCompat.TYPE_VERTICAL_DOUBLE_ARROW:
                return "DNS解析错误";
            case PointerIconCompat.TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW:
                return "DNS解析超时";
            case PointerIconCompat.TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW:
                return "网络写错误";
            case PointerIconCompat.TYPE_ZOOM_IN:
                return "SSL握手错误";
            case PointerIconCompat.TYPE_ZOOM_OUT:
                return "SSL握手超时";
            default:
                return "";
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.baidu.mapapi.map.InfoWindow r8) {
        /*
            r7 = this;
            if (r8 != 0) goto L_0x0003
            return
        L_0x0003:
            java.util.Map<com.baidu.mapapi.map.InfoWindow, com.baidu.mapapi.map.Marker> r0 = r7.L
            java.util.Set r0 = r0.keySet()
            boolean r1 = r0.isEmpty()
            r2 = 0
            if (r1 != 0) goto L_0x00aa
            boolean r0 = r0.contains(r8)
            if (r0 != 0) goto L_0x0018
            goto L_0x00aa
        L_0x0018:
            android.view.View r0 = r8.b
            r1 = 1
            if (r0 == 0) goto L_0x006e
            boolean r3 = r8.j
            if (r3 == 0) goto L_0x006e
            r0.destroyDrawingCache()
            com.baidu.mapapi.map.MapViewLayoutParams$Builder r3 = new com.baidu.mapapi.map.MapViewLayoutParams$Builder
            r3.<init>()
            com.baidu.mapapi.map.MapViewLayoutParams$ELayoutMode r4 = com.baidu.mapapi.map.MapViewLayoutParams.ELayoutMode.mapMode
            com.baidu.mapapi.map.MapViewLayoutParams$Builder r3 = r3.layoutMode(r4)
            com.baidu.mapapi.model.LatLng r4 = r8.c
            com.baidu.mapapi.map.MapViewLayoutParams$Builder r3 = r3.position(r4)
            int r4 = r8.f
            com.baidu.mapapi.map.MapViewLayoutParams$Builder r3 = r3.yOffset(r4)
            com.baidu.mapapi.map.MapViewLayoutParams r3 = r3.build()
            int[] r4 = com.baidu.mapapi.map.f.b
            com.baidu.mapsdkplatform.comapi.map.ac r5 = r7.d
            int r5 = r5.ordinal()
            r4 = r4[r5]
            if (r4 == r1) goto L_0x005c
            r5 = 2
            if (r4 == r5) goto L_0x004f
            goto L_0x0068
        L_0x004f:
            com.baidu.mapapi.map.MapView r4 = r7.a
            if (r4 == 0) goto L_0x0068
            r4.removeView(r0)
            com.baidu.mapapi.map.MapView r4 = r7.a
            r4.addView(r0, r3)
            goto L_0x0068
        L_0x005c:
            com.baidu.mapapi.map.TextureMapView r4 = r7.b
            if (r4 == 0) goto L_0x0068
            r4.removeView(r0)
            com.baidu.mapapi.map.TextureMapView r4 = r7.b
            r4.addView(r0, r3)
        L_0x0068:
            boolean r0 = r8.i
            if (r0 == 0) goto L_0x006e
            r0 = 0
            goto L_0x006f
        L_0x006e:
            r0 = 1
        L_0x006f:
            com.baidu.mapapi.map.BitmapDescriptor r3 = r7.b((com.baidu.mapapi.map.InfoWindow) r8)
            java.util.Map<com.baidu.mapapi.map.InfoWindow, com.baidu.mapapi.map.Marker> r4 = r7.L
            java.lang.Object r4 = r4.get(r8)
            com.baidu.mapapi.map.Marker r4 = (com.baidu.mapapi.map.Marker) r4
            if (r4 == 0) goto L_0x00a9
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            com.baidu.mapapi.map.BitmapDescriptor r6 = r8.a
            if (r6 == 0) goto L_0x0099
            com.baidu.mapsdkplatform.comapi.map.j r6 = com.baidu.mapsdkplatform.comapi.map.j.popup
            r4.type = r6
            r4.b = r3
            android.view.View r3 = r8.b
            java.lang.String r6 = "draw_with_view"
            if (r3 == 0) goto L_0x0096
            r5.putInt(r6, r1)
            goto L_0x0099
        L_0x0096:
            r5.putInt(r6, r2)
        L_0x0099:
            com.baidu.mapapi.model.LatLng r8 = r8.c
            r4.a = r8
            r4.a(r5)
            com.baidu.mapsdkplatform.comapi.map.e r8 = r7.i
            if (r8 == 0) goto L_0x00a9
            if (r0 == 0) goto L_0x00a9
            r8.c((android.os.Bundle) r5)
        L_0x00a9:
            return
        L_0x00aa:
            r7.showInfoWindow(r8, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.map.BaiduMap.a(com.baidu.mapapi.map.InfoWindow):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x019c  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01f2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void a(com.baidu.mapapi.map.MyLocationData r21, com.baidu.mapapi.map.MyLocationConfiguration r22) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r3 = r22
            java.lang.String r0 = "direction_wheel"
            java.lang.String r4 = "iconarrowfocid"
            java.lang.String r5 = "iconarrowfoc"
            java.lang.String r6 = "iconarrownorid"
            java.lang.String r7 = "iconarrownor"
            java.lang.String r8 = "direction"
            java.lang.String r9 = "radius"
            java.lang.String r10 = "pty"
            java.lang.String r11 = "ptx"
            if (r2 == 0) goto L_0x0231
            if (r3 == 0) goto L_0x0231
            boolean r12 = r20.isMyLocationEnabled()
            if (r12 != 0) goto L_0x0024
            goto L_0x0231
        L_0x0024:
            org.json.JSONObject r12 = new org.json.JSONObject
            r12.<init>()
            org.json.JSONArray r13 = new org.json.JSONArray
            r13.<init>()
            org.json.JSONObject r14 = new org.json.JSONObject
            r14.<init>()
            org.json.JSONObject r15 = new org.json.JSONObject
            r15.<init>()
            com.baidu.mapapi.model.LatLng r1 = new com.baidu.mapapi.model.LatLng
            r16 = r4
            r17 = r5
            double r4 = r2.latitude
            r18 = r6
            r19 = r7
            double r6 = r2.longitude
            r1.<init>(r4, r6)
            com.baidu.mapapi.model.inner.GeoPoint r4 = com.baidu.mapapi.model.CoordUtil.ll2mc(r1)
            r5 = 0
            java.lang.String r6 = "type"
            r12.put(r6, r5)     // Catch:{ JSONException -> 0x00f6 }
            double r6 = r4.getLongitudeE6()     // Catch:{ JSONException -> 0x00f6 }
            r14.put(r11, r6)     // Catch:{ JSONException -> 0x00f6 }
            double r6 = r4.getLatitudeE6()     // Catch:{ JSONException -> 0x00f6 }
            r14.put(r10, r6)     // Catch:{ JSONException -> 0x00f6 }
            float r6 = r2.accuracy     // Catch:{ JSONException -> 0x00f6 }
            int r6 = (int) r6     // Catch:{ JSONException -> 0x00f6 }
            int r1 = com.baidu.mapapi.model.CoordUtil.getMCDistanceByOneLatLngAndRadius(r1, r6)     // Catch:{ JSONException -> 0x00f6 }
            float r1 = (float) r1     // Catch:{ JSONException -> 0x00f6 }
            double r6 = (double) r1     // Catch:{ JSONException -> 0x00f6 }
            r14.put(r9, r6)     // Catch:{ JSONException -> 0x00f6 }
            float r1 = r2.direction     // Catch:{ JSONException -> 0x00f6 }
            boolean r1 = r3.enableDirection     // Catch:{ JSONException -> 0x00f6 }
            if (r1 == 0) goto L_0x0088
            float r1 = r2.direction     // Catch:{ JSONException -> 0x00f6 }
            r6 = 1135869952(0x43b40000, float:360.0)
            float r1 = r1 % r6
            r7 = 1127481344(0x43340000, float:180.0)
            int r7 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r7 <= 0) goto L_0x0080
            float r1 = r1 - r6
            goto L_0x008b
        L_0x0080:
            r7 = -1020002304(0xffffffffc3340000, float:-180.0)
            int r7 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x008b
            float r1 = r1 + r6
            goto L_0x008b
        L_0x0088:
            r1 = -998621184(0xffffffffc47a4000, float:-1001.0)
        L_0x008b:
            double r6 = (double) r1     // Catch:{ JSONException -> 0x00f6 }
            r14.put(r8, r6)     // Catch:{ JSONException -> 0x00f6 }
            java.lang.String r1 = "NormalLocArrow"
            r6 = r19
            r14.put(r6, r1)     // Catch:{ JSONException -> 0x00f6 }
            r1 = 28
            r7 = r18
            r14.put(r7, r1)     // Catch:{ JSONException -> 0x00f6 }
            java.lang.String r1 = "FocusLocArrow"
            r5 = r17
            r14.put(r5, r1)     // Catch:{ JSONException -> 0x00f6 }
            r1 = 29
            r2 = r16
            r14.put(r2, r1)     // Catch:{ JSONException -> 0x00f6 }
            java.lang.String r1 = "lineid"
            r16 = r2
            int r2 = r3.accuracyCircleStrokeColor     // Catch:{ JSONException -> 0x00f6 }
            r14.put(r1, r2)     // Catch:{ JSONException -> 0x00f6 }
            java.lang.String r1 = "areaid"
            int r2 = r3.accuracyCircleFillColor     // Catch:{ JSONException -> 0x00f6 }
            r14.put(r1, r2)     // Catch:{ JSONException -> 0x00f6 }
            r13.put(r14)     // Catch:{ JSONException -> 0x00f6 }
            java.lang.String r1 = "data"
            r12.put(r1, r13)     // Catch:{ JSONException -> 0x00f6 }
            com.baidu.mapapi.map.MyLocationConfiguration$LocationMode r1 = r3.locationMode     // Catch:{ JSONException -> 0x00f6 }
            com.baidu.mapapi.map.MyLocationConfiguration$LocationMode r2 = com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.COMPASS     // Catch:{ JSONException -> 0x00f6 }
            if (r1 != r2) goto L_0x00f4
            double r1 = r4.getLongitudeE6()     // Catch:{ JSONException -> 0x00f6 }
            r15.put(r11, r1)     // Catch:{ JSONException -> 0x00f6 }
            double r1 = r4.getLatitudeE6()     // Catch:{ JSONException -> 0x00f6 }
            r15.put(r10, r1)     // Catch:{ JSONException -> 0x00f6 }
            r1 = 0
            r15.put(r9, r1)     // Catch:{ JSONException -> 0x00f2 }
            r15.put(r8, r1)     // Catch:{ JSONException -> 0x00f2 }
            r15.put(r6, r0)     // Catch:{ JSONException -> 0x00f2 }
            r2 = 54
            r15.put(r7, r2)     // Catch:{ JSONException -> 0x00f2 }
            r15.put(r5, r0)     // Catch:{ JSONException -> 0x00f2 }
            r0 = r16
            r15.put(r0, r2)     // Catch:{ JSONException -> 0x00f2 }
            r13.put(r15)     // Catch:{ JSONException -> 0x00f2 }
            goto L_0x00fb
        L_0x00f2:
            r0 = move-exception
            goto L_0x00f8
        L_0x00f4:
            r1 = 0
            goto L_0x00fb
        L_0x00f6:
            r0 = move-exception
            r1 = 0
        L_0x00f8:
            r0.printStackTrace()
        L_0x00fb:
            com.baidu.mapapi.map.BitmapDescriptor r0 = r3.customMarker
            if (r0 != 0) goto L_0x0104
            r0 = 0
            r1 = r20
            goto L_0x0198
        L_0x0104:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            com.baidu.mapapi.map.BitmapDescriptor r2 = r3.customMarker
            r0.add(r2)
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.Iterator r0 = r0.iterator()
        L_0x011c:
            boolean r5 = r0.hasNext()
            if (r5 == 0) goto L_0x0172
            java.lang.Object r5 = r0.next()
            com.baidu.mapapi.map.BitmapDescriptor r5 = (com.baidu.mapapi.map.BitmapDescriptor) r5
            com.baidu.mapapi.model.ParcelItem r6 = new com.baidu.mapapi.model.ParcelItem
            r6.<init>()
            android.os.Bundle r7 = new android.os.Bundle
            r7.<init>()
            android.graphics.Bitmap r8 = r5.a
            int r9 = r8.getWidth()
            int r10 = r8.getHeight()
            int r9 = r9 * r10
            int r9 = r9 * 4
            java.nio.ByteBuffer r9 = java.nio.ByteBuffer.allocate(r9)
            r8.copyPixelsToBuffer(r9)
            byte[] r9 = r9.array()
            java.lang.String r10 = "imgdata"
            r7.putByteArray(r10, r9)
            int r5 = r5.hashCode()
            java.lang.String r9 = "imgindex"
            r7.putInt(r9, r5)
            int r5 = r8.getHeight()
            java.lang.String r9 = "imgH"
            r7.putInt(r9, r5)
            int r5 = r8.getWidth()
            java.lang.String r8 = "imgW"
            r7.putInt(r8, r5)
            r6.setBundle(r7)
            r4.add(r6)
            goto L_0x011c
        L_0x0172:
            int r0 = r4.size()
            if (r0 <= 0) goto L_0x0195
            int r0 = r4.size()
            com.baidu.mapapi.model.ParcelItem[] r0 = new com.baidu.mapapi.model.ParcelItem[r0]
            r5 = 0
        L_0x017f:
            int r1 = r4.size()
            if (r5 >= r1) goto L_0x0190
            java.lang.Object r1 = r4.get(r5)
            com.baidu.mapapi.model.ParcelItem r1 = (com.baidu.mapapi.model.ParcelItem) r1
            r0[r5] = r1
            int r5 = r5 + 1
            goto L_0x017f
        L_0x0190:
            java.lang.String r1 = "icondata"
            r2.putParcelableArray(r1, r0)
        L_0x0195:
            r1 = r20
            r0 = r2
        L_0x0198:
            com.baidu.mapsdkplatform.comapi.map.e r2 = r1.i
            if (r2 == 0) goto L_0x01a3
            java.lang.String r4 = r12.toString()
            r2.a((java.lang.String) r4, (android.os.Bundle) r0)
        L_0x01a3:
            int[] r0 = com.baidu.mapapi.map.f.a
            com.baidu.mapapi.map.MyLocationConfiguration$LocationMode r2 = r3.locationMode
            int r2 = r2.ordinal()
            r0 = r0[r2]
            r2 = 1
            if (r0 == r2) goto L_0x01f2
            r2 = 2
            if (r0 == r2) goto L_0x01b5
            goto L_0x0231
        L_0x01b5:
            com.baidu.mapapi.map.MapStatus$Builder r0 = new com.baidu.mapapi.map.MapStatus$Builder
            r0.<init>()
            com.baidu.mapapi.model.LatLng r2 = new com.baidu.mapapi.model.LatLng
            r3 = r21
            double r4 = r3.latitude
            double r6 = r3.longitude
            r2.<init>(r4, r6)
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.target(r2)
            com.baidu.mapapi.map.MapStatus r2 = r20.getMapStatus()
            float r2 = r2.zoom
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.zoom(r2)
            com.baidu.mapapi.map.MapStatus r2 = r20.getMapStatus()
            float r2 = r2.rotate
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.rotate(r2)
            com.baidu.mapapi.map.MapStatus r2 = r20.getMapStatus()
            float r2 = r2.overlook
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.overlook(r2)
            com.baidu.mapapi.map.MapStatus r2 = r20.getMapStatus()
            android.graphics.Point r2 = r2.targetScreen
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.targetScreen(r2)
            goto L_0x0226
        L_0x01f2:
            r3 = r21
            com.baidu.mapapi.map.MapStatus$Builder r0 = new com.baidu.mapapi.map.MapStatus$Builder
            r0.<init>()
            float r2 = r3.direction
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.rotate(r2)
            r2 = -1036779520(0xffffffffc2340000, float:-45.0)
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.overlook(r2)
            com.baidu.mapapi.model.LatLng r2 = new com.baidu.mapapi.model.LatLng
            double r4 = r3.latitude
            double r6 = r3.longitude
            r2.<init>(r4, r6)
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.target(r2)
            com.baidu.mapapi.map.MapStatus r2 = r20.getMapStatus()
            android.graphics.Point r2 = r2.targetScreen
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.targetScreen(r2)
            com.baidu.mapapi.map.MapStatus r2 = r20.getMapStatus()
            float r2 = r2.zoom
            com.baidu.mapapi.map.MapStatus$Builder r0 = r0.zoom(r2)
        L_0x0226:
            com.baidu.mapapi.map.MapStatus r0 = r0.build()
            com.baidu.mapapi.map.MapStatusUpdate r0 = com.baidu.mapapi.map.MapStatusUpdateFactory.newMapStatus(r0)
            r1.animateMapStatus(r0)
        L_0x0231:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.map.BaiduMap.a(com.baidu.mapapi.map.MyLocationData, com.baidu.mapapi.map.MyLocationConfiguration):void");
    }

    private BitmapDescriptor b(InfoWindow infoWindow) {
        BitmapDescriptor bitmapDescriptor;
        if (infoWindow.b == null || !infoWindow.j) {
            return infoWindow.a;
        }
        if (infoWindow.g) {
            if (infoWindow.h <= 0) {
                infoWindow.h = SysOSUtil.getDensityDpi();
            }
            bitmapDescriptor = BitmapDescriptorFactory.fromViewWithDpi(infoWindow.b, infoWindow.h);
        } else {
            bitmapDescriptor = BitmapDescriptorFactory.fromView(infoWindow.b);
        }
        infoWindow.a = bitmapDescriptor;
        return bitmapDescriptor;
    }

    private void c() {
        this.k = new CopyOnWriteArrayList();
        this.l = new CopyOnWriteArrayList();
        this.m = new CopyOnWriteArrayList();
        this.K = new ConcurrentHashMap();
        this.L = new ConcurrentHashMap();
        this.n = new CopyOnWriteArrayList();
        this.T = new Point((int) (SysOSUtil.getDensity() * 40.0f), (int) (SysOSUtil.getDensity() * 40.0f));
        this.g = new UiSettings(this.i);
        this.o = new a(this);
        this.p = new b(this);
        this.i.a((n) new c(this));
        this.i.a((q) new d(this));
        this.i.a((ak) new e(this));
        this.P = this.i.C();
        this.Q = this.i.D();
    }

    /* access modifiers changed from: package-private */
    public void a() {
        e eVar = this.i;
        if (eVar != null) {
            eVar.t();
        }
    }

    /* access modifiers changed from: package-private */
    public void a(HeatMap heatMap) {
        this.I.lock();
        try {
            if (!(this.H == null || this.i == null || heatMap != this.H)) {
                this.H.b();
                this.H.c();
                this.H.a = null;
                this.i.o();
                this.H = null;
                this.i.p(false);
            }
        } finally {
            this.I.unlock();
        }
    }

    /* access modifiers changed from: package-private */
    public void a(TileOverlay tileOverlay) {
        this.J.lock();
        if (tileOverlay != null) {
            try {
                if (this.G == tileOverlay) {
                    tileOverlay.b();
                    tileOverlay.a = null;
                    if (this.i != null) {
                        this.i.f(false);
                    }
                }
            } catch (Throwable th) {
                this.G = null;
                this.J.unlock();
                throw th;
            }
        }
        this.G = null;
        this.J.unlock();
    }

    public void addHeatMap(HeatMap heatMap) {
        if (heatMap != null && this.i != null) {
            this.I.lock();
            try {
                if (heatMap != this.H) {
                    if (this.H != null) {
                        this.H.b();
                        this.H.c();
                        this.H.a = null;
                        this.i.o();
                    }
                    this.H = heatMap;
                    heatMap.a = this;
                    this.i.p(true);
                    this.I.unlock();
                }
            } finally {
                this.I.unlock();
            }
        }
    }

    public final Overlay addOverlay(OverlayOptions overlayOptions) {
        if (overlayOptions == null) {
            return null;
        }
        Overlay a2 = overlayOptions.a();
        a2.listener = this.o;
        if (a2 instanceof Marker) {
            Marker marker = (Marker) a2;
            marker.x = this.p;
            if (!(marker.p == null || marker.p.size() == 0)) {
                this.l.add(marker);
                e eVar = this.i;
                if (eVar != null) {
                    eVar.b(true);
                }
            }
            this.m.add(marker);
            if (marker.w != null) {
                showInfoWindow(marker.w, false);
            }
        }
        Bundle bundle = new Bundle();
        a2.a(bundle);
        e eVar2 = this.i;
        if (eVar2 != null) {
            eVar2.b(bundle);
        }
        this.k.add(a2);
        return a2;
    }

    public final List<Overlay> addOverlays(List<OverlayOptions> list) {
        int i2;
        if (list == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int size = list.size();
        Bundle[] bundleArr = new Bundle[size];
        int i3 = 0;
        for (OverlayOptions next : list) {
            if (next != null) {
                Bundle bundle = new Bundle();
                Overlay a2 = next.a();
                a2.listener = this.o;
                if (a2 instanceof Marker) {
                    Marker marker = (Marker) a2;
                    marker.x = this.p;
                    if (!(marker.p == null || marker.p.size() == 0)) {
                        this.l.add(marker);
                        e eVar = this.i;
                        if (eVar != null) {
                            eVar.b(true);
                        }
                    }
                    this.m.add(marker);
                }
                this.k.add(a2);
                arrayList.add(a2);
                a2.a(bundle);
                bundleArr[i3] = bundle;
                i3++;
            }
        }
        int i4 = size / 400;
        int i5 = 0;
        while (i5 < i4 + 1) {
            ArrayList arrayList2 = new ArrayList();
            int i6 = 0;
            while (i6 < 400 && (i2 = (i5 * 400) + i6) < size) {
                if (bundleArr[i2] != null) {
                    arrayList2.add(bundleArr[i2]);
                }
                i6++;
            }
            e eVar2 = this.i;
            if (eVar2 != null) {
                eVar2.a((List<Bundle>) arrayList2);
            }
            i5++;
        }
        return arrayList;
    }

    public TileOverlay addTileLayer(TileOverlayOptions tileOverlayOptions) {
        if (tileOverlayOptions == null) {
            return null;
        }
        TileOverlay tileOverlay = this.G;
        if (tileOverlay != null) {
            tileOverlay.b();
            this.G.a = null;
        }
        e eVar = this.i;
        if (eVar == null || !eVar.a(tileOverlayOptions.a())) {
            return null;
        }
        TileOverlay a2 = tileOverlayOptions.a(this);
        this.G = a2;
        return a2;
    }

    public final void animateMapStatus(MapStatusUpdate mapStatusUpdate) {
        animateMapStatus(mapStatusUpdate, 300);
    }

    public final void animateMapStatus(MapStatusUpdate mapStatusUpdate, int i2) {
        if (mapStatusUpdate != null && i2 > 0) {
            ad a2 = a(mapStatusUpdate);
            e eVar = this.i;
            if (eVar != null) {
                mapStatusReason |= 256;
                if (!this.S) {
                    eVar.a(a2);
                } else {
                    eVar.a(a2, i2);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean b() {
        e eVar = this.i;
        if (eVar == null) {
            return false;
        }
        return eVar.e();
    }

    public void changeLocationLayerOrder(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.d(z2);
        }
    }

    public void cleanCache(int i2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.b(i2);
        }
    }

    public final void clear() {
        this.k.clear();
        this.l.clear();
        this.m.clear();
        e eVar = this.i;
        if (eVar != null) {
            eVar.b(false);
            this.i.n();
        }
        hideInfoWindow();
    }

    public List<InfoWindow> getAllInfoWindows() {
        return this.n;
    }

    public final Point getCompassPosition() {
        e eVar = this.i;
        if (eVar != null) {
            return a(eVar.h());
        }
        return null;
    }

    public MapBaseIndoorMapInfo getFocusedBaseIndoorMapInfo() {
        e eVar = this.i;
        if (eVar == null) {
            return null;
        }
        return eVar.p();
    }

    public l getGLMapView() {
        return this.h;
    }

    @Deprecated
    public final MyLocationConfiguration getLocationConfigeration() {
        return getLocationConfiguration();
    }

    public final MyLocationConfiguration getLocationConfiguration() {
        return this.O;
    }

    public final MyLocationData getLocationData() {
        return this.N;
    }

    public final MapStatus getMapStatus() {
        e eVar = this.i;
        if (eVar == null) {
            return null;
        }
        return MapStatus.a(eVar.E());
    }

    public final LatLngBounds getMapStatusLimit() {
        e eVar = this.i;
        if (eVar == null) {
            return null;
        }
        return eVar.F();
    }

    public final int getMapType() {
        e eVar = this.i;
        if (eVar == null) {
            return 1;
        }
        if (!eVar.l()) {
            return 3;
        }
        return this.i.k() ? 2 : 1;
    }

    public List<Marker> getMarkersInBounds(LatLngBounds latLngBounds) {
        if (getMapStatus() == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        if (this.m.size() == 0) {
            return null;
        }
        for (Marker next : this.m) {
            if (latLngBounds.contains(next.getPosition())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public final float getMaxZoomLevel() {
        e eVar = this.i;
        if (eVar == null) {
            return 0.0f;
        }
        return eVar.a;
    }

    public final float getMinZoomLevel() {
        e eVar = this.i;
        if (eVar == null) {
            return 0.0f;
        }
        return eVar.b;
    }

    public final Projection getProjection() {
        return this.f;
    }

    public float[] getProjectionMatrix() {
        e eVar = this.i;
        if (eVar == null) {
            return null;
        }
        return eVar.N();
    }

    public final UiSettings getUiSettings() {
        return this.g;
    }

    public float[] getViewMatrix() {
        e eVar = this.i;
        if (eVar == null) {
            return null;
        }
        return eVar.O();
    }

    public float getZoomToBound(int i2, int i3, int i4, int i5, int i6, int i7) {
        e eVar = this.i;
        if (eVar == null) {
            return 0.0f;
        }
        return eVar.a(i2, i3, i4, i5, i6, i7);
    }

    @Deprecated
    public l getmGLMapView() {
        return this.h;
    }

    public void hideInfoWindow() {
        View view;
        MapView mapView;
        Collection<InfoWindow> values = this.K.values();
        if (!values.isEmpty()) {
            for (InfoWindow next : values) {
                if (!(next == null || (view = next.b) == null)) {
                    int i2 = f.b[this.d.ordinal()];
                    if (i2 == 1) {
                        TextureMapView textureMapView = this.b;
                        if (textureMapView != null) {
                            textureMapView.removeView(view);
                        }
                    } else if (i2 == 2 && (mapView = this.a) != null) {
                        mapView.removeView(view);
                    }
                }
            }
        }
        for (Overlay next2 : this.k) {
            Set<String> keySet = this.K.keySet();
            String str = next2.z;
            if ((next2 instanceof Marker) && !keySet.isEmpty() && keySet.contains(str)) {
                next2.remove();
            }
        }
        this.K.clear();
        this.L.clear();
        this.n.clear();
    }

    public void hideInfoWindow(InfoWindow infoWindow) {
        MapView mapView;
        Set<InfoWindow> keySet = this.L.keySet();
        if (infoWindow != null && !keySet.isEmpty() && keySet.contains(infoWindow)) {
            View view = infoWindow.b;
            if (view != null) {
                int i2 = f.b[this.d.ordinal()];
                if (i2 == 1) {
                    TextureMapView textureMapView = this.b;
                    if (textureMapView != null) {
                        textureMapView.removeView(view);
                    }
                } else if (i2 == 2 && (mapView = this.a) != null) {
                    mapView.removeView(view);
                }
            }
            Marker marker = this.L.get(infoWindow);
            if (marker != null) {
                marker.remove();
                this.K.remove(marker.z);
            }
            this.L.remove(infoWindow);
            this.n.remove(infoWindow);
        }
    }

    public void hideSDKLayer() {
        e eVar = this.i;
        if (eVar != null) {
            eVar.c();
        }
    }

    public final boolean isBaiduHeatMapEnabled() {
        e eVar = this.i;
        if (eVar == null) {
            return false;
        }
        return eVar.i();
    }

    public boolean isBaseIndoorMapMode() {
        e eVar = this.i;
        if (eVar == null) {
            return false;
        }
        return eVar.q();
    }

    public final boolean isBuildingsEnabled() {
        e eVar = this.i;
        if (eVar == null) {
            return false;
        }
        return eVar.m();
    }

    public final boolean isMyLocationEnabled() {
        e eVar = this.i;
        if (eVar == null) {
            return false;
        }
        return eVar.s();
    }

    public final boolean isSupportBaiduHeatMap() {
        e eVar = this.i;
        if (eVar == null) {
            return false;
        }
        return eVar.j();
    }

    public final boolean isTrafficEnabled() {
        e eVar = this.i;
        if (eVar == null) {
            return false;
        }
        return eVar.g();
    }

    public final void removeMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
        if (this.x.contains(onMarkerClickListener)) {
            this.x.remove(onMarkerClickListener);
        }
    }

    public final void setBaiduHeatMapEnabled(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.h(z2);
        }
    }

    public final void setBuildingsEnabled(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.j(z2);
        }
    }

    public void setCompassEnable(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.e(z2);
        }
    }

    public void setCompassIcon(Bitmap bitmap) {
        if (bitmap != null) {
            e eVar = this.i;
            if (eVar != null) {
                eVar.a(bitmap);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: compass's icon can not be null");
    }

    public void setCompassPosition(Point point) {
        e eVar = this.i;
        if (eVar != null && eVar.a(point)) {
            this.T = point;
        }
    }

    public boolean setCustomTrafficColor(String str, String str2, String str3, String str4) {
        if (this.i == null) {
            return false;
        }
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3) || TextUtils.isEmpty(str4)) {
            String str5 = str;
            String str6 = str2;
            String str7 = str3;
            String str8 = str4;
            if (!TextUtils.isEmpty(str) || !TextUtils.isEmpty(str2) || !TextUtils.isEmpty(str3) || !TextUtils.isEmpty(str4)) {
                return true;
            }
            this.i.a((long) Color.parseColor("#ffffffff"), (long) Color.parseColor("#ffffffff"), (long) Color.parseColor("#ffffffff"), (long) Color.parseColor("#ffffffff"), false);
            return true;
        } else if (!str.matches("^#[0-9a-fA-F]{8}$") || !str2.matches("^#[0-9a-fA-F]{8}$") || !str3.matches("^#[0-9a-fA-F]{8}$") || !str4.matches("^#[0-9a-fA-F]{8}$")) {
            Log.e(e, "the string of the input customTrafficColor is error");
            return false;
        } else {
            this.i.a((long) Color.parseColor(str), (long) Color.parseColor(str2), (long) Color.parseColor(str3), (long) Color.parseColor(str4), true);
            return true;
        }
    }

    public final void setIndoorEnable(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            this.R = z2;
            eVar.l(z2);
        }
        OnBaseIndoorMapListener onBaseIndoorMapListener = this.D;
        if (onBaseIndoorMapListener != null && !z2) {
            onBaseIndoorMapListener.onBaseIndoorMapMode(false, (MapBaseIndoorMapInfo) null);
        }
    }

    public void setLayerClickable(MapLayer mapLayer, boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.a(mapLayer, z2);
        }
    }

    public final void setMapStatus(MapStatusUpdate mapStatusUpdate) {
        if (mapStatusUpdate != null) {
            ad a2 = a(mapStatusUpdate);
            e eVar = this.i;
            if (eVar != null) {
                eVar.a(a2);
                OnMapStatusChangeListener onMapStatusChangeListener = this.q;
                if (onMapStatusChangeListener != null) {
                    onMapStatusChangeListener.onMapStatusChange(getMapStatus());
                }
            }
        }
    }

    public final void setMapStatusLimits(LatLngBounds latLngBounds) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.a(latLngBounds);
            setMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
        }
    }

    public final void setMapType(int i2) {
        e eVar = this.i;
        if (eVar != null) {
            if (i2 == 1) {
                eVar.a(false);
                this.i.v(this.P);
                this.i.w(this.Q);
                this.i.g(true);
                this.i.l(this.R);
            } else if (i2 == 2) {
                eVar.a(true);
                this.i.v(this.P);
                this.i.w(this.Q);
                this.i.g(true);
            } else if (i2 == 3) {
                if (eVar.C()) {
                    this.i.v(false);
                }
                if (this.i.D()) {
                    this.i.w(false);
                }
                this.i.g(false);
                this.i.l(false);
            }
            l lVar = this.h;
            if (lVar != null) {
                lVar.a(i2);
            }
        }
    }

    public final void setMaxAndMinZoomLevel(float f2, float f3) {
        e eVar;
        if (f2 <= 21.0f && f3 >= 4.0f && f2 >= f3 && (eVar = this.i) != null) {
            eVar.a(f2, f3);
        }
    }

    @Deprecated
    public final void setMyLocationConfigeration(MyLocationConfiguration myLocationConfiguration) {
        setMyLocationConfiguration(myLocationConfiguration);
    }

    public final void setMyLocationConfiguration(MyLocationConfiguration myLocationConfiguration) {
        this.O = myLocationConfiguration;
        a(this.N, myLocationConfiguration);
    }

    public final void setMyLocationData(MyLocationData myLocationData) {
        this.N = myLocationData;
        if (this.O == null) {
            this.O = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, (BitmapDescriptor) null);
        }
        a(myLocationData, this.O);
    }

    public final void setMyLocationEnabled(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.o(z2);
        }
    }

    public final void setOnBaseIndoorMapListener(OnBaseIndoorMapListener onBaseIndoorMapListener) {
        this.D = onBaseIndoorMapListener;
    }

    public final void setOnMapClickListener(OnMapClickListener onMapClickListener) {
        this.s = onMapClickListener;
    }

    public final void setOnMapDoubleClickListener(OnMapDoubleClickListener onMapDoubleClickListener) {
        this.v = onMapDoubleClickListener;
    }

    public final void setOnMapDrawFrameCallback(OnMapDrawFrameCallback onMapDrawFrameCallback) {
        this.C = onMapDrawFrameCallback;
    }

    public void setOnMapLoadedCallback(OnMapLoadedCallback onMapLoadedCallback) {
        this.t = onMapLoadedCallback;
    }

    public final void setOnMapLongClickListener(OnMapLongClickListener onMapLongClickListener) {
        this.w = onMapLongClickListener;
    }

    public void setOnMapRenderCallbadk(OnMapRenderCallback onMapRenderCallback) {
        this.u = onMapRenderCallback;
    }

    public final void setOnMapRenderValidDataListener(OnMapRenderValidDataListener onMapRenderValidDataListener) {
        this.E = onMapRenderValidDataListener;
    }

    public final void setOnMapStatusChangeListener(OnMapStatusChangeListener onMapStatusChangeListener) {
        this.q = onMapStatusChangeListener;
    }

    public final void setOnMapTouchListener(OnMapTouchListener onMapTouchListener) {
        this.r = onMapTouchListener;
    }

    public final void setOnMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
        if (onMarkerClickListener != null && !this.x.contains(onMarkerClickListener)) {
            this.x.add(onMarkerClickListener);
        }
    }

    public final void setOnMarkerDragListener(OnMarkerDragListener onMarkerDragListener) {
        this.z = onMarkerDragListener;
    }

    public final void setOnMyLocationClickListener(OnMyLocationClickListener onMyLocationClickListener) {
        this.A = onMyLocationClickListener;
    }

    public final void setOnPolylineClickListener(OnPolylineClickListener onPolylineClickListener) {
        if (onPolylineClickListener != null) {
            this.y.add(onPolylineClickListener);
        }
    }

    public final void setOnSynchronizationListener(OnSynchronizationListener onSynchronizationListener) {
        this.F = onSynchronizationListener;
    }

    public void setOverlayUnderPoi(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.c(z2);
        }
    }

    @Deprecated
    public final void setPadding(int i2, int i3, int i4, int i5) {
        setViewPadding(i2, i3, i4, i5);
    }

    public void setPixelFormatTransparent(boolean z2) {
        l lVar = this.h;
        if (lVar != null) {
            if (z2) {
                lVar.d();
            } else {
                lVar.e();
            }
        }
    }

    public final void setTrafficEnabled(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.i(z2);
        }
    }

    public final void setViewPadding(int i2, int i3, int i4, int i5) {
        MapView mapView;
        if (i2 >= 0 && i3 >= 0 && i4 >= 0 && i5 >= 0 && this.i != null) {
            int i6 = f.b[this.d.ordinal()];
            if (i6 == 1) {
                TextureMapView textureMapView = this.b;
                if (textureMapView != null) {
                    this.i.a(new Point((int) (((float) i2) + (((float) this.T.x) * (((float) ((textureMapView.getWidth() - i2) - i4)) / ((float) this.b.getWidth())))), (int) (((float) i3) + (((float) this.T.y) * (((float) ((this.b.getHeight() - i3) - i5)) / ((float) this.b.getHeight()))))));
                    this.b.setPadding(i2, i3, i4, i5);
                    this.b.invalidate();
                }
            } else if (i6 == 2 && (mapView = this.a) != null) {
                this.i.a(new Point((int) (((float) i2) + (((float) this.T.x) * (((float) ((mapView.getWidth() - i2) - i4)) / ((float) this.a.getWidth())))), (int) (((float) i3) + (((float) this.T.y) * (((float) ((this.a.getHeight() - i3) - i5)) / ((float) this.a.getHeight()))))));
                this.a.setPadding(i2, i3, i4, i5);
                this.a.invalidate();
            }
        }
    }

    public void showInfoWindow(InfoWindow infoWindow) {
        showInfoWindow(infoWindow, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showInfoWindow(com.baidu.mapapi.map.InfoWindow r7, boolean r8) {
        /*
            r6 = this;
            java.util.Map<com.baidu.mapapi.map.InfoWindow, com.baidu.mapapi.map.Marker> r0 = r6.L
            java.util.Set r0 = r0.keySet()
            if (r7 == 0) goto L_0x00d9
            boolean r0 = r0.contains(r7)
            if (r0 == 0) goto L_0x0010
            goto L_0x00d9
        L_0x0010:
            if (r8 == 0) goto L_0x0015
            r6.hideInfoWindow()
        L_0x0015:
            com.baidu.mapapi.map.InfoWindow$a r8 = r6.p
            r7.e = r8
            android.view.View r8 = r7.b
            r0 = 0
            r1 = 1
            if (r8 == 0) goto L_0x006a
            boolean r8 = r7.j
            if (r8 == 0) goto L_0x006a
            android.view.View r8 = r7.b
            r8.destroyDrawingCache()
            com.baidu.mapapi.map.MapViewLayoutParams$Builder r2 = new com.baidu.mapapi.map.MapViewLayoutParams$Builder
            r2.<init>()
            com.baidu.mapapi.map.MapViewLayoutParams$ELayoutMode r3 = com.baidu.mapapi.map.MapViewLayoutParams.ELayoutMode.mapMode
            com.baidu.mapapi.map.MapViewLayoutParams$Builder r2 = r2.layoutMode(r3)
            com.baidu.mapapi.model.LatLng r3 = r7.c
            com.baidu.mapapi.map.MapViewLayoutParams$Builder r2 = r2.position(r3)
            int r3 = r7.f
            com.baidu.mapapi.map.MapViewLayoutParams$Builder r2 = r2.yOffset(r3)
            com.baidu.mapapi.map.MapViewLayoutParams r2 = r2.build()
            int[] r3 = com.baidu.mapapi.map.f.b
            com.baidu.mapsdkplatform.comapi.map.ac r4 = r6.d
            int r4 = r4.ordinal()
            r3 = r3[r4]
            if (r3 == r1) goto L_0x005d
            r4 = 2
            if (r3 == r4) goto L_0x0053
            goto L_0x0064
        L_0x0053:
            com.baidu.mapsdkplatform.comapi.map.l r3 = r6.h
            if (r3 == 0) goto L_0x0064
            com.baidu.mapapi.map.MapView r3 = r6.a
            r3.addView(r8, r2)
            goto L_0x0064
        L_0x005d:
            com.baidu.mapapi.map.TextureMapView r3 = r6.b
            if (r3 == 0) goto L_0x0064
            r3.addView(r8, r2)
        L_0x0064:
            boolean r8 = r7.i
            if (r8 == 0) goto L_0x006a
            r8 = 0
            goto L_0x006b
        L_0x006a:
            r8 = 1
        L_0x006b:
            com.baidu.mapapi.map.BitmapDescriptor r2 = r6.b((com.baidu.mapapi.map.InfoWindow) r7)
            com.baidu.mapapi.map.MarkerOptions r3 = new com.baidu.mapapi.map.MarkerOptions
            r3.<init>()
            com.baidu.mapapi.map.MarkerOptions r3 = r3.perspective(r0)
            com.baidu.mapapi.map.MarkerOptions r2 = r3.icon(r2)
            com.baidu.mapapi.model.LatLng r3 = r7.c
            com.baidu.mapapi.map.MarkerOptions r2 = r2.position(r3)
            r3 = 2147483647(0x7fffffff, float:NaN)
            com.baidu.mapapi.map.MarkerOptions r2 = r2.zIndex(r3)
            int r3 = r7.f
            com.baidu.mapapi.map.MarkerOptions r2 = r2.yOffset(r3)
            com.baidu.mapapi.map.MarkerOptions r2 = r2.infoWindow(r7)
            com.baidu.mapapi.map.Overlay r2 = r2.a()
            com.baidu.mapapi.map.Overlay$a r3 = r6.o
            r2.listener = r3
            com.baidu.mapsdkplatform.comapi.map.j r3 = com.baidu.mapsdkplatform.comapi.map.j.popup
            r2.type = r3
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            r2.a(r3)
            android.view.View r4 = r7.b
            java.lang.String r5 = "draw_with_view"
            if (r4 == 0) goto L_0x00b1
            r3.putInt(r5, r1)
            goto L_0x00b4
        L_0x00b1:
            r3.putInt(r5, r0)
        L_0x00b4:
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.i
            if (r0 == 0) goto L_0x00c2
            if (r8 == 0) goto L_0x00c2
            r0.b((android.os.Bundle) r3)
            java.util.List<com.baidu.mapapi.map.Overlay> r8 = r6.k
            r8.add(r2)
        L_0x00c2:
            com.baidu.mapapi.map.Marker r2 = (com.baidu.mapapi.map.Marker) r2
            com.baidu.mapapi.map.InfoWindow$a r8 = r6.p
            r2.x = r8
            java.util.Map<java.lang.String, com.baidu.mapapi.map.InfoWindow> r8 = r6.K
            java.lang.String r0 = r2.z
            r8.put(r0, r7)
            java.util.Map<com.baidu.mapapi.map.InfoWindow, com.baidu.mapapi.map.Marker> r8 = r6.L
            r8.put(r7, r2)
            java.util.List<com.baidu.mapapi.map.InfoWindow> r8 = r6.n
            r8.add(r7)
        L_0x00d9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.map.BaiduMap.showInfoWindow(com.baidu.mapapi.map.InfoWindow, boolean):void");
    }

    public void showInfoWindows(List<InfoWindow> list) {
        if (list != null && !list.isEmpty()) {
            for (InfoWindow showInfoWindow : list) {
                showInfoWindow(showInfoWindow, false);
            }
        }
    }

    public final void showMapIndoorPoi(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.w(z2);
            this.Q = z2;
        }
    }

    public final void showMapPoi(boolean z2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.v(z2);
            this.P = z2;
        }
    }

    public void showSDKLayer() {
        e eVar = this.i;
        if (eVar != null) {
            eVar.d();
        }
    }

    public final void snapshot(SnapshotReadyCallback snapshotReadyCallback) {
        l lVar;
        this.B = snapshotReadyCallback;
        int i2 = f.b[this.d.ordinal()];
        if (i2 == 1) {
            ae aeVar = this.j;
            if (aeVar != null) {
                aeVar.a("anything", (Rect) null);
            }
        } else if (i2 == 2 && (lVar = this.h) != null) {
            lVar.a("anything", (Rect) null);
        }
    }

    public final void snapshotScope(Rect rect, SnapshotReadyCallback snapshotReadyCallback) {
        l lVar;
        this.B = snapshotReadyCallback;
        int i2 = f.b[this.d.ordinal()];
        if (i2 == 1) {
            ae aeVar = this.j;
            if (aeVar != null) {
                aeVar.a("anything", rect);
            }
        } else if (i2 == 2 && (lVar = this.h) != null) {
            lVar.a("anything", rect);
        }
    }

    public MapBaseIndoorMapInfo.SwitchFloorError switchBaseIndoorMapFloor(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return MapBaseIndoorMapInfo.SwitchFloorError.FLOOR_INFO_ERROR;
        }
        MapBaseIndoorMapInfo focusedBaseIndoorMapInfo = getFocusedBaseIndoorMapInfo();
        if (focusedBaseIndoorMapInfo == null) {
            return MapBaseIndoorMapInfo.SwitchFloorError.SWITCH_ERROR;
        }
        if (!str2.equals(focusedBaseIndoorMapInfo.a)) {
            return MapBaseIndoorMapInfo.SwitchFloorError.FOCUSED_ID_ERROR;
        }
        ArrayList<String> floors = focusedBaseIndoorMapInfo.getFloors();
        if (floors == null || !floors.contains(str)) {
            return MapBaseIndoorMapInfo.SwitchFloorError.FLOOR_OVERLFLOW;
        }
        e eVar = this.i;
        return (eVar == null || !eVar.a(str, str2)) ? MapBaseIndoorMapInfo.SwitchFloorError.SWITCH_ERROR : MapBaseIndoorMapInfo.SwitchFloorError.SWITCH_OK;
    }

    public void switchLayerOrder(MapLayer mapLayer, MapLayer mapLayer2) {
        e eVar = this.i;
        if (eVar != null) {
            eVar.a(mapLayer, mapLayer2);
        }
    }
}

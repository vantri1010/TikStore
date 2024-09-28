package com.baidu.mapapi.map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.baidu.mapapi.model.LatLng;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class HeatMap {
    public static final Gradient DEFAULT_GRADIENT;
    public static final double DEFAULT_OPACITY = 0.6d;
    public static final int DEFAULT_RADIUS = 12;
    private static final String b = HeatMap.class.getSimpleName();
    private static final SparseIntArray c;
    private static final int[] d;
    private static final float[] e;
    private static int r = 0;
    BaiduMap a;
    private p<WeightedLatLng> f;
    private Collection<WeightedLatLng> g;
    private int h;
    private Gradient i;
    private double j;
    private h k;
    private int[] l;
    private double[] m;
    private double[] n;
    private HashMap<String, Tile> o;
    private ExecutorService p;
    private HashSet<String> q;

    public static class Builder {
        /* access modifiers changed from: private */
        public Collection<WeightedLatLng> a;
        /* access modifiers changed from: private */
        public int b = 12;
        /* access modifiers changed from: private */
        public Gradient c = HeatMap.DEFAULT_GRADIENT;
        /* access modifiers changed from: private */
        public double d = 0.6d;

        public HeatMap build() {
            if (this.a != null) {
                return new HeatMap(this, (j) null);
            }
            throw new IllegalStateException("BDMapSDKException: No input data: you must use either .data or .weightedData before building");
        }

        public Builder data(Collection<LatLng> collection) {
            if (collection == null || collection.isEmpty()) {
                throw new IllegalArgumentException("BDMapSDKException: No input points.");
            } else if (!collection.contains((Object) null)) {
                return weightedData(HeatMap.c(collection));
            } else {
                throw new IllegalArgumentException("BDMapSDKException: input points can not contain null.");
            }
        }

        public Builder gradient(Gradient gradient) {
            if (gradient != null) {
                this.c = gradient;
                return this;
            }
            throw new IllegalArgumentException("BDMapSDKException: gradient can not be null");
        }

        public Builder opacity(double d2) {
            this.d = d2;
            if (d2 >= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && d2 <= 1.0d) {
                return this;
            }
            throw new IllegalArgumentException("BDMapSDKException: Opacity must be in range [0, 1]");
        }

        public Builder radius(int i) {
            this.b = i;
            if (i >= 10 && i <= 50) {
                return this;
            }
            throw new IllegalArgumentException("BDMapSDKException: Radius not within bounds.");
        }

        public Builder weightedData(Collection<WeightedLatLng> collection) {
            if (collection == null || collection.isEmpty()) {
                throw new IllegalArgumentException("BDMapSDKException: No input points.");
            } else if (!collection.contains((Object) null)) {
                ArrayList arrayList = new ArrayList();
                for (WeightedLatLng next : collection) {
                    LatLng latLng = next.latLng;
                    if (latLng.latitude < 0.37532d || latLng.latitude > 54.562495d || latLng.longitude < 72.508319d || latLng.longitude > 135.942198d) {
                        arrayList.add(next);
                    }
                }
                collection.removeAll(arrayList);
                this.a = collection;
                return this;
            } else {
                throw new IllegalArgumentException("BDMapSDKException: input points can not contain null.");
            }
        }
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        c = sparseIntArray;
        sparseIntArray.put(3, 8388608);
        c.put(4, 4194304);
        c.put(5, 2097152);
        c.put(6, 1048576);
        c.put(7, 524288);
        c.put(8, 262144);
        c.put(9, 131072);
        c.put(10, 65536);
        c.put(11, 32768);
        c.put(12, 16384);
        c.put(13, 8192);
        c.put(14, 4096);
        c.put(15, 2048);
        c.put(16, 1024);
        c.put(17, 512);
        c.put(18, 256);
        c.put(19, 128);
        c.put(20, 64);
        int[] iArr = {Color.rgb(0, 0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION), Color.rgb(0, 225, 0), Color.rgb(255, 0, 0)};
        d = iArr;
        float[] fArr = {0.08f, 0.4f, 1.0f};
        e = fArr;
        DEFAULT_GRADIENT = new Gradient(iArr, fArr);
    }

    private HeatMap(Builder builder) {
        this.o = new HashMap<>();
        this.p = Executors.newFixedThreadPool(1);
        this.q = new HashSet<>();
        this.g = builder.a;
        this.h = builder.b;
        this.i = builder.c;
        this.j = builder.d;
        int i2 = this.h;
        this.m = a(i2, ((double) i2) / 3.0d);
        a(this.i);
        b(this.g);
    }

    /* synthetic */ HeatMap(Builder builder, j jVar) {
        this(builder);
    }

    private static double a(Collection<WeightedLatLng> collection, h hVar, int i2, int i3) {
        h hVar2 = hVar;
        double d2 = hVar2.a;
        double d3 = hVar2.c;
        double d4 = hVar2.b;
        double d5 = d3 - d2;
        double d6 = hVar2.d - d4;
        if (d5 <= d6) {
            d5 = d6;
        }
        double d7 = ((double) ((int) (((double) (i3 / (i2 * 2))) + 0.5d))) / d5;
        LongSparseArray longSparseArray = new LongSparseArray();
        double d8 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        for (WeightedLatLng next : collection) {
            int i4 = (int) ((((double) next.a().y) - d4) * d7);
            long j2 = (long) ((int) ((((double) next.a().x) - d2) * d7));
            LongSparseArray longSparseArray2 = (LongSparseArray) longSparseArray.get(j2);
            if (longSparseArray2 == null) {
                longSparseArray2 = new LongSparseArray();
                longSparseArray.put(j2, longSparseArray2);
            }
            long j3 = (long) i4;
            Double d9 = (Double) longSparseArray2.get(j3);
            if (d9 == null) {
                d9 = Double.valueOf(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
            }
            LongSparseArray longSparseArray3 = longSparseArray;
            double d10 = d2;
            Double valueOf = Double.valueOf(d9.doubleValue() + next.intensity);
            longSparseArray2.put(j3, valueOf);
            if (valueOf.doubleValue() > d8) {
                d8 = valueOf.doubleValue();
            }
            longSparseArray = longSparseArray3;
            d2 = d10;
        }
        return d8;
    }

    private static Bitmap a(double[][] dArr, int[] iArr, double d2) {
        double[][] dArr2 = dArr;
        int[] iArr2 = iArr;
        int i2 = iArr2[iArr2.length - 1];
        double length = ((double) (iArr2.length - 1)) / d2;
        int length2 = dArr2.length;
        int[] iArr3 = new int[(length2 * length2)];
        for (int i3 = 0; i3 < length2; i3++) {
            for (int i4 = 0; i4 < length2; i4++) {
                double d3 = dArr2[i4][i3];
                int i5 = (i3 * length2) + i4;
                int i6 = (int) (d3 * length);
                if (d3 == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                    iArr3[i5] = 0;
                } else if (i6 < iArr2.length) {
                    iArr3[i5] = iArr2[i6];
                } else {
                    iArr3[i5] = i2;
                }
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(length2, length2, Bitmap.Config.ARGB_8888);
        createBitmap.setPixels(iArr3, 0, length2, 0, 0, length2, length2);
        return createBitmap;
    }

    private static Tile a(Bitmap bitmap) {
        ByteBuffer allocate = ByteBuffer.allocate(bitmap.getWidth() * bitmap.getHeight() * 4);
        bitmap.copyPixelsToBuffer(allocate);
        return new Tile(256, 256, allocate.array());
    }

    private void a(Gradient gradient) {
        this.i = gradient;
        this.l = gradient.a(this.j);
    }

    private synchronized void a(String str, Tile tile) {
        this.o.put(str, tile);
    }

    private synchronized boolean a(String str) {
        return this.q.contains(str);
    }

    private double[] a(int i2) {
        int i3;
        double[] dArr = new double[20];
        int i4 = 5;
        while (true) {
            if (i4 >= 11) {
                break;
            }
            dArr[i4] = a(this.g, this.k, i2, (int) (Math.pow(2.0d, (double) (i4 - 3)) * 1280.0d));
            if (i4 == 5) {
                for (int i5 = 0; i5 < i4; i5++) {
                    dArr[i5] = dArr[i4];
                }
            }
            i4++;
        }
        for (i3 = 11; i3 < 20; i3++) {
            dArr[i3] = dArr[10];
        }
        return dArr;
    }

    private static double[] a(int i2, double d2) {
        double[] dArr = new double[((i2 * 2) + 1)];
        for (int i3 = -i2; i3 <= i2; i3++) {
            dArr[i3 + i2] = Math.exp(((double) ((-i3) * i3)) / ((2.0d * d2) * d2));
        }
        return dArr;
    }

    private static double[][] a(double[][] dArr, double[] dArr2) {
        double[][] dArr3 = dArr;
        double[] dArr4 = dArr2;
        Class<double> cls = double.class;
        int floor = (int) Math.floor(((double) dArr4.length) / 2.0d);
        int length = dArr3.length;
        int i2 = length - (floor * 2);
        int i3 = 1;
        int i4 = (floor + i2) - 1;
        int[] iArr = new int[2];
        iArr[1] = length;
        iArr[0] = length;
        double[][] dArr5 = (double[][]) Array.newInstance(cls, iArr);
        int i5 = 0;
        while (true) {
            double d2 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            if (i5 >= length) {
                break;
            }
            int i6 = 0;
            while (i6 < length) {
                double d3 = dArr3[i5][i6];
                if (d3 != d2) {
                    int i7 = i5 + floor;
                    if (i4 < i7) {
                        i7 = i4;
                    }
                    int i8 = i7 + 1;
                    int i9 = i5 - floor;
                    for (int i10 = floor > i9 ? floor : i9; i10 < i8; i10++) {
                        double[] dArr6 = dArr5[i10];
                        dArr6[i6] = dArr6[i6] + (dArr4[i10 - i9] * d3);
                    }
                }
                i6++;
                d2 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            }
            i5++;
        }
        int[] iArr2 = new int[2];
        iArr2[1] = i2;
        iArr2[0] = i2;
        double[][] dArr7 = (double[][]) Array.newInstance(cls, iArr2);
        int i11 = floor;
        while (i11 < i4 + 1) {
            int i12 = 0;
            while (i12 < length) {
                double d4 = dArr5[i11][i12];
                if (d4 != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                    int i13 = i12 + floor;
                    if (i4 < i13) {
                        i13 = i4;
                    }
                    int i14 = i13 + i3;
                    int i15 = i12 - floor;
                    for (int i16 = floor > i15 ? floor : i15; i16 < i14; i16++) {
                        double[] dArr8 = dArr7[i11 - floor];
                        int i17 = i16 - floor;
                        dArr8[i17] = dArr8[i17] + (dArr4[i16 - i15] * d4);
                    }
                }
                i12++;
                i3 = 1;
            }
            i11++;
            i3 = 1;
        }
        return dArr7;
    }

    /* access modifiers changed from: private */
    public void b(int i2, int i3, int i4) {
        int i5 = i2;
        int i6 = i3;
        int i7 = i4;
        double d2 = (double) c.get(i7);
        int i8 = this.h;
        double d3 = (((double) i8) * d2) / 256.0d;
        double d4 = ((2.0d * d3) + d2) / ((double) ((i8 * 2) + 256));
        if (i5 >= 0 && i6 >= 0) {
            double d5 = (((double) i5) * d2) - d3;
            double d6 = (((double) (i5 + 1)) * d2) + d3;
            double d7 = (((double) i6) * d2) - d3;
            double d8 = (((double) (i6 + 1)) * d2) + d3;
            h hVar = new h(d5, d6, d7, d8);
            double d9 = d5;
            if (hVar.a(new h(this.k.a - d3, this.k.c + d3, this.k.b - d3, this.k.d + d3))) {
                Collection<WeightedLatLng> a2 = this.f.a(hVar);
                if (!a2.isEmpty()) {
                    int i9 = this.h;
                    int[] iArr = new int[2];
                    iArr[1] = (i9 * 2) + 256;
                    iArr[0] = (i9 * 2) + 256;
                    double[][] dArr = (double[][]) Array.newInstance(double.class, iArr);
                    for (WeightedLatLng next : a2) {
                        Point a3 = next.a();
                        int i10 = (int) ((((double) a3.x) - d9) / d4);
                        int i11 = (int) ((d8 - ((double) a3.y)) / d4);
                        int i12 = this.h;
                        if (i10 >= (i12 * 2) + 256) {
                            i10 = ((i12 * 2) + 256) - 1;
                        }
                        int i13 = this.h;
                        if (i11 >= (i13 * 2) + 256) {
                            i11 = ((i13 * 2) + 256) - 1;
                        }
                        double[] dArr2 = dArr[i10];
                        dArr2[i11] = dArr2[i11] + next.intensity;
                        d8 = d8;
                    }
                    Bitmap a4 = a(a(dArr, this.m), this.l, this.n[i7 - 1]);
                    Tile a5 = a(a4);
                    a4.recycle();
                    a(i5 + "_" + i6 + "_" + i7, a5);
                    if (this.o.size() > r) {
                        a();
                    }
                    BaiduMap baiduMap = this.a;
                    if (baiduMap != null) {
                        baiduMap.a();
                    }
                }
            }
        }
    }

    private synchronized void b(String str) {
        this.q.add(str);
    }

    private void b(Collection<WeightedLatLng> collection) {
        this.g = collection;
        if (!collection.isEmpty()) {
            h d2 = d(this.g);
            this.k = d2;
            this.f = new p<>(d2);
            for (WeightedLatLng a2 : this.g) {
                this.f.a(a2);
            }
            this.n = a(this.h);
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: No input points.");
    }

    private synchronized Tile c(String str) {
        if (!this.o.containsKey(str)) {
            return null;
        }
        Tile tile = this.o.get(str);
        this.o.remove(str);
        return tile;
    }

    /* access modifiers changed from: private */
    public static Collection<WeightedLatLng> c(Collection<LatLng> collection) {
        ArrayList arrayList = new ArrayList();
        for (LatLng weightedLatLng : collection) {
            arrayList.add(new WeightedLatLng(weightedLatLng));
        }
        return arrayList;
    }

    private static h d(Collection<WeightedLatLng> collection) {
        Iterator<WeightedLatLng> it = collection.iterator();
        WeightedLatLng next = it.next();
        double d2 = (double) next.a().x;
        double d3 = (double) next.a().x;
        double d4 = (double) next.a().y;
        double d5 = (double) next.a().y;
        while (it.hasNext()) {
            WeightedLatLng next2 = it.next();
            double d6 = (double) next2.a().x;
            double d7 = (double) next2.a().y;
            if (d6 < d2) {
                d2 = d6;
            }
            if (d6 > d3) {
                d3 = d6;
            }
            if (d7 < d4) {
                d4 = d7;
            }
            if (d7 > d5) {
                d5 = d7;
            }
        }
        return new h(d2, d3, d4, d5);
    }

    private synchronized void d() {
        this.o.clear();
    }

    /* access modifiers changed from: package-private */
    public Tile a(int i2, int i3, int i4) {
        String str = i2 + "_" + i3 + "_" + i4;
        Tile c2 = c(str);
        if (c2 != null) {
            return c2;
        }
        if (a(str)) {
            return null;
        }
        BaiduMap baiduMap = this.a;
        if (baiduMap != null && r == 0) {
            MapStatus mapStatus = baiduMap.getMapStatus();
            r = (((mapStatus.a.j.right - mapStatus.a.j.left) / 256) + 2) * (((mapStatus.a.j.bottom - mapStatus.a.j.top) / 256) + 2) * 4;
        }
        if (this.o.size() > r) {
            a();
        }
        if (this.p.isShutdown()) {
            return null;
        }
        try {
            this.p.execute(new j(this, i2, i3, i4));
            b(str);
            return null;
        } catch (RejectedExecutionException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void a() {
        this.q.clear();
        this.o.clear();
    }

    /* access modifiers changed from: package-private */
    public void b() {
        d();
    }

    /* access modifiers changed from: package-private */
    public void c() {
        this.p.shutdownNow();
    }

    public void removeHeatMap() {
        BaiduMap baiduMap = this.a;
        if (baiduMap != null) {
            baiduMap.a(this);
        }
    }
}

package com.baidu.location;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.location.Address;
import com.baidu.location.g.k;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BDLocation implements Parcelable {
    public static final String BDLOCATION_BD09LL_TO_GCJ02 = "bd09ll2gcj";
    public static final String BDLOCATION_BD09_TO_GCJ02 = "bd092gcj";
    public static final String BDLOCATION_GCJ02_TO_BD09 = "bd09";
    public static final String BDLOCATION_GCJ02_TO_BD09LL = "bd09ll";
    public static final String BDLOCATION_WGS84_TO_GCJ02 = "gps2gcj";
    public static final Parcelable.Creator<BDLocation> CREATOR = new a();
    public static final int GPS_ACCURACY_BAD = 3;
    public static final int GPS_ACCURACY_GOOD = 1;
    public static final int GPS_ACCURACY_MID = 2;
    public static final int GPS_ACCURACY_UNKNOWN = 0;
    public static final int GPS_RECTIFY_INDOOR = 1;
    public static final int GPS_RECTIFY_NONE = 0;
    public static final int GPS_RECTIFY_OUTDOOR = 2;
    public static final int INDOOR_LOCATION_NEARBY_SURPPORT_TRUE = 2;
    public static final int INDOOR_LOCATION_SOURCE_BLUETOOTH = 4;
    public static final int INDOOR_LOCATION_SOURCE_MAGNETIC = 2;
    public static final int INDOOR_LOCATION_SOURCE_SMALLCELLSTATION = 8;
    public static final int INDOOR_LOCATION_SOURCE_UNKNOWN = 0;
    public static final int INDOOR_LOCATION_SOURCE_WIFI = 1;
    public static final int INDOOR_LOCATION_SURPPORT_FALSE = 0;
    public static final int INDOOR_LOCATION_SURPPORT_TRUE = 1;
    public static final int INDOOR_NETWORK_STATE_HIGH = 2;
    public static final int INDOOR_NETWORK_STATE_LOW = 0;
    public static final int INDOOR_NETWORK_STATE_MIDDLE = 1;
    public static final int LOCATION_WHERE_IN_CN = 1;
    public static final int LOCATION_WHERE_OUT_CN = 0;
    public static final int LOCATION_WHERE_UNKNOW = 2;
    public static final int OPERATORS_TYPE_MOBILE = 1;
    public static final int OPERATORS_TYPE_TELECOMU = 3;
    public static final int OPERATORS_TYPE_UNICOM = 2;
    public static final int OPERATORS_TYPE_UNKONW = 0;
    public static final int TypeCacheLocation = 65;
    public static final int TypeCriteriaException = 62;
    public static final int TypeGpsLocation = 61;
    public static final int TypeNetWorkException = 63;
    public static final int TypeNetWorkLocation = 161;
    public static final int TypeNone = 0;
    public static final int TypeOffLineLocation = 66;
    public static final int TypeOffLineLocationFail = 67;
    public static final int TypeOffLineLocationNetworkFail = 68;
    public static final int TypeServerCheckKeyError = 505;
    public static final int TypeServerDecryptError = 162;
    public static final int TypeServerError = 167;
    public static final int USER_INDDOR_TRUE = 1;
    public static final int USER_INDOOR_FALSE = 0;
    public static final int USER_INDOOR_UNKNOW = -1;
    private int A;
    private String B;
    private int C;
    private String D;
    private int E;
    private int F;
    private int G;
    private int H;
    private String I;
    private String J;
    private String K;
    private List<Poi> L;
    private String M;
    private String N;
    private String O;
    private Bundle P;
    private int Q;
    private int R;
    private long S;
    private String T;
    private double U;
    private double V;
    private boolean W;
    private PoiRegion X;
    private float Y;
    private int a;
    private String b;
    private double c;
    private double d;
    private boolean e;
    private double f;
    private boolean g;
    private float h;
    private boolean i;
    private float j;
    private boolean k;
    private int l;
    private float m;
    private String n;
    private boolean o;
    private String p;
    private String q;
    private String r;
    private String s;
    private boolean t;
    private Address u;
    private String v;
    private String w;
    private String x;
    private boolean y;
    private int z;

    public BDLocation() {
        this.a = 0;
        this.b = null;
        this.c = Double.MIN_VALUE;
        this.d = Double.MIN_VALUE;
        this.e = false;
        this.f = Double.MIN_VALUE;
        this.g = false;
        this.h = 0.0f;
        this.i = false;
        this.j = 0.0f;
        this.k = false;
        this.l = -1;
        this.m = -1.0f;
        this.n = null;
        this.o = false;
        this.p = null;
        this.q = null;
        this.r = null;
        this.s = null;
        this.t = false;
        this.u = new Address.Builder().build();
        this.v = null;
        this.w = null;
        this.x = null;
        this.y = false;
        this.z = 0;
        this.A = 1;
        this.B = null;
        this.D = "";
        this.E = -1;
        this.F = 0;
        this.G = 2;
        this.H = 0;
        this.I = null;
        this.J = null;
        this.K = null;
        this.L = null;
        this.M = null;
        this.N = null;
        this.O = null;
        this.P = new Bundle();
        this.Q = 0;
        this.R = 0;
        this.S = 0;
        this.T = null;
        this.U = Double.MIN_VALUE;
        this.V = Double.MIN_VALUE;
        this.W = false;
        this.X = null;
        this.Y = -1.0f;
    }

    private BDLocation(Parcel parcel) {
        Parcel parcel2 = parcel;
        this.a = 0;
        this.b = null;
        this.c = Double.MIN_VALUE;
        this.d = Double.MIN_VALUE;
        this.e = false;
        this.f = Double.MIN_VALUE;
        this.g = false;
        this.h = 0.0f;
        this.i = false;
        this.j = 0.0f;
        this.k = false;
        this.l = -1;
        this.m = -1.0f;
        this.n = null;
        this.o = false;
        this.p = null;
        this.q = null;
        this.r = null;
        this.s = null;
        this.t = false;
        this.u = new Address.Builder().build();
        this.v = null;
        this.w = null;
        this.x = null;
        this.y = false;
        this.z = 0;
        this.A = 1;
        this.B = null;
        this.D = "";
        this.E = -1;
        this.F = 0;
        this.G = 2;
        this.H = 0;
        this.I = null;
        this.J = null;
        this.K = null;
        this.L = null;
        this.M = null;
        this.N = null;
        this.O = null;
        this.P = new Bundle();
        this.Q = 0;
        this.R = 0;
        this.S = 0;
        this.T = null;
        this.U = Double.MIN_VALUE;
        this.V = Double.MIN_VALUE;
        this.W = false;
        this.X = null;
        this.Y = -1.0f;
        this.a = parcel.readInt();
        this.b = parcel.readString();
        this.c = parcel.readDouble();
        this.d = parcel.readDouble();
        this.f = parcel.readDouble();
        this.h = parcel.readFloat();
        this.j = parcel.readFloat();
        this.l = parcel.readInt();
        this.m = parcel.readFloat();
        this.v = parcel.readString();
        this.z = parcel.readInt();
        this.w = parcel.readString();
        this.x = parcel.readString();
        this.B = parcel.readString();
        String readString = parcel.readString();
        String readString2 = parcel.readString();
        String readString3 = parcel.readString();
        String readString4 = parcel.readString();
        String readString5 = parcel.readString();
        String readString6 = parcel.readString();
        parcel.readString();
        String readString7 = parcel.readString();
        String readString8 = parcel.readString();
        String readString9 = parcel.readString();
        this.u = new Address.Builder().country(readString7).countryCode(readString8).province(readString).city(readString2).cityCode(readString6).district(readString3).street(readString4).streetNumber(readString5).adcode(readString9).town(parcel.readString()).build();
        boolean[] zArr = new boolean[8];
        this.C = parcel.readInt();
        this.D = parcel.readString();
        this.q = parcel.readString();
        this.r = parcel.readString();
        this.s = parcel.readString();
        this.A = parcel.readInt();
        this.M = parcel.readString();
        this.E = parcel.readInt();
        this.F = parcel.readInt();
        this.G = parcel.readInt();
        this.H = parcel.readInt();
        this.I = parcel.readString();
        this.J = parcel.readString();
        this.K = parcel.readString();
        this.Q = parcel.readInt();
        this.N = parcel.readString();
        this.R = parcel.readInt();
        this.O = parcel.readString();
        this.T = parcel.readString();
        this.S = parcel.readLong();
        this.U = parcel.readDouble();
        this.V = parcel.readDouble();
        this.Y = parcel.readFloat();
        try {
            parcel2.readBooleanArray(zArr);
            this.e = zArr[0];
            this.g = zArr[1];
            this.i = zArr[2];
            this.k = zArr[3];
            this.o = zArr[4];
            this.t = zArr[5];
            this.y = zArr[6];
            this.W = zArr[7];
        } catch (Exception e2) {
        }
        ArrayList arrayList = new ArrayList();
        try {
            parcel2.readList(arrayList, Poi.class.getClassLoader());
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        if (arrayList.size() == 0) {
            this.L = null;
        } else {
            this.L = arrayList;
        }
        try {
            this.P = parcel.readBundle();
        } catch (Exception e4) {
            e4.printStackTrace();
            this.P = new Bundle();
        }
        try {
            this.X = (PoiRegion) parcel2.readParcelable(PoiRegion.class.getClassLoader());
        } catch (Exception e5) {
            this.X = null;
            e5.printStackTrace();
        }
    }

    /* synthetic */ BDLocation(Parcel parcel, a aVar) {
        this(parcel);
    }

    public BDLocation(BDLocation bDLocation) {
        this.a = 0;
        ArrayList arrayList = null;
        this.b = null;
        this.c = Double.MIN_VALUE;
        this.d = Double.MIN_VALUE;
        this.e = false;
        this.f = Double.MIN_VALUE;
        this.g = false;
        this.h = 0.0f;
        this.i = false;
        this.j = 0.0f;
        this.k = false;
        this.l = -1;
        this.m = -1.0f;
        this.n = null;
        this.o = false;
        this.p = null;
        this.q = null;
        this.r = null;
        this.s = null;
        this.t = false;
        this.u = new Address.Builder().build();
        this.v = null;
        this.w = null;
        this.x = null;
        this.y = false;
        this.z = 0;
        this.A = 1;
        this.B = null;
        this.D = "";
        this.E = -1;
        this.F = 0;
        this.G = 2;
        this.H = 0;
        this.I = null;
        this.J = null;
        this.K = null;
        this.L = null;
        this.M = null;
        this.N = null;
        this.O = null;
        this.P = new Bundle();
        this.Q = 0;
        this.R = 0;
        this.S = 0;
        this.T = null;
        this.U = Double.MIN_VALUE;
        this.V = Double.MIN_VALUE;
        this.W = false;
        this.X = null;
        this.Y = -1.0f;
        this.a = bDLocation.a;
        this.b = bDLocation.b;
        this.c = bDLocation.c;
        this.d = bDLocation.d;
        this.e = bDLocation.e;
        this.f = bDLocation.f;
        this.g = bDLocation.g;
        this.h = bDLocation.h;
        this.i = bDLocation.i;
        this.j = bDLocation.j;
        this.k = bDLocation.k;
        this.l = bDLocation.l;
        this.m = bDLocation.m;
        this.n = bDLocation.n;
        this.o = bDLocation.o;
        this.p = bDLocation.p;
        this.t = bDLocation.t;
        this.u = new Address.Builder().country(bDLocation.u.country).countryCode(bDLocation.u.countryCode).province(bDLocation.u.province).city(bDLocation.u.city).cityCode(bDLocation.u.cityCode).district(bDLocation.u.district).street(bDLocation.u.street).streetNumber(bDLocation.u.streetNumber).adcode(bDLocation.u.adcode).town(bDLocation.u.town).build();
        this.v = bDLocation.v;
        this.w = bDLocation.w;
        this.x = bDLocation.x;
        this.A = bDLocation.A;
        this.z = bDLocation.z;
        this.y = bDLocation.y;
        this.B = bDLocation.B;
        this.C = bDLocation.C;
        this.D = bDLocation.D;
        this.q = bDLocation.q;
        this.r = bDLocation.r;
        this.s = bDLocation.s;
        this.E = bDLocation.E;
        this.F = bDLocation.F;
        this.G = bDLocation.F;
        this.H = bDLocation.H;
        this.I = bDLocation.I;
        this.J = bDLocation.J;
        this.K = bDLocation.K;
        this.Q = bDLocation.Q;
        this.O = bDLocation.O;
        this.U = bDLocation.U;
        this.V = bDLocation.V;
        this.S = bDLocation.S;
        this.N = bDLocation.N;
        if (bDLocation.L != null) {
            arrayList = new ArrayList();
            for (int i2 = 0; i2 < bDLocation.L.size(); i2++) {
                Poi poi = bDLocation.L.get(i2);
                arrayList.add(new Poi(poi.getId(), poi.getName(), poi.getRank(), poi.getTags(), poi.getAddr()));
            }
        }
        this.L = arrayList;
        this.M = bDLocation.M;
        this.P = bDLocation.P;
        this.R = bDLocation.R;
        this.W = bDLocation.W;
        this.X = bDLocation.X;
        this.Y = bDLocation.Y;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0388, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0389, code lost:
        r3 = r0;
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0394, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0395, code lost:
        r3 = r0;
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x03a1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x03a2, code lost:
        r3 = r0;
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x03ae, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x03af, code lost:
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x03bc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x03bd, code lost:
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x03cb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x03cc, code lost:
        r9 = r28;
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x03db, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x03dc, code lost:
        r9 = r28;
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x03eb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x03ec, code lost:
        r9 = r28;
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0402, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0403, code lost:
        r3 = r0;
        r9 = null;
        r10 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0406, code lost:
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0407, code lost:
        r13 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0408, code lost:
        r14 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x0409, code lost:
        r15 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x040a, code lost:
        r18 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x040c, code lost:
        r19 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:?, code lost:
        r3.printStackTrace();
        r28 = "y";
        r6 = r12;
        r3 = r19;
        r4 = null;
        r19 = "x";
        r12 = r10;
        r10 = r18;
        r7 = null;
        r18 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x05bf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:?, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x06d7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x06d8, code lost:
        r3 = r0;
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x06db, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x06dc, code lost:
        r0.printStackTrace();
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x06e2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x06e3, code lost:
        r2 = false;
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x06e5, code lost:
        r3.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x06e8, code lost:
        r1.a = r2 ? 1 : 0;
        r1.o = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x028f, code lost:
        r3 = null;
        r15 = false;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0421 A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x045a A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0472 A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0489 A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x04a8 A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x04c1 A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x04da A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x04f3 A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x051e  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x05d2 A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x05dc A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x05e6 A[Catch:{ Exception -> 0x05f7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x05f2 A[Catch:{ Exception -> 0x05f7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x05fc A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0609 A[Catch:{ Exception -> 0x06d7, Error -> 0x06db }] */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0629 A[Catch:{ all -> 0x0647 }] */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x06db A[ExcHandler: Error (r0v1 'e' java.lang.Error A[CUSTOM_DECLARE]), Splitter:B:4:0x009d] */
    /* JADX WARNING: Removed duplicated region for block: B:362:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:296:0x05e0=Splitter:B:296:0x05e0, B:17:0x0135=Splitter:B:17:0x0135, B:302:0x05f8=Splitter:B:302:0x05f8, B:23:0x014d=Splitter:B:23:0x014d, B:212:0x040e=Splitter:B:212:0x040e} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:302:0x05f8=Splitter:B:302:0x05f8, B:23:0x014d=Splitter:B:23:0x014d, B:212:0x040e=Splitter:B:212:0x040e} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public BDLocation(java.lang.String r28) {
        /*
            r27 = this;
            r1 = r27
            r2 = r28
            java.lang.String r3 = "aptagd"
            java.lang.String r4 = "floor"
            java.lang.String r5 = "aptag"
            java.lang.String r6 = "sema"
            r27.<init>()
            r7 = 0
            r1.a = r7
            r8 = 0
            r1.b = r8
            r9 = 1
            r1.c = r9
            r1.d = r9
            r1.e = r7
            r1.f = r9
            r1.g = r7
            r11 = 0
            r1.h = r11
            r1.i = r7
            r1.j = r11
            r1.k = r7
            r11 = -1
            r1.l = r11
            r12 = -1082130432(0xffffffffbf800000, float:-1.0)
            r1.m = r12
            r1.n = r8
            r1.o = r7
            r1.p = r8
            r1.q = r8
            r1.r = r8
            r1.s = r8
            r1.t = r7
            com.baidu.location.Address$Builder r13 = new com.baidu.location.Address$Builder
            r13.<init>()
            com.baidu.location.Address r13 = r13.build()
            r1.u = r13
            r1.v = r8
            r1.w = r8
            r1.x = r8
            r1.y = r7
            r1.z = r7
            r13 = 1
            r1.A = r13
            r1.B = r8
            java.lang.String r14 = ""
            r1.D = r14
            r1.E = r11
            r1.F = r7
            r11 = 2
            r1.G = r11
            r1.H = r7
            r1.I = r8
            r1.J = r8
            r1.K = r8
            r1.L = r8
            r1.M = r8
            r1.N = r8
            r1.O = r8
            android.os.Bundle r15 = new android.os.Bundle
            r15.<init>()
            r1.P = r15
            r1.Q = r7
            r1.R = r7
            r16 = r14
            r13 = 0
            r1.S = r13
            r1.T = r8
            r1.U = r9
            r1.V = r9
            r1.W = r7
            r1.X = r8
            r1.Y = r12
            if (r2 == 0) goto L_0x06ec
            r12 = r16
            boolean r13 = r2.equals(r12)
            if (r13 == 0) goto L_0x009d
            goto L_0x06ec
        L_0x009d:
            org.json.JSONObject r13 = new org.json.JSONObject     // Catch:{ Exception -> 0x06e2, Error -> 0x06db }
            r13.<init>(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r2 = "result"
            org.json.JSONObject r2 = r13.getJSONObject(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r14 = "error"
            java.lang.String r14 = r2.getString(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            int r14 = java.lang.Integer.parseInt(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setLocType(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r15 = "time"
            java.lang.String r2 = r2.getString(r15)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setTime(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r2 = 61
            java.lang.String r15 = "gcj02"
            java.lang.String r9 = "radius"
            java.lang.String r10 = "point"
            java.lang.String r8 = "content"
            java.lang.String r11 = "in_cn"
            java.lang.String r7 = "x"
            r17 = r4
            java.lang.String r4 = "y"
            if (r14 != r2) goto L_0x015d
            org.json.JSONObject r2 = r13.getJSONObject(r8)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            org.json.JSONObject r3 = r2.getJSONObject(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r4 = r3.getString(r4)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            double r4 = java.lang.Double.parseDouble(r4)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setLatitude(r4)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = r3.getString(r7)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            double r3 = java.lang.Double.parseDouble(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setLongitude(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = r2.getString(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            float r3 = java.lang.Float.parseFloat(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setRadius(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = "s"
            java.lang.String r3 = r2.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            float r3 = java.lang.Float.parseFloat(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setSpeed(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = "d"
            java.lang.String r3 = r2.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            float r3 = java.lang.Float.parseFloat(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setDirection(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = "n"
            java.lang.String r3 = r2.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setSatelliteNumber(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = "h"
            boolean r3 = r2.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x0135
            java.lang.String r3 = "h"
            double r3 = r2.getDouble(r3)     // Catch:{ Exception -> 0x0134, Error -> 0x06db }
            r1.setAltitude(r3)     // Catch:{ Exception -> 0x0134, Error -> 0x06db }
            goto L_0x0135
        L_0x0134:
            r0 = move-exception
        L_0x0135:
            boolean r3 = r2.has(r11)     // Catch:{ Exception -> 0x014c, Error -> 0x06db }
            if (r3 == 0) goto L_0x0147
            java.lang.String r2 = r2.getString(r11)     // Catch:{ Exception -> 0x014c, Error -> 0x06db }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x014c, Error -> 0x06db }
            r1.setLocationWhere(r2)     // Catch:{ Exception -> 0x014c, Error -> 0x06db }
            goto L_0x014d
        L_0x0147:
            r2 = 1
            r1.setLocationWhere(r2)     // Catch:{ Exception -> 0x014c, Error -> 0x06db }
            goto L_0x014d
        L_0x014c:
            r0 = move-exception
        L_0x014d:
            int r2 = r1.A     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r2 != 0) goto L_0x0158
            java.lang.String r2 = "wgs84"
        L_0x0153:
            r1.setCoorType(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x06ec
        L_0x0158:
            r1.setCoorType(r15)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x06ec
        L_0x015d:
            r2 = r15
            r15 = 161(0xa1, float:2.26E-43)
            if (r14 != r15) goto L_0x0688
            org.json.JSONObject r8 = r13.getJSONObject(r8)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            org.json.JSONObject r10 = r8.getJSONObject(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r13 = r10.getString(r4)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            double r13 = java.lang.Double.parseDouble(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setLatitude(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r10 = r10.getString(r7)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            double r13 = java.lang.Double.parseDouble(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setLongitude(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r9 = r8.getString(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            float r9 = java.lang.Float.parseFloat(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setRadius(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r9 = r8.has(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r10 = "addr"
            if (r9 == 0) goto L_0x0280
            org.json.JSONObject r6 = r8.getJSONObject(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r9 = r6.has(r5)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r9 == 0) goto L_0x01ac
            java.lang.String r5 = r6.getString(r5)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r9 = android.text.TextUtils.isEmpty(r5)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r9 != 0) goto L_0x01aa
            r1.q = r5     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x01ac
        L_0x01aa:
            r1.q = r12     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x01ac:
            boolean r5 = r6.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r5 == 0) goto L_0x020f
            org.json.JSONObject r3 = r6.getJSONObject(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r5 = "pois"
            org.json.JSONArray r3 = r3.getJSONArray(r5)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r5.<init>()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r9 = 0
        L_0x01c2:
            int r13 = r3.length()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r9 >= r13) goto L_0x020d
            org.json.JSONObject r13 = r3.getJSONObject(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r14 = "pname"
            java.lang.String r20 = r13.getString(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r14 = "pid"
            java.lang.String r19 = r13.getString(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r14 = "pr"
            double r21 = r13.getDouble(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r14 = "tags"
            boolean r14 = r13.has(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r14 == 0) goto L_0x01ef
            java.lang.String r14 = "tags"
            java.lang.String r14 = r13.getString(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r23 = r14
            goto L_0x01f1
        L_0x01ef:
            r23 = r12
        L_0x01f1:
            boolean r14 = r13.has(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r14 == 0) goto L_0x01fe
            java.lang.String r13 = r13.getString(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r24 = r13
            goto L_0x0200
        L_0x01fe:
            r24 = r12
        L_0x0200:
            com.baidu.location.Poi r13 = new com.baidu.location.Poi     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r18 = r13
            r18.<init>(r19, r20, r21, r23, r24)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r5.add(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            int r9 = r9 + 1
            goto L_0x01c2
        L_0x020d:
            r1.L = r5     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x020f:
            java.lang.String r3 = "poiregion"
            boolean r3 = r6.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x0225
            java.lang.String r3 = "poiregion"
            java.lang.String r3 = r6.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r5 != 0) goto L_0x0225
            r1.r = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x0225:
            java.lang.String r3 = "poi_regions"
            boolean r3 = r6.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x026a
            java.lang.String r3 = "poi_regions"
            org.json.JSONObject r3 = r6.getJSONObject(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r5 = "direction_desc"
            boolean r5 = r3.has(r5)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r5 == 0) goto L_0x0242
            java.lang.String r5 = "direction_desc"
            java.lang.String r5 = r3.getString(r5)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x0243
        L_0x0242:
            r5 = r12
        L_0x0243:
            java.lang.String r9 = "name"
            boolean r9 = r3.has(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r9 == 0) goto L_0x0252
            java.lang.String r9 = "name"
            java.lang.String r9 = r3.getString(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x0253
        L_0x0252:
            r9 = r12
        L_0x0253:
            java.lang.String r13 = "tag"
            boolean r13 = r3.has(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r13 == 0) goto L_0x0262
            java.lang.String r13 = "tag"
            java.lang.String r3 = r3.getString(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x0263
        L_0x0262:
            r3 = r12
        L_0x0263:
            com.baidu.location.PoiRegion r13 = new com.baidu.location.PoiRegion     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r13.<init>(r5, r9, r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.X = r13     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x026a:
            java.lang.String r3 = "regular"
            boolean r3 = r6.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x0280
            java.lang.String r3 = "regular"
            java.lang.String r3 = r6.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r5 != 0) goto L_0x0280
            r1.s = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x0280:
            boolean r3 = r8.has(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r5 = ","
            if (r3 == 0) goto L_0x045d
            org.json.JSONObject r3 = r8.getJSONObject(r10)     // Catch:{ Exception -> 0x028e, Error -> 0x06db }
            r15 = 1
            goto L_0x0291
        L_0x028e:
            r0 = move-exception
            r3 = 0
            r15 = 0
        L_0x0291:
            if (r3 == 0) goto L_0x0370
            java.lang.String r6 = "city"
            boolean r6 = r3.has(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r6 == 0) goto L_0x02a2
            java.lang.String r6 = "city"
            java.lang.String r6 = r3.getString(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x02a3
        L_0x02a2:
            r6 = r12
        L_0x02a3:
            java.lang.String r9 = "city_code"
            boolean r9 = r3.has(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r9 == 0) goto L_0x02b2
            java.lang.String r9 = "city_code"
            java.lang.String r9 = r3.getString(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x02b3
        L_0x02b2:
            r9 = r12
        L_0x02b3:
            java.lang.String r10 = "country"
            boolean r10 = r3.has(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r10 == 0) goto L_0x02c2
            java.lang.String r10 = "country"
            java.lang.String r10 = r3.getString(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x02c3
        L_0x02c2:
            r10 = r12
        L_0x02c3:
            java.lang.String r13 = "country_code"
            boolean r13 = r3.has(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r13 == 0) goto L_0x02d2
            java.lang.String r13 = "country_code"
            java.lang.String r13 = r3.getString(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x02d3
        L_0x02d2:
            r13 = r12
        L_0x02d3:
            java.lang.String r14 = "province"
            boolean r14 = r3.has(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r14 == 0) goto L_0x02e4
            java.lang.String r14 = "province"
            java.lang.String r14 = r3.getString(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r28 = r6
            goto L_0x02e7
        L_0x02e4:
            r28 = r6
            r14 = r12
        L_0x02e7:
            java.lang.String r6 = "district"
            boolean r6 = r3.has(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r6 == 0) goto L_0x02f8
            java.lang.String r6 = "district"
            java.lang.String r6 = r3.getString(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r18 = r6
            goto L_0x02fa
        L_0x02f8:
            r18 = r12
        L_0x02fa:
            java.lang.String r6 = "street"
            boolean r6 = r3.has(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r6 == 0) goto L_0x030b
            java.lang.String r6 = "street"
            java.lang.String r6 = r3.getString(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r19 = r6
            goto L_0x030d
        L_0x030b:
            r19 = r12
        L_0x030d:
            java.lang.String r6 = "street_number"
            boolean r6 = r3.has(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r6 == 0) goto L_0x031e
            java.lang.String r6 = "street_number"
            java.lang.String r6 = r3.getString(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r20 = r6
            goto L_0x0320
        L_0x031e:
            r20 = r12
        L_0x0320:
            java.lang.String r6 = "adcode"
            boolean r6 = r3.has(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r6 == 0) goto L_0x032f
            java.lang.String r6 = "adcode"
            java.lang.String r6 = r3.getString(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x0330
        L_0x032f:
            r6 = r12
        L_0x0330:
            java.lang.String r12 = "town"
            boolean r12 = r3.has(r12)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r12 == 0) goto L_0x0358
            java.lang.String r12 = "town"
            java.lang.String r3 = r3.getString(r12)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r12 = r14
            r14 = r20
            r25 = r6
            r6 = r28
            r28 = r4
            r4 = r25
            r26 = r7
            r7 = r3
            r3 = r13
            r13 = r18
            r18 = r15
            r15 = r9
            r9 = r19
            r19 = r26
            goto L_0x041f
        L_0x0358:
            r3 = r13
            r12 = r14
            r13 = r18
            r14 = r20
            r18 = r15
            r15 = r9
            r9 = r19
            r19 = r7
            r7 = 0
            r25 = r6
            r6 = r28
            r28 = r4
            r4 = r25
            goto L_0x041f
        L_0x0370:
            java.lang.String r3 = r8.getString(r10)     // Catch:{ Exception -> 0x0402, Error -> 0x06db }
            java.lang.String[] r3 = r3.split(r5)     // Catch:{ Exception -> 0x0402, Error -> 0x06db }
            int r6 = r3.length     // Catch:{ Exception -> 0x0402, Error -> 0x06db }
            if (r6 <= 0) goto L_0x0380
            r9 = 0
            r10 = r3[r9]     // Catch:{ Exception -> 0x0402, Error -> 0x06db }
            r9 = 1
            goto L_0x0382
        L_0x0380:
            r9 = 1
            r10 = 0
        L_0x0382:
            if (r6 <= r9) goto L_0x038d
            r12 = r3[r9]     // Catch:{ Exception -> 0x0388, Error -> 0x06db }
            r9 = 2
            goto L_0x038f
        L_0x0388:
            r0 = move-exception
            r3 = r0
            r9 = 0
            goto L_0x0406
        L_0x038d:
            r9 = 2
            r12 = 0
        L_0x038f:
            if (r6 <= r9) goto L_0x0399
            r13 = r3[r9]     // Catch:{ Exception -> 0x0394, Error -> 0x06db }
            goto L_0x039a
        L_0x0394:
            r0 = move-exception
            r3 = r0
            r9 = 0
            goto L_0x0407
        L_0x0399:
            r13 = 0
        L_0x039a:
            r9 = 3
            if (r6 <= r9) goto L_0x03a6
            r9 = 3
            r9 = r3[r9]     // Catch:{ Exception -> 0x03a1, Error -> 0x06db }
            goto L_0x03a7
        L_0x03a1:
            r0 = move-exception
            r3 = r0
            r9 = 0
            goto L_0x0408
        L_0x03a6:
            r9 = 0
        L_0x03a7:
            r14 = 4
            if (r6 <= r14) goto L_0x03b2
            r14 = 4
            r14 = r3[r14]     // Catch:{ Exception -> 0x03ae, Error -> 0x06db }
            goto L_0x03b3
        L_0x03ae:
            r0 = move-exception
            r3 = r0
            goto L_0x0408
        L_0x03b2:
            r14 = 0
        L_0x03b3:
            r15 = 5
            if (r6 <= r15) goto L_0x03bf
            r15 = 5
            r15 = r3[r15]     // Catch:{ Exception -> 0x03bc, Error -> 0x06db }
            r28 = r9
            goto L_0x03c2
        L_0x03bc:
            r0 = move-exception
            r3 = r0
            goto L_0x0409
        L_0x03bf:
            r28 = r9
            r15 = 0
        L_0x03c2:
            r9 = 6
            if (r6 <= r9) goto L_0x03d0
            r9 = 6
            r9 = r3[r9]     // Catch:{ Exception -> 0x03cb, Error -> 0x06db }
            r18 = r9
            goto L_0x03d2
        L_0x03cb:
            r0 = move-exception
            r9 = r28
            r3 = r0
            goto L_0x040a
        L_0x03d0:
            r18 = 0
        L_0x03d2:
            r9 = 7
            if (r6 <= r9) goto L_0x03e0
            r9 = 7
            r9 = r3[r9]     // Catch:{ Exception -> 0x03db, Error -> 0x06db }
            r19 = r9
            goto L_0x03e2
        L_0x03db:
            r0 = move-exception
            r9 = r28
            r3 = r0
            goto L_0x040c
        L_0x03e0:
            r19 = 0
        L_0x03e2:
            r9 = 8
            if (r6 <= r9) goto L_0x03f0
            r6 = 8
            r3 = r3[r6]     // Catch:{ Exception -> 0x03eb, Error -> 0x06db }
            goto L_0x03f1
        L_0x03eb:
            r0 = move-exception
            r9 = r28
            r3 = r0
            goto L_0x040e
        L_0x03f0:
            r3 = 0
        L_0x03f1:
            r9 = r28
            r28 = r4
            r6 = r12
            r4 = r3
            r12 = r10
            r10 = r18
            r3 = r19
            r18 = 1
            r19 = r7
            r7 = 0
            goto L_0x041f
        L_0x0402:
            r0 = move-exception
            r3 = r0
            r9 = 0
            r10 = 0
        L_0x0406:
            r12 = 0
        L_0x0407:
            r13 = 0
        L_0x0408:
            r14 = 0
        L_0x0409:
            r15 = 0
        L_0x040a:
            r18 = 0
        L_0x040c:
            r19 = 0
        L_0x040e:
            r3.printStackTrace()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r28 = r4
            r6 = r12
            r3 = r19
            r4 = 0
            r19 = r7
            r12 = r10
            r10 = r18
            r7 = 0
            r18 = 0
        L_0x041f:
            if (r18 == 0) goto L_0x045a
            r18 = r5
            com.baidu.location.Address$Builder r5 = new com.baidu.location.Address$Builder     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r5.<init>()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r5 = r5.country(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r5.countryCode(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r3.province(r12)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r3.city(r6)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r3.cityCode(r15)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r3.district(r13)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r3.street(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r3.streetNumber(r14)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r3.adcode(r4)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address$Builder r3 = r3.town(r7)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            com.baidu.location.Address r3 = r3.build()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.u = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r3 = 1
            r1.o = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x046a
        L_0x045a:
            r18 = r5
            goto L_0x046a
        L_0x045d:
            r28 = r4
            r18 = r5
            r19 = r7
            r3 = 0
            r1.o = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r3 = 0
            r1.setAddrStr(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x046a:
            r3 = r17
            boolean r4 = r8.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r4 == 0) goto L_0x0481
            java.lang.String r3 = r8.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.v = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x0481
            r3 = 0
            r1.v = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x0481:
            java.lang.String r3 = "indoor"
            boolean r3 = r8.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x04a0
            java.lang.String r3 = "indoor"
            java.lang.String r3 = r8.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r4 != 0) goto L_0x04a0
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setUserIndoorState(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x04a0:
            java.lang.String r3 = "loctp"
            boolean r3 = r8.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x04b9
            java.lang.String r3 = "loctp"
            java.lang.String r3 = r8.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.B = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x04b9
            r3 = 0
            r1.B = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x04b9:
            java.lang.String r3 = "bldgid"
            boolean r3 = r8.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x04d2
            java.lang.String r3 = "bldgid"
            java.lang.String r3 = r8.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.w = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x04d2
            r3 = 0
            r1.w = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x04d2:
            java.lang.String r3 = "bldg"
            boolean r3 = r8.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x04eb
            java.lang.String r3 = "bldg"
            java.lang.String r3 = r8.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.x = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x04eb
            r3 = 0
            r1.x = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x04eb:
            java.lang.String r3 = "ibav"
            boolean r3 = r8.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x0516
            java.lang.String r3 = "ibav"
            java.lang.String r3 = r8.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r4 == 0) goto L_0x0503
        L_0x04ff:
            r4 = 0
            r1.z = r4     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x0516
        L_0x0503:
            java.lang.String r4 = "0"
            boolean r4 = r3.equals(r4)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r4 == 0) goto L_0x050c
            goto L_0x04ff
        L_0x050c:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.z = r3     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x0516:
            java.lang.String r3 = "indoorflags"
            boolean r3 = r8.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x05ca
            java.lang.String r3 = "indoorflags"
            org.json.JSONObject r3 = r8.getJSONObject(r3)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            java.lang.String r4 = "area"
            boolean r4 = r3.has(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            if (r4 == 0) goto L_0x0547
            java.lang.String r4 = "area"
            java.lang.String r4 = r3.getString(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            int r4 = r4.intValue()     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            if (r4 != 0) goto L_0x0541
            r5 = 2
            r1.setIndoorLocationSurpport(r5)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            goto L_0x0547
        L_0x0541:
            r5 = 1
            if (r4 != r5) goto L_0x0547
            r1.setIndoorLocationSurpport(r5)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
        L_0x0547:
            java.lang.String r4 = "support"
            boolean r4 = r3.has(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            if (r4 == 0) goto L_0x0560
            java.lang.String r4 = "support"
            java.lang.String r4 = r3.getString(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            int r4 = r4.intValue()     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            r1.setIndoorLocationSource(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
        L_0x0560:
            java.lang.String r4 = "inbldg"
            boolean r4 = r3.has(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            if (r4 == 0) goto L_0x0570
            java.lang.String r4 = "inbldg"
            java.lang.String r4 = r3.getString(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            r1.I = r4     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
        L_0x0570:
            java.lang.String r4 = "inbldgid"
            boolean r4 = r3.has(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            if (r4 == 0) goto L_0x0580
            java.lang.String r4 = "inbldgid"
            java.lang.String r4 = r3.getString(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            r1.J = r4     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
        L_0x0580:
            java.lang.String r4 = "polygon"
            boolean r4 = r3.has(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            if (r4 == 0) goto L_0x0591
            java.lang.String r4 = "polygon"
            java.lang.String r4 = r3.getString(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            r1.setIndoorSurpportPolygon(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
        L_0x0591:
            java.lang.String r4 = "ret_fields"
            boolean r4 = r3.has(r4)     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            if (r4 == 0) goto L_0x05ca
            java.lang.String r4 = "ret_fields"
            java.lang.String r3 = r3.getString(r4)     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            java.lang.String r4 = "\\|"
            java.lang.String[] r3 = r3.split(r4)     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            int r4 = r3.length     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            r5 = 0
        L_0x05a7:
            if (r5 >= r4) goto L_0x05ca
            r6 = r3[r5]     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            java.lang.String r7 = "="
            java.lang.String[] r6 = r6.split(r7)     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            r7 = 0
            r9 = r6[r7]     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            r7 = 1
            r6 = r6[r7]     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            android.os.Bundle r7 = r1.P     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            r7.putString(r9, r6)     // Catch:{ Exception -> 0x05bf, Error -> 0x06db }
            int r5 = r5 + 1
            goto L_0x05a7
        L_0x05bf:
            r0 = move-exception
            r3 = r0
            r3.printStackTrace()     // Catch:{ Exception -> 0x05c5, Error -> 0x06db }
            goto L_0x05ca
        L_0x05c5:
            r0 = move-exception
            r3 = r0
            r3.printStackTrace()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x05ca:
            java.lang.String r3 = "gpscs"
            boolean r3 = r8.has(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 == 0) goto L_0x05dc
            java.lang.String r3 = "gpscs"
            int r3 = r8.getInt(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setGpsCheckStatus(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x05e0
        L_0x05dc:
            r3 = 0
            r1.setGpsCheckStatus(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x05e0:
            boolean r3 = r8.has(r11)     // Catch:{ Exception -> 0x05f7, Error -> 0x06db }
            if (r3 == 0) goto L_0x05f2
            java.lang.String r3 = r8.getString(r11)     // Catch:{ Exception -> 0x05f7, Error -> 0x06db }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x05f7, Error -> 0x06db }
            r1.setLocationWhere(r3)     // Catch:{ Exception -> 0x05f7, Error -> 0x06db }
            goto L_0x05f8
        L_0x05f2:
            r3 = 1
            r1.setLocationWhere(r3)     // Catch:{ Exception -> 0x05f7, Error -> 0x06db }
            goto L_0x05f8
        L_0x05f7:
            r0 = move-exception
        L_0x05f8:
            int r3 = r1.A     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r3 != 0) goto L_0x05fe
            java.lang.String r2 = "wgs84"
        L_0x05fe:
            r1.setCoorType(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r2 = "navi"
            boolean r2 = r8.has(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r2 == 0) goto L_0x0611
            java.lang.String r2 = "navi"
            java.lang.String r2 = r8.getString(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.T = r2     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x0611:
            java.lang.String r2 = "navi_client"
            boolean r2 = r8.has(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r2 == 0) goto L_0x064c
            java.lang.String r2 = "navi_client"
            java.lang.String r2 = r8.getString(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r2 == 0) goto L_0x064c
            r3 = r18
            boolean r4 = r2.contains(r3)     // Catch:{ all -> 0x0647 }
            if (r4 == 0) goto L_0x064c
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x0647 }
            r3 = 0
            r4 = r2[r3]     // Catch:{ all -> 0x0647 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0647 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x0647 }
            r4 = 1
            r2 = r2[r4]     // Catch:{ all -> 0x0647 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0647 }
            r2.intValue()     // Catch:{ all -> 0x0647 }
            if (r3 <= 0) goto L_0x064c
            r1.W = r4     // Catch:{ all -> 0x0647 }
            goto L_0x064c
        L_0x0647:
            r0 = move-exception
            r2 = r0
            r2.printStackTrace()     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
        L_0x064c:
            java.lang.String r2 = "nrl_point"
            boolean r2 = r8.has(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            if (r2 == 0) goto L_0x06ec
            java.lang.String r2 = "nrl_point"
            org.json.JSONObject r2 = r8.getJSONObject(r2)     // Catch:{ all -> 0x0680 }
            r3 = r19
            boolean r4 = r2.has(r3)     // Catch:{ all -> 0x0680 }
            if (r4 == 0) goto L_0x06ec
            r4 = r28
            boolean r5 = r2.has(r4)     // Catch:{ all -> 0x0680 }
            if (r5 == 0) goto L_0x06ec
            java.lang.String r4 = r2.getString(r4)     // Catch:{ all -> 0x0680 }
            double r4 = java.lang.Double.parseDouble(r4)     // Catch:{ all -> 0x0680 }
            r1.U = r4     // Catch:{ all -> 0x0680 }
            java.lang.String r2 = r2.getString(r3)     // Catch:{ all -> 0x0680 }
            double r2 = java.lang.Double.parseDouble(r2)     // Catch:{ all -> 0x0680 }
            r1.V = r2     // Catch:{ all -> 0x0680 }
            goto L_0x06ec
        L_0x0680:
            r0 = move-exception
            r2 = 1
            r1.U = r2     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.V = r2     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x06ec
        L_0x0688:
            r3 = r7
            r5 = 66
            if (r14 == r5) goto L_0x069b
            r5 = 68
            if (r14 != r5) goto L_0x0692
            goto L_0x069b
        L_0x0692:
            r2 = 167(0xa7, float:2.34E-43)
            if (r14 != r2) goto L_0x06ec
            r2 = 2
            r1.setLocationWhere(r2)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x06ec
        L_0x069b:
            org.json.JSONObject r5 = r13.getJSONObject(r8)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            org.json.JSONObject r6 = r5.getJSONObject(r10)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r4 = r6.getString(r4)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            double r7 = java.lang.Double.parseDouble(r4)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setLatitude(r7)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = r6.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            double r3 = java.lang.Double.parseDouble(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setLongitude(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = r5.getString(r9)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            float r3 = java.lang.Float.parseFloat(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.setRadius(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.String r3 = "isCellChanged"
            java.lang.String r3 = r5.getString(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            boolean r3 = java.lang.Boolean.parseBoolean(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            r1.a(r3)     // Catch:{ Exception -> 0x06d7, Error -> 0x06db }
            goto L_0x0153
        L_0x06d7:
            r0 = move-exception
            r3 = r0
            r2 = 0
            goto L_0x06e5
        L_0x06db:
            r0 = move-exception
            r2 = r0
            r2.printStackTrace()
            r2 = 0
            goto L_0x06e8
        L_0x06e2:
            r0 = move-exception
            r2 = 0
            r3 = r0
        L_0x06e5:
            r3.printStackTrace()
        L_0x06e8:
            r1.a = r2
            r1.o = r2
        L_0x06ec:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.BDLocation.<init>(java.lang.String):void");
    }

    private void a(Boolean bool) {
        this.t = bool.booleanValue();
    }

    public int describeContents() {
        return 0;
    }

    public String getAdCode() {
        return this.u.adcode;
    }

    public String getAddrStr() {
        return this.u.address;
    }

    public Address getAddress() {
        return this.u;
    }

    public double getAltitude() {
        return this.f;
    }

    public String getBuildingID() {
        return this.w;
    }

    public String getBuildingName() {
        return this.x;
    }

    public String getCity() {
        return this.u.city;
    }

    public String getCityCode() {
        return this.u.cityCode;
    }

    public String getCoorType() {
        return this.n;
    }

    public String getCountry() {
        return this.u.country;
    }

    public String getCountryCode() {
        return this.u.countryCode;
    }

    public long getDelayTime() {
        return this.S;
    }

    @Deprecated
    public float getDerect() {
        return this.m;
    }

    public float getDirection() {
        return this.m;
    }

    public String getDistrict() {
        return this.u.district;
    }

    public Location getExtraLocation(String str) {
        Bundle bundle = this.P;
        if (bundle == null) {
            return null;
        }
        Parcelable parcelable = bundle.getParcelable(str);
        if (parcelable instanceof Location) {
            return (Location) parcelable;
        }
        return null;
    }

    public String getFloor() {
        return this.v;
    }

    public double[] getFusionLocInfo(String str) {
        return this.P.getDoubleArray(str);
    }

    public int getGpsAccuracyStatus() {
        return this.Q;
    }

    public float getGpsBiasProb() {
        return this.Y;
    }

    public int getGpsCheckStatus() {
        return this.R;
    }

    public int getIndoorLocationSource() {
        return this.H;
    }

    public int getIndoorLocationSurpport() {
        return this.F;
    }

    public String getIndoorLocationSurpportBuidlingID() {
        return this.J;
    }

    public String getIndoorLocationSurpportBuidlingName() {
        return this.I;
    }

    public int getIndoorNetworkState() {
        return this.G;
    }

    public String getIndoorSurpportPolygon() {
        return this.K;
    }

    public double getLatitude() {
        return this.c;
    }

    public int getLocType() {
        return this.a;
    }

    public String getLocTypeDescription() {
        return this.M;
    }

    public String getLocationDescribe() {
        return this.q;
    }

    public String getLocationID() {
        return this.N;
    }

    public int getLocationWhere() {
        return this.A;
    }

    public double getLongitude() {
        return this.d;
    }

    public String getNetworkLocationType() {
        return this.B;
    }

    public double getNrlLat() {
        return this.U;
    }

    public double getNrlLon() {
        return this.V;
    }

    public String getNrlResult() {
        return this.T;
    }

    public int getOperators() {
        return this.C;
    }

    public List<Poi> getPoiList() {
        return this.L;
    }

    public PoiRegion getPoiRegion() {
        return this.X;
    }

    public String getProvince() {
        return this.u.province;
    }

    public float getRadius() {
        return this.j;
    }

    public String getRetFields(String str) {
        return this.P.getString(str);
    }

    public String getRoadLocString() {
        return this.O;
    }

    public int getSatelliteNumber() {
        this.k = true;
        return this.l;
    }

    @Deprecated
    public String getSemaAptag() {
        return this.q;
    }

    public float getSpeed() {
        return this.h;
    }

    public String getStreet() {
        return this.u.street;
    }

    public String getStreetNumber() {
        return this.u.streetNumber;
    }

    public String getTime() {
        return this.b;
    }

    public String getTown() {
        return this.u.town;
    }

    public int getUserIndoorState() {
        return this.E;
    }

    public String getVdrJsonString() {
        Bundle bundle = this.P;
        if (bundle == null || !bundle.containsKey("vdr")) {
            return null;
        }
        return this.P.getString("vdr");
    }

    public boolean hasAddr() {
        return this.o;
    }

    public boolean hasAltitude() {
        return this.e;
    }

    public boolean hasRadius() {
        return this.i;
    }

    public boolean hasSateNumber() {
        return this.k;
    }

    public boolean hasSpeed() {
        return this.g;
    }

    public boolean isCellChangeFlag() {
        return this.t;
    }

    public boolean isInIndoorPark() {
        return this.W;
    }

    public boolean isIndoorLocMode() {
        return this.y;
    }

    public boolean isNrlAvailable() {
        return (this.V == Double.MIN_VALUE || this.U == Double.MIN_VALUE) ? false : true;
    }

    public int isParkAvailable() {
        return this.z;
    }

    public void setAddr(Address address) {
        if (address != null) {
            this.u = address;
            this.o = true;
        }
    }

    public void setAddrStr(String str) {
        this.p = str;
        this.o = str != null;
    }

    public void setAltitude(double d2) {
        if (d2 < 9999.0d) {
            this.f = d2;
            this.e = true;
        }
    }

    public void setBuildingID(String str) {
        this.w = str;
    }

    public void setBuildingName(String str) {
        this.x = str;
    }

    public void setCoorType(String str) {
        this.n = str;
    }

    public void setDelayTime(long j2) {
        this.S = j2;
    }

    public void setDirection(float f2) {
        this.m = f2;
    }

    public void setExtraLocation(String str, Location location) {
        if (this.P == null) {
            this.P = new Bundle();
        }
        this.P.putParcelable(str, location);
    }

    public void setFloor(String str) {
        this.v = str;
    }

    public void setFusionLocInfo(String str, double[] dArr) {
        if (this.P == null) {
            this.P = new Bundle();
        }
        this.P.putDoubleArray(str, dArr);
    }

    public void setGpsAccuracyStatus(int i2) {
        this.Q = i2;
    }

    public void setGpsBiasProb(float f2) {
        this.Y = f2;
    }

    public void setGpsCheckStatus(int i2) {
        this.R = i2;
    }

    public void setIndoorLocMode(boolean z2) {
        this.y = z2;
    }

    public void setIndoorLocationSource(int i2) {
        this.H = i2;
    }

    public void setIndoorLocationSurpport(int i2) {
        this.F = i2;
    }

    public void setIndoorNetworkState(int i2) {
        this.G = i2;
    }

    public void setIndoorSurpportPolygon(String str) {
        this.K = str;
    }

    public void setLatitude(double d2) {
        this.c = d2;
    }

    public void setLocType(int i2) {
        String str;
        this.a = i2;
        if (i2 != 66) {
            if (i2 != 67) {
                if (i2 == 161) {
                    str = "NetWork location successful!";
                } else if (i2 == 162) {
                    str = "NetWork location failed because baidu location service can not decrypt the request query, please check the so file !";
                } else if (i2 == 167) {
                    str = "NetWork location failed because baidu location service can not caculate the location!";
                } else if (i2 != 505) {
                    switch (i2) {
                        case 61:
                            setLocTypeDescription("GPS location successful!");
                            setUserIndoorState(0);
                            return;
                        case 62:
                            str = "Location failed beacuse we can not get any loc information!";
                            break;
                        case 63:
                            break;
                        default:
                            str = "UnKnown!";
                            break;
                    }
                } else {
                    str = "NetWork location failed because baidu location service check the key is unlegal, please check the key in AndroidManifest.xml !";
                }
            }
            str = "Offline location failed, please check the net (wifi/cell)!";
        } else {
            str = "Offline location successful!";
        }
        setLocTypeDescription(str);
    }

    public void setLocTypeDescription(String str) {
        this.M = str;
    }

    public void setLocationDescribe(String str) {
        this.q = str;
    }

    public void setLocationID(String str) {
        this.N = str;
    }

    public void setLocationWhere(int i2) {
        this.A = i2;
    }

    public void setLongitude(double d2) {
        this.d = d2;
    }

    public void setNetworkLocationType(String str) {
        this.B = str;
    }

    public void setNrlData(String str) {
        this.T = str;
    }

    public void setOperators(int i2) {
        this.C = i2;
    }

    public void setParkAvailable(int i2) {
        this.z = i2;
    }

    public void setPoiList(List<Poi> list) {
        this.L = list;
    }

    public void setPoiRegion(PoiRegion poiRegion) {
        this.X = poiRegion;
    }

    public void setRadius(float f2) {
        this.j = f2;
        this.i = true;
    }

    public void setRetFields(String str, String str2) {
        if (this.P == null) {
            this.P = new Bundle();
        }
        this.P.putString(str, str2);
    }

    public void setRoadLocString(float f2, float f3) {
        String str;
        String str2 = "";
        if (((double) f2) > 0.001d) {
            str = String.format("%.2f", new Object[]{Float.valueOf(f2)});
        } else {
            str = str2;
        }
        if (((double) f3) > 0.001d) {
            str2 = String.format("%.2f", new Object[]{Float.valueOf(f3)});
        }
        if (this.T != null) {
            this.O = String.format(Locale.US, "%s|%s,%s", new Object[]{this.T, str, str2});
        }
    }

    public void setSatelliteNumber(int i2) {
        this.l = i2;
    }

    public void setSpeed(float f2) {
        this.h = f2;
        this.g = true;
    }

    public void setTime(String str) {
        this.b = str;
        setLocationID(k.a(str));
    }

    public void setUserIndoorState(int i2) {
        this.E = i2;
    }

    public void setVdrJsonValue(String str) {
        if (this.P == null) {
            this.P = new Bundle();
        }
        this.P.putString("vdr", str);
    }

    public void writeToParcel(Parcel parcel, int i2) {
        parcel.writeInt(this.a);
        parcel.writeString(this.b);
        parcel.writeDouble(this.c);
        parcel.writeDouble(this.d);
        parcel.writeDouble(this.f);
        parcel.writeFloat(this.h);
        parcel.writeFloat(this.j);
        parcel.writeInt(this.l);
        parcel.writeFloat(this.m);
        parcel.writeString(this.v);
        parcel.writeInt(this.z);
        parcel.writeString(this.w);
        parcel.writeString(this.x);
        parcel.writeString(this.B);
        parcel.writeString(this.u.province);
        parcel.writeString(this.u.city);
        parcel.writeString(this.u.district);
        parcel.writeString(this.u.street);
        parcel.writeString(this.u.streetNumber);
        parcel.writeString(this.u.cityCode);
        parcel.writeString(this.u.address);
        parcel.writeString(this.u.country);
        parcel.writeString(this.u.countryCode);
        parcel.writeString(this.u.adcode);
        parcel.writeString(this.u.town);
        parcel.writeInt(this.C);
        parcel.writeString(this.D);
        parcel.writeString(this.q);
        parcel.writeString(this.r);
        parcel.writeString(this.s);
        parcel.writeInt(this.A);
        parcel.writeString(this.M);
        parcel.writeInt(this.E);
        parcel.writeInt(this.F);
        parcel.writeInt(this.G);
        parcel.writeInt(this.H);
        parcel.writeString(this.I);
        parcel.writeString(this.J);
        parcel.writeString(this.K);
        parcel.writeInt(this.Q);
        parcel.writeString(this.N);
        parcel.writeInt(this.R);
        parcel.writeString(this.O);
        parcel.writeString(this.T);
        parcel.writeLong(this.S);
        parcel.writeDouble(this.U);
        parcel.writeDouble(this.V);
        parcel.writeFloat(this.Y);
        parcel.writeBooleanArray(new boolean[]{this.e, this.g, this.i, this.k, this.o, this.t, this.y, this.W});
        parcel.writeList(this.L);
        parcel.writeBundle(this.P);
        parcel.writeParcelable(this.X, i2);
    }
}

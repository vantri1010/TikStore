package com.google.android.exoplayer2.util;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.security.NetworkSecurityPolicy;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.blankj.utilcode.constant.TimeConstants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import kotlin.UByte;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;

public final class Util {
    private static final int[] CRC32_BYTES_MSBF = {0, 79764919, 159529838, 222504665, 319059676, 398814059, 445009330, 507990021, 638119352, 583659535, 797628118, 726387553, 890018660, 835552979, 1015980042, 944750013, 1276238704, 1221641927, 1167319070, 1095957929, 1595256236, 1540665371, 1452775106, 1381403509, 1780037320, 1859660671, 1671105958, 1733955601, 2031960084, 2111593891, 1889500026, 1952343757, -1742489888, -1662866601, -1851683442, -1788833735, -1960329156, -1880695413, -2103051438, -2040207643, -1104454824, -1159051537, -1213636554, -1284997759, -1389417084, -1444007885, -1532160278, -1603531939, -734892656, -789352409, -575645954, -646886583, -952755380, -1007220997, -827056094, -898286187, -231047128, -151282273, -71779514, -8804623, -515967244, -436212925, -390279782, -327299027, 881225847, 809987520, 1023691545, 969234094, 662832811, 591600412, 771767749, 717299826, 311336399, 374308984, 453813921, 533576470, 25881363, 88864420, 134795389, 214552010, 2023205639, 2086057648, 1897238633, 1976864222, 1804852699, 1867694188, 1645340341, 1724971778, 1587496639, 1516133128, 1461550545, 1406951526, 1302016099, 1230646740, 1142491917, 1087903418, -1398421865, -1469785312, -1524105735, -1578704818, -1079922613, -1151291908, -1239184603, -1293773166, -1968362705, -1905510760, -2094067647, -2014441994, -1716953613, -1654112188, -1876203875, -1796572374, -525066777, -462094256, -382327159, -302564546, -206542021, -143559028, -97365931, -17609246, -960696225, -1031934488, -817968335, -872425850, -709327229, -780559564, -600130067, -654598054, 1762451694, 1842216281, 1619975040, 1682949687, 2047383090, 2127137669, 1938468188, 2001449195, 1325665622, 1271206113, 1183200824, 1111960463, 1543535498, 1489069629, 1434599652, 1363369299, 622672798, 568075817, 748617968, 677256519, 907627842, 853037301, 1067152940, 995781531, 51762726, 131386257, 177728840, 240578815, 269590778, 349224269, 429104020, 491947555, -248556018, -168932423, -122852000, -60002089, -500490030, -420856475, -341238852, -278395381, -685261898, -739858943, -559578920, -630940305, -1004286614, -1058877219, -845023740, -916395085, -1119974018, -1174433591, -1262701040, -1333941337, -1371866206, -1426332139, -1481064244, -1552294533, -1690935098, -1611170447, -1833673816, -1770699233, -2009983462, -1930228819, -2119160460, -2056179517, 1569362073, 1498123566, 1409854455, 1355396672, 1317987909, 1246755826, 1192025387, 1137557660, 2072149281, 2135122070, 1912620623, 1992383480, 1753615357, 1816598090, 1627664531, 1707420964, 295390185, 358241886, 404320391, 483945776, 43990325, 106832002, 186451547, 266083308, 932423249, 861060070, 1041341759, 986742920, 613929101, 542559546, 756411363, 701822548, -978770311, -1050133554, -869589737, -924188512, -693284699, -764654318, -550540341, -605129092, -475935807, -413084042, -366743377, -287118056, -257573603, -194731862, -114850189, -35218492, -1984365303, -1921392450, -2143631769, -2063868976, -1698919467, -1635936670, -1824608069, -1744851700, -1347415887, -1418654458, -1506661409, -1561119128, -1129027987, -1200260134, -1254728445, -1309196108};
    public static final String DEVICE = Build.DEVICE;
    public static final String DEVICE_DEBUG_INFO = (DEVICE + ", " + MODEL + ", " + MANUFACTURER + ", " + SDK_INT);
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final Pattern ESCAPED_CHARACTER_PATTERN = Pattern.compile("%([A-Fa-f0-9]{2})");
    public static final String MANUFACTURER = Build.MANUFACTURER;
    public static final String MODEL = Build.MODEL;
    public static final int SDK_INT = Build.VERSION.SDK_INT;
    private static final String TAG = "Util";
    private static final Pattern XS_DATE_TIME_PATTERN = Pattern.compile("(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)[Tt](\\d\\d):(\\d\\d):(\\d\\d)([\\.,](\\d+))?([Zz]|((\\+|\\-)(\\d?\\d):?(\\d\\d)))?");
    private static final Pattern XS_DURATION_PATTERN = Pattern.compile("^(-)?P(([0-9]*)Y)?(([0-9]*)M)?(([0-9]*)D)?(T(([0-9]*)H)?(([0-9]*)M)?(([0-9.]*)S)?)?$");

    private Util() {
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while (true) {
            int read = inputStream.read(buffer);
            int bytesRead = read;
            if (read == -1) {
                return outputStream.toByteArray();
            }
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    public static ComponentName startForegroundService(Context context, Intent intent) {
        if (SDK_INT >= 26) {
            return context.startForegroundService(intent);
        }
        return context.startService(intent);
    }

    public static boolean maybeRequestReadExternalStoragePermission(Activity activity, Uri... uris) {
        if (SDK_INT < 23) {
            return false;
        }
        int length = uris.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            } else if (!isLocalFileUri(uris[i])) {
                i++;
            } else if (activity.checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) != 0) {
                activity.requestPermissions(new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, 0);
                return true;
            }
        }
        return false;
    }

    public static boolean checkCleartextTrafficPermitted(Uri... uris) {
        if (SDK_INT < 24) {
            return true;
        }
        for (Uri uri : uris) {
            if ("http".equals(uri.getScheme()) && !NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted((String) Assertions.checkNotNull(uri.getHost()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLocalFileUri(Uri uri) {
        String scheme = uri.getScheme();
        return TextUtils.isEmpty(scheme) || "file".equals(scheme);
    }

    public static boolean areEqual(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    public static boolean contains(Object[] items, Object item) {
        for (Object arrayItem : items) {
            if (areEqual(arrayItem, item)) {
                return true;
            }
        }
        return false;
    }

    public static <T> void removeRange(List<T> list, int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > list.size() || fromIndex > toIndex) {
            throw new IllegalArgumentException();
        } else if (fromIndex != toIndex) {
            list.subList(fromIndex, toIndex).clear();
        }
    }

    @EnsuresNonNull({"#1"})
    public static <T> T castNonNull(T value) {
        return value;
    }

    @EnsuresNonNull({"#1"})
    public static <T> T[] castNonNullTypeArray(T[] value) {
        return value;
    }

    public static <T> T[] nullSafeArrayCopy(T[] input, int length) {
        Assertions.checkArgument(length <= input.length);
        return Arrays.copyOf(input, length);
    }

    public static Handler createHandler(Handler.Callback callback) {
        return createHandler(getLooper(), callback);
    }

    public static Handler createHandler(Looper looper, Handler.Callback callback) {
        return new Handler(looper, callback);
    }

    public static Looper getLooper() {
        Looper myLooper = Looper.myLooper();
        return myLooper != null ? myLooper : Looper.getMainLooper();
    }

    static /* synthetic */ Thread lambda$newSingleThreadExecutor$0(String threadName, Runnable runnable) {
        return new Thread(runnable, threadName);
    }

    public static ExecutorService newSingleThreadExecutor(String threadName) {
        return Executors.newSingleThreadExecutor(new ThreadFactory(threadName) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final Thread newThread(Runnable runnable) {
                return Util.lambda$newSingleThreadExecutor$0(this.f$0, runnable);
            }
        });
    }

    public static void closeQuietly(DataSource dataSource) {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static boolean readBoolean(Parcel parcel) {
        return parcel.readInt() != 0;
    }

    public static void writeBoolean(Parcel parcel, boolean value) {
        parcel.writeInt(value);
    }

    public static String normalizeLanguageCode(String language) {
        if (language == null) {
            return null;
        }
        try {
            return new Locale(language).getISO3Language();
        } catch (MissingResourceException e) {
            return toLowerInvariant(language);
        }
    }

    public static String fromUtf8Bytes(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public static String fromUtf8Bytes(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, Charset.forName("UTF-8"));
    }

    public static byte[] getUtf8Bytes(String value) {
        return value.getBytes(Charset.forName("UTF-8"));
    }

    public static String[] split(String value, String regex) {
        return value.split(regex, -1);
    }

    public static String[] splitAtFirst(String value, String regex) {
        return value.split(regex, 2);
    }

    public static boolean isLinebreak(int c) {
        return c == 10 || c == 13;
    }

    public static String toLowerInvariant(String text) {
        return text == null ? text : text.toLowerCase(Locale.US);
    }

    public static String toUpperInvariant(String text) {
        return text == null ? text : text.toUpperCase(Locale.US);
    }

    public static String formatInvariant(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }

    public static int ceilDivide(int numerator, int denominator) {
        return ((numerator + denominator) - 1) / denominator;
    }

    public static long ceilDivide(long numerator, long denominator) {
        return ((numerator + denominator) - 1) / denominator;
    }

    public static int constrainValue(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static long constrainValue(long value, long min, long max) {
        return Math.max(min, Math.min(value, max));
    }

    public static float constrainValue(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public static long addWithOverflowDefault(long x, long y, long overflowResult) {
        long result = x + y;
        if (((x ^ result) & (y ^ result)) < 0) {
            return overflowResult;
        }
        return result;
    }

    public static long subtractWithOverflowDefault(long x, long y, long overflowResult) {
        long result = x - y;
        if (((x ^ y) & (x ^ result)) < 0) {
            return overflowResult;
        }
        return result;
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0015  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int binarySearchFloor(int[] r2, int r3, boolean r4, boolean r5) {
        /*
            int r0 = java.util.Arrays.binarySearch(r2, r3)
            if (r0 >= 0) goto L_0x000a
            int r1 = r0 + 2
            int r0 = -r1
            goto L_0x0017
        L_0x000a:
            int r0 = r0 + -1
            if (r0 < 0) goto L_0x0013
            r1 = r2[r0]
            if (r1 != r3) goto L_0x0013
            goto L_0x000a
        L_0x0013:
            if (r4 == 0) goto L_0x0017
            int r0 = r0 + 1
        L_0x0017:
            if (r5 == 0) goto L_0x001f
            r1 = 0
            int r1 = java.lang.Math.max(r1, r0)
            goto L_0x0020
        L_0x001f:
            r1 = r0
        L_0x0020:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.Util.binarySearchFloor(int[], int, boolean, boolean):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0017  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int binarySearchFloor(long[] r4, long r5, boolean r7, boolean r8) {
        /*
            int r0 = java.util.Arrays.binarySearch(r4, r5)
            if (r0 >= 0) goto L_0x000a
            int r1 = r0 + 2
            int r0 = -r1
            goto L_0x0019
        L_0x000a:
            int r0 = r0 + -1
            if (r0 < 0) goto L_0x0015
            r1 = r4[r0]
            int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x0015
            goto L_0x000a
        L_0x0015:
            if (r7 == 0) goto L_0x0019
            int r0 = r0 + 1
        L_0x0019:
            if (r8 == 0) goto L_0x0021
            r1 = 0
            int r1 = java.lang.Math.max(r1, r0)
            goto L_0x0022
        L_0x0021:
            r1 = r0
        L_0x0022:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.Util.binarySearchFloor(long[], long, boolean, boolean):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x001d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T extends java.lang.Comparable<? super T>> int binarySearchFloor(java.util.List<? extends java.lang.Comparable<? super T>> r2, T r3, boolean r4, boolean r5) {
        /*
            int r0 = java.util.Collections.binarySearch(r2, r3)
            if (r0 >= 0) goto L_0x000a
            int r1 = r0 + 2
            int r0 = -r1
            goto L_0x001f
        L_0x000a:
            int r0 = r0 + -1
            if (r0 < 0) goto L_0x001b
            java.lang.Object r1 = r2.get(r0)
            java.lang.Comparable r1 = (java.lang.Comparable) r1
            int r1 = r1.compareTo(r3)
            if (r1 != 0) goto L_0x001b
            goto L_0x000a
        L_0x001b:
            if (r4 == 0) goto L_0x001f
            int r0 = r0 + 1
        L_0x001f:
            if (r5 == 0) goto L_0x0027
            r1 = 0
            int r1 = java.lang.Math.max(r1, r0)
            goto L_0x0028
        L_0x0027:
            r1 = r0
        L_0x0028:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.Util.binarySearchFloor(java.util.List, java.lang.Comparable, boolean, boolean):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0014  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int binarySearchCeil(int[] r2, int r3, boolean r4, boolean r5) {
        /*
            int r0 = java.util.Arrays.binarySearch(r2, r3)
            if (r0 >= 0) goto L_0x0008
            int r0 = ~r0
            goto L_0x0016
        L_0x0008:
            int r0 = r0 + 1
            int r1 = r2.length
            if (r0 >= r1) goto L_0x0012
            r1 = r2[r0]
            if (r1 != r3) goto L_0x0012
            goto L_0x0008
        L_0x0012:
            if (r4 == 0) goto L_0x0016
            int r0 = r0 + -1
        L_0x0016:
            if (r5 == 0) goto L_0x0020
            int r1 = r2.length
            int r1 = r1 + -1
            int r1 = java.lang.Math.min(r1, r0)
            goto L_0x0021
        L_0x0020:
            r1 = r0
        L_0x0021:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.Util.binarySearchCeil(int[], int, boolean, boolean):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0016  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int binarySearchCeil(long[] r4, long r5, boolean r7, boolean r8) {
        /*
            int r0 = java.util.Arrays.binarySearch(r4, r5)
            if (r0 >= 0) goto L_0x0008
            int r0 = ~r0
            goto L_0x0018
        L_0x0008:
            int r0 = r0 + 1
            int r1 = r4.length
            if (r0 >= r1) goto L_0x0014
            r1 = r4[r0]
            int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x0014
            goto L_0x0008
        L_0x0014:
            if (r7 == 0) goto L_0x0018
            int r0 = r0 + -1
        L_0x0018:
            if (r8 == 0) goto L_0x0022
            int r1 = r4.length
            int r1 = r1 + -1
            int r1 = java.lang.Math.min(r1, r0)
            goto L_0x0023
        L_0x0022:
            r1 = r0
        L_0x0023:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.Util.binarySearchCeil(long[], long, boolean, boolean):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x001f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T extends java.lang.Comparable<? super T>> int binarySearchCeil(java.util.List<? extends java.lang.Comparable<? super T>> r3, T r4, boolean r5, boolean r6) {
        /*
            int r0 = java.util.Collections.binarySearch(r3, r4)
            if (r0 >= 0) goto L_0x0008
            int r0 = ~r0
            goto L_0x0021
        L_0x0008:
            int r1 = r3.size()
        L_0x000c:
            int r0 = r0 + 1
            if (r0 >= r1) goto L_0x001d
            java.lang.Object r2 = r3.get(r0)
            java.lang.Comparable r2 = (java.lang.Comparable) r2
            int r2 = r2.compareTo(r4)
            if (r2 != 0) goto L_0x001d
            goto L_0x000c
        L_0x001d:
            if (r5 == 0) goto L_0x0021
            int r0 = r0 + -1
        L_0x0021:
            if (r6 == 0) goto L_0x002e
            int r1 = r3.size()
            int r1 = r1 + -1
            int r1 = java.lang.Math.min(r1, r0)
            goto L_0x002f
        L_0x002e:
            r1 = r0
        L_0x002f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.Util.binarySearchCeil(java.util.List, java.lang.Comparable, boolean, boolean):int");
    }

    public static int compareLong(long left, long right) {
        if (left < right) {
            return -1;
        }
        return left == right ? 0 : 1;
    }

    public static long parseXsDuration(String value) {
        Matcher matcher = XS_DURATION_PATTERN.matcher(value);
        if (!matcher.matches()) {
            return (long) (Double.parseDouble(value) * 3600.0d * 1000.0d);
        }
        boolean negated = true ^ TextUtils.isEmpty(matcher.group(1));
        String years = matcher.group(3);
        double d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double durationSeconds = years != null ? Double.parseDouble(years) * 3.1556908E7d : 0.0d;
        String months = matcher.group(5);
        double durationSeconds2 = durationSeconds + (months != null ? Double.parseDouble(months) * 2629739.0d : 0.0d);
        String days = matcher.group(7);
        double durationSeconds3 = durationSeconds2 + (days != null ? Double.parseDouble(days) * 86400.0d : 0.0d);
        String hours = matcher.group(10);
        double durationSeconds4 = durationSeconds3 + (hours != null ? Double.parseDouble(hours) * 3600.0d : 0.0d);
        String minutes = matcher.group(12);
        double durationSeconds5 = durationSeconds4 + (minutes != null ? Double.parseDouble(minutes) * 60.0d : 0.0d);
        String seconds = matcher.group(14);
        if (seconds != null) {
            d = Double.parseDouble(seconds);
        }
        long durationMillis = (long) (1000.0d * (durationSeconds5 + d));
        return negated ? -durationMillis : durationMillis;
    }

    public static long parseXsDateTime(String value) throws ParserException {
        int timezoneShift;
        Matcher matcher = XS_DATE_TIME_PATTERN.matcher(value);
        if (matcher.matches()) {
            if (matcher.group(9) == null) {
                timezoneShift = 0;
            } else if (matcher.group(9).equalsIgnoreCase("Z")) {
                timezoneShift = 0;
            } else {
                timezoneShift = (Integer.parseInt(matcher.group(12)) * 60) + Integer.parseInt(matcher.group(13));
                if ("-".equals(matcher.group(11))) {
                    timezoneShift *= -1;
                }
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            gregorianCalendar.clear();
            gregorianCalendar.set(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)) - 1, Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)));
            if (!TextUtils.isEmpty(matcher.group(8))) {
                gregorianCalendar.set(14, new BigDecimal("0." + matcher.group(8)).movePointRight(3).intValue());
            }
            long time = gregorianCalendar.getTimeInMillis();
            if (timezoneShift != 0) {
                return time - ((long) (TimeConstants.MIN * timezoneShift));
            }
            return time;
        }
        throw new ParserException("Invalid date/time format: " + value);
    }

    public static long scaleLargeTimestamp(long timestamp, long multiplier, long divisor) {
        if (divisor >= multiplier && divisor % multiplier == 0) {
            return timestamp / (divisor / multiplier);
        }
        if (divisor < multiplier && multiplier % divisor == 0) {
            return timestamp * (multiplier / divisor);
        }
        return (long) (((double) timestamp) * (((double) multiplier) / ((double) divisor)));
    }

    public static long[] scaleLargeTimestamps(List<Long> timestamps, long multiplier, long divisor) {
        long[] scaledTimestamps = new long[timestamps.size()];
        if (divisor >= multiplier && divisor % multiplier == 0) {
            long divisionFactor = divisor / multiplier;
            for (int i = 0; i < scaledTimestamps.length; i++) {
                scaledTimestamps[i] = timestamps.get(i).longValue() / divisionFactor;
            }
        } else if (divisor >= multiplier || multiplier % divisor != 0) {
            double multiplicationFactor = ((double) multiplier) / ((double) divisor);
            for (int i2 = 0; i2 < scaledTimestamps.length; i2++) {
                scaledTimestamps[i2] = (long) (((double) timestamps.get(i2).longValue()) * multiplicationFactor);
            }
        } else {
            long multiplicationFactor2 = multiplier / divisor;
            for (int i3 = 0; i3 < scaledTimestamps.length; i3++) {
                scaledTimestamps[i3] = timestamps.get(i3).longValue() * multiplicationFactor2;
            }
        }
        return scaledTimestamps;
    }

    public static void scaleLargeTimestampsInPlace(long[] timestamps, long multiplier, long divisor) {
        if (divisor >= multiplier && divisor % multiplier == 0) {
            long divisionFactor = divisor / multiplier;
            for (int i = 0; i < timestamps.length; i++) {
                timestamps[i] = timestamps[i] / divisionFactor;
            }
        } else if (divisor >= multiplier || multiplier % divisor != 0) {
            double multiplicationFactor = ((double) multiplier) / ((double) divisor);
            for (int i2 = 0; i2 < timestamps.length; i2++) {
                timestamps[i2] = (long) (((double) timestamps[i2]) * multiplicationFactor);
            }
        } else {
            long multiplicationFactor2 = multiplier / divisor;
            for (int i3 = 0; i3 < timestamps.length; i3++) {
                timestamps[i3] = timestamps[i3] * multiplicationFactor2;
            }
        }
    }

    public static long getMediaDurationForPlayoutDuration(long playoutDuration, float speed) {
        if (speed == 1.0f) {
            return playoutDuration;
        }
        return Math.round(((double) playoutDuration) * ((double) speed));
    }

    public static long getPlayoutDurationForMediaDuration(long mediaDuration, float speed) {
        if (speed == 1.0f) {
            return mediaDuration;
        }
        return Math.round(((double) mediaDuration) / ((double) speed));
    }

    public static long resolveSeekPositionUs(long positionUs, SeekParameters seekParameters, long firstSyncUs, long secondSyncUs) {
        SeekParameters seekParameters2 = seekParameters;
        if (SeekParameters.EXACT.equals(seekParameters)) {
            return positionUs;
        }
        long j = positionUs;
        long minPositionUs = subtractWithOverflowDefault(j, seekParameters2.toleranceBeforeUs, Long.MIN_VALUE);
        long maxPositionUs = addWithOverflowDefault(j, seekParameters2.toleranceAfterUs, Long.MAX_VALUE);
        boolean secondSyncPositionValid = true;
        boolean firstSyncPositionValid = minPositionUs <= firstSyncUs && firstSyncUs <= maxPositionUs;
        if (minPositionUs > secondSyncUs || secondSyncUs > maxPositionUs) {
            secondSyncPositionValid = false;
        }
        if (!firstSyncPositionValid || !secondSyncPositionValid) {
            if (firstSyncPositionValid) {
                return firstSyncUs;
            }
            if (secondSyncPositionValid) {
                return secondSyncUs;
            }
            return minPositionUs;
        } else if (Math.abs(firstSyncUs - positionUs) <= Math.abs(secondSyncUs - positionUs)) {
            return firstSyncUs;
        } else {
            return secondSyncUs;
        }
    }

    public static int[] toArray(List<Integer> list) {
        if (list == null) {
            return null;
        }
        int length = list.size();
        int[] intArray = new int[length];
        for (int i = 0; i < length; i++) {
            intArray[i] = list.get(i).intValue();
        }
        return intArray;
    }

    public static int getIntegerCodeForString(String string) {
        int length = string.length();
        Assertions.checkArgument(length <= 4);
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = (result << 8) | string.charAt(i);
        }
        return result;
    }

    public static byte[] getBytesFromHexString(String hexString) {
        byte[] data = new byte[(hexString.length() / 2)];
        for (int i = 0; i < data.length; i++) {
            int stringOffset = i * 2;
            data[i] = (byte) ((Character.digit(hexString.charAt(stringOffset), 16) << 4) + Character.digit(hexString.charAt(stringOffset + 1), 16));
        }
        return data;
    }

    public static String getCommaDelimitedSimpleClassNames(Object[] objects) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            stringBuilder.append(objects[i].getClass().getSimpleName());
            if (i < objects.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public static String getUserAgent(Context context, String applicationName) {
        String packageName;
        try {
            packageName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            packageName = "?";
        }
        return applicationName + "/" + packageName + " (Linux;Android " + Build.VERSION.RELEASE + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY;
    }

    public static String getCodecsOfType(String codecs, int trackType) {
        String[] codecArray = splitCodecs(codecs);
        if (codecArray.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String codec : codecArray) {
            if (trackType == MimeTypes.getTrackTypeOfCodec(codec)) {
                if (builder.length() > 0) {
                    builder.append(",");
                }
                builder.append(codec);
            }
        }
        if (builder.length() > 0) {
            return builder.toString();
        }
        return null;
    }

    public static String[] splitCodecs(String codecs) {
        if (TextUtils.isEmpty(codecs)) {
            return new String[0];
        }
        return split(codecs.trim(), "(\\s*,\\s*)");
    }

    public static int getPcmEncoding(int bitDepth) {
        if (bitDepth == 8) {
            return 3;
        }
        if (bitDepth == 16) {
            return 2;
        }
        if (bitDepth == 24) {
            return Integer.MIN_VALUE;
        }
        if (bitDepth != 32) {
            return 0;
        }
        return 1073741824;
    }

    public static boolean isEncodingLinearPcm(int encoding) {
        return encoding == 3 || encoding == 2 || encoding == Integer.MIN_VALUE || encoding == 1073741824 || encoding == 4;
    }

    public static boolean isEncodingHighResolutionIntegerPcm(int encoding) {
        return encoding == Integer.MIN_VALUE || encoding == 1073741824;
    }

    public static int getAudioTrackChannelConfig(int channelCount) {
        switch (channelCount) {
            case 1:
                return 4;
            case 2:
                return 12;
            case 3:
                return 28;
            case 4:
                return 204;
            case 5:
                return 220;
            case 6:
                return 252;
            case 7:
                return 1276;
            case 8:
                int i = SDK_INT;
                if (i < 23 && i < 21) {
                    return 0;
                }
                return 6396;
            default:
                return 0;
        }
    }

    public static int getPcmFrameSize(int pcmEncoding, int channelCount) {
        if (pcmEncoding == Integer.MIN_VALUE) {
            return channelCount * 3;
        }
        if (pcmEncoding != 1073741824) {
            if (pcmEncoding == 2) {
                return channelCount * 2;
            }
            if (pcmEncoding == 3) {
                return channelCount;
            }
            if (pcmEncoding != 4) {
                throw new IllegalArgumentException();
            }
        }
        return channelCount * 4;
    }

    public static int getAudioUsageForStreamType(int streamType) {
        if (streamType == 0) {
            return 2;
        }
        if (streamType == 1) {
            return 13;
        }
        if (streamType == 2) {
            return 6;
        }
        if (streamType == 4) {
            return 4;
        }
        if (streamType == 5) {
            return 5;
        }
        if (streamType != 8) {
            return 1;
        }
        return 3;
    }

    public static int getAudioContentTypeForStreamType(int streamType) {
        if (streamType != 0) {
            return (streamType == 1 || streamType == 2 || streamType == 4 || streamType == 5 || streamType == 8) ? 4 : 2;
        }
        return 1;
    }

    public static int getStreamTypeForAudioUsage(int usage) {
        if (usage == 13) {
            return 1;
        }
        switch (usage) {
            case 2:
                return 0;
            case 3:
                return 8;
            case 4:
                return 4;
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
                return 5;
            case 6:
                return 2;
            default:
                return 3;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x004e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.UUID getDrmUuid(java.lang.String r5) {
        /*
            java.lang.String r0 = toLowerInvariant(r5)
            int r1 = r0.hashCode()
            r2 = -1860423953(0xffffffff911c2eef, float:-1.2320693E-28)
            r3 = 2
            r4 = 1
            if (r1 == r2) goto L_0x002f
            r2 = -1400551171(0xffffffffac8548fd, float:-3.7881907E-12)
            if (r1 == r2) goto L_0x0024
            r2 = 790309106(0x2f1b28f2, float:1.4111715E-10)
            if (r1 == r2) goto L_0x001a
        L_0x0019:
            goto L_0x0039
        L_0x001a:
            java.lang.String r1 = "clearkey"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0019
            r0 = 2
            goto L_0x003a
        L_0x0024:
            java.lang.String r1 = "widevine"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0019
            r0 = 0
            goto L_0x003a
        L_0x002f:
            java.lang.String r1 = "playready"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0019
            r0 = 1
            goto L_0x003a
        L_0x0039:
            r0 = -1
        L_0x003a:
            if (r0 == 0) goto L_0x004e
            if (r0 == r4) goto L_0x004b
            if (r0 == r3) goto L_0x0048
            java.util.UUID r0 = java.util.UUID.fromString(r5)     // Catch:{ RuntimeException -> 0x0045 }
            return r0
        L_0x0045:
            r0 = move-exception
            r1 = 0
            return r1
        L_0x0048:
            java.util.UUID r0 = com.google.android.exoplayer2.C.CLEARKEY_UUID
            return r0
        L_0x004b:
            java.util.UUID r0 = com.google.android.exoplayer2.C.PLAYREADY_UUID
            return r0
        L_0x004e:
            java.util.UUID r0 = com.google.android.exoplayer2.C.WIDEVINE_UUID
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.Util.getDrmUuid(java.lang.String):java.util.UUID");
    }

    public static int inferContentType(Uri uri, String overrideExtension) {
        if (TextUtils.isEmpty(overrideExtension)) {
            return inferContentType(uri);
        }
        return inferContentType("." + overrideExtension);
    }

    public static int inferContentType(Uri uri) {
        String path = uri.getPath();
        if (path == null) {
            return 3;
        }
        return inferContentType(path);
    }

    public static int inferContentType(String fileName) {
        String fileName2 = toLowerInvariant(fileName);
        if (fileName2.endsWith(".mpd")) {
            return 0;
        }
        if (fileName2.endsWith(".m3u8")) {
            return 2;
        }
        if (fileName2.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?")) {
            return 1;
        }
        return 3;
    }

    public static String getStringForTime(StringBuilder builder, Formatter formatter, long timeMs) {
        long timeMs2;
        Formatter formatter2 = formatter;
        if (timeMs == C.TIME_UNSET) {
            timeMs2 = 0;
        } else {
            timeMs2 = timeMs;
        }
        long totalSeconds = (500 + timeMs2) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        builder.setLength(0);
        if (hours > 0) {
            return formatter2.format("%d:%02d:%02d", new Object[]{Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)}).toString();
        }
        return formatter2.format("%02d:%02d", new Object[]{Long.valueOf(minutes), Long.valueOf(seconds)}).toString();
    }

    public static int getDefaultBufferSize(int trackType) {
        switch (trackType) {
            case 0:
                return 16777216;
            case 1:
                return C.DEFAULT_AUDIO_BUFFER_SIZE;
            case 2:
                return C.DEFAULT_VIDEO_BUFFER_SIZE;
            case 3:
                return 131072;
            case 4:
                return 131072;
            case 5:
                return 131072;
            case 6:
                return 0;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static String escapeFileName(String fileName) {
        int length = fileName.length();
        int charactersToEscapeCount = 0;
        for (int i = 0; i < length; i++) {
            if (shouldEscapeCharacter(fileName.charAt(i))) {
                charactersToEscapeCount++;
            }
        }
        if (charactersToEscapeCount == 0) {
            return fileName;
        }
        int i2 = 0;
        StringBuilder builder = new StringBuilder((charactersToEscapeCount * 2) + length);
        while (charactersToEscapeCount > 0) {
            int i3 = i2 + 1;
            char c = fileName.charAt(i2);
            if (shouldEscapeCharacter(c)) {
                builder.append('%');
                builder.append(Integer.toHexString(c));
                charactersToEscapeCount--;
            } else {
                builder.append(c);
            }
            i2 = i3;
        }
        if (i2 < length) {
            builder.append(fileName, i2, length);
        }
        return builder.toString();
    }

    private static boolean shouldEscapeCharacter(char c) {
        if (c == '\"' || c == '%' || c == '*' || c == '/' || c == ':' || c == '<' || c == '\\' || c == '|' || c == '>' || c == '?') {
            return true;
        }
        return false;
    }

    public static String unescapeFileName(String fileName) {
        int length = fileName.length();
        int percentCharacterCount = 0;
        for (int i = 0; i < length; i++) {
            if (fileName.charAt(i) == '%') {
                percentCharacterCount++;
            }
        }
        if (percentCharacterCount == 0) {
            return fileName;
        }
        int expectedLength = length - (percentCharacterCount * 2);
        StringBuilder builder = new StringBuilder(expectedLength);
        Matcher matcher = ESCAPED_CHARACTER_PATTERN.matcher(fileName);
        int startOfNotEscaped = 0;
        while (percentCharacterCount > 0 && matcher.find()) {
            builder.append(fileName, startOfNotEscaped, matcher.start());
            builder.append((char) Integer.parseInt(matcher.group(1), 16));
            startOfNotEscaped = matcher.end();
            percentCharacterCount--;
        }
        if (startOfNotEscaped < length) {
            builder.append(fileName, startOfNotEscaped, length);
        }
        if (builder.length() != expectedLength) {
            return null;
        }
        return builder.toString();
    }

    public static void sneakyThrow(Throwable t) {
        sneakyThrowInternal(t);
    }

    private static <T extends Throwable> void sneakyThrowInternal(Throwable t) throws Throwable {
        throw t;
    }

    public static void recursiveDelete(File fileOrDirectory) {
        File[] directoryFiles = fileOrDirectory.listFiles();
        if (directoryFiles != null) {
            for (File child : directoryFiles) {
                recursiveDelete(child);
            }
        }
        fileOrDirectory.delete();
    }

    public static File createTempDirectory(Context context, String prefix) throws IOException {
        File tempFile = createTempFile(context, prefix);
        tempFile.delete();
        tempFile.mkdir();
        return tempFile;
    }

    public static File createTempFile(Context context, String prefix) throws IOException {
        return File.createTempFile(prefix, (String) null, context.getCacheDir());
    }

    public static int crc(byte[] bytes, int start, int end, int initialValue) {
        for (int i = start; i < end; i++) {
            initialValue = (initialValue << 8) ^ CRC32_BYTES_MSBF[((initialValue >>> 24) ^ (bytes[i] & UByte.MAX_VALUE)) & UByte.MAX_VALUE];
        }
        return initialValue;
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager;
        if (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) == null) {
            return 0;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return 1;
        }
        int type = networkInfo.getType();
        if (type != 0) {
            if (type == 1) {
                return 2;
            }
            if (!(type == 4 || type == 5)) {
                if (type == 6) {
                    return 5;
                }
                if (type != 9) {
                    return 8;
                }
                return 7;
            }
        }
        return getMobileNetworkType(networkInfo);
    }

    public static String getCountryCode(Context context) {
        TelephonyManager telephonyManager;
        if (!(context == null || (telephonyManager = (TelephonyManager) context.getSystemService("phone")) == null)) {
            String countryCode = telephonyManager.getNetworkCountryIso();
            if (!TextUtils.isEmpty(countryCode)) {
                return toUpperInvariant(countryCode);
            }
        }
        return toUpperInvariant(Locale.getDefault().getCountry());
    }

    public static boolean inflate(ParsableByteArray input, ParsableByteArray output, Inflater inflater) {
        if (input.bytesLeft() <= 0) {
            return false;
        }
        byte[] outputData = output.data;
        if (outputData.length < input.bytesLeft()) {
            outputData = new byte[(input.bytesLeft() * 2)];
        }
        if (inflater == null) {
            inflater = new Inflater();
        }
        inflater.setInput(input.data, input.getPosition(), input.bytesLeft());
        int outputSize = 0;
        while (true) {
            try {
                outputSize += inflater.inflate(outputData, outputSize, outputData.length - outputSize);
                if (inflater.finished()) {
                    output.reset(outputData, outputSize);
                    inflater.reset();
                    return true;
                } else if (inflater.needsDictionary()) {
                    break;
                } else if (inflater.needsInput()) {
                    break;
                } else if (outputSize == outputData.length) {
                    outputData = Arrays.copyOf(outputData, outputData.length * 2);
                }
            } catch (DataFormatException e) {
                return false;
            } finally {
                inflater.reset();
            }
        }
        return false;
    }

    public static boolean isTv(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getApplicationContext().getSystemService("uimode");
        return uiModeManager != null && uiModeManager.getCurrentModeType() == 4;
    }

    public static Point getPhysicalDisplaySize(Context context) {
        return getPhysicalDisplaySize(context, ((WindowManager) context.getSystemService("window")).getDefaultDisplay());
    }

    public static Point getPhysicalDisplaySize(Context context, Display display) {
        String displaySize;
        if (SDK_INT <= 28 && display.getDisplayId() == 0 && isTv(context)) {
            if ("Sony".equals(MANUFACTURER) && MODEL.startsWith("BRAVIA") && context.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
                return new Point(3840, 2160);
            }
            if (SDK_INT < 28) {
                displaySize = getSystemProperty("sys.display-size");
            } else {
                displaySize = getSystemProperty("vendor.display-size");
            }
            if (!TextUtils.isEmpty(displaySize)) {
                try {
                    String[] displaySizeParts = split(displaySize.trim(), "x");
                    if (displaySizeParts.length == 2) {
                        int width = Integer.parseInt(displaySizeParts[0]);
                        int height = Integer.parseInt(displaySizeParts[1]);
                        if (width > 0 && height > 0) {
                            return new Point(width, height);
                        }
                    }
                } catch (NumberFormatException e) {
                }
                Log.e(TAG, "Invalid display size: " + displaySize);
            }
        }
        Point displaySize2 = new Point();
        int i = SDK_INT;
        if (i >= 23) {
            getDisplaySizeV23(display, displaySize2);
        } else if (i >= 17) {
            getDisplaySizeV17(display, displaySize2);
        } else if (i >= 16) {
            getDisplaySizeV16(display, displaySize2);
        } else {
            getDisplaySizeV9(display, displaySize2);
        }
        return displaySize2;
    }

    public static RendererCapabilities[] getRendererCapabilities(RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        Renderer[] renderers = renderersFactory.createRenderers(new Handler(), new VideoRendererEventListener() {
            public /* synthetic */ void onDroppedFrames(int i, long j) {
                VideoRendererEventListener.CC.$default$onDroppedFrames(this, i, j);
            }

            public /* synthetic */ void onRenderedFirstFrame(Surface surface) {
                VideoRendererEventListener.CC.$default$onRenderedFirstFrame(this, surface);
            }

            public /* synthetic */ void onVideoDecoderInitialized(String str, long j, long j2) {
                VideoRendererEventListener.CC.$default$onVideoDecoderInitialized(this, str, j, j2);
            }

            public /* synthetic */ void onVideoDisabled(DecoderCounters decoderCounters) {
                VideoRendererEventListener.CC.$default$onVideoDisabled(this, decoderCounters);
            }

            public /* synthetic */ void onVideoEnabled(DecoderCounters decoderCounters) {
                VideoRendererEventListener.CC.$default$onVideoEnabled(this, decoderCounters);
            }

            public /* synthetic */ void onVideoInputFormatChanged(Format format) {
                VideoRendererEventListener.CC.$default$onVideoInputFormatChanged(this, format);
            }

            public /* synthetic */ void onVideoSizeChanged(int i, int i2, int i3, float f) {
                VideoRendererEventListener.CC.$default$onVideoSizeChanged(this, i, i2, i3, f);
            }
        }, new AudioRendererEventListener() {
            public /* synthetic */ void onAudioDecoderInitialized(String str, long j, long j2) {
                AudioRendererEventListener.CC.$default$onAudioDecoderInitialized(this, str, j, j2);
            }

            public /* synthetic */ void onAudioDisabled(DecoderCounters decoderCounters) {
                AudioRendererEventListener.CC.$default$onAudioDisabled(this, decoderCounters);
            }

            public /* synthetic */ void onAudioEnabled(DecoderCounters decoderCounters) {
                AudioRendererEventListener.CC.$default$onAudioEnabled(this, decoderCounters);
            }

            public /* synthetic */ void onAudioInputFormatChanged(Format format) {
                AudioRendererEventListener.CC.$default$onAudioInputFormatChanged(this, format);
            }

            public /* synthetic */ void onAudioSessionId(int i) {
                AudioRendererEventListener.CC.$default$onAudioSessionId(this, i);
            }

            public /* synthetic */ void onAudioSinkUnderrun(int i, long j, long j2) {
                AudioRendererEventListener.CC.$default$onAudioSinkUnderrun(this, i, j, j2);
            }
        }, $$Lambda$Util$Re8Vg9XyGFeMHzExHNw9i73qY8.INSTANCE, $$Lambda$Util$M3KUmpUnPzRNYEBE_Y1k2KDhE.INSTANCE, drmSessionManager);
        RendererCapabilities[] capabilities = new RendererCapabilities[renderers.length];
        for (int i = 0; i < renderers.length; i++) {
            capabilities[i] = renderers[i].getCapabilities();
        }
        return capabilities;
    }

    static /* synthetic */ void lambda$getRendererCapabilities$1(List cues) {
    }

    static /* synthetic */ void lambda$getRendererCapabilities$2(Metadata metadata) {
    }

    private static String getSystemProperty(String name) {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            return (String) systemProperties.getMethod("get", new Class[]{String.class}).invoke(systemProperties, new Object[]{name});
        } catch (Exception e) {
            Log.e(TAG, "Failed to read system property " + name, e);
            return null;
        }
    }

    private static void getDisplaySizeV23(Display display, Point outSize) {
        Display.Mode mode = display.getMode();
        outSize.x = mode.getPhysicalWidth();
        outSize.y = mode.getPhysicalHeight();
    }

    private static void getDisplaySizeV17(Display display, Point outSize) {
        display.getRealSize(outSize);
    }

    private static void getDisplaySizeV16(Display display, Point outSize) {
        display.getSize(outSize);
    }

    private static void getDisplaySizeV9(Display display, Point outSize) {
        outSize.x = display.getWidth();
        outSize.y = display.getHeight();
    }

    private static int getMobileNetworkType(NetworkInfo networkInfo) {
        switch (networkInfo.getSubtype()) {
            case 1:
            case 2:
                return 3;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
            case 17:
                return 4;
            case 13:
                return 5;
            case 18:
                return 2;
            default:
                return 6;
        }
    }
}

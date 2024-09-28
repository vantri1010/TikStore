package im.bclpbkiauv.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.webkit.MimeTypeMap;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager.widget.ViewPager;
import com.android.internal.telephony.ITelephony;
import com.bumptech.glide.load.resource.bitmap.HardwareConfigState;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.king.zxing.util.LogUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ThemePreviewActivity;
import im.bclpbkiauv.ui.WallpapersListActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.TextDetailSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.BackgroundGradientDrawable;
import im.bclpbkiauv.ui.components.ForegroundDetector;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.PickerBottomLayout;
import im.bclpbkiauv.ui.components.TypefaceSpan;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import kotlin.UByte;
import org.slf4j.Marker;

public class AndroidUtilities {
    public static final int FLAG_TAG_ALL = 11;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static final int FLAG_TAG_URL = 8;
    public static Pattern WEB_URL;
    public static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private static int adjustOwnerClassGuid = 0;
    private static RectF bitmapRect;
    private static final Object callLock = new Object();
    private static ContentObserver callLogContentObserver;
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static float density = 1.0f;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Point displaySize = new Point();
    private static int[] documentIcons = {R.drawable.media_doc_blue, R.drawable.media_doc_green, R.drawable.media_doc_red, R.drawable.media_doc_yellow};
    private static int[] documentMediaIcons = {R.drawable.media_doc_blue_b, R.drawable.media_doc_green_b, R.drawable.media_doc_red_b, R.drawable.media_doc_yellow_b};
    public static boolean firstConfigurationWas;
    private static boolean hasCallPermissions;
    public static boolean incorrectDisplaySizeFix;
    public static boolean isInMultiwindow;
    private static Boolean isTablet = null;
    public static int leftBaseline = (isTablet() ? 80 : 72);
    private static Field mAttachInfoField;
    private static Field mStableInsetsField;
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    public static Integer photoSize = null;
    private static int prevOrientation = -10;
    public static int roundMessageSize;
    private static Paint roundPaint;
    private static final Object smsLock = new Object();
    public static int statusBarHeight = 0;
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();
    private static Runnable unregisterRunnable;
    public static boolean usingHardwareInput;
    private static boolean waitingForCall = false;
    private static boolean waitingForSms = false;

    static {
        boolean z = false;
        WEB_URL = null;
        Object obj = "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯";
        try {
            Pattern IP_ADDRESS = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
            Pattern DOMAIN_NAME = Pattern.compile("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|" + IP_ADDRESS + SQLBuilder.PARENTHESES_RIGHT);
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + DOMAIN_NAME + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[" + "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        checkDisplaySize(ApplicationLoader.applicationContext, (Configuration) null);
        if (Build.VERSION.SDK_INT >= 23) {
            z = true;
        }
        hasCallPermissions = z;
    }

    public static int getThumbForNameOrMime(String name, String mime, boolean media) {
        if (name == null || name.length() == 0) {
            return media ? documentMediaIcons[0] : documentIcons[0];
        }
        int color = -1;
        if (name.contains(".doc") || name.contains(".txt") || name.contains(".psd")) {
            color = 0;
        } else if (name.contains(".xls") || name.contains(".csv")) {
            color = 1;
        } else if (name.contains(".pdf") || name.contains(".ppt") || name.contains(".key")) {
            color = 2;
        } else if (name.contains(".zip") || name.contains(".rar") || name.contains(".ai") || name.contains(DefaultHlsExtractorFactory.MP3_FILE_EXTENSION) || name.contains(".mov") || name.contains(".avi")) {
            color = 3;
        }
        if (color == -1) {
            int idx = name.lastIndexOf(46);
            String ext = idx == -1 ? "" : name.substring(idx + 1);
            if (ext.length() != 0) {
                color = ext.charAt(0) % documentIcons.length;
            } else {
                color = name.charAt(0) % documentIcons.length;
            }
        }
        return media ? documentMediaIcons[color] : documentIcons[color];
    }

    public static int[] calcDrawableColor(Drawable drawable) {
        int[] colors;
        Bitmap b;
        Drawable drawable2 = drawable;
        int bitmapColor = -16777216;
        int[] result = new int[4];
        try {
            if (drawable2 instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable2).getBitmap();
                if (!(bitmap == null || (b = Bitmaps.createScaledBitmap(bitmap, 1, 1, true)) == null)) {
                    bitmapColor = b.getPixel(0, 0);
                    if (bitmap != b) {
                        b.recycle();
                    }
                }
            } else if (drawable2 instanceof ColorDrawable) {
                bitmapColor = ((ColorDrawable) drawable2).getColor();
            } else if ((drawable2 instanceof BackgroundGradientDrawable) && (colors = ((BackgroundGradientDrawable) drawable2).getColorsList()) != null && colors.length > 0) {
                bitmapColor = colors[0];
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        double[] hsv = rgbToHsv((bitmapColor >> 16) & 255, (bitmapColor >> 8) & 255, bitmapColor & 255);
        hsv[1] = Math.min(1.0d, hsv[1] + 0.05d + ((1.0d - hsv[1]) * 0.1d));
        int[] rgb = hsvToRgb(hsv[0], hsv[1], Math.max(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, hsv[2] * 0.65d));
        result[0] = Color.argb(102, rgb[0], rgb[1], rgb[2]);
        result[1] = Color.argb(136, rgb[0], rgb[1], rgb[2]);
        int[] rgb2 = hsvToRgb(hsv[0], hsv[1], Math.max(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, hsv[2] * 0.72d));
        result[2] = Color.argb(102, rgb2[0], rgb2[1], rgb2[2]);
        result[3] = Color.argb(136, rgb2[0], rgb2[1], rgb2[2]);
        return result;
    }

    public static double[] rgbToHsv(int r, int g, int b) {
        double h;
        double h2;
        double rf = ((double) r) / 255.0d;
        double gf = ((double) g) / 255.0d;
        double bf = ((double) b) / 255.0d;
        double max = (rf <= gf || rf <= bf) ? gf > bf ? gf : bf : rf;
        double min = (rf >= gf || rf >= bf) ? gf < bf ? gf : bf : rf;
        double d = max - min;
        double s = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        if (max != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            s = d / max;
        }
        if (max == min) {
            h = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            double d2 = min;
        } else {
            if (rf <= gf || rf <= bf) {
                if (gf > bf) {
                    h2 = ((bf - rf) / d) + 2.0d;
                } else {
                    h2 = ((rf - gf) / d) + 4.0d;
                }
            } else {
                double d3 = min;
                h2 = ((gf - bf) / d) + ((double) (gf < bf ? 6 : 0));
            }
            h = h2 / 6.0d;
        }
        return new double[]{h, s, max};
    }

    private static int[] hsvToRgb(double h, double s, double v) {
        double r;
        double g = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double b = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double i = (double) ((int) Math.floor(h * 6.0d));
        double f = (6.0d * h) - i;
        double p = (1.0d - s) * v;
        double q = (1.0d - (f * s)) * v;
        double t = (1.0d - ((1.0d - f) * s)) * v;
        int i2 = ((int) i) % 6;
        if (i2 == 0) {
            r = v;
            g = t;
            b = p;
        } else if (i2 == 1) {
            r = q;
            g = v;
            b = p;
        } else if (i2 == 2) {
            r = p;
            g = v;
            b = t;
        } else if (i2 == 3) {
            r = p;
            g = q;
            b = v;
        } else if (i2 == 4) {
            r = t;
            g = p;
            b = v;
        } else if (i2 != 5) {
            r = 0.0d;
        } else {
            r = v;
            g = p;
            b = q;
        }
        double d = f;
        double d2 = i;
        return new int[]{(int) (r * 255.0d), (int) (g * 255.0d), (int) (b * 255.0d)};
    }

    public static void requestAdjustResize(Activity activity, int classGuid) {
        if (activity != null && !isTablet()) {
            activity.getWindow().setSoftInputMode(16);
            adjustOwnerClassGuid = classGuid;
        }
    }

    public static void setAdjustResizeToNothing(Activity activity, int classGuid) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == classGuid) {
            activity.getWindow().setSoftInputMode(48);
        }
    }

    public static void removeAdjustResize(Activity activity, int classGuid) {
        if (activity != null && !isTablet() && adjustOwnerClassGuid == classGuid) {
            activity.getWindow().setSoftInputMode(32);
        }
    }

    public static boolean isGoogleMapsInstalled(BaseFragment fragment) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            if (fragment.getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder.setMessage(LocaleController.getString("InstallGoogleMaps", R.string.InstallGoogleMaps));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AndroidUtilities.lambda$isGoogleMapsInstalled$0(BaseFragment.this, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create());
            return false;
        }
    }

    static /* synthetic */ void lambda$isGoogleMapsInstalled$0(BaseFragment fragment, DialogInterface dialogInterface, int i) {
        try {
            fragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps")), 500);
        } catch (Exception e1) {
            FileLog.e((Throwable) e1);
        }
    }

    public static int[] toIntArray(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    public static boolean isInternalUri(Uri uri) {
        String pathString = uri.getPath();
        if (pathString == null) {
            return false;
        }
        if (pathString.matches(Pattern.quote(new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs").getAbsolutePath()) + "/\\d+\\.log")) {
            return false;
        }
        int tries = 0;
        do {
            if (pathString != null && pathString.length() > 4096) {
                return true;
            }
            try {
                String newPath = Utilities.readlink(pathString);
                if (newPath == null || newPath.equals(pathString)) {
                    if (pathString != null) {
                        try {
                            String path = new File(pathString).getCanonicalPath();
                            if (path != null) {
                                pathString = path;
                            }
                        } catch (Exception e) {
                            pathString.replace("/./", "/");
                        }
                    }
                    if (pathString.endsWith(".attheme") || pathString == null) {
                        return false;
                    }
                    String lowerCase = pathString.toLowerCase();
                    if (lowerCase.contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName())) {
                        return true;
                    }
                    return false;
                }
                pathString = newPath;
                tries++;
            } catch (Throwable th) {
                return true;
            }
        } while (tries < 10);
        return true;
    }

    public static void lockOrientation(Activity activity) {
        if (activity != null && prevOrientation == -10) {
            try {
                prevOrientation = activity.getRequestedOrientation();
                WindowManager manager = (WindowManager) activity.getSystemService("window");
                if (manager != null && manager.getDefaultDisplay() != null) {
                    int rotation = manager.getDefaultDisplay().getRotation();
                    int orientation = activity.getResources().getConfiguration().orientation;
                    if (rotation == 3) {
                        if (orientation == 1) {
                            activity.setRequestedOrientation(1);
                        } else {
                            activity.setRequestedOrientation(8);
                        }
                    } else if (rotation == 1) {
                        if (orientation == 1) {
                            activity.setRequestedOrientation(9);
                        } else {
                            activity.setRequestedOrientation(0);
                        }
                    } else if (rotation == 0) {
                        if (orientation == 2) {
                            activity.setRequestedOrientation(0);
                        } else {
                            activity.setRequestedOrientation(1);
                        }
                    } else if (orientation == 2) {
                        activity.setRequestedOrientation(8);
                    } else {
                        activity.setRequestedOrientation(9);
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void unlockOrientation(Activity activity) {
        if (activity != null) {
            try {
                if (prevOrientation != -10) {
                    activity.setRequestedOrientation(prevOrientation);
                    prevOrientation = -10;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static class VcardData {
        String name;
        ArrayList<String> phones;
        StringBuilder vcard;

        private VcardData() {
            this.phones = new ArrayList<>();
            this.vcard = new StringBuilder();
        }
    }

    public static class VcardItem {
        public boolean checked = true;
        public String fullData = "";
        public int type;
        public ArrayList<String> vcardData = new ArrayList<>();

        public String[] getRawValue() {
            byte[] bytes;
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return new String[0];
            }
            String valueType = this.fullData.substring(0, idx);
            String value = this.fullData.substring(idx + 1);
            String nameEncoding = null;
            String nameCharset = "UTF-8";
            String[] params = valueType.split(";");
            for (String split : params) {
                String[] args2 = split.split("=");
                if (args2.length == 2) {
                    if (args2[0].equals("CHARSET")) {
                        nameCharset = args2[1];
                    } else if (args2[0].equals("ENCODING")) {
                        nameEncoding = args2[1];
                    }
                }
            }
            String[] args = value.split(";");
            for (int a = 0; a < args.length; a++) {
                if (!(TextUtils.isEmpty(args[a]) || nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") || (bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(args[a]))) == null || bytes.length == 0)) {
                    try {
                        args[a] = new String(bytes, nameCharset);
                    } catch (Exception e) {
                    }
                }
            }
            return args;
        }

        public String getValue(boolean format) {
            byte[] bytes;
            StringBuilder result = new StringBuilder();
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return "";
            }
            if (result.length() > 0) {
                result.append(", ");
            }
            String valueType = this.fullData.substring(0, idx);
            String value = this.fullData.substring(idx + 1);
            String[] params = valueType.split(";");
            String nameEncoding = null;
            String nameCharset = "UTF-8";
            for (String split : params) {
                String[] args2 = split.split("=");
                if (args2.length == 2) {
                    if (args2[0].equals("CHARSET")) {
                        nameCharset = args2[1];
                    } else if (args2[0].equals("ENCODING")) {
                        nameEncoding = args2[1];
                    }
                }
            }
            String[] args = value.split(";");
            boolean added = false;
            for (int a = 0; a < args.length; a++) {
                if (!TextUtils.isEmpty(args[a])) {
                    if (!(nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") || (bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(args[a]))) == null || bytes.length == 0)) {
                        try {
                            args[a] = new String(bytes, nameCharset);
                        } catch (Exception e) {
                        }
                    }
                    if (added && result.length() > 0) {
                        result.append(" ");
                    }
                    result.append(args[a]);
                    if (!added) {
                        added = args[a].length() > 0;
                    }
                }
            }
            if (format) {
                int i = this.type;
                if (i == 0) {
                    return PhoneFormat.getInstance().format(result.toString());
                }
                if (i == 5) {
                    String[] date = result.toString().split(ExifInterface.GPS_DIRECTION_TRUE);
                    if (date.length > 0) {
                        String[] date2 = date[0].split("-");
                        if (date2.length == 3) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(1, Utilities.parseInt(date2[0]).intValue());
                            calendar.set(2, Utilities.parseInt(date2[1]).intValue() - 1);
                            calendar.set(5, Utilities.parseInt(date2[2]).intValue());
                            return LocaleController.getInstance().formatterYearMax.format(calendar.getTime());
                        }
                    }
                }
            }
            return result.toString();
        }

        public String getRawType(boolean first) {
            int idx = this.fullData.indexOf(58);
            if (idx < 0) {
                return "";
            }
            String value = this.fullData.substring(0, idx);
            if (this.type == 20) {
                String[] args = value.substring(2).split(";");
                if (first) {
                    return args[0];
                }
                if (args.length > 1) {
                    return args[args.length - 1];
                }
                return "";
            }
            String[] args2 = value.split(";");
            for (int a = 0; a < args2.length; a++) {
                if (args2[a].indexOf(61) < 0) {
                    value = args2[a];
                }
            }
            return value;
        }

        public String getType() {
            String value;
            int i = this.type;
            if (i == 5) {
                return LocaleController.getString("ContactBirthday", R.string.ContactBirthday);
            }
            if (i != 6) {
                int idx = this.fullData.indexOf(58);
                if (idx < 0) {
                    return "";
                }
                String value2 = this.fullData.substring(0, idx);
                if (this.type == 20) {
                    value = value2.substring(2).split(";")[0];
                } else {
                    String[] args = value2.split(";");
                    for (int a = 0; a < args.length; a++) {
                        if (args[a].indexOf(61) < 0) {
                            value2 = args[a];
                        }
                    }
                    if (value2.startsWith("X-")) {
                        value2 = value2.substring(2);
                    }
                    char c = 65535;
                    switch (value2.hashCode()) {
                        case -2015525726:
                            if (value2.equals("MOBILE")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 2064738:
                            if (value2.equals("CELL")) {
                                c = 3;
                                break;
                            }
                            break;
                        case 2223327:
                            if (value2.equals("HOME")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 2464291:
                            if (value2.equals("PREF")) {
                                c = 0;
                                break;
                            }
                            break;
                        case 2670353:
                            if (value2.equals("WORK")) {
                                c = 5;
                                break;
                            }
                            break;
                        case 75532016:
                            if (value2.equals("OTHER")) {
                                c = 4;
                                break;
                            }
                            break;
                    }
                    if (c == 0) {
                        value = LocaleController.getString("PhoneMain", R.string.PhoneMain);
                    } else if (c == 1) {
                        value = LocaleController.getString("PhoneHome", R.string.PhoneHome);
                    } else if (c == 2 || c == 3) {
                        value = LocaleController.getString("PhoneMobile", R.string.PhoneMobile);
                    } else if (c != 4) {
                        value = c != 5 ? value2 : LocaleController.getString("PhoneWork", R.string.PhoneWork);
                    } else {
                        value = LocaleController.getString("PhoneOther", R.string.PhoneOther);
                    }
                }
                return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
            } else if ("ORG".equalsIgnoreCase(getRawType(true))) {
                return LocaleController.getString("ContactJob", R.string.ContactJob);
            } else {
                return LocaleController.getString("ContactJobTitle", R.string.ContactJobTitle);
            }
        }
    }

    public static byte[] getStringBytes(String src) {
        try {
            return src.getBytes("UTF-8");
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static ArrayList<TLRPC.User> loadVCardFromStream(Uri uri, int currentAccount, boolean asset, ArrayList<VcardItem> items, String name) {
        InputStream stream;
        ArrayList<TLRPC.User> result;
        InputStream stream2;
        String[] args;
        VcardItem currentItem;
        boolean currentIsPhoto;
        String pendingLine;
        byte[] bytes;
        Uri uri2 = uri;
        ArrayList<VcardItem> arrayList = items;
        ArrayList<TLRPC.User> result2 = null;
        if (asset) {
            try {
                stream = ApplicationLoader.applicationContext.getContentResolver().openAssetFileDescriptor(uri2, "r").createInputStream();
                InputStream inputStream = stream;
            } catch (Throwable th) {
                e = th;
                FileLog.e(e);
                return result2;
            }
        } else {
            try {
                stream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri2);
                InputStream inputStream2 = stream;
            } catch (Throwable th2) {
                e = th2;
                ArrayList<TLRPC.User> arrayList2 = result2;
                FileLog.e(e);
                return result2;
            }
        }
        ArrayList arrayList3 = new ArrayList();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        VcardItem currentItem2 = null;
        boolean currentIsPhoto2 = false;
        String pendingLine2 = null;
        VcardData currentData = null;
        while (true) {
            String readLine = bufferedReader.readLine();
            String line = readLine;
            String originalLine = readLine;
            if (readLine == null) {
                break;
            } else if (originalLine.startsWith("PHOTO")) {
                currentIsPhoto2 = true;
            } else {
                if (originalLine.indexOf(58) >= 0) {
                    VcardItem currentItem3 = null;
                    currentIsPhoto2 = false;
                    if (originalLine.startsWith("BEGIN:VCARD")) {
                        VcardData vcardData = new VcardData();
                        VcardData currentData2 = vcardData;
                        arrayList3.add(vcardData);
                        currentData2.name = name;
                        currentItem2 = null;
                        currentData = currentData2;
                    } else {
                        String str = name;
                        if (!originalLine.startsWith("END:VCARD")) {
                            if (arrayList != null) {
                                if (originalLine.startsWith("TEL")) {
                                    currentItem3 = new VcardItem();
                                    currentItem3.type = 0;
                                } else if (originalLine.startsWith("EMAIL")) {
                                    currentItem3 = new VcardItem();
                                    currentItem3.type = 1;
                                } else {
                                    if (!originalLine.startsWith("ADR") && !originalLine.startsWith("LABEL")) {
                                        if (!originalLine.startsWith("GEO")) {
                                            if (originalLine.startsWith("URL")) {
                                                currentItem3 = new VcardItem();
                                                currentItem3.type = 3;
                                            } else if (originalLine.startsWith("NOTE")) {
                                                currentItem3 = new VcardItem();
                                                currentItem3.type = 4;
                                            } else if (originalLine.startsWith("BDAY")) {
                                                currentItem3 = new VcardItem();
                                                currentItem3.type = 5;
                                            } else {
                                                if (!originalLine.startsWith("ORG") && !originalLine.startsWith("TITLE")) {
                                                    if (!originalLine.startsWith("ROLE")) {
                                                        if (originalLine.startsWith("X-ANDROID")) {
                                                            currentItem3 = new VcardItem();
                                                            currentItem3.type = -1;
                                                        } else if (originalLine.startsWith("X-PHONETIC")) {
                                                            currentItem3 = null;
                                                        } else if (originalLine.startsWith("X-")) {
                                                            currentItem3 = new VcardItem();
                                                            currentItem3.type = 20;
                                                        }
                                                    }
                                                }
                                                if (0 == 0) {
                                                    currentItem3 = new VcardItem();
                                                    currentItem3.type = 6;
                                                }
                                            }
                                        }
                                    }
                                    currentItem3 = new VcardItem();
                                    currentItem3.type = 2;
                                }
                                if (currentItem3 != null && currentItem3.type >= 0) {
                                    arrayList.add(currentItem3);
                                }
                            }
                        }
                        currentItem2 = currentItem3;
                    }
                }
                if (!currentIsPhoto2 && currentData != null) {
                    if (currentItem2 == null) {
                        if (currentData.vcard.length() > 0) {
                            currentData.vcard.append(10);
                        }
                        currentData.vcard.append(originalLine);
                    } else {
                        currentItem2.vcardData.add(originalLine);
                    }
                }
                if (pendingLine2 != null) {
                    line = pendingLine2 + line;
                    pendingLine2 = null;
                }
                String str2 = "=";
                if (line.contains("=QUOTED-PRINTABLE")) {
                    if (line.endsWith(str2)) {
                        pendingLine2 = line.substring(0, line.length() - 1);
                        Uri uri3 = uri;
                    }
                }
                if (!(currentIsPhoto2 || currentData == null || currentItem2 == null)) {
                    currentItem2.fullData = line;
                }
                int idx = line.indexOf(LogUtils.COLON);
                if (idx >= 0) {
                    result = result2;
                    try {
                        args = new String[]{line.substring(0, idx), line.substring(idx + 1).trim()};
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th3) {
                        e = th3;
                        result2 = result;
                        FileLog.e(e);
                        return result2;
                    }
                } else {
                    result = result2;
                    args = new String[]{line.trim()};
                }
                int i = idx;
                if (args.length < 2) {
                    pendingLine = pendingLine2;
                    currentIsPhoto = currentIsPhoto2;
                    currentItem = currentItem2;
                } else if (currentData == null) {
                    pendingLine = pendingLine2;
                    currentIsPhoto = currentIsPhoto2;
                    currentItem = currentItem2;
                } else {
                    if (!args[0].startsWith("FN")) {
                        if (!args[0].startsWith("ORG") || !TextUtils.isEmpty(currentData.name)) {
                            if (args[0].startsWith("TEL")) {
                                currentData.phones.add(args[1]);
                                pendingLine = pendingLine2;
                                currentIsPhoto = currentIsPhoto2;
                                currentItem = currentItem2;
                            } else {
                                pendingLine = pendingLine2;
                                currentIsPhoto = currentIsPhoto2;
                                currentItem = currentItem2;
                            }
                        }
                    }
                    String nameEncoding = null;
                    String nameCharset = null;
                    String[] params = args[0].split(";");
                    int length = params.length;
                    pendingLine = pendingLine2;
                    int i2 = 0;
                    while (i2 < length) {
                        boolean currentIsPhoto3 = currentIsPhoto2;
                        String param = params[i2];
                        String str3 = str2;
                        String[] args2 = param.split(str2);
                        String str4 = param;
                        VcardItem currentItem4 = currentItem2;
                        if (args2.length == 2) {
                            if (args2[0].equals("CHARSET")) {
                                nameCharset = args2[1];
                            } else if (args2[0].equals("ENCODING")) {
                                nameEncoding = args2[1];
                            }
                        }
                        i2++;
                        currentIsPhoto2 = currentIsPhoto3;
                        str2 = str3;
                        currentItem2 = currentItem4;
                    }
                    currentIsPhoto = currentIsPhoto2;
                    currentItem = currentItem2;
                    currentData.name = args[1];
                    if (!(nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") || (bytes = decodeQuotedPrintable(getStringBytes(currentData.name))) == null || bytes.length == 0)) {
                        currentData.name = new String(bytes, nameCharset);
                    }
                }
                Uri uri4 = uri;
                arrayList = items;
                result2 = result;
                pendingLine2 = pendingLine;
                currentIsPhoto2 = currentIsPhoto;
                currentItem2 = currentItem;
            }
        }
        result = result2;
        bufferedReader.close();
        stream.close();
        int a = 0;
        result2 = result;
        while (a < arrayList3.size()) {
            VcardData vcardData2 = (VcardData) arrayList3.get(a);
            if (vcardData2.name == null || vcardData2.phones.isEmpty()) {
                stream2 = stream;
            } else {
                if (result2 == null) {
                    result2 = new ArrayList<>();
                }
                String phoneToUse = vcardData2.phones.get(0);
                int b = 0;
                while (true) {
                    if (b >= vcardData2.phones.size()) {
                        stream2 = stream;
                        break;
                    }
                    String phone = vcardData2.phones.get(b);
                    String phoneToUse2 = phoneToUse;
                    stream2 = stream;
                    if (ContactsController.getInstance(currentAccount).contactsByShortPhone.get(phone.substring(Math.max(0, phone.length() - 7))) != null) {
                        phoneToUse = phone;
                        break;
                    }
                    b++;
                    stream = stream2;
                    phoneToUse = phoneToUse2;
                }
                TLRPC.User user = new TLRPC.TL_userContact_old2();
                user.phone = phoneToUse;
                user.first_name = vcardData2.name;
                user.last_name = "";
                user.id = 0;
                TLRPC.TL_restrictionReason reason = new TLRPC.TL_restrictionReason();
                reason.text = vcardData2.vcard.toString();
                reason.platform = "";
                reason.reason = "";
                user.restriction_reason.add(reason);
                result2.add(user);
            }
            a++;
            stream = stream2;
        }
        return result2;
    }

    public static Typeface getTypeface(String assetPath) {
        Typeface typeface;
        Typeface t;
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    if (Build.VERSION.SDK_INT >= 26) {
                        Typeface.Builder builder = new Typeface.Builder(ApplicationLoader.applicationContext.getAssets(), assetPath);
                        if (assetPath.contains("medium")) {
                            builder.setWeight(HardwareConfigState.DEFAULT_MAXIMUM_FDS_FOR_HARDWARE_CONFIGS);
                        }
                        if (assetPath.contains(TtmlNode.ITALIC)) {
                            builder.setItalic(true);
                        }
                        t = builder.build();
                    } else {
                        t = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), assetPath);
                    }
                    typefaceCache.put(assetPath, t);
                } catch (Exception e) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    }
                    return null;
                }
            }
            typeface = typefaceCache.get(assetPath);
        }
        return typeface;
    }

    public static boolean isWaitingForSms() {
        boolean value;
        synchronized (smsLock) {
            value = waitingForSms;
        }
        return value;
    }

    public static void setWaitingForSms(boolean value) {
        synchronized (smsLock) {
            waitingForSms = value;
            if (value) {
                try {
                    SmsRetriever.getClient(ApplicationLoader.applicationContext).startSmsRetriever().addOnSuccessListener($$Lambda$AndroidUtilities$0Rl_fXHqzmvnhmVPJcl_YLxLMY.INSTANCE);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
    }

    static /* synthetic */ void lambda$setWaitingForSms$1(Void aVoid) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("sms listener registered");
        }
    }

    public static int getShadowHeight() {
        float f = density;
        if (f >= 4.0f) {
            return 3;
        }
        if (f >= 2.0f) {
            return 2;
        }
        return 1;
    }

    public static boolean isWaitingForCall() {
        boolean value;
        synchronized (callLock) {
            value = waitingForCall;
        }
        return value;
    }

    public static void setWaitingForCall(boolean value) {
        synchronized (callLock) {
            waitingForCall = value;
        }
    }

    public static boolean showKeyboard(View view) {
        if (view == null) {
            return false;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 1);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        try {
            return ((InputMethodManager) view.getContext().getSystemService("input_method")).isActive(view);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static String[] getCurrentKeyboardLanguage() {
        try {
            InputMethodManager inputManager = (InputMethodManager) ApplicationLoader.applicationContext.getSystemService("input_method");
            InputMethodSubtype inputMethodSubtype = inputManager.getCurrentInputMethodSubtype();
            String locale = null;
            if (inputMethodSubtype != null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    locale = inputMethodSubtype.getLanguageTag();
                }
                if (TextUtils.isEmpty(locale)) {
                    locale = inputMethodSubtype.getLocale();
                }
            } else {
                InputMethodSubtype inputMethodSubtype2 = inputManager.getLastInputMethodSubtype();
                if (inputMethodSubtype2 != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        locale = inputMethodSubtype2.getLanguageTag();
                    }
                    if (TextUtils.isEmpty(locale)) {
                        locale = inputMethodSubtype2.getLocale();
                    }
                }
            }
            if (TextUtils.isEmpty(locale)) {
                String locale2 = LocaleController.getSystemLocaleStringIso639();
                LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                String locale22 = localeInfo.getBaseLangCode();
                if (TextUtils.isEmpty(locale22)) {
                    locale22 = localeInfo.getLangCode();
                }
                if (locale2.contains(locale22) || locale22.contains(locale2)) {
                    if (!locale2.contains("en")) {
                        locale22 = "en";
                    } else {
                        locale22 = null;
                    }
                }
                if (!TextUtils.isEmpty(locale22)) {
                    return new String[]{locale2.replace('_', '-'), locale22};
                }
                return new String[]{locale2.replace('_', '-')};
            }
            return new String[]{locale.replace('_', '-')};
        } catch (Exception e) {
            return new String[]{"en"};
        }
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            try {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService("input_method");
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static File getCacheDir() {
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (state == null || state.startsWith("mounted")) {
            try {
                File file = ApplicationLoader.applicationContext.getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
        try {
            File file2 = ApplicationLoader.applicationContext.getCacheDir();
            if (file2 != null) {
                return file2;
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        return new File("");
    }

    public static int dp(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.ceil((double) (density * value));
    }

    public static int dpr(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return Math.round(density * value);
    }

    public static int dp2(float value) {
        if (value == 0.0f) {
            return 0;
        }
        return (int) Math.floor((double) (density * value));
    }

    public static int compare(int lhs, int rhs) {
        if (lhs == rhs) {
            return 0;
        }
        if (lhs > rhs) {
            return 1;
        }
        return -1;
    }

    public static float dpf2(float value) {
        if (value == 0.0f) {
            return 0.0f;
        }
        return density * value;
    }

    public static float sp2px(float spValue) {
        if (spValue == 0.0f || ApplicationLoader.applicationContext == null) {
            return 0.0f;
        }
        return (ApplicationLoader.applicationContext.getResources().getDisplayMetrics().scaledDensity * spValue) + 0.5f;
    }

    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        Display display;
        try {
            int oldDensity = (int) density;
            float f = context.getResources().getDisplayMetrics().density;
            density = f;
            int newDensity = (int) f;
            if (firstConfigurationWas && oldDensity != newDensity) {
                Theme.reloadAllResources(context);
            }
            boolean z = true;
            firstConfigurationWas = true;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            if (configuration.keyboard == 1 || configuration.hardKeyboardHidden != 1) {
                z = false;
            }
            usingHardwareInput = z;
            WindowManager manager = (WindowManager) context.getSystemService("window");
            if (!(manager == null || (display = manager.getDefaultDisplay()) == null)) {
                display.getMetrics(displayMetrics);
                display.getSize(displaySize);
            }
            if (configuration.screenWidthDp != 0) {
                int newSize = (int) Math.ceil((double) (((float) configuration.screenWidthDp) * density));
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != 0) {
                int newSize2 = (int) Math.ceil((double) (((float) configuration.screenHeightDp) * density));
                if (Math.abs(displaySize.y - newSize2) > 3) {
                    displaySize.y = newSize2;
                }
            }
            if (roundMessageSize == 0) {
                if (isTablet()) {
                    roundMessageSize = (int) (((float) getMinTabletSide()) * 0.6f);
                } else {
                    roundMessageSize = (int) (((float) Math.min(displaySize.x, displaySize.y)) * 0.6f);
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static double fixLocationCoord(double value) {
        return ((double) ((long) (value * 1000000.0d))) / 1000000.0d;
    }

    public static String formapMapUrl(int account, double lat, double lon, int width, int height, boolean marker, int zoom) {
        int scale = Math.min(2, (int) Math.ceil((double) density));
        int provider = MessagesController.getInstance(account).mapProvider;
        if (provider == 1 || provider == 3) {
            String lang = null;
            String[] availableLangs = {"ru_RU", "tr_TR"};
            LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getCurrentLocaleInfo();
            for (int a = 0; a < availableLangs.length; a++) {
                if (availableLangs[a].toLowerCase().contains(localeInfo.shortName)) {
                    lang = availableLangs[a];
                }
            }
            if (lang == null) {
                lang = "en_US";
            }
            if (marker) {
                return String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&pt=%.6f,%.6f,vkbkm&lang=%s", new Object[]{Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(zoom), Integer.valueOf(width * scale), Integer.valueOf(height * scale), Integer.valueOf(scale), Double.valueOf(lon), Double.valueOf(lat), lang});
            }
            return String.format(Locale.US, "https://static-maps.yandex.ru/1.x/?ll=%.6f,%.6f&z=%d&size=%d,%d&l=map&scale=%d&lang=%s", new Object[]{Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(zoom), Integer.valueOf(width * scale), Integer.valueOf(height * scale), Integer.valueOf(scale), lang});
        }
        String k = ApplicationLoader.applicationContext.getResources().getString(R.string.baidu_map_key);
        String mCode = ApplicationLoader.applicationContext.getResources().getString(R.string.baidu_map_code);
        if (marker) {
            return String.format(Locale.US, "http://api.map.baidu.com/staticimage/v2?ak=%s&mcode=%s&center=%.6f,%.6f&width=%d&height=%d&zoom=%d&scale=%d&coordtype=gcj02ll&copyright=1&dpiType=ph&markers=%.6f,%.6f&markerStyles=l,,red", new Object[]{k, mCode, Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(zoom), Integer.valueOf(scale), Double.valueOf(lon), Double.valueOf(lat)});
        }
        return String.format(Locale.US, "http://api.map.baidu.com/staticimage/v2?ak=%s&mcode=%s&center=%.6f,%.6f&width=%d&height=%d&zoom=%d&scale=%d&coordtype=gcj02ll&copyright=1&dpiType=ph", new Object[]{k, mCode, Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(zoom), Integer.valueOf(scale)});
    }

    public static float getPixelsInCM(float cm, boolean isX) {
        float f = cm / 2.54f;
        DisplayMetrics displayMetrics2 = displayMetrics;
        return f * (isX ? displayMetrics2.xdpi : displayMetrics2.ydpi);
    }

    public static int getMyLayerVersion(int layer) {
        return 65535 & layer;
    }

    public static int getPeerLayerVersion(int layer) {
        return (layer >> 16) & 65535;
    }

    public static int setMyLayerVersion(int layer, int version) {
        return (-65536 & layer) | version;
    }

    public static int setPeerLayerVersion(int layer, int version) {
        return (65535 & layer) | (version << 16);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.isTablet));
        }
        return isTablet.booleanValue();
    }

    public static boolean isSmallTablet() {
        return ((float) Math.min(displaySize.x, displaySize.y)) / density <= 700.0f;
    }

    public static int getMinTabletSide() {
        if (!isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int leftSide = (smallSide * 35) / 100;
            if (leftSide < dp(320.0f)) {
                leftSide = dp(320.0f);
            }
            return smallSide - leftSide;
        }
        int smallSide2 = Math.min(displaySize.x, displaySize.y);
        int maxSide = Math.max(displaySize.x, displaySize.y);
        int leftSide2 = (maxSide * 35) / 100;
        if (leftSide2 < dp(320.0f)) {
            leftSide2 = dp(320.0f);
        }
        return Math.min(smallSide2, maxSide - leftSide2);
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            photoSize = 1280;
        }
        return photoSize.intValue();
    }

    public static void endIncomingCall() {
        if (hasCallPermissions) {
            try {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                Method m = Class.forName(tm.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
                m.setAccessible(true);
                ITelephony iTelephony = (ITelephony) m.invoke(tm, new Object[0]);
                ITelephony telephonyService = (ITelephony) m.invoke(tm, new Object[0]);
                telephonyService.silenceRinger();
                telephonyService.endCall();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006f, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0070, code lost:
        if (r0 != null) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x007a, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String obtainLoginPhoneCall(java.lang.String r10) {
        /*
            boolean r0 = hasCallPermissions
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x007b }
            android.content.ContentResolver r2 = r0.getContentResolver()     // Catch:{ Exception -> 0x007b }
            android.net.Uri r3 = android.provider.CallLog.Calls.CONTENT_URI     // Catch:{ Exception -> 0x007b }
            java.lang.String r0 = "number"
            java.lang.String r4 = "date"
            java.lang.String[] r4 = new java.lang.String[]{r0, r4}     // Catch:{ Exception -> 0x007b }
            java.lang.String r5 = "type IN (3,1,5)"
            r6 = 0
            java.lang.String r7 = "date DESC LIMIT 5"
            android.database.Cursor r0 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x007b }
        L_0x0020:
            boolean r2 = r0.moveToNext()     // Catch:{ all -> 0x006d }
            if (r2 == 0) goto L_0x0067
            r2 = 0
            java.lang.String r2 = r0.getString(r2)     // Catch:{ all -> 0x006d }
            r3 = 1
            long r3 = r0.getLong(r3)     // Catch:{ all -> 0x006d }
            boolean r5 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x006d }
            if (r5 == 0) goto L_0x0048
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x006d }
            r5.<init>()     // Catch:{ all -> 0x006d }
            java.lang.String r6 = "number = "
            r5.append(r6)     // Catch:{ all -> 0x006d }
            r5.append(r2)     // Catch:{ all -> 0x006d }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x006d }
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x006d }
        L_0x0048:
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x006d }
            long r5 = r5 - r3
            long r5 = java.lang.Math.abs(r5)     // Catch:{ all -> 0x006d }
            r7 = 3600000(0x36ee80, double:1.7786363E-317)
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 < 0) goto L_0x0059
            goto L_0x0020
        L_0x0059:
            boolean r5 = checkPhonePattern(r10, r2)     // Catch:{ all -> 0x006d }
            if (r5 == 0) goto L_0x0066
            if (r0 == 0) goto L_0x0065
            r0.close()     // Catch:{ Exception -> 0x007b }
        L_0x0065:
            return r2
        L_0x0066:
            goto L_0x0020
        L_0x0067:
            if (r0 == 0) goto L_0x006c
            r0.close()     // Catch:{ Exception -> 0x007b }
        L_0x006c:
            goto L_0x007f
        L_0x006d:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x006f }
        L_0x006f:
            r3 = move-exception
            if (r0 == 0) goto L_0x007a
            r0.close()     // Catch:{ all -> 0x0076 }
            goto L_0x007a
        L_0x0076:
            r4 = move-exception
            r2.addSuppressed(r4)     // Catch:{ Exception -> 0x007b }
        L_0x007a:
            throw r3     // Catch:{ Exception -> 0x007b }
        L_0x007b:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x007f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.AndroidUtilities.obtainLoginPhoneCall(java.lang.String):java.lang.String");
    }

    public static boolean checkPhonePattern(String pattern, String phone) {
        if (TextUtils.isEmpty(pattern) || pattern.equals("*")) {
            return true;
        }
        String[] args = pattern.split("\\*");
        String phone2 = PhoneFormat.stripExceptNumbers(phone);
        int checkStart = 0;
        for (String arg : args) {
            if (!TextUtils.isEmpty(arg)) {
                int indexOf = phone2.indexOf(arg, checkStart);
                int index = indexOf;
                if (indexOf == -1) {
                    return false;
                }
                checkStart = arg.length() + index;
            }
        }
        return true;
    }

    public static int getViewInset(View view) {
        if (view == null || Build.VERSION.SDK_INT < 21 || view.getHeight() == displaySize.y || view.getHeight() == displaySize.y - statusBarHeight) {
            return 0;
        }
        try {
            if (mAttachInfoField == null) {
                Field declaredField = View.class.getDeclaredField("mAttachInfo");
                mAttachInfoField = declaredField;
                declaredField.setAccessible(true);
            }
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo != null) {
                if (mStableInsetsField == null) {
                    Field declaredField2 = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                    mStableInsetsField = declaredField2;
                    declaredField2.setAccessible(true);
                }
                return ((Rect) mStableInsetsField.get(mAttachInfo)).bottom;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return 0;
    }

    public static Point getRealScreenSize() {
        Point size = new Point();
        try {
            WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            if (Build.VERSION.SDK_INT >= 17) {
                windowManager.getDefaultDisplay().getRealSize(size);
            } else {
                try {
                    size.set(((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(windowManager.getDefaultDisplay(), new Object[0])).intValue());
                } catch (Exception e) {
                    size.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    FileLog.e((Throwable) e);
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        return size;
    }

    public static void setEnabled(View view, boolean enabled) {
        if (view != null) {
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setEnabled(viewGroup.getChildAt(i), enabled);
                }
            }
        }
    }

    public static CharSequence getTrimmedString(CharSequence src) {
        if (src == null || src.length() == 0) {
            return src;
        }
        while (src.length() > 0 && (src.charAt(0) == 10 || src.charAt(0) == ' ')) {
            src = src.subSequence(1, src.length());
        }
        while (src.length() > 0 && (src.charAt(src.length() - 1) == 10 || src.charAt(src.length() - 1) == ' ')) {
            src = src.subSequence(0, src.length() - 1);
        }
        return src;
    }

    public static void setViewPagerEdgeEffectColor(ViewPager viewPager, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ViewPager.class.getDeclaredField("mLeftEdge");
                field.setAccessible(true);
                EdgeEffect mLeftEdge = (EdgeEffect) field.get(viewPager);
                if (mLeftEdge != null) {
                    mLeftEdge.setColor(color);
                }
                Field field2 = ViewPager.class.getDeclaredField("mRightEdge");
                field2.setAccessible(true);
                EdgeEffect mRightEdge = (EdgeEffect) field2.get(viewPager);
                if (mRightEdge != null) {
                    mRightEdge.setColor(color);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(HorizontalScrollView scrollView, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = HorizontalScrollView.class.getDeclaredField("mEdgeGlowLeft");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                Field field2 = HorizontalScrollView.class.getDeclaredField("mEdgeGlowRight");
                field2.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field2.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void setScrollViewEdgeEffectColor(ScrollView scrollView, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                Field field = ScrollView.class.getDeclaredField("mEdgeGlowTop");
                field.setAccessible(true);
                EdgeEffect mEdgeGlowTop = (EdgeEffect) field.get(scrollView);
                if (mEdgeGlowTop != null) {
                    mEdgeGlowTop.setColor(color);
                }
                Field field2 = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
                field2.setAccessible(true);
                EdgeEffect mEdgeGlowBottom = (EdgeEffect) field2.get(scrollView);
                if (mEdgeGlowBottom != null) {
                    mEdgeGlowBottom.setColor(color);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT >= 21 && view != null) {
            if (view instanceof ListView) {
                Drawable drawable = ((ListView) view).getSelector();
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                    return;
                }
                return;
            }
            Drawable drawable2 = view.getBackground();
            if (drawable2 != null) {
                drawable2.setState(StateSet.NOTHING);
                drawable2.jumpToCurrentState();
            }
        }
    }

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, 11, new Object[0]);
    }

    public static SpannableStringBuilder replaceTags(String str, int flag, Object... args) {
        try {
            StringBuilder stringBuilder = new StringBuilder(str);
            if ((flag & 1) != 0) {
                while (true) {
                    int indexOf = stringBuilder.indexOf("<br>");
                    int start = indexOf;
                    if (indexOf == -1) {
                        break;
                    }
                    stringBuilder.replace(start, start + 4, "\n");
                }
                while (true) {
                    int indexOf2 = stringBuilder.indexOf("<br/>");
                    int start2 = indexOf2;
                    if (indexOf2 == -1) {
                        break;
                    }
                    stringBuilder.replace(start2, start2 + 5, "\n");
                }
            }
            ArrayList<Integer> bolds = new ArrayList<>();
            if ((flag & 2) != 0) {
                while (true) {
                    int indexOf3 = stringBuilder.indexOf("<b>");
                    int start3 = indexOf3;
                    if (indexOf3 == -1) {
                        break;
                    }
                    stringBuilder.replace(start3, start3 + 3, "");
                    int end = stringBuilder.indexOf("</b>");
                    if (end == -1) {
                        end = stringBuilder.indexOf("<b>");
                    }
                    stringBuilder.replace(end, end + 4, "");
                    bolds.add(Integer.valueOf(start3));
                    bolds.add(Integer.valueOf(end));
                }
                while (true) {
                    int indexOf4 = stringBuilder.indexOf("**");
                    int start4 = indexOf4;
                    if (indexOf4 == -1) {
                        break;
                    }
                    stringBuilder.replace(start4, start4 + 2, "");
                    int end2 = stringBuilder.indexOf("**");
                    if (end2 >= 0) {
                        stringBuilder.replace(end2, end2 + 2, "");
                        bolds.add(Integer.valueOf(start4));
                        bolds.add(Integer.valueOf(end2));
                    }
                }
            }
            if ((flag & 8) != 0) {
                while (true) {
                    int indexOf5 = stringBuilder.indexOf("**");
                    int start5 = indexOf5;
                    if (indexOf5 == -1) {
                        break;
                    }
                    stringBuilder.replace(start5, start5 + 2, "");
                    int end3 = stringBuilder.indexOf("**");
                    if (end3 >= 0) {
                        stringBuilder.replace(end3, end3 + 2, "");
                        bolds.add(Integer.valueOf(start5));
                        bolds.add(Integer.valueOf(end3));
                    }
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), bolds.get(a * 2).intValue(), bolds.get((a * 2) + 1).intValue(), 33);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new SpannableStringBuilder(str);
        }
    }

    public static class LinkMovementMethodMy extends LinkMovementMethod {
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public static boolean needShowPasscode() {
        return needShowPasscode(false);
    }

    public static boolean needShowPasscode(boolean reset) {
        boolean wasInBackground = ForegroundDetector.getInstance().isWasInBackground(reset);
        if (reset) {
            ForegroundDetector.getInstance().resetBackgroundVar();
        }
        return SharedConfig.passcodeHash.length() > 0 && wasInBackground && (SharedConfig.appLocked || ((SharedConfig.autoLockIn != 0 && SharedConfig.lastPauseTime != 0 && !SharedConfig.appLocked && SharedConfig.lastPauseTime + SharedConfig.autoLockIn <= ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) || ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() + 5 < SharedConfig.lastPauseTime));
    }

    public static void shakeView(final View view, final float x, final int num) {
        if (view != null) {
            if (num == 6) {
                view.setTranslationX(0.0f);
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "translationX", new float[]{(float) dp(x)})});
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AndroidUtilities.shakeView(view, num == 5 ? 0.0f : -x, num + 1);
                }
            });
            animatorSet.start();
        }
    }

    public static void checkForCrashes(Activity context) {
    }

    public static void checkForUpdates(Activity context) {
        boolean z = BuildVars.DEBUG_VERSION;
    }

    public static void unregisterUpdates() {
        boolean z = BuildVars.DEBUG_VERSION;
    }

    public static void addToClipboard(CharSequence str) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void addMediaToGallery(String fromPath) {
        if (fromPath != null) {
            addMediaToGallery(Uri.fromFile(new File(fromPath)));
        }
    }

    public static void addMediaToGallery(Uri uri) {
        if (uri != null) {
            try {
                Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                mediaScanIntent.setData(uri);
                ApplicationLoader.applicationContext.sendBroadcast(mediaScanIntent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static File getAlbumDir(boolean secretChat) {
        if (secretChat || (Build.VERSION.SDK_INT >= 23 && ApplicationLoader.applicationContext.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
            return FileLoader.getDirectory(4);
        }
        File storageDir = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Yixin");
            if (!storageDir.mkdirs() && !storageDir.exists()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("failed to create directory");
                }
                File storageDir2 = ApplicationLoader.applicationContext.getExternalFilesDir("Yixin");
                if (storageDir2.mkdirs()) {
                    return null;
                }
                storageDir2.exists();
                return null;
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x00b9 A[Catch:{ Exception -> 0x00fe }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00c6 A[Catch:{ Exception -> 0x00fe }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getPath(android.net.Uri r13) {
        /*
            java.lang.String r0 = "_id=?"
            r1 = 0
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00fe }
            r3 = 19
            r4 = 0
            r5 = 1
            if (r2 < r3) goto L_0x000d
            r2 = 1
            goto L_0x000e
        L_0x000d:
            r2 = 0
        L_0x000e:
            if (r2 == 0) goto L_0x00d9
            android.content.Context r3 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00fe }
            boolean r3 = android.provider.DocumentsContract.isDocumentUri(r3, r13)     // Catch:{ Exception -> 0x00fe }
            if (r3 == 0) goto L_0x00d9
            boolean r3 = isExternalStorageDocument(r13)     // Catch:{ Exception -> 0x00fe }
            java.lang.String r6 = ":"
            if (r3 == 0) goto L_0x004f
            java.lang.String r0 = android.provider.DocumentsContract.getDocumentId(r13)     // Catch:{ Exception -> 0x00fe }
            java.lang.String[] r3 = r0.split(r6)     // Catch:{ Exception -> 0x00fe }
            r4 = r3[r4]     // Catch:{ Exception -> 0x00fe }
            java.lang.String r6 = "primary"
            boolean r6 = r6.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x00fe }
            if (r6 == 0) goto L_0x004d
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00fe }
            r6.<init>()     // Catch:{ Exception -> 0x00fe }
            java.io.File r7 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x00fe }
            r6.append(r7)     // Catch:{ Exception -> 0x00fe }
            java.lang.String r7 = "/"
            r6.append(r7)     // Catch:{ Exception -> 0x00fe }
            r5 = r3[r5]     // Catch:{ Exception -> 0x00fe }
            r6.append(r5)     // Catch:{ Exception -> 0x00fe }
            java.lang.String r1 = r6.toString()     // Catch:{ Exception -> 0x00fe }
            return r1
        L_0x004d:
            goto L_0x00fd
        L_0x004f:
            boolean r3 = isDownloadsDocument(r13)     // Catch:{ Exception -> 0x00fe }
            if (r3 == 0) goto L_0x0072
            java.lang.String r0 = android.provider.DocumentsContract.getDocumentId(r13)     // Catch:{ Exception -> 0x00fe }
            java.lang.String r3 = "content://downloads/public_downloads"
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x00fe }
            java.lang.Long r4 = java.lang.Long.valueOf(r0)     // Catch:{ Exception -> 0x00fe }
            long r4 = r4.longValue()     // Catch:{ Exception -> 0x00fe }
            android.net.Uri r3 = android.content.ContentUris.withAppendedId(r3, r4)     // Catch:{ Exception -> 0x00fe }
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00fe }
            java.lang.String r1 = getDataColumn(r4, r3, r1, r1)     // Catch:{ Exception -> 0x00fe }
            return r1
        L_0x0072:
            boolean r3 = isMediaDocument(r13)     // Catch:{ Exception -> 0x00fe }
            if (r3 == 0) goto L_0x00fd
            java.lang.String r3 = android.provider.DocumentsContract.getDocumentId(r13)     // Catch:{ Exception -> 0x00fe }
            java.lang.String[] r6 = r3.split(r6)     // Catch:{ Exception -> 0x00fe }
            r7 = r6[r4]     // Catch:{ Exception -> 0x00fe }
            r8 = 0
            r9 = -1
            int r10 = r7.hashCode()     // Catch:{ Exception -> 0x00fe }
            r11 = 93166550(0x58d9bd6, float:1.3316821E-35)
            r12 = 2
            if (r10 == r11) goto L_0x00ae
            r11 = 100313435(0x5faa95b, float:2.3572098E-35)
            if (r10 == r11) goto L_0x00a4
            r11 = 112202875(0x6b0147b, float:6.6233935E-35)
            if (r10 == r11) goto L_0x0099
        L_0x0098:
            goto L_0x00b7
        L_0x0099:
            java.lang.String r10 = "video"
            boolean r10 = r7.equals(r10)     // Catch:{ Exception -> 0x00fe }
            if (r10 == 0) goto L_0x0098
            r9 = 1
            goto L_0x00b7
        L_0x00a4:
            java.lang.String r10 = "image"
            boolean r10 = r7.equals(r10)     // Catch:{ Exception -> 0x00fe }
            if (r10 == 0) goto L_0x0098
            r9 = 0
            goto L_0x00b7
        L_0x00ae:
            java.lang.String r10 = "audio"
            boolean r10 = r7.equals(r10)     // Catch:{ Exception -> 0x00fe }
            if (r10 == 0) goto L_0x0098
            r9 = 2
        L_0x00b7:
            if (r9 == 0) goto L_0x00c6
            if (r9 == r5) goto L_0x00c2
            if (r9 == r12) goto L_0x00be
            goto L_0x00ca
        L_0x00be:
            android.net.Uri r9 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00fe }
            r8 = r9
            goto L_0x00ca
        L_0x00c2:
            android.net.Uri r9 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00fe }
            r8 = r9
            goto L_0x00ca
        L_0x00c6:
            android.net.Uri r9 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00fe }
            r8 = r9
        L_0x00ca:
            r9 = r0
            java.lang.String[] r10 = new java.lang.String[r5]     // Catch:{ Exception -> 0x00fe }
            r5 = r6[r5]     // Catch:{ Exception -> 0x00fe }
            r10[r4] = r5     // Catch:{ Exception -> 0x00fe }
            r4 = r10
            android.content.Context r5 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00fe }
            java.lang.String r0 = getDataColumn(r5, r8, r0, r4)     // Catch:{ Exception -> 0x00fe }
            return r0
        L_0x00d9:
            java.lang.String r0 = "content"
            java.lang.String r3 = r13.getScheme()     // Catch:{ Exception -> 0x00fe }
            boolean r0 = r0.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x00fe }
            if (r0 == 0) goto L_0x00ec
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00fe }
            java.lang.String r0 = getDataColumn(r0, r13, r1, r1)     // Catch:{ Exception -> 0x00fe }
            return r0
        L_0x00ec:
            java.lang.String r0 = "file"
            java.lang.String r3 = r13.getScheme()     // Catch:{ Exception -> 0x00fe }
            boolean r0 = r0.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x00fe }
            if (r0 == 0) goto L_0x00fd
            java.lang.String r0 = r13.getPath()     // Catch:{ Exception -> 0x00fe }
            return r0
        L_0x00fd:
            goto L_0x0102
        L_0x00fe:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0102:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.AndroidUtilities.getPath(android.net.Uri):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004f, code lost:
        if (r2 != null) goto L_0x0051;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0059, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getDataColumn(android.content.Context r9, android.net.Uri r10, java.lang.String r11, java.lang.String[] r12) {
        /*
            java.lang.String r0 = "_data"
            java.lang.String r1 = "_data"
            java.lang.String[] r4 = new java.lang.String[]{r1}
            r8 = 0
            android.content.ContentResolver r2 = r9.getContentResolver()     // Catch:{ Exception -> 0x0060 }
            r7 = 0
            r3 = r10
            r5 = r11
            r6 = r12
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0060 }
            if (r2 == 0) goto L_0x005a
            boolean r3 = r2.moveToFirst()     // Catch:{ all -> 0x004c }
            if (r3 == 0) goto L_0x005a
            int r1 = r2.getColumnIndexOrThrow(r1)     // Catch:{ all -> 0x004c }
            java.lang.String r3 = r2.getString(r1)     // Catch:{ all -> 0x004c }
            java.lang.String r5 = "content://"
            boolean r5 = r3.startsWith(r5)     // Catch:{ all -> 0x004c }
            if (r5 != 0) goto L_0x0045
            java.lang.String r5 = "/"
            boolean r5 = r3.startsWith(r5)     // Catch:{ all -> 0x004c }
            if (r5 != 0) goto L_0x003e
            java.lang.String r5 = "file://"
            boolean r5 = r3.startsWith(r5)     // Catch:{ all -> 0x004c }
            if (r5 != 0) goto L_0x003e
            goto L_0x0045
        L_0x003e:
            if (r2 == 0) goto L_0x0044
            r2.close()     // Catch:{ Exception -> 0x0060 }
        L_0x0044:
            return r3
        L_0x0045:
            if (r2 == 0) goto L_0x004b
            r2.close()     // Catch:{ Exception -> 0x0060 }
        L_0x004b:
            return r8
        L_0x004c:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x004e }
        L_0x004e:
            r3 = move-exception
            if (r2 == 0) goto L_0x0059
            r2.close()     // Catch:{ all -> 0x0055 }
            goto L_0x0059
        L_0x0055:
            r5 = move-exception
            r1.addSuppressed(r5)     // Catch:{ Exception -> 0x0060 }
        L_0x0059:
            throw r3     // Catch:{ Exception -> 0x0060 }
        L_0x005a:
            if (r2 == 0) goto L_0x005f
            r2.close()     // Catch:{ Exception -> 0x0060 }
        L_0x005f:
            goto L_0x0061
        L_0x0060:
            r1 = move-exception
        L_0x0061:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.AndroidUtilities.getDataColumn(android.content.Context, android.net.Uri, java.lang.String, java.lang.String[]):java.lang.String");
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static File generatePicturePath() {
        return generatePicturePath(false);
    }

    public static File generatePicturePath(boolean secretChat) {
        try {
            File storageDir = getAlbumDir(secretChat);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000)) + 1);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
            return new File(storageDir, "IMG_" + timeStamp + ".jpg");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static CharSequence generateSearchName(String name, String name2, String q) {
        if (name == null && name2 == null) {
            return "";
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String wholeString = name;
        if (wholeString == null || wholeString.length() == 0) {
            wholeString = name2;
        } else if (!(name2 == null || name2.length() == 0)) {
            wholeString = wholeString + " " + name2;
        }
        String wholeString2 = wholeString.trim();
        String lower = " " + wholeString2.toLowerCase();
        int lastIndex = 0;
        while (true) {
            int indexOf = lower.indexOf(" " + q, lastIndex);
            int index = indexOf;
            if (indexOf == -1) {
                break;
            }
            int i = 1;
            int idx = index - (index == 0 ? 0 : 1);
            int length = q.length();
            if (index == 0) {
                i = 0;
            }
            int end = length + i + idx;
            if (lastIndex != 0 && lastIndex != idx + 1) {
                builder.append(wholeString2.substring(lastIndex, idx));
            } else if (lastIndex == 0 && idx != 0) {
                builder.append(wholeString2.substring(0, idx));
            }
            String query = wholeString2.substring(idx, Math.min(wholeString2.length(), end));
            if (query.startsWith(" ")) {
                builder.append(" ");
            }
            String query2 = query.trim();
            int start = builder.length();
            builder.append(query2);
            builder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), start, query2.length() + start, 33);
            lastIndex = end;
        }
        if (lastIndex != -1 && lastIndex < wholeString2.length()) {
            builder.append(wholeString2.substring(lastIndex));
        }
        return builder;
    }

    public static boolean isAirplaneModeOn() {
        if (Build.VERSION.SDK_INT < 17) {
            if (Settings.System.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
                return true;
            }
            return false;
        } else if (Settings.Global.getInt(ApplicationLoader.applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public static File generateVideoPath() {
        return generateVideoPath(false);
    }

    public static File generateVideoPath(boolean secretChat) {
        try {
            File storageDir = getAlbumDir(secretChat);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + ((long) Utilities.random.nextInt(1000)) + 1);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
            return new File(storageDir, "VID_" + timeStamp + ".mp4");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static String formatFileSize(long size) {
        return formatFileSize(size, false);
    }

    public static String formatFileSize(long size, boolean removeZero) {
        if (size < 1024) {
            return String.format("%d B", new Object[]{Long.valueOf(size)});
        } else if (size < 1048576) {
            float value = ((float) size) / 1024.0f;
            if (!removeZero || (value - ((float) ((int) value))) * 10.0f != 0.0f) {
                return String.format("%.1f KB", new Object[]{Float.valueOf(value)});
            }
            return String.format("%d KB", new Object[]{Integer.valueOf((int) value)});
        } else if (size < 1073741824) {
            float value2 = (((float) size) / 1024.0f) / 1024.0f;
            if (!removeZero || (value2 - ((float) ((int) value2))) * 10.0f != 0.0f) {
                return String.format("%.1f MB", new Object[]{Float.valueOf(value2)});
            }
            return String.format("%d MB", new Object[]{Integer.valueOf((int) value2)});
        } else {
            float value3 = ((((float) size) / 1024.0f) / 1024.0f) / 1024.0f;
            if (!removeZero || (value3 - ((float) ((int) value3))) * 10.0f != 0.0f) {
                return String.format("%.1f GB", new Object[]{Float.valueOf(value3)});
            }
            return String.format("%d GB", new Object[]{Integer.valueOf((int) value3)});
        }
    }

    public static byte[] decodeQuotedPrintable(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int i = 0;
        while (i < bytes.length) {
            byte b = bytes[i];
            if (b == 61) {
                int i2 = i + 1;
                try {
                    int u = Character.digit((char) bytes[i2], 16);
                    i = i2 + 1;
                    buffer.write((char) ((u << 4) + Character.digit((char) bytes[i], 16)));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return null;
                }
            } else {
                buffer.write(b);
            }
            i++;
        }
        byte[] array = buffer.toByteArray();
        try {
            buffer.close();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        return array;
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        OutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        while (true) {
            int read = sourceFile.read(buf);
            int len = read;
            if (read > 0) {
                Thread.yield();
                out.write(buf, 0, len);
            } else {
                out.close();
                return true;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003f, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r1.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0043, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0046, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x004f, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean copyFile(java.io.File r9, java.io.File r10) throws java.io.IOException {
        /*
            boolean r0 = r9.equals(r10)
            r1 = 1
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            boolean r0 = r10.exists()
            if (r0 != 0) goto L_0x0011
            r10.createNewFile()
        L_0x0011:
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0050 }
            r0.<init>(r9)     // Catch:{ Exception -> 0x0050 }
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ all -> 0x0044 }
            r2.<init>(r10)     // Catch:{ all -> 0x0044 }
            java.nio.channels.FileChannel r3 = r2.getChannel()     // Catch:{ all -> 0x0038 }
            java.nio.channels.FileChannel r4 = r0.getChannel()     // Catch:{ all -> 0x0038 }
            r5 = 0
            java.nio.channels.FileChannel r7 = r0.getChannel()     // Catch:{ all -> 0x0038 }
            long r7 = r7.size()     // Catch:{ all -> 0x0038 }
            r3.transferFrom(r4, r5, r7)     // Catch:{ all -> 0x0038 }
            r2.close()     // Catch:{ all -> 0x0044 }
            r0.close()     // Catch:{ Exception -> 0x0050 }
            return r1
        L_0x0038:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x003a }
        L_0x003a:
            r3 = move-exception
            r2.close()     // Catch:{ all -> 0x003f }
            goto L_0x0043
        L_0x003f:
            r4 = move-exception
            r1.addSuppressed(r4)     // Catch:{ all -> 0x0044 }
        L_0x0043:
            throw r3     // Catch:{ all -> 0x0044 }
        L_0x0044:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x004b }
            goto L_0x004f
        L_0x004b:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ Exception -> 0x0050 }
        L_0x004f:
            throw r2     // Catch:{ Exception -> 0x0050 }
        L_0x0050:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.AndroidUtilities.copyFile(java.io.File, java.io.File):boolean");
    }

    public static byte[] calcAuthKeyHash(byte[] auth_key) {
        byte[] key_hash = new byte[16];
        System.arraycopy(Utilities.computeSHA1(auth_key), 0, key_hash, 0, 16);
        return key_hash;
    }

    public static void openDocument(MessageObject message, Activity activity, BaseFragment parentFragment) {
        TLRPC.Document document;
        File f;
        String str;
        String str2;
        MessageObject messageObject = message;
        Activity activity2 = activity;
        BaseFragment baseFragment = parentFragment;
        if (messageObject != null && (document = message.getDocument()) != null) {
            File f2 = null;
            String fileName = messageObject.messageOwner.media != null ? FileLoader.getAttachFileName(document) : "";
            if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0)) {
                f2 = new File(messageObject.messageOwner.attachPath);
            }
            if (f2 == null || (f2 != null && !f2.exists())) {
                f = FileLoader.getPathToMessage(messageObject.messageOwner);
            } else {
                f = f2;
            }
            if (f != null && f.exists()) {
                if (baseFragment == null || !f.getName().toLowerCase().endsWith("attheme")) {
                    String realMimeType = null;
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setFlags(1);
                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        int idx = fileName.lastIndexOf(46);
                        if (idx != -1 && (realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase())) == null && ((realMimeType = document.mime_type) == null || realMimeType.length() == 0)) {
                            realMimeType = null;
                        }
                        if (Build.VERSION.SDK_INT >= 24) {
                            Uri uriForFile = FileProvider.getUriForFile(activity2, "im.bclpbkiauv.messenger.provider", f);
                            if (realMimeType != null) {
                                str2 = realMimeType;
                            } else {
                                str2 = "text/plain";
                            }
                            intent.setDataAndType(uriForFile, str2);
                        } else {
                            Uri fromFile = Uri.fromFile(f);
                            if (realMimeType != null) {
                                str = realMimeType;
                            } else {
                                str = "text/plain";
                            }
                            intent.setDataAndType(fromFile, str);
                        }
                        if (realMimeType != null) {
                            try {
                                activity2.startActivityForResult(intent, 500);
                            } catch (Exception e) {
                                Exception exc = e;
                                if (Build.VERSION.SDK_INT >= 24) {
                                    intent.setDataAndType(FileProvider.getUriForFile(activity2, "im.bclpbkiauv.messenger.provider", f), "text/plain");
                                } else {
                                    intent.setDataAndType(Uri.fromFile(f), "text/plain");
                                }
                                activity2.startActivityForResult(intent, 500);
                            }
                        } else {
                            activity2.startActivityForResult(intent, 500);
                        }
                    } catch (Exception e2) {
                        if (activity2 != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                            builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, message.getDocument().mime_type));
                            if (baseFragment != null) {
                                baseFragment.showDialog(builder.create());
                            } else {
                                builder.show();
                            }
                        }
                    }
                } else {
                    Theme.ThemeInfo themeInfo = Theme.applyThemeFile(f, message.getDocumentName(), (TLRPC.TL_theme) null, true);
                    if (themeInfo != null) {
                        baseFragment.presentFragment(new ThemePreviewActivity(themeInfo));
                        return;
                    }
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) activity2);
                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder2.setMessage(LocaleController.getString("IncorrectTheme", R.string.IncorrectTheme));
                    builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    baseFragment.showDialog(builder2.create());
                }
            }
        }
    }

    public static void openForView(MessageObject message, Activity activity) {
        String str;
        String str2;
        File f = null;
        String fileName = message.getFileName();
        if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
            f = new File(message.messageOwner.attachPath);
        }
        if (f == null || !f.exists()) {
            f = FileLoader.getPathToMessage(message.messageOwner);
        }
        if (f != null && f.exists()) {
            String realMimeType = null;
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(1);
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            int idx = fileName.lastIndexOf(46);
            if (idx != -1 && (realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase())) == null) {
                if (message.type == 9 || message.type == 0) {
                    realMimeType = message.getDocument().mime_type;
                }
                if (realMimeType == null || realMimeType.length() == 0) {
                    realMimeType = null;
                }
            }
            if (Build.VERSION.SDK_INT < 26 || realMimeType == null || !realMimeType.equals("application/vnd.android.package-archive") || ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
                if (Build.VERSION.SDK_INT >= 24) {
                    Uri uriForFile = FileProvider.getUriForFile(activity, "im.bclpbkiauv.messenger.provider", f);
                    if (realMimeType != null) {
                        str2 = realMimeType;
                    } else {
                        str2 = "text/plain";
                    }
                    intent.setDataAndType(uriForFile, str2);
                } else {
                    Uri fromFile = Uri.fromFile(f);
                    if (realMimeType != null) {
                        str = realMimeType;
                    } else {
                        str = "text/plain";
                    }
                    intent.setDataAndType(fromFile, str);
                }
                if (realMimeType != null) {
                    try {
                        activity.startActivityForResult(intent, 500);
                    } catch (Exception e) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            intent.setDataAndType(FileProvider.getUriForFile(activity, "im.bclpbkiauv.messenger.provider", f), "text/plain");
                        } else {
                            intent.setDataAndType(Uri.fromFile(f), "text/plain");
                        }
                        activity.startActivityForResult(intent, 500);
                    }
                } else {
                    activity.startActivityForResult(intent, 500);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ApkRestricted", R.string.ApkRestricted));
                builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener(activity) {
                    private final /* synthetic */ Activity f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AndroidUtilities.lambda$openForView$2(this.f$0, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                builder.show();
            }
        }
    }

    static /* synthetic */ void lambda$openForView$2(Activity activity, DialogInterface dialogInterface, int i) {
        try {
            activity.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + activity.getPackageName())));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void openForView(TLObject media, Activity activity) {
        String str;
        String str2;
        if (media != null && activity != null) {
            String fileName = FileLoader.getAttachFileName(media);
            File f = FileLoader.getPathToAttach(media, true);
            if (f != null && f.exists()) {
                String realMimeType = null;
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                int idx = fileName.lastIndexOf(46);
                if (idx != -1 && (realMimeType = myMime.getMimeTypeFromExtension(fileName.substring(idx + 1).toLowerCase())) == null) {
                    if (media instanceof TLRPC.TL_document) {
                        realMimeType = ((TLRPC.TL_document) media).mime_type;
                    }
                    if (realMimeType == null || realMimeType.length() == 0) {
                        realMimeType = null;
                    }
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    Uri uriForFile = FileProvider.getUriForFile(activity, "im.bclpbkiauv.messenger.provider", f);
                    if (realMimeType != null) {
                        str2 = realMimeType;
                    } else {
                        str2 = "text/plain";
                    }
                    intent.setDataAndType(uriForFile, str2);
                } else {
                    Uri fromFile = Uri.fromFile(f);
                    if (realMimeType != null) {
                        str = realMimeType;
                    } else {
                        str = "text/plain";
                    }
                    intent.setDataAndType(fromFile, str);
                }
                if (realMimeType != null) {
                    try {
                        activity.startActivityForResult(intent, 500);
                    } catch (Exception e) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            intent.setDataAndType(FileProvider.getUriForFile(activity, "im.bclpbkiauv.messenger.provider", f), "text/plain");
                        } else {
                            intent.setDataAndType(Uri.fromFile(f), "text/plain");
                        }
                        activity.startActivityForResult(intent, 500);
                    }
                } else {
                    activity.startActivityForResult(intent, 500);
                }
            }
        }
    }

    public static boolean isBannedForever(TLRPC.TL_chatBannedRights rights) {
        return rights == null || Math.abs(((long) rights.until_date) - (System.currentTimeMillis() / 1000)) > 157680000;
    }

    public static void setRectToRect(Matrix matrix, RectF src, RectF dst, int rotation, boolean translate) {
        float sy;
        float sx;
        float ty;
        float tx;
        float diff;
        boolean xLarger = false;
        if (rotation == 90 || rotation == 270) {
            sx = dst.height() / src.width();
            sy = dst.width() / src.height();
        } else {
            sx = dst.width() / src.width();
            sy = dst.height() / src.height();
        }
        if (sx < sy) {
            sx = sy;
            xLarger = true;
        } else {
            sy = sx;
        }
        if (translate) {
            matrix.setTranslate(dst.left, dst.top);
        }
        if (rotation == 90) {
            matrix.preRotate(90.0f);
            matrix.preTranslate(0.0f, -dst.width());
        } else if (rotation == 180) {
            matrix.preRotate(180.0f);
            matrix.preTranslate(-dst.width(), -dst.height());
        } else if (rotation == 270) {
            matrix.preRotate(270.0f);
            matrix.preTranslate(-dst.height(), 0.0f);
        }
        if (translate) {
            tx = (-src.left) * sx;
            ty = (-src.top) * sy;
        } else {
            tx = dst.left - (src.left * sx);
            ty = dst.top - (src.top * sy);
        }
        if (xLarger) {
            diff = dst.width() - (src.width() * sy);
        } else {
            diff = dst.height() - (src.height() * sy);
        }
        float diff2 = diff / 2.0f;
        if (xLarger) {
            tx += diff2;
        } else {
            ty += diff2;
        }
        matrix.preScale(sx, sy);
        if (translate) {
            matrix.preTranslate(tx, ty);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0070, code lost:
        if (r10.startsWith("hchat://socks") == false) goto L_0x00f5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:68:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean handleProxyIntent(android.app.Activity r19, android.content.Intent r20) {
        /*
            java.lang.String r0 = "hchat:proxy"
            java.lang.String r1 = "hchat://m12345.com"
            r2 = 0
            if (r20 != 0) goto L_0x0008
            return r2
        L_0x0008:
            int r3 = r20.getFlags()     // Catch:{ Exception -> 0x011d }
            r4 = 1048576(0x100000, float:1.469368E-39)
            r3 = r3 & r4
            if (r3 == 0) goto L_0x0012
            return r2
        L_0x0012:
            android.net.Uri r3 = r20.getData()     // Catch:{ Exception -> 0x011d }
            if (r3 == 0) goto L_0x011c
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            java.lang.String r9 = r3.getScheme()     // Catch:{ Exception -> 0x011d }
            if (r9 == 0) goto L_0x00f1
            java.lang.String r10 = "http"
            boolean r10 = r9.equals(r10)     // Catch:{ Exception -> 0x011d }
            java.lang.String r11 = "secret"
            java.lang.String r12 = "pass"
            java.lang.String r13 = "user"
            java.lang.String r14 = "port"
            java.lang.String r15 = "server"
            if (r10 != 0) goto L_0x00a6
            java.lang.String r10 = "https"
            boolean r10 = r9.equals(r10)     // Catch:{ Exception -> 0x011d }
            if (r10 == 0) goto L_0x0044
            r17 = r4
            r18 = r5
            goto L_0x00aa
        L_0x0044:
            java.lang.String r10 = "hchat"
            boolean r10 = r9.equals(r10)     // Catch:{ Exception -> 0x011d }
            if (r10 == 0) goto L_0x00a1
            java.lang.String r10 = r3.toString()     // Catch:{ Exception -> 0x011d }
            boolean r16 = r10.startsWith(r0)     // Catch:{ Exception -> 0x011d }
            java.lang.String r2 = "hchat://socks"
            r17 = r4
            java.lang.String r4 = "hchat:socks"
            r18 = r5
            java.lang.String r5 = "hchat://proxy"
            if (r16 != 0) goto L_0x0072
            boolean r16 = r10.startsWith(r5)     // Catch:{ Exception -> 0x011d }
            if (r16 != 0) goto L_0x0072
            boolean r16 = r10.startsWith(r4)     // Catch:{ Exception -> 0x011d }
            if (r16 != 0) goto L_0x0072
            boolean r16 = r10.startsWith(r2)     // Catch:{ Exception -> 0x011d }
            if (r16 == 0) goto L_0x00f5
        L_0x0072:
            java.lang.String r0 = r10.replace(r0, r1)     // Catch:{ Exception -> 0x011d }
            java.lang.String r0 = r0.replace(r5, r1)     // Catch:{ Exception -> 0x011d }
            java.lang.String r0 = r0.replace(r2, r1)     // Catch:{ Exception -> 0x011d }
            java.lang.String r0 = r0.replace(r4, r1)     // Catch:{ Exception -> 0x011d }
            android.net.Uri r1 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x011d }
            r3 = r1
            java.lang.String r1 = r3.getQueryParameter(r15)     // Catch:{ Exception -> 0x011d }
            r7 = r1
            java.lang.String r1 = r3.getQueryParameter(r14)     // Catch:{ Exception -> 0x011d }
            r6 = r1
            java.lang.String r1 = r3.getQueryParameter(r13)     // Catch:{ Exception -> 0x011d }
            r4 = r1
            java.lang.String r1 = r3.getQueryParameter(r12)     // Catch:{ Exception -> 0x011d }
            r5 = r1
            java.lang.String r1 = r3.getQueryParameter(r11)     // Catch:{ Exception -> 0x011d }
            r8 = r1
            goto L_0x00f9
        L_0x00a1:
            r17 = r4
            r18 = r5
            goto L_0x00f5
        L_0x00a6:
            r17 = r4
            r18 = r5
        L_0x00aa:
            java.lang.String r0 = r3.getHost()     // Catch:{ Exception -> 0x011d }
            if (r0 == 0) goto L_0x00ec
            java.lang.String r1 = "m12345.com"
            java.lang.String r2 = r0.toLowerCase()     // Catch:{ Exception -> 0x011d }
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x011d }
            if (r1 == 0) goto L_0x00ec
            java.lang.String r1 = r3.getPath()     // Catch:{ Exception -> 0x011d }
            if (r1 == 0) goto L_0x00ec
            java.lang.String r2 = "/socks"
            boolean r2 = r1.startsWith(r2)     // Catch:{ Exception -> 0x011d }
            if (r2 != 0) goto L_0x00d2
            java.lang.String r2 = "/proxy"
            boolean r2 = r1.startsWith(r2)     // Catch:{ Exception -> 0x011d }
            if (r2 == 0) goto L_0x00ec
        L_0x00d2:
            java.lang.String r2 = r3.getQueryParameter(r15)     // Catch:{ Exception -> 0x011d }
            r7 = r2
            java.lang.String r2 = r3.getQueryParameter(r14)     // Catch:{ Exception -> 0x011d }
            r6 = r2
            java.lang.String r2 = r3.getQueryParameter(r13)     // Catch:{ Exception -> 0x011d }
            r4 = r2
            java.lang.String r2 = r3.getQueryParameter(r12)     // Catch:{ Exception -> 0x011d }
            r5 = r2
            java.lang.String r2 = r3.getQueryParameter(r11)     // Catch:{ Exception -> 0x011d }
            r8 = r2
            goto L_0x00f0
        L_0x00ec:
            r4 = r17
            r5 = r18
        L_0x00f0:
            goto L_0x00f9
        L_0x00f1:
            r17 = r4
            r18 = r5
        L_0x00f5:
            r4 = r17
            r5 = r18
        L_0x00f9:
            boolean r0 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x011d }
            if (r0 != 0) goto L_0x011c
            boolean r0 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x011d }
            if (r0 != 0) goto L_0x011c
            java.lang.String r0 = ""
            if (r4 != 0) goto L_0x010a
            r4 = r0
        L_0x010a:
            if (r5 != 0) goto L_0x010d
            r5 = r0
        L_0x010d:
            if (r8 != 0) goto L_0x0110
            r8 = r0
        L_0x0110:
            r10 = r19
            r11 = r7
            r12 = r6
            r13 = r4
            r14 = r5
            r15 = r8
            showProxyAlert(r10, r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x011d }
            r0 = 1
            return r0
        L_0x011c:
            goto L_0x011e
        L_0x011d:
            r0 = move-exception
        L_0x011e:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.AndroidUtilities.handleProxyIntent(android.app.Activity, android.content.Intent):boolean");
    }

    public static boolean shouldEnableAnimation() {
        if (Build.VERSION.SDK_INT < 26 || Build.VERSION.SDK_INT >= 28) {
            return true;
        }
        if (!((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).isPowerSaveMode() && Settings.Global.getFloat(ApplicationLoader.applicationContext.getContentResolver(), "animator_duration_scale", 1.0f) > 0.0f) {
            return true;
        }
        return false;
    }

    public static void showProxyAlert(Activity activity, String address, String port, String user, String password, String secret) {
        Activity activity2 = activity;
        BottomSheet.Builder builder = new BottomSheet.Builder(activity2);
        Runnable dismissRunnable = builder.getDismissRunnable();
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        LinearLayout linearLayout = new LinearLayout(activity2);
        builder.setCustomView(linearLayout);
        boolean z = true;
        linearLayout.setOrientation(1);
        int i = 5;
        if (!TextUtils.isEmpty(secret)) {
            TextView titleTextView = new TextView(activity2);
            titleTextView.setText(LocaleController.getString("UseProxySettingsTips", R.string.UseProxySettingsTips));
            titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
            titleTextView.setTextSize(1, 14.0f);
            titleTextView.setGravity(49);
            linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 8, 17, 8));
            View lineView = new View(activity2);
            lineView.setBackgroundColor(Theme.getColor(Theme.key_divider));
            linearLayout.addView(lineView, new LinearLayout.LayoutParams(-1, 1));
        }
        int a = 0;
        while (true) {
            if (a >= i) {
                String str = port;
                break;
            }
            String text = null;
            String detail = null;
            if (a == 0) {
                text = address;
                detail = LocaleController.getString("UseProxyAddress", R.string.UseProxyAddress);
                String str2 = port;
            } else if (a == z) {
                text = "" + port;
                detail = LocaleController.getString("UseProxyPort", R.string.UseProxyPort);
            } else {
                String str3 = port;
                if (a == 2) {
                    text = secret;
                    detail = LocaleController.getString("UseProxySecret", R.string.UseProxySecret);
                } else if (a == 3) {
                    text = user;
                    detail = LocaleController.getString("UseProxyUsername", R.string.UseProxyUsername);
                } else if (a == 4) {
                    text = password;
                    detail = LocaleController.getString("UseProxyPassword", R.string.UseProxyPassword);
                }
            }
            if (!TextUtils.isEmpty(text)) {
                TextDetailSettingsCell cell = new TextDetailSettingsCell(activity2);
                cell.setTextAndValue(text, detail, z);
                cell.getTextView().setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                cell.getValueTextView().setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, -2));
                if (a == 2) {
                    break;
                }
            }
            a++;
            z = true;
            i = 5;
        }
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(activity2, false);
        pickerBottomLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        linearLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.cancelButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener(dismissRunnable) {
            private final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(View view) {
                this.f$0.run();
            }
        });
        pickerBottomLayout.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        pickerBottomLayout.doneButton.setPadding(dp(18.0f), 0, dp(18.0f), 0);
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("ConnectingConnectProxy", R.string.ConnectingConnectProxy).toUpperCase());
        pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener(address, port, secret, password, user, dismissRunnable) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ String f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ String f$4;
            private final /* synthetic */ Runnable f$5;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void onClick(View view) {
                AndroidUtilities.lambda$showProxyAlert$4(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
            }
        });
        builder.show();
    }

    static /* synthetic */ void lambda$showProxyAlert$4(String address, String port, String secret, String password, String user, Runnable dismissRunnable, View v) {
        SharedConfig.ProxyInfo info;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("proxy_enabled", true);
        editor.putString("proxy_ip", address);
        int p = Utilities.parseInt(port).intValue();
        editor.putInt("proxy_port", p);
        if (TextUtils.isEmpty(secret)) {
            editor.remove("proxy_secret");
            if (TextUtils.isEmpty(password)) {
                editor.remove("proxy_pass");
            } else {
                editor.putString("proxy_pass", password);
            }
            if (TextUtils.isEmpty(user)) {
                editor.remove("proxy_user");
            } else {
                editor.putString("proxy_user", user);
            }
            info = new SharedConfig.ProxyInfo(address, p, user, password, "");
        } else {
            editor.remove("proxy_pass");
            editor.remove("proxy_user");
            editor.putString("proxy_secret", secret);
            info = new SharedConfig.ProxyInfo(address, p, "", "", secret);
        }
        editor.commit();
        SharedConfig.currentProxy = SharedConfig.addProxy(info);
        ConnectionsManager.setProxySettings(true, address, p, user, password, secret);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
        dismissRunnable.run();
    }

    public static String getSystemProperty(String key) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{key});
        } catch (Exception e) {
            return null;
        }
    }

    public static CharSequence concat(CharSequence... text) {
        if (text.length == 0) {
            return "";
        }
        int i = 0;
        if (text.length == 1) {
            return text[0];
        }
        boolean spanned = false;
        int length = text.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                break;
            } else if (text[i2] instanceof Spanned) {
                spanned = true;
                break;
            } else {
                i2++;
            }
        }
        if (spanned) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            int length2 = text.length;
            while (i < length2) {
                CharSequence piece = text[i];
                ssb.append(piece == null ? "null" : piece);
                i++;
            }
            return new SpannedString(ssb);
        }
        StringBuilder sb = new StringBuilder();
        int length3 = text.length;
        while (i < length3) {
            sb.append(text[i]);
            i++;
        }
        return sb.toString();
    }

    public static float[] RGBtoHSB(int r, int g, int b) {
        float saturation;
        float hue;
        float hue2;
        float[] hsbvals = new float[3];
        int cmax = r > g ? r : g;
        if (b > cmax) {
            cmax = b;
        }
        int cmin = r < g ? r : g;
        if (b < cmin) {
            cmin = b;
        }
        float brightness = ((float) cmax) / 255.0f;
        if (cmax != 0) {
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        } else {
            saturation = 0.0f;
        }
        if (saturation == 0.0f) {
            hue = 0.0f;
        } else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) {
                hue2 = bluec - greenc;
            } else if (g == cmax) {
                hue2 = (2.0f + redc) - bluec;
            } else {
                hue2 = (4.0f + greenc) - redc;
            }
            float hue3 = hue2 / 6.0f;
            if (hue3 < 0.0f) {
                hue = 1.0f + hue3;
            } else {
                hue = hue3;
            }
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0f) {
            int i = (int) ((255.0f * brightness) + 0.5f);
            b = i;
            g = i;
            r = i;
        } else {
            float h = (hue - ((float) Math.floor((double) hue))) * 6.0f;
            float f = h - ((float) Math.floor((double) h));
            float p = (1.0f - saturation) * brightness;
            float q = (1.0f - (saturation * f)) * brightness;
            float t = (1.0f - ((1.0f - f) * saturation)) * brightness;
            int i2 = (int) h;
            if (i2 == 0) {
                r = (int) ((brightness * 255.0f) + 0.5f);
                g = (int) ((t * 255.0f) + 0.5f);
                b = (int) ((255.0f * p) + 0.5f);
            } else if (i2 == 1) {
                r = (int) ((q * 255.0f) + 0.5f);
                g = (int) ((brightness * 255.0f) + 0.5f);
                b = (int) ((255.0f * p) + 0.5f);
            } else if (i2 == 2) {
                r = (int) ((p * 255.0f) + 0.5f);
                g = (int) ((brightness * 255.0f) + 0.5f);
                b = (int) ((255.0f * t) + 0.5f);
            } else if (i2 == 3) {
                r = (int) ((p * 255.0f) + 0.5f);
                g = (int) ((q * 255.0f) + 0.5f);
                b = (int) ((255.0f * brightness) + 0.5f);
            } else if (i2 == 4) {
                r = (int) ((t * 255.0f) + 0.5f);
                g = (int) ((p * 255.0f) + 0.5f);
                b = (int) ((255.0f * brightness) + 0.5f);
            } else if (i2 == 5) {
                r = (int) ((brightness * 255.0f) + 0.5f);
                g = (int) ((p * 255.0f) + 0.5f);
                b = (int) ((255.0f * q) + 0.5f);
            }
        }
        return -16777216 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }

    public static int getPatternColor(int color) {
        float[] hsb = RGBtoHSB(Color.red(color), Color.green(color), Color.blue(color));
        if (hsb[1] > 0.0f || (hsb[2] < 1.0f && hsb[2] > 0.0f)) {
            hsb[1] = Math.min(1.0f, hsb[1] + 0.05f + ((1.0f - hsb[1]) * 0.1f));
        }
        if (hsb[2] > 0.5f) {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.65f);
        } else {
            hsb[2] = Math.max(0.0f, Math.min(1.0f, 1.0f - (hsb[2] * 0.65f)));
        }
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 1728053247;
    }

    public static int getPatternSideColor(int color) {
        float[] hsb = RGBtoHSB(Color.red(color), Color.green(color), Color.blue(color));
        hsb[1] = Math.min(1.0f, hsb[1] + 0.05f);
        if (hsb[2] > 0.5f) {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.9f);
        } else {
            hsb[2] = Math.max(0.0f, hsb[2] * 0.9f);
        }
        return HSBtoRGB(hsb[0], hsb[1], hsb[2]) | -16777216;
    }

    public static String getWallPaperUrl(Object object, int currentAccount) {
        if (object instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
            String link = "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/bg/" + wallPaper.slug;
            StringBuilder modes = new StringBuilder();
            if (wallPaper.settings != null) {
                if (wallPaper.settings.blur) {
                    modes.append("blur");
                }
                if (wallPaper.settings.motion) {
                    if (modes.length() > 0) {
                        modes.append(Marker.ANY_NON_NULL_MARKER);
                    }
                    modes.append("motion");
                }
            }
            if (modes.length() <= 0) {
                return link;
            }
            return link + "?mode=" + modes.toString();
        } else if (!(object instanceof WallpapersListActivity.ColorWallpaper)) {
            return null;
        } else {
            WallpapersListActivity.ColorWallpaper wallPaper2 = (WallpapersListActivity.ColorWallpaper) object;
            String color = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (wallPaper2.color >> 16)) & UByte.MAX_VALUE), Integer.valueOf(((byte) (wallPaper2.color >> 8)) & UByte.MAX_VALUE), Byte.valueOf((byte) (wallPaper2.color & 255))}).toLowerCase();
            if (wallPaper2.pattern != null) {
                return "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/bg/" + wallPaper2.pattern.slug + "?intensity=" + ((int) (wallPaper2.intensity * 100.0f)) + "&bg_color=" + color;
            }
            return "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/bg/" + color;
        }
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
    }

    public static void makeAccessibilityAnnouncement(CharSequence what) {
        AccessibilityManager am = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        if (am.isEnabled()) {
            AccessibilityEvent ev = AccessibilityEvent.obtain();
            ev.setEventType(16384);
            ev.getText().add(what);
            am.sendAccessibilityEvent(ev);
        }
    }

    public static int getOffsetColor(int color1, int color2, float offset, float alpha) {
        int rF = Color.red(color2);
        int gF = Color.green(color2);
        int bF = Color.blue(color2);
        int aF = Color.alpha(color2);
        int rS = Color.red(color1);
        int gS = Color.green(color1);
        int bS = Color.blue(color1);
        int aS = Color.alpha(color1);
        return Color.argb((int) ((((float) aS) + (((float) (aF - aS)) * offset)) * alpha), (int) (((float) rS) + (((float) (rF - rS)) * offset)), (int) (((float) gS) + (((float) (gF - gS)) * offset)), (int) (((float) bS) + (((float) (bF - bS)) * offset)));
    }

    public static int indexOfIgnoreCase(String origin, String searchStr) {
        if (searchStr.isEmpty() || origin.isEmpty()) {
            return origin.indexOf(searchStr);
        }
        int i = 0;
        while (i < origin.length() && searchStr.length() + i <= origin.length()) {
            int j = 0;
            int ii = i;
            while (ii < origin.length() && j < searchStr.length() && Character.toLowerCase(origin.charAt(ii)) == Character.toLowerCase(searchStr.charAt(j))) {
                j++;
                ii++;
            }
            if (j == searchStr.length()) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static float computePerceivedBrightness(int color) {
        return (((((float) Color.red(color)) * 0.2126f) + (((float) Color.green(color)) * 0.7152f)) + (((float) Color.blue(color)) * 0.0722f)) / 255.0f;
    }

    public static void setLightNavigationBar(Window window, boolean enable) {
        int flags;
        if (Build.VERSION.SDK_INT >= 26) {
            View decorView = window.getDecorView();
            int flags2 = decorView.getSystemUiVisibility();
            if (enable) {
                flags = flags2 | 16;
            } else {
                flags = flags2 & -17;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    public static int getVersionCode(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] getBlock(long offset, File file, int blockSize) {
        byte[] result = new byte[blockSize];
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "r");
            accessFile.seek(offset);
            int readSize = accessFile.read(result);
            if (readSize == -1) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                }
                return null;
            } else if (readSize == blockSize) {
                try {
                    accessFile.close();
                } catch (IOException e2) {
                }
                return result;
            } else {
                byte[] tmpByte = new byte[readSize];
                System.arraycopy(result, 0, tmpByte, 0, readSize);
                try {
                    accessFile.close();
                } catch (IOException e3) {
                }
                return tmpByte;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e5) {
                }
            }
            return null;
        } catch (Throwable th) {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e6) {
                }
            }
            throw th;
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        byte[] buffer = new byte[1024];
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            while (true) {
                int read = in.read(buffer, 0, 1024);
                int len = read;
                if (read != -1) {
                    digest.update(buffer, 0, len);
                } else {
                    in.close();
                    return byteArrayToHex(digest.digest());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] resultCharArray = new char[(byteArray.length * 2)];
        int index = 0;
        for (byte b : byteArray) {
            int index2 = index + 1;
            resultCharArray[index] = hexDigits[(b >>> 4) & 15];
            index = index2 + 1;
            resultCharArray[index2] = hexDigits[b & 15];
        }
        return new String(resultCharArray);
    }

    public static boolean checkHasExitsUserOrDeletedEverAndShowDialog(BaseFragment host, String phone) {
        if (phone == null) {
            return false;
        }
        int i = 0;
        while (i < 3) {
            UserConfig userConfig = UserConfig.getInstance(i);
            if (UserObject.isDeleted(userConfig.getCurrentUser())) {
                AlertsCreator.showSimpleAlert(host, LocaleController.getString("ReminderDeletedEverPleaseUseOtherAccount", R.string.ReminderDeletedEverPleaseUseOtherAccount));
                return true;
            } else if (PhoneNumberUtils.compare(phone, userConfig.getCurrentUser().phone)) {
                if (i == UserConfig.selectedAccount) {
                    AlertsCreator.showSimpleAlert(host, LocaleController.getString("AccountAlreadyLoggedIn", R.string.AccountAlreadyLoggedIn));
                } else {
                    AlertsCreator.showSimpleAlert(host, LocaleController.getString("ReminderAccountHadExitsAndSwitchAccount", R.string.ReminderAccountHadExitsAndSwitchAccount));
                }
                return true;
            } else {
                i++;
            }
        }
        return false;
    }

    public static boolean checkCamera(Context context) {
        String[] cameraIds = new String[0];
        try {
            cameraIds = ((CameraManager) context.getSystemService("camera")).getCameraIdList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cameraIds != null && cameraIds.length > 0) {
            String str = cameraIds[0];
            if (cameraIds[1] != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService("activity");
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
        if (activityManager != null) {
            appProcesses = activityManager.getRunningAppProcesses();
        }
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == 100) {
                return true;
            }
        }
        return false;
    }

    public static int getSystemVersion() {
        return Integer.parseInt(Build.VERSION.RELEASE);
    }

    public static void handleKeyboardShelterProblem(EditText editText) {
        handleKeyboardShelterProblem(editText, false);
    }

    public static void handleKeyboardShelterProblem(EditText editText, boolean callSuper) {
        if (editText != null && isEMUI() && Build.VERSION.SDK_INT >= 27) {
            int variation = editText.getInputType() & 4095;
            boolean numberPasswordInputType = false;
            boolean passwordInputType = variation == 129;
            boolean webPasswordInputType = variation == 225;
            if (variation == 18) {
                numberPasswordInputType = true;
            }
            if (passwordInputType) {
                editText.setInputType(1);
            } else if (webPasswordInputType) {
                editText.setInputType(1);
            } else if (numberPasswordInputType) {
                editText.setInputType(2);
            }
            if (passwordInputType || webPasswordInputType || numberPasswordInputType) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public static boolean isMIUI() {
        if ("xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }
        return false;
    }

    public static boolean isEMUI() {
        if ("HUAWEI".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }
        return false;
    }

    public static boolean isOPPO() {
        if ("OPPO".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }
        return false;
    }

    public static boolean isVIVO() {
        if ("vivo".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }
        return false;
    }

    public static int alphaColor(float fraction, int color) {
        return (((int) (256.0f * fraction)) << 24) | (((color >> 16) & 255) << 16) | (((color >> 8) & 255) << 8) | (color & 255);
    }

    public static boolean containsEmoji(CharSequence source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            if (!isEmojiCharacter(source.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmojiCharacter(char codePoint) {
        return codePoint == 0 || codePoint == 9 || codePoint == 10 || codePoint == 13 || (codePoint >= ' ' && codePoint <= 55295) || ((codePoint >= 57344 && codePoint <= 65533) || (codePoint >= 0 && codePoint <= 65535));
    }

    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == 1;
    }

    public static Bitmap blurBitmap(Context context, Bitmap bitmap) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(15.0f);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);
        rs.destroy();
        return outBitmap;
    }
}

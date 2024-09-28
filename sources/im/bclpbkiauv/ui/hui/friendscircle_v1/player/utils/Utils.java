package im.bclpbkiauv.ui.hui.friendscircle_v1.player.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import com.king.zxing.util.LogUtils;

public class Utils {
    private static final String UNKNOWN_SIZE = "00:00";

    public static String formatVideoTimeLength(long miliseconds) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        Object obj;
        int seconds = (int) (miliseconds / 1000);
        if (seconds == 0) {
            return UNKNOWN_SIZE;
        }
        if (seconds < 60) {
            StringBuilder sb = new StringBuilder();
            sb.append("00:");
            if (seconds < 10) {
                obj = "0" + seconds;
            } else {
                obj = Integer.valueOf(seconds);
            }
            sb.append(obj);
            return sb.toString();
        } else if (seconds < 3600) {
            long sec = (long) (seconds % 60);
            long min = (long) (seconds / 60);
            StringBuilder sb2 = new StringBuilder();
            if (min < 10) {
                str4 = "0" + min;
            } else {
                str4 = String.valueOf(min);
            }
            sb2.append(str4);
            sb2.append(LogUtils.COLON);
            if (sec < 10) {
                str5 = "0" + sec;
            } else {
                str5 = String.valueOf(sec);
            }
            sb2.append(str5);
            return sb2.toString();
        } else {
            long hour = (long) (seconds / 3600);
            long min2 = (long) ((seconds % 3600) / 60);
            long sec2 = (long) ((seconds % 3600) % 60);
            StringBuilder sb3 = new StringBuilder();
            if (hour < 10) {
                str = "0" + hour;
            } else {
                str = String.valueOf(hour);
            }
            sb3.append(str);
            sb3.append(LogUtils.COLON);
            if (min2 < 10) {
                str2 = "0" + min2;
            } else {
                str2 = String.valueOf(min2);
            }
            sb3.append(str2);
            sb3.append(LogUtils.COLON);
            if (sec2 < 10) {
                str3 = "0" + sec2;
            } else {
                str3 = String.valueOf(sec2);
            }
            sb3.append(str3);
            return sb3.toString();
        }
    }

    public static void showViewIfNeed(View view) {
        if (view.getVisibility() == 8 || view.getVisibility() == 4) {
            view.setVisibility(0);
        }
    }

    public static void hideViewIfNeed(View view) {
        if (view.getVisibility() == 0) {
            view.setVisibility(8);
        }
    }

    public static boolean isViewShown(View view) {
        return view.getVisibility() == 0;
    }

    public static boolean isViewHide(View view) {
        return view.getVisibility() == 8 || view.getVisibility() == 4;
    }

    public static void log(String message) {
        Log.d("__VideoPlayer__", message);
    }

    public static void logTouch(String message) {
        Log.d("__GestureTouch__", message);
    }

    public static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return getActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static int getWindowWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 17) {
            Point outPoint = new Point();
            display.getRealSize(outPoint);
            return outPoint.x;
        } else if (Build.VERSION.SDK_INT < 13) {
            return display.getWidth();
        } else {
            Point outPoint2 = new Point();
            display.getSize(outPoint2);
            return outPoint2.x;
        }
    }

    public static int getWindowHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static String getCacheDir(Context context) {
        return context.getExternalCacheDir().getAbsolutePath() + "/VideoCache";
    }

    public static boolean isConnected(Context context) {
        try {
            NetworkInfo net = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (net == null || !net.isConnected()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}

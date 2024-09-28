package im.bclpbkiauv.ui.components.toast;

import android.app.AppOpsManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import java.lang.reflect.InvocationTargetException;

public final class ToastUtils {
    private static ToastStrategy sStrategy;
    private static ToastBlackStyle sStyle;
    private static Toast sToast;

    private ToastUtils() {
    }

    public static void init(Context context) {
        checkNullPointer(context);
        if (sStrategy == null) {
            setToastHandler(new ToastStrategy());
        }
        if (sStyle == null) {
            initStyle(new ToastBlackStyle());
        }
        if (!isNotificationEnabled(context)) {
            setToast(new SupportToast(context));
        } else if (Build.VERSION.SDK_INT == 25) {
            setToast(new SafeToast(context));
        } else {
            setToast(new BaseToast(context));
        }
        setView((View) createTextView(context.getApplicationContext()));
        setGravity(sStyle.getGravity(), sStyle.getXOffset(), sStyle.getYOffset());
    }

    public static void show(Object object) {
        if (object != null) {
            if ((object + "").length() != 0) {
                if (!"null".equals(object + "")) {
                    show((CharSequence) object.toString());
                }
            }
        }
    }

    public static void show(int id) {
        checkToastState();
        try {
            show((CharSequence) LocaleController.getString(id + "", id));
        } catch (Resources.NotFoundException e) {
            show((CharSequence) String.valueOf(id));
        }
    }

    public static void showFormat(int resId, Object... args) {
        checkToastState();
        try {
            show((CharSequence) LocaleController.formatString(resId + "", resId, args));
        } catch (Resources.NotFoundException e) {
            FileLog.e("id not exits");
        }
    }

    public static synchronized void show(CharSequence text) {
        synchronized (ToastUtils.class) {
            checkToastState();
            sStrategy.show(text);
        }
    }

    public static synchronized void cancel() {
        synchronized (ToastUtils.class) {
            checkToastState();
            sStrategy.cancel();
        }
    }

    public static void setGravity(int gravity, int xOffset, int yOffset) {
        checkToastState();
        if (Build.VERSION.SDK_INT >= 17) {
            gravity = Gravity.getAbsoluteGravity(gravity, sToast.getView().getResources().getConfiguration().getLayoutDirection());
        }
        sToast.setGravity(gravity, xOffset, yOffset);
    }

    public static void setView(int layoutId) {
        checkToastState();
        setView(View.inflate(sToast.getView().getContext().getApplicationContext(), layoutId, (ViewGroup) null));
    }

    public static void setView(View view) {
        checkToastState();
        checkNullPointer(view);
        if (view.getContext() instanceof Application) {
            Toast toast = sToast;
            if (toast != null) {
                toast.cancel();
                sToast.setView(view);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("The view must be initialized using the context of the application");
    }

    public static <V extends View> V getView() {
        checkToastState();
        return sToast.getView();
    }

    private static void initStyle(ToastBlackStyle style) {
        checkNullPointer(style);
        sStyle = style;
        Toast toast = sToast;
        if (toast != null) {
            toast.cancel();
            Toast toast2 = sToast;
            toast2.setView(createTextView(toast2.getView().getContext().getApplicationContext()));
            sToast.setGravity(sStyle.getGravity(), sStyle.getXOffset(), sStyle.getYOffset());
        }
    }

    public static void setToast(Toast toast) {
        checkNullPointer(toast);
        sToast = toast;
        ToastStrategy toastStrategy = sStrategy;
        if (toastStrategy != null) {
            toastStrategy.bind(toast);
        }
    }

    public static void setToastHandler(ToastStrategy handler) {
        checkNullPointer(handler);
        sStrategy = handler;
        Toast toast = sToast;
        if (toast != null) {
            handler.bind(toast);
        }
    }

    public static Toast getToast() {
        return sToast;
    }

    private static void checkToastState() {
        if (sToast == null) {
            throw new IllegalStateException("ToastUtils has not been initialized");
        }
    }

    private static void checkNullPointer(Object object) {
        if (object == null) {
            throw new NullPointerException("are you ok?");
        }
    }

    private static TextView createTextView(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(sStyle.getBackgroundColor());
        drawable.setCornerRadius((float) sStyle.getCornerRadius());
        TextView textView = new TextView(context);
        textView.setId(16908299);
        textView.setTextColor(sStyle.getTextColor());
        textView.setTextSize(2, sStyle.getTextSize());
        textView.setMinimumWidth(sStyle.getMinimumWidth());
        textView.setMinimumHeight(sStyle.getMinimumHeight());
        textView.setTypeface(sStyle.getTypeFace());
        textView.setGravity(17);
        if (Build.VERSION.SDK_INT >= 16) {
            textView.setPaddingRelative(sStyle.getPaddingStart(), sStyle.getPaddingTop(), sStyle.getPaddingEnd(), sStyle.getPaddingBottom());
        } else {
            textView.setPadding(sStyle.getPaddingStart(), sStyle.getPaddingTop(), sStyle.getPaddingEnd(), sStyle.getPaddingBottom());
        }
        textView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        if (Build.VERSION.SDK_INT >= 16) {
            textView.setBackground(drawable);
        } else {
            textView.setBackgroundDrawable(drawable);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            textView.setZ((float) sStyle.getZ());
        }
        if (sStyle.getMaxLines() > 0) {
            textView.setMaxLines(sStyle.getMaxLines());
        }
        return textView;
    }

    private static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return ((NotificationManager) context.getSystemService("notification")).areNotificationsEnabled();
        }
        if (Build.VERSION.SDK_INT < 19) {
            return true;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService("appops");
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            if (((Integer) appOpsClass.getMethod("checkOpNoThrow", new Class[]{Integer.TYPE, Integer.TYPE, String.class}).invoke(appOps, new Object[]{Integer.valueOf(((Integer) appOpsClass.getDeclaredField("OP_POST_NOTIFICATION").get(Integer.class)).intValue()), Integer.valueOf(uid), pkg})).intValue() == 0) {
                return true;
            }
            return false;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | RuntimeException | InvocationTargetException e) {
            return true;
        }
    }
}

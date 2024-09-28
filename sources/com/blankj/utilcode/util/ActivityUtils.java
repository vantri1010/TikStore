package com.blankj.utilcode.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.List;

public final class ActivityUtils {
    private ActivityUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Activity getActivityByView(View view) {
        if (view != null) {
            return getActivityByContext(view.getContext());
        }
        throw new NullPointerException("Argument 'view' of type View (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Activity getActivityByContext(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static boolean isActivityExists(String pkg, String cls) {
        if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            Intent intent = new Intent();
            intent.setClassName(pkg, cls);
            if (Utils.getApp().getPackageManager().resolveActivity(intent, 0) == null || intent.resolveActivity(Utils.getApp().getPackageManager()) == null || Utils.getApp().getPackageManager().queryIntentActivities(intent, 0).size() == 0) {
                return false;
            }
            return true;
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Class<? extends Activity> clz) {
        if (clz != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivity(context, (Bundle) null, context.getPackageName(), clz.getName(), (Bundle) null);
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void startActivity(Class<? extends Activity> clz, Bundle options) {
        if (clz != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivity(context, (Bundle) null, context.getPackageName(), clz.getName(), options);
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void startActivity(Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        if (clz != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivity(context, (Bundle) null, context.getPackageName(), clz.getName(), getOptionsBundle(context, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16 && (context instanceof Activity)) {
                ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
                return;
            }
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void startActivity(Activity activity, Class<? extends Activity> clz) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivity((Context) activity, (Bundle) null, activity.getPackageName(), clz.getName(), (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, Class<? extends Activity> clz, Bundle options) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivity((Context) activity, (Bundle) null, activity.getPackageName(), clz.getName(), options);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, Class<? extends Activity> clz, View... sharedElements) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivity((Context) activity, (Bundle) null, activity.getPackageName(), clz.getName(), getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivity((Context) activity, (Bundle) null, activity.getPackageName(), clz.getName(), getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Class<? extends Activity> clz) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivity(context, extras, context.getPackageName(), clz.getName(), (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Class<? extends Activity> clz, Bundle options) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivity(context, extras, context.getPackageName(), clz.getName(), options);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivity(context, extras, context.getPackageName(), clz.getName(), getOptionsBundle(context, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16 && (context instanceof Activity)) {
                ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Activity activity, Class<? extends Activity> clz) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivity((Context) activity, extras, activity.getPackageName(), clz.getName(), (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Activity activity, Class<? extends Activity> clz, Bundle options) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivity((Context) activity, extras, activity.getPackageName(), clz.getName(), options);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Activity activity, Class<? extends Activity> clz, View... sharedElements) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivity((Context) activity, extras, activity.getPackageName(), clz.getName(), getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Activity activity, Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivity((Context) activity, extras, activity.getPackageName(), clz.getName(), getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(String pkg, String cls) {
        if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity(Utils.getTopActivityOrApp(), (Bundle) null, pkg, cls, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(String pkg, String cls, Bundle options) {
        if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity(Utils.getTopActivityOrApp(), (Bundle) null, pkg, cls, options);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(String pkg, String cls, int enterAnim, int exitAnim) {
        if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivity(context, (Bundle) null, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16 && (context instanceof Activity)) {
                ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, String pkg, String cls) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity((Context) activity, (Bundle) null, pkg, cls, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, String pkg, String cls, Bundle options) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity((Context) activity, (Bundle) null, pkg, cls, options);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, String pkg, String cls, View... sharedElements) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity((Context) activity, (Bundle) null, pkg, cls, getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, String pkg, String cls, int enterAnim, int exitAnim) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity((Context) activity, (Bundle) null, pkg, cls, getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, String pkg, String cls) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity(Utils.getTopActivityOrApp(), extras, pkg, cls, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#2 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, String pkg, String cls, Bundle options) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity(Utils.getTopActivityOrApp(), extras, pkg, cls, options);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, String pkg, String cls, int enterAnim, int exitAnim) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivity(context, extras, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16 && (context instanceof Activity)) {
                ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Activity activity, String pkg, String cls) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity((Context) activity, extras, pkg, cls, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Activity activity, String pkg, String cls, Bundle options) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity((Context) activity, extras, pkg, cls, options);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Activity activity, String pkg, String cls, View... sharedElements) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity((Context) activity, extras, pkg, cls, getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Bundle extras, Activity activity, String pkg, String cls, int enterAnim, int exitAnim) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivity((Context) activity, extras, pkg, cls, getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static boolean startActivity(Intent intent) {
        if (intent != null) {
            return startActivity(intent, Utils.getTopActivityOrApp(), (Bundle) null);
        }
        throw new NullPointerException("Argument 'intent' of type Intent (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean startActivity(Intent intent, Bundle options) {
        if (intent != null) {
            return startActivity(intent, Utils.getTopActivityOrApp(), options);
        }
        throw new NullPointerException("Argument 'intent' of type Intent (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean startActivity(Intent intent, int enterAnim, int exitAnim) {
        if (intent != null) {
            Context context = Utils.getTopActivityOrApp();
            boolean isSuccess = startActivity(intent, context, getOptionsBundle(context, enterAnim, exitAnim));
            if (isSuccess && Build.VERSION.SDK_INT < 16 && (context instanceof Activity)) {
                ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
            }
            return isSuccess;
        }
        throw new NullPointerException("Argument 'intent' of type Intent (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void startActivity(Activity activity, Intent intent) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivity(intent, (Context) activity, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, Intent intent, Bundle options) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivity(intent, (Context) activity, options);
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, Intent intent, View... sharedElements) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivity(intent, (Context) activity, getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivity(Activity activity, Intent intent, int enterAnim, int exitAnim) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivity(intent, (Context) activity, getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> clz, int requestCode) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(activity, (Bundle) null, activity.getPackageName(), clz.getName(), requestCode, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> clz, int requestCode, Bundle options) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(activity, (Bundle) null, activity.getPackageName(), clz.getName(), requestCode, options);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> clz, int requestCode, View... sharedElements) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(activity, (Bundle) null, activity.getPackageName(), clz.getName(), requestCode, getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Activity activity, Class<? extends Activity> clz, int requestCode, int enterAnim, int exitAnim) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(activity, (Bundle) null, activity.getPackageName(), clz.getName(), requestCode, getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Activity activity, Class<? extends Activity> clz, int requestCode) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(), requestCode, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Activity activity, Class<? extends Activity> clz, int requestCode, Bundle options) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(), requestCode, options);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Activity activity, Class<? extends Activity> clz, int requestCode, View... sharedElements) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(), requestCode, getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Activity activity, Class<? extends Activity> clz, int requestCode, int enterAnim, int exitAnim) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(), requestCode, getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Activity activity, String pkg, String cls, int requestCode) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivityForResult(activity, extras, pkg, cls, requestCode, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Activity activity, String pkg, String cls, int requestCode, Bundle options) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivityForResult(activity, extras, pkg, cls, requestCode, options);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Activity activity, String pkg, String cls, int requestCode, View... sharedElements) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivityForResult(activity, extras, pkg, cls, requestCode, getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Activity activity, String pkg, String cls, int requestCode, int enterAnim, int exitAnim) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#1 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivityForResult(activity, extras, pkg, cls, requestCode, getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivityForResult(intent, activity, requestCode, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, Bundle options) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivityForResult(intent, activity, requestCode, options);
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, View... sharedElements) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivityForResult(intent, activity, requestCode, getOptionsBundle(activity, sharedElements));
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, int enterAnim, int exitAnim) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivityForResult(intent, activity, requestCode, getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> clz, int requestCode) {
        if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(fragment, (Bundle) null, Utils.getApp().getPackageName(), clz.getName(), requestCode, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> clz, int requestCode, Bundle options) {
        if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(fragment, (Bundle) null, Utils.getApp().getPackageName(), clz.getName(), requestCode, options);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> clz, int requestCode, View... sharedElements) {
        if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(fragment, (Bundle) null, Utils.getApp().getPackageName(), clz.getName(), requestCode, getOptionsBundle(fragment, sharedElements));
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> clz, int requestCode, int enterAnim, int exitAnim) {
        if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(fragment, (Bundle) null, Utils.getApp().getPackageName(), clz.getName(), requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, Class<? extends Activity> clz, int requestCode) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(fragment, extras, Utils.getApp().getPackageName(), clz.getName(), requestCode, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, Class<? extends Activity> clz, int requestCode, Bundle options) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(fragment, extras, Utils.getApp().getPackageName(), clz.getName(), requestCode, options);
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, Class<? extends Activity> clz, int requestCode, View... sharedElements) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(fragment, extras, Utils.getApp().getPackageName(), clz.getName(), requestCode, getOptionsBundle(fragment, sharedElements));
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, Class<? extends Activity> clz, int requestCode, int enterAnim, int exitAnim) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (clz != null) {
            startActivityForResult(fragment, extras, Utils.getApp().getPackageName(), clz.getName(), requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
        } else {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#2 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, String pkg, String cls, int requestCode) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivityForResult(fragment, extras, pkg, cls, requestCode, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, String pkg, String cls, int requestCode, Bundle options) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivityForResult(fragment, extras, pkg, cls, requestCode, options);
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, String pkg, String cls, int requestCode, View... sharedElements) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivityForResult(fragment, extras, pkg, cls, requestCode, getOptionsBundle(fragment, sharedElements));
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 6, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Bundle extras, Fragment fragment, String pkg, String cls, int requestCode, int enterAnim, int exitAnim) {
        if (extras == null) {
            throw new NullPointerException("Argument 'extras' of type Bundle (#0 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#1 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (pkg == null) {
            throw new NullPointerException("Argument 'pkg' of type String (#2 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (cls != null) {
            startActivityForResult(fragment, extras, pkg, cls, requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
        } else {
            throw new NullPointerException("Argument 'cls' of type String (#3 out of 7, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode) {
        if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivityForResult(intent, fragment, requestCode, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivityForResult(intent, fragment, requestCode, options);
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode, View... sharedElements) {
        if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivityForResult(intent, fragment, requestCode, getOptionsBundle(fragment, sharedElements));
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, int requestCode, int enterAnim, int exitAnim) {
        if (fragment == null) {
            throw new NullPointerException("Argument 'fragment' of type Fragment (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intent != null) {
            startActivityForResult(intent, fragment, requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
        } else {
            throw new NullPointerException("Argument 'intent' of type Intent (#1 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivities(Intent[] intents) {
        if (intents != null) {
            startActivities(intents, Utils.getTopActivityOrApp(), (Bundle) null);
            return;
        }
        throw new NullPointerException("Argument 'intents' of type Intent[] (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void startActivities(Intent[] intents, Bundle options) {
        if (intents != null) {
            startActivities(intents, Utils.getTopActivityOrApp(), options);
            return;
        }
        throw new NullPointerException("Argument 'intents' of type Intent[] (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void startActivities(Intent[] intents, int enterAnim, int exitAnim) {
        if (intents != null) {
            Context context = Utils.getTopActivityOrApp();
            startActivities(intents, context, getOptionsBundle(context, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16 && (context instanceof Activity)) {
                ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
                return;
            }
            return;
        }
        throw new NullPointerException("Argument 'intents' of type Intent[] (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void startActivities(Activity activity, Intent[] intents) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intents != null) {
            startActivities(intents, (Context) activity, (Bundle) null);
        } else {
            throw new NullPointerException("Argument 'intents' of type Intent[] (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivities(Activity activity, Intent[] intents, Bundle options) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intents != null) {
            startActivities(intents, (Context) activity, options);
        } else {
            throw new NullPointerException("Argument 'intents' of type Intent[] (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startActivities(Activity activity, Intent[] intents, int enterAnim, int exitAnim) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (intents != null) {
            startActivities(intents, (Context) activity, getOptionsBundle((Context) activity, enterAnim, exitAnim));
            if (Build.VERSION.SDK_INT < 16) {
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        } else {
            throw new NullPointerException("Argument 'intents' of type Intent[] (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void startHomeActivity() throws SecurityException {
        Intent homeIntent = new Intent("android.intent.action.MAIN");
        homeIntent.addCategory("android.intent.category.HOME");
        homeIntent.setFlags(C.ENCODING_PCM_MU_LAW);
        startActivity(homeIntent);
    }

    public static void startLauncherActivity() {
        startLauncherActivity(Utils.getApp().getPackageName());
    }

    public static void startLauncherActivity(String pkg) {
        if (pkg != null) {
            String launcherActivity = getLauncherActivity(pkg);
            if (!TextUtils.isEmpty(launcherActivity)) {
                startActivity(pkg, launcherActivity);
                return;
            }
            return;
        }
        throw new NullPointerException("Argument 'pkg' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static List<Activity> getActivityList() {
        return Utils.getActivityList();
    }

    public static String getLauncherActivity() {
        return getLauncherActivity(Utils.getApp().getPackageName());
    }

    public static String getLauncherActivity(String pkg) {
        if (pkg != null) {
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(pkg);
            List<ResolveInfo> info = Utils.getApp().getPackageManager().queryIntentActivities(intent, 0);
            int size = info.size();
            if (size == 0) {
                return "";
            }
            for (int i = 0; i < size; i++) {
                ResolveInfo ri = info.get(i);
                if (ri.activityInfo.processName.equals(pkg)) {
                    return ri.activityInfo.name;
                }
            }
            return info.get(0).activityInfo.name;
        }
        throw new NullPointerException("Argument 'pkg' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static List<String> getMainActivities() {
        return getMainActivities(Utils.getApp().getPackageName());
    }

    public static List<String> getMainActivities(String pkg) {
        if (pkg != null) {
            List<String> ret = new ArrayList<>();
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.setPackage(pkg);
            List<ResolveInfo> info = Utils.getApp().getPackageManager().queryIntentActivities(intent, 0);
            int size = info.size();
            if (size == 0) {
                return ret;
            }
            for (int i = 0; i < size; i++) {
                ResolveInfo ri = info.get(i);
                if (ri.activityInfo.processName.equals(pkg)) {
                    ret.add(ri.activityInfo.name);
                }
            }
            return ret;
        }
        throw new NullPointerException("Argument 'pkg' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Activity getTopActivity() {
        return Utils.getActivityLifecycle().getTopActivity();
    }

    public static boolean isActivityAlive(Context context) {
        return isActivityAlive(getActivityByContext(context));
    }

    public static boolean isActivityAlive(Activity activity) {
        return activity != null && !activity.isFinishing() && (Build.VERSION.SDK_INT < 17 || !activity.isDestroyed());
    }

    public static boolean isActivityExistsInStack(Activity activity) {
        if (activity != null) {
            for (Activity aActivity : Utils.getActivityList()) {
                if (aActivity.equals(activity)) {
                    return true;
                }
            }
            return false;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isActivityExistsInStack(Class<? extends Activity> clz) {
        if (clz != null) {
            for (Activity aActivity : Utils.getActivityList()) {
                if (aActivity.getClass().equals(clz)) {
                    return true;
                }
            }
            return false;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishActivity(Activity activity) {
        if (activity != null) {
            finishActivity(activity, false);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishActivity(Activity activity, boolean isLoadAnim) {
        if (activity != null) {
            activity.finish();
            if (!isLoadAnim) {
                activity.overridePendingTransition(0, 0);
                return;
            }
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishActivity(Activity activity, int enterAnim, int exitAnim) {
        if (activity != null) {
            activity.finish();
            activity.overridePendingTransition(enterAnim, exitAnim);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishActivity(Class<? extends Activity> clz) {
        if (clz != null) {
            finishActivity(clz, false);
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishActivity(Class<? extends Activity> clz, boolean isLoadAnim) {
        if (clz != null) {
            for (Activity activity : Utils.getActivityList()) {
                if (activity.getClass().equals(clz)) {
                    activity.finish();
                    if (!isLoadAnim) {
                        activity.overridePendingTransition(0, 0);
                    }
                }
            }
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishActivity(Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        if (clz != null) {
            for (Activity activity : Utils.getActivityList()) {
                if (activity.getClass().equals(clz)) {
                    activity.finish();
                    activity.overridePendingTransition(enterAnim, exitAnim);
                }
            }
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean finishToActivity(Activity activity, boolean isIncludeSelf) {
        if (activity != null) {
            return finishToActivity(activity, isIncludeSelf, false);
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean finishToActivity(Activity activity, boolean isIncludeSelf, boolean isLoadAnim) {
        if (activity != null) {
            List<Activity> activities = Utils.getActivityList();
            for (int i = activities.size() - 1; i >= 0; i--) {
                Activity aActivity = activities.get(i);
                if (aActivity.equals(activity)) {
                    if (isIncludeSelf) {
                        finishActivity(aActivity, isLoadAnim);
                    }
                    return true;
                }
                finishActivity(aActivity, isLoadAnim);
            }
            return false;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean finishToActivity(Activity activity, boolean isIncludeSelf, int enterAnim, int exitAnim) {
        if (activity != null) {
            List<Activity> activities = Utils.getActivityList();
            for (int i = activities.size() - 1; i >= 0; i--) {
                Activity aActivity = activities.get(i);
                if (aActivity.equals(activity)) {
                    if (isIncludeSelf) {
                        finishActivity(aActivity, enterAnim, exitAnim);
                    }
                    return true;
                }
                finishActivity(aActivity, enterAnim, exitAnim);
            }
            return false;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean finishToActivity(Class<? extends Activity> clz, boolean isIncludeSelf) {
        if (clz != null) {
            return finishToActivity(clz, isIncludeSelf, false);
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean finishToActivity(Class<? extends Activity> clz, boolean isIncludeSelf, boolean isLoadAnim) {
        if (clz != null) {
            List<Activity> activities = Utils.getActivityList();
            for (int i = activities.size() - 1; i >= 0; i--) {
                Activity aActivity = activities.get(i);
                if (aActivity.getClass().equals(clz)) {
                    if (isIncludeSelf) {
                        finishActivity(aActivity, isLoadAnim);
                    }
                    return true;
                }
                finishActivity(aActivity, isLoadAnim);
            }
            return false;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean finishToActivity(Class<? extends Activity> clz, boolean isIncludeSelf, int enterAnim, int exitAnim) {
        if (clz != null) {
            List<Activity> activities = Utils.getActivityList();
            for (int i = activities.size() - 1; i >= 0; i--) {
                Activity aActivity = activities.get(i);
                if (aActivity.getClass().equals(clz)) {
                    if (isIncludeSelf) {
                        finishActivity(aActivity, enterAnim, exitAnim);
                    }
                    return true;
                }
                finishActivity(aActivity, enterAnim, exitAnim);
            }
            return false;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishOtherActivities(Class<? extends Activity> clz) {
        if (clz != null) {
            finishOtherActivities(clz, false);
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishOtherActivities(Class<? extends Activity> clz, boolean isLoadAnim) {
        if (clz != null) {
            List<Activity> activities = Utils.getActivityList();
            for (int i = activities.size() - 1; i >= 0; i--) {
                Activity activity = activities.get(i);
                if (!activity.getClass().equals(clz)) {
                    finishActivity(activity, isLoadAnim);
                }
            }
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishOtherActivities(Class<? extends Activity> clz, int enterAnim, int exitAnim) {
        if (clz != null) {
            List<Activity> activities = Utils.getActivityList();
            for (int i = activities.size() - 1; i >= 0; i--) {
                Activity activity = activities.get(i);
                if (!activity.getClass().equals(clz)) {
                    finishActivity(activity, enterAnim, exitAnim);
                }
            }
            return;
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void finishAllActivities() {
        finishAllActivities(false);
    }

    public static void finishAllActivities(boolean isLoadAnim) {
        List<Activity> activityList = Utils.getActivityList();
        for (int i = activityList.size() - 1; i >= 0; i--) {
            Activity activity = activityList.get(i);
            activity.finish();
            if (!isLoadAnim) {
                activity.overridePendingTransition(0, 0);
            }
        }
    }

    public static void finishAllActivities(int enterAnim, int exitAnim) {
        List<Activity> activityList = Utils.getActivityList();
        for (int i = activityList.size() - 1; i >= 0; i--) {
            Activity activity = activityList.get(i);
            activity.finish();
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    public static void finishAllActivitiesExceptNewest() {
        finishAllActivitiesExceptNewest(false);
    }

    public static void finishAllActivitiesExceptNewest(boolean isLoadAnim) {
        List<Activity> activities = Utils.getActivityList();
        for (int i = activities.size() - 2; i >= 0; i--) {
            finishActivity(activities.get(i), isLoadAnim);
        }
    }

    public static void finishAllActivitiesExceptNewest(int enterAnim, int exitAnim) {
        List<Activity> activities = Utils.getActivityList();
        for (int i = activities.size() - 2; i >= 0; i--) {
            finishActivity(activities.get(i), enterAnim, exitAnim);
        }
    }

    public static Drawable getActivityIcon(Activity activity) {
        if (activity != null) {
            return getActivityIcon(activity.getComponentName());
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Drawable getActivityIcon(Class<? extends Activity> clz) {
        if (clz != null) {
            return getActivityIcon(new ComponentName(Utils.getApp(), clz));
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Drawable getActivityIcon(ComponentName activityName) {
        if (activityName != null) {
            try {
                return Utils.getApp().getPackageManager().getActivityIcon(activityName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new NullPointerException("Argument 'activityName' of type ComponentName (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static Drawable getActivityLogo(Activity activity) {
        if (activity != null) {
            return getActivityLogo(activity.getComponentName());
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Drawable getActivityLogo(Class<? extends Activity> clz) {
        if (clz != null) {
            return getActivityLogo(new ComponentName(Utils.getApp(), clz));
        }
        throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Drawable getActivityLogo(ComponentName activityName) {
        if (activityName != null) {
            try {
                return Utils.getApp().getPackageManager().getActivityLogo(activityName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new NullPointerException("Argument 'activityName' of type ComponentName (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    private static void startActivity(Context context, Bundle extras, String pkg, String cls, Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        startActivity(intent, context, options);
    }

    private static boolean startActivity(Intent intent, Context context, Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(C.ENCODING_PCM_MU_LAW);
        }
        if (options == null || Build.VERSION.SDK_INT < 16) {
            context.startActivity(intent);
            return true;
        }
        context.startActivity(intent, options);
        return true;
    }

    private static boolean isIntentAvailable(Intent intent) {
        return Utils.getApp().getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
    }

    private static boolean startActivityForResult(Activity activity, Bundle extras, String pkg, String cls, int requestCode, Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        return startActivityForResult(intent, activity, requestCode, options);
    }

    private static boolean startActivityForResult(Intent intent, Activity activity, int requestCode, Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        } else if (options == null || Build.VERSION.SDK_INT < 16) {
            activity.startActivityForResult(intent, requestCode);
            return true;
        } else {
            activity.startActivityForResult(intent, requestCode, options);
            return true;
        }
    }

    private static void startActivities(Intent[] intents, Context context, Bundle options) {
        if (!(context instanceof Activity)) {
            for (Intent intent : intents) {
                intent.addFlags(C.ENCODING_PCM_MU_LAW);
            }
        }
        if (options == null || Build.VERSION.SDK_INT < 16) {
            context.startActivities(intents);
        } else {
            context.startActivities(intents, options);
        }
    }

    private static boolean startActivityForResult(Fragment fragment, Bundle extras, String pkg, String cls, int requestCode, Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        return startActivityForResult(intent, fragment, requestCode, options);
    }

    private static boolean startActivityForResult(Intent intent, Fragment fragment, int requestCode, Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        } else if (fragment.getActivity() == null) {
            Log.e("ActivityUtils", "Fragment " + fragment + " not attached to Activity");
            return false;
        } else if (options == null || Build.VERSION.SDK_INT < 16) {
            fragment.startActivityForResult(intent, requestCode);
            return true;
        } else {
            fragment.startActivityForResult(intent, requestCode, options);
            return true;
        }
    }

    private static Bundle getOptionsBundle(Fragment fragment, int enterAnim, int exitAnim) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return null;
        }
        return ActivityOptionsCompat.makeCustomAnimation(activity, enterAnim, exitAnim).toBundle();
    }

    private static Bundle getOptionsBundle(Context context, int enterAnim, int exitAnim) {
        return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle();
    }

    private static Bundle getOptionsBundle(Fragment fragment, View[] sharedElements) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return null;
        }
        return getOptionsBundle(activity, sharedElements);
    }

    private static Bundle getOptionsBundle(Activity activity, View[] sharedElements) {
        int len;
        if (Build.VERSION.SDK_INT < 21 || sharedElements == null || (len = sharedElements.length) <= 0) {
            return null;
        }
        Pair<View, String>[] pairs = new Pair[len];
        for (int i = 0; i < len; i++) {
            pairs[i] = Pair.create(sharedElements[i], sharedElements[i].getTransitionName());
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs).toBundle();
    }
}

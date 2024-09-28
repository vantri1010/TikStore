package im.bclpbkiauv.messenger.browser;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.support.customtabs.CustomTabsCallback;
import im.bclpbkiauv.messenger.support.customtabs.CustomTabsClient;
import im.bclpbkiauv.messenger.support.customtabs.CustomTabsServiceConnection;
import im.bclpbkiauv.messenger.support.customtabs.CustomTabsSession;
import im.bclpbkiauv.messenger.support.customtabsclient.shared.CustomTabsHelper;
import im.bclpbkiauv.messenger.support.customtabsclient.shared.ServiceConnection;
import im.bclpbkiauv.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import java.lang.ref.WeakReference;

public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    /* access modifiers changed from: private */
    public static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    private static CustomTabsSession getCurrentSession() {
        WeakReference<CustomTabsSession> weakReference = customTabsCurrentSession;
        if (weakReference == null) {
            return null;
        }
        return (CustomTabsSession) weakReference.get();
    }

    private static void setCurrentSession(CustomTabsSession session) {
        customTabsCurrentSession = new WeakReference<>(session);
    }

    private static CustomTabsSession getSession() {
        CustomTabsClient customTabsClient2 = customTabsClient;
        if (customTabsClient2 == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            CustomTabsSession newSession = customTabsClient2.newSession(new NavigationCallback());
            customTabsSession = newSession;
            setCurrentSession(newSession);
        }
        return customTabsSession;
    }

    public static void bindCustomTabsService(Activity activity) {
        WeakReference<Activity> weakReference = currentCustomTabsActivity;
        Activity currentActivity = weakReference == null ? null : (Activity) weakReference.get();
        if (!(currentActivity == null || currentActivity == activity)) {
            unbindCustomTabsService(currentActivity);
        }
        if (customTabsClient == null) {
            currentCustomTabsActivity = new WeakReference<>(activity);
            try {
                if (TextUtils.isEmpty(customTabsPackageToBind)) {
                    String packageNameToUse = CustomTabsHelper.getPackageNameToUse(activity);
                    customTabsPackageToBind = packageNameToUse;
                    if (packageNameToUse == null) {
                        return;
                    }
                }
                ServiceConnection serviceConnection = new ServiceConnection(new ServiceConnectionCallback() {
                    public void onServiceConnected(CustomTabsClient client) {
                        CustomTabsClient unused = Browser.customTabsClient = client;
                        if (SharedConfig.customTabs && Browser.customTabsClient != null) {
                            try {
                                Browser.customTabsClient.warmup(0);
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        }
                    }

                    public void onServiceDisconnected() {
                        CustomTabsClient unused = Browser.customTabsClient = null;
                    }
                });
                customTabsServiceConnection = serviceConnection;
                if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, serviceConnection)) {
                    customTabsServiceConnection = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (customTabsServiceConnection != null) {
            WeakReference<Activity> weakReference = currentCustomTabsActivity;
            if ((weakReference == null ? null : (Activity) weakReference.get()) == activity) {
                currentCustomTabsActivity.clear();
            }
            try {
                activity.unbindService(customTabsServiceConnection);
            } catch (Exception e) {
            }
            customTabsClient = null;
            customTabsSession = null;
        }
    }

    private static class NavigationCallback extends CustomTabsCallback {
        private NavigationCallback() {
        }

        public void onNavigationEvent(int navigationEvent, Bundle extras) {
        }
    }

    public static void openUrl(Context context, String url) {
        if (url != null) {
            openUrl(context, Uri.parse(url), true);
        }
    }

    public static void openUrl(Context context, Uri uri) {
        openUrl(context, uri, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom) {
        if (context != null && url != null) {
            openUrl(context, Uri.parse(url), allowCustom);
        }
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom) {
        openUrl(context, uri, allowCustom, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom, boolean tryTelegraph) {
        openUrl(context, Uri.parse(url), allowCustom, tryTelegraph);
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x025e A[Catch:{ Exception -> 0x0283 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void openUrl(android.content.Context r17, android.net.Uri r18, boolean r19, boolean r20) {
        /*
            r7 = r17
            r8 = r18
            java.lang.String r9 = "android.intent.action.VIEW"
            if (r7 == 0) goto L_0x0288
            if (r8 != 0) goto L_0x000c
            goto L_0x0288
        L_0x000c:
            int r10 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            r11 = 1
            boolean[] r0 = new boolean[r11]
            r12 = 0
            r0[r12] = r12
            r13 = r0
            boolean r14 = isInternalUri(r8, r13)
            if (r20 == 0) goto L_0x007e
            java.lang.String r0 = r18.getHost()     // Catch:{ Exception -> 0x007d }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x007d }
            java.lang.String r1 = "telegra.ph"
            boolean r1 = r0.equals(r1)     // Catch:{ Exception -> 0x007d }
            if (r1 != 0) goto L_0x003e
            java.lang.String r1 = r18.toString()     // Catch:{ Exception -> 0x007d }
            java.lang.String r1 = r1.toLowerCase()     // Catch:{ Exception -> 0x007d }
            java.lang.String r2 = "m12345.com/faq"
            boolean r1 = r1.contains(r2)     // Catch:{ Exception -> 0x007d }
            if (r1 == 0) goto L_0x003d
            goto L_0x003e
        L_0x003d:
            goto L_0x007e
        L_0x003e:
            im.bclpbkiauv.ui.actionbar.AlertDialog[] r1 = new im.bclpbkiauv.ui.actionbar.AlertDialog[r11]     // Catch:{ Exception -> 0x007d }
            im.bclpbkiauv.ui.actionbar.AlertDialog r2 = new im.bclpbkiauv.ui.actionbar.AlertDialog     // Catch:{ Exception -> 0x007d }
            r3 = 3
            r2.<init>(r7, r3)     // Catch:{ Exception -> 0x007d }
            r1[r12] = r2     // Catch:{ Exception -> 0x007d }
            r15 = r1
            r4 = r18
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_getWebPagePreview r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getWebPagePreview     // Catch:{ Exception -> 0x007d }
            r1.<init>()     // Catch:{ Exception -> 0x007d }
            r6 = r1
            java.lang.String r1 = r18.toString()     // Catch:{ Exception -> 0x007d }
            r6.message = r1     // Catch:{ Exception -> 0x007d }
            int r1 = im.bclpbkiauv.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x007d }
            im.bclpbkiauv.tgnet.ConnectionsManager r5 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r1)     // Catch:{ Exception -> 0x007d }
            im.bclpbkiauv.messenger.browser.-$$Lambda$Browser$R_raZNRZpinrVRTLCxRxiOF5Hp8 r3 = new im.bclpbkiauv.messenger.browser.-$$Lambda$Browser$R_raZNRZpinrVRTLCxRxiOF5Hp8     // Catch:{ Exception -> 0x007d }
            r1 = r3
            r2 = r15
            r11 = r3
            r3 = r10
            r12 = r5
            r5 = r17
            r16 = r0
            r0 = r6
            r6 = r19
            r1.<init>(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x007d }
            int r1 = r12.sendRequest(r0, r11)     // Catch:{ Exception -> 0x007d }
            im.bclpbkiauv.messenger.browser.-$$Lambda$Browser$YWawQCxTp-4oTnnx1hI5S_-EAa8 r2 = new im.bclpbkiauv.messenger.browser.-$$Lambda$Browser$YWawQCxTp-4oTnnx1hI5S_-EAa8     // Catch:{ Exception -> 0x007d }
            r2.<init>(r15, r1)     // Catch:{ Exception -> 0x007d }
            r5 = 1000(0x3e8, double:4.94E-321)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r2, r5)     // Catch:{ Exception -> 0x007d }
            return
        L_0x007d:
            r0 = move-exception
        L_0x007e:
            java.lang.String r0 = r18.getScheme()     // Catch:{ Exception -> 0x0253 }
            if (r0 == 0) goto L_0x008d
            java.lang.String r0 = r18.getScheme()     // Catch:{ Exception -> 0x0253 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0253 }
            goto L_0x008f
        L_0x008d:
            java.lang.String r0 = ""
        L_0x008f:
            r1 = r0
            java.lang.String r0 = "http"
            boolean r0 = r0.equals(r1)     // Catch:{ Exception -> 0x0253 }
            if (r0 != 0) goto L_0x00a0
            java.lang.String r0 = "https"
            boolean r0 = r0.equals(r1)     // Catch:{ Exception -> 0x0253 }
            if (r0 == 0) goto L_0x00ac
        L_0x00a0:
            android.net.Uri r0 = r18.normalizeScheme()     // Catch:{ Exception -> 0x00a6 }
            r8 = r0
            goto L_0x00ac
        L_0x00a6:
            r0 = move-exception
            r2 = r0
            r0 = r2
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0253 }
        L_0x00ac:
            java.lang.String r0 = r8.getHost()     // Catch:{ Exception -> 0x0251 }
            r2 = r0
            if (r19 == 0) goto L_0x024e
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.customTabs     // Catch:{ Exception -> 0x0251 }
            if (r0 == 0) goto L_0x024e
            if (r14 != 0) goto L_0x024e
            java.lang.String r0 = "tel"
            boolean r0 = r1.equals(r0)     // Catch:{ Exception -> 0x0251 }
            if (r0 != 0) goto L_0x024e
            if (r2 == 0) goto L_0x024e
            java.lang.String r0 = "www.shareinstall.com.cn"
            boolean r0 = r0.equals(r2)     // Catch:{ Exception -> 0x0251 }
            if (r0 != 0) goto L_0x024e
            r3 = 0
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0122 }
            java.lang.String r4 = "http://www.google.com"
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0122 }
            r0.<init>(r9, r4)     // Catch:{ Exception -> 0x0122 }
            android.content.pm.PackageManager r4 = r17.getPackageManager()     // Catch:{ Exception -> 0x0122 }
            r5 = 0
            java.util.List r4 = r4.queryIntentActivities(r0, r5)     // Catch:{ Exception -> 0x0122 }
            if (r4 == 0) goto L_0x0121
            boolean r5 = r4.isEmpty()     // Catch:{ Exception -> 0x0122 }
            if (r5 != 0) goto L_0x0121
            int r5 = r4.size()     // Catch:{ Exception -> 0x0122 }
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ Exception -> 0x0122 }
            r3 = r5
            r5 = 0
        L_0x00f2:
            int r6 = r4.size()     // Catch:{ Exception -> 0x0122 }
            if (r5 >= r6) goto L_0x0121
            java.lang.Object r6 = r4.get(r5)     // Catch:{ Exception -> 0x0122 }
            android.content.pm.ResolveInfo r6 = (android.content.pm.ResolveInfo) r6     // Catch:{ Exception -> 0x0122 }
            android.content.pm.ActivityInfo r6 = r6.activityInfo     // Catch:{ Exception -> 0x0122 }
            java.lang.String r6 = r6.packageName     // Catch:{ Exception -> 0x0122 }
            r3[r5] = r6     // Catch:{ Exception -> 0x0122 }
            boolean r6 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0122 }
            if (r6 == 0) goto L_0x011e
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0122 }
            r6.<init>()     // Catch:{ Exception -> 0x0122 }
            java.lang.String r11 = "default browser name = "
            r6.append(r11)     // Catch:{ Exception -> 0x0122 }
            r11 = r3[r5]     // Catch:{ Exception -> 0x0122 }
            r6.append(r11)     // Catch:{ Exception -> 0x0122 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0122 }
            im.bclpbkiauv.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0122 }
        L_0x011e:
            int r5 = r5 + 1
            goto L_0x00f2
        L_0x0121:
            goto L_0x0123
        L_0x0122:
            r0 = move-exception
        L_0x0123:
            r4 = 0
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x01d3 }
            r0.<init>(r9, r8)     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.PackageManager r5 = r17.getPackageManager()     // Catch:{ Exception -> 0x01d3 }
            r6 = 0
            java.util.List r5 = r5.queryIntentActivities(r0, r6)     // Catch:{ Exception -> 0x01d3 }
            r4 = r5
            if (r3 == 0) goto L_0x015f
            r5 = 0
        L_0x0136:
            int r6 = r4.size()     // Catch:{ Exception -> 0x01d3 }
            if (r5 >= r6) goto L_0x015e
            r6 = 0
        L_0x013d:
            int r11 = r3.length     // Catch:{ Exception -> 0x01d3 }
            if (r6 >= r11) goto L_0x015b
            r11 = r3[r6]     // Catch:{ Exception -> 0x01d3 }
            java.lang.Object r12 = r4.get(r5)     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.ResolveInfo r12 = (android.content.pm.ResolveInfo) r12     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.ActivityInfo r12 = r12.activityInfo     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r12 = r12.packageName     // Catch:{ Exception -> 0x01d3 }
            boolean r11 = r11.equals(r12)     // Catch:{ Exception -> 0x01d3 }
            if (r11 == 0) goto L_0x0158
            r4.remove(r5)     // Catch:{ Exception -> 0x01d3 }
            int r5 = r5 + -1
            goto L_0x015b
        L_0x0158:
            int r6 = r6 + 1
            goto L_0x013d
        L_0x015b:
            r6 = 1
            int r5 = r5 + r6
            goto L_0x0136
        L_0x015e:
            goto L_0x019a
        L_0x015f:
            r5 = 0
        L_0x0160:
            int r6 = r4.size()     // Catch:{ Exception -> 0x01d3 }
            if (r5 >= r6) goto L_0x019a
            java.lang.Object r6 = r4.get(r5)     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.ResolveInfo r6 = (android.content.pm.ResolveInfo) r6     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.ActivityInfo r6 = r6.activityInfo     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r6 = r6.packageName     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r6 = r6.toLowerCase()     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r11 = "browser"
            boolean r6 = r6.contains(r11)     // Catch:{ Exception -> 0x01d3 }
            if (r6 != 0) goto L_0x0192
            java.lang.Object r6 = r4.get(r5)     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.ResolveInfo r6 = (android.content.pm.ResolveInfo) r6     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.ActivityInfo r6 = r6.activityInfo     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r6 = r6.packageName     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r6 = r6.toLowerCase()     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r11 = "chrome"
            boolean r6 = r6.contains(r11)     // Catch:{ Exception -> 0x01d3 }
            if (r6 == 0) goto L_0x0197
        L_0x0192:
            r4.remove(r5)     // Catch:{ Exception -> 0x01d3 }
            int r5 = r5 + -1
        L_0x0197:
            r6 = 1
            int r5 = r5 + r6
            goto L_0x0160
        L_0x019a:
            boolean r5 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01d3 }
            if (r5 == 0) goto L_0x01d2
            r5 = 0
        L_0x019f:
            int r6 = r4.size()     // Catch:{ Exception -> 0x01d3 }
            if (r5 >= r6) goto L_0x01d2
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01d3 }
            r6.<init>()     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r11 = "device has "
            r6.append(r11)     // Catch:{ Exception -> 0x01d3 }
            java.lang.Object r11 = r4.get(r5)     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.ResolveInfo r11 = (android.content.pm.ResolveInfo) r11     // Catch:{ Exception -> 0x01d3 }
            android.content.pm.ActivityInfo r11 = r11.activityInfo     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r11 = r11.packageName     // Catch:{ Exception -> 0x01d3 }
            r6.append(r11)     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r11 = " to open "
            r6.append(r11)     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r11 = r8.toString()     // Catch:{ Exception -> 0x01d3 }
            r6.append(r11)     // Catch:{ Exception -> 0x01d3 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x01d3 }
            im.bclpbkiauv.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x01d3 }
            int r5 = r5 + 1
            goto L_0x019f
        L_0x01d2:
            goto L_0x01d4
        L_0x01d3:
            r0 = move-exception
        L_0x01d4:
            r5 = 0
            boolean r0 = r13[r5]     // Catch:{ Exception -> 0x0251 }
            if (r0 != 0) goto L_0x01e1
            if (r4 == 0) goto L_0x01e1
            boolean r0 = r4.isEmpty()     // Catch:{ Exception -> 0x0251 }
            if (r0 == 0) goto L_0x0250
        L_0x01e1:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0251 }
            android.content.Context r5 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0251 }
            java.lang.Class<im.bclpbkiauv.messenger.ShareBroadcastReceiver> r6 = im.bclpbkiauv.messenger.ShareBroadcastReceiver.class
            r0.<init>(r5, r6)     // Catch:{ Exception -> 0x0251 }
            java.lang.String r5 = "android.intent.action.SEND"
            r0.setAction(r5)     // Catch:{ Exception -> 0x0251 }
            android.content.Context r5 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0251 }
            android.content.Intent r6 = new android.content.Intent     // Catch:{ Exception -> 0x0251 }
            android.content.Context r11 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0251 }
            java.lang.Class<im.bclpbkiauv.messenger.CustomTabsCopyReceiver> r12 = im.bclpbkiauv.messenger.CustomTabsCopyReceiver.class
            r6.<init>(r11, r12)     // Catch:{ Exception -> 0x0251 }
            r11 = 134217728(0x8000000, float:3.85186E-34)
            r12 = 0
            android.app.PendingIntent r5 = android.app.PendingIntent.getBroadcast(r5, r12, r6, r11)     // Catch:{ Exception -> 0x0251 }
            im.bclpbkiauv.messenger.support.customtabs.CustomTabsIntent$Builder r6 = new im.bclpbkiauv.messenger.support.customtabs.CustomTabsIntent$Builder     // Catch:{ Exception -> 0x0251 }
            im.bclpbkiauv.messenger.support.customtabs.CustomTabsSession r11 = getSession()     // Catch:{ Exception -> 0x0251 }
            r6.<init>(r11)     // Catch:{ Exception -> 0x0251 }
            java.lang.String r11 = "CopyLink"
            r12 = 2131690736(0x7f0f04f0, float:1.9010524E38)
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r12)     // Catch:{ Exception -> 0x0251 }
            r6.addMenuItem(r11, r5)     // Catch:{ Exception -> 0x0251 }
            java.lang.String r11 = "actionBarBrowser"
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)     // Catch:{ Exception -> 0x0251 }
            r6.setToolbarColor(r11)     // Catch:{ Exception -> 0x0251 }
            r11 = 1
            r6.setShowTitle(r11)     // Catch:{ Exception -> 0x0251 }
            android.content.res.Resources r11 = r17.getResources()     // Catch:{ Exception -> 0x0251 }
            r12 = 2131230758(0x7f080026, float:1.8077578E38)
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeResource(r11, r12)     // Catch:{ Exception -> 0x0251 }
            java.lang.String r12 = "ShareFile"
            r15 = 2131693926(0x7f0f1166, float:1.9016994E38)
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r15)     // Catch:{ Exception -> 0x0251 }
            android.content.Context r15 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0251 }
            r16 = r1
            r1 = 0
            android.app.PendingIntent r15 = android.app.PendingIntent.getBroadcast(r15, r1, r0, r1)     // Catch:{ Exception -> 0x0251 }
            r6.setActionButton(r11, r12, r15, r1)     // Catch:{ Exception -> 0x0251 }
            im.bclpbkiauv.messenger.support.customtabs.CustomTabsIntent r1 = r6.build()     // Catch:{ Exception -> 0x0251 }
            r1.setUseNewTask()     // Catch:{ Exception -> 0x0251 }
            r1.launchUrl(r7, r8)     // Catch:{ Exception -> 0x0251 }
            return
        L_0x024e:
            r16 = r1
        L_0x0250:
            goto L_0x0257
        L_0x0251:
            r0 = move-exception
            goto L_0x0254
        L_0x0253:
            r0 = move-exception
        L_0x0254:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0257:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0283 }
            r0.<init>(r9, r8)     // Catch:{ Exception -> 0x0283 }
            if (r14 == 0) goto L_0x0270
            android.content.ComponentName r1 = new android.content.ComponentName     // Catch:{ Exception -> 0x0283 }
            java.lang.String r2 = r17.getPackageName()     // Catch:{ Exception -> 0x0283 }
            java.lang.Class<im.bclpbkiauv.ui.LaunchActivity> r3 = im.bclpbkiauv.ui.LaunchActivity.class
            java.lang.String r3 = r3.getName()     // Catch:{ Exception -> 0x0283 }
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0283 }
            r0.setComponent(r1)     // Catch:{ Exception -> 0x0283 }
        L_0x0270:
            java.lang.String r1 = "create_new_tab"
            r2 = 1
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x0283 }
            java.lang.String r1 = "com.android.browser.application_id"
            java.lang.String r2 = r17.getPackageName()     // Catch:{ Exception -> 0x0283 }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x0283 }
            r7.startActivity(r0)     // Catch:{ Exception -> 0x0283 }
            goto L_0x0287
        L_0x0283:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0287:
            return
        L_0x0288:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.browser.Browser.openUrl(android.content.Context, android.net.Uri, boolean, boolean):void");
    }

    static /* synthetic */ void lambda$null$0(AlertDialog[] progressDialog, TLObject response, int currentAccount, Uri finalUri, Context context, boolean allowCustom) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        boolean ok = false;
        if (response instanceof TLRPC.TL_messageMediaWebPage) {
            TLRPC.TL_messageMediaWebPage webPage = (TLRPC.TL_messageMediaWebPage) response;
            if ((webPage.webpage instanceof TLRPC.TL_webPage) && webPage.webpage.cached_page != null) {
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.openArticle, webPage.webpage, finalUri.toString());
                ok = true;
            }
        }
        if (!ok) {
            openUrl(context, finalUri, allowCustom, false);
        }
    }

    static /* synthetic */ void lambda$openUrl$3(AlertDialog[] progressDialog, int reqId) {
        if (progressDialog[0] != null) {
            try {
                progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
                    private final /* synthetic */ int f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        ConnectionsManager.getInstance(UserConfig.selectedAccount).cancelRequest(this.f$0, true);
                    }
                });
                progressDialog[0].show();
            } catch (Exception e) {
            }
        }
    }

    public static boolean isPassportUrl(String url) {
        if (url == null) {
            return false;
        }
        try {
            String url2 = url.toLowerCase();
            if (url2.startsWith("hchat:passport") || url2.startsWith("hchat://passport") || url2.startsWith("hchat:secureid")) {
                return true;
            }
            if (!url2.contains("resolve") || !url2.contains("domain=hchatpassport")) {
                return false;
            }
            return true;
        } catch (Throwable th) {
        }
    }

    public static boolean isInternalUrl(String url, boolean[] forceBrowser) {
        return isInternalUri(Uri.parse(url), forceBrowser);
    }

    public static boolean isInternalUri(Uri uri, boolean[] forceBrowser) {
        String path;
        String host = uri.getHost();
        String host2 = host != null ? host.toLowerCase() : "";
        if ("hchat".equals(uri.getScheme()) || "www.shareinstall.com.cn".equals(host2)) {
            return true;
        }
        if (!"m12345.com".equals(host2) || (path = uri.getPath()) == null || path.length() <= 1) {
            return false;
        }
        String path2 = path.substring(1).toLowerCase();
        if (!path2.startsWith("blog") && !path2.equals("iv") && !path2.startsWith("faq") && !path2.equals("apps") && !path2.startsWith("s/")) {
            return true;
        }
        if (forceBrowser != null) {
            forceBrowser[0] = true;
        }
        return false;
    }
}

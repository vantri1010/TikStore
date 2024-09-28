package im.bclpbkiauv.ui;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Base64;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.PointerIconCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import com.bjz.comm.net.premission.PermissionUtils;
import com.bjz.comm.net.utils.AppPreferenceUtil;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.javaBean.fc.FollowedFcListBean;
import im.bclpbkiauv.javaBean.fc.HomeFcListBean;
import im.bclpbkiauv.javaBean.fc.RecommendFcListBean;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.messenger.camera.CameraController;
import im.bclpbkiauv.messenger.utils.PlayerUtils;
import im.bclpbkiauv.tel.CallApiBelow26And28Service;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCCall;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.NewLocationActivity;
import im.bclpbkiauv.ui.PhoneBookSelectActivity;
import im.bclpbkiauv.ui.WallpapersListActivity;
import im.bclpbkiauv.ui.actionbar.ActionBarLayout;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.adapters.DrawerLayoutAdapter;
import im.bclpbkiauv.ui.cells.DrawerUserCell;
import im.bclpbkiauv.ui.cells.LanguageCell;
import im.bclpbkiauv.ui.cells.ThemesHorizontalListCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.BlockingUpdateView;
import im.bclpbkiauv.ui.components.EmbedBottomSheet;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.PasscodeView;
import im.bclpbkiauv.ui.components.PipRoundVideoView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.TermsOfServiceView;
import im.bclpbkiauv.ui.components.ThemeEditorView;
import im.bclpbkiauv.ui.components.UpdateAppAlertDialog;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.discoveryweb.DiscoveryJumpPausedFloatingView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.DatabaseInstance;
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.FcDBHelper;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreviewActivity;
import im.bclpbkiauv.ui.hui.login.LoginContronllerActivity;
import im.bclpbkiauv.ui.hui.visualcall.AVideoCallInterface;
import im.bclpbkiauv.ui.hui.visualcall.RingUtils;
import im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity;
import im.bclpbkiauv.ui.hui.visualcall.VisualCallReceiveActivity;
import im.bclpbkiauv.ui.hui.visualcall.VisualCallRequestParaBean;
import im.bclpbkiauv.ui.utils.AppUpdater;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LaunchActivity extends AppCompatActivity implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static final int CODE = 102;
    private static final int PLAY_SERVICES_REQUEST_CHECK_SETTINGS = 140;
    /* access modifiers changed from: private */
    public static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public ActionBarLayout actionBarLayout;
    /* access modifiers changed from: private */
    public View backgroundTablet;
    private BlockingUpdateView blockingUpdateView;
    private AlertDialog checkUpdateDialog;
    private ArrayList<TLRPC.User> contactsToSend;
    private Uri contactsToSendUri;
    /* access modifiers changed from: private */
    public int currentAccount;
    private int currentConnectionState;
    private String documentsMimeType;
    private ArrayList<String> documentsOriginalPathsArray;
    private ArrayList<String> documentsPathsArray;
    private ArrayList<Uri> documentsUrisArray;
    private DrawerLayoutAdapter drawerLayoutAdapter;
    protected DrawerLayoutContainer drawerLayoutContainer;
    /* access modifiers changed from: private */
    public HashMap<String, String> englishLocaleStrings;
    private boolean finished;
    /* access modifiers changed from: private */
    public ActionBarLayout layersActionBarLayout;
    private boolean loadingLocaleDialog;
    private TLRPC.TL_theme loadingTheme;
    private String loadingThemeFileName;
    private Theme.ThemeInfo loadingThemeInfo;
    private AlertDialog loadingThemeProgressDialog;
    private String loadingThemeWallpaperName;
    /* access modifiers changed from: private */
    public AlertDialog localeDialog;
    /* access modifiers changed from: private */
    public Runnable lockRunnable;
    private byte mBytJumpFromBack = 0;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private PasscodeView passcodeView;
    private ArrayList<SendMessagesHelper.SendingMediaInfo> photoPathsArray;
    /* access modifiers changed from: private */
    public AlertDialog proxyErrorDialog;
    /* access modifiers changed from: private */
    public ActionBarLayout rightActionBarLayout;
    private String sendingText;
    /* access modifiers changed from: private */
    public FrameLayout shadowTablet;
    /* access modifiers changed from: private */
    public FrameLayout shadowTabletSide;
    private RecyclerListView sideMenu;
    /* access modifiers changed from: private */
    public HashMap<String, String> systemLocaleStrings;
    /* access modifiers changed from: private */
    public boolean tabletFullSize;
    /* access modifiers changed from: private */
    public TermsOfServiceView termsOfServiceView;
    /* access modifiers changed from: private */
    public UpdateAppAlertDialog updateAppAlertDialog;
    private String videoPath;
    private ActionMode visibleActionMode;
    /* access modifiers changed from: private */
    public AlertDialog visibleDialog;

    private void checkPermission() {
        ArrayList<String> pers = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 29) {
            startActivityIfNeeded(((RoleManager) getSystemService("role")).createRequestRoleIntent("android.app.role.CALL_SCREENING"), PointerIconCompat.TYPE_COPY, (Bundle) null);
        } else {
            pers.add("android.permission.WRITE_CONTACTS");
            pers.add(PermissionUtils.LINKMAIN);
            pers.add("android.permission.READ_CALL_LOG");
            pers.add("android.permission.WRITE_CALL_LOG");
            pers.add("android.permission.READ_PHONE_STATE");
            pers.add("android.permission.MODIFY_PHONE_STATE");
            pers.add(im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_CALL_PHONE);
            pers.add("android.permission.PROCESS_OUTGOING_CALLS");
            pers.add("android.permission.WRITE_SECURE_SETTINGS");
            if (Build.VERSION.SDK_INT >= 26) {
                pers.add("android.permission.ANSWER_PHONE_CALLS");
                pers.add("android.permission.MANAGE_OWN_CALLS");
                pers.add("android.permission.READ_PHONE_NUMBERS");
            }
            if (Build.VERSION.SDK_INT >= 30) {
                pers.add("android.permission.QUERY_ALL_PACKAGES");
            }
        }
        ArrayList<String> realPers = new ArrayList<>();
        Iterator<String> it = pers.iterator();
        while (it.hasNext()) {
            String realPer = it.next();
            if (ActivityCompat.checkSelfPermission(this, realPer) != 0) {
                realPers.add(realPer);
            }
        }
        if (!realPers.isEmpty()) {
            String[] arr = new String[0];
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions((String[]) realPers.toArray(arr), 102);
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r15) {
        /*
            r14 = this;
            java.lang.String r0 = "flyme"
            im.bclpbkiauv.messenger.ApplicationLoader.postInitApplication()
            android.content.Intent r1 = r14.getIntent()
            r14.queryProxyAccount(r1)
            android.content.res.Resources r1 = r14.getResources()
            android.content.res.Configuration r1 = r1.getConfiguration()
            im.bclpbkiauv.messenger.AndroidUtilities.checkDisplaySize(r14, r1)
            int r1 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            r14.currentAccount = r1
            im.bclpbkiauv.ui.utils.AppUpdater r1 = im.bclpbkiauv.ui.utils.AppUpdater.getInstance(r1)
            r1.loadUpdateConfig()
            boolean r1 = im.bclpbkiauv.messenger.BuildVars.PHONE_CHECK
            if (r1 == 0) goto L_0x0029
            r14.checkPermission()
        L_0x0029:
            int r1 = r14.currentAccount
            im.bclpbkiauv.messenger.UserConfig r1 = im.bclpbkiauv.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isClientActivated()
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L_0x00c3
            android.content.Intent r1 = r14.getIntent()
            r4 = 0
            if (r1 == 0) goto L_0x00a4
            java.lang.String r5 = r1.getAction()
            if (r5 == 0) goto L_0x00a4
            java.lang.String r5 = r1.getAction()
            java.lang.String r6 = "android.intent.action.SEND"
            boolean r5 = r6.equals(r5)
            if (r5 != 0) goto L_0x009d
            java.lang.String r5 = r1.getAction()
            java.lang.String r6 = "android.intent.action.SEND_MULTIPLE"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x005d
            goto L_0x009d
        L_0x005d:
            java.lang.String r5 = r1.getAction()
            java.lang.String r6 = "android.intent.action.VIEW"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x00a4
            android.net.Uri r5 = r1.getData()
            if (r5 == 0) goto L_0x00a4
            java.lang.String r6 = r5.toString()
            java.lang.String r6 = r6.toLowerCase()
            java.lang.String r7 = "hchat:proxy"
            boolean r7 = r6.startsWith(r7)
            if (r7 != 0) goto L_0x009a
            java.lang.String r7 = "hchat://proxy"
            boolean r7 = r6.startsWith(r7)
            if (r7 != 0) goto L_0x009a
            java.lang.String r7 = "hchat:socks"
            boolean r7 = r6.startsWith(r7)
            if (r7 != 0) goto L_0x009a
            java.lang.String r7 = "hchat://socks"
            boolean r7 = r6.startsWith(r7)
            if (r7 == 0) goto L_0x0098
            goto L_0x009a
        L_0x0098:
            r7 = 0
            goto L_0x009b
        L_0x009a:
            r7 = 1
        L_0x009b:
            r4 = r7
            goto L_0x00a4
        L_0x009d:
            super.onCreate(r15)
            r14.finish()
            return
        L_0x00a4:
            android.content.SharedPreferences r5 = im.bclpbkiauv.messenger.MessagesController.getGlobalMainSettings()
            r6 = 0
            java.lang.String r8 = "intro_crashed_time"
            long r9 = r5.getLong(r8, r6)
            java.lang.String r11 = "fromIntro"
            boolean r11 = r1.getBooleanExtra(r11, r3)
            if (r11 == 0) goto L_0x00c3
            android.content.SharedPreferences$Editor r12 = r5.edit()
            android.content.SharedPreferences$Editor r6 = r12.putLong(r8, r6)
            r6.commit()
        L_0x00c3:
            r14.requestWindowFeature(r2)
            r1 = 2131755390(0x7f10017e, float:1.9141658E38)
            r14.setTheme(r1)
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r1 < r4) goto L_0x00ef
            r1 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            android.app.ActivityManager$TaskDescription r5 = new android.app.ActivityManager$TaskDescription     // Catch:{ Exception -> 0x00e5 }
            java.lang.String r6 = "actionBarDefault"
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)     // Catch:{ Exception -> 0x00e5 }
            r6 = r6 | r1
            r7 = 0
            r5.<init>(r7, r7, r6)     // Catch:{ Exception -> 0x00e5 }
            r14.setTaskDescription(r5)     // Catch:{ Exception -> 0x00e5 }
            goto L_0x00e6
        L_0x00e5:
            r5 = move-exception
        L_0x00e6:
            android.view.Window r5 = r14.getWindow()     // Catch:{ Exception -> 0x00ee }
            r5.setNavigationBarColor(r1)     // Catch:{ Exception -> 0x00ee }
            goto L_0x00ef
        L_0x00ee:
            r1 = move-exception
        L_0x00ef:
            android.view.Window r1 = r14.getWindow()
            r5 = 2131231649(0x7f0803a1, float:1.8079385E38)
            r1.setBackgroundDrawableResource(r5)
            java.lang.String r1 = im.bclpbkiauv.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0113
            boolean r1 = im.bclpbkiauv.messenger.SharedConfig.allowScreenCapture
            if (r1 != 0) goto L_0x0113
            android.view.Window r1 = r14.getWindow()     // Catch:{ Exception -> 0x010f }
            r5 = 8192(0x2000, float:1.14794E-41)
            r1.setFlags(r5, r5)     // Catch:{ Exception -> 0x010f }
            goto L_0x0113
        L_0x010f:
            r1 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0113:
            super.onCreate(r15)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r4) goto L_0x012a
            android.view.Window r1 = r14.getWindow()
            android.view.View r4 = r1.getDecorView()
            r5 = 1280(0x500, float:1.794E-42)
            r4.setSystemUiVisibility(r5)
            r1.setStatusBarColor(r3)
        L_0x012a:
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 24
            if (r1 < r4) goto L_0x0136
            boolean r1 = r14.isInMultiWindowMode()
            im.bclpbkiauv.messenger.AndroidUtilities.isInMultiwindow = r1
        L_0x0136:
            im.bclpbkiauv.ui.actionbar.Theme.createChatResources(r14, r3)
            java.lang.String r1 = im.bclpbkiauv.messenger.SharedConfig.passcodeHash
            int r1 = r1.length()
            if (r1 == 0) goto L_0x0151
            boolean r1 = im.bclpbkiauv.messenger.SharedConfig.appLocked
            if (r1 == 0) goto L_0x0151
            int r1 = r14.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r1 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getCurrentTime()
            im.bclpbkiauv.messenger.SharedConfig.lastPauseTime = r1
        L_0x0151:
            android.content.res.Resources r1 = r14.getResources()
            java.lang.String r4 = "status_bar_height"
            java.lang.String r5 = "dimen"
            java.lang.String r6 = "android"
            int r1 = r1.getIdentifier(r4, r5, r6)
            if (r1 <= 0) goto L_0x016c
            android.content.res.Resources r4 = r14.getResources()
            int r4 = r4.getDimensionPixelSize(r1)
            im.bclpbkiauv.messenger.AndroidUtilities.statusBarHeight = r4
        L_0x016c:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r4 = new im.bclpbkiauv.ui.actionbar.ActionBarLayout
            r4.<init>(r14)
            r14.actionBarLayout = r4
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r4 = new im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer
            r4.<init>(r14)
            r14.drawerLayoutContainer = r4
            java.lang.String r5 = "windowBackgroundWhite"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            r4.setBehindKeyboardColor(r5)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r4 = r14.drawerLayoutContainer
            android.view.ViewGroup$LayoutParams r5 = new android.view.ViewGroup$LayoutParams
            r6 = -1
            r5.<init>(r6, r6)
            r14.setContentView(r4, r5)
            boolean r4 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            r5 = -1082130432(0xffffffffbf800000, float:-1.0)
            if (r4 == 0) goto L_0x027f
            android.view.Window r4 = r14.getWindow()
            r7 = 16
            r4.setSoftInputMode(r7)
            im.bclpbkiauv.ui.LaunchActivity$1 r4 = new im.bclpbkiauv.ui.LaunchActivity$1
            r4.<init>(r14)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r7 = r14.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r8 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r6, r5)
            r7.addView(r4, r8)
            android.view.View r7 = new android.view.View
            r7.<init>(r14)
            r14.backgroundTablet = r7
            android.content.res.Resources r7 = r14.getResources()
            r8 = 2131230914(0x7f0800c2, float:1.8077894E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)
            android.graphics.drawable.BitmapDrawable r7 = (android.graphics.drawable.BitmapDrawable) r7
            android.graphics.Shader$TileMode r8 = android.graphics.Shader.TileMode.REPEAT
            android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.REPEAT
            r7.setTileModeXY(r8, r9)
            android.view.View r8 = r14.backgroundTablet
            r8.setBackgroundDrawable(r7)
            android.view.View r8 = r14.backgroundTablet
            android.widget.RelativeLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createRelative(r6, r6)
            r4.addView(r8, r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.actionBarLayout
            r4.addView(r8)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = new im.bclpbkiauv.ui.actionbar.ActionBarLayout
            r8.<init>(r14)
            r14.rightActionBarLayout = r8
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r9 = rightFragmentsStack
            r8.init(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.rightActionBarLayout
            r8.setDelegate(r14)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.rightActionBarLayout
            r4.addView(r8)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r14)
            r14.shadowTabletSide = r8
            r9 = 1076449908(0x40295274, float:2.6456575)
            r8.setBackgroundColor(r9)
            android.widget.FrameLayout r8 = r14.shadowTabletSide
            r4.addView(r8)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r14)
            r14.shadowTablet = r8
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            r10 = 8
            if (r9 == 0) goto L_0x0218
            r9 = 8
            goto L_0x0219
        L_0x0218:
            r9 = 0
        L_0x0219:
            r8.setVisibility(r9)
            android.widget.FrameLayout r8 = r14.shadowTablet
            r9 = 2130706432(0x7f000000, float:1.7014118E38)
            r8.setBackgroundColor(r9)
            android.widget.FrameLayout r8 = r14.shadowTablet
            r4.addView(r8)
            android.widget.FrameLayout r8 = r14.shadowTablet
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$aHDLubgf2UDUEuZAns3U6Nw2UxM r9 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$aHDLubgf2UDUEuZAns3U6Nw2UxM
            r9.<init>()
            r8.setOnTouchListener(r9)
            android.widget.FrameLayout r8 = r14.shadowTablet
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$AO2d-r-PXWVND3Zk2xR11ea2brQ r9 = im.bclpbkiauv.ui.$$Lambda$LaunchActivity$AO2drPXWVND3Zk2xR11ea2brQ.INSTANCE
            r8.setOnClickListener(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = new im.bclpbkiauv.ui.actionbar.ActionBarLayout
            r8.<init>(r14)
            r14.layersActionBarLayout = r8
            r8.setRemoveActionBarExtraHeight(r2)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            android.widget.FrameLayout r9 = r14.shadowTablet
            r8.setBackgroundView(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            r8.setUseAlphaAnimations(r2)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            r9 = 2131230882(0x7f0800a2, float:1.807783E38)
            r8.setBackgroundResource(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r9 = layerFragmentsStack
            r8.init(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            r8.setDelegate(r14)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r9 = r14.drawerLayoutContainer
            r8.setDrawerLayoutContainer(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r9 = layerFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 == 0) goto L_0x0275
            goto L_0x0276
        L_0x0275:
            r10 = 0
        L_0x0276:
            r8.setVisibility(r10)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            r4.addView(r8)
            goto L_0x028b
        L_0x027f:
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r4 = r14.drawerLayoutContainer
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r7 = r14.actionBarLayout
            android.view.ViewGroup$LayoutParams r8 = new android.view.ViewGroup$LayoutParams
            r8.<init>(r6, r6)
            r4.addView(r7, r8)
        L_0x028b:
            im.bclpbkiauv.ui.components.RecyclerListView r4 = new im.bclpbkiauv.ui.components.RecyclerListView
            r4.<init>(r14)
            r14.sideMenu = r4
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r4 = r4.getItemAnimator()
            androidx.recyclerview.widget.DefaultItemAnimator r4 = (androidx.recyclerview.widget.DefaultItemAnimator) r4
            r4.setDelayAnimations(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r14.sideMenu
            java.lang.String r7 = "chats_menuBackground"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r4.setBackgroundColor(r7)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r14.sideMenu
            androidx.recyclerview.widget.LinearLayoutManager r7 = new androidx.recyclerview.widget.LinearLayoutManager
            r7.<init>(r14, r2, r3)
            r4.setLayoutManager(r7)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r14.sideMenu
            im.bclpbkiauv.ui.adapters.DrawerLayoutAdapter r7 = new im.bclpbkiauv.ui.adapters.DrawerLayoutAdapter
            r7.<init>(r14)
            r14.drawerLayoutAdapter = r7
            r4.setAdapter(r7)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r4 = r14.drawerLayoutContainer
            im.bclpbkiauv.ui.components.RecyclerListView r7 = r14.sideMenu
            r4.setDrawerLayout(r7)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r14.sideMenu
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            android.graphics.Point r7 = im.bclpbkiauv.messenger.AndroidUtilities.getRealScreenSize()
            boolean r8 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            r9 = 1134559232(0x43a00000, float:320.0)
            if (r8 == 0) goto L_0x02dc
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            goto L_0x02f3
        L_0x02dc:
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r9 = r7.x
            int r10 = r7.y
            int r9 = java.lang.Math.min(r9, r10)
            r10 = 1113587712(0x42600000, float:56.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 - r10
            int r8 = java.lang.Math.min(r8, r9)
        L_0x02f3:
            r4.width = r8
            r4.height = r6
            im.bclpbkiauv.ui.components.RecyclerListView r8 = r14.sideMenu
            r8.setLayoutParams(r4)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r8 = r14.drawerLayoutContainer
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r14.actionBarLayout
            r8.setParentActionBarLayout(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.actionBarLayout
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r9 = r14.drawerLayoutContainer
            r8.setDrawerLayoutContainer(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r9 = mainFragmentsStack
            r8.init(r9)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.actionBarLayout
            r8.setDelegate(r14)
            im.bclpbkiauv.ui.actionbar.Theme.loadWallpaper()
            im.bclpbkiauv.ui.components.PasscodeView r8 = new im.bclpbkiauv.ui.components.PasscodeView
            r8.<init>(r14)
            r14.passcodeView = r8
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r9 = r14.drawerLayoutContainer
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r6, r5)
            r9.addView(r8, r5)
            r14.checkCurrentAccount()
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.closeOtherAppActivities
            java.lang.Object[] r9 = new java.lang.Object[r2]
            r9[r3] = r14
            r5.postNotificationName(r8, r9)
            int r5 = r14.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r5 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r5)
            int r5 = r5.getConnectionState()
            r14.currentConnectionState = r5
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.needShowAlert
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.reloadInterface
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.suggestedLangpack
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.didSetNewTheme
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.needSetDayNightTheme
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.closeOtherAppActivities
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.didSetPasscode
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.didSetNewWallpapper
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.notificationsCountUpdated
            r5.addObserver(r14, r8)
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.receivedAVideoCallRequest
            r5.addObserver(r14, r8)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r5 = r14.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r5 = r5.fragmentsStack
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x04a7
            int r5 = r14.currentAccount
            im.bclpbkiauv.messenger.UserConfig r5 = im.bclpbkiauv.messenger.UserConfig.getInstance(r5)
            boolean r5 = r5.isClientActivated()
            if (r5 != 0) goto L_0x03c5
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r5 = r14.actionBarLayout
            im.bclpbkiauv.ui.hui.login.LoginContronllerActivity r8 = new im.bclpbkiauv.ui.hui.login.LoginContronllerActivity
            r8.<init>()
            r5.addFragmentToStack(r8)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r5 = r14.drawerLayoutContainer
            r5.setAllowOpenDrawer(r3, r3)
            goto L_0x03d4
        L_0x03c5:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r5 = r14.actionBarLayout
            im.bclpbkiauv.ui.IndexActivity r8 = new im.bclpbkiauv.ui.IndexActivity
            r8.<init>()
            r5.addFragmentToStack(r8)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r5 = r14.drawerLayoutContainer
            r5.setAllowOpenDrawer(r3, r3)
        L_0x03d4:
            if (r15 == 0) goto L_0x04a6
            java.lang.String r5 = "fragment"
            java.lang.String r5 = r15.getString(r5)     // Catch:{ Exception -> 0x04a2 }
            if (r5 == 0) goto L_0x04a6
            java.lang.String r8 = "args"
            android.os.Bundle r8 = r15.getBundle(r8)     // Catch:{ Exception -> 0x04a2 }
            int r9 = r5.hashCode()     // Catch:{ Exception -> 0x04a2 }
            r10 = 5
            r11 = 4
            r12 = 3
            r13 = 2
            switch(r9) {
                case -1529105743: goto L_0x0422;
                case -1349522494: goto L_0x0418;
                case 3052376: goto L_0x040e;
                case 98629247: goto L_0x0404;
                case 738950403: goto L_0x03fa;
                case 1434631203: goto L_0x03f0;
                default: goto L_0x03ef;
            }     // Catch:{ Exception -> 0x04a2 }
        L_0x03ef:
            goto L_0x042c
        L_0x03f0:
            java.lang.String r9 = "settings"
            boolean r9 = r5.equals(r9)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x03ef
            r6 = 1
            goto L_0x042c
        L_0x03fa:
            java.lang.String r9 = "channel"
            boolean r9 = r5.equals(r9)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x03ef
            r6 = 3
            goto L_0x042c
        L_0x0404:
            java.lang.String r9 = "group"
            boolean r9 = r5.equals(r9)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x03ef
            r6 = 2
            goto L_0x042c
        L_0x040e:
            java.lang.String r9 = "chat"
            boolean r9 = r5.equals(r9)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x03ef
            r6 = 0
            goto L_0x042c
        L_0x0418:
            java.lang.String r9 = "chat_profile"
            boolean r9 = r5.equals(r9)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x03ef
            r6 = 4
            goto L_0x042c
        L_0x0422:
            java.lang.String r9 = "wallpapers"
            boolean r9 = r5.equals(r9)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x03ef
            r6 = 5
        L_0x042c:
            if (r6 == 0) goto L_0x048f
            if (r6 == r2) goto L_0x0481
            if (r6 == r13) goto L_0x046e
            if (r6 == r12) goto L_0x045b
            if (r6 == r11) goto L_0x0448
            if (r6 == r10) goto L_0x043a
            goto L_0x04a6
        L_0x043a:
            im.bclpbkiauv.ui.WallpapersListActivity r6 = new im.bclpbkiauv.ui.WallpapersListActivity     // Catch:{ Exception -> 0x04a2 }
            r6.<init>(r3)     // Catch:{ Exception -> 0x04a2 }
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r14.actionBarLayout     // Catch:{ Exception -> 0x04a2 }
            r9.addFragmentToStack(r6)     // Catch:{ Exception -> 0x04a2 }
            r6.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x04a2 }
            goto L_0x04a6
        L_0x0448:
            if (r8 == 0) goto L_0x04a6
            im.bclpbkiauv.ui.ProfileActivity r6 = new im.bclpbkiauv.ui.ProfileActivity     // Catch:{ Exception -> 0x04a2 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x04a2 }
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r14.actionBarLayout     // Catch:{ Exception -> 0x04a2 }
            boolean r9 = r9.addFragmentToStack(r6)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x045a
            r6.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x04a2 }
        L_0x045a:
            goto L_0x04a6
        L_0x045b:
            if (r8 == 0) goto L_0x04a6
            im.bclpbkiauv.ui.ChannelCreateActivity r6 = new im.bclpbkiauv.ui.ChannelCreateActivity     // Catch:{ Exception -> 0x04a2 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x04a2 }
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r14.actionBarLayout     // Catch:{ Exception -> 0x04a2 }
            boolean r9 = r9.addFragmentToStack(r6)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x046d
            r6.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x04a2 }
        L_0x046d:
            goto L_0x04a6
        L_0x046e:
            if (r8 == 0) goto L_0x04a6
            im.bclpbkiauv.ui.GroupCreateFinalActivity r6 = new im.bclpbkiauv.ui.GroupCreateFinalActivity     // Catch:{ Exception -> 0x04a2 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x04a2 }
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r14.actionBarLayout     // Catch:{ Exception -> 0x04a2 }
            boolean r9 = r9.addFragmentToStack(r6)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x0480
            r6.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x04a2 }
        L_0x0480:
            goto L_0x04a6
        L_0x0481:
            im.bclpbkiauv.ui.SettingsActivity r6 = new im.bclpbkiauv.ui.SettingsActivity     // Catch:{ Exception -> 0x04a2 }
            r6.<init>()     // Catch:{ Exception -> 0x04a2 }
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r14.actionBarLayout     // Catch:{ Exception -> 0x04a2 }
            r9.addFragmentToStack(r6)     // Catch:{ Exception -> 0x04a2 }
            r6.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x04a2 }
            goto L_0x04a6
        L_0x048f:
            if (r8 == 0) goto L_0x04a6
            im.bclpbkiauv.ui.ChatActivity r6 = new im.bclpbkiauv.ui.ChatActivity     // Catch:{ Exception -> 0x04a2 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x04a2 }
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r14.actionBarLayout     // Catch:{ Exception -> 0x04a2 }
            boolean r9 = r9.addFragmentToStack(r6)     // Catch:{ Exception -> 0x04a2 }
            if (r9 == 0) goto L_0x04a1
            r6.restoreSelfArgs(r15)     // Catch:{ Exception -> 0x04a2 }
        L_0x04a1:
            goto L_0x04a6
        L_0x04a2:
            r5 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x04a6:
            goto L_0x0505
        L_0x04a7:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r5 = r14.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r5 = r5.fragmentsStack
            java.lang.Object r5 = r5.get(r3)
            im.bclpbkiauv.ui.actionbar.BaseFragment r5 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r5
            boolean r6 = r5 instanceof im.bclpbkiauv.ui.IndexActivity
            r6 = 1
            boolean r8 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x04e9
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            int r8 = r8.size()
            if (r8 > r2) goto L_0x04d0
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            boolean r8 = r8.isEmpty()
            if (r8 == 0) goto L_0x04d0
            r8 = 1
            goto L_0x04d1
        L_0x04d0:
            r8 = 0
        L_0x04d1:
            r6 = r8
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            int r8 = r8.size()
            if (r8 != r2) goto L_0x04e9
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            java.lang.Object r8 = r8.get(r3)
            boolean r8 = r8 instanceof im.bclpbkiauv.ui.hui.login.LoginContronllerActivity
            if (r8 == 0) goto L_0x04e9
            r6 = 0
        L_0x04e9:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            int r8 = r8.size()
            if (r8 != r2) goto L_0x0500
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r14.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            java.lang.Object r8 = r8.get(r3)
            boolean r8 = r8 instanceof im.bclpbkiauv.ui.hui.login.LoginContronllerActivity
            if (r8 == 0) goto L_0x0500
            r6 = 0
        L_0x0500:
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r8 = r14.drawerLayoutContainer
            r8.setAllowOpenDrawer(r3, r3)
        L_0x0505:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r5 = r14.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            if (r5 < r2) goto L_0x0514
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r5 = r14.actionBarLayout
            r5.showLastFragment()
        L_0x0514:
            r14.checkLayout()
            r14.checkSystemBarColors()
            android.content.Intent r5 = r14.getIntent()
            if (r15 == 0) goto L_0x0522
            r6 = 1
            goto L_0x0523
        L_0x0522:
            r6 = 0
        L_0x0523:
            r14.handleIntent(r5, r3, r6, r3)
            java.lang.String r3 = android.os.Build.DISPLAY     // Catch:{ Exception -> 0x0587 }
            java.lang.String r5 = android.os.Build.USER     // Catch:{ Exception -> 0x0587 }
            java.lang.String r6 = ""
            if (r3 == 0) goto L_0x0534
            java.lang.String r8 = r3.toLowerCase()     // Catch:{ Exception -> 0x0587 }
            r3 = r8
            goto L_0x0535
        L_0x0534:
            r3 = r6
        L_0x0535:
            if (r5 == 0) goto L_0x053d
            java.lang.String r6 = r3.toLowerCase()     // Catch:{ Exception -> 0x0587 }
            r5 = r6
            goto L_0x053e
        L_0x053d:
            r5 = r6
        L_0x053e:
            boolean r6 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0587 }
            if (r6 == 0) goto L_0x055e
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0587 }
            r6.<init>()     // Catch:{ Exception -> 0x0587 }
            java.lang.String r8 = "OS name "
            r6.append(r8)     // Catch:{ Exception -> 0x0587 }
            r6.append(r3)     // Catch:{ Exception -> 0x0587 }
            java.lang.String r8 = " "
            r6.append(r8)     // Catch:{ Exception -> 0x0587 }
            r6.append(r5)     // Catch:{ Exception -> 0x0587 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0587 }
            im.bclpbkiauv.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0587 }
        L_0x055e:
            boolean r6 = r3.contains(r0)     // Catch:{ Exception -> 0x0587 }
            if (r6 != 0) goto L_0x056a
            boolean r0 = r5.contains(r0)     // Catch:{ Exception -> 0x0587 }
            if (r0 == 0) goto L_0x0586
        L_0x056a:
            im.bclpbkiauv.messenger.AndroidUtilities.incorrectDisplaySizeFix = r2     // Catch:{ Exception -> 0x0587 }
            android.view.Window r0 = r14.getWindow()     // Catch:{ Exception -> 0x0587 }
            android.view.View r0 = r0.getDecorView()     // Catch:{ Exception -> 0x0587 }
            android.view.View r0 = r0.getRootView()     // Catch:{ Exception -> 0x0587 }
            android.view.ViewTreeObserver r6 = r0.getViewTreeObserver()     // Catch:{ Exception -> 0x0587 }
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$Xs2dlTwwpNq00NYsV_sSDrXsyBM r8 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$Xs2dlTwwpNq00NYsV_sSDrXsyBM     // Catch:{ Exception -> 0x0587 }
            r8.<init>(r0)     // Catch:{ Exception -> 0x0587 }
            r14.onGlobalLayoutListener = r8     // Catch:{ Exception -> 0x0587 }
            r6.addOnGlobalLayoutListener(r8)     // Catch:{ Exception -> 0x0587 }
        L_0x0586:
            goto L_0x058b
        L_0x0587:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x058b:
            im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.getInstance()
            r0.setBaseActivity(r14, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    public /* synthetic */ boolean lambda$onCreate$0$LaunchActivity(View v, MotionEvent event) {
        if (this.actionBarLayout.fragmentsStack.isEmpty() || event.getAction() != 1) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int[] location = new int[2];
        this.layersActionBarLayout.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        if (this.layersActionBarLayout.checkTransitionAnimation() || (x > ((float) viewX) && x < ((float) (this.layersActionBarLayout.getWidth() + viewX)) && y > ((float) viewY) && y < ((float) (this.layersActionBarLayout.getHeight() + viewY)))) {
            return false;
        }
        if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
            for (int a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
                actionBarLayout2.removeFragmentFromStack(actionBarLayout2.fragmentsStack.get(0));
            }
            this.layersActionBarLayout.closeLastFragment(true);
        }
        return true;
    }

    static /* synthetic */ void lambda$onCreate$1(View v) {
    }

    static /* synthetic */ void lambda$onCreate$2(View view) {
        int height = view.getMeasuredHeight();
        FileLog.d("height = " + height + " displayHeight = " + AndroidUtilities.displaySize.y);
        if (Build.VERSION.SDK_INT >= 21) {
            height -= AndroidUtilities.statusBarHeight;
        }
        if (height > AndroidUtilities.dp(100.0f) && height < AndroidUtilities.displaySize.y && AndroidUtilities.dp(100.0f) + height > AndroidUtilities.displaySize.y) {
            AndroidUtilities.displaySize.y = height;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("fix display size y to " + AndroidUtilities.displaySize.y);
            }
        }
    }

    @Deprecated
    private void queryProxyAccount(Intent intent) {
    }

    private void checkSystemBarColors() {
        checkSystemBarColors(true);
    }

    private void checkSystemBarColors(boolean checkNavigationBar) {
        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT >= 26 && checkNavigationBar) {
            Window window = getWindow();
            int color = Theme.getColor(Theme.key_windowBackgroundGray);
            if (window.getNavigationBarColor() != color) {
                window.setNavigationBarColor(color);
                AndroidUtilities.setLightNavigationBar(getWindow(), AndroidUtilities.computePerceivedBrightness(color) >= 0.721f);
            }
        }
    }

    public void switchToAccount(int account, boolean removeAll) {
        if (account != UserConfig.selectedAccount) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Launch ===> switchToAccount start  newAccount = " + account + " oldAccount = " + this.currentAccount + " userConfigAccount = " + UserConfig.selectedAccount + " removeAll = " + removeAll);
            }
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
            UserConfig.selectedAccount = account;
            UserConfig.getInstance(0).saveConfig(false);
            checkCurrentAccount();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.removeAllFragments();
                this.rightActionBarLayout.removeAllFragments();
                if (!this.tabletFullSize) {
                    this.shadowTabletSide.setVisibility(0);
                    if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        this.backgroundTablet.setVisibility(0);
                    }
                    this.rightActionBarLayout.setVisibility(8);
                }
                this.layersActionBarLayout.setVisibility(8);
            }
            if (removeAll) {
                this.actionBarLayout.removeAllFragments();
            } else {
                this.actionBarLayout.removeFragmentFromStack(0);
            }
            this.actionBarLayout.addFragmentToStack(new IndexActivity(), 0);
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
                this.rightActionBarLayout.showLastFragment();
            }
            if (!ApplicationLoader.mainInterfacePaused) {
                ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
            }
            if (UserConfig.getInstance(account).unacceptedTermsOfService != null) {
                showTosActivity(account, UserConfig.getInstance(account).unacceptedTermsOfService);
            }
            AppPreferenceUtil.putString("PublishFcBean", "");
            FcDBHelper.getInstance().deleteAll(HomeFcListBean.class);
            FcDBHelper.getInstance().deleteAll(RecommendFcListBean.class);
            FcDBHelper.getInstance().deleteAll(FollowedFcListBean.class);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Launch ===> switchToAccount end  newAccount = " + account + " oldAccount =   = " + this.currentAccount + " userConfigAccount = " + UserConfig.selectedAccount + " removeAll = " + removeAll);
            }
            DiscoveryJumpPausedFloatingView.getInstance().hide(true);
        }
    }

    private void switchToAvailableAccountOrLogout() {
        int account = -1;
        int a = 0;
        while (true) {
            if (a >= 3) {
                break;
            } else if (UserConfig.getInstance(a).isClientActivated()) {
                account = a;
                break;
            } else {
                a++;
            }
        }
        TermsOfServiceView termsOfServiceView2 = this.termsOfServiceView;
        if (termsOfServiceView2 != null) {
            termsOfServiceView2.setVisibility(8);
        }
        if (account != -1) {
            switchToAccount(account, true);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        UserConfig.selectedAccount = 0;
        UserConfig.getInstance(0).saveConfig(false);
        checkCurrentAccount();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.removeAllFragments();
            this.rightActionBarLayout.removeAllFragments();
            if (!this.tabletFullSize) {
                this.shadowTabletSide.setVisibility(0);
                if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    this.backgroundTablet.setVisibility(0);
                }
                this.rightActionBarLayout.setVisibility(8);
            }
            this.layersActionBarLayout.setVisibility(8);
        }
        this.actionBarLayout.removeAllFragments();
        LoginContronllerActivity loginPage = new LoginContronllerActivity();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("LaunchActivity ===> switchToAvailableAccountOrLogout() , logoutAccount = " + 0 + " UserConfig.selectedAccount = " + UserConfig.selectedAccount + " This.currentAccount = " + this.currentAccount + " loginPage.currentAccount = " + loginPage.getCurrentAccount());
        }
        this.actionBarLayout.addFragmentToStack(loginPage, 0);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
        }
        if (!ApplicationLoader.mainInterfacePaused) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
    }

    public int getMainFragmentsCount() {
        return mainFragmentsStack.size();
    }

    private void checkCurrentAccount() {
        if (this.currentAccount != UserConfig.selectedAccount) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowPlayServicesAlert);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.folderWebView);
        }
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowPlayServicesAlert);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.folderWebView);
        updateCurrentConnectionState(this.currentAccount);
    }

    private void checkLayout() {
        if (AndroidUtilities.isTablet() && this.rightActionBarLayout != null) {
            int i = 0;
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                this.tabletFullSize = true;
                if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    for (int a = 0; a < this.rightActionBarLayout.fragmentsStack.size(); a = (a - 1) + 1) {
                        BaseFragment chatFragment = this.rightActionBarLayout.fragmentsStack.get(a);
                        if (chatFragment instanceof ChatActivity) {
                            ((ChatActivity) chatFragment).setIgnoreAttachOnPause(true);
                        }
                        chatFragment.onPause();
                        this.rightActionBarLayout.fragmentsStack.remove(a);
                        this.actionBarLayout.fragmentsStack.add(chatFragment);
                    }
                    if (this.passcodeView.getVisibility() != 0) {
                        this.actionBarLayout.showLastFragment();
                    }
                }
                this.shadowTabletSide.setVisibility(8);
                this.rightActionBarLayout.setVisibility(8);
                View view = this.backgroundTablet;
                if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    i = 8;
                }
                view.setVisibility(i);
                return;
            }
            this.tabletFullSize = false;
            if (this.actionBarLayout.fragmentsStack.size() >= 2) {
                for (int a2 = 1; a2 < this.actionBarLayout.fragmentsStack.size(); a2 = (a2 - 1) + 1) {
                    BaseFragment chatFragment2 = this.actionBarLayout.fragmentsStack.get(a2);
                    if (chatFragment2 instanceof ChatActivity) {
                        ((ChatActivity) chatFragment2).setIgnoreAttachOnPause(true);
                    }
                    chatFragment2.onPause();
                    this.actionBarLayout.fragmentsStack.remove(a2);
                    this.rightActionBarLayout.fragmentsStack.add(chatFragment2);
                }
                if (this.passcodeView.getVisibility() != 0) {
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                }
            }
            ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
            actionBarLayout2.setVisibility(actionBarLayout2.fragmentsStack.isEmpty() ? 8 : 0);
            this.backgroundTablet.setVisibility(this.rightActionBarLayout.fragmentsStack.isEmpty() ? 0 : 8);
            FrameLayout frameLayout = this.shadowTabletSide;
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                i = 8;
            }
            frameLayout.setVisibility(i);
        }
    }

    private void showUpdateActivity(int account, TLRPC.TL_help_appUpdate update, boolean check) {
        if (this.blockingUpdateView == null) {
            AnonymousClass2 r0 = new BlockingUpdateView(this) {
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (visibility == 8) {
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    }
                }
            };
            this.blockingUpdateView = r0;
            this.drawerLayoutContainer.addView(r0, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.blockingUpdateView.show(account, update, check);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    private void showTosActivity(int account, TLRPC.TL_help_termsOfService tos) {
        if (this.termsOfServiceView == null) {
            TermsOfServiceView termsOfServiceView2 = new TermsOfServiceView(this);
            this.termsOfServiceView = termsOfServiceView2;
            this.drawerLayoutContainer.addView(termsOfServiceView2, LayoutHelper.createFrame(-1, -1.0f));
            this.termsOfServiceView.setDelegate(new TermsOfServiceView.TermsOfServiceViewDelegate() {
                public void onAcceptTerms(int account) {
                    UserConfig.getInstance(account).unacceptedTermsOfService = null;
                    UserConfig.getInstance(account).saveConfig(false);
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }

                public void onDeclineTerms(int account) {
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    LaunchActivity.this.termsOfServiceView.setVisibility(8);
                }
            });
        }
        TLRPC.TL_help_termsOfService currentTos = UserConfig.getInstance(account).unacceptedTermsOfService;
        if (currentTos != tos && (currentTos == null || !currentTos.id.data.equals(tos.id.data))) {
            UserConfig.getInstance(account).unacceptedTermsOfService = tos;
            UserConfig.getInstance(account).saveConfig(false);
        }
        this.termsOfServiceView.show(account, tos);
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    }

    /* access modifiers changed from: private */
    public void showPasscodeActivity() {
        if (this.passcodeView != null) {
            SharedConfig.appLocked = true;
            if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
                SecretMediaViewer.getInstance().closePhoto(false, false);
            } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(false, true);
            } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                ArticleViewer.getInstance().close(false, true);
            }
            this.passcodeView.onShow();
            SharedConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new PasscodeView.PasscodeViewDelegate() {
                public final void didAcceptedPassword() {
                    LaunchActivity.this.lambda$showPasscodeActivity$3$LaunchActivity();
                }
            });
            this.actionBarLayout.setVisibility(4);
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getVisibility() == 0) {
                    this.layersActionBarLayout.setVisibility(4);
                }
                this.rightActionBarLayout.setVisibility(4);
            }
        }
    }

    public /* synthetic */ void lambda$showPasscodeActivity$3$LaunchActivity() {
        SharedConfig.isWaitingForPasscodeEnter = false;
        Intent intent = this.passcodeSaveIntent;
        if (intent != null) {
            handleIntent(intent, this.passcodeSaveIntentIsNew, this.passcodeSaveIntentIsRestore, true);
            this.passcodeSaveIntent = null;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        this.actionBarLayout.setVisibility(0);
        this.actionBarLayout.showLastFragment();
        if (AndroidUtilities.isTablet()) {
            this.layersActionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
            if (this.layersActionBarLayout.getVisibility() == 4) {
                this.layersActionBarLayout.setVisibility(0);
            }
            this.rightActionBarLayout.setVisibility(0);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v33, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r2v7 */
    /* JADX WARNING: type inference failed for: r2v8 */
    /* JADX WARNING: type inference failed for: r2v9 */
    /* JADX WARNING: type inference failed for: r2v10 */
    /* JADX WARNING: type inference failed for: r2v11 */
    /* JADX WARNING: type inference failed for: r2v12 */
    /* JADX WARNING: type inference failed for: r2v13 */
    /* JADX WARNING: type inference failed for: r2v15 */
    /* JADX WARNING: type inference failed for: r2v147 */
    /* JADX WARNING: type inference failed for: r2v148 */
    /* JADX WARNING: type inference failed for: r2v149 */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0e96, code lost:
        r55[0] = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:?, code lost:
        switchToAccount(r55[0], true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0ec1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x1104, code lost:
        if (r9.checkCanOpenChat(r8, r10.get(r10.size() - 1)) != false) goto L_0x1106;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x1151, code lost:
        if (r9.checkCanOpenChat(r8, r10.get(r10.size() - 1)) != false) goto L_0x1153;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x01cd, code lost:
        if (r4.toString().toLowerCase().endsWith(".jpg") != false) goto L_0x01cf;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r2v2, types: [int, boolean] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0390  */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0e23  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0e2c  */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x10d0  */
    /* JADX WARNING: Removed duplicated region for block: B:756:0x13ac  */
    /* JADX WARNING: Removed duplicated region for block: B:762:0x13b9  */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x13f9  */
    /* JADX WARNING: Removed duplicated region for block: B:778:0x1439  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean handleIntent(android.content.Intent r68, boolean r69, boolean r70, boolean r71) {
        /*
            r67 = this;
            r15 = r67
            r14 = r68
            r13 = r70
            boolean r0 = im.bclpbkiauv.messenger.AndroidUtilities.handleProxyIntent(r67, r68)
            r12 = 1
            if (r0 == 0) goto L_0x0023
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r0 = r15.actionBarLayout
            r0.showLastFragment()
            boolean r0 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0022
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r0 = r15.layersActionBarLayout
            r0.showLastFragment()
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r0 = r15.rightActionBarLayout
            r0.showLastFragment()
        L_0x0022:
            return r12
        L_0x0023:
            boolean r0 = im.bclpbkiauv.ui.PhotoViewer.hasInstance()
            r11 = 0
            if (r0 == 0) goto L_0x0049
            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.getInstance()
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0049
            if (r14 == 0) goto L_0x0042
            java.lang.String r0 = r68.getAction()
            java.lang.String r1 = "android.intent.action.MAIN"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0049
        L_0x0042:
            im.bclpbkiauv.ui.PhotoViewer r0 = im.bclpbkiauv.ui.PhotoViewer.getInstance()
            r0.closePhoto(r11, r12)
        L_0x0049:
            int r20 = r68.getFlags()
            int[] r0 = new int[r12]
            int r1 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            java.lang.String r2 = "currentAccount"
            int r1 = r14.getIntExtra(r2, r1)
            r0[r11] = r1
            r10 = r0
            r0 = r10[r11]
            r15.switchToAccount(r0, r12)
            if (r71 != 0) goto L_0x0084
            boolean r0 = im.bclpbkiauv.messenger.AndroidUtilities.needShowPasscode(r12)
            if (r0 != 0) goto L_0x006f
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x006c
            goto L_0x006f
        L_0x006c:
            r9 = r69
            goto L_0x0086
        L_0x006f:
            r67.showPasscodeActivity()
            r15.passcodeSaveIntent = r14
            r9 = r69
            r15.passcodeSaveIntentIsNew = r9
            r15.passcodeSaveIntentIsRestore = r13
            int r0 = r15.currentAccount
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)
            r0.saveConfig(r11)
            return r11
        L_0x0084:
            r9 = r69
        L_0x0086:
            r21 = 0
            r1 = 0
            r2 = 0
            r22 = 0
            r3 = 0
            r23 = 0
            r24 = 0
            r4 = 0
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.directShare
            r7 = 0
            java.lang.String r6 = "hash"
            if (r0 == 0) goto L_0x00c3
            if (r14 == 0) goto L_0x00c3
            android.os.Bundle r0 = r68.getExtras()
            if (r0 == 0) goto L_0x00c3
            android.os.Bundle r0 = r68.getExtras()
            java.lang.String r12 = "dialogId"
            long r4 = r0.getLong(r12, r7)
            android.os.Bundle r0 = r68.getExtras()
            long r17 = r0.getLong(r6, r7)
            long r25 = im.bclpbkiauv.messenger.SharedConfig.directShareHash
            int r0 = (r17 > r25 ? 1 : (r17 == r25 ? 0 : -1))
            if (r0 == 0) goto L_0x00c0
            r4 = 0
            r25 = r4
            goto L_0x00c5
        L_0x00c0:
            r25 = r4
            goto L_0x00c5
        L_0x00c3:
            r25 = r4
        L_0x00c5:
            r27 = 0
            r28 = 0
            r29 = 0
            r12 = 0
            r15.photoPathsArray = r12
            r15.videoPath = r12
            r15.sendingText = r12
            r15.documentsPathsArray = r12
            r15.documentsOriginalPathsArray = r12
            r15.documentsMimeType = r12
            r15.documentsUrisArray = r12
            r15.contactsToSend = r12
            r15.contactsToSendUri = r12
            r0 = 1048576(0x100000, float:1.469368E-39)
            r0 = r20 & r0
            java.lang.String r5 = "message_id"
            if (r0 != 0) goto L_0x10ae
            if (r14 == 0) goto L_0x109f
            java.lang.String r0 = r68.getAction()
            if (r0 == 0) goto L_0x109f
            if (r13 != 0) goto L_0x109f
            java.lang.String r0 = r68.getAction()
            java.lang.String r4 = "android.intent.action.SEND"
            boolean r0 = r4.equals(r0)
            java.lang.String r4 = "android.intent.extra.STREAM"
            java.lang.String r7 = "\n"
            java.lang.String r8 = ""
            if (r0 == 0) goto L_0x0260
            r6 = 0
            java.lang.String r11 = r68.getType()
            if (r11 == 0) goto L_0x0136
            java.lang.String r0 = "text/x-vcard"
            boolean r0 = r11.equals(r0)
            if (r0 == 0) goto L_0x0136
            android.os.Bundle r0 = r68.getExtras()     // Catch:{ Exception -> 0x012d }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ Exception -> 0x012d }
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x012d }
            if (r0 == 0) goto L_0x012a
            int r4 = r15.currentAccount     // Catch:{ Exception -> 0x012d }
            r7 = 0
            java.util.ArrayList r4 = im.bclpbkiauv.messenger.AndroidUtilities.loadVCardFromStream(r0, r4, r7, r12, r12)     // Catch:{ Exception -> 0x012d }
            r15.contactsToSend = r4     // Catch:{ Exception -> 0x012d }
            r15.contactsToSendUri = r0     // Catch:{ Exception -> 0x012d }
            goto L_0x0132
        L_0x012a:
            r4 = 1
            r6 = r4
            goto L_0x0132
        L_0x012d:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r6 = 1
        L_0x0132:
            r19 = r1
            goto L_0x024b
        L_0x0136:
            java.lang.String r0 = "android.intent.extra.TEXT"
            java.lang.String r0 = r14.getStringExtra(r0)
            if (r0 != 0) goto L_0x014a
            java.lang.String r12 = "android.intent.extra.TEXT"
            java.lang.CharSequence r12 = r14.getCharSequenceExtra(r12)
            if (r12 == 0) goto L_0x014a
            java.lang.String r0 = r12.toString()
        L_0x014a:
            java.lang.String r12 = "android.intent.extra.SUBJECT"
            java.lang.String r12 = r14.getStringExtra(r12)
            boolean r19 = android.text.TextUtils.isEmpty(r0)
            if (r19 != 0) goto L_0x0183
            r19 = r1
            java.lang.String r1 = "http://"
            boolean r1 = r0.startsWith(r1)
            if (r1 != 0) goto L_0x0168
            java.lang.String r1 = "https://"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x0180
        L_0x0168:
            boolean r1 = android.text.TextUtils.isEmpty(r12)
            if (r1 != 0) goto L_0x0180
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r12)
            r1.append(r7)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x0180:
            r15.sendingText = r0
            goto L_0x018d
        L_0x0183:
            r19 = r1
            boolean r1 = android.text.TextUtils.isEmpty(r12)
            if (r1 != 0) goto L_0x018d
            r15.sendingText = r12
        L_0x018d:
            android.os.Parcelable r1 = r14.getParcelableExtra(r4)
            if (r1 == 0) goto L_0x0242
            boolean r4 = r1 instanceof android.net.Uri
            if (r4 != 0) goto L_0x019f
            java.lang.String r4 = r1.toString()
            android.net.Uri r1 = android.net.Uri.parse(r4)
        L_0x019f:
            r4 = r1
            android.net.Uri r4 = (android.net.Uri) r4
            if (r4 == 0) goto L_0x01ab
            boolean r7 = im.bclpbkiauv.messenger.AndroidUtilities.isInternalUri(r4)
            if (r7 == 0) goto L_0x01ab
            r6 = 1
        L_0x01ab:
            if (r6 != 0) goto L_0x023f
            if (r4 == 0) goto L_0x01e7
            if (r11 == 0) goto L_0x01bd
            java.lang.String r7 = "image/"
            boolean r7 = r11.startsWith(r7)
            if (r7 != 0) goto L_0x01ba
            goto L_0x01bd
        L_0x01ba:
            r32 = r0
            goto L_0x01cf
        L_0x01bd:
            java.lang.String r7 = r4.toString()
            java.lang.String r7 = r7.toLowerCase()
            r32 = r0
            java.lang.String r0 = ".jpg"
            boolean r0 = r7.endsWith(r0)
            if (r0 == 0) goto L_0x01e9
        L_0x01cf:
            java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo> r0 = r15.photoPathsArray
            if (r0 != 0) goto L_0x01da
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15.photoPathsArray = r0
        L_0x01da:
            im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo r0 = new im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo
            r0.<init>()
            r0.uri = r4
            java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo> r7 = r15.photoPathsArray
            r7.add(r0)
            goto L_0x024a
        L_0x01e7:
            r32 = r0
        L_0x01e9:
            java.lang.String r0 = im.bclpbkiauv.messenger.AndroidUtilities.getPath(r4)
            if (r0 == 0) goto L_0x022c
            java.lang.String r7 = "file:"
            boolean r7 = r0.startsWith(r7)
            if (r7 == 0) goto L_0x01fd
            java.lang.String r7 = "file://"
            java.lang.String r0 = r0.replace(r7, r8)
        L_0x01fd:
            if (r11 == 0) goto L_0x020b
            java.lang.String r7 = "video/"
            boolean r7 = r11.startsWith(r7)
            if (r7 == 0) goto L_0x020b
            r15.videoPath = r0
            goto L_0x024a
        L_0x020b:
            java.util.ArrayList<java.lang.String> r7 = r15.documentsPathsArray
            if (r7 != 0) goto L_0x021d
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r15.documentsPathsArray = r7
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r15.documentsOriginalPathsArray = r7
        L_0x021d:
            java.util.ArrayList<java.lang.String> r7 = r15.documentsPathsArray
            r7.add(r0)
            java.util.ArrayList<java.lang.String> r7 = r15.documentsOriginalPathsArray
            java.lang.String r8 = r4.toString()
            r7.add(r8)
            goto L_0x024a
        L_0x022c:
            java.util.ArrayList<android.net.Uri> r7 = r15.documentsUrisArray
            if (r7 != 0) goto L_0x0237
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r15.documentsUrisArray = r7
        L_0x0237:
            java.util.ArrayList<android.net.Uri> r7 = r15.documentsUrisArray
            r7.add(r4)
            r15.documentsMimeType = r11
            goto L_0x024a
        L_0x023f:
            r32 = r0
            goto L_0x024a
        L_0x0242:
            r32 = r0
            java.lang.String r0 = r15.sendingText
            if (r0 != 0) goto L_0x024a
            r6 = 1
            goto L_0x024b
        L_0x024a:
        L_0x024b:
            if (r6 == 0) goto L_0x0252
            java.lang.String r0 = "Unsupported content"
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((java.lang.CharSequence) r0)
        L_0x0252:
            r47 = r2
            r50 = r3
            r63 = r5
            r6 = r10
            r1 = r14
            r5 = r15
            r2 = 0
            r48 = 0
            goto L_0x10bc
        L_0x0260:
            r19 = r1
            java.lang.String r0 = r68.getAction()
            java.lang.String r1 = "android.intent.action.SEND_MULTIPLE"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x03a3
            r1 = 0
            java.util.ArrayList r0 = r14.getParcelableArrayListExtra(r4)     // Catch:{ Exception -> 0x0387 }
            java.lang.String r4 = r68.getType()     // Catch:{ Exception -> 0x0387 }
            if (r0 == 0) goto L_0x02b3
            r6 = 0
        L_0x027a:
            int r7 = r0.size()     // Catch:{ Exception -> 0x02ae }
            if (r6 >= r7) goto L_0x02a6
            java.lang.Object r7 = r0.get(r6)     // Catch:{ Exception -> 0x02ae }
            android.os.Parcelable r7 = (android.os.Parcelable) r7     // Catch:{ Exception -> 0x02ae }
            boolean r11 = r7 instanceof android.net.Uri     // Catch:{ Exception -> 0x02ae }
            if (r11 != 0) goto L_0x0293
            java.lang.String r11 = r7.toString()     // Catch:{ Exception -> 0x02ae }
            android.net.Uri r11 = android.net.Uri.parse(r11)     // Catch:{ Exception -> 0x02ae }
            r7 = r11
        L_0x0293:
            r11 = r7
            android.net.Uri r11 = (android.net.Uri) r11     // Catch:{ Exception -> 0x02ae }
            if (r11 == 0) goto L_0x02a3
            boolean r12 = im.bclpbkiauv.messenger.AndroidUtilities.isInternalUri(r11)     // Catch:{ Exception -> 0x02ae }
            if (r12 == 0) goto L_0x02a3
            r0.remove(r6)     // Catch:{ Exception -> 0x02ae }
            int r6 = r6 + -1
        L_0x02a3:
            r7 = 1
            int r6 = r6 + r7
            goto L_0x027a
        L_0x02a6:
            boolean r6 = r0.isEmpty()     // Catch:{ Exception -> 0x02ae }
            if (r6 == 0) goto L_0x02b3
            r0 = 0
            goto L_0x02b3
        L_0x02ae:
            r0 = move-exception
            r32 = r1
            goto L_0x038a
        L_0x02b3:
            if (r0 == 0) goto L_0x0381
            if (r4 == 0) goto L_0x02fb
            java.lang.String r6 = "image/"
            boolean r6 = r4.startsWith(r6)     // Catch:{ Exception -> 0x02ae }
            if (r6 == 0) goto L_0x02fb
            r6 = 0
        L_0x02c0:
            int r7 = r0.size()     // Catch:{ Exception -> 0x02ae }
            if (r6 >= r7) goto L_0x02f7
            java.lang.Object r7 = r0.get(r6)     // Catch:{ Exception -> 0x02ae }
            android.os.Parcelable r7 = (android.os.Parcelable) r7     // Catch:{ Exception -> 0x02ae }
            boolean r8 = r7 instanceof android.net.Uri     // Catch:{ Exception -> 0x02ae }
            if (r8 != 0) goto L_0x02d9
            java.lang.String r8 = r7.toString()     // Catch:{ Exception -> 0x02ae }
            android.net.Uri r8 = android.net.Uri.parse(r8)     // Catch:{ Exception -> 0x02ae }
            r7 = r8
        L_0x02d9:
            r8 = r7
            android.net.Uri r8 = (android.net.Uri) r8     // Catch:{ Exception -> 0x02ae }
            java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo> r11 = r15.photoPathsArray     // Catch:{ Exception -> 0x02ae }
            if (r11 != 0) goto L_0x02e7
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ Exception -> 0x02ae }
            r11.<init>()     // Catch:{ Exception -> 0x02ae }
            r15.photoPathsArray = r11     // Catch:{ Exception -> 0x02ae }
        L_0x02e7:
            im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo r11 = new im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo     // Catch:{ Exception -> 0x02ae }
            r11.<init>()     // Catch:{ Exception -> 0x02ae }
            r11.uri = r8     // Catch:{ Exception -> 0x02ae }
            java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo> r12 = r15.photoPathsArray     // Catch:{ Exception -> 0x02ae }
            r12.add(r11)     // Catch:{ Exception -> 0x02ae }
            int r6 = r6 + 1
            goto L_0x02c0
        L_0x02f7:
            r32 = r1
            goto L_0x037e
        L_0x02fb:
            r6 = 0
        L_0x02fc:
            int r7 = r0.size()     // Catch:{ Exception -> 0x0387 }
            if (r6 >= r7) goto L_0x037a
            java.lang.Object r7 = r0.get(r6)     // Catch:{ Exception -> 0x0387 }
            android.os.Parcelable r7 = (android.os.Parcelable) r7     // Catch:{ Exception -> 0x0387 }
            boolean r11 = r7 instanceof android.net.Uri     // Catch:{ Exception -> 0x0387 }
            if (r11 != 0) goto L_0x0315
            java.lang.String r11 = r7.toString()     // Catch:{ Exception -> 0x02ae }
            android.net.Uri r11 = android.net.Uri.parse(r11)     // Catch:{ Exception -> 0x02ae }
            r7 = r11
        L_0x0315:
            r11 = r7
            android.net.Uri r11 = (android.net.Uri) r11     // Catch:{ Exception -> 0x0387 }
            java.lang.String r12 = im.bclpbkiauv.messenger.AndroidUtilities.getPath(r11)     // Catch:{ Exception -> 0x0387 }
            java.lang.String r32 = r7.toString()     // Catch:{ Exception -> 0x0387 }
            if (r32 != 0) goto L_0x0329
            r32 = r12
            r33 = r0
            r0 = r32
            goto L_0x032d
        L_0x0329:
            r33 = r0
            r0 = r32
        L_0x032d:
            if (r12 == 0) goto L_0x035d
            r32 = r1
            java.lang.String r1 = "file:"
            boolean r1 = r12.startsWith(r1)     // Catch:{ Exception -> 0x0378 }
            if (r1 == 0) goto L_0x0340
            java.lang.String r1 = "file://"
            java.lang.String r1 = r12.replace(r1, r8)     // Catch:{ Exception -> 0x0378 }
            r12 = r1
        L_0x0340:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray     // Catch:{ Exception -> 0x0378 }
            if (r1 != 0) goto L_0x0352
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0378 }
            r1.<init>()     // Catch:{ Exception -> 0x0378 }
            r15.documentsPathsArray = r1     // Catch:{ Exception -> 0x0378 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0378 }
            r1.<init>()     // Catch:{ Exception -> 0x0378 }
            r15.documentsOriginalPathsArray = r1     // Catch:{ Exception -> 0x0378 }
        L_0x0352:
            java.util.ArrayList<java.lang.String> r1 = r15.documentsPathsArray     // Catch:{ Exception -> 0x0378 }
            r1.add(r12)     // Catch:{ Exception -> 0x0378 }
            java.util.ArrayList<java.lang.String> r1 = r15.documentsOriginalPathsArray     // Catch:{ Exception -> 0x0378 }
            r1.add(r0)     // Catch:{ Exception -> 0x0378 }
            goto L_0x0371
        L_0x035d:
            r32 = r1
            java.util.ArrayList<android.net.Uri> r1 = r15.documentsUrisArray     // Catch:{ Exception -> 0x0378 }
            if (r1 != 0) goto L_0x036a
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0378 }
            r1.<init>()     // Catch:{ Exception -> 0x0378 }
            r15.documentsUrisArray = r1     // Catch:{ Exception -> 0x0378 }
        L_0x036a:
            java.util.ArrayList<android.net.Uri> r1 = r15.documentsUrisArray     // Catch:{ Exception -> 0x0378 }
            r1.add(r11)     // Catch:{ Exception -> 0x0378 }
            r15.documentsMimeType = r4     // Catch:{ Exception -> 0x0378 }
        L_0x0371:
            int r6 = r6 + 1
            r1 = r32
            r0 = r33
            goto L_0x02fc
        L_0x0378:
            r0 = move-exception
            goto L_0x038a
        L_0x037a:
            r33 = r0
            r32 = r1
        L_0x037e:
            r1 = r32
            goto L_0x0386
        L_0x0381:
            r33 = r0
            r32 = r1
            r1 = 1
        L_0x0386:
            goto L_0x038e
        L_0x0387:
            r0 = move-exception
            r32 = r1
        L_0x038a:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 1
        L_0x038e:
            if (r1 == 0) goto L_0x0395
            java.lang.String r0 = "Unsupported content"
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((java.lang.CharSequence) r0)
        L_0x0395:
            r47 = r2
            r50 = r3
            r63 = r5
            r6 = r10
            r1 = r14
            r5 = r15
            r2 = 0
            r48 = 0
            goto L_0x10bc
        L_0x03a3:
            java.lang.String r0 = r68.getAction()
            java.lang.String r1 = "android.intent.action.VIEW"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0fa6
            android.net.Uri r1 = r68.getData()
            if (r1 == 0) goto L_0x0f8d
            r4 = 0
            r11 = 0
            r12 = 0
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            r40 = 0
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            r45 = 0
            r46 = 0
            java.lang.String r14 = r1.getScheme()
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0403
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r47 = r2
            java.lang.String r2 = "handleIntent() ===>  , uri = "
            r0.append(r2)
            java.lang.String r2 = r1.toString()
            r0.append(r2)
            java.lang.String r2 = " , scheme = "
            r0.append(r2)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "OpenApp"
            android.util.Log.i(r2, r0)
            goto L_0x0405
        L_0x0403:
            r47 = r2
        L_0x0405:
            if (r14 == 0) goto L_0x0df5
            java.lang.String r0 = "http"
            boolean r0 = r14.equals(r0)
            r48 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            java.lang.String r2 = "text"
            r50 = r3
            if (r0 != 0) goto L_0x0a10
            java.lang.String r0 = "https"
            boolean r0 = r14.equals(r0)
            if (r0 == 0) goto L_0x042a
            r51 = r4
            r55 = r10
            r52 = r11
            r53 = r12
            r54 = r14
            goto L_0x0a1a
        L_0x042a:
            java.lang.String r0 = "hchat"
            boolean r0 = r14.equals(r0)
            if (r0 == 0) goto L_0x0a03
            java.lang.String r0 = r1.toString()
            java.lang.String r3 = "hchat:resolve"
            boolean r3 = r0.startsWith(r3)
            r51 = r4
            java.lang.String r4 = "nonce"
            java.lang.String r9 = "callback_url"
            r52 = r11
            java.lang.String r11 = "public_key"
            r53 = r12
            java.lang.String r12 = "bot_id"
            java.lang.String r13 = "payload"
            r54 = r14
            java.lang.String r14 = "scope"
            r55 = r10
            java.lang.String r10 = "hchat://m12345.cc"
            if (r3 != 0) goto L_0x092f
            java.lang.String r3 = "hchat://resolve"
            boolean r3 = r0.startsWith(r3)
            if (r3 == 0) goto L_0x0460
            goto L_0x092f
        L_0x0460:
            java.lang.String r3 = "hchat:privatepost"
            boolean r3 = r0.startsWith(r3)
            if (r3 != 0) goto L_0x08d0
            java.lang.String r3 = "hchat://privatepost"
            boolean r3 = r0.startsWith(r3)
            if (r3 == 0) goto L_0x0472
            goto L_0x08d0
        L_0x0472:
            java.lang.String r3 = "hchat:bg"
            boolean r3 = r0.startsWith(r3)
            if (r3 != 0) goto L_0x07f8
            java.lang.String r3 = "hchat://bg"
            boolean r3 = r0.startsWith(r3)
            if (r3 == 0) goto L_0x0484
            goto L_0x07f8
        L_0x0484:
            java.lang.String r3 = "hchat:join"
            boolean r3 = r0.startsWith(r3)
            if (r3 != 0) goto L_0x07cb
            java.lang.String r3 = "hchat://join"
            boolean r3 = r0.startsWith(r3)
            if (r3 == 0) goto L_0x0496
            goto L_0x07cb
        L_0x0496:
            java.lang.String r3 = "hchat:addstickers"
            boolean r3 = r0.startsWith(r3)
            if (r3 != 0) goto L_0x079e
            java.lang.String r3 = "hchat://addstickers"
            boolean r3 = r0.startsWith(r3)
            if (r3 == 0) goto L_0x04a8
            goto L_0x079e
        L_0x04a8:
            java.lang.String r3 = "hchat:msg"
            boolean r3 = r0.startsWith(r3)
            if (r3 != 0) goto L_0x0714
            java.lang.String r3 = "hchat://msg"
            boolean r3 = r0.startsWith(r3)
            if (r3 != 0) goto L_0x0714
            java.lang.String r3 = "hchat://share"
            boolean r3 = r0.startsWith(r3)
            if (r3 != 0) goto L_0x0714
            java.lang.String r3 = "hchat:share"
            boolean r3 = r0.startsWith(r3)
            if (r3 == 0) goto L_0x04ca
            goto L_0x0714
        L_0x04ca:
            java.lang.String r2 = "hchat:confirmphone"
            boolean r2 = r0.startsWith(r2)
            if (r2 != 0) goto L_0x06e5
            java.lang.String r2 = "hchat://confirmphone"
            boolean r2 = r0.startsWith(r2)
            if (r2 == 0) goto L_0x04dc
            goto L_0x06e5
        L_0x04dc:
            java.lang.String r2 = "hchat:login"
            boolean r2 = r0.startsWith(r2)
            if (r2 != 0) goto L_0x06ba
            java.lang.String r2 = "hchat://login"
            boolean r2 = r0.startsWith(r2)
            if (r2 == 0) goto L_0x04ee
            goto L_0x06ba
        L_0x04ee:
            java.lang.String r2 = "hchat:openmessage"
            boolean r2 = r0.startsWith(r2)
            if (r2 != 0) goto L_0x0662
            java.lang.String r2 = "hchat://openmessage"
            boolean r2 = r0.startsWith(r2)
            if (r2 == 0) goto L_0x0500
            goto L_0x0662
        L_0x0500:
            java.lang.String r2 = "hchat:passport"
            boolean r2 = r0.startsWith(r2)
            if (r2 != 0) goto L_0x05ef
            java.lang.String r2 = "hchat://passport"
            boolean r2 = r0.startsWith(r2)
            if (r2 != 0) goto L_0x05ef
            java.lang.String r2 = "hchat:secureid"
            boolean r2 = r0.startsWith(r2)
            if (r2 == 0) goto L_0x051a
            goto L_0x05ef
        L_0x051a:
            java.lang.String r2 = "hchat:setlanguage"
            boolean r2 = r0.startsWith(r2)
            if (r2 != 0) goto L_0x05c4
            java.lang.String r2 = "hchat://setlanguage"
            boolean r2 = r0.startsWith(r2)
            if (r2 == 0) goto L_0x052c
            goto L_0x05c4
        L_0x052c:
            java.lang.String r2 = "hchat:addtheme"
            boolean r2 = r0.startsWith(r2)
            if (r2 != 0) goto L_0x0598
            java.lang.String r2 = "hchat://addtheme"
            boolean r2 = r0.startsWith(r2)
            if (r2 == 0) goto L_0x053d
            goto L_0x0598
        L_0x053d:
            java.lang.String r2 = "hchat:openKey"
            boolean r2 = r0.startsWith(r2)
            if (r2 != 0) goto L_0x0592
            java.lang.String r2 = "hchat://openKey"
            boolean r2 = r0.startsWith(r2)
            if (r2 == 0) goto L_0x054e
            goto L_0x0592
        L_0x054e:
            java.lang.String r2 = "hchat://"
            java.lang.String r2 = r0.replace(r2, r8)
            java.lang.String r3 = "hchat:"
            java.lang.String r2 = r2.replace(r3, r8)
            r3 = 63
            int r3 = r2.indexOf(r3)
            r4 = r3
            if (r3 < 0) goto L_0x057d
            r3 = 0
            java.lang.String r33 = r2.substring(r3, r4)
            r31 = r1
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x057d:
            r31 = r1
            r33 = r32
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r32 = r19
            r36 = r35
            r35 = r34
            r34 = r2
            goto L_0x0e14
        L_0x0592:
            r15.parseSechmeOpenAccount(r0)
            r12 = 0
            goto L_0x0e02
        L_0x0598:
            java.lang.String r2 = "hchat:addtheme"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://addtheme"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r2 = "slug"
            java.lang.String r41 = r1.getQueryParameter(r2)
            r31 = r1
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x05c4:
            java.lang.String r2 = "hchat:setlanguage"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://setlanguage"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r2 = "lang"
            java.lang.String r40 = r1.getQueryParameter(r2)
            r31 = r1
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x05ef:
            java.lang.String r2 = "hchat:passport"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://passport"
            java.lang.String r2 = r2.replace(r3, r10)
            java.lang.String r3 = "hchat:secureid"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.lang.String r3 = r1.getQueryParameter(r14)
            boolean r7 = android.text.TextUtils.isEmpty(r3)
            if (r7 != 0) goto L_0x062e
            java.lang.String r7 = "{"
            boolean r7 = r3.startsWith(r7)
            if (r7 == 0) goto L_0x062e
            java.lang.String r7 = "}"
            boolean r7 = r3.endsWith(r7)
            if (r7 == 0) goto L_0x062e
            java.lang.String r7 = r1.getQueryParameter(r4)
            r2.put(r4, r7)
            goto L_0x0635
        L_0x062e:
            java.lang.String r4 = r1.getQueryParameter(r13)
            r2.put(r13, r4)
        L_0x0635:
            java.lang.String r4 = r1.getQueryParameter(r12)
            r2.put(r12, r4)
            r2.put(r14, r3)
            java.lang.String r4 = r1.getQueryParameter(r11)
            r2.put(r11, r4)
            java.lang.String r4 = r1.getQueryParameter(r9)
            r2.put(r9, r4)
            r31 = r1
            r32 = r19
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r2
            goto L_0x0e14
        L_0x0662:
            java.lang.String r2 = "hchat:openmessage"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://openmessage"
            java.lang.String r2 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r2)
            java.lang.String r0 = "user_id"
            java.lang.String r3 = r1.getQueryParameter(r0)
            java.lang.String r0 = "chat_id"
            java.lang.String r4 = r1.getQueryParameter(r0)
            java.lang.String r7 = r1.getQueryParameter(r5)
            if (r3 == 0) goto L_0x068e
            int r0 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x068c }
            r19 = r0
            goto L_0x0698
        L_0x068c:
            r0 = move-exception
            goto L_0x0698
        L_0x068e:
            if (r4 == 0) goto L_0x0698
            int r0 = java.lang.Integer.parseInt(r4)     // Catch:{ NumberFormatException -> 0x0697 }
            r47 = r0
            goto L_0x0698
        L_0x0697:
            r0 = move-exception
        L_0x0698:
            if (r7 == 0) goto L_0x06a1
            int r0 = java.lang.Integer.parseInt(r7)     // Catch:{ NumberFormatException -> 0x06a0 }
            r3 = r0
            goto L_0x06a3
        L_0x06a0:
            r0 = move-exception
        L_0x06a1:
            r3 = r50
        L_0x06a3:
            r31 = r1
            r50 = r3
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x06ba:
            java.lang.String r2 = "hchat:login"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://login"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r2 = "code"
            java.lang.String r42 = r1.getQueryParameter(r2)
            r31 = r1
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x06e5:
            java.lang.String r2 = "hchat:confirmphone"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://confirmphone"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r2 = "phone"
            java.lang.String r37 = r1.getQueryParameter(r2)
            java.lang.String r39 = r1.getQueryParameter(r6)
            r31 = r1
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x0714:
            java.lang.String r3 = "hchat:msg"
            java.lang.String r3 = r0.replace(r3, r10)
            java.lang.String r4 = "hchat://msg"
            java.lang.String r3 = r3.replace(r4, r10)
            java.lang.String r4 = "hchat://share"
            java.lang.String r3 = r3.replace(r4, r10)
            java.lang.String r4 = "hchat:share"
            java.lang.String r0 = r3.replace(r4, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r3 = "url"
            java.lang.String r3 = r1.getQueryParameter(r3)
            if (r3 != 0) goto L_0x073b
            java.lang.String r3 = ""
        L_0x073b:
            java.lang.String r4 = r1.getQueryParameter(r2)
            if (r4 == 0) goto L_0x076b
            int r4 = r3.length()
            if (r4 <= 0) goto L_0x0758
            r46 = 1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r7)
            java.lang.String r3 = r4.toString()
        L_0x0758:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r2 = r1.getQueryParameter(r2)
            r4.append(r2)
            java.lang.String r3 = r4.toString()
        L_0x076b:
            int r2 = r3.length()
            r4 = 16384(0x4000, float:2.2959E-41)
            if (r2 <= r4) goto L_0x0779
            r2 = 0
            java.lang.String r3 = r3.substring(r2, r4)
            goto L_0x077a
        L_0x0779:
            r2 = 0
        L_0x077a:
            boolean r4 = r3.endsWith(r7)
            if (r4 == 0) goto L_0x078b
            int r4 = r3.length()
            r8 = 1
            int r4 = r4 - r8
            java.lang.String r3 = r3.substring(r2, r4)
            goto L_0x077a
        L_0x078b:
            r31 = r1
            r36 = r35
            r14 = r37
            r13 = r39
            r12 = 0
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x079e:
            java.lang.String r2 = "hchat:addstickers"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://addstickers"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r2 = "set"
            java.lang.String r12 = r1.getQueryParameter(r2)
            r31 = r1
            r53 = r12
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x07cb:
            java.lang.String r2 = "hchat:join"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://join"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r2 = "invite"
            java.lang.String r11 = r1.getQueryParameter(r2)
            r31 = r1
            r52 = r11
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x07f8:
            java.lang.String r2 = "hchat:bg"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://bg"
            java.lang.String r2 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r2)
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper
            r0.<init>()
            r3 = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings
            r0.<init>()
            r3.settings = r0
            java.lang.String r0 = "slug"
            java.lang.String r0 = r1.getQueryParameter(r0)
            r3.slug = r0
            java.lang.String r0 = r3.slug
            if (r0 != 0) goto L_0x082a
            java.lang.String r0 = "color"
            java.lang.String r0 = r1.getQueryParameter(r0)
            r3.slug = r0
        L_0x082a:
            java.lang.String r0 = r3.slug
            if (r0 == 0) goto L_0x084c
            java.lang.String r0 = r3.slug
            int r0 = r0.length()
            r4 = 6
            if (r0 != r4) goto L_0x084c
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r0 = r3.settings     // Catch:{ Exception -> 0x0846 }
            java.lang.String r4 = r3.slug     // Catch:{ Exception -> 0x0846 }
            r7 = 16
            int r4 = java.lang.Integer.parseInt(r4, r7)     // Catch:{ Exception -> 0x0846 }
            r4 = r4 | r48
            r0.background_color = r4     // Catch:{ Exception -> 0x0846 }
            goto L_0x0847
        L_0x0846:
            r0 = move-exception
        L_0x0847:
            r4 = 0
            r3.slug = r4
            goto L_0x08b9
        L_0x084c:
            java.lang.String r0 = "mode"
            java.lang.String r0 = r1.getQueryParameter(r0)
            if (r0 == 0) goto L_0x088b
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r4 = " "
            java.lang.String[] r4 = r0.split(r4)
            if (r4 == 0) goto L_0x0889
            int r7 = r4.length
            if (r7 <= 0) goto L_0x0889
            r7 = 0
        L_0x0864:
            int r8 = r4.length
            if (r7 >= r8) goto L_0x0889
            r8 = r4[r7]
            java.lang.String r9 = "blur"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x0877
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r8 = r3.settings
            r9 = 1
            r8.blur = r9
            goto L_0x0886
        L_0x0877:
            r9 = 1
            r8 = r4[r7]
            java.lang.String r10 = "motion"
            boolean r8 = r10.equals(r8)
            if (r8 == 0) goto L_0x0886
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r8 = r3.settings
            r8.motion = r9
        L_0x0886:
            int r7 = r7 + 1
            goto L_0x0864
        L_0x0889:
            r4 = r0
            goto L_0x088c
        L_0x088b:
            r4 = r0
        L_0x088c:
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r0 = r3.settings
            java.lang.String r7 = "intensity"
            java.lang.String r7 = r1.getQueryParameter(r7)
            java.lang.Integer r7 = im.bclpbkiauv.messenger.Utilities.parseInt(r7)
            int r7 = r7.intValue()
            r0.intensity = r7
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r1.getQueryParameter(r0)     // Catch:{ Exception -> 0x08b7 }
            boolean r7 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x08b7 }
            if (r7 != 0) goto L_0x08b6
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r7 = r3.settings     // Catch:{ Exception -> 0x08b7 }
            r8 = 16
            int r8 = java.lang.Integer.parseInt(r0, r8)     // Catch:{ Exception -> 0x08b7 }
            r8 = r8 | r48
            r7.background_color = r8     // Catch:{ Exception -> 0x08b7 }
        L_0x08b6:
            goto L_0x08b8
        L_0x08b7:
            r0 = move-exception
        L_0x08b8:
        L_0x08b9:
            r31 = r1
            r43 = r3
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x08d0:
            java.lang.String r2 = "hchat:privatepost"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://privatepost"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r2 = "post"
            java.lang.String r2 = r1.getQueryParameter(r2)
            java.lang.Integer r44 = im.bclpbkiauv.messenger.Utilities.parseInt(r2)
            java.lang.String r2 = "channel"
            java.lang.String r2 = r1.getQueryParameter(r2)
            java.lang.Integer r45 = im.bclpbkiauv.messenger.Utilities.parseInt(r2)
            int r2 = r44.intValue()
            if (r2 == 0) goto L_0x0916
            int r2 = r45.intValue()
            if (r2 != 0) goto L_0x0901
            goto L_0x0916
        L_0x0901:
            r31 = r1
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x0916:
            r44 = 0
            r45 = 0
            r31 = r1
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x092f:
            java.lang.String r2 = "hchat:resolve"
            java.lang.String r2 = r0.replace(r2, r10)
            java.lang.String r3 = "hchat://resolve"
            java.lang.String r0 = r2.replace(r3, r10)
            android.net.Uri r1 = android.net.Uri.parse(r0)
            java.lang.String r2 = "domain"
            java.lang.String r2 = r1.getQueryParameter(r2)
            java.lang.String r3 = "hchatpassport"
            boolean r3 = r3.equals(r2)
            if (r3 == 0) goto L_0x09af
            r2 = 0
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            java.lang.String r7 = r1.getQueryParameter(r14)
            boolean r8 = android.text.TextUtils.isEmpty(r7)
            if (r8 != 0) goto L_0x0977
            java.lang.String r8 = "{"
            boolean r8 = r7.startsWith(r8)
            if (r8 == 0) goto L_0x0977
            java.lang.String r8 = "}"
            boolean r8 = r7.endsWith(r8)
            if (r8 == 0) goto L_0x0977
            java.lang.String r8 = r1.getQueryParameter(r4)
            r3.put(r4, r8)
            goto L_0x097e
        L_0x0977:
            java.lang.String r4 = r1.getQueryParameter(r13)
            r3.put(r13, r4)
        L_0x097e:
            java.lang.String r4 = r1.getQueryParameter(r12)
            r3.put(r12, r4)
            r3.put(r14, r7)
            java.lang.String r4 = r1.getQueryParameter(r11)
            r3.put(r11, r4)
            java.lang.String r4 = r1.getQueryParameter(r9)
            r3.put(r9, r4)
            r31 = r1
            r51 = r2
            r32 = r19
            r14 = r37
            r13 = r39
            r12 = 0
            r66 = r33
            r33 = r3
            r3 = r36
            r36 = r35
            r35 = r34
            r34 = r66
            goto L_0x0e14
        L_0x09af:
            java.lang.String r3 = "start"
            java.lang.String r34 = r1.getQueryParameter(r3)
            java.lang.String r3 = "startgroup"
            java.lang.String r35 = r1.getQueryParameter(r3)
            java.lang.String r3 = "game"
            java.lang.String r38 = r1.getQueryParameter(r3)
            java.lang.String r3 = "post"
            java.lang.String r3 = r1.getQueryParameter(r3)
            java.lang.Integer r44 = im.bclpbkiauv.messenger.Utilities.parseInt(r3)
            int r3 = r44.intValue()
            if (r3 != 0) goto L_0x09ec
            r44 = 0
            r31 = r1
            r51 = r2
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x09ec:
            r31 = r1
            r51 = r2
            r3 = r36
            r14 = r37
            r13 = r39
            r12 = 0
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x0a03:
            r51 = r4
            r55 = r10
            r52 = r11
            r53 = r12
            r54 = r14
            r12 = 0
            goto L_0x0e02
        L_0x0a10:
            r51 = r4
            r55 = r10
            r52 = r11
            r53 = r12
            r54 = r14
        L_0x0a1a:
            java.lang.String r0 = r1.getHost()
            java.lang.String r3 = r0.toLowerCase()
            java.lang.String r0 = "www.shareinstall.com.cn"
            boolean r0 = r3.equals(r0)
            java.lang.String r4 = "="
            if (r0 == 0) goto L_0x0aba
            java.lang.String r0 = "Key"
            java.lang.String r0 = r1.getQueryParameter(r0)
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0ab7
            java.lang.String r2 = "%3D"
            java.lang.String r2 = r0.replace(r2, r4)
            r7 = 0
            byte[] r8 = android.util.Base64.decode(r2, r7)
            java.lang.String r0 = new java.lang.String
            r0.<init>(r8)
            r9 = r0
            java.lang.String r0 = "#"
            java.lang.String[] r10 = r9.split(r0)
            r0 = r10[r7]
            java.lang.String[] r0 = r0.split(r4)
            r7 = 1
            r11 = r0[r7]
            r0 = r10[r7]
            java.lang.String[] r0 = r0.split(r4)
            r12 = r0[r7]
            java.lang.String r0 = "Uname"
            boolean r0 = r9.contains(r0)
            if (r0 == 0) goto L_0x0a84
            r0 = 2
            r0 = r10[r0]
            java.lang.String[] r0 = r0.split(r4)
            r0 = r0[r7]
            int r4 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r13 = r67.getActionBarLayout()
            im.bclpbkiauv.ui.actionbar.BaseFragment r13 = r13.getCurrentFragment()
            r4.openByUserName((java.lang.String) r0, (im.bclpbkiauv.ui.actionbar.BaseFragment) r13, (int) r7, (boolean) r7)
            goto L_0x0ab7
        L_0x0a84:
            im.bclpbkiauv.tgnet.TLRPC$TL_user r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_user
            r0.<init>()
            r4 = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r11)     // Catch:{ NumberFormatException -> 0x0aa2 }
            int r0 = r0.intValue()     // Catch:{ NumberFormatException -> 0x0aa2 }
            r4.id = r0     // Catch:{ NumberFormatException -> 0x0aa2 }
            java.lang.Long r0 = java.lang.Long.valueOf(r12)     // Catch:{ NumberFormatException -> 0x0aa2 }
            long r13 = r0.longValue()     // Catch:{ NumberFormatException -> 0x0aa2 }
            r4.access_hash = r13     // Catch:{ NumberFormatException -> 0x0aa2 }
            r15.getUserInfo(r4)     // Catch:{ NumberFormatException -> 0x0aa2 }
            goto L_0x0ab7
        L_0x0aa2:
            r0 = move-exception
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r13 = "parse qr code err:"
            r7.append(r13)
            r7.append(r0)
            java.lang.String r7 = r7.toString()
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r7)
        L_0x0ab7:
            r12 = 0
            goto L_0x0dda
        L_0x0aba:
            java.lang.String r0 = "m12345.cc"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0dd8
            java.lang.String r0 = r1.getPath()
            if (r0 == 0) goto L_0x0dd5
            int r9 = r0.length()
            r10 = 1
            if (r9 <= r10) goto L_0x0dd5
            java.lang.String r9 = r0.substring(r10)
            java.lang.String r0 = "install.html"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0b71
            java.lang.String r0 = "Key"
            java.lang.String r0 = r1.getQueryParameter(r0)
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0b6d
            java.lang.String r2 = "%3D"
            java.lang.String r2 = r0.replace(r2, r4)
            r7 = 0
            byte[] r8 = android.util.Base64.decode(r2, r7)
            java.lang.String r0 = new java.lang.String
            r0.<init>(r8)
            r10 = r0
            java.lang.String r0 = "#"
            java.lang.String[] r11 = r10.split(r0)
            r0 = r11[r7]
            java.lang.String[] r0 = r0.split(r4)
            r7 = 1
            r12 = r0[r7]
            r0 = r11[r7]
            java.lang.String[] r0 = r0.split(r4)
            r13 = r0[r7]
            java.lang.String r0 = "Uname"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x0b33
            r0 = 2
            r0 = r11[r0]
            java.lang.String[] r0 = r0.split(r4)
            r0 = r0[r7]
            int r4 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r14 = r67.getActionBarLayout()
            im.bclpbkiauv.ui.actionbar.BaseFragment r14 = r14.getCurrentFragment()
            r4.openByUserName((java.lang.String) r0, (im.bclpbkiauv.ui.actionbar.BaseFragment) r14, (int) r7, (boolean) r7)
            r14 = r3
            goto L_0x0b6e
        L_0x0b33:
            im.bclpbkiauv.tgnet.TLRPC$TL_user r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_user
            r0.<init>()
            r4 = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r12)     // Catch:{ NumberFormatException -> 0x0b55 }
            int r0 = r0.intValue()     // Catch:{ NumberFormatException -> 0x0b55 }
            r4.id = r0     // Catch:{ NumberFormatException -> 0x0b55 }
            java.lang.Long r0 = java.lang.Long.valueOf(r13)     // Catch:{ NumberFormatException -> 0x0b55 }
            r7 = r2
            r14 = r3
            long r2 = r0.longValue()     // Catch:{ NumberFormatException -> 0x0b53 }
            r4.access_hash = r2     // Catch:{ NumberFormatException -> 0x0b53 }
            r15.getUserInfo(r4)     // Catch:{ NumberFormatException -> 0x0b53 }
            goto L_0x0b6e
        L_0x0b53:
            r0 = move-exception
            goto L_0x0b58
        L_0x0b55:
            r0 = move-exception
            r7 = r2
            r14 = r3
        L_0x0b58:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "parse qr code err:"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r2)
            goto L_0x0b6e
        L_0x0b6d:
            r14 = r3
        L_0x0b6e:
            r12 = 0
            goto L_0x0dda
        L_0x0b71:
            r14 = r3
            java.lang.String r0 = "bg/"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0c3a
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper
            r0.<init>()
            r2 = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings
            r0.<init>()
            r2.settings = r0
            java.lang.String r0 = "bg/"
            java.lang.String r0 = r9.replace(r0, r8)
            r2.slug = r0
            java.lang.String r0 = r2.slug
            if (r0 == 0) goto L_0x0bb1
            java.lang.String r0 = r2.slug
            int r0 = r0.length()
            r3 = 6
            if (r0 != r3) goto L_0x0bb1
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r0 = r2.settings     // Catch:{ Exception -> 0x0bab }
            java.lang.String r3 = r2.slug     // Catch:{ Exception -> 0x0bab }
            r4 = 16
            int r3 = java.lang.Integer.parseInt(r3, r4)     // Catch:{ Exception -> 0x0bab }
            r3 = r3 | r48
            r0.background_color = r3     // Catch:{ Exception -> 0x0bab }
            goto L_0x0bac
        L_0x0bab:
            r0 = move-exception
        L_0x0bac:
            r12 = 0
            r2.slug = r12
            goto L_0x0c32
        L_0x0bb1:
            r12 = 0
            java.lang.String r0 = "mode"
            java.lang.String r0 = r1.getQueryParameter(r0)
            if (r0 == 0) goto L_0x0bf1
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r3 = " "
            java.lang.String[] r3 = r0.split(r3)
            if (r3 == 0) goto L_0x0bef
            int r4 = r3.length
            if (r4 <= 0) goto L_0x0bef
            r4 = 0
        L_0x0bca:
            int r7 = r3.length
            if (r4 >= r7) goto L_0x0bef
            r7 = r3[r4]
            java.lang.String r8 = "blur"
            boolean r7 = r8.equals(r7)
            if (r7 == 0) goto L_0x0bdd
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r7 = r2.settings
            r8 = 1
            r7.blur = r8
            goto L_0x0bec
        L_0x0bdd:
            r8 = 1
            r7 = r3[r4]
            java.lang.String r10 = "motion"
            boolean r7 = r10.equals(r7)
            if (r7 == 0) goto L_0x0bec
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r7 = r2.settings
            r7.motion = r8
        L_0x0bec:
            int r4 = r4 + 1
            goto L_0x0bca
        L_0x0bef:
            r3 = r0
            goto L_0x0bf2
        L_0x0bf1:
            r3 = r0
        L_0x0bf2:
            java.lang.String r0 = "intensity"
            java.lang.String r4 = r1.getQueryParameter(r0)
            boolean r0 = android.text.TextUtils.isEmpty(r4)
            if (r0 != 0) goto L_0x0c0b
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r0 = r2.settings
            java.lang.Integer r7 = im.bclpbkiauv.messenger.Utilities.parseInt(r4)
            int r7 = r7.intValue()
            r0.intensity = r7
            goto L_0x0c11
        L_0x0c0b:
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r0 = r2.settings
            r7 = 50
            r0.intensity = r7
        L_0x0c11:
            java.lang.String r0 = "bg_color"
            java.lang.String r0 = r1.getQueryParameter(r0)     // Catch:{ Exception -> 0x0c30 }
            boolean r7 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0c30 }
            if (r7 != 0) goto L_0x0c2a
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x0c30 }
            r8 = 16
            int r8 = java.lang.Integer.parseInt(r0, r8)     // Catch:{ Exception -> 0x0c30 }
            r8 = r8 | r48
            r7.background_color = r8     // Catch:{ Exception -> 0x0c30 }
            goto L_0x0c2f
        L_0x0c2a:
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r7 = r2.settings     // Catch:{ Exception -> 0x0c30 }
            r8 = -1
            r7.background_color = r8     // Catch:{ Exception -> 0x0c30 }
        L_0x0c2f:
            goto L_0x0c31
        L_0x0c30:
            r0 = move-exception
        L_0x0c31:
        L_0x0c32:
            r43 = r2
            r4 = r51
            r11 = r52
            goto L_0x0dde
        L_0x0c3a:
            r12 = 0
            java.lang.String r0 = "login/"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0c4f
            java.lang.String r0 = "login/"
            java.lang.String r42 = r9.replace(r0, r8)
            r4 = r51
            r11 = r52
            goto L_0x0dde
        L_0x0c4f:
            java.lang.String r0 = "joinchat/"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0c61
            java.lang.String r0 = "joinchat/"
            java.lang.String r11 = r9.replace(r0, r8)
            r4 = r51
            goto L_0x0dde
        L_0x0c61:
            java.lang.String r0 = "addstickers/"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0c77
            java.lang.String r0 = "addstickers/"
            java.lang.String r0 = r9.replace(r0, r8)
            r53 = r0
            r4 = r51
            r11 = r52
            goto L_0x0dde
        L_0x0c77:
            java.lang.String r0 = "msg/"
            boolean r0 = r9.startsWith(r0)
            if (r0 != 0) goto L_0x0d73
            java.lang.String r0 = "share/"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0c89
            goto L_0x0d73
        L_0x0c89:
            java.lang.String r0 = "confirmphone"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0ca1
            java.lang.String r0 = "phone"
            java.lang.String r37 = r1.getQueryParameter(r0)
            java.lang.String r39 = r1.getQueryParameter(r6)
            r4 = r51
            r11 = r52
            goto L_0x0dde
        L_0x0ca1:
            java.lang.String r0 = "setlanguage/"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0cb5
            r0 = 12
            java.lang.String r40 = r9.substring(r0)
            r4 = r51
            r11 = r52
            goto L_0x0dde
        L_0x0cb5:
            java.lang.String r0 = "addtheme/"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0cc9
            r0 = 9
            java.lang.String r41 = r9.substring(r0)
            r4 = r51
            r11 = r52
            goto L_0x0dde
        L_0x0cc9:
            java.lang.String r0 = "c/"
            boolean r0 = r9.startsWith(r0)
            if (r0 == 0) goto L_0x0d08
            java.util.List r0 = r1.getPathSegments()
            int r2 = r0.size()
            r3 = 3
            if (r2 != r3) goto L_0x0d02
            r2 = 1
            java.lang.Object r3 = r0.get(r2)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            java.lang.Integer r45 = im.bclpbkiauv.messenger.Utilities.parseInt(r3)
            r2 = 2
            java.lang.Object r2 = r0.get(r2)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.Integer r44 = im.bclpbkiauv.messenger.Utilities.parseInt(r2)
            int r2 = r44.intValue()
            if (r2 == 0) goto L_0x0cfe
            int r2 = r45.intValue()
            if (r2 != 0) goto L_0x0d02
        L_0x0cfe:
            r44 = 0
            r45 = 0
        L_0x0d02:
            r4 = r51
            r11 = r52
            goto L_0x0dde
        L_0x0d08:
            int r0 = r9.length()
            r2 = 1
            if (r0 < r2) goto L_0x0dda
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.List r2 = r1.getPathSegments()
            r0.<init>(r2)
            int r2 = r0.size()
            if (r2 <= 0) goto L_0x0d31
            r2 = 0
            java.lang.Object r3 = r0.get(r2)
            java.lang.String r3 = (java.lang.String) r3
            java.lang.String r4 = "s"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x0d32
            r0.remove(r2)
            goto L_0x0d32
        L_0x0d31:
            r2 = 0
        L_0x0d32:
            int r3 = r0.size()
            if (r3 <= 0) goto L_0x0d59
            java.lang.Object r3 = r0.get(r2)
            r4 = r3
            java.lang.String r4 = (java.lang.String) r4
            int r2 = r0.size()
            r3 = 1
            if (r2 <= r3) goto L_0x0d5b
            java.lang.Object r2 = r0.get(r3)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.Integer r44 = im.bclpbkiauv.messenger.Utilities.parseInt(r2)
            int r2 = r44.intValue()
            if (r2 != 0) goto L_0x0d5b
            r44 = 0
            goto L_0x0d5b
        L_0x0d59:
            r4 = r51
        L_0x0d5b:
            java.lang.String r2 = "start"
            java.lang.String r34 = r1.getQueryParameter(r2)
            java.lang.String r2 = "startgroup"
            java.lang.String r35 = r1.getQueryParameter(r2)
            java.lang.String r2 = "game"
            java.lang.String r38 = r1.getQueryParameter(r2)
            r11 = r52
            goto L_0x0dde
        L_0x0d73:
            java.lang.String r0 = "url"
            java.lang.String r0 = r1.getQueryParameter(r0)
            if (r0 != 0) goto L_0x0d7e
            java.lang.String r0 = ""
        L_0x0d7e:
            java.lang.String r3 = r1.getQueryParameter(r2)
            if (r3 == 0) goto L_0x0dae
            int r3 = r0.length()
            if (r3 <= 0) goto L_0x0d9b
            r46 = 1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r7)
            java.lang.String r0 = r3.toString()
        L_0x0d9b:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            java.lang.String r2 = r1.getQueryParameter(r2)
            r3.append(r2)
            java.lang.String r0 = r3.toString()
        L_0x0dae:
            int r2 = r0.length()
            r3 = 16384(0x4000, float:2.2959E-41)
            if (r2 <= r3) goto L_0x0dbc
            r2 = 0
            java.lang.String r0 = r0.substring(r2, r3)
            goto L_0x0dbd
        L_0x0dbc:
            r2 = 0
        L_0x0dbd:
            boolean r3 = r0.endsWith(r7)
            if (r3 == 0) goto L_0x0dce
            int r3 = r0.length()
            r4 = 1
            int r3 = r3 - r4
            java.lang.String r0 = r0.substring(r2, r3)
            goto L_0x0dbd
        L_0x0dce:
            r36 = r0
            r4 = r51
            r11 = r52
            goto L_0x0dde
        L_0x0dd5:
            r14 = r3
            r12 = 0
            goto L_0x0dda
        L_0x0dd8:
            r14 = r3
            r12 = 0
        L_0x0dda:
            r4 = r51
            r11 = r52
        L_0x0dde:
            r31 = r1
            r51 = r4
            r52 = r11
            r3 = r36
            r14 = r37
            r13 = r39
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
            goto L_0x0e14
        L_0x0df5:
            r50 = r3
            r51 = r4
            r55 = r10
            r52 = r11
            r53 = r12
            r54 = r14
            r12 = 0
        L_0x0e02:
            r31 = r1
            r3 = r36
            r14 = r37
            r13 = r39
            r36 = r35
            r35 = r34
            r34 = r33
            r33 = r32
            r32 = r19
        L_0x0e14:
            if (r42 != 0) goto L_0x0e2c
            int r0 = r15.currentAccount
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x0e23
            goto L_0x0e2c
        L_0x0e23:
            r63 = r5
            r5 = r15
            r62 = r55
            r48 = 0
            goto L_0x0f86
        L_0x0e2c:
            if (r14 != 0) goto L_0x0f5e
            if (r13 == 0) goto L_0x0e3e
            r63 = r5
            r64 = r13
            r65 = r14
            r30 = r54
            r62 = r55
            r48 = 0
            goto L_0x0f6a
        L_0x0e3e:
            if (r51 != 0) goto L_0x0f03
            if (r52 != 0) goto L_0x0f03
            if (r53 != 0) goto L_0x0f03
            if (r3 != 0) goto L_0x0f03
            if (r38 != 0) goto L_0x0f03
            if (r33 != 0) goto L_0x0f03
            if (r34 != 0) goto L_0x0f03
            if (r40 != 0) goto L_0x0f03
            if (r42 != 0) goto L_0x0f03
            if (r43 != 0) goto L_0x0f03
            if (r45 != 0) goto L_0x0f03
            if (r41 == 0) goto L_0x0e59
            r11 = 1
            goto L_0x0f04
        L_0x0e59:
            android.content.ContentResolver r56 = r67.getContentResolver()     // Catch:{ Exception -> 0x0eef }
            android.net.Uri r57 = r68.getData()     // Catch:{ Exception -> 0x0eef }
            r58 = 0
            r59 = 0
            r60 = 0
            r61 = 0
            android.database.Cursor r0 = r56.query(r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x0eef }
            r1 = r0
            if (r1 == 0) goto L_0x0ed7
            boolean r0 = r1.moveToFirst()     // Catch:{ all -> 0x0ec5 }
            if (r0 == 0) goto L_0x0ec3
            java.lang.String r0 = "account_name"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0ec5 }
            java.lang.String r0 = r1.getString(r0)     // Catch:{ all -> 0x0ec5 }
            java.lang.Integer r0 = im.bclpbkiauv.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x0ec5 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0ec5 }
            r2 = 0
        L_0x0e89:
            r4 = 3
            if (r2 >= r4) goto L_0x0ea4
            im.bclpbkiauv.messenger.UserConfig r4 = im.bclpbkiauv.messenger.UserConfig.getInstance(r2)     // Catch:{ all -> 0x0ec5 }
            int r4 = r4.getClientUserId()     // Catch:{ all -> 0x0ec5 }
            if (r4 != r0) goto L_0x0ea0
            r4 = 0
            r55[r4] = r2     // Catch:{ all -> 0x0ec5 }
            r6 = r55[r4]     // Catch:{ all -> 0x0ec5 }
            r11 = 1
            r15.switchToAccount(r6, r11)     // Catch:{ all -> 0x0ec1 }
            goto L_0x0ea5
        L_0x0ea0:
            r11 = 1
            int r2 = r2 + 1
            goto L_0x0e89
        L_0x0ea4:
            r11 = 1
        L_0x0ea5:
            java.lang.String r2 = "DATA4"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ all -> 0x0ec1 }
            int r2 = r1.getInt(r2)     // Catch:{ all -> 0x0ec1 }
            r4 = 0
            r6 = r55[r4]     // Catch:{ all -> 0x0ec1 }
            im.bclpbkiauv.messenger.NotificationCenter r6 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r6)     // Catch:{ all -> 0x0ec1 }
            int r7 = im.bclpbkiauv.messenger.NotificationCenter.closeChats     // Catch:{ all -> 0x0ec1 }
            java.lang.Object[] r8 = new java.lang.Object[r4]     // Catch:{ all -> 0x0ec1 }
            r6.postNotificationName(r7, r8)     // Catch:{ all -> 0x0ec1 }
            r4 = r2
            r32 = r4
            goto L_0x0ed8
        L_0x0ec1:
            r0 = move-exception
            goto L_0x0ec7
        L_0x0ec3:
            r11 = 1
            goto L_0x0ed8
        L_0x0ec5:
            r0 = move-exception
            r11 = 1
        L_0x0ec7:
            r2 = r0
            throw r2     // Catch:{ all -> 0x0ec9 }
        L_0x0ec9:
            r0 = move-exception
            r4 = r0
            if (r1 == 0) goto L_0x0ed6
            r1.close()     // Catch:{ all -> 0x0ed1 }
            goto L_0x0ed6
        L_0x0ed1:
            r0 = move-exception
            r6 = r0
            r2.addSuppressed(r6)     // Catch:{ Exception -> 0x0ede }
        L_0x0ed6:
            throw r4     // Catch:{ Exception -> 0x0ede }
        L_0x0ed7:
            r11 = 1
        L_0x0ed8:
            if (r1 == 0) goto L_0x0ee0
            r1.close()     // Catch:{ Exception -> 0x0ede }
            goto L_0x0ee0
        L_0x0ede:
            r0 = move-exception
            goto L_0x0ef1
        L_0x0ee0:
            r63 = r5
            r5 = r15
            r1 = r32
            r2 = r47
            r3 = r50
            r62 = r55
            r48 = 0
            goto L_0x0f9a
        L_0x0eef:
            r0 = move-exception
            r11 = 1
        L_0x0ef1:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r63 = r5
            r5 = r15
            r1 = r32
            r2 = r47
            r3 = r50
            r62 = r55
            r48 = 0
            goto L_0x0f9a
        L_0x0f03:
            r11 = 1
        L_0x0f04:
            if (r3 == 0) goto L_0x0f21
            java.lang.String r0 = "@"
            boolean r0 = r3.startsWith(r0)
            if (r0 == 0) goto L_0x0f21
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = " "
            r0.append(r1)
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            r0 = r3
            goto L_0x0f22
        L_0x0f21:
            r0 = r3
        L_0x0f22:
            r16 = 0
            r2 = r55[r16]
            r19 = 0
            r1 = r67
            r3 = r51
            r4 = r52
            r10 = r5
            r5 = r53
            r6 = r35
            r48 = 0
            r7 = r36
            r8 = r0
            r9 = r46
            r63 = r10
            r62 = r55
            r10 = r44
            r16 = 1
            r11 = r45
            r12 = r38
            r64 = r13
            r13 = r33
            r65 = r14
            r30 = r54
            r14 = r40
            r15 = r34
            r16 = r42
            r17 = r43
            r18 = r41
            r1.runLinkRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r5 = r67
            goto L_0x0f86
        L_0x0f5e:
            r63 = r5
            r64 = r13
            r65 = r14
            r30 = r54
            r62 = r55
            r48 = 0
        L_0x0f6a:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "phone"
            r2 = r65
            r0.putString(r1, r2)
            r1 = r64
            r0.putString(r6, r1)
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$lqQEDxg4ZWVnjMYuhdxJtI8kViM r4 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$lqQEDxg4ZWVnjMYuhdxJtI8kViM
            r5 = r67
            r4.<init>(r0)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r4)
        L_0x0f86:
            r1 = r32
            r2 = r47
            r3 = r50
            goto L_0x0f9a
        L_0x0f8d:
            r47 = r2
            r50 = r3
            r63 = r5
            r62 = r10
            r5 = r15
            r48 = 0
            r1 = r19
        L_0x0f9a:
            r0 = r1
            r4 = r3
            r7 = r22
            r6 = r62
            r1 = r68
            r3 = r2
            r2 = 0
            goto L_0x10c4
        L_0x0fa6:
            r47 = r2
            r50 = r3
            r63 = r5
            r62 = r10
            r5 = r15
            r48 = 0
            java.lang.String r0 = r68.getAction()
            java.lang.String r1 = "im.bclpbkiauv.messenger.OPEN_ACCOUNT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0fce
            r23 = 1
            r1 = r68
            r0 = r19
            r7 = r22
            r3 = r47
            r4 = r50
            r6 = r62
            r2 = 0
            goto L_0x10c4
        L_0x0fce:
            java.lang.String r0 = r68.getAction()
            java.lang.String r1 = "new_dialog"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0feb
            r24 = 1
            r1 = r68
            r0 = r19
            r7 = r22
            r3 = r47
            r4 = r50
            r6 = r62
            r2 = 0
            goto L_0x10c4
        L_0x0feb:
            java.lang.String r0 = r68.getAction()
            java.lang.String r1 = "com.tmessages.openchat"
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x1057
            java.lang.String r0 = "chatId"
            r1 = r68
            r2 = 0
            int r0 = r1.getIntExtra(r0, r2)
            java.lang.String r3 = "userId"
            int r3 = r1.getIntExtra(r3, r2)
            java.lang.String r4 = "encId"
            int r4 = r1.getIntExtra(r4, r2)
            if (r0 == 0) goto L_0x1022
            r6 = r62
            r7 = r6[r2]
            im.bclpbkiauv.messenger.NotificationCenter r7 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r7)
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.closeChats
            java.lang.Object[] r9 = new java.lang.Object[r2]
            r7.postNotificationName(r8, r9)
            r7 = r0
            r47 = r7
            goto L_0x104d
        L_0x1022:
            r6 = r62
            if (r3 == 0) goto L_0x1037
            r7 = r6[r2]
            im.bclpbkiauv.messenger.NotificationCenter r7 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r7)
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.closeChats
            java.lang.Object[] r9 = new java.lang.Object[r2]
            r7.postNotificationName(r8, r9)
            r7 = r3
            r19 = r7
            goto L_0x104d
        L_0x1037:
            if (r4 == 0) goto L_0x104a
            r7 = r6[r2]
            im.bclpbkiauv.messenger.NotificationCenter r7 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r7)
            int r8 = im.bclpbkiauv.messenger.NotificationCenter.closeChats
            java.lang.Object[] r9 = new java.lang.Object[r2]
            r7.postNotificationName(r8, r9)
            r7 = r4
            r22 = r7
            goto L_0x104d
        L_0x104a:
            r7 = 1
            r27 = r7
        L_0x104d:
            r0 = r19
            r7 = r22
            r3 = r47
            r4 = r50
            goto L_0x10c4
        L_0x1057:
            r1 = r68
            r6 = r62
            r2 = 0
            java.lang.String r0 = r68.getAction()
            java.lang.String r3 = "com.tmessages.openplayer"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x1073
            r28 = 1
            r0 = r19
            r7 = r22
            r3 = r47
            r4 = r50
            goto L_0x10c4
        L_0x1073:
            java.lang.String r0 = r68.getAction()
            java.lang.String r3 = "org.tmessages.openlocations"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x108a
            r29 = 1
            r0 = r19
            r7 = r22
            r3 = r47
            r4 = r50
            goto L_0x10c4
        L_0x108a:
            java.lang.String r0 = r68.getAction()
            java.lang.String r3 = "im.bclpbkiauv.contacts.add"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x10bc
            im.bclpbkiauv.ui.hui.contacts.NewFriendsActivity r0 = new im.bclpbkiauv.ui.hui.contacts.NewFriendsActivity
            r0.<init>()
            r5.lambda$runLinkRequest$28$LaunchActivity(r0)
            return r2
        L_0x109f:
            r19 = r1
            r47 = r2
            r50 = r3
            r63 = r5
            r48 = r7
            r6 = r10
            r1 = r14
            r5 = r15
            r2 = 0
            goto L_0x10bc
        L_0x10ae:
            r19 = r1
            r47 = r2
            r50 = r3
            r63 = r5
            r48 = r7
            r6 = r10
            r1 = r14
            r5 = r15
            r2 = 0
        L_0x10bc:
            r0 = r19
            r7 = r22
            r3 = r47
            r4 = r50
        L_0x10c4:
            int r8 = r5.currentAccount
            im.bclpbkiauv.messenger.UserConfig r8 = im.bclpbkiauv.messenger.UserConfig.getInstance(r8)
            boolean r8 = r8.isClientActivated()
            if (r8 == 0) goto L_0x13ac
            if (r0 == 0) goto L_0x111e
            android.os.Bundle r8 = new android.os.Bundle
            r8.<init>()
            java.lang.String r9 = "user_id"
            r8.putInt(r9, r0)
            if (r4 == 0) goto L_0x10e4
            r9 = r63
            r8.putInt(r9, r4)
        L_0x10e4:
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r9 = mainFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 != 0) goto L_0x1106
            r9 = r6[r2]
            im.bclpbkiauv.messenger.MessagesController r9 = im.bclpbkiauv.messenger.MessagesController.getInstance(r9)
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r10 = mainFragmentsStack
            int r11 = r10.size()
            r12 = 1
            int r11 = r11 - r12
            java.lang.Object r10 = r10.get(r11)
            im.bclpbkiauv.ui.actionbar.BaseFragment r10 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r10
            boolean r9 = r9.checkCanOpenChat(r8, r10)
            if (r9 == 0) goto L_0x1119
        L_0x1106:
            im.bclpbkiauv.ui.ChatActivity r11 = new im.bclpbkiauv.ui.ChatActivity
            r11.<init>(r8)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.actionBarLayout
            r12 = 0
            r13 = 1
            r14 = 1
            r15 = 0
            boolean r9 = r10.presentFragment(r11, r12, r13, r14, r15)
            if (r9 == 0) goto L_0x1119
            r21 = 1
        L_0x1119:
            r8 = r69
            r9 = 0
            goto L_0x13af
        L_0x111e:
            r9 = r63
            r12 = 1
            if (r3 == 0) goto L_0x116b
            android.os.Bundle r8 = new android.os.Bundle
            r8.<init>()
            java.lang.String r10 = "chat_id"
            r8.putInt(r10, r3)
            if (r4 == 0) goto L_0x1132
            r8.putInt(r9, r4)
        L_0x1132:
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r9 = mainFragmentsStack
            boolean r9 = r9.isEmpty()
            if (r9 != 0) goto L_0x1153
            r9 = r6[r2]
            im.bclpbkiauv.messenger.MessagesController r9 = im.bclpbkiauv.messenger.MessagesController.getInstance(r9)
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r10 = mainFragmentsStack
            int r11 = r10.size()
            int r11 = r11 - r12
            java.lang.Object r10 = r10.get(r11)
            im.bclpbkiauv.ui.actionbar.BaseFragment r10 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r10
            boolean r9 = r9.checkCanOpenChat(r8, r10)
            if (r9 == 0) goto L_0x1166
        L_0x1153:
            im.bclpbkiauv.ui.ChatActivity r11 = new im.bclpbkiauv.ui.ChatActivity
            r11.<init>(r8)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.actionBarLayout
            r12 = 0
            r13 = 1
            r14 = 1
            r15 = 0
            boolean r9 = r10.presentFragment(r11, r12, r13, r14, r15)
            if (r9 == 0) goto L_0x1166
            r21 = 1
        L_0x1166:
            r8 = r69
            r9 = 0
            goto L_0x13af
        L_0x116b:
            if (r7 == 0) goto L_0x118f
            android.os.Bundle r8 = new android.os.Bundle
            r8.<init>()
            java.lang.String r9 = "enc_id"
            r8.putInt(r9, r7)
            im.bclpbkiauv.ui.ChatActivity r11 = new im.bclpbkiauv.ui.ChatActivity
            r11.<init>(r8)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.actionBarLayout
            r12 = 0
            r13 = 1
            r14 = 1
            r15 = 0
            boolean r9 = r10.presentFragment(r11, r12, r13, r14, r15)
            if (r9 == 0) goto L_0x118a
            r21 = 1
        L_0x118a:
            r8 = r69
            r9 = 0
            goto L_0x13af
        L_0x118f:
            if (r27 == 0) goto L_0x11cf
            boolean r8 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r8 != 0) goto L_0x119d
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.actionBarLayout
            r8.removeAllFragments()
            goto L_0x11c9
        L_0x119d:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x11c9
            r8 = 0
        L_0x11a8:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r5.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r9 = r9.fragmentsStack
            int r9 = r9.size()
            int r9 = r9 - r12
            if (r8 >= r9) goto L_0x11c4
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r5.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r10 = r9.fragmentsStack
            java.lang.Object r10 = r10.get(r2)
            im.bclpbkiauv.ui.actionbar.BaseFragment r10 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r10
            r9.removeFragmentFromStack((im.bclpbkiauv.ui.actionbar.BaseFragment) r10)
            int r8 = r8 + -1
            int r8 = r8 + r12
            goto L_0x11a8
        L_0x11c4:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.layersActionBarLayout
            r8.closeLastFragment(r2)
        L_0x11c9:
            r21 = 0
            r8 = 0
            r9 = 0
            goto L_0x13af
        L_0x11cf:
            if (r28 == 0) goto L_0x11f4
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x11ed
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            java.lang.Object r8 = r8.get(r2)
            im.bclpbkiauv.ui.actionbar.BaseFragment r8 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r8
            im.bclpbkiauv.ui.components.AudioPlayerAlert r9 = new im.bclpbkiauv.ui.components.AudioPlayerAlert
            r9.<init>(r5)
            r8.showDialog(r9)
        L_0x11ed:
            r21 = 0
            r8 = r69
            r9 = 0
            goto L_0x13af
        L_0x11f4:
            if (r29 == 0) goto L_0x121e
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x1217
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = r8.fragmentsStack
            java.lang.Object r8 = r8.get(r2)
            im.bclpbkiauv.ui.actionbar.BaseFragment r8 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r8
            im.bclpbkiauv.ui.components.SharingLocationsAlert r9 = new im.bclpbkiauv.ui.components.SharingLocationsAlert
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$MI8UjDdiQsjbMSWTyeoWNZB4W94 r10 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$MI8UjDdiQsjbMSWTyeoWNZB4W94
            r10.<init>(r6)
            r9.<init>(r5, r10)
            r8.showDialog(r9)
        L_0x1217:
            r21 = 0
            r8 = r69
            r9 = 0
            goto L_0x13af
        L_0x121e:
            java.lang.String r8 = r5.videoPath
            if (r8 != 0) goto L_0x12af
            java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo> r8 = r5.photoPathsArray
            if (r8 != 0) goto L_0x12af
            java.lang.String r8 = r5.sendingText
            if (r8 != 0) goto L_0x12af
            java.util.ArrayList<java.lang.String> r8 = r5.documentsPathsArray
            if (r8 != 0) goto L_0x12af
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r8 = r5.contactsToSend
            if (r8 != 0) goto L_0x12af
            java.util.ArrayList<android.net.Uri> r8 = r5.documentsUrisArray
            if (r8 == 0) goto L_0x1238
            goto L_0x12af
        L_0x1238:
            if (r23 == 0) goto L_0x126d
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r13 = r5.actionBarLayout
            im.bclpbkiauv.ui.SettingsActivity r14 = new im.bclpbkiauv.ui.SettingsActivity
            r14.<init>()
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            r13.presentFragment(r14, r15, r16, r17, r18)
            boolean r8 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x1261
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.actionBarLayout
            r8.showLastFragment()
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r5.rightActionBarLayout
            r8.showLastFragment()
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r8 = r5.drawerLayoutContainer
            r8.setAllowOpenDrawer(r2, r2)
            goto L_0x1266
        L_0x1261:
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r8 = r5.drawerLayoutContainer
            r8.setAllowOpenDrawer(r2, r2)
        L_0x1266:
            r21 = 1
            r8 = r69
            r9 = 0
            goto L_0x13af
        L_0x126d:
            if (r24 == 0) goto L_0x12ac
            android.os.Bundle r8 = new android.os.Bundle
            r8.<init>()
            java.lang.String r9 = "destroyAfterSelect"
            r8.putBoolean(r9, r12)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r13 = r5.actionBarLayout
            im.bclpbkiauv.ui.ContactsActivity r14 = new im.bclpbkiauv.ui.ContactsActivity
            r14.<init>(r8)
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            r13.presentFragment(r14, r15, r16, r17, r18)
            boolean r9 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r9 == 0) goto L_0x12a0
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r5.actionBarLayout
            r9.showLastFragment()
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r9 = r5.rightActionBarLayout
            r9.showLastFragment()
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r9 = r5.drawerLayoutContainer
            r9.setAllowOpenDrawer(r2, r2)
            goto L_0x12a5
        L_0x12a0:
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r9 = r5.drawerLayoutContainer
            r9.setAllowOpenDrawer(r2, r2)
        L_0x12a5:
            r21 = 1
            r8 = r69
            r9 = 0
            goto L_0x13af
        L_0x12ac:
            r9 = 0
            goto L_0x13ad
        L_0x12af:
            boolean r8 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r8 != 0) goto L_0x12c2
            r8 = r6[r2]
            im.bclpbkiauv.messenger.NotificationCenter r8 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r8)
            int r9 = im.bclpbkiauv.messenger.NotificationCenter.closeChats
            java.lang.Object[] r10 = new java.lang.Object[r2]
            r8.postNotificationName(r9, r10)
        L_0x12c2:
            int r8 = (r25 > r48 ? 1 : (r25 == r48 ? 0 : -1))
            if (r8 != 0) goto L_0x139b
            android.os.Bundle r8 = new android.os.Bundle
            r8.<init>()
            java.lang.String r9 = "onlySelect"
            r8.putBoolean(r9, r12)
            r9 = 3
            java.lang.String r10 = "dialogsType"
            r8.putInt(r10, r9)
            java.lang.String r9 = "allowSwitchAccount"
            r8.putBoolean(r9, r12)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r9 = r5.contactsToSend
            if (r9 == 0) goto L_0x1302
            int r9 = r9.size()
            if (r9 == r12) goto L_0x131e
            r9 = 2131693821(0x7f0f10fd, float:1.9016781E38)
            java.lang.String r10 = "SendContactTo"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            java.lang.String r10 = "selectAlertString"
            r8.putString(r10, r9)
            r9 = 2131693801(0x7f0f10e9, float:1.901674E38)
            java.lang.String r10 = "SendContactToGroup"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            java.lang.String r10 = "selectAlertStringGroup"
            r8.putString(r10, r9)
            goto L_0x131e
        L_0x1302:
            r9 = 2131693821(0x7f0f10fd, float:1.9016781E38)
            java.lang.String r10 = "SendMessagesTo"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            java.lang.String r10 = "selectAlertString"
            r8.putString(r10, r9)
            r9 = 2131693822(0x7f0f10fe, float:1.9016783E38)
            java.lang.String r10 = "SendMessagesToGroup"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            java.lang.String r10 = "selectAlertStringGroup"
            r8.putString(r10, r9)
        L_0x131e:
            im.bclpbkiauv.ui.DialogsActivity r9 = new im.bclpbkiauv.ui.DialogsActivity
            r9.<init>(r8)
            r9.setDelegate(r5)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r13 = r5.actionBarLayout
            r15 = 0
            r16 = 1
            r17 = 1
            r18 = 0
            r14 = r9
            r13.presentFragment(r14, r15, r16, r17, r18)
            r21 = 1
            boolean r10 = im.bclpbkiauv.ui.SecretMediaViewer.hasInstance()
            if (r10 == 0) goto L_0x134d
            im.bclpbkiauv.ui.SecretMediaViewer r10 = im.bclpbkiauv.ui.SecretMediaViewer.getInstance()
            boolean r10 = r10.isVisible()
            if (r10 == 0) goto L_0x134d
            im.bclpbkiauv.ui.SecretMediaViewer r10 = im.bclpbkiauv.ui.SecretMediaViewer.getInstance()
            r10.closePhoto(r2, r2)
            goto L_0x137c
        L_0x134d:
            boolean r10 = im.bclpbkiauv.ui.PhotoViewer.hasInstance()
            if (r10 == 0) goto L_0x1365
            im.bclpbkiauv.ui.PhotoViewer r10 = im.bclpbkiauv.ui.PhotoViewer.getInstance()
            boolean r10 = r10.isVisible()
            if (r10 == 0) goto L_0x1365
            im.bclpbkiauv.ui.PhotoViewer r10 = im.bclpbkiauv.ui.PhotoViewer.getInstance()
            r10.closePhoto(r2, r12)
            goto L_0x137c
        L_0x1365:
            boolean r10 = im.bclpbkiauv.ui.ArticleViewer.hasInstance()
            if (r10 == 0) goto L_0x137c
            im.bclpbkiauv.ui.ArticleViewer r10 = im.bclpbkiauv.ui.ArticleViewer.getInstance()
            boolean r10 = r10.isVisible()
            if (r10 == 0) goto L_0x137c
            im.bclpbkiauv.ui.ArticleViewer r10 = im.bclpbkiauv.ui.ArticleViewer.getInstance()
            r10.close(r2, r12)
        L_0x137c:
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r10 = r5.drawerLayoutContainer
            r10.setAllowOpenDrawer(r2, r2)
            boolean r10 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r10 == 0) goto L_0x1392
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.actionBarLayout
            r10.showLastFragment()
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.rightActionBarLayout
            r10.showLastFragment()
            goto L_0x1397
        L_0x1392:
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r10 = r5.drawerLayoutContainer
            r10.setAllowOpenDrawer(r2, r2)
        L_0x1397:
            r8 = r69
            r9 = 0
            goto L_0x13af
        L_0x139b:
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            java.lang.Long r9 = java.lang.Long.valueOf(r25)
            r8.add(r9)
            r9 = 0
            r5.didSelectDialogs(r9, r8, r9, r2)
            goto L_0x13ad
        L_0x13ac:
            r9 = 0
        L_0x13ad:
            r8 = r69
        L_0x13af:
            if (r21 != 0) goto L_0x1443
            if (r8 != 0) goto L_0x1443
            boolean r10 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r10 == 0) goto L_0x13f9
            int r10 = r5.currentAccount
            im.bclpbkiauv.messenger.UserConfig r10 = im.bclpbkiauv.messenger.UserConfig.getInstance(r10)
            boolean r10 = r10.isClientActivated()
            if (r10 != 0) goto L_0x13df
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r10 = r10.fragmentsStack
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x142e
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.layersActionBarLayout
            im.bclpbkiauv.ui.hui.login.LoginContronllerActivity r11 = new im.bclpbkiauv.ui.hui.login.LoginContronllerActivity
            r11.<init>()
            r10.addFragmentToStack(r11)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r10 = r5.drawerLayoutContainer
            r10.setAllowOpenDrawer(r2, r2)
            goto L_0x142e
        L_0x13df:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r10 = r10.fragmentsStack
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x142e
            im.bclpbkiauv.ui.IndexActivity r10 = new im.bclpbkiauv.ui.IndexActivity
            r10.<init>()
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r11 = r5.actionBarLayout
            r11.addFragmentToStack(r10)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r11 = r5.drawerLayoutContainer
            r11.setAllowOpenDrawer(r2, r2)
            goto L_0x142e
        L_0x13f9:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r10 = r10.fragmentsStack
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x142e
            int r10 = r5.currentAccount
            im.bclpbkiauv.messenger.UserConfig r10 = im.bclpbkiauv.messenger.UserConfig.getInstance(r10)
            boolean r10 = r10.isClientActivated()
            if (r10 != 0) goto L_0x141f
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r5.actionBarLayout
            im.bclpbkiauv.ui.hui.login.LoginContronllerActivity r11 = new im.bclpbkiauv.ui.hui.login.LoginContronllerActivity
            r11.<init>()
            r10.addFragmentToStack(r11)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r10 = r5.drawerLayoutContainer
            r10.setAllowOpenDrawer(r2, r2)
            goto L_0x142e
        L_0x141f:
            im.bclpbkiauv.ui.IndexActivity r10 = new im.bclpbkiauv.ui.IndexActivity
            r10.<init>()
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r11 = r5.actionBarLayout
            r11.addFragmentToStack(r10)
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r11 = r5.drawerLayoutContainer
            r11.setAllowOpenDrawer(r2, r2)
        L_0x142e:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r2 = r5.actionBarLayout
            r2.showLastFragment()
            boolean r2 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x1443
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r2 = r5.layersActionBarLayout
            r2.showLastFragment()
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r2 = r5.rightActionBarLayout
            r2.showLastFragment()
        L_0x1443:
            r1.setAction(r9)
            return r21
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LaunchActivity.handleIntent(android.content.Intent, boolean, boolean, boolean):boolean");
    }

    public /* synthetic */ void lambda$handleIntent$4$LaunchActivity(Bundle args) {
        lambda$runLinkRequest$28$LaunchActivity(new CancelAccountDeletionActivity(args));
    }

    public /* synthetic */ void lambda$handleIntent$6$LaunchActivity(int[] intentAccount, LocationController.SharingLocationInfo info) {
        intentAccount[0] = info.messageObject.currentAccount;
        switchToAccount(intentAccount[0], true);
        NewLocationActivity locationActivity = new NewLocationActivity(3, info.messageObject);
        locationActivity.setDelegate(new NewLocationActivity.LocationActivityDelegate(info.messageObject.getDialogId()) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                SendMessagesHelper.getInstance(LocationController.SharingLocationInfo.this.messageObject.currentAccount).sendMessage(messageMedia, this.f$1, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
            }
        });
        lambda$runLinkRequest$28$LaunchActivity(locationActivity);
    }

    private void getUserInfo(TLRPC.User user) {
        TLRPC.TL_users_getFullUser req = new TLRPC.TL_users_getFullUser();
        req.id = MessagesController.getInstance(UserConfig.selectedAccount).getInputUser(user);
        XAlertDialog progressDialog = new XAlertDialog(this, 4);
        progressDialog.setLoadingText(LocaleController.getString(R.string.Loading));
        int reqId = ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ XAlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LaunchActivity.this.lambda$getUserInfo$8$LaunchActivity(this.f$1, tLObject, tL_error);
            }
        });
        progressDialog.show();
        ConnectionsManager.getInstance(UserConfig.selectedAccount).bindRequestToGuid(reqId, getActionBarLayout().getCurrentFragment().getClassGuid());
    }

    public /* synthetic */ void lambda$getUserInfo$8$LaunchActivity(XAlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        progressDialog.dismiss();
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LaunchActivity.this.lambda$null$7$LaunchActivity(this.f$1);
                }
            });
        } else if ("USERNOTEXITST".equals(error.text)) {
            AlertsCreator.showSimpleAlert(getActionBarLayout().getCurrentFragment(), LocaleController.getString("UserNotExist", R.string.UserNotExist));
        }
    }

    public /* synthetic */ void lambda$null$7$LaunchActivity(TLObject response) {
        TLRPC.UserFull userFull = (TLRPC.UserFull) response;
        MessagesController.getInstance(UserConfig.selectedAccount).putUser(userFull.user, false);
        if (userFull.user != null) {
            if (userFull.user.self || userFull.user.contact) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", userFull.user.id);
                lambda$runLinkRequest$28$LaunchActivity(new NewProfileActivity(bundle));
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putInt("from_type", 4);
            lambda$runLinkRequest$28$LaunchActivity(new AddContactsInfoActivity(bundle2, userFull.user));
        }
    }

    private void runLinkRequest(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, Integer channelId, String game, HashMap<String, String> auth, String lang, String unsupportedUrl, String code, TLRPC.TL_wallPaper wallPaper, String theme, int state) {
        int i;
        AlertDialog progressDialog;
        int[] requestId;
        int i2 = intentAccount;
        String str = username;
        String str2 = group;
        String str3 = sticker;
        String str4 = message;
        HashMap<String, String> hashMap = auth;
        String str5 = lang;
        String str6 = unsupportedUrl;
        TLRPC.TL_wallPaper tL_wallPaper = wallPaper;
        String str7 = theme;
        if (state != 0 || UserConfig.getActivatedAccountsCount() < 2 || hashMap == null) {
            BaseFragment baseFragment = null;
            if (code == null) {
                AlertDialog progressDialog2 = new AlertDialog(this, 3);
                int[] requestId2 = {0};
                Runnable cancelRunnable = null;
                String str8 = username;
                if (str8 != null) {
                    TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                    req.username = str8;
                    requestId2[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(req, new RequestDelegate(progressDialog2, game, intentAccount, botChat, botUser, messageId) {
                        private final /* synthetic */ AlertDialog f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ int f$3;
                        private final /* synthetic */ String f$4;
                        private final /* synthetic */ String f$5;
                        private final /* synthetic */ Integer f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LaunchActivity.this.lambda$runLinkRequest$13$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
                        }
                    });
                    String str9 = lang;
                    String str10 = unsupportedUrl;
                    requestId = requestId2;
                    progressDialog = progressDialog2;
                    i = intentAccount;
                    String str11 = group;
                } else {
                    String str12 = group;
                    if (str12 == null) {
                        AlertDialog progressDialog3 = progressDialog2;
                        i = intentAccount;
                        String str13 = str12;
                        requestId = requestId2;
                        int i3 = state;
                        String str14 = sticker;
                        if (str14 == null) {
                            String str15 = message;
                            if (str15 != null) {
                                Bundle args = new Bundle();
                                args.putBoolean("onlySelect", true);
                                DialogsActivity fragment = new DialogsActivity(args);
                                fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate(hasUrl, i, str15) {
                                    private final /* synthetic */ boolean f$1;
                                    private final /* synthetic */ int f$2;
                                    private final /* synthetic */ String f$3;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                        this.f$3 = r4;
                                    }

                                    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                                        LaunchActivity.this.lambda$runLinkRequest$19$LaunchActivity(this.f$1, this.f$2, this.f$3, dialogsActivity, arrayList, charSequence, z);
                                    }
                                });
                                presentFragment(fragment, false, true);
                                String str16 = lang;
                                progressDialog = progressDialog3;
                                String str17 = unsupportedUrl;
                            } else {
                                boolean z = hasUrl;
                                HashMap<String, String> hashMap2 = auth;
                                if (hashMap2 != null) {
                                    int bot_id = Utilities.parseInt(hashMap2.get("bot_id")).intValue();
                                    if (bot_id != 0) {
                                        TLRPC.TL_account_getAuthorizationForm req2 = new TLRPC.TL_account_getAuthorizationForm();
                                        req2.bot_id = bot_id;
                                        req2.scope = hashMap2.get("scope");
                                        req2.public_key = hashMap2.get("public_key");
                                        $$Lambda$LaunchActivity$WCtZw5N_iej8ESLziUg2SIwu9vA r11 = r1;
                                        int i4 = bot_id;
                                        ConnectionsManager instance = ConnectionsManager.getInstance(intentAccount);
                                        TLRPC.TL_account_getAuthorizationForm req3 = req2;
                                        progressDialog = progressDialog3;
                                        $$Lambda$LaunchActivity$WCtZw5N_iej8ESLziUg2SIwu9vA r1 = new RequestDelegate(requestId, intentAccount, progressDialog3, req3, hashMap2.get("payload"), hashMap2.get("nonce"), hashMap2.get("callback_url")) {
                                            private final /* synthetic */ int[] f$1;
                                            private final /* synthetic */ int f$2;
                                            private final /* synthetic */ AlertDialog f$3;
                                            private final /* synthetic */ TLRPC.TL_account_getAuthorizationForm f$4;
                                            private final /* synthetic */ String f$5;
                                            private final /* synthetic */ String f$6;
                                            private final /* synthetic */ String f$7;

                                            {
                                                this.f$1 = r2;
                                                this.f$2 = r3;
                                                this.f$3 = r4;
                                                this.f$4 = r5;
                                                this.f$5 = r6;
                                                this.f$6 = r7;
                                                this.f$7 = r8;
                                            }

                                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                                LaunchActivity.this.lambda$runLinkRequest$23$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
                                            }
                                        };
                                        requestId[0] = instance.sendRequest(req3, r11);
                                        String str18 = lang;
                                        String str19 = unsupportedUrl;
                                    } else {
                                        return;
                                    }
                                } else {
                                    progressDialog = progressDialog3;
                                    String str20 = unsupportedUrl;
                                    if (str20 != null) {
                                        TLRPC.TL_help_getDeepLinkInfo req4 = new TLRPC.TL_help_getDeepLinkInfo();
                                        req4.path = str20;
                                        requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req4, new RequestDelegate(progressDialog) {
                                            private final /* synthetic */ AlertDialog f$1;

                                            {
                                                this.f$1 = r2;
                                            }

                                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                                LaunchActivity.this.lambda$runLinkRequest$25$LaunchActivity(this.f$1, tLObject, tL_error);
                                            }
                                        });
                                        String str21 = lang;
                                    } else {
                                        String str22 = lang;
                                        if (str22 != null) {
                                            TLRPC.TL_langpack_getLanguage req5 = new TLRPC.TL_langpack_getLanguage();
                                            req5.lang_code = str22;
                                            req5.lang_pack = "android";
                                            requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req5, new RequestDelegate(progressDialog) {
                                                private final /* synthetic */ AlertDialog f$1;

                                                {
                                                    this.f$1 = r2;
                                                }

                                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                                    LaunchActivity.this.lambda$runLinkRequest$27$LaunchActivity(this.f$1, tLObject, tL_error);
                                                }
                                            });
                                        } else {
                                            TLRPC.TL_wallPaper tL_wallPaper2 = wallPaper;
                                            if (tL_wallPaper2 != null) {
                                                boolean ok = false;
                                                if (TextUtils.isEmpty(tL_wallPaper2.slug)) {
                                                    try {
                                                        AndroidUtilities.runOnUIThread(new Runnable(new WallpaperActivity(new WallpapersListActivity.ColorWallpaper(-100, tL_wallPaper2.settings.background_color), (Bitmap) null)) {
                                                            private final /* synthetic */ WallpaperActivity f$1;

                                                            {
                                                                this.f$1 = r2;
                                                            }

                                                            public final void run() {
                                                                LaunchActivity.this.lambda$runLinkRequest$28$LaunchActivity(this.f$1);
                                                            }
                                                        });
                                                        ok = true;
                                                    } catch (Exception e) {
                                                        FileLog.e((Throwable) e);
                                                    }
                                                }
                                                if (!ok) {
                                                    TLRPC.TL_account_getWallPaper req6 = new TLRPC.TL_account_getWallPaper();
                                                    TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                                                    inputWallPaperSlug.slug = tL_wallPaper2.slug;
                                                    req6.wallpaper = inputWallPaperSlug;
                                                    requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req6, new RequestDelegate(progressDialog, tL_wallPaper2) {
                                                        private final /* synthetic */ AlertDialog f$1;
                                                        private final /* synthetic */ TLRPC.TL_wallPaper f$2;

                                                        {
                                                            this.f$1 = r2;
                                                            this.f$2 = r3;
                                                        }

                                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                                            LaunchActivity.this.lambda$runLinkRequest$30$LaunchActivity(this.f$1, this.f$2, tLObject, tL_error);
                                                        }
                                                    });
                                                }
                                            } else {
                                                String str23 = theme;
                                                if (str23 != null) {
                                                    Runnable cancelRunnable2 = new Runnable() {
                                                        public final void run() {
                                                            LaunchActivity.this.lambda$runLinkRequest$31$LaunchActivity();
                                                        }
                                                    };
                                                    TLRPC.TL_account_getTheme req7 = new TLRPC.TL_account_getTheme();
                                                    req7.format = "android";
                                                    TLRPC.TL_inputThemeSlug inputThemeSlug = new TLRPC.TL_inputThemeSlug();
                                                    inputThemeSlug.slug = str23;
                                                    req7.theme = inputThemeSlug;
                                                    requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req7, new RequestDelegate(progressDialog) {
                                                        private final /* synthetic */ AlertDialog f$1;

                                                        {
                                                            this.f$1 = r2;
                                                        }

                                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                                            LaunchActivity.this.lambda$runLinkRequest$33$LaunchActivity(this.f$1, tLObject, tL_error);
                                                        }
                                                    });
                                                    cancelRunnable = cancelRunnable2;
                                                } else if (!(channelId == null || messageId == null)) {
                                                    Bundle args2 = new Bundle();
                                                    args2.putInt("chat_id", channelId.intValue());
                                                    args2.putInt("message_id", messageId.intValue());
                                                    if (!mainFragmentsStack.isEmpty()) {
                                                        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                                                        baseFragment = arrayList.get(arrayList.size() - 1);
                                                    }
                                                    BaseFragment lastFragment = baseFragment;
                                                    if (lastFragment == null || MessagesController.getInstance(intentAccount).checkCanOpenChat(args2, lastFragment)) {
                                                        AndroidUtilities.runOnUIThread(new Runnable(args2, channelId, requestId, progressDialog, lastFragment, intentAccount) {
                                                            private final /* synthetic */ Bundle f$1;
                                                            private final /* synthetic */ Integer f$2;
                                                            private final /* synthetic */ int[] f$3;
                                                            private final /* synthetic */ AlertDialog f$4;
                                                            private final /* synthetic */ BaseFragment f$5;
                                                            private final /* synthetic */ int f$6;

                                                            {
                                                                this.f$1 = r2;
                                                                this.f$2 = r3;
                                                                this.f$3 = r4;
                                                                this.f$4 = r5;
                                                                this.f$5 = r6;
                                                                this.f$6 = r7;
                                                            }

                                                            public final void run() {
                                                                LaunchActivity.this.lambda$runLinkRequest$36$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (!mainFragmentsStack.isEmpty()) {
                            TLRPC.TL_inputStickerSetShortName stickerset = new TLRPC.TL_inputStickerSetShortName();
                            stickerset.short_name = str14;
                            ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
                            BaseFragment fragment2 = arrayList2.get(arrayList2.size() - 1);
                            fragment2.showDialog(new StickersAlert(this, fragment2, stickerset, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                            return;
                        } else {
                            return;
                        }
                    } else if (state == 0) {
                        TLRPC.TL_messages_checkChatInvite req8 = new TLRPC.TL_messages_checkChatInvite();
                        req8.hash = str12;
                        $$Lambda$LaunchActivity$muOptaOUPVc09jzfX_wOwe5CUW4 r22 = r1;
                        ConnectionsManager instance2 = ConnectionsManager.getInstance(intentAccount);
                        AlertDialog progressDialog4 = progressDialog2;
                        $$Lambda$LaunchActivity$muOptaOUPVc09jzfX_wOwe5CUW4 r12 = new RequestDelegate(this, progressDialog2, intentAccount, group, username, sticker, botUser, botChat, message, hasUrl, messageId, channelId, game, auth, lang, unsupportedUrl, code, wallPaper, theme) {
                            private final /* synthetic */ LaunchActivity f$0;
                            private final /* synthetic */ AlertDialog f$1;
                            private final /* synthetic */ Integer f$10;
                            private final /* synthetic */ Integer f$11;
                            private final /* synthetic */ String f$12;
                            private final /* synthetic */ HashMap f$13;
                            private final /* synthetic */ String f$14;
                            private final /* synthetic */ String f$15;
                            private final /* synthetic */ String f$16;
                            private final /* synthetic */ TLRPC.TL_wallPaper f$17;
                            private final /* synthetic */ String f$18;
                            private final /* synthetic */ int f$2;
                            private final /* synthetic */ String f$3;
                            private final /* synthetic */ String f$4;
                            private final /* synthetic */ String f$5;
                            private final /* synthetic */ String f$6;
                            private final /* synthetic */ String f$7;
                            private final /* synthetic */ String f$8;
                            private final /* synthetic */ boolean f$9;

                            {
                                this.f$0 = r3;
                                this.f$1 = r4;
                                this.f$2 = r5;
                                this.f$3 = r6;
                                this.f$4 = r7;
                                this.f$5 = r8;
                                this.f$6 = r9;
                                this.f$7 = r10;
                                this.f$8 = r11;
                                this.f$9 = r12;
                                this.f$10 = r13;
                                this.f$11 = r14;
                                this.f$12 = r15;
                                this.f$13 = r16;
                                this.f$14 = r17;
                                this.f$15 = r18;
                                this.f$16 = r19;
                                this.f$17 = r20;
                                this.f$18 = r21;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LaunchActivity launchActivity = this.f$0;
                                LaunchActivity launchActivity2 = launchActivity;
                                launchActivity2.lambda$runLinkRequest$16$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18, tLObject, tL_error);
                            }
                        };
                        ConnectionsManager connectionsManager = instance2;
                        requestId = requestId2;
                        requestId[0] = connectionsManager.sendRequest(req8, r22, 2);
                        i = intentAccount;
                        String str24 = group;
                        String str25 = lang;
                        String str26 = unsupportedUrl;
                        progressDialog = progressDialog4;
                    } else {
                        requestId = requestId2;
                        AlertDialog progressDialog5 = progressDialog2;
                        if (state == 1) {
                            TLRPC.TL_messages_importChatInvite req9 = new TLRPC.TL_messages_importChatInvite();
                            req9.hash = group;
                            i = intentAccount;
                            AlertDialog progressDialog6 = progressDialog5;
                            ConnectionsManager.getInstance(intentAccount).sendRequest(req9, new RequestDelegate(i, progressDialog6) {
                                private final /* synthetic */ int f$1;
                                private final /* synthetic */ AlertDialog f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    LaunchActivity.this.lambda$runLinkRequest$18$LaunchActivity(this.f$1, this.f$2, tLObject, tL_error);
                                }
                            }, 2);
                            String str27 = lang;
                            progressDialog = progressDialog6;
                            String str28 = unsupportedUrl;
                        } else {
                            i = intentAccount;
                            String str29 = group;
                            String str30 = lang;
                            progressDialog = progressDialog5;
                            String str31 = unsupportedUrl;
                        }
                    }
                }
                if (requestId[0] != 0) {
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(i, requestId, cancelRunnable) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ int[] f$1;
                        private final /* synthetic */ Runnable f$2;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onCancel(DialogInterface dialogInterface) {
                            LaunchActivity.lambda$runLinkRequest$37(this.f$0, this.f$1, this.f$2, dialogInterface);
                        }
                    });
                    try {
                        progressDialog.show();
                    } catch (Exception e2) {
                    }
                }
            } else if (NotificationCenter.getGlobalInstance().hasObservers(NotificationCenter.didReceiveSmsCode)) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, code);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("OtherLoginCode", R.string.OtherLoginCode, code)));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder);
            }
        } else {
            AlertsCreator.createAccountSelectDialog(this, new AlertsCreator.AccountSelectDelegate(this, intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, channelId, game, auth, lang, unsupportedUrl, code, wallPaper, theme) {
                private final /* synthetic */ LaunchActivity f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ Integer f$10;
                private final /* synthetic */ String f$11;
                private final /* synthetic */ HashMap f$12;
                private final /* synthetic */ String f$13;
                private final /* synthetic */ String f$14;
                private final /* synthetic */ String f$15;
                private final /* synthetic */ TLRPC.TL_wallPaper f$16;
                private final /* synthetic */ String f$17;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ String f$4;
                private final /* synthetic */ String f$5;
                private final /* synthetic */ String f$6;
                private final /* synthetic */ String f$7;
                private final /* synthetic */ boolean f$8;
                private final /* synthetic */ Integer f$9;

                {
                    this.f$0 = r3;
                    this.f$1 = r4;
                    this.f$2 = r5;
                    this.f$3 = r6;
                    this.f$4 = r7;
                    this.f$5 = r8;
                    this.f$6 = r9;
                    this.f$7 = r10;
                    this.f$8 = r11;
                    this.f$9 = r12;
                    this.f$10 = r13;
                    this.f$11 = r14;
                    this.f$12 = r15;
                    this.f$13 = r16;
                    this.f$14 = r17;
                    this.f$15 = r18;
                    this.f$16 = r19;
                    this.f$17 = r20;
                }

                public final void didSelectAccount(int i) {
                    int i2 = i;
                    LaunchActivity launchActivity = this.f$0;
                    LaunchActivity launchActivity2 = launchActivity;
                    launchActivity2.lambda$runLinkRequest$9$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, i2);
                }
            }).show();
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$9$LaunchActivity(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, Integer channelId, String game, HashMap auth, String lang, String unsupportedUrl, String code, TLRPC.TL_wallPaper wallPaper, String theme, int account) {
        int i = account;
        if (i != intentAccount) {
            switchToAccount(i, true);
        }
        runLinkRequest(account, username, group, sticker, botUser, botChat, message, hasUrl, messageId, channelId, game, auth, lang, unsupportedUrl, code, wallPaper, theme, 1);
    }

    public /* synthetic */ void lambda$runLinkRequest$13$LaunchActivity(AlertDialog progressDialog, String game, int intentAccount, String botChat, String botUser, Integer messageId, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, response, error, game, intentAccount, botChat, botUser, messageId) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_error f$3;
            private final /* synthetic */ String f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ String f$6;
            private final /* synthetic */ String f$7;
            private final /* synthetic */ Integer f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$12$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    /* JADX WARNING: type inference failed for: r8v3, types: [im.bclpbkiauv.ui.actionbar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r9v15, types: [im.bclpbkiauv.ui.actionbar.BaseFragment] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$12$LaunchActivity(im.bclpbkiauv.ui.actionbar.AlertDialog r18, im.bclpbkiauv.tgnet.TLObject r19, im.bclpbkiauv.tgnet.TLRPC.TL_error r20, java.lang.String r21, int r22, java.lang.String r23, java.lang.String r24, java.lang.Integer r25) {
        /*
            r17 = this;
            r1 = r17
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            boolean r0 = r17.isFinishing()
            if (r0 != 0) goto L_0x0273
            r18.dismiss()     // Catch:{ Exception -> 0x0014 }
            goto L_0x001a
        L_0x0014:
            r0 = move-exception
            r6 = r0
            r0 = r6
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x001a:
            r6 = r19
            im.bclpbkiauv.tgnet.TLRPC$TL_contacts_resolvedPeer r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_contacts_resolvedPeer) r6
            if (r20 != 0) goto L_0x025e
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r0 = r1.actionBarLayout
            if (r0 == 0) goto L_0x025e
            if (r2 == 0) goto L_0x0030
            if (r2 == 0) goto L_0x025e
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r0 = r6.users
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x025e
        L_0x0030:
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r22)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r7 = r6.users
            r8 = 0
            r0.putUsers(r7, r8)
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r22)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r7 = r6.chats
            r0.putChats(r7, r8)
            im.bclpbkiauv.messenger.MessagesStorage r0 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r22)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r7 = r6.users
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r9 = r6.chats
            r10 = 1
            r0.putUsersAndChats(r7, r9, r8, r10)
            java.lang.String r0 = "dialogsType"
            java.lang.String r7 = "onlySelect"
            if (r2 == 0) goto L_0x0148
            android.os.Bundle r9 = new android.os.Bundle
            r9.<init>()
            r9.putBoolean(r7, r10)
            java.lang.String r7 = "cantSendToChannels"
            r9.putBoolean(r7, r10)
            r9.putInt(r0, r10)
            r0 = 2131693806(0x7f0f10ee, float:1.901675E38)
            java.lang.String r7 = "SendGameTo"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r0)
            java.lang.String r7 = "selectAlertString"
            r9.putString(r7, r0)
            r0 = 2131693807(0x7f0f10ef, float:1.9016753E38)
            java.lang.String r7 = "SendGameToGroup"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r0)
            java.lang.String r7 = "selectAlertStringGroup"
            r9.putString(r7, r0)
            im.bclpbkiauv.ui.DialogsActivity r0 = new im.bclpbkiauv.ui.DialogsActivity
            r0.<init>(r9)
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$xp0YAgIguKNzTN1dTd_y1VGANZ8 r7 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$xp0YAgIguKNzTN1dTd_y1VGANZ8
            r7.<init>(r2, r3, r6)
            r0.setDelegate(r7)
            boolean r7 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r7 == 0) goto L_0x00b7
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r7 = r1.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r7 = r7.fragmentsStack
            int r7 = r7.size()
            if (r7 <= 0) goto L_0x00b5
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r7 = r1.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r7 = r7.fragmentsStack
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r11 = r1.layersActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r11 = r11.fragmentsStack
            int r11 = r11.size()
            int r11 = r11 - r10
            java.lang.Object r7 = r7.get(r11)
            boolean r7 = r7 instanceof im.bclpbkiauv.ui.IndexActivity
            if (r7 == 0) goto L_0x00b5
            r7 = 1
            goto L_0x00b6
        L_0x00b5:
            r7 = 0
        L_0x00b6:
            goto L_0x00d9
        L_0x00b7:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r7 = r1.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r7 = r7.fragmentsStack
            int r7 = r7.size()
            if (r7 <= r10) goto L_0x00d8
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r7 = r1.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r7 = r7.fragmentsStack
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r11 = r1.actionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r11 = r11.fragmentsStack
            int r11 = r11.size()
            int r11 = r11 - r10
            java.lang.Object r7 = r7.get(r11)
            boolean r7 = r7 instanceof im.bclpbkiauv.ui.IndexActivity
            if (r7 == 0) goto L_0x00d8
            r7 = 1
            goto L_0x00d9
        L_0x00d8:
            r7 = 0
        L_0x00d9:
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r11 = r1.actionBarLayout
            r14 = 1
            r15 = 1
            r16 = 0
            r12 = r0
            r13 = r7
            r11.presentFragment(r12, r13, r14, r15, r16)
            boolean r11 = im.bclpbkiauv.ui.SecretMediaViewer.hasInstance()
            if (r11 == 0) goto L_0x00fc
            im.bclpbkiauv.ui.SecretMediaViewer r11 = im.bclpbkiauv.ui.SecretMediaViewer.getInstance()
            boolean r11 = r11.isVisible()
            if (r11 == 0) goto L_0x00fc
            im.bclpbkiauv.ui.SecretMediaViewer r10 = im.bclpbkiauv.ui.SecretMediaViewer.getInstance()
            r10.closePhoto(r8, r8)
            goto L_0x012b
        L_0x00fc:
            boolean r11 = im.bclpbkiauv.ui.PhotoViewer.hasInstance()
            if (r11 == 0) goto L_0x0114
            im.bclpbkiauv.ui.PhotoViewer r11 = im.bclpbkiauv.ui.PhotoViewer.getInstance()
            boolean r11 = r11.isVisible()
            if (r11 == 0) goto L_0x0114
            im.bclpbkiauv.ui.PhotoViewer r11 = im.bclpbkiauv.ui.PhotoViewer.getInstance()
            r11.closePhoto(r8, r10)
            goto L_0x012b
        L_0x0114:
            boolean r11 = im.bclpbkiauv.ui.ArticleViewer.hasInstance()
            if (r11 == 0) goto L_0x012b
            im.bclpbkiauv.ui.ArticleViewer r11 = im.bclpbkiauv.ui.ArticleViewer.getInstance()
            boolean r11 = r11.isVisible()
            if (r11 == 0) goto L_0x012b
            im.bclpbkiauv.ui.ArticleViewer r11 = im.bclpbkiauv.ui.ArticleViewer.getInstance()
            r11.close(r8, r10)
        L_0x012b:
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r10 = r1.drawerLayoutContainer
            r10.setAllowOpenDrawer(r8, r8)
            boolean r10 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r10 == 0) goto L_0x0141
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r1.actionBarLayout
            r8.showLastFragment()
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r8 = r1.rightActionBarLayout
            r8.showLastFragment()
            goto L_0x0146
        L_0x0141:
            im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer r10 = r1.drawerLayoutContainer
            r10.setAllowOpenDrawer(r8, r8)
        L_0x0146:
            goto L_0x0273
        L_0x0148:
            r9 = 0
            if (r4 == 0) goto L_0x01ac
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r11 = r6.users
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x015b
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r9 = r6.users
            java.lang.Object r9 = r9.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$User r9 = (im.bclpbkiauv.tgnet.TLRPC.User) r9
        L_0x015b:
            if (r9 == 0) goto L_0x019e
            boolean r11 = r9.bot
            if (r11 == 0) goto L_0x0166
            boolean r11 = r9.bot_nochats
            if (r11 == 0) goto L_0x0166
            goto L_0x019e
        L_0x0166:
            android.os.Bundle r11 = new android.os.Bundle
            r11.<init>()
            r11.putBoolean(r7, r10)
            r7 = 2
            r11.putInt(r0, r7)
            r0 = 2131689715(0x7f0f00f3, float:1.9008453E38)
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r12 = im.bclpbkiauv.messenger.UserObject.getName(r9)
            r7[r8] = r12
            java.lang.String r8 = "%1$s"
            r7[r10] = r8
            java.lang.String r8 = "AddToTheGroupTitle"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.formatString(r8, r0, r7)
            java.lang.String r7 = "addToGroupAlertString"
            r11.putString(r7, r0)
            im.bclpbkiauv.ui.DialogsActivity r0 = new im.bclpbkiauv.ui.DialogsActivity
            r0.<init>(r11)
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$HfGcrD1s1TFVdaQkR3-VtX4ooa0 r7 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$HfGcrD1s1TFVdaQkR3-VtX4ooa0
            r7.<init>(r3, r9, r4)
            r0.setDelegate(r7)
            r1.lambda$runLinkRequest$28$LaunchActivity(r0)
            goto L_0x0273
        L_0x019e:
            r0 = 2131690181(0x7f0f02c5, float:1.9009398E38)
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((int) r0)     // Catch:{ Exception -> 0x01a5 }
            goto L_0x01ab
        L_0x01a5:
            r0 = move-exception
            r7 = r0
            r0 = r7
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01ab:
            return
        L_0x01ac:
            r0 = 0
            android.os.Bundle r7 = new android.os.Bundle
            r7.<init>()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r11 = r6.chats
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x01d6
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r11 = r6.chats
            java.lang.Object r11 = r11.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r11
            int r11 = r11.id
            java.lang.String r12 = "chat_id"
            r7.putInt(r12, r11)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r11 = r6.chats
            java.lang.Object r11 = r11.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r11
            int r11 = r11.id
            int r11 = -r11
            long r11 = (long) r11
            goto L_0x01f1
        L_0x01d6:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r11 = r6.users
            java.lang.Object r11 = r11.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$User r11 = (im.bclpbkiauv.tgnet.TLRPC.User) r11
            int r11 = r11.id
            java.lang.String r12 = "user_id"
            r7.putInt(r12, r11)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r11 = r6.users
            java.lang.Object r11 = r11.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$User r11 = (im.bclpbkiauv.tgnet.TLRPC.User) r11
            int r11 = r11.id
            long r11 = (long) r11
        L_0x01f1:
            if (r5 == 0) goto L_0x020d
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r13 = r6.users
            int r13 = r13.size()
            if (r13 <= 0) goto L_0x020d
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r13 = r6.users
            java.lang.Object r8 = r13.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$User r8 = (im.bclpbkiauv.tgnet.TLRPC.User) r8
            boolean r8 = r8.bot
            if (r8 == 0) goto L_0x020d
            java.lang.String r8 = "botUser"
            r7.putString(r8, r5)
            r0 = 1
        L_0x020d:
            if (r25 == 0) goto L_0x0218
            int r8 = r25.intValue()
            java.lang.String r13 = "message_id"
            r7.putInt(r13, r8)
        L_0x0218:
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = mainFragmentsStack
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x022e
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r8 = mainFragmentsStack
            int r9 = r8.size()
            int r9 = r9 - r10
            java.lang.Object r8 = r8.get(r9)
            r9 = r8
            im.bclpbkiauv.ui.actionbar.BaseFragment r9 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r9
        L_0x022e:
            r8 = r9
            if (r8 == 0) goto L_0x023b
            im.bclpbkiauv.messenger.MessagesController r9 = im.bclpbkiauv.messenger.MessagesController.getInstance(r22)
            boolean r9 = r9.checkCanOpenChat(r7, r8)
            if (r9 == 0) goto L_0x025d
        L_0x023b:
            if (r0 == 0) goto L_0x0253
            boolean r9 = r8 instanceof im.bclpbkiauv.ui.ChatActivity
            if (r9 == 0) goto L_0x0253
            r9 = r8
            im.bclpbkiauv.ui.ChatActivity r9 = (im.bclpbkiauv.ui.ChatActivity) r9
            long r9 = r9.getDialogId()
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x0253
            r9 = r8
            im.bclpbkiauv.ui.ChatActivity r9 = (im.bclpbkiauv.ui.ChatActivity) r9
            r9.setBotUser(r5)
            goto L_0x025d
        L_0x0253:
            im.bclpbkiauv.ui.ChatActivity r9 = new im.bclpbkiauv.ui.ChatActivity
            r9.<init>(r7)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r10 = r1.actionBarLayout
            r10.presentFragment(r9)
        L_0x025d:
            goto L_0x0273
        L_0x025e:
            java.lang.String r0 = "JoinToGroupErrorNotExist"
            r7 = 2131691729(0x7f0f08d1, float:1.9012538E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r7)     // Catch:{ Exception -> 0x026f }
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r0 = im.bclpbkiauv.ui.components.AlertsCreator.createSimpleAlert(r1, r0)     // Catch:{ Exception -> 0x026f }
            r0.show()     // Catch:{ Exception -> 0x026f }
            goto L_0x0273
        L_0x026f:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0273:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LaunchActivity.lambda$null$12$LaunchActivity(im.bclpbkiauv.ui.actionbar.AlertDialog, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error, java.lang.String, int, java.lang.String, java.lang.String, java.lang.Integer):void");
    }

    public /* synthetic */ void lambda$null$10$LaunchActivity(String game, int intentAccount, TLRPC.TL_contacts_resolvedPeer res, DialogsActivity fragment1, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        TLRPC.TL_inputMediaGame inputMediaGame = new TLRPC.TL_inputMediaGame();
        inputMediaGame.id = new TLRPC.TL_inputGameShortName();
        inputMediaGame.id.short_name = game;
        inputMediaGame.id.bot_id = MessagesController.getInstance(intentAccount).getInputUser(res.users.get(0));
        SendMessagesHelper.getInstance(intentAccount).sendGame(MessagesController.getInstance(intentAccount).getInputPeer((int) did), inputMediaGame, 0, 0);
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_part == 0) {
            args1.putInt("enc_id", high_id);
        } else if (lower_part > 0) {
            args1.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args1.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args1, fragment1)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.actionBarLayout.presentFragment(new ChatActivity(args1), true, false, true, false);
            return;
        }
    }

    public /* synthetic */ void lambda$null$11$LaunchActivity(int intentAccount, TLRPC.User user, String botChat, DialogsActivity fragment12, ArrayList dids, CharSequence message1, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args12 = new Bundle();
        args12.putBoolean("scrollToTopOnResume", true);
        args12.putInt("chat_id", -((int) did));
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(intentAccount);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            if (!instance.checkCanOpenChat(args12, arrayList.get(arrayList.size() - 1))) {
                return;
            }
        }
        NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        MessagesController.getInstance(intentAccount).addUserToChat(-((int) did), user, (TLRPC.ChatFull) null, 0, botChat, (BaseFragment) null, (Runnable) null);
        this.actionBarLayout.presentFragment(new ChatActivity(args12), true, false, true, false);
    }

    public /* synthetic */ void lambda$runLinkRequest$16$LaunchActivity(AlertDialog progressDialog, int intentAccount, String group, String username, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, Integer channelId, String game, HashMap auth, String lang, String unsupportedUrl, String code, TLRPC.TL_wallPaper wallPaper, String theme, TLObject response, TLRPC.TL_error error) {
        TLRPC.TL_error tL_error = error;
        AndroidUtilities.runOnUIThread(new Runnable(this, progressDialog, tL_error, response, intentAccount, group, username, sticker, botUser, botChat, message, hasUrl, messageId, channelId, game, auth, lang, unsupportedUrl, code, wallPaper, theme) {
            private final /* synthetic */ LaunchActivity f$0;
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ String f$10;
            private final /* synthetic */ boolean f$11;
            private final /* synthetic */ Integer f$12;
            private final /* synthetic */ Integer f$13;
            private final /* synthetic */ String f$14;
            private final /* synthetic */ HashMap f$15;
            private final /* synthetic */ String f$16;
            private final /* synthetic */ String f$17;
            private final /* synthetic */ String f$18;
            private final /* synthetic */ TLRPC.TL_wallPaper f$19;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ String f$20;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ String f$5;
            private final /* synthetic */ String f$6;
            private final /* synthetic */ String f$7;
            private final /* synthetic */ String f$8;
            private final /* synthetic */ String f$9;

            {
                this.f$0 = r3;
                this.f$1 = r4;
                this.f$2 = r5;
                this.f$3 = r6;
                this.f$4 = r7;
                this.f$5 = r8;
                this.f$6 = r9;
                this.f$7 = r10;
                this.f$8 = r11;
                this.f$9 = r12;
                this.f$10 = r13;
                this.f$11 = r14;
                this.f$12 = r15;
                this.f$13 = r16;
                this.f$14 = r17;
                this.f$15 = r18;
                this.f$16 = r19;
                this.f$17 = r20;
                this.f$18 = r21;
                this.f$19 = r22;
                this.f$20 = r23;
            }

            public final void run() {
                LaunchActivity launchActivity = this.f$0;
                LaunchActivity launchActivity2 = launchActivity;
                launchActivity2.lambda$null$15$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18, this.f$19, this.f$20);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x008c, code lost:
        if (r2.checkCanOpenChat(r1, r3.get(r3.size() - 1)) != false) goto L_0x008e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x010e  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0113  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$15$LaunchActivity(im.bclpbkiauv.ui.actionbar.AlertDialog r24, im.bclpbkiauv.tgnet.TLRPC.TL_error r25, im.bclpbkiauv.tgnet.TLObject r26, int r27, java.lang.String r28, java.lang.String r29, java.lang.String r30, java.lang.String r31, java.lang.String r32, java.lang.String r33, boolean r34, java.lang.Integer r35, java.lang.Integer r36, java.lang.String r37, java.util.HashMap r38, java.lang.String r39, java.lang.String r40, java.lang.String r41, im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper r42, java.lang.String r43) {
        /*
            r23 = this;
            r15 = r23
            r14 = r25
            boolean r0 = r23.isFinishing()
            if (r0 != 0) goto L_0x01b0
            r24.dismiss()     // Catch:{ Exception -> 0x000e }
            goto L_0x0014
        L_0x000e:
            r0 = move-exception
            r1 = r0
            r0 = r1
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0014:
            r0 = 2131692462(0x7f0f0bae, float:1.9014025E38)
            java.lang.String r1 = "OK"
            r2 = 2131689824(0x7f0f0160, float:1.9008674E38)
            java.lang.String r3 = "AppName"
            r13 = 0
            if (r14 != 0) goto L_0x0172
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r4 = r15.actionBarLayout
            if (r4 == 0) goto L_0x0172
            r12 = r26
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r12 = (im.bclpbkiauv.tgnet.TLRPC.ChatInvite) r12
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            r5 = 1
            r6 = 0
            if (r4 == 0) goto L_0x00b2
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            boolean r4 = im.bclpbkiauv.messenger.ChatObject.isLeftFromChat(r4)
            if (r4 == 0) goto L_0x0047
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            boolean r4 = r4.kicked
            if (r4 != 0) goto L_0x00b2
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            java.lang.String r4 = r4.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x00b2
        L_0x0047:
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r27)
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r12.chat
            r0.putChat(r1, r6)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r12.chat
            r0.add(r1)
            im.bclpbkiauv.messenger.MessagesStorage r1 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r27)
            r1.putUsersAndChats(r13, r0, r6, r5)
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r12.chat
            int r2 = r2.id
            java.lang.String r3 = "chat_id"
            r1.putInt(r3, r2)
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r2 = mainFragmentsStack
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x008e
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r27)
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r3 = mainFragmentsStack
            int r4 = r3.size()
            int r4 = r4 - r5
            java.lang.Object r3 = r3.get(r4)
            im.bclpbkiauv.ui.actionbar.BaseFragment r3 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r3
            boolean r2 = r2.checkCanOpenChat(r1, r3)
            if (r2 == 0) goto L_0x00af
        L_0x008e:
            im.bclpbkiauv.ui.ChatActivity r2 = new im.bclpbkiauv.ui.ChatActivity
            r2.<init>(r1)
            r17 = r2
            im.bclpbkiauv.messenger.NotificationCenter r2 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r27)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.closeChats
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r2.postNotificationName(r3, r4)
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r2 = r15.actionBarLayout
            r18 = 0
            r19 = 1
            r20 = 1
            r21 = 0
            r16 = r2
            r16.presentFragment(r17, r18, r19, r20, r21)
        L_0x00af:
            r5 = r15
            goto L_0x016f
        L_0x00b2:
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            if (r4 != 0) goto L_0x00be
            boolean r4 = r12.channel
            if (r4 == 0) goto L_0x00d4
            boolean r4 = r12.megagroup
            if (r4 != 0) goto L_0x00d4
        L_0x00be:
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            if (r4 == 0) goto L_0x00f6
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            boolean r4 = im.bclpbkiauv.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x00d4
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            boolean r4 = r4.megagroup
            if (r4 == 0) goto L_0x00d1
            goto L_0x00d4
        L_0x00d1:
            r11 = r28
            goto L_0x00f8
        L_0x00d4:
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r4 = mainFragmentsStack
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x00f6
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r0 = mainFragmentsStack
            int r1 = r0.size()
            int r1 = r1 - r5
            java.lang.Object r0 = r0.get(r1)
            im.bclpbkiauv.ui.actionbar.BaseFragment r0 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r0
            im.bclpbkiauv.ui.components.JoinGroupAlert r1 = new im.bclpbkiauv.ui.components.JoinGroupAlert
            r11 = r28
            r1.<init>(r15, r12, r11, r0)
            r0.showDialog(r1)
            r5 = r15
            goto L_0x016f
        L_0x00f6:
            r11 = r28
        L_0x00f8:
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r4 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            r4.<init>((android.content.Context) r15)
            r10 = r4
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r10.setTitle(r2)
            r2 = 2131690431(0x7f0f03bf, float:1.9009905E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            if (r4 == 0) goto L_0x0113
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r12.chat
            java.lang.String r4 = r4.title
            goto L_0x0115
        L_0x0113:
            java.lang.String r4 = r12.title
        L_0x0115:
            r3[r6] = r4
            java.lang.String r4 = "ChannelJoinTo"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r10.setMessage(r2)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$ycZdajBLEbIjBVDBuEmTeZXOPyg r9 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$ycZdajBLEbIjBVDBuEmTeZXOPyg
            r1 = r9
            r2 = r23
            r3 = r27
            r4 = r29
            r5 = r28
            r6 = r30
            r7 = r31
            r8 = r32
            r20 = r0
            r0 = r9
            r9 = r33
            r21 = r0
            r0 = r10
            r10 = r34
            r11 = r35
            r22 = r12
            r12 = r36
            r13 = r37
            r14 = r38
            r15 = r39
            r16 = r40
            r17 = r41
            r18 = r42
            r19 = r43
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r1 = r20
            r2 = r21
            r0.setPositiveButton(r1, r2)
            r1 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r4 = 0
            r0.setNegativeButton(r1, r4)
            r5 = r23
            r5.showAlertDialog((im.bclpbkiauv.ui.actionbar.AlertDialog.Builder) r0)
        L_0x016f:
            r2 = r25
            goto L_0x01b2
        L_0x0172:
            r4 = r13
            r5 = r15
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r6 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            r6.<init>((android.content.Context) r5)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r6.setTitle(r2)
            r2 = r25
            java.lang.String r3 = r2.text
            java.lang.String r7 = "FLOOD_WAIT"
            boolean r3 = r3.startsWith(r7)
            if (r3 == 0) goto L_0x0199
            r3 = 2131691324(0x7f0f073c, float:1.9011717E38)
            java.lang.String r7 = "FloodWait"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r3)
            r6.setMessage(r3)
            goto L_0x01a5
        L_0x0199:
            r3 = 2131691729(0x7f0f08d1, float:1.9012538E38)
            java.lang.String r7 = "JoinToGroupErrorNotExist"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r3)
            r6.setMessage(r3)
        L_0x01a5:
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r6.setPositiveButton(r0, r4)
            r5.showAlertDialog((im.bclpbkiauv.ui.actionbar.AlertDialog.Builder) r6)
            goto L_0x01b2
        L_0x01b0:
            r2 = r14
            r5 = r15
        L_0x01b2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LaunchActivity.lambda$null$15$LaunchActivity(im.bclpbkiauv.ui.actionbar.AlertDialog, im.bclpbkiauv.tgnet.TLRPC$TL_error, im.bclpbkiauv.tgnet.TLObject, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Integer, java.lang.Integer, java.lang.String, java.util.HashMap, java.lang.String, java.lang.String, java.lang.String, im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper, java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$14$LaunchActivity(int intentAccount, String username, String group, String sticker, String botUser, String botChat, String message, boolean hasUrl, Integer messageId, Integer channelId, String game, HashMap auth, String lang, String unsupportedUrl, String code, TLRPC.TL_wallPaper wallPaper, String theme, DialogInterface dialogInterface, int i) {
        runLinkRequest(intentAccount, username, group, sticker, botUser, botChat, message, hasUrl, messageId, channelId, game, auth, lang, unsupportedUrl, code, wallPaper, theme, 1);
    }

    public /* synthetic */ void lambda$runLinkRequest$18$LaunchActivity(int intentAccount, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            MessagesController.getInstance(intentAccount).processUpdates((TLRPC.Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, response, intentAccount) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$17$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$LaunchActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLObject response, int intentAccount) {
        if (!isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (error != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                if (error.text.startsWith("FLOOD_WAIT")) {
                    builder.setMessage(LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.text.equals("USERS_TOO_MUCH")) {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
                } else {
                    builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder);
            } else if (this.actionBarLayout != null) {
                TLRPC.Updates updates = (TLRPC.Updates) response;
                if (!updates.chats.isEmpty()) {
                    TLRPC.Chat chat = updates.chats.get(0);
                    chat.left = false;
                    chat.kicked = false;
                    MessagesController.getInstance(intentAccount).putUsers(updates.users, false);
                    MessagesController.getInstance(intentAccount).putChats(updates.chats, false);
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.id);
                    if (!mainFragmentsStack.isEmpty()) {
                        MessagesController instance = MessagesController.getInstance(intentAccount);
                        ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                        if (!instance.checkCanOpenChat(args, arrayList.get(arrayList.size() - 1))) {
                            return;
                        }
                    }
                    ChatActivity fragment = new ChatActivity(args);
                    NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    this.actionBarLayout.presentFragment(fragment, false, true, true, false);
                }
            }
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$19$LaunchActivity(boolean hasUrl, int intentAccount, String message, DialogsActivity fragment13, ArrayList dids, CharSequence m, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args13 = new Bundle();
        args13.putBoolean("scrollToTopOnResume", true);
        args13.putBoolean("hasUrl", hasUrl);
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_part == 0) {
            args13.putInt("enc_id", high_id);
        } else if (lower_part > 0) {
            args13.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args13.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(intentAccount).checkCanOpenChat(args13, fragment13)) {
            NotificationCenter.getInstance(intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            MediaDataController.getInstance(intentAccount).saveDraft(did, message, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.Message) null, false);
            this.actionBarLayout.presentFragment(new ChatActivity(args13), true, false, true, false);
            return;
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$23$LaunchActivity(int[] requestId, int intentAccount, AlertDialog progressDialog, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response, TLRPC.TL_error error) {
        TLRPC.TL_account_authorizationForm authorizationForm = (TLRPC.TL_account_authorizationForm) response;
        if (authorizationForm != null) {
            requestId[0] = ConnectionsManager.getInstance(intentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new RequestDelegate(progressDialog, intentAccount, authorizationForm, req, payload, nonce, callbackUrl) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ TLRPC.TL_account_authorizationForm f$3;
                private final /* synthetic */ TLRPC.TL_account_getAuthorizationForm f$4;
                private final /* synthetic */ String f$5;
                private final /* synthetic */ String f$6;
                private final /* synthetic */ String f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LaunchActivity.this.lambda$null$21$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
                }
            });
            TLRPC.TL_error tL_error = error;
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$22$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$21$LaunchActivity(AlertDialog progressDialog, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, response1, intentAccount, authorizationForm, req, payload, nonce, callbackUrl) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ TLRPC.TL_account_authorizationForm f$4;
            private final /* synthetic */ TLRPC.TL_account_getAuthorizationForm f$5;
            private final /* synthetic */ String f$6;
            private final /* synthetic */ String f$7;
            private final /* synthetic */ String f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$20$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$20$LaunchActivity(AlertDialog progressDialog, TLObject response1, int intentAccount, TLRPC.TL_account_authorizationForm authorizationForm, TLRPC.TL_account_getAuthorizationForm req, String payload, String nonce, String callbackUrl) {
        TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm = req;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response1 != null) {
            MessagesController.getInstance(intentAccount).putUsers(authorizationForm.users, false);
            lambda$runLinkRequest$28$LaunchActivity(new PassportActivity(5, tL_account_getAuthorizationForm.bot_id, tL_account_getAuthorizationForm.scope, tL_account_getAuthorizationForm.public_key, payload, nonce, callbackUrl, authorizationForm, (TLRPC.TL_account_password) response1));
            return;
        }
        TLRPC.TL_account_authorizationForm tL_account_authorizationForm = authorizationForm;
    }

    public /* synthetic */ void lambda$null$22$LaunchActivity(AlertDialog progressDialog, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
            if ("APP_VERSION_OUTDATED".equals(error.text)) {
                AlertsCreator.showUpdateAppAlert(this, LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$25$LaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, response) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$24$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$24$LaunchActivity(AlertDialog progressDialog, TLObject response) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response instanceof TLRPC.TL_help_deepLinkInfo) {
            TLRPC.TL_help_deepLinkInfo res = (TLRPC.TL_help_deepLinkInfo) response;
            AlertsCreator.showUpdateAppAlert(this, res.message, res.update_app);
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$27$LaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, response, error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$26$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$26$LaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response instanceof TLRPC.TL_langPackLanguage) {
            showAlertDialog(AlertsCreator.createLanguageAlert(this, (TLRPC.TL_langPackLanguage) response));
        } else if (error == null) {
        } else {
            if ("LANG_CODE_NOT_SUPPORTED".equals(error.text)) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LanguageUnsupportedError", R.string.LanguageUnsupportedError)));
                return;
            }
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text));
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$30$LaunchActivity(AlertDialog progressDialog, TLRPC.TL_wallPaper wallPaper, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, response, wallPaper, error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_wallPaper f$3;
            private final /* synthetic */ TLRPC.TL_error f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$29$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: im.bclpbkiauv.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: im.bclpbkiauv.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$29$LaunchActivity(im.bclpbkiauv.ui.actionbar.AlertDialog r12, im.bclpbkiauv.tgnet.TLObject r13, im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper r14, im.bclpbkiauv.tgnet.TLRPC.TL_error r15) {
        /*
            r11 = this;
            r12.dismiss()     // Catch:{ Exception -> 0x0004 }
            goto L_0x0008
        L_0x0004:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0008:
            boolean r0 = r13 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper
            if (r0 == 0) goto L_0x0049
            r0 = r13
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper) r0
            boolean r1 = r0.pattern
            if (r1 == 0) goto L_0x0033
            im.bclpbkiauv.ui.WallpapersListActivity$ColorWallpaper r1 = new im.bclpbkiauv.ui.WallpapersListActivity$ColorWallpaper
            r3 = -1
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r2 = r14.settings
            int r5 = r2.background_color
            long r6 = r0.id
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r2 = r14.settings
            int r2 = r2.intensity
            float r2 = (float) r2
            r8 = 1120403456(0x42c80000, float:100.0)
            float r8 = r2 / r8
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r2 = r14.settings
            boolean r9 = r2.motion
            r10 = 0
            r2 = r1
            r2.<init>(r3, r5, r6, r8, r9, r10)
            r1.pattern = r0
            goto L_0x0034
        L_0x0033:
            r1 = r0
        L_0x0034:
            im.bclpbkiauv.ui.WallpaperActivity r2 = new im.bclpbkiauv.ui.WallpaperActivity
            r3 = 0
            r2.<init>(r1, r3)
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r3 = r14.settings
            boolean r3 = r3.blur
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaperSettings r4 = r14.settings
            boolean r4 = r4.motion
            r2.setInitialModes(r3, r4)
            r11.lambda$runLinkRequest$28$LaunchActivity(r2)
            goto L_0x006f
        L_0x0049:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 2131691144(0x7f0f0688, float:1.9011352E38)
            java.lang.String r2 = "ErrorOccurred"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.append(r1)
            java.lang.String r1 = "\n"
            r0.append(r1)
            java.lang.String r1 = r15.text
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r0 = im.bclpbkiauv.ui.components.AlertsCreator.createSimpleAlert(r11, r0)
            r11.showAlertDialog((im.bclpbkiauv.ui.actionbar.AlertDialog.Builder) r0)
        L_0x006f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LaunchActivity.lambda$null$29$LaunchActivity(im.bclpbkiauv.ui.actionbar.AlertDialog, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$runLinkRequest$31$LaunchActivity() {
        this.loadingThemeFileName = null;
        this.loadingThemeWallpaperName = null;
        this.loadingThemeInfo = null;
        this.loadingThemeProgressDialog = null;
        this.loadingTheme = null;
    }

    public /* synthetic */ void lambda$runLinkRequest$33$LaunchActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response, progressDialog, error) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ AlertDialog f$2;
            private final /* synthetic */ TLRPC.TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$32$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$32$LaunchActivity(TLObject response, AlertDialog progressDialog, TLRPC.TL_error error) {
        int notFound = 2;
        if (response instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme t = (TLRPC.TL_theme) response;
            if (t.document != null) {
                this.loadingTheme = t;
                this.loadingThemeFileName = FileLoader.getAttachFileName(t.document);
                this.loadingThemeProgressDialog = progressDialog;
                FileLoader.getInstance(this.currentAccount).loadFile(this.loadingTheme.document, t, 1, 1);
                notFound = 0;
            } else {
                notFound = 1;
            }
        } else if (error != null && "THEME_FORMAT_INVALID".equals(error.text)) {
            notFound = 1;
        }
        if (notFound != 0) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (notFound == 1) {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("ThemeNotSupported", R.string.ThemeNotSupported)));
            } else {
                showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("Theme", R.string.Theme), LocaleController.getString("ThemeNotFound", R.string.ThemeNotFound)));
            }
        }
    }

    public /* synthetic */ void lambda$runLinkRequest$36$LaunchActivity(Bundle args, Integer channelId, int[] requestId, AlertDialog progressDialog, BaseFragment lastFragment, int intentAccount) {
        Bundle bundle = args;
        if (!this.actionBarLayout.presentFragment(new ChatActivity(args))) {
            TLRPC.TL_channels_getChannels req = new TLRPC.TL_channels_getChannels();
            TLRPC.TL_inputChannel inputChannel = new TLRPC.TL_inputChannel();
            inputChannel.channel_id = channelId.intValue();
            req.id.add(inputChannel);
            requestId[0] = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog, lastFragment, intentAccount, args) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ Bundle f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LaunchActivity.this.lambda$null$35$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$35$LaunchActivity(AlertDialog progressDialog, BaseFragment lastFragment, int intentAccount, Bundle args, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, response, lastFragment, intentAccount, args) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ BaseFragment f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ Bundle f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$34$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$34$LaunchActivity(AlertDialog progressDialog, TLObject response, BaseFragment lastFragment, int intentAccount, Bundle args) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        boolean notFound = true;
        if (response instanceof TLRPC.TL_messages_chats) {
            TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;
            if (!res.chats.isEmpty()) {
                notFound = false;
                MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
                TLRPC.Chat chat = (TLRPC.Chat) res.chats.get(0);
                if (lastFragment == null || MessagesController.getInstance(intentAccount).checkCanOpenChat(args, lastFragment)) {
                    this.actionBarLayout.presentFragment(new ChatActivity(args));
                }
            }
        }
        if (notFound) {
            showAlertDialog(AlertsCreator.createSimpleAlert(this, LocaleController.getString("LinkNotFound", R.string.LinkNotFound)));
        }
    }

    static /* synthetic */ void lambda$runLinkRequest$37(int intentAccount, int[] requestId, Runnable cancelRunnableFinal, DialogInterface dialog) {
        ConnectionsManager.getInstance(intentAccount).cancelRequest(requestId[0], true);
        if (cancelRunnableFinal != null) {
            cancelRunnableFinal.run();
        }
    }

    public void checkAppUpdate(final boolean isClick) {
        if (isClick) {
            showCheckUpdateDialog();
        }
        AppUpdater.getInstance(this.currentAccount).checkAppUpdate(new AppUpdater.OnForceUpdateCallback() {
            public void onForce(TLRPC.TL_help_appUpdate res) {
                LaunchActivity.this.dismissCheckUpdateDialog();
                if (LaunchActivity.this.updateAppAlertDialog == null || !LaunchActivity.this.updateAppAlertDialog.isShowing()) {
                    LaunchActivity launchActivity = LaunchActivity.this;
                    LaunchActivity launchActivity2 = LaunchActivity.this;
                    UpdateAppAlertDialog unused = launchActivity.updateAppAlertDialog = new UpdateAppAlertDialog(launchActivity2, res, launchActivity2.currentAccount);
                    LaunchActivity.this.updateAppAlertDialog.show();
                }
            }

            public void onNormal(TLRPC.TL_help_appUpdate res) {
                LaunchActivity.this.dismissCheckUpdateDialog();
                if (LaunchActivity.this.updateAppAlertDialog == null || !LaunchActivity.this.updateAppAlertDialog.isShowing()) {
                    LaunchActivity launchActivity = LaunchActivity.this;
                    LaunchActivity launchActivity2 = LaunchActivity.this;
                    UpdateAppAlertDialog unused = launchActivity.updateAppAlertDialog = new UpdateAppAlertDialog(launchActivity2, res, launchActivity2.currentAccount);
                    LaunchActivity.this.updateAppAlertDialog.show();
                }
            }

            public void onNoUpdate() {
                LaunchActivity.this.dismissCheckUpdateDialog();
                LaunchActivity.this.dismissUpdateAppAlertDialog();
                if (isClick) {
                    ToastUtils.show((int) R.string.NoUpdate);
                }
            }
        }, isClick);
    }

    private void showCheckUpdateDialog() {
        dismissCheckUpdateDialog();
        AlertDialog alertDialog = new AlertDialog(this, 3);
        this.checkUpdateDialog = alertDialog;
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                LaunchActivity.this.lambda$showCheckUpdateDialog$38$LaunchActivity(dialogInterface);
            }
        });
        this.checkUpdateDialog.show();
    }

    public /* synthetic */ void lambda$showCheckUpdateDialog$38$LaunchActivity(DialogInterface dialog) {
        AppUpdater.getInstance(this.currentAccount).cancel();
    }

    /* access modifiers changed from: private */
    public void dismissCheckUpdateDialog() {
        AlertDialog alertDialog = this.checkUpdateDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.checkUpdateDialog = null;
        }
    }

    /* access modifiers changed from: private */
    public void dismissUpdateAppAlertDialog() {
        UpdateAppAlertDialog updateAppAlertDialog2 = this.updateAppAlertDialog;
        if (updateAppAlertDialog2 != null) {
            updateAppAlertDialog2.dismiss();
            this.updateAppAlertDialog = null;
        }
    }

    public AlertDialog showAlertDialog(AlertDialog.Builder builder) {
        return showAlertDialog(builder.show());
    }

    public AlertDialog showAlertDialog(AlertDialog dialog) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            this.visibleDialog = dialog;
            dialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    if (LaunchActivity.this.visibleDialog != null) {
                        if (LaunchActivity.this.visibleDialog == LaunchActivity.this.localeDialog) {
                            try {
                                ToastUtils.show((CharSequence) LaunchActivity.this.getStringForLanguageAlert(LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals("en") ? LaunchActivity.this.englishLocaleStrings : LaunchActivity.this.systemLocaleStrings, "ChangeLanguageLater", R.string.ChangeLanguageLater));
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                            AlertDialog unused = LaunchActivity.this.localeDialog = null;
                        } else if (LaunchActivity.this.visibleDialog == LaunchActivity.this.proxyErrorDialog) {
                            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                            SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                            editor.putBoolean("proxy_enabled", false);
                            editor.putBoolean("proxy_enabled_calls", false);
                            editor.commit();
                            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
                            ConnectionsManager.setProxySettings(false, "", 1080, "", "", "");
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                            AlertDialog unused2 = LaunchActivity.this.proxyErrorDialog = null;
                        }
                    }
                    AlertDialog unused3 = LaunchActivity.this.visibleDialog = null;
                }
            });
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        queryProxyAccount(intent);
        handleIntent(intent, true, false, false);
    }

    public void didSelectDialogs(DialogsActivity dialogsFragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        int attachesCount;
        long did;
        int i;
        DialogsActivity dialogsActivity = dialogsFragment;
        long did2 = dids.get(0).longValue();
        int lower_part = (int) did2;
        int high_id = (int) (did2 >> 32);
        int attachesCount2 = 0;
        ArrayList<TLRPC.User> arrayList = this.contactsToSend;
        if (arrayList != null) {
            attachesCount2 = 0 + arrayList.size();
        }
        if (this.videoPath != null) {
            attachesCount2++;
        }
        ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList2 = this.photoPathsArray;
        if (arrayList2 != null) {
            attachesCount2 += arrayList2.size();
        }
        ArrayList<String> arrayList3 = this.documentsPathsArray;
        if (arrayList3 != null) {
            attachesCount2 += arrayList3.size();
        }
        ArrayList<Uri> arrayList4 = this.documentsUrisArray;
        if (arrayList4 != null) {
            attachesCount2 += arrayList4.size();
        }
        if (this.videoPath == null && this.photoPathsArray == null && this.documentsPathsArray == null && this.documentsUrisArray == null && this.sendingText != null) {
            attachesCount = attachesCount2 + 1;
        } else {
            attachesCount = attachesCount2;
        }
        if (!AlertsCreator.checkSlowMode(this, this.currentAccount, did2, attachesCount > 1)) {
            Bundle args = new Bundle();
            int account = dialogsActivity != null ? dialogsFragment.getCurrentAccount() : this.currentAccount;
            args.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            }
            if (lower_part == 0) {
                args.putInt("enc_id", high_id);
            } else if (lower_part > 0) {
                args.putInt("user_id", lower_part);
            } else if (lower_part < 0) {
                args.putInt("chat_id", -lower_part);
            }
            if (MessagesController.getInstance(account).checkCanOpenChat(args, dialogsActivity)) {
                ChatActivity fragment = new ChatActivity(args);
                ArrayList<TLRPC.User> arrayList5 = this.contactsToSend;
                if (arrayList5 == null || arrayList5.size() != 1) {
                    Bundle bundle = args;
                    ChatActivity fragment2 = fragment;
                    AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
                    this.actionBarLayout.presentFragment(fragment2, dialogsActivity != null, dialogsActivity == null, true, false);
                    String str = this.videoPath;
                    if (str != null) {
                        fragment2.openVideoEditor(str, this.sendingText);
                        this.sendingText = null;
                    }
                    if (this.photoPathsArray != null) {
                        String str2 = this.sendingText;
                        if (str2 != null && str2.length() <= 1024 && this.photoPathsArray.size() == 1) {
                            this.photoPathsArray.get(0).caption = this.sendingText;
                            this.sendingText = null;
                        }
                        i = 1;
                        int i2 = attachesCount;
                        int i3 = lower_part;
                        int i4 = high_id;
                        did = did2;
                        SendMessagesHelper.prepareSendingMedia(accountInstance, this.photoPathsArray, did2, (MessageObject) null, (InputContentInfoCompat) null, false, false, (MessageObject) null, true, 0, false);
                    } else {
                        int i5 = lower_part;
                        int i6 = high_id;
                        did = did2;
                        i = 1;
                    }
                    if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                        String caption = null;
                        String str3 = this.sendingText;
                        if (str3 != null && str3.length() <= 1024) {
                            ArrayList<String> arrayList6 = this.documentsPathsArray;
                            int size = arrayList6 != null ? arrayList6.size() : 0;
                            ArrayList<Uri> arrayList7 = this.documentsUrisArray;
                            if (size + (arrayList7 != null ? arrayList7.size() : 0) == i) {
                                caption = this.sendingText;
                                this.sendingText = null;
                            }
                        }
                        SendMessagesHelper.prepareSendingDocuments(accountInstance, this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, caption, this.documentsMimeType, did, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                    }
                    String str4 = this.sendingText;
                    if (str4 != null) {
                        SendMessagesHelper.prepareSendingText(accountInstance, str4, did, true, 0);
                    }
                    ArrayList<TLRPC.User> arrayList8 = this.contactsToSend;
                    if (arrayList8 != null && !arrayList8.isEmpty()) {
                        for (int a = 0; a < this.contactsToSend.size(); a++) {
                            SendMessagesHelper.getInstance(account).sendMessage(this.contactsToSend.get(a), did, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        }
                    }
                } else if (this.contactsToSend.size() == 1) {
                    $$Lambda$LaunchActivity$nEc9ig058bxGNYASc3xhEKfLmgo r16 = r0;
                    PhonebookShareActivity contactFragment = new PhonebookShareActivity((ContactsController.Contact) null, this.contactsToSendUri, (File) null, (String) null);
                    Bundle bundle2 = args;
                    ChatActivity fragment3 = fragment;
                    $$Lambda$LaunchActivity$nEc9ig058bxGNYASc3xhEKfLmgo r0 = new PhoneBookSelectActivity.PhoneBookSelectActivityDelegate(fragment, account, did2) {
                        private final /* synthetic */ ChatActivity f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ long f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void didSelectContact(TLRPC.User user, boolean z, int i) {
                            LaunchActivity.this.lambda$didSelectDialogs$39$LaunchActivity(this.f$1, this.f$2, this.f$3, user, z, i);
                        }
                    };
                    contactFragment.setDelegate(r0);
                    this.actionBarLayout.presentFragment(contactFragment, dialogsActivity != null, dialogsActivity == null, true, false);
                    int i7 = attachesCount;
                    int i8 = lower_part;
                    int i9 = high_id;
                    long j = did2;
                    ChatActivity chatActivity = fragment3;
                } else {
                    Bundle bundle3 = args;
                    int i10 = attachesCount;
                    int i11 = lower_part;
                    int i12 = high_id;
                    long j2 = did2;
                    ChatActivity chatActivity2 = fragment;
                }
                this.photoPathsArray = null;
                this.videoPath = null;
                this.sendingText = null;
                this.documentsPathsArray = null;
                this.documentsOriginalPathsArray = null;
                this.contactsToSend = null;
                this.contactsToSendUri = null;
            }
        }
    }

    public /* synthetic */ void lambda$didSelectDialogs$39$LaunchActivity(ChatActivity fragment, int account, long did, TLRPC.User user, boolean notify, int scheduleDate) {
        this.actionBarLayout.presentFragment(fragment, true, false, true, false);
        SendMessagesHelper.getInstance(account).sendMessage(user, did, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate);
    }

    private void onFinish() {
        if (!this.finished) {
            this.finished = true;
            Runnable runnable = this.lockRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.lockRunnable = null;
            }
            int i = this.currentAccount;
            if (i != -1) {
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.receivedAVideoCallRequest);
        }
    }

    /* renamed from: presentFragment */
    public void lambda$runLinkRequest$28$LaunchActivity(BaseFragment fragment) {
        this.actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        return this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    public ActionBarLayout getActionBarLayout() {
        return this.actionBarLayout;
    }

    public ActionBarLayout getLayersActionBarLayout() {
        return this.layersActionBarLayout;
    }

    public ActionBarLayout getRightActionBarLayout() {
        return this.rightActionBarLayout;
    }

    private void parseSechmeOpenAccount(String url) {
        if (!TextUtils.isEmpty(url)) {
            String result = url.replace("hchat:openKey=", "").replace("hchat://openKey=", "");
            if (!TextUtils.isEmpty(result)) {
                String ret = new String(Base64.decode(result.replace("%3D", "="), 0));
                String[] split = ret.split("#");
                String pUid = split[0].split("=")[1];
                String hash = split[1].split("=")[1];
                if (ret.contains("Uname")) {
                    String uName = split[2].split("=")[1];
                    boolean closeLast = true;
                    if (getActionBarLayout().fragmentsStack != null && getActionBarLayout().fragmentsStack.size() > 1) {
                        closeLast = false;
                    }
                    MessagesController.getInstance(UserConfig.selectedAccount).openByUserName(uName, getActionBarLayout().getCurrentFragment(), 1, closeLast);
                    return;
                }
                TLRPC.User user = new TLRPC.TL_user();
                try {
                    user.id = Integer.valueOf(pUid).intValue();
                    user.access_hash = Long.valueOf(hash).longValue();
                    getUserInfo(user);
                } catch (NumberFormatException e) {
                    FileLog.e("parse qr code err:" + e);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean z = false;
        if (!(SharedConfig.passcodeHash.length() == 0 || SharedConfig.lastPauseTime == 0)) {
            SharedConfig.lastPauseTime = 0;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_SERVICES_REQUEST_CHECK_SETTINGS) {
            LocationController instance = LocationController.getInstance(this.currentAccount);
            if (resultCode == -1) {
                z = true;
            }
            instance.startFusedLocationRequest(z);
            return;
        }
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onActivityResult(requestCode, resultCode, data);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1).onActivityResultFragment(requestCode, resultCode, data);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1).onActivityResultFragment(requestCode, resultCode, data);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1).onActivityResultFragment(requestCode, resultCode, data);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = grantResults.length > 0 && grantResults[0] == 0;
        if (requestCode == 4 || requestCode == 17) {
            if (!granted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
            } else {
                ImageLoader.getInstance().checkMediaPaths();
            }
        } else if (requestCode == 5) {
            if (!granted) {
                ContactsController.getInstance(this.currentAccount).forceImportContacts();
            } else {
                showPermissionErrorAlert(LocaleController.getString("PermissionContacts", R.string.PermissionContacts));
                return;
            }
        } else if (requestCode == 3) {
            boolean audioGranted = true;
            boolean cameraGranted = true;
            int size = permissions.length;
            for (int i = 0; i < size; i++) {
                if ("android.permission.RECORD_AUDIO".equals(permissions[i])) {
                    audioGranted = grantResults[i] == 0;
                } else if ("android.permission.CAMERA".equals(permissions[i])) {
                    cameraGranted = grantResults[i] == 0;
                }
            }
            if (!audioGranted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoAudio", R.string.PermissionNoAudio));
            } else if (!cameraGranted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoCamera", R.string.PermissionNoCamera));
            } else if (SharedConfig.inappCamera) {
                CameraController.getInstance().initCamera((Runnable) null);
                return;
            } else {
                return;
            }
        } else if (requestCode == 18 || requestCode == 19 || requestCode == 20 || requestCode == 22) {
            if (!granted) {
                showPermissionErrorAlert(LocaleController.getString("PermissionNoCamera", R.string.PermissionNoCamera));
            }
        } else if (requestCode == 2 && granted) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
        }
        if (requestCode == 102 && Build.VERSION.SDK_INT < 29) {
            boolean needStartService = true;
            int i2 = 0;
            while (true) {
                if (i2 >= permissions.length) {
                    break;
                }
                String per = permissions[i2];
                if ((grantResults[i2] == 0) || !(per == "android.permission.READ_CALL_LOG" || per == "android.permission.WRITE_CALL_LOG")) {
                    i2++;
                }
            }
            needStartService = false;
            if (needStartService) {
                if (Build.VERSION.SDK_INT < 26 || Build.VERSION.SDK_INT == 28) {
                    startService(new Intent(this, CallApiBelow26And28Service.class));
                } else if (Build.VERSION.SDK_INT == 26 || Build.VERSION.SDK_INT == 27) {
                    Intent intent = new Intent("android.telecom.action.CHANGE_DEFAULT_DIALER");
                    intent.putExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME", getApplicationContext().getPackageName());
                    intent.addFlags(C.ENCODING_PCM_MU_LAW);
                    startActivity(intent);
                }
            }
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1).onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
        }
    }

    private void showPermissionErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(message);
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LaunchActivity.this.lambda$showPermissionErrorAlert$40$LaunchActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    public /* synthetic */ void lambda$showPermissionErrorAlert$40$LaunchActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("LaunchActivity ---> onPause");
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4096);
        SharedConfig.lastAppPauseTime = System.currentTimeMillis();
        ApplicationLoader.mainInterfacePaused = true;
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$U21rWfNlGrlgZqsW8UYxEvlFyY0.INSTANCE);
        onPasscodePause();
        this.actionBarLayout.onPause();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onPause();
            this.layersActionBarLayout.onPause();
        }
        PasscodeView passcodeView2 = this.passcodeView;
        if (passcodeView2 != null) {
            passcodeView2.onPause();
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onPause();
        }
    }

    static /* synthetic */ void lambda$onPause$41() {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (!AndroidUtilities.isAppOnForeground(this)) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.hideAVideoFloatWindow, 1);
            this.mBytJumpFromBack = 1;
            clearNotification();
        }
        Browser.unbindCustomTabsService(this);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (PhotoViewer.getPipInstance() != null) {
            PhotoViewer.getPipInstance().destroyPhotoViewer();
        }
        if (PhotoViewer.hasInstance()) {
            PhotoViewer.getInstance().destroyPhotoViewer();
        }
        if (ImagePreviewActivity.getPipInstance() != null) {
            ImagePreviewActivity.getPipInstance().destroyPhotoViewer();
        }
        if (ImagePreviewActivity.hasInstance()) {
            ImagePreviewActivity.getInstance().destroyPhotoViewer();
        }
        if (PlayerUtils.getPlayer() != null) {
            PlayerUtils.getPlayer().pause();
            PlayerUtils.getPlayer().destroy();
        }
        if (SecretMediaViewer.hasInstance()) {
            SecretMediaViewer.getInstance().destroyPhotoViewer();
        }
        if (ArticleViewer.hasInstance()) {
            ArticleViewer.getInstance().destroyArticleViewer();
        }
        if (ContentPreviewViewer.hasInstance()) {
            ContentPreviewViewer.getInstance().destroy();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        MediaController.getInstance().setBaseActivity(this, false);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, false);
        if (pipRoundVideoView != null) {
            pipRoundVideoView.close(false);
        }
        Theme.destroyResources();
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.destroy();
        }
        DiscoveryJumpPausedFloatingView.destroy();
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            if (this.onGlobalLayoutListener != null) {
                getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        super.onDestroy();
        onFinish();
    }

    private void clearNotification() {
        NotificationManager service = (NotificationManager) getSystemService("notification");
        if (service != null) {
            service.cancelAll();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MessageObject messageObject;
        super.onResume();
        byte b = this.mBytJumpFromBack;
        if (b == 1 || b == 0) {
            clearNotification();
        }
        this.mBytJumpFromBack = 2;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4096);
        MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
        ApplicationLoader.mainInterfacePaused = false;
        showLanguageAlert(false);
        Utilities.stageQueue.postRunnable($$Lambda$LaunchActivity$wlaQPVtRHFjIZBIKj6azOE5tLXg.INSTANCE);
        checkFreeDiscSpace();
        MediaController.checkGallery();
        onPasscodeResume();
        if (this.passcodeView.getVisibility() != 0) {
            this.actionBarLayout.onResume();
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onResume();
                this.layersActionBarLayout.onResume();
            }
        } else {
            this.actionBarLayout.dismissDialogs();
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.dismissDialogs();
                this.layersActionBarLayout.dismissDialogs();
            }
            this.passcodeView.onResume();
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        updateCurrentConnectionState(this.currentAccount);
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().onResume();
        }
        if (!(PipRoundVideoView.getInstance() == null || !MediaController.getInstance().isMessagePaused() || (messageObject = MediaController.getInstance().getPlayingMessageObject()) == null)) {
            MediaController.getInstance().seekToProgress(messageObject, messageObject.audioProgress);
        }
        if (UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService != null) {
            showTosActivity(UserConfig.selectedAccount, UserConfig.getInstance(UserConfig.selectedAccount).unacceptedTermsOfService);
        } else if (AppUpdater.pendingAppUpdate != null) {
            dismissCheckUpdateDialog();
            UpdateAppAlertDialog updateAppAlertDialog2 = this.updateAppAlertDialog;
            if (updateAppAlertDialog2 == null || !updateAppAlertDialog2.isShowing()) {
                UpdateAppAlertDialog updateAppAlertDialog3 = new UpdateAppAlertDialog(this, AppUpdater.pendingAppUpdate, this.currentAccount);
                this.updateAppAlertDialog = updateAppAlertDialog3;
                updateAppAlertDialog3.show();
            } else {
                return;
            }
        }
        checkAppUpdate(false);
        processVisualCallRequest();
        RingUtils.stopMediaPlayerRing();
        try {
            if (ApplicationLoader.mbytAVideoCallBusy == 1) {
                startActivity(new Intent(this, VisualCallReceiveActivity.class));
            } else if (ApplicationLoader.mbytAVideoCallBusy == 2) {
                startActivity(new Intent(this, VisualCallActivity.class));
            } else if (ApplicationLoader.mbytAVideoCallBusy == 3 || ApplicationLoader.mbytAVideoCallBusy == 4) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.hideAVideoFloatWindow, 0);
            }
        } catch (Exception e) {
        }
    }

    static /* synthetic */ void lambda$onResume$42() {
        ApplicationLoader.mainInterfacePausedStageQueue = false;
        ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
    }

    private void processVisualCallRequest() {
        ArrayList<VisualCallRequestParaBean> arrayList = DatabaseInstance.queryVisualCallRequest();
        if (arrayList.size() > 0) {
            this.actionBarLayout.postDelayed(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LaunchActivity.this.lambda$processVisualCallRequest$43$LaunchActivity(this.f$1);
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$processVisualCallRequest$43$LaunchActivity(ArrayList arrayList) {
        DatabaseInstance.deleteVisualCallRequest();
        VisualCallRequestParaBean paraBean = (VisualCallRequestParaBean) arrayList.get(0);
        Intent actIntent = new Intent(this, VisualCallReceiveActivity.class);
        actIntent.putExtra(MimeTypes.BASE_TYPE_VIDEO, paraBean.isVideo());
        actIntent.putExtra(TtmlNode.ATTR_ID, paraBean.getStrId());
        actIntent.putExtra("admin_id", paraBean.getAdmin_id());
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Launch call ===> processVisualCallRequest admin_id = " + paraBean.getAdmin_id());
        }
        actIntent.putExtra("app_id", paraBean.getApp_id());
        actIntent.putExtra("token", paraBean.getToken());
        actIntent.putStringArrayListExtra("gslb", new ArrayList(Arrays.asList(paraBean.getGslb().split(","))));
        actIntent.putExtra("json", paraBean.getJson());
        actIntent.putExtra("from", 1);
        actIntent.addFlags(C.ENCODING_PCM_MU_LAW);
        startActivity(actIntent);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        AndroidUtilities.checkDisplaySize(this, newConfig);
        super.onConfigurationChanged(newConfig);
        checkLayout();
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.onConfigurationChanged();
        }
        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
        if (embedBottomSheet != null) {
            embedBottomSheet.onConfigurationChanged(newConfig);
        }
        PhotoViewer photoViewer = PhotoViewer.getPipInstance();
        if (photoViewer != null) {
            photoViewer.onConfigurationChanged(newConfig);
        }
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onConfigurationChanged();
        }
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        AndroidUtilities.isInMultiwindow = isInMultiWindowMode;
        checkLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        View child;
        int i = id;
        int i2 = account;
        if (i == NotificationCenter.appDidLogout) {
            switchToAvailableAccountOrLogout();
            return;
        }
        boolean last = false;
        if (i == NotificationCenter.closeOtherAppActivities) {
            if (args[0] != this) {
                onFinish();
                finish();
            }
        } else if (i == NotificationCenter.didUpdateConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("switch to state " + state);
                }
                this.currentConnectionState = state;
                updateCurrentConnectionState(i2);
            }
        } else if (i == NotificationCenter.mainUserInfoChanged) {
            this.drawerLayoutAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.needShowAlert) {
            Integer reason = args[0];
            if (reason.intValue() == 3 && this.proxyErrorDialog != null) {
                return;
            }
            if (reason.intValue() == 4) {
                showTosActivity(i2, args[1]);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (!(reason.intValue() == 2 || reason.intValue() == 3)) {
                builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new DialogInterface.OnClickListener(i2) {
                    private final /* synthetic */ int f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LaunchActivity.lambda$didReceivedNotification$44(this.f$0, dialogInterface, i);
                    }
                });
            }
            if (reason.intValue() == 5) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam3", R.string.NobodyLikesSpam3));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            } else if (reason.intValue() == 0) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam1", R.string.NobodyLikesSpam1));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            } else if (reason.intValue() == 1) {
                builder.setMessage(LocaleController.getString("NobodyLikesSpam2", R.string.NobodyLikesSpam2));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            } else if (reason.intValue() == 2) {
                if ("ErrorSendMessageTooFreq".equals(args[1])) {
                    builder.setMessage(LocaleController.getString(R.string.ErrorSendMessageTooFreq));
                } else {
                    builder.setMessage(args[1]);
                }
                if (args[2].startsWith("AUTH_KEY_DROP_")) {
                    builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    builder.setNegativeButton(LocaleController.getString("LogOut", R.string.LogOut), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            LaunchActivity.this.lambda$didReceivedNotification$45$LaunchActivity(dialogInterface, i);
                        }
                    });
                } else {
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                }
            } else if (reason.intValue() == 3) {
                builder.setMessage(LocaleController.getString("UseProxyErrorTips", R.string.UseProxyErrorTips));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                this.proxyErrorDialog = showAlertDialog(builder);
                return;
            }
            if (!mainFragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList = mainFragmentsStack;
                arrayList.get(arrayList.size() - 1).showDialog(builder.create());
            }
        } else if (i == NotificationCenter.wasUnableToFindCurrentLocation) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) this);
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            builder2.setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", R.string.ShareYouLocationUnableManually), new DialogInterface.OnClickListener(args[0], i2) {
                private final /* synthetic */ HashMap f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.this.lambda$didReceivedNotification$47$LaunchActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder2.setMessage(LocaleController.getString("ShareYouLocationUnable", R.string.ShareYouLocationUnable));
            if (!mainFragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList2 = mainFragmentsStack;
                arrayList2.get(arrayList2.size() - 1).showDialog(builder2.create());
            }
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            RecyclerListView recyclerListView = this.sideMenu;
            if (recyclerListView != null && (child = recyclerListView.getChildAt(0)) != null) {
                child.invalidate();
            }
        } else if (i == NotificationCenter.didSetPasscode) {
            if (SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture) {
                try {
                    getWindow().setFlags(8192, 8192);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (!MediaController.getInstance().hasFlagSecureFragment()) {
                try {
                    getWindow().clearFlags(8192);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
        } else if (i == NotificationCenter.reloadInterface) {
            if (mainFragmentsStack.size() > 1) {
                ArrayList<BaseFragment> arrayList3 = mainFragmentsStack;
                if (arrayList3.get(arrayList3.size() - 1) instanceof SettingsActivity) {
                    last = true;
                }
            }
            rebuildAllFragments(last);
        } else if (i == NotificationCenter.suggestedLangpack) {
            showLanguageAlert(false);
        } else if (i == NotificationCenter.openArticle) {
            if (!mainFragmentsStack.isEmpty()) {
                ArticleViewer instance = ArticleViewer.getInstance();
                ArrayList<BaseFragment> arrayList4 = mainFragmentsStack;
                instance.setParentActivity(this, arrayList4.get(arrayList4.size() - 1));
                ArticleViewer.getInstance().open(args[0], args[1]);
            }
        } else if (i == NotificationCenter.hasNewContactsToImport) {
            ActionBarLayout actionBarLayout2 = this.actionBarLayout;
            if (actionBarLayout2 != null && !actionBarLayout2.fragmentsStack.isEmpty()) {
                int intValue = args[0].intValue();
                HashMap<String, ContactsController.Contact> contactHashMap = args[1];
                boolean first = args[2].booleanValue();
                boolean schedule = args[3].booleanValue();
                AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) this);
                builder3.setTitle(LocaleController.getString("UpdateContactsTitle", R.string.UpdateContactsTitle));
                builder3.setMessage(LocaleController.getString("UpdateContactsMessage", R.string.UpdateContactsMessage));
                builder3.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(i2, contactHashMap, first, schedule) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ HashMap f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ boolean f$3;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ContactsController.getInstance(this.f$0).syncPhoneBookByAlert(this.f$1, this.f$2, this.f$3, false);
                    }
                });
                builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener(i2, contactHashMap, first, schedule) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ HashMap f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ boolean f$3;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ContactsController.getInstance(this.f$0).syncPhoneBookByAlert(this.f$1, this.f$2, this.f$3, true);
                    }
                });
                builder3.setOnBackButtonListener(new DialogInterface.OnClickListener(i2, contactHashMap, first, schedule) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ HashMap f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ boolean f$3;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ContactsController.getInstance(this.f$0).syncPhoneBookByAlert(this.f$1, this.f$2, this.f$3, true);
                    }
                });
                AlertDialog dialog = builder3.create();
                this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1).showDialog(dialog);
                dialog.setCanceledOnTouchOutside(false);
            }
        } else if (i == NotificationCenter.didSetNewTheme) {
            if (!args[0].booleanValue()) {
                RecyclerListView recyclerListView2 = this.sideMenu;
                if (recyclerListView2 != null) {
                    recyclerListView2.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
                    this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
                    this.sideMenu.setListSelectorColor(Theme.getColor(Theme.key_listSelector));
                    this.sideMenu.getAdapter().notifyDataSetChanged();
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    try {
                        setTaskDescription(new ActivityManager.TaskDescription((String) null, (Bitmap) null, Theme.getColor(Theme.key_actionBarDefault) | -16777216));
                    } catch (Exception e3) {
                    }
                }
            }
            this.drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        } else if (i == NotificationCenter.needSetDayNightTheme) {
            Theme.ThemeInfo theme = args[0];
            boolean nigthTheme = args[1].booleanValue();
            this.actionBarLayout.animateThemedValues(theme, nigthTheme);
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.animateThemedValues(theme, nigthTheme);
                this.rightActionBarLayout.animateThemedValues(theme, nigthTheme);
            }
        } else if (i == NotificationCenter.notificationsCountUpdated) {
            RecyclerListView recyclerListView3 = this.sideMenu;
            if (recyclerListView3 != null) {
                Integer accountNum = args[0];
                int count = recyclerListView3.getChildCount();
                int a = 0;
                while (a < count) {
                    View child2 = this.sideMenu.getChildAt(a);
                    if (!(child2 instanceof DrawerUserCell) || ((DrawerUserCell) child2).getAccountNumber() != accountNum.intValue()) {
                        a++;
                    } else {
                        child2.invalidate();
                        return;
                    }
                }
            }
        } else if (i == NotificationCenter.needShowPlayServicesAlert) {
            try {
                args[0].startResolutionForResult(this, PLAY_SERVICES_REQUEST_CHECK_SETTINGS);
            } catch (Throwable th) {
            }
        } else if (i == NotificationCenter.fileDidLoad) {
            String str = this.loadingThemeFileName;
            if (str == null) {
                String str2 = this.loadingThemeWallpaperName;
                if (str2 != null && str2.equals(args[0])) {
                    this.loadingThemeWallpaperName = null;
                    Utilities.globalQueue.postRunnable(new Runnable(args[1]) {
                        private final /* synthetic */ File f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            LaunchActivity.this.lambda$didReceivedNotification$54$LaunchActivity(this.f$1);
                        }
                    });
                }
            } else if (str.equals(args[0])) {
                this.loadingThemeFileName = null;
                File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                File locFile = new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme");
                Theme.ThemeInfo themeInfo = Theme.fillThemeValues(locFile, this.loadingTheme.title, this.loadingTheme);
                if (themeInfo != null) {
                    if (themeInfo.pathToWallpaper == null || new File(themeInfo.pathToWallpaper).exists()) {
                        Theme.ThemeInfo finalThemeInfo = Theme.applyThemeFile(locFile, this.loadingTheme.title, this.loadingTheme, true);
                        if (finalThemeInfo != null) {
                            lambda$runLinkRequest$28$LaunchActivity(new ThemePreviewActivity(finalThemeInfo, true, 0, false));
                        }
                    } else {
                        TLRPC.TL_account_getWallPaper req = new TLRPC.TL_account_getWallPaper();
                        TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                        inputWallPaperSlug.slug = themeInfo.slug;
                        req.wallpaper = inputWallPaperSlug;
                        ConnectionsManager.getInstance(themeInfo.account).sendRequest(req, new RequestDelegate(themeInfo) {
                            private final /* synthetic */ Theme.ThemeInfo f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LaunchActivity.this.lambda$didReceivedNotification$52$LaunchActivity(this.f$1, tLObject, tL_error);
                            }
                        });
                        return;
                    }
                }
                onThemeLoadFinish();
            }
        } else if (i == NotificationCenter.fileDidFailToLoad) {
            String path = args[0];
            if (path.equals(this.loadingThemeFileName) || path.equals(this.loadingThemeWallpaperName)) {
                onThemeLoadFinish();
            }
        } else if (i == NotificationCenter.receivedAVideoCallRequest) {
            TLRPCCall.TL_UpdateMeetCallRequested requested = args[0];
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Launch call ===> receive video call , type = " + ApplicationLoader.mbytAVideoCallBusy);
            }
            if (requested == null) {
                return;
            }
            if (!isExistMainActivity(VisualCallReceiveActivity.class)) {
                Intent intent = new Intent(this, VisualCallReceiveActivity.class);
                intent.putExtra(MimeTypes.BASE_TYPE_VIDEO, false);
                intent.putExtra(TtmlNode.ATTR_ID, requested.id);
                intent.putExtra("admin_id", requested.admin_id);
                intent.putExtra("app_id", requested.appid);
                intent.putExtra("token", requested.token);
                intent.putStringArrayListExtra("gslb", requested.gslb);
                intent.putExtra("json", requested.data.data);
                startActivity(intent);
                ApplicationLoader.mbytAVideoCallBusy = 1;
                return;
            }
            AVideoCallInterface.IsBusyingNow(requested.id);
        } else if (i == NotificationCenter.folderWebView) {
            createGamePlayingFloatingView();
        }
    }

    static /* synthetic */ void lambda$didReceivedNotification$44(int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty()) {
            MessagesController instance = MessagesController.getInstance(account);
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            instance.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$45$LaunchActivity(DialogInterface dialog, int which) {
        MessagesController.getInstance(this.currentAccount).performLogout(2);
    }

    public /* synthetic */ void lambda$didReceivedNotification$47$LaunchActivity(HashMap waitingForLocation, int account, DialogInterface dialogInterface, int i) {
        if (!mainFragmentsStack.isEmpty()) {
            if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
                NewLocationActivity activity = new NewLocationActivity(0);
                activity.setDelegate(new NewLocationActivity.LocationActivityDelegate(waitingForLocation, account) {
                    private final /* synthetic */ HashMap f$0;
                    private final /* synthetic */ int f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                        LaunchActivity.lambda$null$46(this.f$0, this.f$1, messageMedia, i, z, i2);
                    }
                });
                lambda$runLinkRequest$28$LaunchActivity(activity);
                return;
            }
            requestPermissions(new String[]{im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_FINE_LOCATION"}, 2);
        }
    }

    static /* synthetic */ void lambda$null$46(HashMap waitingForLocation, int account, TLRPC.MessageMedia location, int locationType, boolean notify, int scheduleDate) {
        for (Map.Entry<String, MessageObject> entry : waitingForLocation.entrySet()) {
            MessageObject messageObject = entry.getValue();
            SendMessagesHelper.getInstance(account).sendMessage(location, messageObject.getDialogId(), messageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$52$LaunchActivity(Theme.ThemeInfo themeInfo, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response, themeInfo) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ Theme.ThemeInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$51$LaunchActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$51$LaunchActivity(TLObject response, Theme.ThemeInfo themeInfo) {
        if (response instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) response;
            this.loadingThemeInfo = themeInfo;
            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(wallPaper.document);
            FileLoader.getInstance(themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
            return;
        }
        onThemeLoadFinish();
    }

    public /* synthetic */ void lambda$didReceivedNotification$54$LaunchActivity(File file) {
        try {
            Bitmap bitmap = ThemesHorizontalListCell.getScaledBitmap((float) AndroidUtilities.dp(640.0f), (float) AndroidUtilities.dp(360.0f), file.getAbsolutePath(), (String) null, 0);
            if (this.loadingThemeInfo.isBlured) {
                bitmap = Utilities.blurWallpaper(bitmap);
            }
            FileOutputStream stream = new FileOutputStream(this.loadingThemeInfo.pathToWallpaper);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
            stream.close();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.lambda$null$53$LaunchActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$53$LaunchActivity() {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        Theme.ThemeInfo finalThemeInfo = Theme.applyThemeFile(new File(filesDirFixed, "remote" + this.loadingTheme.id + ".attheme"), this.loadingTheme.title, this.loadingTheme, true);
        if (finalThemeInfo != null) {
            lambda$runLinkRequest$28$LaunchActivity(new ThemePreviewActivity(finalThemeInfo, true, 0, false));
        }
        onThemeLoadFinish();
    }

    private boolean isExistMainActivity(Class<?> cls) {
        return ApplicationLoader.mbytAVideoCallBusy == 1;
    }

    /* access modifiers changed from: private */
    public String getStringForLanguageAlert(HashMap<String, String> map, String key, int intKey) {
        String value = map.get(key);
        if (value == null) {
            return LocaleController.getString(key, intKey);
        }
        return value;
    }

    private void onThemeLoadFinish() {
        AlertDialog alertDialog = this.loadingThemeProgressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } finally {
                this.loadingThemeProgressDialog = null;
            }
        }
        this.loadingThemeWallpaperName = null;
        this.loadingThemeInfo = null;
        this.loadingThemeFileName = null;
        this.loadingTheme = null;
    }

    private void checkFreeDiscSpace() {
        SharedConfig.checkKeepMedia();
        if (Build.VERSION.SDK_INT < 26) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    LaunchActivity.this.lambda$checkFreeDiscSpace$56$LaunchActivity();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$56$LaunchActivity() {
        File path;
        long freeSpace;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (Math.abs(preferences.getLong("last_space_check", 0) - System.currentTimeMillis()) >= 259200000 && (path = FileLoader.getDirectory(4)) != null) {
                    StatFs statFs = new StatFs(path.getAbsolutePath());
                    if (Build.VERSION.SDK_INT < 18) {
                        freeSpace = (long) Math.abs(statFs.getAvailableBlocks() * statFs.getBlockSize());
                    } else {
                        freeSpace = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
                    }
                    if (freeSpace < 104857600) {
                        preferences.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                LaunchActivity.this.lambda$null$55$LaunchActivity();
                            }
                        });
                    }
                }
            } catch (Throwable th) {
            }
        }
    }

    public /* synthetic */ void lambda$null$55$LaunchActivity() {
        try {
            AlertsCreator.createFreeSpaceDialog(this).show();
        } catch (Throwable th) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0054 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0056 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x005c A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005f A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0064 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0066 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006d A[Catch:{ Exception -> 0x0120 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showLanguageAlertInternal(im.bclpbkiauv.messenger.LocaleController.LocaleInfo r17, im.bclpbkiauv.messenger.LocaleController.LocaleInfo r18, java.lang.String r19) {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r0 = "ChooseYourLanguageOther"
            java.lang.String r2 = "ChooseYourLanguage"
            r3 = 0
            r1.loadingLocaleDialog = r3     // Catch:{ Exception -> 0x0122 }
            r4 = r17
            boolean r5 = r4.builtIn     // Catch:{ Exception -> 0x0120 }
            r6 = 1
            if (r5 != 0) goto L_0x001d
            im.bclpbkiauv.messenger.LocaleController r5 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0120 }
            boolean r5 = r5.isCurrentLocalLocale()     // Catch:{ Exception -> 0x0120 }
            if (r5 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r5 = 0
            goto L_0x001e
        L_0x001d:
            r5 = 1
        L_0x001e:
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r7 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder     // Catch:{ Exception -> 0x0120 }
            r7.<init>((android.content.Context) r1)     // Catch:{ Exception -> 0x0120 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0120 }
            r9 = 2131690603(0x7f0f046b, float:1.9010254E38)
            java.lang.String r8 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x0120 }
            r7.setTitle(r8)     // Catch:{ Exception -> 0x0120 }
            java.util.HashMap<java.lang.String, java.lang.String> r8 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x0120 }
            java.lang.String r2 = r1.getStringForLanguageAlert(r8, r2, r9)     // Catch:{ Exception -> 0x0120 }
            r7.setSubtitle(r2)     // Catch:{ Exception -> 0x0120 }
            android.widget.LinearLayout r2 = new android.widget.LinearLayout     // Catch:{ Exception -> 0x0120 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0120 }
            r2.setOrientation(r6)     // Catch:{ Exception -> 0x0120 }
            r8 = 2
            im.bclpbkiauv.ui.cells.LanguageCell[] r9 = new im.bclpbkiauv.ui.cells.LanguageCell[r8]     // Catch:{ Exception -> 0x0120 }
            im.bclpbkiauv.messenger.LocaleController$LocaleInfo[] r10 = new im.bclpbkiauv.messenger.LocaleController.LocaleInfo[r6]     // Catch:{ Exception -> 0x0120 }
            im.bclpbkiauv.messenger.LocaleController$LocaleInfo[] r11 = new im.bclpbkiauv.messenger.LocaleController.LocaleInfo[r8]     // Catch:{ Exception -> 0x0120 }
            java.util.HashMap<java.lang.String, java.lang.String> r12 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x0120 }
            java.lang.String r13 = "English"
            r14 = 2131691117(0x7f0f066d, float:1.9011297E38)
            java.lang.String r12 = r1.getStringForLanguageAlert(r12, r13, r14)     // Catch:{ Exception -> 0x0120 }
            if (r5 == 0) goto L_0x0056
            r13 = r4
            goto L_0x0058
        L_0x0056:
            r13 = r18
        L_0x0058:
            r11[r3] = r13     // Catch:{ Exception -> 0x0120 }
            if (r5 == 0) goto L_0x005f
            r13 = r18
            goto L_0x0060
        L_0x005f:
            r13 = r4
        L_0x0060:
            r11[r6] = r13     // Catch:{ Exception -> 0x0120 }
            if (r5 == 0) goto L_0x0066
            r13 = r4
            goto L_0x0068
        L_0x0066:
            r13 = r18
        L_0x0068:
            r10[r3] = r13     // Catch:{ Exception -> 0x0120 }
            r13 = 0
        L_0x006b:
            if (r13 >= r8) goto L_0x00c2
            im.bclpbkiauv.ui.cells.LanguageCell r3 = new im.bclpbkiauv.ui.cells.LanguageCell     // Catch:{ Exception -> 0x0120 }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x0120 }
            r9[r13] = r3     // Catch:{ Exception -> 0x0120 }
            r3 = r9[r13]     // Catch:{ Exception -> 0x0120 }
            r14 = r11[r13]     // Catch:{ Exception -> 0x0120 }
            r15 = r11[r13]     // Catch:{ Exception -> 0x0120 }
            r8 = r18
            if (r15 != r8) goto L_0x0080
            r15 = r12
            goto L_0x0081
        L_0x0080:
            r15 = 0
        L_0x0081:
            r3.setLanguage(r14, r15, r6)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x011e }
            r3.setTag(r14)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            java.lang.String r14 = "dialogButtonSelector"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)     // Catch:{ Exception -> 0x011e }
            r15 = 2
            android.graphics.drawable.Drawable r14 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r14, r15)     // Catch:{ Exception -> 0x011e }
            r3.setBackgroundDrawable(r14)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            if (r13 != 0) goto L_0x00a3
            r14 = 1
            goto L_0x00a4
        L_0x00a3:
            r14 = 0
        L_0x00a4:
            r3.setLanguageSelected(r14)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            r14 = 50
            r15 = -1
            android.widget.LinearLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r15, (int) r14)     // Catch:{ Exception -> 0x011e }
            r2.addView(r3, r14)     // Catch:{ Exception -> 0x011e }
            r3 = r9[r13]     // Catch:{ Exception -> 0x011e }
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$nz6hVGsRf1ybUDlujYXFiz-djLM r14 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$nz6hVGsRf1ybUDlujYXFiz-djLM     // Catch:{ Exception -> 0x011e }
            r14.<init>(r10, r9)     // Catch:{ Exception -> 0x011e }
            r3.setOnClickListener(r14)     // Catch:{ Exception -> 0x011e }
            int r13 = r13 + 1
            r3 = 0
            r8 = 2
            goto L_0x006b
        L_0x00c2:
            r8 = r18
            im.bclpbkiauv.ui.cells.LanguageCell r3 = new im.bclpbkiauv.ui.cells.LanguageCell     // Catch:{ Exception -> 0x011e }
            r3.<init>(r1, r6)     // Catch:{ Exception -> 0x011e }
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r1.systemLocaleStrings     // Catch:{ Exception -> 0x011e }
            r13 = 2131690604(0x7f0f046c, float:1.9010256E38)
            java.lang.String r6 = r1.getStringForLanguageAlert(r6, r0, r13)     // Catch:{ Exception -> 0x011e }
            java.util.HashMap<java.lang.String, java.lang.String> r14 = r1.englishLocaleStrings     // Catch:{ Exception -> 0x011e }
            java.lang.String r0 = r1.getStringForLanguageAlert(r14, r0, r13)     // Catch:{ Exception -> 0x011e }
            r3.setValue(r6, r0)     // Catch:{ Exception -> 0x011e }
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$hkAHdwoAbbwz2gLZyIypyqc63Hw r0 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$hkAHdwoAbbwz2gLZyIypyqc63Hw     // Catch:{ Exception -> 0x011e }
            r0.<init>()     // Catch:{ Exception -> 0x011e }
            r3.setOnClickListener(r0)     // Catch:{ Exception -> 0x011e }
            r0 = 50
            r6 = -1
            android.widget.LinearLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r6, (int) r0)     // Catch:{ Exception -> 0x011e }
            r2.addView(r3, r0)     // Catch:{ Exception -> 0x011e }
            r7.setView(r2)     // Catch:{ Exception -> 0x011e }
            java.lang.String r0 = "OK"
            r6 = 2131692462(0x7f0f0bae, float:1.9014025E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r6)     // Catch:{ Exception -> 0x011e }
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$FrEImwHZA40JT56uLrrDmaQU_MM r6 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$FrEImwHZA40JT56uLrrDmaQU_MM     // Catch:{ Exception -> 0x011e }
            r6.<init>(r10)     // Catch:{ Exception -> 0x011e }
            r7.setNegativeButton(r0, r6)     // Catch:{ Exception -> 0x011e }
            im.bclpbkiauv.ui.actionbar.AlertDialog r0 = r1.showAlertDialog((im.bclpbkiauv.ui.actionbar.AlertDialog.Builder) r7)     // Catch:{ Exception -> 0x011e }
            r1.localeDialog = r0     // Catch:{ Exception -> 0x011e }
            android.content.SharedPreferences r0 = im.bclpbkiauv.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x011e }
            android.content.SharedPreferences$Editor r6 = r0.edit()     // Catch:{ Exception -> 0x011e }
            java.lang.String r13 = "language_showed2"
            r14 = r19
            android.content.SharedPreferences$Editor r6 = r6.putString(r13, r14)     // Catch:{ Exception -> 0x011c }
            r6.commit()     // Catch:{ Exception -> 0x011c }
            goto L_0x012c
        L_0x011c:
            r0 = move-exception
            goto L_0x0129
        L_0x011e:
            r0 = move-exception
            goto L_0x0127
        L_0x0120:
            r0 = move-exception
            goto L_0x0125
        L_0x0122:
            r0 = move-exception
            r4 = r17
        L_0x0125:
            r8 = r18
        L_0x0127:
            r14 = r19
        L_0x0129:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x012c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LaunchActivity.showLanguageAlertInternal(im.bclpbkiauv.messenger.LocaleController$LocaleInfo, im.bclpbkiauv.messenger.LocaleController$LocaleInfo, java.lang.String):void");
    }

    static /* synthetic */ void lambda$showLanguageAlertInternal$57(LocaleController.LocaleInfo[] selectedLanguage, LanguageCell[] cells, View v) {
        Integer tag = (Integer) v.getTag();
        selectedLanguage[0] = ((LanguageCell) v).getCurrentLocale();
        int a1 = 0;
        while (a1 < cells.length) {
            cells[a1].setLanguageSelected(a1 == tag.intValue());
            a1++;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$58$LaunchActivity(View v) {
        this.localeDialog = null;
        this.drawerLayoutContainer.closeDrawer(true);
        lambda$runLinkRequest$28$LaunchActivity(new LanguageSelectActivity());
        AlertDialog alertDialog = this.visibleDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.visibleDialog = null;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$59$LaunchActivity(LocaleController.LocaleInfo[] selectedLanguage, DialogInterface dialog, int which) {
        LocaleController.getInstance().applyLanguage(selectedLanguage[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d3 A[Catch:{ Exception -> 0x0184 }, LOOP:0: B:29:0x0080->B:48:0x00d3, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00d2 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showLanguageAlert(boolean r18) {
        /*
            r17 = this;
            r1 = r17
            java.lang.String r0 = "ChangeLanguageLater"
            java.lang.String r2 = "ChooseYourLanguageOther"
            java.lang.String r3 = "ChooseYourLanguage"
            java.lang.String r4 = "English"
            java.lang.String r5 = "-"
            boolean r6 = r1.loadingLocaleDialog     // Catch:{ Exception -> 0x0184 }
            if (r6 != 0) goto L_0x0183
            boolean r6 = im.bclpbkiauv.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0184 }
            if (r6 == 0) goto L_0x0016
            goto L_0x0183
        L_0x0016:
            android.content.SharedPreferences r6 = im.bclpbkiauv.messenger.MessagesController.getGlobalMainSettings()     // Catch:{ Exception -> 0x0184 }
            java.lang.String r7 = "language_showed2"
            java.lang.String r8 = ""
            java.lang.String r7 = r6.getString(r7, r8)     // Catch:{ Exception -> 0x0184 }
            int r8 = r1.currentAccount     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.messenger.MessagesController r8 = im.bclpbkiauv.messenger.MessagesController.getInstance(r8)     // Catch:{ Exception -> 0x0184 }
            java.lang.String r8 = r8.suggestedLangCode     // Catch:{ Exception -> 0x0184 }
            if (r18 != 0) goto L_0x004b
            boolean r9 = r7.equals(r8)     // Catch:{ Exception -> 0x0184 }
            if (r9 == 0) goto L_0x004b
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0184 }
            if (r0 == 0) goto L_0x004a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
            r0.<init>()     // Catch:{ Exception -> 0x0184 }
            java.lang.String r2 = "alert already showed for "
            r0.append(r2)     // Catch:{ Exception -> 0x0184 }
            r0.append(r7)     // Catch:{ Exception -> 0x0184 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x0184 }
        L_0x004a:
            return
        L_0x004b:
            r9 = 2
            im.bclpbkiauv.messenger.LocaleController$LocaleInfo[] r9 = new im.bclpbkiauv.messenger.LocaleController.LocaleInfo[r9]     // Catch:{ Exception -> 0x0184 }
            boolean r10 = r8.contains(r5)     // Catch:{ Exception -> 0x0184 }
            r11 = 0
            if (r10 == 0) goto L_0x005c
            java.lang.String[] r10 = r8.split(r5)     // Catch:{ Exception -> 0x0184 }
            r10 = r10[r11]     // Catch:{ Exception -> 0x0184 }
            goto L_0x005d
        L_0x005c:
            r10 = r8
        L_0x005d:
            java.lang.String r12 = "in"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0184 }
            if (r12 == 0) goto L_0x0068
            java.lang.String r12 = "id"
            goto L_0x007f
        L_0x0068:
            java.lang.String r12 = "iw"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0184 }
            if (r12 == 0) goto L_0x0073
            java.lang.String r12 = "he"
            goto L_0x007f
        L_0x0073:
            java.lang.String r12 = "jw"
            boolean r12 = r12.equals(r10)     // Catch:{ Exception -> 0x0184 }
            if (r12 == 0) goto L_0x007e
            java.lang.String r12 = "jv"
            goto L_0x007f
        L_0x007e:
            r12 = 0
        L_0x007f:
            r13 = 0
        L_0x0080:
            im.bclpbkiauv.messenger.LocaleController r14 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<im.bclpbkiauv.messenger.LocaleController$LocaleInfo> r14 = r14.languages     // Catch:{ Exception -> 0x0184 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0184 }
            if (r13 >= r14) goto L_0x00d7
            im.bclpbkiauv.messenger.LocaleController r14 = im.bclpbkiauv.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<im.bclpbkiauv.messenger.LocaleController$LocaleInfo> r14 = r14.languages     // Catch:{ Exception -> 0x0184 }
            java.lang.Object r14 = r14.get(r13)     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.messenger.LocaleController$LocaleInfo r14 = (im.bclpbkiauv.messenger.LocaleController.LocaleInfo) r14     // Catch:{ Exception -> 0x0184 }
            java.lang.String r15 = r14.shortName     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = "en"
            boolean r11 = r15.equals(r11)     // Catch:{ Exception -> 0x0184 }
            if (r11 == 0) goto L_0x00a5
            r11 = 0
            r9[r11] = r14     // Catch:{ Exception -> 0x0184 }
        L_0x00a5:
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0184 }
            java.lang.String r15 = "_"
            java.lang.String r11 = r11.replace(r15, r5)     // Catch:{ Exception -> 0x0184 }
            boolean r11 = r11.equals(r8)     // Catch:{ Exception -> 0x0184 }
            if (r11 != 0) goto L_0x00c6
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0184 }
            boolean r11 = r11.equals(r10)     // Catch:{ Exception -> 0x0184 }
            if (r11 != 0) goto L_0x00c6
            java.lang.String r11 = r14.shortName     // Catch:{ Exception -> 0x0184 }
            boolean r11 = r11.equals(r12)     // Catch:{ Exception -> 0x0184 }
            if (r11 == 0) goto L_0x00c4
            goto L_0x00c6
        L_0x00c4:
            r11 = 1
            goto L_0x00c9
        L_0x00c6:
            r11 = 1
            r9[r11] = r14     // Catch:{ Exception -> 0x0184 }
        L_0x00c9:
            r15 = 0
            r16 = r9[r15]     // Catch:{ Exception -> 0x0184 }
            if (r16 == 0) goto L_0x00d3
            r15 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            if (r15 == 0) goto L_0x00d3
            goto L_0x00d7
        L_0x00d3:
            int r13 = r13 + 1
            r11 = 0
            goto L_0x0080
        L_0x00d7:
            r5 = 0
            r11 = r9[r5]     // Catch:{ Exception -> 0x0184 }
            if (r11 == 0) goto L_0x0182
            r11 = 1
            r13 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            if (r13 == 0) goto L_0x0182
            r13 = r9[r5]     // Catch:{ Exception -> 0x0184 }
            r5 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            if (r13 != r5) goto L_0x00e9
            goto L_0x0182
        L_0x00e9:
            boolean r5 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0184 }
            if (r5 == 0) goto L_0x0117
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
            r5.<init>()     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = "show lang alert for "
            r5.append(r11)     // Catch:{ Exception -> 0x0184 }
            r11 = 0
            r13 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = r13.getKey()     // Catch:{ Exception -> 0x0184 }
            r5.append(r11)     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = " and "
            r5.append(r11)     // Catch:{ Exception -> 0x0184 }
            r11 = 1
            r13 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = r13.getKey()     // Catch:{ Exception -> 0x0184 }
            r5.append(r11)     // Catch:{ Exception -> 0x0184 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x0184 }
        L_0x0117:
            r5 = 0
            r1.systemLocaleStrings = r5     // Catch:{ Exception -> 0x0184 }
            r1.englishLocaleStrings = r5     // Catch:{ Exception -> 0x0184 }
            r5 = 1
            r1.loadingLocaleDialog = r5     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.tgnet.TLRPC$TL_langpack_getStrings r11 = new im.bclpbkiauv.tgnet.TLRPC$TL_langpack_getStrings     // Catch:{ Exception -> 0x0184 }
            r11.<init>()     // Catch:{ Exception -> 0x0184 }
            r5 = r9[r5]     // Catch:{ Exception -> 0x0184 }
            java.lang.String r5 = r5.getLangCode()     // Catch:{ Exception -> 0x0184 }
            r11.lang_code = r5     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0184 }
            r5.add(r4)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0184 }
            r5.add(r3)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0184 }
            r5.add(r2)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r5 = r11.keys     // Catch:{ Exception -> 0x0184 }
            r5.add(r0)     // Catch:{ Exception -> 0x0184 }
            int r5 = r1.currentAccount     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.tgnet.ConnectionsManager r5 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r5)     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$rLY4K0HzRvP9OrgjHdrx7EhgJQ0 r13 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$rLY4K0HzRvP9OrgjHdrx7EhgJQ0     // Catch:{ Exception -> 0x0184 }
            r13.<init>(r9, r8)     // Catch:{ Exception -> 0x0184 }
            r14 = 8
            r5.sendRequest(r11, r13, r14)     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.tgnet.TLRPC$TL_langpack_getStrings r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_langpack_getStrings     // Catch:{ Exception -> 0x0184 }
            r5.<init>()     // Catch:{ Exception -> 0x0184 }
            r11 = 0
            r11 = r9[r11]     // Catch:{ Exception -> 0x0184 }
            java.lang.String r11 = r11.getLangCode()     // Catch:{ Exception -> 0x0184 }
            r5.lang_code = r11     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r11 = r5.keys     // Catch:{ Exception -> 0x0184 }
            r11.add(r4)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r4 = r5.keys     // Catch:{ Exception -> 0x0184 }
            r4.add(r3)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r3 = r5.keys     // Catch:{ Exception -> 0x0184 }
            r3.add(r2)     // Catch:{ Exception -> 0x0184 }
            java.util.ArrayList<java.lang.String> r2 = r5.keys     // Catch:{ Exception -> 0x0184 }
            r2.add(r0)     // Catch:{ Exception -> 0x0184 }
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r0)     // Catch:{ Exception -> 0x0184 }
            im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$p7J1BpUIxkIZLIJeCU8WzyEzoPQ r2 = new im.bclpbkiauv.ui.-$$Lambda$LaunchActivity$p7J1BpUIxkIZLIJeCU8WzyEzoPQ     // Catch:{ Exception -> 0x0184 }
            r2.<init>(r9, r8)     // Catch:{ Exception -> 0x0184 }
            r0.sendRequest(r5, r2, r14)     // Catch:{ Exception -> 0x0184 }
            goto L_0x0188
        L_0x0182:
            return
        L_0x0183:
            return
        L_0x0184:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0188:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LaunchActivity.showLanguageAlert(boolean):void");
    }

    public /* synthetic */ void lambda$showLanguageAlert$61$LaunchActivity(LocaleController.LocaleInfo[] infos, String systemLang, TLObject response, TLRPC.TL_error error) {
        HashMap<String, String> keys = new HashMap<>();
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(keys, infos, systemLang) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ LocaleController.LocaleInfo[] f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$60$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$60$LaunchActivity(HashMap keys, LocaleController.LocaleInfo[] infos, String systemLang) {
        this.systemLocaleStrings = keys;
        if (this.englishLocaleStrings != null && keys != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$63$LaunchActivity(LocaleController.LocaleInfo[] infos, String systemLang, TLObject response, TLRPC.TL_error error) {
        HashMap<String, String> keys = new HashMap<>();
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(a);
                keys.put(string.key, string.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(keys, infos, systemLang) {
            private final /* synthetic */ HashMap f$1;
            private final /* synthetic */ LocaleController.LocaleInfo[] f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LaunchActivity.this.lambda$null$62$LaunchActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$62$LaunchActivity(HashMap keys, LocaleController.LocaleInfo[] infos, String systemLang) {
        this.englishLocaleStrings = keys;
        if (keys != null && this.systemLocaleStrings != null) {
            showLanguageAlertInternal(infos[1], infos[0], systemLang);
        }
    }

    private void onPasscodePause() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            this.lockRunnable = new Runnable() {
                public void run() {
                    if (LaunchActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            LaunchActivity.this.showPasscodeActivity();
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("didn't pass lock check");
                        }
                        Runnable unused = LaunchActivity.this.lockRunnable = null;
                    }
                }
            };
            if (SharedConfig.appLocked) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, 1000);
            } else if (SharedConfig.autoLockIn != 0) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, (((long) SharedConfig.autoLockIn) * 1000) + 1000);
            }
        } else {
            SharedConfig.lastPauseTime = 0;
        }
        SharedConfig.saveConfig();
    }

    private void onPasscodeResume() {
        Runnable runnable = this.lockRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity();
        }
        if (SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            SharedConfig.saveConfig();
        }
    }

    private void updateCurrentConnectionState(int account) {
        if (this.actionBarLayout != null) {
            String title = null;
            int titleId = 0;
            Runnable action = null;
            int connectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            this.currentConnectionState = connectionState;
            if (connectionState == 2) {
                title = "WaitingForNetwork";
                titleId = R.string.WaitingForNetwork;
            } else if (connectionState == 5) {
                title = "Updating";
                titleId = R.string.Updating;
            } else if (connectionState == 4) {
                title = "ConnectingToProxy";
                titleId = R.string.ConnectingToProxy;
            } else if (connectionState == 1) {
                title = "Connecting";
                titleId = R.string.Connecting;
            }
            int i = this.currentConnectionState;
            if (i == 1 || i == 4) {
                action = new Runnable() {
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.lang.Object} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: im.bclpbkiauv.ui.actionbar.BaseFragment} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: java.lang.Object} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: im.bclpbkiauv.ui.actionbar.BaseFragment} */
                    /* JADX WARNING: Multi-variable type inference failed */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                            r3 = this;
                            r0 = 0
                            boolean r1 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
                            if (r1 == 0) goto L_0x0027
                            java.util.ArrayList r1 = im.bclpbkiauv.ui.LaunchActivity.layerFragmentsStack
                            boolean r1 = r1.isEmpty()
                            if (r1 != 0) goto L_0x0046
                            java.util.ArrayList r1 = im.bclpbkiauv.ui.LaunchActivity.layerFragmentsStack
                            java.util.ArrayList r2 = im.bclpbkiauv.ui.LaunchActivity.layerFragmentsStack
                            int r2 = r2.size()
                            int r2 = r2 + -1
                            java.lang.Object r1 = r1.get(r2)
                            r0 = r1
                            im.bclpbkiauv.ui.actionbar.BaseFragment r0 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r0
                            goto L_0x0046
                        L_0x0027:
                            java.util.ArrayList r1 = im.bclpbkiauv.ui.LaunchActivity.mainFragmentsStack
                            boolean r1 = r1.isEmpty()
                            if (r1 != 0) goto L_0x0046
                            java.util.ArrayList r1 = im.bclpbkiauv.ui.LaunchActivity.mainFragmentsStack
                            java.util.ArrayList r2 = im.bclpbkiauv.ui.LaunchActivity.mainFragmentsStack
                            int r2 = r2.size()
                            int r2 = r2 + -1
                            java.lang.Object r1 = r1.get(r2)
                            r0 = r1
                            im.bclpbkiauv.ui.actionbar.BaseFragment r0 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r0
                        L_0x0046:
                            boolean r1 = r0 instanceof im.bclpbkiauv.ui.ProxyListActivity
                            if (r1 != 0) goto L_0x0050
                            boolean r1 = r0 instanceof im.bclpbkiauv.ui.ProxySettingsActivity
                            if (r1 == 0) goto L_0x004f
                            goto L_0x0050
                        L_0x004f:
                            return
                        L_0x0050:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.LaunchActivity.AnonymousClass7.run():void");
                    }
                };
            }
            this.actionBarLayout.setTitleOverlayText(title, titleId, action);
        }
    }

    public void hideVisibleActionMode() {
        ActionMode actionMode = this.visibleActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            BaseFragment lastFragment = null;
            if (AndroidUtilities.isTablet()) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
                } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
            } else if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                lastFragment = this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            }
            if (lastFragment != null) {
                Bundle args = lastFragment.getArguments();
                if ((lastFragment instanceof ChatActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat");
                } else if (lastFragment instanceof SettingsActivity) {
                    outState.putString("fragment", "settings");
                } else if ((lastFragment instanceof GroupCreateFinalActivity) && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "group");
                } else if (lastFragment instanceof WallpapersListActivity) {
                    outState.putString("fragment", "wallpapers");
                } else if ((lastFragment instanceof ProfileActivity) && ((ProfileActivity) lastFragment).isChat() && args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "chat_profile");
                } else if ((lastFragment instanceof ChannelCreateActivity) && args != null && args.getInt("step") == 0) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "channel");
                }
                lastFragment.saveSelfArgs(outState);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            finish();
        } else if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(true, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (!AndroidUtilities.isTablet()) {
            this.actionBarLayout.onBackPressed();
        } else if (this.layersActionBarLayout.getVisibility() == 0) {
            this.layersActionBarLayout.onBackPressed();
        } else {
            boolean cancel = false;
            if (this.rightActionBarLayout.getVisibility() == 0 && !this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                cancel = true ^ this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1).onBackPressed();
            }
            if (!cancel) {
                this.actionBarLayout.onBackPressed();
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onLowMemory();
            this.layersActionBarLayout.onLowMemory();
        }
    }

    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        this.visibleActionMode = mode;
        try {
            Menu menu = mode.getMenu();
            if (menu != null && !this.actionBarLayout.extendActionMode(menu) && AndroidUtilities.isTablet() && !this.rightActionBarLayout.extendActionMode(menu)) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (Build.VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeStarted(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeStarted(mode);
                this.layersActionBarLayout.onActionModeStarted(mode);
            }
        }
    }

    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        if (this.visibleActionMode == mode) {
            this.visibleActionMode = null;
        }
        if (Build.VERSION.SDK_INT < 23 || mode.getType() != 1) {
            this.actionBarLayout.onActionModeFinished(mode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeFinished(mode);
                this.layersActionBarLayout.onActionModeFinished(mode);
            }
        }
    }

    public boolean onPreIme() {
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(true, false);
            return true;
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
            return true;
        } else if (!ArticleViewer.hasInstance() || !ArticleViewer.getInstance().isVisible()) {
            return false;
        } else {
            ArticleViewer.getInstance().close(true, false);
            return true;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (!mainFragmentsStack.isEmpty() && ((!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) && event.getRepeatCount() == 0 && event.getAction() == 0 && (event.getKeyCode() == 24 || event.getKeyCode() == 25))) {
            ArrayList<BaseFragment> arrayList = mainFragmentsStack;
            BaseFragment fragment = arrayList.get(arrayList.size() - 1);
            if ((fragment instanceof ChatActivity) && ((ChatActivity) fragment).maybePlayVisibleVideo()) {
                return true;
            }
            if (AndroidUtilities.isTablet() && !rightFragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList2 = rightFragmentsStack;
                BaseFragment fragment2 = arrayList2.get(arrayList2.size() - 1);
                if ((fragment2 instanceof ChatActivity) && ((ChatActivity) fragment2).maybePlayVisibleVideo()) {
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 82 && !SharedConfig.isWaitingForPasscodeEnter) {
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                return super.onKeyUp(keyCode, event);
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                return super.onKeyUp(keyCode, event);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getVisibility() == 0 && !this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    this.layersActionBarLayout.onKeyUp(keyCode, event);
                } else if (this.rightActionBarLayout.getVisibility() != 0 || this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    this.actionBarLayout.onKeyUp(keyCode, event);
                } else {
                    this.rightActionBarLayout.onKeyUp(keyCode, event);
                }
            } else if (this.actionBarLayout.fragmentsStack.size() != 1) {
                this.actionBarLayout.onKeyUp(keyCode, event);
            } else if (!this.drawerLayoutContainer.isDrawerOpened()) {
                if (getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.drawerLayoutContainer.openDrawer(false);
            } else {
                this.drawerLayoutContainer.closeDrawer(false);
            }
        }
        if (keyCode != 4 || event.getAction() != 1 || ApplicationLoader.mbytLiving != 1 || this.actionBarLayout.fragmentsStack.size() != 1 || !(this.actionBarLayout.fragmentsStack.get(0) instanceof IndexActivity)) {
            return super.onKeyUp(keyCode, event);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage("您正在直播中，确定要退出吗？");
        builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LaunchActivity.this.lambda$onKeyUp$64$LaunchActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showAlertDialog(builder);
        return true;
    }

    public /* synthetic */ void lambda$onKeyUp$64$LaunchActivity(DialogInterface dialog, int which) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.livefinishnotify, new Object[0]);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LaunchActivity.this.finish();
            }
        }, 1000);
    }

    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout) {
        ActionBarLayout actionBarLayout2;
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
            if (fragment instanceof IndexActivity) {
                IndexActivity indexActivity = (IndexActivity) fragment;
                ActionBarLayout actionBarLayout5 = this.actionBarLayout;
                if (layout != actionBarLayout5) {
                    actionBarLayout5.removeAllFragments();
                    this.actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false, false);
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.setVisibility(8);
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    if (!this.tabletFullSize) {
                        this.shadowTabletSide.setVisibility(0);
                        if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                            this.backgroundTablet.setVisibility(0);
                        }
                    }
                    return false;
                }
            }
            if (!(fragment instanceof ChatActivity) || ((ChatActivity) fragment).isInScheduleMode()) {
                ActionBarLayout actionBarLayout6 = this.layersActionBarLayout;
                if (layout == actionBarLayout6) {
                    return true;
                }
                actionBarLayout6.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (fragment instanceof LoginContronllerActivity) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false, false);
                return false;
            } else if ((!this.tabletFullSize && layout == this.rightActionBarLayout) || (this.tabletFullSize && layout == this.actionBarLayout)) {
                boolean result = (this.tabletFullSize && layout == (actionBarLayout2 = this.actionBarLayout) && actionBarLayout2.fragmentsStack.size() == 1) ? false : true;
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    for (int a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                        ActionBarLayout actionBarLayout7 = this.layersActionBarLayout;
                        actionBarLayout7.removeFragmentFromStack(actionBarLayout7.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                }
                if (!result) {
                    this.actionBarLayout.presentFragment(fragment, false, forceWithoutAnimation, false, false);
                }
                return result;
            } else if (!this.tabletFullSize && layout != (actionBarLayout4 = this.rightActionBarLayout)) {
                actionBarLayout4.setVisibility(0);
                this.backgroundTablet.setVisibility(8);
                this.rightActionBarLayout.removeAllFragments();
                this.rightActionBarLayout.presentFragment(fragment, removeLast, true, false, false);
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    for (int a2 = 0; a2 < this.layersActionBarLayout.fragmentsStack.size() - 1; a2 = (a2 - 1) + 1) {
                        ActionBarLayout actionBarLayout8 = this.layersActionBarLayout;
                        actionBarLayout8.removeFragmentFromStack(actionBarLayout8.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                }
                return false;
            } else if (!this.tabletFullSize || layout == (actionBarLayout3 = this.actionBarLayout)) {
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    for (int a3 = 0; a3 < this.layersActionBarLayout.fragmentsStack.size() - 1; a3 = (a3 - 1) + 1) {
                        ActionBarLayout actionBarLayout9 = this.layersActionBarLayout;
                        actionBarLayout9.removeFragmentFromStack(actionBarLayout9.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                }
                ActionBarLayout actionBarLayout10 = this.actionBarLayout;
                actionBarLayout10.presentFragment(fragment, actionBarLayout10.fragmentsStack.size() > 1, forceWithoutAnimation, false, false);
                return false;
            } else {
                actionBarLayout3.presentFragment(fragment, actionBarLayout3.fragmentsStack.size() > 1, forceWithoutAnimation, false, false);
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    for (int a4 = 0; a4 < this.layersActionBarLayout.fragmentsStack.size() - 1; a4 = (a4 - 1) + 1) {
                        ActionBarLayout actionBarLayout11 = this.layersActionBarLayout;
                        actionBarLayout11.removeFragmentFromStack(actionBarLayout11.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(!forceWithoutAnimation);
                }
                return false;
            }
        } else {
            if (fragment instanceof LoginContronllerActivity) {
                if (mainFragmentsStack.size() == 0) {
                }
            } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            return true;
        }
    }

    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout) {
        ActionBarLayout actionBarLayout2;
        ActionBarLayout actionBarLayout3;
        if (AndroidUtilities.isTablet()) {
            this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
            if (fragment instanceof IndexActivity) {
                IndexActivity indexActivity = (IndexActivity) fragment;
                ActionBarLayout actionBarLayout4 = this.actionBarLayout;
                if (layout != actionBarLayout4) {
                    actionBarLayout4.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(fragment);
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.setVisibility(8);
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    if (!this.tabletFullSize) {
                        this.shadowTabletSide.setVisibility(0);
                        if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                            this.backgroundTablet.setVisibility(0);
                        }
                    }
                    return false;
                }
            } else if (!(fragment instanceof ChatActivity) || ((ChatActivity) fragment).isInScheduleMode()) {
                ActionBarLayout actionBarLayout5 = this.layersActionBarLayout;
                if (layout != actionBarLayout5) {
                    actionBarLayout5.setVisibility(0);
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                    if (fragment instanceof LoginContronllerActivity) {
                        this.backgroundTablet.setVisibility(0);
                        this.shadowTabletSide.setVisibility(8);
                        this.shadowTablet.setBackgroundColor(0);
                    } else {
                        this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                    }
                    this.layersActionBarLayout.addFragmentToStack(fragment);
                    return false;
                }
            } else if (!this.tabletFullSize && layout != (actionBarLayout3 = this.rightActionBarLayout)) {
                actionBarLayout3.setVisibility(0);
                this.backgroundTablet.setVisibility(8);
                this.rightActionBarLayout.removeAllFragments();
                this.rightActionBarLayout.addFragmentToStack(fragment);
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    for (int a = 0; a < this.layersActionBarLayout.fragmentsStack.size() - 1; a = (a - 1) + 1) {
                        ActionBarLayout actionBarLayout6 = this.layersActionBarLayout;
                        actionBarLayout6.removeFragmentFromStack(actionBarLayout6.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                }
                return false;
            } else if (this.tabletFullSize && layout != (actionBarLayout2 = this.actionBarLayout)) {
                actionBarLayout2.addFragmentToStack(fragment);
                if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    for (int a2 = 0; a2 < this.layersActionBarLayout.fragmentsStack.size() - 1; a2 = (a2 - 1) + 1) {
                        ActionBarLayout actionBarLayout7 = this.layersActionBarLayout;
                        actionBarLayout7.removeFragmentFromStack(actionBarLayout7.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                }
                return false;
            }
            return true;
        }
        if (fragment instanceof LoginContronllerActivity) {
            if (mainFragmentsStack.size() == 0) {
            }
        } else if ((fragment instanceof CountrySelectActivity) && mainFragmentsStack.size() == 1) {
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (AndroidUtilities.isTablet()) {
            if (layout == this.actionBarLayout && layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (layout == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (layout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (layout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        } else if (layout.fragmentsStack.size() >= 2 && !(layout.fragmentsStack.get(0) instanceof LoginContronllerActivity)) {
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        }
        return true;
    }

    public void rebuildAllFragments(boolean last) {
        ActionBarLayout actionBarLayout2 = this.layersActionBarLayout;
        if (actionBarLayout2 != null) {
            actionBarLayout2.rebuildAllFragmentViews(last, last);
        } else {
            this.actionBarLayout.rebuildAllFragmentViews(last, last);
        }
    }

    public void onRebuildAllFragments(ActionBarLayout layout, boolean last) {
        if (AndroidUtilities.isTablet() && layout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(last, last);
            this.actionBarLayout.rebuildAllFragmentViews(last, last);
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }

    private void createGamePlayingFloatingView() {
        if (this.drawerLayoutContainer != null) {
            DiscoveryJumpPausedFloatingView.getInstance().setContext(this).setRootViewContainer(this.drawerLayoutContainer).setActionBarLayout(this.actionBarLayout).show(true);
        }
    }
}

package im.bclpbkiauv.ui.hui.visualcall;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.app.ActivityCompat;
import com.alivc.rtc.AliRtcAuthInfo;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcEngineEventListener;
import com.alivc.rtc.AliRtcEngineImpl;
import com.alivc.rtc.AliRtcEngineNotify;
import com.alivc.rtc.AliRtcRemoteUserInfo;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.visualcall.FlowService;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo;
import im.bclpbkiauv.ui.hviews.DragFrameLayout;
import org.webrtc.sdk.SophonSurfaceView;

public abstract class BaseCallActivity extends Activity {
    protected int VisualCallType = 1;
    protected AliRtcEngine.AliVideoCanvas aliVideoCanvasBig;
    protected AliRtcEngine.AliVideoCanvas aliVideoCanvasSmall;
    protected int callStyle = 2;
    protected String currentUid;
    protected AliRtcEngine mAliRtcEngine;
    protected LinearLayout mBigWindow;
    protected String mChannel = "0001";
    protected Chronometer mChronometer;
    protected AliRtcEngineNotify mEngineNotify;
    protected AliRtcEngineEventListener mEventListener;
    protected PermissionUtils.PermissionGrant mGrant = new PermissionUtils.PermissionGrant() {
        public void onPermissionGranted(int requestCode) {
            BaseCallActivity.this.initRTCEngineAndStartPreview();
            BaseCallActivity.this.mGrantPermission = true;
            ToastUtils.show((CharSequence) LocaleController.getString("visual_call_granted_tip", R.string.visual_call_granted_tip));
        }

        public void onPermissionCancel() {
            AVideoCallInterface.DiscardAVideoCall(BaseCallActivity.this.getIntent().getStringExtra(TtmlNode.ATTR_ID), 0, BaseCallActivity.this.callStyle == 2);
            ToastUtils.show((CharSequence) LocaleController.getString("grant_permission", R.string.grant_permission));
            RingUtils.stopSoundPoolRing();
            BaseCallActivity.this.cancelCallingState();
            BaseCallActivity.this.finish();
        }
    };
    protected boolean mGrantPermission;
    protected boolean mIsAudioCapture = true;
    protected boolean mIsAudioPlay = true;
    protected RTCAuthInfo mRtcAuthInfo = new RTCAuthInfo();
    protected DragFrameLayout mSmallWindow;
    protected ChartUserAdapter mUserListAdapter = new ChartUserAdapter();
    private String mUsername;
    ServiceConnection mVideoServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            BaseCallActivity.this.myservice = ((FlowService.MyBinder) service).getService();
            if (BaseCallActivity.this.mBigWindow != null) {
                BaseCallActivity.this.changePopWindow();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    };
    protected boolean mblnOtherIsPc = false;
    protected byte mbytLocalPos = 0;
    protected boolean misConnect = false;
    protected FlowService myservice = null;
    protected SophonSurfaceView surfaceView = null;

    /* access modifiers changed from: protected */
    public abstract void changeLocalPreview(SophonSurfaceView sophonSurfaceView);

    /* access modifiers changed from: protected */
    public abstract void changePopWindow();

    /* access modifiers changed from: protected */
    public abstract void changeStatusView();

    /* access modifiers changed from: protected */
    public abstract void initLocalView();

    /* access modifiers changed from: protected */
    public abstract void initView();

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mRtcAuthInfo.data = new RTCAuthInfo.RTCAuthInfo_Data();
        this.mUsername = AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id + "";
    }

    public void setUpSplash() {
        ThreadUtils.runOnUiThread(new Runnable() {
            public final void run() {
                BaseCallActivity.this.lambda$setUpSplash$0$BaseCallActivity();
            }
        }, 1000);
    }

    /* access modifiers changed from: protected */
    /* renamed from: requestPermission */
    public void lambda$setUpSplash$0$BaseCallActivity() {
        PermissionUtils.requestMultiPermissions(this, new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO", PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, this.mGrant);
    }

    /* access modifiers changed from: protected */
    public void showPermissionErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(message);
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                BaseCallActivity.this.lambda$showPermissionErrorAlert$1$BaseCallActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    public /* synthetic */ void lambda$showPermissionErrorAlert$1$BaseCallActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void initRTCEngineAndStartPreview() {
        if (checkPermission("android.permission.CAMERA") || checkPermission("android.permission.RECORD_AUDIO")) {
            setUpSplash();
            this.mGrantPermission = false;
            return;
        }
        this.mGrantPermission = true;
        if (this.mAliRtcEngine == null) {
            AliRtcEngineImpl instance = AliRtcEngine.getInstance(getApplicationContext());
            this.mAliRtcEngine = instance;
            instance.setRtcEngineEventListener(this.mEventListener);
            this.mAliRtcEngine.setRtcEngineNotify(this.mEngineNotify);
            if (this.callStyle == 2) {
                initLocalView();
                startPreview();
                return;
            }
            this.mAliRtcEngine.setAudioOnlyMode(true);
        }
    }

    private void startPreview() {
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            try {
                aliRtcEngine.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkPermission(String permission) {
        try {
            if (ActivityCompat.checkSelfPermission(this, permission) != 0) {
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCallingState() {
        if (ApplicationLoader.mbytAVideoCallBusy != 0) {
            ApplicationLoader.mbytAVideoCallBusy = 0;
            NotificationManager service = (NotificationManager) getSystemService("notification");
            if (service != null) {
                try {
                    if (Build.VERSION.SDK_INT >= 26) {
                        service.deleteNotificationChannel("to-do-it");
                    } else {
                        service.cancel(1114214);
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            ReleaseRtcCall();
        }
    }

    /* access modifiers changed from: protected */
    public void openJoinChannelBeforeNeedParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------cuizi ");
        sb.append(this.mAliRtcEngine == null);
        KLog.d(sb.toString());
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            if (this.mIsAudioCapture) {
                aliRtcEngine.startAudioCapture();
            } else {
                aliRtcEngine.stopAudioCapture();
            }
            if (this.mIsAudioPlay) {
                this.mAliRtcEngine.startAudioPlayer();
            } else {
                this.mAliRtcEngine.stopAudioPlayer();
            }
            this.mAliRtcEngine.enableEarBack(false);
            if (!this.mAliRtcEngine.isSpeakerOn() && this.callStyle == 2) {
                this.mAliRtcEngine.enableSpeakerphone(true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void joinChannel() {
        if (this.mAliRtcEngine != null) {
            AliRtcAuthInfo userInfo = new AliRtcAuthInfo();
            userInfo.setAppid(this.mRtcAuthInfo.data.appid);
            userInfo.setNonce(this.mRtcAuthInfo.data.nonce);
            userInfo.setTimestamp(this.mRtcAuthInfo.data.timestamp);
            userInfo.setUserId(this.mRtcAuthInfo.data.userid);
            userInfo.setGslb(this.mRtcAuthInfo.data.gslb);
            userInfo.setToken(this.mRtcAuthInfo.data.token);
            userInfo.setConferenceId(this.mChannel);
            this.mAliRtcEngine.setAutoPublish(true, true);
            this.mAliRtcEngine.joinChannel(userInfo, this.mUsername);
            Log.d("--------", "=======");
        }
    }

    /* access modifiers changed from: protected */
    public void processOccurError(int error) {
        if (error == 16908812 || error == 33620229) {
            noSessionExit(error);
        }
    }

    /* access modifiers changed from: protected */
    public void noSessionExit(int error) {
        runOnUiThread(new Runnable() {
            public final void run() {
                BaseCallActivity.this.lambda$noSessionExit$2$BaseCallActivity();
            }
        });
    }

    public /* synthetic */ void lambda$noSessionExit$2$BaseCallActivity() {
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            aliRtcEngine.leaveChannel();
            this.mAliRtcEngine.destroy();
            this.mAliRtcEngine = null;
        }
        AliRtcEngineImpl instance = AliRtcEngine.getInstance(getApplicationContext());
        this.mAliRtcEngine = instance;
        if (instance != null) {
            instance.setRtcEngineEventListener(this.mEventListener);
            this.mAliRtcEngine.setRtcEngineNotify(this.mEngineNotify);
            if (this.callStyle == 2) {
                startPreview();
            } else {
                this.mAliRtcEngine.setAudioOnlyMode(true);
            }
            openJoinChannelBeforeNeedParams();
            joinChannel();
        }
    }

    public void startVideoService() {
        try {
            moveTaskToBack(true);
            this.misConnect = bindService(new Intent(this, FlowService.class), this.mVideoServiceConnection, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void updateRemoteDisplay(String uid, AliRtcEngine.AliRtcAudioTrack at, AliRtcEngine.AliRtcVideoTrack vt) {
        runOnUiThread(new Runnable(uid, vt) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ AliRtcEngine.AliRtcVideoTrack f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                BaseCallActivity.this.lambda$updateRemoteDisplay$3$BaseCallActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$updateRemoteDisplay$3$BaseCallActivity(String uid, AliRtcEngine.AliRtcVideoTrack vt) {
        AliRtcEngine.AliVideoCanvas screenCanvas;
        AliRtcEngine.AliVideoCanvas cameraCanvas;
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            AliRtcRemoteUserInfo remoteUserInfo = aliRtcEngine.getUserInfo(uid);
            if (remoteUserInfo == null) {
                Log.e("视频", "updateRemoteDisplay remoteUserInfo = null, uid = " + uid);
                return;
            }
            this.mAliRtcEngine.configRemoteAudio(uid, true);
            this.mAliRtcEngine.muteRemoteAudioPlaying(uid, false);
            AliRtcEngine.AliVideoCanvas cameraCanvas2 = remoteUserInfo.getCameraCanvas();
            AliRtcEngine.AliVideoCanvas screenCanvas2 = remoteUserInfo.getScreenCanvas();
            if (vt == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo) {
                cameraCanvas = null;
                screenCanvas = null;
            } else if (vt == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera) {
                screenCanvas = null;
                cameraCanvas = createCanvasIfNull(cameraCanvas2);
                this.mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
            } else if (vt == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen) {
                cameraCanvas = null;
                screenCanvas = createCanvasIfNull(screenCanvas2);
                this.mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen);
            } else if (vt == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth) {
                cameraCanvas = createCanvasIfNull(cameraCanvas2);
                this.mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
                screenCanvas = createCanvasIfNull(screenCanvas2);
                this.mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen);
            } else {
                return;
            }
            ChartUserBean chartUserBean = convertRemoteUserInfo(remoteUserInfo, cameraCanvas, screenCanvas);
            if (chartUserBean.mCameraSurface != null) {
                KLog.d("---------mScreenSurface");
                ViewParent parent = chartUserBean.mCameraSurface.getParent();
                if (parent != null && (parent instanceof FrameLayout)) {
                    ((FrameLayout) parent).removeAllViews();
                }
                if (this.callStyle == 2) {
                    changeLocalPreview(chartUserBean.mCameraSurface);
                }
            }
        }
    }

    private void createLocalVideoView(ViewGroup v) {
        v.removeAllViews();
        if (this.surfaceView == null) {
            this.surfaceView = new SophonSurfaceView(this);
        }
        this.surfaceView.setZOrderOnTop(true);
        this.surfaceView.setZOrderMediaOverlay(true);
        AliRtcEngine.AliVideoCanvas aliVideoCanvas = new AliRtcEngine.AliVideoCanvas();
        v.addView(this.surfaceView, new ViewGroup.LayoutParams(-1, -1));
        aliVideoCanvas.view = this.surfaceView;
        aliVideoCanvas.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            aliRtcEngine.setLocalViewConfig(aliVideoCanvas, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
            this.mAliRtcEngine.startPreview();
        }
        v.getChildAt(0).setVisibility(0);
    }

    private ChartUserBean convertRemoteUserInfo(AliRtcRemoteUserInfo remoteUserInfo, AliRtcEngine.AliVideoCanvas cameraCanvas, AliRtcEngine.AliVideoCanvas screenCanvas) {
        ChartUserBean ret = this.mUserListAdapter.createDataIfNull(remoteUserInfo.getUserID());
        ret.mUserId = remoteUserInfo.getUserID();
        ret.mUserName = remoteUserInfo.getDisplayName();
        SophonSurfaceView sophonSurfaceView = null;
        ret.mCameraSurface = cameraCanvas != null ? cameraCanvas.view : null;
        boolean z = true;
        ret.mIsCameraFlip = cameraCanvas != null && cameraCanvas.mirrorMode == AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllEnabled;
        if (screenCanvas != null) {
            sophonSurfaceView = screenCanvas.view;
        }
        ret.mScreenSurface = sophonSurfaceView;
        if (screenCanvas == null || screenCanvas.mirrorMode != AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllEnabled) {
            z = false;
        }
        ret.mIsScreenFlip = z;
        return ret;
    }

    private AliRtcEngine.AliVideoCanvas createCanvasIfNull(AliRtcEngine.AliVideoCanvas canvas) {
        if (canvas == null || canvas.view == null) {
            canvas = new AliRtcEngine.AliVideoCanvas();
            SophonSurfaceView surfaceView2 = new SophonSurfaceView(this);
            surfaceView2.setZOrderOnTop(false);
            surfaceView2.setZOrderMediaOverlay(false);
            canvas.view = surfaceView2;
            canvas.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
        }
        if (this.mblnOtherIsPc) {
            canvas.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeClip;
        }
        return canvas;
    }

    /* access modifiers changed from: protected */
    public void addRemoteUser(String uid) {
        AliRtcRemoteUserInfo remoteUserInfo;
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null && (remoteUserInfo = aliRtcEngine.getUserInfo(uid)) != null) {
            ChartUserBean data = convertRemoteUserToUserData(remoteUserInfo);
            KLog.d("---------addRemoteUser-" + data.mCameraSurface + "   " + data.mScreenSurface);
            if (data.mCameraSurface != null) {
                KLog.d("---------addRemoteUser");
                ViewParent parent = data.mCameraSurface.getParent();
                if (parent != null && (parent instanceof FrameLayout)) {
                    ((FrameLayout) parent).removeAllViews();
                }
                if (this.callStyle == 2) {
                    changeLocalPreview(convertRemoteUserToUserData(remoteUserInfo).mCameraSurface);
                }
            }
        }
    }

    private ChartUserBean convertRemoteUserToUserData(AliRtcRemoteUserInfo remoteUserInfo) {
        String uid = remoteUserInfo.getUserID();
        ChartUserBean ret = this.mUserListAdapter.createDataIfNull(uid);
        ret.mUserId = uid;
        ret.mUserName = remoteUserInfo.getDisplayName();
        ret.mIsCameraFlip = false;
        ret.mIsScreenFlip = false;
        return ret;
    }

    /* access modifiers changed from: protected */
    public void setFullScreen() {
        requestWindowFeature(1);
        setTheme(R.style.Theme_TMessages);
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                setTaskDescription(new ActivityManager.TaskDescription((String) null, (Bitmap) null, Theme.getColor(Theme.key_actionBarDefault) | -16777216));
            } catch (Exception e) {
            }
            try {
                getWindow().setNavigationBarColor(-16777216);
            } catch (Exception e2) {
            }
        }
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(1280);
            window.setStatusBarColor(0);
        }
    }

    /* access modifiers changed from: protected */
    public void ReleaseRtcCall() {
        if (this.mAliRtcEngine != null) {
            new Thread(new Runnable() {
                public final void run() {
                    BaseCallActivity.this.lambda$ReleaseRtcCall$4$BaseCallActivity();
                }
            }).start();
            ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(true, false);
        }
    }

    public /* synthetic */ void lambda$ReleaseRtcCall$4$BaseCallActivity() {
        this.mAliRtcEngine.setRtcEngineNotify((AliRtcEngineNotify) null);
        this.mAliRtcEngine.setRtcEngineEventListener((AliRtcEngineEventListener) null);
        if (this.callStyle == 2) {
            this.mAliRtcEngine.stopPreview();
        }
        this.mAliRtcEngine.leaveChannel();
        this.mAliRtcEngine.destroy();
        this.mAliRtcEngine = null;
    }
}

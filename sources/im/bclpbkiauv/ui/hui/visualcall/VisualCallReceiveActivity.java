package im.bclpbkiauv.ui.hui.visualcall;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcEngineEventListener;
import com.alivc.rtc.AliRtcEngineNotify;
import com.blankj.utilcode.constant.TimeConstants;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.util.MimeTypes;
import com.king.zxing.util.LogUtils;
import com.socks.library.KLog;
import ezy.assist.compat.SettingsCompat;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCCall;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.hui.visualcall.AVideoCallInterface;
import im.bclpbkiauv.ui.hui.visualcall.CallNetWorkReceiver;
import im.bclpbkiauv.ui.hui.visualcall.VisualCallReceiveActivity;
import im.bclpbkiauv.ui.hviews.DragFrameLayout;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import im.bclpbkiauv.ui.hviews.helper.MryDeviceHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.alirtcInterface.ALI_RTC_INTERFACE;
import org.webrtc.alirtcInterface.AliParticipantInfo;
import org.webrtc.alirtcInterface.AliStatusInfo;
import org.webrtc.alirtcInterface.AliSubscriberInfo;
import org.webrtc.sdk.SophonSurfaceView;

public class VisualCallReceiveActivity extends BaseCallActivity implements NotificationCenter.NotificationCenterDelegate {
    private byte REQUEST_NETWORK_NO_ANSWER = -6;
    private byte REQUEST_NO_ANSWER = -4;
    private byte RESPONSE_REFUSE = -1;
    private CallNetWorkReceiver callNetWorkReceiver;
    @BindView(2131296449)
    RecyclerView chartContentUserlist;
    @BindView(2131296458)
    DragFrameLayout chartVideoContainer;
    @BindView(2131296463)
    Chronometer chrVisualcallTime;
    private DynamicPoint dynamicPoint;
    @BindView(2131296682)
    ImageView imgOperateA;
    @BindView(2131296683)
    ImageView imgOperateB;
    @BindView(2131296684)
    ImageView imgOperateC;
    @BindView(2131296686)
    ImageView imgPreReceive;
    @BindView(2131296692)
    BackupImageView imgUserHead;
    @BindView(2131296693)
    BackupImageView imgVideoUserHead;
    @BindView(2131296694)
    ImageView imgVisualcall;
    @BindView(2131296833)
    ImageView ivPreRefuse;
    @BindView(2131296868)
    LinearLayout linOperateA;
    @BindView(2131296869)
    LinearLayout linOperateB;
    @BindView(2131296870)
    LinearLayout linOperateC;
    @BindView(2131296871)
    LinearLayout linPreReceive;
    @BindView(2131296872)
    LinearLayout linPreRefuse;
    @BindView(2131296929)
    LinearLayout llBigRemoteView;
    @BindView(2131296930)
    LinearLayout llBigWindow;
    @BindView(2131296956)
    LinearLayout llSmallRemoteView;
    TLRPC.User mUser;
    private boolean mblnResetNoOp = false;
    private boolean mblnUnProcessChooseVoiceTip = false;
    /* access modifiers changed from: private */
    public byte mbytExit = 0;
    /* access modifiers changed from: private */
    public byte mbytFPacketRecCount = 0;
    private byte mbytIsForeground = 1;
    private byte mbytLastClickIndex = -1;
    private byte mbytNoOp = 0;
    private long mlLastClickTime = 0;
    /* access modifiers changed from: private */
    public long mlStart = 0;
    /* access modifiers changed from: private */
    public long mlTipShow;
    @BindView(2131297092)
    RelativeLayout relVideoUser;
    @BindView(2131297093)
    LinearLayout relVisualCallA;
    @BindView(2131297094)
    RelativeLayout relVisualCallB;
    @BindView(2131297095)
    RelativeLayout relVoiceUser;
    @BindView(2131297229)
    RelativeLayout rootView;
    @BindView(2131297284)
    SophonSurfaceView sfLocalView;
    @BindView(2131297285)
    SophonSurfaceView sfSmallView;
    private SoundPool soundPool;
    private int spConnectingId;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    @BindView(2131297884)
    TextView txtCallName;
    @BindView(2131297885)
    ColorTextView txtCallStatus;
    @BindView(2131297897)
    TextView txtMask;
    @BindView(2131297901)
    ColorTextView txtOperateA;
    @BindView(2131297902)
    ColorTextView txtOperateB;
    @BindView(2131297903)
    ColorTextView txtOperateC;
    @BindView(2131297907)
    ColorTextView txtPreChangeToVoice;
    @BindView(2131297911)
    TextView txtTip;
    @BindView(2131297915)
    TextView txtVideoName;
    @BindView(2131297916)
    ColorTextView txtVideoStatus;
    @BindView(2131297917)
    ColorTextView txtVisualcallStatus;

    static /* synthetic */ byte access$508(VisualCallReceiveActivity x0) {
        byte b = x0.mbytFPacketRecCount;
        x0.mbytFPacketRecCount = (byte) (b + 1);
        return b;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLoader.mbytAVideoCallBusy = 1;
        setFullScreen();
        setContentView(R.layout.activity_visual_call_receive);
        if (getIntent().getIntExtra("from", 0) == 0) {
            RingUtils.playRingBySoundPool(this);
        }
        RingUtils.stopPlayVibrator();
        ButterKnife.bind((Activity) this);
        getWindow().addFlags(128);
        fillAliRtcUserInfo();
        regNotification();
        initEventListener();
        this.dynamicPoint = new DynamicPoint();
        this.chartVideoContainer.setY((float) AndroidUtilities.statusBarHeight);
        initRTCEngineAndStartPreview();
        sendKeepLivePacket(this.mChannel);
        this.txtTip.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Color.parseColor("#CB2D2D2D")));
        this.mUser = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(getIntent().getIntExtra("admin_id", 0)));
        this.callStyle = getIntent().getBooleanExtra(MimeTypes.BASE_TYPE_VIDEO, false) ? 2 : 1;
        setAVideoUI();
        this.mSmallWindow = this.chartVideoContainer;
        this.mBigWindow = this.llBigWindow;
        this.mChronometer = this.chrVisualcallTime;
        this.chrVisualcallTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer chronometer) {
                StringBuilder sb;
                String ss;
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int) (time / 3600000);
                int m = ((int) (time - ((long) (h * TimeConstants.HOUR)))) / TimeConstants.MIN;
                int s = ((int) ((time - ((long) (TimeConstants.HOUR * h))) - ((long) (TimeConstants.MIN * m)))) / 1000;
                if (h > 0) {
                    m += h * 60;
                }
                if (m < 10) {
                    sb.append("0");
                    sb.append(m);
                } else {
                    sb = new StringBuilder();
                    sb.append(m);
                    sb.append("");
                }
                String mm = sb.toString();
                if (s < 10) {
                    ss = "0" + s;
                } else {
                    ss = s + "";
                }
                chronometer.setText(mm + LogUtils.COLON + ss);
            }
        });
        regNetWorkReceiver();
        this.chartVideoContainer.setVisibility(8);
        if (this.callStyle == 1) {
            this.txtPreChangeToVoice.setVisibility(8);
        }
        this.linOperateA.postDelayed(new Runnable() {
            public final void run() {
                VisualCallReceiveActivity.this.lambda$onCreate$0$VisualCallReceiveActivity();
            }
        }, 35000);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(false, false);
    }

    public /* synthetic */ void lambda$onCreate$0$VisualCallReceiveActivity() {
        if (this.mbytNoOp == 0 && !this.mblnResetNoOp) {
            ProcessDiscardMessage(1, (String) null);
        }
    }

    /* access modifiers changed from: protected */
    public void regNetWorkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        CallNetWorkReceiver callNetWorkReceiver2 = new CallNetWorkReceiver();
        this.callNetWorkReceiver = callNetWorkReceiver2;
        registerReceiver(callNetWorkReceiver2, filter);
        this.callNetWorkReceiver.setCallBack(new CallNetWorkReceiver.NetWorkStateCallBack() {
            public void onNetWorkConnected() {
                VisualCallReceiveActivity visualCallReceiveActivity = VisualCallReceiveActivity.this;
                visualCallReceiveActivity.sendKeepLivePacket(visualCallReceiveActivity.mChannel);
            }

            public void onNetWorkDisconnected() {
            }
        });
    }

    private void setBlurBitmap() {
        this.txtMask.setAlpha(0.8f);
        this.txtMask.setBackground(new BitmapDrawable((Resources) null, Utilities.blurWallpaper(BitmapFactory.decodeResource(getResources(), R.drawable.visualcall_bg))));
    }

    private void initRing() {
        SoundPool soundPool2 = new SoundPool(1, 0, 0);
        this.soundPool = soundPool2;
        this.spConnectingId = soundPool2.load(this, R.raw.visual_call_receive, 1);
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
                VisualCallReceiveActivity.this.lambda$initRing$1$VisualCallReceiveActivity(soundPool, i, i2);
            }
        });
    }

    public /* synthetic */ void lambda$initRing$1$VisualCallReceiveActivity(SoundPool soundPool2, int sampleId, int status) {
        soundPool2.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    public void stopRinging() {
        RingUtils.stopSoundPoolRing();
    }

    private void setAVideoUI() {
        String strName = "";
        TLRPC.User user = this.mUser;
        if (user != null) {
            strName = user.first_name;
        }
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setInfo(this.mUser);
        if (this.callStyle == 2) {
            this.imgPreReceive.setBackgroundResource(R.drawable.visualcall_video_receive);
            this.relVoiceUser.setVisibility(8);
            this.relVideoUser.setVisibility(0);
            this.sfLocalView.setVisibility(0);
            this.txtVideoName.setText(strName);
            setBlurBitmap();
            this.txtMask.setVisibility(0);
            this.imgVideoUserHead.setRoundRadius(AndroidUtilities.dp(70.0f));
            this.imgVideoUserHead.setImage(ImageLocation.getForUser(this.mUser, false), "50_50", (Drawable) avatarDrawable, (Object) this.mUser);
            this.dynamicPoint.animForWaitting(LocaleController.getString("visual_call_invate", R.string.visual_call_invate), this.txtVideoStatus);
            if (Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.checkCamera(this)) {
                this.txtTip.setVisibility(0);
                setTipPos();
                this.txtTip.setText(LocaleController.getString("visual_call_change_to_voice", R.string.visual_call_change_to_voice));
                return;
            }
            return;
        }
        this.imgPreReceive.setBackgroundResource(R.drawable.visualcall_receive_common);
        this.relVoiceUser.setVisibility(0);
        this.relVideoUser.setVisibility(8);
        this.sfLocalView.setVisibility(8);
        this.txtCallName.setText(strName);
        this.imgUserHead.setRoundRadius(AndroidUtilities.dp(70.0f));
        this.imgUserHead.setImage(ImageLocation.getForUser(this.mUser, false), "50_50", (Drawable) avatarDrawable, (Object) this.mUser);
        if (this.VisualCallType == 3) {
            return;
        }
        if (this.mbytNoOp == 0) {
            this.dynamicPoint.animForWaitting(LocaleController.getString("visual_call_invate_voice", R.string.visual_call_invate_voice), this.txtCallStatus);
            return;
        }
        this.dynamicPoint.animForWaitting(LocaleController.getString(R.string.visual_call_calling), this.txtCallStatus);
        this.imgPreReceive.setBackgroundResource(R.drawable.visualcall_receive);
    }

    /* access modifiers changed from: private */
    public void sendKeepLivePacket(final String strId) {
        if (this.timerTask == null) {
            AnonymousClass3 r2 = new TimerTask() {
                public void run() {
                    ThreadUtils.runOnUiThread(new Runnable(strId) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            VisualCallReceiveActivity.AnonymousClass3.this.lambda$run$0$VisualCallReceiveActivity$3(this.f$1);
                        }
                    });
                }

                public /* synthetic */ void lambda$run$0$VisualCallReceiveActivity$3(String strId) {
                    AVideoCallInterface.sendJumpPacket(strId, new AVideoCallInterface.AVideoRequestCallBack() {
                        public void onError(TLRPC.TL_error error) {
                        }

                        public void onSuccess(TLObject object) {
                            if (object instanceof TLRPCCall.TL_MeetModel) {
                                TLRPCCall.TL_MeetModel model = (TLRPCCall.TL_MeetModel) object;
                                if (model.id.equals(VisualCallReceiveActivity.this.mChannel) && !model.video && VisualCallReceiveActivity.this.callStyle == 2) {
                                    VisualCallReceiveActivity.this.callStyle = 1;
                                    VisualCallReceiveActivity.this.changeToVoice(false);
                                }
                            }
                        }
                    });
                }
            };
            this.timerTask = r2;
            this.timer.schedule(r2, 1000, 14000);
        }
    }

    private void fillAliRtcUserInfo() {
        this.mRtcAuthInfo.data.appid = getIntent().getStringExtra("app_id");
        this.mRtcAuthInfo.data.token = getIntent().getStringExtra("token");
        this.mRtcAuthInfo.data.userid = String.valueOf(AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id);
        ArrayList<String> arrgslb = getIntent().getStringArrayListExtra("gslb");
        if (arrgslb != null) {
            String[] strArr = new String[arrgslb.size()];
            int i = 0;
            Iterator<String> it = arrgslb.iterator();
            while (it.hasNext()) {
                strArr[i] = it.next();
                i++;
            }
            this.mRtcAuthInfo.data.gslb = strArr;
        }
        String strJson = getIntent().getStringExtra("json");
        if (strJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(strJson);
                this.mRtcAuthInfo.data.timestamp = jsonObject.getLong("time_stamp");
                this.mRtcAuthInfo.data.setNonce(jsonObject.getString("nonce"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.mChannel = getIntent().getStringExtra(TtmlNode.ATTR_ID);
    }

    @OnClick({2131296682, 2131296683, 2131296684, 2131296686, 2131296833, 2131296694, 2131296458, 2131296930, 2131297907})
    public void onViewClicked(View view) {
        boolean z = true;
        switch (view.getId()) {
            case R.id.chart_video_container /*2131296458*/:
                if (!this.chartVideoContainer.isDrag()) {
                    changeLocalPreview((SophonSurfaceView) null);
                    return;
                }
                return;
            case R.id.img_operate_a /*2131296682*/:
                if (this.imgOperateA.isEnabled() && this.mAliRtcEngine != null) {
                    if (this.callStyle == 2) {
                        this.callStyle = 1;
                        AVideoCallInterface.ChangeToVoiceCall(this.mChannel, this.callStyle == 2);
                        if (this.mAliRtcEngine.isLocalCameraPublishEnabled()) {
                            KLog.d("--------关闭视频流");
                            this.mAliRtcEngine.configLocalCameraPublish(false);
                            this.mAliRtcEngine.publish();
                        }
                        changeToVoice(true);
                    } else if (this.mbytLastClickIndex != 0 || System.currentTimeMillis() - this.mlLastClickTime > 500) {
                        this.mlLastClickTime = System.currentTimeMillis();
                        this.mIsAudioCapture = !this.mIsAudioCapture;
                        if (this.mIsAudioCapture) {
                            this.mAliRtcEngine.muteLocalMic(false);
                            this.imgOperateA.setBackgroundResource(R.drawable.visualcall_no_voice);
                        } else {
                            this.imgOperateA.setBackgroundResource(R.drawable.visualcall_no_voice_selected);
                            this.mAliRtcEngine.muteLocalMic(true);
                        }
                    }
                }
                this.mbytLastClickIndex = 0;
                return;
            case R.id.img_operate_b /*2131296683*/:
                if (this.imgOperateB.isEnabled()) {
                    this.mChannel = "666";
                    cancelCallingState();
                    this.chrVisualcallTime.stop();
                    this.imgOperateB.setBackgroundResource(R.drawable.visualcall_cancel);
                    this.imgOperateB.setEnabled(false);
                    this.imgOperateA.setEnabled(false);
                    String stringExtra = getIntent().getStringExtra(TtmlNode.ATTR_ID);
                    int currentTimeMillis = ((int) (System.currentTimeMillis() - this.mlStart)) / 1000;
                    if (this.callStyle != 2) {
                        z = false;
                    }
                    AVideoCallInterface.DiscardAVideoCall(stringExtra, currentTimeMillis, z);
                    this.txtTip.setText(LocaleController.getString("visual_call_over", R.string.visual_call_over));
                    this.txtTip.setVisibility(0);
                    setTipPos();
                    this.txtTip.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallReceiveActivity.this.finish();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    return;
                }
                return;
            case R.id.img_operate_c /*2131296684*/:
                if (this.VisualCallType == 3 && this.mAliRtcEngine != null && (this.mbytLastClickIndex != 1 || System.currentTimeMillis() - this.mlLastClickTime > 500)) {
                    this.mlLastClickTime = System.currentTimeMillis();
                    if (this.callStyle == 2) {
                        if (this.mAliRtcEngine.switchCamera() == 0) {
                            KLog.d("----------设置成功");
                            if (this.mAliRtcEngine.getCurrentCameraType().getCameraType() == AliRtcEngine.AliRTCCameraType.AliRTCCameraBack.getCameraType()) {
                                this.imgOperateC.setBackgroundResource(R.drawable.visualcall_camera_changed);
                            } else if (this.mAliRtcEngine.getCurrentCameraType().getCameraType() == AliRtcEngine.AliRTCCameraType.AliRTCCameraFront.getCameraType()) {
                                this.imgOperateC.setBackgroundResource(R.drawable.visualcall_camera);
                            }
                        }
                    } else if (this.mAliRtcEngine.isSpeakerOn()) {
                        this.imgOperateC.setBackgroundResource(R.drawable.visualcall_hands_free);
                        this.mAliRtcEngine.enableSpeakerphone(false);
                    } else {
                        this.imgOperateC.setBackgroundResource(R.drawable.visual_hands_free_selected);
                        this.mAliRtcEngine.enableSpeakerphone(true);
                    }
                }
                this.mbytLastClickIndex = 1;
                return;
            case R.id.img_pre_receive /*2131296686*/:
                this.mbytNoOp = 1;
                if (this.imgPreReceive.isEnabled() && this.mGrantPermission) {
                    this.imgPreReceive.setEnabled(false);
                    stopRinging();
                    if (this.callStyle == 2) {
                        this.dynamicPoint.animForWaitting(LocaleController.getString("visual_call_calling", R.string.visual_call_calling), this.txtVideoStatus);
                        this.imgPreReceive.setBackgroundResource(R.drawable.visualcall_video_receive_common);
                    } else {
                        this.dynamicPoint.animForWaitting(LocaleController.getString("visual_call_calling", R.string.visual_call_calling), this.txtCallStatus);
                        this.imgPreReceive.setBackgroundResource(R.drawable.visualcall_receive);
                    }
                    openJoinChannelBeforeNeedParams();
                    if (this.mGrantPermission) {
                        new Thread(new Runnable() {
                            public final void run() {
                                VisualCallReceiveActivity.this.joinChannel();
                            }
                        }).start();
                    } else {
                        setUpSplash();
                    }
                    AVideoCallInterface.AcceptAVideoCall(getIntent().getStringExtra(TtmlNode.ATTR_ID), new AVideoCallInterface.AVideoRequestCallBack() {
                        public void onError(TLRPC.TL_error error) {
                            if (error.text.equals("PEER_DISCARD")) {
                                VisualCallReceiveActivity.this.ProcessDiscardMessage(1, (String) null);
                            }
                        }

                        public void onSuccess(TLObject object) {
                        }
                    });
                    this.relVideoUser.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallReceiveActivity.this.lambda$onViewClicked$2$VisualCallReceiveActivity();
                        }
                    }, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
                    return;
                }
                return;
            case R.id.img_visualcall /*2131296694*/:
                if (!this.imgVisualcall.isEnabled()) {
                    return;
                }
                if (SettingsCompat.canDrawOverlays(this)) {
                    ApplicationLoader.mbytAVideoCallBusy = 3;
                    if (this.callStyle == 2) {
                        this.chartVideoContainer.setVisibility(8);
                    }
                    startVideoService();
                    return;
                } else if (MryDeviceHelper.isOppo()) {
                    showPermissionErrorAlert(LocaleController.getString("PermissionPopWindowOppo", R.string.PermissionPopWindowOppo));
                    return;
                } else {
                    showPermissionErrorAlert(LocaleController.getString("PermissionPopWindow", R.string.PermissionPopWindow));
                    return;
                }
            case R.id.iv_pre_refuse /*2131296833*/:
                this.mbytNoOp = 1;
                if (this.ivPreRefuse.isEnabled()) {
                    KLog.d("call id === " + getIntent().getStringExtra(TtmlNode.ATTR_ID));
                    this.mChannel = "666";
                    stopRinging();
                    cancelCallingState();
                    this.ivPreRefuse.setBackgroundResource(R.drawable.visualcall_cancel);
                    this.ivPreRefuse.setEnabled(false);
                    this.txtPreChangeToVoice.setEnabled(false);
                    String stringExtra2 = getIntent().getStringExtra(TtmlNode.ATTR_ID);
                    byte b = this.RESPONSE_REFUSE;
                    if (this.callStyle != 2) {
                        z = false;
                    }
                    AVideoCallInterface.DiscardAVideoCall(stringExtra2, b, z);
                    this.txtTip.setText(LocaleController.getString("visual_call_refused_over", R.string.visual_call_refused_over));
                    this.txtTip.setVisibility(0);
                    setTipPos();
                    this.txtTip.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallReceiveActivity.this.finish();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    return;
                }
                return;
            case R.id.ll_big_window /*2131296930*/:
                if (this.callStyle != 2 || this.VisualCallType != 3) {
                    return;
                }
                if (this.imgVisualcall.getVisibility() == 8) {
                    this.imgVisualcall.setVisibility(0);
                    this.linOperateA.setVisibility(0);
                    this.linOperateB.setVisibility(0);
                    this.linOperateC.setVisibility(0);
                    this.chrVisualcallTime.setVisibility(0);
                    return;
                }
                this.imgVisualcall.setVisibility(8);
                this.linOperateA.setVisibility(8);
                this.linOperateB.setVisibility(8);
                this.linOperateC.setVisibility(8);
                this.chrVisualcallTime.setVisibility(8);
                return;
            case R.id.txt_pre_change_to_voice /*2131297907*/:
                if (this.txtPreChangeToVoice.isEnabled() && this.mAliRtcEngine != null) {
                    this.callStyle = 1;
                    AVideoCallInterface.ChangeToVoiceCall(this.mChannel, this.callStyle == 2);
                    if (this.mAliRtcEngine.isLocalCameraPublishEnabled()) {
                        KLog.d("--------关闭视频流");
                        this.mAliRtcEngine.configLocalCameraPublish(false);
                        this.mAliRtcEngine.publish();
                    }
                    changeToVoice(true);
                    this.mblnResetNoOp = true;
                    this.linOperateA.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallReceiveActivity.this.lambda$onViewClicked$3$VisualCallReceiveActivity();
                        }
                    }, 35000);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public /* synthetic */ void lambda$onViewClicked$2$VisualCallReceiveActivity() {
        if (this.VisualCallType != 3) {
            AVideoCallInterface.DiscardAVideoCall(getIntent().getStringExtra(TtmlNode.ATTR_ID), this.REQUEST_NETWORK_NO_ANSWER, this.callStyle == 2);
            this.txtTip.setVisibility(0);
            this.txtTip.setText(LocaleController.getString("visual_call_retry", R.string.visual_call_retry));
            setTipPos();
            cancelCallingState();
            this.txtTip.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallReceiveActivity.this.finish();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    public /* synthetic */ void lambda$onViewClicked$3$VisualCallReceiveActivity() {
        if (this.mbytNoOp == 0) {
            ProcessDiscardMessage(1, (String) null);
        }
    }

    private void enterCallingMode() {
        this.relVisualCallB.setVisibility(8);
        this.relVisualCallA.setVisibility(0);
        if (this.callStyle == 2) {
            this.txtPreChangeToVoice.setVisibility(8);
            this.linOperateA.setVisibility(0);
            this.imgOperateA.setBackgroundResource(R.drawable.visualcall_to_voice);
            this.txtOperateA.setText(LocaleController.getString("Str_visualcall_to_voice", R.string.Str_visualcall_to_voice));
            this.imgOperateC.setBackgroundResource(R.drawable.visualcall_camera);
            this.txtOperateC.setText(LocaleController.getString("Str_visualcall_change_camera", R.string.Str_visualcall_change_camera));
        } else if (this.callStyle == 1) {
            this.txtCallStatus.setVisibility(8);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.receivedAVideoCallReady) {
            TLRPCCall.TL_UpdateMeetCall meetCall = args[0];
            if (meetCall != null && meetCall.id.equals(this.mChannel)) {
                this.mblnOtherIsPc = meetCall.isPc;
                return;
            }
            return;
        }
        boolean z = true;
        if (id == NotificationCenter.reecivedAVideoDiscarded) {
            TLRPCCall.TL_UpdateMeetCallDiscarded discarded = args[0];
            if (discarded != null && discarded.id.equals(this.mChannel)) {
                this.imgVisualcall.setEnabled(false);
                if (discarded.duration == -1) {
                    stopRinging();
                    cancelCallingState();
                    this.ivPreRefuse.setBackgroundResource(R.drawable.visualcall_cancel);
                    this.ivPreRefuse.setEnabled(false);
                    String stringExtra = getIntent().getStringExtra(TtmlNode.ATTR_ID);
                    byte b = this.RESPONSE_REFUSE;
                    if (this.callStyle != 2) {
                        z = false;
                    }
                    AVideoCallInterface.DiscardAVideoCall(stringExtra, b, z);
                    this.txtTip.setText(LocaleController.getString("visual_call_refused_over", R.string.visual_call_refused_over));
                    this.txtTip.setVisibility(0);
                    setTipPos();
                    this.txtTip.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallReceiveActivity.this.finish();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                } else if (discarded.duration != 0) {
                    ProcessDiscardMessage(0, (String) null);
                } else {
                    ProcessDiscardMessage(0, LocaleController.getString(R.string.visual_call_received_in_other));
                }
            }
        } else if (id == NotificationCenter.receivedAVideoCallChangeVoice) {
            this.callStyle = 1;
            changeToVoice(false);
            if (this.mbytNoOp == 0) {
                this.mblnResetNoOp = true;
                this.linOperateA.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.this.lambda$didReceivedNotification$4$VisualCallReceiveActivity();
                    }
                }, 35000);
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$4$VisualCallReceiveActivity() {
        if (this.mbytNoOp == 0) {
            ProcessDiscardMessage(1, (String) null);
        }
    }

    /* access modifiers changed from: protected */
    public void ProcessDiscardMessage(int iFlag, String strTip) {
        boolean z = true;
        if (iFlag == 1) {
            String stringExtra = getIntent().getStringExtra(TtmlNode.ATTR_ID);
            byte b = this.REQUEST_NO_ANSWER;
            if (this.callStyle != 2) {
                z = false;
            }
            AVideoCallInterface.DiscardAVideoCall(stringExtra, b, z);
        }
        stopRinging();
        if (this.VisualCallType == 3) {
            this.txtTip.setText(LocaleController.getString("visual_call_other_side_discard", R.string.visual_call_other_side_discard));
            this.txtTip.setVisibility(0);
            this.chrVisualcallTime.stop();
            this.txtTip.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallReceiveActivity.this.finish();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        } else {
            this.txtTip.setText(strTip == null ? LocaleController.getString(R.string.visual_call_other_side_cancel) : strTip);
            this.txtTip.setVisibility(0);
            setTipPos();
            this.txtTip.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallReceiveActivity.this.finish();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
        cancelCallingState();
    }

    /* access modifiers changed from: protected */
    public void removeRemoteUser(String uid) {
        KLog.d("---------远端用户下线通知" + uid);
        runOnUiThread(new Runnable(uid) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VisualCallReceiveActivity.this.lambda$removeRemoteUser$5$VisualCallReceiveActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeRemoteUser$5$VisualCallReceiveActivity(String uid) {
        this.mUserListAdapter.removeData(uid, true);
        if (!this.mChannel.equals("666")) {
            stopRinging();
            if (this.VisualCallType == 3) {
                this.txtTip.setText(LocaleController.getString("visual_call_other_side_discard", R.string.visual_call_other_side_discard));
                this.txtTip.setVisibility(0);
                this.chrVisualcallTime.stop();
                this.txtTip.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.this.finish();
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            } else {
                this.txtTip.setText(LocaleController.getString("visual_call_other_side_cancel", R.string.visual_call_other_side_cancel));
                this.txtTip.setVisibility(0);
                this.txtTip.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.this.finish();
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
            cancelCallingState();
        }
    }

    private void regNotification() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.receivedAVideoCallReady);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reecivedAVideoDiscarded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.receivedAVideoCallChangeVoice);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mbytNoOp = 1;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.receivedAVideoCallReady);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reecivedAVideoDiscarded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.receivedAVideoCallChangeVoice);
        CallNetWorkReceiver callNetWorkReceiver2 = this.callNetWorkReceiver;
        if (callNetWorkReceiver2 != null) {
            unregisterReceiver(callNetWorkReceiver2);
        }
        this.dynamicPoint.release();
        this.timer.cancel();
        this.timer.purge();
        TimerTask timerTask2 = this.timerTask;
        if (timerTask2 != null) {
            timerTask2.cancel();
        }
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void initLocalView() {
        this.sfLocalView.setZOrderOnTop(false);
        this.sfLocalView.setZOrderMediaOverlay(false);
        this.aliVideoCanvasBig = new AliRtcEngine.AliVideoCanvas();
        this.aliVideoCanvasBig.view = this.sfLocalView;
        this.aliVideoCanvasBig.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
        this.sfSmallView.setZOrderOnTop(true);
        this.sfSmallView.setZOrderMediaOverlay(true);
        this.aliVideoCanvasSmall = new AliRtcEngine.AliVideoCanvas();
        this.aliVideoCanvasSmall.view = this.sfSmallView;
        this.aliVideoCanvasSmall.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
        if (this.mAliRtcEngine != null) {
            this.mAliRtcEngine.setLocalViewConfig(this.aliVideoCanvasBig, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
    }

    /* access modifiers changed from: protected */
    public void changeStatusView() {
        enterCallingMode();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, this.mGrant);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            new Handler().postDelayed(new Runnable() {
                public final void run() {
                    VisualCallReceiveActivity.this.lambda$setUpSplash$0$BaseCallActivity();
                }
            }, 500);
        }
    }

    private void initEventListener() {
        this.mEventListener = new AliRtcEngineEventListener() {
            public void onJoinChannelResult(int i) {
                KLog.d("------加入通道结果 = " + i);
            }

            public void onLeaveChannelResult(int i) {
            }

            public void onPublishResult(int i, String s) {
            }

            public void onUnpublishResult(int i) {
            }

            public void onSubscribeResult(String s, int i, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack) {
                if (i == 0) {
                    VisualCallReceiveActivity.this.updateRemoteDisplay(s, aliRtcAudioTrack, aliRtcVideoTrack);
                }
            }

            public void onUnsubscribeResult(int i, String s) {
            }

            public void onNetworkQualityChanged(String s, AliRtcEngine.AliRtcNetworkQuality upQuality, AliRtcEngine.AliRtcNetworkQuality downQuality) {
                VisualCallReceiveActivity.this.runOnUiThread(new Runnable(upQuality, downQuality, s) {
                    private final /* synthetic */ AliRtcEngine.AliRtcNetworkQuality f$1;
                    private final /* synthetic */ AliRtcEngine.AliRtcNetworkQuality f$2;
                    private final /* synthetic */ String f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        VisualCallReceiveActivity.AnonymousClass5.this.lambda$onNetworkQualityChanged$0$VisualCallReceiveActivity$5(this.f$1, this.f$2, this.f$3);
                    }
                });
            }

            public /* synthetic */ void lambda$onNetworkQualityChanged$0$VisualCallReceiveActivity$5(AliRtcEngine.AliRtcNetworkQuality upQuality, AliRtcEngine.AliRtcNetworkQuality downQuality, String s) {
                if (upQuality == AliRtcEngine.AliRtcNetworkQuality.Network_Disconnected || downQuality == AliRtcEngine.AliRtcNetworkQuality.Network_Disconnected) {
                    byte unused = VisualCallReceiveActivity.this.mbytExit = (byte) 1;
                    AVideoCallInterface.DiscardAVideoCall(VisualCallReceiveActivity.this.getIntent().getStringExtra(TtmlNode.ATTR_ID), ((int) (System.currentTimeMillis() - VisualCallReceiveActivity.this.mlStart)) / 1000, VisualCallReceiveActivity.this.callStyle == 2);
                    VisualCallReceiveActivity.this.stopRinging();
                    if (VisualCallReceiveActivity.this.VisualCallType == 3) {
                        VisualCallReceiveActivity.this.txtTip.setText(LocaleController.getString("visual_call_stop", R.string.visual_call_stop));
                        VisualCallReceiveActivity.this.txtTip.setVisibility(0);
                        VisualCallReceiveActivity.this.chrVisualcallTime.stop();
                        VisualCallReceiveActivity.this.txtTip.postDelayed(new Runnable() {
                            public final void run() {
                                VisualCallReceiveActivity.this.finish();
                            }
                        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    } else {
                        VisualCallReceiveActivity.this.txtTip.setText(LocaleController.getString("visual_call_stop", R.string.visual_call_stop));
                        VisualCallReceiveActivity.this.txtTip.setVisibility(0);
                        VisualCallReceiveActivity.this.txtTip.postDelayed(new Runnable() {
                            public final void run() {
                                VisualCallReceiveActivity.this.finish();
                            }
                        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    }
                    VisualCallReceiveActivity.this.cancelCallingState();
                }
                if (VisualCallReceiveActivity.this.mbytExit == 1 || VisualCallReceiveActivity.this.callStyle != 2) {
                    return;
                }
                if (s.equals(Integer.valueOf(VisualCallReceiveActivity.this.mUser.id))) {
                    if (upQuality.getValue() > AliRtcEngine.AliRtcNetworkQuality.Network_Bad.getValue()) {
                        VisualCallReceiveActivity.this.txtTip.setText(LocaleController.getString("visual_call_other_net_bad", R.string.visual_call_other_net_bad));
                        VisualCallReceiveActivity.this.txtTip.setVisibility(0);
                        long unused2 = VisualCallReceiveActivity.this.mlTipShow = System.currentTimeMillis();
                    } else if (VisualCallReceiveActivity.this.txtTip.getVisibility() == 0 && System.currentTimeMillis() - VisualCallReceiveActivity.this.mlTipShow > AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                        VisualCallReceiveActivity.this.txtTip.setVisibility(8);
                    }
                } else if (downQuality.getValue() > AliRtcEngine.AliRtcNetworkQuality.Network_Bad.getValue()) {
                    VisualCallReceiveActivity.this.txtTip.setText(LocaleController.getString("visual_call_net_bad", R.string.visual_call_net_bad));
                    VisualCallReceiveActivity.this.txtTip.setVisibility(0);
                    long unused3 = VisualCallReceiveActivity.this.mlTipShow = System.currentTimeMillis();
                } else if (VisualCallReceiveActivity.this.txtTip.getVisibility() == 0 && System.currentTimeMillis() - VisualCallReceiveActivity.this.mlTipShow > AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                    VisualCallReceiveActivity.this.txtTip.setVisibility(8);
                }
            }

            public void onOccurWarning(int i) {
            }

            public void onOccurError(int error) {
                VisualCallReceiveActivity.this.processOccurError(error);
            }

            public void onPerformanceLow() {
            }

            public void onPermormanceRecovery() {
            }

            public void onConnectionLost() {
                VisualCallReceiveActivity.this.runOnUiThread(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.AnonymousClass5.this.lambda$onConnectionLost$1$VisualCallReceiveActivity$5();
                    }
                });
            }

            public /* synthetic */ void lambda$onConnectionLost$1$VisualCallReceiveActivity$5() {
                VisualCallReceiveActivity.this.txtTip.setText(LocaleController.getString("visual_call_network_disconnect", R.string.visual_call_network_disconnect));
                VisualCallReceiveActivity.this.txtTip.setVisibility(0);
            }

            public void onTryToReconnect() {
                VisualCallReceiveActivity.this.runOnUiThread(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.AnonymousClass5.this.lambda$onTryToReconnect$2$VisualCallReceiveActivity$5();
                    }
                });
            }

            public /* synthetic */ void lambda$onTryToReconnect$2$VisualCallReceiveActivity$5() {
                VisualCallReceiveActivity.this.txtTip.setText(LocaleController.getString("visual_call_retry_connect", R.string.visual_call_retry_connect));
                VisualCallReceiveActivity.this.txtTip.setVisibility(0);
            }

            public /* synthetic */ void lambda$onConnectionRecovery$3$VisualCallReceiveActivity$5() {
                VisualCallReceiveActivity.this.txtTip.setVisibility(8);
            }

            public void onConnectionRecovery() {
                VisualCallReceiveActivity.this.runOnUiThread(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.AnonymousClass5.this.lambda$onConnectionRecovery$3$VisualCallReceiveActivity$5();
                    }
                });
            }

            public void onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_client_role, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_client_role1) {
            }
        };
        this.mEngineNotify = new AliRtcEngineNotify() {
            public void onRemoteUserUnPublish(AliRtcEngine aliRtcEngine, String s) {
                KLog.d("---------远端用户停止发布通知" + s);
                VisualCallReceiveActivity.this.updateRemoteDisplay(s, AliRtcEngine.AliRtcAudioTrack.AliRtcAudioTrackNo, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo);
                VisualCallReceiveActivity.this.runOnUiThread(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.AnonymousClass6.this.lambda$onRemoteUserUnPublish$0$VisualCallReceiveActivity$6();
                    }
                });
            }

            public /* synthetic */ void lambda$onRemoteUserUnPublish$0$VisualCallReceiveActivity$6() {
                if (!VisualCallReceiveActivity.this.mChannel.equals("666")) {
                    VisualCallReceiveActivity.this.stopRinging();
                    if (VisualCallReceiveActivity.this.VisualCallType == 3) {
                        VisualCallReceiveActivity.this.txtTip.setText(LocaleController.getString("visual_call_other_side_discard", R.string.visual_call_other_side_discard));
                        VisualCallReceiveActivity.this.txtTip.setVisibility(0);
                        VisualCallReceiveActivity.this.chrVisualcallTime.stop();
                        VisualCallReceiveActivity.this.txtTip.postDelayed(new Runnable() {
                            public final void run() {
                                VisualCallReceiveActivity.this.finish();
                            }
                        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    } else {
                        VisualCallReceiveActivity.this.txtTip.setText(LocaleController.getString("visual_call_other_side_cancel", R.string.visual_call_other_side_cancel));
                        VisualCallReceiveActivity.this.txtTip.setVisibility(0);
                        VisualCallReceiveActivity.this.txtTip.postDelayed(new Runnable() {
                            public final void run() {
                                VisualCallReceiveActivity.this.finish();
                            }
                        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    }
                    VisualCallReceiveActivity.this.cancelCallingState();
                }
            }

            public void onRemoteUserOnLineNotify(String s) {
                KLog.d("----------远端用户上线通知" + s);
                if (TextUtils.isEmpty(VisualCallReceiveActivity.this.currentUid)) {
                    VisualCallReceiveActivity.this.currentUid = s;
                    VisualCallReceiveActivity.this.runOnUiThread(new Runnable(s) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            VisualCallReceiveActivity.AnonymousClass6.this.lambda$onRemoteUserOnLineNotify$2$VisualCallReceiveActivity$6(this.f$1);
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$onRemoteUserOnLineNotify$2$VisualCallReceiveActivity$6(String s) {
                VisualCallReceiveActivity.this.addRemoteUser(s);
                VisualCallReceiveActivity.this.VisualCallType = 3;
                VisualCallReceiveActivity.this.imgVisualcall.setVisibility(0);
                VisualCallReceiveActivity.this.relVideoUser.setVisibility(8);
                long unused = VisualCallReceiveActivity.this.mlStart = System.currentTimeMillis();
                VisualCallReceiveActivity.this.chrVisualcallTime.setVisibility(0);
                VisualCallReceiveActivity.this.chrVisualcallTime.setBase(SystemClock.elapsedRealtime());
                VisualCallReceiveActivity.this.chrVisualcallTime.start();
                VisualCallReceiveActivity.this.sfLocalView.setAlpha(1.0f);
                VisualCallReceiveActivity.this.txtVisualcallStatus.setText(LocaleController.getString("Str_visualcalling", R.string.Str_visualcalling));
                VisualCallReceiveActivity.this.txtVisualcallStatus.setVisibility(0);
                VisualCallReceiveActivity.this.txtMask.setVisibility(8);
                VisualCallReceiveActivity.this.txtMask.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.AnonymousClass6.this.lambda$null$1$VisualCallReceiveActivity$6();
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                if (ApplicationLoader.mbytAVideoCallBusy == 3 && VisualCallReceiveActivity.this.myservice != null) {
                    VisualCallReceiveActivity.this.myservice.setView((View) null, (View) null, VisualCallReceiveActivity.this.chrVisualcallTime.getBase(), VisualCallReceiveActivity.this.mChannel);
                }
                VisualCallReceiveActivity.this.changeStatusView();
            }

            public /* synthetic */ void lambda$null$1$VisualCallReceiveActivity$6() {
                VisualCallReceiveActivity.this.txtVisualcallStatus.setVisibility(8);
            }

            public void onRemoteUserOffLineNotify(String s) {
                VisualCallReceiveActivity.this.removeRemoteUser(s);
            }

            public void onRemoteTrackAvailableNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack) {
                StringBuilder sb = new StringBuilder();
                sb.append("---------视频流变化");
                sb.append(aliRtcVideoTrack == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo ? "   没有视频" : "   有视频");
                KLog.d(sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append("---------音频流变化");
                sb2.append(aliRtcAudioTrack == AliRtcEngine.AliRtcAudioTrack.AliRtcAudioTrackNo ? "   没有音频" : "   有音频");
                KLog.d(sb2.toString());
                VisualCallReceiveActivity.this.updateRemoteDisplay(s, aliRtcAudioTrack, aliRtcVideoTrack);
                if (aliRtcVideoTrack.getValue() != 0 && aliRtcVideoTrack.getValue() > AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo.getValue()) {
                    VisualCallReceiveActivity.this.runOnUiThread(new Runnable() {
                        public final void run() {
                            VisualCallReceiveActivity.AnonymousClass6.this.lambda$onRemoteTrackAvailableNotify$3$VisualCallReceiveActivity$6();
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$onRemoteTrackAvailableNotify$3$VisualCallReceiveActivity$6() {
                VisualCallReceiveActivity.this.VisualCallType = 3;
                if (VisualCallReceiveActivity.this.callStyle == 2) {
                    VisualCallReceiveActivity.this.chartVideoContainer.setVisibility(0);
                }
            }

            public void onSubscribeChangedNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack) {
            }

            public void onParticipantSubscribeNotify(AliSubscriberInfo[] aliSubscriberInfos, int i) {
            }

            public void onFirstFramereceived(String s, String s1, String s2, int i) {
            }

            public void onFirstPacketSent(String s, String s1, String s2, int i) {
            }

            public void onFirstPacketReceived(String s, String s1, String s2, int i) {
                VisualCallReceiveActivity.this.runOnUiThread(new Runnable() {
                    public final void run() {
                        VisualCallReceiveActivity.AnonymousClass6.this.lambda$onFirstPacketReceived$4$VisualCallReceiveActivity$6();
                    }
                });
            }

            public /* synthetic */ void lambda$onFirstPacketReceived$4$VisualCallReceiveActivity$6() {
                if (VisualCallReceiveActivity.this.callStyle == 2 && VisualCallReceiveActivity.this.mbytFPacketRecCount <= 0) {
                    VisualCallReceiveActivity.access$508(VisualCallReceiveActivity.this);
                    VisualCallReceiveActivity.this.changeLocalPreview((SophonSurfaceView) null);
                    if (VisualCallReceiveActivity.this.myservice != null && ApplicationLoader.mbytAVideoCallBusy == 3) {
                        VisualCallReceiveActivity.this.llBigWindow.addView(VisualCallReceiveActivity.this.myservice.getViewBig(true), new ViewGroup.LayoutParams(-1, -1));
                        VisualCallReceiveActivity.this.llBigWindow.removeView(VisualCallReceiveActivity.this.llBigRemoteView);
                        VisualCallReceiveActivity.this.chartVideoContainer.addView(VisualCallReceiveActivity.this.myservice.getViewSmall(true), new ViewGroup.LayoutParams(-1, -1));
                        VisualCallReceiveActivity.this.chartVideoContainer.removeView(VisualCallReceiveActivity.this.sfSmallView);
                        VisualCallReceiveActivity.this.myservice.setView(VisualCallReceiveActivity.this.llBigRemoteView, VisualCallReceiveActivity.this.sfSmallView, VisualCallReceiveActivity.this.chrVisualcallTime.getBase(), VisualCallReceiveActivity.this.mChannel);
                    }
                }
            }

            public void onParticipantUnsubscribeNotify(AliParticipantInfo[] aliParticipantInfos, int i) {
            }

            public void onBye(int i) {
            }

            public void onParticipantStatusNotify(AliStatusInfo[] aliStatusInfos, int i) {
            }

            public void onAliRtcStats(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats) {
            }
        };
    }

    public void onBackPressed() {
        XDialog.Builder builder = new XDialog.Builder(this);
        builder.setTitle(LocaleController.getString("Tips", R.string.Tips));
        builder.setMessage(LocaleController.getString(R.string.visual_call_exit_ask));
        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                VisualCallReceiveActivity.this.lambda$onBackPressed$6$VisualCallReceiveActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    public /* synthetic */ void lambda$onBackPressed$6$VisualCallReceiveActivity(DialogInterface dialogInterface, int i) {
        boolean z = true;
        this.mbytNoOp = 1;
        if (this.VisualCallType == 3) {
            String stringExtra = getIntent().getStringExtra(TtmlNode.ATTR_ID);
            int currentTimeMillis = ((int) (System.currentTimeMillis() - this.mlStart)) / 1000;
            if (this.callStyle != 2) {
                z = false;
            }
            AVideoCallInterface.DiscardAVideoCall(stringExtra, currentTimeMillis, z);
        } else {
            String stringExtra2 = getIntent().getStringExtra(TtmlNode.ATTR_ID);
            byte b = this.RESPONSE_REFUSE;
            if (this.callStyle != 2) {
                z = false;
            }
            AVideoCallInterface.DiscardAVideoCall(stringExtra2, b, z);
        }
        stopRinging();
        cancelCallingState();
        super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        KLog.d("--------------------?");
        if (this.mblnUnProcessChooseVoiceTip) {
            this.txtTip.setText(LocaleController.getString(R.string.visual_call_receive_to_voice));
            setTipPos();
            this.txtTip.setVisibility(0);
            this.txtTip.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallReceiveActivity.this.lambda$onResume$7$VisualCallReceiveActivity();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            this.mblnUnProcessChooseVoiceTip = false;
        }
        if (this.myservice != null && this.mbytIsForeground == 1) {
            View videoView = this.myservice.getViewBig(false);
            View smallView = this.myservice.getViewSmall(false);
            if (this.callStyle == 2 && videoView != null) {
                if (this.VisualCallType == 3) {
                    changeLocalPreview((SophonSurfaceView) null);
                }
                if (this.VisualCallType == 3) {
                    changeLocalPreview((SophonSurfaceView) null);
                }
                this.llBigWindow.addView(videoView, new ViewGroup.LayoutParams(-1, -1));
                if (smallView != null) {
                    this.chartVideoContainer.addView(smallView, new ViewGroup.LayoutParams(-1, -1));
                }
                this.chartVideoContainer.setVisibility(0);
            }
        }
        if (this.misConnect) {
            unbindService(this.mVideoServiceConnection);
            this.misConnect = false;
        }
        this.mbytIsForeground = 1;
    }

    public /* synthetic */ void lambda$onResume$7$VisualCallReceiveActivity() {
        this.txtTip.setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.mbytIsForeground = AndroidUtilities.isAppOnForeground(this) ? (byte) 1 : 0;
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void changeLocalPreview(SophonSurfaceView view) {
        if (view != null) {
            if (this.mbytLocalPos == 0) {
                this.mbytLocalPos = 1;
            } else {
                this.mbytLocalPos = 0;
            }
        }
        if (this.mbytLocalPos == 0) {
            this.sfLocalView.setVisibility(8);
            this.llBigRemoteView.setVisibility(0);
            this.sfSmallView.setVisibility(0);
            this.llSmallRemoteView.setVisibility(8);
            View v = this.llSmallRemoteView.getChildAt(0);
            this.llSmallRemoteView.removeAllViews();
            this.llBigRemoteView.removeAllViews();
            if (view != null) {
                this.llBigRemoteView.addView(view, new LinearLayout.LayoutParams(-1, -1));
            } else if (v != null) {
                ((SophonSurfaceView) v).setZOrderOnTop(false);
                ((SophonSurfaceView) v).setZOrderMediaOverlay(false);
                this.llBigRemoteView.addView(v, new LinearLayout.LayoutParams(-1, -1));
            }
            if (this.mAliRtcEngine != null) {
                this.sfSmallView.setZOrderOnTop(true);
                this.sfSmallView.setZOrderMediaOverlay(true);
                this.mAliRtcEngine.setLocalViewConfig(this.aliVideoCanvasSmall, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
                this.mAliRtcEngine.startPreview();
            }
            this.mbytLocalPos = 1;
            return;
        }
        this.sfLocalView.setVisibility(0);
        this.llBigRemoteView.setVisibility(8);
        this.sfSmallView.setVisibility(8);
        this.llSmallRemoteView.setVisibility(0);
        this.llSmallRemoteView.removeAllViews();
        View v2 = this.llBigRemoteView.getChildAt(0);
        this.llBigRemoteView.removeAllViews();
        if (view != null) {
            view.setZOrderOnTop(true);
            view.setZOrderMediaOverlay(true);
            this.llSmallRemoteView.addView(view, new LinearLayout.LayoutParams(-1, -1));
        } else if (v2 != null) {
            ((SophonSurfaceView) v2).setZOrderOnTop(true);
            this.llSmallRemoteView.addView(v2, new LinearLayout.LayoutParams(-1, -1));
        }
        if (this.mAliRtcEngine != null) {
            this.mAliRtcEngine.setLocalViewConfig(this.aliVideoCanvasBig, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
            this.mAliRtcEngine.startPreview();
        }
        this.mbytLocalPos = 0;
    }

    /* access modifiers changed from: protected */
    public void changePopWindow() {
        View view = null;
        View smallView = null;
        if (this.callStyle == 2) {
            if (this.mbytLocalPos == 0) {
                view = this.sfLocalView;
                smallView = this.llSmallRemoteView;
            } else {
                view = this.llBigRemoteView;
                smallView = this.sfSmallView;
            }
            this.llBigWindow.removeView(view);
            this.chartVideoContainer.removeView(smallView);
        }
        this.myservice.setCallStyle(this.callStyle);
        this.myservice.setBlnCaller(false);
        if (this.VisualCallType == 3) {
            this.myservice.setView(view, smallView, this.chrVisualcallTime.getBase(), this.mChannel);
            return;
        }
        this.myservice.setView(view, smallView, -1000000, this.mChannel);
    }

    private void setTipPos() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.txtTip.getLayoutParams();
        if (this.relVoiceUser.getVisibility() == 8) {
            this.txtTip.setGravity(17);
            return;
        }
        layoutParams.addRule(3, R.id.rel_voice_user);
        layoutParams.topMargin = AndroidUtilities.dp(25.0f);
    }

    /* access modifiers changed from: private */
    public void changeToVoice(boolean blnCaller) {
        setAVideoUI();
        this.txtPreChangeToVoice.setVisibility(8);
        if (this.mbytIsForeground == 0) {
            this.mblnUnProcessChooseVoiceTip = true;
        } else {
            if (blnCaller) {
                this.txtTip.setText(LocaleController.getString(R.string.visual_call_caller_to_voice));
            } else {
                this.txtTip.setText(LocaleController.getString(R.string.visual_call_receive_to_voice));
            }
            setTipPos();
            this.txtTip.setVisibility(0);
            this.txtTip.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallReceiveActivity.this.lambda$changeToVoice$8$VisualCallReceiveActivity();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
        if (this.mAliRtcEngine != null) {
            if (this.mAliRtcEngine.isSpeakerOn()) {
                this.mAliRtcEngine.enableSpeakerphone(false);
            }
            this.mAliRtcEngine.stopPreview();
        }
        if (this.VisualCallType == 3) {
            changeStatusView();
            this.linOperateA.setVisibility(0);
            this.linOperateB.setVisibility(0);
            this.linOperateC.setVisibility(0);
            this.chrVisualcallTime.setVisibility(0);
            this.llBigWindow.setVisibility(8);
            this.chartVideoContainer.setVisibility(8);
            this.txtOperateA.setText(LocaleController.getString(R.string.Str_visualcall_no_voice));
            this.txtOperateC.setText(LocaleController.getString(R.string.Str_visualcall_hands_free));
            if (this.mIsAudioCapture) {
                this.imgOperateA.setBackgroundResource(R.drawable.visualcall_no_voice);
            } else {
                this.imgOperateA.setBackgroundResource(R.drawable.visualcall_no_voice_selected);
            }
            if (this.mAliRtcEngine == null) {
                return;
            }
            if (this.mAliRtcEngine.isSpeakerOn()) {
                this.imgOperateC.setBackgroundResource(R.drawable.visual_hands_free_selected);
            } else {
                this.imgOperateC.setBackgroundResource(R.drawable.visualcall_hands_free);
            }
        }
    }

    public /* synthetic */ void lambda$changeToVoice$8$VisualCallReceiveActivity() {
        this.txtTip.setVisibility(8);
    }
}

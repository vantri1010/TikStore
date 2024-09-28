package im.bclpbkiauv.ui.hui.visualcall;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alivc.rtc.AliRtcAuthInfo;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcEngineEventListener;
import com.alivc.rtc.AliRtcEngineImpl;
import com.alivc.rtc.AliRtcEngineNotify;
import com.alivc.rtc.AliRtcRemoteUserInfo;
import com.blankj.utilcode.constant.TimeConstants;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.king.zxing.util.LogUtils;
import com.socks.library.KLog;
import ezy.assist.compat.SettingsCompat;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCCall;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.visualcall.AVideoCallInterface;
import im.bclpbkiauv.ui.hui.visualcall.BaseRecyclerViewAdapter;
import im.bclpbkiauv.ui.hui.visualcall.CallNetWorkReceiver;
import im.bclpbkiauv.ui.hui.visualcall.ChartUserAdapter;
import im.bclpbkiauv.ui.hui.visualcall.FlowService;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.hui.visualcall.RTCAuthInfo;
import im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity;
import im.bclpbkiauv.ui.hviews.DragFrameLayout;
import im.bclpbkiauv.ui.hviews.dialogs.Util;
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

public class VisualCallActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static final String TAG = VisualCallActivity.class.getName();
    TLRPC.InputPeer ChannelPeer;
    /* access modifiers changed from: private */
    public byte REQUEST_CANCEL = -2;
    private byte REQUEST_NETWORK_ERROR = -5;
    private byte REQUEST_NO_ANSWER = -4;
    private byte VISUAL_CALL_BUSY = -3;
    /* access modifiers changed from: private */
    public int VisualCallType = 1;
    protected AliRtcEngine.AliVideoCanvas aliVideoCanvasBig;
    protected AliRtcEngine.AliVideoCanvas aliVideoCanvasSmall;
    private CallNetWorkReceiver callNetWorkReceiver;
    /* access modifiers changed from: private */
    public int callStyle = 2;
    @BindView(2131296449)
    RecyclerView chartUserListView;
    @BindView(2131296458)
    DragFrameLayout chart_video_container;
    @BindView(2131296463)
    Chronometer chrVisualcallTime;
    public String currentUid;
    DynamicPoint dynamicPoint;
    @BindView(2131296692)
    BackupImageView imgUserHead;
    @BindView(2131296693)
    BackupImageView imgVideoUserHead;
    @BindView(2131296694)
    ImageView imgVisualcall;
    @BindView(2131296682)
    ImageView img_operate_a;
    @BindView(2131296683)
    ImageView img_operate_b;
    @BindView(2131296684)
    ImageView img_operate_c;
    @BindView(2131296686)
    ImageView img_pre_receive;
    @BindView(2131296868)
    LinearLayout lin_operate_a;
    @BindView(2131296869)
    LinearLayout lin_operate_b;
    @BindView(2131296870)
    LinearLayout lin_operate_c;
    @BindView(2131296929)
    LinearLayout llBigRemoteView;
    @BindView(2131296930)
    LinearLayout llBigWindow;
    @BindView(2131296956)
    LinearLayout llSmallRemoteView;
    /* access modifiers changed from: private */
    public AliRtcEngine mAliRtcEngine;
    protected boolean mBlnReceiveFeedBack = false;
    Bundle mBundle = new Bundle();
    /* access modifiers changed from: private */
    public String mChannel = "0001";
    protected Context mContext;
    private AliRtcEngineNotify mEngineNotify = new AliRtcEngineNotify() {
        public void onRemoteUserUnPublish(AliRtcEngine aliRtcEngine, String s) {
            KLog.d("---------远端用户停止发布通知" + s);
            VisualCallActivity.this.updateRemoteDisplay(s, AliRtcEngine.AliRtcAudioTrack.AliRtcAudioTrackNo, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo);
            VisualCallActivity.this.runOnUiThread(new Runnable() {
                public final void run() {
                    VisualCallActivity.AnonymousClass7.this.lambda$onRemoteUserUnPublish$0$VisualCallActivity$7();
                }
            });
        }

        public /* synthetic */ void lambda$onRemoteUserUnPublish$0$VisualCallActivity$7() {
            if (!VisualCallActivity.this.mChannel.equals("666")) {
                VisualCallActivity.this.stopRtcAndService();
                if (VisualCallActivity.this.spConnectingId != 0) {
                    VisualCallActivity.this.soundPool.stop(VisualCallActivity.this.spConnectingId);
                    VisualCallActivity.this.spConnectingId = 0;
                }
                if (VisualCallActivity.this.VisualCallType == 3) {
                    VisualCallActivity.this.txtTip.setText(LocaleController.getString("visual_call_other_side_discard", R.string.visual_call_other_side_discard));
                    VisualCallActivity.this.txtTip.setVisibility(0);
                    VisualCallActivity.this.chrVisualcallTime.stop();
                    VisualCallActivity.this.txtTip.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallActivity.this.finish();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    return;
                }
                VisualCallActivity.this.mBlnReceiveFeedBack = true;
                VisualCallActivity.this.txtTip.setText(LocaleController.getString("visual_call_other_side_refuse", R.string.visual_call_other_side_refuse));
                VisualCallActivity.this.txtTip.setVisibility(0);
                VisualCallActivity.this.txtTip.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallActivity.this.finish();
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        }

        public void onRemoteUserOnLineNotify(String s) {
            KLog.d("----------远端用户上线通知" + s);
            VisualCallActivity.this.mAliRtcEngine.configRemoteAudio(s, true);
            VisualCallActivity.this.mAliRtcEngine.muteRemoteAudioPlaying(s, false);
            if (TextUtils.isEmpty(VisualCallActivity.this.currentUid)) {
                VisualCallActivity.this.currentUid = s;
                VisualCallActivity.this.runOnUiThread(new Runnable(s) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        VisualCallActivity.AnonymousClass7.this.lambda$onRemoteUserOnLineNotify$2$VisualCallActivity$7(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onRemoteUserOnLineNotify$2$VisualCallActivity$7(String s) {
            addRemoteUser(s);
            int unused = VisualCallActivity.this.VisualCallType = 3;
            VisualCallActivity.this.imgVisualcall.setVisibility(0);
            VisualCallActivity.this.chrVisualcallTime.setVisibility(0);
            VisualCallActivity.this.chrVisualcallTime.setBase(SystemClock.elapsedRealtime());
            VisualCallActivity.this.chrVisualcallTime.start();
            VisualCallActivity.this.rel_video_user.setVisibility(8);
            VisualCallActivity.this.txtVisualcallStatus.setVisibility(0);
            VisualCallActivity.this.imgVisualcall.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallActivity.AnonymousClass7.this.lambda$null$1$VisualCallActivity$7();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            long unused2 = VisualCallActivity.this.mlStart = System.currentTimeMillis();
            if (ApplicationLoader.mbytAVideoCallBusy == 4 && VisualCallActivity.this.myservice != null) {
                VisualCallActivity.this.myservice.setView((View) null, (View) null, VisualCallActivity.this.chrVisualcallTime.getBase(), VisualCallActivity.this.mChannel);
            }
            VisualCallActivity.this.changeStatusView();
        }

        public /* synthetic */ void lambda$null$1$VisualCallActivity$7() {
            VisualCallActivity.this.txtVisualcallStatus.setVisibility(8);
        }

        private void addRemoteUser(String uid) {
            AliRtcRemoteUserInfo remoteUserInfo;
            if (VisualCallActivity.this.mAliRtcEngine != null && (remoteUserInfo = VisualCallActivity.this.mAliRtcEngine.getUserInfo(uid)) != null) {
                ChartUserBean data = convertRemoteUserToUserData(remoteUserInfo);
                KLog.d("---------mScreenSurface-" + data.mCameraSurface + "   " + data.mScreenSurface);
                if (data.mCameraSurface != null) {
                    KLog.d("---------mScreenSurface");
                    ViewParent parent = data.mCameraSurface.getParent();
                    if (parent != null && (parent instanceof FrameLayout)) {
                        ((FrameLayout) parent).removeAllViews();
                    }
                    if (VisualCallActivity.this.callStyle == 2) {
                        VisualCallActivity.this.changeLocalPreview(convertRemoteUserToUserData(remoteUserInfo).mCameraSurface);
                    }
                }
            }
        }

        private ChartUserBean convertRemoteUserToUserData(AliRtcRemoteUserInfo remoteUserInfo) {
            String uid = remoteUserInfo.getUserID();
            ChartUserBean ret = VisualCallActivity.this.mUserListAdapter.createDataIfNull(uid);
            ret.mUserId = uid;
            ret.mUserName = remoteUserInfo.getDisplayName();
            ret.mIsCameraFlip = false;
            ret.mIsScreenFlip = false;
            return ret;
        }

        public void onRemoteUserOffLineNotify(String s) {
            removeRemoteUser(s);
        }

        private void removeRemoteUser(String uid) {
            KLog.d("---------远端用户下线通知" + uid);
            VisualCallActivity.this.runOnUiThread(new Runnable(uid) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VisualCallActivity.AnonymousClass7.this.lambda$removeRemoteUser$3$VisualCallActivity$7(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$removeRemoteUser$3$VisualCallActivity$7(String uid) {
            VisualCallActivity.this.mUserListAdapter.removeData(uid, true);
            if (!VisualCallActivity.this.mChannel.equals("666")) {
                VisualCallActivity.this.stopRtcAndService();
                if (VisualCallActivity.this.spConnectingId != 0) {
                    VisualCallActivity.this.soundPool.stop(VisualCallActivity.this.spConnectingId);
                    VisualCallActivity.this.spConnectingId = 0;
                }
                if (VisualCallActivity.this.VisualCallType == 3) {
                    VisualCallActivity.this.txtTip.setText(LocaleController.getString("visual_call_other_side_discard", R.string.visual_call_other_side_discard));
                    VisualCallActivity.this.txtTip.setVisibility(0);
                    VisualCallActivity.this.chrVisualcallTime.stop();
                    VisualCallActivity.this.txtTip.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallActivity.this.finish();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    return;
                }
                VisualCallActivity.this.mBlnReceiveFeedBack = true;
                VisualCallActivity.this.txtTip.setText(LocaleController.getString("visual_call_other_side_refuse", R.string.visual_call_other_side_refuse));
                VisualCallActivity.this.txtTip.setVisibility(0);
                VisualCallActivity.this.txtTip.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallActivity.this.finish();
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        }

        public void onRemoteTrackAvailableNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack) {
            StringBuilder sb = new StringBuilder();
            sb.append("---------视频流变化 ");
            sb.append(aliRtcVideoTrack.getValue() == 0 ? "没有视频" : "视频");
            KLog.d(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("---------音频流变化 ");
            sb2.append(aliRtcAudioTrack.getValue() == 0 ? "没有音频" : "音频");
            KLog.d(sb2.toString());
            VisualCallActivity.this.updateRemoteDisplay(s, aliRtcAudioTrack, aliRtcVideoTrack);
            if (aliRtcVideoTrack.getValue() != 0 && aliRtcVideoTrack.getValue() > AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo.getValue()) {
                VisualCallActivity.this.runOnUiThread(new Runnable() {
                    public final void run() {
                        VisualCallActivity.AnonymousClass7.this.lambda$onRemoteTrackAvailableNotify$4$VisualCallActivity$7();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onRemoteTrackAvailableNotify$4$VisualCallActivity$7() {
            int unused = VisualCallActivity.this.VisualCallType = 3;
            if (VisualCallActivity.this.callStyle == 2) {
                VisualCallActivity.this.chart_video_container.setVisibility(0);
            }
        }

        public void onSubscribeChangedNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack) {
        }

        public void onParticipantSubscribeNotify(AliSubscriberInfo[] aliSubscriberInfos, int i) {
        }

        public void onFirstFramereceived(String s, String s1, String s2, int i) {
            KLog.d("------------首桢接收成功");
        }

        public void onFirstPacketSent(String s, String s1, String s2, int i) {
            KLog.d("------------首包的发送回调");
            ThreadUtils.runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }

        public void onFirstPacketReceived(String callId, String streamLabel, String trackLabel, int timeCost) {
            KLog.d("+++++++++++首包数据接收成功");
            VisualCallActivity.this.runOnUiThread(new Runnable() {
                public final void run() {
                    VisualCallActivity.AnonymousClass7.this.lambda$onFirstPacketReceived$5$VisualCallActivity$7();
                }
            });
        }

        public /* synthetic */ void lambda$onFirstPacketReceived$5$VisualCallActivity$7() {
            if (VisualCallActivity.this.callStyle == 2 && VisualCallActivity.this.mbytFPacketRecCount <= 0) {
                VisualCallActivity.access$1808(VisualCallActivity.this);
                VisualCallActivity.this.changeLocalPreview((SophonSurfaceView) null);
                if (VisualCallActivity.this.myservice != null && ApplicationLoader.mbytAVideoCallBusy == 4) {
                    VisualCallActivity.this.llBigWindow.addView(VisualCallActivity.this.myservice.getViewBig(true), new ViewGroup.LayoutParams(-1, -1));
                    VisualCallActivity.this.llBigWindow.removeView(VisualCallActivity.this.llBigRemoteView);
                    VisualCallActivity.this.chart_video_container.addView(VisualCallActivity.this.myservice.getViewSmall(true), new ViewGroup.LayoutParams(-1, -1));
                    VisualCallActivity.this.chart_video_container.removeView(VisualCallActivity.this.sfSmallView);
                    VisualCallActivity.this.myservice.setView(VisualCallActivity.this.llBigRemoteView, VisualCallActivity.this.sfSmallView, VisualCallActivity.this.chrVisualcallTime.getBase(), VisualCallActivity.this.mChannel);
                }
            }
        }

        public void onParticipantUnsubscribeNotify(AliParticipantInfo[] aliParticipantInfos, int i) {
            KLog.d("------当前订阅人数" + i);
        }

        public void onBye(int i) {
            KLog.d("------被服务器踢出或者频道关闭时回调");
        }

        public void onParticipantStatusNotify(AliStatusInfo[] aliStatusInfos, int i) {
        }

        public void onAliRtcStats(ALI_RTC_INTERFACE.AliRtcStats aliRtcStats) {
        }
    };
    private AliRtcEngineEventListener mEventListener = new AliRtcEngineEventListener() {
        public void onJoinChannelResult(int i) {
            KLog.d("++++++++++成功加入房间");
            VisualCallActivity.this.runOnUiThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VisualCallActivity.AnonymousClass6.this.lambda$onJoinChannelResult$0$VisualCallActivity$6(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onJoinChannelResult$0$VisualCallActivity$6(int i) {
            if (i == 0) {
                if (VisualCallActivity.this.mForeServiceIntent == null) {
                    Intent unused = VisualCallActivity.this.mForeServiceIntent = new Intent(VisualCallActivity.this.mContext.getApplicationContext(), ForegroundService.class);
                    VisualCallActivity.this.mForeServiceIntent.putExtras(VisualCallActivity.this.mBundle);
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    VisualCallActivity visualCallActivity = VisualCallActivity.this;
                    visualCallActivity.startForegroundService(visualCallActivity.mForeServiceIntent);
                    return;
                }
                VisualCallActivity visualCallActivity2 = VisualCallActivity.this;
                visualCallActivity2.startService(visualCallActivity2.mForeServiceIntent);
            }
        }

        public void onLeaveChannelResult(int i) {
            KLog.d("-------离开房间");
        }

        public void onPublishResult(int i, String s) {
        }

        public void onUnpublishResult(int i) {
        }

        public void onSubscribeResult(String s, int i, AliRtcEngine.AliRtcVideoTrack aliRtcVideoTrack, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack) {
            if (i == 0) {
                VisualCallActivity.this.updateRemoteDisplay(s, aliRtcAudioTrack, aliRtcVideoTrack);
            }
        }

        public void onUnsubscribeResult(int i, String s) {
            VisualCallActivity.this.updateRemoteDisplay(s, AliRtcEngine.AliRtcAudioTrack.AliRtcAudioTrackNo, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo);
        }

        public void onNetworkQualityChanged(String s, AliRtcEngine.AliRtcNetworkQuality upQuality, AliRtcEngine.AliRtcNetworkQuality downQuality) {
            VisualCallActivity.this.runOnUiThread(new Runnable(upQuality, downQuality, s) {
                private final /* synthetic */ AliRtcEngine.AliRtcNetworkQuality f$1;
                private final /* synthetic */ AliRtcEngine.AliRtcNetworkQuality f$2;
                private final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    VisualCallActivity.AnonymousClass6.this.lambda$onNetworkQualityChanged$1$VisualCallActivity$6(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0011, code lost:
            if (r18 == com.alivc.rtc.AliRtcEngine.AliRtcNetworkQuality.Network_Disconnected) goto L_0x0016;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onNetworkQualityChanged$1$VisualCallActivity$6(com.alivc.rtc.AliRtcEngine.AliRtcNetworkQuality r17, com.alivc.rtc.AliRtcEngine.AliRtcNetworkQuality r18, java.lang.String r19) {
            /*
                r16 = this;
                r0 = r16
                com.alivc.rtc.AliRtcEngine$AliRtcNetworkQuality r1 = com.alivc.rtc.AliRtcEngine.AliRtcNetworkQuality.Network_Disconnected
                r2 = 2000(0x7d0, double:9.88E-321)
                r4 = 2
                r5 = 1
                r6 = 0
                r7 = r17
                if (r7 == r1) goto L_0x0014
                com.alivc.rtc.AliRtcEngine$AliRtcNetworkQuality r1 = com.alivc.rtc.AliRtcEngine.AliRtcNetworkQuality.Network_Disconnected
                r8 = r18
                if (r8 != r1) goto L_0x00c4
                goto L_0x0016
            L_0x0014:
                r8 = r18
            L_0x0016:
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                byte unused = r1.mbytExit = r5
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                int r1 = r1.VisualCallType
                r9 = 3
                r10 = 2131695628(0x7f0f180c, float:1.9020446E38)
                java.lang.String r11 = "visual_call_stop"
                if (r1 != r9) goto L_0x0057
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                java.lang.String r1 = r1.mChannel
                long r12 = java.lang.System.currentTimeMillis()
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r9 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                long r14 = r9.mlStart
                long r12 = r12 - r14
                int r9 = (int) r12
                int r9 = r9 / 1000
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r12 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                int r12 = r12.callStyle
                if (r12 != r4) goto L_0x0047
                r12 = 1
                goto L_0x0048
            L_0x0047:
                r12 = 0
            L_0x0048:
                im.bclpbkiauv.ui.hui.visualcall.AVideoCallInterface.DiscardAVideoCall(r1, r9, r12)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
                r1.setText(r9)
                goto L_0x0099
            L_0x0057:
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                int r1 = r1.VisualCallType
                if (r1 != r5) goto L_0x0099
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                java.lang.String r1 = r1.mChannel
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r9 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                byte r9 = r9.REQUEST_CANCEL
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r12 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                int r12 = r12.callStyle
                if (r12 != r4) goto L_0x0075
                r12 = 1
                goto L_0x0076
            L_0x0075:
                r12 = 0
            L_0x0076:
                im.bclpbkiauv.ui.hui.visualcall.AVideoCallInterface.DiscardAVideoCall(r1, r9, r12)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                int r1 = r1.spConnectingId
                if (r1 == 0) goto L_0x008e
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.media.SoundPool r1 = r1.soundPool
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r9 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                int r9 = r9.spConnectingId
                r1.stop(r9)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                r1.spConnectingId = r6
            L_0x008e:
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
                r1.setText(r9)
            L_0x0099:
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                r1.stopRtcAndService()
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.ImageView r1 = r1.img_operate_b
                r9 = 2131231672(0x7f0803b8, float:1.8079432E38)
                r1.setBackgroundResource(r9)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.ImageView r1 = r1.img_operate_b
                r1.setEnabled(r6)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                r1.setVisibility(r6)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r9 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                im.bclpbkiauv.ui.hui.visualcall.-$$Lambda$jtxyk0FmZ57yqr3Ynbm24NMAq70 r10 = new im.bclpbkiauv.ui.hui.visualcall.-$$Lambda$jtxyk0FmZ57yqr3Ynbm24NMAq70
                r10.<init>()
                r1.postDelayed(r10, r2)
            L_0x00c4:
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                byte r1 = r1.mbytExit
                if (r1 != r5) goto L_0x00cd
                return
            L_0x00cd:
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                int r1 = r1.callStyle
                if (r1 != r4) goto L_0x0190
                int r1 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
                im.bclpbkiauv.messenger.AccountInstance r1 = im.bclpbkiauv.messenger.AccountInstance.getInstance(r1)
                im.bclpbkiauv.messenger.UserConfig r1 = r1.getUserConfig()
                im.bclpbkiauv.tgnet.TLRPC$User r1 = r1.getCurrentUser()
                int r1 = r1.id
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r4 = r19
                boolean r1 = r4.equals(r1)
                r5 = 8
                if (r1 != 0) goto L_0x0142
                int r1 = r17.getValue()
                com.alivc.rtc.AliRtcEngine$AliRtcNetworkQuality r9 = com.alivc.rtc.AliRtcEngine.AliRtcNetworkQuality.Network_Bad
                int r9 = r9.getValue()
                if (r1 <= r9) goto L_0x0121
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                r2 = 2131695612(0x7f0f17fc, float:1.9020414E38)
                java.lang.String r3 = "visual_call_other_net_bad"
                java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                r1.setVisibility(r6)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                long r2 = java.lang.System.currentTimeMillis()
                long unused = r1.mlTipShow = r2
                goto L_0x0192
            L_0x0121:
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                int r1 = r1.getVisibility()
                if (r1 != 0) goto L_0x0192
                long r9 = java.lang.System.currentTimeMillis()
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                long r11 = r1.mlTipShow
                long r9 = r9 - r11
                int r1 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x0192
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                r1.setVisibility(r5)
                goto L_0x0192
            L_0x0142:
                int r1 = r18.getValue()
                com.alivc.rtc.AliRtcEngine$AliRtcNetworkQuality r9 = com.alivc.rtc.AliRtcEngine.AliRtcNetworkQuality.Network_Bad
                int r9 = r9.getValue()
                if (r1 <= r9) goto L_0x016f
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                r2 = 2131695601(0x7f0f17f1, float:1.9020391E38)
                java.lang.String r3 = "visual_call_net_bad"
                java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                r1.setVisibility(r6)
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                long r2 = java.lang.System.currentTimeMillis()
                long unused = r1.mlTipShow = r2
                goto L_0x0192
            L_0x016f:
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                int r1 = r1.getVisibility()
                if (r1 != 0) goto L_0x0192
                long r9 = java.lang.System.currentTimeMillis()
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                long r11 = r1.mlTipShow
                long r9 = r9 - r11
                int r1 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x0192
                im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity r1 = im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.this
                android.widget.TextView r1 = r1.txtTip
                r1.setVisibility(r5)
                goto L_0x0192
            L_0x0190:
                r4 = r19
            L_0x0192:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity.AnonymousClass6.lambda$onNetworkQualityChanged$1$VisualCallActivity$6(com.alivc.rtc.AliRtcEngine$AliRtcNetworkQuality, com.alivc.rtc.AliRtcEngine$AliRtcNetworkQuality, java.lang.String):void");
        }

        public void onOccurWarning(int i) {
        }

        public void onOccurError(int error) {
            VisualCallActivity.this.processOccurError(error);
        }

        public void onPerformanceLow() {
        }

        public void onPermormanceRecovery() {
        }

        public void onConnectionLost() {
            VisualCallActivity.this.runOnUiThread(new Runnable() {
                public final void run() {
                    VisualCallActivity.AnonymousClass6.this.lambda$onConnectionLost$2$VisualCallActivity$6();
                }
            });
        }

        public /* synthetic */ void lambda$onConnectionLost$2$VisualCallActivity$6() {
            VisualCallActivity.this.txtTip.setText(LocaleController.getString("visual_call_network_disconnect", R.string.visual_call_network_disconnect));
            VisualCallActivity.this.txtTip.setVisibility(0);
        }

        public void onTryToReconnect() {
            VisualCallActivity.this.runOnUiThread(new Runnable() {
                public final void run() {
                    VisualCallActivity.AnonymousClass6.this.lambda$onTryToReconnect$3$VisualCallActivity$6();
                }
            });
        }

        public /* synthetic */ void lambda$onTryToReconnect$3$VisualCallActivity$6() {
            VisualCallActivity.this.txtTip.setText(LocaleController.getString("visual_call_retry_connect", R.string.visual_call_retry_connect));
            VisualCallActivity.this.txtTip.setVisibility(0);
        }

        public /* synthetic */ void lambda$onConnectionRecovery$4$VisualCallActivity$6() {
            VisualCallActivity.this.txtTip.setVisibility(8);
        }

        public void onConnectionRecovery() {
            VisualCallActivity.this.runOnUiThread(new Runnable() {
                public final void run() {
                    VisualCallActivity.AnonymousClass6.this.lambda$onConnectionRecovery$4$VisualCallActivity$6();
                }
            });
        }

        public void onUpdateRoleNotify(ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_client_role, ALI_RTC_INTERFACE.AliRTCSDK_Client_Role aliRTCSDK_client_role1) {
        }
    };
    /* access modifiers changed from: private */
    public Intent mForeServiceIntent;
    private PermissionUtils.PermissionGrant mGrant = new PermissionUtils.PermissionGrant() {
        public void onPermissionGranted(int requestCode) {
            VisualCallActivity.this.initRTCEngineAndStartPreview();
            VisualCallActivity.this.sendCallRequest();
            VisualCallActivity.this.chart_video_container.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallActivity.AnonymousClass9.this.lambda$onPermissionGranted$0$VisualCallActivity$9();
                }
            }, 35000);
            VisualCallActivity.this.img_operate_a.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallActivity.AnonymousClass9.this.lambda$onPermissionGranted$1$VisualCallActivity$9();
                }
            }, 15000);
        }

        public /* synthetic */ void lambda$onPermissionGranted$0$VisualCallActivity$9() {
            if (!VisualCallActivity.this.mBlnReceiveFeedBack && !VisualCallActivity.this.mblnResetNoAnswer) {
                VisualCallActivity.this.processNoAnswer();
            }
        }

        public /* synthetic */ void lambda$onPermissionGranted$1$VisualCallActivity$9() {
            if (!VisualCallActivity.this.mBlnReceiveFeedBack && !VisualCallActivity.this.mblnResetNoAnswer) {
                VisualCallActivity.this.processNoAnswerTip();
            }
        }

        public void onPermissionCancel() {
            ToastUtils.show((CharSequence) LocaleController.getString("grant_permission", R.string.grant_permission));
            VisualCallActivity.this.stopRtcAndService();
            VisualCallActivity.this.finish();
        }
    };
    private boolean mGrantPermission;
    private boolean mIsAudioCapture = true;
    private boolean mIsAudioPlay = true;
    @BindView(2131297284)
    SophonSurfaceView mLocalView;
    private ChartUserAdapter.OnSubConfigChangeListener mOnSubConfigChangeListener = new ChartUserAdapter.OnSubConfigChangeListener() {
        public void onFlipView(String uid, int flag, boolean flip) {
            AliRtcEngine.AliVideoCanvas cameraCanvas;
            AliRtcEngine.AliVideoCanvas screenCanvas;
            AliRtcRemoteUserInfo userInfo = VisualCallActivity.this.mAliRtcEngine.getUserInfo(uid);
            if (flag != 1001) {
                if (flag == 1002 && userInfo != null && (screenCanvas = userInfo.getScreenCanvas()) != null) {
                    screenCanvas.mirrorMode = flip ? AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllEnabled : AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllDisable;
                    VisualCallActivity.this.mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen);
                }
            } else if (userInfo != null && (cameraCanvas = userInfo.getCameraCanvas()) != null) {
                cameraCanvas.mirrorMode = flip ? AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllEnabled : AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllDisable;
                VisualCallActivity.this.mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
            }
        }

        public void onShowVideoInfo(String uid, int flag) {
            AliRtcEngine.AliRtcVideoTrack track = AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo;
            if (flag == 1001) {
                track = AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera;
            } else if (flag == 1002) {
                track = AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen;
            }
            if (VisualCallActivity.this.mAliRtcEngine != null) {
                Toast.makeText(VisualCallActivity.this, VisualCallActivity.this.mAliRtcEngine.getMediaInfoWithUserId(uid, track, AliRtcConstants.VIDEO_INFO_KEYS), 0).show();
            }
        }
    };
    /* access modifiers changed from: private */
    public RTCAuthInfo mRtcAuthInfo = new RTCAuthInfo();
    ArrayList<TLRPC.InputPeer> mUserArray;
    /* access modifiers changed from: private */
    public ChartUserAdapter mUserListAdapter;
    private String mUsername;
    ServiceConnection mVideoServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            FlowService unused = VisualCallActivity.this.myservice = ((FlowService.MyBinder) service).getService();
            View view = null;
            View smallView = null;
            if (VisualCallActivity.this.callStyle == 2) {
                if (VisualCallActivity.this.mbytLocalPos == 0) {
                    view = VisualCallActivity.this.mLocalView;
                    smallView = VisualCallActivity.this.llSmallRemoteView;
                } else {
                    view = VisualCallActivity.this.llBigRemoteView;
                    smallView = VisualCallActivity.this.sfSmallView;
                }
                VisualCallActivity.this.llBigWindow.removeView(view);
                VisualCallActivity.this.chart_video_container.removeView(smallView);
                VisualCallActivity.this.chart_video_container.setVisibility(8);
            }
            VisualCallActivity.this.myservice.setCallStyle(VisualCallActivity.this.callStyle);
            VisualCallActivity.this.myservice.setBlnCaller(true);
            if (VisualCallActivity.this.VisualCallType == 3) {
                VisualCallActivity.this.myservice.setView(view, smallView, VisualCallActivity.this.chrVisualcallTime.getBase(), VisualCallActivity.this.mChannel);
                return;
            }
            VisualCallActivity.this.myservice.setView(view, smallView, -1000000, VisualCallActivity.this.mChannel);
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private boolean mblnOtherIsPc = false;
    /* access modifiers changed from: private */
    public boolean mblnResetNoAnswer = false;
    private boolean mblnUnProcessChooseVoiceTip = false;
    /* access modifiers changed from: private */
    public byte mbytExit = 0;
    /* access modifiers changed from: private */
    public byte mbytFPacketRecCount = 0;
    private byte mbytIsForeground = 1;
    private byte mbytLastClickIndex = -1;
    protected byte mbytLocalPos = 0;
    private int miCallReceiverId = -1;
    private boolean misConnect = false;
    private long mlLastClickTime = 0;
    /* access modifiers changed from: private */
    public long mlStart = 0;
    /* access modifiers changed from: private */
    public long mlTipShow;
    /* access modifiers changed from: private */
    public FlowService myservice = null;
    @BindView(2131297092)
    RelativeLayout rel_video_user;
    @BindView(2131297093)
    LinearLayout rel_visual_call_a;
    @BindView(2131297094)
    RelativeLayout rel_visual_call_b;
    @BindView(2131297095)
    RelativeLayout rel_voice_user;
    @BindView(2131297285)
    SophonSurfaceView sfSmallView;
    protected SoundPool soundPool;
    protected int spConnectingId;
    protected SophonSurfaceView surfaceView = null;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    @BindView(2131297884)
    TextView txtCallName;
    @BindView(2131297885)
    ColorTextView txtCallStatus;
    @BindView(2131297911)
    TextView txtTip;
    @BindView(2131297915)
    TextView txtVideoName;
    @BindView(2131297916)
    ColorTextView txtVideoStatus;
    @BindView(2131297917)
    ColorTextView txtVisualcallStatus;
    @BindView(2131297901)
    ColorTextView txt_operate_a;
    @BindView(2131297902)
    ColorTextView txt_operate_b;
    @BindView(2131297903)
    ColorTextView txt_operate_c;
    @BindView(2131297907)
    TextView txt_pre_change_to_voice;

    static /* synthetic */ byte access$1808(VisualCallActivity x0) {
        byte b = x0.mbytFPacketRecCount;
        x0.mbytFPacketRecCount = (byte) (b + 1);
        return b;
    }

    /* access modifiers changed from: private */
    public void changeStatusView() {
        KLog.d("--------haha callStyle" + this.callStyle + "   VisualCallType" + this.VisualCallType);
        if (this.callStyle == 1) {
            AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
            if (aliRtcEngine != null && aliRtcEngine.isLocalCameraPublishEnabled()) {
                KLog.d("--------关闭视频");
                this.mAliRtcEngine.configLocalCameraPublish(false);
                this.mAliRtcEngine.publish();
            }
            this.chart_video_container.setVisibility(8);
            this.mLocalView.setVisibility(8);
            this.rel_video_user.setVisibility(8);
            this.rel_voice_user.setVisibility(0);
            this.txt_pre_change_to_voice.setVisibility(8);
            int i = this.VisualCallType;
            if (i == 1) {
                this.rel_visual_call_b.setVisibility(8);
                this.rel_visual_call_a.setVisibility(0);
                this.lin_operate_a.setVisibility(0);
                this.lin_operate_c.setVisibility(8);
                if (this.mIsAudioPlay) {
                    this.img_operate_a.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_no_voice));
                } else {
                    this.img_operate_a.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_no_voice_selected));
                }
                this.img_operate_c.setVisibility(4);
                this.txt_operate_b.setText(LocaleController.getString("Cancel", R.string.Cancel));
            } else if (i == 2) {
                this.rel_visual_call_b.setVisibility(0);
                this.rel_visual_call_a.setVisibility(8);
                this.img_pre_receive.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_receive_common));
            } else {
                this.rel_visual_call_b.setVisibility(8);
                this.rel_visual_call_a.setVisibility(0);
                this.img_operate_c.setVisibility(0);
                this.lin_operate_a.setVisibility(0);
                this.lin_operate_c.setVisibility(0);
                this.lin_operate_b.setVisibility(0);
                this.chrVisualcallTime.setVisibility(0);
                this.txtCallStatus.setVisibility(8);
                this.txt_operate_b.setText(LocaleController.getString("Str_visualcall_cancel", R.string.Str_visualcall_cancel));
                this.txt_operate_a.setText(LocaleController.getString(R.string.Str_visualcall_no_voice));
                this.txt_operate_c.setText(LocaleController.getString(R.string.Str_visualcall_hands_free));
                if (this.mIsAudioCapture) {
                    this.img_operate_a.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_no_voice));
                } else {
                    this.img_operate_a.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_no_voice_selected));
                }
                AliRtcEngine aliRtcEngine2 = this.mAliRtcEngine;
                if (aliRtcEngine2 == null) {
                    return;
                }
                if (aliRtcEngine2.isSpeakerOn()) {
                    this.img_operate_c.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visual_hands_free_selected));
                } else {
                    this.img_operate_c.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_hands_free));
                }
            }
        } else {
            this.chart_video_container.setVisibility(0);
            this.mLocalView.setVisibility(0);
            this.rel_video_user.setVisibility(0);
            this.rel_voice_user.setVisibility(8);
            AliRtcEngine aliRtcEngine3 = this.mAliRtcEngine;
            if (aliRtcEngine3 != null && !aliRtcEngine3.isLocalCameraPublishEnabled()) {
                KLog.d("--------打开视频");
                this.mAliRtcEngine.configLocalCameraPublish(true);
                this.mAliRtcEngine.publish();
            }
            int i2 = this.VisualCallType;
            if (i2 == 1) {
                this.rel_visual_call_b.setVisibility(8);
                this.rel_visual_call_a.setVisibility(0);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.txt_pre_change_to_voice.getLayoutParams();
                params.setMargins(0, 0, 0, (int) Util.dp2px(this.mContext, 200.0f));
                params.addRule(14, -1);
                params.addRule(12, -1);
                this.txt_pre_change_to_voice.setLayoutParams(params);
                this.rel_video_user.setVisibility(0);
                this.rel_voice_user.setVisibility(8);
                this.lin_operate_a.setVisibility(8);
                this.lin_operate_b.setVisibility(0);
                this.lin_operate_c.setVisibility(8);
            } else if (i2 == 2) {
                this.rel_visual_call_b.setVisibility(0);
                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) this.txt_pre_change_to_voice.getLayoutParams();
                params2.setMargins(0, 0, (int) Util.dp2px(this.mContext, 34.0f), (int) Util.dp2px(this.mContext, 213.0f));
                params2.addRule(11, -1);
                params2.addRule(12, -1);
                this.txt_pre_change_to_voice.setLayoutParams(params2);
                this.rel_visual_call_a.setVisibility(8);
                this.img_pre_receive.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_video_receive));
            } else {
                KLog.d("---------ai ");
                this.rel_visual_call_b.setVisibility(8);
                this.rel_visual_call_a.setVisibility(0);
                this.chart_video_container.setVisibility(0);
                this.txt_pre_change_to_voice.setVisibility(8);
                this.rel_video_user.setVisibility(8);
                this.rel_voice_user.setVisibility(8);
                this.lin_operate_a.setVisibility(0);
                this.lin_operate_b.setVisibility(0);
                this.lin_operate_c.setVisibility(0);
                this.img_operate_a.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_to_voice));
                this.txt_operate_a.setText(LocaleController.getString("Str_visualcall_to_voice", R.string.Str_visualcall_to_voice));
                this.img_operate_b.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_cancel_common));
                this.txt_operate_b.setText(LocaleController.getString("Str_visualcall_cancel", R.string.Str_visualcall_cancel));
                this.img_operate_c.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.visualcall_camera));
                this.txt_operate_c.setText(LocaleController.getString("Str_visualcall_change_camera", R.string.Str_visualcall_change_camera));
            }
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
                if (!VisualCallActivity.this.mChannel.equals("0001")) {
                    VisualCallActivity visualCallActivity = VisualCallActivity.this;
                    visualCallActivity.sendKeepLivePacket(visualCallActivity.mChannel);
                }
            }

            public void onNetWorkDisconnected() {
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLoader.mbytAVideoCallBusy = 2;
        setFullScreen();
        setContentView(R.layout.activity_visualcall);
        ButterKnife.bind((Activity) this);
        getWindow().addFlags(128);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(false, false);
        this.mContext = this;
        this.dynamicPoint = new DynamicPoint();
        initRing();
        this.chart_video_container.setY((float) AndroidUtilities.statusBarHeight);
        this.mRtcAuthInfo.data = new RTCAuthInfo.RTCAuthInfo_Data();
        this.mUsername = AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id + "";
        this.callStyle = getIntent().getIntExtra("CallType", 2);
        ArrayList<Integer> userIdArray = (ArrayList) getIntent().getSerializableExtra("ArrayUser");
        ArrayList<Integer> channelIdArray = (ArrayList) getIntent().getSerializableExtra("channel");
        KLog.d("---------VisualCallType" + this.VisualCallType + "   callStyle" + this.callStyle);
        this.mUserArray = new ArrayList<>();
        if (userIdArray != null && !userIdArray.isEmpty()) {
            Iterator<Integer> it = userIdArray.iterator();
            while (it.hasNext()) {
                this.mUserArray.add(AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getInputPeer(it.next().intValue()));
            }
            this.miCallReceiverId = userIdArray.get(0).intValue();
            setHeadImage();
        }
        if (this.callStyle == 2) {
            this.dynamicPoint.animForWaitting(LocaleController.getString("visual_call_video_waiting", R.string.visual_call_video_waiting), this.txtVideoStatus);
        } else {
            this.dynamicPoint.animForWaitting(LocaleController.getString("visual_call_waiting", R.string.visual_call_waiting), this.txtCallStatus);
        }
        regNotification();
        initRTCEngineAndStartPreview();
        this.txtTip.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Color.parseColor("#CB2D2D2D")));
        ChartUserAdapter chartUserAdapter = new ChartUserAdapter();
        this.mUserListAdapter = chartUserAdapter;
        chartUserAdapter.setOnSubConfigChangeListener(this.mOnSubConfigChangeListener);
        if (channelIdArray.isEmpty()) {
            this.ChannelPeer = null;
        } else {
            this.ChannelPeer = AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getInputPeer(channelIdArray.get(0).intValue());
        }
        if (this.mGrantPermission) {
            sendCallRequest();
        }
        changeStatusView();
        this.chrVisualcallTime.setOnChronometerTickListener($$Lambda$VisualCallActivity$Dzp5QHJDv0mwDVpllSlqbpYvUE.INSTANCE);
        this.chart_video_container.setVisibility(8);
        if (this.mGrantPermission) {
            this.chart_video_container.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallActivity.this.lambda$onCreate$1$VisualCallActivity();
                }
            }, 35000);
            this.img_operate_a.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallActivity.this.lambda$onCreate$2$VisualCallActivity();
                }
            }, 15000);
        }
        regNetWorkReceiver();
    }

    static /* synthetic */ void lambda$onCreate$0(Chronometer chronometer) {
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

    public /* synthetic */ void lambda$onCreate$1$VisualCallActivity() {
        if (!this.mBlnReceiveFeedBack && !this.mblnResetNoAnswer) {
            processNoAnswer();
        }
    }

    public /* synthetic */ void lambda$onCreate$2$VisualCallActivity() {
        if (!this.mBlnReceiveFeedBack && !this.mblnResetNoAnswer) {
            processNoAnswerTip();
        }
    }

    /* access modifiers changed from: private */
    public void processNoAnswer() {
        int i = this.spConnectingId;
        if (i != 0) {
            this.soundPool.stop(i);
            this.spConnectingId = 0;
        }
        stopRtcAndService();
        int currentConnectionState = ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState();
        boolean z = true;
        if (currentConnectionState == 2 || currentConnectionState == 1) {
            this.txtTip.setText(LocaleController.getString("visual_call_fail", R.string.visual_call_fail));
        } else {
            this.txtTip.setText(LocaleController.getString("visual_call_no_answer_tip", R.string.visual_call_no_answer_tip));
        }
        String str = this.mChannel;
        byte b = this.REQUEST_NO_ANSWER;
        if (this.callStyle != 2) {
            z = false;
        }
        AVideoCallInterface.DiscardAVideoCall(str, b, z);
        this.txtTip.setVisibility(0);
        setTipPos();
        this.txtVideoStatus.postDelayed(new Runnable() {
            public final void run() {
                VisualCallActivity.this.lambda$processNoAnswer$3$VisualCallActivity();
            }
        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    public /* synthetic */ void lambda$processNoAnswer$3$VisualCallActivity() {
        finish();
    }

    /* access modifiers changed from: protected */
    public void processNoAnswerTip() {
        this.txtTip.setText(LocaleController.getString("visual_call_no_answer", R.string.visual_call_no_answer));
        this.txtTip.setVisibility(0);
        setTipPos();
        this.txtCallStatus.postDelayed(new Runnable() {
            public final void run() {
                VisualCallActivity.this.lambda$processNoAnswerTip$4$VisualCallActivity();
            }
        }, 15000);
    }

    public /* synthetic */ void lambda$processNoAnswerTip$4$VisualCallActivity() {
        if (!this.mBlnReceiveFeedBack) {
            this.txtTip.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void sendKeepLivePacket(final String strId) {
        if (this.timerTask == null) {
            AnonymousClass2 r2 = new TimerTask() {
                public void run() {
                    ThreadUtils.runOnUiThread(new Runnable(strId) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            VisualCallActivity.AnonymousClass2.this.lambda$run$0$VisualCallActivity$2(this.f$1);
                        }
                    });
                }

                public /* synthetic */ void lambda$run$0$VisualCallActivity$2(String strId) {
                    AVideoCallInterface.sendJumpPacket(strId, new AVideoCallInterface.AVideoRequestCallBack() {
                        public void onError(TLRPC.TL_error error) {
                        }

                        public void onSuccess(TLObject object) {
                            if (object instanceof TLRPCCall.TL_MeetModel) {
                                TLRPCCall.TL_MeetModel model = (TLRPCCall.TL_MeetModel) object;
                                if (model.id.equals(VisualCallActivity.this.mChannel) && !model.video && VisualCallActivity.this.callStyle == 2) {
                                    int unused = VisualCallActivity.this.callStyle = 1;
                                    VisualCallActivity.this.changeToVoice(false);
                                }
                            }
                        }
                    });
                }
            };
            this.timerTask = r2;
            try {
                this.timer.schedule(r2, 14000, 14000);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendCallRequest() {
        AVideoCallInterface.StartAVideoCall(this.callStyle == 2, this.mUserArray, this.ChannelPeer, new AVideoCallInterface.AVideoRequestCallBack() {
            public void onError(TLRPC.TL_error error) {
                if (error.text.equals("MUTUALCONTACTNEED")) {
                    VisualCallActivity.this.txtTip.setText(LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
                    VisualCallActivity.this.txtTip.setVisibility(0);
                    VisualCallActivity.this.setTipPos();
                } else if (error.text.equals("VIDEO_RPC_ERROR")) {
                    VisualCallActivity.this.txtTip.setText(LocaleController.getString("visual_call_server_err", R.string.visual_call_server_err));
                    VisualCallActivity.this.txtTip.setVisibility(0);
                    VisualCallActivity.this.setTipPos();
                } else if (error.text.equals("IS_BUSYING") || error.text.equals("FROM_IS_BLOCKED") || error.text.equals("TO_IS_BLOCKED")) {
                    VisualCallActivity.this.stopRtcAndService();
                    if (VisualCallActivity.this.spConnectingId != 0) {
                        VisualCallActivity.this.soundPool.stop(VisualCallActivity.this.spConnectingId);
                        VisualCallActivity.this.spConnectingId = 0;
                    }
                    VisualCallActivity.this.mBlnReceiveFeedBack = true;
                    String str = error.text;
                    char c = 65535;
                    int hashCode = str.hashCode();
                    if (hashCode != -2133636844) {
                        if (hashCode != -2013590676) {
                            if (hashCode == 1424217083 && str.equals("TO_IS_BLOCKED")) {
                                c = 2;
                            }
                        } else if (str.equals("FROM_IS_BLOCKED")) {
                            c = 1;
                        }
                    } else if (str.equals("IS_BUSYING")) {
                        c = 0;
                    }
                    if (c == 0) {
                        VisualCallActivity.this.txtTip.setText(LocaleController.getString(R.string.visual_call_other_calling));
                    } else if (c == 1) {
                        VisualCallActivity.this.txtTip.setText(LocaleController.getString(R.string.visual_call_block_tip));
                    } else if (c == 2) {
                        VisualCallActivity.this.txtTip.setText(LocaleController.getString(R.string.visual_call_blocked_tip));
                    }
                    VisualCallActivity.this.txtTip.setVisibility(0);
                    VisualCallActivity.this.setTipPos();
                    VisualCallActivity.this.txtTip.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallActivity.this.finish();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                }
            }

            public void onSuccess(TLObject object) {
                TLRPCCall.TL_UpdateMeetCallWaiting res = (TLRPCCall.TL_UpdateMeetCallWaiting) object;
                KLog.d("call id === " + res.id);
                String unused = VisualCallActivity.this.mChannel = res.id;
                VisualCallActivity.this.mRtcAuthInfo.data.appid = res.appid;
                VisualCallActivity.this.mRtcAuthInfo.data.token = res.token;
                VisualCallActivity.this.mRtcAuthInfo.data.userid = String.valueOf(AccountInstance.getInstance(UserConfig.selectedAccount).getUserConfig().getCurrentUser().id);
                String[] strArr = new String[res.gslb.size()];
                int i = 0;
                Iterator<String> it = res.gslb.iterator();
                while (it.hasNext()) {
                    strArr[i] = it.next();
                    i++;
                }
                VisualCallActivity.this.mRtcAuthInfo.data.gslb = strArr;
                if (res.data != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(res.data.data);
                        VisualCallActivity.this.mRtcAuthInfo.data.timestamp = jsonObject.getLong("time_stamp");
                        VisualCallActivity.this.mRtcAuthInfo.data.setNonce(jsonObject.getString("nonce"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                VisualCallActivity visualCallActivity = VisualCallActivity.this;
                visualCallActivity.sendKeepLivePacket(visualCallActivity.mChannel);
            }
        });
    }

    private void initRing() {
        SoundPool soundPool2 = new SoundPool(1, 0, 0);
        this.soundPool = soundPool2;
        this.spConnectingId = soundPool2.load(this, R.raw.voip_ringback, 1);
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
                VisualCallActivity.this.lambda$initRing$5$VisualCallActivity(soundPool, i, i2);
            }
        });
    }

    public /* synthetic */ void lambda$initRing$5$VisualCallActivity(SoundPool soundPool2, int sampleId, int status) {
        soundPool2.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
    }

    private void setHeadImage() {
        if (this.miCallReceiverId != -1) {
            TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(this.miCallReceiverId));
            String strName = "";
            if (user != null) {
                strName = user.first_name;
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(user);
            int i = this.callStyle;
            if (i == 2) {
                this.imgVideoUserHead.setRoundRadius(AndroidUtilities.dp(70.0f));
                this.imgVideoUserHead.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
                this.txtVideoName.setText(strName);
            } else if (i == 1) {
                this.imgUserHead.setRoundRadius(AndroidUtilities.dp(70.0f));
                this.imgUserHead.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
                this.txtCallName.setText(strName);
            }
        }
    }

    private void initView() {
        this.mUserListAdapter = new ChartUserAdapter();
        this.chartUserListView.setLayoutManager(new LinearLayoutManager(this, 0, false));
        this.chartUserListView.addItemDecoration(new BaseRecyclerViewAdapter.DividerGridItemDecoration(getResources().getDrawable(R.drawable.chart_content_userlist_item_divider)));
        DefaultItemAnimator anim = new DefaultItemAnimator();
        anim.setSupportsChangeAnimations(false);
        this.chartUserListView.setItemAnimator(anim);
        this.chartUserListView.setAdapter(this.mUserListAdapter);
        this.mUserListAdapter.setOnSubConfigChangeListener(this.mOnSubConfigChangeListener);
        this.chartUserListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            public void onChildViewAttachedToWindow(View view) {
                String access$700 = VisualCallActivity.TAG;
                Log.i(access$700, "onChildViewAttachedToWindow : " + view);
            }

            public void onChildViewDetachedFromWindow(View view) {
                String access$700 = VisualCallActivity.TAG;
                Log.i(access$700, "onChildViewDetachedFromWindow : " + view);
            }
        });
        changeStatusView();
    }

    private void joinChannel() {
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

    private void openJoinChannelBeforeNeedParams() {
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

    /* access modifiers changed from: private */
    public void initRTCEngineAndStartPreview() {
        if (checkPermission("android.permission.CAMERA") || checkPermission("android.permission.RECORD_AUDIO")) {
            setUpSplash();
            this.mGrantPermission = false;
            return;
        }
        this.mGrantPermission = true;
        if (this.mAliRtcEngine == null) {
            AliRtcEngineImpl instance = AliRtcEngine.getInstance(this.mContext.getApplicationContext());
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

    private void initLocalView() {
        this.mLocalView.setZOrderOnTop(false);
        this.mLocalView.setZOrderMediaOverlay(false);
        AliRtcEngine.AliVideoCanvas aliVideoCanvas = new AliRtcEngine.AliVideoCanvas();
        this.aliVideoCanvasBig = aliVideoCanvas;
        aliVideoCanvas.view = this.mLocalView;
        this.aliVideoCanvasBig.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
        this.sfSmallView.setZOrderOnTop(true);
        this.sfSmallView.setZOrderMediaOverlay(true);
        AliRtcEngine.AliVideoCanvas aliVideoCanvas2 = new AliRtcEngine.AliVideoCanvas();
        this.aliVideoCanvasSmall = aliVideoCanvas2;
        aliVideoCanvas2.view = this.sfSmallView;
        this.aliVideoCanvasSmall.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            aliRtcEngine.setLocalViewConfig(this.aliVideoCanvasBig, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
        }
    }

    @OnClick({2131296683, 2131296682, 2131296684, 2131296930, 2131297907, 2131296694, 2131296458})
    public void onclick(View mView) {
        boolean z = true;
        switch (mView.getId()) {
            case R.id.chart_video_container /*2131296458*/:
                if (!this.chart_video_container.isDrag()) {
                    changeLocalPreview((SophonSurfaceView) null);
                    return;
                }
                return;
            case R.id.img_operate_a /*2131296682*/:
                if (this.mAliRtcEngine != null) {
                    if (this.callStyle == 2) {
                        if (this.VisualCallType == 3) {
                            this.callStyle = 1;
                            AVideoCallInterface.ChangeToVoiceCall(this.mChannel, 1 == 2);
                            if (this.mAliRtcEngine.isLocalCameraPublishEnabled()) {
                                KLog.d("--------关闭视频流");
                                this.mAliRtcEngine.configLocalCameraPublish(false);
                                this.mAliRtcEngine.publish();
                            }
                            changeToVoice(true);
                        }
                    } else if (this.mbytLastClickIndex != 0 || System.currentTimeMillis() - this.mlLastClickTime > 500) {
                        this.mlLastClickTime = System.currentTimeMillis();
                        boolean z2 = !this.mIsAudioCapture;
                        this.mIsAudioCapture = z2;
                        if (z2) {
                            this.mAliRtcEngine.muteLocalMic(false);
                            this.img_operate_a.setBackgroundResource(R.drawable.visualcall_no_voice);
                        } else {
                            this.img_operate_a.setBackgroundResource(R.drawable.visualcall_no_voice_selected);
                            this.mAliRtcEngine.muteLocalMic(true);
                        }
                    }
                }
                this.mbytLastClickIndex = 0;
                return;
            case R.id.img_operate_b /*2131296683*/:
                Log.d("------------", "--" + this.mGrantPermission);
                if (this.img_operate_b.isEnabled()) {
                    int i = this.VisualCallType;
                    if (i == 3) {
                        String str = this.mChannel;
                        int currentTimeMillis = ((int) (System.currentTimeMillis() - this.mlStart)) / 1000;
                        if (this.callStyle != 2) {
                            z = false;
                        }
                        AVideoCallInterface.DiscardAVideoCall(str, currentTimeMillis, z);
                        this.txtTip.setText(LocaleController.getString("visual_call_over", R.string.visual_call_over));
                    } else if (i == 1) {
                        this.mBlnReceiveFeedBack = true;
                        String str2 = this.mChannel;
                        byte b = this.REQUEST_CANCEL;
                        if (this.callStyle != 2) {
                            z = false;
                        }
                        AVideoCallInterface.DiscardAVideoCall(str2, b, z);
                        int i2 = this.spConnectingId;
                        if (i2 != 0) {
                            this.soundPool.stop(i2);
                            this.spConnectingId = 0;
                        }
                        this.txtTip.setText(LocaleController.getString("visual_call_cancel", R.string.visual_call_cancel));
                    }
                    setTipPos();
                    this.mChannel = "666";
                    stopRtcAndService();
                    this.img_operate_b.setBackgroundResource(R.drawable.visualcall_cancel);
                    this.img_operate_b.setEnabled(false);
                    this.txtTip.setVisibility(0);
                    this.txtTip.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallActivity.this.finish();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    return;
                }
                return;
            case R.id.img_operate_c /*2131296684*/:
                KLog.d("-------VisualCallType-" + this.VisualCallType + "   callStyle" + this.callStyle);
                if (this.VisualCallType == 3 && this.mAliRtcEngine != null && (this.mbytLastClickIndex != 1 || System.currentTimeMillis() - this.mlLastClickTime > 500)) {
                    this.mlLastClickTime = System.currentTimeMillis();
                    if (this.callStyle == 2) {
                        if (this.mAliRtcEngine.switchCamera() == 0) {
                            KLog.d("----------设置成功");
                            if (this.mAliRtcEngine.getCurrentCameraType().getCameraType() == AliRtcEngine.AliRTCCameraType.AliRTCCameraBack.getCameraType()) {
                                this.img_operate_c.setBackgroundResource(R.drawable.visualcall_camera_changed);
                            } else if (this.mAliRtcEngine.getCurrentCameraType().getCameraType() == AliRtcEngine.AliRTCCameraType.AliRTCCameraFront.getCameraType()) {
                                this.img_operate_c.setBackgroundResource(R.drawable.visualcall_camera);
                            }
                        }
                    } else if (this.mAliRtcEngine.isSpeakerOn()) {
                        this.img_operate_c.setBackgroundResource(R.drawable.visualcall_hands_free);
                        this.mAliRtcEngine.enableSpeakerphone(false);
                    } else {
                        this.img_operate_c.setBackgroundResource(R.drawable.visual_hands_free_selected);
                        this.mAliRtcEngine.enableSpeakerphone(true);
                    }
                }
                this.mbytLastClickIndex = 1;
                return;
            case R.id.img_visualcall /*2131296694*/:
                if (!this.imgVisualcall.isEnabled()) {
                    return;
                }
                if (SettingsCompat.canDrawOverlays(this)) {
                    ApplicationLoader.mbytAVideoCallBusy = 4;
                    startVideoService();
                    return;
                } else if (MryDeviceHelper.isOppo()) {
                    showPermissionErrorAlert(LocaleController.getString("PermissionPopWindowOppo", R.string.PermissionPopWindowOppo));
                    return;
                } else {
                    showPermissionErrorAlert(LocaleController.getString("PermissionPopWindow", R.string.PermissionPopWindow));
                    return;
                }
            case R.id.ll_big_window /*2131296930*/:
                if (this.callStyle != 2 || this.VisualCallType != 3) {
                    return;
                }
                if (this.imgVisualcall.getVisibility() == 8) {
                    this.imgVisualcall.setVisibility(0);
                    this.lin_operate_b.setVisibility(0);
                    this.lin_operate_c.setVisibility(0);
                    this.lin_operate_a.setVisibility(0);
                    this.chrVisualcallTime.setVisibility(0);
                    return;
                }
                this.imgVisualcall.setVisibility(8);
                this.lin_operate_b.setVisibility(8);
                this.lin_operate_c.setVisibility(8);
                this.lin_operate_a.setVisibility(8);
                this.chrVisualcallTime.setVisibility(8);
                return;
            case R.id.txt_pre_change_to_voice /*2131297907*/:
                if (this.mAliRtcEngine != null) {
                    this.callStyle = 1;
                    AVideoCallInterface.ChangeToVoiceCall(this.mChannel, 1 == 2);
                    if (this.mAliRtcEngine.isLocalCameraPublishEnabled()) {
                        this.mAliRtcEngine.configLocalCameraPublish(false);
                        this.mAliRtcEngine.publish();
                    }
                    changeToVoice(true);
                    reInstallTimer();
                    return;
                }
                return;
            default:
                return;
        }
    }

    private boolean checkPermission(String permission) {
        try {
            if (ActivityCompat.checkSelfPermission(this.mContext, permission) != 0) {
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.mbytIsForeground = AndroidUtilities.isAppOnForeground(this) ? (byte) 1 : 0;
        super.onStop();
    }

    private void showPermissionErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(message);
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                VisualCallActivity.this.lambda$showPermissionErrorAlert$6$VisualCallActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    public /* synthetic */ void lambda$showPermissionErrorAlert$6$VisualCallActivity(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void processOccurError(int error) {
        if (error == 16908812 || error == 33620229) {
            noSessionExit(error);
        }
    }

    private void noSessionExit(int error) {
        runOnUiThread(new Runnable() {
            public final void run() {
                VisualCallActivity.this.lambda$noSessionExit$7$VisualCallActivity();
            }
        });
    }

    public /* synthetic */ void lambda$noSessionExit$7$VisualCallActivity() {
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            aliRtcEngine.leaveChannel();
            this.mAliRtcEngine.destroy();
            this.mAliRtcEngine = null;
        }
        AliRtcEngineImpl instance = AliRtcEngine.getInstance(this.mContext.getApplicationContext());
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

    public void onBackPressed() {
        XDialog.Builder builder = new XDialog.Builder(this);
        builder.setTitle(LocaleController.getString("Tips", R.string.Tips));
        builder.setMessage(LocaleController.getString(R.string.visual_call_exit_ask));
        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                VisualCallActivity.this.lambda$onBackPressed$8$VisualCallActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    public /* synthetic */ void lambda$onBackPressed$8$VisualCallActivity(DialogInterface dialogInterface, int i) {
        boolean z = true;
        if (this.VisualCallType == 3) {
            String str = this.mChannel;
            int currentTimeMillis = ((int) (System.currentTimeMillis() - this.mlStart)) / 1000;
            if (this.callStyle != 2) {
                z = false;
            }
            AVideoCallInterface.DiscardAVideoCall(str, currentTimeMillis, z);
        } else {
            String str2 = this.mChannel;
            byte b = this.REQUEST_CANCEL;
            if (this.callStyle != 2) {
                z = false;
            }
            AVideoCallInterface.DiscardAVideoCall(str2, b, z);
        }
        stopRtcAndService();
        super.onBackPressed();
    }

    /* access modifiers changed from: private */
    public void updateRemoteDisplay(final String uid, AliRtcEngine.AliRtcAudioTrack at, final AliRtcEngine.AliRtcVideoTrack vt) {
        runOnUiThread(new Runnable() {
            public void run() {
                AliRtcEngine.AliVideoCanvas screenCanvas;
                AliRtcEngine.AliVideoCanvas cameraCanvas;
                if (VisualCallActivity.this.mAliRtcEngine != null) {
                    AliRtcRemoteUserInfo remoteUserInfo = VisualCallActivity.this.mAliRtcEngine.getUserInfo(uid);
                    if (remoteUserInfo == null) {
                        Log.e("视频", "updateRemoteDisplay remoteUserInfo = null, uid = " + uid);
                        return;
                    }
                    AliRtcEngine.AliVideoCanvas cameraCanvas2 = remoteUserInfo.getCameraCanvas();
                    AliRtcEngine.AliVideoCanvas screenCanvas2 = remoteUserInfo.getScreenCanvas();
                    if (vt == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo) {
                        cameraCanvas = null;
                        screenCanvas = null;
                    } else if (vt == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera) {
                        screenCanvas = null;
                        cameraCanvas = VisualCallActivity.this.createCanvasIfNull(cameraCanvas2);
                        VisualCallActivity.this.mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
                    } else if (vt == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen) {
                        cameraCanvas = null;
                        screenCanvas = VisualCallActivity.this.createCanvasIfNull(screenCanvas2);
                        VisualCallActivity.this.mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen);
                    } else if (vt == AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth) {
                        cameraCanvas = VisualCallActivity.this.createCanvasIfNull(cameraCanvas2);
                        VisualCallActivity.this.mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
                        screenCanvas = VisualCallActivity.this.createCanvasIfNull(screenCanvas2);
                        VisualCallActivity.this.mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen);
                    } else {
                        return;
                    }
                    ChartUserBean chartUserBean = VisualCallActivity.this.convertRemoteUserInfo(remoteUserInfo, cameraCanvas, screenCanvas);
                    if (chartUserBean.mCameraSurface != null) {
                        KLog.d("---------mScreenSurface");
                        ViewParent parent = chartUserBean.mCameraSurface.getParent();
                        if (parent != null && (parent instanceof FrameLayout)) {
                            ((FrameLayout) parent).removeAllViews();
                        }
                        if (VisualCallActivity.this.callStyle == 2) {
                            VisualCallActivity.this.changeLocalPreview(chartUserBean.mCameraSurface);
                        }
                    }
                }
            }
        });
    }

    private void createLocalVideoView(ViewGroup v) {
        v.removeAllViews();
        SophonSurfaceView surfaceView1 = new SophonSurfaceView(this);
        surfaceView1.setZOrderOnTop(true);
        surfaceView1.setZOrderMediaOverlay(true);
        AliRtcEngine.AliVideoCanvas aliVideoCanvas = new AliRtcEngine.AliVideoCanvas();
        v.addView(surfaceView1, new ViewGroup.LayoutParams(-1, -1));
        aliVideoCanvas.view = surfaceView1;
        aliVideoCanvas.renderMode = AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            aliRtcEngine.stopPreview();
            this.mAliRtcEngine.setLocalViewConfig(aliVideoCanvas, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
            this.mAliRtcEngine.startPreview();
        }
        v.getChildAt(0).setVisibility(0);
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
            this.mLocalView.setVisibility(8);
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
        this.mLocalView.setVisibility(0);
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
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            aliRtcEngine.setLocalViewConfig(this.aliVideoCanvasBig, AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera);
            this.mAliRtcEngine.startPreview();
        }
        this.mbytLocalPos = 0;
    }

    /* access modifiers changed from: private */
    public ChartUserBean convertRemoteUserInfo(AliRtcRemoteUserInfo remoteUserInfo, AliRtcEngine.AliVideoCanvas cameraCanvas, AliRtcEngine.AliVideoCanvas screenCanvas) {
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

    /* access modifiers changed from: private */
    public AliRtcEngine.AliVideoCanvas createCanvasIfNull(AliRtcEngine.AliVideoCanvas canvas) {
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
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.receivedAVideoCallReady);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reecivedAVideoDiscarded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.receivedAVideoCallAccept);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.receivedAVideoCallBusy);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.receivedAVideoCallChangeVoice);
        CallNetWorkReceiver callNetWorkReceiver2 = this.callNetWorkReceiver;
        if (callNetWorkReceiver2 != null) {
            unregisterReceiver(callNetWorkReceiver2);
        }
        SoundPool soundPool2 = this.soundPool;
        if (soundPool2 != null) {
            soundPool2.release();
        }
        DynamicPoint dynamicPoint2 = this.dynamicPoint;
        if (dynamicPoint2 != null) {
            dynamicPoint2.release();
        }
        this.timer.cancel();
        this.timer.purge();
        TimerTask timerTask2 = this.timerTask;
        if (timerTask2 != null) {
            timerTask2.cancel();
        }
    }

    public void setUpSplash() {
        ThreadUtils.runOnUiThread(new Runnable() {
            public final void run() {
                VisualCallActivity.this.requestPermission();
            }
        }, 1000);
    }

    /* access modifiers changed from: private */
    public void requestPermission() {
        PermissionUtils.requestMultiPermissions(this, new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO", PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, this.mGrant);
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
                    VisualCallActivity.this.requestPermission();
                }
            }, 500);
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
    public void onRestart() {
        super.onRestart();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        KLog.d("--------------resume------------");
        if (this.mblnUnProcessChooseVoiceTip) {
            this.txtTip.setText(LocaleController.getString(R.string.visual_call_receive_to_voice));
            setTipPos();
            this.txtTip.setVisibility(0);
            this.txtTip.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallActivity.this.lambda$onResume$9$VisualCallActivity();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            this.mblnUnProcessChooseVoiceTip = false;
        }
        FlowService flowService = this.myservice;
        if (flowService != null && this.mbytIsForeground == 1) {
            View videoView = flowService.getViewBig(false);
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
                    this.chart_video_container.addView(smallView, new ViewGroup.LayoutParams(-1, -1));
                }
                this.chart_video_container.setVisibility(0);
            }
        }
        if (this.misConnect) {
            unbindService(this.mVideoServiceConnection);
            this.misConnect = false;
        }
        this.mbytIsForeground = 1;
    }

    public /* synthetic */ void lambda$onResume$9$VisualCallActivity() {
        this.txtTip.setVisibility(8);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPCCall.TL_UpdateMeetChangeCall changeCall;
        if (id == NotificationCenter.receivedAVideoCallReady) {
            TLRPCCall.TL_UpdateMeetCall meetCall = args[0];
            if (meetCall != null && meetCall.id.equals(this.mChannel)) {
                this.mblnOtherIsPc = meetCall.isPc;
            }
        } else if (id == NotificationCenter.reecivedAVideoDiscarded) {
            TLRPCCall.TL_UpdateMeetCallDiscarded discarded = args[0];
            if (discarded != null && discarded.id.equals(this.mChannel)) {
                this.imgVisualcall.setEnabled(false);
                stopRtcAndService();
                int i = this.spConnectingId;
                if (i != 0) {
                    this.soundPool.stop(i);
                    this.spConnectingId = 0;
                }
                if (this.VisualCallType == 3) {
                    this.txtTip.setText(LocaleController.getString("visual_call_other_side_discard", R.string.visual_call_other_side_discard));
                    this.txtTip.setVisibility(0);
                    this.chrVisualcallTime.stop();
                    this.txtTip.postDelayed(new Runnable() {
                        public final void run() {
                            VisualCallActivity.this.finish();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    return;
                }
                if (this.mBlnReceiveFeedBack) {
                    this.txtTip.setText(LocaleController.getString("visual_call_other_side_discard", R.string.visual_call_other_side_discard));
                } else {
                    this.txtTip.setText(LocaleController.getString("visual_call_other_side_refuse", R.string.visual_call_other_side_refuse));
                }
                this.txtTip.setVisibility(0);
                setTipPos();
                this.mBlnReceiveFeedBack = true;
                this.txtTip.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallActivity.this.finish();
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        } else if (id == NotificationCenter.receivedAVideoCallAccept) {
            TLRPCCall.TL_UpdateMeetCallAccepted callAccepted = args[0];
            if (callAccepted != null && callAccepted.id.equals(this.mChannel)) {
                this.txtTip.setVisibility(8);
                this.mBlnReceiveFeedBack = true;
                if (this.callStyle == 2) {
                    this.dynamicPoint.animForWaitting(LocaleController.getString("visual_call_calling", R.string.visual_call_calling), this.txtVideoStatus);
                } else {
                    this.dynamicPoint.animForWaitting(LocaleController.getString("visual_call_calling", R.string.visual_call_calling), this.txtCallStatus);
                }
                int i2 = this.spConnectingId;
                if (i2 != 0) {
                    this.soundPool.stop(i2);
                    this.spConnectingId = 0;
                }
                openJoinChannelBeforeNeedParams();
                if (this.mGrantPermission) {
                    joinChannel();
                } else {
                    setUpSplash();
                }
                AVideoCallInterface.ConfirmCall(args[0].id, 0, new AVideoCallInterface.AVideoRequestCallBack() {
                    public void onError(TLRPC.TL_error error) {
                    }

                    public void onSuccess(TLObject object) {
                        if (object instanceof TLRPCCall.TL_UpdateMeetCall) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.receivedAVideoCallReady, TLObject.this);
                                }
                            });
                        }
                    }
                });
                this.rel_video_user.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallActivity.this.lambda$didReceivedNotification$10$VisualCallActivity();
                    }
                }, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
            }
        } else if (id == NotificationCenter.receivedAVideoCallBusy) {
            TLRPCCall.TL_UpdateMeetCallWaiting callWaiting = args[0];
            if (callWaiting != null && callWaiting.id.equals(this.mChannel)) {
                AVideoCallInterface.DiscardAVideoCall(this.mChannel, this.VISUAL_CALL_BUSY, this.callStyle == 2);
                stopRtcAndService();
                int i3 = this.spConnectingId;
                if (i3 != 0) {
                    this.soundPool.stop(i3);
                    this.spConnectingId = 0;
                }
                this.mBlnReceiveFeedBack = true;
                this.txtTip.setText(LocaleController.getString("visual_call_other_busing", R.string.visual_call_other_busing));
                this.txtTip.setVisibility(0);
                setTipPos();
                this.txtTip.postDelayed(new Runnable() {
                    public final void run() {
                        VisualCallActivity.this.finish();
                    }
                }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        } else if (id == NotificationCenter.receivedAVideoCallChangeVoice && (changeCall = args[0]) != null && changeCall.id.equals(this.mChannel)) {
            this.callStyle = 1;
            changeToVoice(false);
            if (!this.mBlnReceiveFeedBack) {
                reInstallTimer();
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$10$VisualCallActivity() {
        if (this.VisualCallType != 3) {
            this.txtTip.setVisibility(0);
            this.txtTip.setText(LocaleController.getString("visual_call_retry", R.string.visual_call_retry));
            setTipPos();
            stopRtcAndService();
            this.txtTip.postDelayed(new Runnable() {
                public final void run() {
                    VisualCallActivity.this.finish();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    private void regNotification() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.receivedAVideoCallReady);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reecivedAVideoDiscarded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.receivedAVideoCallAccept);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.receivedAVideoCallBusy);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.receivedAVideoCallChangeVoice);
    }

    private void setFullScreen() {
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

    /* access modifiers changed from: private */
    public void stopRtcAndService() {
        if (ApplicationLoader.mbytAVideoCallBusy != 0) {
            ApplicationLoader.mbytAVideoCallBusy = 0;
            if (this.mForeServiceIntent != null && AppUtils.isServiceRunning(getApplicationContext(), ForegroundService.class.getName())) {
                stopService(this.mForeServiceIntent);
            }
            if (this.mAliRtcEngine != null) {
                new Thread(new Runnable() {
                    public final void run() {
                        VisualCallActivity.this.lambda$stopRtcAndService$11$VisualCallActivity();
                    }
                }).start();
            }
            ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(true, false);
        }
    }

    public /* synthetic */ void lambda$stopRtcAndService$11$VisualCallActivity() {
        this.mAliRtcEngine.setRtcEngineNotify((AliRtcEngineNotify) null);
        this.mAliRtcEngine.setRtcEngineEventListener((AliRtcEngineEventListener) null);
        if (this.callStyle == 2) {
            this.mAliRtcEngine.stopPreview();
        }
        this.mAliRtcEngine.leaveChannel();
        this.mAliRtcEngine.destroy();
        this.mAliRtcEngine = null;
    }

    /* access modifiers changed from: private */
    public void setTipPos() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.txtTip.getLayoutParams();
        if (this.rel_voice_user.getVisibility() == 8) {
            this.txtTip.setGravity(17);
            return;
        }
        layoutParams.addRule(3, R.id.rel_voice_user);
        layoutParams.topMargin = AndroidUtilities.dp(25.0f);
    }

    /* access modifiers changed from: private */
    public void changeToVoice(boolean blnCaller) {
        AliRtcEngine aliRtcEngine = this.mAliRtcEngine;
        if (aliRtcEngine != null) {
            if (aliRtcEngine.isSpeakerOn()) {
                this.mAliRtcEngine.enableSpeakerphone(false);
            }
            this.mAliRtcEngine.stopPreview();
        }
        changeStatusView();
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
                    VisualCallActivity.this.lambda$changeToVoice$12$VisualCallActivity();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
        setHeadImage();
        if (this.VisualCallType == 3) {
            this.llBigWindow.setVisibility(8);
            this.chart_video_container.setVisibility(8);
        } else if (!this.mBlnReceiveFeedBack) {
            this.dynamicPoint.animForWaitting(LocaleController.getString(R.string.visual_call_waiting), this.txtCallStatus);
        } else {
            this.dynamicPoint.animForWaitting(LocaleController.getString(R.string.visual_call_calling), this.txtCallStatus);
        }
    }

    public /* synthetic */ void lambda$changeToVoice$12$VisualCallActivity() {
        this.txtTip.setVisibility(8);
    }

    private void reInstallTimer() {
        this.mblnResetNoAnswer = true;
        this.chart_video_container.postDelayed(new Runnable() {
            public final void run() {
                VisualCallActivity.this.lambda$reInstallTimer$13$VisualCallActivity();
            }
        }, 35000);
        this.img_operate_a.postDelayed(new Runnable() {
            public final void run() {
                VisualCallActivity.this.lambda$reInstallTimer$14$VisualCallActivity();
            }
        }, 15000);
    }

    public /* synthetic */ void lambda$reInstallTimer$13$VisualCallActivity() {
        if (!this.mBlnReceiveFeedBack) {
            processNoAnswer();
        }
    }

    public /* synthetic */ void lambda$reInstallTimer$14$VisualCallActivity() {
        if (!this.mBlnReceiveFeedBack) {
            processNoAnswerTip();
        }
    }
}

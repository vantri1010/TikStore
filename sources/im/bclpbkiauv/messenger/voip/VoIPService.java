package im.bclpbkiauv.messenger.voip;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.XiaomiUtilities;
import im.bclpbkiauv.messenger.voip.VoIPBaseService;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.VoIPActivity;
import im.bclpbkiauv.ui.VoIPFeedbackActivity;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.ByteCompanionObject;

public class VoIPService extends VoIPBaseService {
    public static final int CALL_MAX_LAYER = VoIPController.getConnectionMaxLayer();
    public static final int CALL_MIN_LAYER = 65;
    public static final int STATE_BUSY = 17;
    public static final int STATE_EXCHANGING_KEYS = 12;
    public static final int STATE_HANGING_UP = 10;
    public static final int STATE_REQUESTING = 14;
    public static final int STATE_RINGING = 16;
    public static final int STATE_WAITING = 13;
    public static final int STATE_WAITING_INCOMING = 15;
    public static TLRPC.PhoneCall callIShouldHavePutIntoIntent;
    /* access modifiers changed from: private */
    public byte[] a_or_b;
    private byte[] authKey;
    /* access modifiers changed from: private */
    public TLRPC.PhoneCall call;
    /* access modifiers changed from: private */
    public int callReqId;
    private String debugLog;
    /* access modifiers changed from: private */
    public Runnable delayedStartOutgoingCall;
    /* access modifiers changed from: private */
    public boolean endCallAfterRequest = false;
    private boolean forceRating;
    /* access modifiers changed from: private */
    public byte[] g_a;
    /* access modifiers changed from: private */
    public byte[] g_a_hash;
    private byte[] groupCallEncryptionKey;
    private long groupCallKeyFingerprint;
    private List<Integer> groupUsersToAdd = new ArrayList();
    private boolean joiningGroupCall;
    private long keyFingerprint;
    private boolean needSendDebugLog = false;
    private int peerCapabilities;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.PhoneCall> pendingUpdates = new ArrayList<>();
    private boolean startedRinging = false;
    private boolean upgrading;
    /* access modifiers changed from: private */
    public TLRPC.User user;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (sharedInstance != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Tried to start the VoIP service when it's already started");
            }
            return 2;
        }
        this.currentAccount = intent.getIntExtra("account", -1);
        if (this.currentAccount != -1) {
            int userID = intent.getIntExtra("user_id", 0);
            this.isOutgoing = intent.getBooleanExtra("is_outgoing", false);
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userID));
            this.user = user2;
            if (user2 == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("VoIPService: user==null");
                }
                stopSelf();
                return 2;
            }
            sharedInstance = this;
            if (this.isOutgoing) {
                dispatchStateChanged(14);
                if (USE_CONNECTION_SERVICE) {
                    Bundle extras = new Bundle();
                    Bundle myExtras = new Bundle();
                    extras.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", addAccountToTelecomManager());
                    myExtras.putInt("call_type", 1);
                    extras.putBundle("android.telecom.extra.OUTGOING_CALL_EXTRAS", myExtras);
                    ContactsController.getInstance(this.currentAccount).createOrUpdateConnectionServiceContact(this.user.id, this.user.first_name, this.user.last_name);
                    ((TelecomManager) getSystemService("telecom")).placeCall(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), extras);
                } else {
                    AnonymousClass1 r3 = new Runnable() {
                        public void run() {
                            Runnable unused = VoIPService.this.delayedStartOutgoingCall = null;
                            VoIPService.this.startOutgoingCall();
                        }
                    };
                    this.delayedStartOutgoingCall = r3;
                    AndroidUtilities.runOnUIThread(r3, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                }
                if (intent.getBooleanExtra("start_incall_activity", false)) {
                    startActivity(new Intent(this, VoIPActivity.class).addFlags(C.ENCODING_PCM_MU_LAW));
                }
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeInCallActivity, new Object[0]);
                this.call = callIShouldHavePutIntoIntent;
                callIShouldHavePutIntoIntent = null;
                if (USE_CONNECTION_SERVICE) {
                    acknowledgeCall(false);
                    showNotification();
                } else {
                    acknowledgeCall(true);
                }
            }
            initializeAccountRelatedThings();
            return 2;
        }
        throw new IllegalStateException("No account specified when starting VoIP service");
    }

    public void onCreate() {
        super.onCreate();
        if (callIShouldHavePutIntoIntent != null && Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            startForeground(201, new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setSmallIcon(R.mipmap.notification).setContentTitle(LocaleController.getString("VoipOutgoingCall", R.string.VoipOutgoingCall)).setShowWhen(false).build());
        }
    }

    /* access modifiers changed from: protected */
    public void updateServerConfig() {
        final SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        VoIPServerConfig.setConfig(preferences.getString("voip_server_config", "{}"));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_phone_getCallConfig(), new RequestDelegate() {
            public void run(TLObject response, TLRPC.TL_error error) {
                if (error == null) {
                    String data = ((TLRPC.TL_dataJSON) response).data;
                    VoIPServerConfig.setConfig(data);
                    preferences.edit().putString("voip_server_config", data).commit();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onControllerPreRelease() {
        if (this.debugLog == null) {
            this.debugLog = this.controller.getDebugLog();
        }
    }

    public static VoIPService getSharedInstance() {
        if (sharedInstance instanceof VoIPService) {
            return (VoIPService) sharedInstance;
        }
        return null;
    }

    public TLRPC.User getUser() {
        return this.user;
    }

    public void hangUp() {
        declineIncomingCall((this.currentState == 16 || (this.currentState == 13 && this.isOutgoing)) ? 3 : 1, (Runnable) null);
    }

    public void hangUp(Runnable onDone) {
        declineIncomingCall((this.currentState == 16 || (this.currentState == 13 && this.isOutgoing)) ? 3 : 1, onDone);
    }

    /* access modifiers changed from: private */
    public void startOutgoingCall() {
        if (USE_CONNECTION_SERVICE && this.systemCallConnection != null) {
            this.systemCallConnection.setDialing();
        }
        configureDeviceForCall();
        showNotification();
        startConnectingSound();
        dispatchStateChanged(14);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
            }
        });
        Utilities.random.nextBytes(new byte[256]);
        TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
        req.random_length = 256;
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        req.version = messagesStorage.getLastSecretVersion();
        this.callReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TLRPC.TL_error error) {
                int unused = VoIPService.this.callReqId = 0;
                if (VoIPService.this.endCallAfterRequest) {
                    VoIPService.this.callEnded();
                } else if (error == null) {
                    TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) response;
                    if (response instanceof TLRPC.TL_messages_dhConfig) {
                        if (!Utilities.isGoodPrime(res.p, res.g)) {
                            VoIPService.this.callFailed();
                            return;
                        }
                        messagesStorage.setSecretPBytes(res.p);
                        messagesStorage.setSecretG(res.g);
                        messagesStorage.setLastSecretVersion(res.version);
                        MessagesStorage messagesStorage = messagesStorage;
                        messagesStorage.saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
                    }
                    final byte[] salt = new byte[256];
                    for (int a = 0; a < 256; a++) {
                        salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                    }
                    byte[] g_a = BigInteger.valueOf((long) messagesStorage.getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, messagesStorage.getSecretPBytes())).toByteArray();
                    if (g_a.length > 256) {
                        byte[] correctedAuth = new byte[256];
                        System.arraycopy(g_a, 1, correctedAuth, 0, 256);
                        g_a = correctedAuth;
                    }
                    TLRPC.TL_phone_requestCall reqCall = new TLRPC.TL_phone_requestCall();
                    reqCall.user_id = MessagesController.getInstance(VoIPService.this.currentAccount).getInputUser(VoIPService.this.user);
                    reqCall.protocol = new TLRPC.TL_phoneCallProtocol();
                    reqCall.protocol.udp_p2p = true;
                    reqCall.protocol.udp_reflector = true;
                    reqCall.protocol.min_layer = 65;
                    reqCall.protocol.max_layer = VoIPService.CALL_MAX_LAYER;
                    byte[] unused2 = VoIPService.this.g_a = g_a;
                    reqCall.g_a_hash = Utilities.computeSHA256(g_a, 0, g_a.length);
                    reqCall.random_id = Utilities.random.nextInt();
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(reqCall, new RequestDelegate() {
                        public void run(final TLObject response, final TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TLRPC.TL_error tL_error = error;
                                    if (tL_error == null) {
                                        TLRPC.PhoneCall unused = VoIPService.this.call = ((TLRPC.TL_phone_phoneCall) response).phone_call;
                                        byte[] unused2 = VoIPService.this.a_or_b = salt;
                                        VoIPService.this.dispatchStateChanged(13);
                                        if (VoIPService.this.endCallAfterRequest) {
                                            VoIPService.this.hangUp();
                                            return;
                                        }
                                        if (VoIPService.this.pendingUpdates.size() > 0 && VoIPService.this.call != null) {
                                            Iterator it = VoIPService.this.pendingUpdates.iterator();
                                            while (it.hasNext()) {
                                                VoIPService.this.onCallUpdated((TLRPC.PhoneCall) it.next());
                                            }
                                            VoIPService.this.pendingUpdates.clear();
                                        }
                                        VoIPService.this.timeoutRunnable = new Runnable() {
                                            public void run() {
                                                VoIPService.this.timeoutRunnable = null;
                                                TLRPC.TL_phone_discardCall req = new TLRPC.TL_phone_discardCall();
                                                req.peer = new TLRPC.TL_inputPhoneCall();
                                                req.peer.access_hash = VoIPService.this.call.access_hash;
                                                req.peer.id = VoIPService.this.call.id;
                                                req.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
                                                ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                                    public void run(TLObject response, TLRPC.TL_error error) {
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            if (error != null) {
                                                                FileLog.e("error on phone.discardCall: " + error);
                                                            } else {
                                                                FileLog.d("phone.discardCall " + response);
                                                            }
                                                        }
                                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                                            public void run() {
                                                                VoIPService.this.callFailed();
                                                            }
                                                        });
                                                    }
                                                }, 2);
                                            }
                                        };
                                        AndroidUtilities.runOnUIThread(VoIPService.this.timeoutRunnable, (long) MessagesController.getInstance(VoIPService.this.currentAccount).callReceiveTimeout);
                                    } else if (tL_error.code == 400 && "PARTICIPANT_VERSION_OUTDATED".equals(error.text)) {
                                        VoIPService.this.callFailed(-1);
                                    } else if (error.code == 403) {
                                        VoIPService.this.callFailed(-2);
                                    } else if (error.code == 406) {
                                        VoIPService.this.callFailed(-3);
                                    } else {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.e("Error on phone.requestCall: " + error);
                                        }
                                        VoIPService.this.callFailed();
                                    }
                                }
                            });
                        }
                    }, 2);
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("Error on getDhConfig " + error);
                    }
                    VoIPService.this.callFailed();
                }
            }
        }, 2);
    }

    private void acknowledgeCall(final boolean startRinging) {
        if (this.call instanceof TLRPC.TL_phoneCallDiscarded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("Call " + this.call.id + " was discarded before the service started, stopping");
            }
            stopSelf();
        } else if (Build.VERSION.SDK_INT < 19 || !XiaomiUtilities.isMIUI() || XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_SHOW_WHEN_LOCKED) || !((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            TLRPC.TL_phone_receivedCall req = new TLRPC.TL_phone_receivedCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.id = this.call.id;
            req.peer.access_hash = this.call.access_hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TLRPC.TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (VoIPBaseService.sharedInstance != null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.w("receivedCall response = " + response);
                                }
                                if (error != null) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("error on receivedCall: " + error);
                                    }
                                    VoIPService.this.stopSelf();
                                    return;
                                }
                                if (VoIPBaseService.USE_CONNECTION_SERVICE) {
                                    ContactsController.getInstance(VoIPService.this.currentAccount).createOrUpdateConnectionServiceContact(VoIPService.this.user.id, VoIPService.this.user.first_name, VoIPService.this.user.last_name);
                                    Bundle extras = new Bundle();
                                    extras.putInt("call_type", 1);
                                    ((TelecomManager) VoIPService.this.getSystemService("telecom")).addNewIncomingCall(VoIPService.this.addAccountToTelecomManager(), extras);
                                }
                                if (startRinging) {
                                    VoIPService.this.startRinging();
                                }
                            }
                        }
                    });
                }
            }, 2);
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("MIUI: no permission to show when locked but the screen is locked. ¯\\_(ツ)_/¯");
            }
            stopSelf();
        }
    }

    /* access modifiers changed from: protected */
    public void startRinging() {
        if (this.currentState != 15) {
            if (USE_CONNECTION_SERVICE && this.systemCallConnection != null) {
                this.systemCallConnection.setRinging();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("starting ringing for call " + this.call.id);
            }
            dispatchStateChanged(15);
            if (Build.VERSION.SDK_INT >= 21) {
                showIncomingNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), (CharSequence) null, this.user, (List<TLRPC.User>) null, 0, VoIPActivity.class);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Showing incoming call notification");
                    return;
                }
                return;
            }
            startRingtoneAndVibration(this.user.id);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Starting incall activity for incoming call");
            }
            try {
                PendingIntent.getActivity(this, 12345, new Intent(this, VoIPActivity.class).addFlags(C.ENCODING_PCM_MU_LAW), 0).send();
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting incall activity", (Throwable) x);
                }
            }
        }
    }

    public void startRingtoneAndVibration() {
        if (!this.startedRinging) {
            startRingtoneAndVibration(this.user.id);
            this.startedRinging = true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isRinging() {
        return this.currentState == 15;
    }

    public void acceptIncomingCall() {
        stopRinging();
        showNotification();
        configureDeviceForCall();
        startConnectingSound();
        dispatchStateChanged(12);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
            }
        });
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = messagesStorage.getLastSecretVersion();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TLRPC.TL_error error) {
                if (error == null) {
                    TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) response;
                    if (response instanceof TLRPC.TL_messages_dhConfig) {
                        if (!Utilities.isGoodPrime(res.p, res.g)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("stopping VoIP service, bad prime");
                            }
                            VoIPService.this.callFailed();
                            return;
                        }
                        messagesStorage.setSecretPBytes(res.p);
                        messagesStorage.setSecretG(res.g);
                        messagesStorage.setLastSecretVersion(res.version);
                        MessagesStorage.getInstance(VoIPService.this.currentAccount).saveSecretParams(messagesStorage.getLastSecretVersion(), messagesStorage.getSecretG(), messagesStorage.getSecretPBytes());
                    }
                    byte[] salt = new byte[256];
                    for (int a = 0; a < 256; a++) {
                        salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                    }
                    if (VoIPService.this.call == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("call is null");
                        }
                        VoIPService.this.callFailed();
                        return;
                    }
                    byte[] unused = VoIPService.this.a_or_b = salt;
                    BigInteger g_b = BigInteger.valueOf((long) messagesStorage.getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, messagesStorage.getSecretPBytes()));
                    VoIPService voIPService = VoIPService.this;
                    byte[] unused2 = voIPService.g_a_hash = voIPService.call.g_a_hash;
                    byte[] g_b_bytes = g_b.toByteArray();
                    if (g_b_bytes.length > 256) {
                        byte[] correctedAuth = new byte[256];
                        System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                        g_b_bytes = correctedAuth;
                    }
                    TLRPC.TL_phone_acceptCall req = new TLRPC.TL_phone_acceptCall();
                    req.g_b = g_b_bytes;
                    req.peer = new TLRPC.TL_inputPhoneCall();
                    req.peer.id = VoIPService.this.call.id;
                    req.peer.access_hash = VoIPService.this.call.access_hash;
                    req.protocol = new TLRPC.TL_phoneCallProtocol();
                    TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = req.protocol;
                    req.protocol.udp_reflector = true;
                    tL_phoneCallProtocol.udp_p2p = true;
                    req.protocol.min_layer = 65;
                    req.protocol.max_layer = VoIPService.CALL_MAX_LAYER;
                    ConnectionsManager.getInstance(VoIPService.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TLRPC.TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (error == null) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.w("accept call ok! " + response);
                                        }
                                        TLRPC.PhoneCall unused = VoIPService.this.call = ((TLRPC.TL_phone_phoneCall) response).phone_call;
                                        if (VoIPService.this.call instanceof TLRPC.TL_phoneCallDiscarded) {
                                            VoIPService.this.onCallUpdated(VoIPService.this.call);
                                            return;
                                        }
                                        return;
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("Error on phone.acceptCall: " + error);
                                    }
                                    VoIPService.this.callFailed();
                                }
                            });
                        }
                    }, 2);
                    return;
                }
                VoIPService.this.callFailed();
            }
        });
    }

    public void declineIncomingCall() {
        declineIncomingCall(1, (Runnable) null);
    }

    /* access modifiers changed from: protected */
    public Class<? extends Activity> getUIActivityClass() {
        return VoIPActivity.class;
    }

    public void declineIncomingCall(int reason, final Runnable onDone) {
        final Runnable stopper;
        stopRinging();
        this.callDiscardReason = reason;
        final boolean wasNotConnected = true;
        if (this.currentState == 14) {
            Runnable runnable = this.delayedStartOutgoingCall;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                callEnded();
                return;
            }
            dispatchStateChanged(10);
            this.endCallAfterRequest = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (VoIPService.this.currentState == 10) {
                        VoIPService.this.callEnded();
                    }
                }
            }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } else if (this.currentState != 10 && this.currentState != 11) {
            dispatchStateChanged(10);
            if (this.call == null) {
                if (onDone != null) {
                    onDone.run();
                }
                callEnded();
                if (this.callReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.callReqId, false);
                    this.callReqId = 0;
                    return;
                }
                return;
            }
            TLRPC.TL_phone_discardCall req = new TLRPC.TL_phone_discardCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.access_hash = this.call.access_hash;
            req.peer.id = this.call.id;
            req.duration = (this.controller == null || !this.controllerStarted) ? 0 : (int) (this.controller.getCallDuration() / 1000);
            req.connection_id = (this.controller == null || !this.controllerStarted) ? 0 : this.controller.getPreferredRelayID();
            if (reason == 2) {
                req.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
            } else if (reason == 3) {
                req.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
            } else if (reason != 4) {
                req.reason = new TLRPC.TL_phoneCallDiscardReasonHangup();
            } else {
                req.reason = new TLRPC.TL_phoneCallDiscardReasonBusy();
            }
            if (ConnectionsManager.getInstance(this.currentAccount).getConnectionState() == 3) {
                wasNotConnected = false;
            }
            if (wasNotConnected) {
                if (onDone != null) {
                    onDone.run();
                }
                callEnded();
                stopper = null;
            } else {
                stopper = new Runnable() {
                    private boolean done = false;

                    public void run() {
                        if (!this.done) {
                            this.done = true;
                            Runnable runnable = onDone;
                            if (runnable != null) {
                                runnable.run();
                            }
                            VoIPService.this.callEnded();
                        }
                    }
                };
                AndroidUtilities.runOnUIThread(stopper, (long) ((int) (VoIPServerConfig.getDouble("hangup_ui_timeout", 5.0d) * 1000.0d)));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TLRPC.TL_error error) {
                    if (error == null) {
                        if (response instanceof TLRPC.TL_updates) {
                            MessagesController.getInstance(VoIPService.this.currentAccount).processUpdates((TLRPC.TL_updates) response, false);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("phone.discardCall " + response);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("error on phone.discardCall: " + error);
                    }
                    if (!wasNotConnected) {
                        AndroidUtilities.cancelRunOnUIThread(stopper);
                        Runnable runnable = onDone;
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                }
            }, 2);
        }
    }

    private void dumpCallObject() {
        try {
            if (BuildVars.LOGS_ENABLED) {
                for (Field f : TLRPC.PhoneCall.class.getFields()) {
                    FileLog.d(f.getName() + " = " + f.get(this.call));
                }
            }
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e((Throwable) x);
            }
        }
    }

    public void onCallUpdated(TLRPC.PhoneCall call2) {
        if (this.call == null) {
            this.pendingUpdates.add(call2);
        } else if (call2 != null) {
            if (call2.id == this.call.id) {
                if (call2.access_hash == 0) {
                    call2.access_hash = this.call.access_hash;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("Call updated: " + call2);
                    dumpCallObject();
                }
                this.call = call2;
                if (call2 instanceof TLRPC.TL_phoneCallDiscarded) {
                    this.needSendDebugLog = call2.need_debug;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("call discarded, stopping service");
                    }
                    if (call2.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
                        dispatchStateChanged(17);
                        this.playingSound = true;
                        this.soundPool.play(this.spBusyId, 1.0f, 1.0f, 0, -1, 1.0f);
                        AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1500);
                        endConnectionServiceCall(1500);
                        stopSelf();
                    } else {
                        callEnded();
                    }
                    if (call2.need_rating || this.forceRating || (this.controller != null && VoIPServerConfig.getBoolean("bad_call_rating", true) && this.controller.needRate())) {
                        startRatingActivity();
                    }
                    if (this.debugLog == null && this.controller != null) {
                        this.debugLog = this.controller.getDebugLog();
                    }
                    if (this.needSendDebugLog && this.debugLog != null) {
                        TLRPC.TL_phone_saveCallDebug req = new TLRPC.TL_phone_saveCallDebug();
                        req.debug = new TLRPC.TL_dataJSON();
                        req.debug.data = this.debugLog;
                        req.peer = new TLRPC.TL_inputPhoneCall();
                        req.peer.access_hash = call2.access_hash;
                        req.peer.id = call2.id;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                            public void run(TLObject response, TLRPC.TL_error error) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("Sent debug logs, response=" + response);
                                }
                            }
                        });
                    }
                } else if (!(call2 instanceof TLRPC.TL_phoneCall) || this.authKey != null) {
                    if ((call2 instanceof TLRPC.TL_phoneCallAccepted) && this.authKey == null) {
                        processAcceptedCall();
                    } else if (this.currentState == 13 && call2.receive_date != 0) {
                        dispatchStateChanged(16);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("!!!!!! CALL RECEIVED");
                        }
                        if (this.connectingSoundRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(this.connectingSoundRunnable);
                            this.connectingSoundRunnable = null;
                        }
                        if (this.spPlayID != 0) {
                            this.soundPool.stop(this.spPlayID);
                        }
                        this.spPlayID = this.soundPool.play(this.spRingbackID, 1.0f, 1.0f, 0, -1, 1.0f);
                        if (this.timeoutRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
                            this.timeoutRunnable = null;
                        }
                        this.timeoutRunnable = new Runnable() {
                            public void run() {
                                VoIPService.this.timeoutRunnable = null;
                                VoIPService.this.declineIncomingCall(3, (Runnable) null);
                            }
                        };
                        AndroidUtilities.runOnUIThread(this.timeoutRunnable, (long) MessagesController.getInstance(this.currentAccount).callRingTimeout);
                    }
                } else if (call2.g_a_or_b == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("stopping VoIP service, Ga == null");
                    }
                    callFailed();
                } else if (!Arrays.equals(this.g_a_hash, Utilities.computeSHA256(call2.g_a_or_b, 0, call2.g_a_or_b.length))) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("stopping VoIP service, Ga hash doesn't match");
                    }
                    callFailed();
                } else {
                    this.g_a = call2.g_a_or_b;
                    BigInteger g_a2 = new BigInteger(1, call2.g_a_or_b);
                    BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                    if (!Utilities.isGoodGaAndGb(g_a2, p)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("stopping VoIP service, bad Ga and Gb (accepting)");
                        }
                        callFailed();
                        return;
                    }
                    byte[] authKey2 = g_a2.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
                    if (authKey2.length > 256) {
                        byte[] correctedAuth = new byte[256];
                        System.arraycopy(authKey2, authKey2.length - 256, correctedAuth, 0, 256);
                        authKey2 = correctedAuth;
                    } else if (authKey2.length < 256) {
                        byte[] correctedAuth2 = new byte[256];
                        System.arraycopy(authKey2, 0, correctedAuth2, 256 - authKey2.length, authKey2.length);
                        for (int a = 0; a < 256 - authKey2.length; a++) {
                            correctedAuth2[a] = 0;
                        }
                        authKey2 = correctedAuth2;
                    }
                    byte[] authKeyHash = Utilities.computeSHA1(authKey2);
                    byte[] authKeyId = new byte[8];
                    System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                    this.authKey = authKey2;
                    long bytesToLong = Utilities.bytesToLong(authKeyId);
                    this.keyFingerprint = bytesToLong;
                    if (bytesToLong != call2.key_fingerprint) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("key fingerprints don't match");
                        }
                        callFailed();
                        return;
                    }
                    initiateActualEncryptedCall();
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.w("onCallUpdated called with wrong call id (got " + call2.id + ", expected " + this.call.id + SQLBuilder.PARENTHESES_RIGHT);
            }
        }
    }

    private void startRatingActivity() {
        try {
            PendingIntent.getActivity(this, 0, new Intent(this, VoIPFeedbackActivity.class).putExtra("call_id", this.call.id).putExtra("call_access_hash", this.call.access_hash).putExtra("account", this.currentAccount).addFlags(805306368), 0).send();
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error starting incall activity", (Throwable) x);
            }
        }
    }

    public byte[] getEncryptionKey() {
        return this.authKey;
    }

    private void processAcceptedCall() {
        dispatchStateChanged(12);
        BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
        BigInteger i_authKey = new BigInteger(1, this.call.g_b);
        if (!Utilities.isGoodGaAndGb(i_authKey, p)) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("stopping VoIP service, bad Ga and Gb");
            }
            callFailed();
            return;
        }
        byte[] authKey2 = i_authKey.modPow(new BigInteger(1, this.a_or_b), p).toByteArray();
        if (authKey2.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(authKey2, authKey2.length - 256, correctedAuth, 0, 256);
            authKey2 = correctedAuth;
        } else if (authKey2.length < 256) {
            byte[] correctedAuth2 = new byte[256];
            System.arraycopy(authKey2, 0, correctedAuth2, 256 - authKey2.length, authKey2.length);
            for (int a = 0; a < 256 - authKey2.length; a++) {
                correctedAuth2[a] = 0;
            }
            authKey2 = correctedAuth2;
        }
        byte[] authKeyHash = Utilities.computeSHA1(authKey2);
        byte[] authKeyId = new byte[8];
        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
        long fingerprint = Utilities.bytesToLong(authKeyId);
        this.authKey = authKey2;
        this.keyFingerprint = fingerprint;
        TLRPC.TL_phone_confirmCall req = new TLRPC.TL_phone_confirmCall();
        req.g_a = this.g_a;
        req.key_fingerprint = fingerprint;
        req.peer = new TLRPC.TL_inputPhoneCall();
        req.peer.id = this.call.id;
        req.peer.access_hash = this.call.access_hash;
        req.protocol = new TLRPC.TL_phoneCallProtocol();
        req.protocol.max_layer = CALL_MAX_LAYER;
        req.protocol.min_layer = 65;
        TLRPC.TL_phoneCallProtocol tL_phoneCallProtocol = req.protocol;
        req.protocol.udp_reflector = true;
        tL_phoneCallProtocol.udp_p2p = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (error != null) {
                            VoIPService.this.callFailed();
                            return;
                        }
                        TLRPC.PhoneCall unused = VoIPService.this.call = ((TLRPC.TL_phone_phoneCall) response).phone_call;
                        VoIPService.this.initiateActualEncryptedCall();
                    }
                });
            }
        });
    }

    private int convertDataSavingMode(int mode) {
        if (mode != 3) {
            return mode;
        }
        return ApplicationLoader.isRoaming() ? 1 : 0;
    }

    /* access modifiers changed from: private */
    public void initiateActualEncryptedCall() {
        if (this.timeoutRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
            this.timeoutRunnable = null;
        }
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("InitCall: keyID=" + this.keyFingerprint);
            }
            SharedPreferences nprefs = MessagesController.getNotificationsSettings(this.currentAccount);
            HashSet<String> hashes = new HashSet<>(nprefs.getStringSet("calls_access_hashes", Collections.EMPTY_SET));
            hashes.add(this.call.id + " " + this.call.access_hash + " " + System.currentTimeMillis());
            while (hashes.size() > 20) {
                String oldest = null;
                long oldestTime = Long.MAX_VALUE;
                Iterator<String> itr = hashes.iterator();
                while (itr.hasNext()) {
                    String item = itr.next();
                    String[] s = item.split(" ");
                    if (s.length < 2) {
                        itr.remove();
                    } else {
                        try {
                            long t = Long.parseLong(s[2]);
                            if (t < oldestTime) {
                                oldestTime = t;
                                oldest = item;
                            }
                        } catch (Exception e) {
                            itr.remove();
                        }
                    }
                }
                if (oldest != null) {
                    hashes.remove(oldest);
                }
            }
            nprefs.edit().putStringSet("calls_access_hashes", hashes).commit();
            this.controller.setConfig(((double) MessagesController.getInstance(this.currentAccount).callPacketTimeout) / 1000.0d, ((double) MessagesController.getInstance(this.currentAccount).callConnectTimeout) / 1000.0d, convertDataSavingMode(MessagesController.getGlobalMainSettings().getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault())), this.call.id);
            this.controller.setEncryptionKey(this.authKey, this.isOutgoing);
            SharedPreferences prefs = MessagesController.getGlobalMainSettings();
            this.controller.setRemoteEndpoints((TLRPC.TL_phoneConnection[]) this.call.connections.toArray(new TLRPC.TL_phoneConnection[this.call.connections.size()]), this.call.p2p_allowed, prefs.getBoolean("dbg_force_tcp_in_calls", false), this.call.protocol.max_layer);
            if (prefs.getBoolean("dbg_force_tcp_in_calls", false)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        ToastUtils.show((CharSequence) "This call uses TCP which will degrade its quality.");
                    }
                });
            }
            if (prefs.getBoolean("proxy_enabled", false) && prefs.getBoolean("proxy_enabled_calls", false)) {
                String server = prefs.getString("proxy_ip", (String) null);
                String secret = prefs.getString("proxy_secret", (String) null);
                if (!TextUtils.isEmpty(server) && TextUtils.isEmpty(secret)) {
                    this.controller.setProxy(server, prefs.getInt("proxy_port", 0), prefs.getString("proxy_user", (String) null), prefs.getString("proxy_pass", (String) null));
                }
            }
            this.controller.start();
            updateNetworkType();
            this.controller.connect();
            this.controllerStarted = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (VoIPService.this.controller != null) {
                        VoIPService.this.updateStats();
                        AndroidUtilities.runOnUIThread(this, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                    }
                }
            }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error starting call", (Throwable) x);
            }
            callFailed();
        }
    }

    /* access modifiers changed from: protected */
    public void showNotification() {
        showNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), this.user.photo != null ? this.user.photo.photo_small : null, VoIPActivity.class);
    }

    private void startConnectingSound() {
        if (this.spPlayID != 0) {
            this.soundPool.stop(this.spPlayID);
        }
        this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        if (this.spPlayID == 0) {
            AnonymousClass16 r0 = new Runnable() {
                public void run() {
                    if (VoIPBaseService.sharedInstance != null) {
                        if (VoIPService.this.spPlayID == 0) {
                            VoIPService voIPService = VoIPService.this;
                            voIPService.spPlayID = voIPService.soundPool.play(VoIPService.this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
                        }
                        if (VoIPService.this.spPlayID == 0) {
                            AndroidUtilities.runOnUIThread(this, 100);
                        } else {
                            VoIPService.this.connectingSoundRunnable = null;
                        }
                    }
                }
            };
            this.connectingSoundRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 100);
        }
    }

    /* access modifiers changed from: protected */
    public void callFailed(int errorCode) {
        if (this.call != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Discarding failed call");
            }
            TLRPC.TL_phone_discardCall req = new TLRPC.TL_phone_discardCall();
            req.peer = new TLRPC.TL_inputPhoneCall();
            req.peer.access_hash = this.call.access_hash;
            req.peer.id = this.call.id;
            req.duration = (this.controller == null || !this.controllerStarted) ? 0 : (int) (this.controller.getCallDuration() / 1000);
            req.connection_id = (this.controller == null || !this.controllerStarted) ? 0 : this.controller.getPreferredRelayID();
            req.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TLRPC.TL_error error) {
                    if (error != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("error on phone.discardCall: " + error);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("phone.discardCall " + response);
                    }
                }
            });
        }
        super.callFailed(errorCode);
    }

    public long getCallID() {
        TLRPC.PhoneCall phoneCall = this.call;
        if (phoneCall != null) {
            return phoneCall.id;
        }
        return 0;
    }

    public void onUIForegroundStateChanged(boolean isForeground) {
        if (Build.VERSION.SDK_INT >= 21 || this.currentState != 15) {
            return;
        }
        if (isForeground) {
            stopForeground(true);
        } else if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(VoIPService.this, VoIPActivity.class);
                    intent.addFlags(805306368);
                    try {
                        PendingIntent.getActivity(VoIPService.this, 0, intent, 0).send();
                    } catch (PendingIntent.CanceledException e) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("error restarting activity", (Throwable) e);
                        }
                        VoIPService.this.declineIncomingCall(4, (Runnable) null);
                    }
                    if (Build.VERSION.SDK_INT >= 26) {
                        VoIPService.this.showNotification();
                    }
                }
            }, 500);
        } else if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            showIncomingNotification(ContactsController.formatName(this.user.first_name, this.user.last_name), (CharSequence) null, this.user, (List<TLRPC.User>) null, 0, VoIPActivity.class);
        } else {
            declineIncomingCall(4, (Runnable) null);
        }
    }

    /* access modifiers changed from: package-private */
    public void onMediaButtonEvent(KeyEvent ev) {
        if ((ev.getKeyCode() != 79 && ev.getKeyCode() != 127 && ev.getKeyCode() != 85) || ev.getAction() != 1) {
            return;
        }
        if (this.currentState == 15) {
            acceptIncomingCall();
            return;
        }
        setMicMute(!isMicMute());
        Iterator it = this.stateListeners.iterator();
        while (it.hasNext()) {
            ((VoIPBaseService.StateListener) it.next()).onAudioSettingsChanged();
        }
    }

    public void debugCtl(int request, int param) {
        if (this.controller != null) {
            this.controller.debugCtl(request, param);
        }
    }

    public byte[] getGA() {
        return this.g_a;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
            callEnded();
        }
    }

    public void forceRating() {
        this.forceRating = true;
    }

    private String[] getEmoji() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            os.write(this.authKey);
            os.write(this.g_a);
        } catch (IOException e) {
        }
        return EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(os.toByteArray(), 0, os.size()));
    }

    public boolean canUpgrate() {
        return (this.peerCapabilities & 1) == 1;
    }

    public void upgradeToGroupCall(List<Integer> usersToAdd) {
        if (!this.upgrading) {
            this.groupUsersToAdd = usersToAdd;
            if (!this.isOutgoing) {
                this.controller.requestCallUpgrade();
                return;
            }
            this.upgrading = true;
            this.groupCallEncryptionKey = new byte[256];
            Utilities.random.nextBytes(this.groupCallEncryptionKey);
            byte[] bArr = this.groupCallEncryptionKey;
            bArr[0] = (byte) (bArr[0] & ByteCompanionObject.MAX_VALUE);
            byte[] authKeyHash = Utilities.computeSHA1(bArr);
            byte[] authKeyId = new byte[8];
            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
            this.groupCallKeyFingerprint = Utilities.bytesToLong(authKeyId);
            this.controller.sendGroupCallKey(this.groupCallEncryptionKey);
        }
    }

    public void onConnectionStateChanged(int newState) {
        if (newState == 3) {
            this.peerCapabilities = this.controller.getPeerCapabilities();
        }
        super.onConnectionStateChanged(newState);
    }

    public void onGroupCallKeyReceived(byte[] key) {
        this.joiningGroupCall = true;
        this.groupCallEncryptionKey = key;
        byte[] authKeyHash = Utilities.computeSHA1(key);
        byte[] authKeyId = new byte[8];
        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
        this.groupCallKeyFingerprint = Utilities.bytesToLong(authKeyId);
    }

    public void onGroupCallKeySent() {
        boolean z = this.isOutgoing;
    }

    public void onCallUpgradeRequestReceived() {
        upgradeToGroupCall(new ArrayList());
    }

    public VoIPBaseService.CallConnection getConnectionAndStartCall() {
        if (this.systemCallConnection == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("creating call connection");
            }
            this.systemCallConnection = new VoIPBaseService.CallConnection();
            this.systemCallConnection.setInitializing();
            if (this.isOutgoing) {
                AnonymousClass19 r0 = new Runnable() {
                    public void run() {
                        Runnable unused = VoIPService.this.delayedStartOutgoingCall = null;
                        VoIPService.this.startOutgoingCall();
                    }
                };
                this.delayedStartOutgoingCall = r0;
                AndroidUtilities.runOnUIThread(r0, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
            VoIPBaseService.CallConnection callConnection = this.systemCallConnection;
            callConnection.setAddress(Uri.fromParts("tel", "+99084" + this.user.id, (String) null), 1);
            this.systemCallConnection.setCallerDisplayName(ContactsController.formatName(this.user.first_name, this.user.last_name), 1);
        }
        return this.systemCallConnection;
    }
}

package im.bclpbkiauv.ui.components.voip;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.voip.VoIPService;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.VoIPActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.components.BetterRatingView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class VoIPHelper {
    private static final int VOIP_SUPPORT_ID = 4244000;
    public static long lastCallTime = 0;

    public static void startCall(TLRPC.User user, Activity activity, TLRPC.UserFull userFull) {
        String str;
        int i;
        String str2;
        int i2;
        boolean isAirplaneMode = true;
        if (userFull != null && userFull.phone_calls_private) {
            new AlertDialog.Builder((Context) activity).setTitle(LocaleController.getString("VoipFailed", R.string.VoipFailed)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", R.string.CallNotAvailable, ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null).show();
        } else if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            if (Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == 0) {
                isAirplaneMode = false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
            if (isAirplaneMode) {
                i = R.string.VoipOfflineAirplaneTitle;
                str = "VoipOfflineAirplaneTitle";
            } else {
                i = R.string.VoipOfflineTitle;
                str = "VoipOfflineTitle";
            }
            AlertDialog.Builder title = builder.setTitle(LocaleController.getString(str, i));
            if (isAirplaneMode) {
                i2 = R.string.VoipOfflineAirplane;
                str2 = "VoipOfflineAirplane";
            } else {
                i2 = R.string.VoipOffline;
                str2 = "VoipOffline";
            }
            AlertDialog.Builder bldr = title.setMessage(LocaleController.getString(str2, i2)).setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            if (isAirplaneMode) {
                Intent settingsIntent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (settingsIntent.resolveActivity(activity.getPackageManager()) != null) {
                    bldr.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", R.string.VoipOfflineOpenSettings), new DialogInterface.OnClickListener(activity, settingsIntent) {
                        private final /* synthetic */ Activity f$0;
                        private final /* synthetic */ Intent f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            this.f$0.startActivity(this.f$1);
                        }
                    });
                }
            }
            bldr.show();
        } else if (Build.VERSION.SDK_INT < 23 || activity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            initiateCall(user, activity);
        } else {
            activity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
        }
    }

    private static void initiateCall(TLRPC.User user, Activity activity) {
        if (activity != null && user != null) {
            if (VoIPService.getSharedInstance() != null) {
                TLRPC.User callUser = VoIPService.getSharedInstance().getUser();
                if (callUser.id != user.id) {
                    new AlertDialog.Builder((Context) activity).setTitle(LocaleController.getString("VoipOngoingAlertTitle", R.string.VoipOngoingAlertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipOngoingAlert", R.string.VoipOngoingAlert, ContactsController.formatName(callUser.first_name, callUser.last_name), ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(activity) {
                        private final /* synthetic */ Activity f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            VoIPHelper.lambda$initiateCall$2(TLRPC.User.this, this.f$1, dialogInterface, i);
                        }
                    }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null).show();
                    return;
                }
                activity.startActivity(new Intent(activity, VoIPActivity.class).addFlags(C.ENCODING_PCM_MU_LAW));
            } else if (VoIPService.callIShouldHavePutIntoIntent == null) {
                doInitiateCall(user, activity);
            }
        }
    }

    static /* synthetic */ void lambda$initiateCall$2(TLRPC.User user, Activity activity, DialogInterface dialog, int which) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(new Runnable(activity) {
                private final /* synthetic */ Activity f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPHelper.doInitiateCall(TLRPC.User.this, this.f$1);
                }
            });
        } else {
            doInitiateCall(user, activity);
        }
    }

    /* access modifiers changed from: private */
    public static void doInitiateCall(TLRPC.User user, Activity activity) {
        if (activity != null && user != null && System.currentTimeMillis() - lastCallTime >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
            lastCallTime = System.currentTimeMillis();
            Intent intent = new Intent(activity, VoIPService.class);
            intent.putExtra("user_id", user.id);
            intent.putExtra("is_outgoing", true);
            intent.putExtra("start_incall_activity", true);
            intent.putExtra("account", UserConfig.selectedAccount);
            try {
                activity.startService(intent);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public static void permissionDenied(Activity activity, Runnable onFinish) {
        if (!activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            new AlertDialog.Builder((Context) activity).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("VoipNeedMicPermission", R.string.VoipNeedMicPermission)).setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString("Settings", R.string.Settings), new DialogInterface.OnClickListener(activity) {
                private final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPHelper.lambda$permissionDenied$3(this.f$0, dialogInterface, i);
                }
            }).show().setOnDismissListener(new DialogInterface.OnDismissListener(onFinish) {
                private final /* synthetic */ Runnable f$0;

                {
                    this.f$0 = r1;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    VoIPHelper.lambda$permissionDenied$4(this.f$0, dialogInterface);
                }
            });
        }
    }

    static /* synthetic */ void lambda$permissionDenied$3(Activity activity, DialogInterface dialog, int which) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), (String) null));
        activity.startActivity(intent);
    }

    static /* synthetic */ void lambda$permissionDenied$4(Runnable onFinish, DialogInterface dialog) {
        if (onFinish != null) {
            onFinish.run();
        }
    }

    public static File getLogsDir() {
        File logsDir = new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
        return logsDir;
    }

    public static boolean canRateCall(TLRPC.TL_messageActionPhoneCall call) {
        if (!(call.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) && !(call.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) {
            for (String hash : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
                String[] d = hash.split(" ");
                if (d.length >= 2) {
                    String str = d[0];
                    if (str.equals(call.call_id + "")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showRateAlert(Context context, TLRPC.TL_messageActionPhoneCall call) {
        for (String hash : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
            String[] d = hash.split(" ");
            if (d.length >= 2) {
                String str = d[0];
                if (str.equals(call.call_id + "")) {
                    try {
                        Context context2 = context;
                        showRateAlert(context2, (Runnable) null, call.call_id, Long.parseLong(d[1]), UserConfig.selectedAccount, true);
                        return;
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        }
    }

    public static void showRateAlert(Context context, Runnable onDismiss, long callID, long accessHash, int account, boolean userInitiative) {
        final Context context2 = context;
        final File log = getLogFile(callID);
        int i = 1;
        boolean z = false;
        final int[] page = {0};
        LinearLayout alertView = new LinearLayout(context2);
        alertView.setOrientation(1);
        int pad = AndroidUtilities.dp(16.0f);
        alertView.setPadding(pad, pad, pad, 0);
        TextView text = new TextView(context2);
        text.setTextSize(2, 16.0f);
        text.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        text.setGravity(17);
        text.setText(LocaleController.getString("VoipRateCallAlert", R.string.VoipRateCallAlert));
        alertView.addView(text);
        BetterRatingView bar = new BetterRatingView(context2);
        alertView.addView(bar, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        LinearLayout problemsWrap = new LinearLayout(context2);
        problemsWrap.setOrientation(1);
        AnonymousClass1 r10 = new View.OnClickListener() {
            public void onClick(View v) {
                CheckBoxCell check = (CheckBoxCell) v;
                check.setChecked(!check.isChecked(), true);
            }
        };
        String[] problems = {"echo", "noise", "interruptions", "distorted_speech", "silent_local", "silent_remote", "dropped"};
        int i2 = 0;
        while (i2 < problems.length) {
            CheckBoxCell check = new CheckBoxCell(context2, i);
            check.setClipToPadding(z);
            check.setTag(problems[i2]);
            String label = null;
            switch (i2) {
                case 0:
                    label = LocaleController.getString("RateCallEcho", R.string.RateCallEcho);
                    break;
                case 1:
                    label = LocaleController.getString("RateCallNoise", R.string.RateCallNoise);
                    break;
                case 2:
                    label = LocaleController.getString("RateCallInterruptions", R.string.RateCallInterruptions);
                    break;
                case 3:
                    label = LocaleController.getString("RateCallDistorted", R.string.RateCallDistorted);
                    break;
                case 4:
                    label = LocaleController.getString("RateCallSilentLocal", R.string.RateCallSilentLocal);
                    break;
                case 5:
                    label = LocaleController.getString("RateCallSilentRemote", R.string.RateCallSilentRemote);
                    break;
                case 6:
                    label = LocaleController.getString("RateCallDropped", R.string.RateCallDropped);
                    break;
            }
            check.setText(label, (String) null, false, false);
            check.setOnClickListener(r10);
            check.setTag(problems[i2]);
            problemsWrap.addView(check);
            i2++;
            i = 1;
            z = false;
        }
        alertView.addView(problemsWrap, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        problemsWrap.setVisibility(8);
        EditText commentBox = new EditText(context2);
        commentBox.setHint(LocaleController.getString("VoipFeedbackCommentHint", R.string.VoipFeedbackCommentHint));
        commentBox.setInputType(147457);
        commentBox.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        commentBox.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        commentBox.setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
        commentBox.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        commentBox.setTextSize(18.0f);
        commentBox.setVisibility(8);
        alertView.addView(commentBox, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        final boolean[] includeLogs = {true};
        final CheckBoxCell checkbox = new CheckBoxCell(context2, 1);
        AnonymousClass2 r4 = new View.OnClickListener() {
            public void onClick(View v) {
                boolean[] zArr = includeLogs;
                zArr[0] = !zArr[0];
                checkbox.setChecked(zArr[0], true);
            }
        };
        String[] strArr = problems;
        AnonymousClass1 r21 = r10;
        checkbox.setText(LocaleController.getString("CallReportIncludeLogs", R.string.CallReportIncludeLogs), (String) null, true, false);
        checkbox.setClipToPadding(false);
        checkbox.setOnClickListener(r4);
        alertView.addView(checkbox, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        TextView logsText = new TextView(context2);
        logsText.setTextSize(2, 14.0f);
        logsText.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        logsText.setText(LocaleController.getString("CallReportLogsExplain", R.string.CallReportLogsExplain));
        logsText.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        logsText.setOnClickListener(r4);
        alertView.addView(logsText);
        checkbox.setVisibility(8);
        logsText.setVisibility(8);
        if (!log.exists()) {
            includeLogs[0] = false;
        }
        final Runnable runnable = onDismiss;
        AlertDialog alert = new AlertDialog.Builder(context2).setTitle(LocaleController.getString("CallMessageReportProblem", R.string.CallMessageReportProblem)).setView(alertView).setPositiveButton(LocaleController.getString("Send", R.string.Send), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null).setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                Runnable runnable = runnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }).create();
        if (BuildVars.DEBUG_VERSION && log.exists()) {
            alert.setNeutralButton("Send log", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context2, LaunchActivity.class);
                    intent.setAction("android.intent.action.SEND");
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(log));
                    context2.startActivity(intent);
                }
            });
        }
        alert.show();
        alert.getWindow().setSoftInputMode(3);
        final View btn = alert.getButton(-1);
        final View view = btn;
        btn.setEnabled(false);
        bar.setOnRatingChangeListener(new BetterRatingView.OnRatingChangeListener() {
            public void onRatingChanged(int rating) {
                String str;
                int i;
                btn.setEnabled(rating > 0);
                TextView textView = (TextView) btn;
                if (rating < 4) {
                    i = R.string.Next;
                    str = "Next";
                } else {
                    i = R.string.Send;
                    str = "Send";
                }
                textView.setText(LocaleController.getString(str, i).toUpperCase());
            }
        });
        AnonymousClass7 r19 = r1;
        AlertDialog alert2 = alert;
        final BetterRatingView betterRatingView = bar;
        AnonymousClass2 r23 = r4;
        final LinearLayout linearLayout = problemsWrap;
        CheckBoxCell checkbox2 = checkbox;
        final EditText editText = commentBox;
        boolean[] zArr = includeLogs;
        EditText editText2 = commentBox;
        final long j = accessHash;
        AnonymousClass1 r27 = r21;
        LinearLayout linearLayout2 = problemsWrap;
        final long j2 = callID;
        BetterRatingView betterRatingView2 = bar;
        final boolean z2 = userInitiative;
        TextView text2 = text;
        final int i3 = account;
        int i4 = pad;
        final File file = log;
        LinearLayout linearLayout3 = alertView;
        final AlertDialog alertDialog = alert2;
        File file2 = log;
        final TextView textView = text2;
        final CheckBoxCell checkBoxCell = checkbox2;
        final TextView textView2 = logsText;
        AnonymousClass7 r1 = new View.OnClickListener() {
            public void onClick(View v) {
                if (betterRatingView.getRating() < 4) {
                    int[] iArr = page;
                    if (iArr[0] != 1) {
                        iArr[0] = 1;
                        betterRatingView.setVisibility(8);
                        textView.setVisibility(8);
                        alertDialog.setTitle(LocaleController.getString("CallReportHint", R.string.CallReportHint));
                        editText.setVisibility(0);
                        if (file.exists()) {
                            checkBoxCell.setVisibility(0);
                            textView2.setVisibility(0);
                        }
                        linearLayout.setVisibility(0);
                        ((TextView) view).setText(LocaleController.getString("Send", R.string.Send).toUpperCase());
                        return;
                    }
                }
                int currentAccount = UserConfig.selectedAccount;
                TLRPC.TL_phone_setCallRating req = new TLRPC.TL_phone_setCallRating();
                req.rating = betterRatingView.getRating();
                ArrayList<String> problemTags = new ArrayList<>();
                for (int i = 0; i < linearLayout.getChildCount(); i++) {
                    CheckBoxCell check = (CheckBoxCell) linearLayout.getChildAt(i);
                    if (check.isChecked()) {
                        problemTags.add("#" + check.getTag());
                    }
                }
                if (req.rating < 5) {
                    req.comment = editText.getText().toString();
                } else {
                    req.comment = "";
                }
                if (!problemTags.isEmpty() && !includeLogs[0]) {
                    req.comment += " " + TextUtils.join(" ", problemTags);
                }
                req.peer = new TLRPC.TL_inputPhoneCall();
                req.peer.access_hash = j;
                req.peer.id = j2;
                req.user_initiative = z2;
                ConnectionsManager.getInstance(i3).sendRequest(req, new RequestDelegate(currentAccount, includeLogs, file, req, problemTags) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ boolean[] f$1;
                    private final /* synthetic */ File f$2;
                    private final /* synthetic */ TLRPC.TL_phone_setCallRating f$3;
                    private final /* synthetic */ ArrayList f$4;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        VoIPHelper.AnonymousClass7.lambda$onClick$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                    }
                });
                alertDialog.dismiss();
            }

            static /* synthetic */ void lambda$onClick$0(int currentAccount, boolean[] includeLogs, File log, TLRPC.TL_phone_setCallRating req, ArrayList problemTags, TLObject response, TLRPC.TL_error error) {
                TLObject tLObject = response;
                if (tLObject instanceof TLRPC.TL_updates) {
                    MessagesController.getInstance(currentAccount).processUpdates((TLRPC.TL_updates) tLObject, false);
                }
                if (!includeLogs[0] || !log.exists()) {
                    TLRPC.TL_phone_setCallRating tL_phone_setCallRating = req;
                } else if (req.rating < 4) {
                    SendMessagesHelper.prepareSendingDocument(AccountInstance.getInstance(UserConfig.selectedAccount), log.getAbsolutePath(), log.getAbsolutePath(), (Uri) null, TextUtils.join(" ", problemTags), "text/plain", 4244000, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                    ToastUtils.show((int) R.string.CallReportSent);
                }
            }
        };
        btn.setOnClickListener(r1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = new java.io.File(im.bclpbkiauv.messenger.ApplicationLoader.applicationContext.getExternalFilesDir((java.lang.String) null), "logs");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.io.File getLogFile(long r7) {
        /*
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0043
            java.io.File r0 = new java.io.File
            android.content.Context r1 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r2 = 0
            java.io.File r1 = r1.getExternalFilesDir(r2)
            java.lang.String r2 = "logs"
            r0.<init>(r1, r2)
            java.lang.String[] r1 = r0.list()
            if (r1 == 0) goto L_0x0043
            int r2 = r1.length
            r3 = 0
        L_0x001a:
            if (r3 >= r2) goto L_0x0043
            r4 = r1[r3]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "voip"
            r5.append(r6)
            r5.append(r7)
            java.lang.String r6 = ".txt"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            boolean r5 = r4.endsWith(r5)
            if (r5 == 0) goto L_0x0040
            java.io.File r2 = new java.io.File
            r2.<init>(r0, r4)
            return r2
        L_0x0040:
            int r3 = r3 + 1
            goto L_0x001a
        L_0x0043:
            java.io.File r0 = new java.io.File
            java.io.File r1 = getLogsDir()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            java.lang.String r3 = ".log"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.<init>(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.voip.VoIPHelper.getLogFile(long):java.io.File");
    }

    public static void showCallDebugSettings(Context context) {
        final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(1);
        TextView warning = new TextView(context);
        warning.setTextSize(1, 15.0f);
        warning.setText("Please only change these settings if you know exactly what they do.");
        warning.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        ll.addView(warning, LayoutHelper.createLinear(-1, -2, 16.0f, 8.0f, 16.0f, 8.0f));
        final TextCheckCell tcpCell = new TextCheckCell(context);
        tcpCell.setTextAndCheck("Force TCP", preferences.getBoolean("dbg_force_tcp_in_calls", false), false);
        tcpCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean force = preferences.getBoolean("dbg_force_tcp_in_calls", false);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("dbg_force_tcp_in_calls", !force);
                editor.commit();
                tcpCell.setChecked(!force);
            }
        });
        ll.addView(tcpCell);
        if (BuildVars.DEBUG_VERSION && BuildVars.LOGS_ENABLED) {
            final TextCheckCell dumpCell = new TextCheckCell(context);
            dumpCell.setTextAndCheck("Dump detailed stats", preferences.getBoolean("dbg_dump_call_stats", false), false);
            dumpCell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean force = preferences.getBoolean("dbg_dump_call_stats", false);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("dbg_dump_call_stats", !force);
                    editor.commit();
                    dumpCell.setChecked(!force);
                }
            });
            ll.addView(dumpCell);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            final TextCheckCell connectionServiceCell = new TextCheckCell(context);
            connectionServiceCell.setTextAndCheck("Enable ConnectionService", preferences.getBoolean("dbg_force_connection_service", false), false);
            connectionServiceCell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean force = preferences.getBoolean("dbg_force_connection_service", false);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("dbg_force_connection_service", !force);
                    editor.commit();
                    connectionServiceCell.setChecked(!force);
                }
            });
            ll.addView(connectionServiceCell);
        }
        new AlertDialog.Builder(context).setTitle(LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings)).setView(ll).show();
    }

    public static int getDataSavingDefault() {
        boolean low = DownloadController.getInstance(0).lowPreset.lessCallData;
        boolean medium = DownloadController.getInstance(0).mediumPreset.lessCallData;
        boolean high = DownloadController.getInstance(0).highPreset.lessCallData;
        if (!low && !medium && !high) {
            return 0;
        }
        if (low && !medium && !high) {
            return 3;
        }
        if (low && medium && !high) {
            return 1;
        }
        if (low && medium && high) {
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("Invalid call data saving preset configuration: " + low + "/" + medium + "/" + high);
        }
        return 0;
    }
}

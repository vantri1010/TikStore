package im.bclpbkiauv.ui.hui.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bjz.comm.net.SPConstant;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.IpChangeActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.WebviewActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import im.bclpbkiauv.ui.constants.Constants;
import im.bclpbkiauv.ui.hcells.IndexTextCell;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AboutAppActivity extends BaseFragment {
    private PageSelectionAdapter<Integer, PageHolder> adapter;
    private int pressCount;
    private RecyclerListView rv;

    public View createView(Context context) {
        initActionBar(context);
        this.fragmentView = initContentView(context);
        return this.fragmentView;
    }

    private void initActionBar(Context context) {
        this.actionBar = createActionBar(context);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                super.onItemClick(id);
                if (id == -1) {
                    AboutAppActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.setTitle(LocaleController.getString("AboutApp", R.string.AboutApp));
    }

    public void onResume() {
        super.onResume();
        getParentActivity().setRequestedOrientation(1);
    }

    public void onFragmentDestroy() {
        getParentActivity().setRequestedOrientation(2);
        super.onFragmentDestroy();
    }

    private View initContentView(Context context) {
        Context context2 = context;
        NestedScrollView myScrollView = new NestedScrollView(context2);
        myScrollView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        myScrollView.setFillViewport(true);
        LinearLayout par = new LinearLayout(context2);
        par.setOrientation(1);
        par.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ImageView ivLogo = new ImageView(context2);
        ivLogo.setImageResource(R.mipmap.ic_logo);
        par.addView(ivLogo, LayoutHelper.createLinear(-2, -2, 1, 0, AndroidUtilities.dp(30.0f), 0, AndroidUtilities.dp(5.0f)));
        addTextView(par, new TextView(context2), 14, Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), LocaleController.getString("AppName", R.string.AppName), LayoutHelper.createLinear(-2, -2, 1));
        TextView tvAppVersion = new TextView(context2);
        int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2);
        addTextView(par, tvAppVersion, 13, color, LocaleController.getString("Version", R.string.Version) + " " + AndroidUtilities.getVersionName(context), LayoutHelper.createLinear(-2, -2, 1, 0, AndroidUtilities.dp(2.0f), 0, 0));
        tvAppVersion.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return AboutAppActivity.this.lambda$initContentView$1$AboutAppActivity(view);
            }
        });
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.rv = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context2));
        this.rv.setVerticalScrollBarEnabled(false);
        this.rv.setOverScrollMode(2);
        this.rv.setNestedScrollingEnabled(false);
        this.rv.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AboutAppActivity.this.lambda$initContentView$2$AboutAppActivity(view, i);
            }
        });
        AnonymousClass2 r1 = new PageSelectionAdapter<Integer, PageHolder>(getParentActivity()) {
            public PageHolder onCreateViewHolderForChild(ViewGroup parent, int viewType) {
                return new PageHolder(new IndexTextCell(getContext()));
            }

            public void onBindViewHolderForChild(PageHolder holder, int position, Integer item) {
                if (position == 0) {
                    ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("OnlineService", R.string.OnlineService), 0, (int) R.mipmap.icon_arrow_right, true);
                } else if (position == 1) {
                    ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("UserAgreementOnly", R.string.UserAgreementOnly), 0, (int) R.mipmap.icon_arrow_right, true);
                } else if (position == 2) {
                    ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("PrivacyPolicyWithoutBookTitleMark", R.string.PrivacyPolicyWithoutBookTitleMark), 0, (int) R.mipmap.icon_arrow_right, true);
                } else if (position == 3) {
                    ((IndexTextCell) holder.itemView).setTextAndIcon(LocaleController.getString("CheckForUpdates", R.string.CheckForUpdates), 0, (int) R.mipmap.icon_arrow_right, true);
                }
                holder.itemView.invalidate();
            }
        };
        this.adapter = r1;
        r1.setData(Arrays.asList(new Integer[]{0, 1, 2, 3}));
        this.adapter.setShowLoadMoreViewEnable(false);
        this.rv.setAdapter(this.adapter);
        LinearLayout.LayoutParams lp = LayoutHelper.createLinear(-1, 0, 1.0f);
        lp.topMargin = AndroidUtilities.dp(30.0f);
        par.addView(this.rv, lp);
        myScrollView.addView((View) par, (ViewGroup.LayoutParams) LayoutHelper.createFrame(-1, -1.0f));
        return myScrollView;
    }

    public /* synthetic */ boolean lambda$initContentView$1$AboutAppActivity(View v) {
        String str;
        int i;
        String str2;
        int i2;
        boolean z = BuildVars.RELEASE_VERSION;
        int i3 = this.pressCount + 1;
        this.pressCount = i3;
        if (i3 >= 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
            CharSequence[] items = new CharSequence[11];
            items[0] = LocaleController.getString("DebugSendLogs", R.string.DebugSendLogs);
            items[1] = LocaleController.getString("DebugClearLogs", R.string.DebugClearLogs);
            items[2] = LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs);
            if (BuildVars.LOGS_ENABLED) {
                i = R.string.DebugMenuDisableLogs;
                str = "DebugMenuDisableLogs";
            } else {
                i = R.string.DebugMenuEnableLogs;
                str = "DebugMenuEnableLogs";
            }
            items[3] = LocaleController.getString(str, i);
            if (SharedConfig.inappCamera) {
                i2 = R.string.DebugMenuDisableCamera;
                str2 = "DebugMenuDisableCamera";
            } else {
                i2 = R.string.DebugMenuEnableCamera;
                str2 = "DebugMenuEnableCamera";
            }
            items[4] = LocaleController.getString(str2, i2);
            items[5] = LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache);
            items[6] = LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings);
            items[7] = null;
            items[8] = BuildVars.RELEASE_VERSION ? LocaleController.getString("CheckAppUpdates", R.string.CheckAppUpdates) : null;
            items[9] = LocaleController.getString("DebugMenuReadAllDialogs", R.string.DebugMenuReadAllDialogs);
            items[10] = BuildVars.RELEASE_VERSION ? null : "切换IP";
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AboutAppActivity.this.lambda$null$0$AboutAppActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else {
            try {
                ToastUtils.show((CharSequence) "¯\\_(ツ)_/¯");
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$null$0$AboutAppActivity(DialogInterface dialog, int which) {
        if (which == 0) {
            sendLogs();
        } else if (which == 1) {
            FileLog.cleanupLogs();
        } else if (which == 2) {
            MessagesController.getInstance(this.currentAccount).forceResetDialogs();
        } else if (which == 3) {
            BuildVars.LOGS_ENABLED = true ^ BuildVars.LOGS_ENABLED;
            ApplicationLoader.applicationContext.getSharedPreferences(SPConstant.SP_SYSTEM_CONFIG, 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
        } else if (which == 4) {
            SharedConfig.toggleInappCamera();
        } else if (which == 5) {
            MessagesStorage.getInstance(this.currentAccount).clearSentMedia();
            SharedConfig.setNoSoundHintShowed(false);
            MessagesController.getGlobalMainSettings().edit().remove("archivehint").remove("archivehint_l").remove("gifhint").remove("soundHint").commit();
        } else if (which == 6) {
            VoIPHelper.showCallDebugSettings(getParentActivity());
        } else if (which == 7) {
            SharedConfig.toggleRoundCamera16to9();
        } else if (which == 8) {
            ((LaunchActivity) getParentActivity()).checkAppUpdate(true);
        } else if (which == 9) {
            MessagesStorage.getInstance(this.currentAccount).readAllDialogs();
        } else if (which == 10) {
            presentFragment(new IpChangeActivity());
        }
    }

    public /* synthetic */ void lambda$initContentView$2$AboutAppActivity(View view, int position) {
        if (position == 0) {
            performService(this);
        } else if (position == 1) {
            presentFragment(new WebviewActivity(Constants.URL_USER_AGREEMENT, (String) null));
        } else if (position == 2) {
            presentFragment(new WebviewActivity(Constants.URL_PRIVACY_POLICY, (String) null));
        } else if (position == 3) {
            ((LaunchActivity) getParentActivity()).checkAppUpdate(true);
        }
    }

    public void performService(BaseFragment fragment) {
        String userString;
        int currentAccount = fragment.getCurrentAccount();
        SharedPreferences preferences = MessagesController.getMainSettings(currentAccount);
        int uid = preferences.getInt("support_id", 0);
        TLRPC.User supportUser = null;
        if (!(uid == 0 || (supportUser = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(uid))) != null || (userString = preferences.getString("support_user", (String) null)) == null)) {
            try {
                byte[] datacentersBytes = Base64.decode(userString, 0);
                if (datacentersBytes != null) {
                    SerializedData data = new SerializedData(datacentersBytes);
                    supportUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                    if (supportUser != null && supportUser.id == 333000) {
                        supportUser = null;
                    }
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                supportUser = null;
            }
        }
        if (supportUser == null) {
            XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
            progressDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_help_getSupport(), new RequestDelegate(preferences, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ XAlertDialog f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ BaseFragment f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AboutAppActivity.lambda$performService$5(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$performService$5(SharedPreferences preferences, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(preferences, (TLRPC.TL_help_support) response, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ TLRPC.TL_help_support f$1;
                private final /* synthetic */ XAlertDialog f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ BaseFragment f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    AboutAppActivity.lambda$null$3(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    AboutAppActivity.lambda$null$4(XAlertDialog.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$3(SharedPreferences preferences, TLRPC.TL_help_support res, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("support_id", res.user.id);
        SerializedData data = new SerializedData();
        res.user.serializeToStream(data);
        editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
        editor.commit();
        data.cleanup();
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(currentAccount).putUser(res.user, false);
        Bundle args = new Bundle();
        args.putInt("user_id", res.user.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$null$4(XAlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void sendLogs() {
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
            Utilities.globalQueue.postRunnable(new Runnable(progressDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AboutAppActivity.this.lambda$sendLogs$7$AboutAppActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendLogs$7$AboutAppActivity(AlertDialog progressDialog) {
        try {
            File dir = new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null).getAbsolutePath() + "/logs");
            File zipFile = new File(dir, "logs.zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }
            File[] files = dir.listFiles();
            boolean[] finished = new boolean[1];
            BufferedInputStream origin = null;
            ZipOutputStream out = null;
            try {
                out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
                byte[] data = new byte[65536];
                for (int i = 0; i < files.length; i++) {
                    BufferedInputStream origin2 = new BufferedInputStream(new FileInputStream(files[i]), data.length);
                    out.putNextEntry(new ZipEntry(files[i].getName()));
                    while (true) {
                        int read = origin2.read(data, 0, data.length);
                        int count = read;
                        if (read == -1) {
                            break;
                        }
                        out.write(data, 0, count);
                    }
                    origin2.close();
                    origin = null;
                }
                finished[0] = true;
                if (origin != null) {
                    origin.close();
                }
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    if (origin != null) {
                        origin.close();
                    }
                    if (out != null) {
                    }
                } catch (Exception e2) {
                    e = e2;
                    e.printStackTrace();
                } catch (Throwable th) {
                    AlertDialog alertDialog = progressDialog;
                    if (origin != null) {
                        origin.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    throw th;
                }
            }
            out.close();
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, finished, zipFile) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ boolean[] f$2;
                private final /* synthetic */ File f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    AboutAppActivity.this.lambda$null$6$AboutAppActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        } catch (Exception e3) {
            e = e3;
            AlertDialog alertDialog2 = progressDialog;
            e.printStackTrace();
        }
    }

    public /* synthetic */ void lambda$null$6$AboutAppActivity(AlertDialog progressDialog, boolean[] finished, File zipFile) {
        Uri uri;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        if (finished[0]) {
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getParentActivity(), "im.bclpbkiauv.messenger.provider", zipFile);
            } else {
                uri = Uri.fromFile(zipFile);
            }
            Intent i = new Intent("android.intent.action.SEND");
            if (Build.VERSION.SDK_INT >= 24) {
                i.addFlags(1);
            }
            i.setType("message/rfc822");
            i.putExtra("android.intent.extra.EMAIL", "");
            i.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            i.putExtra("android.intent.extra.STREAM", uri);
            getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
            return;
        }
        ToastUtils.show((int) R.string.ErrorOccurred);
    }

    private void addTextView(LinearLayout parent, TextView tv, int textSize, int textColor, CharSequence text, ViewGroup.LayoutParams lp) {
        if (tv != null) {
            tv.setTextSize(1, (float) textSize);
            tv.setTextColor(textColor);
            tv.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            tv.setText(text);
            parent.addView(tv, lp);
        }
    }
}

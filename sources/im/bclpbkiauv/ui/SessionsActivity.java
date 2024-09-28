package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.SessionCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;

@Deprecated
public class SessionsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public TLRPC.TL_authorization currentSession;
    /* access modifiers changed from: private */
    public int currentSessionRow;
    /* access modifiers changed from: private */
    public int currentSessionSectionRow;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public LinearLayout emptyLayout;
    private EmptyTextProgressView emptyView;
    private ImageView imageView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    /* access modifiers changed from: private */
    public int noOtherSessionsRow;
    /* access modifiers changed from: private */
    public int otherSessionsEndRow;
    /* access modifiers changed from: private */
    public int otherSessionsSectionRow;
    /* access modifiers changed from: private */
    public int otherSessionsStartRow;
    /* access modifiers changed from: private */
    public int otherSessionsTerminateDetail;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> passwordSessions = new ArrayList<>();
    /* access modifiers changed from: private */
    public int passwordSessionsDetailRow;
    /* access modifiers changed from: private */
    public int passwordSessionsEndRow;
    /* access modifiers changed from: private */
    public int passwordSessionsSectionRow;
    /* access modifiers changed from: private */
    public int passwordSessionsStartRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> sessions = new ArrayList<>();
    /* access modifiers changed from: private */
    public int terminateAllSessionsDetailRow;
    /* access modifiers changed from: private */
    public int terminateAllSessionsRow;
    private TextView textView1;
    private TextView textView2;

    public SessionsActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        loadSessions(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSessionReceived);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSessionReceived);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("SessionsTitle", R.string.SessionsTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    SessionsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context2);
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        if (this.currentType == 0) {
            imageView2.setImageResource(R.drawable.devices);
        } else {
            imageView2.setImageResource(R.drawable.no_apps);
        }
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sessions_devicesImage), PorterDuff.Mode.MULTIPLY));
        this.emptyLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        this.textView1 = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.textView1.setGravity(17);
        this.textView1.setTextSize(1, 17.0f);
        this.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (this.currentType == 0) {
            this.textView1.setText(LocaleController.getString("NoOtherSessions", R.string.NoOtherSessions));
        } else {
            this.textView1.setText(LocaleController.getString("NoOtherWebSessions", R.string.NoOtherWebSessions));
        }
        this.emptyLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        TextView textView3 = new TextView(context2);
        this.textView2 = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.textView2.setGravity(17);
        this.textView2.setTextSize(1, 17.0f);
        this.textView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        if (this.currentType == 0) {
            this.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", R.string.NoOtherSessionsInfo));
        } else {
            this.textView2.setText(LocaleController.getString("NoOtherWebSessionsInfo", R.string.NoOtherWebSessionsInfo));
        }
        this.emptyLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SessionsActivity.this.lambda$createView$11$SessionsActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$11$SessionsActivity(View view, int position) {
        String name;
        int i = position;
        if (i == this.terminateAllSessionsRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                if (this.currentType == 0) {
                    builder.setMessage(LocaleController.getString("AreYouSureSessions", R.string.AreYouSureSessions));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSureWebSessions", R.string.AreYouSureWebSessions));
                }
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SessionsActivity.this.lambda$null$4$SessionsActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        } else if (((i >= this.otherSessionsStartRow && i < this.otherSessionsEndRow) || (i >= this.passwordSessionsStartRow && i < this.passwordSessionsEndRow)) && getParentActivity() != null) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            boolean[] param = new boolean[1];
            if (this.currentType == 0) {
                builder2.setMessage(LocaleController.getString("TerminateSessionQuestion", R.string.TerminateSessionQuestion));
            } else {
                TLRPC.TL_webAuthorization authorization = (TLRPC.TL_webAuthorization) this.sessions.get(i - this.otherSessionsStartRow);
                builder2.setMessage(LocaleController.formatString("TerminateWebSessionQuestion", R.string.TerminateWebSessionQuestion, authorization.domain));
                FrameLayout frameLayout1 = new FrameLayout(getParentActivity());
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(authorization.bot_id));
                if (user != null) {
                    name = UserObject.getFirstName(user);
                } else {
                    name = "";
                }
                CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cell.setText(LocaleController.formatString("TerminateWebSessionStop", R.string.TerminateWebSessionStop, name), "", false, false);
                cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout1.addView(cell, LayoutHelper.createFrame(-1.0f, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                cell.setOnClickListener(new View.OnClickListener(param) {
                    private final /* synthetic */ boolean[] f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        SessionsActivity.lambda$null$5(this.f$0, view);
                    }
                });
                builder2.setCustomViewOffset(16);
                builder2.setView(frameLayout1);
            }
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(i, param) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean[] f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    SessionsActivity.this.lambda$null$10$SessionsActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
        }
    }

    public /* synthetic */ void lambda$null$4$SessionsActivity(DialogInterface dialogInterface, int i) {
        if (this.currentType == 0) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_resetAuthorizations(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.lambda$null$1$SessionsActivity(tLObject, tL_error);
                }
            });
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetWebAuthorizations(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SessionsActivity.this.lambda$null$3$SessionsActivity(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$SessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$0$SessionsActivity(this.f$1, this.f$2);
            }
        });
        for (int a = 0; a < 3; a++) {
            UserConfig userConfig = UserConfig.getInstance(a);
            if (userConfig.isClientActivated()) {
                userConfig.registeredForPush = false;
                userConfig.saveConfig(false);
                MessagesController.getInstance(a).registerForPush(SharedConfig.pushString);
                ConnectionsManager.getInstance(a).setUserId(userConfig.getClientUserId());
            }
        }
    }

    public /* synthetic */ void lambda$null$0$SessionsActivity(TLRPC.TL_error error, TLObject response) {
        if (getParentActivity() != null && error == null && (response instanceof TLRPC.TL_boolTrue)) {
            ToastUtils.show((int) R.string.TerminateAllSessions);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$3$SessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$2$SessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$SessionsActivity(TLRPC.TL_error error, TLObject response) {
        if (getParentActivity() != null) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                ToastUtils.show((int) R.string.UnknownError);
            } else {
                ToastUtils.show((int) R.string.TerminateAllWebSessions);
            }
            finishFragment();
        }
    }

    static /* synthetic */ void lambda$null$5(boolean[] param, View v) {
        if (v.isEnabled()) {
            param[0] = !param[0];
            ((CheckBoxCell) v).setChecked(param[0], true);
        }
    }

    public /* synthetic */ void lambda$null$10$SessionsActivity(int position, boolean[] param, DialogInterface dialogInterface, int option) {
        TLRPC.TL_authorization authorization;
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
            if (this.currentType == 0) {
                int i = this.otherSessionsStartRow;
                if (position < i || position >= this.otherSessionsEndRow) {
                    authorization = (TLRPC.TL_authorization) this.passwordSessions.get(position - this.passwordSessionsStartRow);
                } else {
                    authorization = (TLRPC.TL_authorization) this.sessions.get(position - i);
                }
                TLRPC.TL_account_resetAuthorization req = new TLRPC.TL_account_resetAuthorization();
                req.hash = authorization.hash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog, authorization) {
                    private final /* synthetic */ AlertDialog f$1;
                    private final /* synthetic */ TLRPC.TL_authorization f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SessionsActivity.this.lambda$null$7$SessionsActivity(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_webAuthorization authorization2 = (TLRPC.TL_webAuthorization) this.sessions.get(position - this.otherSessionsStartRow);
            TLRPC.TL_account_resetWebAuthorization req2 = new TLRPC.TL_account_resetWebAuthorization();
            req2.hash = authorization2.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate(progressDialog, authorization2) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ TLRPC.TL_webAuthorization f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.lambda$null$9$SessionsActivity(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
            if (param[0]) {
                MessagesController.getInstance(this.currentAccount).blockUser(authorization2.bot_id);
            }
        }
    }

    public /* synthetic */ void lambda$null$7$SessionsActivity(AlertDialog progressDialog, TLRPC.TL_authorization authorization, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, authorization) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLRPC.TL_authorization f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$6$SessionsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$SessionsActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_authorization authorization) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (error == null) {
            this.sessions.remove(authorization);
            this.passwordSessions.remove(authorization);
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    public /* synthetic */ void lambda$null$9$SessionsActivity(AlertDialog progressDialog, TLRPC.TL_webAuthorization authorization, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, authorization) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLRPC.TL_webAuthorization f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$8$SessionsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$SessionsActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_webAuthorization authorization) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (error == null) {
            this.sessions.remove(authorization);
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.newSessionReceived) {
            loadSessions(true);
        }
    }

    private void loadSessions(boolean silent) {
        if (!this.loading) {
            if (!silent) {
                this.loading = true;
            }
            if (this.currentType == 0) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getAuthorizations(), new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SessionsActivity.this.lambda$loadSessions$13$SessionsActivity(tLObject, tL_error);
                    }
                }), this.classGuid);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getWebAuthorizations(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.lambda$loadSessions$15$SessionsActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$loadSessions$13$SessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$12$SessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$12$SessionsActivity(TLRPC.TL_error error, TLObject response) {
        this.loading = false;
        if (error == null) {
            this.sessions.clear();
            this.passwordSessions.clear();
            TLRPC.TL_account_authorizations res = (TLRPC.TL_account_authorizations) response;
            int N = res.authorizations.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_authorization authorization = res.authorizations.get(a);
                if ((authorization.flags & 1) != 0) {
                    this.currentSession = authorization;
                } else if (authorization.password_pending) {
                    this.passwordSessions.add(authorization);
                } else {
                    this.sessions.add(authorization);
                }
            }
            updateRows();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$loadSessions$15$SessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SessionsActivity.this.lambda$null$14$SessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$14$SessionsActivity(TLRPC.TL_error error, TLObject response) {
        this.loading = false;
        if (error == null) {
            this.sessions.clear();
            TLRPC.TL_account_webAuthorizations res = (TLRPC.TL_account_webAuthorizations) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.sessions.addAll(res.authorizations);
            updateRows();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentSession != null) {
            int i = 0 + 1;
            this.rowCount = i;
            this.currentSessionSectionRow = 0;
            this.rowCount = i + 1;
            this.currentSessionRow = i;
        } else {
            this.currentSessionRow = -1;
            this.currentSessionSectionRow = -1;
        }
        if (!this.passwordSessions.isEmpty() || !this.sessions.isEmpty()) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.terminateAllSessionsRow = i2;
            this.rowCount = i3 + 1;
            this.terminateAllSessionsDetailRow = i3;
            this.noOtherSessionsRow = -1;
        } else {
            this.terminateAllSessionsRow = -1;
            this.terminateAllSessionsDetailRow = -1;
            if (this.currentType == 1 || this.currentSession != null) {
                int i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.noOtherSessionsRow = i4;
            } else {
                this.noOtherSessionsRow = -1;
            }
        }
        if (this.passwordSessions.isEmpty()) {
            this.passwordSessionsDetailRow = -1;
            this.passwordSessionsEndRow = -1;
            this.passwordSessionsStartRow = -1;
            this.passwordSessionsSectionRow = -1;
        } else {
            int i5 = this.rowCount;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.passwordSessionsSectionRow = i5;
            this.passwordSessionsStartRow = i6;
            int size = i6 + this.passwordSessions.size();
            this.rowCount = size;
            this.passwordSessionsEndRow = size;
            this.rowCount = size + 1;
            this.passwordSessionsDetailRow = size;
        }
        if (this.sessions.isEmpty()) {
            this.otherSessionsSectionRow = -1;
            this.otherSessionsStartRow = -1;
            this.otherSessionsEndRow = -1;
            this.otherSessionsTerminateDetail = -1;
            return;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.otherSessionsSectionRow = i7;
        int i8 = i7 + 1;
        this.otherSessionsStartRow = i8;
        this.otherSessionsEndRow = i8 + this.sessions.size();
        int size2 = this.rowCount + this.sessions.size();
        this.rowCount = size2;
        this.rowCount = size2 + 1;
        this.otherSessionsTerminateDetail = size2;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == SessionsActivity.this.terminateAllSessionsRow || (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) || (position >= SessionsActivity.this.passwordSessionsStartRow && position < SessionsActivity.this.passwordSessionsEndRow);
        }

        public int getItemCount() {
            if (SessionsActivity.this.loading) {
                return 0;
            }
            return SessionsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (viewType == 2) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType != 3) {
                view = new SessionCell(this.mContext, SessionsActivity.this.currentType);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                view = SessionsActivity.this.emptyLayout;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v47, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v52, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v53, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v54, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v55, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v56, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v57, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v58, resolved type: boolean} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r7, int r8) {
            /*
                r6 = this;
                int r0 = r7.getItemViewType()
                r1 = 0
                if (r0 == 0) goto L_0x01e3
                r2 = 1
                if (r0 == r2) goto L_0x013e
                r3 = 2
                if (r0 == r3) goto L_0x00e2
                r3 = 3
                if (r0 == r3) goto L_0x00a6
                android.view.View r0 = r7.itemView
                im.bclpbkiauv.ui.cells.SessionCell r0 = (im.bclpbkiauv.ui.cells.SessionCell) r0
                im.bclpbkiauv.ui.SessionsActivity r3 = im.bclpbkiauv.ui.SessionsActivity.this
                int r3 = r3.currentSessionRow
                if (r8 != r3) goto L_0x0040
                im.bclpbkiauv.ui.SessionsActivity r3 = im.bclpbkiauv.ui.SessionsActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_authorization r3 = r3.currentSession
                im.bclpbkiauv.ui.SessionsActivity r4 = im.bclpbkiauv.ui.SessionsActivity.this
                java.util.ArrayList r4 = r4.sessions
                boolean r4 = r4.isEmpty()
                if (r4 == 0) goto L_0x003a
                im.bclpbkiauv.ui.SessionsActivity r4 = im.bclpbkiauv.ui.SessionsActivity.this
                java.util.ArrayList r4 = r4.passwordSessions
                boolean r4 = r4.isEmpty()
                if (r4 != 0) goto L_0x003b
            L_0x003a:
                r1 = 1
            L_0x003b:
                r0.setSession(r3, r1)
                goto L_0x021a
            L_0x0040:
                im.bclpbkiauv.ui.SessionsActivity r3 = im.bclpbkiauv.ui.SessionsActivity.this
                int r3 = r3.otherSessionsStartRow
                if (r8 < r3) goto L_0x0073
                im.bclpbkiauv.ui.SessionsActivity r3 = im.bclpbkiauv.ui.SessionsActivity.this
                int r3 = r3.otherSessionsEndRow
                if (r8 >= r3) goto L_0x0073
                im.bclpbkiauv.ui.SessionsActivity r3 = im.bclpbkiauv.ui.SessionsActivity.this
                java.util.ArrayList r3 = r3.sessions
                im.bclpbkiauv.ui.SessionsActivity r4 = im.bclpbkiauv.ui.SessionsActivity.this
                int r4 = r4.otherSessionsStartRow
                int r4 = r8 - r4
                java.lang.Object r3 = r3.get(r4)
                im.bclpbkiauv.tgnet.TLObject r3 = (im.bclpbkiauv.tgnet.TLObject) r3
                im.bclpbkiauv.ui.SessionsActivity r4 = im.bclpbkiauv.ui.SessionsActivity.this
                int r4 = r4.otherSessionsEndRow
                int r4 = r4 - r2
                if (r8 == r4) goto L_0x006e
                r1 = 1
            L_0x006e:
                r0.setSession(r3, r1)
                goto L_0x021a
            L_0x0073:
                im.bclpbkiauv.ui.SessionsActivity r3 = im.bclpbkiauv.ui.SessionsActivity.this
                int r3 = r3.passwordSessionsStartRow
                if (r8 < r3) goto L_0x021a
                im.bclpbkiauv.ui.SessionsActivity r3 = im.bclpbkiauv.ui.SessionsActivity.this
                int r3 = r3.passwordSessionsEndRow
                if (r8 >= r3) goto L_0x021a
                im.bclpbkiauv.ui.SessionsActivity r3 = im.bclpbkiauv.ui.SessionsActivity.this
                java.util.ArrayList r3 = r3.passwordSessions
                im.bclpbkiauv.ui.SessionsActivity r4 = im.bclpbkiauv.ui.SessionsActivity.this
                int r4 = r4.passwordSessionsStartRow
                int r4 = r8 - r4
                java.lang.Object r3 = r3.get(r4)
                im.bclpbkiauv.tgnet.TLObject r3 = (im.bclpbkiauv.tgnet.TLObject) r3
                im.bclpbkiauv.ui.SessionsActivity r4 = im.bclpbkiauv.ui.SessionsActivity.this
                int r4 = r4.passwordSessionsEndRow
                int r4 = r4 - r2
                if (r8 == r4) goto L_0x00a1
                r1 = 1
            L_0x00a1:
                r0.setSession(r3, r1)
                goto L_0x021a
            L_0x00a6:
                im.bclpbkiauv.ui.SessionsActivity r0 = im.bclpbkiauv.ui.SessionsActivity.this
                android.widget.LinearLayout r0 = r0.emptyLayout
                android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
                if (r0 == 0) goto L_0x021a
                r2 = 1130102784(0x435c0000, float:220.0)
                int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                android.graphics.Point r3 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r3 = r3.y
                int r4 = im.bclpbkiauv.ui.actionbar.ActionBar.getCurrentActionBarHeight()
                int r3 = r3 - r4
                r4 = 1124073472(0x43000000, float:128.0)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                int r3 = r3 - r4
                int r4 = android.os.Build.VERSION.SDK_INT
                r5 = 21
                if (r4 < r5) goto L_0x00d0
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.statusBarHeight
            L_0x00d0:
                int r3 = r3 - r1
                int r1 = java.lang.Math.max(r2, r3)
                r0.height = r1
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                android.widget.LinearLayout r1 = r1.emptyLayout
                r1.setLayoutParams(r0)
                goto L_0x021a
            L_0x00e2:
                android.view.View r0 = r7.itemView
                im.bclpbkiauv.ui.cells.HeaderCell r0 = (im.bclpbkiauv.ui.cells.HeaderCell) r0
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.currentSessionSectionRow
                if (r8 != r1) goto L_0x00fc
                r1 = 2131690769(0x7f0f0511, float:1.901059E38)
                java.lang.String r2 = "CurrentSession"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x021a
            L_0x00fc:
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.otherSessionsSectionRow
                if (r8 != r1) goto L_0x0128
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.currentType
                if (r1 != 0) goto L_0x011a
                r1 = 2131692543(0x7f0f0bff, float:1.901419E38)
                java.lang.String r2 = "OtherSessions"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x021a
            L_0x011a:
                r1 = 2131692544(0x7f0f0c00, float:1.9014191E38)
                java.lang.String r2 = "OtherWebSessions"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x021a
            L_0x0128:
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.passwordSessionsSectionRow
                if (r8 != r1) goto L_0x021a
                r1 = 2131691865(0x7f0f0959, float:1.9012814E38)
                java.lang.String r2 = "LoginAttempts"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x021a
            L_0x013e:
                android.view.View r0 = r7.itemView
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = (im.bclpbkiauv.ui.cells.TextInfoPrivacyCell) r0
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.terminateAllSessionsDetailRow
                r2 = 2131231060(0x7f080154, float:1.807819E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                if (r8 != r1) goto L_0x017c
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.currentType
                if (r1 != 0) goto L_0x0165
                r1 = 2131690619(0x7f0f047b, float:1.9010287E38)
                java.lang.String r4 = "ClearOtherSessionsHelp"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
                r0.setText(r1)
                goto L_0x0171
            L_0x0165:
                r1 = 2131690620(0x7f0f047c, float:1.9010289E38)
                java.lang.String r4 = "ClearOtherWebSessionsHelp"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r1)
                r0.setText(r1)
            L_0x0171:
                android.content.Context r1 = r6.mContext
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r1, (int) r2, (java.lang.String) r3)
                r0.setBackgroundDrawable(r1)
                goto L_0x021a
            L_0x017c:
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.otherSessionsTerminateDetail
                r4 = 2131231061(0x7f080155, float:1.8078192E38)
                if (r8 != r1) goto L_0x01b2
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.currentType
                if (r1 != 0) goto L_0x019c
                r1 = 2131694149(0x7f0f1245, float:1.9017446E38)
                java.lang.String r2 = "TerminateSessionInfo"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x01a8
            L_0x019c:
                r1 = 2131694151(0x7f0f1247, float:1.901745E38)
                java.lang.String r2 = "TerminateWebSessionInfo"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
            L_0x01a8:
                android.content.Context r1 = r6.mContext
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r1, (int) r4, (java.lang.String) r3)
                r0.setBackgroundDrawable(r1)
                goto L_0x021a
            L_0x01b2:
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.passwordSessionsDetailRow
                if (r8 != r1) goto L_0x021a
                r1 = 2131691866(0x7f0f095a, float:1.9012816E38)
                java.lang.String r5 = "LoginAttemptsInfo"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r1)
                r0.setText(r1)
                im.bclpbkiauv.ui.SessionsActivity r1 = im.bclpbkiauv.ui.SessionsActivity.this
                int r1 = r1.otherSessionsTerminateDetail
                r5 = -1
                if (r1 != r5) goto L_0x01d9
                android.content.Context r1 = r6.mContext
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r1, (int) r4, (java.lang.String) r3)
                r0.setBackgroundDrawable(r1)
                goto L_0x021a
            L_0x01d9:
                android.content.Context r1 = r6.mContext
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r1, (int) r2, (java.lang.String) r3)
                r0.setBackgroundDrawable(r1)
                goto L_0x021a
            L_0x01e3:
                android.view.View r0 = r7.itemView
                im.bclpbkiauv.ui.cells.TextSettingsCell r0 = (im.bclpbkiauv.ui.cells.TextSettingsCell) r0
                im.bclpbkiauv.ui.SessionsActivity r2 = im.bclpbkiauv.ui.SessionsActivity.this
                int r2 = r2.terminateAllSessionsRow
                if (r8 != r2) goto L_0x021a
                java.lang.String r2 = "windowBackgroundWhiteRedText2"
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                r0.setTextColor(r2)
                im.bclpbkiauv.ui.SessionsActivity r2 = im.bclpbkiauv.ui.SessionsActivity.this
                int r2 = r2.currentType
                if (r2 != 0) goto L_0x020e
                r2 = 2131694147(0x7f0f1243, float:1.9017442E38)
                java.lang.String r3 = "TerminateAllSessions"
                java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r1)
                goto L_0x021a
            L_0x020e:
                r2 = 2131694148(0x7f0f1244, float:1.9017444E38)
                java.lang.String r3 = "TerminateAllWebSessions"
                java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r1)
            L_0x021a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.SessionsActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int position) {
            if (position == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (position == SessionsActivity.this.terminateAllSessionsDetailRow || position == SessionsActivity.this.otherSessionsTerminateDetail || position == SessionsActivity.this.passwordSessionsDetailRow) {
                return 1;
            }
            if (position == SessionsActivity.this.currentSessionSectionRow || position == SessionsActivity.this.otherSessionsSectionRow || position == SessionsActivity.this.passwordSessionsSectionRow) {
                return 2;
            }
            if (position == SessionsActivity.this.noOtherSessionsRow) {
                return 3;
            }
            if (position == SessionsActivity.this.currentSessionRow) {
                return 4;
            }
            if (position >= SessionsActivity.this.otherSessionsStartRow && position < SessionsActivity.this.otherSessionsEndRow) {
                return 4;
            }
            if (position < SessionsActivity.this.passwordSessionsStartRow || position >= SessionsActivity.this.passwordSessionsEndRow) {
                return 0;
            }
            return 4;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sessions_devicesImage), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText2), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3)};
    }
}

package im.bclpbkiauv.ui.hui.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
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
import im.bclpbkiauv.ui.hui.mine.MrySessionsActivity;
import im.bclpbkiauv.ui.hviews.swipelist.SlidingItemMenuRecyclerView;
import java.util.ArrayList;

public class MrySessionsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
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
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    private SlidingItemMenuRecyclerView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    private Context mContext;
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

    public MrySessionsActivity(int type) {
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

    private void initActionBar() {
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
                    MrySessionsActivity.this.finishFragment();
                }
            }
        });
    }

    private void initList() {
        this.listView = (SlidingItemMenuRecyclerView) this.fragmentView.findViewById(R.id.listview);
        this.listAdapter = new ListAdapter(this.mContext);
        this.listView.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        this.listView.setHasFixedSize(true);
        this.listView.setNestedScrollingEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                MrySessionsActivity.this.lambda$initList$5$MrySessionsActivity(view, i);
            }
        });
    }

    public /* synthetic */ void lambda$initList$5$MrySessionsActivity(View view, int position) {
        if (position == this.terminateAllSessionsRow && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            if (this.currentType == 0) {
                builder.setMessage(LocaleController.getString("AreYouSureSessions", R.string.AreYouSureSessions));
            } else {
                builder.setMessage(LocaleController.getString("AreYouSureWebSessions", R.string.AreYouSureWebSessions));
            }
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MrySessionsActivity.this.lambda$null$4$MrySessionsActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$4$MrySessionsActivity(DialogInterface dialogInterface, int i) {
        if (this.currentType == 0) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_resetAuthorizations(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MrySessionsActivity.this.lambda$null$1$MrySessionsActivity(tLObject, tL_error);
                }
            });
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetWebAuthorizations(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MrySessionsActivity.this.lambda$null$3$MrySessionsActivity(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$MrySessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MrySessionsActivity.this.lambda$null$0$MrySessionsActivity(this.f$1, this.f$2);
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

    public /* synthetic */ void lambda$null$0$MrySessionsActivity(TLRPC.TL_error error, TLObject response) {
        if (getParentActivity() != null && error == null && (response instanceof TLRPC.TL_boolTrue)) {
            ToastUtils.show((int) R.string.TerminateAllSessions);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$3$MrySessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MrySessionsActivity.this.lambda$null$2$MrySessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$MrySessionsActivity(TLRPC.TL_error error, TLObject response) {
        if (getParentActivity() != null) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                ToastUtils.show((int) R.string.UnknownError);
            } else {
                ToastUtils.show((int) R.string.TerminateAllWebSessions);
            }
            finishFragment();
        }
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_slide_listview_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initList();
        LinearLayout linearLayout = new LinearLayout(context);
        this.emptyLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyLayout.setGravity(17);
        this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        if (this.currentType == 0) {
            imageView2.setImageResource(R.drawable.devices);
        } else {
            imageView2.setImageResource(R.drawable.no_apps);
        }
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_sessions_devicesImage), PorterDuff.Mode.MULTIPLY));
        this.emptyLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context);
        this.textView1 = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.textView1.setGravity(17);
        this.textView1.setTextSize(1, 15.0f);
        this.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        if (this.currentType == 0) {
            this.textView1.setText(LocaleController.getString("NoOtherSessions", R.string.NoOtherSessions));
        } else {
            this.textView1.setText(LocaleController.getString("NoOtherWebSessions", R.string.NoOtherWebSessions));
        }
        this.emptyLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
        TextView textView3 = new TextView(context);
        this.textView2 = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.textView2.setGravity(17);
        this.textView2.setTextSize(1, 14.0f);
        this.textView2.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        if (this.currentType == 0) {
            this.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", R.string.NoOtherSessionsInfo));
        } else {
            this.textView2.setText(LocaleController.getString("NoOtherWebSessionsInfo", R.string.NoOtherWebSessionsInfo));
        }
        this.emptyLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        return this.fragmentView;
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
                        MrySessionsActivity.this.lambda$loadSessions$7$MrySessionsActivity(tLObject, tL_error);
                    }
                }), this.classGuid);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getWebAuthorizations(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MrySessionsActivity.this.lambda$loadSessions$9$MrySessionsActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$loadSessions$7$MrySessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MrySessionsActivity.this.lambda$null$6$MrySessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$MrySessionsActivity(TLRPC.TL_error error, TLObject response) {
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

    public /* synthetic */ void lambda$loadSessions$9$MrySessionsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MrySessionsActivity.this.lambda$null$8$MrySessionsActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$MrySessionsActivity(TLRPC.TL_error error, TLObject response) {
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

    /* access modifiers changed from: private */
    public void updateRows() {
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
            return position == MrySessionsActivity.this.terminateAllSessionsRow || (position >= MrySessionsActivity.this.otherSessionsStartRow && position < MrySessionsActivity.this.otherSessionsEndRow) || (position >= MrySessionsActivity.this.passwordSessionsStartRow && position < MrySessionsActivity.this.passwordSessionsEndRow);
        }

        public int getItemCount() {
            if (MrySessionsActivity.this.loading) {
                return 0;
            }
            return MrySessionsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new TextSettingsCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType == 1 || viewType == 2) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (viewType == 3) {
                view = MrySessionsActivity.this.emptyLayout;
            } else if (viewType != 5) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_session_layout, (ViewGroup) null, false);
            } else {
                View view3 = new SessionCell(this.mContext, MrySessionsActivity.this.currentType);
                view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view3;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v42, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v46, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v47, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v60, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v61, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v80, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v94, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v107, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v108, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v112, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v113, resolved type: boolean} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
                r9 = this;
                int r0 = r10.getItemViewType()
                r1 = 0
                java.lang.String r2 = "windowBackgroundWhite"
                r3 = 1084227584(0x40a00000, float:5.0)
                r4 = 0
                if (r0 == 0) goto L_0x03b6
                r5 = 1
                if (r0 == r5) goto L_0x033f
                r6 = 2
                if (r0 == r6) goto L_0x02e3
                r6 = 3
                if (r0 == r6) goto L_0x02a7
                r6 = 5
                if (r0 == r6) goto L_0x0170
                android.view.View r0 = r10.itemView
                r6 = 2131297283(0x7f090403, float:1.8212507E38)
                android.view.View r0 = r0.findViewById(r6)
                im.bclpbkiauv.ui.cells.SessionCell r0 = (im.bclpbkiauv.ui.cells.SessionCell) r0
                android.view.View r6 = r10.itemView
                r7 = 2131296410(0x7f09009a, float:1.8210736E38)
                android.view.View r6 = r6.findViewById(r7)
                android.widget.TextView r6 = (android.widget.TextView) r6
                android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r1)
                r6.setBackground(r7)
                im.bclpbkiauv.ui.hui.mine.-$$Lambda$MrySessionsActivity$ListAdapter$ediA37uquFm7sygB67rBhroOQdI r7 = new im.bclpbkiauv.ui.hui.mine.-$$Lambda$MrySessionsActivity$ListAdapter$ediA37uquFm7sygB67rBhroOQdI
                r7.<init>(r11)
                r6.setOnClickListener(r7)
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.currentSessionRow
                if (r11 != r7) goto L_0x007e
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_authorization r7 = r7.currentSession
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r8 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                java.util.ArrayList r8 = r8.sessions
                boolean r8 = r8.isEmpty()
                if (r8 == 0) goto L_0x0063
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r8 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                java.util.ArrayList r8 = r8.passwordSessions
                boolean r8 = r8.isEmpty()
                if (r8 != 0) goto L_0x0064
            L_0x0063:
                r1 = 1
            L_0x0064:
                r0.setSession(r7, r1)
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r1, r3, r4, r4, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x007e:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.otherSessionsStartRow
                if (r11 < r7) goto L_0x00f7
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.otherSessionsEndRow
                if (r11 >= r7) goto L_0x00f7
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                java.util.ArrayList r7 = r7.sessions
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r8 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r8 = r8.otherSessionsStartRow
                int r8 = r11 - r8
                java.lang.Object r7 = r7.get(r8)
                im.bclpbkiauv.tgnet.TLObject r7 = (im.bclpbkiauv.tgnet.TLObject) r7
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r8 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r8 = r8.otherSessionsEndRow
                int r8 = r8 - r5
                if (r11 == r8) goto L_0x00ac
                r1 = 1
            L_0x00ac:
                r0.setSession(r7, r1)
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.otherSessionsStartRow
                if (r11 != r1) goto L_0x00ce
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r1, r3, r4, r4, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x00ce:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.otherSessionsEndRow
                int r1 = r1 - r5
                if (r11 != r1) goto L_0x00ee
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r4, r4, r1, r3, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x00ee:
                int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x0402
            L_0x00f7:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.passwordSessionsStartRow
                if (r11 < r7) goto L_0x0402
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.passwordSessionsEndRow
                if (r11 >= r7) goto L_0x0402
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                java.util.ArrayList r7 = r7.passwordSessions
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r8 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r8 = r8.passwordSessionsStartRow
                int r8 = r11 - r8
                java.lang.Object r7 = r7.get(r8)
                im.bclpbkiauv.tgnet.TLObject r7 = (im.bclpbkiauv.tgnet.TLObject) r7
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r8 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r8 = r8.passwordSessionsEndRow
                int r8 = r8 - r5
                if (r11 == r8) goto L_0x0125
                r1 = 1
            L_0x0125:
                r0.setSession(r7, r1)
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.passwordSessionsStartRow
                if (r11 != r1) goto L_0x0147
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r1, r3, r4, r4, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x0147:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.passwordSessionsEndRow
                int r1 = r1 - r5
                if (r11 != r1) goto L_0x0167
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r4, r4, r1, r3, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x0167:
                int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x0402
            L_0x0170:
                android.view.View r0 = r10.itemView
                im.bclpbkiauv.ui.cells.SessionCell r0 = (im.bclpbkiauv.ui.cells.SessionCell) r0
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r6 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r6 = r6.currentSessionRow
                if (r11 != r6) goto L_0x01b5
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r6 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                im.bclpbkiauv.tgnet.TLRPC$TL_authorization r6 = r6.currentSession
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                java.util.ArrayList r7 = r7.sessions
                boolean r7 = r7.isEmpty()
                if (r7 == 0) goto L_0x019a
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                java.util.ArrayList r7 = r7.passwordSessions
                boolean r7 = r7.isEmpty()
                if (r7 != 0) goto L_0x019b
            L_0x019a:
                r1 = 1
            L_0x019b:
                r0.setSession(r6, r1)
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r1, r3, r4, r4, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x01b5:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r6 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r6 = r6.otherSessionsStartRow
                if (r11 < r6) goto L_0x022e
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r6 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r6 = r6.otherSessionsEndRow
                if (r11 >= r6) goto L_0x022e
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r6 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                java.util.ArrayList r6 = r6.sessions
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.otherSessionsStartRow
                int r7 = r11 - r7
                java.lang.Object r6 = r6.get(r7)
                im.bclpbkiauv.tgnet.TLObject r6 = (im.bclpbkiauv.tgnet.TLObject) r6
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.otherSessionsEndRow
                int r7 = r7 - r5
                if (r11 == r7) goto L_0x01e3
                r1 = 1
            L_0x01e3:
                r0.setSession(r6, r1)
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.otherSessionsStartRow
                if (r11 != r1) goto L_0x0205
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r1, r3, r4, r4, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x0205:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.otherSessionsEndRow
                int r1 = r1 - r5
                if (r11 != r1) goto L_0x0225
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r4, r4, r1, r3, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x0225:
                int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x0402
            L_0x022e:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r6 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r6 = r6.passwordSessionsStartRow
                if (r11 < r6) goto L_0x0402
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r6 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r6 = r6.passwordSessionsEndRow
                if (r11 >= r6) goto L_0x0402
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r6 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                java.util.ArrayList r6 = r6.passwordSessions
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.passwordSessionsStartRow
                int r7 = r11 - r7
                java.lang.Object r6 = r6.get(r7)
                im.bclpbkiauv.tgnet.TLObject r6 = (im.bclpbkiauv.tgnet.TLObject) r6
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r7 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r7 = r7.passwordSessionsEndRow
                int r7 = r7 - r5
                if (r11 == r7) goto L_0x025c
                r1 = 1
            L_0x025c:
                r0.setSession(r6, r1)
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.passwordSessionsStartRow
                if (r11 != r1) goto L_0x027e
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r1, r3, r4, r4, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x027e:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.passwordSessionsStartRow
                int r1 = r1 - r5
                if (r11 != r1) goto L_0x029e
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r4, r4, r1, r3, r2)
                r0.setBackground(r1)
                goto L_0x0402
            L_0x029e:
                int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x0402
            L_0x02a7:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r0 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                android.widget.LinearLayout r0 = r0.emptyLayout
                android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
                if (r0 == 0) goto L_0x0402
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
                if (r4 < r5) goto L_0x02d1
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.statusBarHeight
            L_0x02d1:
                int r3 = r3 - r1
                int r1 = java.lang.Math.max(r2, r3)
                r0.height = r1
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                android.widget.LinearLayout r1 = r1.emptyLayout
                r1.setLayoutParams(r0)
                goto L_0x0402
            L_0x02e3:
                android.view.View r0 = r10.itemView
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = (im.bclpbkiauv.ui.cells.TextInfoPrivacyCell) r0
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.currentSessionSectionRow
                if (r11 != r1) goto L_0x02fd
                r1 = 2131690769(0x7f0f0511, float:1.901059E38)
                java.lang.String r2 = "CurrentSession"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x0402
            L_0x02fd:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.otherSessionsSectionRow
                if (r11 != r1) goto L_0x0329
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.currentType
                if (r1 != 0) goto L_0x031b
                r1 = 2131692543(0x7f0f0bff, float:1.901419E38)
                java.lang.String r2 = "OtherSessions"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x0402
            L_0x031b:
                r1 = 2131692544(0x7f0f0c00, float:1.9014191E38)
                java.lang.String r2 = "OtherWebSessions"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x0402
            L_0x0329:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.passwordSessionsSectionRow
                if (r11 != r1) goto L_0x0402
                r1 = 2131691865(0x7f0f0959, float:1.9012814E38)
                java.lang.String r2 = "LoginAttempts"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x0402
            L_0x033f:
                android.view.View r0 = r10.itemView
                im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r0 = (im.bclpbkiauv.ui.cells.TextInfoPrivacyCell) r0
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.terminateAllSessionsDetailRow
                if (r11 != r1) goto L_0x036f
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.currentType
                if (r1 != 0) goto L_0x0361
                r1 = 2131690619(0x7f0f047b, float:1.9010287E38)
                java.lang.String r2 = "ClearOtherSessionsHelp"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x0402
            L_0x0361:
                r1 = 2131690620(0x7f0f047c, float:1.9010289E38)
                java.lang.String r2 = "ClearOtherWebSessionsHelp"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x0402
            L_0x036f:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.otherSessionsTerminateDetail
                if (r11 != r1) goto L_0x039a
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.currentType
                if (r1 != 0) goto L_0x038d
                r1 = 2131694149(0x7f0f1245, float:1.9017446E38)
                java.lang.String r2 = "TerminateSessionInfo"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x0402
            L_0x038d:
                r1 = 2131694151(0x7f0f1247, float:1.901745E38)
                java.lang.String r2 = "TerminateWebSessionInfo"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                goto L_0x0402
            L_0x039a:
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.passwordSessionsDetailRow
                if (r11 != r1) goto L_0x0402
                r1 = 2131691866(0x7f0f095a, float:1.9012816E38)
                java.lang.String r2 = "LoginAttemptsInfo"
                java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
                r0.setText(r1)
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r1 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r1 = r1.otherSessionsTerminateDetail
                r2 = -1
                goto L_0x0402
            L_0x03b6:
                android.view.View r0 = r10.itemView
                im.bclpbkiauv.ui.cells.TextSettingsCell r0 = (im.bclpbkiauv.ui.cells.TextSettingsCell) r0
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r5 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r5 = r5.terminateAllSessionsRow
                if (r11 != r5) goto L_0x03ec
                java.lang.String r5 = "windowBackgroundWhiteRedText2"
                int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                r0.setTextColor(r5)
                im.bclpbkiauv.ui.hui.mine.MrySessionsActivity r5 = im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.this
                int r5 = r5.currentType
                if (r5 != 0) goto L_0x03e0
                r5 = 2131694147(0x7f0f1243, float:1.9017442E38)
                java.lang.String r6 = "TerminateAllSessions"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r0.setText(r5, r1)
                goto L_0x03ec
            L_0x03e0:
                r5 = 2131694148(0x7f0f1244, float:1.9017444E38)
                java.lang.String r6 = "TerminateAllWebSessions"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r0.setText(r5, r1)
            L_0x03ec:
                int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r1 = (float) r1
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
                android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r4, r4, r1, r3, r2)
                r0.setBackground(r1)
            L_0x0402:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.mine.MrySessionsActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public /* synthetic */ void lambda$onBindViewHolder$6$MrySessionsActivity$ListAdapter(int position, View v) {
            String name;
            int i = position;
            if (MrySessionsActivity.this.getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) MrySessionsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                boolean[] param = new boolean[1];
                if (MrySessionsActivity.this.currentType == 0) {
                    builder.setMessage(LocaleController.getString("TerminateSessionQuestion", R.string.TerminateSessionQuestion));
                } else {
                    TLRPC.TL_webAuthorization authorization = (TLRPC.TL_webAuthorization) MrySessionsActivity.this.sessions.get(i - MrySessionsActivity.this.otherSessionsStartRow);
                    builder.setMessage(LocaleController.formatString("TerminateWebSessionQuestion", R.string.TerminateWebSessionQuestion, authorization.domain));
                    FrameLayout frameLayout1 = new FrameLayout(MrySessionsActivity.this.getParentActivity());
                    TLRPC.User user = MessagesController.getInstance(MrySessionsActivity.this.currentAccount).getUser(Integer.valueOf(authorization.bot_id));
                    if (user != null) {
                        name = UserObject.getFirstName(user);
                    } else {
                        name = "";
                    }
                    CheckBoxCell cell = new CheckBoxCell(MrySessionsActivity.this.getParentActivity(), 1);
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
                            MrySessionsActivity.ListAdapter.lambda$null$0(this.f$0, view);
                        }
                    });
                    builder.setCustomViewOffset(16);
                    builder.setView(frameLayout1);
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(i, param) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ boolean[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        MrySessionsActivity.ListAdapter.this.lambda$null$5$MrySessionsActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                MrySessionsActivity.this.showDialog(builder.create());
            }
        }

        static /* synthetic */ void lambda$null$0(boolean[] param, View view) {
            if (view.isEnabled()) {
                param[0] = !param[0];
                ((CheckBoxCell) view).setChecked(param[0], true);
            }
        }

        public /* synthetic */ void lambda$null$5$MrySessionsActivity$ListAdapter(int position, boolean[] param, DialogInterface dialogInterface, int option) {
            TLRPC.TL_authorization authorization;
            if (MrySessionsActivity.this.getParentActivity() != null) {
                AlertDialog progressDialog = new AlertDialog(MrySessionsActivity.this.getParentActivity(), 3);
                progressDialog.setCanCancel(false);
                progressDialog.show();
                if (MrySessionsActivity.this.currentType == 0) {
                    if (position < MrySessionsActivity.this.otherSessionsStartRow || position >= MrySessionsActivity.this.otherSessionsEndRow) {
                        authorization = (TLRPC.TL_authorization) MrySessionsActivity.this.passwordSessions.get(position - MrySessionsActivity.this.passwordSessionsStartRow);
                    } else {
                        authorization = (TLRPC.TL_authorization) MrySessionsActivity.this.sessions.get(position - MrySessionsActivity.this.otherSessionsStartRow);
                    }
                    TLRPC.TL_account_resetAuthorization req = new TLRPC.TL_account_resetAuthorization();
                    req.hash = authorization.hash;
                    ConnectionsManager.getInstance(MrySessionsActivity.this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog, authorization) {
                        private final /* synthetic */ AlertDialog f$1;
                        private final /* synthetic */ TLRPC.TL_authorization f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MrySessionsActivity.ListAdapter.this.lambda$null$2$MrySessionsActivity$ListAdapter(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    });
                    return;
                }
                TLRPC.TL_webAuthorization authorization2 = (TLRPC.TL_webAuthorization) MrySessionsActivity.this.sessions.get(position - MrySessionsActivity.this.otherSessionsStartRow);
                TLRPC.TL_account_resetWebAuthorization req2 = new TLRPC.TL_account_resetWebAuthorization();
                req2.hash = authorization2.hash;
                ConnectionsManager.getInstance(MrySessionsActivity.this.currentAccount).sendRequest(req2, new RequestDelegate(progressDialog, authorization2) {
                    private final /* synthetic */ AlertDialog f$1;
                    private final /* synthetic */ TLRPC.TL_webAuthorization f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MrySessionsActivity.ListAdapter.this.lambda$null$4$MrySessionsActivity$ListAdapter(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
                if (param[0]) {
                    MessagesController.getInstance(MrySessionsActivity.this.currentAccount).blockUser(authorization2.bot_id);
                }
            }
        }

        public /* synthetic */ void lambda$null$2$MrySessionsActivity$ListAdapter(AlertDialog progressDialog, TLRPC.TL_authorization authorization, TLObject response, TLRPC.TL_error error) {
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
                    MrySessionsActivity.ListAdapter.this.lambda$null$1$MrySessionsActivity$ListAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$null$1$MrySessionsActivity$ListAdapter(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_authorization authorization) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (error == null) {
                MrySessionsActivity.this.sessions.remove(authorization);
                MrySessionsActivity.this.passwordSessions.remove(authorization);
                MrySessionsActivity.this.updateRows();
                if (MrySessionsActivity.this.listAdapter != null) {
                    MrySessionsActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        public /* synthetic */ void lambda$null$4$MrySessionsActivity$ListAdapter(AlertDialog progressDialog, TLRPC.TL_webAuthorization authorization, TLObject response, TLRPC.TL_error error) {
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
                    MrySessionsActivity.ListAdapter.this.lambda$null$3$MrySessionsActivity$ListAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$null$3$MrySessionsActivity$ListAdapter(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_webAuthorization authorization) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (error == null) {
                MrySessionsActivity.this.sessions.remove(authorization);
                MrySessionsActivity.this.updateRows();
                if (MrySessionsActivity.this.listAdapter != null) {
                    MrySessionsActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == MrySessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (position == MrySessionsActivity.this.terminateAllSessionsDetailRow || position == MrySessionsActivity.this.otherSessionsTerminateDetail || position == MrySessionsActivity.this.passwordSessionsDetailRow) {
                return 1;
            }
            if (position == MrySessionsActivity.this.currentSessionSectionRow || position == MrySessionsActivity.this.otherSessionsSectionRow || position == MrySessionsActivity.this.passwordSessionsSectionRow) {
                return 2;
            }
            if (position == MrySessionsActivity.this.noOtherSessionsRow) {
                return 3;
            }
            if (position >= MrySessionsActivity.this.otherSessionsStartRow && position < MrySessionsActivity.this.otherSessionsEndRow) {
                return 4;
            }
            if (position >= MrySessionsActivity.this.passwordSessionsStartRow && position < MrySessionsActivity.this.passwordSessionsEndRow) {
                return 4;
            }
            if (position == MrySessionsActivity.this.currentSessionRow) {
                return 5;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sessions_devicesImage), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText2), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3)};
    }
}

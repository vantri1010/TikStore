package im.bclpbkiauv.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ColorRelativeLayout;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import im.bclpbkiauv.ui.dialogs.DialogCommonList;
import im.bclpbkiauv.ui.fragments.CallRecordsFragment;
import im.bclpbkiauv.ui.hcells.MryDividerCell;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.decoration.TopBottomDecoration;
import im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity;
import im.bclpbkiauv.ui.hviews.MryEmptyTextProgressView;
import im.bclpbkiauv.ui.hviews.swipelist.SlidingItemMenuRecyclerView;
import im.bclpbkiauv.ui.newcall.AddNewCallActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CallRecordsFragment extends BaseFmts implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    private static final int TYPE_IN = 1;
    private static final int TYPE_MISSED = 2;
    private static final int TYPE_OUT = 0;
    private ArrayList<CallLogRow> allCalls = new ArrayList<>();
    private View.OnClickListener callBtnClickListener = new View.OnClickListener() {
        public final void onClick(View view) {
            CallRecordsFragment.this.lambda$new$6$CallRecordsFragment(view);
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<CallLogRow> calls = new ArrayList<>();
    private ArrayList<CallLogRow> cancelCalls = new ArrayList<>();
    private MryEmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoaded;
    private TLRPC.User lastCallUser;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private SlidingItemMenuRecyclerView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading;
    private ImageView mIvAdd;
    private ImageView mIvBack;
    private RelativeLayout mRlBack;
    private TextView m_tvAll;
    private TextView m_tvCancel;
    private TextView m_tvCurrent;
    private View tabContainer;

    private class CallLogRow {
        public List<TLRPC.Message> calls;
        public int type;
        public TLRPC.User user;

        private CallLogRow() {
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
    }

    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.fragmentView = LayoutInflater.from(this.context).inflate(R.layout.activity_new_call, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initTitleBar();
        initView(this.context);
        return this.fragmentView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCalls(0, 50);
    }

    private void initTitleBar() {
        FrameLayout flTitleBarContainer = (FrameLayout) this.fragmentView.findViewById(R.id.fl_title_bar_container);
        flTitleBarContainer.setBackground(this.defaultActionBarBackgroundDrawable);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) flTitleBarContainer.getLayoutParams();
        layoutParams.height = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        flTitleBarContainer.setLayoutParams(layoutParams);
        flTitleBarContainer.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        this.mRlBack = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_back);
        this.mIvBack = (ImageView) this.fragmentView.findViewById(R.id.iv_back);
        this.mIvAdd = (ImageView) this.fragmentView.findViewById(R.id.iv_add);
        this.mIvBack.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        this.mIvBack.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarDefaultSelector)));
        this.mIvAdd.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        this.mIvAdd.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarDefaultSelector)));
        this.m_tvAll = (TextView) this.fragmentView.findViewById(R.id.tv_all_call);
        this.m_tvCancel = (TextView) this.fragmentView.findViewById(R.id.tv_cancel_call);
        this.m_tvCurrent = this.m_tvAll;
    }

    private void initView(Context context) {
        this.tabContainer = this.fragmentView.findViewById(R.id.tabContainer);
        FrameLayout flContainer = (FrameLayout) this.fragmentView.findViewById(R.id.fl_container);
        flContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        MryEmptyTextProgressView mryEmptyTextProgressView = new MryEmptyTextProgressView(context);
        this.emptyView = mryEmptyTextProgressView;
        mryEmptyTextProgressView.setText(LocaleController.getString("NoCallRecords", R.string.NoCallRecords));
        this.emptyView.setTopImage(R.mipmap.img_empty_default);
        flContainer.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        SlidingItemMenuRecyclerView slidingItemMenuRecyclerView = new SlidingItemMenuRecyclerView(context);
        this.listView = slidingItemMenuRecyclerView;
        slidingItemMenuRecyclerView.addItemDecoration(new TopBottomDecoration());
        this.listView.setEmptyView(this.emptyView);
        int i = 2;
        this.listView.setOverScrollMode(2);
        this.listView.setVerticalScrollBarEnabled(false);
        SlidingItemMenuRecyclerView slidingItemMenuRecyclerView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        slidingItemMenuRecyclerView2.setAdapter(listAdapter);
        SlidingItemMenuRecyclerView slidingItemMenuRecyclerView3 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        slidingItemMenuRecyclerView3.setLayoutManager(linearLayoutManager);
        SlidingItemMenuRecyclerView slidingItemMenuRecyclerView4 = this.listView;
        if (LocaleController.isRTL) {
            i = 1;
        }
        slidingItemMenuRecyclerView4.setVerticalScrollbarPosition(i);
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        flContainer.addView(this.listView, LayoutHelper.createFrame(-1, -2.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CallRecordsFragment.this.lambda$initView$0$CallRecordsFragment(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = CallRecordsFragment.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(CallRecordsFragment.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (visibleItemCount > 0) {
                    int totalItemCount = CallRecordsFragment.this.listViewAdapter.getItemCount();
                    if (!CallRecordsFragment.this.endReached && !CallRecordsFragment.this.loading && !CallRecordsFragment.this.calls.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                        AndroidUtilities.runOnUIThread(new Runnable((CallLogRow) CallRecordsFragment.this.calls.get(CallRecordsFragment.this.calls.size() - 1)) {
                            private final /* synthetic */ CallRecordsFragment.CallLogRow f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                CallRecordsFragment.AnonymousClass1.this.lambda$onScrolled$0$CallRecordsFragment$1(this.f$1);
                            }
                        });
                    }
                }
            }

            public /* synthetic */ void lambda$onScrolled$0$CallRecordsFragment$1(CallLogRow row) {
                CallRecordsFragment.this.getCalls(row.calls.get(row.calls.size() - 1).id, 100);
            }
        });
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        initListener();
        changeTabState(this.m_tvCurrent);
    }

    public /* synthetic */ void lambda$initView$0$CallRecordsFragment(View view, int position) {
        Log.e("tag", "position-->" + position);
        if (position >= 0 && position < this.calls.size()) {
            showAlert(this.calls.get(position).user);
        }
    }

    private void initListener() {
        this.m_tvAll.setOnClickListener(this);
        this.m_tvCancel.setOnClickListener(this);
        this.mRlBack.setOnClickListener(this);
        this.mIvAdd.setOnClickListener(this);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_add) {
            presentFragment(new AddNewCallActivity());
        } else if (id != R.id.tv_all_call) {
            if (id == R.id.tv_cancel_call && this.m_tvCurrent.getId() != v.getId()) {
                changeTabState(v);
                this.m_tvCurrent = this.m_tvCancel;
                this.emptyView.setText(LocaleController.getString("NoCancelCallLog", R.string.NoCancelCallLog));
                ListAdapter listAdapter = this.listViewAdapter;
                if (listAdapter != null && listAdapter != null) {
                    this.calls.clear();
                    this.calls.addAll(this.cancelCalls);
                    this.listViewAdapter.notifyDataSetChanged();
                }
            }
        } else if (this.m_tvCurrent.getId() != v.getId()) {
            changeTabState(v);
            this.m_tvCurrent = this.m_tvAll;
            this.emptyView.setTopImage(R.mipmap.img_empty_default);
            this.emptyView.setText(LocaleController.getString("NoCallRecords", R.string.NoCallRecords));
            if (this.listViewAdapter != null) {
                this.calls.clear();
                this.calls.addAll(this.allCalls);
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void changeTabState(View view) {
        if (view.getId() == this.m_tvAll.getId()) {
            if (this.tabContainer.getBackground() != null) {
                this.tabContainer.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), PorterDuff.Mode.SRC_IN));
            }
            this.m_tvAll.setTextColor(-1);
            this.m_tvAll.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), 0.0f, (float) AndroidUtilities.dp(5.0f), 0.0f, Theme.getColor(Theme.key_windowBackgroundWhiteBlueText)));
            this.m_tvCancel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            this.m_tvCancel.setBackground(Theme.createRoundRectDrawable(0.0f, (float) AndroidUtilities.dp(5.0f), 0.0f, (float) AndroidUtilities.dp(5.0f), 0));
            return;
        }
        if (this.tabContainer.getBackground() != null) {
            this.tabContainer.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), PorterDuff.Mode.SRC_IN));
        }
        this.m_tvCancel.setTextColor(-1);
        this.m_tvCancel.setBackground(Theme.createRoundRectDrawable(0.0f, (float) AndroidUtilities.dp(5.0f), 0.0f, (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhiteBlueText)));
        this.m_tvAll.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        this.m_tvAll.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), 0.0f, (float) AndroidUtilities.dp(5.0f), 0.0f, 0));
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ListAdapter listAdapter;
        int i = id;
        if (i != NotificationCenter.didReceiveNewMessages || !this.firstLoaded) {
            if (i == NotificationCenter.messagesDeleted && this.firstLoaded && !args[2].booleanValue()) {
                boolean didChange = false;
                ArrayList<Integer> ids = args[0];
                Iterator<CallLogRow> itrtr = this.calls.iterator();
                while (itrtr.hasNext()) {
                    CallLogRow row = itrtr.next();
                    Iterator<TLRPC.Message> msgs = row.calls.iterator();
                    while (msgs.hasNext()) {
                        if (ids.contains(Integer.valueOf(msgs.next().id))) {
                            didChange = true;
                            msgs.remove();
                        }
                    }
                    if (row.calls.size() == 0) {
                        itrtr.remove();
                        this.allCalls.remove(row);
                        if (this.m_tvCurrent == this.m_tvCancel) {
                            this.cancelCalls.remove(row);
                        }
                    }
                }
                if (didChange && (listAdapter = this.listViewAdapter) != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        } else if (!args[2].booleanValue()) {
            Iterator<MessageObject> it = args[1].iterator();
            while (it.hasNext()) {
                MessageObject msg = it.next();
                if (msg.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                    int userID = msg.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? msg.messageOwner.to_id.user_id : msg.messageOwner.from_id;
                    int callType = msg.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : 1;
                    TLRPC.PhoneCallDiscardReason reason = msg.messageOwner.action.reason;
                    if (callType == 1 && ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) || msg.messageOwner.action.duration == -3 || msg.messageOwner.action.duration == -4 || msg.messageOwner.action.duration == -5)) {
                        callType = 2;
                    }
                    if (this.calls.size() > 0) {
                        CallLogRow topRow = this.calls.get(0);
                        if (topRow.user.id == userID && topRow.type == callType) {
                            if (this.m_tvCurrent == this.m_tvAll || topRow.type == 2) {
                                topRow.calls.add(0, msg.messageOwner);
                                this.listViewAdapter.notifyItemChanged(0);
                            }
                        }
                    }
                    CallLogRow row2 = new CallLogRow();
                    row2.calls = new ArrayList();
                    row2.calls.add(msg.messageOwner);
                    row2.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userID));
                    row2.type = callType;
                    if ((this.m_tvCurrent == this.m_tvAll || row2.type == 2) && !this.calls.contains(row2)) {
                        this.calls.add(0, row2);
                        this.listViewAdapter.notifyItemInserted(0);
                    }
                    if (!this.allCalls.contains(row2)) {
                        this.allCalls.add(0, row2);
                    }
                    if (row2.type == 2 && !this.cancelCalls.contains(row2)) {
                        this.cancelCalls.add(0, row2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void getCalls(int max_id, int count) {
        if (!this.loading) {
            this.loading = true;
            MryEmptyTextProgressView mryEmptyTextProgressView = this.emptyView;
            if (mryEmptyTextProgressView != null && !this.firstLoaded) {
                mryEmptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.limit = count;
            req.peer = new TLRPC.TL_inputPeerEmpty();
            req.filter = new TLRPC.TL_inputMessagesFilterPhoneCalls();
            req.q = "";
            req.offset_id = max_id;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    CallRecordsFragment.this.lambda$getCalls$2$CallRecordsFragment(tLObject, tL_error);
                }
            }, 2), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getCalls$2$CallRecordsFragment(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CallRecordsFragment.this.lambda$null$1$CallRecordsFragment(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$CallRecordsFragment(TLRPC.TL_error error, TLObject response) {
        CallLogRow currentRow;
        if (error == null) {
            SparseArray<TLRPC.User> users = new SparseArray<>();
            TLRPC.messages_Messages msgs = (TLRPC.messages_Messages) response;
            this.endReached = msgs.messages.isEmpty();
            for (int a = 0; a < msgs.users.size(); a++) {
                TLRPC.User user = msgs.users.get(a);
                users.put(user.id, user);
            }
            if (this.allCalls.size() > 0) {
                ArrayList<CallLogRow> arrayList = this.allCalls;
                currentRow = arrayList.get(arrayList.size() - 1);
            } else {
                currentRow = null;
            }
            for (int a2 = 0; a2 < msgs.messages.size(); a2++) {
                TLRPC.Message msg = msgs.messages.get(a2);
                if (msg.action != null && !(msg.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                    int callType = msg.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : 1;
                    TLRPC.PhoneCallDiscardReason reason = msg.action.reason;
                    if (callType == 1 && ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) || msg.action.duration == -3 || msg.action.duration == -4 || msg.action.duration == -5)) {
                        callType = 2;
                    }
                    int userID = msg.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? msg.to_id.user_id : msg.from_id;
                    if (!(currentRow != null && currentRow.user.id == userID && currentRow.type == callType)) {
                        if (currentRow != null && !this.allCalls.contains(currentRow)) {
                            this.allCalls.add(currentRow);
                            if (currentRow.type == 2 && !this.cancelCalls.contains(currentRow)) {
                                this.cancelCalls.add(currentRow);
                            }
                        }
                        CallLogRow row = new CallLogRow();
                        row.calls = new ArrayList();
                        row.user = users.get(userID);
                        row.type = callType;
                        currentRow = row;
                    }
                    currentRow.calls.add(msg);
                }
            }
            if (currentRow != null && currentRow.calls.size() > 0 && !this.allCalls.contains(currentRow)) {
                this.allCalls.add(currentRow);
                if (currentRow.type == 2 && !this.cancelCalls.contains(currentRow)) {
                    this.cancelCalls.add(currentRow);
                }
            }
        } else {
            this.endReached = true;
        }
        this.loading = false;
        this.firstLoaded = true;
        MryEmptyTextProgressView mryEmptyTextProgressView = this.emptyView;
        if (mryEmptyTextProgressView != null) {
            mryEmptyTextProgressView.showTextView();
        }
        if (this.listViewAdapter != null) {
            this.calls.clear();
            if (this.m_tvCurrent == this.m_tvCancel) {
                this.calls.addAll(this.cancelCalls);
            } else {
                this.calls.addAll(this.allCalls);
            }
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void confirmAndDelete(CallLogRow row) {
        if (getParentActivity() != null) {
            new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("ConfirmDeleteCallLog", R.string.ConfirmDeleteCallLog)).setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener(row) {
                private final /* synthetic */ CallRecordsFragment.CallLogRow f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    CallRecordsFragment.this.lambda$confirmAndDelete$3$CallRecordsFragment(this.f$1, dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null).show().setCanceledOnTouchOutside(true);
        }
    }

    public /* synthetic */ void lambda$confirmAndDelete$3$CallRecordsFragment(CallLogRow row, DialogInterface dialog, int which) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (TLRPC.Message msg : row.calls) {
            ids.add(Integer.valueOf(msg.id));
        }
        MessagesController.getInstance(this.currentAccount).deleteMessages(ids, (ArrayList<Long>) null, (TLRPC.EncryptedChat) null, 0, 0, false, false);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void showAlert(TLRPC.User user) {
        List<String> list = new ArrayList<>();
        list.add(LocaleController.getString("menu_voice_chat", R.string.menu_voice_chat));
        list.add(LocaleController.getString("menu_video_chat", R.string.menu_video_chat));
        List<Integer> list1 = new ArrayList<>();
        list1.add(Integer.valueOf(R.drawable.menu_voice_call));
        list1.add(Integer.valueOf(R.drawable.menu_video_call));
        new DialogCommonList(getParentActivity(), list, list1, Color.parseColor("#222222"), (DialogCommonList.RecyclerviewItemClickCallBack) new DialogCommonList.RecyclerviewItemClickCallBack(user) {
            private final /* synthetic */ TLRPC.User f$1;

            {
                this.f$1 = r2;
            }

            public final void onRecyclerviewItemClick(int i) {
                CallRecordsFragment.this.lambda$showAlert$4$CallRecordsFragment(this.f$1, i);
            }
        }, 1).show();
    }

    public /* synthetic */ void lambda$showAlert$4$CallRecordsFragment(TLRPC.User user, int position) {
        if (position == 0) {
            if (ApplicationLoader.mbytAVideoCallBusy != 0) {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_busing_tip", R.string.visual_call_busing_tip));
            } else if (user.mutual_contact) {
                int currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
                if (currentConnectionState == 2 || currentConnectionState == 1) {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_network", R.string.visual_call_no_network));
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getParentActivity(), VisualCallActivity.class);
                intent.putExtra("CallType", 1);
                ArrayList<Integer> ArrInputPeers = new ArrayList<>();
                ArrInputPeers.add(Integer.valueOf(user.id));
                intent.putExtra("ArrayUser", ArrInputPeers);
                intent.putExtra("channel", new ArrayList());
                getParentActivity().startActivity(intent);
            } else {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
            }
        } else if (position != 1) {
        } else {
            if (ApplicationLoader.mbytAVideoCallBusy != 0) {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_busing_tip", R.string.visual_call_busing_tip));
            } else if (user.mutual_contact) {
                int currentConnectionState2 = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
                if (currentConnectionState2 == 2 || currentConnectionState2 == 1) {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_network", R.string.visual_call_no_network));
                    return;
                }
                Intent intent2 = new Intent();
                intent2.setClass(getParentActivity(), VisualCallActivity.class);
                intent2.putExtra("CallType", 2);
                ArrayList<Integer> ArrInputPeers2 = new ArrayList<>();
                ArrInputPeers2.add(Integer.valueOf(user.id));
                intent2.putExtra("ArrayUser", ArrInputPeers2);
                intent2.putExtra("channel", new ArrayList());
                getParentActivity().startActivity(intent2);
            } else {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return CallRecordsFragment.this.calls.size();
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Drawable drawable;
            Drawable drawable2;
            Drawable drawable3;
            Drawable drawable4;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            ColorTextView tvCallType = (ColorTextView) viewHolder.itemView.findViewById(R.id.tv_call_type);
            ColorTextView tvName = (ColorTextView) viewHolder.itemView.findViewById(R.id.tv_nick_name);
            CallLogRow row = (CallLogRow) CallRecordsFragment.this.calls.get(i);
            TLRPC.Message last = row.calls.get(0);
            Object obj = "";
            String ldir = LocaleController.isRTL ? "‫" : obj;
            SpannableString subtitle = new SpannableString(ldir + "  " + LocaleController.formatDateCallLog((long) last.date));
            StringBuilder sb = new StringBuilder();
            sb.append("onbindview = ");
            sb.append(row.calls.get(0).date);
            sb.append(" ");
            Object obj2 = obj;
            if (row.calls.size() > 1) {
                obj2 = Integer.valueOf(row.calls.get(1).date);
            }
            sb.append(obj2);
            KLog.d(sb.toString());
            int i2 = row.type;
            if (i2 == 0) {
                if ((last.action.flags & 4) != 0) {
                    drawable = this.mContext.getResources().getDrawable(R.drawable.new_call_video_out);
                    drawable.setBounds(0, 0, 42, 42);
                    tvCallType.setText(LocaleController.getString("new_call_video_out", R.string.new_call_video_out));
                } else {
                    drawable = this.mContext.getResources().getDrawable(R.mipmap.ic_new_call_out);
                    drawable.setBounds(0, 0, 42, 42);
                    tvCallType.setText(LocaleController.getString("new_call_voice_out", R.string.new_call_voice_out));
                }
                tvCallType.setCompoundDrawables(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
                tvName.setTextColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
            } else if (i2 == 1) {
                if (last.action.duration == -3 || last.action.duration == -4 || last.action.duration == -5) {
                    if ((last.action.flags & 4) != 0) {
                        drawable3 = this.mContext.getResources().getDrawable(R.drawable.new_call_video_no_answer);
                        drawable3.setBounds(0, 0, 42, 42);
                        tvCallType.setText(LocaleController.getString("new_call_video_no_answer", R.string.new_call_video_no_answer));
                    } else {
                        drawable3 = this.mContext.getResources().getDrawable(R.mipmap.ic_new_call_cancel);
                        drawable3.setBounds(0, 0, 42, 42);
                        tvCallType.setText(LocaleController.getString("new_call_voice_no_answer", R.string.new_call_voice_no_answer));
                    }
                    tvName.setTextColor(-570319);
                } else {
                    if ((last.action.flags & 4) != 0) {
                        drawable2 = this.mContext.getResources().getDrawable(R.drawable.new_call_video_in);
                        drawable2.setBounds(0, 0, 42, 42);
                        tvCallType.setText(LocaleController.getString("new_call_video_in", R.string.new_call_video_in));
                    } else {
                        drawable2 = this.mContext.getResources().getDrawable(R.mipmap.ic_new_call_in);
                        drawable2.setBounds(0, 0, 42, 42);
                        tvCallType.setText(LocaleController.getString("new_call_voice_in", R.string.new_call_voice_in));
                    }
                    tvName.setTextColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
                }
                tvCallType.setCompoundDrawables(drawable2, (Drawable) null, (Drawable) null, (Drawable) null);
            } else if (i2 == 2) {
                if ((last.action.flags & 4) != 0) {
                    drawable4 = this.mContext.getResources().getDrawable(R.drawable.new_call_video_no_answer);
                    drawable4.setBounds(0, 0, 42, 42);
                    tvCallType.setText(LocaleController.getString("new_call_video_no_answer", R.string.new_call_video_no_answer));
                } else {
                    drawable4 = this.mContext.getResources().getDrawable(R.mipmap.ic_new_call_cancel);
                    drawable4.setBounds(0, 0, 42, 42);
                    tvCallType.setText(LocaleController.getString("new_call_voice_no_answer", R.string.new_call_voice_no_answer));
                }
                tvName.setTextColor(-570319);
                tvCallType.setCompoundDrawables(drawable4, (Drawable) null, (Drawable) null, (Drawable) null);
            }
            ((ColorTextView) viewHolder.itemView.findViewById(R.id.tv_date)).setText(subtitle);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(row.user);
            BackupImageView iv_header = (BackupImageView) viewHolder.itemView.findViewById(R.id.iv_head_img);
            iv_header.setImage(ImageLocation.getForUser(row.user, false), "50_50", (Drawable) avatarDrawable, (Object) row.user);
            iv_header.setRoundRadius(AndroidUtilities.dp(7.5f));
            viewHolder.itemView.findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener(row) {
                private final /* synthetic */ CallRecordsFragment.CallLogRow f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    CallRecordsFragment.ListAdapter.this.lambda$onBindViewHolder$0$CallRecordsFragment$ListAdapter(this.f$1, view);
                }
            });
            viewHolder.itemView.findViewById(R.id.iv_more).setTag(row);
            tvName.setText(UserObject.getName(row.user));
            tvName.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            ColorRelativeLayout rlContent = (ColorRelativeLayout) viewHolder.itemView.findViewById(R.id.rl_content);
            MryDividerCell divider = (MryDividerCell) viewHolder.itemView.findViewById(R.id.divider);
            if (getItemCount() == 1) {
                rlContent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                divider.setVisibility(8);
                ColorTextView colorTextView = tvCallType;
            } else if (i == 0) {
                ColorTextView colorTextView2 = tvCallType;
                rlContent.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
            } else {
                if (i == getItemCount() - 1) {
                    divider.setVisibility(8);
                    rlContent.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else {
                    rlContent.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    divider.setVisibility(0);
                }
            }
            ((TextView) viewHolder.itemView.findViewById(R.id.btnDelete)).setOnClickListener(new View.OnClickListener(i, row) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ CallRecordsFragment.CallLogRow f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    CallRecordsFragment.ListAdapter.this.lambda$onBindViewHolder$2$CallRecordsFragment$ListAdapter(this.f$1, this.f$2, view);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$CallRecordsFragment$ListAdapter(CallLogRow row, View v) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", row.user.id);
            CallRecordsFragment.this.presentFragment(new NewProfileActivity(bundle));
        }

        public /* synthetic */ void lambda$onBindViewHolder$2$CallRecordsFragment$ListAdapter(int position, CallLogRow row, View v) {
            if (position >= 0 && position < CallRecordsFragment.this.calls.size()) {
                CallLogRow callLogRow = (CallLogRow) CallRecordsFragment.this.calls.get(position);
                ArrayList<String> items = new ArrayList<>();
                items.add(LocaleController.getString("Delete", R.string.Delete));
                if (VoIPHelper.canRateCall((TLRPC.TL_messageActionPhoneCall) callLogRow.calls.get(0).action)) {
                    items.add(LocaleController.getString("CallMessageReportProblem", R.string.CallMessageReportProblem));
                }
                new AlertDialog.Builder((Context) CallRecordsFragment.this.getParentActivity()).setTitle(LocaleController.getString("Calls", R.string.Calls)).setItems((CharSequence[]) items.toArray(new String[0]), new DialogInterface.OnClickListener(row, callLogRow) {
                    private final /* synthetic */ CallRecordsFragment.CallLogRow f$1;
                    private final /* synthetic */ CallRecordsFragment.CallLogRow f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        CallRecordsFragment.ListAdapter.this.lambda$null$1$CallRecordsFragment$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                    }
                }).show();
            }
        }

        public /* synthetic */ void lambda$null$1$CallRecordsFragment$ListAdapter(CallLogRow row, CallLogRow callLogRow, DialogInterface dialog, int which) {
            if (which == 0) {
                CallRecordsFragment.this.confirmAndDelete(row);
            } else if (which == 1) {
                VoIPHelper.showRateAlert(CallRecordsFragment.this.getParentActivity(), (TLRPC.TL_messageActionPhoneCall) callLogRow.calls.get(0).action);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemViewType(int position) {
            return position;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_call, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(71.0f));
            layoutParams.leftMargin = AndroidUtilities.dp(10.0f);
            layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
            view.setLayoutParams(layoutParams);
            ((ImageView) view.findViewById(R.id.iv_more)).setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
            ((BackupImageView) view.findViewById(R.id.iv_head_img)).setRoundRadius(AndroidUtilities.dp(7.5f));
            return new RecyclerListView.Holder(view);
        }
    }

    public /* synthetic */ void lambda$new$6$CallRecordsFragment(View v) {
        List<String> list = new ArrayList<>();
        list.add(LocaleController.getString("menu_voice_chat", R.string.menu_voice_chat));
        list.add(LocaleController.getString("menu_video_chat", R.string.menu_video_chat));
        List<Integer> list1 = new ArrayList<>();
        list1.add(Integer.valueOf(R.drawable.menu_voice_call));
        list1.add(Integer.valueOf(R.drawable.menu_video_call));
        new DialogCommonList(getParentActivity(), list, list1, Color.parseColor("#222222"), (DialogCommonList.RecyclerviewItemClickCallBack) new DialogCommonList.RecyclerviewItemClickCallBack((CallLogRow) v.getTag()) {
            private final /* synthetic */ CallRecordsFragment.CallLogRow f$1;

            {
                this.f$1 = r2;
            }

            public final void onRecyclerviewItemClick(int i) {
                CallRecordsFragment.this.lambda$null$5$CallRecordsFragment(this.f$1, i);
            }
        }, 1).show();
    }

    public /* synthetic */ void lambda$null$5$CallRecordsFragment(CallLogRow row, int position) {
        if (position == 0) {
            if (ApplicationLoader.mbytAVideoCallBusy != 0) {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_busing_tip", R.string.visual_call_busing_tip));
            } else if (row.user.mutual_contact) {
                int currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
                if (currentConnectionState == 2 || currentConnectionState == 1) {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_network", R.string.visual_call_no_network));
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getParentActivity(), VisualCallActivity.class);
                intent.putExtra("CallType", 1);
                ArrayList<Integer> ArrInputPeers = new ArrayList<>();
                ArrInputPeers.add(Integer.valueOf(row.user.id));
                intent.putExtra("ArrayUser", ArrInputPeers);
                intent.putExtra("channel", new ArrayList());
                getParentActivity().startActivity(intent);
            } else {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
            }
        } else if (position != 1) {
        } else {
            if (ApplicationLoader.mbytAVideoCallBusy != 0) {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_busing_tip", R.string.visual_call_busing_tip));
            } else if (row.user.mutual_contact) {
                int currentConnectionState2 = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
                if (currentConnectionState2 == 2 || currentConnectionState2 == 1) {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_network", R.string.visual_call_no_network));
                    return;
                }
                Intent intent2 = new Intent();
                intent2.setClass(getParentActivity(), VisualCallActivity.class);
                intent2.putExtra("CallType", 2);
                ArrayList<Integer> ArrInputPeers2 = new ArrayList<>();
                ArrInputPeers2.add(Integer.valueOf(row.user.id));
                intent2.putExtra("ArrayUser", ArrInputPeers2);
                intent2.putExtra("channel", new ArrayList());
                getParentActivity().startActivity(intent2);
            } else {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
            }
        }
    }
}

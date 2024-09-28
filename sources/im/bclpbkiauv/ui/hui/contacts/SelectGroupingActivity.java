package im.bclpbkiauv.ui.hui.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hcells.MryDividerCell;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectGroupingActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int item_done = 1;
    private int contactsHash;
    /* access modifiers changed from: private */
    public Context context;
    /* access modifiers changed from: private */
    public int defaultGroupId;
    private MryEmptyView emptyView;
    /* access modifiers changed from: private */
    public List<TLRPCContacts.TL_contactsGroupInfo> groupInfos = new ArrayList();
    private ListAdapter listAdapter;
    RecyclerListView listView;
    private SelectGroupingActivityDelegate mDelegate;
    /* access modifiers changed from: private */
    public TLRPCContacts.TL_contactsGroupInfo selectedGroup;
    private TLRPC.User user;
    private int user_id;

    public interface SelectGroupingActivityDelegate {
        void onFinish(TLRPCContacts.TL_contactsGroupInfo tL_contactsGroupInfo);
    }

    public SelectGroupingActivity(Bundle args) {
        super(args);
    }

    public void setDelegate(SelectGroupingActivityDelegate delegate) {
        this.mDelegate = delegate;
    }

    public boolean onFragmentCreate() {
        this.swipeBackEnabled = false;
        if (this.arguments != null) {
            this.user_id = this.arguments.getInt("user_id");
            this.defaultGroupId = this.arguments.getInt("groupId");
        }
        TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(this.user_id));
        this.user = user2;
        if (user2 == null) {
            return false;
        }
        getNotificationCenter().addObserver(this, NotificationCenter.groupingChanged);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.groupingChanged);
        super.onFragmentDestroy();
    }

    public View createView(Context context2) {
        this.context = context2;
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initActionBar();
        initEmptyView();
        initList();
        initData();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("SelectGrouping", R.string.SelectGrouping));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id != 1) {
                    return;
                }
                if (SelectGroupingActivity.this.selectedGroup == null || SelectGroupingActivity.this.selectedGroup.group_id == SelectGroupingActivity.this.defaultGroupId) {
                    SelectGroupingActivity.this.finishFragment();
                } else {
                    SelectGroupingActivity.this.saveGroup();
                }
            }
        });
        this.actionBar.setBackTitle(LocaleController.getString("Cancel", R.string.Cancel));
        this.actionBar.getBackTitleTextView().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SelectGroupingActivity.this.lambda$initActionBar$0$SelectGroupingActivity(view);
            }
        });
        this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", R.string.Done));
    }

    public /* synthetic */ void lambda$initActionBar$0$SelectGroupingActivity(View v) {
        TLRPCContacts.TL_contactsGroupInfo tL_contactsGroupInfo = this.selectedGroup;
        if (tL_contactsGroupInfo == null || tL_contactsGroupInfo.group_id == this.defaultGroupId) {
            finishFragment();
        } else {
            showSaveDialog();
        }
    }

    private void initList() {
        RecyclerListView recyclerListView = new RecyclerListView(this.context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(this.context));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter();
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SelectGroupingActivity.this.lambda$initList$1$SelectGroupingActivity(view, i);
            }
        });
    }

    public /* synthetic */ void lambda$initList$1$SelectGroupingActivity(View view, int position) {
        this.selectedGroup = this.groupInfos.get(position);
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void initEmptyView() {
        MryEmptyView mryEmptyView = new MryEmptyView(getParentActivity());
        this.emptyView = mryEmptyView;
        mryEmptyView.attach((BaseFragment) this);
        this.emptyView.setEmptyText("暂无分组");
        this.emptyView.setEmptyBtnText(LocaleController.getString("AddGrouping", R.string.AddGrouping));
        this.emptyView.setEmptyResId(R.mipmap.img_empty_default);
        this.emptyView.setErrorResId(R.mipmap.img_empty_default);
        this.emptyView.setOnEmptyClickListener(new MryEmptyView.OnEmptyOrErrorClickListener() {
            public final boolean onEmptyViewButtonClick(boolean z) {
                return SelectGroupingActivity.this.lambda$initEmptyView$2$SelectGroupingActivity(z);
            }
        });
    }

    public /* synthetic */ boolean lambda$initEmptyView$2$SelectGroupingActivity(boolean isEmptyButton) {
        if (isEmptyButton) {
            presentFragment(new CreateGroupingActivity());
            return false;
        }
        getContacts();
        return false;
    }

    private void initData() {
        getContacts();
    }

    private void showSaveDialog() {
        WalletDialog dialog = new WalletDialog(getParentActivity());
        dialog.setMessage(LocaleController.getString("SaveGroupingChangeTips", R.string.SaveGroupingChangeTips));
        dialog.setPositiveButton(LocaleController.getString("Save", R.string.Save), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                SelectGroupingActivity.this.lambda$showSaveDialog$3$SelectGroupingActivity(dialogInterface, i);
            }
        });
        dialog.setNegativeButton(LocaleController.getString("NotSave", R.string.NotSave), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                SelectGroupingActivity.this.lambda$showSaveDialog$4$SelectGroupingActivity(dialogInterface, i);
            }
        });
        showDialog(dialog);
    }

    public /* synthetic */ void lambda$showSaveDialog$3$SelectGroupingActivity(DialogInterface dialogInterface, int i) {
        saveGroup();
    }

    public /* synthetic */ void lambda$showSaveDialog$4$SelectGroupingActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void saveGroup() {
        if (this.user != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            TLRPCContacts.TL_setUserGroup req = new TLRPCContacts.TL_setUserGroup();
            req.group_id = this.selectedGroup.group_id;
            TLRPCContacts.TL_inputPeerUserChange inputPeer = new TLRPCContacts.TL_inputPeerUserChange();
            inputPeer.access_hash = this.user.access_hash;
            inputPeer.user_id = this.user.id;
            inputPeer.fist_name = this.user.first_name;
            req.users.add(inputPeer);
            int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(alertDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SelectGroupingActivity.this.lambda$saveGroup$6$SelectGroupingActivity(this.f$1, tLObject, tL_error);
                }
            });
            getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(reqId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    SelectGroupingActivity.this.lambda$saveGroup$7$SelectGroupingActivity(this.f$1, dialogInterface);
                }
            });
            showDialog(alertDialog);
        }
    }

    public /* synthetic */ void lambda$saveGroup$6$SelectGroupingActivity(AlertDialog alertDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, error, response) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SelectGroupingActivity.this.lambda$null$5$SelectGroupingActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$SelectGroupingActivity(AlertDialog alertDialog, TLRPC.TL_error error, TLObject response) {
        alertDialog.dismiss();
        if (error != null) {
            ToastUtils.show((CharSequence) error.text);
        } else if (response instanceof TLRPC.TL_boolTrue) {
            SelectGroupingActivityDelegate selectGroupingActivityDelegate = this.mDelegate;
            if (selectGroupingActivityDelegate != null) {
                selectGroupingActivityDelegate.onFinish(this.selectedGroup);
            }
            finishFragment();
        } else {
            ToastUtils.show((CharSequence) "修改分组失败，请稍后重试");
        }
    }

    public /* synthetic */ void lambda$saveGroup$7$SelectGroupingActivity(int reqId, DialogInterface dialog1) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    public void getContacts() {
        this.emptyView.showLoading();
        TLRPCContacts.TL_getContactsV1 req = new TLRPCContacts.TL_getContactsV1();
        req.hash = this.contactsHash;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SelectGroupingActivity.this.lambda$getContacts$9$SelectGroupingActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getContacts$9$SelectGroupingActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SelectGroupingActivity.this.lambda$null$8$SelectGroupingActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$SelectGroupingActivity(TLRPC.TL_error error, TLObject response) {
        if (error != null) {
            this.emptyView.showError(error.text);
        } else if (response instanceof TLRPCContacts.TL_contactsV1) {
            TLRPCContacts.TL_contactsV1 contacts = (TLRPCContacts.TL_contactsV1) response;
            this.contactsHash = contacts.hash;
            if (!contacts.users.isEmpty()) {
                Iterator it = contacts.users.iterator();
                while (it.hasNext()) {
                    getMessagesController().putUser((TLRPC.User) it.next(), false);
                }
            }
            this.groupInfos.clear();
            this.groupInfos.addAll(contacts.group_infos);
            if (this.groupInfos.isEmpty()) {
                this.emptyView.showEmpty();
            } else {
                Iterator<TLRPCContacts.TL_contactsGroupInfo> it2 = this.groupInfos.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    TLRPCContacts.TL_contactsGroupInfo groupInfo = it2.next();
                    if (groupInfo.group_id == this.defaultGroupId) {
                        this.selectedGroup = groupInfo;
                        break;
                    }
                }
                this.emptyView.showContent();
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.groupingChanged) {
            getContacts();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private ListAdapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(LayoutInflater.from(SelectGroupingActivity.this.context).inflate(R.layout.item_select_grouping, parent, false));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MryTextView tvGroupName = (MryTextView) holder.itemView.findViewById(R.id.tv_group_name);
            ImageView ivSelector = (ImageView) holder.itemView.findViewById(R.id.iv_selector);
            MryDividerCell divider = (MryDividerCell) holder.itemView.findViewById(R.id.divider);
            tvGroupName.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            int i = 0;
            if (position == getItemCount() - 1) {
                divider.setVisibility(position != getItemCount() + -1 ? 0 : 8);
            }
            TLRPCContacts.TL_contactsGroupInfo groupInfo = (TLRPCContacts.TL_contactsGroupInfo) SelectGroupingActivity.this.groupInfos.get(position);
            tvGroupName.setText(groupInfo.title);
            if (SelectGroupingActivity.this.selectedGroup == null || SelectGroupingActivity.this.selectedGroup.group_id != groupInfo.group_id) {
                i = 8;
            }
            ivSelector.setVisibility(i);
        }

        public int getItemCount() {
            return SelectGroupingActivity.this.groupInfos.size();
        }
    }

    public boolean onBackPressed() {
        TLRPCContacts.TL_contactsGroupInfo tL_contactsGroupInfo = this.selectedGroup;
        if (tL_contactsGroupInfo == null || tL_contactsGroupInfo.group_id == this.defaultGroupId) {
            return super.onBackPressed();
        }
        showSaveDialog();
        return false;
    }
}

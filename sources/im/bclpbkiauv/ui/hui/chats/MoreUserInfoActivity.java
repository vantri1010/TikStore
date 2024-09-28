package im.bclpbkiauv.ui.hui.chats;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.CommonGroupsActivity;
import im.bclpbkiauv.ui.MediaActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hcells.TextSettingCell;

public class MoreUserInfoActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ListAdapter adapter;
    private long dialog_id;
    /* access modifiers changed from: private */
    public int groupRow;
    private int[] lastMediaCount;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int shareMediaRow;
    private MediaActivity.SharedMediaData[] sharedMediaData;
    /* access modifiers changed from: private */
    public int sourceRow;
    /* access modifiers changed from: private */
    public int userId;
    /* access modifiers changed from: private */
    public TLRPCContacts.CL_userFull_v1 userInfo;

    public MoreUserInfoActivity(int userId2, long dialog_id2, int[] lastMediaCount2) {
        this.userId = userId2;
        this.dialog_id = dialog_id2;
        this.lastMediaCount = lastMediaCount2;
    }

    public boolean onFragmentCreate() {
        if (this.userInfo == null) {
            TLRPC.UserFull full = MessagesController.getInstance(this.currentAccount).getUserFull(getUserConfig().getClientUserId());
            if (full instanceof TLRPCContacts.CL_userFull_v1) {
                this.userInfo = (TLRPCContacts.CL_userFull_v1) full;
            }
            if (this.userInfo == null) {
                MessagesController.getInstance(this.currentAccount).loadFullUser(getUserConfig().getClientUserId(), this.classGuid, true);
            }
            getNotificationCenter().addObserver(this, NotificationCenter.userFullInfoDidLoad);
        }
        this.sharedMediaData = new MediaActivity.SharedMediaData[5];
        int a = 0;
        while (true) {
            MediaActivity.SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (a >= sharedMediaDataArr.length) {
                return super.onFragmentCreate();
            }
            sharedMediaDataArr[a] = new MediaActivity.SharedMediaData();
            this.sharedMediaData[a].setMaxId(0, this.dialog_id != 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
            a++;
        }
    }

    public View createView(Context context) {
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initList(context);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("MoreInformation", R.string.MoreInformation));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    MoreUserInfoActivity.this.finishFragment();
                }
            }
        });
    }

    private void initList(Context context) {
        RecyclerListView listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        listView.setAdapter(listAdapter);
        listView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        ((FrameLayout) this.fragmentView).addView(listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                MoreUserInfoActivity.this.lambda$initList$0$MoreUserInfoActivity(view, i);
            }
        });
        updateRow();
    }

    public /* synthetic */ void lambda$initList$0$MoreUserInfoActivity(View view, int position) {
        if (position == this.shareMediaRow) {
            Bundle args = new Bundle();
            int i = this.userId;
            if (i != 0) {
                long j = this.dialog_id;
                if (j == 0) {
                    j = (long) i;
                }
                args.putLong("dialog_id", j);
            }
            int[] media = new int[5];
            System.arraycopy(this.lastMediaCount, 0, media, 0, media.length);
            presentFragment(new MediaActivity(args, media, this.sharedMediaData, 0));
        } else if (position == this.groupRow) {
            presentFragment(new CommonGroupsActivity(this.userId));
        }
    }

    private void updateRow() {
        this.rowCount = 0;
        this.shareMediaRow = -1;
        this.groupRow = -1;
        this.sourceRow = -1;
        int i = 0 + 1;
        this.rowCount = i;
        this.shareMediaRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.groupRow = i;
        this.rowCount = i2 + 1;
        this.sourceRow = i2;
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void setUserInfo(TLRPCContacts.CL_userFull_v1 userInfo2) {
        this.userInfo = userInfo2;
    }

    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        super.onFragmentDestroy();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.userFullInfoDidLoad && args[0].intValue() == getUserConfig().getClientUserId() && (args[1] instanceof TLRPCContacts.CL_userFull_v1)) {
            this.userInfo = args[1];
            updateRow();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == MoreUserInfoActivity.this.shareMediaRow || position == MoreUserInfoActivity.this.groupRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new TextSettingCell(this.mContext);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPCContacts.CL_userFull_v1_Bean extendBean;
            if (holder.getItemViewType() == 0) {
                TextSettingCell cell = (TextSettingCell) holder.itemView;
                boolean z = false;
                boolean z2 = true;
                if (position == MoreUserInfoActivity.this.shareMediaRow) {
                    String string = LocaleController.getString("SharedMedia", R.string.SharedMedia);
                    if (position != MoreUserInfoActivity.this.rowCount - 1) {
                        z = true;
                    }
                    cell.setText(string, z, true);
                } else if (position == MoreUserInfoActivity.this.groupRow) {
                    TLRPC.UserFull userFull = MoreUserInfoActivity.this.getMessagesController().getUserFull(MoreUserInfoActivity.this.userId);
                    String string2 = LocaleController.getString("GroupsInCommonTitle", R.string.GroupsInCommonTitle);
                    String valueOf = userFull != null ? String.valueOf(userFull.common_chats_count) : "";
                    if (position != MoreUserInfoActivity.this.rowCount - 1) {
                        z = true;
                    }
                    cell.setTextAndValue(string2, valueOf, z, true);
                } else if (position == MoreUserInfoActivity.this.sourceRow) {
                    String sourceStr = "";
                    if (!(MoreUserInfoActivity.this.userInfo == null || (extendBean = MoreUserInfoActivity.this.userInfo.getExtendBean()) == null)) {
                        switch (extendBean.source) {
                            case 1:
                                sourceStr = LocaleController.getString(R.string.AddContactByScanQrCode);
                                break;
                            case 2:
                                sourceStr = LocaleController.getString(R.string.AddContactByGroup);
                                break;
                            case 3:
                                sourceStr = LocaleController.getString(R.string.AddContactByPhoneNumber);
                                break;
                            case 4:
                                sourceStr = LocaleController.getString(R.string.AddContactByAccount);
                                break;
                            case 5:
                                sourceStr = LocaleController.getString(R.string.AddContactByNearBy);
                                break;
                            case 6:
                                sourceStr = LocaleController.getString(R.string.AddContactByPhoneBook);
                                break;
                        }
                    }
                    String string3 = LocaleController.getString("FriendSource", R.string.FriendSource);
                    if (position == MoreUserInfoActivity.this.rowCount - 1) {
                        z2 = false;
                    }
                    cell.setTextAndValue(string3, sourceStr, z2, false);
                }
            }
        }

        public int getItemCount() {
            return MoreUserInfoActivity.this.rowCount;
        }
    }
}

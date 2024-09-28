package im.bclpbkiauv.ui.hui.contacts;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hcells.MryDividerCell;
import im.bclpbkiauv.ui.hui.adapter.grouping.Artist;
import im.bclpbkiauv.ui.hui.adapter.grouping.Genre;
import im.bclpbkiauv.ui.hui.contacts.AddGroupingUserActivity;
import im.bclpbkiauv.ui.hui.contacts.GroupingMgrActivity;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.slidemenu.SwipeLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupingMgrActivity extends BaseFragment {
    private static final int item_done = 1;
    /* access modifiers changed from: private */
    public List<Integer> defaultOrders;
    /* access modifiers changed from: private */
    public List<Integer> deletedGroupIds;
    /* access modifiers changed from: private */
    public List<Genre> genres = new ArrayList();
    /* access modifiers changed from: private */
    public GroupManageAdapter mAdapter;
    @BindView(2131297082)
    RecyclerListView mRcvList;
    @BindView(2131297714)
    MryTextView mTvAddGroup;
    private int requestCount;
    private int requestDoneCount;

    public void setGenres(List<Genre> genres2) {
        if (this.requestCount == 0) {
            this.genres = new ArrayList(genres2);
            this.deletedGroupIds = new ArrayList();
            this.defaultOrders = new ArrayList();
            for (Genre genre : genres2) {
                this.defaultOrders.add(Integer.valueOf(genre.getOrderId()));
            }
            GroupManageAdapter groupManageAdapter = this.mAdapter;
            if (groupManageAdapter != null) {
                groupManageAdapter.notifyDataSetChanged();
            }
        }
    }

    public boolean onFragmentCreate() {
        this.swipeBackEnabled = false;
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_grouping_mgr_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initActionbar();
        initView();
        return this.fragmentView;
    }

    private void initActionbar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("ManageGrouping", R.string.ManageGrouping));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (GroupingMgrActivity.this.isOrderChanged() || !GroupingMgrActivity.this.deletedGroupIds.isEmpty()) {
                        GroupingMgrActivity.this.showSaveDialog();
                    } else {
                        GroupingMgrActivity.this.finishFragment();
                    }
                } else if (id != 1) {
                } else {
                    if (GroupingMgrActivity.this.isOrderChanged() || !GroupingMgrActivity.this.deletedGroupIds.isEmpty()) {
                        GroupingMgrActivity.this.saveChanged();
                    } else {
                        GroupingMgrActivity.this.finishFragment();
                    }
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        MryTextView tvOkView = new MryTextView(getParentActivity());
        tvOkView.setText(LocaleController.getString("Done", R.string.Done));
        tvOkView.setTextSize(1, 14.0f);
        tvOkView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        tvOkView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        tvOkView.setGravity(16);
        menu.addItemView(1, tvOkView);
    }

    private void initView() {
        this.mTvAddGroup.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mTvAddGroup.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        Drawable[] ds = this.mTvAddGroup.getCompoundDrawables();
        if (ds[0] != null) {
            ds[0].setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton), PorterDuff.Mode.SRC_IN));
            this.mTvAddGroup.setCompoundDrawables(ds[0], ds[1], ds[2], ds[3]);
        }
        initList();
    }

    private void initList() {
        this.mRcvList.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        RecyclerListView recyclerListView = this.mRcvList;
        GroupManageAdapter groupManageAdapter = new GroupManageAdapter(getParentActivity());
        this.mAdapter = groupManageAdapter;
        recyclerListView.setAdapter(groupManageAdapter);
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.mRcvList);
    }

    @OnClick({2131297714})
    public void onViewClicked() {
        presentFragment(new CreateGroupingActivity());
    }

    /* access modifiers changed from: private */
    public void showSaveDialog() {
        WalletDialog dialog = new WalletDialog(getParentActivity());
        dialog.setMessage(LocaleController.getString("SaveGroupingChangeTips", R.string.SaveGroupingChangeTips));
        dialog.setPositiveButton(LocaleController.getString("Save", R.string.Save), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                GroupingMgrActivity.this.lambda$showSaveDialog$0$GroupingMgrActivity(dialogInterface, i);
            }
        });
        dialog.setNegativeButton(LocaleController.getString("NotSave", R.string.NotSave), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                GroupingMgrActivity.this.lambda$showSaveDialog$1$GroupingMgrActivity(dialogInterface, i);
            }
        });
        showDialog(dialog);
    }

    public /* synthetic */ void lambda$showSaveDialog$0$GroupingMgrActivity(DialogInterface dialogInterface, int i) {
        saveChanged();
    }

    public /* synthetic */ void lambda$showSaveDialog$1$GroupingMgrActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void saveChanged() {
        this.requestCount = 0;
        this.requestDoneCount = 0;
        if (isOrderChanged()) {
            saveOrderChange();
            this.requestCount++;
        }
        if (!this.deletedGroupIds.isEmpty()) {
            saveDeleteChange();
            this.requestCount++;
        }
    }

    private void saveOrderChange() {
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        TLRPCContacts.TL_changeGroupOrder req = new TLRPCContacts.TL_changeGroupOrder();
        for (int i = 0; i < this.genres.size(); i++) {
            TLRPCContacts.TL_contactGroupOrderInfo orderInfo = new TLRPCContacts.TL_contactGroupOrderInfo();
            orderInfo.group_id = this.genres.get(i).getGroupId();
            orderInfo.order_id = i;
            req.group_orders.add(orderInfo);
        }
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupingMgrActivity.this.lambda$saveOrderChange$3$GroupingMgrActivity(this.f$1, tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                GroupingMgrActivity.this.lambda$saveOrderChange$4$GroupingMgrActivity(this.f$1, dialogInterface);
            }
        });
        showDialog(alertDialog);
    }

    public /* synthetic */ void lambda$saveOrderChange$3$GroupingMgrActivity(AlertDialog alertDialog, TLObject response, TLRPC.TL_error error) {
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
                GroupingMgrActivity.this.lambda$null$2$GroupingMgrActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$GroupingMgrActivity(AlertDialog alertDialog, TLRPC.TL_error error, TLObject response) {
        alertDialog.dismiss();
        this.requestDoneCount++;
        if (error != null) {
            ToastUtils.show((CharSequence) error.text);
        } else if (response instanceof TLRPC.TL_boolTrue) {
            this.defaultOrders.clear();
            for (Genre genre : this.genres) {
                this.defaultOrders.add(Integer.valueOf(genre.getOrderId()));
            }
            if (this.requestDoneCount == this.requestCount) {
                finishFragment();
            }
        } else {
            ToastUtils.show((CharSequence) "修改分组顺序失败，请稍后重试");
        }
    }

    public /* synthetic */ void lambda$saveOrderChange$4$GroupingMgrActivity(int reqId, DialogInterface dialog1) {
        getConnectionsManager().cancelRequest(reqId, true);
        this.requestCount--;
    }

    private void saveDeleteChange() {
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        TLRPCContacts.TL_deleteGroups req = new TLRPCContacts.TL_deleteGroups();
        req.group_ids.addAll(this.deletedGroupIds);
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupingMgrActivity.this.lambda$saveDeleteChange$6$GroupingMgrActivity(this.f$1, tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                GroupingMgrActivity.this.lambda$saveDeleteChange$7$GroupingMgrActivity(this.f$1, dialogInterface);
            }
        });
        showDialog(alertDialog);
    }

    public /* synthetic */ void lambda$saveDeleteChange$6$GroupingMgrActivity(AlertDialog alertDialog, TLObject response, TLRPC.TL_error error) {
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
                GroupingMgrActivity.this.lambda$null$5$GroupingMgrActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$GroupingMgrActivity(AlertDialog alertDialog, TLRPC.TL_error error, TLObject response) {
        alertDialog.dismiss();
        this.requestDoneCount++;
        if (error != null) {
            ToastUtils.show((CharSequence) error.text);
        } else if (response instanceof TLRPC.TL_boolTrue) {
            this.deletedGroupIds.clear();
            if (this.requestDoneCount == this.requestCount) {
                finishFragment();
            }
        } else {
            ToastUtils.show((CharSequence) "删除分组失败，请稍后重试");
        }
    }

    public /* synthetic */ void lambda$saveDeleteChange$7$GroupingMgrActivity(int reqId, DialogInterface dialog1) {
        getConnectionsManager().cancelRequest(reqId, true);
        this.requestCount--;
    }

    /* access modifiers changed from: private */
    public boolean isOrderChanged() {
        List<Integer> newOrders = new ArrayList<>();
        for (Genre genre : this.genres) {
            newOrders.add(Integer.valueOf(genre.getOrderId()));
        }
        if (!newOrders.equals(this.defaultOrders)) {
            return true;
        }
        return false;
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            GroupingMgrActivity.this.mAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                GroupingMgrActivity.this.mRcvList.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public class GroupManageAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public GroupManageAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SwipeLayout swipeLayout = new SwipeLayout(this.mContext) {
                public boolean onTouchEvent(MotionEvent event) {
                    if (isExpanded()) {
                        return true;
                    }
                    return super.onTouchEvent(event);
                }
            };
            swipeLayout.setUpView(LayoutInflater.from(this.mContext).inflate(R.layout.item_group_manage, parent, false));
            return new RecyclerListView.Holder(swipeLayout);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int[] rightColors;
            String[] rightTexts;
            int[] rightColors2;
            int i = position;
            SwipeLayout swipeLayout = (SwipeLayout) holder.itemView;
            swipeLayout.setItemWidth(AndroidUtilities.dp(75.0f));
            View content = swipeLayout.getMainLayout();
            MryDividerCell divider = (MryDividerCell) content.findViewById(R.id.divider);
            Genre genre = (Genre) GroupingMgrActivity.this.genres.get(i);
            ((MryTextView) content.findViewById(R.id.tv_group_name)).setText(genre.getTitle());
            ((MryTextView) content.findViewById(R.id.tv_member_number)).setText(genre.getOnlineCount() + "/" + genre.getItemCount());
            if (getItemCount() == 1) {
                content.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                divider.setVisibility(8);
            } else if (i == 0) {
                content.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                divider.setVisibility(0);
            } else if (i == getItemCount() - 1) {
                content.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                divider.setVisibility(8);
            } else {
                content.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                divider.setVisibility(0);
            }
            if (genre.getGroupId() == 0) {
                rightColors2 = new int[]{-3881788};
                rightTexts = new String[]{LocaleController.getString(R.string.Rename)};
                rightColors = new int[]{-1};
            } else {
                rightTexts = new String[]{LocaleController.getString(R.string.Rename), LocaleController.getString(R.string.GroupAddMembers), LocaleController.getString(R.string.Delete)};
                rightColors = new int[]{-1, -1, -1};
                rightColors2 = new int[]{-3881788, -12862209, -570319};
            }
            swipeLayout.setRightTexts(rightTexts);
            swipeLayout.setRightTextColors(rightColors);
            swipeLayout.setRightColors(rightColors2);
            swipeLayout.setTextSize(AndroidUtilities.sp2px(13.0f));
            swipeLayout.rebuildLayout();
            swipeLayout.setOnSwipeItemClickListener(new SwipeLayout.OnSwipeItemClickListener(genre) {
                private final /* synthetic */ Genre f$1;

                {
                    this.f$1 = r2;
                }

                public final void onSwipeItemClick(boolean z, int i) {
                    GroupingMgrActivity.GroupManageAdapter.this.lambda$onBindViewHolder$10$GroupingMgrActivity$GroupManageAdapter(this.f$1, z, i);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$10$GroupingMgrActivity$GroupManageAdapter(Genre genre, boolean left, int index) {
            Genre genre2 = genre;
            int i = index;
            if (left) {
                return;
            }
            if (i == 0) {
                Dialog dialog = new Dialog(GroupingMgrActivity.this.getParentActivity());
                GroupingMgrActivity.this.showDialog(dialog);
                View view = LayoutInflater.from(GroupingMgrActivity.this.getParentActivity()).inflate(R.layout.dialog_rename_grouping_layout, (ViewGroup) null);
                view.setOnClickListener(new View.OnClickListener(dialog) {
                    private final /* synthetic */ Dialog f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        this.f$0.dismiss();
                    }
                });
                Window window = dialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable());
                Display display = GroupingMgrActivity.this.getParentActivity().getWindowManager().getDefaultDisplay();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = display.getWidth();
                lp.height = display.getHeight();
                window.setAttributes(lp);
                window.setContentView(view);
                final LinearLayout llNotSupportEmojiTips = (LinearLayout) view.findViewById(R.id.ll_not_support_emoji_tips);
                MryEditText etGroupingName = (MryEditText) view.findViewById(R.id.et_grouping_name);
                MryTextView tvNotSave = (MryTextView) view.findViewById(R.id.tv_not_save);
                final MryTextView tvSave = (MryTextView) view.findViewById(R.id.tv_save);
                llNotSupportEmojiTips.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                ((LinearLayout) view.findViewById(R.id.ll_container)).setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(10.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                tvNotSave.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                tvSave.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                etGroupingName.setText(genre.getTitle());
                View view2 = view;
                Window window2 = window;
                etGroupingName.setFilters(new InputFilter[]{new LengthFilter(28)});
                etGroupingName.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        boolean hasEmoji = false;
                        int i = 0;
                        while (true) {
                            if (i >= s.length()) {
                                break;
                            }
                            int type = Character.getType(s.charAt(i));
                            if (type == 19 || type == 28) {
                                hasEmoji = true;
                            } else {
                                i++;
                            }
                        }
                        boolean z = false;
                        llNotSupportEmojiTips.setVisibility(hasEmoji ? 0 : 8);
                        MryTextView mryTextView = tvSave;
                        if (!hasEmoji && !TextUtils.isEmpty(s)) {
                            z = true;
                        }
                        mryTextView.setEnabled(z);
                    }

                    public void afterTextChanged(Editable s) {
                    }
                });
                tvNotSave.setOnClickListener(new View.OnClickListener(dialog) {
                    private final /* synthetic */ Dialog f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        this.f$0.dismiss();
                    }
                });
                tvSave.setOnClickListener(new View.OnClickListener(dialog, genre2, etGroupingName) {
                    private final /* synthetic */ Dialog f$1;
                    private final /* synthetic */ Genre f$2;
                    private final /* synthetic */ MryEditText f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void onClick(View view) {
                        GroupingMgrActivity.GroupManageAdapter.this.lambda$null$5$GroupingMgrActivity$GroupManageAdapter(this.f$1, this.f$2, this.f$3, view);
                    }
                });
            } else if (i == 1) {
                List<TLRPC.User> users = new ArrayList<>();
                for (Artist artist : genre.getItems()) {
                    TLRPC.User user = GroupingMgrActivity.this.getMessagesController().getUser(Integer.valueOf(artist.getUserId()));
                    if (user != null) {
                        users.add(user);
                    }
                }
                AddGroupingUserActivity fragment = new AddGroupingUserActivity(users, 2);
                fragment.setDelegate(new AddGroupingUserActivity.AddGroupingUserActivityDelegate(users, genre2) {
                    private final /* synthetic */ List f$1;
                    private final /* synthetic */ Genre f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void didSelectedContact(ArrayList arrayList) {
                        GroupingMgrActivity.GroupManageAdapter.this.lambda$null$9$GroupingMgrActivity$GroupManageAdapter(this.f$1, this.f$2, arrayList);
                    }
                });
                GroupingMgrActivity.this.presentFragment(fragment);
            } else if (i == 2 && genre.getGroupId() != 0) {
                GroupingMgrActivity.this.defaultOrders.remove(Integer.valueOf(genre.getOrderId()));
                GroupingMgrActivity.this.deletedGroupIds.add(Integer.valueOf(genre.getGroupId()));
                GroupingMgrActivity.this.genres.remove(genre2);
                notifyDataSetChanged();
            }
        }

        public /* synthetic */ void lambda$null$5$GroupingMgrActivity$GroupManageAdapter(Dialog dialog, Genre genre, MryEditText etGroupingName, View v) {
            dialog.dismiss();
            AlertDialog alertDialog = new AlertDialog(GroupingMgrActivity.this.getParentActivity(), 3);
            TLRPCContacts.TL_changeGroupName req = new TLRPCContacts.TL_changeGroupName();
            req.group_id = genre.getGroupId();
            req.title = etGroupingName.getText().toString();
            int reqId = GroupingMgrActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject) {
                        private final /* synthetic */ TLRPC.TL_error f$1;
                        private final /* synthetic */ TLObject f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            GroupingMgrActivity.GroupManageAdapter.lambda$null$2(AlertDialog.this, this.f$1, this.f$2);
                        }
                    });
                }
            });
            GroupingMgrActivity.this.getConnectionsManager().bindRequestToGuid(reqId, GroupingMgrActivity.this.classGuid);
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(reqId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    GroupingMgrActivity.GroupManageAdapter.this.lambda$null$4$GroupingMgrActivity$GroupManageAdapter(this.f$1, dialogInterface);
                }
            });
            GroupingMgrActivity.this.showDialog(alertDialog);
        }

        static /* synthetic */ void lambda$null$2(AlertDialog alertDialog, TLRPC.TL_error error, TLObject response) {
            alertDialog.dismiss();
            if (error != null) {
                ToastUtils.show((CharSequence) error.text);
            } else if (!(response instanceof TLRPC.TL_boolTrue)) {
                ToastUtils.show((CharSequence) "重命名失败，请稍后重试");
            }
        }

        public /* synthetic */ void lambda$null$4$GroupingMgrActivity$GroupManageAdapter(int reqId, DialogInterface dialog1) {
            GroupingMgrActivity.this.getConnectionsManager().cancelRequest(reqId, true);
        }

        public /* synthetic */ void lambda$null$9$GroupingMgrActivity$GroupManageAdapter(List users, Genre genre, ArrayList users1) {
            if (!users.equals(users1)) {
                AlertDialog alertDialog = new AlertDialog(GroupingMgrActivity.this.getParentActivity(), 3);
                TLRPCContacts.TL_setUserGroup req = new TLRPCContacts.TL_setUserGroup();
                req.group_id = genre.getGroupId();
                Iterator it = users1.iterator();
                while (it.hasNext()) {
                    TLRPC.User user = (TLRPC.User) it.next();
                    TLRPCContacts.TL_inputPeerUserChange inputPeer = new TLRPCContacts.TL_inputPeerUserChange();
                    inputPeer.access_hash = user.access_hash;
                    inputPeer.user_id = user.id;
                    inputPeer.fist_name = user.first_name;
                    req.users.add(inputPeer);
                }
                int reqId = GroupingMgrActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject) {
                            private final /* synthetic */ TLRPC.TL_error f$1;
                            private final /* synthetic */ TLObject f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                GroupingMgrActivity.GroupManageAdapter.lambda$null$6(AlertDialog.this, this.f$1, this.f$2);
                            }
                        });
                    }
                });
                GroupingMgrActivity.this.getConnectionsManager().bindRequestToGuid(reqId, GroupingMgrActivity.this.classGuid);
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(reqId) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onDismiss(DialogInterface dialogInterface) {
                        GroupingMgrActivity.GroupManageAdapter.this.lambda$null$8$GroupingMgrActivity$GroupManageAdapter(this.f$1, dialogInterface);
                    }
                });
                GroupingMgrActivity.this.showDialog(alertDialog);
            }
        }

        static /* synthetic */ void lambda$null$6(AlertDialog alertDialog, TLRPC.TL_error error, TLObject response) {
            alertDialog.dismiss();
            if (error != null) {
                ToastUtils.show((CharSequence) error.text);
            } else if (response instanceof TLRPC.TL_boolTrue) {
                ToastUtils.show((CharSequence) "添加成功");
            } else {
                ToastUtils.show((CharSequence) "添加失败，请稍后重试");
            }
        }

        public /* synthetic */ void lambda$null$8$GroupingMgrActivity$GroupManageAdapter(int reqId, DialogInterface dialog1) {
            GroupingMgrActivity.this.getConnectionsManager().cancelRequest(reqId, true);
        }

        public int getItemCount() {
            return GroupingMgrActivity.this.genres.size();
        }

        public void swapElements(int fromIndex, int toIndex) {
            GroupingMgrActivity.this.genres.set(fromIndex, (Genre) GroupingMgrActivity.this.genres.get(toIndex));
            GroupingMgrActivity.this.genres.set(toIndex, (Genre) GroupingMgrActivity.this.genres.get(fromIndex));
            notifyItemMoved(fromIndex, toIndex);
            notifyItemRangeChanged(Math.min(fromIndex, toIndex), Math.abs(fromIndex - toIndex) + 1);
        }
    }

    private class LengthFilter implements InputFilter {
        private int maxLen;

        public LengthFilter(int maxLen2) {
            this.maxLen = maxLen2;
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int count;
            int dindex = 0;
            int count2 = 0;
            while (count2 <= this.maxLen && dindex < dest.length()) {
                int dindex2 = dindex + 1;
                if (dest.charAt(dindex) < 128) {
                    count2++;
                } else {
                    count2 += 2;
                }
                dindex = dindex2;
            }
            if (count2 > this.maxLen) {
                return dest.subSequence(0, dindex - 1);
            }
            int sindex = 0;
            while (count2 <= this.maxLen && sindex < source.length()) {
                int sindex2 = sindex + 1;
                if (source.charAt(sindex) < 128) {
                    count = count2 + 1;
                } else {
                    count = count2 + 2;
                }
                sindex = sindex2;
            }
            if (count2 > this.maxLen) {
                sindex--;
            }
            return source.subSequence(0, sindex);
        }
    }

    public boolean onBackPressed() {
        if (!isOrderChanged() && this.deletedGroupIds.isEmpty()) {
            return super.onBackPressed();
        }
        showSaveDialog();
        return false;
    }
}

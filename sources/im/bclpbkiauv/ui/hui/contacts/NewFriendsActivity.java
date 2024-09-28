package im.bclpbkiauv.ui.hui.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.decoration.StickyDecoration;
import im.bclpbkiauv.ui.decoration.listener.GroupListener;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.NewFriendsActivity;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import im.bclpbkiauv.ui.hviews.slidemenu.SwipeLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class NewFriendsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    private ArrayList<TLRPCContacts.ContactApplyInfo> contactsApplyInfos = new ArrayList<>();
    @BindView(2131296556)
    LinearLayout emptyLayout;
    private final int item_add = 1;
    @BindView(2131296893)
    RecyclerListView listview;
    private HashMap<String, ArrayList<TLRPCContacts.ContactApplyInfo>> map = new HashMap<>();
    private ArrayList<String> mapKeysList = new ArrayList<>();
    @BindView(2131297072)
    RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    @BindView(2131297488)
    MryTextView tvEmptyText;
    /* access modifiers changed from: private */
    public HashMap<Integer, TLRPC.User> userMap = new HashMap<>();
    private ArrayList<TLRPC.User> users = new ArrayList<>();

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        notifyServerClearUnread();
        getMessagesController().handleUpdatesContactsApply(0);
        getNotificationCenter().postNotificationName(NotificationCenter.contactApplyUpdateCount, 0);
        getMessagesController().getContactsApplyDifferenceV2(true, true, false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactApplyUpdateState);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactApplyUpdateReceived);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactApplieReceived);
        getNotificationCenter().addObserver(this, NotificationCenter.contactApplyUpdateCount);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactApplyUpdateState);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactApplyUpdateReceived);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactApplieReceived);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactApplyUpdateCount);
        getMessagesController().handleUpdatesContactsApply(0);
        getNotificationCenter().postNotificationName(NotificationCenter.contactApplyUpdateCount, 0);
    }

    private void groupingApplyInfos(ArrayList<TLRPCContacts.ContactApplyInfo> applyInfos) {
        if (applyInfos != null) {
            this.mapKeysList.clear();
            this.map.clear();
            Collections.sort(applyInfos, $$Lambda$NewFriendsActivity$kCZf66diXSxnY8K550uSCgDpQE.INSTANCE);
            Iterator<TLRPCContacts.ContactApplyInfo> it = applyInfos.iterator();
            while (it.hasNext()) {
                TLRPCContacts.ContactApplyInfo item = it.next();
                if (getConnectionsManager().getCurrentTime() - item.date < 259200) {
                    ArrayList<TLRPCContacts.ContactApplyInfo> in = this.map.get("in");
                    if (in == null) {
                        in = new ArrayList<>();
                        this.map.put("in", in);
                        this.mapKeysList.add("in");
                    }
                    in.add(item);
                } else {
                    ArrayList<TLRPCContacts.ContactApplyInfo> out = this.map.get("out");
                    if (out == null) {
                        out = new ArrayList<>();
                        this.map.put("out", out);
                        this.mapKeysList.add("out");
                    }
                    out.add(item);
                }
            }
        }
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_new_friend_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        super.createView(context);
        initActionbar();
        initProgressBar();
        initList();
        return this.fragmentView;
    }

    private void initActionbar() {
        this.actionBar.setTitle(LocaleController.getString("NewFriends", R.string.NewFriends));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NewFriendsActivity.this.finishFragment();
                } else if (id == 1) {
                    NewFriendsActivity.this.presentFragment(new AddContactsActivity());
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        MryTextView tvAddView = new MryTextView(getParentActivity());
        tvAddView.setText(LocaleController.getString("Add", R.string.Add));
        tvAddView.setTextSize(1, 14.0f);
        tvAddView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        tvAddView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        tvAddView.setGravity(16);
        menu.addItemView(1, tvAddView);
    }

    private void initProgressBar() {
        this.progressBar.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        this.progressBar.setBackgroundResource(R.drawable.system_loader);
        this.progressBar.getBackground().setColorFilter(Theme.colorFilter);
    }

    private void initList() {
        this.tvEmptyText.setTextColor(Theme.key_windowBackgroundWhiteGrayText6);
        this.listview.setHasFixedSize(true);
        this.listview.setVerticalScrollBarEnabled(false);
        this.listview.setLayoutManager(new LinearLayoutManager(this.fragmentView.getContext()));
        ListAdapter listAdapter = new ListAdapter(this.fragmentView.getContext());
        this.adapter = listAdapter;
        listAdapter.setList(this.mapKeysList, this.map, true);
        this.listview.addItemDecoration(StickyDecoration.Builder.init(new GroupListener() {
            public final String getGroupName(int i) {
                return NewFriendsActivity.this.lambda$initList$1$NewFriendsActivity(i);
            }
        }).setOffset(1).setGroupBackground(Theme.getColor(Theme.key_windowBackgroundGray)).setGroupTextColor(Theme.getColor(Theme.key_list_decorationTextColor)).setGroupTextTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf")).setGroupHeight(AndroidUtilities.dp(38.5f)).setGroupTextSize(AndroidUtilities.dp(14.0f)).setTextSideMargin(AndroidUtilities.dp(15.0f)).build());
        this.listview.setDisableHighlightState(true);
        this.listview.setAdapter(this.adapter);
        this.listview.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                NewFriendsActivity.this.lambda$initList$2$NewFriendsActivity(view, i);
            }
        });
        this.listview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    if (NewFriendsActivity.this.searching && NewFriendsActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(NewFriendsActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ String lambda$initList$1$NewFriendsActivity(int position) {
        String str;
        int i;
        if (this.adapter.getItemCount() <= position || position <= -1) {
            return null;
        }
        String letter = this.adapter.getLetter(position);
        if (letter == null) {
            return letter;
        }
        if ("in".equals(letter)) {
            i = R.string.new_friends_three_days;
            str = "new_friends_three_days";
        } else {
            i = R.string.new_friends_three_days_before;
            str = "new_friends_three_days_before";
        }
        return LocaleController.getString(str, i);
    }

    public /* synthetic */ void lambda$initList$2$NewFriendsActivity(View view, int position) {
        int section = this.adapter.getSectionForPosition(position);
        if (section != 0) {
            Object item = this.adapter.getItem(section, this.adapter.getPositionInSectionForPosition(position));
            if (item instanceof TLRPCContacts.ContactApplyInfo) {
                TLRPCContacts.ContactApplyInfo info = (TLRPCContacts.ContactApplyInfo) item;
                TLRPC.User user = getMessagesController().getUser(Integer.valueOf(info.from_peer.user_id));
                if (user != null) {
                    Bundle bundle = new Bundle();
                    if (user.contact) {
                        bundle.putInt("user_id", user.id);
                        presentFragment(new NewProfileActivity(bundle));
                        return;
                    }
                    bundle.putInt("from_type", 7);
                    bundle.putInt("req_state", info.state);
                    bundle.putInt("apply_id", info.id);
                    bundle.putInt("expire", info.expire);
                    bundle.putString("greet", info.greet);
                    presentFragment(new AddContactsInfoActivity(bundle, user));
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactApplyUpdateState) {
            int apply_id = args[0].intValue();
            int state = args[1].intValue();
            int index = getIndex(apply_id);
            ArrayList<TLRPCContacts.ContactApplyInfo> arrayList = this.contactsApplyInfos;
            if (arrayList != null) {
                arrayList.get(index).state = state;
                groupingApplyInfos(this.contactsApplyInfos);
                ListAdapter listAdapter = this.adapter;
                if (listAdapter != null) {
                    listAdapter.setList(this.mapKeysList, this.map, false);
                    this.adapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.contactApplyUpdateReceived) {
            TLRPCContacts.ContactApplyInfo recvInfo = args[0];
            if (this.contactsApplyInfos != null) {
                int i = 0;
                while (true) {
                    if (i >= this.contactsApplyInfos.size()) {
                        break;
                    } else if (recvInfo.from_peer.user_id == this.contactsApplyInfos.get(i).from_peer.user_id) {
                        this.contactsApplyInfos.remove(i);
                        break;
                    } else {
                        i++;
                    }
                }
                this.contactsApplyInfos.add(0, recvInfo);
                groupingApplyInfos(this.contactsApplyInfos);
                ListAdapter listAdapter2 = this.adapter;
                if (listAdapter2 != null) {
                    listAdapter2.setList(this.mapKeysList, this.map, false);
                    this.adapter.notifyDataSetChanged();
                }
                getMessagesController().handleUpdatesContactsApply(0);
                getNotificationCenter().postNotificationName(NotificationCenter.contactApplyUpdateCount, 0);
            }
        } else if (id == NotificationCenter.contactApplieReceived) {
            this.contactsApplyInfos = args[0];
            ArrayList<TLRPC.User> arrayList2 = args[1];
            this.users = arrayList2;
            Iterator<TLRPC.User> it = arrayList2.iterator();
            while (it.hasNext()) {
                TLRPC.User user = it.next();
                this.userMap.put(Integer.valueOf(user.id), user);
            }
            groupingApplyInfos(this.contactsApplyInfos);
            ListAdapter listAdapter3 = this.adapter;
            if (listAdapter3 != null) {
                listAdapter3.setList(this.mapKeysList, this.map, false);
                this.adapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.contactApplyUpdateCount && this.adapter != null && args != null && args.length > 0) {
            int intValue = args[0].intValue();
            boolean unused = this.adapter.isFirst = false;
            this.adapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SectionsAdapter {
        /* access modifiers changed from: private */
        public boolean isFirst;
        private ArrayList<String> list;
        private Context mContext;
        private HashMap<String, ArrayList<TLRPCContacts.ContactApplyInfo>> updateMaps;

        /* access modifiers changed from: package-private */
        public void setList(ArrayList<String> list2, HashMap<String, ArrayList<TLRPCContacts.ContactApplyInfo>> map, boolean isFirst2) {
            this.list = list2;
            this.updateMaps = map;
            this.isFirst = isFirst2;
        }

        ListAdapter(Context context) {
            this.mContext = context;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            int count = super.getItemCount();
            int i = 0;
            if (!this.isFirst) {
                NewFriendsActivity.this.progressBar.setVisibility(8);
                LinearLayout linearLayout = NewFriendsActivity.this.emptyLayout;
                if (count != 1) {
                    i = 8;
                }
                linearLayout.setVisibility(i);
            } else if (NewFriendsActivity.this.progressBar.getVisibility() != 0) {
                NewFriendsActivity.this.progressBar.setVisibility(0);
            }
        }

        public int getItemCount() {
            int count = 1;
            if (this.updateMaps != null) {
                Iterator<String> it = this.list.iterator();
                while (it.hasNext()) {
                    count += this.updateMaps.get(it.next()).size();
                }
            }
            return count;
        }

        public int getSectionCount() {
            return this.list.size() + 1;
        }

        public int getPositionForSection(int section) {
            if (section == -1) {
                return -1;
            }
            int positionStart = 0;
            int N = getSectionCount();
            for (int i = 0; i < N; i++) {
                if (i >= section) {
                    return positionStart;
                }
                positionStart += getCountForSection(i);
            }
            return -1;
        }

        public int getCountForSection(int section) {
            if (section == 0) {
                return 1;
            }
            return this.updateMaps.get(this.list.get(section - 1)).size();
        }

        public boolean isEnabled(int section, int row) {
            return true;
        }

        public int getItemViewType(int section, int position) {
            return section == 0 ? 0 : 1;
        }

        public Object getItem(int section, int position) {
            if (section == 0) {
                return null;
            }
            return this.updateMaps.get(this.list.get(section - 1)).get(position);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            Drawable bg;
            int i = position;
            RecyclerView.ViewHolder viewHolder = holder;
            if (holder.getItemViewType() == 1) {
                SwipeLayout swipeLayout = (SwipeLayout) viewHolder.itemView;
                swipeLayout.setItemWidth(AndroidUtilities.dp(65.0f));
                int radius = AndroidUtilities.dp(5.0f);
                if (getItemCount() == 1) {
                    bg = Theme.createRoundRectDrawable((float) radius, Theme.getColor(Theme.key_windowBackgroundWhite));
                } else if (i != 0 && i != getItemCount() - 1) {
                    bg = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite));
                } else if (i == 0) {
                    bg = Theme.createRoundRectDrawable((float) radius, (float) radius, 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite));
                } else {
                    bg = Theme.createRoundRectDrawable(0.0f, 0.0f, (float) radius, (float) radius, Theme.getColor(Theme.key_windowBackgroundWhite));
                }
                swipeLayout.setRightTexts(LocaleController.getString(R.string.Delete));
                swipeLayout.setRightColors(Theme.getColor(Theme.key_chat_inRedCall));
                swipeLayout.setRightTextColors(-1);
                swipeLayout.setTextSize(AndroidUtilities.sp2px(12.0f));
                swipeLayout.setCanFullSwipeFromRight(false);
                swipeLayout.setCanFullSwipeFromLeft(false);
                swipeLayout.setAutoHideSwipe(true);
                swipeLayout.setOnlyOneSwipe(true);
                swipeLayout.rebuildLayout();
                RelativeLayout rlMainLayout = (RelativeLayout) viewHolder.itemView.findViewById(R.id.rlMainLayout);
                rlMainLayout.setBackground(bg);
                BackupImageView avatar = (BackupImageView) viewHolder.itemView.findViewById(R.id.avatarImage);
                avatar.setRoundRadius(AndroidUtilities.dp(7.5f));
                TextView nameText = (TextView) viewHolder.itemView.findViewById(R.id.nameText);
                TextView bioText = (TextView) viewHolder.itemView.findViewById(R.id.bioText);
                MryRoundButton statusBtn = (MryRoundButton) viewHolder.itemView.findViewById(R.id.statusText);
                statusBtn.setPrimaryRoundFillStyle((float) AndroidUtilities.dp(26.0f));
                statusBtn.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)));
                TextView statusText2 = (TextView) viewHolder.itemView.findViewById(R.id.statusText2);
                DividerCell divider = (DividerCell) viewHolder.itemView.findViewById(R.id.divider);
                nameText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                statusText2.setTextColor(-4737097);
                RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) statusBtn.getLayoutParams();
                lp1.rightMargin = AndroidUtilities.dp(5.0f);
                statusBtn.setLayoutParams(lp1);
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) statusText2.getLayoutParams();
                lp2.rightMargin = AndroidUtilities.dp(19.5f);
                statusText2.setLayoutParams(lp2);
                rlMainLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                RelativeLayout.LayoutParams layoutParams = lp2;
                if (i == getCountForSection(section) - 1) {
                    divider.setVisibility(8);
                    if (section == getSectionCount() - 1) {
                        rlMainLayout.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    }
                }
                TLRPCContacts.ContactApplyInfo info = (TLRPCContacts.ContactApplyInfo) getItem(section, position);
                TLRPC.User user = (TLRPC.User) NewFriendsActivity.this.userMap.get(Integer.valueOf(info.from_peer.user_id));
                if (user == null) {
                    user = NewFriendsActivity.this.getMessagesController().getUser(Integer.valueOf(info.from_peer.user_id));
                }
                AvatarDrawable avatarDrawable = new AvatarDrawable(user);
                avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
                DividerCell dividerCell = divider;
                avatar.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
                if (user != null) {
                    nameText.setText(UserObject.getName(user));
                }
                bioText.setText(info.greet);
                if (info.state == 0) {
                    statusBtn.setText(LocaleController.getString("Agree", R.string.Agree));
                    statusBtn.setVisibility(0);
                    statusText2.setVisibility(8);
                    if (NewFriendsActivity.this.getConnectionsManager().getCurrentTime() > info.expire) {
                        statusText2.setText(LocaleController.getString("RequestExpired", R.string.RequestExpired));
                        statusBtn.setVisibility(8);
                        statusText2.setVisibility(0);
                    }
                } else if (info.state == 1) {
                    statusText2.setText(LocaleController.getString("ApplyApproved", R.string.ApplyApproved));
                    statusBtn.setVisibility(8);
                    statusText2.setVisibility(0);
                }
                swipeLayout.setOnSwipeItemClickListener(new SwipeLayout.OnSwipeItemClickListener(info) {
                    private final /* synthetic */ TLRPCContacts.ContactApplyInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onSwipeItemClick(boolean z, int i) {
                        NewFriendsActivity.ListAdapter.this.lambda$onBindViewHolder$2$NewFriendsActivity$ListAdapter(this.f$1, z, i);
                    }
                });
                TLRPC.User user2 = user;
                TLRPC.User user3 = user;
                statusBtn.getParent().requestDisallowInterceptTouchEvent(true);
                statusBtn.setOnClickListener(new View.OnClickListener(info) {
                    private final /* synthetic */ TLRPCContacts.ContactApplyInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        NewFriendsActivity.ListAdapter.this.lambda$onBindViewHolder$9$NewFriendsActivity$ListAdapter(this.f$1, view);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$2$NewFriendsActivity$ListAdapter(TLRPCContacts.ContactApplyInfo info, boolean left, int index) {
            XDialog.Builder builder = new XDialog.Builder(NewFriendsActivity.this.getParentActivity());
            builder.setMessage(LocaleController.getString("SureDeleteApply", R.string.SureDeleteApply));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(info) {
                private final /* synthetic */ TLRPCContacts.ContactApplyInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    NewFriendsActivity.ListAdapter.this.lambda$null$0$NewFriendsActivity$ListAdapter(this.f$1, dialogInterface, i);
                }
            });
            XDialog xDialog = builder.create();
            NewFriendsActivity.this.showDialog(xDialog);
            xDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    NewFriendsActivity.ListAdapter.this.lambda$null$1$NewFriendsActivity$ListAdapter(dialogInterface);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$NewFriendsActivity$ListAdapter(TLRPCContacts.ContactApplyInfo info, DialogInterface dialog, int which) {
            ArrayList<Integer> ids = new ArrayList<>();
            ids.add(Integer.valueOf(info.id));
            NewFriendsActivity.this.deleteApplyRequest(ids);
        }

        public /* synthetic */ void lambda$null$1$NewFriendsActivity$ListAdapter(DialogInterface dialog) {
            NewFriendsActivity.this.adapter.notifyDataSetChanged();
        }

        public /* synthetic */ void lambda$onBindViewHolder$9$NewFriendsActivity$ListAdapter(TLRPCContacts.ContactApplyInfo info, View v) {
            AndroidUtilities.runOnUIThread(new Runnable(info) {
                private final /* synthetic */ TLRPCContacts.ContactApplyInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NewFriendsActivity.ListAdapter.this.lambda$null$8$NewFriendsActivity$ListAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$8$NewFriendsActivity$ListAdapter(TLRPCContacts.ContactApplyInfo info) {
            WalletDialogUtil.showWalletDialog(NewFriendsActivity.this, (String) null, LocaleController.getString("AcceptContactTip", R.string.AcceptContactTip), LocaleController.getString("Cancel", R.string.Cancel), LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null, new DialogInterface.OnClickListener(info) {
                private final /* synthetic */ TLRPCContacts.ContactApplyInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    NewFriendsActivity.ListAdapter.this.lambda$null$7$NewFriendsActivity$ListAdapter(this.f$1, dialogInterface, i);
                }
            }, (DialogInterface.OnDismissListener) null);
        }

        public /* synthetic */ void lambda$null$7$NewFriendsActivity$ListAdapter(TLRPCContacts.ContactApplyInfo info, DialogInterface dialogInterface, int i) {
            XAlertDialog progressDialog = new XAlertDialog(NewFriendsActivity.this.getParentActivity(), 4);
            progressDialog.setLoadingText(LocaleController.getString(R.string.ApplyAdding));
            TLRPCContacts.AcceptContactApply req = new TLRPCContacts.AcceptContactApply();
            req.apply_id = info.id;
            req.group_id = 0;
            req.first_name = "";
            req.last_name = "";
            ConnectionsManager access$800 = NewFriendsActivity.this.getConnectionsManager();
            int reqId = NewFriendsActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog, info) {
                private final /* synthetic */ XAlertDialog f$1;
                private final /* synthetic */ TLRPCContacts.ContactApplyInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NewFriendsActivity.ListAdapter.this.lambda$null$5$NewFriendsActivity$ListAdapter(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
            access$800.bindRequestToGuid(reqId, NewFriendsActivity.this.classGuid);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    NewFriendsActivity.ListAdapter.this.lambda$null$6$NewFriendsActivity$ListAdapter(this.f$1, dialogInterface);
                }
            });
            progressDialog.show();
        }

        public /* synthetic */ void lambda$null$5$NewFriendsActivity$ListAdapter(XAlertDialog progressDialog, TLRPCContacts.ContactApplyInfo info, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(error, progressDialog, response, info) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ XAlertDialog f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ TLRPCContacts.ContactApplyInfo f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    NewFriendsActivity.ListAdapter.this.lambda$null$4$NewFriendsActivity$ListAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$null$4$NewFriendsActivity$ListAdapter(TLRPC.TL_error error, XAlertDialog progressDialog, TLObject response, TLRPCContacts.ContactApplyInfo info) {
            if (error != null) {
                progressDialog.dismiss();
                ToastUtils.show((CharSequence) ContactsUtils.getAboutContactsErrText(error));
                return;
            }
            NewFriendsActivity.this.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            TLRPCContacts.ContactApplyInfo aInfo = new TLRPCContacts.ContactApplyInfo();
            aInfo.id = info.id;
            aInfo.state = 1;
            NewFriendsActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.contactApplyUpdateState, Integer.valueOf(info.id), 1);
            progressDialog.setLoadingImage(NewFriendsActivity.this.getParentActivity().getResources().getDrawable(R.mipmap.ic_apply_send_done), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(20.0f));
            progressDialog.setLoadingText(LocaleController.getString(R.string.AddedContacts));
            NewFriendsActivity.this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    XAlertDialog.this.dismiss();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        public /* synthetic */ void lambda$null$6$NewFriendsActivity$ListAdapter(int reqId, DialogInterface hintDialog) {
            NewFriendsActivity.this.getConnectionsManager().cancelRequest(reqId, true);
        }

        public View getSectionHeaderView(int section, View view) {
            return null;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new View(this.mContext);
            } else if (viewType == 1) {
                view = new SwipeLayout(this.mContext) {
                    public boolean onTouchEvent(MotionEvent event) {
                        if (isExpanded()) {
                            return true;
                        }
                        return super.onTouchEvent(event);
                    }
                };
                ((ViewGroup) view).setClipChildren(false);
                ((SwipeLayout) view).setUpView(LayoutInflater.from(this.mContext).inflate(R.layout.item_contacts_apply_layout, parent, false));
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(65.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section == 0) {
                return null;
            }
            return this.list.get(section - 1);
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }
    }

    private void notifyServerClearUnread() {
        TLRPCContacts.ClearUnreadApply req = new TLRPCContacts.ClearUnreadApply();
        req.max_apply_id = MessagesController.getMainSettings(this.currentAccount).getInt("contacts_apply_id", 0);
        getConnectionsManager().sendRequest(req, $$Lambda$NewFriendsActivity$oDpPyptf2syHUcnojAffKTcKeWE.INSTANCE);
    }

    static /* synthetic */ void lambda$notifyServerClearUnread$3(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
        }
    }

    /* access modifiers changed from: private */
    public void deleteApplyRequest(ArrayList<Integer> ids) {
        TLRPCContacts.DeleteContactApply req = new TLRPCContacts.DeleteContactApply();
        TLRPCContacts.DeleteActionClearSome action = new TLRPCContacts.DeleteActionClearSome();
        action.ids = ids;
        req.action = action;
        getConnectionsManager().sendRequest(req, new RequestDelegate(ids) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                NewFriendsActivity.this.lambda$deleteApplyRequest$6$NewFriendsActivity(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$deleteApplyRequest$6$NewFriendsActivity(ArrayList ids, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    NewFriendsActivity.this.lambda$null$4$NewFriendsActivity();
                }
            });
        } else if (response instanceof TLRPC.TL_boolTrue) {
            Iterator it = ids.iterator();
            while (it.hasNext()) {
                Integer id = (Integer) it.next();
                if (getIndex(id.intValue()) != -1) {
                    this.contactsApplyInfos.remove(getIndex(id.intValue()));
                }
                this.users.remove(getMessagesController().getUser(id));
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    NewFriendsActivity.this.notifyListUpdate();
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    NewFriendsActivity.this.lambda$null$5$NewFriendsActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$4$NewFriendsActivity() {
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("new_friends_delete_fail", R.string.new_friends_delete_fail));
    }

    public /* synthetic */ void lambda$null$5$NewFriendsActivity() {
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("new_friends_delete_fail", R.string.new_friends_delete_fail));
    }

    private void deleteAllRequsts() {
        TLRPCContacts.DeleteContactApply req = new TLRPCContacts.DeleteContactApply();
        TLRPCContacts.DeleteActionClearHistory action = new TLRPCContacts.DeleteActionClearHistory();
        action.max_id = MessagesController.getMainSettings(this.currentAccount).getInt("contacts_apply_id", 0);
        req.action = action;
        getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                NewFriendsActivity.this.lambda$deleteAllRequsts$9$NewFriendsActivity(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$deleteAllRequsts$9$NewFriendsActivity(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    NewFriendsActivity.this.lambda$null$7$NewFriendsActivity();
                }
            });
        } else if (response instanceof TLRPC.TL_boolTrue) {
            this.contactsApplyInfos.clear();
            this.users.clear();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    NewFriendsActivity.this.notifyListUpdate();
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    NewFriendsActivity.this.lambda$null$8$NewFriendsActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$7$NewFriendsActivity() {
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("new_friends_delete_fail", R.string.new_friends_delete_fail));
    }

    public /* synthetic */ void lambda$null$8$NewFriendsActivity() {
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("new_friends_delete_fail", R.string.new_friends_delete_fail));
    }

    /* access modifiers changed from: private */
    public void notifyListUpdate() {
        this.userMap.clear();
        Iterator<TLRPC.User> it = this.users.iterator();
        while (it.hasNext()) {
            TLRPC.User user = it.next();
            this.userMap.put(Integer.valueOf(user.id), user);
        }
        groupingApplyInfos(this.contactsApplyInfos);
        this.adapter.setList(this.mapKeysList, this.map, false);
        this.adapter.notifyDataSetChanged();
    }

    private int getIndex(int id) {
        if (this.contactsApplyInfos == null) {
            return -1;
        }
        for (int i = 0; i < this.contactsApplyInfos.size(); i++) {
            if (this.contactsApplyInfos.get(i).id == id) {
                return i;
            }
        }
        return -1;
    }
}

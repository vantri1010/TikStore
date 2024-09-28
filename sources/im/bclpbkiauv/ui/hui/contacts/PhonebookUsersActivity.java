package im.bclpbkiauv.ui.hui.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.bjz.comm.net.premission.PermissionUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseSearchViewFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.CharacterParser;
import im.bclpbkiauv.ui.hui.contacts.PhonebookUsersActivity;
import im.bclpbkiauv.ui.hviews.MryEmptyTextProgressView;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PhonebookUsersActivity extends BaseSearchViewFragment implements NotificationCenter.NotificationCenterDelegate {
    private boolean askAboutContacts = true;
    private boolean checkPermission = true;
    /* access modifiers changed from: private */
    public HashMap<String, TLRPC.TL_inputPhoneContact> inputPhoneContactsMap = new HashMap<>();
    private LinearLayoutManager layoutManager;
    @BindView(2131296893)
    RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    @BindView(2131296558)
    MryEmptyTextProgressView mEmptyView;
    @BindView(2131297291)
    SideBar mSideBar;
    @BindView(2131297736)
    MryTextView mTvChar;
    private HashMap<String, ArrayList<TLRPC.User>> map = new HashMap<>();
    private ArrayList<String> mapKeysList = new ArrayList<>();
    private AlertDialog permissionDialog;
    private ArrayList<TLRPC.User> phoneBookUsers = new ArrayList<>();
    @BindView(2131297262)
    FrameLayout searchLayout;
    @BindView(2131297263)
    MrySearchView searchView;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactAboutPhonebookLoaded);
        this.checkPermission = UserConfig.getInstance(this.currentAccount).syncContacts;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactAboutPhonebookLoaded);
    }

    public void onResume() {
        super.onResume();
        this.mEmptyView.showProgress();
        if (!this.checkPermission || Build.VERSION.SDK_INT < 23) {
            this.mEmptyView.showTextView();
            return;
        }
        Activity activity = getParentActivity();
        if (activity != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission(PermissionUtils.LINKMAIN) == 0) {
                ContactsController.getInstance(this.currentAccount).checkPhonebookUsers();
            } else if (activity.shouldShowRequestPermissionRationale(PermissionUtils.LINKMAIN)) {
                AlertDialog create = AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() {
                    public final void run(int i) {
                        PhonebookUsersActivity.this.lambda$onResume$0$PhonebookUsersActivity(i);
                    }
                }).create();
                this.permissionDialog = create;
                showDialog(create);
            } else {
                askForPermissons(true);
            }
        }
    }

    public /* synthetic */ void lambda$onResume$0$PhonebookUsersActivity(int param) {
        this.askAboutContacts = param != 0;
        if (param != 0) {
            askForPermissons(false);
        }
    }

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null && UserConfig.getInstance(this.currentAccount).syncContacts && activity.checkSelfPermission(PermissionUtils.LINKMAIN) != 0) {
            if (!alert || !this.askAboutContacts) {
                ArrayList<String> permissons = new ArrayList<>();
                permissons.add(PermissionUtils.LINKMAIN);
                activity.requestPermissions((String[]) permissons.toArray(new String[0]), 1);
                return;
            }
            showDialog(AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    PhonebookUsersActivity.this.lambda$askForPermissons$1$PhonebookUsersActivity(i);
                }
            }).create());
        }
    }

    public /* synthetic */ void lambda$askForPermissons$1$PhonebookUsersActivity(int param) {
        this.askAboutContacts = param != 0;
        if (param != 0) {
            askForPermissons(false);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a && PermissionUtils.LINKMAIN.equals(permissions[a])) {
                    if (grantResults[a] == 0) {
                        ContactsController.getInstance(this.currentAccount).checkPhonebookUsers();
                    } else {
                        SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                        this.askAboutContacts = false;
                        edit.putBoolean("askAboutContacts", false).commit();
                    }
                }
            }
        }
    }

    private void groupingUsers(ArrayList<TLRPC.User> users) {
        String key;
        if (users != null) {
            this.mapKeysList.clear();
            this.map.clear();
            Iterator<TLRPC.User> it = users.iterator();
            while (it.hasNext()) {
                TLRPC.User user = it.next();
                String key2 = CharacterParser.getInstance().getSelling(UserObject.getFirstName(user));
                if (key2.length() > 1) {
                    key2 = key2.substring(0, 1);
                }
                if (key2.length() == 0) {
                    key = "#";
                } else {
                    key = key2.toUpperCase();
                }
                ArrayList<TLRPC.User> arr = this.map.get(key);
                if (arr == null) {
                    arr = new ArrayList<>();
                    this.map.put(key, arr);
                    this.mapKeysList.add(key);
                }
                arr.add(user);
            }
        }
    }

    public void onBeginSlide() {
        super.onBeginSlide();
        MrySearchView mrySearchView = this.searchView;
        if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_phone_book_users_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        super.createView(context);
        initActionbar();
        initEmptyView();
        initSideBar();
        initList();
        return this.fragmentView;
    }

    private void initSideBar() {
        this.mSideBar.setTextView(this.mTvChar);
        this.mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public final void onTouchingLetterChanged(String str) {
                PhonebookUsersActivity.this.lambda$initSideBar$2$PhonebookUsersActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$2$PhonebookUsersActivity(String s) {
        int position;
        if ("↑".equals(s)) {
            this.listView.scrollToPosition(0);
        } else if (!"☆".equals(s) && (position = this.listViewAdapter.getPositionForSection(this.listViewAdapter.getSectionForChar(s.charAt(0)))) != -1) {
            this.listView.getLayoutManager().scrollToPosition(position);
        }
    }

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        this.searchView.setHintText(LocaleController.getString("Search", R.string.Search));
        this.searchLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        return this.searchView;
    }

    /* access modifiers changed from: protected */
    public void initSearchView() {
        super.initSearchView();
    }

    private void initActionbar() {
        this.actionBar.setTitle(LocaleController.getString("AppContacts", R.string.AppContacts));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhonebookUsersActivity.this.finishFragment();
                }
            }
        });
    }

    private void initEmptyView() {
        this.mEmptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.mEmptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.mEmptyView.setTopImage(R.mipmap.img_empty_default);
    }

    private void initList() {
        this.listView.setEmptyView(this.mEmptyView);
        this.listView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.listView.setHasFixedSize(true);
        this.listView.setNestedScrollingEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.fragmentView.getContext(), 1, false);
        this.layoutManager = linearLayoutManager;
        this.listView.setLayoutManager(linearLayoutManager);
        ListAdapter listAdapter = new ListAdapter(getParentActivity());
        this.listViewAdapter = listAdapter;
        listAdapter.setList(this.mapKeysList, this.map);
        this.listView.setOverScrollMode(2);
        this.listView.requestDisallowInterceptTouchEvent(true);
        this.listView.setDisallowInterceptTouchEvents(true);
        this.listView.setDisableHighlightState(true);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                PhonebookUsersActivity.this.mSideBar.setChooseChar(PhonebookUsersActivity.this.listViewAdapter.getLetter(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
            }
        });
        this.listViewAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactAboutPhonebookLoaded) {
            ArrayList<TLRPC.User> arrayList = args[0];
            this.phoneBookUsers = arrayList;
            this.inputPhoneContactsMap = args[1];
            groupingUsers(arrayList);
            this.listViewAdapter.setList(this.mapKeysList, this.map);
            this.listViewAdapter.notifyDataSetChanged();
            this.mEmptyView.showTextView();
        }
    }

    private class ListAdapter extends RecyclerListView.SectionsAdapter {
        private ArrayList<String> list;
        private Context mContext;
        private HashMap<String, ArrayList<TLRPC.User>> updateMaps;

        public void setList(ArrayList<String> list2, HashMap<String, ArrayList<TLRPC.User>> map) {
            this.list = list2;
            this.updateMaps = map;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            int count = 0;
            if (this.updateMaps != null) {
                Iterator<String> it = this.list.iterator();
                while (it.hasNext()) {
                    count += this.updateMaps.get(it.next()).size();
                }
            }
            return count;
        }

        public int getSectionCount() {
            return this.list.size();
        }

        public int getCountForSection(int section) {
            return this.updateMaps.get(this.list.get(section)).size();
        }

        public int getSectionForChar(char section) {
            for (int i = 0; i < getSectionCount(); i++) {
                if (this.list.get(i).toUpperCase().charAt(0) == section) {
                    return i;
                }
            }
            return -1;
        }

        public int getPositionForSection(int section) {
            if (section == -1) {
                return -1;
            }
            int positionStart = 0;
            for (int i = 0; i < getSectionCount(); i++) {
                if (i >= section) {
                    return positionStart;
                }
                positionStart += getCountForSection(i);
            }
            return -1;
        }

        public boolean isEnabled(int section, int row) {
            return true;
        }

        public int getItemViewType(int section, int position) {
            return 1;
        }

        public Object getItem(int section, int position) {
            return this.updateMaps.get(this.list.get(section)).get(position);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            String str;
            RecyclerView.ViewHolder viewHolder = holder;
            if (holder.getItemViewType() != 1) {
                int i = position;
                return;
            }
            RelativeLayout rlMainLayout = (RelativeLayout) viewHolder.itemView.findViewById(R.id.rlMainLayout);
            BackupImageView avatar = (BackupImageView) viewHolder.itemView.findViewById(R.id.avatarImage);
            avatar.setRoundRadius(AndroidUtilities.dp(7.5f));
            TextView nameText = (TextView) viewHolder.itemView.findViewById(R.id.nameText);
            TextView appCodeNameText = (TextView) viewHolder.itemView.findViewById(R.id.bioText);
            MryRoundButton statusBtn = (MryRoundButton) viewHolder.itemView.findViewById(R.id.statusText);
            TextView statusText2 = (TextView) viewHolder.itemView.findViewById(R.id.statusText2);
            DividerCell divider = (DividerCell) viewHolder.itemView.findViewById(R.id.divider);
            nameText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            appCodeNameText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            statusBtn.setPrimaryRoundFillStyle((float) AndroidUtilities.dp(26.0f));
            statusText2.setTextColor(-4737097);
            RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) statusBtn.getLayoutParams();
            lp1.rightMargin = AndroidUtilities.dp(27.5f);
            statusBtn.setLayoutParams(lp1);
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) statusText2.getLayoutParams();
            lp2.rightMargin = AndroidUtilities.dp(27.5f);
            statusText2.setLayoutParams(lp2);
            if (position == getItemCount() - 1) {
                divider.setVisibility(8);
            }
            TLRPC.User user = (TLRPC.User) getItem(section, position);
            AvatarDrawable avatarDrawable = new AvatarDrawable(user);
            avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            avatar.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
            TLRPC.TL_inputPhoneContact contact = (TLRPC.TL_inputPhoneContact) PhonebookUsersActivity.this.inputPhoneContactsMap.get(user.phone);
            if (contact != null) {
                nameText.setText(contact.last_name + contact.first_name);
            }
            if (TextUtils.isEmpty(user.first_name)) {
                str = "";
                RelativeLayout relativeLayout = rlMainLayout;
            } else {
                StringBuilder sb = new StringBuilder();
                RelativeLayout relativeLayout2 = rlMainLayout;
                sb.append(LocaleController.getString("AppName", R.string.AppName));
                sb.append(": ");
                sb.append(user.first_name);
                str = sb.toString();
            }
            appCodeNameText.setText(str);
            if (!user.mutual_contact) {
                statusBtn.setText(LocaleController.getString("Add", R.string.Add));
                statusBtn.setVisibility(0);
                statusText2.setVisibility(8);
            } else {
                statusText2.setText(LocaleController.getString("AddedContacts", R.string.AddedContacts));
                statusBtn.setVisibility(8);
                statusText2.setVisibility(0);
            }
            statusBtn.setOnClickListener(new View.OnClickListener(user) {
                private final /* synthetic */ TLRPC.User f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    PhonebookUsersActivity.ListAdapter.this.lambda$onBindViewHolder$0$PhonebookUsersActivity$ListAdapter(this.f$1, view);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$PhonebookUsersActivity$ListAdapter(TLRPC.User user, View v) {
            PhonebookUsersActivity.this.startContactApply("hello", user);
            Bundle bundle = new Bundle();
            bundle.putInt("from_type", 6);
            PhonebookUsersActivity.this.presentFragment(new AddContactsInfoActivity(bundle, user));
        }

        public View getSectionHeaderView(int section, View view) {
            return null;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 1) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_contacts_apply_layout, parent, false);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(65.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section == -1) {
                return null;
            }
            return this.list.get(section);
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void startContactApply(String greet, TLRPC.User user) {
        XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
        TLRPCContacts.ContactsRequestApply req = new TLRPCContacts.ContactsRequestApply();
        req.flag = 0;
        req.from_type = 2;
        req.inputUser = getMessagesController().getInputUser(user);
        req.first_name = user.first_name;
        req.last_name = user.first_name;
        req.greet = greet;
        req.group_id = 0;
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ XAlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PhonebookUsersActivity.this.lambda$startContactApply$3$PhonebookUsersActivity(this.f$1, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                PhonebookUsersActivity.this.lambda$startContactApply$4$PhonebookUsersActivity(this.f$1, dialogInterface);
            }
        });
        try {
            progressDialog.show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$startContactApply$3$PhonebookUsersActivity(XAlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            progressDialog.dismiss();
            XDialog.Builder builder = new XDialog.Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("friends_apply_fail", R.string.friends_apply_fail));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            builder.create().show();
            return;
        }
        progressDialog.dismiss();
        if (response instanceof TLRPCContacts.ContactApplyResp) {
            getMessagesController().saveContactsAppliesId(((TLRPCContacts.ContactApplyResp) response).applyInfo.id);
        }
    }

    public /* synthetic */ void lambda$startContactApply$4$PhonebookUsersActivity(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    public void onSearchExpand() {
        this.mEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
    }

    public void onSearchCollapse() {
        this.mEmptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        groupingUsers(this.phoneBookUsers);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.setList(this.mapKeysList, this.map);
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void onTextChange(String text) {
        if (!TextUtils.isEmpty(text)) {
            ArrayList<TLRPC.User> searchedUsers = new ArrayList<>();
            Iterator<TLRPC.User> it = this.phoneBookUsers.iterator();
            while (it.hasNext()) {
                TLRPC.User user = it.next();
                TLRPC.TL_inputPhoneContact contact = this.inputPhoneContactsMap.get(user.phone);
                if (contact != null) {
                    String str = "";
                    if (contact.last_name != null) {
                        if ((contact.last_name + contact.first_name) != null) {
                            str = contact.first_name;
                        }
                    }
                    String name = str;
                    if (name.contains(text) || name.toLowerCase().contains(text)) {
                        searchedUsers.add(user);
                    }
                }
            }
            groupingUsers(searchedUsers);
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.setList(this.mapKeysList, this.map);
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }
}

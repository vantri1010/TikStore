package im.bclpbkiauv.ui.hui.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hcells.MryDividerCell;
import im.bclpbkiauv.ui.hui.CharacterParser;
import im.bclpbkiauv.ui.hui.contacts.AddGroupingUserActivity;
import im.bclpbkiauv.ui.hui.contacts.CreateGroupingActivity;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;
import im.bclpbkiauv.ui.hviews.slidemenu.SwipeLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CreateGroupingActivity extends BaseFragment {
    private static final int item_done = 1;
    /* access modifiers changed from: private */
    public boolean hasEmoji;
    private boolean isFirst = true;
    /* access modifiers changed from: private */
    public ListAdapter mAdapter;
    @BindView(2131296595)
    MryEditText mEtGroupName;
    @BindView(2131296631)
    FrameLayout mFlGroupName;
    @BindView(2131296793)
    ImageView mIvClear;
    @BindView(2131296933)
    LinearLayout mLlContainer;
    @BindView(2131296947)
    LinearLayout mLlNotSupportEmojiTips;
    @BindView(2131297248)
    RecyclerListView mRvUsers;
    @BindView(2131297291)
    SideBar mSideBar;
    @BindView(2131297715)
    TextView mTvAddUser;
    @BindView(2131297736)
    MryTextView mTvChar;
    private TextWatcher mWatcher;
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<TLRPC.User>> map = new HashMap<>();
    /* access modifiers changed from: private */
    public ArrayList<String> mapKeysList = new ArrayList<>();
    /* access modifiers changed from: private */
    public List<TLRPC.User> selectedUsers = new ArrayList();
    /* access modifiers changed from: private */
    public MryTextView tvOkView;

    public boolean onFragmentCreate() {
        this.swipeBackEnabled = false;
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_create_grouping_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initActionbar();
        initView();
        return this.fragmentView;
    }

    private void initActionbar() {
        this.actionBar.setAddToContainer(false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("AddGrouping", R.string.AddGrouping));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!TextUtils.isEmpty(CreateGroupingActivity.this.mEtGroupName.getText())) {
                        CreateGroupingActivity.this.showSaveDialog();
                    } else {
                        CreateGroupingActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    CreateGroupingActivity.this.createGrouping();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        MryTextView mryTextView = new MryTextView(getParentActivity());
        this.tvOkView = mryTextView;
        mryTextView.setEnabled(false);
        this.tvOkView.setText(LocaleController.getString("Done", R.string.Done));
        this.tvOkView.setTextSize(1, 14.0f);
        this.tvOkView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.tvOkView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.tvOkView.setGravity(16);
        menu.addItemView(1, this.tvOkView);
        ((RelativeLayout) this.fragmentView).addView(this.actionBar, 0);
    }

    private void initView() {
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) this.mLlContainer.getLayoutParams();
        lp1.topMargin = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        this.mLlContainer.setLayoutParams(lp1);
        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) this.mLlNotSupportEmojiTips.getLayoutParams();
        lp2.topMargin = AndroidUtilities.statusBarHeight;
        this.mLlNotSupportEmojiTips.setLayoutParams(lp2);
        this.mFlGroupName.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mTvAddUser.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mTvAddUser.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        Drawable[] ds = this.mTvAddUser.getCompoundDrawables();
        if (ds[0] != null) {
            ds[0].setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton), PorterDuff.Mode.SRC_IN));
            this.mTvAddUser.setCompoundDrawables(ds[0], ds[1], ds[2], ds[3]);
        }
        this.mLlNotSupportEmojiTips.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mEtGroupName.setFilters(new InputFilter[]{new LengthFilter(28)});
        MryEditText mryEditText = this.mEtGroupName;
        AnonymousClass2 r4 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 0;
                boolean unused = CreateGroupingActivity.this.hasEmoji = false;
                int i2 = 0;
                while (true) {
                    if (i2 >= s.length()) {
                        break;
                    }
                    int type = Character.getType(s.charAt(i2));
                    if (type == 19 || type == 28) {
                        boolean unused2 = CreateGroupingActivity.this.hasEmoji = true;
                    } else {
                        i2++;
                    }
                }
                CreateGroupingActivity.this.mLlNotSupportEmojiTips.setVisibility(CreateGroupingActivity.this.hasEmoji ? 0 : 8);
                CreateGroupingActivity.this.actionBar.setVisibility(CreateGroupingActivity.this.hasEmoji ? 4 : 0);
                ImageView imageView = CreateGroupingActivity.this.mIvClear;
                if (TextUtils.isEmpty(s)) {
                    i = 4;
                }
                imageView.setVisibility(i);
                CreateGroupingActivity.this.tvOkView.setEnabled(!TextUtils.isEmpty(s));
            }

            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(CreateGroupingActivity.this.mEtGroupName.getHint())) {
                    CreateGroupingActivity.this.mEtGroupName.setHint(LocaleController.getString(R.string.EmptyGroupingNameTips));
                }
            }
        };
        this.mWatcher = r4;
        mryEditText.addTextChangedListener(r4);
        this.mEtGroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public final void onFocusChange(View view, boolean z) {
                CreateGroupingActivity.this.lambda$initView$0$CreateGroupingActivity(view, z);
            }
        });
        initSideBar();
        initList();
    }

    public /* synthetic */ void lambda$initView$0$CreateGroupingActivity(View v, boolean hasFocus) {
        if (v.getId() == R.id.et_group_name && this.isFirst && hasFocus) {
            this.mEtGroupName.setHint("");
            this.isFirst = false;
        }
    }

    private void initSideBar() {
        this.mSideBar.setTextView(this.mTvChar);
        this.mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            public final void onTouchingLetterChanged(String str) {
                CreateGroupingActivity.this.lambda$initSideBar$1$CreateGroupingActivity(str);
            }
        });
    }

    public /* synthetic */ void lambda$initSideBar$1$CreateGroupingActivity(String s) {
        int position;
        if ("↑".equals(s)) {
            this.mRvUsers.scrollToPosition(0);
        } else if (!"☆".equals(s) && (position = this.mAdapter.getPositionForSection(this.mAdapter.getSectionForChar(s.charAt(0)))) != -1) {
            this.mRvUsers.getLayoutManager().scrollToPosition(position);
        }
    }

    private void initList() {
        this.mRvUsers.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        ListAdapter listAdapter = new ListAdapter();
        this.mAdapter = listAdapter;
        listAdapter.setList(this.mapKeysList, this.map);
        this.mRvUsers.setAdapter(this.mAdapter);
        this.mRvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CreateGroupingActivity.this.mSideBar.setChooseChar(CreateGroupingActivity.this.mAdapter.getLetter(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
            }
        });
        this.mAdapter.notifyDataSetChanged();
    }

    @OnClick({2131296793, 2131297715})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_clear) {
            this.mEtGroupName.setText((CharSequence) null);
        } else if (id == R.id.tv_add_user) {
            AddGroupingUserActivity fragment = new AddGroupingUserActivity(this.selectedUsers, 1);
            fragment.setDelegate(new AddGroupingUserActivity.AddGroupingUserActivityDelegate() {
                public final void didSelectedContact(ArrayList arrayList) {
                    CreateGroupingActivity.this.lambda$onViewClicked$2$CreateGroupingActivity(arrayList);
                }
            });
            presentFragment(fragment);
        }
    }

    public /* synthetic */ void lambda$onViewClicked$2$CreateGroupingActivity(ArrayList users) {
        this.selectedUsers.clear();
        this.selectedUsers.addAll(users);
        groupingUsers(this.selectedUsers);
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter != null) {
            listAdapter.setList(this.mapKeysList, this.map);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SectionsAdapter {
        private ArrayList<String> list;
        private HashMap<String, ArrayList<TLRPC.User>> updateMaps;

        private ListAdapter() {
        }

        public void setList(ArrayList<String> list2, HashMap<String, ArrayList<TLRPC.User>> map) {
            this.list = list2;
            this.updateMaps = map;
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

        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section != -1) {
                return this.list.get(section);
            }
            return null;
        }

        public int getPositionForScrollProgress(float progress) {
            return 0;
        }

        public boolean isEnabled(int section, int row) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SwipeLayout swipeLayout = new SwipeLayout(CreateGroupingActivity.this.getParentActivity()) {
                public boolean onTouchEvent(MotionEvent event) {
                    if (isExpanded()) {
                        return true;
                    }
                    return super.onTouchEvent(event);
                }
            };
            swipeLayout.setUpView(LayoutInflater.from(CreateGroupingActivity.this.getParentActivity()).inflate(R.layout.item_create_grouping, parent, false));
            return new RecyclerListView.Holder(swipeLayout);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            int i = section;
            int i2 = position;
            SwipeLayout swipeLayout = (SwipeLayout) holder.itemView;
            swipeLayout.setItemWidth(AndroidUtilities.dp(65.0f));
            View content = swipeLayout.getMainLayout();
            BackupImageView ivAvatar = (BackupImageView) content.findViewById(R.id.iv_avatar);
            MryDividerCell divider = (MryDividerCell) content.findViewById(R.id.divider);
            TLRPC.User user = (TLRPC.User) getItem(section, position);
            ivAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
            AvatarDrawable drawable = new AvatarDrawable(user);
            drawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            ivAvatar.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) drawable, (Object) user);
            ((MryTextView) content.findViewById(R.id.tv_name)).setText(UserObject.getName(user));
            if (getItemCount() == 1) {
                content.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                divider.setVisibility(8);
            } else if (i == 0 && i2 == 0) {
                content.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
            } else if (i == getSectionCount() - 1 && i2 == getCountForSection(section) - 1) {
                divider.setVisibility(8);
                content.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
            } else {
                content.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            swipeLayout.setRightTexts(LocaleController.getString(R.string.Delete));
            swipeLayout.setRightTextColors(-1);
            swipeLayout.setRightColors(-570319);
            swipeLayout.setTextSize(AndroidUtilities.sp2px(14.0f));
            swipeLayout.rebuildLayout();
            swipeLayout.setOnSwipeItemClickListener(new SwipeLayout.OnSwipeItemClickListener(user) {
                private final /* synthetic */ TLRPC.User f$1;

                {
                    this.f$1 = r2;
                }

                public final void onSwipeItemClick(boolean z, int i) {
                    CreateGroupingActivity.ListAdapter.this.lambda$onBindViewHolder$0$CreateGroupingActivity$ListAdapter(this.f$1, z, i);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$CreateGroupingActivity$ListAdapter(TLRPC.User user, boolean left, int index) {
            if (!left && index == 0) {
                CreateGroupingActivity.this.selectedUsers.remove(user);
                CreateGroupingActivity createGroupingActivity = CreateGroupingActivity.this;
                createGroupingActivity.groupingUsers(createGroupingActivity.selectedUsers);
                setList(CreateGroupingActivity.this.mapKeysList, CreateGroupingActivity.this.map);
                notifyDataSetChanged();
            }
        }

        public int getItemViewType(int section, int position) {
            return 0;
        }

        public Object getItem(int section, int position) {
            return this.updateMaps.get(this.list.get(section)).get(position);
        }

        public View getSectionHeaderView(int section, View view) {
            return null;
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

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (getItemCount() == 0) {
                CreateGroupingActivity.this.mSideBar.setVisibility(8);
                return;
            }
            CreateGroupingActivity.this.mSideBar.setVisibility(0);
            CreateGroupingActivity.this.mSideBar.setChars((String[]) CreateGroupingActivity.this.mapKeysList.toArray(new String[CreateGroupingActivity.this.mapKeysList.size()]));
        }
    }

    /* access modifiers changed from: private */
    public void showSaveDialog() {
        WalletDialog dialog = new WalletDialog(getParentActivity());
        dialog.setMessage(LocaleController.getString("SaveGroupingChangeTips", R.string.SaveGroupingChangeTips));
        dialog.setPositiveButton(LocaleController.getString("Save", R.string.Save), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                CreateGroupingActivity.this.lambda$showSaveDialog$3$CreateGroupingActivity(dialogInterface, i);
            }
        });
        dialog.setNegativeButton(LocaleController.getString("NotSave", R.string.NotSave), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                CreateGroupingActivity.this.lambda$showSaveDialog$4$CreateGroupingActivity(dialogInterface, i);
            }
        });
        showDialog(dialog);
    }

    public /* synthetic */ void lambda$showSaveDialog$3$CreateGroupingActivity(DialogInterface dialogInterface, int i) {
        createGrouping();
    }

    public /* synthetic */ void lambda$showSaveDialog$4$CreateGroupingActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void createGrouping() {
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        TLRPCContacts.TL_createGroup req = new TLRPCContacts.TL_createGroup();
        req.title = this.mEtGroupName.getText().toString();
        req.random_id = (long) getConnectionsManager().getCurrentTime();
        for (TLRPC.User user : this.selectedUsers) {
            TLRPC.InputUser inputUser = getMessagesController().getInputUser(user);
            if (inputUser != null) {
                req.users.add(inputUser);
            }
        }
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CreateGroupingActivity.this.lambda$createGrouping$6$CreateGroupingActivity(this.f$1, tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                CreateGroupingActivity.this.lambda$createGrouping$7$CreateGroupingActivity(this.f$1, dialogInterface);
            }
        });
        showDialog(alertDialog);
    }

    public /* synthetic */ void lambda$createGrouping$6$CreateGroupingActivity(AlertDialog alertDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, error) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CreateGroupingActivity.this.lambda$null$5$CreateGroupingActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$CreateGroupingActivity(AlertDialog alertDialog, TLRPC.TL_error error) {
        alertDialog.dismiss();
        if (error == null) {
            finishFragment();
        } else {
            ToastUtils.show((CharSequence) error.text);
        }
    }

    public /* synthetic */ void lambda$createGrouping$7$CreateGroupingActivity(int reqId, DialogInterface dialog1) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    public void onFragmentDestroy() {
        TextWatcher textWatcher;
        super.onFragmentDestroy();
        MryEditText mryEditText = this.mEtGroupName;
        if (mryEditText != null && (textWatcher = this.mWatcher) != null) {
            mryEditText.removeTextChangedListener(textWatcher);
        }
    }

    /* access modifiers changed from: private */
    public void groupingUsers(List<TLRPC.User> users) {
        String key;
        if (users != null) {
            this.mapKeysList.clear();
            this.map.clear();
            for (TLRPC.User user : users) {
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
            Collections.sort(this.mapKeysList, $$Lambda$CreateGroupingActivity$rHu93luasBa48GoxkRy0nn_9qSU.INSTANCE);
        }
    }

    static /* synthetic */ int lambda$groupingUsers$8(String s1, String s2) {
        char cv1 = s1.charAt(0);
        char cv2 = s2.charAt(0);
        if (cv1 == '#') {
            return 1;
        }
        if (cv2 == '#') {
            return -1;
        }
        return s1.compareTo(s2);
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
        if (this.hasEmoji || TextUtils.isEmpty(this.mEtGroupName.getText())) {
            return super.onBackPressed();
        }
        showSaveDialog();
        return false;
    }
}

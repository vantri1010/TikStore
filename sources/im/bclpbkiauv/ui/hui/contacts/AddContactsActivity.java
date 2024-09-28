package im.bclpbkiauv.ui.hui.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.RegexUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseSearchViewFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.discovery.QrScanActivity;
import im.bclpbkiauv.ui.hui.mine.QrCodeActivity;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import java.util.ArrayList;
import java.util.List;

public class AddContactsActivity extends BaseSearchViewFragment {
    private static final int ID_EMPTY_IMAGE_VIEW = 345686;
    private static final int ID_EMPTY_TEXT_VIEW = 345687;
    private static final int VIEW_TYPE_LIST_ICON = 0;
    private static final int VIEW_TYPE_LIST_SEARCHING = 3;
    private static final int VIEW_TYPE_LIST_SEARCH_EMPTY = 2;
    private static final int VIEW_TYPE_LIST_SEARCH_ERROR = 4;
    private static final int VIEW_TYPE_LIST_SEARCH_RESULT = 1;
    /* access modifiers changed from: private */
    public int codeScan;
    private int from_type;
    /* access modifiers changed from: private */
    public int inviteMore;
    private int lastSectionRow;
    @BindView(2131296918)
    LinearLayout llSearchLayout;
    private ListAdapter mAdapter;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public List<TLRPC.User> mSearchResultList;
    /* access modifiers changed from: private */
    public int myQRCode;
    private int offset;
    /* access modifiers changed from: private */
    public int phoneBook;
    @BindView(2131297082)
    RecyclerListView rcvList;
    private int reqId;
    /* access modifiers changed from: private */
    public int rowCount;
    @BindView(2131297262)
    FrameLayout searchLayout;
    @BindView(2131297263)
    MrySearchView searchView;
    private boolean searching;
    @BindView(2131297640)
    TextView tvSearchHeader;
    @BindView(2131297641)
    TextView tvSearchNumber;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.offset = -1;
        int i = this.rowCount;
        int i2 = i + 1;
        this.rowCount = i2;
        this.myQRCode = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.codeScan = i2;
        this.phoneBook = -1;
        this.rowCount = i3 + 1;
        this.inviteMore = i3;
        this.lastSectionRow = -1;
        return true;
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_add_contacts, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        useButterKnife();
        initActionBar();
        initView();
        initList();
        super.createView(context);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("AddFriends", R.string.AddFriends));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    AddContactsActivity.this.finishFragment();
                }
            }
        });
    }

    private void initView() {
        this.llSearchLayout.setVisibility(8);
        this.llSearchLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AddContactsActivity.this.lambda$initView$0$AddContactsActivity(view);
            }
        });
        this.tvSearchHeader.setText(LocaleController.getString("SearchHint", R.string.SearchHint));
        this.tvSearchNumber.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
    }

    public /* synthetic */ void lambda$initView$0$AddContactsActivity(View v) {
        searchUser(this.tvSearchNumber.getText().toString());
    }

    private void initList() {
        RecyclerListView recyclerListView = (RecyclerListView) this.fragmentView.findViewById(R.id.rcvList);
        this.rcvList = recyclerListView;
        recyclerListView.setHasFixedSize(true);
        this.rcvList.setNestedScrollingEnabled(false);
        this.rcvList.setVerticalScrollBarEnabled(false);
        this.rcvList.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        ListAdapter listAdapter = new ListAdapter();
        this.mAdapter = listAdapter;
        this.rcvList.setAdapter(listAdapter);
        this.rcvList.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                AddContactsActivity.this.lambda$initList$1$AddContactsActivity(view, i);
            }
        });
    }

    public /* synthetic */ void lambda$initList$1$AddContactsActivity(View view, int position) {
        TLRPC.User user;
        if (this.mAdapter.mType == 0) {
            if (position == this.myQRCode) {
                presentFragment(new QrCodeActivity(getUserConfig().getClientUserId()));
            } else if (position == this.codeScan) {
                presentFragment(new QrScanActivity(), false, true);
            } else if (position == this.phoneBook) {
                presentFragment(new PhonebookUsersActivity());
            } else if (position == this.inviteMore) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    String text = ContactsController.getInstance(this.currentAccount).getInviteText(0);
                    intent.putExtra("android.intent.extra.TEXT", text);
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, text), 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } else if (this.mAdapter.mType == 1 && (user = this.mSearchResultList.get(position)) != null) {
            MrySearchView mrySearchView = this.searchView;
            if (mrySearchView != null && mrySearchView.isSearchFieldVisible()) {
                this.searchView.closeSearchField(false);
            }
            if (user.self || user.contact) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", user.id);
                presentFragment(new NewProfileActivity(bundle), true);
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putInt("from_type", this.from_type);
            presentFragment(new AddContactsInfoActivity(bundle2, user), true);
        }
    }

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        this.searchLayout.setBackgroundColor(Theme.getColor(Theme.key_searchview_solidColor));
        this.searchView.setEditTextBackground(getParentActivity().getDrawable(R.drawable.shape_edit_bg));
        this.searchView.setHintText(LocaleController.getString("UserNameOrPhoneNumberSearch", R.string.UserNameOrPhoneNumberSearch));
        return this.searchView;
    }

    public void onSearchExpand() {
        this.searching = true;
        setAdapterViewType(1);
    }

    public boolean canCollapseSearch() {
        return true;
    }

    public void onSearchCollapse() {
        this.searching = false;
        this.mSearchResultList = null;
        cancelRequest();
        setAdapterViewType(0);
    }

    public void onTextChange(String text) {
        boolean z = false;
        this.llSearchLayout.setVisibility(text.length() > 0 ? 0 : 8);
        if (text.length() > 0) {
            this.tvSearchNumber.setText(text);
            LinearLayout linearLayout = this.llSearchLayout;
            if (text.length() > 0) {
                z = true;
            }
            linearLayout.setEnabled(z);
            this.mSearchResultList = null;
            setAdapterViewType(1);
            return;
        }
        this.mSearchResultList = null;
        setAdapterViewType(1);
    }

    public void onActionSearch(String trim) {
        AndroidUtilities.hideKeyboard(this.fragmentView);
    }

    private void searchUser(String inputText) {
        TLRPCContacts.SearchUserByPhone req = new TLRPCContacts.SearchUserByPhone();
        req.phone = inputText;
        cancelRequest();
        setAdapterViewType(3);
        ConnectionsManager connectionsManager = getConnectionsManager();
        int sendRequest = getConnectionsManager().sendRequest(req, new RequestDelegate(inputText) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AddContactsActivity.this.lambda$searchUser$3$AddContactsActivity(this.f$1, tLObject, tL_error);
            }
        });
        this.reqId = sendRequest;
        connectionsManager.bindRequestToGuid(sendRequest, this.classGuid);
    }

    public /* synthetic */ void lambda$searchUser$3$AddContactsActivity(String inputText, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, inputText) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                AddContactsActivity.this.lambda$null$2$AddContactsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$AddContactsActivity(TLRPC.TL_error error, TLObject response, String inputText) {
        this.reqId = 0;
        this.llSearchLayout.setVisibility(8);
        if (error != null) {
            if (!TextUtils.isEmpty(error.text)) {
                setAdapterViewType(4, WalletErrorUtil.getErrorDescription(error.text));
                WalletDialog dialog = new WalletDialog(getParentActivity());
                dialog.setMessage(WalletErrorUtil.getErrorDescription(error.text));
                dialog.setPositiveButton(LocaleController.getString("confirm", R.string.confirm), (DialogInterface.OnClickListener) null);
                showDialog(dialog);
            }
        } else if ((response instanceof TLRPC.TL_contacts_found) && !getParentActivity().isFinishing()) {
            ArrayList<TLRPC.User> arrayList = ((TLRPC.TL_contacts_found) response).users;
            this.mSearchResultList = arrayList;
            if (arrayList != null && this.mAdapter != null) {
                if (arrayList.size() == 0) {
                    setAdapterViewType(2);
                    WalletDialog dialog2 = new WalletDialog(getParentActivity());
                    dialog2.setMessage(LocaleController.getString("UserNotExist", R.string.UserNotExist));
                    dialog2.setPositiveButton(LocaleController.getString("confirm", R.string.confirm), (DialogInterface.OnClickListener) null);
                    showDialog(dialog2);
                    return;
                }
                if (this.mSearchResultList.size() > 1) {
                    this.mSearchResultList = this.mSearchResultList.subList(0, 1);
                }
                setAdapterViewType(1);
                if (RegexUtils.isMobileSimple(inputText)) {
                    this.from_type = 3;
                } else {
                    this.from_type = 4;
                }
            }
        }
    }

    private void cancelRequest() {
        if (this.reqId != 0) {
            getConnectionsManager().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
    }

    private void setAdapterViewType(int viewType) {
        setAdapterViewType(viewType, (String) null);
    }

    private void setAdapterViewType(int viewType, String errorText) {
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter == null) {
            return;
        }
        if (listAdapter.mType != viewType) {
            this.mAdapter.setType(viewType);
            this.mAdapter.setErrorText(errorText);
            this.mAdapter.notifyDataSetChanged();
        } else if (viewType == 4 && errorText != null && !errorText.equals(this.mAdapter.mErrorText)) {
            this.mAdapter.setErrorText(errorText);
            this.mAdapter.notifyDataSetChanged();
        } else if (this.mAdapter.mType != 3) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public String mErrorText;
        /* access modifiers changed from: private */
        public int mType = 0;

        public ListAdapter() {
        }

        public void setErrorText(String errorText) {
            this.mErrorText = errorText;
        }

        public void setType(int type) {
            this.mType = type;
            if (type != 4) {
                this.mErrorText = null;
            }
        }

        public int getItemCount() {
            int i = this.mType;
            if (i == 1) {
                if (AddContactsActivity.this.mSearchResultList != null) {
                    return AddContactsActivity.this.mSearchResultList.size();
                }
                return 0;
            } else if (i == 2 || i == 3) {
                return 1;
            } else {
                return AddContactsActivity.this.rowCount;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return (viewType == 2 || viewType == 3 || viewType == 4) ? false : true;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String userName;
            int viewType = holder.getItemViewType();
            if (viewType == 0) {
                ImageView itemImage = (ImageView) holder.itemView.findViewById(R.id.itemImage);
                TextView itemTitle = (TextView) holder.itemView.findViewById(R.id.itemTitle);
                TextView itemSubTitle = (TextView) holder.itemView.findViewById(R.id.itemSubTitle);
                View vDivider = holder.itemView.findViewById(R.id.vDivider);
                itemTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                itemSubTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                vDivider.setBackgroundColor(Theme.getColor(Theme.key_divider));
                if (position == AddContactsActivity.this.myQRCode) {
                    itemImage.setImageResource(R.mipmap.icon_my_qr_code);
                    itemTitle.setText(LocaleController.getString("MyQRCode", R.string.MyQRCode));
                    itemSubTitle.setText(LocaleController.getString("ShareToAdd", R.string.ShareToAdd));
                } else if (position == AddContactsActivity.this.codeScan) {
                    itemImage.setImageResource(R.mipmap.icon_qr_scan);
                    itemTitle.setText(LocaleController.getString("Scan", R.string.Scan));
                    itemSubTitle.setText(LocaleController.getString("QRCodeScanToAdd", R.string.QRCodeScanToAdd));
                } else if (position == AddContactsActivity.this.phoneBook) {
                    itemImage.setImageResource(R.mipmap.icon_mail_list);
                    itemTitle.setText(LocaleController.getString("AppContacts", R.string.AppContacts));
                    itemSubTitle.setText(LocaleController.getString("AddContactsFriend", R.string.AddContactsFriend));
                } else if (position == AddContactsActivity.this.inviteMore) {
                    itemImage.setImageResource(R.mipmap.icon_invite_more);
                    itemTitle.setText(LocaleController.getString("InviteMore", R.string.InviteMore));
                    itemSubTitle.setText(LocaleController.getString("InviteApps", R.string.InviteApps));
                }
                if (position == getItemCount() - 1) {
                    vDivider.setVisibility(8);
                    holder.itemView.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (position == 0) {
                    holder.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            } else if (viewType == 1) {
                BackupImageView itemImage2 = (BackupImageView) holder.itemView.findViewById(R.id.itemImage);
                TextView itemTitle2 = (TextView) holder.itemView.findViewById(R.id.itemTitle);
                TextView itemSubTitle2 = (TextView) holder.itemView.findViewById(R.id.itemSubTitle);
                View vDivider2 = holder.itemView.findViewById(R.id.vDivider);
                itemTitle2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                itemSubTitle2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                vDivider2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                if (AddContactsActivity.this.mSearchResultList == null || position >= AddContactsActivity.this.mSearchResultList.size()) {
                    itemImage2.setImageResource(R.drawable.round_grey);
                } else {
                    TLRPC.User user = (TLRPC.User) AddContactsActivity.this.mSearchResultList.get(position);
                    if (user != null) {
                        AvatarDrawable avatarDrawable = new AvatarDrawable();
                        avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
                        avatarDrawable.setInfo(user);
                        itemImage2.setRoundRadius(AndroidUtilities.dp(7.5f));
                        itemImage2.setImage(ImageLocation.getForUser(user, false), "34_34", (Drawable) avatarDrawable, (Object) user);
                        if (user.first_name != null) {
                            userName = user.first_name;
                        } else if (user.last_name != null) {
                            userName = user.last_name;
                        } else {
                            userName = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                        }
                        itemTitle2.setText(userName);
                        itemSubTitle2.setText(LocaleController.formatUserStatus(AddContactsActivity.this.currentAccount, user));
                    }
                }
                if (position == getItemCount() - 1) {
                    vDivider2.setVisibility(8);
                }
            } else if (viewType == 2 || viewType == 4) {
                ((ImageView) holder.itemView.findViewById(AddContactsActivity.ID_EMPTY_IMAGE_VIEW)).setImageResource(0);
                ((TextView) holder.itemView.findViewById(AddContactsActivity.ID_EMPTY_TEXT_VIEW)).setText((CharSequence) null);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int i = viewType;
            View view = null;
            if (i == 0) {
                view = LayoutInflater.from(AddContactsActivity.this.mContext).inflate(R.layout.item_add_contacts_layout, (ViewGroup) null, false);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(65.0f)));
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (i == 1) {
                view = LayoutInflater.from(AddContactsActivity.this.mContext).inflate(R.layout.item_add_contacts_search_result_layout, (ViewGroup) null, false);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(65.0f)));
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (i == 2 || i == 4) {
                view = new FrameLayout(AddContactsActivity.this.mContext);
                LinearLayout ll = new LinearLayout(AddContactsActivity.this.mContext);
                ll.setOrientation(1);
                ll.setGravity(17);
                ImageView iv = new ImageView(AddContactsActivity.this.mContext);
                iv.setId(AddContactsActivity.ID_EMPTY_IMAGE_VIEW);
                ll.addView(iv, LayoutHelper.createLinear((int) BDLocation.TypeServerError, 104, 17, 0, 0, 0, 8));
                TextView tv = new TextView(AddContactsActivity.this.mContext);
                tv.setId(AddContactsActivity.ID_EMPTY_TEXT_VIEW);
                tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                tv.setTextSize(2, 14.0f);
                ll.addView(tv, LayoutHelper.createLinear(-2, -2, 17, 0, 8, 0, 0));
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                ((FrameLayout) view).addView(ll, LayoutHelper.createFrame(-2, -2, 17));
            } else if (i == 3) {
                view = new FrameLayout(AddContactsActivity.this.mContext);
                ProgressBar progressBar = new ProgressBar(AddContactsActivity.this.mContext);
                ColorStateList colorStateList = ColorStateList.valueOf(Theme.getColor(Theme.key_actionBarTabActiveText));
                if (Build.VERSION.SDK_INT >= 21) {
                    progressBar.setIndeterminateTintList(colorStateList);
                    progressBar.setIndeterminateTintMode(PorterDuff.Mode.MULTIPLY);
                } else {
                    progressBar.setIndeterminate(true);
                }
                ((FrameLayout) view).addView(progressBar, LayoutHelper.createFrame(40, 40, 17));
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            return this.mType;
        }
    }

    private boolean checkSearchByAccountName(String inputText) {
        if (inputText.matches("^\\w{5,32}$") && !inputText.matches("(^_|^\\d|_$|__)") && !inputText.contains("3549")) {
            return true;
        }
        return false;
    }
}

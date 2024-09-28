package im.bclpbkiauv.ui.hui.chats;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.ShapeUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.NewContactActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseSearchViewFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.adapters.DialogsAdapter;
import im.bclpbkiauv.ui.adapters.DialogsSearchAdapter;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ColorTextView;
import im.bclpbkiauv.ui.components.JoinGroupAlert;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.contacts.ShareCardSelectContactActivity;
import im.bclpbkiauv.ui.hui.decoration.TopDecorationWithSearch;
import im.bclpbkiauv.ui.hviews.MryEmptyView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UserProfileShareStepOneActivity extends BaseSearchViewFragment implements NotificationCenter.NotificationCenterDelegate {
    private static boolean CanMultiSelect = false;
    private static final int STEP_ONE = 0;
    private static final int STEP_TWO = 1;
    private ArrayList<Object> data = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Object> dataList = null;
    private MryEmptyView emptyView;
    protected long lid = 0;
    private DialogsAdapter mAdapter;
    private AdapterMultiSelect mAdapterMultiSelect;
    private DialogsSearchAdapter mSearchAdapter;
    private int mStep;
    private int mUserId;
    private RecyclerListView rv;
    private RecyclerListView rvMultiSelect;
    private ArrayList<Object> searchData = new ArrayList<>();
    private FrameLayout searchLayout;

    public UserProfileShareStepOneActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        if (getArguments() != null) {
            this.mUserId = getArguments().getInt("user_id");
            this.mStep = getArguments().getInt("step");
        }
        getNotificationCenter().addObserver(this, NotificationCenter.dialogsNeedReload);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.appDidLogout);
        getNotificationCenter().addObserver(this, NotificationCenter.openedChatChanged);
        if (!DialogsActivity.dialogsLoaded[this.currentAccount]) {
            getMessagesController().loadGlobalNotificationsSettings();
            getMessagesController().loadDialogs(0, 0, 100, true);
            getMessagesController().loadHintDialogs();
            getContactsController().checkInviteText();
            getMediaDataController().loadRecents(2, false, true, false);
            getMediaDataController().checkFeaturedStickers();
            DialogsActivity.dialogsLoaded[this.currentAccount] = true;
        }
        getMessagesController().loadPinnedDialogs(0, 0, (ArrayList<Long>) null);
        this.mblnMove = false;
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogsNeedReload);
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.appDidLogout);
        getNotificationCenter().removeObserver(this, NotificationCenter.openedChatChanged);
    }

    public View createView(Context context) {
        FrameLayout container = new FrameLayout(context);
        this.fragmentView = container;
        container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initEmptyView(context);
        initListRv(context);
        super.createView(context);
        return this.fragmentView;
    }

    private void initActionBar() {
        if (this.mStep == 0) {
            this.actionBar.setTitle(LocaleController.getString(R.string.ShareFriendBusinessCard));
            this.actionBar.setBackTitle(LocaleController.getString(R.string.Cancel));
        } else {
            this.actionBar.setTitle(LocaleController.getString(R.string.SelectNewContactUser));
            this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    UserProfileShareStepOneActivity.this.finishFragment(true);
                }
            }
        });
        this.actionBar.getBackTitleTextView().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                UserProfileShareStepOneActivity.this.lambda$initActionBar$0$UserProfileShareStepOneActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initActionBar$0$UserProfileShareStepOneActivity(View v) {
        finishFragmentFromUp(true);
    }

    private void getRecentlyContacterList() {
        filterList(DialogsActivity.getDialogsArray(this.currentAccount, 3, 0, false));
    }

    /* access modifiers changed from: protected */
    public void filterList(ArrayList<TLRPC.Dialog> array) {
        if (array != null) {
            Iterator<TLRPC.Dialog> it = array.iterator();
            while (it.hasNext()) {
                TLRPC.User user = null;
                TLRPC.Chat chat = null;
                long dialogId = it.next().id;
                if (dialogId != 0) {
                    int lower_id = (int) dialogId;
                    int i = (int) (dialogId >> 32);
                    if (lower_id != 0) {
                        if (lower_id < 0) {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                        } else {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
                        }
                    }
                }
                if (user != null) {
                    if (!(user.id == UserConfig.getInstance(this.currentAccount).getCurrentUser().id || user.id == 777000)) {
                        this.data.add(user);
                    }
                } else if (chat != null && ChatObject.canSendMessages(chat)) {
                    if (chat.default_banned_rights.embed_links && ChatObject.hasAdminRights(chat)) {
                        this.data.add(chat);
                    } else if (!chat.default_banned_rights.embed_links) {
                        this.data.add(chat);
                    }
                }
            }
        }
        this.dataList = this.data;
    }

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        this.searchLayout = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ((FrameLayout) this.fragmentView).addView(this.searchLayout, LayoutHelper.createFrame(-1, 55.0f));
        this.searchView = new MrySearchView(getParentActivity());
        this.searchView.setHintText(LocaleController.getString("Search", R.string.Search));
        this.searchView.setCancelTextColor(Color.parseColor("#999999"));
        this.searchLayout.addView(this.searchView, LayoutHelper.createFrame(-1.0f, 35.0f, 17, 10.0f, 10.0f, 10.0f, 10.0f));
        return this.searchView;
    }

    public void onTextChange(String value) {
        super.onTextChange(value);
        if (!TextUtils.isEmpty(value)) {
            this.searchData.clear();
            Iterator<Object> it = this.data.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) obj;
                    if (user.first_name.contains(value)) {
                        this.searchData.add(user);
                    }
                } else if (obj instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) obj;
                    if (chat.title.contains(value)) {
                        this.searchData.add(chat);
                    }
                }
            }
            this.dataList = this.searchData;
            if (this.rv.getAdapter() != null) {
                this.rv.getAdapter().notifyDataSetChanged();
                return;
            }
            return;
        }
        this.dataList = this.data;
        if (this.rv.getAdapter() != null) {
            this.rv.getAdapter().notifyDataSetChanged();
        }
    }

    public void onSearchCollapse() {
        super.onSearchCollapse();
        this.dataList = this.data;
        if (this.rv.getAdapter() != null) {
            this.rv.getAdapter().notifyDataSetChanged();
        }
    }

    private void initEmptyView(Context context) {
        MryEmptyView mryEmptyView = new MryEmptyView(context);
        this.emptyView = mryEmptyView;
        mryEmptyView.attach((ViewGroup) this.fragmentView);
    }

    private void initListRv(Context context) {
        getRecentlyContacterList();
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.rv = recyclerListView;
        recyclerListView.setTag("rv_list");
        this.rv.setOverScrollMode(2);
        this.rv.setLayoutManager(new LinearLayoutManager(context));
        this.rv.addItemDecoration(new TopDecorationWithSearch());
        this.rv.setVerticalScrollBarEnabled(false);
        this.rv.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.rv.setInstantClick(true);
        ((FrameLayout) this.fragmentView).addView(this.rv, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0));
        this.rv.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                UserProfileShareStepOneActivity.this.lambda$initListRv$2$UserProfileShareStepOneActivity(view, i);
            }
        });
        this.rv.setAdapter(new Adapter());
    }

    public /* synthetic */ void lambda$initListRv$2$UserProfileShareStepOneActivity(View view, int position) {
        long dialog_id;
        int i = position;
        RecyclerListView recyclerListView = this.rv;
        if (recyclerListView != null && recyclerListView.getAdapter() != null && getParentActivity() != null) {
            RecyclerView.Adapter adapter = this.rv.getAdapter();
            DialogsAdapter dialogsAdapter = this.mAdapter;
            if (adapter == dialogsAdapter) {
                TLObject object = dialogsAdapter.getItem(i);
                if (object instanceof TLRPC.User) {
                    dialog_id = (long) ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Dialog) {
                    TLRPC.Dialog dialog = (TLRPC.Dialog) object;
                    if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                        dialog_id = dialog.id;
                    } else if (!this.actionBar.isActionModeShowed()) {
                        Bundle args = new Bundle();
                        args.putInt("folderId", ((TLRPC.TL_dialogFolder) dialog).folder.id);
                        presentFragment(new MryDialogsActivity(args));
                        return;
                    } else {
                        return;
                    }
                } else if (object instanceof TLRPC.TL_recentMeUrlChat) {
                    dialog_id = (long) (-((TLRPC.TL_recentMeUrlChat) object).chat_id);
                } else if (object instanceof TLRPC.TL_recentMeUrlUser) {
                    dialog_id = (long) ((TLRPC.TL_recentMeUrlUser) object).user_id;
                } else if (object instanceof TLRPC.TL_recentMeUrlChatInvite) {
                    TLRPC.TL_recentMeUrlChatInvite chatInvite = (TLRPC.TL_recentMeUrlChatInvite) object;
                    TLRPC.ChatInvite invite = chatInvite.chat_invite;
                    if ((invite.chat == null && (!invite.channel || invite.megagroup)) || (invite.chat != null && (!ChatObject.isChannel(invite.chat) || invite.chat.megagroup))) {
                        String hash = chatInvite.url;
                        int index = hash.indexOf(47);
                        if (index > 0) {
                            hash = hash.substring(index + 1);
                        }
                        showDialog(new JoinGroupAlert(getParentActivity(), invite, hash, this));
                        return;
                    } else if (invite.chat != null) {
                        dialog_id = (long) (-invite.chat.id);
                    } else {
                        return;
                    }
                } else if (object instanceof TLRPC.TL_recentMeUrlStickerSet) {
                    TLRPC.StickerSet stickerSet = ((TLRPC.TL_recentMeUrlStickerSet) object).set.set;
                    TLRPC.TL_inputStickerSetID set = new TLRPC.TL_inputStickerSetID();
                    set.id = stickerSet.id;
                    set.access_hash = stickerSet.access_hash;
                    StickersAlert stickersAlert = r0;
                    StickersAlert stickersAlert2 = new StickersAlert(getParentActivity(), this, set, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
                    showDialog(stickersAlert);
                    return;
                } else if (!(object instanceof TLRPC.TL_recentMeUrlUnknown)) {
                    return;
                } else {
                    return;
                }
            } else {
                DialogsSearchAdapter dialogsSearchAdapter = this.mSearchAdapter;
                if (adapter == dialogsSearchAdapter) {
                    Object obj = dialogsSearchAdapter.getItem(i);
                    boolean isGlobalSearch = this.mSearchAdapter.isGlobalSearch(i);
                    if (obj instanceof TLRPC.User) {
                        dialog_id = (long) ((TLRPC.User) obj).id;
                    } else if (obj instanceof TLRPC.Chat) {
                        dialog_id = (long) (-((TLRPC.Chat) obj).id);
                    } else if (obj instanceof TLRPC.EncryptedChat) {
                        dialog_id = ((long) ((TLRPC.EncryptedChat) obj).id) << 32;
                    } else if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        dialog_id = messageObject.getDialogId();
                        int message_id = messageObject.getId();
                        DialogsSearchAdapter dialogsSearchAdapter2 = this.mSearchAdapter;
                        dialogsSearchAdapter2.addHashtagsFromMessage(dialogsSearchAdapter2.getLastSearchString());
                    } else {
                        if (obj instanceof String) {
                            String str = (String) obj;
                            if (this.mSearchAdapter.isHashtagSearch()) {
                                this.actionBar.openSearchField(str, false);
                            } else if (!str.equals("section")) {
                                NewContactActivity activity = new NewContactActivity();
                                activity.setInitialPhoneNumber(str);
                                presentFragment(activity);
                            }
                        }
                        dialog_id = 0;
                    }
                } else {
                    dialog_id = 0;
                }
            }
            if (i == 0) {
                ShareCardSelectContactActivity activity2 = new ShareCardSelectContactActivity((Bundle) null);
                activity2.setDelegate(new ShareCardSelectContactActivity.ContactsActivityDelegate() {
                    public final void didSelectContact(TLRPC.User user) {
                        UserProfileShareStepOneActivity.this.lambda$null$1$UserProfileShareStepOneActivity(user);
                    }
                });
                presentFragment(activity2);
            }
            if (i > 1) {
                Object obj2 = this.dataList.get(i - 2);
                if (obj2 instanceof TLRPC.User) {
                    this.lid = (long) ((TLRPC.User) obj2).id;
                } else {
                    this.lid = (long) (((TLRPC.Chat) obj2).id * -1);
                }
                showDialog(obj2);
            }
            if (dialog_id != 0) {
            }
        }
    }

    public /* synthetic */ void lambda$null$1$UserProfileShareStepOneActivity(TLRPC.User user) {
        this.lid = (long) user.id;
        showDialog(user);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
    }

    private void showDialog(Object obj) {
        XDialog.Builder builder = new XDialog.Builder(getParentActivity(), 15);
        View v = LayoutInflater.from(getParentActivity()).inflate(R.layout.dialog_share_contact, (ViewGroup) null);
        EditText etContent = (EditText) v.findViewById(R.id.et_content);
        BackupImageView ivHead = (BackupImageView) v.findViewById(R.id.iv_head_img);
        ivHead.setRoundRadius(AndroidUtilities.dp(7.5f));
        TextView tvName = (TextView) v.findViewById(R.id.tv_name);
        TextView tvCard = (TextView) v.findViewById(R.id.tv_card);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        if (obj != null) {
            if (obj instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) obj;
                avatarDrawable.setInfo(user);
                ivHead.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
                tvName.setText(user.first_name);
                etContent.setHint(LocaleController.getString(R.string.share_contact_content));
            } else {
                TLRPC.Chat chat = (TLRPC.Chat) obj;
                avatarDrawable.setInfo(chat);
                ivHead.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) avatarDrawable, (Object) chat);
                tvName.setText(chat.title);
                etContent.setHint(LocaleController.getString(R.string.share_contact_content_group));
            }
        }
        tvCard.setText(String.format("[%s]%s", new Object[]{LocaleController.getString(R.string.share_contact_person_card), MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.mUserId)).first_name}));
        etContent.setBackground(ShapeUtils.create(Color.parseColor("#F6F7F9"), (float) AndroidUtilities.dp(8.0f)));
        builder.setView(v);
        builder.setPositiveButton(LocaleController.getString("Send", R.string.Send), new DialogInterface.OnClickListener(etContent) {
            private final /* synthetic */ EditText f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                UserProfileShareStepOneActivity.this.lambda$showDialog$3$UserProfileShareStepOneActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    public /* synthetic */ void lambda$showDialog$3$UserProfileShareStepOneActivity(EditText etContent, DialogInterface dialogInterface, int i) {
        int currentConnectionState = ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState();
        if (currentConnectionState == 2 || currentConnectionState == 1) {
            ToastUtils.show((CharSequence) LocaleController.getString(R.string.visual_call_no_network));
        } else {
            startSendCard(etContent.getText().toString());
        }
    }

    private void startSendCard(String strContent) {
        XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
        progressDialog.setLoadingText(LocaleController.getString(R.string.ApplySending));
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.mUserId)), this.lid, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        if (!TextUtils.isEmpty(strContent)) {
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(strContent, this.lid, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
        progressDialog.show();
        progressDialog.setLoadingImage(getParentActivity().getResources().getDrawable(R.mipmap.ic_apply_send_done), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(20.0f));
        progressDialog.setLoadingText(LocaleController.getString(R.string.ApplySent));
        progressDialog.setCanCacnel(false);
        this.fragmentView.postDelayed(new Runnable(progressDialog) {
            private final /* synthetic */ XAlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                UserProfileShareStepOneActivity.this.lambda$startSendCard$4$UserProfileShareStepOneActivity(this.f$1);
            }
        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    public /* synthetic */ void lambda$startSendCard$4$UserProfileShareStepOneActivity(XAlertDialog progressDialog) {
        progressDialog.dismiss();
        finishFragmentFromUp(true);
    }

    public void onPause() {
        super.onPause();
        if (this.searchView != null && this.searchView.isSearchFieldVisible()) {
            this.searchView.closeSearchField();
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<Long> selectedDialogs;

        private Adapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1) {
                MryTextView tv = new MryTextView(UserProfileShareStepOneActivity.this.getParentActivity());
                tv.setTextColor(Color.parseColor("#999999"));
                tv.setTextSize(13.0f);
                tv.setText(LocaleController.getString(R.string.share_contact_recently_chat));
                view = new FrameLayout(UserProfileShareStepOneActivity.this.getParentActivity());
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(35.0f)));
                ((FrameLayout) view).addView(tv, LayoutHelper.createFrame(-2.0f, -2.0f, 16, 0.0f, 0.0f, 0.0f, 0.0f));
            } else if (viewType == 0) {
                view = new FrameLayout(UserProfileShareStepOneActivity.this.getParentActivity());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(55.0f)));
                view.setBackground(Theme.createRoundRectDrawable(7.5f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                MryTextView tv2 = new MryTextView(UserProfileShareStepOneActivity.this.getParentActivity());
                tv2.setTextColor(Theme.key_windowBackgroundWhiteBlackText);
                tv2.setTextSize(14.0f);
                tv2.setText(LocaleController.getString(R.string.SelectNewContactUserToSend));
                ((FrameLayout) view).addView(tv2, LayoutHelper.createFrame(-2.0f, -2.0f, 16, 12.0f, 0.0f, 0.0f, 0.0f));
                ImageView iv = new ImageView(UserProfileShareStepOneActivity.this.getParentActivity());
                iv.setImageResource(R.mipmap.icon_arrow_right);
                ((FrameLayout) view).addView(iv, LayoutHelper.createFrame(7.0f, 12.0f, 21, 10.0f, 0.0f, 12.0f, 0.0f));
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recently_contacter, parent, false);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(65.0f)));
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            int itemViewType = holder.getItemViewType();
            if (i > 1) {
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                ColorTextView tvCount = (ColorTextView) viewHolder.itemView.findViewById(R.id.tv_count);
                ColorTextView tvName = (ColorTextView) viewHolder.itemView.findViewById(R.id.tv_name);
                ColorTextView tvPersonName = (ColorTextView) viewHolder.itemView.findViewById(R.id.tv_person_name);
                ColorTextView tvState = (ColorTextView) viewHolder.itemView.findViewById(R.id.tv_state);
                BackupImageView iv_Header = (BackupImageView) viewHolder.itemView.findViewById(R.id.iv_head_img);
                iv_Header.setRoundRadius(AndroidUtilities.dp(7.5f));
                Object obj = UserProfileShareStepOneActivity.this.dataList.get(i - 2);
                if (obj == null) {
                    return;
                }
                if (obj instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) obj;
                    avatarDrawable.setInfo(user);
                    iv_Header.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
                    tvPersonName.setText(user.first_name);
                    boolean[] booleans = {false};
                    tvState.setText(LocaleController.formatUserStatusNew(UserProfileShareStepOneActivity.this.currentAccount, user, booleans));
                    if (booleans[0]) {
                        tvState.setTextColor(Color.parseColor("#42B71E"));
                    } else {
                        tvState.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
                    }
                    tvCount.setText("");
                    tvName.setText("");
                } else if (obj instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) obj;
                    avatarDrawable.setInfo(chat);
                    iv_Header.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) avatarDrawable, (Object) chat);
                    tvName.setText(chat.title);
                    tvPersonName.setText("");
                    tvState.setText("");
                    tvCount.setText(String.format("(%s)", new Object[]{LocaleController.formatString("share_contact_person", R.string.share_contact_person, Integer.valueOf(chat.participants_count))}));
                }
            }
        }

        public int getItemCount() {
            int dialogsCount = UserProfileShareStepOneActivity.this.dataList.size();
            if (dialogsCount != 0 || !MessagesController.getInstance(UserProfileShareStepOneActivity.this.currentAccount).isLoadingDialogs(0)) {
                return dialogsCount + 2;
            }
            return 0;
        }

        public int getItemViewType(int position) {
            return position;
        }
    }

    private class AdapterMultiSelect extends RecyclerListView.SelectionAdapter {
        private AdapterMultiSelect() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        }

        public int getItemCount() {
            return 0;
        }
    }
}

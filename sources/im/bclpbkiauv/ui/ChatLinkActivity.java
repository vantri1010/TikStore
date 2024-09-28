package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatLinkActivity;
import im.bclpbkiauv.ui.GroupCreateFinalActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.ManageChatTextCell;
import im.bclpbkiauv.ui.cells.ManageChatUserCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;

public class ChatLinkActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public int chatEndRow;
    /* access modifiers changed from: private */
    public int chatStartRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
    /* access modifiers changed from: private */
    public int createChatRow;
    private TLRPC.Chat currentChat;
    private int currentChatId;
    /* access modifiers changed from: private */
    public int detailRow;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int helpRow;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public boolean isChannel;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loadingChats;
    /* access modifiers changed from: private */
    public int removeChatRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private boolean waitingForChatCreate;
    private TLRPC.Chat waitingForFullChat;
    private AlertDialog waitingForFullChatProgressAlert;

    public ChatLinkActivity(int chatId) {
        this.currentChatId = chatId;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(chatId));
        this.currentChat = chat;
        this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
    }

    private void updateRows() {
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId));
        this.currentChat = chat;
        if (chat != null) {
            int i = 0;
            this.rowCount = 0;
            this.helpRow = -1;
            this.createChatRow = -1;
            this.chatStartRow = -1;
            this.chatEndRow = -1;
            this.removeChatRow = -1;
            this.detailRow = -1;
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.helpRow = 0;
            if (this.isChannel) {
                if (this.info.linked_chat_id == 0) {
                    int i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.createChatRow = i3;
                }
                int i4 = this.rowCount;
                this.chatStartRow = i4;
                int size = i4 + this.chats.size();
                this.rowCount = size;
                this.chatEndRow = size;
                if (this.info.linked_chat_id != 0) {
                    int i5 = this.rowCount;
                    this.rowCount = i5 + 1;
                    this.createChatRow = i5;
                }
                int i6 = this.rowCount;
                this.rowCount = i6 + 1;
                this.detailRow = i6;
            } else {
                this.chatStartRow = i2;
                int size2 = i2 + this.chats.size();
                this.rowCount = size2;
                this.chatEndRow = size2;
                int i7 = size2 + 1;
                this.rowCount = i7;
                this.createChatRow = size2;
                this.rowCount = i7 + 1;
                this.detailRow = i7;
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                if (this.chats.size() <= 10) {
                    i = 8;
                }
                actionBarMenuItem.setVisibility(i);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        loadChats();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.currentChatId) {
                this.info = chatFull;
                loadChats();
                updateRows();
                return;
            }
            TLRPC.Chat chat = this.waitingForFullChat;
            if (chat != null && chat.id == chatFull.id) {
                try {
                    this.waitingForFullChatProgressAlert.dismiss();
                } catch (Throwable th) {
                }
                this.waitingForFullChatProgressAlert = null;
                showLinkAlert(this.waitingForFullChat, false);
                this.waitingForFullChat = null;
            }
        }
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Discussion", R.string.Discussion));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatLinkActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = ChatLinkActivity.this.searching = true;
                ChatLinkActivity.this.emptyView.setShowAtCenter(true);
            }

            public void onSearchCollapse() {
                ChatLinkActivity.this.searchAdapter.searchDialogs((String) null);
                boolean unused = ChatLinkActivity.this.searching = false;
                boolean unused2 = ChatLinkActivity.this.searchWas = false;
                ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.listViewAdapter);
                ChatLinkActivity.this.listViewAdapter.notifyDataSetChanged();
                ChatLinkActivity.this.listView.setFastScrollVisible(true);
                ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(false);
                ChatLinkActivity.this.emptyView.setShowAtCenter(false);
                ChatLinkActivity.this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                ChatLinkActivity.this.fragmentView.setTag(Theme.key_windowBackgroundGray);
                ChatLinkActivity.this.emptyView.showProgress();
            }

            public void onTextChanged(EditText editText) {
                if (ChatLinkActivity.this.searchAdapter != null) {
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
                        boolean unused = ChatLinkActivity.this.searchWas = true;
                        if (!(ChatLinkActivity.this.listView == null || ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter)) {
                            ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.searchAdapter);
                            ChatLinkActivity.this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            ChatLinkActivity.this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
                            ChatLinkActivity.this.searchAdapter.notifyDataSetChanged();
                            ChatLinkActivity.this.listView.setFastScrollVisible(false);
                            ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(true);
                            ChatLinkActivity.this.emptyView.showProgress();
                        }
                    }
                    ChatLinkActivity.this.searchAdapter.searchDialogs(text);
                }
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChatLinkActivity.this.lambda$createView$5$ChatLinkActivity(view, i);
            }
        });
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$5$ChatLinkActivity(View view, int position) {
        TLRPC.Chat chat;
        String title;
        String message;
        if (getParentActivity() != null) {
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            SearchAdapter searchAdapter2 = this.searchAdapter;
            if (adapter == searchAdapter2) {
                chat = searchAdapter2.getItem(position);
            } else {
                int i = this.chatStartRow;
                if (position < i || position >= this.chatEndRow) {
                    chat = null;
                } else {
                    chat = this.chats.get(position - i);
                }
            }
            if (chat != null) {
                if (!this.isChannel || this.info.linked_chat_id != 0) {
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.id);
                    presentFragment(new ChatActivity(args));
                    return;
                }
                showLinkAlert(chat, true);
            } else if (position != this.createChatRow) {
            } else {
                if (this.isChannel && this.info.linked_chat_id == 0) {
                    Bundle args2 = new Bundle();
                    ArrayList<Integer> result = new ArrayList<>();
                    result.add(Integer.valueOf(getUserConfig().getClientUserId()));
                    args2.putIntegerArrayList("result", result);
                    args2.putInt("chatType", 4);
                    GroupCreateFinalActivity activity = new GroupCreateFinalActivity(args2);
                    activity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() {
                        public void didStartChatCreation() {
                        }

                        public void didFinishChatCreation(GroupCreateFinalActivity fragment, int chatId) {
                            ChatLinkActivity chatLinkActivity = ChatLinkActivity.this;
                            chatLinkActivity.linkChat(chatLinkActivity.getMessagesController().getChat(Integer.valueOf(chatId)), fragment);
                        }

                        public void didFailChatCreation() {
                        }
                    });
                    presentFragment(activity);
                } else if (!this.chats.isEmpty()) {
                    TLRPC.Chat c = this.chats.get(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (this.isChannel) {
                        title = LocaleController.getString("DiscussionUnlinkGroup", R.string.DiscussionUnlinkGroup);
                        message = LocaleController.formatString("DiscussionUnlinkChannelAlert", R.string.DiscussionUnlinkChannelAlert, c.title);
                    } else {
                        title = LocaleController.getString("DiscussionUnlink", R.string.DiscussionUnlinkChannel);
                        message = LocaleController.formatString("DiscussionUnlinkGroupAlert", R.string.DiscussionUnlinkGroupAlert, c.title);
                    }
                    builder.setTitle(title);
                    builder.setMessage(AndroidUtilities.replaceTags(message));
                    builder.setPositiveButton(LocaleController.getString("DiscussionUnlink", R.string.DiscussionUnlink), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatLinkActivity.this.lambda$null$4$ChatLinkActivity(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    AlertDialog dialog = builder.create();
                    showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$4$ChatLinkActivity(DialogInterface dialogInterface, int i) {
        if (!this.isChannel || this.info.linked_chat_id != 0) {
            AlertDialog[] progressDialog = {new AlertDialog(getParentActivity(), 3)};
            TLRPC.TL_channels_setDiscussionGroup req = new TLRPC.TL_channels_setDiscussionGroup();
            if (this.isChannel) {
                req.broadcast = MessagesController.getInputChannel(this.currentChat);
                req.group = new TLRPC.TL_inputChannelEmpty();
            } else {
                req.broadcast = new TLRPC.TL_inputChannelEmpty();
                req.group = MessagesController.getInputChannel(this.currentChat);
            }
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
                private final /* synthetic */ AlertDialog[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatLinkActivity.this.lambda$null$1$ChatLinkActivity(this.f$1, tLObject, tL_error);
                }
            })) {
                private final /* synthetic */ AlertDialog[] f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatLinkActivity.this.lambda$null$3$ChatLinkActivity(this.f$1, this.f$2);
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$null$1$ChatLinkActivity(AlertDialog[] progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog) {
            private final /* synthetic */ AlertDialog[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatLinkActivity.this.lambda$null$0$ChatLinkActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$ChatLinkActivity(AlertDialog[] progressDialog) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        this.info.linked_chat_id = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, false, null);
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
        if (!this.isChannel) {
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$3$ChatLinkActivity(AlertDialog[] progressDialog, int requestId) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener(requestId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    ChatLinkActivity.this.lambda$null$2$ChatLinkActivity(this.f$1, dialogInterface);
                }
            });
            showDialog(progressDialog[0]);
        }
    }

    public /* synthetic */ void lambda$null$2$ChatLinkActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    private void showLinkAlert(TLRPC.Chat chat, boolean query) {
        String message;
        TLRPC.Chat chat2 = chat;
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(chat2.id);
        int i = 3;
        if (chatFull != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            TextView messageTextView = new TextView(getParentActivity());
            messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            messageTextView.setTextSize(1, 16.0f);
            messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (TextUtils.isEmpty(chat2.username)) {
                message = LocaleController.formatString("DiscussionLinkGroupPublicPrivateAlert", R.string.DiscussionLinkGroupPublicPrivateAlert, chat2.title, this.currentChat.title);
            } else if (TextUtils.isEmpty(this.currentChat.username)) {
                message = LocaleController.formatString("DiscussionLinkGroupPrivateAlert", R.string.DiscussionLinkGroupPrivateAlert, chat2.title, this.currentChat.title);
            } else {
                message = LocaleController.formatString("DiscussionLinkGroupPublicAlert", R.string.DiscussionLinkGroupPublicAlert, chat2.title, this.currentChat.title);
            }
            if (chatFull.hidden_prehistory) {
                message = message + "\n\n" + LocaleController.getString("DiscussionLinkGroupAlertHistory", R.string.DiscussionLinkGroupAlertHistory);
            }
            messageTextView.setText(AndroidUtilities.replaceTags(message));
            FrameLayout frameLayout2 = new FrameLayout(getParentActivity());
            builder.setView(frameLayout2);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView imageView = new BackupImageView(getParentActivity());
            imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            frameLayout2.addView(imageView, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
            TextView textView = new TextView(getParentActivity());
            textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(chat2.title);
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 21;
            float f = (float) (LocaleController.isRTL ? 21 : 76);
            if (LocaleController.isRTL) {
                i3 = 76;
            }
            frameLayout2.addView(textView, LayoutHelper.createFrame(-1.0f, -2.0f, i2, f, 11.0f, (float) i3, 0.0f));
            if (LocaleController.isRTL) {
                i = 5;
            }
            frameLayout2.addView(messageTextView, LayoutHelper.createFrame(-2.0f, -2.0f, i | 48, 24.0f, 57.0f, 24.0f, 9.0f));
            avatarDrawable.setInfo(chat2);
            imageView.setImage(ImageLocation.getForChat(chat2, false), "50_50", (Drawable) avatarDrawable, (Object) chat2);
            builder.setPositiveButton(LocaleController.getString("DiscussionLinkGroup", R.string.DiscussionLinkGroup), new DialogInterface.OnClickListener(chatFull, chat2) {
                private final /* synthetic */ TLRPC.ChatFull f$1;
                private final /* synthetic */ TLRPC.Chat f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatLinkActivity.this.lambda$showLinkAlert$8$ChatLinkActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else if (query) {
            getMessagesController().loadFullChat(chat2.id, 0, true);
            this.waitingForFullChat = chat2;
            this.waitingForFullChatProgressAlert = new AlertDialog(getParentActivity(), 3);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChatLinkActivity.this.lambda$showLinkAlert$7$ChatLinkActivity();
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$showLinkAlert$7$ChatLinkActivity() {
        AlertDialog alertDialog = this.waitingForFullChatProgressAlert;
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    ChatLinkActivity.this.lambda$null$6$ChatLinkActivity(dialogInterface);
                }
            });
            showDialog(this.waitingForFullChatProgressAlert);
        }
    }

    public /* synthetic */ void lambda$null$6$ChatLinkActivity(DialogInterface dialog) {
        this.waitingForFullChat = null;
    }

    public /* synthetic */ void lambda$showLinkAlert$8$ChatLinkActivity(TLRPC.ChatFull chatFull, TLRPC.Chat chat, DialogInterface dialogInterface, int i) {
        if (chatFull.hidden_prehistory) {
            MessagesController.getInstance(this.currentAccount).toogleChannelInvitesHistory(chat.id, false);
        }
        linkChat(chat, (BaseFragment) null);
    }

    /* access modifiers changed from: private */
    public void linkChat(TLRPC.Chat chat, BaseFragment createFragment) {
        if (chat != null) {
            if (!ChatObject.isChannel(chat)) {
                MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), chat.id, this, new MessagesStorage.IntCallback(createFragment) {
                    private final /* synthetic */ BaseFragment f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(int i) {
                        ChatLinkActivity.this.lambda$linkChat$9$ChatLinkActivity(this.f$1, i);
                    }
                });
                return;
            }
            AlertDialog[] progressDialog = new AlertDialog[1];
            progressDialog[0] = createFragment != null ? null : new AlertDialog(getParentActivity(), 3);
            TLRPC.TL_channels_setDiscussionGroup req = new TLRPC.TL_channels_setDiscussionGroup();
            req.broadcast = MessagesController.getInputChannel(this.currentChat);
            req.group = MessagesController.getInputChannel(chat);
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog, chat, createFragment) {
                private final /* synthetic */ AlertDialog[] f$1;
                private final /* synthetic */ TLRPC.Chat f$2;
                private final /* synthetic */ BaseFragment f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatLinkActivity.this.lambda$linkChat$11$ChatLinkActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            })) {
                private final /* synthetic */ AlertDialog[] f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatLinkActivity.this.lambda$linkChat$13$ChatLinkActivity(this.f$1, this.f$2);
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$linkChat$9$ChatLinkActivity(BaseFragment createFragment, int param) {
        MessagesController.getInstance(this.currentAccount).toogleChannelInvitesHistory(param, false);
        linkChat(getMessagesController().getChat(Integer.valueOf(param)), createFragment);
    }

    public /* synthetic */ void lambda$linkChat$11$ChatLinkActivity(AlertDialog[] progressDialog, TLRPC.Chat chat, BaseFragment createFragment, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, chat, createFragment) {
            private final /* synthetic */ AlertDialog[] f$1;
            private final /* synthetic */ TLRPC.Chat f$2;
            private final /* synthetic */ BaseFragment f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatLinkActivity.this.lambda$null$10$ChatLinkActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$10$ChatLinkActivity(AlertDialog[] progressDialog, TLRPC.Chat chat, BaseFragment createFragment) {
        if (progressDialog[0] != null) {
            try {
                progressDialog[0].dismiss();
            } catch (Throwable th) {
            }
            progressDialog[0] = null;
        }
        this.info.linked_chat_id = chat.id;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, false, null);
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
        if (createFragment != null) {
            removeSelfFromStack();
            createFragment.finishFragment();
            return;
        }
        finishFragment();
    }

    public /* synthetic */ void lambda$linkChat$13$ChatLinkActivity(AlertDialog[] progressDialog, int requestId) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener(requestId) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    ChatLinkActivity.this.lambda$null$12$ChatLinkActivity(this.f$1, dialogInterface);
                }
            });
            showDialog(progressDialog[0]);
        }
    }

    public /* synthetic */ void lambda$null$12$ChatLinkActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
    }

    private void loadChats() {
        if (this.info.linked_chat_id != 0) {
            this.chats.clear();
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(this.info.linked_chat_id));
            if (chat != null) {
                this.chats.add(chat);
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
        }
        if (!this.loadingChats && this.isChannel && this.info.linked_chat_id == 0) {
            this.loadingChats = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_channels_getGroupsForDiscussion(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatLinkActivity.this.lambda$loadChats$15$ChatLinkActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadChats$15$ChatLinkActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatLinkActivity.this.lambda$null$14$ChatLinkActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$14$ChatLinkActivity(TLObject response) {
        if (response instanceof TLRPC.messages_Chats) {
            TLRPC.messages_Chats res = (TLRPC.messages_Chats) response;
            getMessagesController().putChats(res.chats, false);
            this.chats = res.chats;
        }
        this.loadingChats = false;
        updateRows();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageResource(Theme.getCurrentTheme().isDark() ? R.drawable.tip6_dark : R.drawable.tip6);
            addView(this.imageView, LayoutHelper.createFrame(-2.0f, -2.0f, 49, 0.0f, 20.0f, 8.0f, 0.0f));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_chats_message));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            if (!ChatLinkActivity.this.isChannel) {
                TLRPC.Chat chat = ChatLinkActivity.this.getMessagesController().getChat(Integer.valueOf(ChatLinkActivity.this.info.linked_chat_id));
                if (chat != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionGroupHelp", R.string.DiscussionGroupHelp, chat.title)));
                }
            } else if (ChatLinkActivity.this.info == null || ChatLinkActivity.this.info.linked_chat_id == 0) {
                this.messageTextView.setText(LocaleController.getString("DiscussionChannelHelp", R.string.DiscussionChannelHelp));
            } else {
                TLRPC.Chat chat2 = ChatLinkActivity.this.getMessagesController().getChat(Integer.valueOf(ChatLinkActivity.this.info.linked_chat_id));
                if (chat2 != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionChannelGroupSetHelp", R.string.DiscussionChannelGroupSetHelp, chat2.title)));
                }
            }
            addView(this.messageTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 52.0f, 124.0f, 52.0f, 27.0f));
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<TLRPC.Chat> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        private int searchStartRow;
        private int totalCount;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$ChatLinkActivity$SearchAdapter$96CPMUg56NwHjUoKHMGto3Jlk3I r1 = new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$searchDialogs$0$ChatLinkActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchDialogs$0$ChatLinkActivity$SearchAdapter(String query) {
            AndroidUtilities.runOnUIThread(new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$processSearch$2$ChatLinkActivity$SearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$2$ChatLinkActivity$SearchAdapter(String query) {
            this.searchRunnable = null;
            Utilities.searchQueue.postRunnable(new Runnable(query, new ArrayList<>(ChatLinkActivity.this.chats)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$null$1$ChatLinkActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b2, code lost:
            if (r12.contains(" " + r3) != false) goto L_0x00c6;
         */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x0105 A[LOOP:1: B:23:0x0074->B:46:0x0105, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00ca A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$1$ChatLinkActivity$SearchAdapter(java.lang.String r19, java.util.ArrayList r20) {
            /*
                r18 = this;
                r0 = r18
                java.lang.String r1 = r19.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r0.updateSearchResults(r2, r3)
                return
            L_0x001e:
                im.bclpbkiauv.messenger.LocaleController r2 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r2 = r2.getTranslitString(r1)
                boolean r3 = r1.equals(r2)
                if (r3 != 0) goto L_0x0032
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r3 = 0
                r4 = 1
                if (r2 == 0) goto L_0x0039
                r5 = 1
                goto L_0x003a
            L_0x0039:
                r5 = 0
            L_0x003a:
                int r5 = r5 + r4
                java.lang.String[] r5 = new java.lang.String[r5]
                r5[r3] = r1
                if (r2 == 0) goto L_0x0043
                r5[r4] = r2
            L_0x0043:
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                r8 = 0
            L_0x004e:
                int r9 = r20.size()
                if (r8 >= r9) goto L_0x0117
                r9 = r20
                java.lang.Object r10 = r9.get(r8)
                im.bclpbkiauv.tgnet.TLRPC$Chat r10 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r10
                java.lang.String r11 = r10.title
                java.lang.String r11 = r11.toLowerCase()
                im.bclpbkiauv.messenger.LocaleController r12 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r12 = r12.getTranslitString(r11)
                boolean r13 = r11.equals(r12)
                if (r13 == 0) goto L_0x0071
                r12 = 0
            L_0x0071:
                r13 = 0
                int r14 = r5.length
                r15 = 0
            L_0x0074:
                if (r15 >= r14) goto L_0x010d
                r3 = r5[r15]
                boolean r16 = r11.startsWith(r3)
                if (r16 != 0) goto L_0x00c4
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r17 = r1
                java.lang.String r1 = " "
                r4.append(r1)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r11.contains(r4)
                if (r4 != 0) goto L_0x00c6
                if (r12 == 0) goto L_0x00b5
                boolean r4 = r12.startsWith(r3)
                if (r4 != 0) goto L_0x00c6
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r1)
                r4.append(r3)
                java.lang.String r1 = r4.toString()
                boolean r1 = r12.contains(r1)
                if (r1 == 0) goto L_0x00b5
                goto L_0x00c6
            L_0x00b5:
                java.lang.String r1 = r10.username
                if (r1 == 0) goto L_0x00c8
                java.lang.String r1 = r10.username
                boolean r1 = r1.startsWith(r3)
                if (r1 == 0) goto L_0x00c8
                r1 = 2
                r13 = r1
                goto L_0x00c8
            L_0x00c4:
                r17 = r1
            L_0x00c6:
                r1 = 1
                r13 = r1
            L_0x00c8:
                if (r13 == 0) goto L_0x0105
                r1 = 0
                r4 = 1
                if (r13 != r4) goto L_0x00d8
                java.lang.String r14 = r10.title
                java.lang.CharSequence r1 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r14, r1, r3)
                r7.add(r1)
                goto L_0x0101
            L_0x00d8:
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                java.lang.String r15 = "@"
                r14.append(r15)
                java.lang.String r4 = r10.username
                r14.append(r4)
                java.lang.String r4 = r14.toString()
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r15)
                r14.append(r3)
                java.lang.String r14 = r14.toString()
                java.lang.CharSequence r1 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r4, r1, r14)
                r7.add(r1)
            L_0x0101:
                r6.add(r10)
                goto L_0x010f
            L_0x0105:
                int r15 = r15 + 1
                r1 = r17
                r3 = 0
                r4 = 1
                goto L_0x0074
            L_0x010d:
                r17 = r1
            L_0x010f:
                int r8 = r8 + 1
                r1 = r17
                r3 = 0
                r4 = 1
                goto L_0x004e
            L_0x0117:
                r0.updateSearchResults(r6, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatLinkActivity.SearchAdapter.lambda$null$1$ChatLinkActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLRPC.Chat> chats, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable(chats, names) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$updateSearchResults$3$ChatLinkActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$3$ChatLinkActivity$SearchAdapter(ArrayList chats, ArrayList names) {
            this.searchResult = chats;
            this.searchResultNames = names;
            if (ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter) {
                ChatLinkActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int count = this.searchResult.size();
            if (count != 0) {
                int i = this.totalCount;
                this.searchStartRow = i;
                this.totalCount = i + count + 1;
            } else {
                this.searchStartRow = -1;
            }
            super.notifyDataSetChanged();
        }

        public TLRPC.Chat getItem(int i) {
            return this.searchResult.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ManageChatUserCell(this.mContext, 6, 2, false);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.Chat chat = this.searchResult.get(position);
            String un = chat.username;
            CharSequence username = null;
            CharSequence name = this.searchResultNames.get(position);
            if (name != null && !TextUtils.isEmpty(un)) {
                String charSequence = name.toString();
                if (charSequence.startsWith("@" + un)) {
                    username = name;
                    name = null;
                }
            }
            ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
            userCell.setTag(Integer.valueOf(position));
            userCell.setData(chat, name, username, false);
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2;
        }

        public int getItemCount() {
            if (ChatLinkActivity.this.loadingChats) {
                return 0;
            }
            return ChatLinkActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new ManageChatUserCell(this.mContext, 6, 2, false);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (viewType != 2) {
                view = new HintInnerCell(this.mContext);
            } else {
                View view3 = new ManageChatTextCell(this.mContext);
                view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view3;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String str;
            int itemViewType = holder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                userCell.setTag(Integer.valueOf(position));
                TLRPC.Chat chat = (TLRPC.Chat) ChatLinkActivity.this.chats.get(position - ChatLinkActivity.this.chatStartRow);
                if (TextUtils.isEmpty(chat.username)) {
                    str = null;
                } else {
                    str = "@" + chat.username;
                }
                if (position == ChatLinkActivity.this.chatEndRow - 1 && ChatLinkActivity.this.info.linked_chat_id == 0) {
                    z = false;
                }
                userCell.setData(chat, (CharSequence) null, str, z);
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                if (position != ChatLinkActivity.this.detailRow) {
                    return;
                }
                if (ChatLinkActivity.this.isChannel) {
                    privacyCell.setText(LocaleController.getString("DiscussionChannelHelp2", R.string.DiscussionChannelHelp2));
                } else {
                    privacyCell.setText(LocaleController.getString("DiscussionGroupHelp2", R.string.DiscussionGroupHelp2));
                }
            } else if (itemViewType == 2) {
                ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                if (!ChatLinkActivity.this.isChannel) {
                    actionCell.setColors(Theme.key_windowBackgroundWhiteRedText5, Theme.key_windowBackgroundWhiteRedText5);
                    actionCell.setText(LocaleController.getString("DiscussionUnlinkChannel", R.string.DiscussionUnlinkChannel), (String) null, R.drawable.actions_remove_user, false);
                } else if (ChatLinkActivity.this.info.linked_chat_id != 0) {
                    actionCell.setColors(Theme.key_windowBackgroundWhiteRedText5, Theme.key_windowBackgroundWhiteRedText5);
                    actionCell.setText(LocaleController.getString("DiscussionUnlinkGroup", R.string.DiscussionUnlinkGroup), (String) null, R.drawable.actions_remove_user, false);
                } else {
                    actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                    actionCell.setText(LocaleController.getString("DiscussionCreateGroup", R.string.DiscussionCreateGroup), (String) null, R.drawable.menu_groups, false);
                    actionCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if (position == ChatLinkActivity.this.helpRow) {
                return 3;
            }
            if (position == ChatLinkActivity.this.createChatRow || position == ChatLinkActivity.this.removeChatRow) {
                return 2;
            }
            if (position < ChatLinkActivity.this.chatStartRow || position >= ChatLinkActivity.this.chatEndRow) {
                return 1;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatLinkActivity.this.lambda$getThemeDescriptions$16$ChatLinkActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        RecyclerListView recyclerListView5 = this.listView;
        RecyclerListView recyclerListView6 = recyclerListView5;
        RecyclerListView recyclerListView7 = this.listView;
        RecyclerListView recyclerListView8 = recyclerListView7;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_message), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) recyclerListView6, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueButton), new ThemeDescription((View) recyclerListView8, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueIcon)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$16$ChatLinkActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}

package im.bclpbkiauv.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.MediaActivity;
import im.bclpbkiauv.ui.ProfileActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.PlayingGameDrawable;
import im.bclpbkiauv.ui.components.RecordStatusDrawable;
import im.bclpbkiauv.ui.components.RoundStatusDrawable;
import im.bclpbkiauv.ui.components.ScamDrawable;
import im.bclpbkiauv.ui.components.SendingFileDrawable;
import im.bclpbkiauv.ui.components.StatusDrawable;
import im.bclpbkiauv.ui.components.TypingDotsDrawable;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.DialogCommonList;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.visualcall.VisualCallActivity;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Marker;

public class ChatActionBarHelper {
    private ActionBar actionBar;
    private ChatActivity chatActivity;
    private int currentConnectionState;
    private boolean inPreviewMode;
    private boolean[] isOnline = new boolean[1];
    private final Boolean isSysNotifyMessage;
    private CharSequence lastSubtitle;
    private String lastSubtitleColorKey;
    private int onlineCount;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private MryTextView tvUnreadCount;
    private FrameLayout unreadCountContainer;

    public ChatActionBarHelper(ChatActivity chatActivity2, ActionBar actionBar2, boolean isEncryptedChat, boolean inPreviewMode2) {
        this.chatActivity = chatActivity2;
        this.actionBar = actionBar2;
        this.inPreviewMode = inPreviewMode2;
        this.isSysNotifyMessage = chatActivity2.isSysNotifyMessage();
        if (!(chatActivity2 == null || actionBar2 == null)) {
            actionBar2.setOnClickListener(new View.OnClickListener(chatActivity2, isEncryptedChat) {
                private final /* synthetic */ ChatActivity f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    ChatActionBarHelper.this.lambda$new$0$ChatActionBarHelper(this.f$1, this.f$2, view);
                }
            });
        }
        TLRPC.Chat chat = chatActivity2.getCurrentChat();
        this.statusDrawables[0] = new TypingDotsDrawable();
        this.statusDrawables[1] = new RecordStatusDrawable();
        this.statusDrawables[2] = new SendingFileDrawable();
        this.statusDrawables[3] = new PlayingGameDrawable();
        this.statusDrawables[4] = new RoundStatusDrawable();
        int a = 0;
        while (true) {
            StatusDrawable[] statusDrawableArr = this.statusDrawables;
            if (a < statusDrawableArr.length) {
                statusDrawableArr[a].setIsChat(chat != null);
                a++;
            } else {
                return;
            }
        }
    }

    public /* synthetic */ void lambda$new$0$ChatActionBarHelper(ChatActivity chatActivity2, boolean isEncryptedChat, View v) {
        TLRPC.User user = chatActivity2.getCurrentUser();
        TLRPC.Chat chat = chatActivity2.getCurrentChat();
        if (user != null) {
            Bundle args = new Bundle();
            if (UserObject.isUserSelf(user)) {
                args.putLong("dialog_id", chatActivity2.getDialogId());
                MediaActivity fragment = new MediaActivity(args, new int[]{-1, -1, -1, -1, -1});
                fragment.setChatInfo(chatActivity2.getCurrentChatInfo());
                chatActivity2.presentFragment(fragment);
                return;
            }
            args.putInt("user_id", user.id);
            args.putBoolean("reportSpam", chatActivity2.hasReportSpam());
            if (isEncryptedChat) {
                args.putLong("dialog_id", chatActivity2.getDialogId());
            }
            if (!this.isSysNotifyMessage.booleanValue()) {
                chatActivity2.presentFragment(new NewProfileActivity(args));
            }
        } else if (chat != null) {
            Bundle args2 = new Bundle();
            args2.putInt("chat_id", chat.id);
            ProfileActivity fragment2 = new ProfileActivity(args2);
            fragment2.setChatInfo(chatActivity2.getCurrentChatInfo());
            fragment2.setPlayProfileAnimation(true);
            chatActivity2.presentFragment(fragment2);
        }
    }

    public void update() {
        updateTitle();
        updateOnlineCount();
        updateSubtitle();
        updateUnreadMessageCount();
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 != null) {
            updateCurrentConnectionState(ConnectionsManager.getInstance(chatActivity2.getCurrentAccount()).getConnectionState());
        }
    }

    public void updateTitle() {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 != null && this.actionBar != null) {
            TLRPC.User currentUser = chatActivity2.getCurrentUser();
            TLRPC.Chat currentChat = this.chatActivity.getCurrentChat();
            ContactsController contactsController = ContactsController.getInstance(this.chatActivity.getCurrentAccount());
            if (this.chatActivity.isInScheduleMode()) {
                if (UserObject.isUserSelf(currentUser)) {
                    this.actionBar.setTitle(LocaleController.getString("Reminders", R.string.Reminders));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ScheduledMessages", R.string.ScheduledMessages));
                }
            } else if (currentChat != null) {
                this.actionBar.setTitle(currentChat.title);
            } else if (currentUser != null) {
                if (currentUser.self) {
                    this.actionBar.setTitle(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                } else if (MessagesController.isSupportUser(currentUser) || contactsController.contactsDict.get(Integer.valueOf(currentUser.id)) != null || (contactsController.contactsDict.size() == 0 && contactsController.isLoadingContacts())) {
                    this.actionBar.setTitle(UserObject.getName(currentUser));
                } else if (!TextUtils.isEmpty(currentUser.phone)) {
                    ActionBar actionBar2 = this.actionBar;
                    PhoneFormat instance = PhoneFormat.getInstance();
                    actionBar2.setTitle(instance.format(Marker.ANY_NON_NULL_MARKER + currentUser.phone));
                } else {
                    this.actionBar.setTitle(UserObject.getName(currentUser));
                }
            }
            this.chatActivity.getParentActivity().setTitle(this.actionBar.getTitle());
        }
    }

    public void updateSubtitle() {
        CharSequence newSubtitle;
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 != null && this.actionBar != null) {
            TLRPC.User user = chatActivity2.getCurrentUser();
            if (UserObject.isUserSelf(user) || this.chatActivity.isInScheduleMode()) {
                this.actionBar.setSubtitle((CharSequence) null);
                return;
            }
            TLRPC.Chat chat = this.chatActivity.getCurrentChat();
            CharSequence printString = MessagesController.getInstance(this.chatActivity.getCurrentAccount()).printingStrings.get(this.chatActivity.getDialogId());
            if (printString != null) {
                printString = TextUtils.replace(printString, new String[]{"..."}, new String[]{""});
            }
            boolean useOnlineColor = false;
            if (printString == null || printString.length() == 0 || (ChatObject.isChannel(chat) && !chat.megagroup)) {
                setTypingAnimation(false);
                if (chat != null) {
                    TLRPC.ChatFull info = this.chatActivity.getCurrentChatInfo();
                    if (ChatObject.isChannel(chat)) {
                        if (info == null || info.participants_count == 0) {
                            newSubtitle = chat.megagroup ? info == null ? LocaleController.getString("Loading", R.string.Loading).toLowerCase() : chat.has_geo ? LocaleController.getString("MegaLocation", R.string.MegaLocation).toLowerCase() : !TextUtils.isEmpty(chat.username) ? LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase() : LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase() : (chat.flags & 64) != 0 ? LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase() : LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                        } else if (chat.megagroup) {
                            newSubtitle = LocaleController.formatPluralString("Members", info.participants_count);
                        } else {
                            int[] result = new int[1];
                            String shortNumber = LocaleController.formatShortNumber(info.participants_count, result);
                            newSubtitle = chat.megagroup ? LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber) : LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                        }
                    } else if (ChatObject.isKickedFromChat(chat)) {
                        newSubtitle = LocaleController.getString("YouWereKicked", R.string.YouWereKicked);
                    } else if (ChatObject.isLeftFromChat(chat)) {
                        newSubtitle = LocaleController.getString("YouLeft", R.string.YouLeft);
                    } else {
                        int count = chat.participants_count;
                        if (!(info == null || info.participants == null)) {
                            count = info.participants.participants.size();
                        }
                        newSubtitle = LocaleController.formatPluralString("Members", count);
                    }
                } else if (user != null) {
                    TLRPC.User newUser = MessagesController.getInstance(this.chatActivity.getCurrentAccount()).getUser(Integer.valueOf(user.id));
                    if (newUser != null) {
                        user = newUser;
                    }
                    if (user.id == UserConfig.getInstance(this.chatActivity.getCurrentAccount()).getClientUserId()) {
                        newSubtitle = LocaleController.getString("ChatYourSelf", R.string.ChatYourSelf);
                    } else if (user.id == 333000 || user.id == 777000 || user.id == 42777) {
                        newSubtitle = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                    } else if (MessagesController.isSupportUser(user)) {
                        newSubtitle = LocaleController.getString("SupportStatus", R.string.SupportStatus);
                    } else if (user.bot) {
                        newSubtitle = LocaleController.getString("Bot", R.string.Bot);
                    } else {
                        this.isOnline[0] = false;
                        CharSequence newStatus = LocaleController.formatUserStatus(this.chatActivity.getCurrentAccount(), user, this.isOnline);
                        useOnlineColor = this.isOnline[0];
                        newSubtitle = newStatus;
                    }
                } else {
                    newSubtitle = "";
                }
            } else {
                newSubtitle = printString;
                useOnlineColor = true;
                setTypingAnimation(true);
            }
            this.lastSubtitleColorKey = useOnlineColor ? Theme.key_chat_status : Theme.key_actionBarDefaultSubtitle;
            if (this.isSysNotifyMessage.booleanValue()) {
                this.actionBar.setSubtitle((CharSequence) null);
            } else if (this.lastSubtitle == null) {
                this.actionBar.setSubtitle(newSubtitle, true);
                this.actionBar.setSubtitleColor(Theme.getColor(this.lastSubtitleColorKey));
                this.actionBar.getSubtitleTextView().setTag(this.lastSubtitleColorKey);
            } else {
                this.lastSubtitle = newSubtitle;
            }
        }
    }

    private void setTypingAnimation(boolean start) {
        ActionBar actionBar2;
        if (this.chatActivity != null && (actionBar2 = this.actionBar) != null) {
            SimpleTextView subtitleTextView = actionBar2.getSubtitleTextView();
            if (start) {
                try {
                    Integer type = MessagesController.getInstance(this.chatActivity.getCurrentAccount()).printingStringsTypes.get(this.chatActivity.getDialogId());
                    if (subtitleTextView != null) {
                        subtitleTextView.setLeftDrawable((Drawable) this.statusDrawables[type.intValue()]);
                    }
                    for (int a = 0; a < this.statusDrawables.length; a++) {
                        if (a == type.intValue()) {
                            this.statusDrawables[a].start();
                        } else {
                            this.statusDrawables[a].stop();
                        }
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                if (subtitleTextView != null) {
                    subtitleTextView.setLeftDrawable((Drawable) null);
                }
                int a2 = 0;
                while (true) {
                    StatusDrawable[] statusDrawableArr = this.statusDrawables;
                    if (a2 < statusDrawableArr.length) {
                        statusDrawableArr[a2].stop();
                        a2++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void setTitleIcons(Drawable leftIcon, Drawable rightIcon) {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            SimpleTextView titleTextView = actionBar2.getTitleTextView();
            titleTextView.setLeftDrawable(leftIcon);
            if (!(titleTextView.getRightDrawable() instanceof ScamDrawable)) {
                titleTextView.setRightDrawable(rightIcon);
            }
        }
    }

    public void updateOnlineCount() {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 != null && this.actionBar != null) {
            this.onlineCount = 0;
            TLRPC.ChatFull info = chatActivity2.getCurrentChatInfo();
            if (info != null) {
                int currentTime = ConnectionsManager.getInstance(this.chatActivity.getCurrentAccount()).getCurrentTime();
                if ((info instanceof TLRPC.TL_chatFull) || ((info instanceof TLRPC.TL_channelFull) && info.participants_count <= 200 && info.participants != null)) {
                    for (int a = 0; a < info.participants.participants.size(); a++) {
                        TLRPC.User user = MessagesController.getInstance(this.chatActivity.getCurrentAccount()).getUser(Integer.valueOf(info.participants.participants.get(a).user_id));
                        if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getInstance(this.chatActivity.getCurrentAccount()).getClientUserId()) || user.status.expires <= 10000))) {
                            this.onlineCount++;
                        }
                    }
                } else if ((info instanceof TLRPC.TL_channelFull) && info.participants_count > 200) {
                    this.onlineCount = info.online_count;
                }
            }
        }
    }

    public void updateCurrentConnectionState(int state) {
        if (this.actionBar != null && this.currentConnectionState != state) {
            this.currentConnectionState = state;
            String title = null;
            if (state == 2) {
                title = LocaleController.getString("WaitingForNetwork", R.string.WaitingForNetwork);
            } else if (state == 1) {
                title = LocaleController.getString("Connecting", R.string.Connecting);
            } else if (state == 5) {
                title = LocaleController.getString("Updating", R.string.Updating);
            } else if (state == 4) {
                title = LocaleController.getString("ConnectingToProxy", R.string.ConnectingToProxy);
            }
            if (title == null) {
                CharSequence charSequence = this.lastSubtitle;
                if (charSequence != null) {
                    this.actionBar.setSubtitle(charSequence, true);
                    this.lastSubtitle = null;
                    String str = this.lastSubtitleColorKey;
                    if (str != null) {
                        this.actionBar.setSubtitleColor(Theme.getColor(str));
                        this.actionBar.getSubtitleTextView().setTag(this.lastSubtitleColorKey);
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.lastSubtitle == null && this.actionBar.getSubtitleTextView() != null) {
                this.lastSubtitle = this.actionBar.getSubtitleTextView().getText();
            }
            if (this.isSysNotifyMessage.booleanValue()) {
                this.actionBar.setSubtitle((CharSequence) null);
                return;
            }
            this.actionBar.setSubtitle(title, true);
            this.actionBar.setSubtitleColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
            this.actionBar.getSubtitleTextView().setTag(Theme.key_actionBarDefaultSubtitle);
        }
    }

    public void updateUnreadMessageCount() {
        if (this.chatActivity != null && this.actionBar != null) {
            int unreadCount = getAllUnreadCount();
            MryTextView mryTextView = this.tvUnreadCount;
            int i = 4;
            if (mryTextView == null) {
                if (unreadCount > 0) {
                    this.unreadCountContainer = new FrameLayout(this.chatActivity.getParentActivity());
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.unreadCountContainer.setPadding(0, this.inPreviewMode ? 0 : AndroidUtilities.statusBarHeight, 0, 0);
                    }
                    FrameLayout frameLayout = this.unreadCountContainer;
                    if (!this.inPreviewMode) {
                        i = 0;
                    }
                    frameLayout.setVisibility(i);
                    MryTextView mryTextView2 = new MryTextView(this.chatActivity.getParentActivity());
                    this.tvUnreadCount = mryTextView2;
                    mryTextView2.setText(String.valueOf(unreadCount));
                    this.tvUnreadCount.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), 0);
                    this.tvUnreadCount.setGravity(17);
                    this.tvUnreadCount.setTextColor(-1);
                    this.tvUnreadCount.setTextSize(12.0f);
                    this.tvUnreadCount.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(10.0f), this.chatActivity.getParentActivity().getResources().getColor(R.color.color_item_menu_red_f74c31)));
                    this.unreadCountContainer.addView(this.tvUnreadCount, LayoutHelper.createFrame(-2, -2, 19));
                    this.actionBar.addView(this.unreadCountContainer, LayoutHelper.createFrame(-2.0f, -1.0f, 3, 35.0f, 0.0f, 0.0f, 0.0f));
                }
            } else if (unreadCount > 0) {
                mryTextView.setText(String.valueOf(unreadCount));
                this.tvUnreadCount.setVisibility(0);
            } else {
                mryTextView.setVisibility(4);
            }
        }
    }

    private int getAllUnreadCount() {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null) {
            return 0;
        }
        int count = 0;
        MessagesController messagesController = MessagesController.getInstance(chatActivity2.getCurrentAccount());
        NotificationsController notificationsController = NotificationsController.getInstance(this.chatActivity.getCurrentAccount());
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(this.chatActivity.getCurrentAccount());
        Iterator<TLRPC.Dialog> it = messagesController.getAllDialogs().iterator();
        while (it.hasNext()) {
            TLRPC.Dialog dialog = it.next();
            if (notificationsController.showBadgeNumber) {
                if (notificationsController.showBadgeMessages) {
                    if (notificationsController.showBadgeMuted || dialog.notify_settings == null || (!dialog.notify_settings.silent && dialog.notify_settings.mute_until <= connectionsManager.getCurrentTime())) {
                        count += dialog.unread_count;
                    }
                } else if ((notificationsController.showBadgeMuted || dialog.notify_settings == null || (!dialog.notify_settings.silent && dialog.notify_settings.mute_until <= connectionsManager.getCurrentTime())) && dialog.unread_count != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public void setInPreviewMode(boolean inPreviewMode2) {
        this.inPreviewMode = inPreviewMode2;
        if (this.unreadCountContainer != null) {
            int i = 0;
            if (Build.VERSION.SDK_INT >= 21) {
                this.unreadCountContainer.setPadding(0, inPreviewMode2 ? 0 : AndroidUtilities.statusBarHeight, 0, 0);
            }
            FrameLayout frameLayout = this.unreadCountContainer;
            if (inPreviewMode2) {
                i = 8;
            }
            frameLayout.setVisibility(i);
        }
    }

    public void startCall(TLRPC.User user) {
        List<String> list = new ArrayList<>();
        list.add(LocaleController.getString("menu_voice_chat", R.string.menu_voice_chat));
        list.add(LocaleController.getString("menu_video_chat", R.string.menu_video_chat));
        List<Integer> list1 = new ArrayList<>();
        list1.add(Integer.valueOf(R.drawable.menu_voice_call));
        list1.add(Integer.valueOf(R.drawable.menu_video_call));
        new DialogCommonList((Activity) this.chatActivity.getParentActivity(), list, list1, Color.parseColor("#222222"), (DialogCommonList.RecyclerviewItemClickCallBack) new DialogCommonList.RecyclerviewItemClickCallBack(user) {
            private final /* synthetic */ TLRPC.User f$1;

            {
                this.f$1 = r2;
            }

            public final void onRecyclerviewItemClick(int i) {
                ChatActionBarHelper.this.lambda$startCall$1$ChatActionBarHelper(this.f$1, i);
            }
        }, 1).show();
    }

    public /* synthetic */ void lambda$startCall$1$ChatActionBarHelper(TLRPC.User user, int position) {
        if (position == 0) {
            if (ApplicationLoader.mbytAVideoCallBusy != 0) {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_busing_tip", R.string.visual_call_busing_tip));
            } else if (user.mutual_contact) {
                int currentConnectionState2 = ConnectionsManager.getInstance(this.chatActivity.getCurrentAccount()).getConnectionState();
                if (currentConnectionState2 == 2 || currentConnectionState2 == 1) {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_network", R.string.visual_call_no_network));
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(this.chatActivity.getParentActivity(), VisualCallActivity.class);
                intent.putExtra("CallType", 1);
                ArrayList<Integer> ArrInputPeers = new ArrayList<>();
                ArrInputPeers.add(Integer.valueOf(user.id));
                intent.putExtra("ArrayUser", ArrInputPeers);
                intent.putExtra("channel", new ArrayList());
                this.chatActivity.getParentActivity().startActivity(intent);
            } else {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
            }
        } else if (position != 1) {
        } else {
            if (ApplicationLoader.mbytAVideoCallBusy != 0) {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_busing_tip", R.string.visual_call_busing_tip));
            } else if (user.mutual_contact) {
                int currentConnectionState3 = ConnectionsManager.getInstance(this.chatActivity.getCurrentAccount()).getConnectionState();
                if (currentConnectionState3 == 2 || currentConnectionState3 == 1) {
                    ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_network", R.string.visual_call_no_network));
                    return;
                }
                Intent intent2 = new Intent();
                intent2.setClass(this.chatActivity.getParentActivity(), VisualCallActivity.class);
                intent2.putExtra("CallType", 2);
                ArrayList<Integer> ArrInputPeers2 = new ArrayList<>();
                ArrInputPeers2.add(Integer.valueOf(user.id));
                intent2.putExtra("ArrayUser", ArrInputPeers2);
                intent2.putExtra("channel", new ArrayList());
                this.chatActivity.getParentActivity().startActivity(intent2);
            } else {
                ToastUtils.show((CharSequence) LocaleController.getString("visual_call_no_friend_tip", R.string.visual_call_no_friend_tip));
            }
        }
    }
}

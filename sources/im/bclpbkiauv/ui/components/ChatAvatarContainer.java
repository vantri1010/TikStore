package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.MediaActivity;
import im.bclpbkiauv.ui.ProfileActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;

public class ChatAvatarContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentConnectionState;
    private boolean[] isOnline = new boolean[1];
    private CharSequence lastSubtitle;
    private String lastSubtitleColorKey;
    private boolean occupyStatusBar = true;
    private int onlineCount = -1;
    private ChatActivity parentFragment;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private SimpleTextView subtitleTextView;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private SimpleTextView titleTextView;

    public ChatAvatarContainer(Context context, ChatActivity chatActivity, boolean needTime) {
        super(context);
        this.parentFragment = chatActivity;
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        addView(this.avatarImageView);
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.titleTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.titleTextView.setTextSize(14);
        this.titleTextView.setGravity(3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
        addView(this.titleTextView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.subtitleTextView = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.subtitleTextView.setTag(Theme.key_actionBarDefaultSubtitle);
        this.subtitleTextView.setTextSize(11);
        this.subtitleTextView.setGravity(3);
        addView(this.subtitleTextView);
        if (needTime) {
            ImageView imageView = new ImageView(context);
            this.timeItem = imageView;
            imageView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
            this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView2 = this.timeItem;
            TimerDrawable timerDrawable2 = new TimerDrawable(context);
            this.timerDrawable = timerDrawable2;
            imageView2.setImageDrawable(timerDrawable2);
            addView(this.timeItem);
            this.timeItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatAvatarContainer.this.lambda$new$0$ChatAvatarContainer(view);
                }
            });
            this.timeItem.setContentDescription(LocaleController.getString("SetTimer", R.string.SetTimer));
        }
        ChatActivity chatActivity2 = this.parentFragment;
        if (chatActivity2 != null && !chatActivity2.isInScheduleMode()) {
            setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatAvatarContainer.this.lambda$new$1$ChatAvatarContainer(view);
                }
            });
            TLRPC.Chat chat = this.parentFragment.getCurrentChat();
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
    }

    public /* synthetic */ void lambda$new$0$ChatAvatarContainer(View v) {
        this.parentFragment.showDialog(AlertsCreator.createTTLAlert(getContext(), this.parentFragment.getCurrentEncryptedChat()).create());
    }

    public /* synthetic */ void lambda$new$1$ChatAvatarContainer(View v) {
        TLRPC.User user = this.parentFragment.getCurrentUser();
        TLRPC.Chat chat = this.parentFragment.getCurrentChat();
        if (user != null) {
            Bundle args = new Bundle();
            if (UserObject.isUserSelf(user)) {
                args.putLong("dialog_id", this.parentFragment.getDialogId());
                MediaActivity fragment = new MediaActivity(args, new int[]{-1, -1, -1, -1, -1});
                fragment.setChatInfo(this.parentFragment.getCurrentChatInfo());
                this.parentFragment.presentFragment(fragment);
                return;
            }
            args.putInt("user_id", user.id);
            args.putBoolean("reportSpam", this.parentFragment.hasReportSpam());
            if (this.timeItem != null) {
                args.putLong("dialog_id", this.parentFragment.getDialogId());
            }
            this.parentFragment.presentFragment(new NewProfileActivity(args));
        } else if (chat != null) {
            Bundle args2 = new Bundle();
            args2.putInt("chat_id", chat.id);
            ProfileActivity fragment2 = new ProfileActivity(args2);
            fragment2.setChatInfo(this.parentFragment.getCurrentChatInfo());
            fragment2.setPlayProfileAnimation(true);
            this.parentFragment.presentFragment(fragment2);
        }
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
    }

    public void setTitleColors(int title, int subtitle) {
        this.titleTextView.setTextColor(title);
        this.subtitleTextView.setTextColor(subtitle);
        this.subtitleTextView.setTag(Integer.valueOf(subtitle));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - AndroidUtilities.dp(70.0f);
        this.avatarImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
        this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
        this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), 1073741824));
        }
        setMeasuredDimension(width, View.MeasureSpec.getSize(heightMeasureSpec));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int viewTop = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(32.0f)) / 2) + ((Build.VERSION.SDK_INT < 21 || !this.occupyStatusBar) ? 0 : AndroidUtilities.statusBarHeight);
        this.avatarImageView.layout(AndroidUtilities.dp(3.0f), viewTop, AndroidUtilities.dp(35.0f), AndroidUtilities.dp(32.0f) + viewTop);
        if (this.subtitleTextView.getVisibility() == 0) {
            this.titleTextView.layout(AndroidUtilities.dp((float) 45), AndroidUtilities.dp(1.3f) + viewTop, AndroidUtilities.dp((float) 45) + this.titleTextView.getMeasuredWidth(), this.titleTextView.getTextHeight() + viewTop + AndroidUtilities.dp(1.3f));
        } else {
            this.titleTextView.layout(AndroidUtilities.dp((float) 45), AndroidUtilities.dp(11.0f) + viewTop, AndroidUtilities.dp((float) 45) + this.titleTextView.getMeasuredWidth(), this.titleTextView.getTextHeight() + viewTop + AndroidUtilities.dp(11.0f));
        }
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.layout(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(14.0f) + viewTop, AndroidUtilities.dp(41.0f), AndroidUtilities.dp(32.0f) + viewTop);
        }
        this.subtitleTextView.layout(AndroidUtilities.dp((float) 45), AndroidUtilities.dp(20.0f) + viewTop, AndroidUtilities.dp((float) 45) + this.subtitleTextView.getMeasuredWidth(), this.subtitleTextView.getTextHeight() + viewTop + AndroidUtilities.dp(20.0f));
    }

    public void showTimeItem() {
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.setVisibility(0);
        }
    }

    public void hideTimeItem() {
        ImageView imageView = this.timeItem;
        if (imageView != null) {
            imageView.setVisibility(8);
        }
    }

    public void setTime(int value) {
        TimerDrawable timerDrawable2 = this.timerDrawable;
        if (timerDrawable2 != null) {
            timerDrawable2.setTime(value);
        }
    }

    public void setTitleIcons(Drawable leftIcon, Drawable rightIcon) {
        this.titleTextView.setLeftDrawable(leftIcon);
        if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
            this.titleTextView.setRightDrawable(rightIcon);
        }
    }

    public void setTitle(CharSequence value) {
        setTitle(value, false);
    }

    public void setTitle(CharSequence value, boolean scam) {
        this.titleTextView.setText(value);
        if (scam) {
            if (!(this.titleTextView.getRightDrawable() instanceof ScamDrawable)) {
                ScamDrawable drawable = new ScamDrawable(11);
                drawable.setColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
                this.titleTextView.setRightDrawable((Drawable) drawable);
            }
        } else if (this.titleTextView.getRightDrawable() instanceof ScamDrawable) {
            this.titleTextView.setRightDrawable((Drawable) null);
        }
    }

    public void setSubtitle(CharSequence value) {
        if (this.lastSubtitle == null) {
            this.subtitleTextView.setText(value);
        } else {
            this.lastSubtitle = value;
        }
    }

    public ImageView getTimeItem() {
        return this.timeItem;
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView;
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    private void setTypingAnimation(boolean start) {
        if (start) {
            try {
                Integer type = MessagesController.getInstance(this.currentAccount).printingStringsTypes.get(this.parentFragment.getDialogId());
                this.subtitleTextView.setLeftDrawable((Drawable) this.statusDrawables[type.intValue()]);
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
            this.subtitleTextView.setLeftDrawable((Drawable) null);
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

    public void updateSubtitle() {
        CharSequence newSubtitle;
        CharSequence newSubtitle2;
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC.User user = chatActivity.getCurrentUser();
            if (!UserObject.isUserSelf(user) && !this.parentFragment.isInScheduleMode()) {
                TLRPC.Chat chat = this.parentFragment.getCurrentChat();
                CharSequence printString = MessagesController.getInstance(this.currentAccount).printingStrings.get(this.parentFragment.getDialogId());
                if (printString != null) {
                    printString = TextUtils.replace(printString, new String[]{"..."}, new String[]{""});
                }
                boolean useOnlineColor = false;
                if (printString == null || printString.length() == 0 || (ChatObject.isChannel(chat) && !chat.megagroup)) {
                    setTypingAnimation(false);
                    if (chat != null) {
                        TLRPC.ChatFull info = this.parentFragment.getCurrentChatInfo();
                        if (ChatObject.isChannel(chat)) {
                            if (info == null || info.participants_count == 0) {
                                newSubtitle = chat.megagroup ? info == null ? LocaleController.getString("Loading", R.string.Loading).toLowerCase() : chat.has_geo ? LocaleController.getString("MegaLocation", R.string.MegaLocation).toLowerCase() : !TextUtils.isEmpty(chat.username) ? LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase() : LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase() : (chat.flags & 64) != 0 ? LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase() : LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                            } else if (chat.megagroup) {
                                newSubtitle = LocaleController.formatPluralString("Members", info.participants_count);
                            } else {
                                int[] result = new int[1];
                                String shortNumber = LocaleController.formatShortNumber(info.participants_count, result);
                                if (chat.megagroup) {
                                    newSubtitle2 = LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                                } else {
                                    newSubtitle2 = LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                                }
                                newSubtitle = newSubtitle2;
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
                        TLRPC.User newUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(user.id));
                        if (newUser != null) {
                            user = newUser;
                        }
                        if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            newSubtitle = LocaleController.getString("ChatYourSelf", R.string.ChatYourSelf);
                        } else if (user.id == 333000 || user.id == 777000 || user.id == 42777) {
                            newSubtitle = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                        } else if (MessagesController.isSupportUser(user)) {
                            newSubtitle = LocaleController.getString("SupportStatus", R.string.SupportStatus);
                        } else if (user.bot) {
                            newSubtitle = LocaleController.getString("Bot", R.string.Bot);
                        } else {
                            boolean[] zArr = this.isOnline;
                            zArr[0] = false;
                            CharSequence newStatus = LocaleController.formatUserStatus(this.currentAccount, user, zArr);
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
                if (this.lastSubtitle == null) {
                    this.subtitleTextView.setText(newSubtitle);
                    this.subtitleTextView.setTextColor(Theme.getColor(this.lastSubtitleColorKey));
                    this.subtitleTextView.setTag(this.lastSubtitleColorKey);
                    return;
                }
                this.lastSubtitle = newSubtitle;
            } else if (this.subtitleTextView.getVisibility() != 8) {
                this.subtitleTextView.setVisibility(8);
            }
        }
    }

    public void setChatAvatar(TLRPC.Chat chat) {
        this.avatarDrawable.setInfo(chat);
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
        }
    }

    public void setUserAvatar(TLRPC.User user) {
        this.avatarDrawable.setInfo(user);
        if (UserObject.isUserSelf(user)) {
            this.avatarDrawable.setAvatarType(2);
            BackupImageView backupImageView = this.avatarImageView;
            if (backupImageView != null) {
                backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) user);
                return;
            }
            return;
        }
        BackupImageView backupImageView2 = this.avatarImageView;
        if (backupImageView2 != null) {
            backupImageView2.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
        }
    }

    public void checkAndUpdateAvatar() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            TLRPC.User user = chatActivity.getCurrentUser();
            TLRPC.Chat chat = this.parentFragment.getCurrentChat();
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                if (UserObject.isUserSelf(user)) {
                    this.avatarDrawable.setAvatarType(2);
                    BackupImageView backupImageView = this.avatarImageView;
                    if (backupImageView != null) {
                        backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) user);
                        return;
                    }
                    return;
                }
                BackupImageView backupImageView2 = this.avatarImageView;
                if (backupImageView2 != null) {
                    backupImageView2.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                }
            } else if (chat != null) {
                this.avatarDrawable.setInfo(chat);
                BackupImageView backupImageView3 = this.avatarImageView;
                if (backupImageView3 != null) {
                    backupImageView3.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
                }
            }
        }
    }

    public void updateOnlineCount() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            this.onlineCount = 0;
            TLRPC.ChatFull info = chatActivity.getCurrentChatInfo();
            if (info != null) {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if ((info instanceof TLRPC.TL_chatFull) || ((info instanceof TLRPC.TL_channelFull) && info.participants_count <= 200 && info.participants != null)) {
                    for (int a = 0; a < info.participants.participants.size(); a++) {
                        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(info.participants.participants.get(a).user_id));
                        if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                            this.onlineCount++;
                        }
                    }
                } else if ((info instanceof TLRPC.TL_channelFull) && info.participants_count > 200) {
                    this.onlineCount = info.online_count;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            updateCurrentConnectionState();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.parentFragment != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int state;
        if (id == NotificationCenter.didUpdateConnectionState && this.currentConnectionState != (state = ConnectionsManager.getInstance(this.currentAccount).getConnectionState())) {
            this.currentConnectionState = state;
            updateCurrentConnectionState();
        }
    }

    private void updateCurrentConnectionState() {
        String title = null;
        int i = this.currentConnectionState;
        if (i == 2) {
            title = LocaleController.getString("WaitingForNetwork", R.string.WaitingForNetwork);
        } else if (i == 1) {
            title = LocaleController.getString("Connecting", R.string.Connecting);
        } else if (i == 5) {
            title = LocaleController.getString("Updating", R.string.Updating);
        } else if (i == 4) {
            title = LocaleController.getString("ConnectingToProxy", R.string.ConnectingToProxy);
        }
        if (title == null) {
            CharSequence charSequence = this.lastSubtitle;
            if (charSequence != null) {
                this.subtitleTextView.setText(charSequence);
                this.lastSubtitle = null;
                String str = this.lastSubtitleColorKey;
                if (str != null) {
                    this.subtitleTextView.setTextColor(Theme.getColor(str));
                    this.subtitleTextView.setTag(this.lastSubtitleColorKey);
                    return;
                }
                return;
            }
            return;
        }
        if (this.lastSubtitle == null) {
            this.lastSubtitle = this.subtitleTextView.getText();
        }
        this.subtitleTextView.setText(title);
        this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.subtitleTextView.setTag(Theme.key_actionBarDefaultSubtitle);
    }
}

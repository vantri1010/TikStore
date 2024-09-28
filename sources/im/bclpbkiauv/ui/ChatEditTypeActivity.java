package im.bclpbkiauv.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.AdminedChannelCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.RadioButtonCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextBlockCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ChatEditTypeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList<>();
    private ShadowSectionCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private boolean canCreatePublic = true;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextInfoPrivacyCell checkTextView;
    private TextSettingsCell copyCell;
    private TLRPC.Chat currentChat;
    private EditText editText;
    private HeaderCell headerCell;
    private HeaderCell headerCell2;
    /* access modifiers changed from: private */
    public boolean ignoreTextChanges;
    private TLRPC.ChatFull info;
    private TextInfoPrivacyCell infoCell;
    private TLRPC.ExportedChatInvite invite;
    private boolean isChannel;
    private boolean isForcePublic;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutTypeContainer;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private LinearLayout privateContainer;
    private TextBlockCell privateTextView;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private TextSettingsCell revokeCell;
    private ShadowSectionCell sectionCell2;
    private TextSettingsCell shareCell;
    private TextSettingsCell textCell;
    private TextSettingsCell textCell2;
    private TextInfoPrivacyCell typeInfoCell;
    /* access modifiers changed from: private */
    public EditTextBoldCursor usernameTextView;

    public ChatEditTypeActivity(int id, boolean forcePublic) {
        this.chatId = id;
        this.isForcePublic = forcePublic;
    }

    public boolean onFragmentCreate() {
        TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(this.chatId));
        this.currentChat = chat;
        boolean z = true;
        if (chat == null) {
            TLRPC.Chat chatSync = getMessagesStorage().getChatSync(this.chatId);
            this.currentChat = chatSync;
            if (chatSync == null) {
                return false;
            }
            getMessagesController().putChat(this.currentChat, true);
            if (this.info == null) {
                TLRPC.ChatFull loadChatInfo = getMessagesStorage().loadChatInfo(this.chatId, new CountDownLatch(1), false, false);
                this.info = loadChatInfo;
                if (loadChatInfo == null) {
                    return false;
                }
            }
        }
        this.isPrivate = !this.isForcePublic && TextUtils.isEmpty(this.currentChat.username);
        if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
            z = false;
        }
        this.isChannel = z;
        if ((this.isForcePublic && TextUtils.isEmpty(this.currentChat.username)) || (this.isPrivate && this.currentChat.creator)) {
            TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TLRPC.TL_inputChannelEmpty();
            getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatEditTypeActivity.this.lambda$onFragmentCreate$1$ChatEditTypeActivity(tLObject, tL_error);
                }
            });
        }
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        return super.onFragmentCreate();
    }

    public /* synthetic */ void lambda$onFragmentCreate$1$ChatEditTypeActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatEditTypeActivity.this.lambda$null$0$ChatEditTypeActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$ChatEditTypeActivity(TLRPC.TL_error error) {
        boolean z = error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
        this.canCreatePublic = z;
        if (!z) {
            loadAdminedChannels();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        TLRPC.ChatFull chatFull;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (this.textCell2 != null && (chatFull = this.info) != null) {
            if (chatFull.stickerset != null) {
                this.textCell2.setTextAndValue(LocaleController.getString("GroupStickers", R.string.GroupStickers), this.info.stickerset.title, false);
            } else {
                this.textCell2.setText(LocaleController.getString("GroupStickers", R.string.GroupStickers), false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        EditTextBoldCursor editTextBoldCursor;
        super.onBecomeFullyVisible();
        if (this.isForcePublic && (editTextBoldCursor = this.usernameTextView) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatEditTypeActivity.this.finishFragment();
                } else if (id == 1) {
                    ChatEditTypeActivity.this.processDone();
                }
            }
        });
        this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                rectangle.bottom += AndroidUtilities.dp(60.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.linearLayout = linearLayout2;
        scrollView.addView(linearLayout2, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.linearLayout.setOrientation(1);
        if (this.isForcePublic) {
            this.actionBar.setTitle(LocaleController.getString("GroupNumber", R.string.GroupNumber2));
        } else if (this.isChannel) {
            this.actionBar.setTitle(LocaleController.getString("ChannelSettingsTitle", R.string.ChannelSettingsTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("GroupSettingsTitle", R.string.GroupSettingsTitle));
        }
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.linearLayoutTypeContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.linearLayoutTypeContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell3 = new HeaderCell(context2, 23);
        this.headerCell2 = headerCell3;
        headerCell3.setHeight(46);
        if (this.isChannel) {
            this.headerCell2.setText(LocaleController.getString("ChannelTypeHeader", R.string.ChannelTypeHeader));
        } else {
            this.headerCell2.setText(LocaleController.getString("GroupTypeHeader", R.string.GroupTypeHeader));
        }
        this.linearLayoutTypeContainer.addView(this.headerCell2);
        RadioButtonCell radioButtonCell = new RadioButtonCell(context2);
        this.radioButtonCell2 = radioButtonCell;
        radioButtonCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.isChannel) {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", R.string.ChannelPrivateInfo), false, this.isPrivate);
        } else {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", R.string.MegaPrivate), LocaleController.getString("MegaPrivateInfo", R.string.MegaPrivateInfo), false, this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$2$ChatEditTypeActivity(view);
            }
        });
        RadioButtonCell radioButtonCell3 = new RadioButtonCell(context2);
        this.radioButtonCell1 = radioButtonCell3;
        radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (this.isChannel) {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", R.string.ChannelPublicInfo), false, !this.isPrivate);
        } else {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", R.string.MegaPublic), LocaleController.getString("MegaPublicInfo", R.string.MegaPublicInfo), false, !this.isPrivate);
        }
        this.linearLayoutTypeContainer.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$3$ChatEditTypeActivity(view);
            }
        });
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
        this.sectionCell2 = shadowSectionCell;
        this.linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        if (this.isForcePublic) {
            this.radioButtonCell2.setVisibility(8);
            this.radioButtonCell1.setVisibility(8);
            this.sectionCell2.setVisibility(8);
            this.headerCell2.setVisibility(8);
        }
        LinearLayout linearLayout4 = new LinearLayout(context2);
        this.linkContainer = linearLayout4;
        linearLayout4.setOrientation(1);
        this.linkContainer.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell4 = new HeaderCell(context2, 23);
        this.headerCell = headerCell4;
        this.linkContainer.addView(headerCell4);
        LinearLayout linearLayout5 = new LinearLayout(context2);
        this.publicContainer = linearLayout5;
        linearLayout5.setOrientation(0);
        this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 23.0f, 7.0f, 23.0f, 0.0f));
        EditText editText2 = new EditText(context2);
        this.editText = editText2;
        editText2.setText(getMessagesController().linkPrefix + "/");
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable((Drawable) null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.editText.setVisibility(8);
        this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.usernameTextView = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.usernameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.usernameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable((Drawable) null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.publicContainer.addView(this.usernameTextView, LayoutHelper.createLinear(-1, 36));
        this.usernameTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!ChatEditTypeActivity.this.ignoreTextChanges) {
                    ChatEditTypeActivity chatEditTypeActivity = ChatEditTypeActivity.this;
                    boolean unused = chatEditTypeActivity.checkUserName(chatEditTypeActivity.usernameTextView.getText().toString());
                }
            }

            public void afterTextChanged(Editable editable) {
            }
        });
        LinearLayout linearLayout6 = new LinearLayout(context2);
        this.privateContainer = linearLayout6;
        linearLayout6.setOrientation(1);
        this.linkContainer.addView(this.privateContainer, LayoutHelper.createLinear(-1, -2));
        TextBlockCell textBlockCell = new TextBlockCell(context2);
        this.privateTextView = textBlockCell;
        textBlockCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.privateContainer.addView(this.privateTextView);
        this.privateTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$4$ChatEditTypeActivity(view);
            }
        });
        TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
        this.copyCell = textSettingsCell;
        textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.copyCell.setText(LocaleController.getString("CopyLink", R.string.CopyLink), true);
        this.privateContainer.addView(this.copyCell, LayoutHelper.createLinear(-1, -2));
        this.copyCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$5$ChatEditTypeActivity(view);
            }
        });
        TextSettingsCell textSettingsCell2 = new TextSettingsCell(context2);
        this.revokeCell = textSettingsCell2;
        textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.revokeCell.setText(LocaleController.getString("RevokeLink", R.string.RevokeLink), true);
        this.privateContainer.addView(this.revokeCell, LayoutHelper.createLinear(-1, -2));
        this.revokeCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$7$ChatEditTypeActivity(view);
            }
        });
        TextSettingsCell textSettingsCell3 = new TextSettingsCell(context2);
        this.shareCell = textSettingsCell3;
        textSettingsCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.shareCell.setText(LocaleController.getString("ShareLink", R.string.ShareLink), false);
        this.privateContainer.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatEditTypeActivity.this.lambda$createView$8$ChatEditTypeActivity(view);
            }
        });
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.checkTextView = textInfoPrivacyCell;
        textInfoPrivacyCell.setBottomPadding(6);
        this.linearLayout.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.typeInfoCell = textInfoPrivacyCell2;
        this.linearLayout.addView(textInfoPrivacyCell2, LayoutHelper.createLinear(-1, -2));
        LoadingCell loadingCell = new LoadingCell(context2);
        this.loadingAdminedCell = loadingCell;
        this.linearLayout.addView(loadingCell, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout7 = new LinearLayout(context2);
        this.adminnedChannelsLayout = linearLayout7;
        linearLayout7.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.adminnedChannelsLayout.setOrientation(1);
        this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
        ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context2);
        this.adminedInfoCell = shadowSectionCell2;
        this.linearLayout.addView(shadowSectionCell2, LayoutHelper.createLinear(-1, -2));
        if (!this.isPrivate && this.currentChat.username != null) {
            this.ignoreTextChanges = true;
            this.usernameTextView.setText(this.currentChat.username);
            this.usernameTextView.setSelection(this.currentChat.username.length());
            this.ignoreTextChanges = false;
        }
        updatePrivatePublic();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$ChatEditTypeActivity(View v) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$createView$3$ChatEditTypeActivity(View v) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$createView$4$ChatEditTypeActivity(View v) {
        if (this.invite != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                ToastUtils.show((int) R.string.LinkCopied);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$createView$5$ChatEditTypeActivity(View v) {
        if (this.invite != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                ToastUtils.show((int) R.string.LinkCopied);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$createView$7$ChatEditTypeActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setMessage(LocaleController.getString("RevokeAlert", R.string.RevokeAlert));
        builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditTypeActivity.this.lambda$null$6$ChatEditTypeActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$6$ChatEditTypeActivity(DialogInterface dialogInterface, int i) {
        generateLink(true);
    }

    public /* synthetic */ void lambda$createView$8$ChatEditTypeActivity(View v) {
        if (this.invite != null) {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink)), 500);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                this.info = chatFull;
                this.invite = chatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull == null) {
            return;
        }
        if (chatFull.exported_invite instanceof TLRPC.TL_chatInviteExported) {
            this.invite = chatFull.exported_invite;
        } else {
            generateLink(false);
        }
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (trySetUsername()) {
            finishFragment();
        }
    }

    private boolean trySetUsername() {
        if (this.isPrivate || (((this.currentChat.username != null || this.usernameTextView.length() == 0) && (this.currentChat.username == null || this.currentChat.username.equalsIgnoreCase(this.usernameTextView.getText().toString()))) || this.usernameTextView.length() == 0 || this.lastNameAvailable)) {
            String newUserName = "";
            String oldUserName = this.currentChat.username != null ? this.currentChat.username : newUserName;
            if (!this.isPrivate) {
                newUserName = this.usernameTextView.getText().toString();
            }
            if (oldUserName.equals(newUserName)) {
                return true;
            }
            if (!ChatObject.isChannel(this.currentChat)) {
                getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.IntCallback() {
                    public final void run(int i) {
                        ChatEditTypeActivity.this.lambda$trySetUsername$9$ChatEditTypeActivity(i);
                    }
                });
                return false;
            }
            getMessagesController().updateChannelUserName(this.chatId, newUserName);
            this.currentChat.username = newUserName;
            return true;
        }
        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(this.checkTextView, 2.0f, 0);
        return false;
    }

    public /* synthetic */ void lambda$trySetUsername$9$ChatEditTypeActivity(int param) {
        this.chatId = param;
        this.currentChat = getMessagesController().getChat(Integer.valueOf(param));
        processDone();
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels && this.adminnedChannelsLayout != null) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            getConnectionsManager().sendRequest(new TLRPC.TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatEditTypeActivity.this.lambda$loadAdminedChannels$15$ChatEditTypeActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadAdminedChannels$15$ChatEditTypeActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatEditTypeActivity.this.lambda$null$14$ChatEditTypeActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$14$ChatEditTypeActivity(TLObject response) {
        this.loadingAdminedChannels = false;
        if (response != null && getParentActivity() != null) {
            for (int a = 0; a < this.adminedChannelCells.size(); a++) {
                this.linearLayout.removeView(this.adminedChannelCells.get(a));
            }
            this.adminedChannelCells.clear();
            TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;
            for (int a2 = 0; a2 < res.chats.size(); a2++) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChatEditTypeActivity.this.lambda$null$13$ChatEditTypeActivity(view);
                    }
                });
                TLRPC.Chat chat = (TLRPC.Chat) res.chats.get(a2);
                boolean z = true;
                if (a2 != res.chats.size() - 1) {
                    z = false;
                }
                adminedChannelCell.setChannel(chat, z);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
            }
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$null$13$ChatEditTypeActivity(View view) {
        TLRPC.Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (this.isChannel) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, getMessagesController().linkPrefix + "/" + channel.username, channel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, getMessagesController().linkPrefix + "/" + channel.username, channel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener(channel) {
            private final /* synthetic */ TLRPC.Chat f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditTypeActivity.this.lambda$null$12$ChatEditTypeActivity(this.f$1, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$12$ChatEditTypeActivity(TLRPC.Chat channel, DialogInterface dialogInterface, int i) {
        TLRPC.TL_channels_updateUsername req1 = new TLRPC.TL_channels_updateUsername();
        req1.channel = MessagesController.getInputChannel(channel);
        req1.username = "";
        getConnectionsManager().sendRequest(req1, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatEditTypeActivity.this.lambda$null$11$ChatEditTypeActivity(tLObject, tL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$null$11$ChatEditTypeActivity(TLObject response1, TLRPC.TL_error error1) {
        if (response1 instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChatEditTypeActivity.this.lambda$null$10$ChatEditTypeActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$10$ChatEditTypeActivity() {
        this.canCreatePublic = true;
        if (this.usernameTextView.length() > 0) {
            checkUserName(this.usernameTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    private void updatePrivatePublic() {
        String str;
        int i;
        String str2;
        int i2;
        if (this.sectionCell2 != null) {
            int i3 = 8;
            if (this.isPrivate || this.canCreatePublic) {
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteGrayText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                if (this.isForcePublic) {
                    this.sectionCell2.setVisibility(8);
                } else {
                    this.sectionCell2.setVisibility(0);
                }
                this.adminedInfoCell.setVisibility(8);
                this.adminnedChannelsLayout.setVisibility(8);
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                if (this.isChannel) {
                    TextInfoPrivacyCell textInfoPrivacyCell = this.typeInfoCell;
                    if (this.isPrivate) {
                        i2 = R.string.ChannelPrivateLinkHelp;
                        str2 = "ChannelPrivateLinkHelp";
                    } else {
                        i2 = R.string.ChannelUsernameHelp;
                        str2 = "ChannelUsernameHelp";
                    }
                    textInfoPrivacyCell.setText(LocaleController.getString(str2, i2));
                    this.headerCell.setText(LocaleController.getString("GroupNumber", R.string.GroupNumber2));
                } else {
                    TextInfoPrivacyCell textInfoPrivacyCell2 = this.typeInfoCell;
                    if (this.isPrivate) {
                        i = R.string.MegaPrivateLinkHelp;
                        str = "MegaPrivateLinkHelp";
                    } else {
                        i = R.string.MegaUsernameHelp;
                        str = "MegaUsernameHelp";
                    }
                    textInfoPrivacyCell2.setText(LocaleController.getString(str, i));
                    this.headerCell.setText(LocaleController.getString("GroupNumber", R.string.GroupNumber2));
                }
                this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
                this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
                if (this.isPrivate) {
                    this.linkContainer.setVisibility(8);
                } else {
                    this.linkContainer.setVisibility(0);
                }
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                TextBlockCell textBlockCell = this.privateTextView;
                TLRPC.ExportedChatInvite exportedChatInvite = this.invite;
                textBlockCell.setText(exportedChatInvite != null ? exportedChatInvite.link : LocaleController.getString("Loading", R.string.Loading), true);
                TextInfoPrivacyCell textInfoPrivacyCell3 = this.checkTextView;
                if (!this.isPrivate && textInfoPrivacyCell3.length() != 0) {
                    i3 = 0;
                }
                textInfoPrivacyCell3.setVisibility(i3);
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", R.string.ChangePublicLimitReached));
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                this.linkContainer.setVisibility(8);
                this.checkTextView.setVisibility(8);
                this.sectionCell2.setVisibility(8);
                this.adminedInfoCell.setVisibility(0);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    this.adminnedChannelsLayout.setVisibility(8);
                    this.adminedInfoCell.setBackgroundDrawable((Drawable) null);
                } else {
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                }
            }
            this.radioButtonCell1.setChecked(!this.isPrivate, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.usernameTextView.clearFocus();
        }
    }

    /* access modifiers changed from: private */
    public boolean checkUserName(String name) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                getConnectionsManager().cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                this.checkTextView.setTextColor(Theme.key_windowBackgroundWhiteRedText4);
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (this.isChannel) {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", R.string.LinkInvalidStartNumber));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", R.string.LinkInvalidStartNumberMega));
                    }
                    this.checkTextView.setTextColor(Theme.key_windowBackgroundWhiteRedText4);
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                    this.checkTextView.setTextColor(Theme.key_windowBackgroundWhiteRedText4);
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (this.isChannel) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", R.string.LinkInvalidShort));
            } else {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", R.string.LinkInvalidShortMega));
            }
            this.checkTextView.setTextColor(Theme.key_windowBackgroundWhiteRedText4);
            return false;
        } else if (name.length() > 24) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", R.string.LinkInvalidLong));
            this.checkTextView.setTextColor(Theme.key_windowBackgroundWhiteRedText4);
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", R.string.LinkChecking));
            this.checkTextView.setTextColor(Theme.key_windowBackgroundWhiteGrayText8);
            this.lastCheckName = name;
            $$Lambda$ChatEditTypeActivity$cDt4go2kRngzAg5bkb8tTgha82I r0 = new Runnable(name) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatEditTypeActivity.this.lambda$checkUserName$18$ChatEditTypeActivity(this.f$1);
                }
            };
            this.checkRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUserName$18$ChatEditTypeActivity(String name) {
        TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
        req.username = name;
        req.channel = getMessagesController().getInputChannel(this.chatId);
        this.checkReqId = getConnectionsManager().sendRequest(req, new RequestDelegate(name) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatEditTypeActivity.this.lambda$null$17$ChatEditTypeActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$null$17$ChatEditTypeActivity(String name, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(name, error, response) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatEditTypeActivity.this.lambda$null$16$ChatEditTypeActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$16$ChatEditTypeActivity(String name, TLRPC.TL_error error, TLObject response) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(name)) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                    this.checkTextView.setText(LocaleController.getString("LinkInUse", R.string.LinkInUse));
                } else {
                    this.canCreatePublic = false;
                    loadAdminedChannels();
                }
                this.checkTextView.setTextColor(Theme.key_windowBackgroundWhiteRedText4);
                this.lastNameAvailable = false;
                return;
            }
            this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, name));
            this.checkTextView.setTextColor(Theme.key_windowBackgroundWhiteGreenText);
            this.lastNameAvailable = true;
        }
    }

    private void generateLink(boolean newRequest) {
        this.loadingInvite = true;
        TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
        req.peer = getMessagesController().getInputPeer(-this.chatId);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(newRequest) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatEditTypeActivity.this.lambda$generateLink$20$ChatEditTypeActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$generateLink$20$ChatEditTypeActivity(boolean newRequest, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, newRequest) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatEditTypeActivity.this.lambda$null$19$ChatEditTypeActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$19$ChatEditTypeActivity(TLRPC.TL_error error, TLObject response, boolean newRequest) {
        if (error == null) {
            TLRPC.ExportedChatInvite exportedChatInvite = (TLRPC.ExportedChatInvite) response;
            this.invite = exportedChatInvite;
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null) {
                chatFull.exported_invite = exportedChatInvite;
            }
            if (newRequest) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setMessage(LocaleController.getString("RevokeAlertNewLink", R.string.RevokeAlertNewLink));
                    builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                    builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                } else {
                    return;
                }
            }
        }
        this.loadingInvite = false;
        TextBlockCell textBlockCell = this.privateTextView;
        if (textBlockCell != null) {
            TLRPC.ExportedChatInvite exportedChatInvite2 = this.invite;
            textBlockCell.setText(exportedChatInvite2 != null ? exportedChatInvite2.link : LocaleController.getString("Loading", R.string.Loading), true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatEditTypeActivity.this.lambda$getThemeDescriptions$21$ChatEditTypeActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.infoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription(this.textCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription((View) this.checkTextView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGreenText), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.privateTextView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.privateTextView, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_progressCircle), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackground), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackgroundChecked), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackground), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackgroundChecked), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.copyCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.copyCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.revokeCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.revokeCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.shareCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.shareCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteLinkText), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, themeDescriptionDelegate, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$21$ChatEditTypeActivity() {
        LinearLayout linearLayout2 = this.adminnedChannelsLayout;
        if (linearLayout2 != null) {
            int count = linearLayout2.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.adminnedChannelsLayout.getChildAt(a);
                if (child instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) child).update();
                }
            }
        }
    }
}

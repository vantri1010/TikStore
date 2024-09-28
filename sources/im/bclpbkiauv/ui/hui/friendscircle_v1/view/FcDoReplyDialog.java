package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelRelativeLayout;
import com.bjz.comm.net.bean.FCEntitysRequest;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EmojiView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ChooseAtContactsActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.KeyboardUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcDoReplyDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.method.AtUserMethod;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.method.MethodContext;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.AtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.MryAlphaImageView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class FcDoReplyDialog extends Dialog implements NotificationCenter.NotificationCenterDelegate, ChooseAtContactsActivity.ContactsActivityDelegate {
    private ViewTreeObserver.OnGlobalLayoutListener attach;
    /* access modifiers changed from: private */
    public long currentForumId = -1;
    private final Window dialogWindow;
    /* access modifiers changed from: private */
    public EditTextBoldCursor etReply;
    private FrameLayout flEtContainer;
    /* access modifiers changed from: private */
    public boolean isAutoDismiss = false;
    /* access modifiers changed from: private */
    public boolean isEnableAtUser = false;
    /* access modifiers changed from: private */
    public boolean isNewReply = true;
    private ImageView ivEmoji;
    private MryAlphaImageView ivSend;
    /* access modifiers changed from: private */
    public HashMap<Long, Editable> lastUnPostContent = null;
    /* access modifiers changed from: private */
    public OnFcDoReplyListener listener;
    /* access modifiers changed from: private */
    public KPSwitchPanelRelativeLayout llEmoji;
    /* access modifiers changed from: private */
    public Activity mActivity;
    /* access modifiers changed from: private */
    public EmojiView mEmojiView;
    /* access modifiers changed from: private */
    public int maxContentLen = 400;
    private MethodContext methodContext;
    private final RelativeLayout rlContentView;

    public interface OnFcDoReplyListener {
        void onInputReplyContent(String str, ArrayList<FCEntitysRequest> arrayList);

        void startFragment(BaseFragment baseFragment);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FcDoReplyDialog(android.content.Context r17) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = 2131755219(0x7f1000d3, float:1.9141311E38)
            r0.<init>(r1, r2)
            r2 = 400(0x190, float:5.6E-43)
            r0.maxContentLen = r2
            r2 = 0
            r0.isEnableAtUser = r2
            r0.isAutoDismiss = r2
            r3 = 0
            r0.lastUnPostContent = r3
            r4 = 1
            r0.isNewReply = r4
            r5 = -1
            r0.currentForumId = r5
            r5 = r1
            android.app.Activity r5 = (android.app.Activity) r5
            r0.mActivity = r5
            android.widget.RelativeLayout r5 = new android.widget.RelativeLayout
            android.app.Activity r6 = r0.mActivity
            r5.<init>(r6)
            r6 = -1
            android.widget.RelativeLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createRelative(r6, r6)
            r5.setLayoutParams(r7)
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcDoReplyDialog$1 r8 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcDoReplyDialog$1
            r8.<init>()
            r5.setOnClickListener(r8)
            android.widget.RelativeLayout r8 = new android.widget.RelativeLayout
            android.app.Activity r9 = r0.mActivity
            r8.<init>(r9)
            r0.rlContentView = r8
            r8 = -2
            android.widget.RelativeLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createRelative(r6, r8)
            r10 = 12
            r9.addRule(r10)
            android.widget.RelativeLayout r10 = r0.rlContentView
            r10.setLayoutParams(r9)
            android.widget.RelativeLayout r10 = r0.rlContentView
            java.lang.String r11 = "windowBackgroundWhite"
            int r12 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            r10.setBackgroundColor(r12)
            android.widget.FrameLayout r10 = new android.widget.FrameLayout
            android.app.Activity r12 = r0.mActivity
            r10.<init>(r12)
            r0.flEtContainer = r10
            int r12 = r10.hashCode()
            r10.setId(r12)
            im.bclpbkiauv.ui.hviews.MryAlphaImageView r10 = new im.bclpbkiauv.ui.hviews.MryAlphaImageView
            r10.<init>(r1)
            r0.ivSend = r10
            int r12 = r10.hashCode()
            r10.setId(r12)
            im.bclpbkiauv.ui.hviews.MryAlphaImageView r10 = r0.ivSend
            r12 = 2131231155(0x7f0801b3, float:1.8078383E38)
            r10.setImageResource(r12)
            im.bclpbkiauv.ui.hviews.MryAlphaImageView r10 = r0.ivSend
            android.graphics.drawable.Drawable r10 = r10.getDrawable()
            android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
            java.lang.String r13 = "chat_messagePanelSend"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r12.<init>(r13, r14)
            r10.setColorFilter(r12)
            im.bclpbkiauv.ui.hviews.MryAlphaImageView r10 = r0.ivSend
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.-$$Lambda$FcDoReplyDialog$o77w0T1UAO9TpngFWcOssAgiSBE r12 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.-$$Lambda$FcDoReplyDialog$o77w0T1UAO9TpngFWcOssAgiSBE
            r12.<init>()
            r10.setOnClickListener(r12)
            r10 = 40
            android.widget.RelativeLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createRelative(r10, r10)
            r13 = 21
            r12.addRule(r13)
            r13 = 15
            r12.addRule(r13)
            android.widget.FrameLayout r14 = r0.flEtContainer
            int r14 = r14.getId()
            r15 = 6
            r12.addRule(r15, r14)
            android.widget.FrameLayout r14 = r0.flEtContainer
            int r14 = r14.getId()
            r4 = 8
            r12.addRule(r4, r14)
            im.bclpbkiauv.ui.hviews.MryAlphaImageView r14 = r0.ivSend
            r14.setLayoutParams(r12)
            im.bclpbkiauv.ui.hviews.MryAlphaImageView r14 = r0.ivSend
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            r14.setScaleType(r3)
            android.widget.RelativeLayout r3 = r0.rlContentView
            im.bclpbkiauv.ui.hviews.MryAlphaImageView r14 = r0.ivSend
            r3.addView(r14)
            android.widget.ImageView r3 = new android.widget.ImageView
            android.app.Activity r14 = r0.mActivity
            r3.<init>(r14)
            r0.ivEmoji = r3
            int r14 = r3.hashCode()
            r3.setId(r14)
            android.widget.RelativeLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createRelative(r10, r10)
            im.bclpbkiauv.ui.hviews.MryAlphaImageView r10 = r0.ivSend
            int r10 = r10.getId()
            r3.addRule(r2, r10)
            r3.addRule(r13)
            android.widget.FrameLayout r10 = r0.flEtContainer
            int r10 = r10.getId()
            r3.addRule(r15, r10)
            android.widget.FrameLayout r10 = r0.flEtContainer
            int r10 = r10.getId()
            r3.addRule(r4, r10)
            android.widget.ImageView r4 = r0.ivEmoji
            r4.setLayoutParams(r3)
            android.widget.ImageView r4 = r0.ivEmoji
            android.widget.ImageView$ScaleType r10 = android.widget.ImageView.ScaleType.CENTER_INSIDE
            r4.setScaleType(r10)
            android.widget.ImageView r4 = r0.ivEmoji
            r10 = 2131230983(0x7f080107, float:1.8078034E38)
            r4.setImageResource(r10)
            android.widget.RelativeLayout r4 = r0.rlContentView
            android.widget.ImageView r10 = r0.ivEmoji
            r4.addView(r10)
            r4 = 10
            android.widget.RelativeLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createRelative(r6, r8, r4)
            r8 = 1097859072(0x41700000, float:15.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r4.setMarginStart(r8)
            r8 = 1099956224(0x41900000, float:18.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r4.topMargin = r10
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r4.bottomMargin = r8
            android.widget.ImageView r8 = r0.ivEmoji
            int r8 = r8.getId()
            r4.addRule(r2, r8)
            android.widget.FrameLayout r8 = r0.flEtContainer
            r8.setLayoutParams(r4)
            android.widget.FrameLayout r8 = r0.flEtContainer
            android.app.Activity r10 = r0.mActivity
            android.content.res.Resources r10 = r10.getResources()
            r13 = 2131099743(0x7f06005f, float:1.7811848E38)
            int r10 = r10.getColor(r13)
            r13 = 1065353216(0x3f800000, float:1.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r14 = 1101004800(0x41a00000, float:20.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            im.bclpbkiauv.messenger.utils.ShapeUtils$ShapeDrawable r10 = im.bclpbkiauv.messenger.utils.ShapeUtils.createStrokeAndFill(r10, r13, r14, r11)
            r8.setBackground(r10)
            android.widget.FrameLayout r8 = r0.flEtContainer
            r10 = 1109393408(0x42200000, float:40.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r8.setMinimumHeight(r10)
            android.widget.FrameLayout r8 = r0.flEtContainer
            r10 = 1098907648(0x41800000, float:16.0)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r13 = 1073741824(0x40000000, float:2.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            r8.setPadding(r11, r14, r10, r13)
            android.widget.RelativeLayout r8 = r0.rlContentView
            android.widget.FrameLayout r10 = r0.flEtContainer
            r8.addView(r10)
            android.widget.RelativeLayout r8 = r0.rlContentView
            r5.addView(r8)
            r16.initReply()
            r16.initEmoji()
            r0.setContentView(r5)
            android.view.Window r8 = r16.getWindow()
            r0.dialogWindow = r8
            r10 = 0
            r8.setBackgroundDrawable(r10)
            android.view.Window r8 = r0.dialogWindow
            android.view.WindowManager$LayoutParams r8 = r8.getAttributes()
            r8.width = r6
            r8.height = r6
            android.view.Window r6 = r0.dialogWindow
            r6.setAttributes(r8)
            android.view.Window r6 = r0.dialogWindow
            android.view.View r6 = r6.getDecorView()
            r6.setPadding(r2, r2, r2, r2)
            android.app.Activity r2 = r0.mActivity
            android.view.Window r2 = r2.getWindow()
            r6 = 16
            r2.setSoftInputMode(r6)
            android.view.Window r2 = r0.dialogWindow
            r6 = 131072(0x20000, float:1.83671E-40)
            r2.clearFlags(r6)
            android.view.Window r2 = r0.dialogWindow
            r6 = 20
            r2.setSoftInputMode(r6)
            r2 = 1
            r0.setCanceledOnTouchOutside(r2)
            im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcDoReplyDialog$2 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcDoReplyDialog$2
            r2.<init>()
            r0.setOnKeyListener(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcDoReplyDialog.<init>(android.content.Context):void");
    }

    public /* synthetic */ void lambda$new$0$FcDoReplyDialog(View v) {
        tryPublishComment();
        dismiss();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        EmojiView emojiView;
        KLog.d("---------通知" + id);
        if (id == NotificationCenter.emojiDidLoad && (emojiView = this.mEmojiView) != null) {
            emojiView.invalidateViews();
        }
    }

    private void initReply() {
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(this.mActivity);
        this.etReply = editTextBoldCursor;
        editTextBoldCursor.setBackground((Drawable) null);
        this.etReply.setTextSize(2, 15.0f);
        this.etReply.setGravity(8388627);
        this.etReply.setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.maxContentLen)});
        this.etReply.setHint("");
        this.etReply.setImeOptions(6);
        this.etReply.setInputType(131072);
        this.etReply.setSingleLine(false);
        if (Theme.getCurrentTheme().isDark()) {
            this.etReply.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        }
        this.etReply.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.etReply.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return FcDoReplyDialog.this.lambda$initReply$1$FcDoReplyDialog(textView, i, keyEvent);
            }
        });
        this.etReply.addTextChangedListener(new TextWatcher() {
            private int beforeCount;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                this.beforeCount = s.toString().length();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String setMsg = s.toString();
                if (setMsg.length() >= FcDoReplyDialog.this.maxContentLen) {
                    FcToastUtils.show((CharSequence) "不能输入更多啦～");
                }
                if (FcDoReplyDialog.this.isEnableAtUser && setMsg.length() >= this.beforeCount && FcDoReplyDialog.this.etReply.getSelectionEnd() > 0 && setMsg.charAt(FcDoReplyDialog.this.etReply.getSelectionEnd() - 1) == '@' && FcDoReplyDialog.this.listener != null) {
                    FcDoReplyDialog.this.dismiss();
                    ChooseAtContactsActivity chooseAtContactsActivity = new ChooseAtContactsActivity(new Bundle());
                    chooseAtContactsActivity.setDelegate(FcDoReplyDialog.this);
                    FcDoReplyDialog.this.listener.startFragment(chooseAtContactsActivity);
                }
            }

            public void afterTextChanged(Editable s) {
                FcDoReplyDialog.this.changeSendBtn();
            }
        });
        MethodContext methodContext2 = new MethodContext();
        this.methodContext = methodContext2;
        methodContext2.setMethod(AtUserMethod.INSTANCE);
        this.methodContext.init(this.etReply);
        this.flEtContainer.addView(this.etReply, LayoutHelper.createFrame(-1, -1, 8388627));
    }

    public /* synthetic */ boolean lambda$initReply$1$FcDoReplyDialog(TextView v, int actionId, KeyEvent event) {
        if (actionId != 6) {
            return false;
        }
        tryPublishComment();
        dismiss();
        return true;
    }

    private void initEmoji() {
        this.llEmoji = new KPSwitchPanelRelativeLayout(this.mActivity);
        this.llEmoji.setLayoutParams(LayoutHelper.createRelative(-1, KeyboardUtils.getValidPanelHeight(this.mActivity), 3, this.flEtContainer.getId()));
        EmojiView emojiView = new EmojiView(false, false, this.mActivity, false, (TLRPC.ChatFull) null);
        this.mEmojiView = emojiView;
        this.llEmoji.addView(emojiView, LayoutHelper.createRelative(-1, -2));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        this.mEmojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
            public /* synthetic */ boolean canSchedule() {
                return EmojiView.EmojiViewDelegate.CC.$default$canSchedule(this);
            }

            public /* synthetic */ boolean isExpanded() {
                return EmojiView.EmojiViewDelegate.CC.$default$isExpanded(this);
            }

            public /* synthetic */ boolean isInScheduleMode() {
                return EmojiView.EmojiViewDelegate.CC.$default$isInScheduleMode(this);
            }

            public /* synthetic */ boolean isSearchOpened() {
                return EmojiView.EmojiViewDelegate.CC.$default$isSearchOpened(this);
            }

            public /* synthetic */ void onGifSelected(View view, Object obj, Object obj2, boolean z, int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onGifSelected(this, view, obj, obj2, z, i);
            }

            public /* synthetic */ void onSearchOpenClose(int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onSearchOpenClose(this, i);
            }

            public /* synthetic */ void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
                EmojiView.EmojiViewDelegate.CC.$default$onShowStickerSet(this, stickerSet, inputStickerSet);
            }

            public /* synthetic */ void onStickerSelected(View view, TLRPC.Document document, Object obj, boolean z, int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onStickerSelected(this, view, document, obj, z, i);
            }

            public /* synthetic */ void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered) {
                EmojiView.EmojiViewDelegate.CC.$default$onStickerSetAdd(this, stickerSetCovered);
            }

            public /* synthetic */ void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered) {
                EmojiView.EmojiViewDelegate.CC.$default$onStickerSetRemove(this, stickerSetCovered);
            }

            public /* synthetic */ void onStickersGroupClick(int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onStickersGroupClick(this, i);
            }

            public /* synthetic */ void onStickersSettingsClick() {
                EmojiView.EmojiViewDelegate.CC.$default$onStickersSettingsClick(this);
            }

            public /* synthetic */ void onTabOpened(int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onTabOpened(this, i);
            }

            public boolean onBackspace() {
                if (FcDoReplyDialog.this.etReply.length() == 0) {
                    return false;
                }
                FcDoReplyDialog.this.etReply.dispatchKeyEvent(new KeyEvent(0, 67));
                return true;
            }

            public void onEmojiSelected(String symbol) {
                int i = FcDoReplyDialog.this.etReply.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    CharSequence localCharSequence = Emoji.replaceEmoji(symbol, FcDoReplyDialog.this.etReply.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    FcDoReplyDialog.this.etReply.setText(FcDoReplyDialog.this.etReply.getText().insert(i, localCharSequence));
                    int j = localCharSequence.length() + i;
                    FcDoReplyDialog.this.etReply.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }

            public void onClearEmojiRecent() {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) FcDoReplyDialog.this.mActivity);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ClearRecentEmoji", R.string.ClearRecentEmoji));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FcDoReplyDialog.AnonymousClass4.this.lambda$onClearEmojiRecent$0$FcDoReplyDialog$4(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            }

            public /* synthetic */ void lambda$onClearEmojiRecent$0$FcDoReplyDialog$4(DialogInterface dialogInterface, int i) {
                FcDoReplyDialog.this.mEmojiView.clearRecentEmoji();
            }
        });
        this.ivEmoji.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean unused = FcDoReplyDialog.this.isAutoDismiss = true;
                if (FcDoReplyDialog.this.llEmoji.getVisibility() == 0) {
                    FcDoReplyDialog.this.llEmoji.setVisibility(4);
                    AndroidUtilities.showKeyboard(FcDoReplyDialog.this.etReply);
                    return;
                }
                AndroidUtilities.hideKeyboard(FcDoReplyDialog.this.etReply);
            }
        });
        this.attach = KeyboardUtils.attach(this.mActivity, this.llEmoji, new KeyboardUtils.OnKeyboardShowingListener() {
            public void onKeyboardShowing(boolean isShowing) {
                if (isShowing) {
                    FcDoReplyDialog.this.llEmoji.setVisibility(8);
                    if (FcDoReplyDialog.this.isNewReply) {
                        if (FcDoReplyDialog.this.currentForumId == -1 || FcDoReplyDialog.this.lastUnPostContent == null || FcDoReplyDialog.this.lastUnPostContent.size() <= 0 || !FcDoReplyDialog.this.lastUnPostContent.containsKey(Long.valueOf(FcDoReplyDialog.this.currentForumId)) || FcDoReplyDialog.this.lastUnPostContent.get(Long.valueOf(FcDoReplyDialog.this.currentForumId)) == null) {
                            FcDoReplyDialog.this.etReply.setText("");
                        } else {
                            FcDoReplyDialog.this.etReply.setText((CharSequence) FcDoReplyDialog.this.lastUnPostContent.get(Long.valueOf(FcDoReplyDialog.this.currentForumId)));
                        }
                        boolean unused = FcDoReplyDialog.this.isNewReply = false;
                    }
                } else if (FcDoReplyDialog.this.isAutoDismiss) {
                    FcDoReplyDialog.this.llEmoji.setVisibility(0);
                } else {
                    FcDoReplyDialog.this.saveUnPostContent();
                    FcDoReplyDialog.this.dismiss();
                }
                boolean unused2 = FcDoReplyDialog.this.isAutoDismiss = false;
            }
        });
        this.rlContentView.addView(this.llEmoji);
    }

    private void tryPublishComment() {
        HashMap<Long, Editable> hashMap;
        Editable text = this.etReply.getText();
        if (!TextUtils.isEmpty(text.toString().trim())) {
            String mStrContent = text.toString().trim();
            if (this.listener != null) {
                String replaceStr = mStrContent;
                ArrayList<FCEntitysRequest> atUserBeanList = null;
                if (this.isEnableAtUser) {
                    atUserBeanList = new ArrayList<>();
                    AtUserSpan[] spans = (AtUserSpan[]) text.getSpans(0, text.length(), AtUserSpan.class);
                    if (spans.length > 1) {
                        Arrays.sort(spans, new Comparator(text) {
                            private final /* synthetic */ Editable f$0;

                            {
                                this.f$0 = r1;
                            }

                            public final int compare(Object obj, Object obj2) {
                                return FcDoReplyDialog.lambda$tryPublishComment$2(this.f$0, (AtUserSpan) obj, (AtUserSpan) obj2);
                            }
                        });
                    }
                    for (AtUserSpan atUserSpan : spans) {
                        atUserBeanList.add(new FCEntitysRequest("@" + atUserSpan.getNickName(), atUserSpan.getUserID(), atUserSpan.getAccessHash()));
                        if (!TextUtils.isEmpty(atUserSpan.getUserName())) {
                            String s = "@" + atUserSpan.getNickName() + SQLBuilder.PARENTHESES_LEFT + atUserSpan.getUserName() + SQLBuilder.PARENTHESES_RIGHT;
                            if (replaceStr.contains(s)) {
                                replaceStr = replaceStr.replace(s, "@" + atUserSpan.getNickName());
                            }
                        }
                    }
                }
                this.listener.onInputReplyContent(replaceStr, atUserBeanList);
                if (this.currentForumId != -1 && (hashMap = this.lastUnPostContent) != null && hashMap.size() > 0 && this.lastUnPostContent.containsKey(Long.valueOf(this.currentForumId))) {
                    this.lastUnPostContent.remove(Long.valueOf(this.currentForumId));
                }
                this.currentForumId = -1;
                this.etReply.setText("");
            }
        }
    }

    static /* synthetic */ int lambda$tryPublishComment$2(Editable text, AtUserSpan o1, AtUserSpan o2) {
        return text.getSpanStart(o1) - text.getSpanStart(o2);
    }

    public void saveUnPostContent() {
        Editable text = this.etReply.getText();
        if (this.currentForumId != -1 && text != null && text.length() > 0) {
            if (this.lastUnPostContent == null) {
                this.lastUnPostContent = new HashMap<>();
            }
            this.lastUnPostContent.put(Long.valueOf(this.currentForumId), text);
        }
    }

    public void dismiss() {
        this.etReply.clearFocus();
        AndroidUtilities.hideKeyboard(this.etReply);
        super.dismiss();
    }

    public void show(String receiver, long forumId, boolean isEnableAtUser2, boolean isComment) {
        if (this.etReply != null) {
            String hint = "";
            if (!TextUtils.isEmpty(receiver)) {
                if (isComment) {
                    hint = String.format("%s%s", new Object[]{this.mActivity.getString(R.string.friends_circle_hint_edittext_comment), receiver});
                } else {
                    hint = String.format("%s%s", new Object[]{this.mActivity.getString(R.string.friends_circle_hint_edittext_reply), receiver});
                }
                if (!hint.endsWith("...")) {
                    hint = hint + "...";
                }
            }
            this.etReply.setHint(hint);
            if (!isComment) {
                this.isEnableAtUser = false;
            } else {
                this.isEnableAtUser = isEnableAtUser2;
            }
            this.currentForumId = forumId;
            this.isNewReply = true;
            show();
            this.etReply.setFocusable(true);
            this.etReply.setFocusableInTouchMode(true);
            this.etReply.requestFocus();
            AndroidUtilities.showKeyboard(this.etReply);
        }
    }

    /* access modifiers changed from: private */
    public void changeSendBtn() {
        if (this.ivSend != null) {
            EditTextBoldCursor editTextBoldCursor = this.etReply;
            if (editTextBoldCursor == null || editTextBoldCursor.getText() == null) {
                this.ivSend.setEnabled(false);
            }
            this.ivSend.setEnabled(!TextUtils.isEmpty(this.etReply.getText().toString().trim()));
        }
    }

    public void onDestroy() {
        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.emojiDidLoad);
        Window window = this.dialogWindow;
        if (window != null) {
            window.setSoftInputMode(16);
        }
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = this.attach;
        if (onGlobalLayoutListener != null) {
            KeyboardUtils.detach(this.mActivity, onGlobalLayoutListener);
        }
        Activity activity = this.mActivity;
        if (activity != null) {
            activity.getWindow().setSoftInputMode(16);
        }
        EmojiView emojiView = this.mEmojiView;
        if (emojiView != null) {
            emojiView.onDestroy();
        }
        HashMap<Long, Editable> hashMap = this.lastUnPostContent;
        if (hashMap != null) {
            hashMap.clear();
            this.lastUnPostContent = null;
        }
    }

    public void didSelectContact(TLRPC.User user) {
        String str;
        show();
        if (user != null && !TextUtils.isEmpty(user.first_name)) {
            String nickName = user.first_name.trim();
            Editable text = this.etReply.getText();
            if (text instanceof SpannableStringBuilder) {
                int index = text.toString().indexOf("@", this.etReply.getSelectionEnd() - 1);
                if (index != -1) {
                    text.delete(index, index + 1);
                }
                AtUserSpan insertAtUserSpan = new AtUserSpan(user.id, nickName, user.username, "@" + nickName, user.access_hash);
                AtUserSpan[] spans = (AtUserSpan[]) text.getSpans(0, text.length(), AtUserSpan.class);
                Arrays.sort(spans, new Comparator(text) {
                    private final /* synthetic */ Editable f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return FcDoReplyDialog.lambda$didSelectContact$3(this.f$0, (AtUserSpan) obj, (AtUserSpan) obj2);
                    }
                });
                for (AtUserSpan result : spans) {
                    if (TextUtils.equals(result.getShowName(), insertAtUserSpan.getShowName())) {
                        if (result.getUserID() == insertAtUserSpan.getUserID()) {
                            insertAtUserSpan.setShowName(result.getShowName());
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append(insertAtUserSpan.getShowName());
                            if (TextUtils.isEmpty(insertAtUserSpan.getUserName())) {
                                str = "";
                            } else {
                                str = SQLBuilder.PARENTHESES_LEFT + insertAtUserSpan.getUserName() + SQLBuilder.PARENTHESES_RIGHT;
                            }
                            sb.append(str);
                            insertAtUserSpan.setShowName(sb.toString());
                        }
                    } else if (result.getUserID() == insertAtUserSpan.getUserID()) {
                        insertAtUserSpan.setShowName(result.getShowName());
                    }
                }
                this.etReply.getText().insert(this.etReply.getSelectionStart(), this.methodContext.newSpannable(insertAtUserSpan)).insert(this.etReply.getSelectionStart(), " ");
            }
        }
    }

    static /* synthetic */ int lambda$didSelectContact$3(Editable text, AtUserSpan o1, AtUserSpan o2) {
        return text.getSpanStart(o1) - text.getSpanStart(o2);
    }

    public void setListener(OnFcDoReplyListener listener2) {
        this.listener = listener2;
    }
}

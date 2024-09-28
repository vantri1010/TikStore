package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.EditTextEmoji;
import im.bclpbkiauv.ui.components.EmojiView;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;

public class EditTextEmoji extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate {
    public static final int STYLE_DIALOG = 1;
    public static final int STYLE_FRAGMENT = 0;
    private int currentStyle;
    private EditTextEmojiDelegate delegate;
    /* access modifiers changed from: private */
    public boolean destroyed;
    /* access modifiers changed from: private */
    public EditTextBoldCursor editText;
    private ImageView emojiButton;
    private int emojiPadding;
    /* access modifiers changed from: private */
    public EmojiView emojiView;
    private boolean emojiViewVisible;
    /* access modifiers changed from: private */
    public int innerTextChange;
    private boolean isPaused = true;
    private int keyboardHeight;
    private int keyboardHeightLand;
    /* access modifiers changed from: private */
    public boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    /* access modifiers changed from: private */
    public Runnable openKeyboardRunnable = new Runnable() {
        public void run() {
            if (!EditTextEmoji.this.destroyed && EditTextEmoji.this.editText != null && EditTextEmoji.this.waitingForKeyboardOpen && !EditTextEmoji.this.keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow && AndroidUtilities.isTablet()) {
                EditTextEmoji.this.editText.requestFocus();
                AndroidUtilities.showKeyboard(EditTextEmoji.this.editText);
                AndroidUtilities.cancelRunOnUIThread(EditTextEmoji.this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(EditTextEmoji.this.openKeyboardRunnable, 100);
            }
        }
    };
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    private boolean showKeyboardOnResume;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    /* access modifiers changed from: private */
    public boolean waitingForKeyboardOpen;

    public interface EditTextEmojiDelegate {
        void onWindowSizeChanged(int i);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public EditTextEmoji(Context context, SizeNotifierFrameLayout parent, BaseFragment fragment, int style) {
        super(context);
        Context context2 = context;
        SizeNotifierFrameLayout sizeNotifierFrameLayout = parent;
        int i = style;
        this.currentStyle = i;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        this.parentFragment = fragment;
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(this);
        AnonymousClass2 r6 = new EditTextBoldCursor(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (EditTextEmoji.this.isPopupShowing() && event.getAction() == 0) {
                    EditTextEmoji.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
                    EditTextEmoji.this.openKeyboardInternal();
                }
                if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                try {
                    return super.onTouchEvent(event);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return false;
                }
            }
        };
        this.editText = r6;
        r6.setTextSize(1, 16.0f);
        this.editText.setImeOptions(C.ENCODING_PCM_MU_LAW);
        this.editText.setInputType(16385);
        EditTextBoldCursor editTextBoldCursor = this.editText;
        editTextBoldCursor.setFocusable(editTextBoldCursor.isEnabled());
        this.editText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.editText.setCursorWidth(1.5f);
        this.editText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        int i2 = 5;
        if (i == 0) {
            this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.editText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(8.0f));
            addView(this.editText, LayoutHelper.createFrame(-1.0f, -2.0f, 19, LocaleController.isRTL ? 11.0f : 0.0f, 1.0f, !LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        } else {
            this.editText.setGravity(19);
            this.editText.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
            this.editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.editText.setBackgroundDrawable((Drawable) null);
            this.editText.setPadding(0, 0, 0, 0);
            addView(this.editText, LayoutHelper.createFrame(-1.0f, -1.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        }
        ImageView imageView = new ImageView(context2);
        this.emojiButton = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
        this.emojiButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (i == 0) {
            this.emojiButton.setPadding(0, 0, 0, AndroidUtilities.dp(7.0f));
            this.emojiButton.setImageResource(R.drawable.smiles_tab_smiles);
            addView(this.emojiButton, LayoutHelper.createFrame(48.0f, 48.0f, (LocaleController.isRTL ? 3 : i2) | 16, 0.0f, 0.0f, 0.0f, 0.0f));
        } else {
            this.emojiButton.setImageResource(R.drawable.input_smile);
            addView(this.emojiButton, LayoutHelper.createFrame(48.0f, 48.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        this.emojiButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                EditTextEmoji.this.lambda$new$0$EditTextEmoji(view);
            }
        });
        this.emojiButton.setContentDescription(LocaleController.getString("Emoji", R.string.Emoji));
    }

    public /* synthetic */ void lambda$new$0$EditTextEmoji(View view) {
        if (this.emojiButton.isEnabled()) {
            if (!isPopupShowing()) {
                boolean z = true;
                showPopup(1);
                EmojiView emojiView2 = this.emojiView;
                if (this.editText.length() <= 0) {
                    z = false;
                }
                emojiView2.onOpen(z);
                this.editText.requestFocus();
                return;
            }
            openKeyboardInternal();
        }
    }

    public void hideEditBackgroup() {
        this.editText.setBackgroundDrawable((Drawable) null);
    }

    public void setSizeNotifierLayout(SizeNotifierFrameLayout layout) {
        this.sizeNotifierLayout = layout;
        layout.setDelegate(this);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        EmojiView emojiView2;
        if (id == NotificationCenter.emojiDidLoad && (emojiView2 = this.emojiView) != null) {
            emojiView2.invalidateViews();
        }
    }

    public void setEnabled(boolean enabled) {
        this.editText.setEnabled(enabled);
        this.emojiButton.setVisibility(enabled ? 0 : 8);
        if (enabled) {
            this.editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(8.0f));
        } else {
            this.editText.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        }
    }

    public void setFocusable(boolean focusable) {
        this.editText.setFocusable(focusable);
    }

    public void hideEmojiView() {
        EmojiView emojiView2;
        if (!this.emojiViewVisible && (emojiView2 = this.emojiView) != null && emojiView2.getVisibility() != 8) {
            this.emojiView.setVisibility(8);
        }
    }

    public void setDelegate(EditTextEmojiDelegate editTextEmojiDelegate) {
        this.delegate = editTextEmojiDelegate;
    }

    public void onPause() {
        this.isPaused = true;
        closeKeyboard();
    }

    public void onResume() {
        this.isPaused = false;
        if (this.showKeyboardOnResume) {
            this.showKeyboardOnResume = false;
            this.editText.requestFocus();
            AndroidUtilities.showKeyboard(this.editText);
            if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                this.waitingForKeyboardOpen = true;
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
            }
        }
    }

    public void onDestroy() {
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.onDestroy();
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setDelegate((SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate) null);
        }
    }

    public void updateColors() {
        if (this.currentStyle == 0) {
            this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.editText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        } else {
            this.editText.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
            this.editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        }
        this.emojiButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.updateColors();
        }
    }

    public void setMaxLines(int value) {
        this.editText.setMaxLines(value);
    }

    public int length() {
        return this.editText.length();
    }

    public void setFilters(InputFilter[] filters) {
        this.editText.setFilters(filters);
    }

    public Editable getText() {
        return this.editText.getText();
    }

    public void setHint(CharSequence hint) {
        this.editText.setHint(hint);
    }

    public void setText(CharSequence text) {
        this.editText.setText(text);
    }

    public void setSelection(int selection) {
        this.editText.setSelection(selection);
    }

    public void hidePopup(boolean byBackButton) {
        if (isPopupShowing()) {
            showPopup(0);
        }
        if (byBackButton) {
            hideEmojiView();
        }
    }

    public void openKeyboard() {
        AndroidUtilities.showKeyboard(this.editText);
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.editText);
    }

    public boolean isPopupShowing() {
        return this.emojiViewVisible;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    /* access modifiers changed from: private */
    public void openKeyboardInternal() {
        showPopup((AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2);
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
        } else if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            this.waitingForKeyboardOpen = true;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
        }
    }

    /* access modifiers changed from: private */
    public void showPopup(int show) {
        if (show == 1) {
            if (this.emojiView == null) {
                createEmojiView();
            }
            this.emojiView.setVisibility(0);
            this.emojiViewVisible = true;
            View currentView = this.emojiView;
            if (this.keyboardHeight <= 0) {
                if (AndroidUtilities.isTablet()) {
                    this.keyboardHeight = AndroidUtilities.dp(150.0f);
                } else {
                    this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(236.0f));
                }
            }
            if (this.keyboardHeightLand <= 0) {
                if (AndroidUtilities.isTablet()) {
                    this.keyboardHeightLand = AndroidUtilities.dp(150.0f);
                } else {
                    this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(236.0f));
                }
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                AndroidUtilities.hideKeyboard(this.editText);
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout != null) {
                this.emojiPadding = currentHeight;
                sizeNotifierFrameLayout.requestLayout();
                this.emojiButton.setImageResource(R.drawable.input_keyboard);
                onWindowSizeChanged();
                return;
            }
            return;
        }
        ImageView imageView = this.emojiButton;
        if (imageView != null) {
            if (this.currentStyle == 0) {
                imageView.setImageResource(R.drawable.smiles_tab_smiles);
            } else {
                imageView.setImageResource(R.drawable.input_smile);
            }
        }
        if (this.emojiView != null) {
            this.emojiViewVisible = false;
            if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                this.emojiView.setVisibility(8);
            }
        }
        if (this.sizeNotifierLayout != null) {
            if (show == 0) {
                this.emojiPadding = 0;
            }
            this.sizeNotifierLayout.requestLayout();
            onWindowSizeChanged();
        }
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        EditTextEmojiDelegate editTextEmojiDelegate = this.delegate;
        if (editTextEmojiDelegate != null) {
            editTextEmojiDelegate.onWindowSizeChanged(size);
        }
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            EmojiView emojiView2 = new EmojiView(false, false, getContext(), false, (TLRPC.ChatFull) null);
            this.emojiView = emojiView2;
            emojiView2.setVisibility(8);
            if (AndroidUtilities.isTablet()) {
                this.emojiView.setForseMultiwindowLayout(true);
            }
            this.emojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
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
                    if (EditTextEmoji.this.editText.length() == 0) {
                        return false;
                    }
                    EditTextEmoji.this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onEmojiSelected(String symbol) {
                    int i = EditTextEmoji.this.editText.getSelectionEnd();
                    if (i < 0) {
                        i = 0;
                    }
                    try {
                        int unused = EditTextEmoji.this.innerTextChange = 2;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, EditTextEmoji.this.editText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        EditTextEmoji.this.editText.setText(EditTextEmoji.this.editText.getText().insert(i, localCharSequence));
                        int j = localCharSequence.length() + i;
                        EditTextEmoji.this.editText.setSelection(j, j);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    } catch (Throwable th) {
                        int unused2 = EditTextEmoji.this.innerTextChange = 0;
                        throw th;
                    }
                    int unused3 = EditTextEmoji.this.innerTextChange = 0;
                }

                public void onClearEmojiRecent() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditTextEmoji.this.getContext());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("ClearRecentEmoji", R.string.ClearRecentEmoji));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            EditTextEmoji.AnonymousClass3.this.lambda$onClearEmojiRecent$0$EditTextEmoji$3(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    if (EditTextEmoji.this.parentFragment != null) {
                        EditTextEmoji.this.parentFragment.showDialog(builder.create());
                    } else {
                        builder.show();
                    }
                }

                public /* synthetic */ void lambda$onClearEmojiRecent$0$EditTextEmoji$3(DialogInterface dialogInterface, int i) {
                    EditTextEmoji.this.emojiView.clearRecentEmoji();
                }
            });
            this.sizeNotifierLayout.addView(this.emojiView);
        }
    }

    public boolean isPopupView(View view) {
        return view == this.emojiView;
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z;
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            int newHeight = isWidthGreater ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            if (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == newHeight)) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                this.emojiView.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }
        if (this.lastSizeChangeValue1 == height && this.lastSizeChangeValue2 == isWidthGreater) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = height;
        this.lastSizeChangeValue2 = isWidthGreater;
        boolean oldValue = this.keyboardVisible;
        boolean z2 = height > 0;
        this.keyboardVisible = z2;
        if (z2 && isPopupShowing()) {
            showPopup(0);
        }
        if (this.emojiPadding != 0 && !(z = this.keyboardVisible) && z != oldValue && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public EditTextBoldCursor getEditText() {
        return this.editText;
    }
}

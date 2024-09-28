package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.CheckBox2;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgress2;

public class SharedAudioCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(29.0f);
    private int hasMiniProgress;
    private boolean miniButtonPressed;
    private int miniButtonState;
    private boolean needDivider;
    private RadialProgress2 radialProgress;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(9.0f);

    public SharedAudioCell(Context context) {
        super(context);
        setFocusable(true);
        RadialProgress2 radialProgress2 = new RadialProgress2(this);
        this.radialProgress = radialProgress2;
        radialProgress2.setColors(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setWillNotDraw(false);
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        this.checkBox.setDrawUnchecked(false);
        int i = 3;
        this.checkBox.setDrawBackgroundAsArc(3);
        addView(this.checkBox, LayoutHelper.createFrame(24.0f, 24.0f, (LocaleController.isRTL ? 5 : i) | 48, LocaleController.isRTL ? 0.0f : 38.0f, 32.0f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.descriptionLayout = null;
        this.titleLayout = null;
        int maxWidth = (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(28.0f);
        try {
            String title = this.currentMessageObject.getMusicTitle();
            this.titleLayout = new StaticLayout(TextUtils.ellipsize(title.replace(10, ' '), Theme.chat_contextResult_titleTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_titleTextPaint.measureText(title)), maxWidth), TextUtils.TruncateAt.END), Theme.chat_contextResult_titleTextPaint, maxWidth + AndroidUtilities.dp(4.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            String author = this.currentMessageObject.getMusicAuthor();
            this.descriptionLayout = new StaticLayout(TextUtils.ellipsize(author.replace(10, ' '), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(author)), maxWidth), TextUtils.TruncateAt.END), Theme.chat_contextResult_descriptionTextPaint, maxWidth + AndroidUtilities.dp(4.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(56.0f) + (this.needDivider ? 1 : 0));
        int x = LocaleController.isRTL ? (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(8.0f)) - AndroidUtilities.dp(52.0f) : AndroidUtilities.dp(8.0f);
        RadialProgress2 radialProgress2 = this.radialProgress;
        int dp = AndroidUtilities.dp(4.0f) + x;
        this.buttonX = dp;
        int dp2 = AndroidUtilities.dp(6.0f);
        this.buttonY = dp2;
        radialProgress2.setProgressRect(dp, dp2, AndroidUtilities.dp(48.0f) + x, AndroidUtilities.dp(50.0f));
        measureChildWithMargins(this.checkBox, widthMeasureSpec, 0, heightMeasureSpec, 0);
    }

    public void setMessageObject(MessageObject messageObject, boolean divider) {
        this.needDivider = divider;
        this.currentMessageObject = messageObject;
        TLRPC.Document document = messageObject.getDocument();
        TLRPC.PhotoSize thumb = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90) : null;
        if (thumb instanceof TLRPC.TL_photoSize) {
            this.radialProgress.setImageOverlay(thumb, document, messageObject);
        } else {
            String artworkUrl = messageObject.getArtworkUrl(true);
            if (!TextUtils.isEmpty(artworkUrl)) {
                this.radialProgress.setImageOverlay(artworkUrl);
            } else {
                this.radialProgress.setImageOverlay((TLRPC.PhotoSize) null, (TLRPC.Document) null, (Object) null);
            }
        }
        updateButtonState(false, false);
        requestLayout();
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.radialProgress.onAttachedToWindow();
        updateButtonState(false, false);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        this.radialProgress.onDetachedFromWindow();
    }

    public MessageObject getMessage() {
        return this.currentMessageObject;
    }

    public void initStreamingIcons() {
        this.radialProgress.initMiniIcons();
    }

    private boolean checkAudioMotionEvent(MotionEvent event) {
        boolean z;
        int x = (int) event.getX();
        int y = (int) event.getY();
        int side = AndroidUtilities.dp(36.0f);
        boolean area = false;
        if (this.miniButtonState >= 0) {
            int offset = AndroidUtilities.dp(27.0f);
            int i = this.buttonX;
            if (x >= i + offset && x <= i + offset + side) {
                int i2 = this.buttonY;
                if (y >= i2 + offset && y <= i2 + offset + side) {
                    z = true;
                    area = z;
                }
            }
            z = false;
            area = z;
        }
        if (event.getAction() == 0) {
            if (!area) {
                return false;
            }
            this.miniButtonPressed = true;
            this.radialProgress.setPressed(true, true);
            invalidate();
            return true;
        } else if (!this.miniButtonPressed) {
            return false;
        } else {
            if (event.getAction() == 1) {
                this.miniButtonPressed = false;
                playSoundEffect(0);
                didPressedMiniButton(true);
                invalidate();
            } else if (event.getAction() == 3) {
                this.miniButtonPressed = false;
                invalidate();
            } else if (event.getAction() == 2 && !area) {
                this.miniButtonPressed = false;
                invalidate();
            }
            this.radialProgress.setPressed(this.miniButtonPressed, true);
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(event);
        }
        boolean result = checkAudioMotionEvent(event);
        if (event.getAction() != 3) {
            return result;
        }
        this.miniButtonPressed = false;
        this.buttonPressed = false;
        this.radialProgress.setPressed(false, false);
        this.radialProgress.setPressed(this.miniButtonPressed, true);
        return false;
    }

    private void didPressedMiniButton(boolean animated) {
        int i = this.miniButtonState;
        if (i == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 1) {
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        }
    }

    public void didPressedButton() {
        int i = this.buttonState;
        if (i == 0) {
            if (this.miniButtonState == 0) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            }
            if (needPlayMessage(this.currentMessageObject)) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
                }
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        } else if (i == 1) {
            if (MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        } else if (i == 2) {
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 4;
            this.radialProgress.setIcon(getIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 4) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.buttonState = 2;
            this.radialProgress.setIcon(getIconForCurrentState(), false, true);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
        this.radialProgress.draw(canvas);
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    private int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        if (i == 0) {
            return 2;
        }
        return 3;
    }

    private int getIconForCurrentState() {
        int i = this.buttonState;
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        if (i == 4) {
            return 3;
        }
        return 0;
    }

    public void updateButtonState(boolean ifSame, boolean animated) {
        String fileName = this.currentMessageObject.getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            boolean fileExists = this.currentMessageObject.attachPathExists || this.currentMessageObject.mediaExists;
            if (!SharedConfig.streamMedia || !this.currentMessageObject.isMusic() || ((int) this.currentMessageObject.getDialogId()) == 0) {
                this.hasMiniProgress = 0;
                this.miniButtonState = -1;
            } else {
                this.hasMiniProgress = fileExists ? 1 : 2;
                fileExists = true;
            }
            if (this.hasMiniProgress != 0) {
                this.radialProgress.setMiniProgressBackgroundColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outLoader : Theme.key_chat_inLoader));
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                if (this.hasMiniProgress == 1) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    this.miniButtonState = -1;
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), ifSame, animated);
                    return;
                }
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.miniButtonState = 0;
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), ifSame, animated);
                    return;
                }
                this.miniButtonState = 1;
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), ifSame, animated);
                Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress != null) {
                    this.radialProgress.setProgress(progress.floatValue(), animated);
                } else {
                    this.radialProgress.setProgress(0.0f, animated);
                }
            } else if (fileExists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                boolean playing2 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing2 || (playing2 && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, animated);
                this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                invalidate();
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                } else {
                    this.buttonState = 4;
                    Float progress2 = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress2 != null) {
                        this.radialProgress.setProgress(progress2.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                }
                invalidate();
            }
        }
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        if (this.hasMiniProgress != 0) {
            if (this.miniButtonState != 1) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 4) {
            updateButtonState(false, true);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    /* access modifiers changed from: protected */
    public boolean needPlayMessage(MessageObject messageObject) {
        return false;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.currentMessageObject.isMusic()) {
            info.setText(LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, this.currentMessageObject.getMusicAuthor(), this.currentMessageObject.getMusicTitle()));
        } else {
            info.setText(this.titleLayout.getText() + ", " + this.descriptionLayout.getText());
        }
        if (this.checkBox.isChecked()) {
            info.setCheckable(true);
            info.setChecked(true);
        }
    }
}

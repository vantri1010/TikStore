package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class StickerEmojiCell extends FrameLayout {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private float alpha = 1.0f;
    private boolean changingAlpha;
    private int currentAccount = UserConfig.selectedAccount;
    private TextView emojiTextView;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private boolean recent;
    private float scale;
    private boolean scaled;
    private TLRPC.Document sticker;
    private long time;

    public StickerEmojiCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
        TextView textView = new TextView(context);
        this.emojiTextView = textView;
        textView.setTextSize(1, 14.0f);
        addView(this.emojiTextView, LayoutHelper.createFrame(28, 28, 85));
        setFocusable(true);
    }

    public TLRPC.Document getSticker() {
        return this.sticker;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public boolean isRecent() {
        return this.recent;
    }

    public void setRecent(boolean value) {
        this.recent = value;
    }

    public void setSticker(TLRPC.Document document, Object parent, boolean showEmoji) {
        setSticker(document, parent, (String) null, showEmoji);
    }

    public void setSticker(TLRPC.Document document, Object parent, String emoji, boolean showEmoji) {
        TLRPC.Document document2 = document;
        String str = emoji;
        if (document2 != null) {
            this.sticker = document2;
            this.parentObject = parent;
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
            if (MessageObject.canAutoplayAnimatedSticker(document)) {
                if (thumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "80_80", ImageLocation.getForDocument(thumb, document2), (String) null, 0, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(document), "80_80", (String) null, (Drawable) null, this.parentObject);
                }
            } else if (thumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(thumb, document2), (String) null, "webp", (Drawable) null, this.parentObject);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(document), (String) null, "webp", (Drawable) null, this.parentObject);
            }
            if (str != null) {
                TextView textView = this.emojiTextView;
                textView.setText(Emoji.replaceEmoji(str, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                this.emojiTextView.setVisibility(0);
            } else if (showEmoji) {
                boolean set = false;
                int a = 0;
                while (true) {
                    if (a >= document2.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = document2.attributes.get(a);
                    if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                        a++;
                    } else if (attribute.alt != null && attribute.alt.length() > 0) {
                        this.emojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                        set = true;
                    }
                }
                if (!set) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                }
                this.emojiTextView.setVisibility(0);
            } else {
                this.emojiTextView.setVisibility(4);
            }
        } else {
            Object obj = parent;
        }
    }

    public void disable() {
        this.changingAlpha = true;
        this.alpha = 0.5f;
        this.time = 0;
        this.imageView.getImageReceiver().setAlpha(this.alpha);
        this.imageView.invalidate();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean isDisabled() {
        return this.changingAlpha;
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void invalidate() {
        this.emojiTextView.invalidate();
        super.invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && (this.changingAlpha || ((this.scaled && this.scale != 0.8f) || (!this.scaled && this.scale != 1.0f)))) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.changingAlpha) {
                long j = this.time + dt;
                this.time = j;
                if (j > 1050) {
                    this.time = 1050;
                }
                float interpolation = (interpolator.getInterpolation(((float) this.time) / 1050.0f) * 0.5f) + 0.5f;
                this.alpha = interpolation;
                if (interpolation >= 1.0f) {
                    this.changingAlpha = false;
                    this.alpha = 1.0f;
                }
                this.imageView.getImageReceiver().setAlpha(this.alpha);
            } else {
                if (this.scaled) {
                    float f = this.scale;
                    if (f != 0.8f) {
                        float f2 = f - (((float) dt) / 400.0f);
                        this.scale = f2;
                        if (f2 < 0.8f) {
                            this.scale = 0.8f;
                        }
                    }
                }
                float f3 = this.scale + (((float) dt) / 400.0f);
                this.scale = f3;
                if (f3 > 1.0f) {
                    this.scale = 1.0f;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return result;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        String descr = LocaleController.getString("AttachSticker", R.string.AttachSticker);
        int a = 0;
        while (true) {
            if (a >= this.sticker.attributes.size()) {
                break;
            }
            TLRPC.DocumentAttribute attribute = this.sticker.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.alt != null && attribute.alt.length() > 0) {
                this.emojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                descr = attribute.alt + " " + descr;
            }
        }
        info.setContentDescription(descr);
        info.setEnabled(true);
    }
}

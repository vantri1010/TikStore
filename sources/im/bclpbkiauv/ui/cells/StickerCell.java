package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class StickerCell extends FrameLayout {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private boolean clearsInputField;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private float scale;
    private boolean scaled;
    private TLRPC.Document sticker;
    private long time = 0;

    public StickerCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(66.0f, 66.0f, 1, 0.0f, 5.0f, 0.0f, 0.0f));
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(76.0f) + getPaddingLeft() + getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f), 1073741824));
    }

    public void setPressed(boolean pressed) {
        if (this.imageView.getImageReceiver().getPressed() != pressed) {
            this.imageView.getImageReceiver().setPressed(pressed);
            this.imageView.invalidate();
        }
        super.setPressed(pressed);
    }

    public void setClearsInputField(boolean value) {
        this.clearsInputField = value;
    }

    public boolean isClearsInputField() {
        return this.clearsInputField;
    }

    public void setSticker(TLRPC.Document document, Object parent, int side) {
        TLRPC.Document document2 = document;
        int i = side;
        this.parentObject = parent;
        if (document2 != null) {
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
            if (!MessageObject.canAutoplayAnimatedSticker(document)) {
                this.imageView.setImage(ImageLocation.getForDocument(thumb, document2), (String) null, "webp", (Drawable) null, this.parentObject);
            } else if (thumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(document), "80_80", ImageLocation.getForDocument(thumb, document2), (String) null, 0, this.parentObject);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(document), "80_80", (String) null, (Drawable) null, this.parentObject);
            }
        }
        this.sticker = document2;
        if (i == -1) {
            setBackgroundResource(R.drawable.stickers_back_left);
            setPadding(AndroidUtilities.dp(7.0f), 0, 0, 0);
        } else if (i == 0) {
            setBackgroundResource(R.drawable.stickers_back_center);
            setPadding(0, 0, 0, 0);
        } else if (i == 1) {
            setBackgroundResource(R.drawable.stickers_back_right);
            setPadding(0, 0, AndroidUtilities.dp(7.0f), 0);
        } else if (i == 2) {
            setBackgroundResource(R.drawable.stickers_back_all);
            setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        }
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(230);
            background.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_stickersHintPanel), PorterDuff.Mode.MULTIPLY));
        }
    }

    public TLRPC.Document getSticker() {
        return this.sticker;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && ((this.scaled && this.scale != 0.8f) || (!this.scaled && this.scale != 1.0f))) {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.scaled) {
                float f = this.scale;
                if (f != 0.8f) {
                    float f2 = f - (((float) dt) / 400.0f);
                    this.scale = f2;
                    if (f2 < 0.8f) {
                        this.scale = 0.8f;
                    }
                    this.imageView.setScaleX(this.scale);
                    this.imageView.setScaleY(this.scale);
                    this.imageView.invalidate();
                    invalidate();
                }
            }
            float f3 = this.scale + (((float) dt) / 400.0f);
            this.scale = f3;
            if (f3 > 1.0f) {
                this.scale = 1.0f;
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
        if (this.sticker != null) {
            String emoji = null;
            for (int a = 0; a < this.sticker.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = this.sticker.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                    emoji = (attribute.alt == null || attribute.alt.length() <= 0) ? null : attribute.alt;
                }
            }
            if (emoji != null) {
                info.setText(emoji + " " + LocaleController.getString("AttachSticker", R.string.AttachSticker));
            } else {
                info.setText(LocaleController.getString("AttachSticker", R.string.AttachSticker));
            }
            info.setEnabled(true);
        }
    }
}

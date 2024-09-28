package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.Switch;

public class ArchivedStickerSetCell extends FrameLayout {
    private Switch checkBox;
    private BackupImageView imageView;
    private boolean needDivider;
    private Switch.OnCheckedChangeListener onCheckedChangeListener;
    private Rect rect = new Rect();
    private TLRPC.StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;

    public ArchivedStickerSetCell(Context context, boolean needCheckBox) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, LocaleController.isRTL ? 5 : 3, 71.0f, 10.0f, needCheckBox ? 71.0f : 21.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2.0f, -2.0f, LocaleController.isRTL ? 5 : 3, 71.0f, 35.0f, needCheckBox ? 71.0f : 21.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(48.0f, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        if (needCheckBox) {
            Switch switchR = new Switch(context);
            this.checkBox = switchR;
            switchR.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
            addView(this.checkBox, LayoutHelper.createFrame(37.0f, 40.0f, (LocaleController.isRTL ? 3 : i) | 16, 16.0f, 0.0f, 16.0f, 0.0f));
        }
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public Switch getCheckBox() {
        return this.checkBox;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setStickersSet(TLRPC.StickerSetCovered set, boolean divider) {
        TLRPC.Document sticker;
        TLObject object;
        ImageLocation imageLocation;
        this.needDivider = divider;
        this.stickersSet = set;
        setWillNotDraw(!divider);
        this.textView.setText(this.stickersSet.set.title);
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", set.set.count));
        if (set.cover != null) {
            sticker = set.cover;
        } else if (!set.covers.isEmpty()) {
            sticker = set.covers.get(0);
        } else {
            sticker = null;
        }
        if (sticker != null) {
            if (set.set.thumb instanceof TLRPC.TL_photoSize) {
                object = set.set.thumb;
            } else {
                object = sticker;
            }
            if (object instanceof TLRPC.Document) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90), sticker);
            } else {
                imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) object, sticker);
            }
            if ((object instanceof TLRPC.Document) && MessageObject.isAnimatedStickerDocument(sticker)) {
                this.imageView.setImage(ImageLocation.getForDocument(sticker), "50_50", imageLocation, (String) null, 0, set);
            } else if (imageLocation == null || !imageLocation.lottieAnimation) {
                this.imageView.setImage(imageLocation, "50_50", "webp", (Drawable) null, (Object) set);
            } else {
                this.imageView.setImage(imageLocation, "50_50", "tgs", (Drawable) null, (Object) set);
            }
        } else {
            this.imageView.setImage((ImageLocation) null, (String) null, "webp", (Drawable) null, (Object) set);
        }
    }

    public void setOnCheckClick(Switch.OnCheckedChangeListener listener) {
        Switch switchR = this.checkBox;
        this.onCheckedChangeListener = listener;
        switchR.setOnCheckedChangeListener(listener);
        this.checkBox.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArchivedStickerSetCell.this.lambda$setOnCheckClick$0$ArchivedStickerSetCell(view);
            }
        });
    }

    public /* synthetic */ void lambda$setOnCheckClick$0$ArchivedStickerSetCell(View v) {
        Switch switchR = this.checkBox;
        switchR.setChecked(!switchR.isChecked(), true);
    }

    public void setChecked(boolean checked) {
        this.checkBox.setOnCheckedChangeListener((Switch.OnCheckedChangeListener) null);
        this.checkBox.setChecked(checked, true);
        this.checkBox.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    public boolean isChecked() {
        Switch switchR = this.checkBox;
        return switchR != null && switchR.isChecked();
    }

    public TLRPC.StickerSetCovered getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent event) {
        Switch switchR = this.checkBox;
        if (switchR != null) {
            switchR.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), (int) event.getY())) {
                event.offsetLocation(-this.checkBox.getX(), -this.checkBox.getY());
                return this.checkBox.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}

package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;

public class StickerSetCell extends FrameLayout {
    private BackupImageView imageView;
    private boolean needDivider;
    private ImageView optionsButton;
    private RadialProgressView progressView;
    private Rect rect = new Rect();
    private TLRPC.TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StickerSetCell(Context context, int option) {
        super(context);
        Context context2 = context;
        int i = option;
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i2 = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 9.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2.0f, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 40.0f : 71.0f, 32.0f, LocaleController.isRTL ? 71.0f : 40.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 13.0f, 9.0f, LocaleController.isRTL ? 13.0f : 0.0f, 0.0f));
        if (i == 2) {
            RadialProgressView radialProgressView = new RadialProgressView(getContext());
            this.progressView = radialProgressView;
            radialProgressView.setProgressColor(Theme.getColor(Theme.key_dialogProgressCircle));
            this.progressView.setSize(AndroidUtilities.dp(30.0f));
            addView(this.progressView, LayoutHelper.createFrame(48.0f, 48.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 5.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        } else if (i != 0) {
            ImageView imageView2 = new ImageView(context2);
            this.optionsButton = imageView2;
            int i3 = 0;
            imageView2.setFocusable(false);
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            if (i == 1) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(R.drawable.msg_actions);
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? 3 : i2) | 16));
            } else if (i == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.SRC_IN));
                this.optionsButton.setImageResource(R.mipmap.ic_selected);
                addView(this.optionsButton, LayoutHelper.createFrame(40.0f, 40.0f, (LocaleController.isRTL ? 3 : i2) | 48, (float) (LocaleController.isRTL ? 10 : 0), 9.0f, (float) (!LocaleController.isRTL ? 10 : i3), 0.0f));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setText(String title, String subtitle, int icon, boolean divider) {
        this.needDivider = divider;
        this.stickersSet = null;
        this.textView.setText(title);
        this.valueTextView.setText(subtitle);
        if (TextUtils.isEmpty(subtitle)) {
            this.textView.setTranslationY((float) AndroidUtilities.dp(10.0f));
        } else {
            this.textView.setTranslationY(0.0f);
        }
        if (icon != 0) {
            this.imageView.setImageResource(icon, Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon));
            this.imageView.setVisibility(0);
            RadialProgressView radialProgressView = this.progressView;
            if (radialProgressView != null) {
                radialProgressView.setVisibility(4);
                return;
            }
            return;
        }
        this.imageView.setVisibility(4);
        RadialProgressView radialProgressView2 = this.progressView;
        if (radialProgressView2 != null) {
            radialProgressView2.setVisibility(0);
        }
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setStickersSet(im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet r13, boolean r14) {
        /*
            r12 = this;
            r12.needDivider = r14
            r12.stickersSet = r13
            im.bclpbkiauv.ui.components.BackupImageView r0 = r12.imageView
            r1 = 0
            r0.setVisibility(r1)
            im.bclpbkiauv.ui.components.RadialProgressView r0 = r12.progressView
            if (r0 == 0) goto L_0x0012
            r2 = 4
            r0.setVisibility(r2)
        L_0x0012:
            android.widget.TextView r0 = r12.textView
            r2 = 0
            r0.setTranslationY(r2)
            android.widget.TextView r0 = r12.textView
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r2 = r12.stickersSet
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r2 = r2.set
            java.lang.String r2 = r2.title
            r0.setText(r2)
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r0 = r12.stickersSet
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r0 = r0.set
            boolean r0 = r0.archived
            if (r0 == 0) goto L_0x003d
            android.widget.TextView r0 = r12.textView
            r2 = 1056964608(0x3f000000, float:0.5)
            r0.setAlpha(r2)
            android.widget.TextView r0 = r12.valueTextView
            r0.setAlpha(r2)
            im.bclpbkiauv.ui.components.BackupImageView r0 = r12.imageView
            r0.setAlpha(r2)
            goto L_0x004e
        L_0x003d:
            android.widget.TextView r0 = r12.textView
            r2 = 1065353216(0x3f800000, float:1.0)
            r0.setAlpha(r2)
            android.widget.TextView r0 = r12.valueTextView
            r0.setAlpha(r2)
            im.bclpbkiauv.ui.components.BackupImageView r0 = r12.imageView
            r0.setAlpha(r2)
        L_0x004e:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r0 = r13.documents
            java.lang.String r2 = "Stickers"
            if (r0 == 0) goto L_0x00ce
            boolean r3 = r0.isEmpty()
            if (r3 != 0) goto L_0x00ce
            android.widget.TextView r3 = r12.valueTextView
            int r4 = r0.size()
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r2, r4)
            r3.setText(r2)
            java.lang.Object r1 = r0.get(r1)
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = (im.bclpbkiauv.tgnet.TLRPC.Document) r1
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r2 = r13.set
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r2 = r2.thumb
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photoSize
            if (r2 == 0) goto L_0x007a
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r2 = r13.set
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r2 = r2.thumb
            goto L_0x007b
        L_0x007a:
            r2 = r1
        L_0x007b:
            boolean r3 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.Document
            if (r3 == 0) goto L_0x008c
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r3 = r1.thumbs
            r4 = 90
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4)
            im.bclpbkiauv.messenger.ImageLocation r3 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r3, r1)
            goto L_0x0094
        L_0x008c:
            r3 = r2
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r3
            im.bclpbkiauv.messenger.ImageLocation r4 = im.bclpbkiauv.messenger.ImageLocation.getForSticker(r3, r1)
            r3 = r4
        L_0x0094:
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.Document
            if (r4 == 0) goto L_0x00ae
            boolean r4 = im.bclpbkiauv.messenger.MessageObject.isAnimatedStickerDocument(r1)
            if (r4 == 0) goto L_0x00ae
            im.bclpbkiauv.ui.components.BackupImageView r5 = r12.imageView
            im.bclpbkiauv.messenger.ImageLocation r6 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r1)
            r9 = 0
            r10 = 0
            java.lang.String r7 = "50_50"
            r8 = r3
            r11 = r13
            r5.setImage(r6, r7, r8, r9, r10, r11)
            goto L_0x00cd
        L_0x00ae:
            if (r3 == 0) goto L_0x00c1
            boolean r4 = r3.lottieAnimation
            if (r4 == 0) goto L_0x00c1
            im.bclpbkiauv.ui.components.BackupImageView r5 = r12.imageView
            r9 = 0
            java.lang.String r7 = "50_50"
            java.lang.String r8 = "tgs"
            r6 = r3
            r10 = r13
            r5.setImage((im.bclpbkiauv.messenger.ImageLocation) r6, (java.lang.String) r7, (java.lang.String) r8, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r10)
            goto L_0x00cd
        L_0x00c1:
            im.bclpbkiauv.ui.components.BackupImageView r5 = r12.imageView
            r9 = 0
            java.lang.String r7 = "50_50"
            java.lang.String r8 = "webp"
            r6 = r3
            r10 = r13
            r5.setImage((im.bclpbkiauv.messenger.ImageLocation) r6, (java.lang.String) r7, (java.lang.String) r8, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r10)
        L_0x00cd:
            goto L_0x00d7
        L_0x00ce:
            android.widget.TextView r3 = r12.valueTextView
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r2, r1)
            r3.setText(r1)
        L_0x00d7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.StickerSetCell.setStickersSet(im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet, boolean):void");
    }

    public void setChecked(boolean checked) {
        ImageView imageView2 = this.optionsButton;
        if (imageView2 != null) {
            imageView2.setVisibility(checked ? 0 : 4);
        }
    }

    public void setOnOptionsClick(View.OnClickListener listener) {
        ImageView imageView2 = this.optionsButton;
        if (imageView2 != null) {
            imageView2.setOnClickListener(listener);
        }
    }

    public TLRPC.TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    public boolean onTouchEvent(MotionEvent event) {
        ImageView imageView2;
        if (!(Build.VERSION.SDK_INT < 21 || getBackground() == null || (imageView2 = this.optionsButton) == null)) {
            imageView2.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
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

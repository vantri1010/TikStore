package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.ColorSpanUnderline;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class FeaturedStickerSetInfoCell extends FrameLayout {
    private TextView addButton;
    private Drawable addDrawable = Theme.createSimpleSelectorRoundRectDrawable((float) AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
    /* access modifiers changed from: private */
    public int angle;
    /* access modifiers changed from: private */
    public Paint botProgressPaint;
    private int currentAccount = UserConfig.selectedAccount;
    private Drawable delDrawable = Theme.createSimpleSelectorRoundRectDrawable((float) AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_featuredStickers_delButton), Theme.getColor(Theme.key_featuredStickers_delButtonPressed));
    /* access modifiers changed from: private */
    public boolean drawProgress;
    private boolean hasOnClick;
    private TextView infoTextView;
    private boolean isInstalled;
    private boolean isUnread;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    private TextView nameTextView;
    private Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public float progressAlpha;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    private TLRPC.StickerSetCovered set;

    public FeaturedStickerSetInfoCell(Context context, int left) {
        super(context);
        Paint paint2 = new Paint(1);
        this.botProgressPaint = paint2;
        paint2.setColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
        this.botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.botProgressPaint.setStyle(Paint.Style.STROKE);
        this.botProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelTrendingTitle));
        this.nameTextView.setTextSize(1, 17.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        addView(this.nameTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, (float) left, 8.0f, 40.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.infoTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelTrendingDescription));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.infoTextView.setSingleLine(true);
        addView(this.infoTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, (float) left, 30.0f, 100.0f, 0.0f));
        AnonymousClass1 r0 = new TextView(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (FeaturedStickerSetInfoCell.this.drawProgress || (!FeaturedStickerSetInfoCell.this.drawProgress && FeaturedStickerSetInfoCell.this.progressAlpha != 0.0f)) {
                    FeaturedStickerSetInfoCell.this.botProgressPaint.setAlpha(Math.min(255, (int) (FeaturedStickerSetInfoCell.this.progressAlpha * 255.0f)));
                    int x = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                    FeaturedStickerSetInfoCell.this.rect.set((float) x, (float) AndroidUtilities.dp(3.0f), (float) (AndroidUtilities.dp(8.0f) + x), (float) AndroidUtilities.dp(11.0f));
                    canvas.drawArc(FeaturedStickerSetInfoCell.this.rect, (float) FeaturedStickerSetInfoCell.this.angle, 220.0f, false, FeaturedStickerSetInfoCell.this.botProgressPaint);
                    invalidate(((int) FeaturedStickerSetInfoCell.this.rect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetInfoCell.this.rect.bottom) + AndroidUtilities.dp(2.0f));
                    long newTime = System.currentTimeMillis();
                    if (Math.abs(FeaturedStickerSetInfoCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                        long delta = newTime - FeaturedStickerSetInfoCell.this.lastUpdateTime;
                        FeaturedStickerSetInfoCell featuredStickerSetInfoCell = FeaturedStickerSetInfoCell.this;
                        int unused = featuredStickerSetInfoCell.angle = (int) (((float) featuredStickerSetInfoCell.angle) + (((float) (360 * delta)) / 2000.0f));
                        FeaturedStickerSetInfoCell featuredStickerSetInfoCell2 = FeaturedStickerSetInfoCell.this;
                        int unused2 = featuredStickerSetInfoCell2.angle = featuredStickerSetInfoCell2.angle - ((FeaturedStickerSetInfoCell.this.angle / 360) * 360);
                        if (FeaturedStickerSetInfoCell.this.drawProgress) {
                            if (FeaturedStickerSetInfoCell.this.progressAlpha < 1.0f) {
                                FeaturedStickerSetInfoCell featuredStickerSetInfoCell3 = FeaturedStickerSetInfoCell.this;
                                float unused3 = featuredStickerSetInfoCell3.progressAlpha = featuredStickerSetInfoCell3.progressAlpha + (((float) delta) / 200.0f);
                                if (FeaturedStickerSetInfoCell.this.progressAlpha > 1.0f) {
                                    float unused4 = FeaturedStickerSetInfoCell.this.progressAlpha = 1.0f;
                                }
                            }
                        } else if (FeaturedStickerSetInfoCell.this.progressAlpha > 0.0f) {
                            FeaturedStickerSetInfoCell featuredStickerSetInfoCell4 = FeaturedStickerSetInfoCell.this;
                            float unused5 = featuredStickerSetInfoCell4.progressAlpha = featuredStickerSetInfoCell4.progressAlpha - (((float) delta) / 200.0f);
                            if (FeaturedStickerSetInfoCell.this.progressAlpha < 0.0f) {
                                float unused6 = FeaturedStickerSetInfoCell.this.progressAlpha = 0.0f;
                            }
                        }
                    }
                    long unused7 = FeaturedStickerSetInfoCell.this.lastUpdateTime = newTime;
                    invalidate();
                }
            }
        };
        this.addButton = r0;
        r0.setGravity(17);
        this.addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.addButton, LayoutHelper.createFrame(-2.0f, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), 1073741824));
        measureChildWithMargins(this.nameTextView, widthMeasureSpec, this.addButton.getMeasuredWidth(), heightMeasureSpec, 0);
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.hasOnClick = true;
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread) {
        setStickerSet(stickerSet, unread, 0, 0);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSet, boolean unread, int index, int searchLength) {
        this.lastUpdateTime = System.currentTimeMillis();
        if (searchLength != 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(stickerSet.set.title);
            try {
                builder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + searchLength, 33);
            } catch (Exception e) {
            }
            this.nameTextView.setText(builder);
        } else {
            this.nameTextView.setText(stickerSet.set.title);
        }
        this.infoTextView.setText(LocaleController.formatPluralString("Stickers", stickerSet.set.count));
        this.isUnread = unread;
        if (this.hasOnClick) {
            this.addButton.setVisibility(0);
            boolean isStickerPackInstalled = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSet.set.id);
            this.isInstalled = isStickerPackInstalled;
            if (isStickerPackInstalled) {
                this.addButton.setBackgroundDrawable(this.delDrawable);
                this.addButton.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
            } else {
                this.addButton.setBackgroundDrawable(this.addDrawable);
                this.addButton.setText(LocaleController.getString("Add", R.string.Add));
            }
            this.addButton.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        } else {
            this.addButton.setVisibility(8);
        }
        this.set = stickerSet;
    }

    public void setUrl(CharSequence text, int searchLength) {
        if (text != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            try {
                builder.setSpan(new ColorSpanUnderline(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), 0, searchLength, 33);
                builder.setSpan(new ColorSpanUnderline(Theme.getColor(Theme.key_chat_emojiPanelTrendingDescription)), searchLength, text.length(), 33);
            } catch (Exception e) {
            }
            this.infoTextView.setText(builder);
        }
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    public void setDrawProgress(boolean value) {
        this.drawProgress = value;
        this.lastUpdateTime = System.currentTimeMillis();
        this.addButton.invalidate();
    }

    public TLRPC.StickerSetCovered getStickerSet() {
        return this.set;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.isUnread) {
            this.paint.setColor(Theme.getColor(Theme.key_featuredStickers_unread));
            canvas.drawCircle((float) (this.nameTextView.getRight() + AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(20.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
        }
    }
}

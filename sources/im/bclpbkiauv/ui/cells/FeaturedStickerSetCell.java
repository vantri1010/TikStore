package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hviews.MryAlphaImageView;

public class FeaturedStickerSetCell extends FrameLayout {
    /* access modifiers changed from: private */
    public int angle;
    /* access modifiers changed from: private */
    public ImageView checkImage;
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public AnimatorSet currentAnimation;
    /* access modifiers changed from: private */
    public boolean drawProgress;
    private BackupImageView imageView;
    private boolean isInstalled;
    /* access modifiers changed from: private */
    public MryAlphaImageView ivAdd;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public float progressAlpha;
    /* access modifiers changed from: private */
    public Paint progressPaint;
    /* access modifiers changed from: private */
    public RectF progressRect = new RectF();
    private Rect rect = new Rect();
    private TLRPC.StickerSetCovered stickersSet;
    private TextView textView;
    private TextView valueTextView;
    private boolean wasLayout;

    public FeaturedStickerSetCell(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
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
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 22.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 22.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2.0f, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 100.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 100.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(48.0f, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        AnonymousClass1 r0 = new MryAlphaImageView(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (FeaturedStickerSetCell.this.drawProgress || (!FeaturedStickerSetCell.this.drawProgress && FeaturedStickerSetCell.this.progressAlpha != 0.0f)) {
                    FeaturedStickerSetCell.this.progressPaint.setAlpha(Math.min(255, (int) (FeaturedStickerSetCell.this.progressAlpha * 255.0f)));
                    int x = getMeasuredWidth() - AndroidUtilities.dp(11.0f);
                    FeaturedStickerSetCell.this.progressRect.set((float) x, (float) AndroidUtilities.dp(10.0f), (float) (AndroidUtilities.dp(10.0f) + x), (float) AndroidUtilities.dp(20.0f));
                    canvas.drawArc(FeaturedStickerSetCell.this.progressRect, (float) FeaturedStickerSetCell.this.angle, 220.0f, false, FeaturedStickerSetCell.this.progressPaint);
                    invalidate(((int) FeaturedStickerSetCell.this.progressRect.left) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.top) - AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.right) + AndroidUtilities.dp(2.0f), ((int) FeaturedStickerSetCell.this.progressRect.bottom) + AndroidUtilities.dp(2.0f));
                    long newTime = System.currentTimeMillis();
                    if (Math.abs(FeaturedStickerSetCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                        long delta = newTime - FeaturedStickerSetCell.this.lastUpdateTime;
                        FeaturedStickerSetCell featuredStickerSetCell = FeaturedStickerSetCell.this;
                        int unused = featuredStickerSetCell.angle = (int) (((float) featuredStickerSetCell.angle) + (((float) (360 * delta)) / 2000.0f));
                        FeaturedStickerSetCell featuredStickerSetCell2 = FeaturedStickerSetCell.this;
                        int unused2 = featuredStickerSetCell2.angle = featuredStickerSetCell2.angle - ((FeaturedStickerSetCell.this.angle / 360) * 360);
                        if (FeaturedStickerSetCell.this.drawProgress) {
                            if (FeaturedStickerSetCell.this.progressAlpha < 1.0f) {
                                FeaturedStickerSetCell featuredStickerSetCell3 = FeaturedStickerSetCell.this;
                                float unused3 = featuredStickerSetCell3.progressAlpha = featuredStickerSetCell3.progressAlpha + (((float) delta) / 200.0f);
                                if (FeaturedStickerSetCell.this.progressAlpha > 1.0f) {
                                    float unused4 = FeaturedStickerSetCell.this.progressAlpha = 1.0f;
                                }
                            }
                        } else if (FeaturedStickerSetCell.this.progressAlpha > 0.0f) {
                            FeaturedStickerSetCell featuredStickerSetCell4 = FeaturedStickerSetCell.this;
                            float unused5 = featuredStickerSetCell4.progressAlpha = featuredStickerSetCell4.progressAlpha - (((float) delta) / 200.0f);
                            if (FeaturedStickerSetCell.this.progressAlpha < 0.0f) {
                                float unused6 = FeaturedStickerSetCell.this.progressAlpha = 0.0f;
                            }
                        }
                    }
                    long unused7 = FeaturedStickerSetCell.this.lastUpdateTime = newTime;
                    invalidate();
                    return;
                }
                super.onDraw(canvas);
            }
        };
        this.ivAdd = r0;
        r0.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.ivAdd.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.SRC_IN));
        this.ivAdd.setImageResource(R.mipmap.icon_add);
        addView(this.ivAdd, LayoutHelper.createFrame(-2.0f, 28.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 14.0f : 0.0f, 18.0f, LocaleController.isRTL ? 0.0f : 14.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.checkImage = imageView2;
        imageView2.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#A7A7A7"), PorterDuff.Mode.SRC_IN));
        this.checkImage.setImageResource(R.mipmap.ic_selected);
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
        measureChildWithMargins(this.textView, widthMeasureSpec, this.ivAdd.getMeasuredWidth(), heightMeasureSpec, 0);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int l = (this.ivAdd.getLeft() + (this.ivAdd.getMeasuredWidth() / 2)) - (this.checkImage.getMeasuredWidth() / 2);
        int t = (this.ivAdd.getTop() + (this.ivAdd.getMeasuredHeight() / 2)) - (this.checkImage.getMeasuredHeight() / 2);
        ImageView imageView2 = this.checkImage;
        imageView2.layout(l, t, imageView2.getMeasuredWidth() + l, this.checkImage.getMeasuredHeight() + t);
        this.wasLayout = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setStickersSet(TLRPC.StickerSetCovered set, boolean divider, boolean unread) {
        TLRPC.Document sticker;
        TLObject object;
        ImageLocation imageLocation;
        TLRPC.StickerSetCovered stickerSetCovered = set;
        boolean sameSet = stickerSetCovered == this.stickersSet && this.wasLayout;
        this.needDivider = divider;
        this.stickersSet = stickerSetCovered;
        this.lastUpdateTime = System.currentTimeMillis();
        setWillNotDraw(!this.needDivider);
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
        this.textView.setText(this.stickersSet.set.title);
        if (unread) {
            Drawable drawable = new Drawable() {
                Paint paint = new Paint(1);

                public void draw(Canvas canvas) {
                    this.paint.setColor(-12277526);
                    canvas.drawCircle((float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
                }

                public void setAlpha(int alpha) {
                }

                public void setColorFilter(ColorFilter colorFilter) {
                }

                public int getOpacity() {
                    return -2;
                }

                public int getIntrinsicWidth() {
                    return AndroidUtilities.dp(12.0f);
                }

                public int getIntrinsicHeight() {
                    return AndroidUtilities.dp(8.0f);
                }
            };
            this.textView.setCompoundDrawablesWithIntrinsicBounds(LocaleController.isRTL ? null : drawable, (Drawable) null, LocaleController.isRTL ? drawable : null, (Drawable) null);
        } else {
            this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        this.valueTextView.setText(LocaleController.formatPluralString("Stickers", stickerSetCovered.set.count));
        if (stickerSetCovered.cover != null) {
            sticker = stickerSetCovered.cover;
        } else if (!stickerSetCovered.covers.isEmpty()) {
            sticker = stickerSetCovered.covers.get(0);
        } else {
            sticker = null;
        }
        if (sticker != null) {
            if (stickerSetCovered.set.thumb instanceof TLRPC.TL_photoSize) {
                object = stickerSetCovered.set.thumb;
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
        if (sameSet) {
            boolean wasInstalled = this.isInstalled;
            boolean isStickerPackInstalled = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
            this.isInstalled = isStickerPackInstalled;
            if (isStickerPackInstalled) {
                if (!wasInstalled) {
                    this.checkImage.setVisibility(0);
                    this.ivAdd.setEnabled(false);
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentAnimation = animatorSet2;
                    animatorSet2.setDuration(200);
                    this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.ivAdd, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.ivAdd, "scaleX", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.ivAdd, "scaleY", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[]{0.01f, 1.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[]{0.01f, 1.0f})});
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                                FeaturedStickerSetCell.this.ivAdd.setVisibility(4);
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                                AnimatorSet unused = FeaturedStickerSetCell.this.currentAnimation = null;
                            }
                        }
                    });
                    this.currentAnimation.start();
                }
            } else if (wasInstalled) {
                this.ivAdd.setVisibility(0);
                this.ivAdd.setEnabled(true);
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.currentAnimation = animatorSet3;
                animatorSet3.setDuration(200);
                this.currentAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[]{1.0f, 0.01f}), ObjectAnimator.ofFloat(this.ivAdd, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.ivAdd, "scaleX", new float[]{0.01f, 1.0f}), ObjectAnimator.ofFloat(this.ivAdd, "scaleY", new float[]{0.01f, 1.0f})});
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                            FeaturedStickerSetCell.this.checkImage.setVisibility(4);
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (FeaturedStickerSetCell.this.currentAnimation != null && FeaturedStickerSetCell.this.currentAnimation.equals(animator)) {
                            AnimatorSet unused = FeaturedStickerSetCell.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
            }
        } else {
            boolean isStickerPackInstalled2 = MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
            this.isInstalled = isStickerPackInstalled2;
            if (isStickerPackInstalled2) {
                this.ivAdd.setVisibility(4);
                this.ivAdd.setEnabled(false);
                this.checkImage.setVisibility(0);
                this.checkImage.setScaleX(1.0f);
                this.checkImage.setScaleY(1.0f);
                this.checkImage.setAlpha(1.0f);
                return;
            }
            this.ivAdd.setVisibility(0);
            this.ivAdd.setEnabled(true);
            this.checkImage.setVisibility(4);
            this.ivAdd.setScaleX(1.0f);
            this.ivAdd.setScaleY(1.0f);
            this.ivAdd.setAlpha(1.0f);
        }
    }

    public TLRPC.StickerSetCovered getStickerSet() {
        return this.stickersSet;
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.ivAdd.setOnClickListener(onClickListener);
    }

    public void setDrawProgress(boolean value) {
        this.drawProgress = value;
        this.lastUpdateTime = System.currentTimeMillis();
        this.ivAdd.invalidate();
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needDivider) {
            canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}

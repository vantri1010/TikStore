package im.bclpbkiauv.ui.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.MediaActionDrawable;
import java.util.Locale;

public class AudioProgress {
    private int backgroundStroke;
    private int circleColor;
    private String circleColorKey;
    private Paint circleMiniPaint = new Paint(1);
    private Paint circlePaint = new Paint(1);
    private int circlePressedColor;
    private String circlePressedColorKey;
    private int circleRadius;
    private boolean drawBackground = true;
    private boolean drawMiniIcon;
    private int iconColor;
    private String iconColorKey;
    private int iconPressedColor;
    private String iconPressedColorKey;
    private boolean isPressed;
    private boolean isPressedMini;
    private MediaActionDrawable mediaActionDrawable;
    private Bitmap miniDrawBitmap;
    private Canvas miniDrawCanvas;
    private MediaActionDrawable miniMediaActionDrawable;
    private Paint miniProgressBackgroundPaint = new Paint(1);
    private ImageReceiver overlayImageView;
    private Paint overlayPaint = new Paint(1);
    private float overrideAlpha = 1.0f;
    private View parent;
    private boolean previousCheckDrawable;
    private int progressColor = -1;
    private RectF progressRect = new RectF();

    public AudioProgress(View parentView) {
        this.parent = parentView;
        ImageReceiver imageReceiver = new ImageReceiver(parentView);
        this.overlayImageView = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        MediaActionDrawable mediaActionDrawable2 = new MediaActionDrawable();
        this.mediaActionDrawable = mediaActionDrawable2;
        parentView.getClass();
        mediaActionDrawable2.setDelegate(new MediaActionDrawable.MediaActionDrawableDelegate(parentView) {
            private final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void invalidate() {
                this.f$0.invalidate();
            }
        });
        MediaActionDrawable mediaActionDrawable3 = new MediaActionDrawable();
        this.miniMediaActionDrawable = mediaActionDrawable3;
        parentView.getClass();
        mediaActionDrawable3.setDelegate(new MediaActionDrawable.MediaActionDrawableDelegate(parentView) {
            private final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void invalidate() {
                this.f$0.invalidate();
            }
        });
        this.miniMediaActionDrawable.setMini(true);
        this.miniMediaActionDrawable.setIcon(4, false);
        int dp = AndroidUtilities.dp(22.0f);
        this.circleRadius = dp;
        this.overlayImageView.setRoundRadius(dp);
        this.overlayPaint.setColor(1677721600);
    }

    public void setCircleRadius(int value) {
        this.circleRadius = value;
        this.overlayImageView.setRoundRadius(value);
    }

    public void setBackgroundRadius(int value) {
        this.overlayImageView.setRoundRadius(value);
    }

    public void setBackgroundStroke(int value) {
        this.backgroundStroke = value;
        this.circlePaint.setStrokeWidth((float) value);
        this.circlePaint.setStyle(Paint.Style.STROKE);
        invalidateParent();
    }

    public void setImageOverlay(TLRPC.PhotoSize image, TLRPC.Document document, Object parentObject) {
        this.overlayImageView.setImage(ImageLocation.getForDocument(image, document), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)}), (Drawable) null, (String) null, parentObject, 1);
    }

    public void setImageOverlay(String url) {
        String str;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (url != null) {
            str = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            str = null;
        }
        imageReceiver.setImage(url, str, (Drawable) null, (String) null, -1);
    }

    public void onAttachedToWindow() {
        this.overlayImageView.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        this.overlayImageView.onDetachedFromWindow();
    }

    public void setColors(int circle, int circlePressed, int icon, int iconPressed) {
        this.circleColor = circle;
        this.circlePressedColor = circlePressed;
        this.iconColor = icon;
        this.iconPressedColor = iconPressed;
        this.circleColorKey = null;
        this.circlePressedColorKey = null;
        this.iconColorKey = null;
        this.iconPressedColorKey = null;
    }

    public void setColors(String circle, String circlePressed, String icon, String iconPressed) {
        this.circleColorKey = circle;
        this.circlePressedColorKey = circlePressed;
        this.iconColorKey = icon;
        this.iconPressedColorKey = iconPressed;
    }

    public void setDrawBackground(boolean value) {
        this.drawBackground = value;
    }

    public void setProgressRect(int left, int top, int right, int bottom) {
        this.progressRect.set((float) left, (float) top, (float) right, (float) bottom);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    public void setMiniProgressBackgroundColor(int color) {
        this.miniProgressBackgroundPaint.setColor(color);
    }

    public void setProgress(float value, boolean animated) {
        if (this.drawMiniIcon) {
            this.miniMediaActionDrawable.setProgress(value, animated);
        } else {
            this.mediaActionDrawable.setProgress(value, animated);
        }
    }

    private void invalidateParent() {
        int offset = AndroidUtilities.dp(2.0f);
        this.parent.invalidate(((int) this.progressRect.left) - offset, ((int) this.progressRect.top) - offset, ((int) this.progressRect.right) + (offset * 2), ((int) this.progressRect.bottom) + (offset * 2));
    }

    public int getIcon() {
        return this.mediaActionDrawable.getCurrentIcon();
    }

    public int getMiniIcon() {
        return this.miniMediaActionDrawable.getCurrentIcon();
    }

    public void setIcon(int icon, boolean ifSame, boolean animated) {
        if (!ifSame || icon != this.mediaActionDrawable.getCurrentIcon()) {
            this.mediaActionDrawable.setIcon(icon, animated);
            if (!animated) {
                this.parent.invalidate();
            } else {
                invalidateParent();
            }
        }
    }

    public void setMiniIcon(int icon, boolean ifSame, boolean animated) {
        if (icon != 2 && icon != 3 && icon != 4) {
            return;
        }
        if (!ifSame || icon != this.miniMediaActionDrawable.getCurrentIcon()) {
            this.miniMediaActionDrawable.setIcon(icon, animated);
            boolean z = icon != 4 || this.miniMediaActionDrawable.getTransitionProgress() < 1.0f;
            this.drawMiniIcon = z;
            if (z) {
                initMiniIcons();
            }
            if (!animated) {
                this.parent.invalidate();
            } else {
                invalidateParent();
            }
        }
    }

    public void initMiniIcons() {
        if (this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Bitmap.Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable th) {
            }
        }
    }

    public boolean swapIcon(int icon) {
        if (this.mediaActionDrawable.setIcon(icon, false)) {
            return true;
        }
        return false;
    }

    public void setPressed(boolean value, boolean mini) {
        if (mini) {
            this.isPressedMini = value;
        } else {
            this.isPressed = value;
        }
        invalidateParent();
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public void draw(Canvas canvas) {
        float wholeAlpha;
        int color;
        int centerY;
        int centerX;
        float cy;
        float cx;
        int size;
        int offset;
        Canvas canvas2;
        Canvas canvas3;
        int r;
        int color2;
        Canvas canvas4 = canvas;
        if (this.mediaActionDrawable.getCurrentIcon() != 4 || this.mediaActionDrawable.getTransitionProgress() < 1.0f) {
            int currentIcon = this.mediaActionDrawable.getCurrentIcon();
            int prevIcon = this.mediaActionDrawable.getPreviousIcon();
            if (this.backgroundStroke != 0) {
                wholeAlpha = currentIcon == 3 ? 1.0f - this.mediaActionDrawable.getTransitionProgress() : prevIcon == 3 ? this.mediaActionDrawable.getTransitionProgress() : 1.0f;
            } else if ((currentIcon == 3 || currentIcon == 6 || currentIcon == 10 || currentIcon == 8 || currentIcon == 0) && prevIcon == 4) {
                wholeAlpha = this.mediaActionDrawable.getTransitionProgress();
            } else {
                wholeAlpha = currentIcon != 4 ? 1.0f : 1.0f - this.mediaActionDrawable.getTransitionProgress();
            }
            if (this.isPressedMini) {
                String str = this.iconPressedColorKey;
                if (str != null) {
                    this.miniMediaActionDrawable.setColor(Theme.getColor(str));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconPressedColor);
                }
                String str2 = this.circlePressedColorKey;
                if (str2 != null) {
                    this.circleMiniPaint.setColor(Theme.getColor(str2));
                } else {
                    this.circleMiniPaint.setColor(this.circlePressedColor);
                }
            } else {
                String str3 = this.iconColorKey;
                if (str3 != null) {
                    this.miniMediaActionDrawable.setColor(Theme.getColor(str3));
                } else {
                    this.miniMediaActionDrawable.setColor(this.iconColor);
                }
                String str4 = this.circleColorKey;
                if (str4 != null) {
                    this.circleMiniPaint.setColor(Theme.getColor(str4));
                } else {
                    this.circleMiniPaint.setColor(this.circleColor);
                }
            }
            if (this.isPressed) {
                String str5 = this.iconPressedColorKey;
                if (str5 != null) {
                    MediaActionDrawable mediaActionDrawable2 = this.mediaActionDrawable;
                    int color3 = Theme.getColor(str5);
                    color = color3;
                    mediaActionDrawable2.setColor(color3);
                } else {
                    MediaActionDrawable mediaActionDrawable3 = this.mediaActionDrawable;
                    int i = this.iconPressedColor;
                    color = i;
                    mediaActionDrawable3.setColor(i);
                }
                String str6 = this.circlePressedColorKey;
                if (str6 != null) {
                    this.circlePaint.setColor(Theme.getColor(str6));
                } else {
                    this.circlePaint.setColor(this.circlePressedColor);
                }
            } else {
                String str7 = this.iconColorKey;
                if (str7 != null) {
                    MediaActionDrawable mediaActionDrawable4 = this.mediaActionDrawable;
                    int color4 = Theme.getColor(str7);
                    color2 = color4;
                    mediaActionDrawable4.setColor(color4);
                } else {
                    MediaActionDrawable mediaActionDrawable5 = this.mediaActionDrawable;
                    int i2 = this.iconColor;
                    color2 = i2;
                    mediaActionDrawable5.setColor(i2);
                }
                String str8 = this.circleColorKey;
                if (str8 != null) {
                    this.circlePaint.setColor(Theme.getColor(str8));
                } else {
                    this.circlePaint.setColor(this.circleColor);
                }
            }
            if (this.drawMiniIcon && this.miniDrawCanvas != null) {
                this.miniDrawBitmap.eraseColor(0);
            }
            this.circlePaint.setAlpha((int) (((float) this.circlePaint.getAlpha()) * wholeAlpha * this.overrideAlpha));
            int originalAlpha = this.circleMiniPaint.getAlpha();
            this.circleMiniPaint.setAlpha((int) (((float) originalAlpha) * wholeAlpha * this.overrideAlpha));
            boolean drawCircle = true;
            if (!this.drawMiniIcon || this.miniDrawCanvas == null) {
                centerX = (int) this.progressRect.centerX();
                centerY = (int) this.progressRect.centerY();
            } else {
                centerX = (int) (this.progressRect.width() / 2.0f);
                centerY = (int) (this.progressRect.height() / 2.0f);
            }
            if (this.overlayImageView.hasBitmapImage()) {
                float alpha = this.overlayImageView.getCurrentAlpha();
                this.overlayPaint.setAlpha((int) (100.0f * alpha * wholeAlpha * this.overrideAlpha));
                if (alpha >= 1.0f) {
                    drawCircle = false;
                    r = -1;
                    int i3 = originalAlpha;
                    int i4 = color;
                    float f = alpha;
                } else {
                    int c = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    int a = Color.alpha(color);
                    int rD = (int) (((float) (255 - c)) * alpha);
                    int i5 = originalAlpha;
                    int gD = (int) (((float) (255 - g)) * alpha);
                    int i6 = color;
                    float f2 = alpha;
                    int i7 = a;
                    int i8 = rD;
                    int i9 = gD;
                    r = Color.argb(a + ((int) (((float) (255 - a)) * alpha)), c + rD, g + gD, b + ((int) (((float) (255 - b)) * alpha)));
                    drawCircle = true;
                }
                this.mediaActionDrawable.setColor(r);
                ImageReceiver imageReceiver = this.overlayImageView;
                int i10 = this.circleRadius;
                imageReceiver.setImageCoords(centerX - i10, centerY - i10, i10 * 2, i10 * 2);
            } else {
                int i11 = color;
            }
            if (drawCircle && this.drawBackground) {
                if (this.drawMiniIcon && (canvas3 = this.miniDrawCanvas) != null) {
                    canvas3.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.circlePaint);
                } else if (!(currentIcon == 4 && wholeAlpha == 0.0f)) {
                    if (this.backgroundStroke != 0) {
                        canvas4.drawCircle((float) centerX, (float) centerY, (float) (this.circleRadius - AndroidUtilities.dp(3.5f)), this.circlePaint);
                    } else if (currentIcon == 1 || currentIcon == 0 || prevIcon == 1 || prevIcon == 0) {
                        canvas4.drawCircle((float) centerX, (float) centerY, ((float) this.circleRadius) * wholeAlpha, this.circlePaint);
                    } else {
                        int i12 = this.circleRadius;
                        canvas4.drawRoundRect(new RectF((float) (centerX - i12), (float) (centerY - i12), (float) (centerX + i12), (float) (i12 + centerY)), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), this.circleMiniPaint);
                    }
                }
            }
            if (this.overlayImageView.hasBitmapImage()) {
                this.overlayImageView.setAlpha(this.overrideAlpha * wholeAlpha);
                if (!this.drawMiniIcon || (canvas2 = this.miniDrawCanvas) == null) {
                    this.overlayImageView.draw(canvas4);
                    canvas4.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                } else {
                    this.overlayImageView.draw(canvas2);
                    this.miniDrawCanvas.drawCircle((float) centerX, (float) centerY, (float) this.circleRadius, this.overlayPaint);
                }
            }
            MediaActionDrawable mediaActionDrawable6 = this.mediaActionDrawable;
            int i13 = this.circleRadius;
            mediaActionDrawable6.setBounds(centerX - i13, centerY - i13, centerX + i13, i13 + centerY);
            if (this.drawMiniIcon) {
                Canvas canvas5 = this.miniDrawCanvas;
                if (canvas5 != null) {
                    this.mediaActionDrawable.draw(canvas5);
                } else {
                    this.mediaActionDrawable.draw(canvas4);
                }
            } else {
                this.mediaActionDrawable.setOverrideAlpha(this.overrideAlpha);
                this.mediaActionDrawable.draw(canvas4);
            }
            if (this.drawMiniIcon) {
                if (Math.abs(this.progressRect.width() - ((float) AndroidUtilities.dp(44.0f))) < AndroidUtilities.density) {
                    offset = 0;
                    size = 20;
                    cx = this.progressRect.centerX() + ((float) AndroidUtilities.dp((float) (0 + 16)));
                    cy = this.progressRect.centerY() + ((float) AndroidUtilities.dp((float) (0 + 16)));
                } else {
                    offset = 2;
                    size = 22;
                    cx = this.progressRect.centerX() + ((float) AndroidUtilities.dp(18.0f));
                    cy = ((float) AndroidUtilities.dp(18.0f)) + this.progressRect.centerY();
                }
                int halfSize = size / 2;
                float alpha2 = this.miniMediaActionDrawable.getCurrentIcon() != 4 ? 1.0f : 1.0f - this.miniMediaActionDrawable.getTransitionProgress();
                if (alpha2 == 0.0f) {
                    this.drawMiniIcon = false;
                }
                Canvas canvas6 = this.miniDrawCanvas;
                if (canvas6 != null) {
                    int i14 = currentIcon;
                    int i15 = offset;
                    int i16 = size;
                    canvas6.drawCircle((float) AndroidUtilities.dp((float) (size + 18 + offset)), (float) AndroidUtilities.dp((float) (size + 18 + offset)), ((float) AndroidUtilities.dp((float) (halfSize + 1))) * alpha2, Theme.checkboxSquare_eraserPaint);
                } else {
                    int i17 = offset;
                    int i18 = size;
                    this.miniProgressBackgroundPaint.setColor(this.progressColor);
                    canvas4.drawCircle(cx, cy, (float) AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
                }
                if (this.miniDrawCanvas != null) {
                    canvas4.drawBitmap(this.miniDrawBitmap, (float) ((int) this.progressRect.left), (float) ((int) this.progressRect.top), (Paint) null);
                }
                canvas4.drawCircle(cx, cy, ((float) AndroidUtilities.dp((float) halfSize)) * alpha2, this.circleMiniPaint);
                this.miniMediaActionDrawable.setBounds((int) (cx - (((float) AndroidUtilities.dp((float) halfSize)) * alpha2)), (int) (cy - (((float) AndroidUtilities.dp((float) halfSize)) * alpha2)), (int) ((((float) AndroidUtilities.dp((float) halfSize)) * alpha2) + cx), (int) ((((float) AndroidUtilities.dp((float) halfSize)) * alpha2) + cy));
                this.miniMediaActionDrawable.draw(canvas4);
                return;
            }
        }
    }
}

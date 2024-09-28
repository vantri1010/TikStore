package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;

public class ShareLocationDrawable extends Drawable {
    private int currentType;
    private Drawable drawable;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private long lastUpdateTime = 0;
    private float[] progress = {0.0f, -0.5f};

    public ShareLocationDrawable(Context context, int type) {
        this.currentType = type;
        if (type == 3) {
            this.drawable = context.getResources().getDrawable(R.drawable.nearby_l);
            this.drawableLeft = context.getResources().getDrawable(R.drawable.animationpinleft);
            this.drawableRight = context.getResources().getDrawable(R.drawable.animationpinright);
        } else if (type == 2) {
            this.drawable = context.getResources().getDrawable(R.drawable.nearby_m);
            this.drawableLeft = context.getResources().getDrawable(R.drawable.animationpinleft);
            this.drawableRight = context.getResources().getDrawable(R.drawable.animationpinright);
        } else if (type == 1) {
            this.drawable = context.getResources().getDrawable(R.drawable.smallanimationpin);
            this.drawableLeft = context.getResources().getDrawable(R.drawable.smallanimationpinleft);
            this.drawableRight = context.getResources().getDrawable(R.drawable.smallanimationpinright);
        } else {
            this.drawable = context.getResources().getDrawable(R.drawable.animationpin);
            this.drawableLeft = context.getResources().getDrawable(R.drawable.animationpinleft);
            this.drawableRight = context.getResources().getDrawable(R.drawable.animationpinright);
        }
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 16) {
            dt = 16;
        }
        for (int a = 0; a < 2; a++) {
            float[] fArr = this.progress;
            if (fArr[a] >= 1.0f) {
                fArr[a] = 0.0f;
            }
            float[] fArr2 = this.progress;
            fArr2[a] = fArr2[a] + (((float) dt) / 1300.0f);
            if (fArr2[a] > 1.0f) {
                fArr2[a] = 1.0f;
            }
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        int size;
        int x;
        int y;
        int size2;
        int cx2;
        int cx;
        int h;
        int w;
        int cy;
        float alpha;
        Canvas canvas2 = canvas;
        int size3 = this.currentType;
        int i = 3;
        int i2 = 1;
        if (size3 == 3) {
            size = AndroidUtilities.dp(44.0f);
        } else if (size3 == 2) {
            size = AndroidUtilities.dp(32.0f);
        } else if (size3 == 1) {
            size = AndroidUtilities.dp(30.0f);
        } else {
            size = AndroidUtilities.dp(120.0f);
        }
        int y2 = getBounds().top + ((getIntrinsicHeight() - size) / 2);
        int x2 = getBounds().left + ((getIntrinsicWidth() - size) / 2);
        Drawable drawable2 = this.drawable;
        drawable2.setBounds(x2, y2, drawable2.getIntrinsicWidth() + x2, this.drawable.getIntrinsicHeight() + y2);
        this.drawable.draw(canvas2);
        int a = 0;
        for (int i3 = 2; a < i3; i3 = 2) {
            float[] fArr = this.progress;
            if (fArr[a] < 0.0f) {
                size2 = size;
                y = y2;
                x = x2;
            } else {
                float scale = (fArr[a] * 0.5f) + 0.5f;
                int w2 = this.currentType;
                if (w2 == i) {
                    w = AndroidUtilities.dp(5.0f * scale);
                    h = AndroidUtilities.dp(18.0f * scale);
                    int tx = AndroidUtilities.dp(this.progress[a] * 15.0f);
                    cx = (AndroidUtilities.dp(2.0f) + x2) - tx;
                    cy = ((this.drawable.getIntrinsicHeight() / i3) + y2) - AndroidUtilities.dp(7.0f);
                    cx2 = ((this.drawable.getIntrinsicWidth() + x2) - AndroidUtilities.dp(2.0f)) + tx;
                } else if (w2 == i3) {
                    w = AndroidUtilities.dp(5.0f * scale);
                    h = AndroidUtilities.dp(18.0f * scale);
                    int tx2 = AndroidUtilities.dp(this.progress[a] * 15.0f);
                    cx = (AndroidUtilities.dp(2.0f) + x2) - tx2;
                    cy = (this.drawable.getIntrinsicHeight() / i3) + y2;
                    cx2 = ((this.drawable.getIntrinsicWidth() + x2) - AndroidUtilities.dp(2.0f)) + tx2;
                } else if (w2 == i2) {
                    w = AndroidUtilities.dp(2.5f * scale);
                    h = AndroidUtilities.dp(6.5f * scale);
                    int tx3 = AndroidUtilities.dp(this.progress[a] * 6.0f);
                    cx = (AndroidUtilities.dp(7.0f) + x2) - tx3;
                    cy = (this.drawable.getIntrinsicHeight() / i3) + y2;
                    cx2 = ((this.drawable.getIntrinsicWidth() + x2) - AndroidUtilities.dp(7.0f)) + tx3;
                } else {
                    w = AndroidUtilities.dp(5.0f * scale);
                    h = AndroidUtilities.dp(18.0f * scale);
                    int tx4 = AndroidUtilities.dp(this.progress[a] * 15.0f);
                    cx = (AndroidUtilities.dp(42.0f) + x2) - tx4;
                    int cy2 = ((this.drawable.getIntrinsicHeight() / i3) + y2) - AndroidUtilities.dp(7.0f);
                    cx2 = ((this.drawable.getIntrinsicWidth() + x2) - AndroidUtilities.dp(42.0f)) + tx4;
                    cy = cy2;
                }
                float[] fArr2 = this.progress;
                if (fArr2[a] < 0.5f) {
                    alpha = fArr2[a] / 0.5f;
                } else {
                    alpha = 1.0f - ((fArr2[a] - 0.5f) / 0.5f);
                }
                this.drawableLeft.setAlpha((int) (alpha * 255.0f));
                size2 = size;
                y = y2;
                x = x2;
                this.drawableLeft.setBounds(cx - w, cy - h, cx + w, cy + h);
                this.drawableLeft.draw(canvas2);
                this.drawableRight.setAlpha((int) (alpha * 255.0f));
                this.drawableRight.setBounds(cx2 - w, cy - h, cx2 + w, cy + h);
                this.drawableRight.draw(canvas2);
            }
            a++;
            size = size2;
            y2 = y;
            x2 = x;
            i = 3;
            i2 = 1;
        }
        update();
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
        this.drawable.setColorFilter(cf);
        this.drawableLeft.setColorFilter(cf);
        this.drawableRight.setColorFilter(cf);
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        int i = this.currentType;
        if (i == 3) {
            return AndroidUtilities.dp(100.0f);
        }
        if (i == 2) {
            return AndroidUtilities.dp(74.0f);
        }
        if (i == 1) {
            return AndroidUtilities.dp(40.0f);
        }
        return AndroidUtilities.dp(120.0f);
    }

    public int getIntrinsicHeight() {
        int i = this.currentType;
        if (i == 3) {
            return AndroidUtilities.dp(100.0f);
        }
        if (i == 2) {
            return AndroidUtilities.dp(74.0f);
        }
        if (i == 1) {
            return AndroidUtilities.dp(40.0f);
        }
        return AndroidUtilities.dp(180.0f);
    }
}

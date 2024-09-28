package im.bclpbkiauv.ui.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class RoundStatusDrawable extends StatusDrawable {
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private float progress;
    private int progressDirection = 1;
    private boolean started = false;

    public void setIsChat(boolean value) {
        this.isChat = value;
    }

    private void update() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (dt > 50) {
            dt = 50;
        }
        float f = this.progress;
        int i = this.progressDirection;
        float f2 = f + (((float) (((long) i) * dt)) / 400.0f);
        this.progress = f2;
        if (i > 0 && f2 >= 1.0f) {
            this.progressDirection = -1;
            this.progress = 1.0f;
        } else if (this.progressDirection < 0 && this.progress <= 0.0f) {
            this.progressDirection = 1;
            this.progress = 0.0f;
        }
        invalidateSelf();
    }

    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    public void stop() {
        this.started = false;
    }

    public void draw(Canvas canvas) {
        Theme.chat_statusPaint.setAlpha(((int) (this.progress * 200.0f)) + 55);
        canvas.drawCircle((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(this.isChat ? 8.0f : 9.0f), (float) AndroidUtilities.dp(4.0f), Theme.chat_statusPaint);
        if (this.started) {
            update();
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return 0;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(12.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(10.0f);
    }
}

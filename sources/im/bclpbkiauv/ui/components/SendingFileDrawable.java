package im.bclpbkiauv.ui.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class SendingFileDrawable extends StatusDrawable {
    private boolean isChat = false;
    private long lastUpdateTime = 0;
    private float progress;
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
        this.progress += ((float) dt) / 500.0f;
        while (true) {
            float f = this.progress;
            if (f > 1.0f) {
                this.progress = f - 1.0f;
            } else {
                invalidateSelf();
                return;
            }
        }
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
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                Theme.chat_statusRecordPaint.setAlpha((int) (this.progress * 255.0f));
            } else if (a == 2) {
                Theme.chat_statusRecordPaint.setAlpha((int) ((1.0f - this.progress) * 255.0f));
            } else {
                Theme.chat_statusRecordPaint.setAlpha(255);
            }
            float side = ((float) (AndroidUtilities.dp(5.0f) * a)) + (((float) AndroidUtilities.dp(5.0f)) * this.progress);
            float f = 7.0f;
            canvas.drawLine(side, (float) AndroidUtilities.dp(this.isChat ? 3.0f : 4.0f), side + ((float) AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(this.isChat ? 7.0f : 8.0f), Theme.chat_statusRecordPaint);
            float dp = (float) AndroidUtilities.dp(this.isChat ? 11.0f : 12.0f);
            float dp2 = side + ((float) AndroidUtilities.dp(4.0f));
            if (!this.isChat) {
                f = 8.0f;
            }
            canvas.drawLine(side, dp, dp2, (float) AndroidUtilities.dp(f), Theme.chat_statusRecordPaint);
        }
        if (this.started != 0) {
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
        return AndroidUtilities.dp(18.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }
}

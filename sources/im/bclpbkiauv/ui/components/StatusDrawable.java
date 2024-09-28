package im.bclpbkiauv.ui.components;

import android.graphics.drawable.Drawable;

public abstract class StatusDrawable extends Drawable {
    public abstract void setIsChat(boolean z);

    public abstract void start();

    public abstract void stop();
}

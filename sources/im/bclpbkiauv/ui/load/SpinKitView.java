package im.bclpbkiauv.ui.load;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.load.sprite.Sprite;

public class SpinKitView extends ProgressBar {
    private int mColor;
    private Sprite mSprite;
    private Style mStyle;

    public SpinKitView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SpinKitView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SpinKitViewStyle);
    }

    public SpinKitView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.SpinKitView);
    }

    public SpinKitView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpinKitView, defStyleAttr, defStyleRes);
        this.mStyle = Style.values()[a.getInt(1, 0)];
        this.mColor = a.getColor(0, -1);
        a.recycle();
        init();
        setIndeterminate(true);
    }

    private void init() {
        Sprite sprite = SpriteFactory.create(this.mStyle);
        sprite.setColor(this.mColor);
        setIndeterminateDrawable(sprite);
    }

    public void setIndeterminateDrawable(Drawable d) {
        if (d instanceof Sprite) {
            setIndeterminateDrawable((Sprite) d);
            return;
        }
        throw new IllegalArgumentException("this d must be instanceof Sprite");
    }

    public void setIndeterminateDrawable(Sprite d) {
        super.setIndeterminateDrawable(d);
        this.mSprite = d;
        if (d.getColor() == 0) {
            this.mSprite.setColor(this.mColor);
        }
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        if (getVisibility() == 0) {
            this.mSprite.start();
        }
    }

    public Sprite getIndeterminateDrawable() {
        return this.mSprite;
    }

    public void setColor(int color) {
        this.mColor = color;
        Sprite sprite = this.mSprite;
        if (sprite != null) {
            sprite.setColor(color);
        }
        invalidate();
    }

    public void stop() {
        Sprite sprite = this.mSprite;
        if (sprite != null) {
            sprite.stop();
        }
    }

    public void start() {
        Sprite sprite = this.mSprite;
        if (sprite != null) {
            sprite.start();
        }
    }

    public void unscheduleDrawable(Drawable who) {
        super.unscheduleDrawable(who);
        if (who instanceof Sprite) {
            ((Sprite) who).stop();
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && this.mSprite != null && getVisibility() == 0) {
            this.mSprite.start();
        }
    }

    public void onScreenStateChanged(int screenState) {
        Sprite sprite;
        super.onScreenStateChanged(screenState);
        if (screenState == 0 && (sprite = this.mSprite) != null) {
            sprite.stop();
        }
    }
}

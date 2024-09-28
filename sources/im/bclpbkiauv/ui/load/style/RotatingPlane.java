package im.bclpbkiauv.ui.load.style;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import im.bclpbkiauv.ui.load.animation.SpriteAnimatorBuilder;
import im.bclpbkiauv.ui.load.sprite.RectSprite;

public class RotatingPlane extends RectSprite {
    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        setDrawBounds(clipSquare(bounds));
    }

    public ValueAnimator onCreateAnimation() {
        float[] fractions = {0.0f, 0.5f, 1.0f};
        return new SpriteAnimatorBuilder(this).rotateX(fractions, 0, -180, -180).rotateY(fractions, 0, 0, -180).duration(1200).easeInOut(fractions).build();
    }
}

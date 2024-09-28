package im.bclpbkiauv.ui.load.style;

import android.animation.ValueAnimator;
import im.bclpbkiauv.ui.load.animation.SpriteAnimatorBuilder;
import im.bclpbkiauv.ui.load.sprite.CircleSprite;

public class Pulse extends CircleSprite {
    public Pulse() {
        setScale(0.0f);
    }

    public ValueAnimator onCreateAnimation() {
        float[] fractions = {0.0f, 1.0f};
        return new SpriteAnimatorBuilder(this).scale(fractions, Float.valueOf(0.0f), Float.valueOf(1.0f)).alpha(fractions, 255, 0).duration(1000).easeInOut(fractions).build();
    }
}

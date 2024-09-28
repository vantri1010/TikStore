package im.bclpbkiauv.ui.load.style;

import android.animation.ValueAnimator;
import android.os.Build;
import im.bclpbkiauv.ui.load.animation.SpriteAnimatorBuilder;
import im.bclpbkiauv.ui.load.sprite.CircleLayoutContainer;
import im.bclpbkiauv.ui.load.sprite.CircleSprite;
import im.bclpbkiauv.ui.load.sprite.Sprite;

public class Circle extends CircleLayoutContainer {
    public Sprite[] onCreateChild() {
        Dot[] dots = new Dot[12];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new Dot();
            if (Build.VERSION.SDK_INT >= 24) {
                dots[i].setAnimationDelay(i * 100);
            } else {
                dots[i].setAnimationDelay((i * 100) - 1200);
            }
        }
        return dots;
    }

    private class Dot extends CircleSprite {
        Dot() {
            setScale(0.0f);
        }

        public ValueAnimator onCreateAnimation() {
            float[] fractions = {0.0f, 0.5f, 1.0f};
            SpriteAnimatorBuilder spriteAnimatorBuilder = new SpriteAnimatorBuilder(this);
            Float valueOf = Float.valueOf(0.0f);
            return spriteAnimatorBuilder.scale(fractions, valueOf, Float.valueOf(1.0f), valueOf).duration(1200).easeInOut(fractions).build();
        }
    }
}

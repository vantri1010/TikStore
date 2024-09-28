package im.bclpbkiauv.ui.load.style;

import android.animation.ValueAnimator;
import android.os.Build;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.ui.load.animation.SpriteAnimatorBuilder;
import im.bclpbkiauv.ui.load.sprite.CircleSprite;
import im.bclpbkiauv.ui.load.sprite.Sprite;
import im.bclpbkiauv.ui.load.sprite.SpriteContainer;

public class DoubleBounce extends SpriteContainer {
    public Sprite[] onCreateChild() {
        return new Sprite[]{new Bounce(), new Bounce()};
    }

    public void onChildCreated(Sprite... sprites) {
        super.onChildCreated(sprites);
        if (Build.VERSION.SDK_INT >= 24) {
            sprites[1].setAnimationDelay(1000);
        } else {
            sprites[1].setAnimationDelay(-1000);
        }
    }

    private class Bounce extends CircleSprite {
        Bounce() {
            setAlpha(153);
            setScale(0.0f);
        }

        public ValueAnimator onCreateAnimation() {
            float[] fractions = {0.0f, 0.5f, 1.0f};
            SpriteAnimatorBuilder spriteAnimatorBuilder = new SpriteAnimatorBuilder(this);
            Float valueOf = Float.valueOf(0.0f);
            return spriteAnimatorBuilder.scale(fractions, valueOf, Float.valueOf(1.0f), valueOf).duration(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS).easeInOut(fractions).build();
        }
    }
}

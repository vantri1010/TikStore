package im.bclpbkiauv.ui.load.style;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import im.bclpbkiauv.ui.load.animation.SpriteAnimatorBuilder;
import im.bclpbkiauv.ui.load.sprite.CircleSprite;
import im.bclpbkiauv.ui.load.sprite.Sprite;
import im.bclpbkiauv.ui.load.sprite.SpriteContainer;

public class ThreeBounce extends SpriteContainer {
    public Sprite[] onCreateChild() {
        return new Sprite[]{new Bounce(), new Bounce(), new Bounce()};
    }

    public void onChildCreated(Sprite... sprites) {
        super.onChildCreated(sprites);
        sprites[1].setAnimationDelay(160);
        sprites[2].setAnimationDelay(320);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        Rect bounds2 = clipSquare(bounds);
        int radius = bounds2.width() / 8;
        int top = bounds2.centerY() - radius;
        int bottom = bounds2.centerY() + radius;
        for (int i = 0; i < getChildCount(); i++) {
            int left = ((bounds2.width() * i) / 3) + bounds2.left;
            getChildAt(i).setDrawBounds(left, top, (radius * 2) + left, bottom);
        }
    }

    private class Bounce extends CircleSprite {
        Bounce() {
            setScale(0.0f);
        }

        public ValueAnimator onCreateAnimation() {
            float[] fractions = {0.0f, 0.4f, 0.8f, 1.0f};
            SpriteAnimatorBuilder spriteAnimatorBuilder = new SpriteAnimatorBuilder(this);
            Float valueOf = Float.valueOf(0.0f);
            return spriteAnimatorBuilder.scale(fractions, valueOf, Float.valueOf(1.0f), valueOf, valueOf).duration(1400).easeInOut(fractions).build();
        }
    }
}

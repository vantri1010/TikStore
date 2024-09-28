package im.bclpbkiauv.ui.load.style;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import androidx.recyclerview.widget.ItemTouchHelper;
import im.bclpbkiauv.ui.load.animation.SpriteAnimatorBuilder;
import im.bclpbkiauv.ui.load.sprite.RectSprite;
import im.bclpbkiauv.ui.load.sprite.Sprite;
import im.bclpbkiauv.ui.load.sprite.SpriteContainer;

public class CubeGrid extends SpriteContainer {
    public Sprite[] onCreateChild() {
        int[] delays = {ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 300, 400, 100, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 300, 0, 100, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION};
        GridItem[] gridItems = new GridItem[9];
        for (int i = 0; i < gridItems.length; i++) {
            gridItems[i] = new GridItem();
            gridItems[i].setAnimationDelay(delays[i]);
        }
        return gridItems;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        Rect bounds2 = clipSquare(bounds);
        int width = (int) (((float) bounds2.width()) * 0.33f);
        int height = (int) (((float) bounds2.height()) * 0.33f);
        for (int i = 0; i < getChildCount(); i++) {
            int l = bounds2.left + ((i % 3) * width);
            int t = bounds2.top + ((i / 3) * height);
            getChildAt(i).setDrawBounds(l, t, l + width, t + height);
        }
    }

    private class GridItem extends RectSprite {
        private GridItem() {
        }

        public ValueAnimator onCreateAnimation() {
            float[] fractions = {0.0f, 0.35f, 0.7f, 1.0f};
            SpriteAnimatorBuilder spriteAnimatorBuilder = new SpriteAnimatorBuilder(this);
            Float valueOf = Float.valueOf(1.0f);
            return spriteAnimatorBuilder.scale(fractions, valueOf, Float.valueOf(0.0f), valueOf, valueOf).duration(1300).easeInOut(fractions).build();
        }
    }
}

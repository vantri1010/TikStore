package im.bclpbkiauv.ui.load.style;

import androidx.recyclerview.widget.ItemTouchHelper;
import im.bclpbkiauv.ui.load.sprite.Sprite;
import im.bclpbkiauv.ui.load.sprite.SpriteContainer;

public class MultiplePulse extends SpriteContainer {
    public Sprite[] onCreateChild() {
        return new Sprite[]{new Pulse(), new Pulse(), new Pulse()};
    }

    public void onChildCreated(Sprite... sprites) {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].setAnimationDelay((i + 1) * ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
    }
}

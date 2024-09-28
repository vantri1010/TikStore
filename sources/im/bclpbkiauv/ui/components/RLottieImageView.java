package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import java.util.HashMap;
import java.util.Map;

public class RLottieImageView extends ImageView {
    private RLottieDrawable drawable;
    private HashMap<String, Integer> layerColors;

    public RLottieImageView(Context context) {
        super(context);
    }

    public void setLayerColor(String layer, int color) {
        if (this.layerColors == null) {
            this.layerColors = new HashMap<>();
        }
        this.layerColors.put(layer, Integer.valueOf(color));
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setLayerColor(layer, color);
        }
    }

    public void setAnimation(int resId, int w, int h) {
        RLottieDrawable rLottieDrawable = new RLottieDrawable(resId, "" + resId, AndroidUtilities.dp((float) w), AndroidUtilities.dp((float) h), false);
        this.drawable = rLottieDrawable;
        rLottieDrawable.beginApplyLayerColors();
        HashMap<String, Integer> hashMap = this.layerColors;
        if (hashMap != null) {
            for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
                this.drawable.setLayerColor(entry.getKey(), entry.getValue().intValue());
            }
        }
        this.drawable.commitApplyLayerColors();
        this.drawable.setAllowDecodeSingleFrame(true);
        this.drawable.setAutoRepeat(1);
        setImageDrawable(this.drawable);
    }

    public void setProgress(float progress) {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.setProgress(progress);
        }
    }

    public void playAnimation() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.start();
        }
    }

    public void stopAnimation() {
        RLottieDrawable rLottieDrawable = this.drawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.start();
        }
    }
}

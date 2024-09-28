package im.bclpbkiauv.ui.components.banner.util;

import android.content.res.Resources;
import android.graphics.Outline;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

public class BannerUtils {
    public static int getRealPosition(boolean isIncrease, int position, int realCount) {
        if (!isIncrease) {
            return position;
        }
        if (position == 0) {
            return realCount - 1;
        }
        if (position == realCount + 1) {
            return 0;
        }
        return position - 1;
    }

    public static View getView(ViewGroup parent, int layoutId) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params.height == -1 && params.width == -1)) {
            params.height = -1;
            params.width = -1;
            view.setLayoutParams(params);
        }
        return view;
    }

    public static float dp2px(float dp) {
        return TypedValue.applyDimension(1, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static void setBannerRound(View view, final float radius) {
        view.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        view.setClipToOutline(true);
    }
}

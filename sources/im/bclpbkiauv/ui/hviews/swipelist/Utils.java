package im.bclpbkiauv.ui.hviews.swipelist;

import android.view.View;
import androidx.core.view.ViewCompat;

public class Utils {
    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == 1;
    }
}

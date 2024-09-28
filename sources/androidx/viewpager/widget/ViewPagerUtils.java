package androidx.viewpager.widget;

import android.view.View;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerUtils {
    public static View getCurrentView(ViewPager viewPager) {
        int currentItem = viewPager.getCurrentItem();
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            View child = viewPager.getChildAt(i);
            ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();
            if (!layoutParams.isDecor && currentItem == layoutParams.position) {
                return child;
            }
        }
        return null;
    }
}

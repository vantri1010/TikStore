package im.bclpbkiauv.ui.hviews.behavior;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

public final class BottomSheetUtils {
    public static void setupViewPager(ViewPager viewPager) {
        View bottomSheetParent = findBottomSheetParent(viewPager);
        if (bottomSheetParent != null) {
            viewPager.addOnPageChangeListener(new BottomSheetViewPagerListener(viewPager, bottomSheetParent));
        }
    }

    private static class BottomSheetViewPagerListener extends ViewPager.SimpleOnPageChangeListener {
        private final ViewPagerBottomSheetBehavior<View> behavior;
        private final ViewPager viewPager;

        private BottomSheetViewPagerListener(ViewPager viewPager2, View bottomSheetParent) {
            this.viewPager = viewPager2;
            this.behavior = ViewPagerBottomSheetBehavior.from(bottomSheetParent);
        }

        public void onPageSelected(int position) {
            ViewPager viewPager2 = this.viewPager;
            ViewPagerBottomSheetBehavior<View> viewPagerBottomSheetBehavior = this.behavior;
            viewPagerBottomSheetBehavior.getClass();
            viewPager2.post(new Runnable() {
                public final void run() {
                    ViewPagerBottomSheetBehavior.this.invalidateScrollingChild();
                }
            });
        }
    }

    private static View findBottomSheetParent(View view) {
        View current = view;
        while (true) {
            View view2 = null;
            if (current == null) {
                return null;
            }
            ViewGroup.LayoutParams params = current.getLayoutParams();
            if ((params instanceof CoordinatorLayout.LayoutParams) && (((CoordinatorLayout.LayoutParams) params).getBehavior() instanceof ViewPagerBottomSheetBehavior)) {
                return current;
            }
            ViewParent parent = current.getParent();
            if (parent != null && (parent instanceof View)) {
                view2 = (View) parent;
            }
            current = view2;
        }
    }
}

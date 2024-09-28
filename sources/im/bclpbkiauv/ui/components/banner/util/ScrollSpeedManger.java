package im.bclpbkiauv.ui.components.banner.util;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import im.bclpbkiauv.ui.components.banner.Banner;
import java.lang.reflect.Field;

public class ScrollSpeedManger extends LinearLayoutManager {
    /* access modifiers changed from: private */
    public Banner banner;

    public ScrollSpeedManger(Banner banner2, LinearLayoutManager linearLayoutManager) {
        super(banner2.getContext(), linearLayoutManager.getOrientation(), false);
        this.banner = banner2;
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            /* access modifiers changed from: protected */
            public int calculateTimeForDeceleration(int dx) {
                return ScrollSpeedManger.this.banner.getScrollTime();
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public static void reflectLayoutManager(Banner banner2) {
        if (banner2.getScrollTime() >= 100) {
            try {
                ViewPager2 viewPager2 = banner2.getViewPager2();
                RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
                recyclerView.setOverScrollMode(2);
                ScrollSpeedManger speedManger = new ScrollSpeedManger(banner2, (LinearLayoutManager) recyclerView.getLayoutManager());
                recyclerView.setLayoutManager(speedManger);
                Field LayoutMangerField = ViewPager2.class.getDeclaredField("mLayoutManager");
                LayoutMangerField.setAccessible(true);
                LayoutMangerField.set(viewPager2, speedManger);
                Field pageTransformerAdapterField = ViewPager2.class.getDeclaredField("mPageTransformerAdapter");
                pageTransformerAdapterField.setAccessible(true);
                Object mPageTransformerAdapter = pageTransformerAdapterField.get(viewPager2);
                if (mPageTransformerAdapter != null) {
                    Field layoutManager = mPageTransformerAdapter.getClass().getDeclaredField("mLayoutManager");
                    layoutManager.setAccessible(true);
                    layoutManager.set(mPageTransformerAdapter, speedManger);
                }
                Field scrollEventAdapterField = ViewPager2.class.getDeclaredField("mScrollEventAdapter");
                scrollEventAdapterField.setAccessible(true);
                Object mScrollEventAdapter = scrollEventAdapterField.get(viewPager2);
                if (mScrollEventAdapter != null) {
                    Field layoutManager2 = mScrollEventAdapter.getClass().getDeclaredField("mLayoutManager");
                    layoutManager2.setAccessible(true);
                    layoutManager2.set(mScrollEventAdapter, speedManger);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

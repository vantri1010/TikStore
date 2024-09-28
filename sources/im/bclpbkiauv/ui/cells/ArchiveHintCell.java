package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BottomPagesView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ArchiveHintCell extends FrameLayout {
    /* access modifiers changed from: private */
    public BottomPagesView bottomPages;
    private ViewPager viewPager;

    public ArchiveHintCell(Context context) {
        super(context);
        AnonymousClass1 r0 = new ViewPager(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                requestLayout();
            }
        };
        this.viewPager = r0;
        AndroidUtilities.setViewPagerEdgeEffectColor(r0, Theme.getColor(Theme.key_actionBarDefaultArchived));
        this.viewPager.setAdapter(new Adapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ArchiveHintCell.this.bottomPages.setPageOffset(position, positionOffset);
            }

            public void onPageSelected(int i) {
                FileLog.d("test1");
            }

            public void onPageScrollStateChanged(int i) {
                FileLog.d("test1");
            }
        });
        BottomPagesView bottomPagesView = new BottomPagesView(context, this.viewPager, 3);
        this.bottomPages = bottomPagesView;
        bottomPagesView.setColor(Theme.key_chats_unreadCounterMuted, Theme.key_chats_actionBackground);
        addView(this.bottomPages, LayoutHelper.createFrame(33.0f, 5.0f, 81, 0.0f, 0.0f, 0.0f, 19.0f));
    }

    public void invalidate() {
        super.invalidate();
        this.bottomPages.invalidate();
    }

    public ViewPager getViewPager() {
        return this.viewPager;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(204.0f), 1073741824));
    }

    private class Adapter extends PagerAdapter {
        private Adapter() {
        }

        public int getCount() {
            return 3;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            ArchiveHintInnerCell innerCell = new ArchiveHintInnerCell(container.getContext(), position);
            if (innerCell.getParent() != null) {
                ((ViewGroup) innerCell.getParent()).removeView(innerCell);
            }
            container.addView(innerCell, 0);
            return innerCell;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArchiveHintCell.this.bottomPages.setCurrentPage(position);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}

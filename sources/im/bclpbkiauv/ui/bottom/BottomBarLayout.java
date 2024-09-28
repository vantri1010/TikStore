package im.bclpbkiauv.ui.bottom;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;
import java.util.List;

public class BottomBarLayout extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final String STATE_INSTANCE = "instance_state";
    private static final String STATE_ITEM = "state_item";
    private int mChildCount;
    /* access modifiers changed from: private */
    public int mCurrentItem;
    private List<BottomBarItem> mItemViews;
    /* access modifiers changed from: private */
    public boolean mSmoothScroll;
    /* access modifiers changed from: private */
    public ViewPager mViewPager;
    /* access modifiers changed from: private */
    public OnItemLongClickListner onItemLongClickListner;
    /* access modifiers changed from: private */
    public OnItemSelectedListener onItemSelectedListener;

    public interface OnItemLongClickListner {
        void onItemLongClick(BottomBarItem bottomBarItem, int i, int i2);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(BottomBarItem bottomBarItem, int i, int i2);
    }

    public BottomBarLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public BottomBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mItemViews = new ArrayList();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomBarLayout);
        this.mSmoothScroll = ta.getBoolean(0, false);
        ta.recycle();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        initBottomItem();
    }

    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        init();
    }

    private void initBottomItem() {
        this.mChildCount = getChildCount();
        int i = 0;
        while (i < this.mChildCount) {
            if (getChildAt(i) instanceof BottomBarItem) {
                BottomBarItem bottomBarItem = (BottomBarItem) getChildAt(i);
                this.mItemViews.add(bottomBarItem);
                bottomBarItem.setOnClickListener(new MyOnClickListener(i));
                bottomBarItem.setOnLongClickListener(new MyOnLongClickListener(i));
                i++;
            } else {
                throw new IllegalArgumentException("BottomBarLayout的子View必须是BottomBarItem");
            }
        }
    }

    private void init() {
        this.mItemViews.clear();
        int childCount = getChildCount();
        this.mChildCount = childCount;
        if (childCount != 0) {
            ViewPager viewPager = this.mViewPager;
            if (viewPager == null || viewPager.getAdapter().getCount() == this.mChildCount) {
                initBottomItem();
                if (this.mCurrentItem < this.mItemViews.size()) {
                    this.mItemViews.get(this.mCurrentItem).refreshTab(true);
                }
                ViewPager viewPager2 = this.mViewPager;
                if (viewPager2 != null) {
                    viewPager2.addOnPageChangeListener(this);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("LinearLayout的子View数量必须和ViewPager条目数量一致");
        }
    }

    public void addItem(BottomBarItem item) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2);
        layoutParams.weight = 1.0f;
        item.setLayoutParams(layoutParams);
        addView(item);
        init();
    }

    public void addItem(BottomBarItem item, int index) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2);
        layoutParams.weight = 1.0f;
        item.setLayoutParams(layoutParams);
        addView(item, index);
        init();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < this.mItemViews.size()) {
            if (this.mItemViews.contains(this.mItemViews.get(position))) {
                resetState();
                removeViewAt(position);
                init();
            }
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
        resetState();
        this.mItemViews.get(position).refreshTab(true);
        OnItemSelectedListener onItemSelectedListener2 = this.onItemSelectedListener;
        if (onItemSelectedListener2 != null) {
            onItemSelectedListener2.onItemSelected(getBottomItem(position), this.mCurrentItem, position);
        }
        this.mCurrentItem = position;
    }

    public void onPageScrollStateChanged(int state) {
    }

    private class MyOnClickListener implements View.OnClickListener {
        private int currentIndex;

        public MyOnClickListener(int i) {
            this.currentIndex = i;
        }

        public void onClick(View v) {
            if (BottomBarLayout.this.mViewPager == null) {
                if (BottomBarLayout.this.onItemSelectedListener != null) {
                    BottomBarLayout.this.onItemSelectedListener.onItemSelected(BottomBarLayout.this.getBottomItem(this.currentIndex), BottomBarLayout.this.mCurrentItem, this.currentIndex);
                }
                BottomBarLayout.this.updateTabState(this.currentIndex);
            } else if (this.currentIndex != BottomBarLayout.this.mCurrentItem) {
                try {
                    BottomBarLayout.this.mViewPager.setCurrentItem(this.currentIndex, BottomBarLayout.this.mSmoothScroll);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (BottomBarLayout.this.onItemSelectedListener != null) {
                BottomBarLayout.this.onItemSelectedListener.onItemSelected(BottomBarLayout.this.getBottomItem(this.currentIndex), BottomBarLayout.this.mCurrentItem, this.currentIndex);
            }
        }
    }

    private class MyOnLongClickListener implements View.OnLongClickListener {
        private int currentIndex;

        public MyOnLongClickListener(int currentIndex2) {
            this.currentIndex = currentIndex2;
        }

        public boolean onLongClick(View v) {
            if (BottomBarLayout.this.mViewPager == null || BottomBarLayout.this.onItemLongClickListner == null) {
                return false;
            }
            BottomBarLayout.this.onItemLongClickListner.onItemLongClick(BottomBarLayout.this.getBottomItem(this.currentIndex), BottomBarLayout.this.mCurrentItem, this.currentIndex);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void updateTabState(int position) {
        resetState();
        this.mCurrentItem = position;
        this.mItemViews.get(position).refreshTab(true);
    }

    private void resetState() {
        if (this.mCurrentItem < this.mItemViews.size()) {
            this.mItemViews.get(this.mCurrentItem).refreshTab(false);
        }
    }

    public void setCurrentItem(int currentItem) {
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null) {
            viewPager.setCurrentItem(currentItem, this.mSmoothScroll);
            return;
        }
        OnItemSelectedListener onItemSelectedListener2 = this.onItemSelectedListener;
        if (onItemSelectedListener2 != null) {
            onItemSelectedListener2.onItemSelected(getBottomItem(currentItem), this.mCurrentItem, currentItem);
        }
        updateTabState(currentItem);
    }

    public void setUnread(int position, int unreadNum) {
        this.mItemViews.get(position).setUnreadNum(unreadNum);
    }

    public void setMsg(int position, String msg) {
        this.mItemViews.get(position).setMsg(msg);
    }

    public void hideMsg(int position) {
        this.mItemViews.get(position).hideMsg();
    }

    public void showNotify(int position) {
        this.mItemViews.get(position).showNotify();
    }

    public void hideNotify(int position) {
        this.mItemViews.get(position).hideNotify();
    }

    public int getCurrentItem() {
        return this.mCurrentItem;
    }

    public void setSmoothScroll(boolean smoothScroll) {
        this.mSmoothScroll = smoothScroll;
    }

    public BottomBarItem getBottomItem(int position) {
        return this.mItemViews.get(position);
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_ITEM, this.mCurrentItem);
        return bundle;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mCurrentItem = bundle.getInt(STATE_ITEM);
            resetState();
            this.mItemViews.get(this.mCurrentItem).refreshTab(true);
            super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener2) {
        this.onItemSelectedListener = onItemSelectedListener2;
    }

    public void setOnItemLongClickListner(OnItemLongClickListner onItemLongClickListner2) {
        this.onItemLongClickListner = onItemLongClickListner2;
    }
}

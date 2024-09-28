package com.tablayout.transformer;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tablayout.SlidingScaleTabLayout;

public class TabScaleTransformer implements ITabScaleTransformer {
    protected boolean openDmg;
    protected SlidingScaleTabLayout slidingScaleTabLayout;
    /* access modifiers changed from: private */
    public float textSelectSize;
    /* access modifiers changed from: private */
    public float textUnSelectSize;

    public TabScaleTransformer(SlidingScaleTabLayout slidingScaleTabLayout2, float textSelectSize2, float textUnSelectSize2, boolean openDmg2) {
        this.slidingScaleTabLayout = slidingScaleTabLayout2;
        this.textSelectSize = textSelectSize2;
        this.textUnSelectSize = textUnSelectSize2;
        this.openDmg = openDmg2;
    }

    public void setNormalWidth(int position, int width, boolean isSelect) {
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i("TabScaleTransformer", "position:" + position);
        if (this.textSelectSize != this.textUnSelectSize) {
            if (this.openDmg) {
                for (int i = 0; i < this.slidingScaleTabLayout.getTabCount(); i++) {
                    if (!(i == position || i == position + 1)) {
                        changTabDmgWidth(i, 0.0f);
                    }
                }
                changeDmgSize(position, positionOffset);
                return;
            }
            for (int i2 = 0; i2 < this.slidingScaleTabLayout.getTabCount(); i2++) {
                if (!(i2 == position || i2 == position + 1)) {
                    updateTextSize(i2, 1.0f);
                }
            }
            changeTextSize(position, positionOffset);
        }
    }

    private void changeTextSize(int position, float positionOffset) {
        updateTextSize(position, positionOffset);
        if (position + 1 < this.slidingScaleTabLayout.getTabCount()) {
            updateTextSize(position + 1, 1.0f - positionOffset);
        }
    }

    private void updateTextSize(int position, final float positionOffset) {
        final TextView currentTab = this.slidingScaleTabLayout.getTitle(position);
        currentTab.post(new Runnable() {
            public void run() {
                int textSize = (int) (TabScaleTransformer.this.textSelectSize - Math.abs((TabScaleTransformer.this.textSelectSize - TabScaleTransformer.this.textUnSelectSize) * positionOffset));
                if (currentTab.getTextSize() != ((float) textSize)) {
                    currentTab.setTextSize(0, (float) textSize);
                    currentTab.requestLayout();
                }
            }
        });
    }

    private void changeDmgSize(final int position, final float positionOffset) {
        this.slidingScaleTabLayout.post(new Runnable() {
            public void run() {
                TabScaleTransformer.this.changTabDmgWidth(position, 1.0f - positionOffset);
                if (position + 1 < TabScaleTransformer.this.slidingScaleTabLayout.getTabCount()) {
                    TabScaleTransformer.this.changTabDmgWidth(position + 1, positionOffset);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void changTabDmgWidth(int position, float scale) {
        ImageView currentTabDmg = this.slidingScaleTabLayout.getDmgView(position);
        if (currentTabDmg != null && currentTabDmg.getDrawable() != null) {
            ViewGroup.LayoutParams params = currentTabDmg.getLayoutParams();
            int width = (int) (((float) currentTabDmg.getMinimumWidth()) + (((float) (currentTabDmg.getMaxWidth() - currentTabDmg.getMinimumWidth())) * scale));
            if (params.width != width) {
                params.width = width;
                currentTabDmg.setLayoutParams(params);
            }
        }
    }
}

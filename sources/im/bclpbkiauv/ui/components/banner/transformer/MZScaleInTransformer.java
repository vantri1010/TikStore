package im.bclpbkiauv.ui.components.banner.transformer;

import android.view.View;
import android.view.ViewParent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class MZScaleInTransformer extends BasePageTransformer {
    private static final float DEFAULT_MIN_SCALE = 0.85f;
    private float mMinScale = DEFAULT_MIN_SCALE;

    public MZScaleInTransformer() {
    }

    public MZScaleInTransformer(float minScale) {
        this.mMinScale = minScale;
    }

    public void transformPage(View view, float position) {
        View view2 = view;
        ViewPager2 viewPager = requireViewPager(view);
        float paddingLeft = (float) viewPager.getPaddingLeft();
        float currentPos = position - (paddingLeft / ((((float) viewPager.getMeasuredWidth()) - paddingLeft) - ((float) viewPager.getPaddingRight())));
        float f = this.mMinScale;
        float reduceX = ((1.0f - f) * ((float) view.getWidth())) / 2.0f;
        if (currentPos <= -1.0f) {
            view2.setTranslationX(reduceX);
            view2.setScaleX(this.mMinScale);
            view2.setScaleY(this.mMinScale);
        } else if (((double) currentPos) <= 1.0d) {
            float scale = (1.0f - f) * Math.abs(1.0f - Math.abs(currentPos));
            float translationX = (-reduceX) * currentPos;
            if (((double) currentPos) <= -0.5d) {
                view2.setTranslationX((Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f) + translationX);
            } else if (currentPos <= 0.0f) {
                view2.setTranslationX(translationX);
            } else if (((double) currentPos) >= 0.5d) {
                view2.setTranslationX(translationX - (Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f));
            } else {
                view2.setTranslationX(translationX);
            }
            view2.setScaleX(this.mMinScale + scale);
            view2.setScaleY(this.mMinScale + scale);
        } else {
            view2.setScaleX(f);
            view2.setScaleY(this.mMinScale);
            view2.setTranslationX(-reduceX);
        }
    }

    private ViewPager2 requireViewPager(View page) {
        ViewParent parent = page.getParent();
        ViewParent parentParent = parent.getParent();
        if ((parent instanceof RecyclerView) && (parentParent instanceof ViewPager2)) {
            return (ViewPager2) parentParent;
        }
        throw new IllegalStateException("Expected the page view to be managed by a ViewPager2 instance.");
    }
}

package im.bclpbkiauv.ui.hviews.helper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.core.content.ContextCompat;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MryViewHelper {
    private static final int[] APPCOMPAT_CHECK_ATTRS = {R.attr.colorPrimary};
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        boolean failed = !a.hasValue(0);
        a.recycle();
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme (or descendant) with the design library.");
        }
    }

    public static View getActivityRoot(Activity activity) {
        return ((ViewGroup) activity.findViewById(16908290)).getChildAt(0);
    }

    public static void requestApplyInsets(Window window) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            window.getDecorView().requestFitSystemWindows();
        } else if (Build.VERSION.SDK_INT >= 21) {
            window.getDecorView().requestApplyInsets();
        }
    }

    public static void expendTouchArea(final View view, final int expendSize) {
        if (view != null) {
            final View parentView = (View) view.getParent();
            parentView.post(new Runnable() {
                public void run() {
                    Rect rect = new Rect();
                    view.getHitRect(rect);
                    rect.left -= expendSize;
                    rect.top -= expendSize;
                    rect.right += expendSize;
                    rect.bottom += expendSize;
                    parentView.setTouchDelegate(new TouchDelegate(rect, view));
                }
            });
        }
    }

    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void setBackgroundKeepingPadding(View view, Drawable drawable) {
        int[] padding = {view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackground(drawable);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    public static void setBackgroundKeepingPadding(View view, int backgroundResId) {
        setBackgroundKeepingPadding(view, ContextCompat.getDrawable(view.getContext(), backgroundResId));
    }

    public static void setBackgroundColorKeepPadding(View view, int color) {
        int[] padding = {view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
        view.setBackgroundColor(color);
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    public static void playBackgroundBlinkAnimation(View v, int bgColor) {
        if (v != null) {
            playViewBackgroundAnimation(v, bgColor, new int[]{0, 255, 0}, 300);
        }
    }

    public static Animator playViewBackgroundAnimation(final View v, int bgColor, int[] alphaArray, int stepDuration, final Runnable endAction) {
        int animationCount = alphaArray.length - 1;
        Drawable bgDrawable = new ColorDrawable(bgColor);
        final Drawable oldBgDrawable = v.getBackground();
        setBackgroundKeepingPadding(v, bgDrawable);
        List<Animator> animatorList = new ArrayList<>();
        for (int i = 0; i < animationCount; i++) {
            animatorList.add(ObjectAnimator.ofInt(v.getBackground(), "alpha", new int[]{alphaArray[i], alphaArray[i + 1]}));
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration((long) stepDuration);
        animatorSet.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                MryViewHelper.setBackgroundKeepingPadding(v, oldBgDrawable);
                Runnable runnable = endAction;
                if (runnable != null) {
                    runnable.run();
                }
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.playSequentially(animatorList);
        animatorSet.start();
        return animatorSet;
    }

    public static void playViewBackgroundAnimation(View v, int bgColor, int[] alphaArray, int stepDuration) {
        playViewBackgroundAnimation(v, bgColor, alphaArray, stepDuration, (Runnable) null);
    }

    public static void playViewBackgroundAnimation(final View v, int startColor, int endColor, long duration, int repeatCount, int setAnimTagId, final Runnable endAction) {
        final Drawable oldBgDrawable = v.getBackground();
        setBackgroundColorKeepPadding(v, startColor);
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(new int[]{startColor, endColor});
        anim.setDuration(duration / ((long) (repeatCount + 1)));
        anim.setRepeatCount(repeatCount);
        anim.setRepeatMode(2);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                MryViewHelper.setBackgroundColorKeepPadding(v, ((Integer) animation.getAnimatedValue()).intValue());
            }
        });
        if (setAnimTagId != 0) {
            v.setTag(setAnimTagId, anim);
        }
        anim.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                MryViewHelper.setBackgroundKeepingPadding(v, oldBgDrawable);
                Runnable runnable = endAction;
                if (runnable != null) {
                    runnable.run();
                }
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
        anim.start();
    }

    public static void playViewBackgroundAnimation(View v, int startColor, int endColor, long duration) {
        playViewBackgroundAnimation(v, startColor, endColor, duration, 0, 0, (Runnable) null);
    }

    public static int generateViewId() {
        int result;
        int newValue;
        if (Build.VERSION.SDK_INT >= 17) {
            return View.generateViewId();
        }
        do {
            result = sNextGeneratedId.get();
            newValue = result + 1;
            if (newValue > 16777215) {
                newValue = 1;
            }
        } while (!sNextGeneratedId.compareAndSet(result, newValue));
        return result;
    }

    public static AlphaAnimation fadeIn(View view, int duration, Animation.AnimationListener listener, boolean isNeedAnimation) {
        if (view == null) {
            return null;
        }
        if (isNeedAnimation) {
            view.setVisibility(0);
            AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
            alpha.setInterpolator(new DecelerateInterpolator());
            alpha.setDuration((long) duration);
            alpha.setFillAfter(true);
            if (listener != null) {
                alpha.setAnimationListener(listener);
            }
            view.startAnimation(alpha);
            return alpha;
        }
        view.setAlpha(1.0f);
        view.setVisibility(0);
        return null;
    }

    public static AlphaAnimation fadeOut(final View view, int duration, final Animation.AnimationListener listener, boolean isNeedAnimation) {
        if (view == null) {
            return null;
        }
        if (isNeedAnimation) {
            AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
            alpha.setInterpolator(new DecelerateInterpolator());
            alpha.setDuration((long) duration);
            alpha.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    Animation.AnimationListener animationListener = listener;
                    if (animationListener != null) {
                        animationListener.onAnimationStart(animation);
                    }
                }

                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(8);
                    Animation.AnimationListener animationListener = listener;
                    if (animationListener != null) {
                        animationListener.onAnimationEnd(animation);
                    }
                }

                public void onAnimationRepeat(Animation animation) {
                    Animation.AnimationListener animationListener = listener;
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat(animation);
                    }
                }
            });
            view.startAnimation(alpha);
            return alpha;
        }
        view.setVisibility(8);
        return null;
    }

    public static void clearValueAnimator(Animator animator) {
        if (animator != null) {
            animator.removeAllListeners();
            if (animator instanceof ValueAnimator) {
                ((ValueAnimator) animator).removeAllUpdateListeners();
            }
            if (Build.VERSION.SDK_INT >= 19) {
                animator.pause();
            }
            animator.cancel();
        }
    }

    public static Rect calcViewScreenLocation(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
    }

    public static TranslateAnimation slideIn(View view, int duration, Animation.AnimationListener listener, boolean isNeedAnimation, MryDirection direction) {
        View view2 = view;
        Animation.AnimationListener animationListener = listener;
        if (view2 == null) {
            return null;
        }
        if (isNeedAnimation) {
            TranslateAnimation translate = null;
            int i = AnonymousClass7.$SwitchMap$im$bclpbkiauv$ui$hviews$helper$MryDirection[direction.ordinal()];
            if (i == 1) {
                translate = new TranslateAnimation(1, -1.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
            } else if (i == 2) {
                translate = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, -1.0f, 1, 0.0f);
            } else if (i == 3) {
                translate = new TranslateAnimation(1, 1.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
            } else if (i == 4) {
                translate = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.0f, 1, 0.0f);
            }
            translate.setInterpolator(new DecelerateInterpolator());
            translate.setDuration((long) duration);
            translate.setFillAfter(true);
            if (animationListener != null) {
                translate.setAnimationListener(animationListener);
            }
            view2.setVisibility(0);
            view2.startAnimation(translate);
            return translate;
        }
        int i2 = duration;
        view.clearAnimation();
        view2.setVisibility(0);
        return null;
    }

    /* renamed from: im.bclpbkiauv.ui.hviews.helper.MryViewHelper$7  reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$im$bclpbkiauv$ui$hviews$helper$MryDirection;

        static {
            int[] iArr = new int[MryDirection.values().length];
            $SwitchMap$im$bclpbkiauv$ui$hviews$helper$MryDirection = iArr;
            try {
                iArr[MryDirection.LEFT_TO_RIGHT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$hviews$helper$MryDirection[MryDirection.TOP_TO_BOTTOM.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$hviews$helper$MryDirection[MryDirection.RIGHT_TO_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$im$bclpbkiauv$ui$hviews$helper$MryDirection[MryDirection.BOTTOM_TO_TOP.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static TranslateAnimation slideOut(final View view, int duration, final Animation.AnimationListener listener, boolean isNeedAnimation, MryDirection direction) {
        if (view == null) {
            return null;
        }
        if (isNeedAnimation) {
            TranslateAnimation translate = null;
            int i = AnonymousClass7.$SwitchMap$im$bclpbkiauv$ui$hviews$helper$MryDirection[direction.ordinal()];
            if (i == 1) {
                translate = new TranslateAnimation(1, 0.0f, 1, 1.0f, 1, 0.0f, 1, 0.0f);
            } else if (i == 2) {
                translate = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 0.0f, 1, 1.0f);
            } else if (i == 3) {
                translate = new TranslateAnimation(1, 0.0f, 1, -1.0f, 1, 0.0f, 1, 0.0f);
            } else if (i == 4) {
                translate = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 0.0f, 1, -1.0f);
            }
            translate.setInterpolator(new DecelerateInterpolator());
            translate.setDuration((long) duration);
            Animation.AnimationListener animationListener = listener;
            translate.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    Animation.AnimationListener animationListener = listener;
                    if (animationListener != null) {
                        animationListener.onAnimationStart(animation);
                    }
                }

                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(8);
                    Animation.AnimationListener animationListener = listener;
                    if (animationListener != null) {
                        animationListener.onAnimationEnd(animation);
                    }
                }

                public void onAnimationRepeat(Animation animation) {
                    Animation.AnimationListener animationListener = listener;
                    if (animationListener != null) {
                        animationListener.onAnimationRepeat(animation);
                    }
                }
            });
            view.startAnimation(translate);
            return translate;
        }
        int i2 = duration;
        Animation.AnimationListener animationListener2 = listener;
        view.clearAnimation();
        view.setVisibility(8);
        return null;
    }

    public static void setPaddingLeft(View view, int value) {
        if (value != view.getPaddingLeft()) {
            view.setPadding(value, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void setPaddingTop(View view, int value) {
        if (value != view.getPaddingTop()) {
            view.setPadding(view.getPaddingLeft(), value, view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void setPaddingRight(View view, int value) {
        if (value != view.getPaddingRight()) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), value, view.getPaddingBottom());
        }
    }

    public static void setPaddingBottom(View view, int value) {
        if (value != view.getPaddingBottom()) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), value);
        }
    }

    public static boolean getIsLastLineSpacingExtraError() {
        return Build.VERSION.SDK_INT < 21;
    }

    public static View findViewFromViewStub(View parentView, int viewStubId, int inflatedViewId) {
        if (parentView == null) {
            return null;
        }
        View view = parentView.findViewById(inflatedViewId);
        if (view != null) {
            return view;
        }
        ViewStub vs = (ViewStub) parentView.findViewById(viewStubId);
        if (vs == null) {
            return null;
        }
        View view2 = vs.inflate();
        if (view2 != null) {
            return view2.findViewById(inflatedViewId);
        }
        return view2;
    }

    public static View findViewFromViewStub(View parentView, int viewStubId, int inflatedViewId, int inflateLayoutResId) {
        if (parentView == null) {
            return null;
        }
        View view = parentView.findViewById(inflatedViewId);
        if (view != null) {
            return view;
        }
        ViewStub vs = (ViewStub) parentView.findViewById(viewStubId);
        if (vs == null) {
            return null;
        }
        if (vs.getLayoutResource() < 1 && inflateLayoutResId > 0) {
            vs.setLayoutResource(inflateLayoutResId);
        }
        View view2 = vs.inflate();
        if (view2 != null) {
            return view2.findViewById(inflatedViewId);
        }
        return view2;
    }

    public static void safeSetImageViewSelected(ImageView imageView, boolean selected) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            imageView.setSelected(selected);
            if (drawable.getIntrinsicWidth() != drawableWidth || drawable.getIntrinsicHeight() != drawableHeight) {
                imageView.requestLayout();
            }
        }
    }

    @Deprecated
    public static ColorFilter setImageViewTintColor(ImageView imageView, int tintColor) {
        LightingColorFilter colorFilter = new LightingColorFilter(Color.argb(255, 0, 0, 0), tintColor);
        imageView.setColorFilter(colorFilter);
        return colorFilter;
    }

    public static boolean isListViewAlreadyAtBottom(ListView listView) {
        View lastItemView;
        if (listView.getAdapter() == null || listView.getHeight() == 0 || listView.getLastVisiblePosition() != listView.getAdapter().getCount() - 1 || (lastItemView = listView.getChildAt(listView.getChildCount() - 1)) == null || lastItemView.getBottom() != listView.getHeight()) {
            return false;
        }
        return true;
    }

    public static void getDescendantRect(ViewGroup parent, View descendant, Rect out) {
        out.set(0, 0, descendant.getWidth(), descendant.getHeight());
        ViewGroupHelper.offsetDescendantRect(parent, descendant, out);
    }

    private static class ViewGroupHelper {
        private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal<>();
        private static final ThreadLocal<RectF> sRectF = new ThreadLocal<>();

        private ViewGroupHelper() {
        }

        public static void offsetDescendantRect(ViewGroup group, View child, Rect rect) {
            Matrix m = sMatrix.get();
            if (m == null) {
                m = new Matrix();
                sMatrix.set(m);
            } else {
                m.reset();
            }
            offsetDescendantMatrix(group, child, m);
            RectF rectF = sRectF.get();
            if (rectF == null) {
                rectF = new RectF();
                sRectF.set(rectF);
            }
            rectF.set(rect);
            m.mapRect(rectF);
            rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f), (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
        }

        static void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
            ViewParent parent = view.getParent();
            if ((parent instanceof View) && parent != target) {
                View vp = (View) parent;
                offsetDescendantMatrix(target, vp, m);
                m.preTranslate((float) (-vp.getScrollX()), (float) (-vp.getScrollY()));
            }
            m.preTranslate((float) view.getLeft(), (float) view.getTop());
            if (!view.getMatrix().isIdentity()) {
                m.preConcat(view.getMatrix());
            }
        }
    }
}

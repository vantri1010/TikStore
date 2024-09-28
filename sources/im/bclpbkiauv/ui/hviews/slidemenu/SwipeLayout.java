package im.bclpbkiauv.ui.hviews.slidemenu;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class SwipeLayout extends FrameLayout implements View.OnTouchListener, View.OnClickListener {
    private static final long ANIMATION_MAX_DURATION = 300;
    private static final long ANIMATION_MIN_DURATION = 100;
    public static final int ITEM_STATE_COLLAPSED = 2;
    public static final int ITEM_STATE_LEFT_EXPAND = 0;
    public static final int ITEM_STATE_RIGHT_EXPAND = 1;
    private static final int NO_ID = 0;
    private static final String TAG = "SwipeLayout";
    private static Typeface typeface;
    private boolean autoHideSwipe;
    private boolean canFullSwipeFromLeft;
    private boolean canFullSwipeFromRight;
    private WeightAnimation collapseAnim;
    private Animation.AnimationListener collapseListener;
    boolean directionLeft;
    private int dividerWidth;
    float downRawX;
    long downTime;
    float downX;
    float downY;
    private WeightAnimation expandAnim;
    private int fullSwipeEdgePadding;
    private int iconSize;
    int id;
    boolean invokedFromLeft;
    private int itemWidth;
    long lastTime;
    private int layoutId;
    private int[] leftColors;
    private int[] leftIconColors;
    private int[] leftIcons;
    private int leftLayoutMaxWidth;
    private LinearLayout leftLinear;
    private LinearLayout leftLinearWithoutFirst;
    private int[] leftTextColors;
    private String[] leftTexts;
    private View[] leftViews;
    private Handler longClickHandler;
    boolean longClickPerformed;
    private Runnable longClickRunnable;
    /* access modifiers changed from: private */
    public View mainLayout;
    boolean movementStarted;
    private boolean needDivderBetweenMainAndMenu;
    private RecyclerView.OnScrollListener onScrollListener;
    private OnSwipeItemClickListener onSwipeItemClickListener;
    private boolean onlyOneSwipe;
    float prevRawX;
    private int[] rightColors;
    private int[] rightIconColors;
    private int[] rightIcons;
    private int rightLayoutMaxWidth;
    private LinearLayout rightLinear;
    private LinearLayout rightLinearWithoutLast;
    private int[] rightTextColors;
    private String[] rightTexts;
    private View[] rightViews;
    boolean shouldPerformLongClick;
    float speed;
    private boolean swipeEnabled;
    private float textSize;
    private int textTopMargin;

    public interface OnSwipeItemClickListener {
        void onSwipeItemClick(boolean z, int i);
    }

    public void setLeftColors(int[] leftColors2) {
        this.leftColors = leftColors2;
    }

    public SwipeLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public void setOnSwipeItemClickListener(OnSwipeItemClickListener onSwipeItemClickListener2) {
        this.onSwipeItemClickListener = onSwipeItemClickListener2;
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.itemWidth = 100;
        this.textTopMargin = 20;
        this.swipeEnabled = true;
        this.autoHideSwipe = true;
        this.onlyOneSwipe = true;
        this.needDivderBetweenMainAndMenu = true;
        this.dividerWidth = AndroidUtilities.dp(10.0f);
        this.prevRawX = -1.0f;
        this.longClickHandler = new Handler();
        this.longClickRunnable = new Runnable() {
            public void run() {
                if (SwipeLayout.this.shouldPerformLongClick && SwipeLayout.this.performLongClick()) {
                    SwipeLayout.this.longClickPerformed = true;
                    SwipeLayout.this.setPressed(false);
                }
            }
        };
        this.collapseListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                SwipeLayout.this.clickBySwipe();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        };
        this.fullSwipeEdgePadding = AndroidUtilities.dp(100.0f);
        if (attrs != null) {
            setUpAttrs(attrs);
        }
        setUpView();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAutoHideSwipe(this.autoHideSwipe);
        setOnlyOneSwipe(this.onlyOneSwipe);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        setItemState(2, false);
        super.onDetachedFromWindow();
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (this.mainLayout != null) {
            super.addView(child, index, params);
            return;
        }
        this.mainLayout = child;
        setUpView();
    }

    public void setUpView(View layout) {
        this.mainLayout = layout;
        compareArrays(this.leftColors, this.leftIcons);
        compareArrays(this.rightColors, this.rightIcons);
        compareArrays(this.leftIconColors, this.leftIcons);
        compareArrays(this.rightIconColors, this.rightIcons);
        addView(this.mainLayout);
        createItemLayouts();
        this.mainLayout.bringToFront();
        this.mainLayout.setOnTouchListener(this);
    }

    public View getMainLayout() {
        return this.mainLayout;
    }

    private void setUpView() {
        if (this.layoutId != 0) {
            this.mainLayout = LayoutInflater.from(getContext()).inflate(this.layoutId, (ViewGroup) null);
        }
        if (this.mainLayout != null) {
            compareArrays(this.leftColors, this.leftIcons);
            compareArrays(this.rightColors, this.rightIcons);
            compareArrays(this.leftIconColors, this.leftIcons);
            compareArrays(this.rightIconColors, this.rightIcons);
            addView(this.mainLayout);
            createItemLayouts();
            this.mainLayout.bringToFront();
            this.mainLayout.setOnTouchListener(this);
        }
    }

    private void compareArrays(int[] arr1, int[] arr2) {
        if (arr1 != null && arr2 != null && arr1.length < arr2.length) {
            throw new IllegalStateException("Drawable array shouldn't be bigger than color array");
        }
    }

    public void invalidateSwipeItems() {
        createItemLayouts();
    }

    public void setItemWidth(int width) {
        this.itemWidth = width;
    }

    public void rebuildLayout() {
        createItemLayouts();
        this.mainLayout.bringToFront();
        this.mainLayout.setOnTouchListener(this);
    }

    private void createItemLayouts() {
        int leftSize;
        int rightSize;
        int[] iArr = this.leftIcons;
        if (iArr != null) {
            leftSize = iArr.length;
        } else {
            String[] strArr = this.leftTexts;
            if (strArr != null) {
                leftSize = strArr.length;
            } else {
                leftSize = 0;
            }
        }
        int[] iArr2 = this.rightIcons;
        if (iArr2 != null) {
            rightSize = iArr2.length;
        } else {
            String[] strArr2 = this.rightTexts;
            if (strArr2 != null) {
                rightSize = strArr2.length;
            } else {
                rightSize = 0;
            }
        }
        if (!(this.rightIcons == null && this.rightTexts == null)) {
            this.rightLayoutMaxWidth = this.itemWidth * rightSize;
            LinearLayout linearLayout = this.rightLinear;
            if (linearLayout != null) {
                removeView(linearLayout);
            }
            LinearLayout createLinearLayout = createLinearLayout(5);
            this.rightLinear = createLinearLayout;
            if (this.needDivderBetweenMainAndMenu) {
                createLinearLayout.addView(getDividerView());
                this.rightLayoutMaxWidth += this.dividerWidth;
            }
            LinearLayout createLinearLayout2 = createLinearLayout(5);
            this.rightLinearWithoutLast = createLinearLayout2;
            createLinearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, -1, (float) (rightSize - 1)));
            addView(this.rightLinear);
            this.rightViews = new View[rightSize];
            this.rightLinear.addView(this.rightLinearWithoutLast);
            addSwipeItems(rightSize, this.rightIcons, this.rightIconColors, this.rightColors, this.rightTexts, this.rightTextColors, this.rightLinear, this.rightLinearWithoutLast, this.rightViews, false);
        }
        if (this.leftIcons != null || this.leftTexts != null) {
            this.leftLayoutMaxWidth = this.itemWidth * leftSize;
            LinearLayout linearLayout2 = this.leftLinear;
            if (linearLayout2 != null) {
                removeView(linearLayout2);
            }
            this.leftLinear = createLinearLayout(3);
            LinearLayout createLinearLayout3 = createLinearLayout(3);
            this.leftLinearWithoutFirst = createLinearLayout3;
            createLinearLayout3.setLayoutParams(new LinearLayout.LayoutParams(0, -1, (float) (leftSize - 1)));
            this.leftViews = new View[leftSize];
            addView(this.leftLinear);
            addSwipeItems(leftSize, this.leftIcons, this.leftIconColors, this.leftColors, this.leftTexts, this.leftTextColors, this.leftLinear, this.leftLinearWithoutFirst, this.leftViews, true);
            this.leftLinear.addView(this.leftLinearWithoutFirst);
            if (this.needDivderBetweenMainAndMenu) {
                this.leftLinear.addView(getDividerView());
                this.leftLayoutMaxWidth += this.dividerWidth;
            }
        }
    }

    private View getDividerView() {
        View diverView = new View(getContext());
        diverView.setLayoutParams(new FrameLayout.LayoutParams(this.dividerWidth, -1));
        diverView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        return diverView;
    }

    public void setDividerWidth(int dividerWidth2) {
        this.dividerWidth = dividerWidth2;
    }

    private void addSwipeItems(int size, int[] icons, int[] iconColors, int[] backgroundColors, String[] texts, int[] textColors, LinearLayout layout, LinearLayout layoutWithout, View[] views, boolean left) {
        int backgroundColor;
        int iconColor;
        String txt;
        int textColor;
        int i = size;
        for (int i2 = 0; i2 < i; i2++) {
            int icon = 0;
            if (icons != null) {
                icon = icons[i2];
            }
            if (backgroundColors != null) {
                backgroundColor = backgroundColors[i2];
            } else {
                backgroundColor = 0;
            }
            if (iconColors != null) {
                iconColor = iconColors[i2];
            } else {
                iconColor = 0;
            }
            if (texts != null) {
                txt = texts[i2];
            } else {
                txt = null;
            }
            if (textColors != null) {
                textColor = textColors[i2];
            } else {
                textColor = 0;
            }
            ViewGroup swipeItem = createSwipeItem(icon, iconColor, backgroundColor, txt, textColor, left);
            int i3 = 1;
            swipeItem.setClickable(true);
            swipeItem.setFocusable(true);
            swipeItem.setOnClickListener(this);
            views[i2] = swipeItem;
            if (left) {
                i3 = i;
            }
            if (i2 == i - i3) {
                layout.addView(swipeItem);
                LinearLayout linearLayout = layoutWithout;
            } else {
                LinearLayout linearLayout2 = layout;
                layoutWithout.addView(swipeItem);
            }
        }
        LinearLayout linearLayout3 = layout;
        LinearLayout linearLayout4 = layoutWithout;
    }

    public void setAlphaAtIndex(boolean left, int index, float alpha) {
        View[] views = left ? this.leftViews : this.rightViews;
        if (index <= views.length - 1) {
            views[index].setAlpha(alpha);
        }
    }

    public void setEnableAtIndex(boolean left, int index, boolean enabled) {
        View[] views = left ? this.leftViews : this.rightViews;
        if (index <= views.length - 1) {
            views[index].setEnabled(enabled);
        }
    }

    public void setTextAtIndex(boolean left, int index, String text) {
        View[] views = left ? this.leftViews : this.rightViews;
        if (index <= views.length - 1) {
            ((TextView) views[index].findViewWithTag("text")).setText(text);
        }
    }

    public void setIconAtIndex(boolean left, int index, int resId) {
        View[] views = left ? this.leftViews : this.rightViews;
        if (index <= views.length - 1) {
            ((ImageView) views[index].findViewWithTag("icon")).setImageResource(resId);
        }
    }

    public float getAlphaAtIndex(boolean left, int index) {
        View[] views = left ? this.leftViews : this.rightViews;
        if (index <= views.length - 1) {
            return views[index].getAlpha();
        }
        return 1.0f;
    }

    public void setTexts(boolean left, String[] texts) {
        View[] views = left ? this.leftViews : this.rightViews;
        if (views == null) {
            return;
        }
        if (views == null || views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                ((TextView) views[i].findViewWithTag("text")).setText(texts[i]);
            }
        }
    }

    public void setIcons(boolean left, int[] icons) {
        View[] views = left ? this.leftViews : this.rightViews;
        if (views == null) {
            return;
        }
        if (views == null || views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                ((ImageView) views[i].findViewWithTag("icon")).setImageResource(icons[i]);
            }
        }
    }

    public boolean isEnabledAtIndex(boolean left, int index) {
        View[] views = left ? this.leftViews : this.rightViews;
        if (index <= views.length - 1) {
            return views[index].isEnabled();
        }
        return true;
    }

    public View[] getRightViews() {
        return this.rightViews;
    }

    public View[] getLeftViews() {
        return this.leftViews;
    }

    public void setOnClickListener(View.OnClickListener l) {
        this.mainLayout.setOnClickListener(l);
    }

    public void setBackgroundSelector(int color) {
        this.mainLayout.setBackground(Theme.getSelectorDrawable(false));
    }

    private Drawable getRippleDrawable() {
        TypedArray ta = getContext().obtainStyledAttributes(new int[]{16843534});
        Drawable ripple = ta.getDrawable(0);
        ta.recycle();
        return ripple;
    }

    private ViewGroup createSwipeItem(int icon, int iconColor, int backgroundColor, String text, int textColor, boolean left) {
        int gravity;
        int i = icon;
        int i2 = iconColor;
        int i3 = backgroundColor;
        String str = text;
        int i4 = textColor;
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
        if (Build.VERSION.SDK_INT >= 16) {
            View view = new View(getContext());
            view.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
            view.setBackground(getRippleDrawable());
            frameLayout.addView(view);
        }
        if (i3 != 0) {
            frameLayout.setBackgroundColor(i3);
        }
        ImageView imageView = null;
        if (i != 0) {
            imageView = new ImageView(getContext());
            imageView.setTag("icon");
            Drawable drawable = ContextCompat.getDrawable(getContext(), i);
            if (i2 != 0) {
                drawable = Utils.setTint(drawable, i2);
            }
            imageView.setImageDrawable(drawable);
        }
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        if (left) {
            gravity = 16 | 5;
        } else {
            gravity = 16 | 3;
        }
        relativeLayout.setLayoutParams(new FrameLayout.LayoutParams(this.itemWidth, -2, gravity));
        if (imageView != null) {
            int i5 = this.iconSize;
            RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(i5, i5);
            imageViewParams.addRule(14, -1);
            imageView.setLayoutParams(imageViewParams);
            int i6 = this.id + 1;
            this.id = i6;
            imageView.setId(i6);
            relativeLayout.addView(imageView);
        }
        if (str != null) {
            TextView textView = new TextView(getContext());
            textView.setMaxLines(2);
            float f = this.textSize;
            if (f > 0.0f) {
                textView.setTextSize(0, f);
            }
            if (i4 != 0) {
                textView.setTextColor(i4);
            }
            Typeface typeface2 = typeface;
            if (typeface2 != null) {
                textView.setTypeface(typeface2);
            }
            textView.setText(str);
            textView.setTag("text");
            textView.setGravity(17);
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(this.itemWidth, -2);
            if (imageView != null) {
                textViewParams.addRule(3, this.id);
                textViewParams.topMargin = this.textTopMargin;
            } else {
                textViewParams.addRule(13, this.id);
            }
            relativeLayout.addView(textView, textViewParams);
        }
        frameLayout.setOnTouchListener(this);
        frameLayout.addView(relativeLayout);
        return frameLayout;
    }

    private LinearLayout createLinearLayout(int gravity) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(0);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0, -1);
        params.gravity = gravity;
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    private void setUpAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
        if (array != null) {
            this.layoutId = array.getResourceId(4, 0);
            this.itemWidth = array.getDimensionPixelSize(17, 100);
            this.iconSize = array.getDimensionPixelSize(5, -1);
            this.textSize = (float) array.getDimensionPixelSize(18, 0);
            this.textTopMargin = array.getDimensionPixelSize(19, 14);
            this.canFullSwipeFromRight = array.getBoolean(2, false);
            this.canFullSwipeFromLeft = array.getBoolean(1, false);
            this.onlyOneSwipe = array.getBoolean(11, true);
            this.autoHideSwipe = array.getBoolean(0, true);
            int rightIconsRes = array.getResourceId(14, 0);
            int rightIconColors2 = array.getResourceId(12, 0);
            int rightTextRes = array.getResourceId(15, 0);
            int rightTextColorRes = array.getResourceId(16, 0);
            int rightColorsRes = array.getResourceId(13, 0);
            int leftIconsRes = array.getResourceId(8, 0);
            int leftIconColors2 = array.getResourceId(6, 0);
            int leftTextRes = array.getResourceId(9, 0);
            int leftTextColorRes = array.getResourceId(10, 0);
            int leftColorsRes = array.getResourceId(7, 0);
            String typefaceAssetPath = array.getString(3);
            if (typefaceAssetPath != null && typeface == null) {
                typeface = Typeface.createFromAsset(getContext().getAssets(), typefaceAssetPath);
            }
            String str = typefaceAssetPath;
            initiateArrays(rightColorsRes, rightIconsRes, leftColorsRes, leftIconsRes, leftTextRes, rightTextRes, leftTextColorRes, rightTextColorRes, leftIconColors2, rightIconColors2);
            array.recycle();
        }
    }

    private void initiateArrays(int rightColorsRes, int rightIconsRes, int leftColorsRes, int leftIconsRes, int leftTextRes, int rightTextRes, int leftTextColorRes, int rightTextColorRes, int leftIconColorsRes, int rightIconColorsRes) {
        Resources res = getResources();
        if (rightColorsRes != 0) {
            this.rightColors = res.getIntArray(rightColorsRes);
        }
        if (rightIconsRes != 0 && !isInEditMode()) {
            this.rightIcons = fillDrawables(res.obtainTypedArray(rightIconsRes));
        }
        if (leftColorsRes != 0) {
            this.leftColors = res.getIntArray(leftColorsRes);
        }
        if (leftIconsRes != 0 && !isInEditMode()) {
            this.leftIcons = fillDrawables(res.obtainTypedArray(leftIconsRes));
        }
        if (leftTextRes != 0) {
            this.leftTexts = res.getStringArray(leftTextRes);
        }
        if (rightTextRes != 0) {
            this.rightTexts = res.getStringArray(rightTextRes);
        }
        if (leftTextColorRes != 0) {
            this.leftTextColors = res.getIntArray(leftTextColorRes);
        }
        if (rightTextColorRes != 0) {
            this.rightTextColors = res.getIntArray(rightTextColorRes);
        }
        if (leftIconColorsRes != 0) {
            this.leftIconColors = res.getIntArray(leftIconColorsRes);
        }
        if (rightIconColorsRes != 0) {
            this.rightIconColors = res.getIntArray(rightIconColorsRes);
        }
    }

    public void setLeftIcons(int... leftIcons2) {
        this.leftIcons = leftIcons2;
    }

    public void setLeftIconColors(int... leftIconColors2) {
        this.leftIconColors = leftIconColors2;
    }

    public void setRightColors(int... rightColors2) {
        this.rightColors = rightColors2;
    }

    public void setRightIcons(int... rightIcons2) {
        this.rightIcons = rightIcons2;
    }

    public void setIconSize(int size) {
        this.iconSize = size;
    }

    public void setRightIconColors(int... rightIconColors2) {
        this.rightIconColors = rightIconColors2;
    }

    public void setRightTextColors(int... rightTextColors2) {
        this.rightTextColors = rightTextColors2;
    }

    public void setLeftTextColors(int... leftTextColors2) {
        this.leftTextColors = leftTextColors2;
    }

    public void setLeftTexts(String... leftTexts2) {
        this.leftTexts = leftTexts2;
    }

    public void setRightTexts(String... rightTexts2) {
        this.rightTexts = rightTexts2;
    }

    public void setTextSize(float size) {
        this.textSize = size;
    }

    private int[] fillDrawables(TypedArray ta) {
        int[] drawableArr = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            drawableArr[i] = ta.getResourceId(i, 0);
        }
        ta.recycle();
        return drawableArr;
    }

    private void clearAnimations() {
        this.mainLayout.clearAnimation();
        LinearLayout linearLayout = this.rightLinear;
        if (linearLayout != null) {
            linearLayout.clearAnimation();
        }
        LinearLayout linearLayout2 = this.leftLinear;
        if (linearLayout2 != null) {
            linearLayout2.clearAnimation();
        }
        LinearLayout linearLayout3 = this.rightLinearWithoutLast;
        if (linearLayout3 != null) {
            linearLayout3.clearAnimation();
        }
        LinearLayout linearLayout4 = this.leftLinearWithoutFirst;
        if (linearLayout4 != null) {
            linearLayout4.clearAnimation();
        }
    }

    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (Build.VERSION.SDK_INT >= 21) {
            drawableHotspotChanged(this.downX, this.downY);
        }
    }

    private View[] getCollapsibleViews() {
        return this.invokedFromLeft ? this.leftViews : this.rightViews;
    }

    /* access modifiers changed from: private */
    public void clickBySwipe() {
        OnSwipeItemClickListener onSwipeItemClickListener2 = this.onSwipeItemClickListener;
        if (onSwipeItemClickListener2 != null) {
            boolean z = this.invokedFromLeft;
            onSwipeItemClickListener2.onSwipeItemClick(z, z ? 0 : this.rightViews.length - 1);
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        int leftSize;
        int rightSize;
        boolean z;
        WeightAnimation weightAnimation;
        WeightAnimation weightAnimation2;
        WeightAnimation weightAnimation3;
        WeightAnimation weightAnimation4;
        View view2 = view;
        MotionEvent motionEvent = event;
        if (this.swipeEnabled && !(this.leftIcons == null && this.leftTexts == null && this.rightIcons == null && this.rightTexts == null)) {
            int[] iArr = this.leftIcons;
            if (iArr != null) {
                leftSize = iArr.length;
            } else {
                String[] strArr = this.leftTexts;
                if (strArr != null) {
                    leftSize = strArr.length;
                } else {
                    leftSize = 0;
                }
            }
            int[] iArr2 = this.rightIcons;
            if (iArr2 != null) {
                rightSize = iArr2.length;
            } else {
                String[] strArr2 = this.rightTexts;
                if (strArr2 != null) {
                    rightSize = strArr2.length;
                } else {
                    rightSize = 0;
                }
            }
            int action = event.getAction();
            if (action == 0) {
                this.downX = event.getX();
                this.downY = event.getY();
                long currentTimeMillis = System.currentTimeMillis();
                this.lastTime = currentTimeMillis;
                this.downTime = currentTimeMillis;
                float rawX = event.getRawX();
                this.prevRawX = rawX;
                this.downRawX = rawX;
                if (ViewCompat.getTranslationX(this.mainLayout) != 0.0f) {
                    return true;
                }
                LinearLayout linearLayout = this.rightLinearWithoutLast;
                if (linearLayout != null) {
                    Utils.setViewWeight(linearLayout, (float) (this.rightViews.length - 1));
                }
                LinearLayout linearLayout2 = this.leftLinearWithoutFirst;
                if (linearLayout2 == null) {
                    return true;
                }
                Utils.setViewWeight(linearLayout2, (float) (this.leftViews.length - 1));
                return true;
            } else if (action == 1) {
                finishMotion(motionEvent);
                if (this.movementStarted) {
                    finishSwipeAnimated();
                } else {
                    view2.setPressed(false);
                    if (System.currentTimeMillis() - this.downTime < ((long) ViewConfiguration.getTapTimeout())) {
                        view2.setPressed(true);
                        view.performClick();
                        view2.setPressed(false);
                    }
                }
                return false;
            } else if (action != 2) {
                if (action == 3) {
                    finishMotion(motionEvent);
                    if (this.movementStarted) {
                        finishSwipeAnimated();
                    }
                    return false;
                }
            } else if (Math.abs(this.prevRawX - event.getRawX()) >= 20.0f || this.movementStarted) {
                if (view.isPressed()) {
                    view2.setPressed(false);
                }
                this.shouldPerformLongClick = false;
                this.movementStarted = true;
                collapseOthersIfNeeded();
                clearAnimations();
                this.directionLeft = this.prevRawX - event.getRawX() > 0.0f;
                float delta = Math.abs(this.prevRawX - event.getRawX());
                this.speed = ((float) (System.currentTimeMillis() - this.lastTime)) / delta;
                if (this.directionLeft) {
                    float left = ViewCompat.getTranslationX(this.mainLayout) - delta;
                    int i = this.rightLayoutMaxWidth;
                    if (left < ((float) (-i))) {
                        if (!this.canFullSwipeFromRight) {
                            left = (float) (-i);
                        } else if (left < ((float) (-getWidth()))) {
                            left = (float) (-getWidth());
                        }
                    }
                    if (this.canFullSwipeFromRight) {
                        if (ViewCompat.getTranslationX(this.mainLayout) <= ((float) (-(getWidth() - this.fullSwipeEdgePadding)))) {
                            if (Utils.getViewWeight(this.rightLinearWithoutLast) > 0.0f && ((weightAnimation4 = this.collapseAnim) == null || weightAnimation4.hasEnded())) {
                                view2.setPressed(false);
                                this.rightLinearWithoutLast.clearAnimation();
                                if (this.expandAnim != null) {
                                    this.expandAnim = null;
                                }
                                this.collapseAnim = new WeightAnimation(0.0f, this.rightLinearWithoutLast);
                                Log.d("WeightAnim", "onTouch - Collapse");
                                view2.performHapticFeedback(3, 2);
                                startAnimation(this.collapseAnim);
                            }
                        } else if (Utils.getViewWeight(this.rightLinearWithoutLast) < ((float) rightSize) - 1.0f && ((weightAnimation3 = this.expandAnim) == null || weightAnimation3.hasEnded())) {
                            Log.d("WeightAnim", "onTouch - Expand");
                            view2.setPressed(false);
                            this.rightLinearWithoutLast.clearAnimation();
                            if (this.collapseAnim != null) {
                                this.collapseAnim = null;
                            }
                            WeightAnimation weightAnimation5 = new WeightAnimation((float) (rightSize - 1), this.rightLinearWithoutLast);
                            this.expandAnim = weightAnimation5;
                            startAnimation(weightAnimation5);
                        }
                    }
                    ViewCompat.setTranslationX(this.mainLayout, left);
                    if (this.rightLinear != null) {
                        int rightLayoutWidth = (int) Math.abs(left);
                        Utils.setViewWidth(this.rightLinear, rightLayoutWidth);
                        int i2 = rightLayoutWidth;
                    }
                    if (this.leftLinear != null && left > 0.0f) {
                        Utils.setViewWidth(this.leftLinear, (int) Math.abs(ViewCompat.getTranslationX(this.mainLayout)));
                    }
                } else {
                    float right = ViewCompat.getTranslationX(this.mainLayout) + delta;
                    int i3 = this.leftLayoutMaxWidth;
                    if (right > ((float) i3)) {
                        if (!this.canFullSwipeFromLeft) {
                            right = (float) i3;
                        } else if (right >= ((float) getWidth())) {
                            right = (float) getWidth();
                        }
                    }
                    if (this.canFullSwipeFromLeft) {
                        if (ViewCompat.getTranslationX(this.mainLayout) >= ((float) (getWidth() - this.fullSwipeEdgePadding))) {
                            if (Utils.getViewWeight(this.leftLinearWithoutFirst) > 0.0f && ((weightAnimation2 = this.collapseAnim) == null || weightAnimation2.hasEnded())) {
                                this.leftLinearWithoutFirst.clearAnimation();
                                if (this.expandAnim != null) {
                                    this.expandAnim = null;
                                }
                                this.collapseAnim = new WeightAnimation(0.0f, this.leftLinearWithoutFirst);
                                view2.performHapticFeedback(3, 2);
                                startAnimation(this.collapseAnim);
                            }
                        } else if (Utils.getViewWeight(this.leftLinearWithoutFirst) < ((float) leftSize) - 1.0f && ((weightAnimation = this.expandAnim) == null || weightAnimation.hasEnded())) {
                            this.leftLinearWithoutFirst.clearAnimation();
                            if (this.collapseAnim != null) {
                                this.collapseAnim = null;
                            }
                            WeightAnimation weightAnimation6 = new WeightAnimation((float) (leftSize - 1), this.leftLinearWithoutFirst);
                            this.expandAnim = weightAnimation6;
                            startAnimation(weightAnimation6);
                        }
                    }
                    ViewCompat.setTranslationX(this.mainLayout, right);
                    if (this.leftLinear != null && right > 0.0f) {
                        Utils.setViewWidth(this.leftLinear, (int) Math.abs(right));
                    }
                    if (this.rightLinear != null) {
                        Utils.setViewWidth(this.rightLinear, (int) Math.abs(ViewCompat.getTranslationX(this.mainLayout)));
                    }
                }
                if (Math.abs(ViewCompat.getTranslationX(this.mainLayout)) > ((float) (this.itemWidth / 5))) {
                    z = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    z = true;
                }
                this.prevRawX = event.getRawX();
                this.lastTime = System.currentTimeMillis();
                return z;
            } else {
                if (System.currentTimeMillis() - this.lastTime >= 50 && !isPressed() && !isExpanding() && !this.longClickPerformed) {
                    view2.setPressed(true);
                    if (!this.shouldPerformLongClick) {
                        this.shouldPerformLongClick = true;
                        this.longClickHandler.postDelayed(this.longClickRunnable, (long) ViewConfiguration.getLongPressTimeout());
                    }
                }
                return false;
            }
        }
        return false;
    }

    private void collapseOthersIfNeeded() {
        ViewParent parent;
        if (this.onlyOneSwipe && (parent = getParent()) != null && (parent instanceof RecyclerView)) {
            RecyclerView recyclerView = (RecyclerView) parent;
            int count = recyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                View item = recyclerView.getChildAt(i);
                if (item != this && (item instanceof SwipeLayout)) {
                    SwipeLayout swipeLayout = (SwipeLayout) item;
                    if (ViewCompat.getTranslationX(swipeLayout.getSwipeableView()) != 0.0f && !swipeLayout.inAnimatedState()) {
                        swipeLayout.setItemState(2, true);
                    }
                }
            }
        }
    }

    public View getSwipeableView() {
        return this.mainLayout;
    }

    private void finishMotion(MotionEvent event) {
        this.directionLeft = event.getRawX() - this.downRawX < 0.0f;
        this.longClickHandler.removeCallbacks(this.longClickRunnable);
        this.shouldPerformLongClick = false;
        this.longClickPerformed = false;
    }

    private void finishSwipeAnimated() {
        int reqWidth;
        int reqWidth2;
        boolean z = false;
        this.shouldPerformLongClick = false;
        setPressed(false);
        getParent().requestDisallowInterceptTouchEvent(false);
        this.movementStarted = false;
        LinearLayout animateView = null;
        boolean left = false;
        int requiredWidth = 0;
        if (ViewCompat.getTranslationX(this.mainLayout) > 0.0f) {
            animateView = this.leftLinear;
            left = true;
            if (this.leftLinear != null) {
                if (this.directionLeft) {
                    int i = this.leftLayoutMaxWidth;
                    reqWidth2 = i - (i / 3);
                } else {
                    reqWidth2 = this.leftLayoutMaxWidth / 3;
                }
                LinearLayout linearLayout = this.rightLinear;
                if (linearLayout != null) {
                    Utils.setViewWidth(linearLayout, 0);
                }
                if (this.leftLinear.getWidth() >= reqWidth2) {
                    requiredWidth = this.leftLayoutMaxWidth;
                }
                if (requiredWidth == this.leftLayoutMaxWidth && !this.directionLeft && ViewCompat.getTranslationX(this.mainLayout) >= ((float) (getWidth() - this.fullSwipeEdgePadding))) {
                    requiredWidth = getWidth();
                    this.invokedFromLeft = true;
                }
                ViewCompat.setTranslationX(this.mainLayout, (float) this.leftLinear.getWidth());
            }
        } else if (ViewCompat.getTranslationX(this.mainLayout) < 0.0f) {
            left = false;
            animateView = this.rightLinear;
            if (this.rightLinear != null) {
                LinearLayout linearLayout2 = this.leftLinear;
                if (linearLayout2 != null) {
                    Utils.setViewWidth(linearLayout2, 0);
                }
                if (this.directionLeft) {
                    reqWidth = this.rightLayoutMaxWidth / 3;
                } else {
                    int i2 = this.rightLayoutMaxWidth;
                    reqWidth = i2 - (i2 / 3);
                }
                if (this.rightLinear.getWidth() >= reqWidth) {
                    requiredWidth = this.rightLayoutMaxWidth;
                }
                if (requiredWidth == this.rightLayoutMaxWidth && this.directionLeft && ViewCompat.getTranslationX(this.mainLayout) <= ((float) (-(getWidth() - this.fullSwipeEdgePadding)))) {
                    requiredWidth = getWidth();
                    this.invokedFromLeft = false;
                }
                ViewCompat.setTranslationX(this.mainLayout, (float) (-this.rightLinear.getWidth()));
            }
        }
        long duration = (long) (this.speed * 100.0f);
        if (animateView != null) {
            SwipeAnimation swipeAnim = new SwipeAnimation(animateView, requiredWidth, this.mainLayout, left);
            if (duration < ANIMATION_MIN_DURATION) {
                duration = ANIMATION_MIN_DURATION;
            } else if (duration > ANIMATION_MAX_DURATION) {
                duration = ANIMATION_MAX_DURATION;
            }
            swipeAnim.setDuration(duration);
            LinearLayout layoutWithout = animateView == this.leftLinear ? this.leftLinearWithoutFirst : this.rightLinearWithoutLast;
            View[] views = animateView == this.leftLinear ? this.leftViews : this.rightViews;
            if (animateView == this.leftLinear) {
                z = true;
            }
            this.invokedFromLeft = z;
            if (requiredWidth != getWidth()) {
                layoutWithout.startAnimation(new WeightAnimation((float) (views.length - 1), layoutWithout));
            } else if (Utils.getViewWeight(layoutWithout) != 0.0f || ((float) getWidth()) == Math.abs(ViewCompat.getTranslationX(this.mainLayout))) {
                WeightAnimation weightAnimation = this.collapseAnim;
                if (weightAnimation != null && !weightAnimation.hasEnded()) {
                    this.collapseAnim.setAnimationListener(this.collapseListener);
                } else if (Utils.getViewWeight(layoutWithout) == 0.0f || ((float) getWidth()) == Math.abs(ViewCompat.getTranslationX(this.mainLayout))) {
                    clickBySwipe();
                } else {
                    layoutWithout.clearAnimation();
                    WeightAnimation weightAnimation2 = this.collapseAnim;
                    if (weightAnimation2 != null) {
                        weightAnimation2.cancel();
                    }
                    WeightAnimation weightAnimation3 = new WeightAnimation(0.0f, layoutWithout);
                    this.collapseAnim = weightAnimation3;
                    weightAnimation3.setAnimationListener(this.collapseListener);
                    layoutWithout.startAnimation(this.collapseAnim);
                }
            } else {
                swipeAnim.setAnimationListener(this.collapseListener);
            }
            animateView.startAnimation(swipeAnim);
        }
    }

    @Deprecated
    public void closeItem() {
        collapseItem(true);
    }

    private void collapseItem(boolean animated) {
        LinearLayout linearLayout = this.leftLinear;
        if (linearLayout == null || linearLayout.getWidth() <= 0) {
            LinearLayout linearLayout2 = this.rightLinear;
            if (linearLayout2 != null && linearLayout2.getWidth() > 0) {
                Utils.setViewWidth(this.rightLinearWithoutLast, this.rightViews.length - 1);
                if (animated) {
                    this.rightLinear.startAnimation(new SwipeAnimation(this.rightLinear, 0, this.mainLayout, false));
                    return;
                }
                ViewCompat.setTranslationX(this.mainLayout, 0.0f);
                Utils.setViewWidth(this.rightLinear, 0);
                return;
            }
            return;
        }
        Utils.setViewWidth(this.leftLinearWithoutFirst, this.leftViews.length - 1);
        if (animated) {
            this.leftLinear.startAnimation(new SwipeAnimation(this.leftLinear, 0, this.mainLayout, true));
            return;
        }
        ViewCompat.setTranslationX(this.mainLayout, 0.0f);
        Utils.setViewWidth(this.leftLinear, 0);
    }

    public void setItemState(int state, boolean animated) {
        if (state == 0) {
            int requiredWidthLeft = this.leftIcons.length * this.itemWidth;
            if (animated) {
                this.leftLinear.startAnimation(new SwipeAnimation(this.leftLinear, requiredWidthLeft, this.mainLayout, true));
                return;
            }
            ViewCompat.setTranslationX(this.mainLayout, (float) requiredWidthLeft);
            Utils.setViewWidth(this.leftLinear, requiredWidthLeft);
        } else if (state == 1) {
            int requiredWidthRight = this.rightIcons.length * this.itemWidth;
            if (animated) {
                this.rightLinear.startAnimation(new SwipeAnimation(this.rightLinear, requiredWidthRight, this.mainLayout, false));
                return;
            }
            ViewCompat.setTranslationX(this.mainLayout, (float) (-requiredWidthRight));
            Utils.setViewWidth(this.rightLinear, requiredWidthRight);
        } else if (state == 2) {
            collapseItem(animated);
        }
    }

    public void setSwipeEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }

    public boolean isSwipeEnabled() {
        return this.swipeEnabled;
    }

    public boolean inAnimatedState() {
        Animation anim;
        Animation anim2;
        LinearLayout linearLayout = this.leftLinear;
        if (linearLayout != null && (anim2 = linearLayout.getAnimation()) != null && !anim2.hasEnded()) {
            return true;
        }
        LinearLayout linearLayout2 = this.rightLinear;
        if (linearLayout2 == null || (anim = linearLayout2.getAnimation()) == null || anim.hasEnded()) {
            return false;
        }
        return true;
    }

    public void setAutoHideSwipe(boolean autoHideSwipe2) {
        this.autoHideSwipe = autoHideSwipe2;
        ViewParent parent = getParent();
        if (parent != null && (parent instanceof RecyclerView)) {
            RecyclerView recyclerView = (RecyclerView) parent;
            RecyclerView.OnScrollListener onScrollListener2 = this.onScrollListener;
            if (onScrollListener2 != null) {
                recyclerView.removeOnScrollListener(onScrollListener2);
            }
            if (autoHideSwipe2) {
                AnonymousClass3 r2 = new RecyclerView.OnScrollListener() {
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == 1 && ViewCompat.getTranslationX(SwipeLayout.this.mainLayout) != 0.0f) {
                            SwipeLayout.this.setItemState(2, true);
                        }
                    }
                };
                this.onScrollListener = r2;
                recyclerView.addOnScrollListener(r2);
            }
        }
    }

    public void setOnlyOneSwipe(boolean onlyOneSwipe2) {
        this.onlyOneSwipe = onlyOneSwipe2;
    }

    public boolean isLeftExpanding() {
        return ViewCompat.getTranslationX(this.mainLayout) > 0.0f;
    }

    public boolean isRightExpanding() {
        return ViewCompat.getTranslationX(this.mainLayout) < 0.0f;
    }

    public boolean isExpanding() {
        return isRightExpanding() || isLeftExpanding();
    }

    public boolean isRightExpanded() {
        LinearLayout linearLayout = this.rightLinear;
        return linearLayout != null && linearLayout.getWidth() >= this.rightLayoutMaxWidth;
    }

    public boolean isLeftExpanded() {
        LinearLayout linearLayout = this.leftLinear;
        return linearLayout != null && linearLayout.getWidth() >= this.leftLayoutMaxWidth;
    }

    public boolean isExpanded() {
        return isLeftExpanded() || isRightExpanded();
    }

    public void setNeedDivderBetweenMainAndMenu(boolean needDivderBetweenMainAndMenu2) {
        this.needDivderBetweenMainAndMenu = needDivderBetweenMainAndMenu2;
    }

    public SwipeLayout getExpandLayout() {
        ViewParent parent = getParent();
        if (parent == null || !(parent instanceof RecyclerView)) {
            return null;
        }
        RecyclerView recyclerView = (RecyclerView) parent;
        int count = recyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View item = recyclerView.getChildAt(i);
            if (item instanceof SwipeLayout) {
                SwipeLayout swipeLayout = (SwipeLayout) item;
                if (swipeLayout.isExpanded() || swipeLayout.isExpanding()) {
                    return swipeLayout;
                }
            }
        }
        return null;
    }

    public void onClick(View view) {
        if (this.onSwipeItemClickListener != null) {
            if (this.leftViews != null) {
                int i = 0;
                while (true) {
                    View[] viewArr = this.leftViews;
                    if (i >= viewArr.length) {
                        break;
                    } else if (viewArr[i] != view) {
                        i++;
                    } else if (viewArr.length == 1 || Utils.getViewWeight(this.leftLinearWithoutFirst) > 0.0f) {
                        this.onSwipeItemClickListener.onSwipeItemClick(true, i);
                        return;
                    } else {
                        return;
                    }
                }
            }
            if (this.rightViews != null) {
                int i2 = 0;
                while (true) {
                    View[] viewArr2 = this.rightViews;
                    if (i2 >= viewArr2.length) {
                        return;
                    }
                    if (viewArr2[i2] != view) {
                        i2++;
                    } else if (viewArr2.length == 1 || Utils.getViewWeight(this.rightLinearWithoutLast) > 0.0f) {
                        this.onSwipeItemClickListener.onSwipeItemClick(false, i2);
                        return;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void collapseAll(boolean animated) {
        ViewParent parent = getParent();
        if (parent != null && (parent instanceof RecyclerView)) {
            RecyclerView recyclerView = (RecyclerView) parent;
            int count = recyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                View item = recyclerView.getChildAt(i);
                if (item instanceof SwipeLayout) {
                    SwipeLayout swipeLayout = (SwipeLayout) item;
                    if (ViewCompat.getTranslationX(swipeLayout.getSwipeableView()) != 0.0f) {
                        swipeLayout.setItemState(2, animated);
                    }
                }
            }
        }
    }

    public void setCanFullSwipeFromLeft(boolean fullSwipeFromLeft) {
        this.canFullSwipeFromLeft = fullSwipeFromLeft;
    }

    public void setCanFullSwipeFromRight(boolean fullSwipeFromRight) {
        this.canFullSwipeFromRight = fullSwipeFromRight;
    }
}

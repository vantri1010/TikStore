package im.bclpbkiauv.ui.actionbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.LineProgressView;
import im.bclpbkiauv.ui.components.RadialProgressView;
import java.util.ArrayList;

public class XAlertDialog extends Dialog implements Drawable.Callback {
    /* access modifiers changed from: private */
    public Rect backgroundPaddings = new Rect();
    protected FrameLayout buttonsLayout;
    private boolean canCacnel = true;
    private XAlertDialog cancelDialog;
    /* access modifiers changed from: private */
    public ScrollView contentScrollView;
    private int currentProgress;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public int customViewOffset = 20;
    private boolean dismissDialogByButtons = true;
    /* access modifiers changed from: private */
    public Runnable dismissRunnable = new Runnable() {
        public final void run() {
            XAlertDialog.this.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public int[] itemIcons;
    private ArrayList<AlertDialogCell> itemViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public CharSequence[] items;
    /* access modifiers changed from: private */
    public int lastScreenWidth;
    /* access modifiers changed from: private */
    public LineProgressView lineProgressView;
    /* access modifiers changed from: private */
    public TextView lineProgressViewPercent;
    private ProgressBar loadingProgressView;
    private CharSequence loadingText;
    /* access modifiers changed from: private */
    public CharSequence message;
    /* access modifiers changed from: private */
    public TextView messageTextView;
    /* access modifiers changed from: private */
    public boolean messageTextViewClickable = true;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener negativeButtonListener;
    /* access modifiers changed from: private */
    public CharSequence negativeButtonText;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener neutralButtonListener;
    /* access modifiers changed from: private */
    public CharSequence neutralButtonText;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onBackButtonListener;
    private DialogInterface.OnCancelListener onCancelListener;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;
    /* access modifiers changed from: private */
    public ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener positiveButtonListener;
    /* access modifiers changed from: private */
    public CharSequence positiveButtonText;
    /* access modifiers changed from: private */
    public FrameLayout progressViewContainer;
    /* access modifiers changed from: private */
    public int progressViewStyle;
    private TextView progressViewTextView;
    /* access modifiers changed from: private */
    public LinearLayout scrollContainer;
    private CharSequence secondTitle;
    /* access modifiers changed from: private */
    public TextView secondTitleTextView;
    /* access modifiers changed from: private */
    public BitmapDrawable[] shadow = new BitmapDrawable[2];
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    private Drawable shadowDrawable;
    private boolean[] shadowVisibility = new boolean[2];
    /* access modifiers changed from: private */
    public CharSequence subtitle;
    /* access modifiers changed from: private */
    public TextView subtitleTextView;
    /* access modifiers changed from: private */
    public CharSequence title;
    /* access modifiers changed from: private */
    public FrameLayout titleContainer;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    /* access modifiers changed from: private */
    public int topBackgroundColor;
    /* access modifiers changed from: private */
    public Drawable topDrawable;
    /* access modifiers changed from: private */
    public int topHeight = 132;
    /* access modifiers changed from: private */
    public ImageView topImageView;
    /* access modifiers changed from: private */
    public int topResId;
    private TextView tvLoadingView;

    public static class AlertDialogCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public TextView textView;

        public AlertDialogCell(Context context) {
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
            setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.textView.setTextSize(1, 16.0f);
            addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            this.textView.setText(text);
            if (icon != 0) {
                this.imageView.setImageResource(icon);
                this.imageView.setVisibility(0);
                this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(56.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(56.0f) : 0, 0);
                return;
            }
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
        }
    }

    public XAlertDialog(Context context, int progressStyle) {
        super(context, R.style.TransparentDialog);
        if (!(progressStyle == 3 || progressStyle == 4)) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
            this.shadowDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(getThemeColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
            this.shadowDrawable.getPadding(this.backgroundPaddings);
        }
        this.progressViewStyle = progressStyle;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        int maxWidth;
        super.onCreate(savedInstanceState);
        LinearLayout containerView = new LinearLayout(getContext()) {
            private boolean inLayout;

            public boolean onTouchEvent(MotionEvent event) {
                if (XAlertDialog.this.progressViewStyle != 3 && XAlertDialog.this.progressViewStyle != 4 && XAlertDialog.this.progressViewStyle != 5) {
                    return super.onTouchEvent(event);
                }
                XAlertDialog.this.showCancelAlert();
                return false;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (XAlertDialog.this.progressViewStyle != 3 && XAlertDialog.this.progressViewStyle != 4 && XAlertDialog.this.progressViewStyle != 5) {
                    return super.onInterceptTouchEvent(ev);
                }
                XAlertDialog.this.showCancelAlert();
                return false;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (XAlertDialog.this.progressViewStyle == 3 || XAlertDialog.this.progressViewStyle == 4) {
                    XAlertDialog.this.progressViewContainer.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), 1073741824));
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                } else if (XAlertDialog.this.progressViewStyle == 5) {
                    XAlertDialog.this.progressViewContainer.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(220.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), 1073741824));
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                } else {
                    this.inLayout = true;
                    int width = View.MeasureSpec.getSize(widthMeasureSpec);
                    int availableHeight = (View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()) - getPaddingBottom();
                    int maxContentHeight = availableHeight;
                    int availableWidth = (width - getPaddingLeft()) - getPaddingRight();
                    int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(availableWidth - AndroidUtilities.dp(48.0f), 1073741824);
                    int childFullWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(availableWidth, 1073741824);
                    if (XAlertDialog.this.buttonsLayout != null) {
                        int count = XAlertDialog.this.buttonsLayout.getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = XAlertDialog.this.buttonsLayout.getChildAt(a);
                            if (child instanceof TextView) {
                                ((TextView) child).setMaxWidth(AndroidUtilities.dp((float) ((availableWidth - AndroidUtilities.dp(24.0f)) / 2)));
                            }
                        }
                        XAlertDialog.this.buttonsLayout.measure(childFullWidthMeasureSpec, heightMeasureSpec);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) XAlertDialog.this.buttonsLayout.getLayoutParams();
                        availableHeight -= (XAlertDialog.this.buttonsLayout.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                    }
                    if (XAlertDialog.this.secondTitleTextView != null) {
                        XAlertDialog.this.secondTitleTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(childWidthMeasureSpec), Integer.MIN_VALUE), heightMeasureSpec);
                    }
                    if (XAlertDialog.this.titleTextView != null) {
                        if (XAlertDialog.this.secondTitleTextView != null) {
                            XAlertDialog.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(childWidthMeasureSpec) - XAlertDialog.this.secondTitleTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), 1073741824), heightMeasureSpec);
                        } else {
                            XAlertDialog.this.titleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                        }
                    }
                    if (XAlertDialog.this.titleContainer != null) {
                        XAlertDialog.this.titleContainer.measure(childWidthMeasureSpec, heightMeasureSpec);
                        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) XAlertDialog.this.titleContainer.getLayoutParams();
                        availableHeight -= (XAlertDialog.this.titleContainer.getMeasuredHeight() + layoutParams2.bottomMargin) + layoutParams2.topMargin;
                    }
                    if (XAlertDialog.this.subtitleTextView != null) {
                        XAlertDialog.this.subtitleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) XAlertDialog.this.subtitleTextView.getLayoutParams();
                        availableHeight -= (XAlertDialog.this.subtitleTextView.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                    }
                    if (XAlertDialog.this.topImageView != null) {
                        XAlertDialog.this.topImageView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) XAlertDialog.this.topHeight), 1073741824));
                        availableHeight -= XAlertDialog.this.topImageView.getMeasuredHeight() - AndroidUtilities.dp(8.0f);
                    }
                    if (XAlertDialog.this.progressViewStyle == 0) {
                        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) XAlertDialog.this.contentScrollView.getLayoutParams();
                        if (XAlertDialog.this.customView != null) {
                            layoutParams4.topMargin = (XAlertDialog.this.titleTextView == null && XAlertDialog.this.messageTextView.getVisibility() == 8 && XAlertDialog.this.items == null) ? AndroidUtilities.dp(16.0f) : 0;
                            layoutParams4.bottomMargin = XAlertDialog.this.buttonsLayout == null ? AndroidUtilities.dp(8.0f) : 0;
                        } else if (XAlertDialog.this.items != null) {
                            layoutParams4.topMargin = (XAlertDialog.this.titleTextView == null && XAlertDialog.this.messageTextView.getVisibility() == 8) ? AndroidUtilities.dp(8.0f) : 0;
                            layoutParams4.bottomMargin = AndroidUtilities.dp(8.0f);
                        } else if (XAlertDialog.this.messageTextView.getVisibility() == 0) {
                            layoutParams4.topMargin = XAlertDialog.this.titleTextView == null ? AndroidUtilities.dp(19.0f) : 0;
                            layoutParams4.bottomMargin = AndroidUtilities.dp(20.0f);
                        }
                        int availableHeight2 = availableHeight - (layoutParams4.bottomMargin + layoutParams4.topMargin);
                        XAlertDialog.this.contentScrollView.measure(childFullWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight2, Integer.MIN_VALUE));
                        availableHeight = availableHeight2 - XAlertDialog.this.contentScrollView.getMeasuredHeight();
                    } else {
                        if (XAlertDialog.this.progressViewContainer != null) {
                            XAlertDialog.this.progressViewContainer.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                            LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) XAlertDialog.this.progressViewContainer.getLayoutParams();
                            availableHeight -= (XAlertDialog.this.progressViewContainer.getMeasuredHeight() + layoutParams5.bottomMargin) + layoutParams5.topMargin;
                        } else if (XAlertDialog.this.messageTextView != null) {
                            XAlertDialog.this.messageTextView.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                            if (XAlertDialog.this.messageTextView.getVisibility() != 8) {
                                LinearLayout.LayoutParams layoutParams6 = (LinearLayout.LayoutParams) XAlertDialog.this.messageTextView.getLayoutParams();
                                availableHeight -= (XAlertDialog.this.messageTextView.getMeasuredHeight() + layoutParams6.bottomMargin) + layoutParams6.topMargin;
                            }
                        }
                        if (XAlertDialog.this.lineProgressView != null) {
                            XAlertDialog.this.lineProgressView.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f), 1073741824));
                            LinearLayout.LayoutParams layoutParams7 = (LinearLayout.LayoutParams) XAlertDialog.this.lineProgressView.getLayoutParams();
                            int availableHeight3 = availableHeight - ((XAlertDialog.this.lineProgressView.getMeasuredHeight() + layoutParams7.bottomMargin) + layoutParams7.topMargin);
                            XAlertDialog.this.lineProgressViewPercent.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight3, Integer.MIN_VALUE));
                            LinearLayout.LayoutParams layoutParams8 = (LinearLayout.LayoutParams) XAlertDialog.this.lineProgressViewPercent.getLayoutParams();
                            availableHeight = availableHeight3 - ((XAlertDialog.this.lineProgressViewPercent.getMeasuredHeight() + layoutParams8.bottomMargin) + layoutParams8.topMargin);
                        }
                    }
                    setMeasuredDimension(width, (maxContentHeight - availableHeight) + getPaddingTop() + getPaddingBottom());
                    this.inLayout = false;
                    if (XAlertDialog.this.lastScreenWidth != AndroidUtilities.displaySize.x) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                XAlertDialog.AnonymousClass1.this.lambda$onMeasure$0$XAlertDialog$1();
                            }
                        });
                    }
                }
            }

            public /* synthetic */ void lambda$onMeasure$0$XAlertDialog$1() {
                int maxWidth;
                int unused = XAlertDialog.this.lastScreenWidth = AndroidUtilities.displaySize.x;
                int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f);
                if (!AndroidUtilities.isTablet()) {
                    maxWidth = AndroidUtilities.dp(356.0f);
                } else if (AndroidUtilities.isSmallTablet()) {
                    maxWidth = AndroidUtilities.dp(446.0f);
                } else {
                    maxWidth = AndroidUtilities.dp(496.0f);
                }
                Window window = XAlertDialog.this.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.copyFrom(window.getAttributes());
                params.width = Math.min(maxWidth, calculatedWidth) + XAlertDialog.this.backgroundPaddings.left + XAlertDialog.this.backgroundPaddings.right;
                window.setAttributes(params);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (XAlertDialog.this.progressViewStyle == 3 || XAlertDialog.this.progressViewStyle == 4 || XAlertDialog.this.progressViewStyle == 5) {
                    int x = ((r - l) - XAlertDialog.this.progressViewContainer.getMeasuredWidth()) / 2;
                    int y = ((b - t) - XAlertDialog.this.progressViewContainer.getMeasuredHeight()) / 2;
                    XAlertDialog.this.progressViewContainer.layout(x, y, XAlertDialog.this.progressViewContainer.getMeasuredWidth() + x, XAlertDialog.this.progressViewContainer.getMeasuredHeight() + y);
                } else if (XAlertDialog.this.contentScrollView != null) {
                    if (XAlertDialog.this.onScrollChangedListener == null) {
                        ViewTreeObserver.OnScrollChangedListener unused = XAlertDialog.this.onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                            public final void onScrollChanged() {
                                XAlertDialog.AnonymousClass1.this.lambda$onLayout$1$XAlertDialog$1();
                            }
                        };
                        XAlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(XAlertDialog.this.onScrollChangedListener);
                    }
                    XAlertDialog.this.onScrollChangedListener.onScrollChanged();
                }
            }

            public /* synthetic */ void lambda$onLayout$1$XAlertDialog$1() {
                XAlertDialog xAlertDialog = XAlertDialog.this;
                boolean z = false;
                xAlertDialog.runShadowAnimation(0, xAlertDialog.titleTextView != null && XAlertDialog.this.contentScrollView.getScrollY() > XAlertDialog.this.scrollContainer.getTop());
                XAlertDialog xAlertDialog2 = XAlertDialog.this;
                if (xAlertDialog2.buttonsLayout != null && XAlertDialog.this.contentScrollView.getScrollY() + XAlertDialog.this.contentScrollView.getHeight() < XAlertDialog.this.scrollContainer.getBottom()) {
                    z = true;
                }
                xAlertDialog2.runShadowAnimation(1, z);
                XAlertDialog.this.contentScrollView.invalidate();
            }

            public void requestLayout() {
                if (!this.inLayout) {
                    super.requestLayout();
                }
            }

            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        containerView.setOrientation(1);
        int i = this.progressViewStyle;
        if (i == 3 || i == 4 || i == 5) {
            containerView.setBackgroundDrawable((Drawable) null);
        } else {
            containerView.setBackgroundDrawable(this.shadowDrawable);
        }
        containerView.setFitsSystemWindows(Build.VERSION.SDK_INT >= 21);
        setContentView(containerView);
        boolean hasButtons = (this.positiveButtonText == null && this.negativeButtonText == null && this.neutralButtonText == null) ? false : true;
        if (!(this.topResId == 0 && this.topDrawable == null)) {
            ImageView imageView = new ImageView(getContext());
            this.topImageView = imageView;
            Drawable drawable = this.topDrawable;
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
            } else {
                imageView.setImageResource(this.topResId);
            }
            this.topImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.popup_fixed_top));
            this.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(this.topBackgroundColor, PorterDuff.Mode.MULTIPLY));
            this.topImageView.setPadding(0, 0, 0, 0);
            containerView.addView(this.topImageView, LayoutHelper.createLinear(-1, this.topHeight, 51, -8, -8, 0, 0));
        }
        if (this.title != null) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.titleContainer = frameLayout;
            containerView.addView(frameLayout, LayoutHelper.createLinear(-2, -2, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(getContext());
            this.titleTextView = textView;
            textView.setText(this.title);
            this.titleTextView.setTextColor(getThemeColor(Theme.key_dialogTextBlack));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.titleContainer.addView(this.titleTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 19.0f, 0.0f, (float) (this.subtitle != null ? 2 : this.items != null ? 14 : 10)));
        }
        if (!(this.secondTitle == null || this.title == null)) {
            TextView textView2 = new TextView(getContext());
            this.secondTitleTextView = textView2;
            textView2.setText(this.secondTitle);
            this.secondTitleTextView.setTextColor(getThemeColor(Theme.key_dialogTextGray3));
            this.secondTitleTextView.setTextSize(1, 18.0f);
            this.secondTitleTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            this.titleContainer.addView(this.secondTitleTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, 0.0f, 21.0f, 0.0f, 0.0f));
        }
        if (this.subtitle != null) {
            TextView textView3 = new TextView(getContext());
            this.subtitleTextView = textView3;
            textView3.setText(this.subtitle);
            this.subtitleTextView.setTextColor(getThemeColor(Theme.key_dialogIcon));
            this.subtitleTextView.setTextSize(1, 14.0f);
            this.subtitleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            containerView.addView(this.subtitleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, this.items != null ? 14 : 10));
        }
        if (this.progressViewStyle == 0) {
            this.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow).mutate();
            this.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow_reverse).mutate();
            this.shadow[0].setAlpha(0);
            this.shadow[1].setAlpha(0);
            this.shadow[0].setCallback(this);
            this.shadow[1].setCallback(this);
            AnonymousClass2 r5 = new ScrollView(getContext()) {
                /* access modifiers changed from: protected */
                public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (XAlertDialog.this.shadow[0].getPaint().getAlpha() != 0) {
                        XAlertDialog.this.shadow[0].setBounds(0, getScrollY(), getMeasuredWidth(), getScrollY() + AndroidUtilities.dp(3.0f));
                        XAlertDialog.this.shadow[0].draw(canvas);
                    }
                    if (XAlertDialog.this.shadow[1].getPaint().getAlpha() != 0) {
                        XAlertDialog.this.shadow[1].setBounds(0, (getScrollY() + getMeasuredHeight()) - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getScrollY() + getMeasuredHeight());
                        XAlertDialog.this.shadow[1].draw(canvas);
                    }
                    return result;
                }
            };
            this.contentScrollView = r5;
            r5.setVerticalScrollBarEnabled(false);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.contentScrollView, getThemeColor(Theme.key_dialogScrollGlow));
            containerView.addView(this.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(getContext());
            this.scrollContainer = linearLayout;
            linearLayout.setOrientation(1);
            this.contentScrollView.addView(this.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
        }
        TextView textView4 = new TextView(getContext());
        this.messageTextView = textView4;
        textView4.setTextColor(getThemeColor(Theme.key_dialogTextBlack));
        this.messageTextView.setTextSize(1, 16.0f);
        this.messageTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.messageTextView.setLinkTextColor(getThemeColor(Theme.key_dialogTextLink));
        if (!this.messageTextViewClickable) {
            this.messageTextView.setClickable(false);
            this.messageTextView.setEnabled(false);
        }
        this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i2 = this.progressViewStyle;
        if (i2 == 1) {
            FrameLayout frameLayout2 = new FrameLayout(getContext());
            this.progressViewContainer = frameLayout2;
            containerView.addView(frameLayout2, LayoutHelper.createLinear(-1, 44, 51, 23, this.title == null ? 24 : 0, 23, 24));
            RadialProgressView progressView = new RadialProgressView(getContext());
            progressView.setProgressColor(getThemeColor(Theme.key_dialogProgressCircle));
            this.progressViewContainer.addView(progressView, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
            this.messageTextView.setLines(1);
            this.messageTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.progressViewContainer.addView(this.messageTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 62), 0.0f, (float) (LocaleController.isRTL ? 62 : 0), 0.0f));
        } else if (i2 == 2) {
            containerView.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, this.title == null ? 19 : 0, 24, 20));
            LineProgressView lineProgressView2 = new LineProgressView(getContext());
            this.lineProgressView = lineProgressView2;
            lineProgressView2.setProgress(((float) this.currentProgress) / 100.0f, false);
            this.lineProgressView.setProgressColor(getThemeColor(Theme.key_dialogLineProgress));
            this.lineProgressView.setBackColor(getThemeColor(Theme.key_dialogLineProgressBackground));
            containerView.addView(this.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
            TextView textView5 = new TextView(getContext());
            this.lineProgressViewPercent = textView5;
            textView5.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.lineProgressViewPercent.setTextColor(getThemeColor(Theme.key_dialogTextGray2));
            this.lineProgressViewPercent.setTextSize(1, 14.0f);
            containerView.addView(this.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
            updateLineProgressTextView();
        } else if (i2 == 3 || i2 == 4) {
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            FrameLayout frameLayout3 = new FrameLayout(getContext());
            this.progressViewContainer = frameLayout3;
            int i3 = this.progressViewStyle;
            if (i3 == 3) {
                frameLayout3.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(18.0f), Theme.getColor(Theme.key_dialog_inlineProgressBackground)));
            } else if (i3 == 4) {
                frameLayout3.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(12.0f), -1895825408));
            }
            containerView.addView(this.progressViewContainer, LayoutHelper.createLinear(86, 86, 17));
            int i4 = this.progressViewStyle;
            if (i4 == 3) {
                RadialProgressView progressView2 = new RadialProgressView(getContext());
                progressView2.setProgressColor(getThemeColor(Theme.key_dialog_inlineProgress));
                this.progressViewContainer.addView(progressView2, LayoutHelper.createFrame(86, 86.0f));
            } else if (i4 == 4) {
                LinearLayout loadingViewContainer = new LinearLayout(getContext());
                loadingViewContainer.setGravity(17);
                loadingViewContainer.setOrientation(1);
                this.progressViewContainer.addView(loadingViewContainer, LayoutHelper.createFrame(-1, -1.0f));
                ProgressBar progressBar = new ProgressBar(getContext());
                this.loadingProgressView = progressBar;
                progressBar.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.rotate_anim));
                loadingViewContainer.addView(this.loadingProgressView, LayoutHelper.createFrame(38, 38.0f));
                TextView textView6 = new TextView(getContext());
                this.tvLoadingView = textView6;
                textView6.setTextColor(-1);
                this.tvLoadingView.setGravity(17);
                this.tvLoadingView.setText(this.loadingText);
                this.tvLoadingView.setVisibility(TextUtils.isEmpty(this.loadingText) ? 8 : 0);
                loadingViewContainer.addView(this.tvLoadingView, LayoutHelper.createFrame(-2, -2, 0, AndroidUtilities.dp(10.0f), 0, 0));
            }
        } else {
            int i5 = 5;
            if (i2 == 5) {
                setCanceledOnTouchOutside(false);
                setCancelable(false);
                FrameLayout frameLayout4 = new FrameLayout(getContext());
                this.progressViewContainer = frameLayout4;
                frameLayout4.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(12.0f), -1895825408));
                containerView.addView(this.progressViewContainer, LayoutHelper.createLinear(-2, -2, 17));
                LinearLayout loadingLayout = new LinearLayout(getContext());
                loadingLayout.setOrientation(0);
                this.progressViewContainer.addView(loadingLayout, LayoutHelper.createFrame(-2, -2, 16));
                RadialProgressView progressView3 = new RadialProgressView(getContext());
                progressView3.setSize(AndroidUtilities.dp(38.0f));
                progressView3.setStrokeWidth(1.5f);
                progressView3.setProgressColor(-3355444);
                loadingLayout.addView(progressView3, LayoutHelper.createLinear(40, 40, 16, 25, 0, 0, 0));
                TextView textView7 = new TextView(getContext());
                this.tvLoadingView = textView7;
                textView7.setText(LocaleController.getString("Loading", R.string.NowLoading));
                loadingLayout.addView(this.tvLoadingView, LayoutHelper.createLinear(-2, -2, 16, 10, 0, 0, 0));
            } else {
                LinearLayout linearLayout2 = this.scrollContainer;
                TextView textView8 = this.messageTextView;
                if (!LocaleController.isRTL) {
                    i5 = 3;
                }
                linearLayout2.addView(textView8, LayoutHelper.createLinear(-2, -2, i5 | 48, 24, 0, 24, (this.customView == null && this.items == null) ? 0 : this.customViewOffset));
            }
        }
        if (!TextUtils.isEmpty(this.message)) {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
        } else {
            this.messageTextView.setVisibility(8);
        }
        if (this.items != null) {
            int a = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (a >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[a] != null) {
                    AlertDialogCell cell = new AlertDialogCell(getContext());
                    CharSequence charSequence = this.items[a];
                    int[] iArr = this.itemIcons;
                    cell.setTextAndIcon(charSequence, iArr != null ? iArr[a] : 0);
                    cell.setTag(Integer.valueOf(a));
                    this.itemViews.add(cell);
                    this.scrollContainer.addView(cell, LayoutHelper.createLinear(-1, 50));
                    cell.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            XAlertDialog.this.lambda$onCreate$0$XAlertDialog(view);
                        }
                    });
                }
                a++;
            }
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.scrollContainer.addView(this.customView, LayoutHelper.createLinear(-1, -2));
        }
        if (hasButtons) {
            AnonymousClass3 r4 = new FrameLayout(getContext()) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int t;
                    int l;
                    int count = getChildCount();
                    View positiveButton = null;
                    int width = right - left;
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        Integer tag = (Integer) child.getTag();
                        if (tag == null) {
                            int w = child.getMeasuredWidth();
                            int h = child.getMeasuredHeight();
                            if (positiveButton != null) {
                                l = positiveButton.getLeft() + ((positiveButton.getMeasuredWidth() - w) / 2);
                                t = positiveButton.getTop() + ((positiveButton.getMeasuredHeight() - h) / 2);
                            } else {
                                l = 0;
                                t = 0;
                            }
                            child.layout(l, t, l + w, t + h);
                        } else if (tag.intValue() == -1) {
                            positiveButton = child;
                            if (LocaleController.isRTL) {
                                child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                            } else {
                                child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                            }
                        } else if (tag.intValue() == -2) {
                            if (LocaleController.isRTL) {
                                int x = getPaddingLeft();
                                if (positiveButton != null) {
                                    x += positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                }
                                child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                            } else {
                                int x2 = (width - getPaddingRight()) - child.getMeasuredWidth();
                                if (positiveButton != null) {
                                    x2 -= positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                }
                                child.layout(x2, getPaddingTop(), child.getMeasuredWidth() + x2, getPaddingTop() + child.getMeasuredHeight());
                            }
                        } else if (tag.intValue() == -3) {
                            if (LocaleController.isRTL) {
                                child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                            } else {
                                child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                            }
                        }
                    }
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int totalWidth = 0;
                    int availableWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                    int count = getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        if ((child instanceof TextView) && child.getTag() != null) {
                            totalWidth += child.getMeasuredWidth();
                        }
                    }
                    if (totalWidth > availableWidth) {
                        View negative = findViewWithTag(-2);
                        View neuntral = findViewWithTag(-3);
                        if (negative != null && neuntral != null) {
                            if (negative.getMeasuredWidth() < neuntral.getMeasuredWidth()) {
                                neuntral.measure(View.MeasureSpec.makeMeasureSpec(neuntral.getMeasuredWidth() - (totalWidth - availableWidth), 1073741824), View.MeasureSpec.makeMeasureSpec(neuntral.getMeasuredHeight(), 1073741824));
                            } else {
                                negative.measure(View.MeasureSpec.makeMeasureSpec(negative.getMeasuredWidth() - (totalWidth - availableWidth), 1073741824), View.MeasureSpec.makeMeasureSpec(negative.getMeasuredHeight(), 1073741824));
                            }
                        }
                    }
                }
            };
            this.buttonsLayout = r4;
            r4.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            containerView.addView(this.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            if (this.positiveButtonText != null) {
                TextView textView9 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView9.setMinWidth(AndroidUtilities.dp(64.0f));
                textView9.setTag(-1);
                textView9.setTextSize(1, 14.0f);
                textView9.setTextColor(getThemeColor(Theme.key_dialogButton));
                textView9.setGravity(17);
                textView9.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView9.setText(this.positiveButtonText.toString().toUpperCase());
                textView9.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor(Theme.key_dialogButton)));
                textView9.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(textView9, LayoutHelper.createFrame(-2, 36, 53));
                textView9.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        XAlertDialog.this.lambda$onCreate$1$XAlertDialog(view);
                    }
                });
            }
            if (this.negativeButtonText != null) {
                TextView textView10 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView10.setMinWidth(AndroidUtilities.dp(64.0f));
                textView10.setTag(-2);
                textView10.setTextSize(1, 14.0f);
                textView10.setTextColor(getThemeColor(Theme.key_dialogButton));
                textView10.setGravity(17);
                textView10.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView10.setEllipsize(TextUtils.TruncateAt.END);
                textView10.setSingleLine(true);
                textView10.setText(this.negativeButtonText.toString().toUpperCase());
                textView10.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor(Theme.key_dialogButton)));
                textView10.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(textView10, LayoutHelper.createFrame(-2, 36, 53));
                textView10.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        XAlertDialog.this.lambda$onCreate$2$XAlertDialog(view);
                    }
                });
            }
            if (this.neutralButtonText != null) {
                TextView textView11 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView11.setMinWidth(AndroidUtilities.dp(64.0f));
                textView11.setTag(-3);
                textView11.setTextSize(1, 14.0f);
                textView11.setTextColor(getThemeColor(Theme.key_dialogButton));
                textView11.setGravity(17);
                textView11.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView11.setEllipsize(TextUtils.TruncateAt.END);
                textView11.setSingleLine(true);
                textView11.setText(this.neutralButtonText.toString().toUpperCase());
                textView11.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor(Theme.key_dialogButton)));
                textView11.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(textView11, LayoutHelper.createFrame(-2, 36, 51));
                textView11.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        XAlertDialog.this.lambda$onCreate$3$XAlertDialog(view);
                    }
                });
            }
        }
        Window window = getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(window.getAttributes());
        int i6 = this.progressViewStyle;
        if (i6 == 3 || i6 == 4) {
            params.width = -1;
        } else {
            params.dimAmount = 0.6f;
            params.flags |= 2;
            this.lastScreenWidth = AndroidUtilities.displaySize.x;
            int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(48.0f);
            if (!AndroidUtilities.isTablet()) {
                maxWidth = AndroidUtilities.dp(356.0f);
            } else if (AndroidUtilities.isSmallTablet()) {
                maxWidth = AndroidUtilities.dp(446.0f);
            } else {
                maxWidth = AndroidUtilities.dp(496.0f);
            }
            params.width = Math.min(maxWidth, calculatedWidth) + this.backgroundPaddings.left + this.backgroundPaddings.right;
        }
        View view2 = this.customView;
        if (view2 == null || !canTextInput(view2)) {
            params.flags |= 131072;
        } else {
            params.softInputMode = 4;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 0;
        }
        window.setAttributes(params);
    }

    public /* synthetic */ void lambda$onCreate$0$XAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener2 = this.onClickListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, ((Integer) v.getTag()).intValue());
        }
        dismiss();
    }

    public /* synthetic */ void lambda$onCreate$1$XAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener2 = this.positiveButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -1);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public /* synthetic */ void lambda$onCreate$2$XAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener2 = this.negativeButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            cancel();
        }
    }

    public /* synthetic */ void lambda$onCreate$3$XAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener2 = this.neutralButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        DialogInterface.OnClickListener onClickListener2 = this.onBackButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -2);
        }
    }

    /* access modifiers changed from: private */
    public void showCancelAlert() {
        if (this.canCacnel && this.cancelDialog == null) {
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("StopLoading", R.string.StopLoading));
            builder.setPositiveButton(LocaleController.getString("WaitMore", R.string.WaitMore), (DialogInterface.OnClickListener) null);
            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    XAlertDialog.this.lambda$showCancelAlert$4$XAlertDialog(dialogInterface, i);
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    XAlertDialog.this.lambda$showCancelAlert$5$XAlertDialog(dialogInterface);
                }
            });
            this.cancelDialog = builder.show();
        }
    }

    public /* synthetic */ void lambda$showCancelAlert$4$XAlertDialog(DialogInterface dialogInterface, int i) {
        DialogInterface.OnCancelListener onCancelListener2 = this.onCancelListener;
        if (onCancelListener2 != null) {
            onCancelListener2.onCancel(this);
        }
        dismiss();
    }

    public /* synthetic */ void lambda$showCancelAlert$5$XAlertDialog(DialogInterface dialog) {
        this.cancelDialog = null;
    }

    /* access modifiers changed from: private */
    public void runShadowAnimation(final int num, boolean show) {
        if ((show && !this.shadowVisibility[num]) || (!show && this.shadowVisibility[num])) {
            this.shadowVisibility[num] = show;
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[num] != null) {
                animatorSetArr[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            BitmapDrawable[] bitmapDrawableArr = this.shadow;
            if (bitmapDrawableArr[num] != null) {
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                BitmapDrawable bitmapDrawable = bitmapDrawableArr[num];
                int[] iArr = new int[1];
                iArr[0] = show ? 255 : 0;
                animatorArr[0] = ObjectAnimator.ofInt(bitmapDrawable, "alpha", iArr);
                animatorSet.playTogether(animatorArr);
            }
            this.shadowAnimation[num].setDuration(150);
            this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (XAlertDialog.this.shadowAnimation[num] != null && XAlertDialog.this.shadowAnimation[num].equals(animation)) {
                        XAlertDialog.this.shadowAnimation[num] = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (XAlertDialog.this.shadowAnimation[num] != null && XAlertDialog.this.shadowAnimation[num].equals(animation)) {
                        XAlertDialog.this.shadowAnimation[num] = null;
                    }
                }
            });
            try {
                this.shadowAnimation[num].start();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void setProgressStyle(int style) {
        this.progressViewStyle = style;
    }

    public void setDismissDialogByButtons(boolean value) {
        this.dismissDialogByButtons = value;
    }

    public void setProgress(int progress) {
        this.currentProgress = progress;
        LineProgressView lineProgressView2 = this.lineProgressView;
        if (lineProgressView2 != null) {
            lineProgressView2.setProgress(((float) progress) / 100.0f, true);
            updateLineProgressTextView();
        }
    }

    private void updateLineProgressTextView() {
        this.lineProgressViewPercent.setText(String.format("%d%%", new Object[]{Integer.valueOf(this.currentProgress)}));
    }

    public void setCanCacnel(boolean value) {
        this.canCacnel = value;
    }

    private boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            if (canTextInput(vg.getChildAt(i))) {
                return true;
            }
        }
        return false;
    }

    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            FileLog.e("XAlertDailog show e: " + e.getMessage());
        }
    }

    public void dismiss() {
        XAlertDialog xAlertDialog = this.cancelDialog;
        if (xAlertDialog != null) {
            xAlertDialog.dismiss();
        }
        try {
            super.dismiss();
        } catch (Throwable th) {
        }
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    public void setTopImage(int resId, int backgroundColor) {
        this.topResId = resId;
        this.topBackgroundColor = backgroundColor;
    }

    public void setLoadingText(CharSequence text) {
        this.loadingText = text;
        TextView textView = this.tvLoadingView;
        if (textView != null) {
            textView.setText(text);
            this.tvLoadingView.setVisibility(TextUtils.isEmpty(this.loadingText) ? 8 : 0);
        }
    }

    public void setLoadingImage(Drawable drawable, int width, int height) {
        ProgressBar progressBar = this.loadingProgressView;
        if (progressBar != null) {
            drawable.setBounds((progressBar.getMeasuredWidth() - width) / 2, (this.loadingProgressView.getMeasuredHeight() - height) / 2, (this.loadingProgressView.getMeasuredWidth() + width) / 2, (this.loadingProgressView.getMeasuredHeight() + height) / 2);
            this.loadingProgressView.setIndeterminateDrawable(drawable);
        }
    }

    public void setTopHeight(int value) {
        this.topHeight = value;
    }

    public void setTopImage(Drawable drawable, int backgroundColor) {
        this.topDrawable = drawable;
        this.topBackgroundColor = backgroundColor;
    }

    public void setTitle(CharSequence text) {
        this.title = text;
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setText(text);
        }
    }

    public void setSecondTitle(CharSequence text) {
        this.secondTitle = text;
    }

    public void setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveButtonListener = listener;
    }

    public void setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.negativeButtonText = text;
        this.negativeButtonListener = listener;
    }

    public void setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.neutralButtonText = text;
        this.neutralButtonListener = listener;
    }

    public void setItemColor(int item, int color, int icon) {
        if (item >= 0 && item < this.itemViews.size()) {
            AlertDialogCell cell = this.itemViews.get(item);
            cell.textView.setTextColor(color);
            cell.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
        }
    }

    public int getItemsCount() {
        return this.itemViews.size();
    }

    public void setMessage(CharSequence text) {
        this.message = text;
        if (this.messageTextView == null) {
            return;
        }
        if (!TextUtils.isEmpty(text)) {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
            return;
        }
        this.messageTextView.setVisibility(8);
    }

    public void setMessageTextViewClickable(boolean value) {
        this.messageTextViewClickable = value;
    }

    public void setButton(int type, CharSequence text, DialogInterface.OnClickListener listener) {
        if (type == -3) {
            this.neutralButtonText = text;
            this.neutralButtonListener = listener;
        } else if (type == -2) {
            this.negativeButtonText = text;
            this.negativeButtonListener = listener;
        } else if (type == -1) {
            this.positiveButtonText = text;
            this.positiveButtonListener = listener;
        }
    }

    public View getButton(int type) {
        FrameLayout frameLayout = this.buttonsLayout;
        if (frameLayout != null) {
            return frameLayout.findViewWithTag(Integer.valueOf(type));
        }
        return null;
    }

    public void invalidateDrawable(Drawable who) {
        this.contentScrollView.invalidate();
        this.scrollContainer.invalidate();
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.postDelayed(what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.removeCallbacks(what);
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        this.onCancelListener = listener;
        super.setOnCancelListener(listener);
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener listener) {
        this.positiveButtonListener = listener;
    }

    /* access modifiers changed from: protected */
    public int getThemeColor(String key) {
        return Theme.getColor(key);
    }

    public static class Builder {
        private XAlertDialog alertDialog;

        protected Builder(XAlertDialog alert) {
            this.alertDialog = alert;
        }

        public Builder(Context context) {
            this.alertDialog = new XAlertDialog(context, 0);
        }

        public Builder(Context context, int progressViewStyle) {
            this.alertDialog = new XAlertDialog(context, progressViewStyle);
        }

        public Context getContext() {
            return this.alertDialog.getContext();
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.alertDialog.items = items;
            DialogInterface.OnClickListener unused2 = this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.alertDialog.items = items;
            int[] unused2 = this.alertDialog.itemIcons = icons;
            DialogInterface.OnClickListener unused3 = this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setView(View view) {
            View unused = this.alertDialog.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            CharSequence unused = this.alertDialog.title = title;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            CharSequence unused = this.alertDialog.subtitle = subtitle;
            return this;
        }

        public Builder setTopImage(int resId, int backgroundColor) {
            int unused = this.alertDialog.topResId = resId;
            int unused2 = this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTopImage(Drawable drawable, int backgroundColor) {
            Drawable unused = this.alertDialog.topDrawable = drawable;
            int unused2 = this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            CharSequence unused = this.alertDialog.message = message;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            CharSequence unused = this.alertDialog.positiveButtonText = text;
            DialogInterface.OnClickListener unused2 = this.alertDialog.positiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            CharSequence unused = this.alertDialog.negativeButtonText = text;
            DialogInterface.OnClickListener unused2 = this.alertDialog.negativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            CharSequence unused = this.alertDialog.neutralButtonText = text;
            DialogInterface.OnClickListener unused2 = this.alertDialog.neutralButtonListener = listener;
            return this;
        }

        public Builder setOnBackButtonListener(DialogInterface.OnClickListener listener) {
            DialogInterface.OnClickListener unused = this.alertDialog.onBackButtonListener = listener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener listener) {
            this.alertDialog.setOnCancelListener(listener);
            return this;
        }

        public Builder setCustomViewOffset(int offset) {
            int unused = this.alertDialog.customViewOffset = offset;
            return this;
        }

        public Builder setMessageTextViewClickable(boolean value) {
            boolean unused = this.alertDialog.messageTextViewClickable = value;
            return this;
        }

        public XAlertDialog create() {
            return this.alertDialog;
        }

        public XAlertDialog show() {
            this.alertDialog.show();
            return this.alertDialog;
        }

        public Runnable getDismissRunnable() {
            return this.alertDialog.dismissRunnable;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.alertDialog.setOnDismissListener(onDismissListener);
            return this;
        }
    }
}

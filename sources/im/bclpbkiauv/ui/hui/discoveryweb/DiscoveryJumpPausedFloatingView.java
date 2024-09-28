package im.bclpbkiauv.ui.hui.discoveryweb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.blankj.utilcode.util.SizeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.BlurKit;
import im.bclpbkiauv.ui.actionbar.ActionBarLayout;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hviews.CircleImageView;
import im.bclpbkiauv.ui.hviews.MryAlphaImageView;
import im.bclpbkiauv.ui.hviews.MryFrameLayout;
import im.bclpbkiauv.ui.hviews.MryLinearLayout;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.dragView.DragCallBack;
import im.bclpbkiauv.ui.hviews.dragView.DragHelperFrameLayout;

public class DiscoveryJumpPausedFloatingView {
    private static final int INTERVAL_AUTO_HIDE_TO_SIDE = 2000;
    private static volatile DiscoveryJumpPausedFloatingView Instance;
    private ActionBarLayout actionBarLayout;
    private boolean canDestroy;
    private Context context;
    /* access modifiers changed from: private */
    public LinearLayout gamePalyingNameAndCloseBtnParent;
    private MryAlphaImageView gamePlayingCloseBtn;
    /* access modifiers changed from: private */
    public DragHelperFrameLayout gamePlayingRootView;
    /* access modifiers changed from: private */
    public MryLinearLayout gamePlayingTagContainer;
    private CircleImageView gamePlayingTagIcon;
    private MryTextView gamePlayingTagName;
    /* access modifiers changed from: private */
    public boolean isFirstShow = true;
    /* access modifiers changed from: private */
    public boolean isHiding;
    /* access modifiers changed from: private */
    public boolean isShowing;
    private int mGamePlayingTagContainerBgHasChangedStatus = 3;
    private boolean mShowBlur = true;
    /* access modifiers changed from: private */
    public ValueAnimator menuExpandAnimator;
    /* access modifiers changed from: private */
    public boolean menuExpandStatus = true;
    /* access modifiers changed from: private */
    public int menuExpandWidth;
    private boolean needResetParent;
    private ViewGroup rootViewContainer;

    public static DiscoveryJumpPausedFloatingView getInstance() {
        if (Instance == null) {
            synchronized (DiscoveryJumpPausedFloatingView.class) {
                if (Instance == null) {
                    Instance = new DiscoveryJumpPausedFloatingView();
                }
            }
        }
        return Instance;
    }

    private DiscoveryJumpPausedFloatingView() {
    }

    public DiscoveryJumpPausedFloatingView setContext(Context context2) {
        this.context = context2;
        return this;
    }

    public DiscoveryJumpPausedFloatingView setRootViewContainer(ViewGroup rootViewContainer2) {
        ViewGroup oldRootViewContainer = this.rootViewContainer;
        this.rootViewContainer = rootViewContainer2;
        if (!(oldRootViewContainer == null || rootViewContainer2 == oldRootViewContainer)) {
            this.needResetParent = true;
        }
        return this;
    }

    public DiscoveryJumpPausedFloatingView setActionBarLayout(ActionBarLayout actionBarLayout2) {
        this.actionBarLayout = actionBarLayout2;
        return this;
    }

    public void create() {
        if (this.gamePlayingRootView == null) {
            this.gamePlayingRootView = new DragHelperFrameLayout(this.context) {
                /* access modifiers changed from: protected */
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    if (DiscoveryJumpPausedFloatingView.this.gamePalyingNameAndCloseBtnParent != null) {
                        DiscoveryJumpPausedFloatingView discoveryJumpPausedFloatingView = DiscoveryJumpPausedFloatingView.this;
                        int unused = discoveryJumpPausedFloatingView.menuExpandWidth = discoveryJumpPausedFloatingView.gamePalyingNameAndCloseBtnParent.getMeasuredWidth();
                    }
                    DiscoveryJumpPausedFloatingView discoveryJumpPausedFloatingView2 = DiscoveryJumpPausedFloatingView.this;
                    discoveryJumpPausedFloatingView2.log("onMeasure", " , menuExpandWidth = " + DiscoveryJumpPausedFloatingView.this.menuExpandWidth);
                }

                public boolean dispatchTouchEvent(MotionEvent ev) {
                    if (!DiscoveryJumpPausedFloatingView.this.menuExpandStatus) {
                        return super.dispatchTouchEvent(ev);
                    }
                    if (isTouchCaptureView(ev)) {
                        return super.dispatchTouchEvent(ev);
                    }
                    DiscoveryJumpPausedFloatingView.this.expandMenu(false);
                    return true;
                }
            };
        }
        this.gamePlayingRootView.setBackgroundColor(0);
        if (this.gamePlayingTagContainer == null) {
            MryLinearLayout mryLinearLayout = new MryLinearLayout(this.context);
            this.gamePlayingTagContainer = mryLinearLayout;
            mryLinearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.gamePlayingTagContainer.setOrientation(0);
            this.gamePlayingTagContainer.setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
            this.gamePlayingTagContainer.setGravity(16);
            this.gamePlayingTagContainer.setShadowColor(-16777216);
            this.gamePlayingTagContainer.setRadiusAndShadow(AndroidUtilities.dp(26.5f), 2, AndroidUtilities.dp(15.0f), 1.0f);
            this.gamePlayingRootView.addView(this.gamePlayingTagContainer, LayoutHelper.createFrame(-2.0f, 53.0f, 8388693, 0.0f, (float) SizeUtils.px2dp((float) AndroidUtilities.statusBarHeight), 0.0f, 150.0f));
        }
        if (this.gamePlayingTagIcon == null) {
            MryFrameLayout iconContainer = new MryFrameLayout(this.context);
            iconContainer.setRadius(AndroidUtilities.dp(23.5f));
            CircleImageView circleImageView = new CircleImageView(this.context);
            this.gamePlayingTagIcon = circleImageView;
            circleImageView.setImageResource(R.mipmap.bl);
            this.gamePlayingTagContainer.addView(iconContainer, LayoutHelper.createLinear(47, 47, 16));
            iconContainer.addView(this.gamePlayingTagIcon, LayoutHelper.createFrame(23, 23, 17));
        }
        if (this.gamePalyingNameAndCloseBtnParent == null) {
            LinearLayout linearLayout = new LinearLayout(this.context);
            this.gamePalyingNameAndCloseBtnParent = linearLayout;
            linearLayout.setOrientation(0);
            this.gamePalyingNameAndCloseBtnParent.setPadding(0, 0, AndroidUtilities.dp(12.0f), 0);
            this.gamePlayingTagContainer.addView(this.gamePalyingNameAndCloseBtnParent, LayoutHelper.createFrame(-2, -2, 16));
        }
        if (this.gamePlayingTagName == null) {
            MryTextView mryTextView = new MryTextView(this.context);
            this.gamePlayingTagName = mryTextView;
            mryTextView.setTextSize(13.0f);
            this.gamePlayingTagName.setSingleLine();
            this.gamePlayingTagName.setGravity(16);
            this.gamePlayingTagName.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.gamePlayingTagName.setPadding(AndroidUtilities.dp(15.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.gamePalyingNameAndCloseBtnParent.addView(this.gamePlayingTagName, LayoutHelper.createLinear(-2, -2, 16));
        }
        this.gamePlayingTagName.setText(DiscoveryJumpToPage.getTitle());
        if (this.gamePlayingCloseBtn == null) {
            MryAlphaImageView mryAlphaImageView = new MryAlphaImageView(this.context);
            this.gamePlayingCloseBtn = mryAlphaImageView;
            mryAlphaImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    DiscoveryJumpPausedFloatingView.this.lambda$create$0$DiscoveryJumpPausedFloatingView(view);
                }
            });
            this.gamePlayingCloseBtn.setImageResource(R.mipmap.ic_turn_off);
            this.gamePalyingNameAndCloseBtnParent.addView(this.gamePlayingCloseBtn, LayoutHelper.createLinear(34, 34, 16));
        }
        this.gamePlayingTagContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DiscoveryJumpPausedFloatingView.this.lambda$create$2$DiscoveryJumpPausedFloatingView(view);
            }
        });
        if (this.gamePlayingRootView.getViewDragCallBack() == null) {
            DragHelperFrameLayout dragHelperFrameLayout = this.gamePlayingRootView;
            dragHelperFrameLayout.setViewDragCallBack(new DragCallBack(dragHelperFrameLayout, this.gamePlayingTagContainer) {
                public boolean tryCaptureView(View child, int pointerId) {
                    if (DiscoveryJumpPausedFloatingView.this.menuExpandStatus) {
                        return false;
                    }
                    boolean result = super.tryCaptureView(child, pointerId);
                    if (result) {
                        if (DiscoveryJumpPausedFloatingView.this.isFirstShow) {
                            boolean unused = DiscoveryJumpPausedFloatingView.this.isFirstShow = false;
                            ((FrameLayout.LayoutParams) DiscoveryJumpPausedFloatingView.this.gamePlayingTagContainer.getLayoutParams()).bottomMargin = 0;
                        }
                        DiscoveryJumpPausedFloatingView.this.expandMenu(false);
                    }
                    return result;
                }

                public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                    super.onViewPositionChanged(changedView, left, top, dx, dy);
                    DiscoveryJumpPausedFloatingView discoveryJumpPausedFloatingView = DiscoveryJumpPausedFloatingView.this;
                    discoveryJumpPausedFloatingView.log("onViewPositionChanged", " , left = " + left + " , top = " + top + " , right = " + changedView.getRight() + " , bottom = " + changedView.getBottom() + " , getMeasuredWidth = " + changedView.getMeasuredWidth() + " , width = " + changedView.getWidth() + " , rootWidth = " + DiscoveryJumpPausedFloatingView.this.gamePlayingRootView.getMeasuredWidth() + " , isDrag = " + isDraging() + " , mCloseToSideWhenViewRealeased = " + this.mCloseToSideWhenViewRealeased);
                    if (!isDraging()) {
                        if (this.mCloseToSideWhenViewRealeased == 1 && left == 0) {
                            DiscoveryJumpPausedFloatingView.this.changeGamePlayingTagContainerBg(false, 1);
                            return;
                        } else if (this.mCloseToSideWhenViewRealeased == 3 && left == this.mParent.getWidth() - changedView.getWidth()) {
                            DiscoveryJumpPausedFloatingView.this.changeGamePlayingTagContainerBg(false, 3);
                            return;
                        }
                    }
                    DiscoveryJumpPausedFloatingView.this.changeGamePlayingTagContainerBg(true, 0);
                }

                public void onViewReleased(View releasedChild, float xvel, float yvel) {
                    int left;
                    this.mIsDraging = false;
                    if (this.mAutoBackBorderAfterRelease && releasedChild == this.mCapturedView && this.mParent != null) {
                        boolean toTB = Math.min(releasedChild.getTop(), this.mParent.getBottom() - releasedChild.getBottom()) <= Math.min(releasedChild.getLeft(), this.mParent.getRight() - releasedChild.getRight());
                        int top = releasedChild.getTop();
                        if (releasedChild.getLeft() < (this.mParent.getWidth() - releasedChild.getWidth()) / 2) {
                            left = 0;
                            this.mCloseToSideWhenViewRealeased = 1;
                        } else {
                            left = this.mParent.getRight() - releasedChild.getMeasuredWidth();
                            this.mCloseToSideWhenViewRealeased = 3;
                        }
                        if (this.mHelper != null) {
                            if (this.mNotchRects != null && this.mNotchRects.size() > 0) {
                                calculateForNotchRects(toTB, left, top);
                            } else if (this.mNotchRects == null) {
                                this.mNotchRects = getNotchRectList();
                            }
                            this.mHelper.settleCapturedViewAt(left, top);
                            if (left == 0 || left == this.mParent.getWidth() - releasedChild.getWidth()) {
                                DiscoveryJumpPausedFloatingView.this.changeGamePlayingTagContainerBg(false, this.mCloseToSideWhenViewRealeased);
                            }
                            this.mParent.invalidate();
                        }
                    }
                    DiscoveryJumpPausedFloatingView discoveryJumpPausedFloatingView = DiscoveryJumpPausedFloatingView.this;
                    discoveryJumpPausedFloatingView.log("onViewReleased ", "releasedChild  = " + releasedChild + " , left = " + releasedChild.getLeft() + " , top = " + releasedChild.getTop() + " , right = " + releasedChild.getRight() + " , bottom = " + releasedChild.getBottom() + " , isDrag = " + isDraging() + " , mCloseToSideWhenViewRealeased = " + this.mCloseToSideWhenViewRealeased);
                }
            });
        }
        this.canDestroy = true;
    }

    public /* synthetic */ void lambda$create$0$DiscoveryJumpPausedFloatingView(View v) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("3531 ===> DiscoveryJumpPausedFloatingView click close button");
        }
        DiscoveryJumpToPage.destroyGameWebView();
        hide(true);
    }

    public /* synthetic */ void lambda$create$2$DiscoveryJumpPausedFloatingView(View v) {
        if (!this.menuExpandStatus) {
            expandMenu(true);
        } else if (DiscoveryJumpToPage.checkCanToPausedPlayGamePage()) {
            ActionBarLayout actionBarLayout2 = this.actionBarLayout;
            if (actionBarLayout2 != null) {
                actionBarLayout2.presentFragment(DiscoveryJumpToPage.toPausedPlayGamePage());
                this.gamePlayingRootView.setVisibility(8);
            }
        } else {
            DragHelperFrameLayout dragHelperFrameLayout = this.gamePlayingRootView;
            if (dragHelperFrameLayout != null) {
                dragHelperFrameLayout.setVisibility(8);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setMessage("游戏已失效，请重新进入！");
            builder.setPositiveButton(LocaleController.getString(R.string.OK), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DiscoveryJumpPausedFloatingView.this.lambda$null$1$DiscoveryJumpPausedFloatingView(dialogInterface, i);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public /* synthetic */ void lambda$null$1$DiscoveryJumpPausedFloatingView(DialogInterface dialog, int which) {
        DiscoveryJumpToPage.destroyGameWebView();
        hide(true);
    }

    public void hide(boolean animated) {
        DragHelperFrameLayout dragHelperFrameLayout;
        if (!this.isHiding && this.gamePlayingTagContainer != null && (dragHelperFrameLayout = this.gamePlayingRootView) != null) {
            this.isHiding = true;
            if (animated) {
                expandMenu(false);
                Animator a3 = ObjectAnimator.ofFloat(this.gamePlayingTagContainer, View.ALPHA, new float[]{1.0f, 0.0f});
                a3.setDuration(300);
                a3.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        boolean unused = DiscoveryJumpPausedFloatingView.this.isHiding = false;
                        if (DiscoveryJumpPausedFloatingView.this.gamePlayingRootView != null) {
                            DiscoveryJumpPausedFloatingView.this.gamePlayingRootView.setVisibility(8);
                        }
                    }
                });
                a3.start();
                return;
            }
            dragHelperFrameLayout.setVisibility(8);
        }
    }

    public void show(boolean animated) {
        DragHelperFrameLayout dragHelperFrameLayout;
        create();
        if (this.gamePlayingTagContainer != null && (dragHelperFrameLayout = this.gamePlayingRootView) != null && this.rootViewContainer != null) {
            if (dragHelperFrameLayout.getParent() != null && this.needResetParent) {
                ((ViewGroup) this.gamePlayingRootView.getParent()).removeView(this.gamePlayingRootView);
            }
            if (this.gamePlayingRootView.getParent() == null) {
                this.rootViewContainer.addView(this.gamePlayingRootView, LayoutHelper.createFrame(-1, -1.0f));
            }
            this.gamePlayingRootView.bringToFront();
            boolean z = false;
            this.gamePlayingRootView.setVisibility(0);
            this.gamePlayingTagContainer.setVisibility(0);
            StringBuilder sb = new StringBuilder();
            sb.append(" , gamePlayingRootView = ");
            sb.append(this.gamePlayingRootView);
            sb.append(" , gamePlayingRootView.getVisibility = ");
            sb.append(this.gamePlayingRootView.getVisibility() == 0);
            sb.append(" , gamePlayingTagContainer.getVisibility = ");
            if (this.gamePlayingTagContainer.getVisibility() == 0) {
                z = true;
            }
            sb.append(z);
            log("show", sb.toString());
            if (!this.isShowing) {
                this.isShowing = true;
                if (animated) {
                    AnimatorListenerAdapter listenerAdapter = new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            boolean unused = DiscoveryJumpPausedFloatingView.this.isShowing = false;
                            DiscoveryJumpPausedFloatingView.this.expandMenu(false);
                        }
                    };
                    Animator a = ObjectAnimator.ofFloat(this.gamePlayingTagContainer, View.ALPHA, new float[]{0.0f, 1.0f});
                    a.setDuration(300);
                    a.addListener(listenerAdapter);
                    a.start();
                }
            }
        }
    }

    public void expandMenu(boolean expand) {
        expandMenu(expand, true);
    }

    public void expandMenu(final boolean expand, boolean animated) {
        int v2;
        int v1;
        if (!this.isHiding && !this.isShowing && this.gamePlayingTagContainer != null && expand != this.menuExpandStatus) {
            ValueAnimator valueAnimator = this.menuExpandAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (animated) {
                if (expand) {
                    changeGameNameAndCloseView(true);
                    v1 = 0;
                    v2 = this.menuExpandWidth;
                } else {
                    this.menuExpandWidth = this.gamePalyingNameAndCloseBtnParent.getWidth();
                    v1 = this.menuExpandWidth;
                    v2 = 0;
                }
                this.menuExpandAnimator = ValueAnimator.ofInt(new int[]{v1, v2});
                log("expandMenu", " , menuExpandStatus = " + this.menuExpandStatus + " , v1 = " + v1 + " , v2 = " + v2 + " , width = " + this.gamePlayingTagContainer.getMeasuredWidth() + " , rootWidth = " + this.gamePlayingRootView.getMeasuredWidth() + " , menuExpandWidth = " + this.menuExpandWidth);
                this.menuExpandAnimator.setTarget(this.gamePalyingNameAndCloseBtnParent);
                this.menuExpandAnimator.setDuration(300);
                this.menuExpandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(expand, this.menuExpandWidth) {
                    private final /* synthetic */ boolean f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        DiscoveryJumpPausedFloatingView.this.lambda$expandMenu$3$DiscoveryJumpPausedFloatingView(this.f$1, this.f$2, valueAnimator);
                    }
                });
                this.menuExpandAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        DiscoveryJumpPausedFloatingView.this.log("expandMenu", " , onAnimationEnd ");
                        if (DiscoveryJumpPausedFloatingView.this.menuExpandAnimator != null && DiscoveryJumpPausedFloatingView.this.menuExpandAnimator.equals(animation)) {
                            ValueAnimator unused = DiscoveryJumpPausedFloatingView.this.menuExpandAnimator = null;
                            if (!expand) {
                                DiscoveryJumpPausedFloatingView.this.changeGameNameAndCloseView(false);
                            }
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        DiscoveryJumpPausedFloatingView.this.log("expandMenu", " , onAnimationCancel ");
                        if (DiscoveryJumpPausedFloatingView.this.menuExpandAnimator != null && DiscoveryJumpPausedFloatingView.this.menuExpandAnimator.equals(animation)) {
                            ValueAnimator unused = DiscoveryJumpPausedFloatingView.this.menuExpandAnimator = null;
                        }
                    }
                });
                this.menuExpandAnimator.start();
            } else {
                changeGameNameAndCloseView(expand);
            }
            this.menuExpandStatus = expand;
            handleBlurBackground();
        }
    }

    public /* synthetic */ void lambda$expandMenu$3$DiscoveryJumpPausedFloatingView(boolean expand, int finalTemWidth, ValueAnimator animation) {
        if (this.gamePlayingTagContainer != null) {
            int value = ((Integer) animation.getAnimatedValue()).intValue();
            ViewGroup.LayoutParams lp = this.gamePalyingNameAndCloseBtnParent.getLayoutParams();
            lp.width = value;
            this.gamePalyingNameAndCloseBtnParent.setLayoutParams(lp);
            if (expand && value == finalTemWidth) {
                lp.width = -2;
                this.gamePalyingNameAndCloseBtnParent.setLayoutParams(lp);
            }
            log("expandMenu onAnimationUpdate", " , value = " + value + " , getWidth = " + this.gamePlayingTagContainer.getWidth() + " , getMeasuredWidth = " + this.gamePlayingTagContainer.getMeasuredWidth() + " , lp.width = " + lp.width + " , menuExpandWidth = " + this.menuExpandWidth);
        }
    }

    /* access modifiers changed from: private */
    public void changeGamePlayingTagContainerBg(boolean isDraging, int side) {
        if (isDraging) {
            if (this.mGamePlayingTagContainerBgHasChangedStatus != 0) {
                this.mGamePlayingTagContainerBgHasChangedStatus = 0;
                this.gamePlayingTagContainer.setRadiusAndShadow(AndroidUtilities.dp(26.5f), 0, AndroidUtilities.dp(15.0f), 1.0f);
            }
        } else if (side == 1) {
            if (this.mGamePlayingTagContainerBgHasChangedStatus != 1) {
                this.mGamePlayingTagContainerBgHasChangedStatus = 1;
                this.gamePlayingTagContainer.setRadiusAndShadow(AndroidUtilities.dp(26.5f), 4, AndroidUtilities.dp(15.0f), 1.0f);
            }
        } else if (this.mGamePlayingTagContainerBgHasChangedStatus != 3) {
            this.mGamePlayingTagContainerBgHasChangedStatus = 3;
            this.gamePlayingTagContainer.setRadiusAndShadow(AndroidUtilities.dp(26.5f), 2, AndroidUtilities.dp(15.0f), 1.0f);
        }
    }

    /* access modifiers changed from: private */
    public void changeGameNameAndCloseView(boolean show) {
        LinearLayout linearLayout = this.gamePalyingNameAndCloseBtnParent;
        if (linearLayout != null) {
            if (show && linearLayout.getVisibility() != 0) {
                this.gamePalyingNameAndCloseBtnParent.setVisibility(0);
            } else if (!show && this.gamePalyingNameAndCloseBtnParent.getVisibility() != 8) {
                this.gamePalyingNameAndCloseBtnParent.setVisibility(8);
            }
        }
    }

    private void handleBlurBackground() {
        ViewGroup viewGroup;
        if (this.mShowBlur && (viewGroup = this.rootViewContainer) != null && this.gamePlayingRootView != null) {
            BlurKit.init(viewGroup.getContext());
            if (!this.menuExpandStatus) {
                this.gamePlayingRootView.setBackground((Drawable) null);
            } else if (Build.VERSION.SDK_INT >= 17) {
                ViewGroup viewGroup2 = this.rootViewContainer;
                if (viewGroup2 != null) {
                    applyBlurDim(viewGroup2);
                } else if (viewGroup2 != null && viewGroup2.getContext() != null && (this.rootViewContainer.getContext() instanceof Activity)) {
                    applyBlurDim((Activity) this.rootViewContainer.getContext());
                }
            }
        }
    }

    private void applyBlurDim(ViewGroup dimView) {
        Drawable dimDrawable = new BitmapDrawable((Resources) null, BlurKit.getInstance().fastBlur(dimView, 10, 0.12f));
        DragHelperFrameLayout dragHelperFrameLayout = this.gamePlayingRootView;
        if (dragHelperFrameLayout != null) {
            dragHelperFrameLayout.setBackground(dimDrawable);
        }
    }

    private void applyBlurDim(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        Drawable dimDrawable = new BitmapDrawable((Resources) null, BlurKit.getInstance().fastBlur(parent, 10, 0.25f));
        dimDrawable.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        parent.getOverlay().add(dimDrawable);
    }

    public void log(String desc, String msg) {
        if (BuildVars.DEBUG_VERSION) {
            Log.i("GamePlayingPaused", "DragCallBack ===> " + desc + msg);
        }
    }

    public boolean canDestroy() {
        return this.canDestroy;
    }

    public static void destroy() {
        if (Instance != null) {
            Instance.context = null;
            if (Instance.rootViewContainer != null && Instance.gamePlayingRootView != null) {
                Instance.gamePlayingRootView.removeAllViews();
                if (Instance.gamePlayingRootView.getParent() == Instance.rootViewContainer) {
                    int count = Instance.rootViewContainer.getChildCount();
                    int i = 0;
                    while (true) {
                        if (i >= count) {
                            break;
                        }
                        View child = Instance.rootViewContainer.getChildAt(i);
                        if (child instanceof DragHelperFrameLayout) {
                            Instance.rootViewContainer.removeView(child);
                            break;
                        }
                        i++;
                    }
                }
            }
            Instance.gamePlayingRootView = null;
            Instance.gamePlayingTagContainer = null;
            Instance.gamePlayingTagIcon = null;
            Instance.gamePlayingTagName = null;
            Instance.gamePlayingCloseBtn = null;
            Instance.isHiding = false;
            Instance.isShowing = false;
            Instance.isFirstShow = true;
            Instance.menuExpandStatus = false;
            Instance.canDestroy = false;
            if (Instance.menuExpandAnimator != null) {
                Instance.menuExpandAnimator.cancel();
            }
            Instance.menuExpandAnimator = null;
            Instance.menuExpandWidth = 0;
            Instance.mGamePlayingTagContainerBgHasChangedStatus = 3;
            Instance = null;
        }
    }
}

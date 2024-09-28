package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ContactsActivity;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ScrollSlidingTextTabStrip;
import im.bclpbkiauv.ui.components.banner.config.BannerConfig;
import im.bclpbkiauv.ui.hui.decoration.TopBottomDecoration;
import java.util.ArrayList;
import java.util.Collections;

public class DialogOrContactPickerActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = $$Lambda$DialogOrContactPickerActivity$2Wn4MfQuiCHrqVx1WSICMJvuBq8.INSTANCE;
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    /* access modifiers changed from: private */
    public ContactsActivity contactsActivity;
    /* access modifiers changed from: private */
    public DialogsActivity dialogsActivity;
    /* access modifiers changed from: private */
    public int maximumVelocity;
    /* access modifiers changed from: private */
    public ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    /* access modifiers changed from: private */
    public ViewPage[] viewPages = new ViewPage[2];

    private class ViewPage extends FrameLayout {
        /* access modifiers changed from: private */
        public ActionBar actionBar;
        /* access modifiers changed from: private */
        public FrameLayout fragmentView;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public BaseFragment parentFragment;
        /* access modifiers changed from: private */
        public int selectedType;

        public ViewPage(Context context) {
            super(context);
        }
    }

    static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    public DialogOrContactPickerActivity() {
        Bundle args = new Bundle();
        args.putBoolean("onlySelect", true);
        args.putBoolean("checkCanWrite", false);
        args.putBoolean("resetDelegate", false);
        args.putInt("dialogsType", 4);
        DialogsActivity dialogsActivity2 = new DialogsActivity(args);
        this.dialogsActivity = dialogsActivity2;
        dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
            public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                DialogOrContactPickerActivity.this.lambda$new$1$DialogOrContactPickerActivity(dialogsActivity, arrayList, charSequence, z);
            }
        });
        this.dialogsActivity.onFragmentCreate();
        Bundle args2 = new Bundle();
        args2.putBoolean("onlyUsers", true);
        args2.putBoolean("destroyAfterSelect", true);
        args2.putBoolean("returnAsResult", true);
        args2.putBoolean("disableSections", true);
        args2.putBoolean("needFinishFragment", false);
        args2.putBoolean("resetDelegate", false);
        ContactsActivity contactsActivity2 = new ContactsActivity(args2);
        this.contactsActivity = contactsActivity2;
        contactsActivity2.setDelegate(new ContactsActivity.ContactsActivityDelegate() {
            public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity) {
                DialogOrContactPickerActivity.this.lambda$new$2$DialogOrContactPickerActivity(user, str, contactsActivity);
            }
        });
        this.contactsActivity.onFragmentCreate();
    }

    public /* synthetic */ void lambda$new$1$DialogOrContactPickerActivity(DialogsActivity fragment, ArrayList dids, CharSequence message, boolean param) {
        if (!dids.isEmpty()) {
            long did = ((Long) dids.get(0)).longValue();
            int lowerId = (int) did;
            if (did > 0) {
                showBlockAlert(getMessagesController().getUser(Integer.valueOf(lowerId)));
            }
        }
    }

    public /* synthetic */ void lambda$new$2$DialogOrContactPickerActivity(TLRPC.User user, String param, ContactsActivity activity) {
        showBlockAlert(user);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("BlockUserMultiTitle", R.string.BlockUserMultiTitle));
        boolean z = false;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DialogOrContactPickerActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        this.searchItem = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().openSearchField("", false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().openSearchField("", false);
                DialogOrContactPickerActivity.this.searchItem.getSearchField().requestFocus();
            }

            public void onSearchCollapse() {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().closeSearchField(false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().closeSearchField(false);
            }

            public void onTextChanged(EditText editText) {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().setSearchFieldText(editText.getText().toString());
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().setSearchFieldText(editText.getText().toString());
            }
        });
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip2;
        scrollSlidingTextTabStrip2.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int id, boolean forward) {
                if (DialogOrContactPickerActivity.this.viewPages[0].selectedType != id) {
                    DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                    boolean unused = dialogOrContactPickerActivity.swipeBackEnabled = id == dialogOrContactPickerActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    int unused2 = DialogOrContactPickerActivity.this.viewPages[1].selectedType = id;
                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                    DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                    boolean unused3 = DialogOrContactPickerActivity.this.animatingForward = forward;
                }
            }

            public void onPageScrolled(float progress) {
                if (progress != 1.0f || DialogOrContactPickerActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DialogOrContactPickerActivity.this.animatingForward) {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((-progress) * ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) - (((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * progress));
                    } else {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * progress);
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) * progress) - ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    if (progress == 1.0f) {
                        ViewPage tempPage = DialogOrContactPickerActivity.this.viewPages[0];
                        DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                        DialogOrContactPickerActivity.this.viewPages[1] = tempPage;
                        DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        FrameLayout r4 = new FrameLayout(context) {
            private boolean globalIgnoreLayout;
            /* access modifiers changed from: private */
            public boolean maybeStartTracking;
            /* access modifiers changed from: private */
            public boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent ev, boolean forward) {
                int id = DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                DialogOrContactPickerActivity.this.actionBar.setEnabled(false);
                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                int unused = DialogOrContactPickerActivity.this.viewPages[1].selectedType = id;
                DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                boolean unused2 = DialogOrContactPickerActivity.this.animatingForward = forward;
                DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (-DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                }
                return true;
            }

            public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
                super.forceHasOverlappingRendering(hasOverlappingRendering);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                measureChildWithMargins(DialogOrContactPickerActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int a = 0; a < DialogOrContactPickerActivity.this.viewPages.length; a++) {
                    if (!(DialogOrContactPickerActivity.this.viewPages[a] == null || DialogOrContactPickerActivity.this.viewPages[a].listView == null)) {
                        DialogOrContactPickerActivity.this.viewPages[a].listView.setPadding(0, actionBarHeight, 0, 0);
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == DialogOrContactPickerActivity.this.actionBar)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DialogOrContactPickerActivity.this.parentLayout != null) {
                    DialogOrContactPickerActivity.this.parentLayout.drawHeaderShadow(canvas, DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight() + ((int) DialogOrContactPickerActivity.this.actionBar.getTranslationY()));
                }
            }

            public void requestLayout() {
                if (!this.globalIgnoreLayout) {
                    super.requestLayout();
                }
            }

            public boolean checkTabsAnimationInProgress() {
                if (!DialogOrContactPickerActivity.this.tabsAnimationInProgress) {
                    return false;
                }
                boolean cancel = false;
                int i = -1;
                if (DialogOrContactPickerActivity.this.backAnimation) {
                    if (Math.abs(DialogOrContactPickerActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(0.0f);
                        ViewPage viewPage = DialogOrContactPickerActivity.this.viewPages[1];
                        int measuredWidth = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                        if (DialogOrContactPickerActivity.this.animatingForward) {
                            i = 1;
                        }
                        viewPage.setTranslationX((float) (measuredWidth * i));
                        cancel = true;
                    }
                } else if (Math.abs(DialogOrContactPickerActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                    ViewPage viewPage2 = DialogOrContactPickerActivity.this.viewPages[0];
                    int measuredWidth2 = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                    if (!DialogOrContactPickerActivity.this.animatingForward) {
                        i = 1;
                    }
                    viewPage2.setTranslationX((float) (measuredWidth2 * i));
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(0.0f);
                    cancel = true;
                }
                if (cancel) {
                    if (DialogOrContactPickerActivity.this.tabsAnimation != null) {
                        DialogOrContactPickerActivity.this.tabsAnimation.cancel();
                        AnimatorSet unused = DialogOrContactPickerActivity.this.tabsAnimation = null;
                    }
                    boolean unused2 = DialogOrContactPickerActivity.this.tabsAnimationInProgress = false;
                }
                return DialogOrContactPickerActivity.this.tabsAnimationInProgress;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                DialogOrContactPickerActivity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                canvas.drawRect(0.0f, ((float) DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight()) + DialogOrContactPickerActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), DialogOrContactPickerActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent ev) {
                float dx;
                int duration;
                boolean z = false;
                if (DialogOrContactPickerActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                if (ev != null && ev.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = ev.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) ev.getX();
                    this.startedTrackingY = (int) ev.getY();
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 != null) {
                        velocityTracker2.clear();
                    }
                } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int dx2 = (int) (ev.getX() - ((float) this.startedTrackingX));
                    int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(ev);
                    if (this.startedTracking && ((DialogOrContactPickerActivity.this.animatingForward && dx2 > 0) || (!DialogOrContactPickerActivity.this.animatingForward && dx2 < 0))) {
                        if (!prepareForMoving(ev, dx2 < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(dx2)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(dx2) / 3 > dy) {
                            if (dx2 < 0) {
                                z = true;
                            }
                            prepareForMoving(ev, z);
                        }
                    } else if (this.startedTracking) {
                        if (DialogOrContactPickerActivity.this.animatingForward) {
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((float) dx2);
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() + dx2));
                        } else {
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((float) dx2);
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((float) (dx2 - DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, ((float) Math.abs(dx2)) / ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6)) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(1000, (float) DialogOrContactPickerActivity.this.maximumVelocity);
                    if (!this.startedTracking) {
                        float velX = this.velocityTracker.getXVelocity();
                        float velY = this.velocityTracker.getYVelocity();
                        if (Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                            prepareForMoving(ev, velX < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x = DialogOrContactPickerActivity.this.viewPages[0].getX();
                        AnimatorSet unused = DialogOrContactPickerActivity.this.tabsAnimation = new AnimatorSet();
                        float velX2 = this.velocityTracker.getXVelocity();
                        boolean unused2 = DialogOrContactPickerActivity.this.backAnimation = Math.abs(x) < ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX2) < 3500.0f || Math.abs(velX2) < Math.abs(this.velocityTracker.getYVelocity()));
                        if (DialogOrContactPickerActivity.this.backAnimation) {
                            dx = Math.abs(x);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth()})});
                            } else {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth())})});
                            }
                        } else {
                            dx = ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            } else {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            }
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setInterpolator(DialogOrContactPickerActivity.interpolator);
                        int width = getMeasuredWidth();
                        int halfWidth = width / 2;
                        float distance = ((float) halfWidth) + (((float) halfWidth) * AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (dx * 1.0f) / ((float) width))));
                        float velX3 = Math.abs(velX2);
                        if (velX3 > 0.0f) {
                            duration = Math.round(Math.abs(distance / velX3) * 1000.0f) * 4;
                        } else {
                            duration = (int) ((1.0f + (dx / ((float) getMeasuredWidth()))) * 100.0f);
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(duration, BannerConfig.SCROLL_TIME)));
                        DialogOrContactPickerActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = DialogOrContactPickerActivity.this.tabsAnimation = null;
                                if (DialogOrContactPickerActivity.this.backAnimation) {
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage tempPage = DialogOrContactPickerActivity.this.viewPages[0];
                                    DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                                    DialogOrContactPickerActivity.this.viewPages[1] = tempPage;
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                    boolean unused2 = DialogOrContactPickerActivity.this.swipeBackEnabled = DialogOrContactPickerActivity.this.viewPages[0].selectedType == DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                boolean unused3 = DialogOrContactPickerActivity.this.tabsAnimationInProgress = false;
                                boolean unused4 = AnonymousClass4.this.maybeStartTracking = false;
                                boolean unused5 = AnonymousClass4.this.startedTracking = false;
                                DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DialogOrContactPickerActivity.this.tabsAnimation.start();
                        boolean unused3 = DialogOrContactPickerActivity.this.tabsAnimationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker3 = this.velocityTracker;
                    if (velocityTracker3 != null) {
                        velocityTracker3.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        FrameLayout frameLayout = r4;
        this.fragmentView = r4;
        frameLayout.setWillNotDraw(false);
        this.dialogsActivity.setParentFragment(this);
        this.contactsActivity.setParentFragment(this);
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            viewPageArr[a] = new ViewPage(context) {
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (DialogOrContactPickerActivity.this.tabsAnimationInProgress && DialogOrContactPickerActivity.this.viewPages[0] == this) {
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, Math.abs(DialogOrContactPickerActivity.this.viewPages[0].getTranslationX()) / ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                }
            };
            frameLayout.addView(this.viewPages[a], LayoutHelper.createFrame(-1, -1.0f));
            if (a == 0) {
                BaseFragment unused = this.viewPages[a].parentFragment = this.dialogsActivity;
                RecyclerListView unused2 = this.viewPages[a].listView = this.dialogsActivity.getListView();
            } else if (a == 1) {
                BaseFragment unused3 = this.viewPages[a].parentFragment = this.contactsActivity;
                RecyclerListView unused4 = this.viewPages[a].listView = this.contactsActivity.getListView();
                this.viewPages[a].setVisibility(8);
            }
            this.viewPages[a].listView.addItemDecoration(new TopBottomDecoration(0, 10));
            this.viewPages[a].setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), 0);
            ViewPage[] viewPageArr2 = this.viewPages;
            FrameLayout unused5 = viewPageArr2[a].fragmentView = (FrameLayout) viewPageArr2[a].parentFragment.getFragmentView();
            this.viewPages[a].listView.setClipToPadding(false);
            ViewPage[] viewPageArr3 = this.viewPages;
            ActionBar unused6 = viewPageArr3[a].actionBar = viewPageArr3[a].parentFragment.getActionBar();
            ViewPage[] viewPageArr4 = this.viewPages;
            viewPageArr4[a].addView(viewPageArr4[a].fragmentView, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr5 = this.viewPages;
            viewPageArr5[a].addView(viewPageArr5[a].actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.viewPages[a].actionBar.setVisibility(8);
            final RecyclerView.OnScrollListener onScrollListener = this.viewPages[a].listView.getOnScrollListener();
            this.viewPages[a].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    onScrollListener.onScrollStateChanged(recyclerView, newState);
                    if (newState != 1) {
                        int scrollY = (int) (-DialogOrContactPickerActivity.this.actionBar.getTranslationY());
                        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                        if (scrollY != 0 && scrollY != actionBarHeight) {
                            if (scrollY < actionBarHeight / 2) {
                                DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, -scrollY);
                            } else {
                                DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, actionBarHeight - scrollY);
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    onScrollListener.onScrolled(recyclerView, dx, dy);
                    if (recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView) {
                        float currentTranslation = DialogOrContactPickerActivity.this.actionBar.getTranslationY();
                        float newTranslation = currentTranslation - ((float) dy);
                        if (newTranslation < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                            newTranslation = (float) (-ActionBar.getCurrentActionBarHeight());
                        } else if (newTranslation > 0.0f) {
                            newTranslation = 0.0f;
                        }
                        if (newTranslation != currentTranslation) {
                            DialogOrContactPickerActivity.this.setScrollY(newTranslation);
                        }
                    }
                }
            });
            a++;
        }
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
            z = true;
        }
        this.swipeBackEnabled = z;
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        DialogsActivity dialogsActivity2 = this.dialogsActivity;
        if (dialogsActivity2 != null) {
            dialogsActivity2.onResume();
        }
        ContactsActivity contactsActivity2 = this.contactsActivity;
        if (contactsActivity2 != null) {
            contactsActivity2.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        DialogsActivity dialogsActivity2 = this.dialogsActivity;
        if (dialogsActivity2 != null) {
            dialogsActivity2.onPause();
        }
        ContactsActivity contactsActivity2 = this.contactsActivity;
        if (contactsActivity2 != null) {
            contactsActivity2.onPause();
        }
    }

    public void onFragmentDestroy() {
        DialogsActivity dialogsActivity2 = this.dialogsActivity;
        if (dialogsActivity2 != null) {
            dialogsActivity2.onFragmentDestroy();
        }
        ContactsActivity contactsActivity2 = this.contactsActivity;
        if (contactsActivity2 != null) {
            contactsActivity2.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    /* access modifiers changed from: private */
    public void setScrollY(float value) {
        this.actionBar.setTranslationY(value);
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a < viewPageArr.length) {
                viewPageArr[a].listView.setPinnedSectionOffsetY((int) value);
                a++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    private void showBlockAlert(TLRPC.User user) {
        if (user != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
            builder.setPositiveButton(LocaleController.getString("BlockContact", R.string.BlockContact), new DialogInterface.OnClickListener(user) {
                private final /* synthetic */ TLRPC.User f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogOrContactPickerActivity.this.lambda$showBlockAlert$3$DialogOrContactPickerActivity(this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            AlertDialog dialog = builder.create();
            showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }
    }

    public /* synthetic */ void lambda$showBlockAlert$3$DialogOrContactPickerActivity(TLRPC.User user, DialogInterface dialogInterface, int i) {
        if (MessagesController.isSupportUser(user)) {
            AlertsCreator.showSimpleToast(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
        } else {
            MessagesController.getInstance(this.currentAccount).blockUser(user.id);
            AlertsCreator.showSimpleToast(this, LocaleController.getString("UserBlocked", R.string.UserBlocked));
        }
        finishFragment();
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip2 != null) {
            scrollSlidingTextTabStrip2.addTextTab(0, LocaleController.getString("BlockUserChatsTitle", R.string.BlockUserChatsTitle));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("BlockUserContactsTitle", R.string.BlockUserContactsTitle));
            this.scrollSlidingTextTabStrip.setVisibility(0);
            this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (id >= 0) {
                int unused = this.viewPages[0].selectedType = id;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    /* access modifiers changed from: private */
    public void switchToCurrentSelectedMode(boolean animated) {
        ViewPage[] viewPageArr;
        int a = 0;
        while (true) {
            viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            viewPageArr[a].listView.stopScroll();
            a++;
        }
        int a2 = animated;
        RecyclerView.Adapter adapter = viewPageArr[a2].listView.getAdapter();
        this.viewPages[a2].listView.setPinnedHeaderShadowDrawable((Drawable) null);
        if (this.actionBar.getTranslationY() != 0.0f) {
            ((LinearLayoutManager) this.viewPages[a2].listView.getLayoutManager()).scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabActiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabUnactiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabLine));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarTabSelector));
        Collections.addAll(arrayList, this.dialogsActivity.getThemeDescriptions());
        Collections.addAll(arrayList, this.contactsActivity.getThemeDescriptions());
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }
}

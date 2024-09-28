package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.GroupCreateActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BackDrawable;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.adapters.SearchAdapterHelper;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.GroupCreateSectionCell;
import im.bclpbkiauv.ui.cells.GroupCreateUserCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.GroupCreateDividerItemDecoration;
import im.bclpbkiauv.ui.components.GroupCreateSpan;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.TypefaceSpan;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupCreateActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public GroupCreateAdapter adapter;
    private boolean addToGroup;
    /* access modifiers changed from: private */
    public ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();
    private int channelId;
    private int chatId;
    private int chatType = 0;
    /* access modifiers changed from: private */
    public int containerHeight;
    /* access modifiers changed from: private */
    public GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private ContactsAddActivityDelegate delegate2;
    private boolean doneButtonVisible;
    /* access modifiers changed from: private */
    public EditTextBoldCursor editText;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int fieldY;
    /* access modifiers changed from: private */
    public ImageView floatingButton;
    /* access modifiers changed from: private */
    public boolean ignoreScrollEvent;
    /* access modifiers changed from: private */
    public SparseArray<TLObject> ignoreUsers;
    private TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public boolean isAlwaysShare;
    private boolean isGroup;
    /* access modifiers changed from: private */
    public boolean isNeverShare;
    /* access modifiers changed from: private */
    public GroupCreateDividerItemDecoration itemDecoration;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public SparseArray<GroupCreateSpan> selectedContacts = new SparseArray<>();
    /* access modifiers changed from: private */
    public SpansContainer spansContainer;

    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Integer> arrayList);
    }

    public interface ContactsAddActivityDelegate {
        void didSelectUsers(ArrayList<TLRPC.User> arrayList, int i);

        void needAddBot(TLRPC.User user);

        /* renamed from: im.bclpbkiauv.ui.GroupCreateActivity$ContactsAddActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$needAddBot(ContactsAddActivityDelegate _this, TLRPC.User user) {
            }
        }
    }

    private class SpansContainer extends ViewGroup {
        /* access modifiers changed from: private */
        public View addingSpan;
        /* access modifiers changed from: private */
        public boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList<>();
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        /* access modifiers changed from: private */
        public View removingSpan;

        public SpansContainer(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int minWidth;
            boolean z;
            float f;
            int count = getChildCount();
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.dp(20.0f);
            int currentLineWidth = 0;
            float f2 = 10.0f;
            int y = AndroidUtilities.dp(10.0f);
            int allCurrentLineWidth = 0;
            int allY = AndroidUtilities.dp(10.0f);
            int a = 0;
            while (a < count) {
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(35.0f), 1073741824));
                    if (child != this.removingSpan && child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                        y += child.getMeasuredHeight() + AndroidUtilities.dp(f2);
                        currentLineWidth = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(f2);
                        allCurrentLineWidth = 0;
                    }
                    int x = AndroidUtilities.dp(f2) + currentLineWidth;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (child == view) {
                            child.setTranslationX((float) (AndroidUtilities.dp(f2) + allCurrentLineWidth));
                            child.setTranslationY((float) allY);
                        } else if (view != null) {
                            if (child.getTranslationX() != ((float) x)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationX", new float[]{(float) x}));
                            }
                            if (child.getTranslationY() != ((float) y)) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationY", new float[]{(float) y}));
                            }
                        } else {
                            child.setTranslationX((float) x);
                            child.setTranslationY((float) y);
                        }
                    }
                    if (child != this.removingSpan) {
                        f = 10.0f;
                        currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(10.0f);
                    } else {
                        f = 10.0f;
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(f);
                }
                a++;
                f2 = 10.0f;
            }
            if (AndroidUtilities.isTablet()) {
                minWidth = AndroidUtilities.dp(372.0f) / 3;
            } else {
                minWidth = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(158.0f)) / 3;
            }
            if (maxWidth - currentLineWidth < minWidth) {
                currentLineWidth = 0;
                y += AndroidUtilities.dp(45.0f);
            }
            if (maxWidth - allCurrentLineWidth < minWidth) {
                allY += AndroidUtilities.dp(45.0f);
            }
            GroupCreateActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(35.0f), 1073741824));
            if (!this.animationStarted) {
                int currentHeight = AndroidUtilities.dp(45.0f) + allY;
                int fieldX = AndroidUtilities.dp(10.0f) + currentLineWidth;
                int unused = GroupCreateActivity.this.fieldY = y;
                if (this.currentAnimation != null) {
                    int resultHeight = AndroidUtilities.dp(45.0f) + y;
                    if (GroupCreateActivity.this.containerHeight != resultHeight) {
                        int i = count;
                        int i2 = maxWidth;
                        this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[]{resultHeight}));
                    } else {
                        int i3 = maxWidth;
                    }
                    if (GroupCreateActivity.this.editText.getTranslationX() != ((float) fieldX)) {
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", new float[]{(float) fieldX}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationY() != ((float) GroupCreateActivity.this.fieldY)) {
                        z = false;
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationY", new float[]{(float) GroupCreateActivity.this.fieldY}));
                    } else {
                        z = false;
                    }
                    GroupCreateActivity.this.editText.setAllowDrawCursor(z);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    int i4 = maxWidth;
                    int unused2 = GroupCreateActivity.this.containerHeight = currentHeight;
                    GroupCreateActivity.this.editText.setTranslationX((float) fieldX);
                    GroupCreateActivity.this.editText.setTranslationY((float) GroupCreateActivity.this.fieldY);
                }
            } else {
                int i5 = maxWidth;
                if (this.currentAnimation != null && !GroupCreateActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                    GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
                }
            }
            setMeasuredDimension(width, GroupCreateActivity.this.containerHeight);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span) {
            GroupCreateActivity.this.allSpans.add(span);
            GroupCreateActivity.this.selectedContacts.put(span.getUid(), span);
            GroupCreateActivity.this.editText.setHintVisible(false);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    View unused = SpansContainer.this.addingSpan = null;
                    AnimatorSet unused2 = SpansContainer.this.currentAnimation = null;
                    boolean unused3 = SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            this.currentAnimation.setDuration(150);
            this.addingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            boolean unused = GroupCreateActivity.this.ignoreScrollEvent = true;
            GroupCreateActivity.this.selectedContacts.remove(span.getUid());
            GroupCreateActivity.this.allSpans.remove(span);
            span.setOnClickListener((View.OnClickListener) null);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    View unused = SpansContainer.this.removingSpan = null;
                    AnimatorSet unused2 = SpansContainer.this.currentAnimation = null;
                    boolean unused3 = SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                    if (GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public GroupCreateActivity() {
    }

    public GroupCreateActivity(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 0);
        this.isAlwaysShare = args.getBoolean("isAlwaysShare", false);
        this.isNeverShare = args.getBoolean("isNeverShare", false);
        this.addToGroup = args.getBoolean("addToGroup", false);
        this.isGroup = args.getBoolean("isGroup", false);
        this.chatId = args.getInt("chatId");
        this.channelId = args.getInt("channelId");
        if (this.isAlwaysShare || this.isNeverShare || this.addToGroup) {
            this.maxCount = 0;
        } else {
            this.maxCount = this.chatType == 0 ? MessagesController.getInstance(this.currentAccount).maxMegagroupCount : MessagesController.getInstance(this.currentAccount).maxBroadcastCount;
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
            updateHint();
            checkVisibleRows();
            return;
        }
        GroupCreateSpan groupCreateSpan = this.currentDeletingSpan;
        if (groupCreateSpan != null) {
            groupCreateSpan.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = span;
        span.startDeleteAnimation();
    }

    public View createView(Context context) {
        String str;
        int i;
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.chatType == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", R.string.ChannelAddMembers));
        } else if (this.addToGroup) {
            this.actionBar.setTitle(LocaleController.getString("GroupAddMembers", R.string.GroupAddMembers));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", R.string.AlwaysShareWithTitle));
            }
        } else if (!this.isNeverShare) {
            ActionBar actionBar = this.actionBar;
            if (this.chatType == 0) {
                i = R.string.NewGroup;
                str = "NewGroup";
            } else {
                i = R.string.NewBroadcastList;
                str = "NewBroadcastList";
            }
            actionBar.setTitle(LocaleController.getString(str, i));
        } else if (this.isGroup) {
            this.actionBar.setTitle(LocaleController.getString("NeverAllow", R.string.NeverAllow));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupCreateActivity.this.finishFragment();
                } else if (id == 1) {
                    boolean unused = GroupCreateActivity.this.onDonePressed(true);
                }
            }
        });
        this.fragmentView = new ViewGroup(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxSize;
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || height > width) {
                    maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    maxSize = AndroidUtilities.dp(56.0f);
                }
                GroupCreateActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), 1073741824));
                GroupCreateActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), 1073741824));
                if (GroupCreateActivity.this.floatingButton != null) {
                    if (Build.VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    int w = AndroidUtilities.dp(f);
                    GroupCreateActivity.this.floatingButton.measure(View.MeasureSpec.makeMeasureSpec(w, 1073741824), View.MeasureSpec.makeMeasureSpec(w, 1073741824));
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(AndroidUtilities.dp(10.0f), GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth() - AndroidUtilities.dp(10.0f), (GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight()) - AndroidUtilities.dp(10.0f));
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
                if (GroupCreateActivity.this.floatingButton != null) {
                    int l = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((right - left) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredWidth();
                    int t = ((bottom - top) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredHeight();
                    GroupCreateActivity.this.floatingButton.layout(l, t, GroupCreateActivity.this.floatingButton.getMeasuredWidth() + l, GroupCreateActivity.this.floatingButton.getMeasuredHeight() + t);
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ViewGroup frameLayout = (ViewGroup) this.fragmentView;
        AnonymousClass3 r6 = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (GroupCreateActivity.this.ignoreScrollEvent) {
                    boolean unused = GroupCreateActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView = r6;
        r6.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(this.scrollView);
        SpansContainer spansContainer2 = new SpansContainer(context2);
        this.spansContainer = spansContainer2;
        this.scrollView.addView(spansContainer2, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCreateActivity.this.lambda$createView$0$GroupCreateActivity(view);
            }
        });
        AnonymousClass4 r62 = new EditTextBoldCursor(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateSpan unused = GroupCreateActivity.this.currentDeletingSpan = null;
                }
                if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText = r62;
        r62.setTextSize(1, 14.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setImeOptions(268435462);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.editText.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.editText.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.spansContainer.addView(this.editText);
        if (this.chatType == 2) {
            this.editText.setHintText(LocaleController.getString("AddMutual", R.string.AddMutual));
        } else if (this.addToGroup) {
            this.editText.setHintText(LocaleController.getString("SearchForPeople", R.string.SearchForPeople));
        } else if (this.isAlwaysShare || this.isNeverShare) {
            this.editText.setHintText(LocaleController.getString("SearchForPeopleAndGroups", R.string.SearchForPeopleAndGroups));
        } else {
            this.editText.setHintText(LocaleController.getString("SendMessageTo", R.string.SendMessageTo));
        }
        this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return GroupCreateActivity.this.lambda$createView$1$GroupCreateActivity(textView, i, keyEvent);
            }
        });
        this.editText.setOnKeyListener(new View.OnKeyListener() {
            private boolean wasEmpty;

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 67) {
                    boolean z = true;
                    if (event.getAction() == 0) {
                        if (GroupCreateActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (event.getAction() == 1 && this.wasEmpty && !GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
                        GroupCreateActivity.this.updateHint();
                        GroupCreateActivity.this.checkVisibleRows();
                        return true;
                    }
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (GroupCreateActivity.this.editText.length() != 0) {
                    if (!GroupCreateActivity.this.adapter.searching) {
                        boolean unused = GroupCreateActivity.this.searching = true;
                        boolean unused2 = GroupCreateActivity.this.searchWas = true;
                        GroupCreateActivity.this.adapter.setSearching(true);
                        GroupCreateActivity.this.itemDecoration.setSearching(true);
                        GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                        GroupCreateActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                        GroupCreateActivity.this.emptyView.showProgress();
                    }
                    GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
                    return;
                }
                GroupCreateActivity.this.closeSearch();
            }
        });
        this.emptyView = new EmptyTextProgressView(context2);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setTopImage(R.mipmap.img_empty_default);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.setTextSize(14);
        frameLayout.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context2);
        this.adapter = groupCreateAdapter;
        recyclerListView2.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView3 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        recyclerListView3.addItemDecoration(groupCreateDividerItemDecoration);
        this.listView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                GroupCreateActivity.this.lambda$createView$3$GroupCreateActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
                }
            }
        });
        ImageView imageView = new ImageView(context2);
        this.floatingButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
        if (this.isNeverShare || this.isAlwaysShare || this.addToGroup) {
            this.floatingButton.setImageResource(R.drawable.floating_check);
        } else {
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setArrowRotation(180);
            this.floatingButton.setImageDrawable(backDrawable);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            LinearLayoutManager linearLayoutManager2 = linearLayoutManager;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.floatingButton);
        this.floatingButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCreateActivity.this.lambda$createView$4$GroupCreateActivity(view);
            }
        });
        if (this.chatType != 2) {
            this.floatingButton.setVisibility(4);
            this.floatingButton.setScaleX(0.0f);
            this.floatingButton.setScaleY(0.0f);
            this.floatingButton.setAlpha(0.0f);
        }
        this.floatingButton.setContentDescription(LocaleController.getString("Next", R.string.Next));
        updateHint();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$GroupCreateActivity(View v) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    public /* synthetic */ boolean lambda$createView$1$GroupCreateActivity(TextView v, int actionId, KeyEvent event) {
        return actionId == 6 && onDonePressed(true);
    }

    public /* synthetic */ void lambda$createView$3$GroupCreateActivity(View view, int position) {
        int id;
        if (position == 0 && this.adapter.inviteViaLink != 0 && !this.adapter.searching) {
            int id2 = this.chatId;
            if (id2 == 0) {
                id2 = this.channelId;
            }
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(id2));
            if (chat == null || !chat.has_geo || TextUtils.isEmpty(chat.username)) {
                presentFragment(new GroupInviteActivity(id2));
                return;
            }
            ChatEditTypeActivity activity = new ChatEditTypeActivity(id2, true);
            activity.setInfo(this.info);
            presentFragment(activity);
        } else if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell cell = (GroupCreateUserCell) view;
            TLObject object = cell.getObject();
            if (object instanceof TLRPC.User) {
                id = ((TLRPC.User) object).id;
            } else if ((object instanceof TLRPC.Chat) != 0) {
                id = -((TLRPC.Chat) object).id;
            } else {
                return;
            }
            SparseArray<TLObject> sparseArray = this.ignoreUsers;
            if (sparseArray == null || sparseArray.indexOfKey(id) < 0) {
                boolean z = false;
                boolean z2 = this.selectedContacts.indexOfKey(id) >= 0;
                boolean exists = z2;
                if (z2) {
                    this.spansContainer.removeSpan(this.selectedContacts.get(id));
                } else if (this.maxCount != 0 && this.selectedContacts.size() == this.maxCount) {
                    return;
                } else {
                    if (this.chatType == 0 && this.selectedContacts.size() == MessagesController.getInstance(this.currentAccount).maxGroupCount) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("SoftUserLimitAlert", R.string.SoftUserLimitAlert));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
                        return;
                    }
                    if (object instanceof TLRPC.User) {
                        TLRPC.User user = (TLRPC.User) object;
                        if (this.addToGroup && user.bot) {
                            if (this.channelId == 0 && user.bot_nochats) {
                                ToastUtils.show((int) R.string.BotCantJoinGroups);
                                return;
                            } else if (this.channelId != 0) {
                                TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.channelId));
                                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                                if (ChatObject.canAddAdmins(chat2)) {
                                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder2.setMessage(LocaleController.getString("AddBotAsAdmin", R.string.AddBotAsAdmin));
                                    builder2.setPositiveButton(LocaleController.getString("MakeAdmin", R.string.MakeAdmin), new DialogInterface.OnClickListener(user) {
                                        private final /* synthetic */ TLRPC.User f$1;

                                        {
                                            this.f$1 = r2;
                                        }

                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            GroupCreateActivity.this.lambda$null$2$GroupCreateActivity(this.f$1, dialogInterface, i);
                                        }
                                    });
                                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                                } else {
                                    builder2.setMessage(LocaleController.getString("CantAddBotAsAdmin", R.string.CantAddBotAsAdmin));
                                    builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                                }
                                showDialog(builder2.create());
                                return;
                            }
                        }
                        MessagesController.getInstance(this.currentAccount).putUser(user, !this.searching);
                    } else if (object instanceof TLRPC.Chat) {
                        MessagesController.getInstance(this.currentAccount).putChat((TLRPC.Chat) object, !this.searching);
                    }
                    GroupCreateSpan span = new GroupCreateSpan(this.editText.getContext(), object);
                    this.spansContainer.addSpan(span);
                    span.setOnClickListener(this);
                }
                updateHint();
                if (this.searching || this.searchWas) {
                    AndroidUtilities.showKeyboard(this.editText);
                } else {
                    if (!exists) {
                        z = true;
                    }
                    cell.setChecked(z, true);
                }
                if (this.editText.length() > 0) {
                    this.editText.setText((CharSequence) null);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$2$GroupCreateActivity(TLRPC.User user, DialogInterface dialogInterface, int i) {
        this.delegate2.needAddBot(user);
        if (this.editText.length() > 0) {
            this.editText.setText((CharSequence) null);
        }
    }

    public /* synthetic */ void lambda$createView$4$GroupCreateActivity(View v) {
        onDonePressed(true);
    }

    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
            GroupCreateAdapter groupCreateAdapter = this.adapter;
            if (groupCreateAdapter != null) {
                groupCreateAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int mask = args[0].intValue();
                int count = this.listView.getChildCount();
                if ((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) {
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    public void setIgnoreUsers(SparseArray<TLObject> users) {
        this.ignoreUsers = users;
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
    }

    public void setContainerHeight(int value) {
        this.containerHeight = value;
        SpansContainer spansContainer2 = this.spansContainer;
        if (spansContainer2 != null) {
            spansContainer2.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    /* access modifiers changed from: private */
    public void checkVisibleRows() {
        int id;
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) child;
                TLObject object = cell.getObject();
                if (object instanceof TLRPC.User) {
                    id = ((TLRPC.User) object).id;
                } else if ((object instanceof TLRPC.Chat) != 0) {
                    id = -((TLRPC.Chat) object).id;
                } else {
                    id = 0;
                }
                if (id != 0) {
                    SparseArray<TLObject> sparseArray = this.ignoreUsers;
                    boolean z = false;
                    if (sparseArray == null || sparseArray.indexOfKey(id) < 0) {
                        if (this.selectedContacts.indexOfKey(id) >= 0) {
                            z = true;
                        }
                        cell.setChecked(z, true);
                        cell.setCheckBoxEnabled(true);
                    } else {
                        cell.setChecked(true, false);
                        cell.setCheckBoxEnabled(false);
                    }
                }
            }
        }
    }

    private void onAddToGroupDone(int count) {
        ArrayList<TLRPC.User> result = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            result.add(getMessagesController().getUser(Integer.valueOf(this.selectedContacts.keyAt(a))));
        }
        ContactsAddActivityDelegate contactsAddActivityDelegate = this.delegate2;
        if (contactsAddActivityDelegate != null) {
            contactsAddActivityDelegate.didSelectUsers(result, count);
        }
        finishFragment();
    }

    /* access modifiers changed from: private */
    public boolean onDonePressed(boolean alert) {
        if (this.selectedContacts.size() == 0 && this.chatType != 2) {
            return false;
        }
        if (!alert || !this.addToGroup) {
            if (this.chatType == 2) {
                ArrayList<TLRPC.InputUser> result = new ArrayList<>();
                for (int a = 0; a < this.selectedContacts.size(); a++) {
                    TLRPC.InputUser user = MessagesController.getInstance(this.currentAccount).getInputUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedContacts.keyAt(a))));
                    if (user != null) {
                        result.add(user);
                    }
                }
                MessagesController.getInstance(this.currentAccount).addUsersToChannel(this.chatId, result, (BaseFragment) null);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                Bundle args2 = new Bundle();
                args2.putInt("chat_id", this.chatId);
                presentFragment(new ChatActivity(args2), true);
            } else if (!this.doneButtonVisible || this.selectedContacts.size() == 0) {
                return false;
            } else {
                if (this.addToGroup) {
                    onAddToGroupDone(0);
                } else {
                    ArrayList<Integer> result2 = new ArrayList<>();
                    for (int a2 = 0; a2 < this.selectedContacts.size(); a2++) {
                        result2.add(Integer.valueOf(this.selectedContacts.keyAt(a2)));
                    }
                    if (this.isAlwaysShare != 0 || this.isNeverShare) {
                        GroupCreateActivityDelegate groupCreateActivityDelegate = this.delegate;
                        if (groupCreateActivityDelegate != null) {
                            groupCreateActivityDelegate.didSelectUsers(result2);
                        }
                        finishFragment();
                    } else {
                        Bundle args = new Bundle();
                        args.putIntegerArrayList("result", result2);
                        args.putInt("chatType", this.chatType);
                        presentFragment(new GroupCreateFinalActivity(args));
                    }
                }
            }
        } else if (getParentActivity() == null) {
            return false;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            if (this.selectedContacts.size() == 1) {
                builder.setTitle(LocaleController.getString("AddOneMemberAlertTitle", R.string.AddOneMemberAlertTitle));
            } else {
                builder.setTitle(LocaleController.formatString("AddMembersAlertTitle", R.string.AddMembersAlertTitle, LocaleController.formatPluralString("Members", this.selectedContacts.size())));
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int a3 = 0; a3 < this.selectedContacts.size(); a3++) {
                TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(this.selectedContacts.keyAt(a3)));
                if (user2 != null) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append("**");
                    stringBuilder.append(ContactsController.formatName(user2.first_name, user2.last_name));
                    stringBuilder.append("**");
                }
            }
            MessagesController messagesController = getMessagesController();
            int i = this.chatId;
            if (i == 0) {
                i = this.channelId;
            }
            TLRPC.Chat chat = messagesController.getChat(Integer.valueOf(i));
            if (chat instanceof TLRPC.TL_channelForbidden) {
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                AlertDialog dialog = new AlertDialog(getParentActivity(), 0);
                dialog.setTitle(LocaleController.getString("AppName", R.string.AppName));
                dialog.setMessage(LocaleController.getString("DeleteThisGroup", R.string.DeleteThisGroup));
                dialog.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        GroupCreateActivity.this.lambda$onDonePressed$5$GroupCreateActivity(dialogInterface, i);
                    }
                });
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                showDialog(dialog);
                return false;
            }
            if (this.selectedContacts.size() > 5) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, LocaleController.formatPluralString("Members", this.selectedContacts.size()), chat.title)));
                String countString = String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())});
                int index = TextUtils.indexOf(spannableStringBuilder, countString);
                if (index >= 0) {
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), index, countString.length() + index, 33);
                }
                builder.setMessage(spannableStringBuilder);
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, stringBuilder, chat.title)));
            }
            CheckBoxCell[] cells = new CheckBoxCell[1];
            if (!ChatObject.isChannel(chat)) {
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                cells[0] = new CheckBoxCell(getParentActivity(), 1);
                cells[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cells[0].setMultiline(true);
                if (this.selectedContacts.size() == 1) {
                    cells[0].setText(AndroidUtilities.replaceTags(LocaleController.formatString("AddOneMemberForwardMessages", R.string.AddOneMemberForwardMessages, UserObject.getFirstName(getMessagesController().getUser(Integer.valueOf(this.selectedContacts.keyAt(0)))))), "", true, false);
                } else {
                    cells[0].setText(LocaleController.getString("AddMembersForwardMessages", R.string.AddMembersForwardMessages), "", true, false);
                }
                cells[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(cells[0], LayoutHelper.createLinear(-1, -2));
                cells[0].setOnClickListener(new View.OnClickListener(cells) {
                    private final /* synthetic */ CheckBoxCell[] f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        this.f$0[0].setChecked(!this.f$0[0].isChecked(), true);
                    }
                });
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
            }
            builder.setPositiveButton(LocaleController.getString("Add", R.string.Add), new DialogInterface.OnClickListener(cells) {
                private final /* synthetic */ CheckBoxCell[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    GroupCreateActivity.this.lambda$onDonePressed$7$GroupCreateActivity(this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
        return true;
    }

    public /* synthetic */ void lambda$onDonePressed$5$GroupCreateActivity(DialogInterface dialog1, int which) {
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        int i = this.chatId;
        if (i == 0) {
            i = this.channelId;
        }
        instance.deleteUserFromChat(i, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info, true, false);
        finishFragment();
    }

    public /* synthetic */ void lambda$onDonePressed$7$GroupCreateActivity(CheckBoxCell[] cells, DialogInterface dialogInterface, int i) {
        int i2 = 0;
        if (cells[0] != null && cells[0].isChecked()) {
            i2 = 100;
        }
        onAddToGroupDone(i2);
    }

    /* access modifiers changed from: private */
    public void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs((String) null);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
    }

    /* access modifiers changed from: private */
    public void updateHint() {
        if (!this.isAlwaysShare && !this.isNeverShare && !this.addToGroup) {
            if (this.chatType == 2) {
                this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", R.string.MembersCountZero, LocaleController.formatPluralString("Members", this.maxCount)));
            } else {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", R.string.MembersCount, Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)));
            }
        }
        if (this.chatType == 2) {
            return;
        }
        if (this.doneButtonVisible && this.allSpans.isEmpty()) {
            AnimatorSet animatorSet = this.currentDoneButtonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentDoneButtonAnimation = animatorSet2;
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, "scaleX", new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButton, "scaleY", new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButton, "alpha", new float[]{0.0f})});
            this.currentDoneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    GroupCreateActivity.this.floatingButton.setVisibility(4);
                }
            });
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = false;
        } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
            AnimatorSet animatorSet3 = this.currentDoneButtonAnimation;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            this.floatingButton.setVisibility(0);
            this.currentDoneButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButton, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButton, "alpha", new float[]{1.0f})});
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = true;
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }

    public void setDelegate(ContactsAddActivityDelegate contactsAddActivityDelegate) {
        this.delegate2 = contactsAddActivityDelegate;
    }

    public class GroupCreateAdapter extends RecyclerListView.FastScrollAdapter {
        private ArrayList<TLObject> contacts = new ArrayList<>();
        private Context context;
        /* access modifiers changed from: private */
        public int inviteViaLink;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<TLObject> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searching;
        private int usersStartRow;

        public GroupCreateAdapter(Context ctx) {
            this.context = ctx;
            ArrayList<TLRPC.Contact> arrayList = ContactsController.getInstance(GroupCreateActivity.this.currentAccount).contacts;
            for (int a = 0; a < arrayList.size(); a++) {
                TLRPC.User user = MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getUser(Integer.valueOf(arrayList.get(a).user_id));
                if (user != null && !user.self && !user.deleted) {
                    this.contacts.add(user);
                }
            }
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                public /* synthetic */ SparseArray<TLRPC.User> getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public final void onDataSetChanged() {
                    GroupCreateActivity.GroupCreateAdapter.this.lambda$new$0$GroupCreateActivity$GroupCreateAdapter();
                }

                public /* synthetic */ void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$GroupCreateActivity$GroupCreateAdapter() {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                GroupCreateActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void setSearching(boolean value) {
            if (this.searching != value) {
                this.searching = value;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int position) {
            String firstName;
            String lastName;
            if (this.searching || position < this.usersStartRow) {
                return null;
            }
            int size = this.contacts.size();
            int i = this.usersStartRow;
            if (position >= size + i) {
                return null;
            }
            TLObject object = this.contacts.get(position - i);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                firstName = user.first_name;
                lastName = user.last_name;
            } else {
                firstName = ((TLRPC.Chat) object).title;
                lastName = "";
            }
            if (LocaleController.nameDisplayOrder == 1) {
                if (!TextUtils.isEmpty(firstName)) {
                    return firstName.substring(0, 1).toUpperCase();
                }
                if (!TextUtils.isEmpty(lastName)) {
                    return lastName.substring(0, 1).toUpperCase();
                }
                return "";
            } else if (!TextUtils.isEmpty(lastName)) {
                return lastName.substring(0, 1).toUpperCase();
            } else {
                if (!TextUtils.isEmpty(firstName)) {
                    return firstName.substring(0, 1).toUpperCase();
                }
                return "";
            }
        }

        public int getItemCount() {
            if (!this.searching) {
                return this.contacts.size();
            }
            int count = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int count2 = count + localServerCount;
            if (globalCount != 0) {
                return count2 + globalCount + 1;
            }
            return count2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new GroupCreateSectionCell(this.context);
            } else if (viewType != 1) {
                view = new TextCell(this.context);
            } else {
                view = new GroupCreateUserCell(this.context, true, 0);
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x0169  */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x016f  */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x017d  */
        /* JADX WARNING: Removed duplicated region for block: B:92:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
                r18 = this;
                r1 = r18
                r2 = r19
                r3 = r20
                int r0 = r19.getItemViewType()
                if (r0 == 0) goto L_0x01b2
                r4 = 0
                r5 = 1
                if (r0 == r5) goto L_0x003c
                r5 = 2
                if (r0 == r5) goto L_0x0015
                goto L_0x01c6
            L_0x0015:
                android.view.View r0 = r2.itemView
                im.bclpbkiauv.ui.cells.TextCell r0 = (im.bclpbkiauv.ui.cells.TextCell) r0
                int r6 = r1.inviteViaLink
                r7 = 2131231474(0x7f0802f2, float:1.807903E38)
                if (r6 != r5) goto L_0x002e
                r5 = 2131690429(0x7f0f03bd, float:1.9009901E38)
                java.lang.String r6 = "ChannelInviteViaLink"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r0.setTextAndIcon(r5, r7, r4)
                goto L_0x01c6
            L_0x002e:
                r5 = 2131691702(0x7f0f08b6, float:1.9012483E38)
                java.lang.String r6 = "InviteToGroupByLink"
                java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
                r0.setTextAndIcon(r5, r7, r4)
                goto L_0x01c6
            L_0x003c:
                android.view.View r0 = r2.itemView
                r6 = r0
                im.bclpbkiauv.ui.cells.GroupCreateUserCell r6 = (im.bclpbkiauv.ui.cells.GroupCreateUserCell) r6
                r7 = 0
                r8 = 0
                boolean r0 = r1.searching
                if (r0 == 0) goto L_0x0153
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r0 = r1.searchResult
                int r9 = r0.size()
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r10 = r0.size()
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                int r11 = r0.size()
                if (r3 < 0) goto L_0x006f
                if (r3 >= r9) goto L_0x006f
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r0 = r1.searchResult
                java.lang.Object r0 = r0.get(r3)
                im.bclpbkiauv.tgnet.TLObject r0 = (im.bclpbkiauv.tgnet.TLObject) r0
                r12 = r0
                goto L_0x00a2
            L_0x006f:
                if (r3 < r9) goto L_0x0085
                int r0 = r11 + r9
                if (r3 >= r0) goto L_0x0085
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getLocalServerSearch()
                int r12 = r3 - r9
                java.lang.Object r0 = r0.get(r12)
                im.bclpbkiauv.tgnet.TLObject r0 = (im.bclpbkiauv.tgnet.TLObject) r0
                r12 = r0
                goto L_0x00a2
            L_0x0085:
                int r0 = r9 + r11
                if (r3 <= r0) goto L_0x00a0
                int r0 = r10 + r9
                int r0 = r0 + r11
                if (r3 > r0) goto L_0x00a0
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r12 = r3 - r9
                int r12 = r12 - r11
                int r12 = r12 - r5
                java.lang.Object r0 = r0.get(r12)
                im.bclpbkiauv.tgnet.TLObject r0 = (im.bclpbkiauv.tgnet.TLObject) r0
                r12 = r0
                goto L_0x00a2
            L_0x00a0:
                r0 = 0
                r12 = r0
            L_0x00a2:
                if (r12 == 0) goto L_0x014e
                boolean r0 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.User
                if (r0 == 0) goto L_0x00af
                r0 = r12
                im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
                java.lang.String r0 = r0.username
                r13 = r0
                goto L_0x00b5
            L_0x00af:
                r0 = r12
                im.bclpbkiauv.tgnet.TLRPC$Chat r0 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r0
                java.lang.String r0 = r0.username
                r13 = r0
            L_0x00b5:
                java.lang.String r0 = "@"
                if (r3 >= r9) goto L_0x00e9
                java.util.ArrayList<java.lang.CharSequence> r14 = r1.searchResultNames
                java.lang.Object r14 = r14.get(r3)
                r8 = r14
                java.lang.CharSequence r8 = (java.lang.CharSequence) r8
                if (r8 == 0) goto L_0x0152
                boolean r14 = android.text.TextUtils.isEmpty(r13)
                if (r14 != 0) goto L_0x0152
                java.lang.String r14 = r8.toString()
                java.lang.StringBuilder r15 = new java.lang.StringBuilder
                r15.<init>()
                r15.append(r0)
                r15.append(r13)
                java.lang.String r0 = r15.toString()
                boolean r0 = r14.startsWith(r0)
                if (r0 == 0) goto L_0x0152
                r0 = r8
                r7 = 0
                r8 = r7
                r7 = r0
                goto L_0x0152
            L_0x00e9:
                if (r3 <= r9) goto L_0x014b
                boolean r14 = android.text.TextUtils.isEmpty(r13)
                if (r14 != 0) goto L_0x014b
                im.bclpbkiauv.ui.adapters.SearchAdapterHelper r14 = r1.searchAdapterHelper
                java.lang.String r14 = r14.getLastFoundUsername()
                boolean r15 = r14.startsWith(r0)
                if (r15 == 0) goto L_0x0101
                java.lang.String r14 = r14.substring(r5)
            L_0x0101:
                android.text.SpannableStringBuilder r15 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0145 }
                r15.<init>()     // Catch:{ Exception -> 0x0145 }
                r15.append(r0)     // Catch:{ Exception -> 0x0145 }
                r15.append(r13)     // Catch:{ Exception -> 0x0145 }
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r13, r14)     // Catch:{ Exception -> 0x0145 }
                r16 = r0
                r4 = -1
                if (r0 == r4) goto L_0x0140
                int r0 = r14.length()     // Catch:{ Exception -> 0x0145 }
                if (r16 != 0) goto L_0x0120
                int r0 = r0 + 1
                r4 = r16
                goto L_0x0124
            L_0x0120:
                int r16 = r16 + 1
                r4 = r16
            L_0x0124:
                android.text.style.ForegroundColorSpan r5 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0145 }
                java.lang.String r16 = "windowBackgroundWhiteBlueText4"
                r17 = r7
                int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)     // Catch:{ Exception -> 0x013e }
                r5.<init>(r7)     // Catch:{ Exception -> 0x013e }
                int r7 = r4 + r0
                r16 = r0
                r0 = 33
                r15.setSpan(r5, r4, r7, r0)     // Catch:{ Exception -> 0x013e }
                r16 = r4
                goto L_0x0142
            L_0x013e:
                r0 = move-exception
                goto L_0x0148
            L_0x0140:
                r17 = r7
            L_0x0142:
                r0 = r15
                r7 = r0
                goto L_0x0152
            L_0x0145:
                r0 = move-exception
                r17 = r7
            L_0x0148:
                r4 = r13
                r7 = r4
                goto L_0x0152
            L_0x014b:
                r17 = r7
                goto L_0x0150
            L_0x014e:
                r17 = r7
            L_0x0150:
                r7 = r17
            L_0x0152:
                goto L_0x0162
            L_0x0153:
                r17 = r7
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLObject> r0 = r1.contacts
                int r4 = r1.usersStartRow
                int r4 = r3 - r4
                java.lang.Object r0 = r0.get(r4)
                r12 = r0
                im.bclpbkiauv.tgnet.TLObject r12 = (im.bclpbkiauv.tgnet.TLObject) r12
            L_0x0162:
                r6.setObject(r12, r8, r7)
                boolean r0 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.User
                if (r0 == 0) goto L_0x016f
                r0 = r12
                im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
                int r0 = r0.id
                goto L_0x017b
            L_0x016f:
                boolean r0 = r12 instanceof im.bclpbkiauv.tgnet.TLRPC.Chat
                if (r0 == 0) goto L_0x017a
                r0 = r12
                im.bclpbkiauv.tgnet.TLRPC$Chat r0 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r0
                int r0 = r0.id
                int r0 = -r0
                goto L_0x017b
            L_0x017a:
                r0 = 0
            L_0x017b:
                if (r0 == 0) goto L_0x01c6
                im.bclpbkiauv.ui.GroupCreateActivity r4 = im.bclpbkiauv.ui.GroupCreateActivity.this
                android.util.SparseArray r4 = r4.ignoreUsers
                if (r4 == 0) goto L_0x019a
                im.bclpbkiauv.ui.GroupCreateActivity r4 = im.bclpbkiauv.ui.GroupCreateActivity.this
                android.util.SparseArray r4 = r4.ignoreUsers
                int r4 = r4.indexOfKey(r0)
                if (r4 < 0) goto L_0x019a
                r4 = 0
                r5 = 1
                r6.setChecked(r5, r4)
                r6.setCheckBoxEnabled(r4)
                goto L_0x01c6
            L_0x019a:
                im.bclpbkiauv.ui.GroupCreateActivity r4 = im.bclpbkiauv.ui.GroupCreateActivity.this
                android.util.SparseArray r4 = r4.selectedContacts
                int r4 = r4.indexOfKey(r0)
                if (r4 < 0) goto L_0x01a8
                r4 = 1
                goto L_0x01a9
            L_0x01a8:
                r4 = 0
            L_0x01a9:
                r5 = 0
                r6.setChecked(r4, r5)
                r4 = 1
                r6.setCheckBoxEnabled(r4)
                goto L_0x01c6
            L_0x01b2:
                android.view.View r0 = r2.itemView
                im.bclpbkiauv.ui.cells.GroupCreateSectionCell r0 = (im.bclpbkiauv.ui.cells.GroupCreateSectionCell) r0
                boolean r4 = r1.searching
                if (r4 == 0) goto L_0x01c6
                r4 = 2131691482(0x7f0f07da, float:1.9012037E38)
                java.lang.String r5 = "GlobalSearch"
                java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
                r0.setText(r4)
            L_0x01c6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.GroupCreateActivity.GroupCreateAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int position) {
            if (this.searching) {
                if (position == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size()) {
                    return 0;
                }
                return 1;
            } else if (this.inviteViaLink == 0 || position != 0) {
                return 1;
            } else {
                return 2;
            }
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (GroupCreateActivity.this.ignoreUsers != null && (holder.itemView instanceof GroupCreateUserCell)) {
                TLObject object = ((GroupCreateUserCell) holder.itemView).getObject();
                if (!(object instanceof TLRPC.User) || GroupCreateActivity.this.ignoreUsers.indexOfKey(((TLRPC.User) object).id) < 0) {
                    return true;
                }
                return false;
            }
            return true;
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare, false, false, 0, false, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$GroupCreateActivity$GroupCreateAdapter$Vbu2rdhccLoMChSk5ynOA0rPwNs r1 = new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GroupCreateActivity.GroupCreateAdapter.this.lambda$searchDialogs$3$GroupCreateActivity$GroupCreateAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        public /* synthetic */ void lambda$searchDialogs$3$GroupCreateActivity$GroupCreateAdapter(String query) {
            AndroidUtilities.runOnUIThread(new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GroupCreateActivity.GroupCreateAdapter.this.lambda$null$2$GroupCreateActivity$GroupCreateAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$2$GroupCreateActivity$GroupCreateAdapter(String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare, true, false, 0, false, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$GroupCreateActivity$GroupCreateAdapter$jJuWTpV26aLtbIUpj7rr5XRLcA r1 = new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GroupCreateActivity.GroupCreateAdapter.this.lambda$null$1$GroupCreateActivity$GroupCreateAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1);
        }

        public /* synthetic */ void lambda$null$1$GroupCreateActivity$GroupCreateAdapter(String query) {
            String name;
            String username;
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList(), new ArrayList());
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            int i = 1;
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<TLObject> resultArray = new ArrayList<>();
            ArrayList<CharSequence> resultArrayNames = new ArrayList<>();
            int a = 0;
            while (a < this.contacts.size()) {
                TLObject object = this.contacts.get(a);
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                    username = user.username;
                } else {
                    TLRPC.Chat chat = (TLRPC.Chat) object;
                    name = chat.title;
                    username = chat.username;
                }
                String tName = LocaleController.getInstance().getTranslitString(name);
                if (name.equals(tName)) {
                    tName = null;
                }
                int found = 0;
                int length = search.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    String q = search[i2];
                    if (name.contains(q) || (tName != null && tName.contains(q))) {
                        found = 1;
                    } else if (username != null && username.contains(q)) {
                        found = 2;
                    }
                    if (found != 0) {
                        if (found != i) {
                            resultArrayNames.add(AndroidUtilities.generateSearchName("@" + username, (String) null, "@" + q));
                        } else if (object instanceof TLRPC.User) {
                            TLRPC.User user2 = (TLRPC.User) object;
                            resultArrayNames.add(AndroidUtilities.generateSearchName(user2.first_name, user2.last_name, q));
                        } else {
                            resultArrayNames.add(AndroidUtilities.generateSearchName(((TLRPC.Chat) object).title, (String) null, q));
                        }
                        resultArray.add(object);
                    } else {
                        i2++;
                        i = 1;
                    }
                }
                a++;
                i = 1;
            }
            updateSearchResults(resultArray, resultArrayNames);
        }

        private void updateSearchResults(ArrayList<TLObject> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable(users, names) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    GroupCreateActivity.GroupCreateAdapter.this.lambda$updateSearchResults$4$GroupCreateActivity$GroupCreateAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$4$GroupCreateActivity$GroupCreateAdapter(ArrayList users, ArrayList names) {
            this.searchRunnable = null;
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            if (this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                GroupCreateActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                GroupCreateActivity.this.lambda$getThemeDescriptions$8$GroupCreateActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollActive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollInactive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollText), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_hintText), new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_cursor), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription((View) this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_groupcreate_sectionShadow), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_groupcreate_sectionText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_groupcreate_sectionText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_checkbox), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_checkboxDisabled), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_checkboxCheck), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink), new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundGroupCreateSpanBlue), new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_spanBackground), new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_spanText), new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_spanDelete), new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundBlue)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$8$GroupCreateActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child).update(0);
                }
            }
        }
    }
}

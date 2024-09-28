package im.bclpbkiauv.ui.hui.chats;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerMiddle;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DialogObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.XiaomiUtilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChannelCreateActivity;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.NewContactActivity;
import im.bclpbkiauv.ui.ProxyListActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BackDrawable;
import im.bclpbkiauv.ui.actionbar.BaseSearchViewFragment;
import im.bclpbkiauv.ui.actionbar.MenuDrawable;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.adapters.DialogsSearchAdapter;
import im.bclpbkiauv.ui.cells.AccountSelectCell;
import im.bclpbkiauv.ui.cells.ArchiveHintInnerCell;
import im.bclpbkiauv.ui.cells.DialogCell;
import im.bclpbkiauv.ui.cells.DialogsEmptyCell;
import im.bclpbkiauv.ui.cells.DividerCell;
import im.bclpbkiauv.ui.cells.DrawerActionCell;
import im.bclpbkiauv.ui.cells.DrawerAddCell;
import im.bclpbkiauv.ui.cells.DrawerProfileCell;
import im.bclpbkiauv.ui.cells.DrawerUserCell;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.HashtagSearchCell;
import im.bclpbkiauv.ui.cells.HintDialogCell;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.ProfileSearchCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AnimatedArrowDrawable;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ChatActivityEnterView;
import im.bclpbkiauv.ui.components.CubicBezierInterpolator;
import im.bclpbkiauv.ui.components.DialogsItemAnimator;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.FragmentContextView;
import im.bclpbkiauv.ui.components.JoinGroupAlert;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.NumberTextView;
import im.bclpbkiauv.ui.components.PacmanAnimation;
import im.bclpbkiauv.ui.components.ProxyDrawable;
import im.bclpbkiauv.ui.components.RLottieDrawable;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import im.bclpbkiauv.ui.components.StickersAlert;
import im.bclpbkiauv.ui.components.UndoView;
import im.bclpbkiauv.ui.constants.ChatEnterMenuType;
import im.bclpbkiauv.ui.hui.adapter.MyDialogsAdapter;
import im.bclpbkiauv.ui.hui.chats.MryDialogsActivity;
import im.bclpbkiauv.ui.hui.decoration.TopBottomDecoration;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import java.util.ArrayList;

public class MryDialogsActivity extends BaseSearchViewFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int archive = 105;
    private static final int clear = 103;
    private static final int create_new_channel = 106;
    private static final int create_new_group = 107;
    private static final int delete = 102;
    public static boolean[] dialogsLoaded = new boolean[3];
    /* access modifiers changed from: private */
    public static ArrayList<TLRPC.Dialog> frozenDialogsList = null;
    private static final int mute = 104;
    private static final int pin = 100;
    private static final int read = 101;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private String addToGroupAlertString;
    /* access modifiers changed from: private */
    public float additionalFloatingTranslation;
    /* access modifiers changed from: private */
    public boolean allowMoving;
    /* access modifiers changed from: private */
    public boolean allowScrollToHiddenView;
    /* access modifiers changed from: private */
    public boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    private ActionBarMenuSubItem archiveItem;
    private AnimatedArrowDrawable arrowDrawable;
    private boolean askAboutContacts = true;
    private BackDrawable backDrawable;
    private int canClearCacheCount;
    private int canMuteCount;
    private int canPinCount;
    private int canReadCount;
    private int canUnmuteCount;
    private boolean cantSendToChannels;
    private boolean checkCanWrite;
    private boolean checkPermission = true;
    private ActionBarMenuSubItem clearItem;
    private boolean closeSearchFieldOnHide;
    /* access modifiers changed from: private */
    public ChatActivityEnterView commentView;
    private FrameLayout containerLayout;
    private int currentConnectionState;
    /* access modifiers changed from: private */
    public DialogsActivityDelegate delegate;
    private ActionBarMenuItem deleteItem;
    /* access modifiers changed from: private */
    public int dialogChangeFinished;
    /* access modifiers changed from: private */
    public int dialogInsertFinished;
    /* access modifiers changed from: private */
    public int dialogRemoveFinished;
    /* access modifiers changed from: private */
    public MyDialogsAdapter dialogsAdapter;
    /* access modifiers changed from: private */
    public DialogsItemAnimator dialogsItemAnimator;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public DialogsSearchAdapter dialogsSearchAdapter;
    /* access modifiers changed from: private */
    public int dialogsType;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    /* access modifiers changed from: private */
    public int folderId;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchhelper;
    /* access modifiers changed from: private */
    public int lastItemsCount;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private MenuDrawable menuDrawable;
    private int messagesCount;
    /* access modifiers changed from: private */
    public DialogCell movingView;
    /* access modifiers changed from: private */
    public boolean movingWas;
    private ActionBarMenuItem muteItem;
    /* access modifiers changed from: private */
    public boolean onlySelect;
    /* access modifiers changed from: private */
    public long openedDialogId;
    /* access modifiers changed from: private */
    public PacmanAnimation pacmanAnimation;
    private ActionBarMenuItem passcodeItem;
    private AlertDialog permissionDialog;
    private ActionBarMenuItem pinItem;
    private int prevPosition;
    private int prevTop;
    /* access modifiers changed from: private */
    public RadialProgressView progressView;
    private ProxyDrawable proxyDrawable;
    private ActionBarMenuItem proxyItem;
    private boolean proxyItemVisisble;
    private ActionBarMenuSubItem readItem;
    private boolean resetDelegate = true;
    private boolean scrollUpdated;
    /* access modifiers changed from: private */
    public boolean scrollingManually;
    private long searchDialogId;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    private TLObject searchObject;
    /* access modifiers changed from: private */
    public String searchString;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public String selectAlertString;
    private String selectAlertStringGroup;
    private NumberTextView selectedDialogsCountTextView;
    private RecyclerView sideMenu;
    /* access modifiers changed from: private */
    public DialogCell slidingView;
    /* access modifiers changed from: private */
    public boolean startedScrollAtTop;
    /* access modifiers changed from: private */
    public SwipeController swipeController;
    private ActionBarMenuItem switchItem;
    /* access modifiers changed from: private */
    public int totalConsumedAmount;
    /* access modifiers changed from: private */
    public UndoView[] undoView = new UndoView[2];
    /* access modifiers changed from: private */
    public boolean waitingForScrollFinished;

    public interface DialogsActivityDelegate {
        void didSelectDialogs(MryDialogsActivity mryDialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    static /* synthetic */ int access$3508(MryDialogsActivity x0) {
        int i = x0.lastItemsCount;
        x0.lastItemsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$3510(MryDialogsActivity x0) {
        int i = x0.lastItemsCount;
        x0.lastItemsCount = i - 1;
        return i;
    }

    private class ContentView extends SizeNotifierFrameLayout {
        private int inputFieldHeight;

        public ContentView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            int heightSize2 = heightSize - getPaddingTop();
            measureChildWithMargins(MryDialogsActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int keyboardSize = getKeyboardHeight();
            int childCount = getChildCount();
            if (MryDialogsActivity.this.commentView != null) {
                measureChildWithMargins(MryDialogsActivity.this.commentView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                Object tag = MryDialogsActivity.this.commentView.getTag();
                if (tag == null || !tag.equals(2)) {
                    this.inputFieldHeight = 0;
                } else {
                    if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                        heightSize2 -= MryDialogsActivity.this.commentView.getEmojiPadding();
                    }
                    this.inputFieldHeight = MryDialogsActivity.this.commentView.getMeasuredHeight();
                }
            }
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (!(child == null || child.getVisibility() == 8 || child == MryDialogsActivity.this.commentView || child == MryDialogsActivity.this.actionBar)) {
                    if (child == MryDialogsActivity.this.listView || child == MryDialogsActivity.this.progressView || child == MryDialogsActivity.this.searchEmptyView) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (heightSize2 - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)), 1073741824));
                    } else if (MryDialogsActivity.this.commentView == null || !MryDialogsActivity.this.commentView.isPopupView(child)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    } else if (!AndroidUtilities.isInMultiwindow) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, 1073741824));
                    } else if (AndroidUtilities.isTablet()) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), ((heightSize2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), 1073741824));
                    } else {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(((heightSize2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), 1073741824));
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int paddingBottom;
            int childLeft;
            int childTop;
            int count = getChildCount();
            Object tag = MryDialogsActivity.this.commentView != null ? MryDialogsActivity.this.commentView.getTag() : null;
            int i = 2;
            if (tag == null || !tag.equals(2)) {
                paddingBottom = 0;
            } else {
                paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : MryDialogsActivity.this.commentView.getEmojiPadding();
            }
            setBottomClip(paddingBottom);
            int i2 = 0;
            while (i2 < count) {
                View child = getChildAt(i2);
                if (child.getVisibility() != 8) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    int i3 = gravity & 7 & 7;
                    if (i3 == 1) {
                        childLeft = ((((r - l) - width) / i) + lp.leftMargin) - lp.rightMargin;
                    } else if (i3 != 5) {
                        childLeft = lp.leftMargin;
                    } else {
                        childLeft = (r - width) - lp.rightMargin;
                    }
                    if (verticalGravity == 16) {
                        childTop = (((((b - paddingBottom) - t) - height) / i) + lp.topMargin) - lp.bottomMargin;
                    } else if (verticalGravity == 48) {
                        childTop = lp.topMargin + getPaddingTop();
                    } else if (verticalGravity != 80) {
                        childTop = lp.topMargin;
                    } else {
                        childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                    }
                    if (MryDialogsActivity.this.commentView != null && MryDialogsActivity.this.commentView.isPopupView(child)) {
                        if (AndroidUtilities.isInMultiwindow) {
                            childTop = (MryDialogsActivity.this.commentView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                        } else {
                            childTop = MryDialogsActivity.this.commentView.getBottom();
                        }
                    }
                    child.layout(childLeft, childTop, width - childLeft, childTop + height);
                }
                i2++;
                i = 2;
            }
            notifyHeightChanged();
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            int action = ev.getActionMasked();
            boolean z = true;
            if (action == 0 || action == 1 || action == 3) {
                if (action == 0) {
                    int currentPosition = MryDialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                    MryDialogsActivity mryDialogsActivity = MryDialogsActivity.this;
                    if (currentPosition > 1) {
                        z = false;
                    }
                    boolean unused = mryDialogsActivity.startedScrollAtTop = z;
                } else if (MryDialogsActivity.this.actionBar.isActionModeShowed()) {
                    boolean unused2 = MryDialogsActivity.this.allowMoving = true;
                }
                int unused3 = MryDialogsActivity.this.totalConsumedAmount = 0;
                boolean unused4 = MryDialogsActivity.this.allowScrollToHiddenView = false;
            }
            return super.onInterceptTouchEvent(ev);
        }
    }

    class SwipeController extends ItemTouchHelper.Callback {
        private RectF buttonInstance;
        private RecyclerView.ViewHolder currentItemViewHolder;
        /* access modifiers changed from: private */
        public boolean swipeFolderBack;
        /* access modifiers changed from: private */
        public boolean swipingFolder;

        SwipeController() {
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (MryDialogsActivity.this.waitingForDialogsAnimationEnd() || (MryDialogsActivity.this.parentLayout != null && MryDialogsActivity.this.parentLayout.isInPreviewMode())) {
                return 0;
            }
            if (this.swipingFolder && this.swipeFolderBack) {
                this.swipingFolder = false;
                return 0;
            } else if (MryDialogsActivity.this.onlySelect || MryDialogsActivity.this.dialogsType != 0 || MryDialogsActivity.this.slidingView != null || recyclerView.getAdapter() != MryDialogsActivity.this.dialogsAdapter || !(viewHolder.itemView instanceof DialogCell)) {
                return 0;
            } else {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                long dialogId = dialogCell.getDialogId();
                if (MryDialogsActivity.this.actionBar.isActionModeShowed()) {
                    TLRPC.Dialog dialog = MryDialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                    if (!MryDialogsActivity.this.allowMoving || dialog == null || !dialog.pinned || DialogObject.isFolderDialogId(dialogId)) {
                        return 0;
                    }
                    DialogCell unused = MryDialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                    MryDialogsActivity.this.movingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    return makeMovementFlags(3, 0);
                } else if (!MryDialogsActivity.this.allowSwipeDuringCurrentTouch || dialogId == ((long) MryDialogsActivity.this.getUserConfig().clientUserId) || dialogId == 777000 || MryDialogsActivity.this.getMessagesController().isProxyDialog(dialogId, false)) {
                    return 0;
                } else {
                    this.swipeFolderBack = false;
                    this.swipingFolder = SharedConfig.archiveHidden && DialogObject.isFolderDialogId(dialogCell.getDialogId());
                    dialogCell.setSliding(true);
                    return makeMovementFlags(0, 4);
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
            r2 = ((im.bclpbkiauv.ui.cells.DialogCell) r11.itemView).getDialogId();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onMove(androidx.recyclerview.widget.RecyclerView r9, androidx.recyclerview.widget.RecyclerView.ViewHolder r10, androidx.recyclerview.widget.RecyclerView.ViewHolder r11) {
            /*
                r8 = this;
                android.view.View r0 = r11.itemView
                boolean r0 = r0 instanceof im.bclpbkiauv.ui.cells.DialogCell
                r1 = 0
                if (r0 != 0) goto L_0x0008
                return r1
            L_0x0008:
                android.view.View r0 = r11.itemView
                im.bclpbkiauv.ui.cells.DialogCell r0 = (im.bclpbkiauv.ui.cells.DialogCell) r0
                long r2 = r0.getDialogId()
                im.bclpbkiauv.ui.hui.chats.MryDialogsActivity r4 = im.bclpbkiauv.ui.hui.chats.MryDialogsActivity.this
                im.bclpbkiauv.messenger.MessagesController r4 = r4.getMessagesController()
                android.util.LongSparseArray<im.bclpbkiauv.tgnet.TLRPC$Dialog> r4 = r4.dialogs_dict
                java.lang.Object r4 = r4.get(r2)
                im.bclpbkiauv.tgnet.TLRPC$Dialog r4 = (im.bclpbkiauv.tgnet.TLRPC.Dialog) r4
                if (r4 == 0) goto L_0x0048
                boolean r5 = r4.pinned
                if (r5 == 0) goto L_0x0048
                boolean r5 = im.bclpbkiauv.messenger.DialogObject.isFolderDialogId(r2)
                if (r5 == 0) goto L_0x002b
                goto L_0x0048
            L_0x002b:
                int r1 = r10.getAdapterPosition()
                int r5 = r11.getAdapterPosition()
                im.bclpbkiauv.ui.hui.chats.MryDialogsActivity r6 = im.bclpbkiauv.ui.hui.chats.MryDialogsActivity.this
                im.bclpbkiauv.ui.hui.adapter.MyDialogsAdapter r6 = r6.dialogsAdapter
                r6.notifyItemMoved(r1, r5)
                im.bclpbkiauv.ui.hui.chats.MryDialogsActivity r6 = im.bclpbkiauv.ui.hui.chats.MryDialogsActivity.this
                r6.updateDialogIndices()
                im.bclpbkiauv.ui.hui.chats.MryDialogsActivity r6 = im.bclpbkiauv.ui.hui.chats.MryDialogsActivity.this
                r7 = 1
                boolean unused = r6.movingWas = r7
                return r7
            L_0x0048:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.chats.MryDialogsActivity.SwipeController.onMove(androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$ViewHolder, androidx.recyclerview.widget.RecyclerView$ViewHolder):boolean");
        }

        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if (this.swipeFolderBack) {
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder != null) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                if (DialogObject.isFolderDialogId(dialogCell.getDialogId())) {
                    SharedConfig.toggleArchiveHidden();
                    if (SharedConfig.archiveHidden) {
                        boolean unused = MryDialogsActivity.this.waitingForScrollFinished = true;
                        MryDialogsActivity.this.listView.smoothScrollBy(0, dialogCell.getMeasuredHeight() + dialogCell.getTop(), CubicBezierInterpolator.EASE_OUT);
                        MryDialogsActivity.this.getUndoView().showWithAction(0, 6, (Runnable) null, (Runnable) null);
                        return;
                    }
                    return;
                }
                DialogCell unused2 = MryDialogsActivity.this.slidingView = dialogCell;
                int position = viewHolder.getAdapterPosition();
                Runnable finishRunnable = new Runnable(MryDialogsActivity.this.dialogsAdapter.fixPosition(position), MryDialogsActivity.this.dialogsAdapter.getItemCount(), position) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ int f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        MryDialogsActivity.SwipeController.this.lambda$onSwiped$1$MryDialogsActivity$SwipeController(this.f$1, this.f$2, this.f$3);
                    }
                };
                MryDialogsActivity.this.setDialogsListFrozen(true);
                if (Utilities.random.nextInt(1000) == 1) {
                    if (MryDialogsActivity.this.pacmanAnimation == null) {
                        MryDialogsActivity mryDialogsActivity = MryDialogsActivity.this;
                        PacmanAnimation unused3 = mryDialogsActivity.pacmanAnimation = new PacmanAnimation(mryDialogsActivity.listView);
                    }
                    MryDialogsActivity.this.pacmanAnimation.setFinishRunnable(finishRunnable);
                    MryDialogsActivity.this.pacmanAnimation.start();
                    return;
                }
                finishRunnable.run();
                return;
            }
            DialogCell unused4 = MryDialogsActivity.this.slidingView = null;
        }

        public /* synthetic */ void lambda$onSwiped$1$MryDialogsActivity$SwipeController(int dialogIndex, int count, int position) {
            RecyclerView.ViewHolder holder;
            int i = position;
            TLRPC.Dialog dialog = (TLRPC.Dialog) MryDialogsActivity.frozenDialogsList.remove(dialogIndex);
            int pinnedNum = dialog.pinnedNum;
            DialogCell unused = MryDialogsActivity.this.slidingView = null;
            MryDialogsActivity.this.listView.invalidate();
            boolean z = false;
            int added = MryDialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, MryDialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
            if (added == 2) {
                MryDialogsActivity.this.dialogsAdapter.notifyItemChanged(count - 1);
            }
            if (!(added == 2 && i == 0)) {
                MryDialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                MryDialogsActivity.access$3510(MryDialogsActivity.this);
                MryDialogsActivity.this.dialogsAdapter.notifyItemRemoved(i);
                int unused2 = MryDialogsActivity.this.dialogRemoveFinished = 2;
            }
            if (MryDialogsActivity.this.folderId == 0) {
                if (added == 2) {
                    MryDialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    if (i == 0) {
                        int unused3 = MryDialogsActivity.this.dialogChangeFinished = 2;
                        MryDialogsActivity.this.setDialogsListFrozen(true);
                        MryDialogsActivity.this.dialogsAdapter.notifyItemChanged(0);
                    } else {
                        MryDialogsActivity.access$3508(MryDialogsActivity.this);
                        MryDialogsActivity.this.dialogsAdapter.notifyItemInserted(0);
                        if (!SharedConfig.archiveHidden && MryDialogsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            MryDialogsActivity.this.listView.smoothScrollBy(0, -AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f));
                        }
                    }
                    MryDialogsActivity.frozenDialogsList.add(0, MryDialogsActivity.getDialogsArray(MryDialogsActivity.this.currentAccount, MryDialogsActivity.this.dialogsType, MryDialogsActivity.this.folderId, false).get(0));
                } else if (added == 1 && (holder = MryDialogsActivity.this.listView.findViewHolderForAdapterPosition(0)) != null && (holder.itemView instanceof DialogCell)) {
                    DialogCell cell = (DialogCell) holder.itemView;
                    cell.checkCurrentDialogIndex(true);
                    cell.animateArchiveAvatar();
                }
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (preferences.getBoolean("archivehint_l", false) || SharedConfig.archiveHidden) {
                    z = true;
                }
                boolean hintShowed = z;
                if (!hintShowed) {
                    preferences.edit().putBoolean("archivehint_l", true).commit();
                }
                MryDialogsActivity.this.getUndoView().showWithAction(dialog.id, hintShowed ? 2 : 3, (Runnable) null, new Runnable(dialog, pinnedNum) {
                    private final /* synthetic */ TLRPC.Dialog f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MryDialogsActivity.SwipeController.this.lambda$null$0$MryDialogsActivity$SwipeController(this.f$1, this.f$2);
                    }
                });
            }
            if (MryDialogsActivity.this.folderId != 0 && MryDialogsActivity.frozenDialogsList.isEmpty()) {
                MryDialogsActivity.this.listView.setEmptyView((View) null);
                MryDialogsActivity.this.progressView.setVisibility(4);
            }
        }

        public /* synthetic */ void lambda$null$0$MryDialogsActivity$SwipeController(TLRPC.Dialog dialog, int pinnedNum) {
            boolean unused = MryDialogsActivity.this.dialogsListFrozen = true;
            MryDialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, 0, pinnedNum, 0);
            boolean unused2 = MryDialogsActivity.this.dialogsListFrozen = false;
            ArrayList<TLRPC.Dialog> dialogs = MryDialogsActivity.this.getMessagesController().getDialogs(0);
            int index = dialogs.indexOf(dialog);
            if (index >= 0) {
                ArrayList<TLRPC.Dialog> archivedDialogs = MryDialogsActivity.this.getMessagesController().getDialogs(1);
                if (!archivedDialogs.isEmpty() || index != 1) {
                    int unused3 = MryDialogsActivity.this.dialogInsertFinished = 2;
                    MryDialogsActivity.this.setDialogsListFrozen(true);
                    MryDialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    MryDialogsActivity.access$3508(MryDialogsActivity.this);
                    MryDialogsActivity.this.dialogsAdapter.notifyItemInserted(index);
                }
                if (archivedDialogs.isEmpty()) {
                    dialogs.remove(0);
                    if (index == 1) {
                        int unused4 = MryDialogsActivity.this.dialogChangeFinished = 2;
                        MryDialogsActivity.this.setDialogsListFrozen(true);
                        MryDialogsActivity.this.dialogsAdapter.notifyItemChanged(0);
                        return;
                    }
                    MryDialogsActivity.frozenDialogsList.remove(0);
                    MryDialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    MryDialogsActivity.access$3510(MryDialogsActivity.this);
                    MryDialogsActivity.this.dialogsAdapter.notifyItemRemoved(0);
                    return;
                }
                return;
            }
            MryDialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                MryDialogsActivity.this.listView.hideSelector();
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            if (animationType == 4) {
                return 200;
            }
            if (animationType == 8 && MryDialogsActivity.this.movingView != null) {
                AndroidUtilities.runOnUIThread(new Runnable(MryDialogsActivity.this.movingView) {
                    private final /* synthetic */ View f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.setBackgroundDrawable((Drawable) null);
                    }
                }, MryDialogsActivity.this.dialogsItemAnimator.getMoveDuration());
                DialogCell unused = MryDialogsActivity.this.movingView = null;
            }
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 0.3f;
        }

        public float getSwipeEscapeVelocity(float defaultValue) {
            return 3500.0f;
        }

        public float getSwipeVelocityThreshold(float defaultValue) {
            return Float.MAX_VALUE;
        }
    }

    public MryDialogsActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            this.onlySelect = this.arguments.getBoolean("onlySelect", false);
            this.cantSendToChannels = this.arguments.getBoolean("cantSendToChannels", false);
            this.dialogsType = this.arguments.getInt("dialogsType", 0);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
            this.allowSwitchAccount = this.arguments.getBoolean("allowSwitchAccount");
            this.checkCanWrite = this.arguments.getBoolean("checkCanWrite", true);
            this.folderId = this.arguments.getInt("folderId", 0);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", true);
            this.messagesCount = this.arguments.getInt("messagesCount", 0);
        }
        if (this.dialogsType == 0) {
            this.askAboutContacts = MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts", true);
            SharedConfig.loadProxyList();
        }
        if (this.searchString == null) {
            this.currentConnectionState = getConnectionsManager().getConnectionState();
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
            }
            getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().addObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().addObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().addObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().addObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().addObserver(this, NotificationCenter.folderBecomeEmpty);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        }
        if (!dialogsLoaded[this.currentAccount]) {
            getMessagesController().loadGlobalNotificationsSettings();
            getMessagesController().loadDialogs(this.folderId, 0, 100, true);
            getMessagesController().loadHintDialogs();
            getContactsController().checkInviteText();
            getMediaDataController().loadRecents(2, false, true, false);
            getMediaDataController().checkFeaturedStickers();
            dialogsLoaded[this.currentAccount] = true;
        }
        getMessagesController().loadPinnedDialogs(this.folderId, 0, (ArrayList<Long>) null);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.searchString == null) {
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
            }
            getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().removeObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().removeObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().removeObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().removeObserver(this, NotificationCenter.folderBecomeEmpty);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        this.delegate = null;
    }

    /* access modifiers changed from: protected */
    public MrySearchView getSearchView() {
        FrameLayout searchLayout = new FrameLayout(getParentActivity());
        searchLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.containerLayout.addView(searchLayout, LayoutHelper.createFrame(-1, 55.0f));
        this.searchView = new MrySearchView(getParentActivity());
        this.searchView.setHintText(LocaleController.getString("SearchMessageOrUser", R.string.SearchMessageOrUser));
        searchLayout.addView(this.searchView, LayoutHelper.createFrame(-1, 35, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        return this.searchView;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    public View createView(Context context) {
        String str;
        int i;
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.pacmanAnimation = null;
        AndroidUtilities.runOnUIThread(new Runnable(context2) {
            private final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                Theme.createChatResources(this.f$0, false);
            }
        });
        FrameLayout frameLayout = new FrameLayout(context2);
        this.containerLayout = frameLayout;
        this.fragmentView = frameLayout;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!this.onlySelect && this.searchString == null && this.folderId == 0) {
            ProxyDrawable proxyDrawable2 = new ProxyDrawable(context2);
            this.proxyDrawable = proxyDrawable2;
            ActionBarMenuItem addItem = menu.addItem(2, (Drawable) proxyDrawable2);
            this.proxyItem = addItem;
            addItem.setContentDescription(LocaleController.getString("ProxySettings", R.string.ProxySettings));
            this.passcodeItem = menu.addItem(1, (int) R.drawable.lock_close);
            updatePasscodeButton();
            updateProxyButton(false);
        }
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitleActionRunnable(new Runnable() {
            public final void run() {
                MryDialogsActivity.this.lambda$createView$1$MryDialogsActivity();
            }
        });
        if (this.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            this.switchItem = menu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView imageView = new BackupImageView(context2);
            imageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            this.switchItem.addView(imageView, LayoutHelper.createFrame(36, 36, 17));
            TLRPC.User user = getUserConfig().getCurrentUser();
            avatarDrawable.setInfo(user);
            imageView.getImageReceiver().setCurrentAccount(this.currentAccount);
            imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
            int a = 0;
            for (int i2 = 3; a < i2; i2 = 3) {
                if (AccountInstance.getInstance(a).getUserConfig().getCurrentUser() != null) {
                    AccountSelectCell cell = new AccountSelectCell(context2);
                    cell.setAccount(a, true);
                    this.switchItem.addSubItem(a + 10, cell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
                a++;
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        ActionBar actionBar = this.actionBar;
        if (this.dialogsType == 5) {
            i = R.string.MyChannels;
            str = "MyChannels";
        } else {
            i = R.string.MyGroups;
            str = "MyGroups";
        }
        actionBar.setTitle(LocaleController.getString(str, i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (MryDialogsActivity.this.actionBar.isActionModeShowed()) {
                        MryDialogsActivity.this.hideActionMode(true);
                    } else {
                        MryDialogsActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    SharedConfig.appLocked = true ^ SharedConfig.appLocked;
                    SharedConfig.saveConfig();
                    MryDialogsActivity.this.updatePasscodeButton();
                } else if (id == 2) {
                    MryDialogsActivity.this.presentFragment(new ProxyListActivity());
                } else if (id < 10 || id >= 13) {
                    if (id == 100 || id == 101 || id == 102 || id == 103 || id == 104 || id == 105) {
                        MryDialogsActivity.this.perfromSelectedDialogsAction(id, true);
                    } else if (id == 106) {
                        Bundle args = new Bundle();
                        args.putInt("step", 0);
                        MryDialogsActivity.this.presentFragment(new ChannelCreateActivity(args), true);
                    } else if (id == 107) {
                        MryDialogsActivity.this.presentFragment(new CreateGroupActivity(new Bundle()));
                    }
                } else if (MryDialogsActivity.this.getParentActivity() != null) {
                    DialogsActivityDelegate oldDelegate = MryDialogsActivity.this.delegate;
                    LaunchActivity launchActivity = (LaunchActivity) MryDialogsActivity.this.getParentActivity();
                    launchActivity.switchToAccount(id - 10, true);
                    MryDialogsActivity dialogsActivity = new MryDialogsActivity(MryDialogsActivity.this.arguments);
                    dialogsActivity.setDelegate(oldDelegate);
                    launchActivity.presentFragment(dialogsActivity, false, true);
                }
            }
        });
        RecyclerView recyclerView = this.sideMenu;
        if (recyclerView != null) {
            recyclerView.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
            this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
            this.sideMenu.getAdapter().notifyDataSetChanged();
        }
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
        this.selectedDialogsCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        actionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener($$Lambda$MryDialogsActivity$Xy6qcOZmYDbZ68egilPRqqplHo.INSTANCE);
        this.pinItem = actionMode.addItemWithWidth(100, R.drawable.msg_pin, AndroidUtilities.dp(54.0f));
        this.muteItem = actionMode.addItemWithWidth(104, R.drawable.msg_archive, AndroidUtilities.dp(54.0f));
        this.deleteItem = actionMode.addItemWithWidth(102, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", R.string.Delete));
        ActionBarMenuItem otherItem = actionMode.addItemWithWidth(0, R.drawable.ic_ab_other, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        int i3 = this.dialogsType;
        if (i3 == 5) {
            MryTextView tvAddView = new MryTextView(getParentActivity());
            tvAddView.setText(LocaleController.getString("Add", R.string.Add));
            tvAddView.setTextSize(1, 14.0f);
            tvAddView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            tvAddView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
            tvAddView.setGravity(16);
            menu.addItemView(106, tvAddView);
        } else if (i3 == 6) {
            menu.addItem(107, (int) R.drawable.groups_create);
        }
        this.readItem = otherItem.addSubItem(101, (int) R.drawable.msg_markread, (CharSequence) LocaleController.getString("MarkAsRead", R.string.MarkAsRead));
        this.clearItem = otherItem.addSubItem(103, (int) R.drawable.msg_clear, (CharSequence) LocaleController.getString("ClearHistory", R.string.ClearHistory));
        this.actionModeViews.add(this.pinItem);
        this.actionModeViews.add(this.muteItem);
        this.actionModeViews.add(this.deleteItem);
        this.actionModeViews.add(otherItem);
        super.createView(context);
        ContentView contentView = new ContentView(context2);
        this.containerLayout.addView(contentView, LayoutHelper.createFrame(-1, -2, 0, AndroidUtilities.dp(55.0f), 0, 0));
        AnonymousClass2 r0 = new RecyclerListView(context2) {
            private boolean firstLayout = true;
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (MryDialogsActivity.this.slidingView != null && MryDialogsActivity.this.pacmanAnimation != null) {
                    MryDialogsActivity.this.pacmanAnimation.draw(canvas, MryDialogsActivity.this.slidingView.getTop() + (MryDialogsActivity.this.slidingView.getMeasuredHeight() / 2));
                }
            }

            public void setAdapter(RecyclerView.Adapter adapter) {
                super.setAdapter(adapter);
                this.firstLayout = true;
            }

            private void checkIfAdapterValid() {
                if (MryDialogsActivity.this.listView != null && MryDialogsActivity.this.dialogsAdapter != null && MryDialogsActivity.this.listView.getAdapter() == MryDialogsActivity.this.dialogsAdapter && MryDialogsActivity.this.lastItemsCount != MryDialogsActivity.this.dialogsAdapter.getItemCount()) {
                    this.ignoreLayout = true;
                    MryDialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                    this.ignoreLayout = false;
                }
            }

            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (MryDialogsActivity.this.searchEmptyView != null) {
                    MryDialogsActivity.this.searchEmptyView.setPadding(left, top, right, bottom);
                }
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthSpec, int heightSpec) {
                if (this.firstLayout && MryDialogsActivity.this.getMessagesController().dialogsLoaded) {
                    if (MryDialogsActivity.this.hasHiddenArchive()) {
                        this.ignoreLayout = true;
                        MryDialogsActivity.this.layoutManager.scrollToPositionWithOffset(1, 0);
                        this.ignoreLayout = false;
                    }
                    this.firstLayout = false;
                }
                checkIfAdapterValid();
                super.onMeasure(widthSpec, heightSpec);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (!(MryDialogsActivity.this.dialogRemoveFinished == 0 && MryDialogsActivity.this.dialogInsertFinished == 0 && MryDialogsActivity.this.dialogChangeFinished == 0) && !MryDialogsActivity.this.dialogsItemAnimator.isRunning()) {
                    MryDialogsActivity.this.onDialogAnimationFinished();
                }
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            public boolean onTouchEvent(MotionEvent e) {
                if (MryDialogsActivity.this.waitingForScrollFinished || MryDialogsActivity.this.dialogRemoveFinished != 0 || MryDialogsActivity.this.dialogInsertFinished != 0 || MryDialogsActivity.this.dialogChangeFinished != 0) {
                    return false;
                }
                int action = e.getAction();
                if ((action == 1 || action == 3) && !MryDialogsActivity.this.itemTouchhelper.isIdle() && MryDialogsActivity.this.swipeController.swipingFolder) {
                    boolean unused = MryDialogsActivity.this.swipeController.swipeFolderBack = true;
                    if (MryDialogsActivity.this.itemTouchhelper.checkHorizontalSwipe((RecyclerView.ViewHolder) null, 4) != 0) {
                        SharedConfig.toggleArchiveHidden();
                        MryDialogsActivity.this.getUndoView().showWithAction(0, 7, (Runnable) null, (Runnable) null);
                    }
                }
                boolean result = super.onTouchEvent(e);
                if ((action == 1 || action == 3) && MryDialogsActivity.this.allowScrollToHiddenView) {
                    int currentPosition = MryDialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                    if (currentPosition == 0) {
                        View view = MryDialogsActivity.this.layoutManager.findViewByPosition(currentPosition);
                        int height = (AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f) / 4) * 3;
                        int diff = view.getTop() + view.getMeasuredHeight();
                        if (view != null) {
                            if (diff < height) {
                                MryDialogsActivity.this.listView.smoothScrollBy(0, diff, CubicBezierInterpolator.EASE_OUT_QUINT);
                            } else {
                                MryDialogsActivity.this.listView.smoothScrollBy(0, view.getTop(), CubicBezierInterpolator.EASE_OUT_QUINT);
                            }
                        }
                    }
                    boolean unused2 = MryDialogsActivity.this.allowScrollToHiddenView = false;
                }
                return result;
            }

            public boolean onInterceptTouchEvent(MotionEvent e) {
                if (MryDialogsActivity.this.waitingForScrollFinished || MryDialogsActivity.this.dialogRemoveFinished != 0 || MryDialogsActivity.this.dialogInsertFinished != 0 || MryDialogsActivity.this.dialogChangeFinished != 0) {
                    return false;
                }
                if (e.getAction() == 0) {
                    MryDialogsActivity mryDialogsActivity = MryDialogsActivity.this;
                    boolean unused = mryDialogsActivity.allowSwipeDuringCurrentTouch = !mryDialogsActivity.actionBar.isActionModeShowed();
                    checkIfAdapterValid();
                }
                return super.onInterceptTouchEvent(e);
            }
        };
        this.listView = r0;
        r0.addItemDecoration(new TopBottomDecoration(0, 10));
        this.listView.setOverScrollMode(2);
        this.listView.setScrollBarStyle(ConnectionsManager.FileTypeVideo);
        AnonymousClass3 r02 = new DialogsItemAnimator() {
            public void onRemoveFinished(RecyclerView.ViewHolder item) {
                if (MryDialogsActivity.this.dialogRemoveFinished == 2) {
                    int unused = MryDialogsActivity.this.dialogRemoveFinished = 1;
                }
            }

            public void onAddFinished(RecyclerView.ViewHolder item) {
                if (MryDialogsActivity.this.dialogInsertFinished == 2) {
                    int unused = MryDialogsActivity.this.dialogInsertFinished = 1;
                }
            }

            public void onChangeFinished(RecyclerView.ViewHolder item, boolean oldItem) {
                if (MryDialogsActivity.this.dialogChangeFinished == 2) {
                    int unused = MryDialogsActivity.this.dialogChangeFinished = 1;
                }
            }

            /* access modifiers changed from: protected */
            public void onAllAnimationsDone() {
                if (MryDialogsActivity.this.dialogRemoveFinished == 1 || MryDialogsActivity.this.dialogInsertFinished == 1 || MryDialogsActivity.this.dialogChangeFinished == 1) {
                    MryDialogsActivity.this.onDialogAnimationFinished();
                }
            }
        };
        this.dialogsItemAnimator = r02;
        this.listView.setItemAnimator(r02);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setInstantClick(true);
        this.listView.setTag(4);
        AnonymousClass4 r03 = new LinearLayoutManager(context2) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                if (!MryDialogsActivity.this.hasHiddenArchive() || position != 1) {
                    LinearSmoothScrollerMiddle linearSmoothScroller = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                    linearSmoothScroller.setTargetPosition(position);
                    startSmoothScroll(linearSmoothScroller);
                    return;
                }
                super.smoothScrollToPosition(recyclerView, state, position);
            }

            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                View view;
                View view2;
                if (MryDialogsActivity.this.listView.getAdapter() == MryDialogsActivity.this.dialogsAdapter && MryDialogsActivity.this.dialogsType == 0 && !MryDialogsActivity.this.onlySelect && !MryDialogsActivity.this.allowScrollToHiddenView && MryDialogsActivity.this.folderId == 0 && dy < 0 && MryDialogsActivity.this.getMessagesController().hasHiddenArchive()) {
                    int currentPosition = MryDialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                    if (currentPosition == 0 && (view2 = MryDialogsActivity.this.layoutManager.findViewByPosition(currentPosition)) != null && view2.getBottom() <= AndroidUtilities.dp(1.0f)) {
                        currentPosition = 1;
                    }
                    if (!(currentPosition == 0 || currentPosition == -1 || (view = MryDialogsActivity.this.layoutManager.findViewByPosition(currentPosition)) == null)) {
                        int canScrollDy = (-view.getTop()) + ((currentPosition - 1) * (AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f) + 1));
                        if (canScrollDy < Math.abs(dy)) {
                            MryDialogsActivity mryDialogsActivity = MryDialogsActivity.this;
                            int unused = mryDialogsActivity.totalConsumedAmount = mryDialogsActivity.totalConsumedAmount + Math.abs(dy);
                            dy = -canScrollDy;
                            if (MryDialogsActivity.this.startedScrollAtTop && MryDialogsActivity.this.totalConsumedAmount >= AndroidUtilities.dp(150.0f)) {
                                boolean unused2 = MryDialogsActivity.this.allowScrollToHiddenView = true;
                                try {
                                    MryDialogsActivity.this.listView.performHapticFeedback(3, 2);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };
        this.layoutManager = r03;
        r03.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        this.listView.setVerticalScrollBarEnabled(false);
        contentView.addView(this.listView, LayoutHelper.createFrame(-1, -2, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                MryDialogsActivity.this.lambda$createView$3$MryDialogsActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            public boolean onItemClick(View view, int position, float x, float y) {
                TLRPC.Chat chat;
                if (MryDialogsActivity.this.getParentActivity() == null) {
                    return false;
                }
                if (!MryDialogsActivity.this.actionBar.isActionModeShowed() && !AndroidUtilities.isTablet() && !MryDialogsActivity.this.onlySelect && (view instanceof DialogCell)) {
                    DialogCell cell = (DialogCell) view;
                    if (cell.isPointInsideAvatar(x, y)) {
                        long dialog_id = cell.getDialogId();
                        Bundle args = new Bundle();
                        int lower_part = (int) dialog_id;
                        int i = (int) (dialog_id >> 32);
                        int message_id = cell.getMessageId();
                        if (lower_part == 0) {
                            return false;
                        }
                        if (lower_part > 0) {
                            args.putInt("user_id", lower_part);
                        } else if (lower_part < 0) {
                            if (!(message_id == 0 || (chat = MryDialogsActivity.this.getMessagesController().getChat(Integer.valueOf(-lower_part))) == null || chat.migrated_to == null)) {
                                args.putInt("migrated_to", lower_part);
                                lower_part = -chat.migrated_to.channel_id;
                            }
                            args.putInt("chat_id", -lower_part);
                        }
                        if (message_id != 0) {
                            args.putInt("message_id", message_id);
                        }
                        if (MryDialogsActivity.this.searchString != null) {
                            if (MryDialogsActivity.this.getMessagesController().checkCanOpenChat(args, MryDialogsActivity.this)) {
                                MryDialogsActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                MryDialogsActivity.this.presentFragmentAsPreview(new ChatActivity(args));
                            }
                        } else if (MryDialogsActivity.this.getMessagesController().checkCanOpenChat(args, MryDialogsActivity.this)) {
                            MryDialogsActivity.this.presentFragmentAsPreview(new ChatActivity(args));
                        }
                        return true;
                    }
                }
                if (MryDialogsActivity.this.listView.getAdapter() == MryDialogsActivity.this.dialogsSearchAdapter) {
                    Object item = MryDialogsActivity.this.dialogsSearchAdapter.getItem(position);
                    return false;
                }
                ArrayList<TLRPC.Dialog> dialogs = MryDialogsActivity.getDialogsArray(MryDialogsActivity.this.currentAccount, MryDialogsActivity.this.dialogsType, MryDialogsActivity.this.folderId, MryDialogsActivity.this.dialogsListFrozen);
                int position2 = MryDialogsActivity.this.dialogsAdapter.fixPosition(position);
                if (position2 < 0 || position2 >= dialogs.size()) {
                    return false;
                }
                TLRPC.Dialog dialog = dialogs.get(position2);
                if (MryDialogsActivity.this.onlySelect) {
                    if (MryDialogsActivity.this.dialogsType != 3 || MryDialogsActivity.this.selectAlertString != null || !MryDialogsActivity.this.validateSlowModeDialog(dialog.id)) {
                        return false;
                    }
                    MryDialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.id, view);
                    MryDialogsActivity.this.updateSelectedCount();
                } else if (dialog instanceof TLRPC.TL_dialogFolder) {
                    return false;
                } else {
                    if (MryDialogsActivity.this.actionBar.isActionModeShowed() && dialog.pinned) {
                        return false;
                    }
                    MryDialogsActivity.this.showOrUpdateActionMode(dialog, view);
                }
                return true;
            }

            public void onLongClickRelease() {
                MryDialogsActivity.this.finishPreviewFragment();
            }

            public void onMove(float dx, float dy) {
                MryDialogsActivity.this.movePreviewFragment(dy);
            }
        });
        SwipeController swipeController2 = new SwipeController();
        this.swipeController = swipeController2;
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController2);
        this.itemTouchhelper = itemTouchHelper;
        itemTouchHelper.attachToRecyclerView(this.listView);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.searchEmptyView = emptyTextProgressView;
        emptyTextProgressView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setTopImage(R.drawable.settings_noresults);
        this.searchEmptyView.setText(LocaleController.getString("SettingsNoResults", R.string.SettingsNoResults));
        contentView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context2);
        this.progressView = radialProgressView;
        radialProgressView.setVisibility(8);
        contentView.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    if (MryDialogsActivity.this.searching && MryDialogsActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(MryDialogsActivity.this.getParentActivity().getCurrentFocus());
                    }
                    boolean unused = MryDialogsActivity.this.scrollingManually = true;
                } else {
                    boolean unused2 = MryDialogsActivity.this.scrollingManually = false;
                }
                if (MryDialogsActivity.this.waitingForScrollFinished && newState == 0) {
                    boolean unused3 = MryDialogsActivity.this.waitingForScrollFinished = false;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = Math.abs(MryDialogsActivity.this.layoutManager.findLastVisibleItemPosition() - MryDialogsActivity.this.layoutManager.findFirstVisibleItemPosition()) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                MryDialogsActivity.this.dialogsItemAnimator.onListScroll(-dy);
                if (!MryDialogsActivity.this.searching || !MryDialogsActivity.this.searchWas) {
                    if (visibleItemCount > 0 && MryDialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= MryDialogsActivity.getDialogsArray(MryDialogsActivity.this.currentAccount, MryDialogsActivity.this.dialogsType, MryDialogsActivity.this.folderId, MryDialogsActivity.this.dialogsListFrozen).size() - 10) {
                        boolean fromCache = !MryDialogsActivity.this.getMessagesController().isDialogsEndReached(MryDialogsActivity.this.folderId);
                        if (fromCache || !MryDialogsActivity.this.getMessagesController().isServerDialogsEndReached(MryDialogsActivity.this.folderId)) {
                            AndroidUtilities.runOnUIThread(new Runnable(fromCache) {
                                private final /* synthetic */ boolean f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    MryDialogsActivity.AnonymousClass6.this.lambda$onScrolled$0$MryDialogsActivity$6(this.f$1);
                                }
                            });
                        }
                    }
                } else if (visibleItemCount > 0 && MryDialogsActivity.this.layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !MryDialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
                    MryDialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
                }
            }

            public /* synthetic */ void lambda$onScrolled$0$MryDialogsActivity$6(boolean fromCache) {
                MryDialogsActivity.this.getMessagesController().loadDialogs(MryDialogsActivity.this.folderId, -1, 100, fromCache);
            }
        });
        if (this.searchString == null) {
            AnonymousClass7 r11 = r0;
            AnonymousClass7 r04 = new MyDialogsAdapter(context, this.dialogsType, this.folderId, this.onlySelect) {
                public void notifyDataSetChanged() {
                    int unused = MryDialogsActivity.this.lastItemsCount = getItemCount();
                    super.notifyDataSetChanged();
                }
            };
            this.dialogsAdapter = r11;
            if (AndroidUtilities.isTablet()) {
                long j = this.openedDialogId;
                if (j != 0) {
                    this.dialogsAdapter.setOpenedDialogId(j);
                }
            }
            this.listView.setAdapter(this.dialogsAdapter);
        }
        int type = 0;
        if (this.searchString != null) {
            type = 2;
        } else if (!this.onlySelect) {
            type = 1;
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = new DialogsSearchAdapter(context2, type, this.dialogsType);
        this.dialogsSearchAdapter = dialogsSearchAdapter2;
        dialogsSearchAdapter2.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate() {
            public void searchStateChanged(boolean search) {
                if (MryDialogsActivity.this.searching && MryDialogsActivity.this.searchWas && MryDialogsActivity.this.searchEmptyView != null) {
                    if (search) {
                        MryDialogsActivity.this.searchEmptyView.showProgress();
                    } else {
                        MryDialogsActivity.this.searchEmptyView.showTextView();
                    }
                }
            }

            public void didPressedOnSubDialog(long did) {
                if (!MryDialogsActivity.this.onlySelect) {
                    int lower_id = (int) did;
                    Bundle args = new Bundle();
                    if (lower_id > 0) {
                        args.putInt("user_id", lower_id);
                    } else {
                        args.putInt("chat_id", -lower_id);
                    }
                    MryDialogsActivity.this.closeSearch();
                    if (AndroidUtilities.isTablet() && MryDialogsActivity.this.dialogsAdapter != null) {
                        MryDialogsActivity.this.dialogsAdapter.setOpenedDialogId(MryDialogsActivity.this.openedDialogId = did);
                        MryDialogsActivity.this.updateVisibleRows(512);
                    }
                    if (MryDialogsActivity.this.searchString != null) {
                        if (MryDialogsActivity.this.getMessagesController().checkCanOpenChat(args, MryDialogsActivity.this)) {
                            MryDialogsActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            MryDialogsActivity.this.presentFragment(new ChatActivity(args));
                        }
                    } else if (MryDialogsActivity.this.getMessagesController().checkCanOpenChat(args, MryDialogsActivity.this)) {
                        MryDialogsActivity.this.presentFragment(new ChatActivity(args));
                    }
                } else if (MryDialogsActivity.this.validateSlowModeDialog(did)) {
                    if (MryDialogsActivity.this.dialogsAdapter.hasSelectedDialogs()) {
                        MryDialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(did, (View) null);
                        MryDialogsActivity.this.updateSelectedCount();
                        MryDialogsActivity.this.closeSearch();
                        return;
                    }
                    MryDialogsActivity.this.didSelectResult(did, true, false);
                }
            }

            public void needRemoveHint(int did) {
                TLRPC.User user;
                if (MryDialogsActivity.this.getParentActivity() != null && (user = MryDialogsActivity.this.getMessagesController().getUser(Integer.valueOf(did))) != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) MryDialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("ChatHintsDeleteAlertTitle", R.string.ChatHintsDeleteAlertTitle));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatHintsDeleteAlert", R.string.ChatHintsDeleteAlert, ContactsController.formatName(user.first_name, user.last_name))));
                    builder.setPositiveButton(LocaleController.getString("StickersRemove", R.string.StickersRemove), new DialogInterface.OnClickListener(did) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MryDialogsActivity.AnonymousClass8.this.lambda$needRemoveHint$0$MryDialogsActivity$8(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    AlertDialog dialog = builder.create();
                    MryDialogsActivity.this.showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    }
                }
            }

            public /* synthetic */ void lambda$needRemoveHint$0$MryDialogsActivity$8(int did, DialogInterface dialogInterface, int i) {
                MryDialogsActivity.this.getMediaDataController().removePeer(did);
            }

            public void needClearList() {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) MryDialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", R.string.ClearSearchAlertTitle));
                builder.setMessage(LocaleController.getString("ClearSearchAlert", R.string.ClearSearchAlert));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        MryDialogsActivity.AnonymousClass8.this.lambda$needClearList$1$MryDialogsActivity$8(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                MryDialogsActivity.this.showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }

            public /* synthetic */ void lambda$needClearList$1$MryDialogsActivity$8(DialogInterface dialogInterface, int i) {
                if (MryDialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    MryDialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    MryDialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }
        });
        this.listView.setEmptyView(this.folderId == 0 ? this.progressView : null);
        if (this.searchString != null) {
            this.actionBar.openSearchField(this.searchString, false);
        }
        if (!this.onlySelect && this.dialogsType == 0) {
            FragmentContextView fragmentLocationContextView = new FragmentContextView(context2, this, true);
            contentView.addView(fragmentLocationContextView, LayoutHelper.createFrame(-1.0f, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            FragmentContextView fragmentContextView = new FragmentContextView(context2, this, false);
            contentView.addView(fragmentContextView, LayoutHelper.createFrame(-1.0f, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            fragmentContextView.setAdditionalContextView(fragmentLocationContextView);
            fragmentLocationContextView.setAdditionalContextView(fragmentContextView);
        } else if (this.dialogsType == 3 && this.selectAlertString == null) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.onDestroy();
            }
            ChatActivityEnterView chatActivityEnterView2 = new ChatActivityEnterView(getParentActivity(), contentView, (ChatActivity) null, false);
            this.commentView = chatActivityEnterView2;
            chatActivityEnterView2.setAllowStickersAndGifs(false, false);
            this.commentView.setForceShowSendButton(true, false);
            this.commentView.setVisibility(8);
            contentView.addView(this.commentView, LayoutHelper.createFrame(-1, -2, 83));
            this.commentView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() {
                public /* synthetic */ boolean hasScheduledMessages() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
                }

                public /* synthetic */ void openScheduledMessages() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
                }

                public /* synthetic */ void scrollToSendingMessage() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
                }

                public void onMessageSend(CharSequence message, boolean notify, int scheduleDate) {
                    if (MryDialogsActivity.this.delegate != null) {
                        ArrayList<Long> selectedDialogs = MryDialogsActivity.this.dialogsAdapter.getSelectedDialogs();
                        if (!selectedDialogs.isEmpty()) {
                            MryDialogsActivity.this.delegate.didSelectDialogs(MryDialogsActivity.this, selectedDialogs, message, false);
                        }
                    }
                }

                public void onSwitchRecordMode(boolean video) {
                }

                public void onTextSelectionChanged(int start, int end) {
                }

                public void onStickersExpandedChange() {
                }

                public void onPreAudioVideoRecord() {
                }

                public void onTextChanged(CharSequence text, boolean bigChange) {
                }

                public void onTextSpansChanged(CharSequence text) {
                }

                public void needSendTyping() {
                }

                public void onAttachButtonHidden() {
                }

                public void onAttachButtonShow() {
                }

                public void onMessageEditEnd(boolean loading) {
                }

                public void onWindowSizeChanged(int size) {
                }

                public void onStickersTab(boolean opened) {
                }

                public void didPressedAttachButton(int position, ChatEnterMenuType menuType) {
                }

                public void needStartRecordVideo(int state, boolean notify, int scheduleDate) {
                }

                public void needChangeVideoPreviewState(int state, float seekProgress) {
                }

                public void needStartRecordAudio(int state) {
                }

                public void needShowMediaBanHint() {
                }

                public void onUpdateSlowModeButton(View button, boolean show, CharSequence time) {
                }
            });
        }
        for (int a2 = 0; a2 < 2; a2++) {
            this.undoView[a2] = new UndoView(context2) {
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    if (this == MryDialogsActivity.this.undoView[0] && MryDialogsActivity.this.undoView[1].getVisibility() != 0) {
                        float unused = MryDialogsActivity.this.additionalFloatingTranslation = ((float) (getMeasuredHeight() + AndroidUtilities.dp(8.0f))) - translationY;
                    }
                }

                /* access modifiers changed from: protected */
                public boolean canUndo() {
                    return !MryDialogsActivity.this.dialogsItemAnimator.isRunning();
                }
            };
            contentView.addView(this.undoView[a2], LayoutHelper.createFrame(-1.0f, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        if (this.folderId != 0) {
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultArchived));
            this.listView.setGlowColor(Theme.getColor(Theme.key_actionBarDefaultArchived));
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_actionBarDefaultArchivedTitle));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultArchivedIcon), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSelector), false);
            this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSearch), false);
            this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSearchPlaceholder), true);
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$MryDialogsActivity() {
        this.listView.smoothScrollToPosition(hasHiddenArchive() ? 1 : 0);
    }

    static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$3$MryDialogsActivity(View view, int position) {
        long dialog_id;
        TLRPC.Chat chat;
        View view2 = view;
        int i = position;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && recyclerListView.getAdapter() != null && getParentActivity() != null) {
            int message_id = 0;
            boolean isGlobalSearch = false;
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            MyDialogsAdapter myDialogsAdapter = this.dialogsAdapter;
            if (adapter == myDialogsAdapter) {
                TLObject object = myDialogsAdapter.getItem(i);
                if (object instanceof TLRPC.User) {
                    dialog_id = (long) ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Dialog) {
                    TLRPC.Dialog dialog = (TLRPC.Dialog) object;
                    if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                        long dialog_id2 = dialog.id;
                        if (this.actionBar.isActionModeShowed()) {
                            showOrUpdateActionMode(dialog, view2);
                            return;
                        }
                        dialog_id = dialog_id2;
                    } else if (!this.actionBar.isActionModeShowed()) {
                        Bundle args = new Bundle();
                        args.putInt("folderId", ((TLRPC.TL_dialogFolder) dialog).folder.id);
                        presentFragment(new MryDialogsActivity(args));
                        return;
                    } else {
                        return;
                    }
                } else if (object instanceof TLRPC.TL_recentMeUrlChat) {
                    dialog_id = (long) (-((TLRPC.TL_recentMeUrlChat) object).chat_id);
                } else if (object instanceof TLRPC.TL_recentMeUrlUser) {
                    dialog_id = (long) ((TLRPC.TL_recentMeUrlUser) object).user_id;
                } else if (object instanceof TLRPC.TL_recentMeUrlChatInvite) {
                    TLRPC.TL_recentMeUrlChatInvite chatInvite = (TLRPC.TL_recentMeUrlChatInvite) object;
                    TLRPC.ChatInvite invite = chatInvite.chat_invite;
                    if ((invite.chat == null && (!invite.channel || invite.megagroup)) || (invite.chat != null && (!ChatObject.isChannel(invite.chat) || invite.chat.megagroup))) {
                        String hash = chatInvite.url;
                        int index = hash.indexOf(47);
                        if (index > 0) {
                            hash = hash.substring(index + 1);
                        }
                        showDialog(new JoinGroupAlert(getParentActivity(), invite, hash, this));
                        return;
                    } else if (invite.chat != null) {
                        dialog_id = (long) (-invite.chat.id);
                    } else {
                        return;
                    }
                } else if (object instanceof TLRPC.TL_recentMeUrlStickerSet) {
                    TLRPC.StickerSet stickerSet = ((TLRPC.TL_recentMeUrlStickerSet) object).set.set;
                    TLRPC.TL_inputStickerSetID set = new TLRPC.TL_inputStickerSetID();
                    set.id = stickerSet.id;
                    set.access_hash = stickerSet.access_hash;
                    StickersAlert stickersAlert = r0;
                    TLRPC.TL_inputStickerSetID tL_inputStickerSetID = set;
                    StickersAlert stickersAlert2 = new StickersAlert(getParentActivity(), this, set, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
                    showDialog(stickersAlert);
                    return;
                } else if (!(object instanceof TLRPC.TL_recentMeUrlUnknown)) {
                    return;
                } else {
                    return;
                }
            } else {
                DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
                if (adapter == dialogsSearchAdapter2) {
                    Object obj = dialogsSearchAdapter2.getItem(i);
                    isGlobalSearch = this.dialogsSearchAdapter.isGlobalSearch(i);
                    if (obj instanceof TLRPC.User) {
                        dialog_id = (long) ((TLRPC.User) obj).id;
                        if (!this.onlySelect) {
                            this.searchDialogId = dialog_id;
                            this.searchObject = (TLRPC.User) obj;
                        }
                    } else if (obj instanceof TLRPC.Chat) {
                        dialog_id = (long) (-((TLRPC.Chat) obj).id);
                        if (!this.onlySelect) {
                            this.searchDialogId = dialog_id;
                            this.searchObject = (TLRPC.Chat) obj;
                        }
                    } else if (obj instanceof TLRPC.EncryptedChat) {
                        dialog_id = ((long) ((TLRPC.EncryptedChat) obj).id) << 32;
                        if (!this.onlySelect) {
                            this.searchDialogId = dialog_id;
                            this.searchObject = (TLRPC.EncryptedChat) obj;
                        }
                    } else if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        dialog_id = messageObject.getDialogId();
                        message_id = messageObject.getId();
                        DialogsSearchAdapter dialogsSearchAdapter3 = this.dialogsSearchAdapter;
                        dialogsSearchAdapter3.addHashtagsFromMessage(dialogsSearchAdapter3.getLastSearchString());
                    } else {
                        if (obj instanceof String) {
                            String str = (String) obj;
                            if (this.dialogsSearchAdapter.isHashtagSearch()) {
                                this.actionBar.openSearchField(str, false);
                            } else if (!str.equals("section")) {
                                NewContactActivity activity = new NewContactActivity();
                                activity.setInitialPhoneNumber(str);
                                presentFragment(activity);
                            }
                        }
                        dialog_id = 0;
                    }
                } else {
                    dialog_id = 0;
                }
            }
            if (dialog_id != 0) {
                if (!this.onlySelect) {
                    Bundle args2 = new Bundle();
                    int lower_part = (int) dialog_id;
                    int high_id = (int) (dialog_id >> 32);
                    if (lower_part == 0) {
                        args2.putInt("enc_id", high_id);
                    } else if (lower_part > 0) {
                        args2.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        if (!(message_id == 0 || (chat = getMessagesController().getChat(Integer.valueOf(-lower_part))) == null || chat.migrated_to == null)) {
                            args2.putInt("migrated_to", lower_part);
                            lower_part = -chat.migrated_to.channel_id;
                        }
                        args2.putInt("chat_id", -lower_part);
                    }
                    if (message_id != 0) {
                        args2.putInt("message_id", message_id);
                    } else if (!isGlobalSearch) {
                        closeSearch();
                    } else {
                        TLObject tLObject = this.searchObject;
                        if (tLObject != null) {
                            this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                            this.searchObject = null;
                        }
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (this.openedDialogId != dialog_id || adapter == this.dialogsSearchAdapter) {
                            MyDialogsAdapter myDialogsAdapter2 = this.dialogsAdapter;
                            if (myDialogsAdapter2 != null) {
                                this.openedDialogId = dialog_id;
                                myDialogsAdapter2.setOpenedDialogId(dialog_id);
                                updateVisibleRows(512);
                            }
                        } else {
                            return;
                        }
                    }
                    if (this.searchString != null) {
                        if (getMessagesController().checkCanOpenChat(args2, this)) {
                            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            presentFragment(new ChatActivity(args2));
                        }
                    } else if (getMessagesController().checkCanOpenChat(args2, this)) {
                        presentFragment(new ChatActivity(args2));
                    }
                } else if (validateSlowModeDialog(dialog_id)) {
                    if (this.dialogsAdapter.hasSelectedDialogs()) {
                        this.dialogsAdapter.addOrRemoveSelectedDialog(dialog_id, view2);
                        updateSelectedCount();
                        return;
                    }
                    didSelectResult(dialog_id, true, false);
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        MyDialogsAdapter myDialogsAdapter = this.dialogsAdapter;
        if (myDialogsAdapter != null && !this.dialogsListFrozen) {
            myDialogsAdapter.notifyDataSetChanged();
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        if (!this.onlySelect && this.folderId == 0) {
            getMediaDataController().checkStickers(4);
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter2 != null) {
            dialogsSearchAdapter2.notifyDataSetChanged();
        }
        boolean hasNotStoragePermission = false;
        if (this.checkPermission && !this.onlySelect && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                if (activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    hasNotStoragePermission = true;
                }
                if (!hasNotStoragePermission) {
                    return;
                }
                if (!hasNotStoragePermission || !activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                    askForPermissons(true);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                this.permissionDialog = create;
                showDialog(create);
            }
        } else if (!this.onlySelect && XiaomiUtilities.isMIUI() && Build.VERSION.SDK_INT >= 19 && !XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_SHOW_WHEN_LOCKED) && getParentActivity() != null && !MessagesController.getGlobalNotificationsSettings().getBoolean("askedAboutMiuiLockscreen", false)) {
            showDialog(new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("PermissionXiaomiLockscreen", R.string.PermissionXiaomiLockscreen)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MryDialogsActivity.this.lambda$onResume$4$MryDialogsActivity(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), $$Lambda$MryDialogsActivity$Y8U7vkySd7s49GgQtR_IOfZQ9q4.INSTANCE).create());
        }
    }

    public /* synthetic */ void lambda$onResume$4$MryDialogsActivity(DialogInterface dialog, int which) {
        Intent intent = XiaomiUtilities.getPermissionManagerIntent();
        if (intent != null) {
            try {
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent2 = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent2.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                    getParentActivity().startActivity(intent2);
                } catch (Exception xx) {
                    FileLog.e((Throwable) xx);
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    public boolean onBackPressed() {
        if (this.actionBar == null || !this.actionBar.isActionModeShowed()) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView == null || !chatActivityEnterView.isPopupShowing()) {
                return super.onBackPressed();
            }
            this.commentView.hidePopup(true);
            return false;
        }
        hideActionMode(true);
        return false;
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        if (this.closeSearchFieldOnHide) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                this.searchObject = null;
            }
            this.closeSearchFieldOnHide = false;
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    /* access modifiers changed from: private */
    public boolean hasHiddenArchive() {
        return this.listView.getAdapter() == this.dialogsAdapter && !this.onlySelect && this.dialogsType == 0 && this.folderId == 0 && getMessagesController().hasHiddenArchive();
    }

    /* access modifiers changed from: private */
    public boolean waitingForDialogsAnimationEnd() {
        return (!this.dialogsItemAnimator.isRunning() && this.dialogRemoveFinished == 0 && this.dialogInsertFinished == 0 && this.dialogChangeFinished == 0) ? false : true;
    }

    /* access modifiers changed from: private */
    public void onDialogAnimationFinished() {
        this.dialogRemoveFinished = 0;
        this.dialogInsertFinished = 0;
        this.dialogChangeFinished = 0;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                MryDialogsActivity.this.lambda$onDialogAnimationFinished$6$MryDialogsActivity();
            }
        });
    }

    public /* synthetic */ void lambda$onDialogAnimationFinished$6$MryDialogsActivity() {
        if (this.folderId != 0 && frozenDialogsList.isEmpty()) {
            this.listView.setEmptyView((View) null);
            this.progressView.setVisibility(4);
            finishFragment();
        }
        setDialogsListFrozen(false);
        updateDialogIndices();
    }

    /* access modifiers changed from: private */
    public void hideActionMode(boolean animateCheck) {
        this.actionBar.hideActionMode();
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        int i = 0;
        this.dialogsAdapter.setEdit(false);
        this.dialogsAdapter.notifyDataSetChanged();
        if (this.menuDrawable != null) {
            this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", R.string.AccDescrOpenMenu));
        }
        this.dialogsAdapter.getSelectedDialogs().clear();
        MenuDrawable menuDrawable2 = this.menuDrawable;
        if (menuDrawable2 != null) {
            menuDrawable2.setRotation(0.0f, true);
        } else {
            BackDrawable backDrawable2 = this.backDrawable;
            if (backDrawable2 != null) {
                backDrawable2.setRotation(0.0f, true);
            }
        }
        this.allowMoving = false;
        if (this.movingWas) {
            getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC.InputDialogPeer>) null, 0);
            this.movingWas = false;
        }
        updateCounters(true);
        this.dialogsAdapter.onReorderStateChanged(false);
        if (animateCheck) {
            i = 8192;
        }
        updateVisibleRows(196608 | i);
    }

    private int getPinnedCount() {
        int pinnedCount = 0;
        ArrayList<TLRPC.Dialog> dialogs = getMessagesController().getDialogs(this.folderId);
        int N = dialogs.size();
        for (int a = 0; a < N; a++) {
            TLRPC.Dialog dialog = dialogs.get(a);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                int i = (int) dialog.id;
                if (!dialog.pinned) {
                    break;
                }
                pinnedCount++;
            }
        }
        return pinnedCount;
    }

    /* access modifiers changed from: private */
    public void perfromSelectedDialogsAction(int action, boolean alert) {
        TLRPC.Chat chat;
        TLRPC.User user;
        int maxPinnedCount;
        ArrayList<TLRPC.Dialog> dialogs;
        int undoAction;
        int i = action;
        if (getParentActivity() != null) {
            ArrayList<Long> selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
            int count = selectedDialogs.size();
            boolean hintShowed = false;
            if (i == 105) {
                ArrayList<Long> copy = new ArrayList<>(selectedDialogs);
                getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 1 : 0, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
                hideActionMode(false);
                if (this.folderId == 0) {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (preferences.getBoolean("archivehint_l", false) || SharedConfig.archiveHidden) {
                        hintShowed = true;
                    }
                    if (!hintShowed) {
                        preferences.edit().putBoolean("archivehint_l", true).commit();
                    }
                    if (hintShowed) {
                        undoAction = copy.size() > 1 ? 4 : 2;
                    } else {
                        undoAction = copy.size() > 1 ? 5 : 3;
                    }
                    getUndoView().showWithAction(0, undoAction, (Runnable) null, new Runnable(copy) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MryDialogsActivity.this.lambda$perfromSelectedDialogsAction$7$MryDialogsActivity(this.f$1);
                        }
                    });
                } else if (getMessagesController().getDialogs(this.folderId).isEmpty()) {
                    this.listView.setEmptyView((View) null);
                    this.progressView.setVisibility(4);
                    finishFragment();
                }
            } else {
                if (i == 100 && this.canPinCount != 0) {
                    int pinnedCount = 0;
                    int pinnedSecretCount = 0;
                    int newPinnedCount = 0;
                    int newPinnedSecretCount = 0;
                    ArrayList<TLRPC.Dialog> dialogs2 = getMessagesController().getDialogs(this.folderId);
                    int a = 0;
                    int N = dialogs2.size();
                    while (true) {
                        if (a >= N) {
                            break;
                        }
                        TLRPC.Dialog dialog = dialogs2.get(a);
                        if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                            dialogs = dialogs2;
                            int lower_id = (int) dialog.id;
                            if (!dialog.pinned) {
                                break;
                            } else if (lower_id == 0) {
                                pinnedSecretCount++;
                            } else {
                                pinnedCount++;
                            }
                        } else {
                            dialogs = dialogs2;
                        }
                        a++;
                        dialogs2 = dialogs;
                    }
                    for (int a2 = 0; a2 < count; a2++) {
                        long selectedDialog = selectedDialogs.get(a2).longValue();
                        TLRPC.Dialog dialog2 = getMessagesController().dialogs_dict.get(selectedDialog);
                        if (dialog2 != null && !dialog2.pinned) {
                            if (((int) selectedDialog) == 0) {
                                newPinnedSecretCount++;
                            } else {
                                newPinnedCount++;
                            }
                        }
                    }
                    if (this.folderId != 0) {
                        maxPinnedCount = getMessagesController().maxFolderPinnedDialogsCount;
                    } else {
                        maxPinnedCount = getMessagesController().maxPinnedDialogsCount;
                    }
                    if (newPinnedSecretCount + pinnedSecretCount > maxPinnedCount || newPinnedCount + pinnedCount > maxPinnedCount) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.formatString("PinToTopLimitReached", R.string.PinToTopLimitReached, LocaleController.formatPluralString("Chats", maxPinnedCount)));
                        AndroidUtilities.shakeView(this.pinItem, 2.0f, 0);
                        Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                        if (v != null) {
                            v.vibrate(200);
                            return;
                        }
                        return;
                    }
                } else if ((i == 102 || i == 103) && count > 1 && alert && alert) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (i == 102) {
                        builder.setTitle(LocaleController.formatString("DeleteFewChatsTitle", R.string.DeleteFewChatsTitle, LocaleController.formatPluralString("ChatsSelected", count)));
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteFewChats", R.string.AreYouSureDeleteFewChats));
                        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener(i) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                MryDialogsActivity.this.lambda$perfromSelectedDialogsAction$8$MryDialogsActivity(this.f$1, dialogInterface, i);
                            }
                        });
                    } else if (this.canClearCacheCount != 0) {
                        builder.setTitle(LocaleController.formatString("ClearCacheFewChatsTitle", R.string.ClearCacheFewChatsTitle, LocaleController.formatPluralString("ChatsSelectedClearCache", count)));
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistoryCacheFewChats", R.string.AreYouSureClearHistoryCacheFewChats));
                        builder.setPositiveButton(LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache), new DialogInterface.OnClickListener(i) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                MryDialogsActivity.this.lambda$perfromSelectedDialogsAction$9$MryDialogsActivity(this.f$1, dialogInterface, i);
                            }
                        });
                    } else {
                        builder.setTitle(LocaleController.formatString("ClearFewChatsTitle", R.string.ClearFewChatsTitle, LocaleController.formatPluralString("ChatsSelectedClear", count)));
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistoryFewChats", R.string.AreYouSureClearHistoryFewChats));
                        builder.setPositiveButton(LocaleController.getString("ClearHistory", R.string.ClearHistory), new DialogInterface.OnClickListener(i) {
                            private final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                MryDialogsActivity.this.lambda$perfromSelectedDialogsAction$10$MryDialogsActivity(this.f$1, dialogInterface, i);
                            }
                        });
                    }
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    AlertDialog alertDialog = builder.create();
                    showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                        return;
                    }
                    return;
                }
                boolean scrollToTop = false;
                for (int a3 = 0; a3 < count; a3++) {
                    long selectedDialog2 = selectedDialogs.get(a3).longValue();
                    TLRPC.Dialog dialog3 = getMessagesController().dialogs_dict.get(selectedDialog2);
                    if (dialog3 != null) {
                        int lower_id2 = (int) selectedDialog2;
                        int high_id = (int) (selectedDialog2 >> 32);
                        if (lower_id2 == 0) {
                            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(high_id));
                            if (encryptedChat != null) {
                                user = getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id));
                                chat = null;
                            } else {
                                user = new TLRPC.TL_userEmpty();
                                chat = null;
                            }
                        } else if (lower_id2 > 0) {
                            user = getMessagesController().getUser(Integer.valueOf(lower_id2));
                            chat = null;
                        } else {
                            user = null;
                            chat = getMessagesController().getChat(Integer.valueOf(-lower_id2));
                        }
                        if (chat != null || user != null) {
                            boolean isBot = user != null && user.bot && !MessagesController.isSupportUser(user);
                            if (i == 100) {
                                if (this.canPinCount != 0) {
                                    if (!dialog3.pinned) {
                                        if (getMessagesController().pinDialog(selectedDialog2, true, (TLRPC.InputPeer) null, -1)) {
                                            scrollToTop = true;
                                        }
                                    }
                                } else if (dialog3.pinned) {
                                    if (getMessagesController().pinDialog(selectedDialog2, false, (TLRPC.InputPeer) null, -1)) {
                                        scrollToTop = true;
                                    }
                                }
                            } else if (i != 101) {
                                if (i != 102) {
                                    if (i != 103) {
                                        if (i == 104) {
                                            if (count == 1 && this.canMuteCount == 1) {
                                                showDialog(AlertsCreator.createMuteAlert(getParentActivity(), selectedDialog2), new DialogInterface.OnDismissListener() {
                                                    public final void onDismiss(DialogInterface dialogInterface) {
                                                        MryDialogsActivity.this.lambda$perfromSelectedDialogsAction$13$MryDialogsActivity(dialogInterface);
                                                    }
                                                });
                                                return;
                                            } else if (this.canUnmuteCount != 0) {
                                                if (getMessagesController().isDialogMuted(selectedDialog2)) {
                                                    getNotificationsController().setDialogNotificationsSettings(selectedDialog2, 4);
                                                }
                                            } else if (!getMessagesController().isDialogMuted(selectedDialog2)) {
                                                getNotificationsController().setDialogNotificationsSettings(selectedDialog2, 3);
                                            }
                                        }
                                    }
                                }
                                if (count == 1) {
                                    int i2 = lower_id2;
                                    TLRPC.Dialog dialog4 = dialog3;
                                    int i3 = high_id;
                                    AlertsCreator.createClearOrDeleteDialogAlert(this, i == 103, chat, user, lower_id2 == 0, new MessagesStorage.BooleanCallback(action, chat, selectedDialog2, isBot) {
                                        private final /* synthetic */ int f$1;
                                        private final /* synthetic */ TLRPC.Chat f$2;
                                        private final /* synthetic */ long f$3;
                                        private final /* synthetic */ boolean f$4;

                                        {
                                            this.f$1 = r2;
                                            this.f$2 = r3;
                                            this.f$3 = r4;
                                            this.f$4 = r6;
                                        }

                                        public final void run(boolean z) {
                                            MryDialogsActivity.this.lambda$perfromSelectedDialogsAction$12$MryDialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, z);
                                        }
                                    });
                                    return;
                                }
                                TLRPC.Dialog dialog5 = dialog3;
                                int i4 = high_id;
                                if (i == 103 && this.canClearCacheCount != 0) {
                                    getMessagesController().deleteDialog(selectedDialog2, 2, false);
                                } else if (i == 103) {
                                    getMessagesController().deleteDialog(selectedDialog2, 1, false);
                                } else {
                                    if (chat == null) {
                                        getMessagesController().deleteDialog(selectedDialog2, 0, false);
                                        if (isBot) {
                                            getMessagesController().blockUser((int) selectedDialog2);
                                        }
                                    } else if (ChatObject.isNotInChat(chat)) {
                                        getMessagesController().deleteDialog(selectedDialog2, 0, false);
                                    } else {
                                        getMessagesController().deleteUserFromChat((int) (-selectedDialog2), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
                                    }
                                    if (AndroidUtilities.isTablet()) {
                                        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(selectedDialog2));
                                    }
                                }
                            } else if (this.canReadCount != 0) {
                                getMessagesController().markMentionsAsRead(selectedDialog2);
                                getMessagesController().markDialogAsRead(selectedDialog2, dialog3.top_message, dialog3.top_message, dialog3.last_message_date, false, 0, true, 0);
                            } else {
                                getMessagesController().markDialogAsUnread(selectedDialog2, (TLRPC.InputPeer) null, 0);
                            }
                        }
                    }
                }
                if (i == 100) {
                    getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC.InputDialogPeer>) null, 0);
                }
                if (scrollToTop) {
                    this.listView.smoothScrollToPosition(hasHiddenArchive() ? 1 : 0);
                }
                hideActionMode((i == 100 || i == 102) ? false : true);
            }
        }
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$7$MryDialogsActivity(ArrayList copy) {
        getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$8$MryDialogsActivity(int action, DialogInterface dialog1, int which) {
        getMessagesController().setDialogsInTransaction(true);
        perfromSelectedDialogsAction(action, false);
        getMessagesController().setDialogsInTransaction(false);
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
        if (this.folderId != 0 && getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false).size() == 0) {
            this.listView.setEmptyView((View) null);
            this.progressView.setVisibility(4);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$9$MryDialogsActivity(int action, DialogInterface dialog1, int which) {
        perfromSelectedDialogsAction(action, false);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$10$MryDialogsActivity(int action, DialogInterface dialog1, int which) {
        perfromSelectedDialogsAction(action, false);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$12$MryDialogsActivity(int action, TLRPC.Chat chat, long selectedDialog, boolean isBot, boolean param) {
        int i = action;
        TLRPC.Chat chat2 = chat;
        long j = selectedDialog;
        hideActionMode(false);
        if (i != 103 || !ChatObject.isChannel(chat)) {
            boolean z = param;
        } else if (!chat2.megagroup || !TextUtils.isEmpty(chat2.username)) {
            getMessagesController().deleteDialog(j, 2, param);
            return;
        } else {
            boolean z2 = param;
        }
        if (i == 102 && this.folderId != 0 && getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false).size() == 1) {
            this.progressView.setVisibility(4);
        }
        UndoView undoView2 = getUndoView();
        int i2 = i == 103 ? 0 : 1;
        $$Lambda$MryDialogsActivity$OqN3EpxRP48_kdogSZz_PyKto r8 = r0;
        $$Lambda$MryDialogsActivity$OqN3EpxRP48_kdogSZz_PyKto r0 = new Runnable(action, selectedDialog, param, chat, isBot) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ TLRPC.Chat f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                MryDialogsActivity.this.lambda$null$11$MryDialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        };
        undoView2.showWithAction(j, i2, (Runnable) r8);
    }

    public /* synthetic */ void lambda$null$11$MryDialogsActivity(int action, long selectedDialog, boolean param, TLRPC.Chat chat, boolean isBot) {
        if (action == 103) {
            getMessagesController().deleteDialog(selectedDialog, 1, param);
            return;
        }
        if (chat == null) {
            getMessagesController().deleteDialog(selectedDialog, 0, param);
            if (isBot) {
                getMessagesController().blockUser((int) selectedDialog);
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(selectedDialog, 0, param);
        } else {
            getMessagesController().deleteUserFromChat((int) (-selectedDialog), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(selectedDialog));
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$13$MryDialogsActivity(DialogInterface dialog12) {
        hideActionMode(true);
    }

    private void updateCounters(boolean hide) {
        ArrayList<Long> selectedDialogs;
        int canUnarchiveCount;
        TLRPC.User user;
        int canClearHistoryCount = 0;
        int canDeleteCount = 0;
        int canUnpinCount = 0;
        int canArchiveCount = 0;
        int high_id = 0;
        this.canUnmuteCount = 0;
        this.canMuteCount = 0;
        this.canPinCount = 0;
        this.canReadCount = 0;
        this.canClearCacheCount = 0;
        if (!hide) {
            ArrayList<Long> selectedDialogs2 = this.dialogsAdapter.getSelectedDialogs();
            int count = selectedDialogs2.size();
            int a = 0;
            while (a < count) {
                TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(selectedDialogs2.get(a).longValue());
                if (dialog == null) {
                    selectedDialogs = selectedDialogs2;
                } else {
                    long selectedDialog = dialog.id;
                    boolean pinned = dialog.pinned;
                    boolean hasUnread = dialog.unread_count != 0 || dialog.unread_mark;
                    if (getMessagesController().isDialogMuted(selectedDialog)) {
                        this.canUnmuteCount++;
                    } else {
                        this.canMuteCount++;
                    }
                    if (hasUnread) {
                        this.canReadCount++;
                    }
                    if (this.folderId == 1) {
                        canUnarchiveCount = high_id + 1;
                    } else {
                        int canUnarchiveCount2 = high_id;
                        if (selectedDialog == ((long) getUserConfig().getClientUserId()) || selectedDialog == 777000 || getMessagesController().isProxyDialog(selectedDialog, false)) {
                            canUnarchiveCount = canUnarchiveCount2;
                        } else {
                            canArchiveCount++;
                            canUnarchiveCount = canUnarchiveCount2;
                        }
                    }
                    int lower_id = (int) selectedDialog;
                    int canArchiveCount2 = canArchiveCount;
                    int canUnarchiveCount3 = canUnarchiveCount;
                    int high_id2 = (int) (selectedDialog >> 32);
                    if (DialogObject.isChannel(dialog)) {
                        TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-lower_id));
                        long j = selectedDialog;
                        selectedDialogs = selectedDialogs2;
                        if (getMessagesController().isProxyDialog(dialog.id, true)) {
                            this.canClearCacheCount++;
                        } else {
                            if (pinned) {
                                canUnpinCount++;
                            } else {
                                this.canPinCount++;
                            }
                            if (chat == null || !chat.megagroup) {
                                this.canClearCacheCount++;
                                canDeleteCount++;
                            } else {
                                if (TextUtils.isEmpty(chat.username)) {
                                    canClearHistoryCount++;
                                } else {
                                    this.canClearCacheCount++;
                                }
                                canDeleteCount++;
                            }
                        }
                        canArchiveCount = canArchiveCount2;
                        high_id = canUnarchiveCount3;
                    } else {
                        selectedDialogs = selectedDialogs2;
                        long j2 = selectedDialog;
                        boolean isChat = lower_id < 0 && high_id2 != 1;
                        TLRPC.User user2 = null;
                        if (isChat) {
                            TLRPC.Chat chat2 = getMessagesController().getChat(Integer.valueOf(-lower_id));
                        }
                        if (lower_id == 0) {
                            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(high_id2));
                            if (encryptedChat != null) {
                                user = getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id));
                            } else {
                                user = new TLRPC.TL_userEmpty();
                            }
                        } else {
                            if (!isChat && lower_id > 0 && high_id2 != 1) {
                                user2 = getMessagesController().getUser(Integer.valueOf(lower_id));
                            }
                            user = user2;
                        }
                        if (user == null || !user.bot || MessagesController.isSupportUser(user)) {
                        }
                        if (pinned) {
                            canUnpinCount++;
                        } else {
                            this.canPinCount++;
                        }
                        canClearHistoryCount++;
                        canDeleteCount++;
                        canArchiveCount = canArchiveCount2;
                        high_id = canUnarchiveCount3;
                    }
                }
                a++;
                selectedDialogs2 = selectedDialogs;
            }
            int i = high_id;
            ArrayList<Long> arrayList = selectedDialogs2;
            if (canDeleteCount != count) {
                this.deleteItem.setVisibility(8);
            } else {
                this.deleteItem.setVisibility(0);
            }
            int i2 = this.canClearCacheCount;
            if ((i2 == 0 || i2 == count) && (canClearHistoryCount == 0 || canClearHistoryCount == count)) {
                this.clearItem.setVisibility(0);
                if (this.canClearCacheCount != 0) {
                    this.clearItem.setText(LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache));
                } else {
                    this.clearItem.setText(LocaleController.getString("ClearHistory", R.string.ClearHistory));
                }
            } else {
                this.clearItem.setVisibility(8);
            }
            if (this.canPinCount + canUnpinCount != count) {
                this.pinItem.setVisibility(8);
            } else {
                this.pinItem.setVisibility(0);
            }
            if (this.canUnmuteCount != 0) {
                this.muteItem.setIcon((int) R.drawable.msg_unmute);
                this.muteItem.setContentDescription(LocaleController.getString("ChatsUnmute", R.string.ChatsUnmute));
            } else {
                this.muteItem.setIcon((int) R.drawable.msg_mute);
                this.muteItem.setContentDescription(LocaleController.getString("ChatsMute", R.string.ChatsMute));
            }
            if (this.canReadCount != 0) {
                this.readItem.setTextAndIcon(LocaleController.getString("MarkAsRead", R.string.MarkAsRead), R.drawable.msg_markread);
            } else {
                this.readItem.setTextAndIcon(LocaleController.getString("MarkAsUnread", R.string.MarkAsUnread), R.drawable.msg_markunread);
            }
            if (this.canPinCount != 0) {
                this.pinItem.setIcon((int) R.drawable.msg_pin);
                this.pinItem.setContentDescription(LocaleController.getString("PinToTop", R.string.PinToTop));
                return;
            }
            this.pinItem.setIcon((int) R.drawable.msg_unpin);
            this.pinItem.setContentDescription(LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop));
        }
    }

    /* access modifiers changed from: private */
    public boolean validateSlowModeDialog(long dialogId) {
        int lowerId;
        TLRPC.Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount <= 1 && ((chatActivityEnterView = this.commentView) == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText()))) || (lowerId = (int) dialogId) >= 0 || (chat = getMessagesController().getChat(Integer.valueOf(-lowerId))) == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError));
        return false;
    }

    /* access modifiers changed from: private */
    public void showOrUpdateActionMode(TLRPC.Dialog dialog, View cell) {
        this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.id, cell);
        ArrayList<Long> selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
        boolean updateAnimated = false;
        if (!this.actionBar.isActionModeShowed()) {
            ActionBarMenu createActionMode = this.actionBar.createActionMode();
            this.actionBar.showActionMode();
            if (!Theme.getCurrentTheme().isDark()) {
                this.actionBar.setBackButtonImage(R.drawable.back_black);
            }
            this.dialogsAdapter.setEdit(true);
            this.dialogsAdapter.notifyDataSetChanged();
            if (this.menuDrawable != null) {
                this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
            }
            if (getPinnedCount() > 1) {
                this.dialogsAdapter.onReorderStateChanged(true);
                updateVisibleRows(131072);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            for (int a = 0; a < this.actionModeViews.size(); a++) {
                View view = this.actionModeViews.get(a);
                view.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                AndroidUtilities.clearDrawableAnimation(view);
                animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.1f, 1.0f}));
            }
            animatorSet.playTogether(animators);
            animatorSet.setDuration(250);
            animatorSet.start();
            MenuDrawable menuDrawable2 = this.menuDrawable;
            if (menuDrawable2 != null) {
                menuDrawable2.setRotateToBack(false);
                this.menuDrawable.setRotation(1.0f, true);
            } else {
                BackDrawable backDrawable2 = this.backDrawable;
                if (backDrawable2 != null) {
                    backDrawable2.setRotation(1.0f, true);
                }
            }
        } else if (selectedDialogs.isEmpty()) {
            hideActionMode(true);
            return;
        } else {
            updateAnimated = true;
        }
        updateCounters(false);
        this.selectedDialogsCountTextView.setNumber(selectedDialogs.size(), updateAnimated);
    }

    /* access modifiers changed from: private */
    public void closeSearch() {
        if (AndroidUtilities.isTablet()) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                this.searchObject = null;
                return;
            }
            return;
        }
        this.closeSearchFieldOnHide = true;
    }

    /* access modifiers changed from: private */
    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            FrameLayout contentView = (FrameLayout) this.fragmentView;
            contentView.removeView(this.undoView[0]);
            contentView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    private void updateProxyButton(boolean animated) {
        if (this.proxyDrawable != null) {
            boolean z = false;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            boolean z2 = preferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(preferences.getString("proxy_ip", ""));
            boolean proxyEnabled = z2;
            if (z2 || (getMessagesController().blockedCountry && !SharedConfig.proxyList.isEmpty())) {
                if (!this.actionBar.isSearchFieldVisible()) {
                    this.proxyItem.setVisibility(0);
                }
                ProxyDrawable proxyDrawable2 = this.proxyDrawable;
                int i = this.currentConnectionState;
                if (i == 3 || i == 5) {
                    z = true;
                }
                proxyDrawable2.setConnected(proxyEnabled, z, animated);
                this.proxyItemVisisble = true;
                return;
            }
            this.proxyItem.setVisibility(8);
            this.proxyItemVisisble = false;
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedCount() {
        if (this.commentView != null) {
            if (!this.dialogsAdapter.hasSelectedDialogs()) {
                if (this.dialogsType == 3 && this.selectAlertString == null) {
                    this.actionBar.setTitle(LocaleController.getString("ForwardTo", R.string.ForwardTo));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
                }
                if (this.commentView.getTag() != null) {
                    this.commentView.hidePopup(false);
                    this.commentView.closeKeyboard();
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{0.0f, (float) this.commentView.getMeasuredHeight()})});
                    animatorSet.setDuration(180);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            MryDialogsActivity.this.commentView.setVisibility(8);
                        }
                    });
                    animatorSet.start();
                    this.commentView.setTag((Object) null);
                    this.listView.requestLayout();
                    return;
                }
                return;
            }
            if (this.commentView.getTag() == null) {
                this.commentView.setFieldText("");
                this.commentView.setVisibility(0);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{(float) this.commentView.getMeasuredHeight(), 0.0f})});
                animatorSet2.setDuration(180);
                animatorSet2.setInterpolator(new DecelerateInterpolator());
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        MryDialogsActivity.this.commentView.setTag(2);
                        MryDialogsActivity.this.commentView.requestLayout();
                    }
                });
                animatorSet2.start();
                this.commentView.setTag(1);
            }
            this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.dialogsAdapter.getSelectedDialogs().size()));
        }
    }

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null) {
            ArrayList<String> permissons = new ArrayList<>();
            if (activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                permissons.add(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE);
                permissons.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            if (!permissons.isEmpty()) {
                try {
                    activity.requestPermissions((String[]) permissons.toArray(new String[0]), 1);
                } catch (Exception e) {
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        AlertDialog alertDialog = this.permissionDialog;
        if (alertDialog != null && dialog == alertDialog && getParentActivity() != null) {
            askForPermissons(false);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a) {
                    String str = permissions[a];
                    char c = 65535;
                    if (str.hashCode() == 1365911975 && str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        c = 0;
                    }
                    if (c == 0 && grantResults[a] == 0) {
                        ImageLoader.getInstance().checkMediaPaths();
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:128:0x01e0, code lost:
        r0 = ((java.lang.Integer) r9[0]).intValue();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r17, int r18, java.lang.Object... r19) {
        /*
            r16 = this;
            r7 = r16
            r8 = r17
            r9 = r19
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.dialogsNeedReload
            if (r8 != r0) goto L_0x006e
            boolean r0 = r7.dialogsListFrozen
            if (r0 == 0) goto L_0x000f
            return
        L_0x000f:
            im.bclpbkiauv.ui.hui.adapter.MyDialogsAdapter r0 = r7.dialogsAdapter
            if (r0 == 0) goto L_0x0028
            boolean r0 = r0.isDataSetChanged()
            if (r0 != 0) goto L_0x0023
            int r0 = r9.length
            if (r0 <= 0) goto L_0x001d
            goto L_0x0023
        L_0x001d:
            r0 = 2048(0x800, float:2.87E-42)
            r7.updateVisibleRows(r0)
            goto L_0x0028
        L_0x0023:
            im.bclpbkiauv.ui.hui.adapter.MyDialogsAdapter r0 = r7.dialogsAdapter
            r0.notifyDataSetChanged()
        L_0x0028:
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r7.listView
            if (r0 == 0) goto L_0x01f8
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()     // Catch:{ Exception -> 0x0068 }
            im.bclpbkiauv.ui.hui.adapter.MyDialogsAdapter r1 = r7.dialogsAdapter     // Catch:{ Exception -> 0x0068 }
            r2 = 0
            r3 = 8
            if (r0 != r1) goto L_0x0048
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r7.searchEmptyView     // Catch:{ Exception -> 0x0068 }
            r0.setVisibility(r3)     // Catch:{ Exception -> 0x0068 }
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r7.listView     // Catch:{ Exception -> 0x0068 }
            int r1 = r7.folderId     // Catch:{ Exception -> 0x0068 }
            if (r1 != 0) goto L_0x0044
            im.bclpbkiauv.ui.components.RadialProgressView r2 = r7.progressView     // Catch:{ Exception -> 0x0068 }
        L_0x0044:
            r0.setEmptyView(r2)     // Catch:{ Exception -> 0x0068 }
            goto L_0x006c
        L_0x0048:
            boolean r0 = r7.searching     // Catch:{ Exception -> 0x0068 }
            if (r0 == 0) goto L_0x0058
            boolean r0 = r7.searchWas     // Catch:{ Exception -> 0x0068 }
            if (r0 == 0) goto L_0x0058
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r7.listView     // Catch:{ Exception -> 0x0068 }
            im.bclpbkiauv.ui.components.EmptyTextProgressView r1 = r7.searchEmptyView     // Catch:{ Exception -> 0x0068 }
            r0.setEmptyView(r1)     // Catch:{ Exception -> 0x0068 }
            goto L_0x0062
        L_0x0058:
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r7.searchEmptyView     // Catch:{ Exception -> 0x0068 }
            r0.setVisibility(r3)     // Catch:{ Exception -> 0x0068 }
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r7.listView     // Catch:{ Exception -> 0x0068 }
            r0.setEmptyView(r2)     // Catch:{ Exception -> 0x0068 }
        L_0x0062:
            im.bclpbkiauv.ui.components.RadialProgressView r0 = r7.progressView     // Catch:{ Exception -> 0x0068 }
            r0.setVisibility(r3)     // Catch:{ Exception -> 0x0068 }
            goto L_0x006c
        L_0x0068:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x006c:
            goto L_0x01f8
        L_0x006e:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.emojiDidLoad
            r10 = 0
            if (r8 != r0) goto L_0x0078
            r7.updateVisibleRows(r10)
            goto L_0x01f8
        L_0x0078:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.closeSearchByActiveAction
            if (r8 != r0) goto L_0x0087
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r7.actionBar
            if (r0 == 0) goto L_0x01f8
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r7.actionBar
            r0.closeSearchField()
            goto L_0x01f8
        L_0x0087:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.proxySettingsChanged
            if (r8 != r0) goto L_0x0090
            r7.updateProxyButton(r10)
            goto L_0x01f8
        L_0x0090:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.updateInterfaces
            r11 = 1
            if (r8 != r0) goto L_0x00b1
            r0 = r9[r10]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r1 = r0.intValue()
            r7.updateVisibleRows(r1)
            int r1 = r0.intValue()
            r1 = r1 & 4
            if (r1 == 0) goto L_0x00af
            im.bclpbkiauv.ui.hui.adapter.MyDialogsAdapter r1 = r7.dialogsAdapter
            if (r1 == 0) goto L_0x00af
            r1.sortOnlineContacts(r11)
        L_0x00af:
            goto L_0x01f8
        L_0x00b1:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.appDidLogout
            if (r8 != r0) goto L_0x00bd
            boolean[] r0 = dialogsLoaded
            int r1 = r7.currentAccount
            r0[r1] = r10
            goto L_0x01f8
        L_0x00bd:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.encryptedChatUpdated
            if (r8 != r0) goto L_0x00c6
            r7.updateVisibleRows(r10)
            goto L_0x01f8
        L_0x00c6:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.contactsDidLoad
            if (r8 != r0) goto L_0x00f1
            boolean r0 = r7.dialogsListFrozen
            if (r0 == 0) goto L_0x00cf
            return
        L_0x00cf:
            int r0 = r7.dialogsType
            if (r0 != 0) goto L_0x00ec
            im.bclpbkiauv.messenger.MessagesController r0 = r16.getMessagesController()
            int r1 = r7.folderId
            java.util.ArrayList r0 = r0.getDialogs(r1)
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x00ec
            im.bclpbkiauv.ui.hui.adapter.MyDialogsAdapter r0 = r7.dialogsAdapter
            if (r0 == 0) goto L_0x01f8
            r0.notifyDataSetChanged()
            goto L_0x01f8
        L_0x00ec:
            r7.updateVisibleRows(r10)
            goto L_0x01f8
        L_0x00f1:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.openedChatChanged
            if (r8 != r0) goto L_0x012e
            int r0 = r7.dialogsType
            if (r0 != 0) goto L_0x01f8
            boolean r0 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x01f8
            r0 = r9[r11]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            r1 = r9[r10]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            if (r0 == 0) goto L_0x011c
            long r3 = r7.openedDialogId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x011e
            r3 = 0
            r7.openedDialogId = r3
            goto L_0x011e
        L_0x011c:
            r7.openedDialogId = r1
        L_0x011e:
            im.bclpbkiauv.ui.hui.adapter.MyDialogsAdapter r3 = r7.dialogsAdapter
            if (r3 == 0) goto L_0x0127
            long r4 = r7.openedDialogId
            r3.setOpenedDialogId(r4)
        L_0x0127:
            r3 = 512(0x200, float:7.175E-43)
            r7.updateVisibleRows(r3)
            goto L_0x01f8
        L_0x012e:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.notificationsSettingsUpdated
            if (r8 != r0) goto L_0x0137
            r7.updateVisibleRows(r10)
            goto L_0x01f8
        L_0x0137:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messageReceivedByAck
            if (r8 == r0) goto L_0x01f3
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messageReceivedByServer
            if (r8 == r0) goto L_0x01f3
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.messageSendError
            if (r8 != r0) goto L_0x0145
            goto L_0x01f3
        L_0x0145:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.didSetPasscode
            if (r8 != r0) goto L_0x014e
            r16.updatePasscodeButton()
            goto L_0x01f8
        L_0x014e:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.needReloadRecentDialogsSearch
            if (r8 != r0) goto L_0x015b
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter r0 = r7.dialogsSearchAdapter
            if (r0 == 0) goto L_0x01f8
            r0.loadRecentSearch()
            goto L_0x01f8
        L_0x015b:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.replyMessagesDidLoad
            if (r8 != r0) goto L_0x0167
            r0 = 32768(0x8000, float:4.5918E-41)
            r7.updateVisibleRows(r0)
            goto L_0x01f8
        L_0x0167:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.reloadHints
            if (r8 != r0) goto L_0x0174
            im.bclpbkiauv.ui.adapters.DialogsSearchAdapter r0 = r7.dialogsSearchAdapter
            if (r0 == 0) goto L_0x01f8
            r0.notifyDataSetChanged()
            goto L_0x01f8
        L_0x0174:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.didUpdateConnectionState
            if (r8 != r0) goto L_0x018e
            im.bclpbkiauv.messenger.AccountInstance r0 = im.bclpbkiauv.messenger.AccountInstance.getInstance(r18)
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = r0.getConnectionsManager()
            int r0 = r0.getConnectionState()
            int r1 = r7.currentConnectionState
            if (r1 == r0) goto L_0x0192
            r7.currentConnectionState = r0
            r7.updateProxyButton(r11)
            goto L_0x0192
        L_0x018e:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.dialogsUnreadCounterChanged
            if (r8 != r0) goto L_0x0194
        L_0x0192:
            goto L_0x01f8
        L_0x0194:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.needDeleteDialog
            if (r8 != r0) goto L_0x01dc
            android.view.View r0 = r7.fragmentView
            if (r0 == 0) goto L_0x01db
            boolean r0 = r7.isPaused
            if (r0 == 0) goto L_0x01a1
            goto L_0x01db
        L_0x01a1:
            r0 = r9[r10]
            java.lang.Long r0 = (java.lang.Long) r0
            long r12 = r0.longValue()
            r0 = r9[r11]
            im.bclpbkiauv.tgnet.TLRPC$User r0 = (im.bclpbkiauv.tgnet.TLRPC.User) r0
            r1 = 2
            r1 = r9[r1]
            r14 = r1
            im.bclpbkiauv.tgnet.TLRPC$Chat r14 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r14
            r1 = 3
            r1 = r9[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r15 = r1.booleanValue()
            im.bclpbkiauv.ui.hui.chats.-$$Lambda$MryDialogsActivity$MHptfK4MtsIuqEfrdfZxE31uzKA r6 = new im.bclpbkiauv.ui.hui.chats.-$$Lambda$MryDialogsActivity$MHptfK4MtsIuqEfrdfZxE31uzKA
            r1 = r6
            r2 = r16
            r3 = r14
            r4 = r12
            r11 = r6
            r6 = r15
            r1.<init>(r3, r4, r6)
            im.bclpbkiauv.ui.components.UndoView[] r1 = r7.undoView
            r1 = r1[r10]
            if (r1 == 0) goto L_0x01d7
            im.bclpbkiauv.ui.components.UndoView r1 = r16.getUndoView()
            r2 = 1
            r1.showWithAction((long) r12, (int) r2, (java.lang.Runnable) r11)
            goto L_0x01f2
        L_0x01d7:
            r11.run()
            goto L_0x01f2
        L_0x01db:
            return
        L_0x01dc:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.folderBecomeEmpty
            if (r8 != r0) goto L_0x01f2
            r0 = r9[r10]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            int r1 = r7.folderId
            if (r1 != r0) goto L_0x01f8
            if (r1 == 0) goto L_0x01f8
            r16.finishFragment()
            goto L_0x01f8
        L_0x01f2:
            goto L_0x01f8
        L_0x01f3:
            r0 = 4096(0x1000, float:5.74E-42)
            r7.updateVisibleRows(r0)
        L_0x01f8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.chats.MryDialogsActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public /* synthetic */ void lambda$didReceivedNotification$14$MryDialogsActivity(TLRPC.Chat chat, long dialogId, boolean revoke) {
        if (chat == null) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else {
            getMessagesController().deleteUserFromChat((int) (-dialogId), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null, false, revoke);
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    /* access modifiers changed from: private */
    public void setDialogsListFrozen(boolean frozen) {
        if (this.dialogsListFrozen != frozen) {
            if (frozen) {
                frozenDialogsList = new ArrayList<>(getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false));
            } else {
                frozenDialogsList = null;
            }
            this.dialogsListFrozen = frozen;
            this.dialogsAdapter.setDialogsListFrozen(frozen);
            if (!frozen) {
                this.dialogsAdapter.notifyDataSetChanged();
            }
        }
    }

    public static ArrayList<TLRPC.Dialog> getDialogsArray(int currentAccount, int dialogsType2, int folderId2, boolean frozen) {
        ArrayList<TLRPC.Dialog> arrayList;
        if (frozen && (arrayList = frozenDialogsList) != null) {
            return arrayList;
        }
        MessagesController messagesController = AccountInstance.getInstance(currentAccount).getMessagesController();
        if (dialogsType2 == 0) {
            return messagesController.getDialogs(folderId2);
        }
        if (dialogsType2 == 1) {
            return messagesController.dialogsServerOnly;
        }
        if (dialogsType2 == 2) {
            return messagesController.dialogsCanAddUsers;
        }
        if (dialogsType2 == 3) {
            return messagesController.dialogsForward;
        }
        if (dialogsType2 == 4) {
            return messagesController.dialogsUsersOnly;
        }
        if (dialogsType2 == 5) {
            return messagesController.dialogsChannelsOnly;
        }
        if (dialogsType2 == 6) {
            return messagesController.dialogsGroupsOnly;
        }
        return null;
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        recyclerView.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
        this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
    }

    /* access modifiers changed from: private */
    public void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (SharedConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                return;
            }
            this.passcodeItem.setVisibility(0);
            if (SharedConfig.appLocked) {
                this.passcodeItem.setIcon((int) R.drawable.lock_close);
                this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeUnlock", R.string.AccDescrPasscodeUnlock));
                return;
            }
            this.passcodeItem.setIcon((int) R.drawable.lock_open);
            this.passcodeItem.setContentDescription(LocaleController.getString("AccDescrPasscodeLock", R.string.AccDescrPasscodeLock));
        }
    }

    /* access modifiers changed from: private */
    public void updateDialogIndices() {
        int index;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && recyclerListView.getAdapter() == this.dialogsAdapter) {
            ArrayList<TLRPC.Dialog> dialogs = getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof DialogCell) {
                    DialogCell dialogCell = (DialogCell) child;
                    TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogCell.getDialogId());
                    if (dialog != null && (index = dialogs.indexOf(dialog)) >= 0) {
                        dialogCell.setDialogIndex(index);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVisibleRows(int mask) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.dialogsListFrozen) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof DialogCell) {
                    if (this.listView.getAdapter() != this.dialogsSearchAdapter) {
                        DialogCell cell = (DialogCell) child;
                        boolean z = true;
                        if ((131072 & mask) != 0) {
                            cell.onReorderStateChanged(this.actionBar.isActionModeShowed(), true);
                        }
                        if ((65536 & mask) != 0) {
                            if ((mask & 8192) == 0) {
                                z = false;
                            }
                            cell.setChecked(false, z);
                        } else {
                            if ((mask & 2048) != 0) {
                                cell.checkCurrentDialogIndex(this.dialogsListFrozen);
                                if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                    if (cell.getDialogId() != this.openedDialogId) {
                                        z = false;
                                    }
                                    cell.setDialogSelected(z);
                                }
                            } else if ((mask & 512) == 0) {
                                cell.update(mask);
                            } else if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                if (cell.getDialogId() != this.openedDialogId) {
                                    z = false;
                                }
                                cell.setDialogSelected(z);
                            }
                            ArrayList<Long> selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
                            if (selectedDialogs != null) {
                                cell.setChecked(selectedDialogs.contains(Long.valueOf(cell.getDialogId())), false);
                            }
                        }
                    }
                } else if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(mask);
                } else if (child instanceof RecyclerListView) {
                    RecyclerListView innerListView = (RecyclerListView) child;
                    int count2 = innerListView.getChildCount();
                    for (int b = 0; b < count2; b++) {
                        View child2 = innerListView.getChildAt(b);
                        if (child2 instanceof HintDialogCell) {
                            ((HintDialogCell) child2).update(mask);
                        }
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        this.delegate = dialogsActivityDelegate;
    }

    public void setSearchString(String string) {
        this.searchString = string;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    /* access modifiers changed from: private */
    public void didSelectResult(long dialog_id, boolean useAlert, boolean param) {
        long j = dialog_id;
        if (this.addToGroupAlertString == null && this.checkCanWrite && ((int) j) < 0) {
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-((int) j)));
            if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(-((int) j), this.currentAccount))) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
                return;
            }
        }
        if (!useAlert || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
            if (this.delegate != null) {
                ArrayList<Long> dids = new ArrayList<>();
                dids.add(Long.valueOf(dialog_id));
                this.delegate.didSelectDialogs(this, dids, (CharSequence) null, param);
                if (this.resetDelegate) {
                    this.delegate = null;
                    return;
                }
                return;
            }
            boolean z = param;
            finishFragment();
        } else if (getParentActivity() != null) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            int lower_part = (int) j;
            int high_id = (int) (j >> 32);
            if (lower_part == 0) {
                TLRPC.User user = getMessagesController().getUser(Integer.valueOf(getMessagesController().getEncryptedChat(Integer.valueOf(high_id)).user_id));
                if (user != null) {
                    builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getName(user)));
                } else {
                    return;
                }
            } else if (lower_part == getUserConfig().getClientUserId()) {
                builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", R.string.SavedMessages)));
            } else if (lower_part > 0) {
                TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(lower_part));
                if (user2 != null) {
                    builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getName(user2)));
                } else {
                    return;
                }
            } else if (lower_part < 0) {
                TLRPC.Chat chat2 = getMessagesController().getChat(Integer.valueOf(-lower_part));
                if (chat2 != null) {
                    String str = this.addToGroupAlertString;
                    if (str != null) {
                        builder2.setMessage(LocaleController.formatStringSimple(str, chat2.title));
                    } else {
                        builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat2.title));
                    }
                } else {
                    return;
                }
            }
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(j) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    MryDialogsActivity.this.lambda$didSelectResult$15$MryDialogsActivity(this.f$1, dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
            boolean z2 = param;
        }
    }

    public /* synthetic */ void lambda$didSelectResult$15$MryDialogsActivity(long dialog_id, DialogInterface dialogInterface, int i) {
        didSelectResult(dialog_id, false, false);
    }

    public void hideTitle(View rootView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", new float[]{0.0f, (float) (-ActionBar.getCurrentActionBarHeight())});
        animator.setDuration(300);
        animator.start();
        this.actionBar.setVisibility(4);
    }

    public void showTitle(View rootView) {
        ObjectAnimator.ofFloat(rootView, "translationY", new float[]{(float) (-ActionBar.getCurrentActionBarHeight()), 0.0f}).start();
        this.actionBar.setVisibility(0);
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                MryDialogsActivity.this.lambda$getThemeDescriptions$16$MryDialogsActivity();
            }
        };
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
        DialogCell dialogCell = this.movingView;
        if (dialogCell != null) {
            arrayList.add(new ThemeDescription(dialogCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite));
        }
        if (this.folderId == 0) {
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, new Drawable[]{Theme.dialogs_holidayDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearch));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSearchPlaceholder));
        } else {
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultArchived));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultArchived));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultArchivedIcon));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, new Drawable[]{Theme.dialogs_holidayDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultArchivedTitle));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultArchivedSelector));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultArchivedSearch));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultArchivedSearchPlaceholder));
        }
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefaultTop));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefaultSelector));
        arrayList.add(new ThemeDescription(this.selectedDialogsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefaultIcon));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameMessage_threeLines));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundSaved));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundArchived));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundArchivedHidden));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounter));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounterMuted));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounterText));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint, Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint, Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_lockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_scamDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_draft));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_pinnedIcon));
        if (SharedConfig.useThreeLinesLayout) {
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message_threeLines));
        } else {
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message));
        }
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameMessage_threeLines));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_draft));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_chats_nameMessage));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_chats_draft));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_chats_attachMessage));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_chats_nameArchived));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_chats_nameMessageArchived));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_chats_nameMessageArchived_threeLines));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_chats_messageArchived));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionMessage));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_date));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_pinnedOverlay));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_tabletSelectedOverlay));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_sentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_sentReadCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_clockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_sentClock));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_sentError));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_errorDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_sentErrorIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_verifiedCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_verifiedBackground));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_muteDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_muteIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_mentionDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_mentionIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_archivePinBackground));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_archiveBackground));
        if (SharedConfig.archiveHidden) {
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", Theme.key_avatar_backgroundArchivedHidden));
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", Theme.key_avatar_backgroundArchivedHidden));
        } else {
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", Theme.key_avatar_backgroundArchived));
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", Theme.key_avatar_backgroundArchived));
        }
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box2", Theme.key_avatar_text));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box1", Theme.key_avatar_text));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Arrow", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Line", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Arrow", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Line", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Arrow", Theme.key_chats_archiveBackground));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box2", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box1", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow1", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow2", Theme.key_chats_archivePinBackground));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box2", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box1", Theme.key_chats_archiveIcon));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuBackground));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuName));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuPhone));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuPhoneCats));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuCloudBackgroundCats));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_serviceBackground));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuTopShadow));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuTopShadowCats));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate3 = cellDelegate;
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, Theme.key_chats_menuTopBackgroundCats));
        arrayList.add(new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, Theme.key_chats_menuTopBackground));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuItemIcon));
        arrayList.add(new ThemeDescription((View) this.sideMenu, 0, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuItemText));
        arrayList.add(new ThemeDescription((View) this.sideMenu, 0, new Class[]{DrawerUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuItemText));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounterText));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounter));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuBackground));
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuItemIcon));
        arrayList.add(new ThemeDescription((View) this.sideMenu, 0, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_menuItemText));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText3));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        MyDialogsAdapter myDialogsAdapter = this.dialogsAdapter;
        RecyclerListView recyclerListView = null;
        arrayList.add(new ThemeDescription((View) myDialogsAdapter != null ? myDialogsAdapter.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameMessage_threeLines));
        MyDialogsAdapter myDialogsAdapter2 = this.dialogsAdapter;
        arrayList.add(new ThemeDescription((View) myDialogsAdapter2 != null ? myDialogsAdapter2.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounter));
        MyDialogsAdapter myDialogsAdapter3 = this.dialogsAdapter;
        arrayList.add(new ThemeDescription((View) myDialogsAdapter3 != null ? myDialogsAdapter3.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameMessage_threeLines));
        MyDialogsAdapter myDialogsAdapter4 = this.dialogsAdapter;
        arrayList.add(new ThemeDescription((View) myDialogsAdapter4 != null ? myDialogsAdapter4.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message));
        MyDialogsAdapter myDialogsAdapter5 = this.dialogsAdapter;
        arrayList.add(new ThemeDescription(myDialogsAdapter5 != null ? myDialogsAdapter5.getArchiveHintCellPager() : null, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultArchived));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray));
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter2 != null ? dialogsSearchAdapter2.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounter));
        DialogsSearchAdapter dialogsSearchAdapter3 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter3 != null ? dialogsSearchAdapter3.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounterMuted));
        DialogsSearchAdapter dialogsSearchAdapter4 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter4 != null ? dialogsSearchAdapter4.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounterText));
        DialogsSearchAdapter dialogsSearchAdapter5 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter5 != null ? dialogsSearchAdapter5.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_archiveTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_archiveText));
        DialogsSearchAdapter dialogsSearchAdapter6 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription((View) dialogsSearchAdapter6 != null ? dialogsSearchAdapter6.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        DialogsSearchAdapter dialogsSearchAdapter7 = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter7 != null) {
            recyclerListView = dialogsSearchAdapter7.getInnerListView();
        }
        arrayList.add(new ThemeDescription(recyclerListView, 0, new Class[]{HintDialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_onlineCircle));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerBackground));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPlayPause));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerTitle));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPerformer));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerClose));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallBackground));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallText));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
        int a = 0;
        while (true) {
            UndoView[] undoViewArr = this.undoView;
            if (a < undoViewArr.length) {
                arrayList.add(new ThemeDescription(undoViewArr[a], ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_background));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info1", Theme.key_undo_background));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info2", Theme.key_undo_background));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc12", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc11", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc10", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc9", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc8", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc7", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc6", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc5", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc4", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc3", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc2", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc1", Theme.key_undo_infoColor));
                arrayList.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Oval", Theme.key_undo_infoColor));
                a++;
            } else {
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBackgroundGray));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextLink));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogLinkSelection));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlue));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlue2));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlue3));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlue4));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextRed));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextRed2));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextGray));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextGray2));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextGray3));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextGray4));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogIcon));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogRedIcon));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextHint));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogInputField));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogInputFieldActivated));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogCheckboxSquareBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogCheckboxSquareCheck));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogCheckboxSquareUnchecked));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogCheckboxSquareDisabled));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogRadioBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogRadioBackgroundChecked));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogProgressCircle));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogButton));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogButtonSelector));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogScrollGlow));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogRoundCheckBox));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogRoundCheckBoxCheck));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBadgeBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBadgeText));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogLineProgress));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogLineProgressBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogGrayLine));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialog_inlineProgressBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialog_inlineProgress));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogSearchBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogSearchHint));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogSearchIcon));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogSearchText));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogFloatingButton));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogFloatingIcon));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogShadowLine));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sheet_scrollUp));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sheet_other));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBar));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarSelector));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarTitle));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarTop));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarSubtitle));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarItems));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_background));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_time));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressCachedBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progress));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_placeholder));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_placeholderBackground));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_button));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_buttonActive));
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
        }
    }

    public /* synthetic */ void lambda$getThemeDescriptions$16$MryDialogsActivity() {
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2 = this.listView;
        if (recyclerListView2 != null) {
            int count = recyclerListView2.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                } else if (child instanceof DialogCell) {
                    ((DialogCell) child).update(0);
                }
            }
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        if (!(dialogsSearchAdapter2 == null || (recyclerListView = dialogsSearchAdapter2.getInnerListView()) == null)) {
            int count2 = recyclerListView.getChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                View child2 = recyclerListView.getChildAt(a2);
                if (child2 instanceof HintDialogCell) {
                    ((HintDialogCell) child2).update();
                }
            }
        }
        RecyclerView recyclerView = this.sideMenu;
        if (recyclerView != null) {
            View child3 = recyclerView.getChildAt(0);
            if (child3 instanceof DrawerProfileCell) {
                ((DrawerProfileCell) child3).applyBackground(true);
            }
        }
    }

    public void onSearchExpand() {
        this.searching = true;
        ActionBarMenuItem actionBarMenuItem = this.switchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(8);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.proxyItem;
        if (actionBarMenuItem2 != null && this.proxyItemVisisble) {
            actionBarMenuItem2.setVisibility(8);
        }
        RecyclerListView recyclerListView = this.listView;
        if (!(recyclerListView == null || this.searchString == null)) {
            recyclerListView.setEmptyView(this.searchEmptyView);
            this.progressView.setVisibility(8);
        }
        updatePasscodeButton();
        this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
    }

    public boolean canCollapseSearch() {
        ActionBarMenuItem actionBarMenuItem = this.switchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(0);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.proxyItem;
        if (actionBarMenuItem2 != null && this.proxyItemVisisble) {
            actionBarMenuItem2.setVisibility(0);
        }
        if (this.searchString != null) {
            return false;
        }
        return true;
    }

    public void onSearchCollapse() {
        this.searching = false;
        this.searchWas = false;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.setEmptyView(this.folderId == 0 ? this.progressView : null);
            this.searchEmptyView.setVisibility(8);
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            MyDialogsAdapter myDialogsAdapter = this.dialogsAdapter;
            if (adapter != myDialogsAdapter) {
                this.listView.setAdapter(myDialogsAdapter);
                this.dialogsAdapter.notifyDataSetChanged();
            }
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter2 != null) {
            dialogsSearchAdapter2.searchDialogs((String) null);
        }
        updatePasscodeButton();
        if (this.menuDrawable != null) {
            this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", R.string.AccDescrOpenMenu));
        }
    }

    public void onTextChange(String text) {
        DialogsSearchAdapter dialogsSearchAdapter2;
        DialogsSearchAdapter dialogsSearchAdapter3;
        if (text.length() != 0 || ((dialogsSearchAdapter3 = this.dialogsSearchAdapter) != null && dialogsSearchAdapter3.hasRecentRearch())) {
            this.searchWas = true;
            if (!(this.dialogsSearchAdapter == null || this.listView.getAdapter() == (dialogsSearchAdapter2 = this.dialogsSearchAdapter))) {
                this.listView.setAdapter(dialogsSearchAdapter2);
                this.dialogsSearchAdapter.notifyDataSetChanged();
            }
            if (!(this.searchEmptyView == null || this.listView.getEmptyView() == this.searchEmptyView)) {
                this.progressView.setVisibility(8);
                this.listView.setEmptyView(this.searchEmptyView);
            }
        }
        DialogsSearchAdapter dialogsSearchAdapter4 = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter4 != null) {
            dialogsSearchAdapter4.searchDialogs(text);
        }
    }
}

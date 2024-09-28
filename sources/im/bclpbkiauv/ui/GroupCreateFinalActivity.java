package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.location.BDLocation;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NewLocationActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.GroupCreateUserCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.EditTextEmoji;
import im.bclpbkiauv.ui.components.GroupCreateDividerItemDecoration;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public class GroupCreateFinalActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    /* access modifiers changed from: private */
    public ImageView avatarEditor;
    private BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public ImageView avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private int chatType;
    private boolean createAfterUpload;
    /* access modifiers changed from: private */
    public String currentGroupCreateAddress;
    private BDLocation currentGroupCreateLocation;
    private GroupCreateFinalActivityDelegate delegate;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    private boolean donePressed;
    /* access modifiers changed from: private */
    public EditTextEmoji editText;
    /* access modifiers changed from: private */
    public FrameLayout editTextContainer;
    private FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public ImageView floatingButtonIcon;
    private ImageUpdater imageUpdater;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private String nameToSet;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    private int reqId;
    /* access modifiers changed from: private */
    public ArrayList<Integer> selectedContacts;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private TLRPC.InputFile uploadedAvatar;

    public interface GroupCreateFinalActivityDelegate {
        void didFailChatCreation();

        void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity, int i);

        void didStartChatCreation();
    }

    public /* synthetic */ void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public GroupCreateFinalActivity(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 0);
        this.currentGroupCreateAddress = args.getString("address");
        this.currentGroupCreateLocation = (BDLocation) args.getParcelable("location");
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        ImageUpdater imageUpdater2 = new ImageUpdater();
        this.imageUpdater = imageUpdater2;
        imageUpdater2.parentFragment = this;
        this.imageUpdater.delegate = this;
        this.selectedContacts = getArguments().getIntegerArrayList("result");
        ArrayList<Integer> usersToLoad = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            Integer uid = this.selectedContacts.get(a);
            if (MessagesController.getInstance(this.currentAccount).getUser(uid) == null) {
                usersToLoad.add(uid);
            }
        }
        if (usersToLoad.isEmpty() == 0) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(users, usersToLoad, countDownLatch) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ CountDownLatch f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    GroupCreateFinalActivity.this.lambda$onFragmentCreate$0$GroupCreateFinalActivity(this.f$1, this.f$2, this.f$3);
                }
            });
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (usersToLoad.size() != users.size() || users.isEmpty()) {
                return false;
            }
            Iterator<TLRPC.User> it = users.iterator();
            while (it.hasNext()) {
                MessagesController.getInstance(this.currentAccount).putUser(it.next(), true);
            }
        }
        return super.onFragmentCreate();
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$GroupCreateFinalActivity(ArrayList users, ArrayList usersToLoad, CountDownLatch countDownLatch) {
        users.addAll(MessagesStorage.getInstance(this.currentAccount).getUsers(usersToLoad));
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        this.imageUpdater.clear();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
        }
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        GroupCreateAdapter groupCreateAdapter = this.adapter;
        if (groupCreateAdapter != null) {
            groupCreateAdapter.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.editText.hidePopup(true);
        return false;
    }

    public View createView(Context context) {
        String str;
        int i;
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NewGroup", R.string.NewGroup));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupCreateFinalActivity.this.finishFragment();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                int heightSize2 = heightSize - getPaddingTop();
                measureChildWithMargins(GroupCreateFinalActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    GroupCreateFinalActivity.this.editText.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == GroupCreateFinalActivity.this.actionBar)) {
                        if (GroupCreateFinalActivity.this.editText == null || !GroupCreateFinalActivity.this.editText.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, 1073741824));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), 1073741824));
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec((heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), 1073741824));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : GroupCreateFinalActivity.this.editText.getEmojiPadding();
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        int i2 = gravity & 7 & 7;
                        if (i2 == 1) {
                            childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                        } else if (i2 != 5) {
                            childLeft = lp.leftMargin;
                        } else {
                            childLeft = (r - width) - lp.rightMargin;
                        }
                        if (verticalGravity == 16) {
                            childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        } else if (verticalGravity == 48) {
                            childTop = lp.topMargin + getPaddingTop();
                        } else if (verticalGravity != 80) {
                            childTop = lp.topMargin;
                        } else {
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                        }
                        if (GroupCreateFinalActivity.this.editText != null && GroupCreateFinalActivity.this.editText.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + getKeyboardHeight()) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.fragmentView = sizeNotifierFrameLayout;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.fragmentView.setOnTouchListener($$Lambda$GroupCreateFinalActivity$xWERlROaMb3DdfARGWfbkGTwNKY.INSTANCE);
        this.shadowDrawable = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
        LinearLayout r4 = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == GroupCreateFinalActivity.this.listView && GroupCreateFinalActivity.this.shadowDrawable != null) {
                    int y = GroupCreateFinalActivity.this.editTextContainer.getMeasuredHeight();
                    GroupCreateFinalActivity.this.shadowDrawable.setBounds(0, y, getMeasuredWidth(), GroupCreateFinalActivity.this.shadowDrawable.getIntrinsicHeight() + y);
                    GroupCreateFinalActivity.this.shadowDrawable.draw(canvas);
                }
                return result;
            }
        };
        r4.setOrientation(1);
        r4.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        sizeNotifierFrameLayout.addView(r4, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.editTextContainer = frameLayout;
        r4.addView(frameLayout, LayoutHelper.createLinear(-1, 65));
        AnonymousClass4 r8 = new BackupImageView(context2) {
            public void invalidate() {
                if (GroupCreateFinalActivity.this.avatarOverlay != null) {
                    GroupCreateFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            public void invalidate(int l, int t, int r, int b) {
                if (GroupCreateFinalActivity.this.avatarOverlay != null) {
                    GroupCreateFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(l, t, r, b);
            }
        };
        this.avatarImage = r8;
        r8.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        this.avatarImage.setContentDescription(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto));
        int i2 = 3;
        this.editTextContainer.addView(this.avatarImage, LayoutHelper.createFrame(49.0f, 49.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        new Paint(1).setColor(1426063360);
        ImageView imageView = new ImageView(context2);
        this.avatarOverlay = imageView;
        imageView.setImageResource(R.mipmap.ic_create_group_photo);
        this.editTextContainer.addView(this.avatarOverlay, LayoutHelper.createFrame(49.0f, 49.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        this.avatarOverlay.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCreateFinalActivity.this.lambda$createView$3$GroupCreateFinalActivity(view);
            }
        });
        AnonymousClass5 r9 = new ImageView(context2) {
            public void invalidate(int l, int t, int r, int b) {
                super.invalidate(l, t, r, b);
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }

            public void invalidate() {
                super.invalidate();
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarEditor = r9;
        r9.setScaleType(ImageView.ScaleType.CENTER);
        this.avatarEditor.setEnabled(false);
        this.avatarEditor.setClickable(false);
        this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
        this.editTextContainer.addView(this.avatarEditor, LayoutHelper.createFrame(16.0f, 16.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        AnonymousClass6 r92 = new RadialProgressView(context2) {
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarProgressView = r92;
        r92.setSize(AndroidUtilities.dp(30.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.editTextContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(49.0f, 49.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        showAvatarProgress(false, false);
        EditTextEmoji editTextEmoji2 = new EditTextEmoji(context2, sizeNotifierFrameLayout, this, 0);
        this.editText = editTextEmoji2;
        editTextEmoji2.hideEditBackgroup();
        EditTextEmoji editTextEmoji3 = this.editText;
        int i3 = this.chatType;
        if (i3 == 0 || i3 == 4) {
            i = R.string.EnterGroupNamePlaceholder;
            str = "EnterGroupNamePlaceholder";
        } else {
            i = R.string.EnterListName;
            str = "EnterListName";
        }
        editTextEmoji3.setHint(LocaleController.getString(str, i));
        String str2 = this.nameToSet;
        if (str2 != null) {
            this.editText.setText(str2);
            this.nameToSet = null;
        }
        this.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        this.editTextContainer.addView(this.editText, LayoutHelper.createFrame(-1.0f, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context2);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        GroupCreateDividerItemDecoration decoration = new GroupCreateDividerItemDecoration();
        decoration.setSkipRows(this.currentGroupCreateAddress != null ? 5 : 2);
        this.listView.addItemDecoration(decoration);
        r4.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                GroupCreateFinalActivity.this.lambda$createView$5$GroupCreateFinalActivity(view, i);
            }
        });
        this.floatingButtonContainer = new FrameLayout(context2);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable2 = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable2, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackgroundDrawable(drawable);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            AnonymousClass3 r15 = r4;
            Drawable drawable2 = drawable;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButtonContainer.setStateListAnimator(animator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        } else {
            LinearLayout linearLayout = r4;
            Drawable drawable3 = drawable;
        }
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        float f = Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f;
        float f2 = Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f;
        if (!LocaleController.isRTL) {
            i2 = 5;
        }
        sizeNotifierFrameLayout.addView(frameLayout2, LayoutHelper.createFrame(f, f2, i2 | 80, LocaleController.isRTL ? 24.0f : 10.0f, 0.0f, LocaleController.isRTL ? 10.0f : 24.0f, 24.0f));
        this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCreateFinalActivity.this.lambda$createView$6$GroupCreateFinalActivity(view);
            }
        });
        ImageView imageView2 = new ImageView(context2);
        this.floatingButtonIcon = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.floatingButtonIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
        this.floatingButtonIcon.setImageResource(R.drawable.checkbig);
        this.floatingButtonIcon.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString("Done", R.string.Done));
        this.floatingButtonContainer.addView(this.floatingButtonIcon, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.progressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.floatingButtonContainer.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$3$GroupCreateFinalActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                GroupCreateFinalActivity.this.lambda$null$2$GroupCreateFinalActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$GroupCreateFinalActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
    }

    public /* synthetic */ void lambda$createView$5$GroupCreateFinalActivity(View view, int position) {
        if (view instanceof TextSettingsCell) {
            NewLocationActivity fragment = new NewLocationActivity(2, 0);
            fragment.setDelegate(new NewLocationActivity.LocationActivityDelegate() {
                public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                    GroupCreateFinalActivity.this.lambda$null$4$GroupCreateFinalActivity(messageMedia, i, z, i2);
                }
            });
            presentFragment(fragment);
        }
    }

    public /* synthetic */ void lambda$null$4$GroupCreateFinalActivity(TLRPC.MessageMedia location, int locationType, boolean notify, int scheduleDate) {
        this.currentGroupCreateLocation.setLatitude(location.geo.lat);
        this.currentGroupCreateLocation.setLongitude(location.geo._long);
        this.currentGroupCreateAddress = location.address;
    }

    public /* synthetic */ void lambda$createView$6$GroupCreateFinalActivity(View view) {
        if (!this.donePressed) {
            if (this.editText.length() == 0) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(this.editText, 2.0f, 0);
                return;
            }
            this.donePressed = true;
            AndroidUtilities.hideKeyboard(this.editText);
            this.editText.setEnabled(false);
            if (this.imageUpdater.uploadingImage != null) {
                this.createAfterUpload = true;
                return;
            }
            showEditDoneProgress(true);
            this.reqId = MessagesController.getInstance(this.currentAccount).createChat(this.editText.getText().toString(), this.selectedContacts, (String) null, this.chatType, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this);
        }
    }

    public void didUploadPhoto(TLRPC.InputFile file, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new Runnable(file, smallSize, bigSize) {
            private final /* synthetic */ TLRPC.InputFile f$1;
            private final /* synthetic */ TLRPC.PhotoSize f$2;
            private final /* synthetic */ TLRPC.PhotoSize f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                GroupCreateFinalActivity.this.lambda$didUploadPhoto$7$GroupCreateFinalActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$7$GroupCreateFinalActivity(TLRPC.InputFile file, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (file != null) {
            this.uploadedAvatar = file;
            if (this.createAfterUpload) {
                GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate = this.delegate;
                if (groupCreateFinalActivityDelegate != null) {
                    groupCreateFinalActivityDelegate.didStartChatCreation();
                }
                MessagesController.getInstance(this.currentAccount).createChat(this.editText.getText().toString(), this.selectedContacts, (String) null, this.chatType, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this);
            }
            showAvatarProgress(false, true);
            this.avatarEditor.setImageDrawable((Drawable) null);
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.editText.getText().toString();
    }

    public void setDelegate(GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate) {
        this.delegate = groupCreateFinalActivityDelegate;
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarEditor != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (GroupCreateFinalActivity.this.avatarAnimation != null && GroupCreateFinalActivity.this.avatarEditor != null) {
                            if (show) {
                                GroupCreateFinalActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                GroupCreateFinalActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = GroupCreateFinalActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = GroupCreateFinalActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        String text;
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (!(imageUpdater2 == null || imageUpdater2.currentPicturePath == null)) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null && (text = editTextEmoji.getText().toString()) != null && text.length() != 0) {
            args.putString("nameTextView", text);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = args.getString("path");
        }
        String text = args.getString("nameTextView");
        if (text != null) {
            EditTextEmoji editTextEmoji = this.editText;
            if (editTextEmoji != null) {
                editTextEmoji.setText(text);
            } else {
                this.nameToSet = text;
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.editText.openKeyboard();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int mask = args[0].intValue();
                if ((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) {
                    int count = this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatDidFailCreate) {
            this.reqId = 0;
            this.donePressed = false;
            showEditDoneProgress(false);
            EditTextEmoji editTextEmoji = this.editText;
            if (editTextEmoji != null) {
                editTextEmoji.setEnabled(true);
            }
            GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate = this.delegate;
            if (groupCreateFinalActivityDelegate != null) {
                groupCreateFinalActivityDelegate.didFailChatCreation();
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            this.reqId = 0;
            int chat_id = args[0].intValue();
            GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate2 = this.delegate;
            if (groupCreateFinalActivityDelegate2 != null) {
                groupCreateFinalActivityDelegate2.didFinishChatCreation(this, chat_id);
            } else {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                Bundle args2 = new Bundle();
                args2.putInt("chat_id", chat_id);
                presentFragment(new ChatActivity(args2), true);
            }
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(chat_id, this.uploadedAvatar, this.avatar, this.avatarBig);
            }
        }
    }

    private void showEditDoneProgress(boolean show) {
        final boolean z = show;
        if (this.floatingButtonIcon != null) {
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            if (z) {
                this.progressView.setVisibility(0);
                this.floatingButtonContainer.setEnabled(false);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
            } else {
                this.floatingButtonIcon.setVisibility(0);
                this.floatingButtonContainer.setEnabled(true);
                this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, "alpha", new float[]{1.0f})});
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animation)) {
                        if (!z) {
                            GroupCreateFinalActivity.this.progressView.setVisibility(4);
                        } else {
                            GroupCreateFinalActivity.this.floatingButtonIcon.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animation)) {
                        AnimatorSet unused = GroupCreateFinalActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public class GroupCreateAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int usersStartRow;

        public GroupCreateAdapter(Context ctx) {
            this.context = ctx;
        }

        public int getItemCount() {
            int count = GroupCreateFinalActivity.this.selectedContacts.size() + 2;
            if (GroupCreateFinalActivity.this.currentGroupCreateAddress != null) {
                return count + 3;
            }
            return count;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 3;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: im.bclpbkiauv.ui.cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: im.bclpbkiauv.ui.cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: im.bclpbkiauv.ui.cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: im.bclpbkiauv.ui.cells.GroupCreateUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: im.bclpbkiauv.ui.cells.TextSettingsCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                if (r6 == 0) goto L_0x0028
                r0 = 1
                if (r6 == r0) goto L_0x001a
                r0 = 2
                if (r6 == r0) goto L_0x0010
                im.bclpbkiauv.ui.cells.TextSettingsCell r0 = new im.bclpbkiauv.ui.cells.TextSettingsCell
                android.content.Context r1 = r4.context
                r0.<init>(r1)
                goto L_0x0030
            L_0x0010:
                im.bclpbkiauv.ui.cells.GroupCreateUserCell r0 = new im.bclpbkiauv.ui.cells.GroupCreateUserCell
                android.content.Context r1 = r4.context
                r2 = 0
                r3 = 3
                r0.<init>(r1, r2, r3)
                goto L_0x0030
            L_0x001a:
                im.bclpbkiauv.ui.cells.HeaderCell r0 = new im.bclpbkiauv.ui.cells.HeaderCell
                android.content.Context r1 = r4.context
                r0.<init>(r1)
                r1 = 46
                r0.setHeight(r1)
                r1 = r0
                goto L_0x0030
            L_0x0028:
                im.bclpbkiauv.ui.cells.ShadowSectionCell r0 = new im.bclpbkiauv.ui.cells.ShadowSectionCell
                android.content.Context r1 = r4.context
                r0.<init>(r1)
            L_0x0030:
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r1 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.GroupCreateFinalActivity.GroupCreateAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 1) {
                HeaderCell cell = (HeaderCell) holder.itemView;
                if (GroupCreateFinalActivity.this.currentGroupCreateAddress == null || position != 1) {
                    cell.setText(LocaleController.formatPluralString("Members", GroupCreateFinalActivity.this.selectedContacts.size()));
                } else {
                    cell.setText(LocaleController.getString("AttachLocation", R.string.AttachLocation));
                }
            } else if (itemViewType == 2) {
                ((GroupCreateUserCell) holder.itemView).setObject(MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).getUser((Integer) GroupCreateFinalActivity.this.selectedContacts.get(position - this.usersStartRow)), (CharSequence) null, (CharSequence) null);
            } else if (itemViewType == 3) {
                ((TextSettingsCell) holder.itemView).setText(GroupCreateFinalActivity.this.currentGroupCreateAddress, false);
            }
        }

        public int getItemViewType(int position) {
            if (GroupCreateFinalActivity.this.currentGroupCreateAddress == null) {
                this.usersStartRow = 2;
            } else if (position == 0) {
                return 0;
            } else {
                if (position == 1) {
                    return 1;
                }
                if (position == 2) {
                    return 3;
                }
                position -= 3;
                this.usersStartRow = 5;
            }
            if (position != 0) {
                return position != 1 ? 2 : 1;
            }
            return 0;
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 2) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                GroupCreateFinalActivity.this.lambda$getThemeDescriptions$8$GroupCreateFinalActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollActive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollInactive), new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_fastScrollText), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_hintText), new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_cursor), new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_groupcreate_sectionText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, themeDescriptionDelegate, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressInner2), new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_contextProgressOuter2), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$8$GroupCreateFinalActivity() {
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

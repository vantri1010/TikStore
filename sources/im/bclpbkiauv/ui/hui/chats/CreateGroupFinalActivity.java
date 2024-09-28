package im.bclpbkiauv.ui.hui.chats;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.SpanUtils;
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
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.GroupCreateUserCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hcells.AvatarDelCell;
import im.bclpbkiauv.ui.hcells.MryTextCheckCell;
import im.bclpbkiauv.ui.hui.chats.CreateGroupFinalActivity;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public class CreateGroupFinalActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private final int CREATE_CHAT = 1;
    private GroupCreateAdapter adapter;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public ImageView avatarEditor;
    private BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public ImageView avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private final int chatType;
    private boolean createAfterUpload;
    private final String currentGroupCreateAddress;
    private final BDLocation currentGroupCreateLocation;
    private CreateGroupDelegate delegate;
    private boolean donePressed;
    /* access modifiers changed from: private */
    public EditText editText;
    /* access modifiers changed from: private */
    public FrameLayout editTextContainer;
    private MryTextCheckCell forbitContacts;
    private ImageUpdater imageUpdater;
    private GridLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private Context mContext;
    /* access modifiers changed from: private */
    public int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
    /* access modifiers changed from: private */
    public MryTextView memberInfoCell;
    private String nameToSet;
    private MryTextView nextTextView;
    private int reqId;
    /* access modifiers changed from: private */
    public ArrayList<Integer> selectedContacts;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private TLRPC.InputFile uploadedAvatar;

    public interface CreateGroupDelegate {
        void didFailChatCreation();

        void didFinishChatCreation(CreateGroupFinalActivity createGroupFinalActivity, int i);

        void didStartChatCreation();
    }

    public /* synthetic */ void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public CreateGroupFinalActivity(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 4);
        this.avatarDrawable = new AvatarDrawable();
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
        if (getArguments() != null) {
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
                        CreateGroupFinalActivity.this.lambda$onFragmentCreate$0$CreateGroupFinalActivity(this.f$1, this.f$2, this.f$3);
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
        }
        return super.onFragmentCreate();
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$CreateGroupFinalActivity(ArrayList users, ArrayList usersToLoad, CountDownLatch countDownLatch) {
        users.addAll(MessagesStorage.getInstance(this.currentAccount).getUsers(usersToLoad));
        countDownLatch.countDown();
    }

    public void onResume() {
        super.onResume();
        GroupCreateAdapter groupCreateAdapter = this.adapter;
        if (groupCreateAdapter != null) {
            groupCreateAdapter.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
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
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public boolean onBackPressed() {
        return true;
    }

    public View createView(Context context) {
        Context context2 = context;
        super.createView(context);
        this.mContext = context2;
        initActionBar();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                int heightSize2 = heightSize - getPaddingTop();
                measureChildWithMargins(CreateGroupFinalActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == CreateGroupFinalActivity.this.actionBar)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                setBottomClip(0);
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
                            childTop = (((((b - 0) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        } else if (verticalGravity == 48) {
                            childTop = lp.topMargin + getPaddingTop();
                        } else if (verticalGravity != 80) {
                            childTop = lp.topMargin;
                        } else {
                            childTop = (((b - 0) - t) - height) - lp.bottomMargin;
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
        this.fragmentView.setOnTouchListener($$Lambda$CreateGroupFinalActivity$Mi8IElONdLhUT8SgfG3aEXOgJh0.INSTANCE);
        this.shadowDrawable = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
        LinearLayout linearLayout = new LinearLayout(context2) {
            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == CreateGroupFinalActivity.this.listView && CreateGroupFinalActivity.this.shadowDrawable != null) {
                    int y = CreateGroupFinalActivity.this.editTextContainer.getMeasuredHeight();
                    CreateGroupFinalActivity.this.shadowDrawable.setBounds(0, y, getMeasuredWidth(), CreateGroupFinalActivity.this.shadowDrawable.getIntrinsicHeight() + y);
                    CreateGroupFinalActivity.this.shadowDrawable.draw(canvas);
                }
                return result;
            }
        };
        linearLayout.setOrientation(1);
        linearLayout.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        sizeNotifierFrameLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.editTextContainer = frameLayout;
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 65));
        AnonymousClass3 r10 = new BackupImageView(context2) {
            public void invalidate() {
                if (CreateGroupFinalActivity.this.avatarOverlay != null) {
                    CreateGroupFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            public void invalidate(int l, int t, int r, int b) {
                if (CreateGroupFinalActivity.this.avatarOverlay != null) {
                    CreateGroupFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(l, t, r, b);
            }
        };
        this.avatarImage = r10;
        r10.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        this.avatarImage.setContentDescription(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto));
        this.editTextContainer.addView(this.avatarImage, LayoutHelper.createFrame(49.0f, 49.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        new Paint(1).setColor(1426063360);
        ImageView imageView = new ImageView(context2);
        this.avatarOverlay = imageView;
        imageView.setImageResource(R.mipmap.ic_create_group_photo);
        this.editTextContainer.addView(this.avatarOverlay, LayoutHelper.createFrame(49.0f, 49.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        this.avatarOverlay.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CreateGroupFinalActivity.this.lambda$createView$3$CreateGroupFinalActivity(view);
            }
        });
        AnonymousClass4 r11 = new AppCompatImageView(context2) {
            public void invalidate(int l, int t, int r, int b) {
                super.invalidate(l, t, r, b);
                CreateGroupFinalActivity.this.avatarOverlay.invalidate();
            }

            public void invalidate() {
                super.invalidate();
                CreateGroupFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarEditor = r11;
        r11.setScaleType(ImageView.ScaleType.CENTER);
        this.avatarEditor.setEnabled(false);
        this.avatarEditor.setClickable(false);
        this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
        this.editTextContainer.addView(this.avatarEditor, LayoutHelper.createFrame(16.0f, 16.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        AnonymousClass5 r112 = new RadialProgressView(context2) {
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                CreateGroupFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarProgressView = r112;
        r112.setSize(AndroidUtilities.dp(30.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.editTextContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(49.0f, 49.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        showAvatarProgress(false, false);
        EditText editText2 = new EditText(context2);
        this.editText = editText2;
        editText2.setBackgroundColor(0);
        this.editText.setHint(LocaleController.getString("EnterGroupNamePlaceholder", R.string.EnterGroupNamePlaceholder));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setHintTextColor(Theme.getColor(Theme.key_groupcreate_hintText));
        String str = this.nameToSet;
        if (str != null) {
            this.editText.setText(str);
            this.nameToSet = null;
        }
        this.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        this.editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CreateGroupFinalActivity.this.updateNextView();
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.editTextContainer.addView(this.editText, LayoutHelper.createFrame(-1.0f, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        linearLayout.addView(new ShadowSectionCell(context2));
        MryTextCheckCell mryTextCheckCell = new MryTextCheckCell(context2);
        this.forbitContacts = mryTextCheckCell;
        mryTextCheckCell.setBackground(Theme.getSelectorDrawable(false));
        linearLayout.addView(this.forbitContacts, LayoutHelper.createLinear(-1, AndroidUtilities.dp(48.0f)));
        this.forbitContacts.setTextAndCheck(LocaleController.getString("ForbidPrivateChat", R.string.ForbidPrivateChat), false, false);
        this.forbitContacts.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CreateGroupFinalActivity.this.lambda$createView$4$CreateGroupFinalActivity(view);
            }
        });
        MryTextView forbitContactsTips = new MryTextView(context2);
        forbitContactsTips.setText(LocaleController.getString(R.string.ForbidPrivateChatTips));
        forbitContactsTips.setTextSize(13.0f);
        forbitContactsTips.setLineSpacing((float) AndroidUtilities.dp(5.0f), 1.0f);
        forbitContactsTips.setTextColor(Theme.getColor(Theme.key_graySectionText));
        forbitContactsTips.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        forbitContactsTips.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f));
        linearLayout.addView(forbitContactsTips, LayoutHelper.createLinear(-1, -2));
        MryTextView mryTextView = new MryTextView(this.mContext);
        this.memberInfoCell = mryTextView;
        mryTextView.setGravity(16);
        this.memberInfoCell.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
        this.memberInfoCell.setTextSize(1, 14.0f);
        this.memberInfoCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.memberInfoCell.setTextColor(Theme.getColor(Theme.key_graySectionText));
        this.memberInfoCell.setText(new SpanUtils().append(LocaleController.getString("GroupMembers", R.string.GroupMembers)).append("  ").append(String.valueOf(this.selectedContacts.size())).setForegroundColor(-12862209).append("/").append(String.valueOf(this.maxCount)).create());
        this.memberInfoCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout.addView(this.memberInfoCell, LayoutHelper.createLinear(-1, 36));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context2);
        this.adapter = groupCreateAdapter;
        recyclerListView2.setAdapter(groupCreateAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        AnonymousClass7 r7 = new GridLayoutManager(context2, 5) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r7;
        recyclerListView3.setLayoutManager(r7);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(CreateGroupFinalActivity.this.editText);
                }
            }
        });
        updateNextView();
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$3$CreateGroupFinalActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                CreateGroupFinalActivity.this.lambda$null$2$CreateGroupFinalActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$CreateGroupFinalActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
    }

    public /* synthetic */ void lambda$createView$4$CreateGroupFinalActivity(View v) {
        MryTextCheckCell mryTextCheckCell = this.forbitContacts;
        mryTextCheckCell.setChecked(!mryTextCheckCell.isChecked());
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
                        if (CreateGroupFinalActivity.this.avatarAnimation != null && CreateGroupFinalActivity.this.avatarEditor != null) {
                            if (show) {
                                CreateGroupFinalActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                CreateGroupFinalActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = CreateGroupFinalActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = CreateGroupFinalActivity.this.avatarAnimation = null;
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

    public void setDelegate(CreateGroupDelegate delegate2) {
        this.delegate = delegate2;
    }

    private void initActionBar() {
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setCastShadows(true);
        this.actionBar.setTitle(LocaleController.getString("NewGroup", R.string.NewGroup));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == 1) {
                    CreateGroupFinalActivity.this.createChat();
                }
            }
        });
        this.actionBar.setBackTitle(LocaleController.getString("Cancel", R.string.Cancel));
        this.actionBar.getBackTitleTextView().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CreateGroupFinalActivity.this.lambda$initActionBar$5$CreateGroupFinalActivity(view);
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        MryTextView mryTextView = new MryTextView(getParentActivity());
        this.nextTextView = mryTextView;
        mryTextView.setText(LocaleController.getString("Create", R.string.Create));
        this.nextTextView.setTextSize(1, 14.0f);
        this.nextTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nextTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.nextTextView.setGravity(17);
        menu.addItemView(1, this.nextTextView);
    }

    public /* synthetic */ void lambda$initActionBar$5$CreateGroupFinalActivity(View v) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void createChat() {
        if (!this.donePressed) {
            if (this.editText.length() == 0) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(this.editText, 2.0f, 0);
            } else if (this.selectedContacts.size() <= 0) {
                Vibrator v2 = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v2 != null) {
                    v2.vibrate(200);
                }
                ToastUtils.show((int) R.string.AtLeastOneContact);
            } else {
                this.donePressed = true;
                AndroidUtilities.hideKeyboard(this.editText);
                this.editText.setEnabled(false);
                if (this.imageUpdater.uploadingImage != null) {
                    this.createAfterUpload = true;
                    return;
                }
                this.reqId = MessagesController.getInstance(this.currentAccount).createMegaGroup(this.editText.getText().toString(), this.selectedContacts, (String) null, this.chatType, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this, this.forbitContacts.isChecked());
            }
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
                CreateGroupFinalActivity.this.lambda$didUploadPhoto$6$CreateGroupFinalActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$6$CreateGroupFinalActivity(TLRPC.InputFile file, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (file != null) {
            this.uploadedAvatar = file;
            if (this.createAfterUpload) {
                CreateGroupDelegate createGroupDelegate = this.delegate;
                if (createGroupDelegate != null) {
                    createGroupDelegate.didStartChatCreation();
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
        this.avatarOverlay.setImageResource(0);
    }

    public String getInitialSearchString() {
        return this.editText.getText().toString();
    }

    public void saveSelfArgs(Bundle args) {
        String text;
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (!(imageUpdater2 == null || imageUpdater2.currentPicturePath == null)) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
        EditText editText2 = this.editText;
        if (editText2 != null && (text = editText2.getText().toString()) != null && text.length() != 0) {
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
            EditText editText2 = this.editText;
            if (editText2 != null) {
                editText2.setText(text);
            } else {
                this.nameToSet = text;
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
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
            EditText editText2 = this.editText;
            if (editText2 != null) {
                editText2.setEnabled(true);
            }
            CreateGroupDelegate createGroupDelegate = this.delegate;
            if (createGroupDelegate != null) {
                createGroupDelegate.didFailChatCreation();
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            this.reqId = 0;
            this.donePressed = false;
            int chat_id = args[0].intValue();
            CreateGroupDelegate createGroupDelegate2 = this.delegate;
            if (createGroupDelegate2 != null) {
                createGroupDelegate2.didFinishChatCreation(this, chat_id);
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

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public class GroupCreateAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int usersStartRow;

        public GroupCreateAdapter(Context ctx) {
            this.context = ctx;
        }

        public int getItemCount() {
            return CreateGroupFinalActivity.this.selectedContacts.size();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 3;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: im.bclpbkiauv.ui.cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: im.bclpbkiauv.ui.cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: im.bclpbkiauv.ui.cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: im.bclpbkiauv.ui.cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: im.bclpbkiauv.ui.hcells.AvatarDelCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: im.bclpbkiauv.ui.cells.TextSettingsCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r7, int r8) {
            /*
                r6 = this;
                r0 = 1
                if (r8 == 0) goto L_0x0026
                if (r8 == r0) goto L_0x0018
                r0 = 2
                if (r8 == r0) goto L_0x0010
                im.bclpbkiauv.ui.cells.TextSettingsCell r0 = new im.bclpbkiauv.ui.cells.TextSettingsCell
                android.content.Context r1 = r6.context
                r0.<init>(r1)
                goto L_0x004f
            L_0x0010:
                im.bclpbkiauv.ui.hcells.AvatarDelCell r0 = new im.bclpbkiauv.ui.hcells.AvatarDelCell
                android.content.Context r1 = r6.context
                r0.<init>(r1)
                goto L_0x004f
            L_0x0018:
                im.bclpbkiauv.ui.cells.HeaderCell r0 = new im.bclpbkiauv.ui.cells.HeaderCell
                android.content.Context r1 = r6.context
                r0.<init>(r1)
                r1 = 46
                r0.setHeight(r1)
                r1 = r0
                goto L_0x004f
            L_0x0026:
                im.bclpbkiauv.ui.cells.ShadowSectionCell r1 = new im.bclpbkiauv.ui.cells.ShadowSectionCell
                android.content.Context r2 = r6.context
                r1.<init>(r2)
                android.content.Context r2 = r6.context
                r3 = 2131231062(0x7f080156, float:1.8078194E38)
                java.lang.String r4 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r4)
                im.bclpbkiauv.ui.components.CombinedDrawable r3 = new im.bclpbkiauv.ui.components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r4 = new android.graphics.drawable.ColorDrawable
                java.lang.String r5 = "windowBackgroundGray"
                int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
                r4.<init>(r5)
                r3.<init>(r4, r2)
                r3.setFullsize(r0)
                r1.setBackgroundDrawable(r3)
                r0 = r1
            L_0x004f:
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r1 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.chats.CreateGroupFinalActivity.GroupCreateAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 1) {
                HeaderCell cell = (HeaderCell) holder.itemView;
                if (position == 1) {
                    cell.setText(LocaleController.getString("AttachLocation", R.string.AttachLocation));
                } else {
                    cell.setText(LocaleController.formatPluralString("Members", CreateGroupFinalActivity.this.selectedContacts.size()));
                }
            } else if (itemViewType == 2) {
                AvatarDelCell cell2 = (AvatarDelCell) holder.itemView;
                cell2.setUser(MessagesController.getInstance(CreateGroupFinalActivity.this.currentAccount).getUser((Integer) CreateGroupFinalActivity.this.selectedContacts.get(position - this.usersStartRow)));
                cell2.setDelegate(new AvatarDelCell.AvatarDelDelegate(position) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClickDelete() {
                        CreateGroupFinalActivity.GroupCreateAdapter.this.lambda$onBindViewHolder$0$CreateGroupFinalActivity$GroupCreateAdapter(this.f$1);
                    }
                });
            } else if (itemViewType == 3) {
                ((TextSettingsCell) holder.itemView).setText("xxxxx", false);
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$CreateGroupFinalActivity$GroupCreateAdapter(int position) {
            CreateGroupFinalActivity.this.selectedContacts.remove(position);
            notifyDataSetChanged();
            CreateGroupFinalActivity.this.memberInfoCell.setText(new SpanUtils().append(LocaleController.getString("GroupMembers", R.string.GroupMembers)).append("  ").append(String.valueOf(CreateGroupFinalActivity.this.selectedContacts.size())).setForegroundColor(-12862209).append("/").append(String.valueOf(CreateGroupFinalActivity.this.maxCount)).create());
        }

        public void notifyDataSetChanged() {
            CreateGroupFinalActivity.this.updateNextView();
            super.notifyDataSetChanged();
        }

        public int getItemViewType(int position) {
            return 2;
        }
    }

    /* access modifiers changed from: private */
    public void updateNextView() {
        this.nextTextView.setEnabled(!TextUtils.isEmpty(this.editText.getText().toString()) && this.adapter.getItemCount() > 0);
    }
}

package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChannelCreateActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.AdminedChannelCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.RadioButtonCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextBlockCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EditTextEmoji;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.util.ArrayList;

public class ChannelCreateActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList<>();
    private TextInfoPrivacyCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public ImageView avatarEditor;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private boolean canCreatePublic = true;
    /* access modifiers changed from: private */
    public int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    /* access modifiers changed from: private */
    public TextView checkTextView;
    /* access modifiers changed from: private */
    public boolean createAfterUpload;
    /* access modifiers changed from: private */
    public int currentStep;
    /* access modifiers changed from: private */
    public EditTextBoldCursor descriptionTextView;
    private View doneButton;
    /* access modifiers changed from: private */
    public boolean donePressed;
    private EditText editText;
    private HeaderCell headerCell;
    private TextView helpTextView;
    /* access modifiers changed from: private */
    public ImageUpdater imageUpdater;
    private TLRPC.ExportedChatInvite invite;
    /* access modifiers changed from: private */
    public boolean isPrivate;
    /* access modifiers changed from: private */
    public String lastCheckName;
    /* access modifiers changed from: private */
    public boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout2;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    /* access modifiers changed from: private */
    public EditTextEmoji nameTextView;
    private String nameToSet;
    private TextBlockCell privateContainer;
    /* access modifiers changed from: private */
    public AlertDialog progressDialog;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;
    private TLRPC.InputFile uploadedAvatar;

    public /* synthetic */ void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public ChannelCreateActivity(Bundle args) {
        super(args);
        int i = args.getInt("step", 0);
        this.currentStep = i;
        if (i == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.imageUpdater = new ImageUpdater();
            TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TLRPC.TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelCreateActivity.this.lambda$new$1$ChannelCreateActivity(tLObject, tL_error);
                }
            });
            return;
        }
        if (i == 1) {
            boolean z = args.getBoolean("canCreatePublic", true);
            this.canCreatePublic = z;
            this.isPrivate = !z;
            if (!z) {
                loadAdminedChannels();
            }
        }
        this.chatId = args.getInt("chat_id", 0);
    }

    public /* synthetic */ void lambda$new$1$ChannelCreateActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$null$0$ChannelCreateActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$ChannelCreateActivity(TLRPC.TL_error error) {
        this.canCreatePublic = error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == 1) {
            generateLink();
        }
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.parentFragment = this;
            this.imageUpdater.delegate = this;
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.clear();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.nameTextView.hidePopup(true);
        return false;
    }

    public View createView(Context context) {
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelCreateActivity.this.finishFragment();
                } else if (id != 1) {
                } else {
                    if (ChannelCreateActivity.this.currentStep == 0) {
                        if (!ChannelCreateActivity.this.donePressed && ChannelCreateActivity.this.getParentActivity() != null) {
                            if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                                Vibrator v = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                if (v != null) {
                                    v.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0f, 0);
                                return;
                            }
                            boolean unused = ChannelCreateActivity.this.donePressed = true;
                            if (ChannelCreateActivity.this.imageUpdater.uploadingImage != null) {
                                boolean unused2 = ChannelCreateActivity.this.createAfterUpload = true;
                                AlertDialog unused3 = ChannelCreateActivity.this.progressDialog = new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 3);
                                ChannelCreateActivity.this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    public final void onCancel(DialogInterface dialogInterface) {
                                        ChannelCreateActivity.AnonymousClass1.this.lambda$onItemClick$0$ChannelCreateActivity$1(dialogInterface);
                                    }
                                });
                                ChannelCreateActivity.this.progressDialog.show();
                                return;
                            }
                            int reqId = MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, (BDLocation) null, (String) null, ChannelCreateActivity.this);
                            AlertDialog unused4 = ChannelCreateActivity.this.progressDialog = new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 3);
                            ChannelCreateActivity.this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
                                private final /* synthetic */ int f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onCancel(DialogInterface dialogInterface) {
                                    ChannelCreateActivity.AnonymousClass1.this.lambda$onItemClick$1$ChannelCreateActivity$1(this.f$1, dialogInterface);
                                }
                            });
                            ChannelCreateActivity.this.progressDialog.show();
                        }
                    } else if (ChannelCreateActivity.this.currentStep == 1) {
                        if (!ChannelCreateActivity.this.isPrivate) {
                            if (ChannelCreateActivity.this.descriptionTextView.length() == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ChannelCreateActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("ChannelPublicEmptyUsername", R.string.ChannelPublicEmptyUsername));
                                builder.setPositiveButton(LocaleController.getString("Close", R.string.Close), (DialogInterface.OnClickListener) null);
                                ChannelCreateActivity.this.showDialog(builder.create());
                                return;
                            } else if (!ChannelCreateActivity.this.lastNameAvailable) {
                                Vibrator v2 = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                if (v2 != null) {
                                    v2.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0f, 0);
                                return;
                            } else {
                                MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
                            }
                        }
                        Bundle args = new Bundle();
                        args.putInt("step", 2);
                        args.putInt("chatId", ChannelCreateActivity.this.chatId);
                        args.putInt("chatType", 2);
                        ChannelCreateActivity.this.presentFragment(new GroupCreateActivity(args), true);
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ChannelCreateActivity$1(DialogInterface dialog) {
                boolean unused = ChannelCreateActivity.this.createAfterUpload = false;
                AlertDialog unused2 = ChannelCreateActivity.this.progressDialog = null;
                boolean unused3 = ChannelCreateActivity.this.donePressed = false;
            }

            public /* synthetic */ void lambda$onItemClick$1$ChannelCreateActivity$1(int reqId, DialogInterface dialog) {
                ConnectionsManager.getInstance(ChannelCreateActivity.this.currentAccount).cancelRequest(reqId, true);
                boolean unused = ChannelCreateActivity.this.donePressed = false;
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        int i = this.currentStep;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewChannel", R.string.NewChannel));
            SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
                private boolean ignoreLayout;

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                    int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                    setMeasuredDimension(widthSize, heightSize);
                    int heightSize2 = heightSize - getPaddingTop();
                    measureChildWithMargins(ChannelCreateActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                        this.ignoreLayout = true;
                        ChannelCreateActivity.this.nameTextView.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                    int childCount = getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = getChildAt(i);
                        if (!(child == null || child.getVisibility() == 8 || child == ChannelCreateActivity.this.actionBar)) {
                            if (ChannelCreateActivity.this.nameTextView == null || !ChannelCreateActivity.this.nameTextView.isPopupView(child)) {
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
                    int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChannelCreateActivity.this.nameTextView.getEmojiPadding();
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
                            if (ChannelCreateActivity.this.nameTextView != null && ChannelCreateActivity.this.nameTextView.isPopupView(child)) {
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
            sizeNotifierFrameLayout.setOnTouchListener($$Lambda$ChannelCreateActivity$suZndXE0QR0Bhp3r3wHIiHeXIgY.INSTANCE);
            this.fragmentView = sizeNotifierFrameLayout;
            this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            LinearLayout linearLayout3 = new LinearLayout(context2);
            this.linearLayout = linearLayout3;
            linearLayout3.setOrientation(1);
            sizeNotifierFrameLayout.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            FrameLayout frameLayout = new FrameLayout(context2);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            AnonymousClass3 r8 = new BackupImageView(context2) {
                public void invalidate() {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                public void invalidate(int l, int t, int r, int b) {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(l, t, r, b);
                }
            };
            this.avatarImage = r8;
            r8.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5, (String) null, (String) null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64.0f, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            final Paint paint = new Paint(1);
            paint.setColor(1426063360);
            AnonymousClass4 r13 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (ChannelCreateActivity.this.avatarImage != null && ChannelCreateActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChannelCreateActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                    }
                }
            };
            this.avatarOverlay = r13;
            frameLayout.addView(r13, LayoutHelper.createFrame(64.0f, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$4$ChannelCreateActivity(view);
                }
            });
            AnonymousClass5 r132 = new ImageView(context2) {
                public void invalidate(int l, int t, int r, int b) {
                    super.invalidate(l, t, r, b);
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }

                public void invalidate() {
                    super.invalidate();
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor = r132;
            r132.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setImageResource(R.drawable.menu_camera_av);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64.0f, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context2);
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64.0f, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
            EditTextEmoji editTextEmoji2 = new EditTextEmoji(context2, sizeNotifierFrameLayout, this, 0);
            this.nameTextView = editTextEmoji2;
            editTextEmoji2.setHint(LocaleController.getString("EnterChannelName", R.string.EnterChannelName));
            String str = this.nameToSet;
            if (str != null) {
                this.nameTextView.setText(str);
                this.nameToSet = null;
            }
            this.nameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
            this.descriptionTextView = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.descriptionTextView.setInputType(180225);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)});
            this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", R.string.DescriptionPlaceholder));
            this.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ChannelCreateActivity.this.lambda$createView$5$ChannelCreateActivity(textView, i, keyEvent);
                }
            });
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                }
            });
            TextView textView = new TextView(context2);
            this.helpTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
            this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.helpTextView.setText(LocaleController.getString("DescriptionInfo", R.string.DescriptionInfo));
            this.linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else if (i == 1) {
            this.fragmentView = new ScrollView(context2);
            ScrollView scrollView = (ScrollView) this.fragmentView;
            scrollView.setFillViewport(true);
            LinearLayout linearLayout4 = new LinearLayout(context2);
            this.linearLayout = linearLayout4;
            linearLayout4.setOrientation(1);
            scrollView.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            this.actionBar.setTitle(LocaleController.getString("ChannelSettings", R.string.ChannelSettings));
            this.fragmentView.setTag(Theme.key_windowBackgroundGray);
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            LinearLayout linearLayout5 = new LinearLayout(context2);
            this.linearLayout2 = linearLayout5;
            linearLayout5.setOrientation(1);
            this.linearLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
            RadioButtonCell radioButtonCell = new RadioButtonCell(context2);
            this.radioButtonCell1 = radioButtonCell;
            radioButtonCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", R.string.ChannelPublicInfo), false, !this.isPrivate);
            this.linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$6$ChannelCreateActivity(view);
                }
            });
            RadioButtonCell radioButtonCell3 = new RadioButtonCell(context2);
            this.radioButtonCell2 = radioButtonCell3;
            radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", R.string.ChannelPrivateInfo), false, this.isPrivate);
            this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell2.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$7$ChannelCreateActivity(view);
                }
            });
            ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
            this.sectionCell = shadowSectionCell;
            this.linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout6 = new LinearLayout(context2);
            this.linkContainer = linearLayout6;
            linearLayout6.setOrientation(1);
            this.linkContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
            HeaderCell headerCell2 = new HeaderCell(context2);
            this.headerCell = headerCell2;
            this.linkContainer.addView(headerCell2);
            LinearLayout linearLayout7 = new LinearLayout(context2);
            this.publicContainer = linearLayout7;
            linearLayout7.setOrientation(0);
            this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
            EditText editText2 = new EditText(context2);
            this.editText = editText2;
            editText2.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/");
            this.editText.setTextSize(1, 18.0f);
            this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.editText.setMaxLines(1);
            this.editText.setLines(1);
            this.editText.setEnabled(false);
            this.editText.setBackgroundDrawable((Drawable) null);
            this.editText.setPadding(0, 0, 0, 0);
            this.editText.setSingleLine(true);
            this.editText.setInputType(163840);
            this.editText.setImeOptions(6);
            this.editText.setVisibility(8);
            this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
            EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
            this.descriptionTextView = editTextBoldCursor2;
            editTextBoldCursor2.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setMaxLines(1);
            this.descriptionTextView.setLines(1);
            this.descriptionTextView.setBackgroundDrawable((Drawable) null);
            this.descriptionTextView.setPadding(0, 0, 0, 0);
            this.descriptionTextView.setSingleLine(true);
            this.descriptionTextView.setInputType(163872);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.publicContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, 36));
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                    boolean unused = channelCreateActivity.checkUserName(channelCreateActivity.descriptionTextView.getText().toString());
                }

                public void afterTextChanged(Editable editable) {
                }
            });
            TextBlockCell textBlockCell = new TextBlockCell(context2);
            this.privateContainer = textBlockCell;
            textBlockCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.linkContainer.addView(this.privateContainer);
            this.privateContainer.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$createView$8$ChannelCreateActivity(view);
                }
            });
            TextView textView2 = new TextView(context2);
            this.checkTextView = textView2;
            textView2.setTextSize(1, 15.0f);
            this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.checkTextView.setVisibility(8);
            this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
            this.typeInfoCell = textInfoPrivacyCell;
            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
            LoadingCell loadingCell = new LoadingCell(context2);
            this.loadingAdminedCell = loadingCell;
            this.linearLayout.addView(loadingCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout8 = new LinearLayout(context2);
            this.adminnedChannelsLayout = linearLayout8;
            linearLayout8.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.adminnedChannelsLayout.setOrientation(1);
            this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
            this.adminedInfoCell = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$4$ChannelCreateActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                ChannelCreateActivity.this.lambda$null$3$ChannelCreateActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$3$ChannelCreateActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
    }

    public /* synthetic */ boolean lambda$createView$5$ChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$6$ChannelCreateActivity(View v) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$createView$7$ChannelCreateActivity(View v) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$createView$8$ChannelCreateActivity(View v) {
        if (this.invite != null) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                ToastUtils.show((int) R.string.LinkCopied);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            this.loadingInvite = true;
            TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelCreateActivity.this.lambda$generateLink$10$ChannelCreateActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$generateLink$10$ChannelCreateActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$null$9$ChannelCreateActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$ChannelCreateActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.invite = (TLRPC.ExportedChatInvite) response;
        }
        this.loadingInvite = false;
        TextBlockCell textBlockCell = this.privateContainer;
        TLRPC.ExportedChatInvite exportedChatInvite = this.invite;
        textBlockCell.setText(exportedChatInvite != null ? exportedChatInvite.link : LocaleController.getString("Loading", R.string.Loading), false);
    }

    private void updatePrivatePublic() {
        String str;
        int i;
        String str2;
        int i2;
        if (this.sectionCell != null) {
            int i3 = 8;
            if (this.isPrivate || this.canCreatePublic) {
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteGrayText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                this.sectionCell.setVisibility(0);
                this.adminedInfoCell.setVisibility(8);
                this.adminnedChannelsLayout.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell = this.typeInfoCell;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell2 = this.typeInfoCell;
                if (this.isPrivate) {
                    i = R.string.ChannelPrivateLinkHelp;
                    str = "ChannelPrivateLinkHelp";
                } else {
                    i = R.string.ChannelUsernameHelp;
                    str = "ChannelUsernameHelp";
                }
                textInfoPrivacyCell2.setText(LocaleController.getString(str, i));
                HeaderCell headerCell2 = this.headerCell;
                if (this.isPrivate) {
                    i2 = R.string.ChannelInviteLinkTitle;
                    str2 = "ChannelInviteLinkTitle";
                } else {
                    i2 = R.string.ChannelLinkTitle;
                    str2 = "ChannelLinkTitle";
                }
                headerCell2.setText(LocaleController.getString(str2, i2));
                this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
                this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                if (this.isPrivate) {
                    this.linkContainer.setVisibility(8);
                } else {
                    this.linkContainer.setVisibility(0);
                }
                TextBlockCell textBlockCell = this.privateContainer;
                TLRPC.ExportedChatInvite exportedChatInvite = this.invite;
                textBlockCell.setText(exportedChatInvite != null ? exportedChatInvite.link : LocaleController.getString("Loading", R.string.Loading), false);
                TextView textView = this.checkTextView;
                if (!this.isPrivate && textView.length() != 0) {
                    i3 = 0;
                }
                textView.setVisibility(i3);
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", R.string.ChangePublicLimitReached));
                this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                this.linkContainer.setVisibility(8);
                this.sectionCell.setVisibility(8);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    this.adminnedChannelsLayout.setVisibility(8);
                    TextInfoPrivacyCell textInfoPrivacyCell3 = this.typeInfoCell;
                    textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell3.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    this.adminedInfoCell.setVisibility(8);
                } else {
                    TextInfoPrivacyCell textInfoPrivacyCell4 = this.typeInfoCell;
                    textInfoPrivacyCell4.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell4.getContext(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                    this.adminedInfoCell.setVisibility(0);
                }
            }
            this.radioButtonCell1.setChecked(!this.isPrivate, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.descriptionTextView.clearFocus();
            AndroidUtilities.hideKeyboard(this.descriptionTextView);
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
                ChannelCreateActivity.this.lambda$didUploadPhoto$11$ChannelCreateActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$11$ChannelCreateActivity(TLRPC.InputFile file, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (file != null) {
            this.uploadedAvatar = file;
            if (this.createAfterUpload) {
                try {
                    if (this.progressDialog != null && this.progressDialog.isShowing()) {
                        this.progressDialog.dismiss();
                        this.progressDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
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
                        if (ChannelCreateActivity.this.avatarAnimation != null && ChannelCreateActivity.this.avatarEditor != null) {
                            if (show) {
                                ChannelCreateActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                ChannelCreateActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = ChannelCreateActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = ChannelCreateActivity.this.avatarAnimation = null;
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
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater2 = this.imageUpdater;
            if (!(imageUpdater2 == null || imageUpdater2.currentPicturePath == null)) {
                args.putString("path", this.imageUpdater.currentPicturePath);
            }
            EditTextEmoji editTextEmoji = this.nameTextView;
            if (editTextEmoji != null && (text = editTextEmoji.getText().toString()) != null && text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater2 = this.imageUpdater;
            if (imageUpdater2 != null) {
                imageUpdater2.currentPicturePath = args.getString("path");
            }
            String text = args.getString("nameTextView");
            if (text != null) {
                EditTextEmoji editTextEmoji = this.nameTextView;
                if (editTextEmoji != null) {
                    editTextEmoji.setText(text);
                } else {
                    this.nameToSet = text;
                }
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.currentStep != 1) {
            this.nameTextView.openKeyboard();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatDidFailCreate) {
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            this.donePressed = false;
        } else if (id == NotificationCenter.chatDidCreated) {
            AlertDialog alertDialog2 = this.progressDialog;
            if (alertDialog2 != null) {
                try {
                    alertDialog2.dismiss();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            int chat_id = ((Integer) args[0]).intValue();
            Bundle bundle = new Bundle();
            bundle.putInt("step", 1);
            bundle.putInt("chat_id", chat_id);
            bundle.putBoolean("canCreatePublic", this.canCreatePublic);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(chat_id, this.uploadedAvatar, this.avatar, this.avatarBig);
            }
            presentFragment(new ChannelCreateActivity(bundle), true);
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelCreateActivity.this.lambda$loadAdminedChannels$17$ChannelCreateActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadAdminedChannels$17$ChannelCreateActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$null$16$ChannelCreateActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$16$ChannelCreateActivity(TLObject response) {
        this.loadingAdminedChannels = false;
        if (response != null && getParentActivity() != null) {
            for (int a = 0; a < this.adminedChannelCells.size(); a++) {
                this.linearLayout.removeView(this.adminedChannelCells.get(a));
            }
            this.adminedChannelCells.clear();
            TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;
            for (int a2 = 0; a2 < res.chats.size(); a2++) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new View.OnClickListener() {
                    public final void onClick(View view) {
                        ChannelCreateActivity.this.lambda$null$15$ChannelCreateActivity(view);
                    }
                });
                TLRPC.Chat chat = (TLRPC.Chat) res.chats.get(a2);
                boolean z = true;
                if (a2 != res.chats.size() - 1) {
                    z = false;
                }
                adminedChannelCell.setChannel(chat, z);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
            }
            updatePrivatePublic();
        }
    }

    public /* synthetic */ void lambda$null$15$ChannelCreateActivity(View view) {
        TLRPC.Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (channel.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener(channel) {
            private final /* synthetic */ TLRPC.Chat f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.lambda$null$14$ChannelCreateActivity(this.f$1, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$14$ChannelCreateActivity(TLRPC.Chat channel, DialogInterface dialogInterface, int i) {
        TLRPC.TL_channels_updateUsername req1 = new TLRPC.TL_channels_updateUsername();
        req1.channel = MessagesController.getInputChannel(channel);
        req1.username = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.lambda$null$13$ChannelCreateActivity(tLObject, tL_error);
            }
        }, 64);
    }

    public /* synthetic */ void lambda$null$13$ChannelCreateActivity(TLObject response1, TLRPC.TL_error error1) {
        if (response1 instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChannelCreateActivity.this.lambda$null$12$ChannelCreateActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$12$ChannelCreateActivity() {
        this.canCreatePublic = true;
        if (this.descriptionTextView.length() > 0) {
            checkUserName(this.descriptionTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    /* access modifiers changed from: private */
    public boolean checkUserName(String name) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", R.string.LinkInvalidStartNumber));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (name == null || name.length() < 5) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", R.string.LinkInvalidShort));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else if (name.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", R.string.LinkInvalidLong));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", R.string.LinkChecking));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
            this.lastCheckName = name;
            $$Lambda$ChannelCreateActivity$yOAZMM01eJ2dD81K1o0sMfh2DJQ r0 = new Runnable(name) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChannelCreateActivity.this.lambda$checkUserName$20$ChannelCreateActivity(this.f$1);
                }
            };
            this.checkRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUserName$20$ChannelCreateActivity(String name) {
        TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
        req.username = name;
        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(name) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.lambda$null$19$ChannelCreateActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$null$19$ChannelCreateActivity(String name, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(name, error, response) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChannelCreateActivity.this.lambda$null$18$ChannelCreateActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$18$ChannelCreateActivity(String name, TLRPC.TL_error error, TLObject response) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(name)) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                    this.checkTextView.setText(LocaleController.getString("LinkInUse", R.string.LinkInUse));
                } else {
                    this.canCreatePublic = false;
                    loadAdminedChannels();
                }
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                this.lastNameAvailable = false;
                return;
            }
            this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, name));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
            this.lastNameAvailable = true;
        }
    }

    private void showErrorAlert(String error) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            char c = 65535;
            int hashCode = error.hashCode();
            if (hashCode != 288843630) {
                if (hashCode == 533175271 && error.equals("USERNAME_OCCUPIED")) {
                    c = 1;
                }
            } else if (error.equals("USERNAME_INVALID")) {
                c = 0;
            }
            if (c == 0) {
                builder.setMessage(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
            } else if (c != 1) {
                builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
            } else {
                builder.setMessage(LocaleController.getString("LinkInUse", R.string.LinkInUse));
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChannelCreateActivity.this.lambda$getThemeDescriptions$21$ChannelCreateActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGreenText), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_progressCircle), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackground), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackgroundChecked), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackground), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackgroundChecked), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteLinkText), new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, themeDescriptionDelegate, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$21$ChannelCreateActivity() {
        LinearLayout linearLayout3 = this.adminnedChannelsLayout;
        if (linearLayout3 != null) {
            int count = linearLayout3.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.adminnedChannelsLayout.getChildAt(a);
                if (child instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) child).update();
                }
            }
        }
    }
}

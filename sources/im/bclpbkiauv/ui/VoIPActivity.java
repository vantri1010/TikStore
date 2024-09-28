package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.voip.EncryptionKeyEmojifier;
import im.bclpbkiauv.messenger.voip.VoIPBaseService;
import im.bclpbkiauv.messenger.voip.VoIPController;
import im.bclpbkiauv.messenger.voip.VoIPService;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.VoIPActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.DarkAlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CorrectlyMeasuringTextView;
import im.bclpbkiauv.ui.components.CubicBezierInterpolator;
import im.bclpbkiauv.ui.components.IdenticonDrawable;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.voip.CallSwipeView;
import im.bclpbkiauv.ui.components.voip.CheckableImageView;
import im.bclpbkiauv.ui.components.voip.DarkTheme;
import im.bclpbkiauv.ui.components.voip.FabBackgroundDrawable;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class VoIPActivity extends Activity implements VoIPBaseService.StateListener, NotificationCenter.NotificationCenterDelegate {
    private static final String TAG = "hchat-voip-ui";
    /* access modifiers changed from: private */
    public View acceptBtn;
    /* access modifiers changed from: private */
    public CallSwipeView acceptSwipe;
    /* access modifiers changed from: private */
    public TextView accountNameText;
    private ImageView addMemberBtn;
    /* access modifiers changed from: private */
    public ImageView blurOverlayView1;
    /* access modifiers changed from: private */
    public ImageView blurOverlayView2;
    /* access modifiers changed from: private */
    public Bitmap blurredPhoto1;
    /* access modifiers changed from: private */
    public Bitmap blurredPhoto2;
    private LinearLayout bottomButtons;
    /* access modifiers changed from: private */
    public TextView brandingText;
    /* access modifiers changed from: private */
    public int callState;
    /* access modifiers changed from: private */
    public View cancelBtn;
    private ImageView chatBtn;
    /* access modifiers changed from: private */
    public FrameLayout content;
    /* access modifiers changed from: private */
    public Animator currentAcceptAnim;
    /* access modifiers changed from: private */
    public int currentAccount = -1;
    /* access modifiers changed from: private */
    public Animator currentDeclineAnim;
    /* access modifiers changed from: private */
    public View declineBtn;
    /* access modifiers changed from: private */
    public CallSwipeView declineSwipe;
    /* access modifiers changed from: private */
    public boolean didAcceptFromHere = false;
    /* access modifiers changed from: private */
    public TextView durationText;
    /* access modifiers changed from: private */
    public AnimatorSet ellAnimator;
    private TextAlphaSpan[] ellSpans;
    /* access modifiers changed from: private */
    public AnimatorSet emojiAnimator;
    boolean emojiExpanded;
    private TextView emojiExpandedText;
    boolean emojiTooltipVisible;
    /* access modifiers changed from: private */
    public LinearLayout emojiWrap;
    /* access modifiers changed from: private */
    public View endBtn;
    private FabBackgroundDrawable endBtnBg;
    /* access modifiers changed from: private */
    public View endBtnIcon;
    /* access modifiers changed from: private */
    public boolean firstStateChange = true;
    /* access modifiers changed from: private */
    public TextView hintTextView;
    /* access modifiers changed from: private */
    public boolean isIncomingWaiting;
    private ImageView[] keyEmojiViews = new ImageView[4];
    private boolean keyEmojiVisible;
    private String lastStateText;
    private CheckableImageView micToggle;
    /* access modifiers changed from: private */
    public TextView nameText;
    /* access modifiers changed from: private */
    public BackupImageView photoView;
    /* access modifiers changed from: private */
    public AnimatorSet retryAnim;
    /* access modifiers changed from: private */
    public boolean retrying;
    /* access modifiers changed from: private */
    public int signalBarsCount;
    private SignalBarsDrawable signalBarsDrawable;
    /* access modifiers changed from: private */
    public CheckableImageView spkToggle;
    /* access modifiers changed from: private */
    public TextView stateText;
    /* access modifiers changed from: private */
    public TextView stateText2;
    /* access modifiers changed from: private */
    public LinearLayout swipeViewsWrap;
    /* access modifiers changed from: private */
    public Animator textChangingAnim;
    /* access modifiers changed from: private */
    public Animator tooltipAnim;
    /* access modifiers changed from: private */
    public Runnable tooltipHider;
    /* access modifiers changed from: private */
    public TLRPC.User user;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        getWindow().addFlags(524288);
        super.onCreate(savedInstanceState);
        if (VoIPService.getSharedInstance() == null) {
            finish();
            return;
        }
        int account = VoIPService.getSharedInstance().getAccount();
        this.currentAccount = account;
        if (account == -1) {
            finish();
            return;
        }
        if ((getResources().getConfiguration().screenLayout & 15) < 3) {
            setRequestedOrientation(1);
        }
        View createContentView = createContentView();
        View contentView = createContentView;
        setContentView(createContentView);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(Integer.MIN_VALUE);
            getWindow().setStatusBarColor(0);
            getWindow().setNavigationBarColor(0);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(201326592);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        }
        TLRPC.User user2 = VoIPService.getSharedInstance().getUser();
        this.user = user2;
        if (user2.photo != null) {
            this.photoView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
                }

                public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
                    ImageReceiver.BitmapHolder bmp = imageReceiver.getBitmapSafe();
                    if (bmp != null) {
                        VoIPActivity.this.updateBlurredPhotos(bmp);
                    }
                }
            });
            this.photoView.setImage(ImageLocation.getForUser(this.user, true), (String) null, (Drawable) new ColorDrawable(-16777216), (Object) this.user);
            this.photoView.setLayerType(2, (Paint) null);
        } else {
            this.photoView.setVisibility(8);
            contentView.setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963}));
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setVolumeControlStream(0);
        this.nameText.setOnClickListener(new View.OnClickListener() {
            private int tapCount = 0;

            public void onClick(View v) {
                int i;
                if (BuildVars.VOIP_DEBUG || (i = this.tapCount) == 9) {
                    VoIPActivity.this.showDebugAlert();
                    this.tapCount = 0;
                    return;
                }
                this.tapCount = i + 1;
            }
        });
        this.endBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                VoIPActivity.this.endBtn.setEnabled(false);
                if (VoIPActivity.this.retrying) {
                    Intent intent = new Intent(VoIPActivity.this, VoIPService.class);
                    intent.putExtra("user_id", VoIPActivity.this.user.id);
                    intent.putExtra("is_outgoing", true);
                    intent.putExtra("start_incall_activity", false);
                    intent.putExtra("account", VoIPActivity.this.currentAccount);
                    try {
                        VoIPActivity.this.startService(intent);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    VoIPActivity.this.hideRetry();
                    VoIPActivity.this.endBtn.postDelayed(new Runnable() {
                        public void run() {
                            if (VoIPService.getSharedInstance() == null && !VoIPActivity.this.isFinishing()) {
                                VoIPActivity.this.endBtn.postDelayed(this, 100);
                            } else if (VoIPService.getSharedInstance() != null) {
                                VoIPService.getSharedInstance().registerStateListener(VoIPActivity.this);
                            }
                        }
                    }, 100);
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().hangUp();
                }
            }
        });
        this.spkToggle.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPActivity.this.lambda$onCreate$0$VoIPActivity(view);
            }
        });
        this.micToggle.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPActivity.this.lambda$onCreate$1$VoIPActivity(view);
            }
        });
        this.chatBtn.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPActivity.this.lambda$onCreate$2$VoIPActivity(view);
            }
        });
        this.spkToggle.setChecked(((AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO)).isSpeakerphoneOn());
        this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
        onAudioSettingsChanged();
        this.nameText.setText(ContactsController.formatName(this.user.first_name, this.user.last_name));
        VoIPService.getSharedInstance().registerStateListener(this);
        this.acceptSwipe.setListener(new CallSwipeView.Listener() {
            public void onDragComplete() {
                VoIPActivity.this.acceptSwipe.setEnabled(false);
                VoIPActivity.this.declineSwipe.setEnabled(false);
                if (VoIPService.getSharedInstance() == null) {
                    VoIPActivity.this.finish();
                    return;
                }
                boolean unused = VoIPActivity.this.didAcceptFromHere = true;
                if (Build.VERSION.SDK_INT < 23 || VoIPActivity.this.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                    VoIPService.getSharedInstance().acceptIncomingCall();
                    VoIPActivity.this.callAccepted();
                    return;
                }
                VoIPActivity.this.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
            }

            public void onDragStart() {
                if (VoIPActivity.this.currentDeclineAnim != null) {
                    VoIPActivity.this.currentDeclineAnim.cancel();
                }
                AnimatorSet set = new AnimatorSet();
                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[]{0.2f}), ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[]{0.2f})});
                set.setDuration(200);
                set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Animator unused = VoIPActivity.this.currentDeclineAnim = null;
                    }
                });
                Animator unused = VoIPActivity.this.currentDeclineAnim = set;
                set.start();
                VoIPActivity.this.declineSwipe.stopAnimatingArrows();
            }

            public void onDragCancel() {
                if (VoIPActivity.this.currentDeclineAnim != null) {
                    VoIPActivity.this.currentDeclineAnim.cancel();
                }
                AnimatorSet set = new AnimatorSet();
                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[]{1.0f})});
                set.setDuration(200);
                set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Animator unused = VoIPActivity.this.currentDeclineAnim = null;
                    }
                });
                Animator unused = VoIPActivity.this.currentDeclineAnim = set;
                set.start();
                VoIPActivity.this.declineSwipe.startAnimatingArrows();
            }
        });
        this.declineSwipe.setListener(new CallSwipeView.Listener() {
            public void onDragComplete() {
                VoIPActivity.this.acceptSwipe.setEnabled(false);
                VoIPActivity.this.declineSwipe.setEnabled(false);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall(4, (Runnable) null);
                } else {
                    VoIPActivity.this.finish();
                }
            }

            public void onDragStart() {
                if (VoIPActivity.this.currentAcceptAnim != null) {
                    VoIPActivity.this.currentAcceptAnim.cancel();
                }
                AnimatorSet set = new AnimatorSet();
                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[]{0.2f}), ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[]{0.2f})});
                set.setDuration(200);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Animator unused = VoIPActivity.this.currentAcceptAnim = null;
                    }
                });
                Animator unused = VoIPActivity.this.currentAcceptAnim = set;
                set.start();
                VoIPActivity.this.acceptSwipe.stopAnimatingArrows();
            }

            public void onDragCancel() {
                if (VoIPActivity.this.currentAcceptAnim != null) {
                    VoIPActivity.this.currentAcceptAnim.cancel();
                }
                AnimatorSet set = new AnimatorSet();
                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[]{1.0f})});
                set.setDuration(200);
                set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        Animator unused = VoIPActivity.this.currentAcceptAnim = null;
                    }
                });
                Animator unused = VoIPActivity.this.currentAcceptAnim = set;
                set.start();
                VoIPActivity.this.acceptSwipe.startAnimatingArrows();
            }
        });
        this.cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                VoIPActivity.this.finish();
            }
        });
        getWindow().getDecorView().setKeepScreenOn(true);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
        this.hintTextView.setText(LocaleController.formatString("CallEmojiKeyTooltip", R.string.CallEmojiKeyTooltip, this.user.first_name));
        this.emojiExpandedText.setText(LocaleController.formatString("CallEmojiKeyTooltip", R.string.CallEmojiKeyTooltip, this.user.first_name));
        if (((AccessibilityManager) getSystemService("accessibility")).isTouchExplorationEnabled()) {
            this.nameText.postDelayed(new Runnable() {
                public void run() {
                    VoIPActivity.this.nameText.sendAccessibilityEvent(8);
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$onCreate$0$VoIPActivity(View v) {
        VoIPService svc = VoIPService.getSharedInstance();
        if (svc != null) {
            svc.toggleSpeakerphoneOrShowRouteSheet(this);
        }
    }

    public /* synthetic */ void lambda$onCreate$1$VoIPActivity(View v) {
        if (VoIPService.getSharedInstance() == null) {
            finish();
            return;
        }
        boolean checked = !this.micToggle.isChecked();
        this.micToggle.setChecked(checked);
        VoIPService.getSharedInstance().setMicMute(checked);
    }

    public /* synthetic */ void lambda$onCreate$2$VoIPActivity(View v) {
        if (this.isIncomingWaiting) {
            showMessagesSheet();
            return;
        }
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setFlags(32768);
        intent.putExtra("userId", this.user.id);
        startActivity(intent);
        finish();
    }

    private View createContentView() {
        FrameLayout content2 = new FrameLayout(this) {
            private void setNegativeMargins(Rect insets, FrameLayout.LayoutParams lp) {
                lp.topMargin = -insets.top;
                lp.bottomMargin = -insets.bottom;
                lp.leftMargin = -insets.left;
                lp.rightMargin = -insets.right;
            }

            /* access modifiers changed from: protected */
            public boolean fitSystemWindows(Rect insets) {
                setNegativeMargins(insets, (FrameLayout.LayoutParams) VoIPActivity.this.photoView.getLayoutParams());
                setNegativeMargins(insets, (FrameLayout.LayoutParams) VoIPActivity.this.blurOverlayView1.getLayoutParams());
                setNegativeMargins(insets, (FrameLayout.LayoutParams) VoIPActivity.this.blurOverlayView2.getLayoutParams());
                return super.fitSystemWindows(insets);
            }
        };
        content2.setBackgroundColor(0);
        content2.setFitsSystemWindows(true);
        content2.setClipToPadding(false);
        BackupImageView photo = new BackupImageView(this) {
            private Drawable bottomGradient = getResources().getDrawable(R.drawable.gradient_bottom);
            private Paint paint = new Paint();
            private Drawable topGradient = getResources().getDrawable(R.drawable.gradient_top);

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                this.paint.setColor(1275068416);
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.paint);
                this.topGradient.setBounds(0, 0, getWidth(), AndroidUtilities.dp(170.0f));
                this.topGradient.setAlpha(128);
                this.topGradient.draw(canvas);
                this.bottomGradient.setBounds(0, getHeight() - AndroidUtilities.dp(220.0f), getWidth(), getHeight());
                this.bottomGradient.setAlpha(178);
                this.bottomGradient.draw(canvas);
            }
        };
        this.photoView = photo;
        content2.addView(photo);
        ImageView imageView = new ImageView(this);
        this.blurOverlayView1 = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.blurOverlayView1.setAlpha(0.0f);
        content2.addView(this.blurOverlayView1);
        ImageView imageView2 = new ImageView(this);
        this.blurOverlayView2 = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.blurOverlayView2.setAlpha(0.0f);
        content2.addView(this.blurOverlayView2);
        TextView branding = new TextView(this);
        branding.setTextColor(-855638017);
        branding.setText(LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding));
        Drawable logo = getResources().getDrawable(R.mipmap.notification).mutate();
        logo.setAlpha(204);
        logo.setBounds(0, 0, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f));
        SignalBarsDrawable signalBarsDrawable2 = new SignalBarsDrawable();
        this.signalBarsDrawable = signalBarsDrawable2;
        signalBarsDrawable2.setBounds(0, 0, signalBarsDrawable2.getIntrinsicWidth(), this.signalBarsDrawable.getIntrinsicHeight());
        branding.setCompoundDrawables(LocaleController.isRTL ? this.signalBarsDrawable : logo, (Drawable) null, LocaleController.isRTL ? logo : this.signalBarsDrawable, (Drawable) null);
        branding.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        branding.setGravity(LocaleController.isRTL ? 5 : 3);
        branding.setCompoundDrawablePadding(AndroidUtilities.dp(5.0f));
        branding.setTextSize(1, 14.0f);
        content2.addView(branding, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 18.0f, 18.0f, 0.0f));
        this.brandingText = branding;
        TextView name = new TextView(this);
        name.setSingleLine();
        name.setTextColor(-1);
        name.setTextSize(1, 40.0f);
        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setGravity(LocaleController.isRTL ? 5 : 3);
        name.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), 1275068416);
        name.setTypeface(Typeface.create("sans-serif-light", 0));
        this.nameText = name;
        content2.addView(name, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 16.0f, 43.0f, 18.0f, 0.0f));
        TextView state = new TextView(this);
        state.setTextColor(-855638017);
        state.setSingleLine();
        state.setEllipsize(TextUtils.TruncateAt.END);
        state.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        state.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), 1275068416);
        state.setTextSize(1, 15.0f);
        state.setGravity(LocaleController.isRTL ? 5 : 3);
        this.stateText = state;
        content2.addView(state, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.durationText = state;
        TextView state2 = new TextView(this);
        state2.setTextColor(-855638017);
        state2.setSingleLine();
        state2.setEllipsize(TextUtils.TruncateAt.END);
        state2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        state2.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), 1275068416);
        state2.setTextSize(1, 15.0f);
        state2.setGravity(LocaleController.isRTL ? 5 : 3);
        state2.setVisibility(8);
        this.stateText2 = state2;
        content2.addView(state2, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.ellSpans = new TextAlphaSpan[]{new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(0);
        content2.addView(buttons, LayoutHelper.createFrame(-1, -2, 80));
        TextView accountName = new TextView(this);
        accountName.setTextColor(-855638017);
        accountName.setSingleLine();
        accountName.setEllipsize(TextUtils.TruncateAt.END);
        accountName.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), 1275068416);
        accountName.setTextSize(1, 15.0f);
        accountName.setGravity(LocaleController.isRTL ? 5 : 3);
        this.accountNameText = accountName;
        content2.addView(accountName, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 18.0f, 120.0f, 18.0f, 0.0f));
        CheckableImageView mic = new CheckableImageView(this);
        mic.setBackgroundResource(R.drawable.bg_voip_icon_btn);
        Drawable micIcon = getResources().getDrawable(R.drawable.ic_mic_off_white_24dp).mutate();
        mic.setAlpha(204);
        mic.setImageDrawable(micIcon);
        mic.setScaleType(ImageView.ScaleType.CENTER);
        mic.setContentDescription(LocaleController.getString("AccDescrMuteMic", R.string.AccDescrMuteMic));
        FrameLayout wrap = new FrameLayout(this);
        this.micToggle = mic;
        wrap.addView(mic, LayoutHelper.createFrame(38.0f, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        buttons.addView(wrap, LayoutHelper.createLinear(0, -2, 1.0f));
        ImageView chat = new ImageView(this);
        Drawable chatIcon = getResources().getDrawable(R.drawable.ic_chat_bubble_white_24dp).mutate();
        chatIcon.setAlpha(204);
        chat.setImageDrawable(chatIcon);
        chat.setScaleType(ImageView.ScaleType.CENTER);
        chat.setContentDescription(LocaleController.getString("AccDescrOpenChat", R.string.AccDescrOpenChat));
        FrameLayout wrap2 = new FrameLayout(this);
        this.chatBtn = chat;
        wrap2.addView(chat, LayoutHelper.createFrame(38.0f, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        Drawable micIcon2 = micIcon;
        TextView accountName2 = accountName;
        buttons.addView(wrap2, LayoutHelper.createLinear(0, -2, 1.0f));
        CheckableImageView speaker = new CheckableImageView(this);
        speaker.setBackgroundResource(R.drawable.bg_voip_icon_btn);
        Drawable speakerIcon = getResources().getDrawable(R.drawable.ic_volume_up_white_24dp).mutate();
        speaker.setAlpha(204);
        speaker.setImageDrawable(speakerIcon);
        speaker.setScaleType(ImageView.ScaleType.CENTER);
        speaker.setContentDescription(LocaleController.getString("VoipAudioRoutingSpeaker", R.string.VoipAudioRoutingSpeaker));
        FrameLayout wrap3 = new FrameLayout(this);
        this.spkToggle = speaker;
        wrap3.addView(speaker, LayoutHelper.createFrame(38.0f, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        Drawable speakerIcon2 = speakerIcon;
        buttons.addView(wrap3, LayoutHelper.createLinear(0, -2, 1.0f));
        this.bottomButtons = buttons;
        LinearLayout swipesWrap = new LinearLayout(this);
        swipesWrap.setOrientation(0);
        CallSwipeView acceptSwipe2 = new CallSwipeView(this);
        acceptSwipe2.setColor(-12207027);
        FrameLayout wrap4 = wrap3;
        acceptSwipe2.setContentDescription(LocaleController.getString("Accept", R.string.Accept));
        this.acceptSwipe = acceptSwipe2;
        swipesWrap.addView(acceptSwipe2, LayoutHelper.createLinear(-1, 70, 1.0f, 4, 4, -35, 4));
        CallSwipeView declineSwipe2 = new CallSwipeView(this);
        declineSwipe2.setColor(-1696188);
        LinearLayout linearLayout = buttons;
        declineSwipe2.setContentDescription(LocaleController.getString("Decline", R.string.Decline));
        this.declineSwipe = declineSwipe2;
        swipesWrap.addView(declineSwipe2, LayoutHelper.createLinear(-1, 70, 1.0f, -35, 4, 4, 4));
        this.swipeViewsWrap = swipesWrap;
        content2.addView(swipesWrap, LayoutHelper.createFrame(-1.0f, -2.0f, 80, 20.0f, 0.0f, 20.0f, 68.0f));
        ImageView acceptBtn2 = new ImageView(this);
        FabBackgroundDrawable acceptBtnBg = new FabBackgroundDrawable();
        LinearLayout swipesWrap2 = swipesWrap;
        acceptBtnBg.setColor(-12207027);
        acceptBtn2.setBackgroundDrawable(acceptBtnBg);
        acceptBtn2.setImageResource(R.drawable.ic_call_end_white_36dp);
        acceptBtn2.setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        FabBackgroundDrawable acceptBtnBg2 = acceptBtnBg;
        Drawable chatIcon2 = chatIcon;
        matrix.setTranslate((float) AndroidUtilities.dp(17.0f), (float) AndroidUtilities.dp(17.0f));
        TextView textView = state2;
        matrix.postRotate(-135.0f, (float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(35.0f));
        acceptBtn2.setImageMatrix(matrix);
        this.acceptBtn = acceptBtn2;
        content2.addView(acceptBtn2, LayoutHelper.createFrame(78.0f, 78.0f, 83, 20.0f, 0.0f, 0.0f, 68.0f));
        ImageView declineBtn2 = new ImageView(this);
        FabBackgroundDrawable rejectBtnBg = new FabBackgroundDrawable();
        rejectBtnBg.setColor(-1696188);
        declineBtn2.setBackgroundDrawable(rejectBtnBg);
        declineBtn2.setImageResource(R.drawable.ic_call_end_white_36dp);
        declineBtn2.setScaleType(ImageView.ScaleType.CENTER);
        this.declineBtn = declineBtn2;
        content2.addView(declineBtn2, LayoutHelper.createFrame(78.0f, 78.0f, 85, 0.0f, 0.0f, 20.0f, 68.0f));
        acceptSwipe2.setViewToDrag(acceptBtn2, false);
        declineSwipe2.setViewToDrag(declineBtn2, true);
        FrameLayout end = new FrameLayout(this);
        Matrix matrix2 = matrix;
        FabBackgroundDrawable fabBackgroundDrawable = rejectBtnBg;
        FabBackgroundDrawable endBtnBg2 = new FabBackgroundDrawable();
        endBtnBg2.setColor(-1696188);
        this.endBtnBg = endBtnBg2;
        end.setBackgroundDrawable(endBtnBg2);
        ImageView endInner = new ImageView(this);
        CallSwipeView callSwipeView = declineSwipe2;
        endInner.setImageResource(R.drawable.ic_call_end_white_36dp);
        endInner.setScaleType(ImageView.ScaleType.CENTER);
        this.endBtnIcon = endInner;
        CallSwipeView acceptSwipe3 = acceptSwipe2;
        end.addView(endInner, LayoutHelper.createFrame(70, 70.0f));
        end.setForeground(getResources().getDrawable(R.drawable.fab_highlight_dark));
        end.setContentDescription(LocaleController.getString("VoipEndCall", R.string.VoipEndCall));
        this.endBtn = end;
        content2.addView(end, LayoutHelper.createFrame(78.0f, 78.0f, 81, 0.0f, 0.0f, 0.0f, 68.0f));
        ImageView cancelBtn2 = new ImageView(this);
        FabBackgroundDrawable cancelBtnBg = new FabBackgroundDrawable();
        FrameLayout end2 = end;
        cancelBtnBg.setColor(-1);
        cancelBtn2.setBackgroundDrawable(cancelBtnBg);
        cancelBtn2.setImageResource(R.drawable.edit_cancel);
        cancelBtn2.setColorFilter(-1996488704);
        cancelBtn2.setScaleType(ImageView.ScaleType.CENTER);
        cancelBtn2.setVisibility(8);
        ImageView endInner2 = endInner;
        cancelBtn2.setContentDescription(LocaleController.getString("Cancel", R.string.Cancel));
        this.cancelBtn = cancelBtn2;
        content2.addView(cancelBtn2, LayoutHelper.createFrame(78.0f, 78.0f, 83, 52.0f, 0.0f, 0.0f, 68.0f));
        LinearLayout linearLayout2 = new LinearLayout(this);
        this.emojiWrap = linearLayout2;
        linearLayout2.setOrientation(0);
        this.emojiWrap.setClipToPadding(false);
        this.emojiWrap.setPivotX(0.0f);
        this.emojiWrap.setPivotY(0.0f);
        FabBackgroundDrawable cancelBtnBg2 = cancelBtnBg;
        ImageView cancelBtn3 = cancelBtn2;
        ImageView acceptBtn3 = acceptBtn2;
        this.emojiWrap.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f));
        int i = 0;
        while (i < 4) {
            ImageView emoji = new ImageView(this);
            emoji.setScaleType(ImageView.ScaleType.FIT_XY);
            this.emojiWrap.addView(emoji, LayoutHelper.createLinear(22, 22, i == 0 ? 0.0f : 4.0f, 0.0f, 0.0f, 0.0f));
            this.keyEmojiViews[i] = emoji;
            i++;
        }
        this.emojiWrap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (VoIPActivity.this.emojiTooltipVisible) {
                    VoIPActivity.this.setEmojiTooltipVisible(false);
                    if (VoIPActivity.this.tooltipHider != null) {
                        VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                        Runnable unused = VoIPActivity.this.tooltipHider = null;
                    }
                }
                VoIPActivity voIPActivity = VoIPActivity.this;
                voIPActivity.setEmojiExpanded(!voIPActivity.emojiExpanded);
            }
        });
        content2.addView(this.emojiWrap, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 3 : 5) | 48));
        this.emojiWrap.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (VoIPActivity.this.emojiExpanded) {
                    return false;
                }
                if (VoIPActivity.this.tooltipHider != null) {
                    VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                    Runnable unused = VoIPActivity.this.tooltipHider = null;
                }
                VoIPActivity voIPActivity = VoIPActivity.this;
                voIPActivity.setEmojiTooltipVisible(!voIPActivity.emojiTooltipVisible);
                if (VoIPActivity.this.emojiTooltipVisible) {
                    VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new Runnable() {
                        public void run() {
                            Runnable unused = VoIPActivity.this.tooltipHider = null;
                            VoIPActivity.this.setEmojiTooltipVisible(false);
                        }
                    }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                }
                return true;
            }
        });
        TextView textView2 = new TextView(this);
        this.emojiExpandedText = textView2;
        textView2.setTextSize(1, 16.0f);
        this.emojiExpandedText.setTextColor(-1);
        this.emojiExpandedText.setGravity(17);
        this.emojiExpandedText.setAlpha(0.0f);
        content2.addView(this.emojiExpandedText, LayoutHelper.createFrame(-1.0f, -2.0f, 17, 10.0f, 32.0f, 10.0f, 0.0f));
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(this);
        this.hintTextView = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(3.0f), -231525581));
        this.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        this.hintTextView.setGravity(17);
        this.hintTextView.setMaxWidth(AndroidUtilities.dp(300.0f));
        this.hintTextView.setAlpha(0.0f);
        content2.addView(this.hintTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 53, 0.0f, 42.0f, 10.0f, 0.0f));
        int ellMaxAlpha = this.stateText.getPaint().getAlpha();
        AnimatorSet animatorSet = new AnimatorSet();
        this.ellAnimator = animatorSet;
        FabBackgroundDrawable fabBackgroundDrawable2 = acceptBtnBg2;
        FrameLayout frameLayout = end2;
        ImageView imageView3 = endInner2;
        LinearLayout linearLayout3 = swipesWrap2;
        Matrix matrix3 = matrix2;
        LinearLayout linearLayout4 = linearLayout3;
        FabBackgroundDrawable fabBackgroundDrawable3 = cancelBtnBg2;
        Drawable drawable = micIcon2;
        FrameLayout frameLayout2 = wrap4;
        Drawable drawable2 = drawable;
        Drawable drawable3 = speakerIcon2;
        TextView textView3 = accountName2;
        CallSwipeView callSwipeView2 = acceptSwipe3;
        ImageView imageView4 = cancelBtn3;
        ImageView imageView5 = acceptBtn3;
        Drawable drawable4 = chatIcon2;
        Drawable chatIcon3 = endBtnBg2;
        Animator[] animatorArr = new Animator[6];
        animatorArr[0] = createAlphaAnimator(this.ellSpans[0], 0, ellMaxAlpha, 0, 300);
        ImageView imageView6 = declineBtn2;
        Animator[] animatorArr2 = animatorArr;
        animatorArr2[1] = createAlphaAnimator(this.ellSpans[1], 0, ellMaxAlpha, 150, 300);
        animatorArr2[2] = createAlphaAnimator(this.ellSpans[2], 0, ellMaxAlpha, 300, 300);
        int i2 = ellMaxAlpha;
        animatorArr2[3] = createAlphaAnimator(this.ellSpans[0], i2, 0, 1000, 400);
        animatorArr2[4] = createAlphaAnimator(this.ellSpans[1], i2, 0, 1000, 400);
        animatorArr2[5] = createAlphaAnimator(this.ellSpans[2], i2, 0, 1000, 400);
        animatorSet.playTogether(animatorArr2);
        this.ellAnimator.addListener(new AnimatorListenerAdapter() {
            private Runnable restarter = new Runnable() {
                public void run() {
                    if (!VoIPActivity.this.isFinishing()) {
                        VoIPActivity.this.ellAnimator.start();
                    }
                }
            };

            public void onAnimationEnd(Animator animation) {
                if (!VoIPActivity.this.isFinishing()) {
                    VoIPActivity.this.content.postDelayed(this.restarter, 300);
                }
            }
        });
        content2.setClipChildren(false);
        this.content = content2;
        return content2;
    }

    private ObjectAnimator createAlphaAnimator(Object target, int startVal, int endVal, int startDelay, int duration) {
        ObjectAnimator a = ObjectAnimator.ofInt(target, "alpha", new int[]{startVal, endVal});
        a.setDuration((long) duration);
        a.setStartDelay((long) startDelay);
        a.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return a;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        if (this.emojiExpanded) {
            setEmojiExpanded(false);
        } else if (!this.isIncomingWaiting) {
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().onUIForegroundStateChanged(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.retrying) {
            finish();
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().onUIForegroundStateChanged(false);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 101) {
            return;
        }
        if (VoIPService.getSharedInstance() == null) {
            finish();
        } else if (grantResults.length > 0 && grantResults[0] == 0) {
            VoIPService.getSharedInstance().acceptIncomingCall();
            callAccepted();
        } else if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            VoIPService.getSharedInstance().declineIncomingCall();
            VoIPHelper.permissionDenied(this, new Runnable() {
                public void run() {
                    VoIPActivity.this.finish();
                }
            });
        } else {
            this.acceptSwipe.reset();
        }
    }

    /* access modifiers changed from: private */
    public void updateKeyView() {
        if (VoIPService.getSharedInstance() != null) {
            new IdenticonDrawable().setColors(new int[]{ViewCompat.MEASURED_SIZE_MASK, -1, -1711276033, 872415231});
            TLRPC.EncryptedChat encryptedChat = new TLRPC.TL_encryptedChat();
            try {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                buf.write(VoIPService.getSharedInstance().getEncryptionKey());
                buf.write(VoIPService.getSharedInstance().getGA());
                encryptedChat.auth_key = buf.toByteArray();
            } catch (Exception e) {
            }
            String[] emoji = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length));
            LinearLayout linearLayout = this.emojiWrap;
            linearLayout.setContentDescription(LocaleController.getString("EncryptionKey", R.string.EncryptionKey) + ", " + TextUtils.join(", ", emoji));
            for (int i = 0; i < 4; i++) {
                Drawable drawable = Emoji.getEmojiDrawable(emoji[i]);
                if (drawable != null) {
                    drawable.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
                    this.keyEmojiViews[i].setImageDrawable(drawable);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public CharSequence getFormattedDebugString() {
        int indexOf;
        String in = VoIPService.getSharedInstance().getDebugString();
        SpannableString ss = new SpannableString(in);
        int offset = 0;
        do {
            int lineEnd = in.indexOf(10, offset + 1);
            if (lineEnd == -1) {
                lineEnd = in.length();
            }
            String line = in.substring(offset, lineEnd);
            if (line.contains("IN_USE")) {
                ss.setSpan(new ForegroundColorSpan(-16711936), offset, lineEnd, 0);
            } else if (line.contains(": ")) {
                ss.setSpan(new ForegroundColorSpan(-1426063361), offset, line.indexOf(58) + offset + 1, 0);
            }
            indexOf = in.indexOf(10, offset + 1);
            offset = indexOf;
        } while (indexOf != -1);
        return ss;
    }

    /* access modifiers changed from: private */
    public void showDebugAlert() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().forceRating();
            final LinearLayout debugOverlay = new LinearLayout(this);
            debugOverlay.setOrientation(1);
            debugOverlay.setBackgroundColor(-872415232);
            int pad = AndroidUtilities.dp(16.0f);
            debugOverlay.setPadding(pad, pad * 2, pad, pad * 2);
            TextView title = new TextView(this);
            title.setTextColor(-1);
            title.setTextSize(1, 15.0f);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setGravity(17);
            title.setText("libtgvoip v" + VoIPController.getVersion());
            debugOverlay.addView(title, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 16.0f));
            ScrollView scroll = new ScrollView(this);
            final TextView debugText = new TextView(this);
            debugText.setTypeface(Typeface.MONOSPACE);
            debugText.setTextSize(1, 11.0f);
            debugText.setMaxWidth(AndroidUtilities.dp(350.0f));
            debugText.setTextColor(-1);
            debugText.setText(getFormattedDebugString());
            scroll.addView(debugText);
            debugOverlay.addView(scroll, LayoutHelper.createLinear(-1, -1, 1.0f));
            TextView closeBtn = new TextView(this);
            closeBtn.setBackgroundColor(-1);
            closeBtn.setTextColor(-16777216);
            closeBtn.setPadding(pad, pad, pad, pad);
            closeBtn.setTextSize(1, 15.0f);
            closeBtn.setText(LocaleController.getString("Close", R.string.Close));
            debugOverlay.addView(closeBtn, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
            final WindowManager wm = (WindowManager) getSystemService("window");
            wm.addView(debugOverlay, new WindowManager.LayoutParams(-1, -1, 1000, 0, -3));
            closeBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    wm.removeView(debugOverlay);
                }
            });
            debugOverlay.postDelayed(new Runnable() {
                public void run() {
                    if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
                        debugText.setText(VoIPActivity.this.getFormattedDebugString());
                        debugOverlay.postDelayed(this, 500);
                    }
                }
            }, 500);
        }
    }

    /* access modifiers changed from: private */
    public void startUpdatingCallDuration() {
        new Runnable() {
            public void run() {
                String str;
                if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
                    if (VoIPActivity.this.callState == 3 || VoIPActivity.this.callState == 5) {
                        long duration = VoIPService.getSharedInstance().getCallDuration() / 1000;
                        TextView access$2800 = VoIPActivity.this.durationText;
                        if (duration > 3600) {
                            str = String.format("%d:%02d:%02d", new Object[]{Long.valueOf(duration / 3600), Long.valueOf((duration % 3600) / 60), Long.valueOf(duration % 60)});
                        } else {
                            str = String.format("%d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60)});
                        }
                        access$2800.setText(str);
                        VoIPActivity.this.durationText.postDelayed(this, 500);
                    }
                }
            }
        }.run();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.isIncomingWaiting || (keyCode != 25 && keyCode != 24)) {
            return super.onKeyDown(keyCode, event);
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopRinging();
            return true;
        }
        finish();
        return true;
    }

    /* access modifiers changed from: private */
    public void callAccepted() {
        ObjectAnimator colorAnim;
        this.endBtn.setVisibility(0);
        if (VoIPService.getSharedInstance().hasEarpiece()) {
            this.spkToggle.setVisibility(0);
        } else {
            this.spkToggle.setVisibility(8);
        }
        this.bottomButtons.setVisibility(0);
        if (this.didAcceptFromHere) {
            this.acceptBtn.setVisibility(8);
            if (Build.VERSION.SDK_INT >= 21) {
                colorAnim = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
            } else {
                colorAnim = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
                colorAnim.setEvaluator(new ArgbEvaluator());
            }
            AnimatorSet set = new AnimatorSet();
            AnimatorSet decSet = new AnimatorSet();
            decSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f}), colorAnim});
            decSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            decSet.setDuration(500);
            AnimatorSet accSet = new AnimatorSet();
            accSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.swipeViewsWrap, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.accountNameText, "alpha", new float[]{0.0f})});
            accSet.setInterpolator(CubicBezierInterpolator.EASE_IN);
            accSet.setDuration(125);
            set.playTogether(new Animator[]{decSet, accSet});
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                    VoIPActivity.this.declineBtn.setVisibility(8);
                    VoIPActivity.this.accountNameText.setVisibility(8);
                }
            });
            set.start();
            return;
        }
        AnimatorSet set2 = new AnimatorSet();
        AnimatorSet decSet2 = new AnimatorSet();
        decSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.bottomButtons, "alpha", new float[]{0.0f, 1.0f})});
        decSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        decSet2.setDuration(500);
        AnimatorSet accSet2 = new AnimatorSet();
        accSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.swipeViewsWrap, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.acceptBtn, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.accountNameText, "alpha", new float[]{0.0f})});
        accSet2.setInterpolator(CubicBezierInterpolator.EASE_IN);
        accSet2.setDuration(125);
        set2.playTogether(new Animator[]{decSet2, accSet2});
        set2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                VoIPActivity.this.declineBtn.setVisibility(8);
                VoIPActivity.this.acceptBtn.setVisibility(8);
                VoIPActivity.this.accountNameText.setVisibility(8);
            }
        });
        set2.start();
    }

    /* access modifiers changed from: private */
    public void showRetry() {
        ObjectAnimator colorAnim;
        AnimatorSet animatorSet = this.retryAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.endBtn.setEnabled(false);
        this.retrying = true;
        this.cancelBtn.setVisibility(0);
        this.cancelBtn.setAlpha(0.0f);
        AnimatorSet set = new AnimatorSet();
        if (Build.VERSION.SDK_INT >= 21) {
            colorAnim = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-1696188, -12207027});
        } else {
            colorAnim = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-1696188, -12207027});
            colorAnim.setEvaluator(new ArgbEvaluator());
        }
        set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f, (float) (((this.content.getWidth() / 2) - AndroidUtilities.dp(52.0f)) - (this.endBtn.getWidth() / 2))}), colorAnim, ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{0.0f, -135.0f})});
        set.setStartDelay(200);
        set.setDuration(300);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AnimatorSet unused = VoIPActivity.this.retryAnim = null;
                VoIPActivity.this.endBtn.setEnabled(true);
            }
        });
        this.retryAnim = set;
        set.start();
    }

    /* access modifiers changed from: private */
    public void hideRetry() {
        ObjectAnimator colorAnim;
        AnimatorSet animatorSet = this.retryAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.retrying = false;
        if (Build.VERSION.SDK_INT >= 21) {
            colorAnim = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
        } else {
            colorAnim = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
            colorAnim.setEvaluator(new ArgbEvaluator());
        }
        AnimatorSet set = new AnimatorSet();
        set.playTogether(new Animator[]{colorAnim, ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f}), ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f}), ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[]{0.0f})});
        set.setStartDelay(200);
        set.setDuration(300);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.cancelBtn.setVisibility(8);
                VoIPActivity.this.endBtn.setEnabled(true);
                AnimatorSet unused = VoIPActivity.this.retryAnim = null;
            }
        });
        this.retryAnim = set;
        set.start();
    }

    public void onStateChanged(final int state) {
        final int prevState = this.callState;
        this.callState = state;
        runOnUiThread(new Runnable() {
            public void run() {
                int count;
                int i;
                boolean wasFirstStateChange = VoIPActivity.this.firstStateChange;
                if (VoIPActivity.this.firstStateChange) {
                    VoIPActivity.this.spkToggle.setChecked(((AudioManager) VoIPActivity.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).isSpeakerphoneOn());
                    if (VoIPActivity.this.isIncomingWaiting = state == 15) {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(0);
                        VoIPActivity.this.endBtn.setVisibility(8);
                        VoIPActivity.this.acceptSwipe.startAnimatingArrows();
                        VoIPActivity.this.declineSwipe.startAnimatingArrows();
                        if (UserConfig.getActivatedAccountsCount() > 1) {
                            TLRPC.User self = UserConfig.getInstance(VoIPActivity.this.currentAccount).getCurrentUser();
                            VoIPActivity.this.accountNameText.setText(LocaleController.formatString("VoipAnsweringAsAccount", R.string.VoipAnsweringAsAccount, ContactsController.formatName(self.first_name, self.last_name)));
                        } else {
                            VoIPActivity.this.accountNameText.setVisibility(8);
                        }
                        VoIPActivity.this.getWindow().addFlags(2097152);
                        VoIPService svc = VoIPService.getSharedInstance();
                        if (svc != null) {
                            svc.startRingtoneAndVibration();
                        }
                        VoIPActivity.this.setTitle(LocaleController.getString("VoipIncoming", R.string.VoipIncoming));
                    } else {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                        VoIPActivity.this.acceptBtn.setVisibility(8);
                        VoIPActivity.this.declineBtn.setVisibility(8);
                        VoIPActivity.this.accountNameText.setVisibility(8);
                        VoIPActivity.this.getWindow().clearFlags(2097152);
                    }
                    if (state != 3) {
                        VoIPActivity.this.emojiWrap.setVisibility(8);
                    }
                    boolean unused = VoIPActivity.this.firstStateChange = false;
                }
                if (!(!VoIPActivity.this.isIncomingWaiting || (i = state) == 15 || i == 11 || i == 10)) {
                    boolean unused2 = VoIPActivity.this.isIncomingWaiting = false;
                    if (!VoIPActivity.this.didAcceptFromHere) {
                        VoIPActivity.this.callAccepted();
                    }
                }
                int i2 = state;
                if (i2 == 15) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", R.string.VoipIncoming), false);
                    VoIPActivity.this.getWindow().addFlags(2097152);
                } else if (i2 == 1 || i2 == 2) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", R.string.VoipConnecting), true);
                } else if (i2 == 12) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", R.string.VoipExchangingKeys), true);
                } else if (i2 == 13) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", R.string.VoipWaiting), true);
                } else if (i2 == 16) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", R.string.VoipRinging), true);
                } else if (i2 == 14) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", R.string.VoipRequesting), true);
                } else if (i2 == 10) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", R.string.VoipHangingUp), true);
                    VoIPActivity.this.endBtnIcon.setAlpha(0.5f);
                    VoIPActivity.this.endBtn.setEnabled(false);
                } else if (i2 == 11) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", R.string.VoipCallEnded), false);
                    VoIPActivity.this.stateText.postDelayed(new Runnable() {
                        public void run() {
                            VoIPActivity.this.finish();
                        }
                    }, 200);
                } else if (i2 == 17) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", R.string.VoipBusy), false);
                    VoIPActivity.this.showRetry();
                } else if (i2 == 3 || i2 == 5) {
                    VoIPActivity.this.setTitle((CharSequence) null);
                    if (!wasFirstStateChange && state == 3 && (count = MessagesController.getGlobalMainSettings().getInt("call_emoji_tooltip_count", 0)) < 3) {
                        VoIPActivity.this.setEmojiTooltipVisible(true);
                        VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new Runnable() {
                            public void run() {
                                Runnable unused = VoIPActivity.this.tooltipHider = null;
                                VoIPActivity.this.setEmojiTooltipVisible(false);
                            }
                        }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                        MessagesController.getGlobalMainSettings().edit().putInt("call_emoji_tooltip_count", count + 1).commit();
                    }
                    int count2 = prevState;
                    if (!(count2 == 3 || count2 == 5)) {
                        VoIPActivity.this.setStateTextAnimated("0:00", false);
                        VoIPActivity.this.startUpdatingCallDuration();
                        VoIPActivity.this.updateKeyView();
                        if (VoIPActivity.this.emojiWrap.getVisibility() != 0) {
                            VoIPActivity.this.emojiWrap.setVisibility(0);
                            VoIPActivity.this.emojiWrap.setAlpha(0.0f);
                            VoIPActivity.this.emojiWrap.animate().alpha(1.0f).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
                        }
                    }
                } else if (i2 == 4) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", R.string.VoipFailed), false);
                    int lastError = VoIPService.getSharedInstance() != null ? VoIPService.getSharedInstance().getLastError() : 0;
                    if (lastError == 1) {
                        VoIPActivity voIPActivity = VoIPActivity.this;
                        voIPActivity.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", R.string.VoipPeerIncompatible, ContactsController.formatName(voIPActivity.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (lastError == -1) {
                        VoIPActivity voIPActivity2 = VoIPActivity.this;
                        voIPActivity2.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", R.string.VoipPeerOutdated, ContactsController.formatName(voIPActivity2.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (lastError == -2) {
                        VoIPActivity voIPActivity3 = VoIPActivity.this;
                        voIPActivity3.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", R.string.CallNotAvailable, ContactsController.formatName(voIPActivity3.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (lastError == 3) {
                        VoIPActivity.this.showErrorDialog("Error initializing audio hardware");
                    } else if (lastError == -3) {
                        VoIPActivity.this.finish();
                    } else if (lastError == -5) {
                        VoIPActivity.this.showErrorDialog(LocaleController.getString("VoipErrorUnknown", R.string.VoipErrorUnknown));
                    } else {
                        VoIPActivity.this.stateText.postDelayed(new Runnable() {
                            public void run() {
                                VoIPActivity.this.finish();
                            }
                        }, 1000);
                    }
                }
                VoIPActivity.this.brandingText.invalidate();
            }
        });
    }

    public void onSignalBarsCountChanged(final int count) {
        runOnUiThread(new Runnable() {
            public void run() {
                int unused = VoIPActivity.this.signalBarsCount = count;
                VoIPActivity.this.brandingText.invalidate();
            }
        });
    }

    /* access modifiers changed from: private */
    public void showErrorDialog(CharSequence message) {
        AlertDialog dlg = new DarkAlertDialog.Builder(this).setTitle(LocaleController.getString("VoipFailed", R.string.VoipFailed)).setMessage(message).setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null).show();
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                VoIPActivity.this.finish();
            }
        });
    }

    public void onAudioSettingsChanged() {
        VoIPBaseService svc = VoIPBaseService.getSharedInstance();
        if (svc != null) {
            this.micToggle.setChecked(svc.isMicMute());
            if (svc.hasEarpiece() || svc.isBluetoothHeadsetConnected()) {
                this.spkToggle.setVisibility(0);
                if (!svc.hasEarpiece()) {
                    this.spkToggle.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                    this.spkToggle.setChecked(svc.isSpeakerphoneOn());
                } else if (svc.isBluetoothHeadsetConnected()) {
                    int currentAudioRoute = svc.getCurrentAudioRoute();
                    if (currentAudioRoute == 0) {
                        this.spkToggle.setImageResource(R.drawable.ic_phone_in_talk_white_24dp);
                    } else if (currentAudioRoute == 1) {
                        this.spkToggle.setImageResource(R.drawable.ic_volume_up_white_24dp);
                    } else if (currentAudioRoute == 2) {
                        this.spkToggle.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                    }
                    this.spkToggle.setChecked(false);
                } else {
                    this.spkToggle.setImageResource(R.drawable.ic_volume_up_white_24dp);
                    this.spkToggle.setChecked(svc.isSpeakerphoneOn());
                }
            } else {
                this.spkToggle.setVisibility(4);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: java.lang.String} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setStateTextAnimated(java.lang.String r17, boolean r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            java.lang.String r2 = r0.lastStateText
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x000d
            return
        L_0x000d:
            r0.lastStateText = r1
            android.animation.Animator r2 = r0.textChangingAnim
            if (r2 == 0) goto L_0x0016
            r2.cancel()
        L_0x0016:
            r2 = 3
            r3 = 1
            r4 = 0
            r5 = 2
            if (r18 == 0) goto L_0x0061
            android.animation.AnimatorSet r6 = r0.ellAnimator
            boolean r6 = r6.isRunning()
            if (r6 != 0) goto L_0x0029
            android.animation.AnimatorSet r6 = r0.ellAnimator
            r6.start()
        L_0x0029:
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            java.lang.String r7 = r17.toUpperCase()
            r6.<init>(r7)
            im.bclpbkiauv.ui.VoIPActivity$TextAlphaSpan[] r7 = r0.ellSpans
            int r8 = r7.length
            r9 = 0
        L_0x0036:
            if (r9 >= r8) goto L_0x0040
            r10 = r7[r9]
            r10.setAlpha(r4)
            int r9 = r9 + 1
            goto L_0x0036
        L_0x0040:
            android.text.SpannableString r7 = new android.text.SpannableString
            java.lang.String r8 = "..."
            r7.<init>(r8)
            im.bclpbkiauv.ui.VoIPActivity$TextAlphaSpan[] r8 = r0.ellSpans
            r8 = r8[r4]
            r7.setSpan(r8, r4, r3, r4)
            im.bclpbkiauv.ui.VoIPActivity$TextAlphaSpan[] r8 = r0.ellSpans
            r8 = r8[r3]
            r7.setSpan(r8, r3, r5, r4)
            im.bclpbkiauv.ui.VoIPActivity$TextAlphaSpan[] r8 = r0.ellSpans
            r8 = r8[r5]
            r7.setSpan(r8, r5, r2, r4)
            r6.append(r7)
            goto L_0x0072
        L_0x0061:
            android.animation.AnimatorSet r6 = r0.ellAnimator
            boolean r6 = r6.isRunning()
            if (r6 == 0) goto L_0x006e
            android.animation.AnimatorSet r6 = r0.ellAnimator
            r6.cancel()
        L_0x006e:
            java.lang.String r6 = r17.toUpperCase()
        L_0x0072:
            android.widget.TextView r7 = r0.stateText2
            r7.setText(r6)
            android.widget.TextView r7 = r0.stateText2
            r7.setVisibility(r4)
            android.widget.TextView r7 = r0.stateText
            boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
            r9 = 0
            if (r8 == 0) goto L_0x008b
            android.widget.TextView r8 = r0.stateText
            int r8 = r8.getWidth()
            float r8 = (float) r8
            goto L_0x008c
        L_0x008b:
            r8 = 0
        L_0x008c:
            r7.setPivotX(r8)
            android.widget.TextView r7 = r0.stateText
            int r8 = r7.getHeight()
            int r8 = r8 / r5
            float r8 = (float) r8
            r7.setPivotY(r8)
            android.widget.TextView r7 = r0.stateText2
            boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x00a8
            android.widget.TextView r8 = r0.stateText
            int r8 = r8.getWidth()
            float r8 = (float) r8
            goto L_0x00a9
        L_0x00a8:
            r8 = 0
        L_0x00a9:
            r7.setPivotX(r8)
            android.widget.TextView r7 = r0.stateText2
            android.widget.TextView r8 = r0.stateText
            int r8 = r8.getHeight()
            int r8 = r8 / r5
            float r8 = (float) r8
            r7.setPivotY(r8)
            android.widget.TextView r7 = r0.stateText2
            r0.durationText = r7
            android.animation.AnimatorSet r7 = new android.animation.AnimatorSet
            r7.<init>()
            r8 = 8
            android.animation.Animator[] r8 = new android.animation.Animator[r8]
            android.widget.TextView r10 = r0.stateText2
            float[] r11 = new float[r5]
            r11 = {0, 1065353216} // fill-array
            java.lang.String r12 = "alpha"
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r11)
            r8[r4] = r10
            android.widget.TextView r10 = r0.stateText2
            float[] r11 = new float[r5]
            android.widget.TextView r13 = r0.stateText
            int r13 = r13.getHeight()
            int r13 = r13 / r5
            float r13 = (float) r13
            r11[r4] = r13
            r11[r3] = r9
            java.lang.String r13 = "translationY"
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r13, r11)
            r8[r3] = r10
            android.widget.TextView r10 = r0.stateText2
            float[] r11 = new float[r5]
            r11 = {1060320051, 1065353216} // fill-array
            java.lang.String r14 = "scaleX"
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r14, r11)
            r8[r5] = r10
            android.widget.TextView r10 = r0.stateText2
            float[] r11 = new float[r5]
            r11 = {1060320051, 1065353216} // fill-array
            java.lang.String r15 = "scaleY"
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r15, r11)
            r8[r2] = r10
            r2 = 4
            android.widget.TextView r10 = r0.stateText
            float[] r11 = new float[r5]
            r11 = {1065353216, 0} // fill-array
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r12, r11)
            r8[r2] = r10
            r2 = 5
            android.widget.TextView r10 = r0.stateText
            float[] r11 = new float[r5]
            r11[r4] = r9
            int r4 = r10.getHeight()
            int r4 = -r4
            int r4 = r4 / r5
            float r4 = (float) r4
            r11[r3] = r4
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r10, r13, r11)
            r8[r2] = r3
            r2 = 6
            android.widget.TextView r3 = r0.stateText
            float[] r4 = new float[r5]
            r4 = {1065353216, 1060320051} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r14, r4)
            r8[r2] = r3
            r2 = 7
            android.widget.TextView r3 = r0.stateText
            float[] r4 = new float[r5]
            r4 = {1065353216, 1060320051} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r15, r4)
            r8[r2] = r3
            r7.playTogether(r8)
            r2 = 200(0xc8, double:9.9E-322)
            r7.setDuration(r2)
            im.bclpbkiauv.ui.components.CubicBezierInterpolator r2 = im.bclpbkiauv.ui.components.CubicBezierInterpolator.DEFAULT
            r7.setInterpolator(r2)
            im.bclpbkiauv.ui.VoIPActivity$24 r2 = new im.bclpbkiauv.ui.VoIPActivity$24
            r2.<init>()
            r7.addListener(r2)
            r0.textChangingAnim = r7
            r7.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.VoIPActivity.setStateTextAnimated(java.lang.String, boolean):void");
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiDidLoad) {
            for (ImageView iv : this.keyEmojiViews) {
                iv.invalidate();
            }
        }
        if (id == NotificationCenter.closeInCallActivity) {
            finish();
        }
    }

    /* access modifiers changed from: private */
    public void setEmojiTooltipVisible(boolean visible) {
        this.emojiTooltipVisible = visible;
        Animator animator = this.tooltipAnim;
        if (animator != null) {
            animator.cancel();
        }
        this.hintTextView.setVisibility(0);
        TextView textView = this.hintTextView;
        float[] fArr = new float[1];
        fArr[0] = visible ? 1.0f : 0.0f;
        ObjectAnimator oa = ObjectAnimator.ofFloat(textView, "alpha", fArr);
        oa.setDuration(300);
        oa.setInterpolator(CubicBezierInterpolator.DEFAULT);
        oa.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Animator unused = VoIPActivity.this.tooltipAnim = null;
            }
        });
        this.tooltipAnim = oa;
        oa.start();
    }

    /* access modifiers changed from: private */
    public void setEmojiExpanded(boolean expanded) {
        boolean z = expanded;
        if (this.emojiExpanded != z) {
            this.emojiExpanded = z;
            AnimatorSet animatorSet = this.emojiAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (z) {
                int[] loc = {0, 0};
                int[] loc2 = {0, 0};
                this.emojiWrap.getLocationInWindow(loc);
                this.emojiExpandedText.getLocationInWindow(loc2);
                Rect rect = new Rect();
                getWindow().getDecorView().getGlobalVisibleRect(rect);
                int offsetY = ((loc2[1] - (loc[1] + this.emojiWrap.getHeight())) - AndroidUtilities.dp(32.0f)) - this.emojiWrap.getHeight();
                int firstOffsetX = ((rect.width() / 2) - (Math.round(((float) this.emojiWrap.getWidth()) * 2.5f) / 2)) - loc[0];
                AnimatorSet set = new AnimatorSet();
                ImageView imageView = this.blurOverlayView1;
                float[] fArr = {imageView.getAlpha(), 1.0f, 1.0f};
                ImageView imageView2 = this.blurOverlayView2;
                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiWrap, "translationY", new float[]{(float) offsetY}), ObjectAnimator.ofFloat(this.emojiWrap, "translationX", new float[]{(float) firstOffsetX}), ObjectAnimator.ofFloat(this.emojiWrap, "scaleX", new float[]{2.5f}), ObjectAnimator.ofFloat(this.emojiWrap, "scaleY", new float[]{2.5f}), ObjectAnimator.ofFloat(imageView, "alpha", fArr), ObjectAnimator.ofFloat(imageView2, "alpha", new float[]{imageView2.getAlpha(), this.blurOverlayView2.getAlpha(), 1.0f}), ObjectAnimator.ofFloat(this.emojiExpandedText, "alpha", new float[]{1.0f})});
                set.setDuration(300);
                set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.emojiAnimator = set;
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = VoIPActivity.this.emojiAnimator = null;
                    }
                });
                set.start();
                return;
            }
            AnimatorSet set2 = new AnimatorSet();
            ImageView imageView3 = this.blurOverlayView1;
            float[] fArr2 = {imageView3.getAlpha(), this.blurOverlayView1.getAlpha(), 0.0f};
            ImageView imageView4 = this.blurOverlayView2;
            set2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiWrap, "translationX", new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiWrap, "translationY", new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiWrap, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiWrap, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(imageView3, "alpha", fArr2), ObjectAnimator.ofFloat(imageView4, "alpha", new float[]{imageView4.getAlpha(), 0.0f, 0.0f}), ObjectAnimator.ofFloat(this.emojiExpandedText, "alpha", new float[]{0.0f})});
            set2.setDuration(300);
            set2.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.emojiAnimator = set2;
            set2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = VoIPActivity.this.emojiAnimator = null;
                }
            });
            set2.start();
        }
    }

    /* access modifiers changed from: private */
    public void updateBlurredPhotos(final ImageReceiver.BitmapHolder src) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Bitmap blur1 = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(blur1);
                    canvas.drawBitmap(src.bitmap, (Rect) null, new Rect(0, 0, 150, 150), new Paint(2));
                    Utilities.blurBitmap(blur1, 3, 0, blur1.getWidth(), blur1.getHeight(), blur1.getRowBytes());
                    Palette palette = Palette.from(src.bitmap).generate();
                    Paint paint = new Paint();
                    paint.setColor((palette.getDarkMutedColor(-11242343) & ViewCompat.MEASURED_SIZE_MASK) | 1140850688);
                    canvas.drawColor(637534208);
                    canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
                    Bitmap blur2 = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                    Canvas canvas2 = new Canvas(blur2);
                    canvas2.drawBitmap(src.bitmap, (Rect) null, new Rect(0, 0, 50, 50), new Paint(2));
                    Utilities.blurBitmap(blur2, 3, 0, blur2.getWidth(), blur2.getHeight(), blur2.getRowBytes());
                    paint.setAlpha(102);
                    canvas2.drawRect(0.0f, 0.0f, (float) canvas2.getWidth(), (float) canvas2.getHeight(), paint);
                    Bitmap unused = VoIPActivity.this.blurredPhoto1 = blur1;
                    Bitmap unused2 = VoIPActivity.this.blurredPhoto2 = blur2;
                    VoIPActivity.this.runOnUiThread(new Runnable(src) {
                        private final /* synthetic */ ImageReceiver.BitmapHolder f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            VoIPActivity.AnonymousClass28.this.lambda$run$0$VoIPActivity$28(this.f$1);
                        }
                    });
                } catch (Throwable th) {
                }
            }

            public /* synthetic */ void lambda$run$0$VoIPActivity$28(ImageReceiver.BitmapHolder src) {
                VoIPActivity.this.blurOverlayView1.setImageBitmap(VoIPActivity.this.blurredPhoto1);
                VoIPActivity.this.blurOverlayView2.setImageBitmap(VoIPActivity.this.blurredPhoto2);
                src.release();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void sendTextMessage(String text) {
        AndroidUtilities.runOnUIThread(new Runnable(text) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPActivity.this.lambda$sendTextMessage$3$VoIPActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$sendTextMessage$3$VoIPActivity(String text) {
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(text, (long) this.user.id, (MessageObject) null, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
    }

    private void showMessagesSheet() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopRinging();
        }
        SharedPreferences prefs = getSharedPreferences("mainconfig", 0);
        String[] msgs = {prefs.getString("quick_reply_msg1", LocaleController.getString("QuickReplyDefault1", R.string.QuickReplyDefault1)), prefs.getString("quick_reply_msg2", LocaleController.getString("QuickReplyDefault2", R.string.QuickReplyDefault2)), prefs.getString("quick_reply_msg3", LocaleController.getString("QuickReplyDefault3", R.string.QuickReplyDefault3)), prefs.getString("quick_reply_msg4", LocaleController.getString("QuickReplyDefault4", R.string.QuickReplyDefault4))};
        LinearLayout sheetView = new LinearLayout(this);
        sheetView.setOrientation(1);
        BottomSheet sheet = new BottomSheet(this, true, 0);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(-13948117);
            sheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    VoIPActivity.this.lambda$showMessagesSheet$4$VoIPActivity(dialogInterface);
                }
            });
        }
        View.OnClickListener listener = new View.OnClickListener(sheet) {
            private final /* synthetic */ BottomSheet f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                VoIPActivity.this.lambda$showMessagesSheet$6$VoIPActivity(this.f$1, view);
            }
        };
        for (String msg : msgs) {
            BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(this, 0);
            cell.setTextAndIcon(msg, 0);
            cell.setTextColor(-1);
            cell.setTag(msg);
            cell.setOnClickListener(listener);
            sheetView.addView(cell);
        }
        FrameLayout customWrap = new FrameLayout(this);
        BottomSheet.BottomSheetCell cell2 = new BottomSheet.BottomSheetCell(this, 0);
        cell2.setTextAndIcon(LocaleController.getString("QuickReplyCustom", R.string.QuickReplyCustom), 0);
        cell2.setTextColor(-1);
        customWrap.addView(cell2);
        FrameLayout editor = new FrameLayout(this);
        EditText field = new EditText(this);
        field.setTextSize(1, 16.0f);
        field.setTextColor(-1);
        field.setHintTextColor(DarkTheme.getColor(Theme.key_chat_messagePanelHint));
        field.setBackgroundDrawable((Drawable) null);
        field.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        field.setHint(LocaleController.getString("QuickReplyCustom", R.string.QuickReplyCustom));
        field.setMinHeight(AndroidUtilities.dp(48.0f));
        field.setGravity(80);
        field.setMaxLines(4);
        field.setSingleLine(false);
        field.setInputType(field.getInputType() | 16384 | 131072);
        editor.addView(field, LayoutHelper.createFrame(-1.0f, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 48.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 48.0f, 0.0f));
        final ImageView sendBtn = new ImageView(this);
        sendBtn.setScaleType(ImageView.ScaleType.CENTER);
        sendBtn.setImageDrawable(DarkTheme.getThemedDrawable(this, R.drawable.ic_send, Theme.key_chat_messagePanelSend));
        if (LocaleController.isRTL) {
            sendBtn.setScaleX(-0.1f);
        } else {
            sendBtn.setScaleX(0.1f);
        }
        sendBtn.setScaleY(0.1f);
        sendBtn.setAlpha(0.0f);
        editor.addView(sendBtn, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        sendBtn.setOnClickListener(new View.OnClickListener(field, sheet) {
            private final /* synthetic */ EditText f$1;
            private final /* synthetic */ BottomSheet f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                VoIPActivity.this.lambda$showMessagesSheet$7$VoIPActivity(this.f$1, this.f$2, view);
            }
        });
        sendBtn.setVisibility(4);
        final ImageView cancelBtn2 = new ImageView(this);
        cancelBtn2.setScaleType(ImageView.ScaleType.CENTER);
        cancelBtn2.setImageDrawable(DarkTheme.getThemedDrawable(this, R.drawable.edit_cancel, Theme.key_chat_messagePanelIcons));
        editor.addView(cancelBtn2, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        cancelBtn2.setOnClickListener(new View.OnClickListener(editor, cell2, field) {
            private final /* synthetic */ FrameLayout f$1;
            private final /* synthetic */ BottomSheet.BottomSheetCell f$2;
            private final /* synthetic */ EditText f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(View view) {
                VoIPActivity.this.lambda$showMessagesSheet$8$VoIPActivity(this.f$1, this.f$2, this.f$3, view);
            }
        });
        field.addTextChangedListener(new TextWatcher() {
            boolean prevState = false;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                boolean hasText = s.length() > 0;
                if (this.prevState != hasText) {
                    this.prevState = hasText;
                    if (hasText) {
                        sendBtn.setVisibility(0);
                        sendBtn.animate().alpha(1.0f).scaleX(LocaleController.isRTL ? -1.0f : 1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        cancelBtn2.animate().alpha(0.0f).scaleX(0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new Runnable() {
                            public void run() {
                                cancelBtn2.setVisibility(4);
                            }
                        }).start();
                        return;
                    }
                    cancelBtn2.setVisibility(0);
                    cancelBtn2.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    sendBtn.animate().alpha(0.0f).scaleX(LocaleController.isRTL ? -0.1f : 0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new Runnable() {
                        public void run() {
                            sendBtn.setVisibility(4);
                        }
                    }).start();
                }
            }
        });
        editor.setVisibility(8);
        customWrap.addView(editor);
        cell2.setOnClickListener(new View.OnClickListener(editor, cell2, field) {
            private final /* synthetic */ FrameLayout f$1;
            private final /* synthetic */ BottomSheet.BottomSheetCell f$2;
            private final /* synthetic */ EditText f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(View view) {
                VoIPActivity.this.lambda$showMessagesSheet$9$VoIPActivity(this.f$1, this.f$2, this.f$3, view);
            }
        });
        sheetView.addView(customWrap);
        sheet.setCustomView(sheetView);
        sheet.setBackgroundColor(-13948117);
        sheet.show();
    }

    public /* synthetic */ void lambda$showMessagesSheet$4$VoIPActivity(DialogInterface dialog) {
        getWindow().setNavigationBarColor(0);
    }

    public /* synthetic */ void lambda$showMessagesSheet$6$VoIPActivity(BottomSheet sheet, View v) {
        sheet.dismiss();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable(v) {
                private final /* synthetic */ View f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPActivity.this.lambda$null$5$VoIPActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$5$VoIPActivity(View v) {
        sendTextMessage((String) v.getTag());
    }

    public /* synthetic */ void lambda$showMessagesSheet$7$VoIPActivity(final EditText field, BottomSheet sheet, View v) {
        if (field.length() != 0) {
            sheet.dismiss();
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable() {
                    public void run() {
                        VoIPActivity.this.sendTextMessage(field.getText().toString());
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$showMessagesSheet$8$VoIPActivity(FrameLayout editor, BottomSheet.BottomSheetCell cell, EditText field, View v) {
        editor.setVisibility(8);
        cell.setVisibility(0);
        field.setText("");
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(field.getWindowToken(), 0);
    }

    public /* synthetic */ void lambda$showMessagesSheet$9$VoIPActivity(FrameLayout editor, BottomSheet.BottomSheetCell cell, EditText field, View v) {
        editor.setVisibility(0);
        cell.setVisibility(4);
        field.requestFocus();
        ((InputMethodManager) getSystemService("input_method")).showSoftInput(field, 0);
    }

    private class TextAlphaSpan extends CharacterStyle {
        private int alpha = 0;

        public TextAlphaSpan() {
        }

        public int getAlpha() {
            return this.alpha;
        }

        public void setAlpha(int alpha2) {
            this.alpha = alpha2;
            VoIPActivity.this.stateText.invalidate();
            VoIPActivity.this.stateText2.invalidate();
        }

        public void updateDrawState(TextPaint tp) {
            tp.setAlpha(this.alpha);
        }
    }

    private class SignalBarsDrawable extends Drawable {
        private int[] barHeights;
        private int offsetStart;
        private Paint paint;
        private RectF rect;

        private SignalBarsDrawable() {
            this.barHeights = new int[]{AndroidUtilities.dp(3.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(12.0f)};
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.offsetStart = 6;
        }

        public void draw(Canvas canvas) {
            if (VoIPActivity.this.callState == 3 || VoIPActivity.this.callState == 5) {
                this.paint.setColor(-1);
                int x = getBounds().left + AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) this.offsetStart);
                int y = getBounds().top;
                for (int i = 0; i < 4; i++) {
                    this.paint.setAlpha(i + 1 <= VoIPActivity.this.signalBarsCount ? 242 : 102);
                    this.rect.set((float) (AndroidUtilities.dp((float) (i * 4)) + x), (float) ((getIntrinsicHeight() + y) - this.barHeights[i]), (float) ((AndroidUtilities.dp(4.0f) * i) + x + AndroidUtilities.dp(3.0f)), (float) (getIntrinsicHeight() + y));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(0.3f), (float) AndroidUtilities.dp(0.3f), this.paint);
                }
            }
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp((float) (this.offsetStart + 15));
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(12.0f);
        }

        public int getOpacity() {
            return -3;
        }
    }
}

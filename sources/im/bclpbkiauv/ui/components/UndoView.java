package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;

public class UndoView extends FrameLayout {
    public static final int ACTION_ARCHIVE = 2;
    public static final int ACTION_ARCHIVE_FEW = 4;
    public static final int ACTION_ARCHIVE_FEW_HINT = 5;
    public static final int ACTION_ARCHIVE_HIDDEN = 6;
    public static final int ACTION_ARCHIVE_HINT = 3;
    public static final int ACTION_ARCHIVE_PINNED = 7;
    public static final int ACTION_CLEAR = 0;
    public static final int ACTION_CONTACT_ADDED = 8;
    public static final int ACTION_DELETE = 1;
    public static final int ACTION_OWNER_TRANSFERED_CHANNEL = 9;
    public static final int ACTION_OWNER_TRANSFERED_GROUP = 10;
    private float additionalTranslationY;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private long currentDialogId;
    private TextView infoTextView;
    private boolean isShowed;
    private long lastUpdateTime;
    private RLottieImageView leftImageView;
    private int prevSeconds;
    private Paint progressPaint;
    private RectF rect;
    private TextView subinfoTextView;
    private TextPaint textPaint;
    private int textWidth;
    private long timeLeft;
    private String timeLeftString;
    private LinearLayout undoButton;
    private ImageView undoImageView;
    private TextView undoTextView;
    private int undoViewHeight;

    public UndoView(Context context) {
        super(context);
        TextView textView = new TextView(context);
        this.infoTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(Theme.getColor(Theme.key_undo_infoColor));
        addView(this.infoTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.subinfoTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(Theme.getColor(Theme.key_undo_infoColor));
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.leftImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.leftImageView.setLayerColor("info1.**", Theme.getColor(Theme.key_undo_background) | -16777216);
        this.leftImageView.setLayerColor("info2.**", Theme.getColor(Theme.key_undo_background) | -16777216);
        this.leftImageView.setLayerColor("luc12.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc11.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc10.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc9.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc8.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc7.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc6.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc5.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc4.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc3.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc2.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc1.**", Theme.getColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("Oval.**", Theme.getColor(Theme.key_undo_infoColor));
        addView(this.leftImageView, LayoutHelper.createFrame(54.0f, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.undoButton = linearLayout;
        linearLayout.setOrientation(0);
        addView(this.undoButton, LayoutHelper.createFrame(-2.0f, -1.0f, 21, 0.0f, 0.0f, 19.0f, 0.0f));
        this.undoButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                UndoView.this.lambda$new$0$UndoView(view);
            }
        });
        ImageView imageView = new ImageView(context);
        this.undoImageView = imageView;
        imageView.setImageResource(R.drawable.chats_undo);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_undo_cancelColor), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19));
        TextView textView3 = new TextView(context);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.undoTextView.setTextColor(Theme.getColor(Theme.key_undo_cancelColor));
        this.undoTextView.setText(LocaleController.getString("Undo", R.string.Undo));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 0, 0, 0));
        this.rect = new RectF((float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(33.0f), (float) AndroidUtilities.dp(33.0f));
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setColor(Theme.getColor(Theme.key_undo_infoColor));
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setColor(Theme.getColor(Theme.key_undo_infoColor));
        setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_undo_background)));
        setOnTouchListener($$Lambda$UndoView$8_q504OoPOCrGm1ZlfpIa7atG8.INSTANCE);
        setVisibility(4);
    }

    public /* synthetic */ void lambda$new$0$UndoView(View v) {
        if (canUndo()) {
            hide(false, 1);
        }
    }

    static /* synthetic */ boolean lambda$new$1(View v, MotionEvent event) {
        return true;
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7;
    }

    public void setAdditionalTranslationY(float value) {
        this.additionalTranslationY = value;
    }

    public void hide(boolean apply, int animated) {
        if (getVisibility() == 0 && this.isShowed) {
            this.isShowed = false;
            Runnable runnable = this.currentActionRunnable;
            if (runnable != null) {
                if (apply) {
                    runnable.run();
                }
                this.currentActionRunnable = null;
            }
            Runnable runnable2 = this.currentCancelRunnable;
            if (runnable2 != null) {
                if (!apply) {
                    runnable2.run();
                }
                this.currentCancelRunnable = null;
            }
            int i = this.currentAction;
            if (i == 0 || i == 1) {
                MessagesController.getInstance(this.currentAccount).removeDialogAction(this.currentDialogId, this.currentAction == 0, apply);
            }
            if (animated != 0) {
                AnimatorSet animatorSet = new AnimatorSet();
                if (animated == 1) {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight)})});
                    animatorSet.setDuration(250);
                } else {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.8f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.8f}), ObjectAnimator.ofFloat(this, View.ALPHA, new float[]{0.0f})});
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        UndoView.this.setVisibility(4);
                        UndoView.this.setScaleX(1.0f);
                        UndoView.this.setScaleY(1.0f);
                        UndoView.this.setAlpha(1.0f);
                    }
                });
                animatorSet.start();
                return;
            }
            setTranslationY((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight));
            setVisibility(4);
        }
    }

    public void showWithAction(long did, int action, Runnable actionRunnable) {
        showWithAction(did, action, (Object) null, actionRunnable, (Runnable) null);
    }

    public void showWithAction(long did, int action, Object infoObject) {
        showWithAction(did, action, infoObject, (Runnable) null, (Runnable) null);
    }

    public void showWithAction(long did, int action, Runnable actionRunnable, Runnable cancelRunnable) {
        showWithAction(did, action, (Object) null, actionRunnable, cancelRunnable);
    }

    public void showWithAction(long did, int action, Object infoObject, Runnable actionRunnable, Runnable cancelRunnable) {
        int i;
        String str;
        int width;
        String subInfoText;
        CharSequence infoText;
        int icon;
        CharSequence infoText2;
        long j = did;
        int i2 = action;
        Runnable runnable = this.currentActionRunnable;
        if (runnable != null) {
            runnable.run();
        }
        this.isShowed = true;
        this.currentActionRunnable = actionRunnable;
        this.currentCancelRunnable = cancelRunnable;
        this.currentDialogId = j;
        this.currentAction = i2;
        this.timeLeft = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        this.lastUpdateTime = SystemClock.uptimeMillis();
        if (isTooltipAction()) {
            int size = 36;
            if (i2 == 9 || i2 == 10) {
                TLRPC.User user = (TLRPC.User) infoObject;
                if (i2 == 9) {
                    infoText2 = AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferChannelToast", R.string.EditAdminTransferChannelToast, UserObject.getFirstName(user)));
                } else {
                    infoText2 = AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferGroupToast", R.string.EditAdminTransferGroupToast, UserObject.getFirstName(user)));
                }
                subInfoText = null;
                icon = R.raw.contact_check;
            } else if (i2 == 8) {
                infoText = LocaleController.formatString("NowInContacts", R.string.NowInContacts, UserObject.getFirstName((TLRPC.User) infoObject));
                subInfoText = null;
                icon = R.raw.contact_check;
            } else if (i2 == 6) {
                infoText = LocaleController.getString("ArchiveHidden", R.string.ArchiveHidden);
                subInfoText = LocaleController.getString("ArchiveHiddenInfo", R.string.ArchiveHiddenInfo);
                icon = R.raw.chats_swipearchive;
                size = 48;
            } else if (i2 == 7) {
                infoText = LocaleController.getString("ArchivePinned", R.string.ArchivePinned);
                subInfoText = LocaleController.getString("ArchivePinnedInfo", R.string.ArchivePinnedInfo);
                icon = R.raw.chats_infotip;
            } else {
                if (i2 == 3) {
                    infoText = LocaleController.getString("ChatArchived", R.string.ChatArchived);
                } else {
                    infoText = LocaleController.getString("ChatsArchived", R.string.ChatsArchived);
                }
                subInfoText = LocaleController.getString("ChatArchivedInfo", R.string.ChatArchivedInfo);
                icon = R.raw.chats_infotip;
            }
            this.infoTextView.setText(infoText);
            this.leftImageView.setAnimation(icon, size, size);
            if (subInfoText != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.infoTextView.getLayoutParams();
                layoutParams.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams.topMargin = AndroidUtilities.dp(6.0f);
                this.subinfoTextView.setText(subInfoText);
                this.subinfoTextView.setVisibility(0);
                this.infoTextView.setTextSize(1, 14.0f);
                this.infoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.infoTextView.getLayoutParams();
                layoutParams2.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams2.topMargin = AndroidUtilities.dp(13.0f);
                this.subinfoTextView.setVisibility(8);
                this.infoTextView.setTextSize(1, 15.0f);
                this.infoTextView.setTypeface(Typeface.DEFAULT);
            }
            this.undoButton.setVisibility(8);
            this.leftImageView.setVisibility(0);
            this.leftImageView.setProgress(0.0f);
            this.leftImageView.playAnimation();
            i = 2;
        } else {
            int i3 = this.currentAction;
            if (i3 == 2 || i3 == 4) {
                i = 2;
                if (i2 == 2) {
                    this.infoTextView.setText(LocaleController.getString("ChatArchived", R.string.ChatArchived));
                } else {
                    this.infoTextView.setText(LocaleController.getString("ChatsArchived", R.string.ChatsArchived));
                }
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.infoTextView.getLayoutParams();
                layoutParams3.leftMargin = AndroidUtilities.dp(58.0f);
                layoutParams3.topMargin = AndroidUtilities.dp(13.0f);
                this.infoTextView.setTextSize(1, 15.0f);
                this.undoButton.setVisibility(0);
                this.infoTextView.setTypeface(Typeface.DEFAULT);
                this.subinfoTextView.setVisibility(8);
                this.leftImageView.setVisibility(0);
                this.leftImageView.setAnimation(R.raw.chats_archived, 36, 36);
                this.leftImageView.setProgress(0.0f);
                this.leftImageView.playAnimation();
            } else {
                FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.infoTextView.getLayoutParams();
                layoutParams4.leftMargin = AndroidUtilities.dp(45.0f);
                layoutParams4.topMargin = AndroidUtilities.dp(13.0f);
                this.infoTextView.setTextSize(1, 15.0f);
                this.undoButton.setVisibility(0);
                this.infoTextView.setTypeface(Typeface.DEFAULT);
                this.subinfoTextView.setVisibility(8);
                this.leftImageView.setVisibility(8);
                if (this.currentAction == 0) {
                    this.infoTextView.setText(LocaleController.getString("HistoryClearedUndo", R.string.HistoryClearedUndo));
                } else {
                    int lowerId = (int) j;
                    if (lowerId < 0) {
                        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lowerId));
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            this.infoTextView.setText(LocaleController.getString("GroupDeletedUndo", R.string.GroupDeletedUndo));
                        } else {
                            this.infoTextView.setText(LocaleController.getString("ChannelDeletedUndo", R.string.ChannelDeletedUndo));
                        }
                    } else {
                        this.infoTextView.setText(LocaleController.getString("ChatDeletedUndo", R.string.ChatDeletedUndo));
                    }
                }
                MessagesController.getInstance(this.currentAccount).addDialogAction(j, this.currentAction == 0);
                i = 2;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.infoTextView.getText());
        if (this.subinfoTextView.getVisibility() == 0) {
            str = ". " + this.subinfoTextView.getText();
        } else {
            str = "";
        }
        sb.append(str);
        AndroidUtilities.makeAccessibilityAnnouncement(sb.toString());
        if (hasSubInfo()) {
            this.undoViewHeight = AndroidUtilities.dp(52.0f);
        } else if (getParent() instanceof ViewGroup) {
            int width2 = ((ViewGroup) getParent()).getMeasuredWidth();
            if (width2 == 0) {
                width = AndroidUtilities.displaySize.x;
            } else {
                width = width2;
            }
            int i4 = width;
            measureChildWithMargins(this.infoTextView, View.MeasureSpec.makeMeasureSpec(width, 1073741824), 0, View.MeasureSpec.makeMeasureSpec(0, 0), 0);
            this.undoViewHeight = this.infoTextView.getMeasuredHeight() + AndroidUtilities.dp(28.0f);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
            setTranslationY((float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight));
            AnimatorSet animatorSet = new AnimatorSet();
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[i];
            fArr[0] = (float) (AndroidUtilities.dp(8.0f) + this.undoViewHeight);
            fArr[1] = -this.additionalTranslationY;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, property, fArr)});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.start();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canUndo() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(this.undoViewHeight, 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = this.currentAction;
        if (i == 1 || i == 0) {
            long j = this.timeLeft;
            int newSeconds = j > 0 ? (int) Math.ceil((double) (((float) j) / 1000.0f)) : 0;
            if (this.prevSeconds != newSeconds) {
                this.prevSeconds = newSeconds;
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, newSeconds))});
                this.timeLeftString = format;
                this.textWidth = (int) Math.ceil((double) this.textPaint.measureText(format));
            }
            canvas.drawText(this.timeLeftString, this.rect.centerX() - ((float) (this.textWidth / 2)), (float) AndroidUtilities.dp(28.2f), this.textPaint);
            canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * -360.0f, false, this.progressPaint);
        }
        long newTime = SystemClock.uptimeMillis();
        long j2 = this.timeLeft - (newTime - this.lastUpdateTime);
        this.timeLeft = j2;
        this.lastUpdateTime = newTime;
        if (j2 <= 0) {
            hide(true, 1);
        }
        invalidate();
    }
}

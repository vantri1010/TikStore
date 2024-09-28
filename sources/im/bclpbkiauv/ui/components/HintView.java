package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.zhy.http.okhttp.OkHttpUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.ChatMessageCell;
import im.bclpbkiauv.ui.components.HintView;

public class HintView extends FrameLayout {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private ImageView arrowImageView;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public View currentView;
    /* access modifiers changed from: private */
    public Runnable hideRunnable;
    private ImageView imageView;
    private boolean isTopArrow;
    /* access modifiers changed from: private */
    public ChatMessageCell messageCell;
    private String overrideText;
    private TextView textView;

    public HintView(Context context, int type) {
        this(context, type, false);
    }

    public HintView(Context context, int type, boolean topArrow) {
        super(context);
        this.currentType = type;
        this.isTopArrow = topArrow;
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context);
        this.textView = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        this.textView.setGravity(51);
        this.textView.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
        int i = this.currentType;
        if (i == 2) {
            this.textView.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
        } else {
            this.textView.setPadding(AndroidUtilities.dp(i == 0 ? 54.0f : 5.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(7.0f));
        }
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, 0.0f, topArrow ? 6.0f : 0.0f, 0.0f, topArrow ? 0.0f : 6.0f));
        if (type == 0) {
            this.textView.setText(LocaleController.getString("AutoplayVideoInfo", R.string.AutoplayVideoInfo));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageResource(R.drawable.tooltip_sound);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_gifSaveHintText), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(38.0f, 34.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        }
        ImageView imageView3 = new ImageView(context);
        this.arrowImageView = imageView3;
        imageView3.setImageResource(topArrow ? R.drawable.tooltip_arrow_up : R.drawable.tooltip_arrow);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_gifSaveHintBackground), PorterDuff.Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14.0f, 6.0f, (topArrow ? 48 : 80) | 3, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void setOverrideText(String text) {
        this.overrideText = text;
        this.textView.setText(text);
        if (this.messageCell != null) {
            ChatMessageCell cell = this.messageCell;
            this.messageCell = null;
            showForMessageCell(cell, false);
        }
    }

    public boolean showForMessageCell(ChatMessageCell cell, boolean animated) {
        int centerX;
        int top;
        ChatMessageCell chatMessageCell = cell;
        if ((this.currentType == 0 && getTag() != null) || this.messageCell == chatMessageCell) {
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        int top2 = cell.getTop();
        View parentView = (View) cell.getParent();
        if (this.currentType == 0) {
            ImageReceiver imageReceiver = cell.getPhotoImage();
            top = top2 + imageReceiver.getImageY();
            int height = imageReceiver.getImageHeight();
            int bottom = top + height;
            int parentHeight = parentView.getMeasuredHeight();
            if (top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || bottom > (height / 4) + parentHeight) {
                return false;
            }
            centerX = cell.getNoSoundIconCenterX();
        } else {
            MessageObject messageObject = cell.getMessageObject();
            String str = this.overrideText;
            if (str == null) {
                this.textView.setText(LocaleController.getString("HidAccount", R.string.HidAccount));
            } else {
                this.textView.setText(str);
            }
            measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
            top = top2 + AndroidUtilities.dp(22.0f);
            if (!messageObject.isOutOwner() && cell.isDrawNameLayout()) {
                top += AndroidUtilities.dp(20.0f);
            }
            if (!this.isTopArrow && top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
                return false;
            }
            centerX = cell.getForwardNameCenterX();
        }
        int parentWidth = parentView.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) (top - getMeasuredHeight()));
        }
        int iconX = cell.getLeft() + centerX;
        int left = AndroidUtilities.dp(19.0f);
        if (iconX > parentView.getMeasuredWidth() / 2) {
            int offset = (parentWidth - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
            setTranslationX((float) offset);
            left += offset;
        } else {
            setTranslationX(0.0f);
        }
        float arrowX = (float) (((cell.getLeft() + centerX) - left) - (this.arrowImageView.getMeasuredWidth() / 2));
        this.arrowImageView.setTranslationX(arrowX);
        if (iconX > parentView.getMeasuredWidth() / 2) {
            if (arrowX < ((float) AndroidUtilities.dp(10.0f))) {
                float diff = arrowX - ((float) AndroidUtilities.dp(10.0f));
                setTranslationX(getTranslationX() + diff);
                this.arrowImageView.setTranslationX(arrowX - diff);
            }
        } else if (arrowX > ((float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)))) {
            float diff2 = (arrowX - ((float) getMeasuredWidth())) + ((float) AndroidUtilities.dp(24.0f));
            setTranslationX(diff2);
            this.arrowImageView.setTranslationX(arrowX - diff2);
        } else if (arrowX < ((float) AndroidUtilities.dp(10.0f))) {
            float diff3 = arrowX - ((float) AndroidUtilities.dp(10.0f));
            setTranslationX(getTranslationX() + diff3);
            this.arrowImageView.setTranslationX(arrowX - diff3);
        }
        this.messageCell = chatMessageCell;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (animated) {
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new Runnable() {
                        public final void run() {
                            HintView.AnonymousClass1.this.lambda$onAnimationEnd$0$HintView$1();
                        }
                    }, HintView.this.currentType == 0 ? OkHttpUtils.DEFAULT_MILLISECONDS : AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$HintView$1() {
                    HintView.this.hide();
                }
            });
            this.animatorSet.setDuration(300);
            this.animatorSet.start();
        } else {
            setAlpha(1.0f);
        }
        return true;
    }

    public boolean showForView(View view, boolean animated) {
        View view2 = view;
        if (this.currentView == view2 || getTag() != null) {
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
        int[] position = new int[2];
        view2.getLocationInWindow(position);
        int top = position[1] - AndroidUtilities.dp(4.0f);
        View parentView = (View) getParent();
        parentView.getLocationInWindow(position);
        int centerX = (position[0] + (view.getMeasuredWidth() / 2)) - position[0];
        int top2 = top - position[1];
        if (Build.VERSION.SDK_INT >= 21) {
            top2 -= AndroidUtilities.statusBarHeight;
        }
        int parentWidth = parentView.getMeasuredWidth();
        if (this.isTopArrow) {
            setTranslationY((float) AndroidUtilities.dp(44.0f));
        } else {
            setTranslationY((float) ((top2 - getMeasuredHeight()) - ActionBar.getCurrentActionBarHeight()));
        }
        int iconX = centerX;
        int left = AndroidUtilities.dp(19.0f);
        if (iconX > parentView.getMeasuredWidth() / 2) {
            int offset = (parentWidth - getMeasuredWidth()) - AndroidUtilities.dp(28.0f);
            setTranslationX((float) offset);
            left += offset;
        } else {
            setTranslationX(0.0f);
        }
        float arrowX = (float) ((centerX - left) - (this.arrowImageView.getMeasuredWidth() / 2));
        this.arrowImageView.setTranslationX(arrowX);
        if (iconX > parentView.getMeasuredWidth() / 2) {
            if (arrowX < ((float) AndroidUtilities.dp(10.0f))) {
                float diff = arrowX - ((float) AndroidUtilities.dp(10.0f));
                setTranslationX(getTranslationX() + diff);
                this.arrowImageView.setTranslationX(arrowX - diff);
            }
        } else if (arrowX > ((float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f)))) {
            float diff2 = (arrowX - ((float) getMeasuredWidth())) + ((float) AndroidUtilities.dp(24.0f));
            setTranslationX(diff2);
            this.arrowImageView.setTranslationX(arrowX - diff2);
        } else if (arrowX < ((float) AndroidUtilities.dp(10.0f))) {
            float diff3 = arrowX - ((float) AndroidUtilities.dp(10.0f));
            setTranslationX(getTranslationX() + diff3);
            this.arrowImageView.setTranslationX(arrowX - diff3);
        }
        this.currentView = view2;
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (animated) {
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, 1.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = HintView.this.animatorSet = null;
                    AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new Runnable() {
                        public final void run() {
                            HintView.AnonymousClass2.this.lambda$onAnimationEnd$0$HintView$2();
                        }
                    }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$HintView$2() {
                    HintView.this.hide();
                }
            });
            this.animatorSet.setDuration(300);
            this.animatorSet.start();
        } else {
            setAlpha(1.0f);
        }
        return true;
    }

    public void hide() {
        if (getTag() != null) {
            setTag((Object) null);
            Runnable runnable = this.hideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.hideRunnable = null;
            }
            AnimatorSet animatorSet2 = this.animatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.animatorSet = null;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f})});
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    HintView.this.setVisibility(4);
                    View unused = HintView.this.currentView = null;
                    ChatMessageCell unused2 = HintView.this.messageCell = null;
                    AnimatorSet unused3 = HintView.this.animatorSet = null;
                }
            });
            this.animatorSet.setDuration(300);
            this.animatorSet.start();
        }
    }

    public void setText(CharSequence text) {
        this.textView.setText(text);
    }

    public ChatMessageCell getMessageCell() {
        return this.messageCell;
    }
}

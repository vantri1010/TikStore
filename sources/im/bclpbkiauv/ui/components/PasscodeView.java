package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.os.CancellationSignal;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.support.fingerprint.FingerprintManagerCompat;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.util.ArrayList;
import java.util.Locale;
import org.slf4j.Marker;

public class PasscodeView extends FrameLayout {
    private static final int id_fingerprint_imageview = 1001;
    private static final int id_fingerprint_textview = 1000;
    private static final int[] ids = {R.id.passcode_btn_0, R.id.passcode_btn_1, R.id.passcode_btn_2, R.id.passcode_btn_3, R.id.passcode_btn_4, R.id.passcode_btn_5, R.id.passcode_btn_6, R.id.passcode_btn_7, R.id.passcode_btn_8, R.id.passcode_btn_9, R.id.passcode_btn_backspace};
    private Drawable backgroundDrawable;
    private FrameLayout backgroundFrameLayout;
    private CancellationSignal cancellationSignal;
    private ImageView checkImage;
    /* access modifiers changed from: private */
    public Runnable checkRunnable = new Runnable() {
        public void run() {
            PasscodeView.this.checkRetryTextView();
            AndroidUtilities.runOnUIThread(PasscodeView.this.checkRunnable, 100);
        }
    };
    private PasscodeViewDelegate delegate;
    private ImageView eraseView;
    /* access modifiers changed from: private */
    public AlertDialog fingerprintDialog;
    private ImageView fingerprintImageView;
    private TextView fingerprintStatusTextView;
    private int keyboardHeight = 0;
    private int lastValue;
    private ArrayList<TextView> lettersTextViews;
    private ArrayList<FrameLayout> numberFrameLayouts;
    private ArrayList<TextView> numberTextViews;
    private FrameLayout numbersFrameLayout;
    private TextView passcodeTextView;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passwordEditText;
    private AnimatingTextView passwordEditText2;
    private FrameLayout passwordFrameLayout;
    private Rect rect = new Rect();
    private TextView retryTextView;
    /* access modifiers changed from: private */
    public boolean selfCancelled;

    public interface PasscodeViewDelegate {
        void didAcceptedPassword();
    }

    private class AnimatingTextView extends FrameLayout {
        private String DOT = "•";
        /* access modifiers changed from: private */
        public ArrayList<TextView> characterTextViews = new ArrayList<>(4);
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation;
        /* access modifiers changed from: private */
        public Runnable dotRunnable;
        /* access modifiers changed from: private */
        public ArrayList<TextView> dotTextViews = new ArrayList<>(4);
        private StringBuilder stringBuilder = new StringBuilder(4);

        public AnimatingTextView(Context context) {
            super(context);
            for (int a = 0; a < 4; a++) {
                TextView textView = new TextView(context);
                textView.setTextColor(-1);
                textView.setTextSize(1, 36.0f);
                textView.setGravity(17);
                textView.setAlpha(0.0f);
                textView.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
                layoutParams.width = AndroidUtilities.dp(50.0f);
                layoutParams.height = AndroidUtilities.dp(50.0f);
                layoutParams.gravity = 51;
                textView.setLayoutParams(layoutParams);
                this.characterTextViews.add(textView);
                TextView textView2 = new TextView(context);
                textView2.setTextColor(-1);
                textView2.setTextSize(1, 36.0f);
                textView2.setGravity(17);
                textView2.setAlpha(0.0f);
                textView2.setText(this.DOT);
                textView2.setPivotX((float) AndroidUtilities.dp(25.0f));
                textView2.setPivotY((float) AndroidUtilities.dp(25.0f));
                addView(textView2);
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) textView2.getLayoutParams();
                layoutParams2.width = AndroidUtilities.dp(50.0f);
                layoutParams2.height = AndroidUtilities.dp(50.0f);
                layoutParams2.gravity = 51;
                textView2.setLayoutParams(layoutParams2);
                this.dotTextViews.add(textView2);
            }
        }

        private int getXForTextView(int pos) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(30.0f))) / 2) + (AndroidUtilities.dp(30.0f) * pos)) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String c) {
            if (this.stringBuilder.length() != 4) {
                try {
                    performHapticFeedback(3);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                ArrayList<Animator> animators = new ArrayList<>();
                final int newPos = this.stringBuilder.length();
                this.stringBuilder.append(c);
                TextView textView = this.characterTextViews.get(newPos);
                textView.setText(c);
                textView.setTranslationX((float) getXForTextView(newPos));
                animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                TextView textView2 = this.dotTextViews.get(newPos);
                textView2.setTranslationX((float) getXForTextView(newPos));
                textView2.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(textView2, "scaleX", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView2, "scaleY", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(textView2, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f), 0.0f}));
                for (int a = newPos + 1; a < 4; a++) {
                    TextView textView3 = this.characterTextViews.get(a);
                    if (textView3.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView3, "scaleX", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView3, "scaleY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView3, "alpha", new float[]{0.0f}));
                    }
                    TextView textView4 = this.dotTextViews.get(a);
                    if (textView4.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView4, "scaleX", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView4, "scaleY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView4, "alpha", new float[]{0.0f}));
                    }
                }
                Runnable runnable = this.dotRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                AnonymousClass1 r1 = new Runnable() {
                    public void run() {
                        if (AnimatingTextView.this.dotRunnable == this) {
                            ArrayList<Animator> animators = new ArrayList<>();
                            TextView textView = (TextView) AnimatingTextView.this.characterTextViews.get(newPos);
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                            TextView textView2 = (TextView) AnimatingTextView.this.dotTextViews.get(newPos);
                            animators.add(ObjectAnimator.ofFloat(textView2, "scaleX", new float[]{1.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView2, "scaleY", new float[]{1.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", new float[]{1.0f}));
                            AnimatorSet unused = AnimatingTextView.this.currentAnimation = new AnimatorSet();
                            AnimatingTextView.this.currentAnimation.setDuration(150);
                            AnimatingTextView.this.currentAnimation.playTogether(animators);
                            AnimatingTextView.this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                                        AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                                    }
                                }
                            });
                            AnimatingTextView.this.currentAnimation.start();
                        }
                    }
                };
                this.dotRunnable = r1;
                AndroidUtilities.runOnUIThread(r1, 1500);
                for (int a2 = 0; a2 < newPos; a2++) {
                    TextView textView5 = this.characterTextViews.get(a2);
                    animators.add(ObjectAnimator.ofFloat(textView5, "translationX", new float[]{(float) getXForTextView(a2)}));
                    animators.add(ObjectAnimator.ofFloat(textView5, "scaleX", new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView5, "scaleY", new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView5, "alpha", new float[]{0.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView5, "translationY", new float[]{0.0f}));
                    TextView textView6 = this.dotTextViews.get(a2);
                    animators.add(ObjectAnimator.ofFloat(textView6, "translationX", new float[]{(float) getXForTextView(a2)}));
                    animators.add(ObjectAnimator.ofFloat(textView6, "scaleX", new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView6, "scaleY", new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView6, "alpha", new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(textView6, "translationY", new float[]{0.0f}));
                }
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.setDuration(150);
                this.currentAnimation.playTogether(animators);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                            AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
            }
        }

        public String getString() {
            return this.stringBuilder.toString();
        }

        public int length() {
            return this.stringBuilder.length();
        }

        public void eraseLastCharacter() {
            if (this.stringBuilder.length() != 0) {
                try {
                    performHapticFeedback(3);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                ArrayList<Animator> animators = new ArrayList<>();
                int deletingPos = this.stringBuilder.length() - 1;
                if (deletingPos != 0) {
                    this.stringBuilder.deleteCharAt(deletingPos);
                }
                for (int a = deletingPos; a < 4; a++) {
                    TextView textView = this.characterTextViews.get(a);
                    if (textView.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "translationY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView, "translationX", new float[]{(float) getXForTextView(a)}));
                    }
                    TextView textView2 = this.dotTextViews.get(a);
                    if (textView2.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView2, "scaleX", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView2, "scaleY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView2, "alpha", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView2, "translationY", new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(textView2, "translationX", new float[]{(float) getXForTextView(a)}));
                    }
                }
                if (deletingPos == 0) {
                    this.stringBuilder.deleteCharAt(deletingPos);
                }
                for (int a2 = 0; a2 < deletingPos; a2++) {
                    animators.add(ObjectAnimator.ofFloat(this.characterTextViews.get(a2), "translationX", new float[]{(float) getXForTextView(a2)}));
                    animators.add(ObjectAnimator.ofFloat(this.dotTextViews.get(a2), "translationX", new float[]{(float) getXForTextView(a2)}));
                }
                Runnable runnable = this.dotRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.dotRunnable = null;
                }
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.setDuration(150);
                this.currentAnimation.playTogether(animators);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                            AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
            }
        }

        /* access modifiers changed from: private */
        public void eraseAllCharacters(boolean animated) {
            if (this.stringBuilder.length() != 0) {
                Runnable runnable = this.dotRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.dotRunnable = null;
                }
                AnimatorSet animatorSet = this.currentAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.currentAnimation = null;
                }
                StringBuilder sb = this.stringBuilder;
                sb.delete(0, sb.length());
                if (animated) {
                    ArrayList<Animator> animators = new ArrayList<>();
                    for (int a = 0; a < 4; a++) {
                        TextView textView = this.characterTextViews.get(a);
                        if (textView.getAlpha() != 0.0f) {
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleX", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "scaleY", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f}));
                        }
                        TextView textView2 = this.dotTextViews.get(a);
                        if (textView2.getAlpha() != 0.0f) {
                            animators.add(ObjectAnimator.ofFloat(textView2, "scaleX", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView2, "scaleY", new float[]{0.0f}));
                            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", new float[]{0.0f}));
                        }
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.currentAnimation = animatorSet2;
                    animatorSet2.setDuration(150);
                    this.currentAnimation.playTogether(animators);
                    this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                                AnimatorSet unused = AnimatingTextView.this.currentAnimation = null;
                            }
                        }
                    });
                    this.currentAnimation.start();
                    return;
                }
                for (int a2 = 0; a2 < 4; a2++) {
                    this.characterTextViews.get(a2).setAlpha(0.0f);
                    this.dotTextViews.get(a2).setAlpha(0.0f);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            Runnable runnable = this.dotRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.dotRunnable = null;
            }
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
            for (int a = 0; a < 4; a++) {
                if (a < this.stringBuilder.length()) {
                    TextView textView = this.characterTextViews.get(a);
                    textView.setAlpha(0.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX((float) getXForTextView(a));
                    TextView textView2 = this.dotTextViews.get(a);
                    textView2.setAlpha(1.0f);
                    textView2.setScaleX(1.0f);
                    textView2.setScaleY(1.0f);
                    textView2.setTranslationY(0.0f);
                    textView2.setTranslationX((float) getXForTextView(a));
                } else {
                    this.characterTextViews.get(a).setAlpha(0.0f);
                    this.dotTextViews.get(a).setAlpha(0.0f);
                }
            }
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PasscodeView(Context context) {
        super(context);
        Context context2 = context;
        char c = 0;
        setWillNotDraw(false);
        setVisibility(8);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.backgroundFrameLayout = frameLayout;
        addView(frameLayout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.backgroundFrameLayout.getLayoutParams();
        int i = -1;
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.backgroundFrameLayout.setLayoutParams(layoutParams);
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.passwordFrameLayout = frameLayout2;
        addView(frameLayout2);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.gravity = 51;
        this.passwordFrameLayout.setLayoutParams(layoutParams2);
        ImageView imageView = new ImageView(context2);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.passcode_logo);
        this.passwordFrameLayout.addView(imageView);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        if (AndroidUtilities.density < 1.0f) {
            layoutParams3.width = AndroidUtilities.dp(30.0f);
            layoutParams3.height = AndroidUtilities.dp(30.0f);
        } else {
            layoutParams3.width = AndroidUtilities.dp(40.0f);
            layoutParams3.height = AndroidUtilities.dp(40.0f);
        }
        layoutParams3.gravity = 81;
        layoutParams3.bottomMargin = AndroidUtilities.dp(100.0f);
        imageView.setLayoutParams(layoutParams3);
        TextView textView = new TextView(context2);
        this.passcodeTextView = textView;
        textView.setTextColor(-1);
        this.passcodeTextView.setTextSize(1, 14.0f);
        this.passcodeTextView.setGravity(1);
        this.passwordFrameLayout.addView(this.passcodeTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 81, 0.0f, 0.0f, 0.0f, 62.0f));
        TextView textView2 = new TextView(context2);
        this.retryTextView = textView2;
        textView2.setTextColor(-1);
        this.retryTextView.setTextSize(1, 15.0f);
        this.retryTextView.setGravity(1);
        this.retryTextView.setVisibility(4);
        addView(this.retryTextView, LayoutHelper.createFrame(-2, -2, 17));
        AnimatingTextView animatingTextView = new AnimatingTextView(context2);
        this.passwordEditText2 = animatingTextView;
        this.passwordFrameLayout.addView(animatingTextView);
        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.passwordEditText2.getLayoutParams();
        layoutParams4.height = -2;
        layoutParams4.width = -1;
        layoutParams4.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams4.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams4.bottomMargin = AndroidUtilities.dp(6.0f);
        layoutParams4.gravity = 81;
        this.passwordEditText2.setLayoutParams(layoutParams4);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.passwordEditText = editTextBoldCursor;
        float f = 36.0f;
        editTextBoldCursor.setTextSize(1, 36.0f);
        this.passwordEditText.setTextColor(-1);
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setImeOptions(6);
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setBackgroundDrawable((Drawable) null);
        this.passwordEditText.setCursorColor(-1);
        this.passwordEditText.setCursorSize(AndroidUtilities.dp(32.0f));
        this.passwordFrameLayout.addView(this.passwordEditText);
        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.passwordEditText.getLayoutParams();
        layoutParams5.height = -2;
        layoutParams5.width = -1;
        layoutParams5.leftMargin = AndroidUtilities.dp(70.0f);
        layoutParams5.rightMargin = AndroidUtilities.dp(70.0f);
        layoutParams5.gravity = 81;
        this.passwordEditText.setLayoutParams(layoutParams5);
        this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return PasscodeView.this.lambda$new$0$PasscodeView(textView, i, keyEvent);
            }
        });
        this.passwordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (PasscodeView.this.passwordEditText.length() == 4 && SharedConfig.passcodeType == 0) {
                    PasscodeView.this.processDone(false);
                }
            }
        });
        this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
        ImageView imageView2 = new ImageView(context2);
        this.checkImage = imageView2;
        imageView2.setImageResource(R.drawable.passcode_check);
        this.checkImage.setScaleType(ImageView.ScaleType.CENTER);
        this.checkImage.setBackgroundResource(R.drawable.bar_selector_lock);
        this.passwordFrameLayout.addView(this.checkImage);
        FrameLayout.LayoutParams layoutParams6 = (FrameLayout.LayoutParams) this.checkImage.getLayoutParams();
        layoutParams6.width = AndroidUtilities.dp(60.0f);
        layoutParams6.height = AndroidUtilities.dp(60.0f);
        layoutParams6.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams6.rightMargin = AndroidUtilities.dp(10.0f);
        layoutParams6.gravity = 85;
        this.checkImage.setLayoutParams(layoutParams6);
        this.checkImage.setContentDescription(LocaleController.getString("Done", R.string.Done));
        this.checkImage.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PasscodeView.this.lambda$new$1$PasscodeView(view);
            }
        });
        FrameLayout lineFrameLayout = new FrameLayout(context2);
        lineFrameLayout.setBackgroundColor(654311423);
        this.passwordFrameLayout.addView(lineFrameLayout);
        FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) lineFrameLayout.getLayoutParams();
        layoutParams7.width = -1;
        layoutParams7.height = AndroidUtilities.dp(1.0f);
        layoutParams7.gravity = 83;
        layoutParams7.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams7.rightMargin = AndroidUtilities.dp(20.0f);
        lineFrameLayout.setLayoutParams(layoutParams7);
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.numbersFrameLayout = frameLayout3;
        addView(frameLayout3);
        FrameLayout.LayoutParams layoutParams8 = (FrameLayout.LayoutParams) this.numbersFrameLayout.getLayoutParams();
        layoutParams8.width = -1;
        layoutParams8.height = -1;
        layoutParams8.gravity = 51;
        this.numbersFrameLayout.setLayoutParams(layoutParams8);
        int i2 = 10;
        this.lettersTextViews = new ArrayList<>(10);
        this.numberTextViews = new ArrayList<>(10);
        this.numberFrameLayouts = new ArrayList<>(10);
        int a = 0;
        while (a < i2) {
            TextView textView3 = new TextView(context2);
            textView3.setTextColor(i);
            textView3.setTextSize(1, f);
            textView3.setGravity(17);
            Locale locale = Locale.US;
            Object[] objArr = new Object[1];
            objArr[c] = Integer.valueOf(a);
            textView3.setText(String.format(locale, "%d", objArr));
            this.numbersFrameLayout.addView(textView3);
            FrameLayout.LayoutParams layoutParams9 = (FrameLayout.LayoutParams) textView3.getLayoutParams();
            layoutParams9.width = AndroidUtilities.dp(50.0f);
            layoutParams9.height = AndroidUtilities.dp(50.0f);
            layoutParams9.gravity = 51;
            textView3.setLayoutParams(layoutParams9);
            textView3.setImportantForAccessibility(2);
            this.numberTextViews.add(textView3);
            TextView textView4 = new TextView(context2);
            textView4.setTextSize(1, 12.0f);
            textView4.setTextColor(Integer.MAX_VALUE);
            textView4.setGravity(17);
            this.numbersFrameLayout.addView(textView4);
            FrameLayout.LayoutParams layoutParams10 = (FrameLayout.LayoutParams) textView4.getLayoutParams();
            layoutParams10.width = AndroidUtilities.dp(50.0f);
            layoutParams10.height = AndroidUtilities.dp(20.0f);
            layoutParams10.gravity = 51;
            textView4.setLayoutParams(layoutParams10);
            textView4.setImportantForAccessibility(2);
            if (a != 0) {
                switch (a) {
                    case 2:
                        textView4.setText("ABC");
                        break;
                    case 3:
                        textView4.setText("DEF");
                        break;
                    case 4:
                        textView4.setText("GHI");
                        break;
                    case 5:
                        textView4.setText("JKL");
                        break;
                    case 6:
                        textView4.setText("MNO");
                        break;
                    case 7:
                        textView4.setText("PQRS");
                        break;
                    case 8:
                        textView4.setText("TUV");
                        break;
                    case 9:
                        textView4.setText("WXYZ");
                        break;
                }
            } else {
                textView4.setText(Marker.ANY_NON_NULL_MARKER);
            }
            this.lettersTextViews.add(textView4);
            a++;
            FrameLayout.LayoutParams layoutParams11 = layoutParams10;
            c = 0;
            i = -1;
            i2 = 10;
            f = 36.0f;
        }
        ImageView imageView3 = new ImageView(context2);
        this.eraseView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.eraseView.setImageResource(R.drawable.passcode_delete);
        this.numbersFrameLayout.addView(this.eraseView);
        FrameLayout.LayoutParams layoutParams12 = (FrameLayout.LayoutParams) this.eraseView.getLayoutParams();
        layoutParams12.width = AndroidUtilities.dp(50.0f);
        layoutParams12.height = AndroidUtilities.dp(50.0f);
        layoutParams12.gravity = 51;
        this.eraseView.setLayoutParams(layoutParams12);
        for (int a2 = 0; a2 < 11; a2++) {
            FrameLayout frameLayout4 = new FrameLayout(context2) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    info.setClassName("android.widget.Button");
                }
            };
            frameLayout4.setBackgroundResource(R.drawable.bar_selector_lock);
            frameLayout4.setTag(Integer.valueOf(a2));
            if (a2 == 10) {
                frameLayout4.setOnLongClickListener(new View.OnLongClickListener() {
                    public final boolean onLongClick(View view) {
                        return PasscodeView.this.lambda$new$2$PasscodeView(view);
                    }
                });
                frameLayout4.setContentDescription(LocaleController.getString("AccDescrBackspace", R.string.AccDescrBackspace));
                setNextFocus(frameLayout4, R.id.passcode_btn_1);
            } else {
                frameLayout4.setContentDescription(a2 + "");
                if (a2 == 0) {
                    setNextFocus(frameLayout4, R.id.passcode_btn_backspace);
                } else if (a2 == 9) {
                    setNextFocus(frameLayout4, R.id.passcode_btn_0);
                } else {
                    setNextFocus(frameLayout4, ids[a2 + 1]);
                }
            }
            frameLayout4.setId(ids[a2]);
            frameLayout4.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PasscodeView.this.lambda$new$3$PasscodeView(view);
                }
            });
            this.numberFrameLayouts.add(frameLayout4);
        }
        for (int a3 = 10; a3 >= 0; a3--) {
            FrameLayout frameLayout5 = this.numberFrameLayouts.get(a3);
            this.numbersFrameLayout.addView(frameLayout5);
            FrameLayout.LayoutParams layoutParams13 = (FrameLayout.LayoutParams) frameLayout5.getLayoutParams();
            layoutParams13.width = AndroidUtilities.dp(100.0f);
            layoutParams13.height = AndroidUtilities.dp(100.0f);
            layoutParams13.gravity = 51;
            frameLayout5.setLayoutParams(layoutParams13);
        }
    }

    public /* synthetic */ boolean lambda$new$0$PasscodeView(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone(false);
        return true;
    }

    public /* synthetic */ void lambda$new$1$PasscodeView(View v) {
        processDone(false);
    }

    public /* synthetic */ boolean lambda$new$2$PasscodeView(View v) {
        this.passwordEditText.setText("");
        this.passwordEditText2.eraseAllCharacters(true);
        return true;
    }

    public /* synthetic */ void lambda$new$3$PasscodeView(View v) {
        switch (((Integer) v.getTag()).intValue()) {
            case 0:
                this.passwordEditText2.appendCharacter("0");
                break;
            case 1:
                this.passwordEditText2.appendCharacter("1");
                break;
            case 2:
                this.passwordEditText2.appendCharacter("2");
                break;
            case 3:
                this.passwordEditText2.appendCharacter(ExifInterface.GPS_MEASUREMENT_3D);
                break;
            case 4:
                this.passwordEditText2.appendCharacter("4");
                break;
            case 5:
                this.passwordEditText2.appendCharacter("5");
                break;
            case 6:
                this.passwordEditText2.appendCharacter("6");
                break;
            case 7:
                this.passwordEditText2.appendCharacter("7");
                break;
            case 8:
                this.passwordEditText2.appendCharacter("8");
                break;
            case 9:
                this.passwordEditText2.appendCharacter("9");
                break;
            case 10:
                this.passwordEditText2.eraseLastCharacter();
                break;
        }
        if (this.passwordEditText2.length() == 4) {
            processDone(false);
        }
    }

    private void setNextFocus(View view, int nextId) {
        view.setNextFocusForwardId(nextId);
        if (Build.VERSION.SDK_INT >= 22) {
            view.setAccessibilityTraversalBefore(nextId);
        }
    }

    public void setDelegate(PasscodeViewDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void processDone(boolean fingerprint) {
        if (!fingerprint) {
            if (SharedConfig.passcodeRetryInMs <= 0) {
                String password = "";
                if (SharedConfig.passcodeType == 0) {
                    password = this.passwordEditText2.getString();
                } else if (SharedConfig.passcodeType == 1) {
                    password = this.passwordEditText.getText().toString();
                }
                if (password.length() == 0) {
                    onPasscodeError();
                    return;
                } else if (!SharedConfig.checkPasscode(password)) {
                    SharedConfig.increaseBadPasscodeTries();
                    if (SharedConfig.passcodeRetryInMs > 0) {
                        checkRetryTextView();
                    }
                    this.passwordEditText.setText("");
                    this.passwordEditText2.eraseAllCharacters(true);
                    onPasscodeError();
                    return;
                }
            } else {
                return;
            }
        }
        SharedConfig.badPasscodeTries = 0;
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.setDuration(200);
        AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "translationY", new float[]{(float) AndroidUtilities.dp(20.0f)}), ObjectAnimator.ofFloat(this, "alpha", new float[]{(float) AndroidUtilities.dp(0.0f)})});
        AnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                PasscodeView.this.setVisibility(8);
            }
        });
        AnimatorSet.start();
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener((View.OnTouchListener) null);
        PasscodeViewDelegate passcodeViewDelegate = this.delegate;
        if (passcodeViewDelegate != null) {
            passcodeViewDelegate.didAcceptedPassword();
        }
    }

    /* access modifiers changed from: private */
    public void shakeTextView(final float x, final int num) {
        if (num != 6) {
            AnimatorSet AnimatorSet = new AnimatorSet();
            AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.passcodeTextView, "translationX", new float[]{(float) AndroidUtilities.dp(x)})});
            AnimatorSet.setDuration(50);
            AnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PasscodeView.this.shakeTextView(num == 5 ? 0.0f : -x, num + 1);
                }
            });
            AnimatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public void checkRetryTextView() {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime > SharedConfig.lastUptimeMillis) {
            SharedConfig.passcodeRetryInMs -= currentTime - SharedConfig.lastUptimeMillis;
            if (SharedConfig.passcodeRetryInMs < 0) {
                SharedConfig.passcodeRetryInMs = 0;
            }
        }
        SharedConfig.lastUptimeMillis = currentTime;
        SharedConfig.saveConfig();
        if (SharedConfig.passcodeRetryInMs > 0) {
            int value = Math.max(1, (int) Math.ceil(((double) SharedConfig.passcodeRetryInMs) / 1000.0d));
            if (value != this.lastValue) {
                this.retryTextView.setText(LocaleController.formatString("TooManyTries", R.string.TooManyTries, LocaleController.formatPluralString("Seconds", value)));
                this.lastValue = value;
            }
            if (this.retryTextView.getVisibility() != 0) {
                this.retryTextView.setVisibility(0);
                this.passwordFrameLayout.setVisibility(4);
                if (this.numbersFrameLayout.getVisibility() == 0) {
                    this.numbersFrameLayout.setVisibility(4);
                }
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
                AndroidUtilities.runOnUIThread(this.checkRunnable, 100);
                return;
            }
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        if (this.passwordFrameLayout.getVisibility() != 0) {
            this.retryTextView.setVisibility(4);
            this.passwordFrameLayout.setVisibility(0);
            if (SharedConfig.passcodeType == 0) {
                this.numbersFrameLayout.setVisibility(0);
            } else if (SharedConfig.passcodeType == 1) {
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        }
    }

    private void onPasscodeError() {
        Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        shakeTextView(2.0f, 0);
    }

    public void onResume() {
        checkRetryTextView();
        if (this.retryTextView.getVisibility() != 0) {
            if (SharedConfig.passcodeType == 1) {
                EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
                if (editTextBoldCursor != null) {
                    editTextBoldCursor.requestFocus();
                    AndroidUtilities.showKeyboard(this.passwordEditText);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        PasscodeView.this.lambda$onResume$4$PasscodeView();
                    }
                }, 200);
            }
            checkFingerprint();
        }
    }

    public /* synthetic */ void lambda$onResume$4$PasscodeView() {
        EditTextBoldCursor editTextBoldCursor;
        if (this.retryTextView.getVisibility() != 0 && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void onPause() {
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        AlertDialog alertDialog = this.fingerprintDialog;
        if (alertDialog != null) {
            try {
                if (alertDialog.isShowing()) {
                    this.fingerprintDialog.dismiss();
                }
                this.fingerprintDialog = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= 23 && this.cancellationSignal != null) {
                this.cancellationSignal.cancel();
                this.cancellationSignal = null;
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    private void checkFingerprint() {
        Activity parentActivity = (Activity) getContext();
        if (Build.VERSION.SDK_INT >= 23 && parentActivity != null && SharedConfig.useFingerprint && !ApplicationLoader.mainInterfacePaused) {
            try {
                if (this.fingerprintDialog != null && this.fingerprintDialog.isShowing()) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    TextView fingerprintTextView = new TextView(getContext());
                    fingerprintTextView.setId(1000);
                    fingerprintTextView.setTextAppearance(16974344);
                    fingerprintTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    fingerprintTextView.setText(LocaleController.getString("FingerprintInfo", R.string.FingerprintInfo));
                    relativeLayout.addView(fingerprintTextView);
                    RelativeLayout.LayoutParams layoutParams = LayoutHelper.createRelative(-2, -2);
                    layoutParams.addRule(10);
                    layoutParams.addRule(20);
                    fingerprintTextView.setLayoutParams(layoutParams);
                    ImageView imageView = new ImageView(getContext());
                    this.fingerprintImageView = imageView;
                    imageView.setImageResource(R.drawable.ic_fp_40px);
                    this.fingerprintImageView.setId(1001);
                    relativeLayout.addView(this.fingerprintImageView, LayoutHelper.createRelative(-2.0f, -2.0f, 0, 20, 0, 0, 20, 3, 1000));
                    TextView textView = new TextView(getContext());
                    this.fingerprintStatusTextView = textView;
                    textView.setGravity(16);
                    this.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", R.string.FingerprintHelp));
                    this.fingerprintStatusTextView.setTextAppearance(16974320);
                    this.fingerprintStatusTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack) & 1124073471);
                    relativeLayout.addView(this.fingerprintStatusTextView);
                    RelativeLayout.LayoutParams layoutParams2 = LayoutHelper.createRelative(-2, -2);
                    layoutParams2.setMarginStart(AndroidUtilities.dp(16.0f));
                    layoutParams2.addRule(8, 1001);
                    layoutParams2.addRule(6, 1001);
                    layoutParams2.addRule(17, 1001);
                    this.fingerprintStatusTextView.setLayoutParams(layoutParams2);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setView(relativeLayout);
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public final void onDismiss(DialogInterface dialogInterface) {
                            PasscodeView.this.lambda$checkFingerprint$5$PasscodeView(dialogInterface);
                        }
                    });
                    if (this.fingerprintDialog != null) {
                        if (this.fingerprintDialog.isShowing()) {
                            this.fingerprintDialog.dismiss();
                        }
                    }
                    this.fingerprintDialog = builder.show();
                    CancellationSignal cancellationSignal2 = new CancellationSignal();
                    this.cancellationSignal = cancellationSignal2;
                    this.selfCancelled = false;
                    fingerprintManager.authenticate((FingerprintManagerCompat.CryptoObject) null, 0, cancellationSignal2, new FingerprintManagerCompat.AuthenticationCallback() {
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            if (!PasscodeView.this.selfCancelled && errMsgId != 5) {
                                PasscodeView.this.showFingerprintError(errString);
                            }
                        }

                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                            PasscodeView.this.showFingerprintError(helpString);
                        }

                        public void onAuthenticationFailed() {
                            PasscodeView.this.showFingerprintError(LocaleController.getString("FingerprintNotRecognized", R.string.FingerprintNotRecognized));
                        }

                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                            try {
                                if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                    PasscodeView.this.fingerprintDialog.dismiss();
                                }
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                            AlertDialog unused = PasscodeView.this.fingerprintDialog = null;
                            PasscodeView.this.processDone(true);
                        }
                    }, (Handler) null);
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            } catch (Throwable th) {
            }
        }
    }

    public /* synthetic */ void lambda$checkFingerprint$5$PasscodeView(DialogInterface dialog) {
        CancellationSignal cancellationSignal2 = this.cancellationSignal;
        if (cancellationSignal2 != null) {
            this.selfCancelled = true;
            try {
                cancellationSignal2.cancel();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.cancellationSignal = null;
        }
    }

    public void onShow() {
        View currentFocus;
        EditTextBoldCursor editTextBoldCursor;
        checkRetryTextView();
        Activity parentActivity = (Activity) getContext();
        if (SharedConfig.passcodeType == 1) {
            if (!(this.retryTextView.getVisibility() == 0 || (editTextBoldCursor = this.passwordEditText) == null)) {
                editTextBoldCursor.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        } else if (!(parentActivity == null || (currentFocus = parentActivity.getCurrentFocus()) == null)) {
            currentFocus.clearFocus();
            AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
        }
        if (this.retryTextView.getVisibility() != 0) {
            checkFingerprint();
        }
        if (getVisibility() != 0) {
            setAlpha(1.0f);
            setTranslationY(0.0f);
            if (Theme.isCustomTheme()) {
                this.backgroundDrawable = Theme.getCachedWallpaper();
                this.backgroundFrameLayout.setBackgroundColor(-1090519040);
            } else if (Theme.getSelectedBackgroundId() == Theme.DEFAULT_BACKGROUND_ID) {
                this.backgroundFrameLayout.setBackgroundColor(-11436898);
            } else {
                Drawable cachedWallpaper = Theme.getCachedWallpaper();
                this.backgroundDrawable = cachedWallpaper;
                if (cachedWallpaper != null) {
                    this.backgroundFrameLayout.setBackgroundColor(-1090519040);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(-11436898);
                }
            }
            this.passcodeTextView.setText(LocaleController.getString("EnterYourPasscode", R.string.EnterYourPasscode));
            if (SharedConfig.passcodeType == 0) {
                if (this.retryTextView.getVisibility() != 0) {
                    this.numbersFrameLayout.setVisibility(0);
                }
                this.passwordEditText.setVisibility(8);
                this.passwordEditText2.setVisibility(0);
                this.checkImage.setVisibility(8);
            } else if (SharedConfig.passcodeType == 1) {
                this.passwordEditText.setFilters(new InputFilter[0]);
                this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                this.numbersFrameLayout.setVisibility(8);
                this.passwordEditText.setFocusable(true);
                this.passwordEditText.setFocusableInTouchMode(true);
                this.passwordEditText.setVisibility(0);
                this.passwordEditText2.setVisibility(8);
                this.checkImage.setVisibility(0);
            }
            setVisibility(0);
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordEditText.setText("");
            this.passwordEditText2.eraseAllCharacters(false);
            setOnTouchListener($$Lambda$PasscodeView$KnJ3x4SBjNNmYCR0wjye3NAwex8.INSTANCE);
        }
    }

    static /* synthetic */ boolean lambda$onShow$6(View v, MotionEvent event) {
        return true;
    }

    /* access modifiers changed from: private */
    public void showFingerprintError(CharSequence error) {
        this.fingerprintImageView.setImageResource(R.drawable.ic_fingerprint_error);
        this.fingerprintStatusTextView.setText(error);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    /* JADX WARNING: type inference failed for: r8v6, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: type inference failed for: r13v10, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: type inference failed for: r7v9, types: [android.view.ViewGroup$LayoutParams] */
    /* JADX WARNING: type inference failed for: r4v11, types: [android.view.ViewGroup$LayoutParams] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r18, int r19) {
        /*
            r17 = this;
            r0 = r17
            int r1 = android.view.View.MeasureSpec.getSize(r18)
            android.graphics.Point r2 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r2 = r2.y
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r3 < r4) goto L_0x0012
            r3 = 0
            goto L_0x0014
        L_0x0012:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.statusBarHeight
        L_0x0014:
            int r2 = r2 - r3
            boolean r3 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            r4 = 2
            if (r3 != 0) goto L_0x0073
            android.content.Context r3 = r17.getContext()
            android.content.res.Resources r3 = r3.getResources()
            android.content.res.Configuration r3 = r3.getConfiguration()
            int r3 = r3.orientation
            if (r3 != r4) goto L_0x0073
            android.widget.FrameLayout r3 = r0.passwordFrameLayout
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r5 = im.bclpbkiauv.messenger.SharedConfig.passcodeType
            if (r5 != 0) goto L_0x003b
            int r5 = r1 / 2
            goto L_0x003c
        L_0x003b:
            r5 = r1
        L_0x003c:
            r3.width = r5
            r5 = 1124859904(0x430c0000, float:140.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r3.height = r6
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = r2 - r5
            int r5 = r5 / r4
            r3.topMargin = r5
            android.widget.FrameLayout r4 = r0.passwordFrameLayout
            r4.setLayoutParams(r3)
            android.widget.FrameLayout r4 = r0.numbersFrameLayout
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            r3 = r4
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r3.height = r2
            int r4 = r1 / 2
            r3.leftMargin = r4
            int r4 = r3.height
            int r4 = r2 - r4
            r3.topMargin = r4
            int r4 = r1 / 2
            r3.width = r4
            android.widget.FrameLayout r4 = r0.numbersFrameLayout
            r4.setLayoutParams(r3)
            goto L_0x00e3
        L_0x0073:
            r3 = 0
            r5 = 0
            boolean r6 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r6 == 0) goto L_0x00a3
            r6 = 1140391936(0x43f90000, float:498.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            if (r1 <= r7) goto L_0x008f
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r7 = r1 - r7
            int r5 = r7 / 2
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
        L_0x008f:
            r6 = 1141112832(0x44040000, float:528.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            if (r2 <= r7) goto L_0x00a3
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r7 = r2 - r7
            int r3 = r7 / 2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
        L_0x00a3:
            android.widget.FrameLayout r6 = r0.passwordFrameLayout
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            int r7 = r2 / 3
            r6.height = r7
            r6.width = r1
            r6.topMargin = r3
            r6.leftMargin = r5
            android.widget.FrameLayout r7 = r0.passwordFrameLayout
            java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
            r7.setTag(r8)
            android.widget.FrameLayout r7 = r0.passwordFrameLayout
            r7.setLayoutParams(r6)
            android.widget.FrameLayout r7 = r0.numbersFrameLayout
            android.view.ViewGroup$LayoutParams r7 = r7.getLayoutParams()
            r6 = r7
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            int r7 = r2 / 3
            int r7 = r7 * 2
            r6.height = r7
            r6.leftMargin = r5
            int r4 = r6.height
            int r4 = r2 - r4
            int r4 = r4 + r3
            r6.topMargin = r4
            r6.width = r1
            android.widget.FrameLayout r4 = r0.numbersFrameLayout
            r4.setLayoutParams(r6)
            r3 = r6
        L_0x00e3:
            int r4 = r3.width
            r5 = 1112014848(0x42480000, float:50.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r6 = r6 * 3
            int r4 = r4 - r6
            int r4 = r4 / 4
            int r6 = r3.height
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r7 = r7 * 4
            int r6 = r6 - r7
            int r6 = r6 / 5
            r7 = 0
        L_0x00fc:
            r8 = 11
            if (r7 >= r8) goto L_0x01b5
            r8 = 10
            if (r7 != 0) goto L_0x0107
            r9 = 10
            goto L_0x010e
        L_0x0107:
            if (r7 != r8) goto L_0x010c
            r9 = 11
            goto L_0x010e
        L_0x010c:
            int r9 = r7 + -1
        L_0x010e:
            int r10 = r9 / 3
            int r11 = r9 % 3
            if (r7 >= r8) goto L_0x015c
            java.util.ArrayList<android.widget.TextView> r8 = r0.numberTextViews
            java.lang.Object r8 = r8.get(r7)
            android.widget.TextView r8 = (android.widget.TextView) r8
            java.util.ArrayList<android.widget.TextView> r12 = r0.lettersTextViews
            java.lang.Object r12 = r12.get(r7)
            android.widget.TextView r12 = (android.widget.TextView) r12
            android.view.ViewGroup$LayoutParams r13 = r8.getLayoutParams()
            r3 = r13
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            android.view.ViewGroup$LayoutParams r13 = r12.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r13 = (android.widget.FrameLayout.LayoutParams) r13
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r14 = r14 + r6
            int r14 = r14 * r10
            int r14 = r14 + r6
            r3.topMargin = r14
            r13.topMargin = r14
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r15 = r15 + r4
            int r15 = r15 * r11
            int r15 = r15 + r4
            r3.leftMargin = r15
            r13.leftMargin = r15
            int r15 = r13.topMargin
            r16 = 1109393408(0x42200000, float:40.0)
            int r16 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r15 = r15 + r16
            r13.topMargin = r15
            r8.setLayoutParams(r3)
            r12.setLayoutParams(r13)
            goto L_0x018b
        L_0x015c:
            android.widget.ImageView r8 = r0.eraseView
            android.view.ViewGroup$LayoutParams r8 = r8.getLayoutParams()
            r3 = r8
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r8 = r8 + r6
            int r8 = r8 * r10
            int r8 = r8 + r6
            r12 = 1090519040(0x41000000, float:8.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            int r8 = r8 + r13
            r3.topMargin = r8
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r13 = r13 + r4
            int r13 = r13 * r11
            int r13 = r13 + r4
            r3.leftMargin = r13
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            int r14 = r8 - r12
            android.widget.ImageView r8 = r0.eraseView
            r8.setLayoutParams(r3)
        L_0x018b:
            java.util.ArrayList<android.widget.FrameLayout> r8 = r0.numberFrameLayouts
            java.lang.Object r8 = r8.get(r7)
            android.widget.FrameLayout r8 = (android.widget.FrameLayout) r8
            android.view.ViewGroup$LayoutParams r12 = r8.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r12 = (android.widget.FrameLayout.LayoutParams) r12
            r13 = 1099431936(0x41880000, float:17.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            int r13 = r14 - r13
            r12.topMargin = r13
            int r13 = r3.leftMargin
            r15 = 1103626240(0x41c80000, float:25.0)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            int r13 = r13 - r15
            r12.leftMargin = r13
            r8.setLayoutParams(r12)
            int r7 = r7 + 1
            goto L_0x00fc
        L_0x01b5:
            super.onMeasure(r18, r19)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.PasscodeView.onMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View rootView = getRootView();
        int usableViewHeight = (rootView.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        this.keyboardHeight = usableViewHeight - (this.rect.bottom - this.rect.top);
        if (SharedConfig.passcodeType == 1 && (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2)) {
            int t = 0;
            if (this.passwordFrameLayout.getTag() != null) {
                t = ((Integer) this.passwordFrameLayout.getTag()).intValue();
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams.topMargin = ((layoutParams.height + t) - (this.keyboardHeight / 2)) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            this.passwordFrameLayout.setLayoutParams(layoutParams);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            Drawable drawable = this.backgroundDrawable;
            if (drawable == null) {
                super.onDraw(canvas);
            } else if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.backgroundDrawable.draw(canvas);
            } else {
                float scaleX = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                float scaleY = ((float) (getMeasuredHeight() + this.keyboardHeight)) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                float scale = scaleX < scaleY ? scaleY : scaleX;
                int width = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * scale));
                int height = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * scale));
                int x = (getMeasuredWidth() - width) / 2;
                int y = ((getMeasuredHeight() - height) + this.keyboardHeight) / 2;
                this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                this.backgroundDrawable.draw(canvas);
            }
        }
    }
}

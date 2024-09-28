package com.bigkoo.pickerview.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.utils.PickerViewAnimateUtil;

public class BasePickerView {
    protected int animGravity = 80;
    protected View clickView;
    protected ViewGroup contentContainer;
    private Context context;
    private ViewGroup dialogView;
    /* access modifiers changed from: private */
    public boolean dismissing;
    private Animation inAnim;
    private boolean isAnim = true;
    /* access modifiers changed from: private */
    public boolean isShowing;
    private Dialog mDialog;
    protected PickerOptions mPickerOptions;
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != 0) {
                return false;
            }
            BasePickerView.this.dismiss();
            return false;
        }
    };
    /* access modifiers changed from: private */
    public OnDismissListener onDismissListener;
    private View.OnKeyListener onKeyBackListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode != 4 || event.getAction() != 0 || !BasePickerView.this.isShowing()) {
                return false;
            }
            BasePickerView.this.dismiss();
            return true;
        }
    };
    private Animation outAnim;
    /* access modifiers changed from: private */
    public ViewGroup rootView;

    public BasePickerView(Context context2) {
        this.context = context2;
    }

    /* access modifiers changed from: protected */
    public void initViews() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2, 80);
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        if (isDialog()) {
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.layout_basepickerview, (ViewGroup) null, false);
            this.dialogView = viewGroup;
            viewGroup.setBackgroundColor(0);
            this.contentContainer = (ViewGroup) this.dialogView.findViewById(R.id.content_container);
            params.leftMargin = 30;
            params.rightMargin = 30;
            this.contentContainer.setLayoutParams(params);
            createDialog();
            this.dialogView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    BasePickerView.this.dismiss();
                }
            });
        } else {
            if (this.mPickerOptions.decorView == null) {
                this.mPickerOptions.decorView = (ViewGroup) ((Activity) this.context).getWindow().getDecorView();
            }
            ViewGroup viewGroup2 = (ViewGroup) layoutInflater.inflate(R.layout.layout_basepickerview, this.mPickerOptions.decorView, false);
            this.rootView = viewGroup2;
            viewGroup2.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
            if (this.mPickerOptions.outSideColor != -1) {
                this.rootView.setBackgroundColor(this.mPickerOptions.outSideColor);
            }
            ViewGroup viewGroup3 = (ViewGroup) this.rootView.findViewById(R.id.content_container);
            this.contentContainer = viewGroup3;
            viewGroup3.setLayoutParams(params);
        }
        setKeyBackCancelable(true);
    }

    /* access modifiers changed from: protected */
    public void initAnim() {
        this.inAnim = getInAnimation();
        this.outAnim = getOutAnimation();
    }

    /* access modifiers changed from: protected */
    public void initEvents() {
    }

    public void show(View v, boolean isAnim2) {
        this.clickView = v;
        this.isAnim = isAnim2;
        show();
    }

    public void show(boolean isAnim2) {
        show((View) null, isAnim2);
    }

    public void show(View v) {
        this.clickView = v;
        show();
    }

    public void show() {
        if (isDialog()) {
            showDialog();
        } else if (!isShowing()) {
            this.isShowing = true;
            onAttached(this.rootView);
            this.rootView.requestFocus();
        }
    }

    private void onAttached(View view) {
        this.mPickerOptions.decorView.addView(view);
        if (this.isAnim) {
            this.contentContainer.startAnimation(this.inAnim);
        }
    }

    public boolean isShowing() {
        if (isDialog()) {
            return false;
        }
        if (this.rootView.getParent() != null || this.isShowing) {
            return true;
        }
        return false;
    }

    public void dismiss() {
        if (isDialog()) {
            dismissDialog();
        } else if (!this.dismissing) {
            if (this.isAnim) {
                this.outAnim.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        BasePickerView.this.dismissImmediately();
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                this.contentContainer.startAnimation(this.outAnim);
            } else {
                dismissImmediately();
            }
            this.dismissing = true;
        }
    }

    public void dismissImmediately() {
        this.mPickerOptions.decorView.post(new Runnable() {
            public void run() {
                BasePickerView.this.mPickerOptions.decorView.removeView(BasePickerView.this.rootView);
                boolean unused = BasePickerView.this.isShowing = false;
                boolean unused2 = BasePickerView.this.dismissing = false;
                if (BasePickerView.this.onDismissListener != null) {
                    BasePickerView.this.onDismissListener.onDismiss(BasePickerView.this);
                }
            }
        });
    }

    private Animation getInAnimation() {
        return AnimationUtils.loadAnimation(this.context, PickerViewAnimateUtil.getAnimationResource(this.animGravity, true));
    }

    private Animation getOutAnimation() {
        return AnimationUtils.loadAnimation(this.context, PickerViewAnimateUtil.getAnimationResource(this.animGravity, false));
    }

    public BasePickerView setOnDismissListener(OnDismissListener onDismissListener2) {
        this.onDismissListener = onDismissListener2;
        return this;
    }

    public void setKeyBackCancelable(boolean isCancelable) {
        ViewGroup View;
        if (isDialog()) {
            View = this.dialogView;
        } else {
            View = this.rootView;
        }
        View.setFocusable(isCancelable);
        View.setFocusableInTouchMode(isCancelable);
        if (isCancelable) {
            View.setOnKeyListener(this.onKeyBackListener);
        } else {
            View.setOnKeyListener((View.OnKeyListener) null);
        }
    }

    /* access modifiers changed from: protected */
    public BasePickerView setOutSideCancelable(boolean isCancelable) {
        ViewGroup viewGroup = this.rootView;
        if (viewGroup != null) {
            View view = viewGroup.findViewById(R.id.outmost_container);
            if (isCancelable) {
                view.setOnTouchListener(this.onCancelableTouchListener);
            } else {
                view.setOnTouchListener((View.OnTouchListener) null);
            }
        }
        return this;
    }

    public void setDialogOutSideCancelable() {
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.setCancelable(this.mPickerOptions.cancelable);
        }
    }

    public View findViewById(int id) {
        return this.contentContainer.findViewById(id);
    }

    public void createDialog() {
        if (this.dialogView != null) {
            Dialog dialog = new Dialog(this.context, R.style.custom_dialog2);
            this.mDialog = dialog;
            dialog.setCancelable(this.mPickerOptions.cancelable);
            this.mDialog.setContentView(this.dialogView);
            Window dialogWindow = this.mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_scale_anim);
                dialogWindow.setGravity(17);
            }
            this.mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    if (BasePickerView.this.onDismissListener != null) {
                        BasePickerView.this.onDismissListener.onDismiss(BasePickerView.this);
                    }
                }
            });
        }
    }

    private void showDialog() {
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.show();
        }
    }

    private void dismissDialog() {
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public ViewGroup getDialogContainerLayout() {
        return this.contentContainer;
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public boolean isDialog() {
        return false;
    }
}

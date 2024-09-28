package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.dialogs.BottomDialog;

public class BottomDialog extends BottomSheetDialog {
    private static final int MAX_COUNT = 5;
    /* access modifiers changed from: private */
    public static int dialogTextColor;
    /* access modifiers changed from: private */
    public static Context mContext;
    /* access modifiers changed from: private */
    public static OnItemClickListener onItemClickListener;
    private BottomSheetBehavior<FrameLayout> mBehavior;
    private View mDivider;
    private LinearLayout mLlContainer;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;
    private NestedScrollView mScrollView;
    private TextView mTvCancel;
    private TextView mTvTitle;
    private OnCancelClickListener onCancelClickListener;

    public interface OnCancelClickListener {
        void onClick();
    }

    public interface OnItemClickListener {
        void onItemClick(int i, View view);
    }

    interface TextItem {
    }

    public BottomDialog(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = this.mBehavior;
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(3);
        }
    }

    private void init(Context context) {
        dialogTextColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_dialog, (ViewGroup) null);
        setContentView(view);
        this.mBehavior = BottomSheetBehavior.from((FrameLayout) view.getParent());
        setCancelable(true);
        Window window = getWindow();
        window.findViewById(R.id.design_bottom_sheet).setBackgroundResource(17170445);
        window.setGravity(80);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth();
        window.setAttributes(lp);
        initView(window);
    }

    private void initView(Window window) {
        ((LinearLayout) window.findViewById(R.id.ll_background)).setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mTvTitle = (TextView) window.findViewById(R.id.tv_title);
        this.mScrollView = (NestedScrollView) window.findViewById(R.id.scroll_view);
        this.mLlContainer = (LinearLayout) window.findViewById(R.id.ll_container);
        this.mTvCancel = (TextView) window.findViewById(R.id.tv_cancel);
        View findViewById = window.findViewById(R.id.divider);
        this.mDivider = findViewById;
        findViewById.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.mTvTitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.mTvCancel.setTextColor(dialogTextColor);
        this.mTvCancel.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mTvCancel.setText(LocaleController.getString(R.string.Cancel));
        setCancelButtonAction();
    }

    public void setDialogTextColor(int color) {
        dialogTextColor = color;
        setCancelButtonColor(color);
    }

    public void setTitle(CharSequence text) {
        this.mTvTitle.setText(text);
        this.mTvTitle.setVisibility(0);
    }

    public void setTitleDivider(boolean visible) {
        this.mDivider.setVisibility(visible ? 0 : 8);
    }

    public void addContentView(View v) {
        if (this.mLlContainer.getChildCount() != 0) {
            this.mLlContainer.removeAllViews();
        }
        this.mLlContainer.addView(v);
        resetContentHeight((ViewGroup) v);
    }

    public void setCancelButtonVisibility(int visible) {
        this.mTvCancel.setVisibility(visible);
    }

    public void setCancelButtonColor(int color) {
        this.mTvCancel.setTextColor(color);
    }

    public void setCancelButtonText(CharSequence text) {
        this.mTvCancel.setText(text);
    }

    public void setCancelButtonTextSize(float size) {
        this.mTvCancel.setTextSize(size);
    }

    public TextView getCancelButtom() {
        return this.mTvCancel;
    }

    private void setCancelButtonAction() {
        OnCancelClickListener onCancelClickListener2 = this.onCancelClickListener;
        if (onCancelClickListener2 == null) {
            this.mTvCancel.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    BottomDialog.this.lambda$setCancelButtonAction$0$BottomDialog(view);
                }
            });
        } else {
            onCancelClickListener2.onClick();
        }
    }

    public /* synthetic */ void lambda$setCancelButtonAction$0$BottomDialog(View v) {
        dismiss();
    }

    public void addDialogItem(TextItem item) {
        this.mLlContainer.addView((View) item);
        resetContentHeight(this.mLlContainer);
    }

    public void addDialogItemView(View view) {
        this.mLlContainer.addView(view);
        resetContentHeight(this.mLlContainer);
    }

    public void removeItem(View v) {
        this.mLlContainer.removeView(v);
        resetContentHeight(this.mLlContainer);
    }

    private void resetContentHeight(ViewGroup viewGroup) {
        this.mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener(viewGroup) {
            private final /* synthetic */ ViewGroup f$1;

            {
                this.f$1 = r2;
            }

            public final void onGlobalLayout() {
                BottomDialog.this.lambda$resetContentHeight$1$BottomDialog(this.f$1);
            }
        };
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
    }

    public /* synthetic */ void lambda$resetContentHeight$1$BottomDialog(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        if (count == 0) {
            this.mDivider.setVisibility(4);
        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mScrollView.getLayoutParams();
        if (count <= 5) {
            lp.height = -2;
        } else {
            int height = 0;
            for (int i = 0; i < 5; i++) {
                height += viewGroup.getChildAt(i).getMeasuredHeight();
            }
            lp.height = height;
        }
        this.mScrollView.setLayoutParams(lp);
        viewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this.mOnGlobalLayoutListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.onCancelClickListener = listener;
    }

    public static class NormalTextItem extends FrameLayout implements TextItem {
        private View mDivider;
        private TextView mTvContent;
        private TextView mTvUnreadCount;

        public NormalTextItem(int id, CharSequence text, boolean needDivider) {
            super(BottomDialog.mContext);
            TextView textView = new TextView(BottomDialog.mContext);
            this.mTvContent = textView;
            textView.setGravity(17);
            this.mTvContent.setTextSize(14.0f);
            this.mTvContent.setTextColor(BottomDialog.dialogTextColor);
            if (!TextUtils.isEmpty(text)) {
                this.mTvContent.setText(text);
            }
            addView(this.mTvContent, LayoutHelper.createFrame(-2, -2, 17));
            TextView textView2 = new TextView(BottomDialog.mContext);
            this.mTvUnreadCount = textView2;
            textView2.setGravity(17);
            this.mTvUnreadCount.setTextSize(10.0f);
            this.mTvUnreadCount.setTextColor(BottomDialog.mContext.getResources().getColor(R.color.white));
            this.mTvUnreadCount.setBackgroundResource(R.drawable.shape_dialog_unread_bg);
            addView(this.mTvUnreadCount);
            this.mTvUnreadCount.setVisibility(8);
            View view = new View(BottomDialog.mContext);
            this.mDivider = view;
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            addView(this.mDivider, LayoutHelper.createFrame(-1, 1, 80));
            if (!needDivider) {
                this.mDivider.setVisibility(8);
            }
            setOnClickListener(new View.OnClickListener(id) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    BottomDialog.NormalTextItem.this.lambda$new$0$BottomDialog$NormalTextItem(this.f$1, view);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$BottomDialog$NormalTextItem(int id, View v) {
            if (BottomDialog.onItemClickListener != null) {
                BottomDialog.onItemClickListener.onItemClick(id, this);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = AndroidUtilities.dp(48.0f);
            TextView textView = this.mTvContent;
            if (!(textView == null || textView.getVisibility() == 8)) {
                this.mTvContent.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
            }
            TextView textView2 = this.mTvUnreadCount;
            if (!(textView2 == null || textView2.getVisibility() == 8)) {
                this.mTvUnreadCount.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), 1073741824));
            }
            View view = this.mDivider;
            if (!(view == null || view.getVisibility() == 8)) {
                this.mDivider.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(1, Integer.MIN_VALUE));
            }
            int lineCount = this.mTvContent.getLayout().getLineCount();
            if (lineCount > 0) {
                setMeasuredDimension(width, AndroidUtilities.dp((float) ((lineCount - 1) * 24)) + height);
            } else {
                setMeasuredDimension(width, height);
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            TextView textView = this.mTvContent;
            if (!(textView == null || textView.getVisibility() == 8)) {
                int textLeft = (getMeasuredWidth() / 2) - (this.mTvContent.getMeasuredWidth() / 2);
                int textTop = (getMeasuredHeight() / 2) - (this.mTvContent.getMeasuredHeight() / 2);
                this.mTvContent.layout(textLeft, textTop, getMeasuredWidth() - textLeft, getMeasuredHeight() - textTop);
                TextView textView2 = this.mTvUnreadCount;
                if (!(textView2 == null || textView2.getVisibility() == 8)) {
                    this.mTvUnreadCount.layout(getMeasuredWidth() - textLeft, textTop - (this.mTvUnreadCount.getMeasuredHeight() / 2), (getMeasuredWidth() - textLeft) + this.mTvUnreadCount.getMeasuredWidth(), (this.mTvUnreadCount.getMeasuredHeight() / 2) + textTop);
                }
            }
            View view = this.mDivider;
            if (view != null && view.getVisibility() != 8) {
                this.mDivider.layout(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight());
            }
        }

        public String getContent() {
            return this.mTvContent.getText().toString().trim();
        }

        public TextView getContentTextView() {
            return this.mTvContent;
        }

        public void setTextColor(int color) {
            this.mTvContent.setTextColor(color);
        }

        public void setContentText(CharSequence text) {
            if (!TextUtils.isEmpty(text)) {
                this.mTvContent.setText(text);
            }
        }

        public void setUnreadCount(int count) {
            if (count <= 0) {
                this.mTvUnreadCount.setText(String.valueOf(0));
                this.mTvUnreadCount.setVisibility(8);
            } else if (count > 99) {
                this.mTvUnreadCount.setText("99+");
                this.mTvUnreadCount.setVisibility(0);
            } else {
                this.mTvUnreadCount.setText(String.valueOf(count));
                this.mTvUnreadCount.setVisibility(0);
            }
        }
    }

    public static class TextWithLeftImageItem extends FrameLayout implements TextItem {
        private View mDivider;
        private ImageView mIvLeft;
        private TextView mTvLeft;
        private TextView mTvRight;

        public TextWithLeftImageItem(int id, int imageResId, CharSequence leftText, CharSequence rightText, boolean needDivider) {
            super(BottomDialog.mContext);
            ImageView imageView = new ImageView(BottomDialog.mContext);
            this.mIvLeft = imageView;
            imageView.setImageResource(imageResId);
            this.mIvLeft.setScaleType(ImageView.ScaleType.FIT_XY);
            addView(this.mIvLeft);
            if (imageResId == 0) {
                this.mIvLeft.setVisibility(8);
            }
            TextView textView = new TextView(BottomDialog.mContext);
            this.mTvLeft = textView;
            textView.setGravity(17);
            this.mTvLeft.setTextSize(14.0f);
            this.mTvLeft.setTextColor(BottomDialog.dialogTextColor);
            addView(this.mTvLeft);
            TextView textView2 = new TextView(BottomDialog.mContext);
            this.mTvRight = textView2;
            textView2.setText(rightText);
            this.mTvLeft.setText(leftText);
            this.mTvRight.setGravity(17);
            this.mTvRight.setTextSize(14.0f);
            this.mTvRight.setTextColor(BottomDialog.dialogTextColor);
            addView(this.mTvRight);
            View view = new View(BottomDialog.mContext);
            this.mDivider = view;
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            addView(this.mDivider, LayoutHelper.createFrame(-1, 1, 80));
            if (!needDivider) {
                this.mDivider.setVisibility(8);
            }
            setOnClickListener(new View.OnClickListener(id) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    BottomDialog.TextWithLeftImageItem.this.lambda$new$0$BottomDialog$TextWithLeftImageItem(this.f$1, view);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$BottomDialog$TextWithLeftImageItem(int id, View v) {
            if (BottomDialog.onItemClickListener != null) {
                BottomDialog.onItemClickListener.onItemClick(id, this);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = AndroidUtilities.dp(48.0f);
            ImageView imageView = this.mIvLeft;
            if (!(imageView == null || imageView.getVisibility() == 8)) {
                this.mIvLeft.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(14.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(14.0f), 1073741824));
            }
            TextView textView = this.mTvLeft;
            if (!(textView == null || textView.getVisibility() == 8)) {
                this.mTvLeft.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
            }
            TextView textView2 = this.mTvRight;
            if (!(textView2 == null || textView2.getVisibility() == 8)) {
                this.mTvRight.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), Integer.MIN_VALUE));
            }
            View view = this.mDivider;
            if (!(view == null || view.getVisibility() == 8)) {
                this.mDivider.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(1, Integer.MIN_VALUE));
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int marginLeft = AndroidUtilities.dp(15.0f);
            int marginRight = AndroidUtilities.dp(20.0f);
            ImageView imageView = this.mIvLeft;
            if (!(imageView == null || imageView.getVisibility() == 8)) {
                this.mIvLeft.layout(marginLeft, (getMeasuredHeight() / 2) - (this.mIvLeft.getMeasuredHeight() / 2), this.mIvLeft.getMeasuredWidth() + marginLeft, (getMeasuredHeight() / 2) + (this.mIvLeft.getMeasuredHeight() / 2));
            }
            TextView textView = this.mTvLeft;
            if (!(textView == null || textView.getVisibility() == 8)) {
                int l = marginLeft;
                ImageView imageView2 = this.mIvLeft;
                if (!(imageView2 == null || imageView2.getVisibility() == 8)) {
                    l = (marginLeft * 2) + this.mIvLeft.getMeasuredWidth();
                }
                this.mTvLeft.layout(l, (getMeasuredHeight() / 2) - (this.mTvLeft.getMeasuredHeight() / 2), this.mTvLeft.getMeasuredWidth() + l, (getMeasuredHeight() / 2) + (this.mTvLeft.getMeasuredHeight() / 2));
            }
            if (!(this.mTvRight == null || this.mTvLeft.getVisibility() == 8)) {
                this.mTvRight.layout((getMeasuredWidth() - marginRight) - this.mTvRight.getMeasuredWidth(), (getMeasuredHeight() / 2) - (this.mTvRight.getMeasuredHeight() / 2), getMeasuredWidth() - marginRight, (getMeasuredHeight() / 2) + (this.mTvRight.getMeasuredHeight() / 2));
            }
            View view = this.mDivider;
            if (view != null && view.getVisibility() != 8) {
                this.mDivider.layout(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight());
            }
        }

        public void setLeftImage(int resId) {
            this.mIvLeft.setImageResource(resId);
        }

        public void setLeftImage(Bitmap bitmap) {
            this.mIvLeft.setImageBitmap(bitmap);
        }

        public void setLeftImage(Drawable drawable) {
            this.mIvLeft.setImageDrawable(drawable);
        }

        public void setLeftImageVisibility(int visible) {
            this.mIvLeft.setVisibility(visible);
        }

        public void setLeftText(CharSequence text) {
            this.mTvLeft.setText(text);
        }

        public void setLeftTextColor(int color) {
            this.mTvLeft.setTextColor(color);
        }

        public void setRightText(CharSequence text) {
            this.mTvRight.setText(text);
        }

        public void setRightTextColor(int color) {
            this.mTvRight.setTextColor(color);
        }
    }
}

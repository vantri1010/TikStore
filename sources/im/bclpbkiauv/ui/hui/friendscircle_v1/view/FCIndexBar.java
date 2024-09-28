package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class FCIndexBar extends View {
    public static String[] a = {ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, "B", "C", "D", ExifInterface.LONGITUDE_EAST, "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", ExifInterface.LATITUDE_SOUTH, ExifInterface.GPS_DIRECTION_TRUE, "U", ExifInterface.GPS_MEASUREMENT_INTERRUPTED, ExifInterface.LONGITUDE_WEST, "X", "Y", "Z", "#"};
    public static String[] b = {"↑", "☆", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, "B", "C", "D", ExifInterface.LONGITUDE_EAST, "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", ExifInterface.LATITUDE_SOUTH, ExifInterface.GPS_DIRECTION_TRUE, "U", ExifInterface.GPS_MEASUREMENT_INTERRUPTED, ExifInterface.LONGITUDE_WEST, "X", "Y", "Z", "#"};
    private float beginY;
    public String[] chars;
    private OnTouchingLetterChangedListener listener;
    private float mCellHeight;
    private int mCellWidth;
    private Context mContext;
    private int mHeight;
    private int mIndex;
    private TextView mTextDialog;
    private boolean onlyChar;
    private Paint paint;

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String str);
    }

    public void setTextView(TextView mTextDialog2) {
        this.mTextDialog = mTextDialog2;
    }

    public FCIndexBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.paint = new Paint();
        this.mIndex = -1;
        this.mContext = context;
    }

    public void setCharsOnly() {
        this.chars = a;
    }

    public FCIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FCIndexBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public void setOnlyChar(boolean flag) {
        this.onlyChar = flag;
    }

    public void setTextColor(int color) {
        this.paint.setColor(color);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String[] strArr = this.chars;
        if (strArr != null && strArr.length > 0) {
            for (int i = 0; i < this.chars.length; i++) {
                this.paint.setAntiAlias(true);
                this.paint.setTextSize((float) dip2px(this.mContext, 9.5f));
                if (i == this.mIndex) {
                    this.paint.setColor(getResources().getColor(R.color.side_bar_text_selected_color));
                    this.paint.setFakeBoldText(true);
                } else {
                    this.paint.setColor(Theme.getColor(Theme.key_sidebar_textDefaultColor));
                    this.paint.setTypeface(Typeface.DEFAULT);
                }
                if (!TextUtils.isEmpty(this.chars[i])) {
                    Rect rect = new Rect();
                    float textWidth = this.paint.measureText(this.chars[i]);
                    Paint paint2 = this.paint;
                    String[] strArr2 = this.chars;
                    paint2.getTextBounds(strArr2[i], 0, strArr2[i].length(), rect);
                    float x = (((float) this.mCellWidth) * 0.5f) - (textWidth * 0.5f);
                    float f = this.mCellHeight;
                    canvas.drawText(this.chars[i], x, (f * 0.5f) + (0.5f * ((float) rect.height())) + (f * ((float) i)) + this.beginY, this.paint);
                }
                this.paint.reset();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        this.mCellWidth = getMeasuredWidth();
        int i = this.mHeight;
        float f = (((float) i) * 1.0f) / 26.0f;
        this.mCellHeight = f;
        String[] strArr = this.chars;
        if (strArr != null) {
            this.beginY = (((float) i) - (f * ((float) strArr.length))) * 0.5f;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        int action = event.getAction();
        if (action == 0) {
            Log.i("TAG", "onTouchEvent:Down ");
            getParent().requestDisallowInterceptTouchEvent(true);
            checkIndex(event.getY());
        } else if (action == 1) {
            setBackgroundDrawable(new ColorDrawable(0));
            this.mIndex = -1;
            invalidate();
            TextView textView = this.mTextDialog;
            if (textView != null) {
                textView.setVisibility(4);
            }
        } else if (action == 2) {
            checkIndex(event.getY());
        }
        return true;
    }

    private void checkIndex(float y) {
        int currentIndex;
        OnTouchingLetterChangedListener onTouchingLetterChangedListener;
        String[] strArr;
        if (y >= this.beginY + ((float) getPaddingTop()) && (currentIndex = (int) (((y - this.beginY) - ((float) getPaddingTop())) / this.mCellHeight)) != this.mIndex && (onTouchingLetterChangedListener = this.listener) != null && (strArr = this.chars) != null && currentIndex < strArr.length) {
            this.mIndex = currentIndex;
            onTouchingLetterChangedListener.onTouchingLetterChanged(strArr[currentIndex]);
            TextView textView = this.mTextDialog;
            if (textView != null) {
                textView.setText(this.chars[currentIndex]);
                this.mTextDialog.setVisibility(0);
            }
            invalidate();
        }
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.listener = onTouchingLetterChangedListener;
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void setChars(String[] chars2) {
        this.chars = chars2;
        this.mHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        this.mCellWidth = getMeasuredWidth();
        int i = this.mHeight;
        float f = (((float) i) * 1.0f) / 26.0f;
        this.mCellHeight = f;
        this.beginY = (((float) i) - (f * ((float) chars2.length))) * 0.5f;
        invalidate();
    }
}

package im.bclpbkiauv.ui.hviews.sidebar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class SideBar extends View {
    public static String[] a = {ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, "B", "C", "D", ExifInterface.LONGITUDE_EAST, "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", ExifInterface.LATITUDE_SOUTH, ExifInterface.GPS_DIRECTION_TRUE, "U", ExifInterface.GPS_MEASUREMENT_INTERRUPTED, ExifInterface.LONGITUDE_WEST, "X", "Y", "Z", "#"};
    public static String[] b = {"↑", "☆", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS, "B", "C", "D", ExifInterface.LONGITUDE_EAST, "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", ExifInterface.LATITUDE_SOUTH, ExifInterface.GPS_DIRECTION_TRUE, "U", ExifInterface.GPS_MEASUREMENT_INTERRUPTED, ExifInterface.LONGITUDE_WEST, "X", "Y", "Z", "#"};
    private Paint bitmapPaint;
    public String[] chars;
    private int choose;
    private int height;
    private Context mContext;
    private TextView mTextDialog;
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private boolean onlyChar;
    private Paint textPaint;

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String str);
    }

    public void setTextView(TextView mTextDialog2) {
        this.mTextDialog = mTextDialog2;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.choose = -1;
        this.textPaint = new Paint();
        this.bitmapPaint = new Paint();
        this.onlyChar = true;
        this.mContext = context;
        this.chars = a;
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public void setOnlyChar(boolean flag) {
        this.onlyChar = flag;
        if (flag) {
            this.chars = a;
        } else {
            this.chars = b;
        }
        requestLayout();
        invalidate();
    }

    public void setCharsOnly() {
        this.chars = a;
    }

    public void setTextColor(int color) {
        this.textPaint.setColor(color);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(AndroidUtilities.dp(35.0f), Math.min(this.chars.length * AndroidUtilities.dp(15.0f), AndroidUtilities.dp(500.0f)));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height2 = getHeight();
        this.height = height2;
        int singleHeight = height2 / this.chars.length;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_slide_bar_char_bg);
        for (int i = 0; i < this.chars.length; i++) {
            this.textPaint.setColor(Theme.getColor(Theme.key_sidebar_textDefaultColor));
            this.textPaint.setTypeface(Typeface.DEFAULT);
            this.textPaint.setAntiAlias(true);
            this.textPaint.setTextSize((float) dip2px(this.mContext, 9.5f));
            this.textPaint.setTextAlign(Paint.Align.CENTER);
            if (i == this.choose) {
                this.textPaint.setColor(-1);
                this.textPaint.setFakeBoldText(true);
                this.bitmapPaint.setAlpha(225);
            } else {
                this.bitmapPaint.setAlpha(0);
            }
            canvas.drawBitmap(bitmap, (float) (((width / 2) - (bitmap.getWidth() / 2)) - AndroidUtilities.dp(1.0f)), (float) (((singleHeight - bitmap.getHeight()) / 2) + (singleHeight * i)), this.bitmapPaint);
            Paint.FontMetrics fontMetrics = this.textPaint.getFontMetrics();
            canvas.drawText(this.chars[i], (float) (width / 2), ((float) (singleHeight / 2)) + (((fontMetrics.bottom - fontMetrics.top) / 2.0f) - fontMetrics.bottom) + ((float) (singleHeight * i)), this.textPaint);
            this.textPaint.reset();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0055, code lost:
        if (r5[r4].equals("↑") != false) goto L_0x0057;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0061  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchTouchEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            int r0 = r9.getAction()
            float r1 = r9.getY()
            int r2 = r8.choose
            im.bclpbkiauv.ui.hviews.sidebar.SideBar$OnTouchingLetterChangedListener r3 = r8.onTouchingLetterChangedListener
            int r4 = r8.height
            float r4 = (float) r4
            float r4 = r1 / r4
            java.lang.String[] r5 = r8.chars
            int r6 = r5.length
            float r6 = (float) r6
            float r4 = r4 * r6
            int r4 = (int) r4
            r6 = 1
            if (r0 == r6) goto L_0x0040
            if (r2 == r4) goto L_0x0065
            if (r4 < 0) goto L_0x0065
            int r7 = r5.length
            if (r4 >= r7) goto L_0x0065
            if (r3 == 0) goto L_0x0029
            r5 = r5[r4]
            r3.onTouchingLetterChanged(r5)
        L_0x0029:
            android.widget.TextView r5 = r8.mTextDialog
            if (r5 == 0) goto L_0x003a
            java.lang.String[] r7 = r8.chars
            r7 = r7[r4]
            r5.setText(r7)
            android.widget.TextView r5 = r8.mTextDialog
            r7 = 0
            r5.setVisibility(r7)
        L_0x003a:
            r8.choose = r4
            r8.invalidate()
            goto L_0x0065
        L_0x0040:
            if (r4 >= 0) goto L_0x0046
            boolean r5 = r8.onlyChar
            if (r5 == 0) goto L_0x0057
        L_0x0046:
            if (r4 <= 0) goto L_0x005a
            java.lang.String[] r5 = r8.chars
            int r7 = r5.length
            if (r4 >= r7) goto L_0x005a
            r5 = r5[r4]
            java.lang.String r7 = "↑"
            boolean r5 = r5.equals(r7)
            if (r5 == 0) goto L_0x005a
        L_0x0057:
            r5 = -1
            r8.choose = r5
        L_0x005a:
            r8.invalidate()
            android.widget.TextView r5 = r8.mTextDialog
            if (r5 == 0) goto L_0x0065
            r7 = 4
            r5.setVisibility(r7)
        L_0x0065:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hviews.sidebar.SideBar.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener2) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener2;
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void setChars(String[] chars2) {
        if (chars2.length != 0) {
            if (this.onlyChar) {
                this.chars = chars2;
                this.choose = 0;
            } else {
                String[] newChars = new String[(chars2.length + 2)];
                newChars[0] = "↑";
                newChars[1] = "☆";
                for (int i = 0; i < chars2.length; i++) {
                    newChars[i + 2] = chars2[i];
                }
                this.chars = newChars;
                this.choose = 2;
            }
            requestLayout();
            invalidate();
        }
    }

    public void setChooseChar(String c) {
        int i = 0;
        while (true) {
            String[] strArr = this.chars;
            if (i < strArr.length) {
                if (strArr[i].equals(c)) {
                    this.choose = i;
                    invalidate();
                }
                i++;
            } else {
                return;
            }
        }
    }
}

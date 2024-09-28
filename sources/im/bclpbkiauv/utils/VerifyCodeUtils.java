package im.bclpbkiauv.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;

public class VerifyCodeUtils {
    private static final int BASE_PADDING_LEFT = 20;
    private static final int BASE_PADDING_TOP = 70;
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] CHARS2 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final int DEFAULT_CODE_LENGTH = 5;
    private static final int DEFAULT_COLOR = Color.rgb(238, 238, 238);
    private static final int DEFAULT_FONT_SIZE = 60;
    private static final int DEFAULT_HEIGHT = 100;
    private static final int DEFAULT_LINE_NUMBER = 12;
    private static final int DEFAULT_WIDTH = 250;
    private static final int RANGE_PADDING_LEFT = 30;
    private static final int RANGE_PADDING_TOP = 15;
    private static VerifyCodeUtils mCodeUtils;
    private String code;
    private StringBuilder mBuilder = new StringBuilder();
    private int mPaddingLeft;
    private int mPaddingTop;
    private Random mRandom = new Random();

    public static VerifyCodeUtils getInstance() {
        if (mCodeUtils == null) {
            mCodeUtils = new VerifyCodeUtils();
        }
        return mCodeUtils;
    }

    public Bitmap createBitmap() {
        this.mPaddingLeft = 0;
        this.mPaddingTop = 0;
        Bitmap bitmap = Bitmap.createBitmap(250, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.code = createCode();
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawColor(DEFAULT_COLOR);
        Paint paint = new Paint();
        paint.setTextSize(60.0f);
        for (int i = 0; i < this.code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            canvas.drawText(this.code.charAt(i) + "", (float) this.mPaddingLeft, (float) this.mPaddingTop, paint);
        }
        for (int i2 = 0; i2 < 12; i2++) {
            drawLine(canvas, paint);
        }
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    public String getCode() {
        return this.code;
    }

    public String createCode() {
        StringBuilder sb = this.mBuilder;
        sb.delete(0, sb.length());
        for (int i = 0; i < 5; i++) {
            StringBuilder sb2 = this.mBuilder;
            char[] cArr = CHARS2;
            sb2.append(cArr[this.mRandom.nextInt(cArr.length)]);
        }
        return this.mBuilder.toString();
    }

    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = this.mRandom.nextInt(250);
        int startY = this.mRandom.nextInt(100);
        int stopX = this.mRandom.nextInt(250);
        int stopY = this.mRandom.nextInt(100);
        paint.setStrokeWidth(1.0f);
        paint.setColor(color);
        canvas.drawLine((float) startX, (float) startY, (float) stopX, (float) stopY, paint);
    }

    private int randomColor() {
        StringBuilder sb = this.mBuilder;
        sb.delete(0, sb.length());
        for (int i = 0; i < 3; i++) {
            String haxString = Integer.toHexString(this.mRandom.nextInt(238));
            if (haxString.length() == 1) {
                haxString = "0" + haxString;
            }
            this.mBuilder.append(haxString);
        }
        return Color.parseColor("#" + this.mBuilder.toString());
    }

    private void randomTextStyle(Paint paint) {
        paint.setColor(randomColor());
        paint.setFakeBoldText(this.mRandom.nextBoolean());
        float skewX = (float) (this.mRandom.nextInt(11) / 10);
        paint.setTextSkewX(this.mRandom.nextBoolean() ? skewX : -skewX);
        paint.setUnderlineText(this.mRandom.nextBoolean());
        paint.setStrikeThruText(this.mRandom.nextBoolean());
    }

    private void randomPadding() {
        this.mPaddingLeft += this.mRandom.nextInt(30) + 20;
        this.mPaddingTop = this.mRandom.nextInt(15) + 70;
    }
}

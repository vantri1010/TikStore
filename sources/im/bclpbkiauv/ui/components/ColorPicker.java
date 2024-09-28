package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import java.util.List;

public class ColorPicker extends FrameLayout {
    private int centerX;
    private int centerY;
    private Drawable circleDrawable;
    private Paint circlePaint;
    private boolean circlePressed;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] colorEditText = new EditTextBoldCursor[2];
    private LinearGradient colorGradient;
    /* access modifiers changed from: private */
    public float[] colorHSV = {0.0f, 0.0f, 1.0f};
    private boolean colorPressed;
    private Bitmap colorWheelBitmap;
    private Paint colorWheelPaint;
    private int colorWheelRadius;
    private final ColorPickerDelegate delegate;
    private float[] hsvTemp = new float[3];
    boolean ignoreTextChange;
    private LinearLayout linearLayout;
    private int lx;
    private int ly;
    private BrightnessLimit maxBrightness;
    private BrightnessLimit minBrightness;
    private final int paramValueSliderWidth = AndroidUtilities.dp(20.0f);
    private Paint valueSliderPaint;

    public interface BrightnessLimit {
        float getLimit(int i, int i2, int i3);
    }

    public interface ColorPickerDelegate {
        void setColor(int i);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ColorPicker(Context context, ColorPickerDelegate delegate2) {
        super(context);
        Context context2 = context;
        final ColorPickerDelegate colorPickerDelegate = delegate2;
        this.delegate = colorPickerDelegate;
        setWillNotDraw(false);
        this.circlePaint = new Paint(1);
        this.circleDrawable = context.getResources().getDrawable(R.drawable.knob_shadow).mutate();
        Paint paint = new Paint();
        this.colorWheelPaint = paint;
        paint.setAntiAlias(true);
        this.colorWheelPaint.setDither(true);
        Paint paint2 = new Paint();
        this.valueSliderPaint = paint2;
        paint2.setAntiAlias(true);
        this.valueSliderPaint.setDither(true);
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.linearLayout = linearLayout2;
        linearLayout2.setOrientation(0);
        addView(this.linearLayout, LayoutHelper.createFrame(-1.0f, 46.0f, 51, 12.0f, 20.0f, 21.0f, 14.0f));
        int a = 0;
        while (a < 2) {
            final int num = a;
            this.colorEditText[a] = new EditTextBoldCursor(context2);
            this.colorEditText[a].setTextSize(1, 18.0f);
            this.colorEditText[a].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.colorEditText[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.colorEditText[a].setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.colorEditText[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.colorEditText[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.colorEditText[a].setCursorWidth(1.5f);
            this.colorEditText[a].setSingleLine(true);
            this.colorEditText[a].setGravity(19);
            this.colorEditText[a].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.colorEditText[a].setTransformHintToHeader(true);
            if (a == 0) {
                this.colorEditText[a].setInputType(1);
                this.colorEditText[a].setHintText(LocaleController.getString("BackgroundHexColorCode", R.string.BackgroundHexColorCode));
            } else {
                this.colorEditText[a].setInputType(2);
                this.colorEditText[a].setHintText(LocaleController.getString("BackgroundBrightness", R.string.BackgroundBrightness));
            }
            this.colorEditText[a].setImeOptions(268435462);
            InputFilter[] inputFilters = new InputFilter[1];
            inputFilters[0] = new InputFilter.LengthFilter(a == 0 ? 7 : 3);
            this.colorEditText[a].setFilters(inputFilters);
            this.colorEditText[a].setPadding(0, AndroidUtilities.dp(6.0f), 0, 0);
            this.linearLayout.addView(this.colorEditText[a], LayoutHelper.createLinear(0, -1, a == 0 ? 0.67f : 0.31f, 0, 0, a != 1 ? 23 : 0, 0));
            this.colorEditText[a].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!ColorPicker.this.ignoreTextChange) {
                        ColorPicker.this.ignoreTextChange = true;
                        if (num == 0) {
                            int a = 0;
                            while (a < editable.length()) {
                                char ch = editable.charAt(a);
                                if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'f') && ((ch < 'A' || ch > 'F') && !(ch == '#' && a == 0)))) {
                                    editable.replace(a, a + 1, "");
                                    a--;
                                }
                                a++;
                            }
                            if (editable.length() == 0) {
                                editable.append("#");
                            } else if (editable.charAt(0) != '#') {
                                editable.insert(0, "#");
                            }
                            if (editable.length() != 7) {
                                ColorPicker.this.ignoreTextChange = false;
                                return;
                            } else {
                                try {
                                    ColorPicker.this.setColor(Integer.parseInt(editable.toString().substring(1), 16) | -16777216);
                                } catch (Exception e) {
                                    ColorPicker.this.setColor(-1);
                                }
                            }
                        } else {
                            int value = Utilities.parseInt(editable.toString()).intValue();
                            if (value > 255 || value < 0) {
                                if (value > 255) {
                                    value = 255;
                                } else {
                                    value = 0;
                                }
                                editable.replace(0, editable.length(), "" + value);
                            }
                            ColorPicker.this.colorHSV[2] = ((float) value) / 255.0f;
                        }
                        int color = ColorPicker.this.getColor();
                        ColorPicker.this.colorEditText[0].setTextKeepState(String.format("#%02x%02x%02x", new Object[]{Byte.valueOf((byte) Color.red(color)), Byte.valueOf((byte) Color.green(color)), Byte.valueOf((byte) Color.blue(color))}).toUpperCase());
                        ColorPicker.this.colorEditText[1].setTextKeepState(String.valueOf((int) (ColorPicker.this.getBrightness() * 255.0f)));
                        colorPickerDelegate.setColor(color);
                        ColorPicker.this.ignoreTextChange = false;
                    }
                }
            });
            this.colorEditText[a].setOnEditorActionListener($$Lambda$ColorPicker$i4HRy3Wb_DFXKfXrjNv0nuF4aAY.INSTANCE);
            a++;
        }
    }

    static /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int size = Math.min(widthSize, View.MeasureSpec.getSize(heightMeasureSpec));
        measureChild(this.linearLayout, View.MeasureSpec.makeMeasureSpec(widthSize - AndroidUtilities.dp(42.0f), 1073741824), heightMeasureSpec);
        setMeasuredDimension(size, size);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        this.centerX = ((getWidth() / 2) - (this.paramValueSliderWidth * 2)) + AndroidUtilities.dp(11.0f);
        int height = (getHeight() / 2) + AndroidUtilities.dp(34.0f);
        this.centerY = height;
        Bitmap bitmap = this.colorWheelBitmap;
        int i = this.centerX;
        int i2 = this.colorWheelRadius;
        canvas2.drawBitmap(bitmap, (float) (i - i2), (float) (height - i2), (Paint) null);
        float hueAngle = (float) Math.toRadians((double) this.colorHSV[0]);
        int colorPointX = ((int) ((-Math.cos((double) hueAngle)) * ((double) this.colorHSV[1]) * ((double) this.colorWheelRadius))) + this.centerX;
        float[] fArr = this.colorHSV;
        int colorPointY = ((int) ((-Math.sin((double) hueAngle)) * ((double) fArr[1]) * ((double) this.colorWheelRadius))) + this.centerY;
        float[] fArr2 = this.hsvTemp;
        fArr2[0] = fArr[0];
        fArr2[1] = fArr[1];
        fArr2[2] = 1.0f;
        drawPointerArrow(canvas2, colorPointX, colorPointY, Color.HSVToColor(fArr2));
        int i3 = this.centerX;
        int i4 = this.colorWheelRadius;
        this.lx = i3 + i4 + (this.paramValueSliderWidth * 2);
        this.ly = this.centerY - i4;
        int width = AndroidUtilities.dp(9.0f);
        int height2 = this.colorWheelRadius * 2;
        if (this.colorGradient == null) {
            int i5 = this.lx;
            int i6 = this.ly;
            this.colorGradient = new LinearGradient((float) i5, (float) i6, (float) (i5 + width), (float) (i6 + height2), new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, (float[]) null, Shader.TileMode.CLAMP);
        }
        this.valueSliderPaint.setShader(this.colorGradient);
        int i7 = this.lx;
        int i8 = this.ly;
        canvas.drawRect((float) i7, (float) i8, (float) (i7 + width), (float) (i8 + height2), this.valueSliderPaint);
        drawPointerArrow(canvas2, this.lx + (width / 2), (int) (((float) this.ly) + (getBrightness() * ((float) height2))), getColor());
    }

    private void drawPointerArrow(Canvas canvas, int x, int y, int color) {
        int side = AndroidUtilities.dp(13.0f);
        this.circleDrawable.setBounds(x - side, y - side, x + side, y + side);
        this.circleDrawable.draw(canvas);
        this.circlePaint.setColor(-1);
        canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(11.0f), this.circlePaint);
        this.circlePaint.setColor(color);
        canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(9.0f), this.circlePaint);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int width, int height, int oldw, int oldh) {
        if (this.colorWheelRadius != AndroidUtilities.dp(120.0f)) {
            int dp = AndroidUtilities.dp(120.0f);
            this.colorWheelRadius = dp;
            this.colorWheelBitmap = createColorWheelBitmap(dp * 2, dp * 2);
            this.colorGradient = null;
        }
    }

    private Bitmap createColorWheelBitmap(int width, int height) {
        int i = width;
        int i2 = height;
        Bitmap bitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        int[] colors = new int[(12 + 1)];
        float[] hsv = {0.0f, 1.0f, 1.0f};
        for (int i3 = 0; i3 < colors.length; i3++) {
            hsv[0] = (float) (((i3 * 30) + 180) % 360);
            colors[i3] = Color.HSVToColor(hsv);
        }
        colors[12] = colors[0];
        this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient(((float) i) * 0.5f, ((float) i2) * 0.5f, colors, (float[]) null), new RadialGradient(((float) i) * 0.5f, ((float) i2) * 0.5f, (float) this.colorWheelRadius, -1, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER));
        new Canvas(bitmap).drawCircle(((float) i) * 0.5f, ((float) i2) * 0.5f, (float) this.colorWheelRadius, this.colorWheelPaint);
        return bitmap;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00a9, code lost:
        if (r6 <= (r4 + (r0.colorWheelRadius * 2))) goto L_0x00b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000d, code lost:
        if (r1 != 2) goto L_0x0014;
     */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:60:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r19) {
        /*
            r18 = this;
            r0 = r18
            int r1 = r19.getAction()
            r2 = 2
            r3 = 0
            r4 = 1
            if (r1 == 0) goto L_0x0019
            if (r1 == r4) goto L_0x0010
            if (r1 == r2) goto L_0x0019
            goto L_0x0014
        L_0x0010:
            r0.colorPressed = r3
            r0.circlePressed = r3
        L_0x0014:
            boolean r2 = super.onTouchEvent(r19)
            return r2
        L_0x0019:
            float r5 = r19.getX()
            int r5 = (int) r5
            float r6 = r19.getY()
            int r6 = (int) r6
            int r7 = r0.centerX
            int r7 = r5 - r7
            int r8 = r0.centerY
            int r8 = r6 - r8
            int r9 = r7 * r7
            int r10 = r8 * r8
            int r9 = r9 + r10
            double r9 = (double) r9
            double r9 = java.lang.Math.sqrt(r9)
            boolean r11 = r0.circlePressed
            r12 = 1065353216(0x3f800000, float:1.0)
            r13 = 0
            if (r11 != 0) goto L_0x004b
            boolean r11 = r0.colorPressed
            if (r11 != 0) goto L_0x0048
            int r11 = r0.colorWheelRadius
            double r14 = (double) r11
            int r11 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
            if (r11 > 0) goto L_0x0048
            goto L_0x004b
        L_0x0048:
            r17 = r5
            goto L_0x008d
        L_0x004b:
            int r11 = r0.colorWheelRadius
            double r14 = (double) r11
            int r16 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
            if (r16 <= 0) goto L_0x0053
            double r9 = (double) r11
        L_0x0053:
            boolean r11 = r0.circlePressed
            if (r11 != 0) goto L_0x005e
            android.view.ViewParent r11 = r18.getParent()
            r11.requestDisallowInterceptTouchEvent(r4)
        L_0x005e:
            r0.circlePressed = r4
            float[] r11 = r0.colorHSV
            double r14 = (double) r8
            r17 = r5
            double r4 = (double) r7
            double r4 = java.lang.Math.atan2(r14, r4)
            double r4 = java.lang.Math.toDegrees(r4)
            r14 = 4640537203540230144(0x4066800000000000, double:180.0)
            double r4 = r4 + r14
            float r4 = (float) r4
            r11[r3] = r4
            float[] r4 = r0.colorHSV
            int r5 = r0.colorWheelRadius
            double r14 = (double) r5
            double r14 = r9 / r14
            float r5 = (float) r14
            float r5 = java.lang.Math.min(r12, r5)
            float r5 = java.lang.Math.max(r13, r5)
            r11 = 1
            r4[r11] = r5
            r4 = 0
            r0.colorGradient = r4
        L_0x008d:
            boolean r4 = r0.colorPressed
            if (r4 != 0) goto L_0x00af
            boolean r4 = r0.circlePressed
            if (r4 != 0) goto L_0x00ac
            int r4 = r0.lx
            r5 = r17
            if (r5 < r4) goto L_0x00de
            int r11 = r0.paramValueSliderWidth
            int r4 = r4 + r11
            if (r5 > r4) goto L_0x00de
            int r4 = r0.ly
            if (r6 < r4) goto L_0x00de
            int r11 = r0.colorWheelRadius
            int r11 = r11 * 2
            int r4 = r4 + r11
            if (r6 > r4) goto L_0x00de
            goto L_0x00b1
        L_0x00ac:
            r5 = r17
            goto L_0x00de
        L_0x00af:
            r5 = r17
        L_0x00b1:
            int r4 = r0.ly
            int r4 = r6 - r4
            float r4 = (float) r4
            int r11 = r0.colorWheelRadius
            float r11 = (float) r11
            r14 = 1073741824(0x40000000, float:2.0)
            float r11 = r11 * r14
            float r4 = r4 / r11
            int r11 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r11 >= 0) goto L_0x00c4
            r4 = 0
            goto L_0x00ca
        L_0x00c4:
            int r11 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r11 <= 0) goto L_0x00ca
            r4 = 1065353216(0x3f800000, float:1.0)
        L_0x00ca:
            float[] r11 = r0.colorHSV
            r11[r2] = r4
            boolean r11 = r0.colorPressed
            if (r11 != 0) goto L_0x00db
            android.view.ViewParent r11 = r18.getParent()
            r12 = 1
            r11.requestDisallowInterceptTouchEvent(r12)
            goto L_0x00dc
        L_0x00db:
            r12 = 1
        L_0x00dc:
            r0.colorPressed = r12
        L_0x00de:
            boolean r4 = r0.colorPressed
            if (r4 != 0) goto L_0x00e6
            boolean r4 = r0.circlePressed
            if (r4 == 0) goto L_0x015c
        L_0x00e6:
            int r4 = r18.getColor()
            boolean r11 = r0.ignoreTextChange
            if (r11 != 0) goto L_0x0154
            int r11 = android.graphics.Color.red(r4)
            int r12 = android.graphics.Color.green(r4)
            int r13 = android.graphics.Color.blue(r4)
            r14 = 1
            r0.ignoreTextChange = r14
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r15 = r0.colorEditText
            r15 = r15[r3]
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            byte r14 = (byte) r11
            java.lang.Byte r14 = java.lang.Byte.valueOf(r14)
            r2[r3] = r14
            byte r14 = (byte) r12
            java.lang.Byte r14 = java.lang.Byte.valueOf(r14)
            r16 = 1
            r2[r16] = r14
            byte r14 = (byte) r13
            java.lang.Byte r14 = java.lang.Byte.valueOf(r14)
            r17 = 2
            r2[r17] = r14
            java.lang.String r14 = "#%02x%02x%02x"
            java.lang.String r2 = java.lang.String.format(r14, r2)
            java.lang.String r2 = r2.toUpperCase()
            r15.setText(r2)
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r2 = r0.colorEditText
            r2 = r2[r16]
            r14 = 1132396544(0x437f0000, float:255.0)
            float r15 = r18.getBrightness()
            float r15 = r15 * r14
            int r14 = (int) r15
            java.lang.String r14 = java.lang.String.valueOf(r14)
            r2.setText(r14)
            r2 = 0
        L_0x013f:
            r14 = 2
            if (r2 >= r14) goto L_0x0152
            im.bclpbkiauv.ui.components.EditTextBoldCursor[] r15 = r0.colorEditText
            r14 = r15[r2]
            r15 = r15[r2]
            int r15 = r15.length()
            r14.setSelection(r15)
            int r2 = r2 + 1
            goto L_0x013f
        L_0x0152:
            r0.ignoreTextChange = r3
        L_0x0154:
            im.bclpbkiauv.ui.components.ColorPicker$ColorPickerDelegate r2 = r0.delegate
            r2.setColor(r4)
            r18.invalidate()
        L_0x015c:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setColor(int color) {
        if (!this.ignoreTextChange) {
            this.ignoreTextChange = true;
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            Color.colorToHSV(color, this.colorHSV);
            this.colorEditText[0].setText(String.format("#%02x%02x%02x", new Object[]{Byte.valueOf((byte) red), Byte.valueOf((byte) green), Byte.valueOf((byte) blue)}).toUpperCase());
            this.colorEditText[1].setText(String.valueOf((int) (getBrightness() * 255.0f)));
            for (int b = 0; b < 2; b++) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
                editTextBoldCursorArr[b].setSelection(editTextBoldCursorArr[b].length());
            }
            this.ignoreTextChange = false;
        } else {
            Color.colorToHSV(color, this.colorHSV);
        }
        this.colorGradient = null;
        invalidate();
    }

    public int getColor() {
        float[] fArr = this.hsvTemp;
        float[] fArr2 = this.colorHSV;
        fArr[0] = fArr2[0];
        fArr[1] = fArr2[1];
        fArr[2] = getBrightness();
        return (Color.HSVToColor(this.hsvTemp) & ViewCompat.MEASURED_SIZE_MASK) | -16777216;
    }

    /* access modifiers changed from: private */
    public float getBrightness() {
        float[] fArr = this.colorHSV;
        float brightness = fArr[2];
        float max = 1.0f;
        fArr[2] = 1.0f;
        int color = Color.HSVToColor(fArr);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        this.colorHSV[2] = brightness;
        BrightnessLimit brightnessLimit = this.minBrightness;
        float min = brightnessLimit == null ? 0.0f : brightnessLimit.getLimit(red, green, blue);
        BrightnessLimit brightnessLimit2 = this.maxBrightness;
        if (brightnessLimit2 != null) {
            max = brightnessLimit2.getLimit(red, green, blue);
        }
        return Math.max(min, Math.min(brightness, max));
    }

    public void setMinBrightness(BrightnessLimit limit) {
        this.minBrightness = limit;
    }

    public void setMaxBrightness(BrightnessLimit limit) {
        this.maxBrightness = limit;
    }

    public void provideThemeDescriptions(List<ThemeDescription> arrayList) {
        List<ThemeDescription> list = arrayList;
        int a = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
            if (a < editTextBoldCursorArr.length) {
                list.add(new ThemeDescription(editTextBoldCursorArr[a], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
                list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
                list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
                list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField));
                list.add(new ThemeDescription(this.colorEditText[a], ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated));
                a++;
            } else {
                return;
            }
        }
    }
}

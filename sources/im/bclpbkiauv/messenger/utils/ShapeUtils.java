package im.bclpbkiauv.messenger.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class ShapeUtils {
    public static ShapeDrawable create(int bgColor, float cornerRadius) {
        return new ShapeDrawable.Builder().gradientType(0).solidColor(bgColor).cornerRadius(cornerRadius).build();
    }

    public static GradientDrawable createGradient(float cornerRadius, int[] colors, GradientDrawable.Orientation orientation) {
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, colors);
        gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(cornerRadius));
        return gradientDrawable;
    }

    public static ShapeDrawable createTop(int bgColor, float cornerRadiusLeftTop, float cornerRadiusRightTop) {
        return new ShapeDrawable.Builder().gradientType(0).solidColor(bgColor).cornerRadiusLeftTop(cornerRadiusLeftTop).cornerRadiusRightTop(cornerRadiusRightTop).build();
    }

    public static ShapeDrawable createLeftAndBottom(int bgColor, float cornerRadiusLeftTop, float cornerRadiusLeftBottom) {
        return new ShapeDrawable.Builder().gradientType(0).solidColor(bgColor).cornerRadiusLeftTop(cornerRadiusLeftTop).cornerRadiusLeftBottom(cornerRadiusLeftBottom).build();
    }

    public static ShapeDrawable createBottom(int bgColor, float cornerRadiusLeftBottom, float cornerRadiusRightBottom) {
        return new ShapeDrawable.Builder().gradientType(0).solidColor(bgColor).cornerRadiusLeftBottom(cornerRadiusLeftBottom).cornerRadiusRightBottom(cornerRadiusRightBottom).build();
    }

    public static ShapeDrawable createOnlyStroke(int strokeColor, float strokeWidth, float cornerRadius) {
        return new ShapeDrawable.Builder().gradientType(0).strokeColor(strokeColor).strokeWidth(strokeWidth).cornerRadius(cornerRadius).build();
    }

    public static ShapeDrawable createStrokeAndFill(int strokeColor, float strokeWidth, float cornerRadius, int solidColor) {
        return new ShapeDrawable.Builder().gradientType(0).strokeColor(strokeColor).strokeWidth(strokeWidth).cornerRadius(cornerRadius).solidColor(solidColor).build();
    }

    public static ShapeDrawable createOnlyFill(float cornerRadius, int solidColor) {
        return new ShapeDrawable.Builder().gradientType(0).cornerRadius(cornerRadius).solidColor(solidColor).build();
    }

    public static class ShapeDrawable extends GradientDrawable {
        public static final int LINE = 2;
        public static final int OVAL = 1;
        public static final int RECTANGLE = 0;
        private float cornerRadius;
        private float cornerRadiusLeftBottom;
        private float cornerRadiusLeftTop;
        private float cornerRadiusRightBottom;
        private float cornerRadiusRightTop;
        private float dashGap;
        private float dashWidth;
        private int gradientType;
        private int solidColor;
        private int strokeColor;
        private float strokeWidth;

        public ShapeDrawable(int gradientType2, int solidColor2, int strokeColor2, float cornerRadius2, float strokeWidth2, float dashGap2, float dashWidth2) {
            this.gradientType = gradientType2;
            this.solidColor = solidColor2;
            this.strokeColor = strokeColor2;
            this.cornerRadius = cornerRadius2;
            this.strokeWidth = strokeWidth2;
            this.dashGap = dashGap2;
            this.dashWidth = dashWidth2;
        }

        private ShapeDrawable(Builder builder) {
            this.gradientType = builder.gradientType;
            this.solidColor = builder.solidColor;
            this.strokeColor = builder.strokeColor;
            this.cornerRadius = builder.cornerRadius;
            this.strokeWidth = builder.strokeWidth;
            this.dashGap = builder.dashGap;
            this.dashWidth = builder.dashWidth;
            this.cornerRadiusLeftTop = builder.cornerRadiusLeftTop;
            this.cornerRadiusLeftBottom = builder.cornerRadiusLeftBottom;
            this.cornerRadiusRightTop = builder.cornerRadiusRightTop;
            this.cornerRadiusRightBottom = builder.cornerRadiusRightBottom;
            setGradientType(this.gradientType);
            int i = this.solidColor;
            this.solidColor = i == 0 ? Color.parseColor("#00000000") : i;
            int i2 = this.strokeColor;
            this.strokeColor = i2 == 0 ? Color.parseColor("#00000000") : i2;
            setColor(this.solidColor);
            float f = this.strokeWidth;
            if (f != 0.0f) {
                float f2 = this.dashGap;
                if (f2 != 0.0f) {
                    float f3 = this.dashWidth;
                    if (f3 != 0.0f) {
                        setStroke((int) f, this.strokeColor, f2, f3);
                    }
                }
                setStroke((int) this.strokeWidth, this.strokeColor);
            }
            float f4 = this.cornerRadius;
            if (f4 > 0.0f) {
                setCornerRadius(f4);
                return;
            }
            float f5 = this.cornerRadiusLeftTop;
            float f6 = this.cornerRadiusRightTop;
            float f7 = this.cornerRadiusRightBottom;
            float f8 = this.cornerRadiusLeftBottom;
            setCornerRadii(new float[]{f5, f5, f6, f6, f7, f7, f8, f8});
        }

        public static final class Builder {
            /* access modifiers changed from: private */
            public float cornerRadius;
            /* access modifiers changed from: private */
            public float cornerRadiusLeftBottom;
            /* access modifiers changed from: private */
            public float cornerRadiusLeftTop;
            /* access modifiers changed from: private */
            public float cornerRadiusRightBottom;
            /* access modifiers changed from: private */
            public float cornerRadiusRightTop;
            /* access modifiers changed from: private */
            public float dashGap;
            /* access modifiers changed from: private */
            public float dashWidth;
            /* access modifiers changed from: private */
            public int gradientType;
            /* access modifiers changed from: private */
            public int solidColor;
            /* access modifiers changed from: private */
            public int strokeColor;
            /* access modifiers changed from: private */
            public float strokeWidth;

            public Builder cornerRadiusLeftTop(float val) {
                this.cornerRadiusLeftTop = val;
                return this;
            }

            public Builder cornerRadiusLeftBottom(float val) {
                this.cornerRadiusLeftBottom = val;
                return this;
            }

            public Builder cornerRadiusRightTop(float val) {
                this.cornerRadiusRightTop = val;
                return this;
            }

            public Builder cornerRadiusRightBottom(float val) {
                this.cornerRadiusRightBottom = val;
                return this;
            }

            public Builder gradientType(int val) {
                this.gradientType = val;
                return this;
            }

            public Builder solidColor(int val) {
                this.solidColor = val;
                return this;
            }

            public Builder solidColor(String val) {
                this.solidColor = Color.parseColor(val);
                return this;
            }

            public Builder strokeColor(int val) {
                this.strokeColor = val;
                return this;
            }

            public Builder strokeColor(String val) {
                this.strokeColor = Color.parseColor(val);
                return this;
            }

            public Builder cornerRadius(float val) {
                this.cornerRadius = val;
                return this;
            }

            public Builder strokeWidth(float val) {
                this.strokeWidth = val;
                return this;
            }

            public Builder dashGap(float val) {
                this.dashGap = val;
                return this;
            }

            public Builder dashWidth(float val) {
                this.dashWidth = val;
                return this;
            }

            public ShapeDrawable build() {
                return new ShapeDrawable(this);
            }
        }
    }
}

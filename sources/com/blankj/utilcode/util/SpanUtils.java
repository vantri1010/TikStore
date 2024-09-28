package com.blankj.utilcode.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public final class SpanUtils {
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_CENTER = 2;
    public static final int ALIGN_TOP = 3;
    private static final int COLOR_DEFAULT = -16777217;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private int alignImage;
    private int alignLine;
    private Layout.Alignment alignment;
    private int backgroundColor;
    private float blurRadius;
    private int bulletColor;
    private int bulletGapWidth;
    private int bulletRadius;
    private ClickableSpan clickSpan;
    private int first;
    private int flag;
    private String fontFamily;
    private int fontSize;
    private boolean fontSizeIsDp;
    private int foregroundColor;
    private Bitmap imageBitmap;
    private Drawable imageDrawable;
    private int imageResourceId;
    private Uri imageUri;
    private boolean isBold;
    private boolean isBoldItalic;
    private boolean isItalic;
    private boolean isStrikethrough;
    private boolean isSubscript;
    private boolean isSuperscript;
    private boolean isUnderline;
    private int lineHeight;
    private SerializableSpannableStringBuilder mBuilder;
    private CharSequence mText;
    private TextView mTextView;
    private int mType;
    private final int mTypeCharSequence;
    private final int mTypeImage;
    private final int mTypeSpace;
    private float proportion;
    private int quoteColor;
    private int quoteGapWidth;
    private int rest;
    private Shader shader;
    private int shadowColor;
    private float shadowDx;
    private float shadowDy;
    private float shadowRadius;
    private int spaceColor;
    private int spaceSize;
    private Object[] spans;
    private int stripeWidth;
    private BlurMaskFilter.Blur style;
    private Typeface typeface;
    private String url;
    private int verticalAlign;
    private float xProportion;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Align {
    }

    private SpanUtils(TextView textView) {
        this();
        this.mTextView = textView;
    }

    public SpanUtils() {
        this.mTypeCharSequence = 0;
        this.mTypeImage = 1;
        this.mTypeSpace = 2;
        this.mBuilder = new SerializableSpannableStringBuilder();
        this.mText = "";
        this.mType = -1;
        setDefault();
    }

    private void setDefault() {
        this.flag = 33;
        this.foregroundColor = COLOR_DEFAULT;
        this.backgroundColor = COLOR_DEFAULT;
        this.lineHeight = -1;
        this.quoteColor = COLOR_DEFAULT;
        this.first = -1;
        this.bulletColor = COLOR_DEFAULT;
        this.fontSize = -1;
        this.proportion = -1.0f;
        this.xProportion = -1.0f;
        this.isStrikethrough = false;
        this.isUnderline = false;
        this.isSuperscript = false;
        this.isSubscript = false;
        this.isBold = false;
        this.isItalic = false;
        this.isBoldItalic = false;
        this.fontFamily = null;
        this.typeface = null;
        this.alignment = null;
        this.verticalAlign = -1;
        this.clickSpan = null;
        this.url = null;
        this.blurRadius = -1.0f;
        this.shader = null;
        this.shadowRadius = -1.0f;
        this.spans = null;
        this.imageBitmap = null;
        this.imageDrawable = null;
        this.imageUri = null;
        this.imageResourceId = -1;
        this.spaceSize = -1;
    }

    public SpanUtils setFlag(int flag2) {
        this.flag = flag2;
        return this;
    }

    public SpanUtils setForegroundColor(int color) {
        this.foregroundColor = color;
        return this;
    }

    public SpanUtils setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public SpanUtils setLineHeight(int lineHeight2) {
        return setLineHeight(lineHeight2, 2);
    }

    public SpanUtils setLineHeight(int lineHeight2, int align) {
        this.lineHeight = lineHeight2;
        this.alignLine = align;
        return this;
    }

    public SpanUtils setQuoteColor(int color) {
        return setQuoteColor(color, 2, 2);
    }

    public SpanUtils setQuoteColor(int color, int stripeWidth2, int gapWidth) {
        this.quoteColor = color;
        this.stripeWidth = stripeWidth2;
        this.quoteGapWidth = gapWidth;
        return this;
    }

    public SpanUtils setLeadingMargin(int first2, int rest2) {
        this.first = first2;
        this.rest = rest2;
        return this;
    }

    public SpanUtils setBullet(int gapWidth) {
        return setBullet(0, 3, gapWidth);
    }

    public SpanUtils setBullet(int color, int radius, int gapWidth) {
        this.bulletColor = color;
        this.bulletRadius = radius;
        this.bulletGapWidth = gapWidth;
        return this;
    }

    public SpanUtils setFontSize(int size) {
        return setFontSize(size, false);
    }

    public SpanUtils setFontSize(int size, boolean isSp) {
        this.fontSize = size;
        this.fontSizeIsDp = isSp;
        return this;
    }

    public SpanUtils setFontProportion(float proportion2) {
        this.proportion = proportion2;
        return this;
    }

    public SpanUtils setFontXProportion(float proportion2) {
        this.xProportion = proportion2;
        return this;
    }

    public SpanUtils setStrikethrough() {
        this.isStrikethrough = true;
        return this;
    }

    public SpanUtils setUnderline() {
        this.isUnderline = true;
        return this;
    }

    public SpanUtils setSuperscript() {
        this.isSuperscript = true;
        return this;
    }

    public SpanUtils setSubscript() {
        this.isSubscript = true;
        return this;
    }

    public SpanUtils setBold() {
        this.isBold = true;
        return this;
    }

    public SpanUtils setItalic() {
        this.isItalic = true;
        return this;
    }

    public SpanUtils setBoldItalic() {
        this.isBoldItalic = true;
        return this;
    }

    public SpanUtils setFontFamily(String fontFamily2) {
        if (fontFamily2 != null) {
            this.fontFamily = fontFamily2;
            return this;
        }
        throw new NullPointerException("Argument 'fontFamily' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils setTypeface(Typeface typeface2) {
        if (typeface2 != null) {
            this.typeface = typeface2;
            return this;
        }
        throw new NullPointerException("Argument 'typeface' of type Typeface (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils setHorizontalAlign(Layout.Alignment alignment2) {
        if (alignment2 != null) {
            this.alignment = alignment2;
            return this;
        }
        throw new NullPointerException("Argument 'alignment' of type Alignment (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils setVerticalAlign(int align) {
        this.verticalAlign = align;
        return this;
    }

    public SpanUtils setClickSpan(ClickableSpan clickSpan2) {
        if (clickSpan2 != null) {
            TextView textView = this.mTextView;
            if (textView != null && textView.getMovementMethod() == null) {
                this.mTextView.setMovementMethod(LinkMovementMethod.getInstance());
            }
            this.clickSpan = clickSpan2;
            return this;
        }
        throw new NullPointerException("Argument 'clickSpan' of type ClickableSpan (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils setUrl(String url2) {
        if (url2 != null) {
            TextView textView = this.mTextView;
            if (textView != null && textView.getMovementMethod() == null) {
                this.mTextView.setMovementMethod(LinkMovementMethod.getInstance());
            }
            this.url = url2;
            return this;
        }
        throw new NullPointerException("Argument 'url' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils setBlur(float radius, BlurMaskFilter.Blur style2) {
        this.blurRadius = radius;
        this.style = style2;
        return this;
    }

    public SpanUtils setShader(Shader shader2) {
        if (shader2 != null) {
            this.shader = shader2;
            return this;
        }
        throw new NullPointerException("Argument 'shader' of type Shader (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils setShadow(float radius, float dx, float dy, int shadowColor2) {
        this.shadowRadius = radius;
        this.shadowDx = dx;
        this.shadowDy = dy;
        this.shadowColor = shadowColor2;
        return this;
    }

    public SpanUtils setSpans(Object... spans2) {
        if (spans2 != null) {
            if (spans2.length > 0) {
                this.spans = spans2;
            }
            return this;
        }
        throw new NullPointerException("Argument 'spans' of type Object[] (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils append(CharSequence text) {
        if (text != null) {
            apply(0);
            this.mText = text;
            return this;
        }
        throw new NullPointerException("Argument 'text' of type CharSequence (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils appendLine() {
        apply(0);
        this.mText = LINE_SEPARATOR;
        return this;
    }

    public SpanUtils appendLine(CharSequence text) {
        if (text != null) {
            apply(0);
            this.mText = text + LINE_SEPARATOR;
            return this;
        }
        throw new NullPointerException("Argument 'text' of type CharSequence (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils appendImage(Bitmap bitmap) {
        if (bitmap != null) {
            return appendImage(bitmap, 0);
        }
        throw new NullPointerException("Argument 'bitmap' of type Bitmap (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils appendImage(Bitmap bitmap, int align) {
        if (bitmap != null) {
            apply(1);
            this.imageBitmap = bitmap;
            this.alignImage = align;
            return this;
        }
        throw new NullPointerException("Argument 'bitmap' of type Bitmap (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils appendImage(Drawable drawable) {
        if (drawable != null) {
            return appendImage(drawable, 0);
        }
        throw new NullPointerException("Argument 'drawable' of type Drawable (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils appendImage(Drawable drawable, int align) {
        if (drawable != null) {
            apply(1);
            this.imageDrawable = drawable;
            this.alignImage = align;
            return this;
        }
        throw new NullPointerException("Argument 'drawable' of type Drawable (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils appendImage(Uri uri) {
        if (uri != null) {
            return appendImage(uri, 0);
        }
        throw new NullPointerException("Argument 'uri' of type Uri (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils appendImage(Uri uri, int align) {
        if (uri != null) {
            apply(1);
            this.imageUri = uri;
            this.alignImage = align;
            return this;
        }
        throw new NullPointerException("Argument 'uri' of type Uri (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public SpanUtils appendImage(int resourceId) {
        return appendImage(resourceId, 0);
    }

    public SpanUtils appendImage(int resourceId, int align) {
        apply(1);
        this.imageResourceId = resourceId;
        this.alignImage = align;
        return this;
    }

    public SpanUtils appendSpace(int size) {
        return appendSpace(size, 0);
    }

    public SpanUtils appendSpace(int size, int color) {
        apply(2);
        this.spaceSize = size;
        this.spaceColor = color;
        return this;
    }

    private void apply(int type) {
        applyLast();
        this.mType = type;
    }

    public SpannableStringBuilder get() {
        return this.mBuilder;
    }

    public SpannableStringBuilder create() {
        applyLast();
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.setText(this.mBuilder);
        }
        return this.mBuilder;
    }

    private void applyLast() {
        int i = this.mType;
        if (i == 0) {
            updateCharCharSequence();
        } else if (i == 1) {
            updateImage();
        } else if (i == 2) {
            updateSpace();
        }
        setDefault();
    }

    private void updateCharCharSequence() {
        if (this.mText.length() != 0) {
            int start = this.mBuilder.length();
            if (start == 0 && this.lineHeight != -1) {
                this.mBuilder.append(Character.toString(2)).append("\n").setSpan(new AbsoluteSizeSpan(0), 0, 2, 33);
                start = 2;
            }
            this.mBuilder.append(this.mText);
            int end = this.mBuilder.length();
            if (this.verticalAlign != -1) {
                this.mBuilder.setSpan(new VerticalAlignSpan(this.verticalAlign), start, end, this.flag);
            }
            if (this.foregroundColor != COLOR_DEFAULT) {
                this.mBuilder.setSpan(new ForegroundColorSpan(this.foregroundColor), start, end, this.flag);
            }
            if (this.backgroundColor != COLOR_DEFAULT) {
                this.mBuilder.setSpan(new BackgroundColorSpan(this.backgroundColor), start, end, this.flag);
            }
            if (this.first != -1) {
                this.mBuilder.setSpan(new LeadingMarginSpan.Standard(this.first, this.rest), start, end, this.flag);
            }
            int i = this.quoteColor;
            if (i != COLOR_DEFAULT) {
                this.mBuilder.setSpan(new CustomQuoteSpan(i, this.stripeWidth, this.quoteGapWidth), start, end, this.flag);
            }
            int i2 = this.bulletColor;
            if (i2 != COLOR_DEFAULT) {
                this.mBuilder.setSpan(new CustomBulletSpan(i2, this.bulletRadius, this.bulletGapWidth), start, end, this.flag);
            }
            if (this.fontSize != -1) {
                this.mBuilder.setSpan(new AbsoluteSizeSpan(this.fontSize, this.fontSizeIsDp), start, end, this.flag);
            }
            if (this.proportion != -1.0f) {
                this.mBuilder.setSpan(new RelativeSizeSpan(this.proportion), start, end, this.flag);
            }
            if (this.xProportion != -1.0f) {
                this.mBuilder.setSpan(new ScaleXSpan(this.xProportion), start, end, this.flag);
            }
            int i3 = this.lineHeight;
            if (i3 != -1) {
                this.mBuilder.setSpan(new CustomLineHeightSpan(i3, this.alignLine), start, end, this.flag);
            }
            if (this.isStrikethrough) {
                this.mBuilder.setSpan(new StrikethroughSpan(), start, end, this.flag);
            }
            if (this.isUnderline) {
                this.mBuilder.setSpan(new UnderlineSpan(), start, end, this.flag);
            }
            if (this.isSuperscript) {
                this.mBuilder.setSpan(new SuperscriptSpan(), start, end, this.flag);
            }
            if (this.isSubscript) {
                this.mBuilder.setSpan(new SubscriptSpan(), start, end, this.flag);
            }
            if (this.isBold) {
                this.mBuilder.setSpan(new StyleSpan(1), start, end, this.flag);
            }
            if (this.isItalic) {
                this.mBuilder.setSpan(new StyleSpan(2), start, end, this.flag);
            }
            if (this.isBoldItalic) {
                this.mBuilder.setSpan(new StyleSpan(3), start, end, this.flag);
            }
            if (this.fontFamily != null) {
                this.mBuilder.setSpan(new TypefaceSpan(this.fontFamily), start, end, this.flag);
            }
            if (this.typeface != null) {
                this.mBuilder.setSpan(new CustomTypefaceSpan(this.typeface), start, end, this.flag);
            }
            if (this.alignment != null) {
                this.mBuilder.setSpan(new AlignmentSpan.Standard(this.alignment), start, end, this.flag);
            }
            ClickableSpan clickableSpan = this.clickSpan;
            if (clickableSpan != null) {
                this.mBuilder.setSpan(clickableSpan, start, end, this.flag);
            }
            if (this.url != null) {
                this.mBuilder.setSpan(new URLSpan(this.url), start, end, this.flag);
            }
            if (this.blurRadius != -1.0f) {
                this.mBuilder.setSpan(new MaskFilterSpan(new BlurMaskFilter(this.blurRadius, this.style)), start, end, this.flag);
            }
            if (this.shader != null) {
                this.mBuilder.setSpan(new ShaderSpan(this.shader), start, end, this.flag);
            }
            if (this.shadowRadius != -1.0f) {
                this.mBuilder.setSpan(new ShadowSpan(this.shadowRadius, this.shadowDx, this.shadowDy, this.shadowColor), start, end, this.flag);
            }
            Object[] objArr = this.spans;
            if (objArr != null) {
                for (Object span : objArr) {
                    this.mBuilder.setSpan(span, start, end, this.flag);
                }
            }
        }
    }

    private void updateImage() {
        int start = this.mBuilder.length();
        this.mText = "<img>";
        updateCharCharSequence();
        int end = this.mBuilder.length();
        if (this.imageBitmap != null) {
            this.mBuilder.setSpan(new CustomImageSpan(this.imageBitmap, this.alignImage), start, end, this.flag);
        } else if (this.imageDrawable != null) {
            this.mBuilder.setSpan(new CustomImageSpan(this.imageDrawable, this.alignImage), start, end, this.flag);
        } else if (this.imageUri != null) {
            this.mBuilder.setSpan(new CustomImageSpan(this.imageUri, this.alignImage), start, end, this.flag);
        } else if (this.imageResourceId != -1) {
            this.mBuilder.setSpan(new CustomImageSpan(this.imageResourceId, this.alignImage), start, end, this.flag);
        }
    }

    private void updateSpace() {
        int start = this.mBuilder.length();
        this.mText = "< >";
        updateCharCharSequence();
        this.mBuilder.setSpan(new SpaceSpan(this.spaceSize, this.spaceColor), start, this.mBuilder.length(), this.flag);
    }

    static class VerticalAlignSpan extends ReplacementSpan {
        static final int ALIGN_CENTER = 2;
        static final int ALIGN_TOP = 3;
        final int mVerticalAlignment;

        VerticalAlignSpan(int verticalAlignment) {
            this.mVerticalAlignment = verticalAlignment;
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (paint != null) {
                return (int) paint.measureText(text.subSequence(start, end).toString());
            }
            throw new NullPointerException("Argument 'paint' of type Paint (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            if (canvas == null) {
                throw new NullPointerException("Argument 'canvas' of type Canvas (#0 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            } else if (paint != null) {
                CharSequence text2 = text.subSequence(start, end);
                Paint.FontMetricsInt fm = paint.getFontMetricsInt();
                canvas.drawText(text2.toString(), x, (float) (y - (((((fm.descent + y) + y) + fm.ascent) / 2) - ((bottom + top) / 2))), paint);
            } else {
                throw new NullPointerException("Argument 'paint' of type Paint (#8 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            }
        }
    }

    static class CustomLineHeightSpan implements LineHeightSpan {
        static final int ALIGN_CENTER = 2;
        static final int ALIGN_TOP = 3;
        static Paint.FontMetricsInt sfm;
        private final int height;
        final int mVerticalAlignment;

        CustomLineHeightSpan(int height2, int verticalAlignment) {
            this.height = height2;
            this.mVerticalAlignment = verticalAlignment;
        }

        public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
            LogUtils.e(fm, sfm);
            Paint.FontMetricsInt fontMetricsInt = sfm;
            if (fontMetricsInt == null) {
                Paint.FontMetricsInt fontMetricsInt2 = new Paint.FontMetricsInt();
                sfm = fontMetricsInt2;
                fontMetricsInt2.top = fm.top;
                sfm.ascent = fm.ascent;
                sfm.descent = fm.descent;
                sfm.bottom = fm.bottom;
                sfm.leading = fm.leading;
            } else {
                fm.top = fontMetricsInt.top;
                fm.ascent = sfm.ascent;
                fm.descent = sfm.descent;
                fm.bottom = sfm.bottom;
                fm.leading = sfm.leading;
            }
            int need = this.height - (((fm.descent + v) - fm.ascent) - spanstartv);
            if (need > 0) {
                int i = this.mVerticalAlignment;
                if (i == 3) {
                    fm.descent += need;
                } else if (i == 2) {
                    fm.descent += need / 2;
                    fm.ascent -= need / 2;
                } else {
                    fm.ascent -= need;
                }
            }
            int need2 = this.height - (((fm.bottom + v) - fm.top) - spanstartv);
            if (need2 > 0) {
                int i2 = this.mVerticalAlignment;
                if (i2 == 3) {
                    fm.bottom += need2;
                } else if (i2 == 2) {
                    fm.bottom += need2 / 2;
                    fm.top -= need2 / 2;
                } else {
                    fm.top -= need2;
                }
            }
            if (end == ((Spanned) text).getSpanEnd(this)) {
                sfm = null;
            }
            LogUtils.e(fm, sfm);
        }
    }

    static class SpaceSpan extends ReplacementSpan {
        private final Paint paint;
        private final int width;

        private SpaceSpan(int width2) {
            this(width2, 0);
        }

        private SpaceSpan(int width2, int color) {
            Paint paint2 = new Paint();
            this.paint = paint2;
            this.width = width2;
            paint2.setColor(color);
            this.paint.setStyle(Paint.Style.FILL);
        }

        public int getSize(Paint paint2, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (paint2 != null) {
                return this.width;
            }
            throw new NullPointerException("Argument 'paint' of type Paint (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint2) {
            if (canvas == null) {
                throw new NullPointerException("Argument 'canvas' of type Canvas (#0 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            } else if (paint2 != null) {
                canvas.drawRect(x, (float) top, x + ((float) this.width), (float) bottom, this.paint);
            } else {
                throw new NullPointerException("Argument 'paint' of type Paint (#8 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            }
        }
    }

    static class CustomQuoteSpan implements LeadingMarginSpan {
        private final int color;
        private final int gapWidth;
        private final int stripeWidth;

        private CustomQuoteSpan(int color2, int stripeWidth2, int gapWidth2) {
            this.color = color2;
            this.stripeWidth = stripeWidth2;
            this.gapWidth = gapWidth2;
        }

        public int getLeadingMargin(boolean first) {
            return this.stripeWidth + this.gapWidth;
        }

        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
            Paint paint = p;
            int i = x;
            Paint.Style style = p.getStyle();
            int color2 = p.getColor();
            p.setStyle(Paint.Style.FILL);
            p.setColor(this.color);
            c.drawRect((float) i, (float) top, (float) ((this.stripeWidth * dir) + i), (float) bottom, p);
            p.setStyle(style);
            p.setColor(color2);
        }
    }

    static class CustomBulletSpan implements LeadingMarginSpan {
        private final int color;
        private final int gapWidth;
        private final int radius;
        private Path sBulletPath;

        private CustomBulletSpan(int color2, int radius2, int gapWidth2) {
            this.sBulletPath = null;
            this.color = color2;
            this.radius = radius2;
            this.gapWidth = gapWidth2;
        }

        public int getLeadingMargin(boolean first) {
            return (this.radius * 2) + this.gapWidth;
        }

        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout l) {
            Canvas canvas = c;
            Paint paint = p;
            if (((Spanned) text).getSpanStart(this) == start) {
                Paint.Style style = p.getStyle();
                int oldColor = p.getColor();
                p.setColor(this.color);
                p.setStyle(Paint.Style.FILL);
                if (c.isHardwareAccelerated()) {
                    if (this.sBulletPath == null) {
                        Path path = new Path();
                        this.sBulletPath = path;
                        path.addCircle(0.0f, 0.0f, (float) this.radius, Path.Direction.CW);
                    }
                    c.save();
                    c.translate((float) ((this.radius * dir) + x), ((float) (top + bottom)) / 2.0f);
                    c.drawPath(this.sBulletPath, p);
                    c.restore();
                } else {
                    int i = this.radius;
                    c.drawCircle((float) ((dir * i) + x), ((float) (top + bottom)) / 2.0f, (float) i, p);
                }
                p.setColor(oldColor);
                p.setStyle(style);
            }
        }
    }

    static class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        private CustomTypefaceSpan(Typeface type) {
            super("");
            this.newType = type;
        }

        public void updateDrawState(TextPaint textPaint) {
            apply(textPaint, this.newType);
        }

        public void updateMeasureState(TextPaint paint) {
            apply(paint, this.newType);
        }

        private void apply(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }
            int fake = (~tf.getStyle()) & oldStyle;
            if ((fake & 1) != 0) {
                paint.setFakeBoldText(true);
            }
            if ((fake & 2) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.getShader();
            paint.setTypeface(tf);
        }
    }

    static class CustomImageSpan extends CustomDynamicDrawableSpan {
        private Uri mContentUri;
        private Drawable mDrawable;
        private int mResourceId;

        private CustomImageSpan(Bitmap b, int verticalAlignment) {
            super(verticalAlignment);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(Utils.getApp().getResources(), b);
            this.mDrawable = bitmapDrawable;
            bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight());
        }

        private CustomImageSpan(Drawable d, int verticalAlignment) {
            super(verticalAlignment);
            this.mDrawable = d;
            d.setBounds(0, 0, d.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight());
        }

        private CustomImageSpan(Uri uri, int verticalAlignment) {
            super(verticalAlignment);
            this.mContentUri = uri;
        }

        private CustomImageSpan(int resourceId, int verticalAlignment) {
            super(verticalAlignment);
            this.mResourceId = resourceId;
        }

        public Drawable getDrawable() {
            Drawable drawable = null;
            if (this.mDrawable != null) {
                return this.mDrawable;
            }
            if (this.mContentUri != null) {
                try {
                    InputStream is = Utils.getApp().getContentResolver().openInputStream(this.mContentUri);
                    Drawable drawable2 = new BitmapDrawable(Utils.getApp().getResources(), BitmapFactory.decodeStream(is));
                    drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
                    if (is == null) {
                        return drawable2;
                    }
                    is.close();
                    return drawable2;
                } catch (Exception e) {
                    Log.e("sms", "Failed to loaded content " + this.mContentUri, e);
                    return null;
                }
            } else {
                try {
                    drawable = ContextCompat.getDrawable(Utils.getApp(), this.mResourceId);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    return drawable;
                } catch (Exception e2) {
                    Log.e("sms", "Unable to find resource: " + this.mResourceId);
                    return drawable;
                }
            }
        }
    }

    static abstract class CustomDynamicDrawableSpan extends ReplacementSpan {
        static final int ALIGN_BASELINE = 1;
        static final int ALIGN_BOTTOM = 0;
        static final int ALIGN_CENTER = 2;
        static final int ALIGN_TOP = 3;
        private WeakReference<Drawable> mDrawableRef;
        final int mVerticalAlignment;

        public abstract Drawable getDrawable();

        private CustomDynamicDrawableSpan() {
            this.mVerticalAlignment = 0;
        }

        private CustomDynamicDrawableSpan(int verticalAlignment) {
            this.mVerticalAlignment = verticalAlignment;
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            int lineHeight;
            if (paint != null) {
                Rect rect = getCachedDrawable().getBounds();
                if (fm != null && (lineHeight = fm.bottom - fm.top) < rect.height()) {
                    int i = this.mVerticalAlignment;
                    if (i == 3) {
                        fm.top = fm.top;
                        fm.bottom = rect.height() + fm.top;
                    } else if (i == 2) {
                        fm.top = ((-rect.height()) / 2) - (lineHeight / 4);
                        fm.bottom = (rect.height() / 2) - (lineHeight / 4);
                    } else {
                        fm.top = (-rect.height()) + fm.bottom;
                        fm.bottom = fm.bottom;
                    }
                    fm.ascent = fm.top;
                    fm.descent = fm.bottom;
                }
                return rect.right;
            }
            throw new NullPointerException("Argument 'paint' of type Paint (#0 out of 5, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            float transY;
            if (canvas == null) {
                throw new NullPointerException("Argument 'canvas' of type Canvas (#0 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            } else if (paint != null) {
                Drawable d = getCachedDrawable();
                Rect rect = d.getBounds();
                canvas.save();
                if (rect.height() < bottom - top) {
                    int i = this.mVerticalAlignment;
                    if (i == 3) {
                        transY = (float) top;
                    } else if (i == 2) {
                        transY = (float) (((bottom + top) - rect.height()) / 2);
                    } else if (i == 1) {
                        transY = (float) (y - rect.height());
                    } else {
                        transY = (float) (bottom - rect.height());
                    }
                    canvas.translate(x, transY);
                } else {
                    canvas.translate(x, (float) top);
                }
                d.draw(canvas);
                canvas.restore();
            } else {
                throw new NullPointerException("Argument 'paint' of type Paint (#8 out of 9, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: android.graphics.drawable.Drawable} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private android.graphics.drawable.Drawable getCachedDrawable() {
            /*
                r3 = this;
                java.lang.ref.WeakReference<android.graphics.drawable.Drawable> r0 = r3.mDrawableRef
                r1 = 0
                if (r0 == 0) goto L_0x000c
                java.lang.Object r2 = r0.get()
                r1 = r2
                android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            L_0x000c:
                if (r1 != 0) goto L_0x0019
                android.graphics.drawable.Drawable r1 = r3.getDrawable()
                java.lang.ref.WeakReference r2 = new java.lang.ref.WeakReference
                r2.<init>(r1)
                r3.mDrawableRef = r2
            L_0x0019:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.SpanUtils.CustomDynamicDrawableSpan.getCachedDrawable():android.graphics.drawable.Drawable");
        }
    }

    static class ShaderSpan extends CharacterStyle implements UpdateAppearance {
        private Shader mShader;

        private ShaderSpan(Shader shader) {
            this.mShader = shader;
        }

        public void updateDrawState(TextPaint tp) {
            tp.setShader(this.mShader);
        }
    }

    static class ShadowSpan extends CharacterStyle implements UpdateAppearance {
        private float dx;
        private float dy;
        private float radius;
        private int shadowColor;

        private ShadowSpan(float radius2, float dx2, float dy2, int shadowColor2) {
            this.radius = radius2;
            this.dx = dx2;
            this.dy = dy2;
            this.shadowColor = shadowColor2;
        }

        public void updateDrawState(TextPaint tp) {
            tp.setShadowLayer(this.radius, this.dx, this.dy, this.shadowColor);
        }
    }

    private static class SerializableSpannableStringBuilder extends SpannableStringBuilder implements Serializable {
        private static final long serialVersionUID = 4909567650765875771L;

        private SerializableSpannableStringBuilder() {
        }
    }

    public static SpanUtils with(TextView textView) {
        return new SpanUtils(textView);
    }
}

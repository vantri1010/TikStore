package im.bclpbkiauv.messenger.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

public class TextTool {
    public static Builder getBuilder(CharSequence text) {
        return new Builder(text);
    }

    public static class Builder {
        private Layout.Alignment align;
        private int backgroundColor;
        private Bitmap bitmap;
        private int bulletColor;
        private ClickableSpan clickSpan;
        private int defaultValue;
        private Drawable drawable;
        private int first;
        private int flag;
        private String fontFamily;
        private int foregroundColor;
        private int gapWidth;
        private boolean imageIsBitmap;
        private boolean imageIsDrawable;
        private boolean imageIsResourceId;
        private boolean imageIsUri;
        private boolean isBlur;
        private boolean isBold;
        private boolean isBoldItalic;
        private boolean isBullet;
        private boolean isItalic;
        private boolean isLeadingMargin;
        private boolean isStrikethrough;
        private boolean isSubscript;
        private boolean isSuperscript;
        private boolean isUnderline;
        private SpannableStringBuilder mBuilder;
        private Context mContext;
        private float proportion;
        private int quoteColor;
        private float radius;
        private int resourceId;
        private int rest;
        private BlurMaskFilter.Blur style;
        private CharSequence text;
        private Uri uri;
        private String url;
        private float xProportion;

        private Builder(CharSequence text2) {
            this.defaultValue = 301989888;
            this.text = text2;
            this.flag = 33;
            this.foregroundColor = 301989888;
            this.backgroundColor = 301989888;
            this.quoteColor = 301989888;
            this.proportion = -1.0f;
            this.xProportion = -1.0f;
            this.mBuilder = new SpannableStringBuilder();
        }

        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setFlag(int flag2) {
            this.flag = flag2;
            return this;
        }

        public Builder setForegroundColor(int color) {
            this.foregroundColor = color;
            return this;
        }

        public Builder setBackgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder setQuoteColor(int color) {
            this.quoteColor = color;
            return this;
        }

        public Builder setLeadingMargin(int first2, int rest2) {
            this.first = first2;
            this.rest = rest2;
            this.isLeadingMargin = true;
            return this;
        }

        public Builder setBullet(int gapWidth2, int color) {
            this.gapWidth = gapWidth2;
            this.bulletColor = color;
            this.isBullet = true;
            return this;
        }

        public Builder setProportion(float proportion2) {
            this.proportion = proportion2;
            return this;
        }

        public Builder setXProportion(float proportion2) {
            this.xProportion = proportion2;
            return this;
        }

        public Builder setStrikethrough() {
            this.isStrikethrough = true;
            return this;
        }

        public Builder setUnderline() {
            this.isUnderline = true;
            return this;
        }

        public Builder setSuperscript() {
            this.isSuperscript = true;
            return this;
        }

        public Builder setSubscript() {
            this.isSubscript = true;
            return this;
        }

        public Builder setBold() {
            this.isBold = true;
            return this;
        }

        public Builder setItalic() {
            this.isItalic = true;
            return this;
        }

        public Builder setBoldItalic() {
            this.isBoldItalic = true;
            return this;
        }

        public Builder setFontFamily(String fontFamily2) {
            this.fontFamily = fontFamily2;
            return this;
        }

        public Builder setAlign(Layout.Alignment align2) {
            this.align = align2;
            return this;
        }

        public Builder setBitmap(Bitmap bitmap2) {
            this.bitmap = bitmap2;
            this.imageIsBitmap = true;
            return this;
        }

        public Builder setDrawable(Drawable drawable2) {
            this.drawable = drawable2;
            this.imageIsDrawable = true;
            return this;
        }

        public Builder setUri(Uri uri2) {
            this.uri = uri2;
            this.imageIsUri = true;
            return this;
        }

        public Builder setResourceId(int resourceId2) {
            this.resourceId = resourceId2;
            this.imageIsResourceId = true;
            return this;
        }

        public Builder setClickSpan(ClickableSpan clickSpan2) {
            this.clickSpan = clickSpan2;
            return this;
        }

        public Builder setUrl(String url2) {
            this.url = url2;
            return this;
        }

        public Builder setBlur(float radius2, BlurMaskFilter.Blur style2) {
            this.radius = radius2;
            this.style = style2;
            this.isBlur = true;
            return this;
        }

        public Builder append(CharSequence text2) {
            setSpan();
            this.text = text2;
            return this;
        }

        public SpannableStringBuilder create() {
            setSpan();
            return this.mBuilder;
        }

        public void into(TextView textView) {
            setSpan();
            if (textView != null) {
                textView.setText(this.mBuilder);
            }
        }

        private void setSpan() {
            int start = this.mBuilder.length();
            this.mBuilder.append(this.text);
            int end = this.mBuilder.length();
            if (this.foregroundColor != this.defaultValue) {
                this.mBuilder.setSpan(new ForegroundColorSpan(this.foregroundColor), start, end, this.flag);
                this.foregroundColor = this.defaultValue;
            }
            if (this.backgroundColor != this.defaultValue) {
                this.mBuilder.setSpan(new BackgroundColorSpan(this.backgroundColor), start, end, this.flag);
                this.backgroundColor = this.defaultValue;
            }
            if (this.isLeadingMargin) {
                this.mBuilder.setSpan(new LeadingMarginSpan.Standard(this.first, this.rest), start, end, this.flag);
                this.isLeadingMargin = false;
            }
            if (this.quoteColor != this.defaultValue) {
                this.mBuilder.setSpan(new QuoteSpan(this.quoteColor), start, end, 0);
                this.quoteColor = this.defaultValue;
            }
            if (this.isBullet) {
                this.mBuilder.setSpan(new BulletSpan(this.gapWidth, this.bulletColor), start, end, 0);
                this.isBullet = false;
            }
            if (this.proportion != -1.0f) {
                this.mBuilder.setSpan(new RelativeSizeSpan(this.proportion), start, end, this.flag);
                this.proportion = -1.0f;
            }
            if (this.xProportion != -1.0f) {
                this.mBuilder.setSpan(new ScaleXSpan(this.xProportion), start, end, this.flag);
                this.xProportion = -1.0f;
            }
            if (this.isStrikethrough) {
                this.mBuilder.setSpan(new StrikethroughSpan(), start, end, this.flag);
                this.isStrikethrough = false;
            }
            if (this.isUnderline) {
                this.mBuilder.setSpan(new UnderlineSpan(), start, end, this.flag);
                this.isUnderline = false;
            }
            if (this.isSuperscript) {
                this.mBuilder.setSpan(new SuperscriptSpan(), start, end, this.flag);
                this.isSuperscript = false;
            }
            if (this.isSubscript) {
                this.mBuilder.setSpan(new SubscriptSpan(), start, end, this.flag);
                this.isSubscript = false;
            }
            if (this.isBold) {
                this.mBuilder.setSpan(new StyleSpan(1), start, end, this.flag);
                this.isBold = false;
            }
            if (this.isItalic) {
                this.mBuilder.setSpan(new StyleSpan(2), start, end, this.flag);
                this.isItalic = false;
            }
            if (this.isBoldItalic) {
                this.mBuilder.setSpan(new StyleSpan(3), start, end, this.flag);
                this.isBoldItalic = false;
            }
            if (this.fontFamily != null) {
                this.mBuilder.setSpan(new TypefaceSpan(this.fontFamily), start, end, this.flag);
                this.fontFamily = null;
            }
            if (this.align != null) {
                this.mBuilder.setSpan(new AlignmentSpan.Standard(this.align), start, end, this.flag);
                this.align = null;
            }
            if (this.imageIsBitmap || this.imageIsDrawable || this.imageIsUri || this.imageIsResourceId) {
                if (this.imageIsBitmap) {
                    this.mBuilder.setSpan(new ImageSpan(this.mContext, this.bitmap), start, end, this.flag);
                    this.bitmap = null;
                    this.imageIsBitmap = false;
                } else if (this.imageIsDrawable) {
                    this.mBuilder.setSpan(new ImageSpan(this.drawable), start, end, this.flag);
                    this.drawable = null;
                    this.imageIsDrawable = false;
                } else if (this.imageIsUri) {
                    this.mBuilder.setSpan(new ImageSpan(this.mContext, this.uri), start, end, this.flag);
                    this.uri = null;
                    this.imageIsUri = false;
                } else {
                    this.mBuilder.setSpan(new ImageSpan(this.mContext, this.resourceId), start, end, this.flag);
                    this.resourceId = 0;
                    this.imageIsResourceId = false;
                }
            }
            ClickableSpan clickableSpan = this.clickSpan;
            if (clickableSpan != null) {
                this.mBuilder.setSpan(clickableSpan, start, end, this.flag);
                this.clickSpan = null;
            }
            if (this.url != null) {
                this.mBuilder.setSpan(new URLSpan(this.url), start, end, this.flag);
                this.url = null;
            }
            if (this.isBlur) {
                this.mBuilder.setSpan(new MaskFilterSpan(new BlurMaskFilter(this.radius, this.style)), start, end, this.flag);
                this.isBlur = false;
            }
            this.flag = 33;
        }
    }
}

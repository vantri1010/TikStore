package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.util.ArrayList;

public class TextCell extends FrameLayout {
    private LinearLayout container;
    private View divider;
    private ImageView imageValue;
    private ImageView imageView;
    private TextView titleText;
    private TextView valueText;

    public TextCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public TextCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.cell_text_layout, this);
        this.container = (LinearLayout) view.findViewById(R.id.container);
        this.titleText = (TextView) view.findViewById(R.id.titleText);
        this.valueText = (TextView) view.findViewById(R.id.valueText);
        this.imageView = (ImageView) view.findViewById(R.id.ivIcon);
        this.imageValue = (ImageView) view.findViewById(R.id.ivArrow);
        this.divider = view.findViewById(R.id.divider);
        this.titleText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleText.setTextSize(1, 14.0f);
        this.titleText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueText.setTextSize(1, 12.0f);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
        this.imageValue.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), PorterDuff.Mode.MULTIPLY));
    }

    public void clearColorFilter() {
        this.imageView.setColorFilter((ColorFilter) null);
    }

    public void setImageColorFilter(int color) {
        this.imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
    }

    public void setTitleSize(int size) {
        this.titleText.setTextSize(1, (float) size);
    }

    public void setTitleColor(int color) {
        this.titleText.setTextColor(color);
    }

    public void setText(String text, boolean div) {
        TextView textView = this.valueText;
        if (textView != null) {
            textView.setVisibility(8);
        }
        setData(0, text, 0, div);
    }

    public void setTypeface(Typeface tf) {
        this.titleText.setTypeface(tf);
    }

    public void setText(String text, String value, boolean div) {
        setData(0, text, value, 0, div);
    }

    public void setText(String text, boolean arrow, boolean div) {
        setData(0, text, "", arrow ? R.mipmap.icon_arrow_right : 0, div);
    }

    public void setText(int icon, String text, boolean arrow, boolean div) {
        setData(icon, text, "", arrow ? R.mipmap.icon_arrow_right : 0, div);
    }

    public void setText(String text, String value, boolean arrow, boolean div) {
        setData(0, text, value, arrow ? R.mipmap.icon_arrow_right : 0, div);
    }

    public void setData(int lId, String title, int rId, boolean divider2) {
        setData(lId, title, "", rId, divider2);
    }

    public void setBold(boolean bold) {
        this.titleText.setTypeface(bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    public void setData(int lId, String title, String value, int rId, boolean divider2) {
        int i = 0;
        if (lId > 0) {
            this.imageView.setVisibility(0);
            this.imageView.setImageResource(lId);
        } else {
            this.imageView.setVisibility(8);
        }
        if (rId > 0) {
            this.imageValue.setVisibility(0);
            this.imageValue.setImageResource(rId);
        } else {
            this.imageValue.setVisibility(8);
        }
        if (title != null) {
            this.titleText.setText(title);
        }
        if (value != null) {
            this.valueText.setText(value);
        }
        View view = this.divider;
        if (!divider2) {
            i = 8;
        }
        view.setVisibility(i);
    }

    public void setData(String title, Drawable rd, boolean divider2) {
        int i = 8;
        this.imageView.setVisibility(8);
        this.valueText.setVisibility(8);
        if (rd != null) {
            this.imageValue.setVisibility(0);
            this.imageValue.setImageDrawable(rd);
        } else {
            this.imageValue.setVisibility(8);
        }
        if (title != null) {
            this.titleText.setText(title);
        }
        View view = this.divider;
        if (divider2) {
            i = 0;
        }
        view.setVisibility(i);
    }

    public void setContainerBackground(Drawable drawable) {
        this.container.setBackground(drawable);
    }

    public void setContainerBackground(int res) {
        this.container.setBackgroundResource(res);
    }

    public boolean isContainerEnabled() {
        return this.container.isEnabled();
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        setContainerEnabled(value);
        float f = 1.0f;
        if (animators != null) {
            TextView textView = this.titleText;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView, "alpha", fArr));
            if (this.valueText.getVisibility() == 0) {
                TextView textView2 = this.valueText;
                float[] fArr2 = new float[1];
                fArr2[0] = value ? 1.0f : 0.5f;
                animators.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr2));
            }
            if (this.imageValue.getVisibility() == 0) {
                ImageView imageView2 = this.imageValue;
                float[] fArr3 = new float[1];
                if (!value) {
                    f = 0.5f;
                }
                fArr3[0] = f;
                animators.add(ObjectAnimator.ofFloat(imageView2, "alpha", fArr3));
                return;
            }
            return;
        }
        this.titleText.setAlpha(value ? 1.0f : 0.5f);
        if (this.valueText.getVisibility() == 0) {
            this.valueText.setAlpha(value ? 1.0f : 0.5f);
        }
        if (this.imageValue.getVisibility() == 0) {
            ImageView imageView3 = this.imageValue;
            if (!value) {
                f = 0.5f;
            }
            imageView3.setAlpha(f);
        }
    }

    public void setContainerEnabled(boolean value) {
        this.container.setEnabled(value);
        float f = 1.0f;
        this.titleText.setAlpha(value ? 1.0f : 0.5f);
        if (this.valueText.getVisibility() == 0) {
            this.valueText.setAlpha(value ? 1.0f : 0.5f);
        }
        if (this.imageValue.getVisibility() == 0) {
            ImageView imageView2 = this.imageValue;
            if (!value) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
        }
    }
}

package im.bclpbkiauv.ui.hui.adapter.pageAdapter;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageHolder;
import java.util.WeakHashMap;

public class PageHolder<H extends PageHolder> extends RecyclerView.ViewHolder {
    private WeakHashMap<Object, View> mViewMap;

    public PageHolder(View itemView) {
        this(itemView, Theme.key_windowBackgroundWhite);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public PageHolder(View itemView, String backgroundThemeKey) {
        this(itemView, backgroundThemeKey == null ? 0 : Theme.getColor(backgroundThemeKey));
    }

    public PageHolder(View itemView, int backgroundColor) {
        super(itemView);
        if (backgroundColor != 0) {
            itemView.setBackgroundColor(backgroundColor);
        }
    }

    private WeakHashMap<Object, View> getViewMap() {
        if (this.mViewMap == null) {
            this.mViewMap = new WeakHashMap<>();
        }
        return this.mViewMap;
    }

    public <V extends View> V getView(int viewId) {
        View v = getViewMap().get(Integer.valueOf(viewId));
        if (v == null) {
            v = this.itemView.findViewById(viewId);
            getViewMap().put(Integer.valueOf(viewId), v);
        }
        if (v != null) {
            return v;
        }
        return null;
    }

    public H setText(int viewId, int textResId) {
        return setText(viewId, (CharSequence) LocaleController.getString(textResId + "", textResId));
    }

    public H setText(View textView, int textResId) {
        return setText(textView, (CharSequence) LocaleController.getString(textResId + "", textResId));
    }

    public H setText(int viewId, CharSequence text) {
        return setText(getView(viewId), text);
    }

    public H setText(View textView, CharSequence text) {
        if (text != null) {
            if (!"null".equals(text + "") && (textView instanceof TextView)) {
                ((TextView) textView).setText(text);
            }
        }
        return this;
    }

    public H setHint(int viewId, int textResId) {
        return setHint(viewId, (CharSequence) LocaleController.getString(textResId + "", textResId));
    }

    public H setHint(View textView, int textResId) {
        return setHint(textView, (CharSequence) LocaleController.getString(textResId + "", textResId));
    }

    public H setHint(int viewId, CharSequence text) {
        return setHint(getView(viewId), text);
    }

    public H setHint(View textView, CharSequence text) {
        if (text != null) {
            if (!"null".equals(text + "") && (textView instanceof TextView)) {
                ((TextView) textView).setHint(text);
            }
        }
        return this;
    }

    public H setTextColorThemeGray(int viewId) {
        return setTextColorThemeGray(getView(viewId));
    }

    public H setTextColorThemeGray(View textView) {
        return setTextColor(textView, Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
    }

    public H setTextColorThemeBlack(int viewId) {
        return setTextColorThemeBlack(getView(viewId));
    }

    public H setTextColorThemeBlack(View textView) {
        return setTextColor(textView, Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    public H setTextColorThemeHint(int viewId) {
        return setTextColorThemeHint(getView(viewId));
    }

    public H setTextColorThemeHint(View textView) {
        return setTextColor(textView, Theme.getColor(Theme.key_dialogTextHint));
    }

    public H setTextColorThemePrimary(int viewId) {
        return setTextColorThemePrimary(getView(viewId));
    }

    public H setTextColorThemePrimary(View textView) {
        return setTextColor(textView, Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
    }

    public H setTextColorThemeLink(int viewId) {
        return setTextColorThemeLink(getView(viewId));
    }

    public H setTextColorThemeLink(View textView) {
        return setTextColor(textView, Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
    }

    public H setTextColor(int viewId, String themeColorKey) {
        return setTextColor(viewId, Theme.getColor(themeColorKey));
    }

    public H setTextColor(View textView, String themeColorKey) {
        return setTextColor(textView, Theme.getColor(themeColorKey));
    }

    public H setTextColor(int viewId, int color) {
        return setTextColor(getView(viewId), color);
    }

    public H setTextColor(View textView, int color) {
        if (textView instanceof TextView) {
            ((TextView) textView).setTextColor(color);
        }
        return this;
    }

    public H setTextSize(int viewId, int textSize) {
        return setTextSize(viewId, 2, textSize);
    }

    public H setTextSize(View textView, int textSize) {
        return setTextSize(textView, 2, textSize);
    }

    public H setTextSize(int viewId, int unit, int textSize) {
        return setTextSize(getView(viewId), unit, textSize);
    }

    public H setTextSize(View textView, int unit, int textSize) {
        if (textView instanceof TextView) {
            ((TextView) textView).setTextSize(unit, (float) textSize);
        }
        return this;
    }

    public H setTextBold(int viewId) {
        return setTextBold(getView(viewId));
    }

    public H setTextBold(View textView) {
        return setTextTypeface(textView, AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    }

    public H setTextItalic(int viewId) {
        return setTextItalic(getView(viewId));
    }

    public H setTextItalic(View textView) {
        return setTextTypeface(textView, AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
    }

    public H setTextTypeface(int viewId, Typeface typeface) {
        return setTextTypeface(getView(viewId), typeface, -1);
    }

    public H setTextTypeface(View textView, Typeface typeface) {
        return setTextTypeface(textView, typeface, -1);
    }

    public H setTextTypeface(int viewId, Typeface typeface, int typefaceStyle) {
        return setTextTypeface(getView(viewId), typeface, typefaceStyle);
    }

    public H setTextTypeface(View textView, Typeface typeface, int typefaceStyle) {
        if (textView instanceof TextView) {
            if (typefaceStyle != -1) {
                ((TextView) textView).setTypeface(typeface, typefaceStyle);
            } else {
                ((TextView) textView).setTypeface(typeface);
            }
        }
        return this;
    }

    public H setImageColorFilter(int viewId, String colorKey) {
        return setImageColorFilter(getView(viewId), colorKey);
    }

    public H setImageColorFilter(View view, String colorKey) {
        return setImageColorFilter(view, colorKey, PorterDuff.Mode.MULTIPLY);
    }

    public H setImageColorFilter(int viewId, int color) {
        return setImageColorFilter(getView(viewId), color);
    }

    public H setImageColorFilter(View view, int color) {
        return setImageColorFilter(view, color, PorterDuff.Mode.MULTIPLY);
    }

    public H setImageColorFilter(int viewId, int color, PorterDuff.Mode mode) {
        return setImageColorFilter(getView(viewId), color, mode);
    }

    public H setImageColorFilter(int viewId, String colorKey, PorterDuff.Mode mode) {
        return setImageColorFilter(getView(viewId), colorKey, mode);
    }

    public H setImageColorFilter(View view, String colorKey, PorterDuff.Mode mode) {
        return setImageColorFilter(view, Theme.getColor(colorKey), mode);
    }

    public H setImageColorFilter(View imageView, int color, PorterDuff.Mode mode) {
        if (imageView instanceof ImageView) {
            ((ImageView) imageView).setColorFilter(color, mode);
        }
        return this;
    }

    public H setImageResId(int viewId, int imageResId) {
        return setImageResId(getView(viewId), imageResId);
    }

    public H setImageResId(View imageView, int imageResId) {
        if (imageView instanceof ImageView) {
            ((ImageView) imageView).setImageResource(imageResId);
        }
        return this;
    }

    public H setImageDrawable(int viewId, Drawable iamgeDrawable) {
        return setImageDrawable(getView(viewId), iamgeDrawable);
    }

    public H setImageDrawable(View imageView, Drawable iamgeDrawable) {
        if (iamgeDrawable != null && (imageView instanceof ImageView)) {
            ((ImageView) imageView).setImageDrawable(iamgeDrawable);
        }
        return this;
    }

    public H setGone(int viewId, boolean gone) {
        return setGone(getView(viewId), gone);
    }

    public H setGone(View view, boolean gone) {
        if (view != null) {
            if (gone && view.getVisibility() != 8) {
                view.setVisibility(8);
            } else if (!gone && view.getVisibility() != 0) {
                view.setVisibility(0);
            }
        }
        return this;
    }

    public H setInVisible(int viewId, boolean inVisible) {
        return setInVisible(getView(viewId), inVisible);
    }

    public H setInVisible(View view, boolean inVisible) {
        if (view != null) {
            if (inVisible && view.getVisibility() != 4) {
                view.setVisibility(4);
            } else if (!inVisible && !(view.getVisibility() == 0 && view.getVisibility() == 8)) {
                view.setVisibility(0);
            }
        }
        return this;
    }

    public H setBackgroundPrimaryColor(int viewId) {
        return setBackgroundPrimaryColor(getView(viewId));
    }

    public H setBackgroundPrimaryColor(View view) {
        return setBackgroundColor(view, Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
    }

    public H setBackgroundWindowColor(int viewId) {
        return setBackgroundWindowColor(getView(viewId));
    }

    public H setBackgroundWindowColor(View view) {
        return setBackgroundColor(view, Theme.getColor(Theme.key_windowBackgroundWhite));
    }

    public H setBackgroundWindowGrayColor(int viewId) {
        return setBackgroundWindowGrayColor(getView(viewId));
    }

    public H setBackgroundWindowGrayColor(View view) {
        return setBackgroundColor(view, Theme.getColor(Theme.key_windowBackgroundGray));
    }

    public H setBackgroundColor(int viewId, String themeColorKey) {
        return setBackgroundColor(viewId, Theme.getColor(themeColorKey));
    }

    public H setBackgroundColor(int viewId, int colorResId) {
        return setBackgroundColor(getView(viewId), colorResId);
    }

    public H setBackgroundColor(View view, String themeColorKey) {
        return setBackgroundColor(view, Theme.getColor(themeColorKey));
    }

    public H setBackgroundColor(View view, int colorResId) {
        if (view != null) {
            view.setBackgroundColor(colorResId);
        }
        return this;
    }

    public H setBackgroundDrawable(int viewId, Drawable background) {
        return setBackgroundDrawable(getView(viewId), background);
    }

    public H setBackgroundDrawable(View view, Drawable background) {
        if (view != null) {
            view.setBackground(background);
        }
        return this;
    }

    public H setOnClickListener(int viewId, View.OnClickListener onClickListener) {
        return setOnClickListener(getView(viewId), onClickListener);
    }

    public H setOnClickListener(View view, View.OnClickListener onClickListener) {
        if (view != null) {
            view.setOnClickListener(onClickListener);
        }
        return this;
    }
}

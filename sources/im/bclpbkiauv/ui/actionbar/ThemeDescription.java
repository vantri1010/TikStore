package im.bclpbkiauv.ui.actionbar;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.SpannedString;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ChatBigEmptyView;
import im.bclpbkiauv.ui.components.CheckBox;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.ContextProgressView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EditTextCaption;
import im.bclpbkiauv.ui.components.EditTextEmoji;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.GroupCreateCheckBox;
import im.bclpbkiauv.ui.components.GroupCreateSpan;
import im.bclpbkiauv.ui.components.LetterDrawable;
import im.bclpbkiauv.ui.components.LineProgressView;
import im.bclpbkiauv.ui.components.MessageBackgroundDrawable;
import im.bclpbkiauv.ui.components.NumberTextView;
import im.bclpbkiauv.ui.components.RLottieDrawable;
import im.bclpbkiauv.ui.components.RLottieImageView;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RadioButton;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ScamDrawable;
import im.bclpbkiauv.ui.components.SeekBarView;
import im.bclpbkiauv.ui.components.TypefaceSpan;
import im.bclpbkiauv.ui.components.VideoTimelineView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class ThemeDescription {
    public static int FLAG_AB_AM_BACKGROUND = 1048576;
    public static int FLAG_AB_AM_ITEMSCOLOR = 512;
    public static int FLAG_AB_AM_SELECTORCOLOR = 4194304;
    public static int FLAG_AB_AM_TOPBACKGROUND = 2097152;
    public static int FLAG_AB_ITEMSCOLOR = 64;
    public static int FLAG_AB_SEARCH = 134217728;
    public static int FLAG_AB_SEARCHPLACEHOLDER = ConnectionsManager.FileTypeFile;
    public static int FLAG_AB_SELECTORCOLOR = 256;
    public static int FLAG_AB_SUBMENUBACKGROUND = Integer.MIN_VALUE;
    public static int FLAG_AB_SUBMENUITEM = 1073741824;
    public static int FLAG_AB_SUBTITLECOLOR = 1024;
    public static int FLAG_AB_TITLECOLOR = 128;
    public static int FLAG_BACKGROUND = 1;
    public static int FLAG_BACKGROUNDFILTER = 32;
    public static int FLAG_CELLBACKGROUNDCOLOR = 16;
    public static int FLAG_CHECKBOX = 8192;
    public static int FLAG_CHECKBOXCHECK = 16384;
    public static int FLAG_CHECKTAG = 262144;
    public static int FLAG_CURSORCOLOR = 16777216;
    public static int FLAG_DRAWABLESELECTEDSTATE = 65536;
    public static int FLAG_FASTSCROLL = ConnectionsManager.FileTypeVideo;
    public static int FLAG_HINTTEXTCOLOR = 8388608;
    public static int FLAG_IMAGECOLOR = 8;
    public static int FLAG_LINKCOLOR = 2;
    public static int FLAG_LISTGLOWCOLOR = 32768;
    public static int FLAG_PROGRESSBAR = 2048;
    public static int FLAG_SECTIONS = 524288;
    public static int FLAG_SELECTOR = 4096;
    public static int FLAG_SELECTORWHITE = C.ENCODING_PCM_MU_LAW;
    public static int FLAG_SERVICEBACKGROUND = 536870912;
    public static int FLAG_TEXTCOLOR = 4;
    public static int FLAG_USEBACKGROUNDDRAWABLE = 131072;
    private int alphaOverride;
    private HashMap<String, Field> cachedFields;
    private int changeFlags;
    private int currentColor;
    private String currentKey;
    private int defaultColor;
    private ThemeDescriptionDelegate delegate;
    private Drawable[] drawablesToUpdate;
    private Class[] listClasses;
    private String[] listClassesFieldName;
    private String lottieLayerName;
    private HashMap<String, Boolean> notFoundCachedFields;
    private Paint[] paintToUpdate;
    private int previousColor;
    private boolean[] previousIsDefault;
    private View viewToInvalidate;

    public interface ThemeDescriptionDelegate {
        void didSetColor();
    }

    public ThemeDescription(View view, int flags, Class[] classes, Paint[] paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key, Object unused) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.paintToUpdate = paint;
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.delegate = themeDescriptionDelegate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, Paint paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        if (paint != null) {
            this.paintToUpdate = new Paint[]{paint};
        }
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.delegate = themeDescriptionDelegate;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, RLottieDrawable[] drawables, String layerName, String key) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.lottieLayerName = layerName;
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        if (view instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view).getEditText();
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, Paint[] paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this(view, flags, classes, classesFields, paint, drawables, -1, themeDescriptionDelegate, key);
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, Paint[] paint, Drawable[] drawables, int alpha, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.paintToUpdate = paint;
        this.drawablesToUpdate = drawables;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.listClassesFieldName = classesFields;
        this.alphaOverride = alpha;
        this.delegate = themeDescriptionDelegate;
        this.cachedFields = new HashMap<>();
        this.notFoundCachedFields = new HashMap<>();
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, String layerName, String key) {
        this.alphaOverride = -1;
        this.previousIsDefault = new boolean[1];
        this.currentKey = key;
        this.lottieLayerName = layerName;
        this.viewToInvalidate = view;
        this.changeFlags = flags;
        this.listClasses = classes;
        this.listClassesFieldName = classesFields;
        this.cachedFields = new HashMap<>();
        this.notFoundCachedFields = new HashMap<>();
        View view2 = this.viewToInvalidate;
        if (view2 instanceof EditTextEmoji) {
            this.viewToInvalidate = ((EditTextEmoji) view2).getEditText();
        }
    }

    public ThemeDescriptionDelegate setDelegateDisabled() {
        ThemeDescriptionDelegate oldDelegate = this.delegate;
        this.delegate = null;
        return oldDelegate;
    }

    public void setColor(int color, boolean useDefault) {
        setColor(color, useDefault, true);
    }

    private boolean checkTag(String key, View view) {
        if (key == null || view == null) {
            return false;
        }
        Object viewTag = view.getTag();
        if (viewTag instanceof String) {
            return ((String) viewTag).contains(key);
        }
        return false;
    }

    public void setColor(int color, boolean useDefault, boolean save) {
        Class[] clsArr;
        Drawable background;
        if (save) {
            Theme.setColor(this.currentKey, color, useDefault);
        }
        this.currentColor = color;
        int i = this.alphaOverride;
        if (i > 0) {
            color = Color.argb(i, Color.red(color), Color.green(color), Color.blue(color));
        }
        if (this.paintToUpdate != null) {
            int a = 0;
            while (true) {
                Paint[] paintArr = this.paintToUpdate;
                if (a >= paintArr.length) {
                    break;
                }
                if ((this.changeFlags & FLAG_LINKCOLOR) == 0 || !(paintArr[a] instanceof TextPaint)) {
                    this.paintToUpdate[a].setColor(color);
                } else {
                    ((TextPaint) paintArr[a]).linkColor = color;
                }
                a++;
            }
        }
        if (this.drawablesToUpdate != null) {
            int a2 = 0;
            while (true) {
                Drawable[] drawableArr = this.drawablesToUpdate;
                if (a2 >= drawableArr.length) {
                    break;
                }
                if (drawableArr[a2] != null) {
                    if (drawableArr[a2] instanceof ScamDrawable) {
                        ((ScamDrawable) drawableArr[a2]).setColor(color);
                    } else if (drawableArr[a2] instanceof RLottieDrawable) {
                        if (this.lottieLayerName != null) {
                            ((RLottieDrawable) drawableArr[a2]).setLayerColor(this.lottieLayerName + ".**", color);
                        }
                    } else if (drawableArr[a2] instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                            ((CombinedDrawable) drawableArr[a2]).getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        } else {
                            ((CombinedDrawable) drawableArr[a2]).getIcon().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        }
                    } else if (drawableArr[a2] instanceof AvatarDrawable) {
                        ((AvatarDrawable) drawableArr[a2]).setColor(color);
                    } else {
                        drawableArr[a2].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                }
                a2++;
            }
        }
        View view = this.viewToInvalidate;
        if (view != null && this.listClasses == null && this.listClassesFieldName == null && ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, view))) {
            if ((this.changeFlags & FLAG_BACKGROUND) != 0) {
                Drawable background2 = this.viewToInvalidate.getBackground();
                if (background2 instanceof MessageBackgroundDrawable) {
                    ((MessageBackgroundDrawable) background2).setColor(color);
                } else {
                    this.viewToInvalidate.setBackgroundColor(color);
                }
            }
            int i2 = this.changeFlags;
            if ((FLAG_BACKGROUNDFILTER & i2) != 0) {
                if ((i2 & FLAG_PROGRESSBAR) != 0) {
                    View view2 = this.viewToInvalidate;
                    if (view2 instanceof EditTextBoldCursor) {
                        ((EditTextBoldCursor) view2).setErrorLineColor(color);
                    }
                } else {
                    Drawable drawable = this.viewToInvalidate.getBackground();
                    if (drawable instanceof CombinedDrawable) {
                        if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                            drawable = ((CombinedDrawable) drawable).getBackground();
                        } else {
                            drawable = ((CombinedDrawable) drawable).getIcon();
                        }
                    }
                    if (drawable != null) {
                        if ((drawable instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable))) {
                            Theme.setSelectorDrawableColor(drawable, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                        } else if (drawable instanceof ShapeDrawable) {
                            ((ShapeDrawable) drawable).getPaint().setColor(color);
                        } else {
                            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                        }
                    }
                }
            }
        }
        View view3 = this.viewToInvalidate;
        if (view3 instanceof ActionBar) {
            if ((this.changeFlags & FLAG_AB_ITEMSCOLOR) != 0) {
                ((ActionBar) view3).setItemsColor(color, false);
            }
            if ((this.changeFlags & FLAG_AB_TITLECOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setTitleColor(color);
            }
            if ((this.changeFlags & FLAG_AB_SELECTORCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(color, false);
            }
            if ((this.changeFlags & FLAG_AB_AM_SELECTORCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsBackgroundColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_AM_ITEMSCOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setItemsColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_SUBTITLECOLOR) != 0) {
                ((ActionBar) this.viewToInvalidate).setSubtitleColor(color);
            }
            if ((this.changeFlags & FLAG_AB_AM_BACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setActionModeColor(color);
            }
            if ((this.changeFlags & FLAG_AB_AM_TOPBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setActionModeTopColor(color);
            }
            if ((this.changeFlags & FLAG_AB_SEARCHPLACEHOLDER) != 0) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(color, true);
            }
            if ((this.changeFlags & FLAG_AB_SEARCH) != 0) {
                ((ActionBar) this.viewToInvalidate).setSearchTextColor(color, false);
            }
            int i3 = this.changeFlags;
            if ((FLAG_AB_SUBMENUITEM & i3) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupItemsColor(color, (i3 & FLAG_IMAGECOLOR) != 0);
            }
            if ((this.changeFlags & FLAG_AB_SUBMENUBACKGROUND) != 0) {
                ((ActionBar) this.viewToInvalidate).setPopupBackgroundColor(color);
            }
        }
        View view4 = this.viewToInvalidate;
        if (view4 instanceof VideoTimelineView) {
            ((VideoTimelineView) view4).setColor(color);
        }
        View view5 = this.viewToInvalidate;
        if (view5 instanceof EmptyTextProgressView) {
            int i4 = this.changeFlags;
            if ((FLAG_TEXTCOLOR & i4) != 0) {
                ((EmptyTextProgressView) view5).setTextColor(color);
            } else if ((i4 & FLAG_PROGRESSBAR) != 0) {
                ((EmptyTextProgressView) view5).setProgressBarColor(color);
            }
        }
        View view6 = this.viewToInvalidate;
        if (view6 instanceof RadialProgressView) {
            ((RadialProgressView) view6).setProgressColor(color);
        } else if (view6 instanceof LineProgressView) {
            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                ((LineProgressView) view6).setProgressColor(color);
            } else {
                ((LineProgressView) view6).setBackColor(color);
            }
        } else if (view6 instanceof ContextProgressView) {
            ((ContextProgressView) view6).updateColors();
        }
        int i5 = this.changeFlags;
        if ((FLAG_TEXTCOLOR & i5) != 0 && ((i5 & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            View view7 = this.viewToInvalidate;
            if (view7 instanceof TextView) {
                ((TextView) view7).setTextColor(color);
            } else if (view7 instanceof NumberTextView) {
                ((NumberTextView) view7).setTextColor(color);
            } else if (view7 instanceof SimpleTextView) {
                ((SimpleTextView) view7).setTextColor(color);
            } else if (view7 instanceof ChatBigEmptyView) {
                ((ChatBigEmptyView) view7).setTextColor(color);
            }
        }
        if ((this.changeFlags & FLAG_CURSORCOLOR) != 0) {
            View view8 = this.viewToInvalidate;
            if (view8 instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) view8).setCursorColor(color);
            }
        }
        int i6 = this.changeFlags;
        if ((FLAG_HINTTEXTCOLOR & i6) != 0) {
            View view9 = this.viewToInvalidate;
            if (view9 instanceof EditTextBoldCursor) {
                if ((i6 & FLAG_PROGRESSBAR) != 0) {
                    ((EditTextBoldCursor) view9).setHeaderHintColor(color);
                } else {
                    ((EditTextBoldCursor) view9).setHintColor(color);
                }
            } else if (view9 instanceof EditText) {
                ((EditText) view9).setHintTextColor(color);
            }
        }
        View view10 = this.viewToInvalidate;
        if (!(view10 == null || (this.changeFlags & FLAG_SERVICEBACKGROUND) == 0 || (background = view10.getBackground()) == null)) {
            background.setColorFilter(Theme.colorFilter);
        }
        int i7 = this.changeFlags;
        if ((FLAG_IMAGECOLOR & i7) != 0 && ((i7 & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, this.viewToInvalidate))) {
            View view11 = this.viewToInvalidate;
            if (view11 instanceof ImageView) {
                if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0) {
                    Drawable drawable2 = ((ImageView) view11).getDrawable();
                    if ((drawable2 instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (drawable2 instanceof RippleDrawable))) {
                        Theme.setSelectorDrawableColor(drawable2, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                    }
                } else {
                    ((ImageView) view11).setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            } else if (!(view11 instanceof BackupImageView) && (view11 instanceof SimpleTextView)) {
                ((SimpleTextView) view11).setSideDrawablesColor(color);
            }
        }
        View view12 = this.viewToInvalidate;
        if ((view12 instanceof ScrollView) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView) view12, color);
        }
        View view13 = this.viewToInvalidate;
        if ((view13 instanceof ViewPager) && (this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
            AndroidUtilities.setViewPagerEdgeEffectColor((ViewPager) view13, color);
        }
        View view14 = this.viewToInvalidate;
        if (view14 instanceof RecyclerListView) {
            RecyclerListView recyclerListView = (RecyclerListView) view14;
            if ((this.changeFlags & FLAG_SELECTOR) != 0 && this.currentKey.equals(Theme.key_listSelector)) {
                recyclerListView.setListSelectorColor(color);
            }
            if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
                recyclerListView.updateFastScrollColors();
            }
            if ((this.changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
                recyclerListView.setGlowColor(color);
            }
            if ((this.changeFlags & FLAG_SECTIONS) != 0) {
                ArrayList<View> headers = recyclerListView.getHeaders();
                if (headers != null) {
                    for (int a3 = 0; a3 < headers.size(); a3++) {
                        processViewColor(headers.get(a3), color);
                    }
                }
                ArrayList<View> headers2 = recyclerListView.getHeadersCache();
                if (headers2 != null) {
                    for (int a4 = 0; a4 < headers2.size(); a4++) {
                        processViewColor(headers2.get(a4), color);
                    }
                }
                View header = recyclerListView.getPinnedHeader();
                if (header != null) {
                    processViewColor(header, color);
                }
            }
        } else if (view14 != null && ((clsArr = this.listClasses) == null || clsArr.length == 0)) {
            int i8 = this.changeFlags;
            if ((FLAG_SELECTOR & i8) != 0) {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            } else if ((i8 & FLAG_SELECTORWHITE) != 0) {
                this.viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            }
        }
        if (this.listClasses != null) {
            View view15 = this.viewToInvalidate;
            if (view15 instanceof RecyclerListView) {
                RecyclerListView recyclerListView2 = (RecyclerListView) view15;
                recyclerListView2.getRecycledViewPool().clear();
                int count = recyclerListView2.getHiddenChildCount();
                for (int a5 = 0; a5 < count; a5++) {
                    processViewColor(recyclerListView2.getHiddenChildAt(a5), color);
                }
                int count2 = recyclerListView2.getCachedChildCount();
                for (int a6 = 0; a6 < count2; a6++) {
                    processViewColor(recyclerListView2.getCachedChildAt(a6), color);
                }
                int count3 = recyclerListView2.getAttachedScrapChildCount();
                for (int a7 = 0; a7 < count3; a7++) {
                    processViewColor(recyclerListView2.getAttachedScrapChildAt(a7), color);
                }
            }
            View view16 = this.viewToInvalidate;
            if (view16 instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view16;
                int count4 = viewGroup.getChildCount();
                for (int a8 = 0; a8 < count4; a8++) {
                    processViewColor(viewGroup.getChildAt(a8), color);
                }
            }
            processViewColor(this.viewToInvalidate, color);
        }
        ThemeDescriptionDelegate themeDescriptionDelegate = this.delegate;
        if (themeDescriptionDelegate != null) {
            themeDescriptionDelegate.didSetColor();
        }
        View view17 = this.viewToInvalidate;
        if (view17 != null) {
            view17.invalidate();
        }
    }

    private void processViewColor(View child, int color) {
        boolean passedCheck;
        Object object;
        TypefaceSpan[] spans;
        int b = 0;
        while (true) {
            Class[] clsArr = this.listClasses;
            if (b < clsArr.length) {
                if (clsArr[b].isInstance(child)) {
                    child.invalidate();
                    boolean z = false;
                    if ((this.changeFlags & FLAG_CHECKTAG) == 0 || checkTag(this.currentKey, child)) {
                        passedCheck = true;
                        child.invalidate();
                        int i = this.changeFlags;
                        if ((FLAG_BACKGROUNDFILTER & i) != 0) {
                            Drawable drawable = child.getBackground();
                            if (drawable != null) {
                                if ((this.changeFlags & FLAG_CELLBACKGROUNDCOLOR) == 0) {
                                    if (drawable instanceof CombinedDrawable) {
                                        drawable = ((CombinedDrawable) drawable).getIcon();
                                    } else if ((drawable instanceof StateListDrawable) || (Build.VERSION.SDK_INT >= 21 && (drawable instanceof RippleDrawable))) {
                                        Theme.setSelectorDrawableColor(drawable, color, (this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                                    }
                                    drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                } else if (drawable instanceof CombinedDrawable) {
                                    Drawable back = ((CombinedDrawable) drawable).getBackground();
                                    if (back instanceof ColorDrawable) {
                                        ((ColorDrawable) back).setColor(color);
                                    }
                                }
                            }
                        } else if ((FLAG_CELLBACKGROUNDCOLOR & i) != 0) {
                            child.setBackgroundColor(color);
                        } else if ((FLAG_TEXTCOLOR & i) != 0) {
                            if (child instanceof TextView) {
                                ((TextView) child).setTextColor(color);
                            }
                        } else if ((FLAG_SERVICEBACKGROUND & i) != 0) {
                            Drawable background = child.getBackground();
                            if (background != null) {
                                background.setColorFilter(Theme.colorFilter);
                            }
                        } else if ((FLAG_SELECTOR & i) != 0) {
                            child.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        } else if ((i & FLAG_SELECTORWHITE) != 0) {
                            child.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        }
                    } else {
                        passedCheck = false;
                    }
                    if (this.listClassesFieldName != null) {
                        String key = this.listClasses[b] + "_" + this.listClassesFieldName[b];
                        HashMap<String, Boolean> hashMap = this.notFoundCachedFields;
                        if (hashMap == null || !hashMap.containsKey(key)) {
                            try {
                                Field field = this.cachedFields.get(key);
                                if (field == null && (field = this.listClasses[b].getDeclaredField(this.listClassesFieldName[b])) != null) {
                                    field.setAccessible(true);
                                    this.cachedFields.put(key, field);
                                }
                                if (!(field == null || (object = field.get(child)) == null)) {
                                    if (passedCheck || !(object instanceof View) || checkTag(this.currentKey, (View) object)) {
                                        if (object instanceof View) {
                                            ((View) object).invalidate();
                                        }
                                        if (this.lottieLayerName != null && (object instanceof RLottieImageView)) {
                                            ((RLottieImageView) object).setLayerColor(this.lottieLayerName + ".**", color);
                                        }
                                        if ((this.changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0 && (object instanceof View)) {
                                            object = ((View) object).getBackground();
                                        }
                                        if ((this.changeFlags & FLAG_BACKGROUND) != 0 && (object instanceof View)) {
                                            View view = (View) object;
                                            Drawable background2 = view.getBackground();
                                            if (background2 instanceof MessageBackgroundDrawable) {
                                                ((MessageBackgroundDrawable) background2).setColor(color);
                                            } else {
                                                view.setBackgroundColor(color);
                                            }
                                        } else if (object instanceof EditTextCaption) {
                                            if ((this.changeFlags & FLAG_HINTTEXTCOLOR) != 0) {
                                                ((EditTextCaption) object).setHintColor(color);
                                                ((EditTextCaption) object).setHintTextColor(color);
                                            } else if ((this.changeFlags & FLAG_CURSORCOLOR) != 0) {
                                                ((EditTextCaption) object).setCursorColor(color);
                                            } else {
                                                ((EditTextCaption) object).setTextColor(color);
                                            }
                                        } else if (object instanceof SimpleTextView) {
                                            if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                ((SimpleTextView) object).setLinkTextColor(color);
                                            } else {
                                                ((SimpleTextView) object).setTextColor(color);
                                            }
                                        } else if (object instanceof TextView) {
                                            TextView textView = (TextView) object;
                                            if ((this.changeFlags & FLAG_IMAGECOLOR) != 0) {
                                                Drawable[] drawables = textView.getCompoundDrawables();
                                                if (drawables != null) {
                                                    for (int a = 0; a < drawables.length; a++) {
                                                        if (drawables[a] != null) {
                                                            drawables[a].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                                        }
                                                    }
                                                }
                                            } else if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                textView.getPaint().linkColor = color;
                                                textView.invalidate();
                                            } else if ((this.changeFlags & FLAG_FASTSCROLL) != 0) {
                                                CharSequence text = textView.getText();
                                                if ((text instanceof SpannedString) && (spans = (TypefaceSpan[]) ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class)) != null && spans.length > 0) {
                                                    for (TypefaceSpan color2 : spans) {
                                                        color2.setColor(color);
                                                    }
                                                }
                                            } else {
                                                textView.setTextColor(color);
                                            }
                                        } else if (object instanceof ImageView) {
                                            ((ImageView) object).setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                        } else if (object instanceof BackupImageView) {
                                            Drawable drawable2 = ((BackupImageView) object).getImageReceiver().getStaticThumb();
                                            if (drawable2 instanceof CombinedDrawable) {
                                                if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                    ((CombinedDrawable) drawable2).getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                                } else {
                                                    ((CombinedDrawable) drawable2).getIcon().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                                }
                                            } else if (drawable2 != null) {
                                                drawable2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                            }
                                        } else if (object instanceof Drawable) {
                                            if (object instanceof LetterDrawable) {
                                                if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                    ((LetterDrawable) object).setBackgroundColor(color);
                                                } else {
                                                    ((LetterDrawable) object).setColor(color);
                                                }
                                            } else if (!(object instanceof CombinedDrawable)) {
                                                if (!(object instanceof StateListDrawable)) {
                                                    if (Build.VERSION.SDK_INT < 21 || !(object instanceof RippleDrawable)) {
                                                        if (object instanceof GradientDrawable) {
                                                            ((GradientDrawable) object).setColor(color);
                                                        } else {
                                                            ((Drawable) object).setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                                        }
                                                    }
                                                }
                                                Drawable drawable3 = (Drawable) object;
                                                if ((this.changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                                                    z = true;
                                                }
                                                Theme.setSelectorDrawableColor(drawable3, color, z);
                                            } else if ((this.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                                ((CombinedDrawable) object).getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                            } else {
                                                ((CombinedDrawable) object).getIcon().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                            }
                                        } else if (object instanceof CheckBox) {
                                            if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                ((CheckBox) object).setBackgroundColor(color);
                                            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                ((CheckBox) object).setCheckColor(color);
                                            }
                                        } else if (object instanceof GroupCreateCheckBox) {
                                            ((GroupCreateCheckBox) object).updateColors();
                                        } else if (object instanceof Integer) {
                                            field.set(child, Integer.valueOf(color));
                                        } else if (object instanceof RadioButton) {
                                            if ((this.changeFlags & FLAG_CHECKBOX) != 0) {
                                                ((RadioButton) object).setBackgroundColor(color);
                                                ((RadioButton) object).invalidate();
                                            } else if ((this.changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                                ((RadioButton) object).setCheckedColor(color);
                                                ((RadioButton) object).invalidate();
                                            }
                                        } else if (object instanceof TextPaint) {
                                            if ((this.changeFlags & FLAG_LINKCOLOR) != 0) {
                                                ((TextPaint) object).linkColor = color;
                                            } else {
                                                ((TextPaint) object).setColor(color);
                                            }
                                        } else if (object instanceof LineProgressView) {
                                            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                                                ((LineProgressView) object).setProgressColor(color);
                                            } else {
                                                ((LineProgressView) object).setBackColor(color);
                                            }
                                        } else if (object instanceof RadialProgressView) {
                                            ((RadialProgressView) object).setProgressColor(color);
                                        } else if (object instanceof Paint) {
                                            ((Paint) object).setColor(color);
                                        } else if (object instanceof SeekBarView) {
                                            if ((this.changeFlags & FLAG_PROGRESSBAR) != 0) {
                                                ((SeekBarView) object).setOuterColor(color);
                                            } else {
                                                ((SeekBarView) object).setInnerColor(color);
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable e) {
                                FileLog.e(e);
                                this.notFoundCachedFields.put(key, true);
                            }
                        }
                    } else if (child instanceof GroupCreateSpan) {
                        ((GroupCreateSpan) child).updateColors();
                    }
                }
                b++;
            } else {
                return;
            }
        }
    }

    public String getCurrentKey() {
        return this.currentKey;
    }

    public void startEditing() {
        int color = Theme.getColor(this.currentKey, this.previousIsDefault);
        this.previousColor = color;
        this.currentColor = color;
    }

    public int getCurrentColor() {
        return this.currentColor;
    }

    public int getSetColor() {
        return Theme.getColor(this.currentKey);
    }

    public void setDefaultColor() {
        setColor(Theme.getDefaultColor(this.currentKey), true);
    }

    public void setPreviousColor() {
        setColor(this.previousColor, this.previousIsDefault[0]);
    }

    public String getTitle() {
        return this.currentKey;
    }
}

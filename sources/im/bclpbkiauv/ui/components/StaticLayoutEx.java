package im.bclpbkiauv.ui.components;

import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import java.lang.reflect.Constructor;

public class StaticLayoutEx {
    private static final String TEXT_DIRS_CLASS = "android.text.TextDirectionHeuristics";
    private static final String TEXT_DIR_CLASS = "android.text.TextDirectionHeuristic";
    private static final String TEXT_DIR_FIRSTSTRONG_LTR = "FIRSTSTRONG_LTR";
    public static Layout.Alignment[] alignments = Layout.Alignment.values();
    private static boolean initialized;
    private static Constructor<StaticLayout> sConstructor;
    private static Object[] sConstructorArgs;
    private static Object sTextDirection;

    public static Layout.Alignment ALIGN_RIGHT() {
        Layout.Alignment[] alignmentArr = alignments;
        return alignmentArr.length >= 5 ? alignmentArr[4] : Layout.Alignment.ALIGN_OPPOSITE;
    }

    public static Layout.Alignment ALIGN_LEFT() {
        Layout.Alignment[] alignmentArr = alignments;
        return alignmentArr.length >= 5 ? alignmentArr[3] : Layout.Alignment.ALIGN_NORMAL;
    }

    public static void init() {
        Class cls;
        if (!initialized) {
            try {
                if (Build.VERSION.SDK_INT >= 18) {
                    cls = TextDirectionHeuristic.class;
                    sTextDirection = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                } else {
                    ClassLoader loader = StaticLayoutEx.class.getClassLoader();
                    Class<?> textDirClass = loader.loadClass(TEXT_DIR_CLASS);
                    Class<?> textDirsClass = loader.loadClass(TEXT_DIRS_CLASS);
                    sTextDirection = textDirsClass.getField(TEXT_DIR_FIRSTSTRONG_LTR).get(textDirsClass);
                    cls = textDirClass;
                }
                Class[] clsArr = {CharSequence.class, Integer.TYPE, Integer.TYPE, TextPaint.class, Integer.TYPE, Layout.Alignment.class, cls, Float.TYPE, Float.TYPE, Boolean.TYPE, TextUtils.TruncateAt.class, Integer.TYPE, Integer.TYPE};
                Constructor<StaticLayout> declaredConstructor = StaticLayout.class.getDeclaredConstructor(clsArr);
                sConstructor = declaredConstructor;
                declaredConstructor.setAccessible(true);
                sConstructorArgs = new Object[clsArr.length];
                initialized = true;
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public static StaticLayout createStaticLayout2(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines) {
        int i = ellipsisWidth;
        if (Build.VERSION.SDK_INT >= 23) {
            return StaticLayout.Builder.obtain(source, 0, source.length(), paint, i).setAlignment(align).setLineSpacing(spacingadd, spacingmult).setIncludePad(includepad).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(i).setMaxLines(maxLines).setBreakStrategy(1).setHyphenationFrequency(0).build();
        }
        CharSequence charSequence = source;
        TextPaint textPaint = paint;
        Layout.Alignment alignment = align;
        float f = spacingmult;
        float f2 = spacingadd;
        boolean z = includepad;
        int i2 = maxLines;
        return createStaticLayout(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad, ellipsize, ellipsisWidth, maxLines, true);
    }

    public static StaticLayout createStaticLayout(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines) {
        return createStaticLayout(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad, ellipsize, ellipsisWidth, maxLines, true);
    }

    public static StaticLayout createStaticLayout(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines, boolean canContainUrl) {
        return createStaticLayout(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad, ellipsize, ellipsisWidth, maxLines, canContainUrl);
    }

    public static StaticLayout createStaticLayoutMiddle(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines, boolean canContainUrl) {
        return createStaticLayoutMiddle(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad, ellipsize, ellipsisWidth, maxLines, canContainUrl);
    }

    public static StaticLayout createStaticLayoutMiddle(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerWidth, Layout.Alignment align, float spacingMult, float spacingAdd, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines, boolean canContainUrl) {
        TextUtils.TruncateAt truncateAt;
        int i;
        int i2;
        StaticLayout layout;
        int off;
        int off2;
        CharSequence charSequence = source;
        TextPaint textPaint = paint;
        int i3 = outerWidth;
        Layout.Alignment alignment = align;
        float f = spacingMult;
        float f2 = spacingAdd;
        boolean z = includePad;
        TextUtils.TruncateAt truncateAt2 = ellipsize;
        int i4 = ellipsisWidth;
        int i5 = 1;
        if (maxLines == 1) {
            try {
                CharSequence text = TextUtils.ellipsize(charSequence, textPaint, (float) i4, truncateAt2);
                return new StaticLayout(text, 0, text.length(), paint, outerWidth, align, spacingMult, spacingAdd, includePad);
            } catch (Exception e) {
                e = e;
                int i6 = maxLines;
                int i7 = i4;
                TextUtils.TruncateAt truncateAt3 = truncateAt2;
                boolean z2 = z;
                TextPaint textPaint2 = textPaint;
                FileLog.e((Throwable) e);
                return null;
            }
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                i2 = maxLines;
                try {
                    layout = StaticLayout.Builder.obtain(charSequence, 0, source.length(), textPaint, i3).setAlignment(alignment).setLineSpacing(f2, f).setIncludePad(z).setEllipsize(truncateAt2).setEllipsizedWidth(i4).setMaxLines(i2).setBreakStrategy(1).setHyphenationFrequency(0).build();
                    i = i4;
                    truncateAt = truncateAt2;
                } catch (Exception e2) {
                    e = e2;
                    int i72 = i4;
                    TextUtils.TruncateAt truncateAt32 = truncateAt2;
                    boolean z22 = z;
                    TextPaint textPaint22 = textPaint;
                    FileLog.e((Throwable) e);
                    return null;
                }
            } else {
                i2 = maxLines;
                i = i4;
                truncateAt = truncateAt2;
                try {
                    StaticLayout staticLayout = new StaticLayout(source, paint, outerWidth, align, spacingMult, spacingAdd, includePad);
                    layout = staticLayout;
                } catch (Exception e3) {
                    e = e3;
                    TextPaint textPaint3 = paint;
                    boolean z3 = includePad;
                    FileLog.e((Throwable) e);
                    return null;
                }
            }
            if (layout.getLineCount() <= i2) {
                return layout;
            }
            float left = layout.getLineLeft(i2 - 1);
            float lineWidth = layout.getLineWidth(i2 - 1);
            if (left != 0.0f) {
                off = layout.getOffsetForHorizontal(i2 - 1, left);
            } else {
                off = layout.getOffsetForHorizontal(i2 - 1, lineWidth);
            }
            if (lineWidth < ((float) (i - AndroidUtilities.dp(10.0f)))) {
                off2 = off + 3;
            } else {
                off2 = off;
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(charSequence.subSequence(0, Math.max(0, off2 - 3)));
            stringBuilder.append("…");
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                } catch (Exception e4) {
                    e = e4;
                    TextPaint textPaint4 = paint;
                    int i8 = outerWidth;
                    Layout.Alignment alignment2 = align;
                    float f3 = spacingMult;
                    float f4 = spacingAdd;
                    boolean z32 = includePad;
                    FileLog.e((Throwable) e);
                    return null;
                }
                try {
                    try {
                    } catch (Exception e5) {
                        e = e5;
                        float f32 = spacingMult;
                        float f42 = spacingAdd;
                        boolean z322 = includePad;
                        FileLog.e((Throwable) e);
                        return null;
                    }
                } catch (Exception e6) {
                    e = e6;
                    Layout.Alignment alignment22 = align;
                    float f322 = spacingMult;
                    float f422 = spacingAdd;
                    boolean z3222 = includePad;
                    FileLog.e((Throwable) e);
                    return null;
                }
                try {
                    try {
                        StaticLayout.Builder maxLines2 = StaticLayout.Builder.obtain(stringBuilder, 0, stringBuilder.length(), paint, outerWidth).setAlignment(align).setLineSpacing(spacingAdd, spacingMult).setIncludePad(includePad).setEllipsize(truncateAt).setEllipsizedWidth(i).setMaxLines(i2);
                        if (!canContainUrl) {
                            i5 = 0;
                        }
                        return maxLines2.setBreakStrategy(i5).setHyphenationFrequency(0).build();
                    } catch (Exception e7) {
                        e = e7;
                        FileLog.e((Throwable) e);
                        return null;
                    }
                } catch (Exception e8) {
                    e = e8;
                    boolean z32222 = includePad;
                    FileLog.e((Throwable) e);
                    return null;
                }
            } else {
                TextPaint textPaint5 = paint;
                int i9 = outerWidth;
                Layout.Alignment alignment3 = align;
                float f5 = spacingMult;
                float f6 = spacingAdd;
                boolean z4 = includePad;
                SpannableStringBuilder spannableStringBuilder = stringBuilder;
                float f7 = lineWidth;
                float f8 = left;
                return new StaticLayout(stringBuilder, paint, outerWidth, align, spacingMult, spacingAdd, includePad);
            }
        }
    }

    public static StaticLayout createStaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerWidth, Layout.Alignment align, float spacingMult, float spacingAdd, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines, boolean canContainUrl) {
        int i;
        int i2;
        boolean z;
        StaticLayout layout;
        int off;
        int off2;
        CharSequence charSequence = source;
        TextPaint textPaint = paint;
        int i3 = outerWidth;
        Layout.Alignment alignment = align;
        float f = spacingMult;
        float f2 = spacingAdd;
        boolean z2 = includePad;
        int i4 = ellipsisWidth;
        int i5 = maxLines;
        if (i5 == 1) {
            try {
                CharSequence text = TextUtils.ellipsize(charSequence, textPaint, (float) i4, TextUtils.TruncateAt.END);
                return new StaticLayout(text, 0, text.length(), paint, outerWidth, align, spacingMult, spacingAdd, includePad);
            } catch (Exception e) {
                e = e;
                int i6 = i5;
                int i7 = i4;
                boolean z3 = z2;
                TextPaint textPaint2 = textPaint;
                FileLog.e((Throwable) e);
                return null;
            }
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                layout = StaticLayout.Builder.obtain(charSequence, 0, source.length(), textPaint, i3).setAlignment(alignment).setLineSpacing(f2, f).setIncludePad(z2).setEllipsize((TextUtils.TruncateAt) null).setEllipsizedWidth(i4).setMaxLines(i5).setBreakStrategy(1).setHyphenationFrequency(0).build();
                i2 = i5;
                i = i4;
                z = z2;
            } else {
                i2 = i5;
                i = i4;
                z = z2;
                try {
                    StaticLayout staticLayout = new StaticLayout(source, paint, outerWidth, align, spacingMult, spacingAdd, includePad);
                    layout = staticLayout;
                } catch (Exception e2) {
                    e = e2;
                    TextPaint textPaint3 = paint;
                    FileLog.e((Throwable) e);
                    return null;
                }
            }
            if (layout.getLineCount() <= i2) {
                return layout;
            }
            float left = layout.getLineLeft(i2 - 1);
            float lineWidth = layout.getLineWidth(i2 - 1);
            if (left != 0.0f) {
                off = layout.getOffsetForHorizontal(i2 - 1, left);
            } else {
                off = layout.getOffsetForHorizontal(i2 - 1, lineWidth);
            }
            if (lineWidth < ((float) (i - AndroidUtilities.dp(10.0f)))) {
                off2 = off + 3;
            } else {
                off2 = off;
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(charSequence.subSequence(0, Math.max(0, off2 - 3)));
            stringBuilder.append("…");
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    try {
                    } catch (Exception e3) {
                        e = e3;
                        Layout.Alignment alignment2 = align;
                        float f3 = spacingMult;
                        float f4 = spacingAdd;
                        FileLog.e((Throwable) e);
                        return null;
                    }
                } catch (Exception e4) {
                    e = e4;
                    TextPaint textPaint4 = paint;
                    int i8 = outerWidth;
                    Layout.Alignment alignment22 = align;
                    float f32 = spacingMult;
                    float f42 = spacingAdd;
                    FileLog.e((Throwable) e);
                    return null;
                }
                try {
                } catch (Exception e5) {
                    e = e5;
                    float f322 = spacingMult;
                    float f422 = spacingAdd;
                    FileLog.e((Throwable) e);
                    return null;
                }
                try {
                    return StaticLayout.Builder.obtain(stringBuilder, 0, stringBuilder.length(), paint, outerWidth).setAlignment(align).setLineSpacing(spacingAdd, spacingMult).setIncludePad(z).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(i).setMaxLines(i2).setBreakStrategy(canContainUrl ? 1 : 0).setHyphenationFrequency(0).build();
                } catch (Exception e6) {
                    e = e6;
                    FileLog.e((Throwable) e);
                    return null;
                }
            } else {
                TextPaint textPaint5 = paint;
                int i9 = outerWidth;
                Layout.Alignment alignment3 = align;
                float f5 = spacingMult;
                float f6 = spacingAdd;
                SpannableStringBuilder spannableStringBuilder = stringBuilder;
                float f7 = lineWidth;
                float f8 = left;
                return new StaticLayout(stringBuilder, paint, outerWidth, align, spacingMult, spacingAdd, includePad);
            }
        }
    }
}

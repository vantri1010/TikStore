package im.bclpbkiauv.messenger;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import im.bclpbkiauv.messenger.Emoji;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Emoji {
    private static final int MAX_RECENT_EMOJI_COUNT = 48;
    /* access modifiers changed from: private */
    public static int bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 34.0f);
    private static final int[][] cols = {new int[]{16, 16, 16, 16}, new int[]{6, 6, 6, 6}, new int[]{5, 5, 5, 5}, new int[]{7, 7, 7, 7}, new int[]{5, 5, 5, 5}, new int[]{7, 7, 7, 7}, new int[]{8, 8, 8, 8}, new int[]{8, 8, 8, 8}};
    /* access modifiers changed from: private */
    public static int drawImgSize = AndroidUtilities.dp(20.0f);
    /* access modifiers changed from: private */
    public static Bitmap[][] emojiBmp = ((Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{8, 4}));
    public static HashMap<String, String> emojiColor = new HashMap<>();
    public static HashMap<String, Integer> emojiUseHistory = new HashMap<>();
    private static boolean inited = false;
    /* access modifiers changed from: private */
    public static boolean[][] loadingEmoji = ((boolean[][]) Array.newInstance(boolean.class, new int[]{8, 4}));
    /* access modifiers changed from: private */
    public static Paint placeholderPaint = null;
    public static ArrayList<String> recentEmoji = new ArrayList<>();
    private static boolean recentEmojiLoaded = false;
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap<>();
    private static final int splitCount = 4;

    static {
        int emojiFullSize;
        int add = 2;
        if (AndroidUtilities.density <= 1.0f) {
            emojiFullSize = 33;
            add = 1;
        } else if (AndroidUtilities.density <= 1.5f) {
            emojiFullSize = 66;
        } else if (AndroidUtilities.density <= 2.0f) {
            emojiFullSize = 66;
        } else {
            emojiFullSize = 66;
        }
        for (int j = 0; j < EmojiData.data.length; j++) {
            int count2 = (int) Math.ceil((double) (((float) EmojiData.data[j].length) / 4.0f));
            for (int i = 0; i < EmojiData.data[j].length; i++) {
                int page = i / count2;
                int position = i - (page * count2);
                int[][] iArr = cols;
                int row = position % iArr[j][page];
                int col = position / iArr[j][page];
                rects.put(EmojiData.data[j][i], new DrawableInfo(new Rect((row * emojiFullSize) + (row * add), (col * emojiFullSize) + (col * add), ((row + 1) * emojiFullSize) + (row * add), ((col + 1) * emojiFullSize) + (col * add)), (byte) j, (byte) page, i));
            }
        }
        Paint paint = new Paint();
        placeholderPaint = paint;
        paint.setColor(0);
    }

    /* access modifiers changed from: private */
    public static void loadEmoji(int page, int page2) {
        float scale;
        int imageResize = 1;
        try {
            if (AndroidUtilities.density <= 1.0f) {
                scale = 2.0f;
                imageResize = 2;
            } else if (AndroidUtilities.density <= 1.5f) {
                scale = 2.0f;
            } else if (AndroidUtilities.density <= 2.0f) {
                scale = 2.0f;
            } else {
                scale = 2.0f;
            }
            for (int a = 12; a < 14; a++) {
                File imageFile = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[]{Integer.valueOf(a), Float.valueOf(scale), Integer.valueOf(page)}));
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        } catch (Throwable x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error loading emoji", x);
                return;
            }
            return;
        }
        Bitmap bitmap = null;
        try {
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            InputStream is = assets.open("emoji/" + String.format(Locale.US, "v14_emoji%.01fx_%d_%d.png", new Object[]{Float.valueOf(scale), Integer.valueOf(page), Integer.valueOf(page2)}));
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = imageResize;
            int i = Build.VERSION.SDK_INT;
            bitmap = BitmapFactory.decodeStream(is, (Rect) null, opts);
            is.close();
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        AndroidUtilities.runOnUIThread(new Runnable(page, page2, bitmap) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ int f$1;
            private final /* synthetic */ Bitmap f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                Emoji.lambda$loadEmoji$0(this.f$0, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$loadEmoji$0(int page, int page2, Bitmap finalBitmap) {
        emojiBmp[page][page2] = finalBitmap;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiDidLoad, new Object[0]);
    }

    public static void invalidateAll(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) view;
            for (int i = 0; i < g.getChildCount(); i++) {
                invalidateAll(g.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            view.invalidate();
        }
    }

    public static String fixEmoji(String emoji) {
        int length = emoji.length();
        int a = 0;
        while (a < length) {
            char ch = emoji.charAt(a);
            if (ch < 55356 || ch > 55358) {
                if (ch == 8419) {
                    return emoji;
                }
                if (ch >= 8252 && ch <= 12953 && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(ch))) {
                    emoji = emoji.substring(0, a + 1) + "️" + emoji.substring(a + 1);
                    length++;
                    a++;
                }
            } else if (ch != 55356 || a >= length - 1) {
                a++;
            } else {
                char ch2 = emoji.charAt(a + 1);
                if (ch2 == 56879 || ch2 == 56324 || ch2 == 56858 || ch2 == 56703) {
                    emoji = emoji.substring(0, a + 2) + "️" + emoji.substring(a + 2);
                    length++;
                    a += 2;
                } else {
                    a++;
                }
            }
            a++;
        }
        return emoji;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence code) {
        CharSequence newCode;
        DrawableInfo info = rects.get(code);
        if (info == null && (newCode = EmojiData.emojiAliasMap.get(code)) != null) {
            info = rects.get(newCode);
        }
        if (info != null) {
            EmojiDrawable ed = new EmojiDrawable(info);
            int i = drawImgSize;
            ed.setBounds(0, 0, i, i);
            return ed;
        } else if (!BuildVars.LOGS_ENABLED) {
            return null;
        } else {
            FileLog.d("No drawable for emoji " + code);
            return null;
        }
    }

    public static boolean isValidEmoji(CharSequence code) {
        CharSequence newCode;
        DrawableInfo info = rects.get(code);
        if (info == null && (newCode = EmojiData.emojiAliasMap.get(code)) != null) {
            info = rects.get(newCode);
        }
        return info != null;
    }

    public static Drawable getEmojiBigDrawable(String code) {
        CharSequence newCode;
        EmojiDrawable ed = getEmojiDrawable(code);
        if (ed == null && (newCode = EmojiData.emojiAliasMap.get(code)) != null) {
            ed = getEmojiDrawable(newCode);
        }
        if (ed == null) {
            return null;
        }
        int i = bigImgSize;
        ed.setBounds(0, 0, i, i);
        boolean unused = ed.fullSize = true;
        return ed;
    }

    public static class EmojiDrawable extends Drawable {
        private static Paint paint = new Paint(2);
        private static Rect rect = new Rect();
        /* access modifiers changed from: private */
        public boolean fullSize = false;
        private DrawableInfo info;

        public EmojiDrawable(DrawableInfo i) {
            this.info = i;
        }

        public DrawableInfo getDrawableInfo() {
            return this.info;
        }

        public Rect getDrawRect() {
            Rect original = getBounds();
            int cX = original.centerX();
            int cY = original.centerY();
            rect.left = cX - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.right = ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2) + cX;
            rect.top = cY - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.bottom = ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2) + cY;
            return rect;
        }

        public void draw(Canvas canvas) {
            Rect b;
            if (Emoji.emojiBmp[this.info.page][this.info.page2] != null) {
                if (this.fullSize) {
                    b = getDrawRect();
                } else {
                    b = getBounds();
                }
                canvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], this.info.rect, b, paint);
            } else if (!Emoji.loadingEmoji[this.info.page][this.info.page2]) {
                Emoji.loadingEmoji[this.info.page][this.info.page2] = true;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        Emoji.EmojiDrawable.this.lambda$draw$0$Emoji$EmojiDrawable();
                    }
                });
                canvas.drawRect(getBounds(), Emoji.placeholderPaint);
            }
        }

        public /* synthetic */ void lambda$draw$0$Emoji$EmojiDrawable() {
            Emoji.loadEmoji(this.info.page, this.info.page2);
            Emoji.loadingEmoji[this.info.page][this.info.page2] = false;
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter cf) {
        }
    }

    private static class DrawableInfo {
        public int emojiIndex;
        public byte page;
        public byte page2;
        public Rect rect;

        public DrawableInfo(Rect r, byte p, byte p2, int index) {
            this.rect = r;
            this.page = p;
            this.page2 = p2;
            this.emojiIndex = index;
        }
    }

    private static boolean inArray(char c, char[] a) {
        for (char cc : a) {
            if (cc == c) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew) {
        return replaceEmoji(cs, fontMetrics, size, createNew, (int[]) null);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew, int[] emojiOnly) {
        Spannable s;
        int length;
        long buf;
        int[] emojiOnly2;
        char c2;
        CharSequence charSequence = cs;
        if (SharedConfig.useSystemEmoji || charSequence == null) {
            int i = size;
        } else if (cs.length() == 0) {
            int i2 = size;
        } else {
            if (createNew || !(charSequence instanceof Spannable)) {
                s = Spannable.Factory.getInstance().newSpannable(cs.toString());
            } else {
                s = (Spannable) charSequence;
            }
            StringBuilder emojiCode = new StringBuilder(16);
            new StringBuilder(2);
            int length2 = cs.length();
            boolean doneEmoji = false;
            int startLength = 0;
            int[] emojiOnly3 = emojiOnly;
            int emojiCount = 0;
            int i3 = 0;
            int previousGoodIndex = 0;
            int startIndex = -1;
            long buf2 = 0;
            while (true) {
                if (i3 >= length2) {
                    int[] iArr = emojiOnly3;
                    long j = buf2;
                    int i4 = size;
                    break;
                }
                try {
                    char c = charSequence.charAt(i3);
                    if ((c < 55356 || c > 55358) && (buf2 == 0 || (buf2 & -4294967296L) != 0 || (buf2 & 65535) != 55356 || c < 56806 || c > 56831)) {
                        length = length2;
                        try {
                            if (emojiCode.length() > 0 && (c == 9792 || c == 9794 || c == 9877)) {
                                try {
                                    emojiCode.append(c);
                                    startLength++;
                                    buf2 = 0;
                                    doneEmoji = true;
                                } catch (Exception e) {
                                    e = e;
                                    long j2 = buf2;
                                    int i5 = length;
                                    int i6 = size;
                                    FileLog.e((Throwable) e);
                                    return charSequence;
                                }
                            } else if (buf2 > 0 && (61440 & c) == 53248) {
                                emojiCode.append(c);
                                startLength++;
                                buf2 = 0;
                                doneEmoji = true;
                            } else if (c != 8419) {
                                if (c == 169 || c == 174 || (c >= 8252 && c <= 12953)) {
                                    if (EmojiData.dataCharsMap.containsKey(Character.valueOf(c))) {
                                        if (startIndex == -1) {
                                            startIndex = i3;
                                        }
                                        startLength++;
                                        emojiCode.append(c);
                                        doneEmoji = true;
                                    }
                                }
                                if (startIndex != -1) {
                                    emojiCode.setLength(0);
                                    startIndex = -1;
                                    startLength = 0;
                                    doneEmoji = false;
                                } else if (!(c == 65039 || emojiOnly3 == null)) {
                                    emojiOnly3[0] = 0;
                                    emojiOnly3 = null;
                                }
                            } else if (i3 > 0 && (((c2 = charSequence.charAt(previousGoodIndex)) >= '0' && c2 <= '9') || c2 == '#' || c2 == '*')) {
                                startIndex = previousGoodIndex;
                                startLength = (i3 - previousGoodIndex) + 1;
                                emojiCode.append(c2);
                                emojiCode.append(c);
                                doneEmoji = true;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            int[] iArr2 = emojiOnly3;
                            long j3 = buf2;
                            int i7 = length;
                            int i8 = size;
                            FileLog.e((Throwable) e);
                            return charSequence;
                        }
                    } else {
                        if (startIndex == -1) {
                            startIndex = i3;
                        }
                        try {
                            emojiCode.append(c);
                            startLength++;
                            length = length2;
                            buf2 = (buf2 << 16) | ((long) c);
                        } catch (Exception e3) {
                            e = e3;
                            long j4 = buf2;
                            int i9 = size;
                            FileLog.e((Throwable) e);
                            return charSequence;
                        }
                    }
                    if (doneEmoji) {
                        length2 = length;
                        if (i3 + 2 < length2) {
                            try {
                                char next = charSequence.charAt(i3 + 1);
                                buf = buf2;
                                if (next == 55356) {
                                    try {
                                        char next2 = charSequence.charAt(i3 + 2);
                                        if (next2 >= 57339 && next2 <= 57343) {
                                            emojiCode.append(charSequence.subSequence(i3 + 1, i3 + 3));
                                            startLength += 2;
                                            i3 += 2;
                                        }
                                    } catch (Exception e4) {
                                        e = e4;
                                        int i10 = size;
                                        FileLog.e((Throwable) e);
                                        return charSequence;
                                    }
                                } else if (emojiCode.length() >= 2 && emojiCode.charAt(0) == 55356 && emojiCode.charAt(1) == 57332 && next == 56128) {
                                    int i11 = i3 + 1;
                                    do {
                                        emojiCode.append(charSequence.subSequence(i11, i11 + 2));
                                        startLength += 2;
                                        i11 += 2;
                                        if (i11 >= cs.length() || charSequence.charAt(i11) != 56128) {
                                            i3 = i11 - 1;
                                        }
                                        emojiCode.append(charSequence.subSequence(i11, i11 + 2));
                                        startLength += 2;
                                        i11 += 2;
                                        break;
                                    } while (charSequence.charAt(i11) != 56128);
                                    i3 = i11 - 1;
                                }
                            } catch (Exception e5) {
                                e = e5;
                                long j5 = buf2;
                                int i12 = size;
                                FileLog.e((Throwable) e);
                                return charSequence;
                            }
                        } else {
                            buf = buf2;
                        }
                    } else {
                        buf = buf2;
                        length2 = length;
                    }
                    previousGoodIndex = i3;
                    char prevCh = c;
                    for (int a = 0; a < 3; a++) {
                        if (i3 + 1 < length2) {
                            c = charSequence.charAt(i3 + 1);
                            if (a != 1) {
                                if (startIndex == -1 && prevCh != '*') {
                                    if (prevCh >= '1') {
                                        if (prevCh <= '9') {
                                        }
                                    }
                                }
                                if (c >= 65024) {
                                    if (c <= 65039) {
                                        i3++;
                                        startLength++;
                                    }
                                }
                            } else if (c == 8205 && emojiCode.length() > 0) {
                                emojiCode.append(c);
                                i3++;
                                startLength++;
                                doneEmoji = false;
                            }
                        }
                    }
                    if (!doneEmoji || i3 + 2 >= length2 || charSequence.charAt(i3 + 1) != 55356) {
                    } else {
                        char next3 = charSequence.charAt(i3 + 2);
                        if (next3 < 57339 || next3 > 57343) {
                        } else {
                            char c3 = prevCh;
                            emojiCode.append(charSequence.subSequence(i3 + 1, i3 + 3));
                            startLength += 2;
                            i3 += 2;
                        }
                    }
                    if (doneEmoji) {
                        if (emojiOnly3 != null) {
                            emojiOnly3[0] = emojiOnly3[0] + 1;
                        }
                        try {
                            CharSequence code = emojiCode.subSequence(0, emojiCode.length());
                            EmojiDrawable drawable = getEmojiDrawable(code);
                            if (drawable != null) {
                                emojiOnly2 = emojiOnly3;
                                CharSequence charSequence2 = code;
                                char c4 = c;
                                try {
                                    s.setSpan(new EmojiSpan(drawable, 0, size, fontMetrics), startIndex, startIndex + startLength, 33);
                                    emojiCount++;
                                } catch (Exception e6) {
                                    e = e6;
                                    int[] iArr3 = emojiOnly2;
                                    FileLog.e((Throwable) e);
                                    return charSequence;
                                }
                            } else {
                                emojiOnly2 = emojiOnly3;
                                CharSequence charSequence3 = code;
                                char c5 = c;
                                int i13 = size;
                            }
                            startLength = 0;
                            startIndex = -1;
                            emojiCode.setLength(0);
                            doneEmoji = false;
                        } catch (Exception e7) {
                            e = e7;
                            int i14 = size;
                            int[] iArr4 = emojiOnly3;
                            FileLog.e((Throwable) e);
                            return charSequence;
                        }
                    } else {
                        int i15 = size;
                        emojiOnly2 = emojiOnly3;
                        char c6 = c;
                    }
                    if (Build.VERSION.SDK_INT < 23 && emojiCount >= 50) {
                        int[] iArr5 = emojiOnly2;
                        break;
                    }
                    i3++;
                    emojiOnly3 = emojiOnly2;
                    buf2 = buf;
                } catch (Exception e8) {
                    e = e8;
                    int[] iArr6 = emojiOnly3;
                    long j6 = buf2;
                    int i16 = size;
                    FileLog.e((Throwable) e);
                    return charSequence;
                }
            }
            return s;
        }
        return charSequence;
    }

    public static class EmojiSpan extends ImageSpan {
        private Paint.FontMetricsInt fontMetrics;
        private int size = AndroidUtilities.dp(20.0f);

        public EmojiSpan(EmojiDrawable d, int verticalAlignment, int s, Paint.FontMetricsInt original) {
            super(d, verticalAlignment);
            this.fontMetrics = original;
            if (original != null) {
                int abs = Math.abs(original.descent) + Math.abs(this.fontMetrics.ascent);
                this.size = abs;
                if (abs == 0) {
                    this.size = AndroidUtilities.dp(20.0f);
                }
            }
        }

        public void replaceFontMetrics(Paint.FontMetricsInt newMetrics, int newSize) {
            this.fontMetrics = newMetrics;
            this.size = newSize;
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (fm == null) {
                fm = new Paint.FontMetricsInt();
            }
            Paint.FontMetricsInt fontMetricsInt = this.fontMetrics;
            if (fontMetricsInt == null) {
                int sz = super.getSize(paint, text, start, end, fm);
                int offset = AndroidUtilities.dp(8.0f);
                int w = AndroidUtilities.dp(10.0f);
                fm.top = (-w) - offset;
                fm.bottom = w - offset;
                fm.ascent = (-w) - offset;
                fm.leading = 0;
                fm.descent = w - offset;
                return sz;
            }
            if (fm != null) {
                fm.ascent = fontMetricsInt.ascent;
                fm.descent = this.fontMetrics.descent;
                fm.top = this.fontMetrics.top;
                fm.bottom = this.fontMetrics.bottom;
            }
            if (getDrawable() != null) {
                Drawable drawable = getDrawable();
                int i = this.size;
                drawable.setBounds(0, 0, i, i);
            }
            return this.size;
        }
    }

    public static void addRecentEmoji(String code) {
        Integer count = emojiUseHistory.get(code);
        if (count == null) {
            count = 0;
        }
        if (count.intValue() == 0 && emojiUseHistory.size() >= 48) {
            ArrayList<String> arrayList = recentEmoji;
            emojiUseHistory.remove(arrayList.get(arrayList.size() - 1));
            ArrayList<String> arrayList2 = recentEmoji;
            arrayList2.set(arrayList2.size() - 1, code);
        }
        HashMap<String, Integer> hashMap = emojiUseHistory;
        Integer valueOf = Integer.valueOf(count.intValue() + 1);
        Integer count2 = valueOf;
        hashMap.put(code, valueOf);
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Map.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            recentEmoji.add(entry.getKey());
        }
        Collections.sort(recentEmoji, $$Lambda$Emoji$2_RQEXBRVrRBxYS47ytaCojPnNc.INSTANCE);
        while (recentEmoji.size() > 48) {
            ArrayList<String> arrayList = recentEmoji;
            arrayList.remove(arrayList.size() - 1);
        }
    }

    static /* synthetic */ int lambda$sortEmoji$1(String lhs, String rhs) {
        Integer count1 = emojiUseHistory.get(lhs);
        Integer count2 = emojiUseHistory.get(rhs);
        if (count1 == null) {
            count1 = 0;
        }
        if (count2 == null) {
            count2 = 0;
        }
        if (count1.intValue() > count2.intValue()) {
            return -1;
        }
        if (count1.intValue() < count2.intValue()) {
            return 1;
        }
        return 0;
    }

    public static void saveRecentEmoji() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    public static void clearRecentEmoji() {
        MessagesController.getGlobalEmojiSettings().edit().putBoolean("filled_default", true).commit();
        emojiUseHistory.clear();
        recentEmoji.clear();
        saveRecentEmoji();
    }

    public static void loadRecentEmoji() {
        String str;
        String str2;
        StringBuilder string;
        String[] args;
        if (!recentEmojiLoaded) {
            recentEmojiLoaded = true;
            SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
            char c = 0;
            try {
                emojiUseHistory.clear();
                if (preferences.contains("emojis")) {
                    String str3 = preferences.getString("emojis", "");
                    if (str3 == null || str3.length() <= 0) {
                        str = str3;
                    } else {
                        String[] args2 = str3.split(",");
                        int length = args2.length;
                        int i = 0;
                        while (i < length) {
                            String[] args22 = args2[i].split("=");
                            long value = Utilities.parseLong(args22[c]).longValue();
                            StringBuilder string2 = new StringBuilder();
                            int a = 0;
                            while (true) {
                                if (a >= 4) {
                                    str2 = str3;
                                    string = string2;
                                    args = args2;
                                    break;
                                }
                                str2 = str3;
                                string = string2;
                                args = args2;
                                string.insert(0, (char) ((int) value));
                                value >>= 16;
                                if (value == 0) {
                                    break;
                                }
                                a++;
                                args2 = args;
                                string2 = string;
                                str3 = str2;
                            }
                            if (string.length() > 0) {
                                emojiUseHistory.put(string.toString(), Utilities.parseInt(args22[1]));
                            }
                            i++;
                            args2 = args;
                            str3 = str2;
                            c = 0;
                        }
                        str = str3;
                        String[] strArr = args2;
                    }
                    preferences.edit().remove("emojis").commit();
                    saveRecentEmoji();
                    String str4 = str;
                } else {
                    String str5 = preferences.getString("emojis2", "");
                    if (str5 != null && str5.length() > 0) {
                        for (String arg : str5.split(",")) {
                            String[] args23 = arg.split("=");
                            emojiUseHistory.put(args23[0], Utilities.parseInt(args23[1]));
                        }
                    }
                }
                if (emojiUseHistory.isEmpty() && !preferences.getBoolean("filled_default", false)) {
                    String[] newRecent = {"😂", "😘", "❤", "😍", "😊", "😁", "👍", "☺", "😔", "😄", "😭", "💋", "😒", "😳", "😜", "🙈", "😉", "😃", "😢", "😝", "😱", "😡", "😏", "😞", "😅", "😚", "🙊", "😌", "😀", "😋", "😆", "👌", "😐", "😕"};
                    for (int i2 = 0; i2 < newRecent.length; i2++) {
                        emojiUseHistory.put(newRecent[i2], Integer.valueOf(newRecent.length - i2));
                    }
                    preferences.edit().putBoolean("filled_default", true).commit();
                    saveRecentEmoji();
                }
                sortEmoji();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                String str6 = preferences.getString(TtmlNode.ATTR_TTS_COLOR, "");
                if (str6 != null && str6.length() > 0) {
                    String[] args3 = str6.split(",");
                    for (String arg2 : args3) {
                        String[] args24 = arg2.split("=");
                        emojiColor.put(args24[0], args24[1]);
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public static void saveEmojiColors() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString(TtmlNode.ATTR_TTS_COLOR, stringBuilder.toString()).commit();
    }
}

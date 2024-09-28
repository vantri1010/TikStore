package im.bclpbkiauv.ui.cells;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ThemeSetUrlActivity;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.ThemesHorizontalListCell;
import im.bclpbkiauv.ui.components.BackgroundGradientDrawable;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadioButton;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ThemesHorizontalListCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static byte[] bytes = new byte[1024];
    private ThemesListAdapter adapter;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> darkThemes;
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> defaultThemes;
    private boolean drawDivider;
    private LinearLayoutManager horizontalLayoutManager;
    /* access modifiers changed from: private */
    public HashMap<String, Theme.ThemeInfo> loadingThemes = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<Theme.ThemeInfo, String> loadingWallpapers = new HashMap<>();
    private Theme.ThemeInfo prevThemeInfo;

    private class ThemesListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        ThemesListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new InnerThemeView(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<Theme.ThemeInfo> arrayList;
            InnerThemeView view = (InnerThemeView) holder.itemView;
            int p = position;
            if (position < ThemesHorizontalListCell.this.defaultThemes.size()) {
                arrayList = ThemesHorizontalListCell.this.defaultThemes;
            } else {
                arrayList = ThemesHorizontalListCell.this.darkThemes;
                p -= ThemesHorizontalListCell.this.defaultThemes.size();
            }
            Theme.ThemeInfo themeInfo = arrayList.get(p);
            boolean z = true;
            boolean z2 = position == getItemCount() - 1;
            if (position != 0) {
                z = false;
            }
            view.setTheme(themeInfo, z2, z);
        }

        public int getItemCount() {
            return ThemesHorizontalListCell.this.defaultThemes.size() + ThemesHorizontalListCell.this.darkThemes.size();
        }
    }

    private class InnerThemeView extends FrameLayout {
        private ObjectAnimator accentAnimator;
        private int accentColor;
        private boolean accentColorChanged;
        private float accentState;
        private Drawable backgroundDrawable;
        private Paint bitmapPaint = new Paint(3);
        private BitmapShader bitmapShader;
        private RadioButton button;
        private final ArgbEvaluator evaluator = new ArgbEvaluator();
        private boolean hasWhiteBackground;
        private Drawable inDrawable;
        private boolean isFirst;
        private boolean isLast;
        private long lastDrawTime;
        private int loadingColor;
        private Drawable loadingDrawable;
        private int oldAccentColor;
        private Drawable optionsDrawable;
        private Drawable outDrawable;
        private Paint paint = new Paint(1);
        private float placeholderAlpha;
        private boolean pressed;
        private RectF rect = new RectF();
        private Matrix shaderMatrix = new Matrix();
        private TextPaint textPaint = new TextPaint(1);
        /* access modifiers changed from: private */
        public Theme.ThemeInfo themeInfo;

        public InnerThemeView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.inDrawable = context.getResources().getDrawable(R.drawable.minibubble_in).mutate();
            this.outDrawable = context.getResources().getDrawable(R.drawable.minibubble_out).mutate();
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            AnonymousClass1 r0 = new RadioButton(context, ThemesHorizontalListCell.this) {
                public void invalidate() {
                    super.invalidate();
                }
            };
            this.button = r0;
            r0.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22.0f, 22.0f, 51, 27.0f, 75.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i = 22;
            int i2 = (this.isLast ? 22 : 15) + 76;
            if (!this.isFirst) {
                i = 0;
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i2 + i)), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), 1073741824));
        }

        public boolean onTouchEvent(MotionEvent event) {
            Theme.ThemeInfo themeInfo2;
            if (this.optionsDrawable == null || (themeInfo2 = this.themeInfo) == null || ((themeInfo2.info != null && !this.themeInfo.themeLoaded) || ThemesHorizontalListCell.this.currentType != 0)) {
                return super.onTouchEvent(event);
            }
            int action = event.getAction();
            if (action == 0 || action == 1) {
                float x = event.getX();
                float y = event.getY();
                if (x > this.rect.centerX() && y < this.rect.centerY() - ((float) AndroidUtilities.dp(10.0f))) {
                    if (action == 0) {
                        this.pressed = true;
                    } else {
                        performHapticFeedback(3);
                        ThemesHorizontalListCell.this.showOptionsForTheme(this.themeInfo);
                    }
                }
                if (action == 1) {
                    this.pressed = false;
                }
            }
            return this.pressed;
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Code restructure failed: missing block: B:108:?, code lost:
            r7.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:113:0x01f3, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:114:0x01f4, code lost:
            r4 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:116:?, code lost:
            r7.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:117:0x01f9, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:120:?, code lost:
            r2.addSuppressed(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:122:0x01ff, code lost:
            r0 = th;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [B:107:0x01e8, B:111:0x01f2, B:115:0x01f5] */
        /* JADX WARNING: Removed duplicated region for block: B:130:0x0213  */
        /* JADX WARNING: Removed duplicated region for block: B:135:0x0262  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x0161 A[Catch:{ all -> 0x00c7, all -> 0x01ab }, FALL_THROUGH] */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x0162 A[Catch:{ all -> 0x00c7, all -> 0x01ab }] */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x016a A[Catch:{ all -> 0x00c7, all -> 0x01ab }] */
        /* JADX WARNING: Removed duplicated region for block: B:74:0x0172 A[Catch:{ all -> 0x00c7, all -> 0x01ab }] */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x017a A[Catch:{ all -> 0x00c7, all -> 0x01ab }] */
        /* JADX WARNING: Removed duplicated region for block: B:82:0x0185 A[Catch:{ all -> 0x00c7, all -> 0x01ab }] */
        /* JADX WARNING: Removed duplicated region for block: B:91:0x019d A[Catch:{ all -> 0x00c7, all -> 0x01ab }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean parseTheme() {
            /*
                r23 = this;
                r1 = r23
                java.lang.String r2 = "chat_inBubble"
                r3 = 0
                java.io.File r0 = new java.io.File
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r4 = r1.themeInfo
                java.lang.String r4 = r4.pathToFile
                r0.<init>(r4)
                r4 = r0
                r6 = 1
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0201 }
                r0.<init>(r4)     // Catch:{ all -> 0x0201 }
                r7 = r0
                r0 = 0
                r8 = 0
            L_0x0018:
                byte[] r9 = im.bclpbkiauv.ui.cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x01ec }
                int r9 = r7.read(r9)     // Catch:{ all -> 0x01ec }
                r10 = r9
                r11 = -1
                if (r9 == r11) goto L_0x01e2
                r9 = r0
                r12 = 0
                r13 = 0
                r22 = r8
                r8 = r0
                r0 = r22
            L_0x002c:
                if (r13 >= r10) goto L_0x01c2
                byte[] r14 = im.bclpbkiauv.ui.cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x01ec }
                byte r14 = r14[r13]     // Catch:{ all -> 0x01ec }
                r15 = 10
                if (r14 != r15) goto L_0x01b0
                int r14 = r0 + 1
                int r0 = r13 - r12
                int r15 = r0 + 1
                java.lang.String r0 = new java.lang.String     // Catch:{ all -> 0x01ec }
                byte[] r5 = im.bclpbkiauv.ui.cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x01ec }
                int r11 = r15 + -1
                java.lang.String r6 = "UTF-8"
                r0.<init>(r5, r12, r11, r6)     // Catch:{ all -> 0x01ec }
                r5 = r0
                java.lang.String r0 = "WLS="
                boolean r0 = r5.startsWith(r0)     // Catch:{ all -> 0x01ec }
                if (r0 == 0) goto L_0x00cf
                r0 = 4
                java.lang.String r0 = r5.substring(r0)     // Catch:{ all -> 0x01ec }
                android.net.Uri r6 = android.net.Uri.parse(r0)     // Catch:{ all -> 0x01ec }
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r11 = r1.themeInfo     // Catch:{ all -> 0x01ec }
                r17 = r3
                java.lang.String r3 = "slug"
                java.lang.String r3 = r6.getQueryParameter(r3)     // Catch:{ all -> 0x00c7 }
                r11.slug = r3     // Catch:{ all -> 0x00c7 }
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x00c7 }
                java.io.File r11 = new java.io.File     // Catch:{ all -> 0x00c7 }
                r18 = r4
                java.io.File r4 = im.bclpbkiauv.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x01ab }
                r19 = r10
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x01ab }
                r10.<init>()     // Catch:{ all -> 0x01ab }
                r20 = r14
                java.lang.String r14 = im.bclpbkiauv.messenger.Utilities.MD5(r0)     // Catch:{ all -> 0x01ab }
                r10.append(r14)     // Catch:{ all -> 0x01ab }
                java.lang.String r14 = ".wp"
                r10.append(r14)     // Catch:{ all -> 0x01ab }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x01ab }
                r11.<init>(r4, r10)     // Catch:{ all -> 0x01ab }
                java.lang.String r4 = r11.getAbsolutePath()     // Catch:{ all -> 0x01ab }
                r3.pathToWallpaper = r4     // Catch:{ all -> 0x01ab }
                java.lang.String r3 = "mode"
                java.lang.String r3 = r6.getQueryParameter(r3)     // Catch:{ all -> 0x01ab }
                if (r3 == 0) goto L_0x00c3
                java.lang.String r4 = r3.toLowerCase()     // Catch:{ all -> 0x01ab }
                r3 = r4
                java.lang.String r4 = " "
                java.lang.String[] r4 = r3.split(r4)     // Catch:{ all -> 0x01ab }
                if (r4 == 0) goto L_0x00c3
                int r10 = r4.length     // Catch:{ all -> 0x01ab }
                if (r10 <= 0) goto L_0x00c3
                r10 = 0
            L_0x00ae:
                int r11 = r4.length     // Catch:{ all -> 0x01ab }
                if (r10 >= r11) goto L_0x00c3
                java.lang.String r11 = "blur"
                r14 = r4[r10]     // Catch:{ all -> 0x01ab }
                boolean r11 = r11.equals(r14)     // Catch:{ all -> 0x01ab }
                if (r11 == 0) goto L_0x00c0
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r11 = r1.themeInfo     // Catch:{ all -> 0x01ab }
                r14 = 1
                r11.isBlured = r14     // Catch:{ all -> 0x01ab }
            L_0x00c0:
                int r10 = r10 + 1
                goto L_0x00ae
            L_0x00c3:
                r21 = r5
                goto L_0x01a6
            L_0x00c7:
                r0 = move-exception
                r18 = r4
                r2 = r0
                r3 = r17
                goto L_0x01f2
            L_0x00cf:
                r17 = r3
                r18 = r4
                r19 = r10
                r20 = r14
                java.lang.String r0 = "WPS"
                boolean r0 = r5.startsWith(r0)     // Catch:{ all -> 0x01ab }
                if (r0 == 0) goto L_0x00eb
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r0 = r1.themeInfo     // Catch:{ all -> 0x01ab }
                int r3 = r8 + r15
                r0.previewWallpaperOffset = r3     // Catch:{ all -> 0x01ab }
                r0 = 1
                r3 = r0
                r0 = r20
                goto L_0x01c8
            L_0x00eb:
                r0 = 61
                int r0 = r5.indexOf(r0)     // Catch:{ all -> 0x01ab }
                r3 = r0
                r14 = -1
                if (r0 == r14) goto L_0x01a2
                r4 = 0
                java.lang.String r0 = r5.substring(r4, r3)     // Catch:{ all -> 0x01ab }
                r4 = r0
                boolean r0 = r4.equals(r2)     // Catch:{ all -> 0x01ab }
                java.lang.String r6 = "chat_wallpaper_gradient_to"
                java.lang.String r10 = "chat_wallpaper"
                java.lang.String r11 = "chat_outBubble"
                if (r0 != 0) goto L_0x011e
                boolean r0 = r4.equals(r11)     // Catch:{ all -> 0x01ab }
                if (r0 != 0) goto L_0x011e
                boolean r0 = r4.equals(r10)     // Catch:{ all -> 0x01ab }
                if (r0 != 0) goto L_0x011e
                boolean r0 = r4.equals(r6)     // Catch:{ all -> 0x01ab }
                if (r0 == 0) goto L_0x011a
                goto L_0x011e
            L_0x011a:
                r21 = r5
                goto L_0x01a6
            L_0x011e:
                int r0 = r3 + 1
                java.lang.String r0 = r5.substring(r0)     // Catch:{ all -> 0x01ab }
                r16 = r0
                int r0 = r16.length()     // Catch:{ all -> 0x01ab }
                if (r0 <= 0) goto L_0x014b
                r14 = r16
                r16 = r3
                r3 = 0
                char r0 = r14.charAt(r3)     // Catch:{ all -> 0x01ab }
                r3 = 35
                if (r0 != r3) goto L_0x014f
                int r0 = android.graphics.Color.parseColor(r14)     // Catch:{ Exception -> 0x013e }
            L_0x013d:
                goto L_0x0157
            L_0x013e:
                r0 = move-exception
                r3 = r0
                r0 = r3
                java.lang.Integer r3 = im.bclpbkiauv.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x01ab }
                int r3 = r3.intValue()     // Catch:{ all -> 0x01ab }
                r0 = r3
                goto L_0x013d
            L_0x014b:
                r14 = r16
                r16 = r3
            L_0x014f:
                java.lang.Integer r0 = im.bclpbkiauv.messenger.Utilities.parseInt(r14)     // Catch:{ all -> 0x01ab }
                int r0 = r0.intValue()     // Catch:{ all -> 0x01ab }
            L_0x0157:
                int r3 = r4.hashCode()     // Catch:{ all -> 0x01ab }
                r21 = r5
                r5 = 2
                switch(r3) {
                    case -1625862693: goto L_0x017a;
                    case -633951866: goto L_0x0172;
                    case 1269980952: goto L_0x016a;
                    case 2052611411: goto L_0x0162;
                    default: goto L_0x0161;
                }     // Catch:{ all -> 0x01ab }
            L_0x0161:
                goto L_0x0182
            L_0x0162:
                boolean r3 = r4.equals(r11)     // Catch:{ all -> 0x01ab }
                if (r3 == 0) goto L_0x0161
                r3 = 1
                goto L_0x0183
            L_0x016a:
                boolean r3 = r4.equals(r2)     // Catch:{ all -> 0x01ab }
                if (r3 == 0) goto L_0x0161
                r3 = 0
                goto L_0x0183
            L_0x0172:
                boolean r3 = r4.equals(r6)     // Catch:{ all -> 0x01ab }
                if (r3 == 0) goto L_0x0161
                r3 = 3
                goto L_0x0183
            L_0x017a:
                boolean r3 = r4.equals(r10)     // Catch:{ all -> 0x01ab }
                if (r3 == 0) goto L_0x0161
                r3 = 2
                goto L_0x0183
            L_0x0182:
                r3 = -1
            L_0x0183:
                if (r3 == 0) goto L_0x019d
                r6 = 1
                if (r3 == r6) goto L_0x0198
                if (r3 == r5) goto L_0x0193
                r5 = 3
                if (r3 == r5) goto L_0x018e
                goto L_0x01a6
            L_0x018e:
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x01ab }
                r3.previewBackgroundGradientColor = r0     // Catch:{ all -> 0x01ab }
                goto L_0x01a6
            L_0x0193:
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x01ab }
                r3.previewBackgroundColor = r0     // Catch:{ all -> 0x01ab }
                goto L_0x01a6
            L_0x0198:
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x01ab }
                r3.previewOutColor = r0     // Catch:{ all -> 0x01ab }
                goto L_0x01a6
            L_0x019d:
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x01ab }
                r3.previewInColor = r0     // Catch:{ all -> 0x01ab }
                goto L_0x01a6
            L_0x01a2:
                r16 = r3
                r21 = r5
            L_0x01a6:
                int r12 = r12 + r15
                int r8 = r8 + r15
                r0 = r20
                goto L_0x01b6
            L_0x01ab:
                r0 = move-exception
                r2 = r0
                r3 = r17
                goto L_0x01f2
            L_0x01b0:
                r17 = r3
                r18 = r4
                r19 = r10
            L_0x01b6:
                int r13 = r13 + 1
                r3 = r17
                r4 = r18
                r10 = r19
                r6 = 1
                r11 = -1
                goto L_0x002c
            L_0x01c2:
                r17 = r3
                r18 = r4
                r19 = r10
            L_0x01c8:
                if (r3 != 0) goto L_0x01e8
                if (r9 != r8) goto L_0x01cd
                goto L_0x01e8
            L_0x01cd:
                java.nio.channels.FileChannel r4 = r7.getChannel()     // Catch:{ all -> 0x01df }
                long r5 = (long) r8     // Catch:{ all -> 0x01df }
                r4.position(r5)     // Catch:{ all -> 0x01df }
                r4 = r18
                r6 = 1
                r22 = r8
                r8 = r0
                r0 = r22
                goto L_0x0018
            L_0x01df:
                r0 = move-exception
                r2 = r0
                goto L_0x01f2
            L_0x01e2:
                r17 = r3
                r18 = r4
                r19 = r10
            L_0x01e8:
                r7.close()     // Catch:{ all -> 0x01ff }
                goto L_0x0207
            L_0x01ec:
                r0 = move-exception
                r17 = r3
                r18 = r4
                r2 = r0
            L_0x01f2:
                throw r2     // Catch:{ all -> 0x01f3 }
            L_0x01f3:
                r0 = move-exception
                r4 = r0
                r7.close()     // Catch:{ all -> 0x01f9 }
                goto L_0x01fe
            L_0x01f9:
                r0 = move-exception
                r5 = r0
                r2.addSuppressed(r5)     // Catch:{ all -> 0x01ff }
            L_0x01fe:
                throw r4     // Catch:{ all -> 0x01ff }
            L_0x01ff:
                r0 = move-exception
                goto L_0x0204
            L_0x0201:
                r0 = move-exception
                r18 = r4
            L_0x0204:
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0207:
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r0 = r1.themeInfo
                java.lang.String r0 = r0.pathToWallpaper
                if (r0 == 0) goto L_0x0262
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r0 = r1.themeInfo
                boolean r0 = r0.badWallpaper
                if (r0 != 0) goto L_0x0262
                java.io.File r0 = new java.io.File
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r2 = r1.themeInfo
                java.lang.String r2 = r2.pathToWallpaper
                r0.<init>(r2)
                r4 = r0
                boolean r0 = r4.exists()
                if (r0 != 0) goto L_0x0264
                im.bclpbkiauv.ui.cells.ThemesHorizontalListCell r0 = im.bclpbkiauv.ui.cells.ThemesHorizontalListCell.this
                java.util.HashMap r0 = r0.loadingWallpapers
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r2 = r1.themeInfo
                boolean r0 = r0.containsKey(r2)
                if (r0 != 0) goto L_0x0260
                im.bclpbkiauv.ui.cells.ThemesHorizontalListCell r0 = im.bclpbkiauv.ui.cells.ThemesHorizontalListCell.this
                java.util.HashMap r0 = r0.loadingWallpapers
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r2 = r1.themeInfo
                java.lang.String r5 = r2.slug
                r0.put(r2, r5)
                im.bclpbkiauv.tgnet.TLRPC$TL_account_getWallPaper r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_account_getWallPaper
                r0.<init>()
                im.bclpbkiauv.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputWallPaperSlug
                r2.<init>()
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r5 = r1.themeInfo
                java.lang.String r5 = r5.slug
                r2.slug = r5
                r0.wallpaper = r2
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r5 = r1.themeInfo
                int r5 = r5.account
                im.bclpbkiauv.tgnet.ConnectionsManager r5 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r5)
                im.bclpbkiauv.ui.cells.-$$Lambda$ThemesHorizontalListCell$InnerThemeView$JA3NgFa2WGpxc_dOA8tmaKbGUac r6 = new im.bclpbkiauv.ui.cells.-$$Lambda$ThemesHorizontalListCell$InnerThemeView$JA3NgFa2WGpxc_dOA8tmaKbGUac
                r6.<init>()
                r5.sendRequest(r0, r6)
            L_0x0260:
                r2 = 0
                return r2
            L_0x0262:
                r4 = r18
            L_0x0264:
                im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo r0 = r1.themeInfo
                r2 = 1
                r0.previewParsed = r2
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.ThemesHorizontalListCell.InnerThemeView.parseTheme():boolean");
        }

        public /* synthetic */ void lambda$parseTheme$1$ThemesHorizontalListCell$InnerThemeView(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ThemesHorizontalListCell.InnerThemeView.this.lambda$null$0$ThemesHorizontalListCell$InnerThemeView(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ThemesHorizontalListCell$InnerThemeView(TLObject response) {
            if (response instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) response;
                String name = FileLoader.getAttachFileName(wallPaper.document);
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(name)) {
                    ThemesHorizontalListCell.this.loadingThemes.put(name, this.themeInfo);
                    FileLoader.getInstance(this.themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
                    return;
                }
                return;
            }
            this.themeInfo.badWallpaper = true;
        }

        /* access modifiers changed from: private */
        public void applyTheme() {
            this.inDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.previewInColor, PorterDuff.Mode.MULTIPLY));
            this.outDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.previewOutColor, PorterDuff.Mode.MULTIPLY));
            if (this.themeInfo.pathToFile == null) {
                updateAccentColor(this.themeInfo.accentColor, false);
                this.optionsDrawable = null;
            } else {
                this.optionsDrawable = getResources().getDrawable(R.drawable.preview_dots).mutate();
            }
            this.bitmapShader = null;
            this.backgroundDrawable = null;
            double[] hsv = null;
            if (this.themeInfo.previewBackgroundGradientColor != 0) {
                BackgroundGradientDrawable drawable = new BackgroundGradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{this.themeInfo.previewBackgroundColor, this.themeInfo.previewBackgroundGradientColor});
                drawable.setCornerRadius((float) AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = drawable;
                hsv = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.previewBackgroundColor), Color.green(this.themeInfo.previewBackgroundColor), Color.blue(this.themeInfo.previewBackgroundColor));
            } else if (this.themeInfo.previewWallpaperOffset > 0 || this.themeInfo.pathToWallpaper != null) {
                Bitmap wallpaper = ThemesHorizontalListCell.getScaledBitmap((float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(97.0f), this.themeInfo.pathToWallpaper, this.themeInfo.pathToFile, this.themeInfo.previewWallpaperOffset);
                if (wallpaper != null) {
                    this.backgroundDrawable = new BitmapDrawable(wallpaper);
                    BitmapShader bitmapShader2 = new BitmapShader(wallpaper, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    this.bitmapShader = bitmapShader2;
                    this.bitmapPaint.setShader(bitmapShader2);
                    int[] colors = AndroidUtilities.calcDrawableColor(this.backgroundDrawable);
                    hsv = AndroidUtilities.rgbToHsv(Color.red(colors[0]), Color.green(colors[0]), Color.blue(colors[0]));
                }
            } else if (this.themeInfo.previewBackgroundColor != 0) {
                hsv = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.previewBackgroundColor), Color.green(this.themeInfo.previewBackgroundColor), Color.blue(this.themeInfo.previewBackgroundColor));
            }
            if (hsv == null || hsv[1] > 0.10000000149011612d || hsv[2] < 0.9599999785423279d) {
                this.hasWhiteBackground = false;
            } else {
                this.hasWhiteBackground = true;
            }
            if (this.themeInfo.previewBackgroundColor == 0 && this.themeInfo.previewParsed && this.backgroundDrawable == null) {
                BitmapDrawable drawable2 = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile).mutate();
                BitmapShader bitmapShader3 = new BitmapShader(drawable2.getBitmap(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                this.bitmapShader = bitmapShader3;
                this.bitmapPaint.setShader(bitmapShader3);
                this.backgroundDrawable = drawable2;
            }
            invalidate();
        }

        public void setTheme(Theme.ThemeInfo theme, boolean last, boolean first) {
            this.themeInfo = theme;
            this.isFirst = first;
            this.isLast = last;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.button.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(this.isFirst ? 49.0f : 27.0f);
            this.button.setLayoutParams(layoutParams);
            this.placeholderAlpha = 0.0f;
            if (this.themeInfo.pathToFile != null && !this.themeInfo.previewParsed) {
                this.themeInfo.previewInColor = Theme.getDefaultColor(Theme.key_chat_inBubble);
                this.themeInfo.previewOutColor = Theme.getDefaultColor(Theme.key_chat_outBubble);
                boolean fileExists = new File(this.themeInfo.pathToFile).exists();
                if ((!(fileExists && parseTheme()) || !fileExists) && this.themeInfo.info != null) {
                    if (this.themeInfo.info.document != null) {
                        this.themeInfo.themeLoaded = false;
                        this.placeholderAlpha = 1.0f;
                        Drawable mutate = getResources().getDrawable(R.drawable.msg_theme).mutate();
                        this.loadingDrawable = mutate;
                        int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                        this.loadingColor = color;
                        Theme.setDrawableColor(mutate, color);
                        if (!fileExists) {
                            String name = FileLoader.getAttachFileName(this.themeInfo.info.document);
                            if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(name)) {
                                ThemesHorizontalListCell.this.loadingThemes.put(name, this.themeInfo);
                                FileLoader.getInstance(this.themeInfo.account).loadFile(this.themeInfo.info.document, this.themeInfo.info, 1, 1);
                            }
                        }
                    } else {
                        Drawable mutate2 = getResources().getDrawable(R.drawable.preview_custom).mutate();
                        this.loadingDrawable = mutate2;
                        int color2 = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                        this.loadingColor = color2;
                        Theme.setDrawableColor(mutate2, color2);
                    }
                }
            }
            applyTheme();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), false);
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            if (themeInfo2 != null && themeInfo2.info != null && !this.themeInfo.themeLoaded) {
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(FileLoader.getAttachFileName(this.themeInfo.info.document)) && !ThemesHorizontalListCell.this.loadingWallpapers.containsKey(this.themeInfo)) {
                    this.themeInfo.themeLoaded = true;
                    this.placeholderAlpha = 0.0f;
                    parseTheme();
                    applyTheme();
                }
            }
        }

        public void updateCurrentThemeCheck() {
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), true);
        }

        /* access modifiers changed from: package-private */
        public void updateAccentColor(int accent, boolean animate) {
            this.oldAccentColor = this.accentColor;
            this.accentColor = accent;
            ObjectAnimator objectAnimator = this.accentAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (animate) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "accentState", new float[]{0.0f, 1.0f});
                this.accentAnimator = ofFloat;
                ofFloat.setDuration(200);
                this.accentAnimator.start();
                return;
            }
            setAccentState(1.0f);
        }

        public float getAccentState() {
            return (float) this.accentColor;
        }

        public void setAccentState(float state) {
            this.accentState = state;
            this.accentColorChanged = true;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float f;
            float bitmapW;
            Canvas canvas2 = canvas;
            boolean drawContent = true;
            if (this.accentColor != this.themeInfo.accentColor) {
                updateAccentColor(this.themeInfo.accentColor, true);
            }
            int x = this.isFirst ? AndroidUtilities.dp(22.0f) : 0;
            int y = AndroidUtilities.dp(11.0f);
            this.rect.set((float) x, (float) y, (float) (AndroidUtilities.dp(76.0f) + x), (float) (AndroidUtilities.dp(97.0f) + y));
            String name = this.themeInfo.getName();
            if (name.toLowerCase().endsWith(".attheme")) {
                name = name.substring(0, name.lastIndexOf(46));
            }
            String text = TextUtils.ellipsize(name, this.textPaint, (float) ((getMeasuredWidth() - AndroidUtilities.dp(this.isFirst ? 10.0f : 15.0f)) - (this.isLast ? AndroidUtilities.dp(7.0f) : 0)), TextUtils.TruncateAt.END).toString();
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas2.drawText(text, (float) (((AndroidUtilities.dp(76.0f) - ((int) Math.ceil((double) this.textPaint.measureText(text)))) / 2) + x), (float) AndroidUtilities.dp(131.0f), this.textPaint);
            if (this.themeInfo.info != null && (this.themeInfo.info.document == null || !this.themeInfo.themeLoaded)) {
                drawContent = false;
            }
            if (drawContent) {
                this.paint.setColor(tint(this.themeInfo.previewBackgroundColor));
                if (this.accentColorChanged) {
                    this.inDrawable.setColorFilter(new PorterDuffColorFilter(tint(this.themeInfo.previewInColor), PorterDuff.Mode.MULTIPLY));
                    this.outDrawable.setColorFilter(new PorterDuffColorFilter(tint(this.themeInfo.previewOutColor), PorterDuff.Mode.MULTIPLY));
                    this.accentColorChanged = false;
                }
                Drawable drawable = this.backgroundDrawable;
                if (drawable == null) {
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.paint);
                } else if (this.bitmapShader != null) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    float bitmapW2 = (float) bitmapDrawable.getBitmap().getWidth();
                    float bitmapH = (float) bitmapDrawable.getBitmap().getHeight();
                    float scaleW = bitmapW2 / this.rect.width();
                    float scaleH = bitmapH / this.rect.height();
                    this.shaderMatrix.reset();
                    float scale = 1.0f / Math.min(scaleW, scaleH);
                    if (bitmapW2 / scaleH > this.rect.width()) {
                        bitmapW = bitmapW2 / scaleH;
                        BitmapDrawable bitmapDrawable2 = bitmapDrawable;
                        boolean z = drawContent;
                        this.shaderMatrix.setTranslate(((float) x) - ((bitmapW - this.rect.width()) / 2.0f), (float) y);
                    } else {
                        boolean z2 = drawContent;
                        this.shaderMatrix.setTranslate((float) x, ((float) y) - (((bitmapH / scaleW) - this.rect.height()) / 2.0f));
                        bitmapW = bitmapW2;
                    }
                    this.shaderMatrix.preScale(scale, scale);
                    this.bitmapShader.setLocalMatrix(this.shaderMatrix);
                    float f2 = bitmapW;
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.bitmapPaint);
                } else {
                    drawable.setBounds((int) this.rect.left, (int) this.rect.top, (int) this.rect.right, (int) this.rect.bottom);
                    this.backgroundDrawable.draw(canvas2);
                }
                this.button.setColor(1728053247, -1);
                if (this.themeInfo.accentBaseColor != 0) {
                    if ("Arctic Blue".equals(this.themeInfo.name)) {
                        this.button.setColor(-5000269, tint(this.themeInfo.accentBaseColor));
                        Theme.chat_instantViewRectPaint.setColor(733001146);
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                        f = 6.0f;
                    } else {
                        f = 6.0f;
                    }
                } else if (this.hasWhiteBackground) {
                    this.button.setColor(-5000269, this.themeInfo.previewOutColor);
                    Theme.chat_instantViewRectPaint.setColor(733001146);
                    f = 6.0f;
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                } else {
                    f = 6.0f;
                }
                this.inDrawable.setBounds(AndroidUtilities.dp(f) + x, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(49.0f) + x, AndroidUtilities.dp(36.0f));
                this.inDrawable.draw(canvas2);
                this.outDrawable.setBounds(AndroidUtilities.dp(27.0f) + x, AndroidUtilities.dp(41.0f), AndroidUtilities.dp(70.0f) + x, AndroidUtilities.dp(55.0f));
                this.outDrawable.draw(canvas2);
                if (this.optionsDrawable != null && ThemesHorizontalListCell.this.currentType == 0) {
                    int x2 = ((int) this.rect.right) - AndroidUtilities.dp(16.0f);
                    int y2 = ((int) this.rect.top) + AndroidUtilities.dp(6.0f);
                    Drawable drawable2 = this.optionsDrawable;
                    drawable2.setBounds(x2, y2, drawable2.getIntrinsicWidth() + x2, this.optionsDrawable.getIntrinsicHeight() + y2);
                    this.optionsDrawable.draw(canvas2);
                }
            }
            if (this.themeInfo.info != null && this.themeInfo.info.document == null) {
                this.button.setAlpha(0.0f);
                Theme.chat_instantViewRectPaint.setColor(733001146);
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                if (this.loadingDrawable != null) {
                    int newColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                    if (this.loadingColor != newColor) {
                        Drawable drawable3 = this.loadingDrawable;
                        this.loadingColor = newColor;
                        Theme.setDrawableColor(drawable3, newColor);
                    }
                    int x3 = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                    int y3 = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                    Drawable drawable4 = this.loadingDrawable;
                    drawable4.setBounds(x3, y3, drawable4.getIntrinsicWidth() + x3, this.loadingDrawable.getIntrinsicHeight() + y3);
                    this.loadingDrawable.draw(canvas2);
                }
            } else if ((this.themeInfo.info != null && !this.themeInfo.themeLoaded) || this.placeholderAlpha > 0.0f) {
                this.button.setAlpha(1.0f - this.placeholderAlpha);
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                this.paint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.paint);
                if (this.loadingDrawable != null) {
                    int newColor2 = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                    if (this.loadingColor != newColor2) {
                        Drawable drawable5 = this.loadingDrawable;
                        this.loadingColor = newColor2;
                        Theme.setDrawableColor(drawable5, newColor2);
                    }
                    int x4 = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                    int y4 = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                    this.loadingDrawable.setAlpha((int) (this.placeholderAlpha * 255.0f));
                    Drawable drawable6 = this.loadingDrawable;
                    drawable6.setBounds(x4, y4, drawable6.getIntrinsicWidth() + x4, this.loadingDrawable.getIntrinsicHeight() + y4);
                    this.loadingDrawable.draw(canvas2);
                }
                if (this.themeInfo.themeLoaded) {
                    long newTime = SystemClock.uptimeMillis();
                    long dt = Math.min(17, newTime - this.lastDrawTime);
                    this.lastDrawTime = newTime;
                    float f3 = this.placeholderAlpha - (((float) dt) / 180.0f);
                    this.placeholderAlpha = f3;
                    if (f3 < 0.0f) {
                        this.placeholderAlpha = 0.0f;
                    }
                    invalidate();
                }
            } else if (this.button.getAlpha() != 1.0f) {
                this.button.setAlpha(1.0f);
            }
        }

        private int tint(int color) {
            if (this.accentState == 1.0f) {
                return Theme.changeColorAccent(this.themeInfo, this.accentColor, color);
            }
            return ((Integer) this.evaluator.evaluate(this.accentState, Integer.valueOf(Theme.changeColorAccent(this.themeInfo, this.oldAccentColor, color)), Integer.valueOf(Theme.changeColorAccent(this.themeInfo, this.accentColor, color)))).intValue();
        }
    }

    public ThemesHorizontalListCell(Context context, int type, ArrayList<Theme.ThemeInfo> def, ArrayList<Theme.ThemeInfo> dark) {
        super(context);
        this.darkThemes = dark;
        this.defaultThemes = def;
        this.currentType = type;
        if (type == 2) {
            setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        } else {
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        setItemAnimator((RecyclerView.ItemAnimator) null);
        setLayoutAnimation((LayoutAnimationController) null);
        this.horizontalLayoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        setPadding(0, 0, 0, 0);
        setClipToPadding(false);
        this.horizontalLayoutManager.setOrientation(0);
        setLayoutManager(this.horizontalLayoutManager);
        ThemesListAdapter themesListAdapter = new ThemesListAdapter(context);
        this.adapter = themesListAdapter;
        setAdapter(themesListAdapter);
        setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ThemesHorizontalListCell.this.lambda$new$0$ThemesHorizontalListCell(view, i);
            }
        });
        setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ThemesHorizontalListCell.this.lambda$new$1$ThemesHorizontalListCell(view, i);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$ThemesHorizontalListCell(View view1, int position) {
        Theme.ThemeInfo themeInfo = ((InnerThemeView) view1).themeInfo;
        if (themeInfo.info != null) {
            if (themeInfo.themeLoaded) {
                if (themeInfo.info.document == null) {
                    presentFragment(new ThemeSetUrlActivity(themeInfo, true));
                    return;
                }
            } else {
                return;
            }
        }
        if (this.currentType == 1) {
            if (themeInfo != Theme.getCurrentNightTheme()) {
                Theme.setCurrentNightTheme(themeInfo);
            } else {
                return;
            }
        } else if (themeInfo != Theme.getCurrentTheme()) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false);
        } else {
            return;
        }
        updateRows();
        int left = view1.getLeft();
        int right = view1.getRight();
        if (left < 0) {
            smoothScrollBy(left - AndroidUtilities.dp(8.0f), 0);
        } else if (right > getMeasuredWidth()) {
            smoothScrollBy(right - getMeasuredWidth(), 0);
        }
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof InnerThemeView) {
                ((InnerThemeView) child).updateCurrentThemeCheck();
            }
        }
    }

    public /* synthetic */ boolean lambda$new$1$ThemesHorizontalListCell(View view12, int position) {
        showOptionsForTheme(((InnerThemeView) view12).themeInfo);
        return true;
    }

    public void setDrawDivider(boolean draw) {
        this.drawDivider = draw;
    }

    public void notifyDataSetChanged(int width) {
        this.adapter.notifyDataSetChanged();
        if (this.prevThemeInfo != (this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme())) {
            scrollToCurrentTheme(width, false);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!(getParent() == null || getParent().getParent() == null)) {
            getParent().getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(e);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public static Bitmap getScaledBitmap(float w, float h, String path, String streamPath, int streamOffset) {
        Bitmap wallpaper;
        FileInputStream stream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (path != null) {
                BitmapFactory.decodeFile(path, options);
            } else {
                stream = new FileInputStream(streamPath);
                stream.getChannel().position((long) streamOffset);
                BitmapFactory.decodeStream(stream, (Rect) null, options);
            }
            if (options.outWidth <= 0 || options.outHeight <= 0) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                return null;
            }
            if (w > h && options.outWidth < options.outHeight) {
                float temp = w;
                w = h;
                h = temp;
            }
            float scale = Math.min(((float) options.outWidth) / w, ((float) options.outHeight) / h);
            options.inSampleSize = 1;
            if (scale > 1.0f) {
                do {
                    options.inSampleSize *= 2;
                } while (((float) options.inSampleSize) < scale);
            }
            options.inJustDecodeBounds = false;
            if (path != null) {
                wallpaper = BitmapFactory.decodeFile(path, options);
            } else {
                stream.getChannel().position((long) streamOffset);
                wallpaper = BitmapFactory.decodeStream(stream, (Rect) null, options);
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e22) {
                    FileLog.e((Throwable) e22);
                }
            }
            return wallpaper;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e23) {
                    FileLog.e((Throwable) e23);
                }
            }
            throw th;
        }
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        invalidateViews();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileDidFailToLoad);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileDidLoad) {
            String fileName = args[0];
            File file = args[1];
            Theme.ThemeInfo info = this.loadingThemes.get(fileName);
            if (info != null) {
                this.loadingThemes.remove(fileName);
                if (this.loadingWallpapers.remove(info) != null) {
                    Utilities.globalQueue.postRunnable(new Runnable(file, info) {
                        private final /* synthetic */ File f$1;
                        private final /* synthetic */ Theme.ThemeInfo f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            ThemesHorizontalListCell.this.lambda$didReceivedNotification$3$ThemesHorizontalListCell(this.f$1, this.f$2);
                        }
                    });
                } else {
                    lambda$null$2$ThemesHorizontalListCell(info);
                }
            }
        } else if (id == NotificationCenter.fileDidFailToLoad) {
            this.loadingThemes.remove(args[0]);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$3$ThemesHorizontalListCell(File file, Theme.ThemeInfo info) {
        try {
            Bitmap bitmap = getScaledBitmap((float) AndroidUtilities.dp(640.0f), (float) AndroidUtilities.dp(360.0f), file.getAbsolutePath(), (String) null, 0);
            if (info.isBlured) {
                bitmap = Utilities.blurWallpaper(bitmap);
            }
            FileOutputStream stream = new FileOutputStream(info.pathToWallpaper);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
            stream.close();
        } catch (Throwable e) {
            FileLog.e(e);
            info.badWallpaper = true;
        }
        AndroidUtilities.runOnUIThread(new Runnable(info) {
            private final /* synthetic */ Theme.ThemeInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemesHorizontalListCell.this.lambda$null$2$ThemesHorizontalListCell(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: checkVisibleTheme */
    public void lambda$null$2$ThemesHorizontalListCell(Theme.ThemeInfo info) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof InnerThemeView) {
                InnerThemeView view = (InnerThemeView) child;
                if (view.themeInfo == info && view.parseTheme()) {
                    view.themeInfo.themeLoaded = true;
                    view.applyTheme();
                }
            }
        }
    }

    public void scrollToCurrentTheme(int width, boolean animated) {
        View parent;
        if (width == 0 && (parent = (View) getParent()) != null) {
            width = parent.getMeasuredWidth();
        }
        if (width != 0) {
            Theme.ThemeInfo currentNightTheme = this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.prevThemeInfo = currentNightTheme;
            int index = this.defaultThemes.indexOf(currentNightTheme);
            if (index < 0 && (index = this.darkThemes.indexOf(this.prevThemeInfo) + this.defaultThemes.size()) < 0) {
                return;
            }
            if (animated) {
                smoothScrollToPosition(index);
            } else {
                this.horizontalLayoutManager.scrollToPositionWithOffset(index, (width - AndroidUtilities.dp(76.0f)) / 2);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
    }

    /* access modifiers changed from: protected */
    public void presentFragment(BaseFragment fragment) {
    }

    /* access modifiers changed from: protected */
    public void updateRows() {
    }
}

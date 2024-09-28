package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ThemeCell extends FrameLayout {
    private static byte[] bytes = new byte[1024];
    private ImageView checkImage;
    private Theme.ThemeInfo currentThemeInfo;
    private boolean isNightTheme;
    private boolean needDivider;
    private ImageView optionsButton;
    private Paint paint = new Paint(1);
    private Paint paintStroke;
    private TextView textView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ThemeCell(Context context, boolean nightTheme) {
        super(context);
        Context context2 = context;
        setWillNotDraw(false);
        this.isNightTheme = nightTheme;
        Paint paint2 = new Paint(1);
        this.paintStroke = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.paintStroke.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 105.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 105.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.checkImage = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.SRC_IN));
        this.checkImage.setImageResource(R.mipmap.ic_selected);
        if (!this.isNightTheme) {
            addView(this.checkImage, LayoutHelper.createFrame(19.0f, 14.0f, (LocaleController.isRTL ? 3 : 5) | 16, 59.0f, 0.0f, 59.0f, 0.0f));
            ImageView imageView2 = new ImageView(context2);
            this.optionsButton = imageView2;
            imageView2.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            this.optionsButton.setImageResource(R.drawable.ic_ab_other);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            addView(this.optionsButton, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : i) | 48));
            return;
        }
        addView(this.checkImage, LayoutHelper.createFrame(19.0f, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setOnOptionsClick(View.OnClickListener listener) {
        this.optionsButton.setOnClickListener(listener);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public Theme.ThemeInfo getCurrentThemeInfo() {
        return this.currentThemeInfo;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00a5, code lost:
        r16 = r3.substring(r4 + 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b1, code lost:
        if (r16.length() <= 0) goto L_0x00d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b3, code lost:
        r20 = r3;
        r17 = r4;
        r3 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c0, code lost:
        if (r3.charAt(0) != '#') goto L_0x00da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        r0 = android.graphics.Color.parseColor(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00d4, code lost:
        r20 = r3;
        r17 = r4;
        r3 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00da, code lost:
        r0 = im.bclpbkiauv.messenger.Utilities.parseInt(r3).intValue();
     */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x011d A[LOOP:0: B:14:0x0051->B:61:0x011d, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0141 A[SYNTHETIC, Splitter:B:73:0x0141] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x017f  */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTheme(im.bclpbkiauv.ui.actionbar.Theme.ThemeInfo r23, boolean r24) {
        /*
            r22 = this;
            r1 = r22
            r2 = r23
            r1.currentThemeInfo = r2
            java.lang.String r0 = r23.getName()
            java.lang.String r3 = ".attheme"
            boolean r3 = r0.endsWith(r3)
            r4 = 0
            if (r3 == 0) goto L_0x001f
            r3 = 46
            int r3 = r0.lastIndexOf(r3)
            java.lang.String r0 = r0.substring(r4, r3)
            r3 = r0
            goto L_0x0020
        L_0x001f:
            r3 = r0
        L_0x0020:
            android.widget.TextView r0 = r1.textView
            r0.setText(r3)
            r5 = r24
            r1.needDivider = r5
            r22.updateCurrentThemeCheck()
            r6 = 0
            boolean r0 = im.bclpbkiauv.ui.actionbar.Theme.isThemeDefault(r23)
            java.lang.String r7 = "actionBarDefault"
            if (r0 != 0) goto L_0x0159
            java.lang.String r0 = r2.assetName
            if (r0 == 0) goto L_0x003d
            r18 = r3
            goto L_0x015b
        L_0x003d:
            java.lang.String r0 = r2.pathToFile
            if (r0 == 0) goto L_0x0156
            r8 = 0
            r0 = 0
            java.io.File r9 = new java.io.File     // Catch:{ all -> 0x0139 }
            java.lang.String r10 = r2.pathToFile     // Catch:{ all -> 0x0139 }
            r9.<init>(r10)     // Catch:{ all -> 0x0139 }
            java.io.FileInputStream r10 = new java.io.FileInputStream     // Catch:{ all -> 0x0139 }
            r10.<init>(r9)     // Catch:{ all -> 0x0139 }
            r8 = r10
            r10 = 0
        L_0x0051:
            byte[] r11 = bytes     // Catch:{ all -> 0x0139 }
            int r11 = r8.read(r11)     // Catch:{ all -> 0x0139 }
            r12 = r11
            r13 = -1
            if (r11 == r13) goto L_0x012b
            r11 = r0
            r14 = 0
            r15 = 0
            r21 = r10
            r10 = r0
            r0 = r21
        L_0x0063:
            if (r15 >= r12) goto L_0x0109
            byte[] r16 = bytes     // Catch:{ all -> 0x0139 }
            byte r4 = r16[r15]     // Catch:{ all -> 0x0139 }
            r13 = 10
            if (r4 != r13) goto L_0x00fd
            int r4 = r0 + 1
            int r0 = r15 - r14
            int r13 = r0 + 1
            java.lang.String r0 = new java.lang.String     // Catch:{ all -> 0x0139 }
            r18 = r3
            byte[] r3 = bytes     // Catch:{ all -> 0x0129 }
            r19 = r4
            int r4 = r13 + -1
            java.lang.String r5 = "UTF-8"
            r0.<init>(r3, r14, r4, r5)     // Catch:{ all -> 0x0129 }
            r3 = r0
            java.lang.String r0 = "WPS"
            boolean r0 = r3.startsWith(r0)     // Catch:{ all -> 0x0129 }
            if (r0 == 0) goto L_0x008f
            r0 = r19
            goto L_0x010b
        L_0x008f:
            r0 = 61
            int r0 = r3.indexOf(r0)     // Catch:{ all -> 0x0129 }
            r4 = r0
            r5 = -1
            if (r0 == r5) goto L_0x00f4
            r5 = 0
            java.lang.String r0 = r3.substring(r5, r4)     // Catch:{ all -> 0x0129 }
            r5 = r0
            boolean r0 = r5.equals(r7)     // Catch:{ all -> 0x0129 }
            if (r0 == 0) goto L_0x00ef
            int r0 = r4 + 1
            java.lang.String r0 = r3.substring(r0)     // Catch:{ all -> 0x0129 }
            r16 = r0
            int r0 = r16.length()     // Catch:{ all -> 0x0129 }
            if (r0 <= 0) goto L_0x00d4
            r20 = r3
            r17 = r4
            r3 = r16
            r4 = 0
            char r0 = r3.charAt(r4)     // Catch:{ all -> 0x0129 }
            r4 = 35
            if (r0 != r4) goto L_0x00da
            int r0 = android.graphics.Color.parseColor(r3)     // Catch:{ Exception -> 0x00c7 }
        L_0x00c6:
            goto L_0x00e2
        L_0x00c7:
            r0 = move-exception
            r4 = r0
            r0 = r4
            java.lang.Integer r4 = im.bclpbkiauv.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x0129 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x0129 }
            r0 = r4
            goto L_0x00c6
        L_0x00d4:
            r20 = r3
            r17 = r4
            r3 = r16
        L_0x00da:
            java.lang.Integer r0 = im.bclpbkiauv.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x0129 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0129 }
        L_0x00e2:
            r4 = 1
            android.graphics.Paint r6 = r1.paint     // Catch:{ all -> 0x00ec }
            r6.setColor(r0)     // Catch:{ all -> 0x00ec }
            r6 = r4
            r0 = r19
            goto L_0x010b
        L_0x00ec:
            r0 = move-exception
            r6 = r4
            goto L_0x013c
        L_0x00ef:
            r20 = r3
            r17 = r4
            goto L_0x00f8
        L_0x00f4:
            r20 = r3
            r17 = r4
        L_0x00f8:
            int r14 = r14 + r13
            int r10 = r10 + r13
            r0 = r19
            goto L_0x00ff
        L_0x00fd:
            r18 = r3
        L_0x00ff:
            int r15 = r15 + 1
            r5 = r24
            r3 = r18
            r4 = 0
            r13 = -1
            goto L_0x0063
        L_0x0109:
            r18 = r3
        L_0x010b:
            if (r11 == r10) goto L_0x012d
            r3 = 500(0x1f4, float:7.0E-43)
            if (r0 < r3) goto L_0x0112
            goto L_0x012d
        L_0x0112:
            java.nio.channels.FileChannel r3 = r8.getChannel()     // Catch:{ all -> 0x0129 }
            long r4 = (long) r10     // Catch:{ all -> 0x0129 }
            r3.position(r4)     // Catch:{ all -> 0x0129 }
            if (r6 == 0) goto L_0x011d
            goto L_0x012d
        L_0x011d:
            r5 = r24
            r3 = r18
            r4 = 0
            r21 = r10
            r10 = r0
            r0 = r21
            goto L_0x0051
        L_0x0129:
            r0 = move-exception
            goto L_0x013c
        L_0x012b:
            r18 = r3
        L_0x012d:
            r8.close()     // Catch:{ Exception -> 0x0132 }
        L_0x0131:
            goto L_0x0169
        L_0x0132:
            r0 = move-exception
            r3 = r0
            r0 = r3
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0169
        L_0x0139:
            r0 = move-exception
            r18 = r3
        L_0x013c:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0145 }
            if (r8 == 0) goto L_0x0131
            r8.close()     // Catch:{ Exception -> 0x0132 }
            goto L_0x0131
        L_0x0145:
            r0 = move-exception
            r3 = r0
            if (r8 == 0) goto L_0x0154
            r8.close()     // Catch:{ Exception -> 0x014d }
            goto L_0x0154
        L_0x014d:
            r0 = move-exception
            r4 = r0
            r0 = r4
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0155
        L_0x0154:
        L_0x0155:
            throw r3
        L_0x0156:
            r18 = r3
            goto L_0x0169
        L_0x0159:
            r18 = r3
        L_0x015b:
            android.graphics.Paint r0 = r1.paint
            int r3 = r2.accentColor
            int r4 = r2.previewBackgroundColor
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.changeColorAccent((im.bclpbkiauv.ui.actionbar.Theme.ThemeInfo) r2, (int) r3, (int) r4)
            r0.setColor(r3)
            r6 = 1
        L_0x0169:
            if (r6 != 0) goto L_0x0174
            android.graphics.Paint r0 = r1.paint
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getDefaultColor(r7)
            r0.setColor(r3)
        L_0x0174:
            android.graphics.Paint r0 = r1.paintStroke
            int r3 = r2.accentColor
            r0.setColor(r3)
            int r0 = r2.accentColor
            if (r0 == 0) goto L_0x0186
            android.graphics.Paint r0 = r1.paintStroke
            r3 = 180(0xb4, float:2.52E-43)
            r0.setAlpha(r3)
        L_0x0186:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.ThemeCell.setTheme(im.bclpbkiauv.ui.actionbar.Theme$ThemeInfo, boolean):void");
    }

    public void updateCurrentThemeCheck() {
        Theme.ThemeInfo currentTheme;
        if (this.isNightTheme) {
            currentTheme = Theme.getCurrentNightTheme();
        } else {
            currentTheme = Theme.getCurrentTheme();
        }
        int newVisibility = this.currentThemeInfo == currentTheme ? 0 : 4;
        if (this.checkImage.getVisibility() != newVisibility) {
            this.checkImage.setVisibility(newVisibility);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        int x = AndroidUtilities.dp(31.0f);
        if (LocaleController.isRTL) {
            x = getWidth() - x;
        }
        canvas.drawCircle((float) x, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(11.0f), this.paint);
        canvas.drawCircle((float) x, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(10.0f), this.paintStroke);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        setSelected(this.checkImage.getVisibility() == 0);
    }
}

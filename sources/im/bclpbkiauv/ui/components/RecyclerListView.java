package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class RecyclerListView extends RecyclerView {
    private static int[] attributes;
    private static boolean gotAttributes;
    /* access modifiers changed from: private */
    public Runnable clickRunnable;
    /* access modifiers changed from: private */
    public int currentChildPosition;
    /* access modifiers changed from: private */
    public View currentChildView;
    /* access modifiers changed from: private */
    public int currentFirst;
    private int currentVisible;
    private boolean disableHighlightState;
    private boolean disallowInterceptTouchEvents;
    private View emptyView;
    private FastScroll fastScroll;
    private boolean foreIntercept;
    /* access modifiers changed from: private */
    public GestureDetector gestureDetector;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private boolean hiddenByEmptyView;
    private boolean ignoreOnScroll;
    /* access modifiers changed from: private */
    public boolean instantClick;
    /* access modifiers changed from: private */
    public boolean interceptedByChild;
    private boolean isChildViewEnabled;
    private long lastAlphaAnimationTime;
    /* access modifiers changed from: private */
    public boolean longPressCalled;
    private RecyclerView.AdapterDataObserver observer;
    private OnInterceptTouchListener onInterceptTouchListener;
    /* access modifiers changed from: private */
    public OnItemClickListener onItemClickListener;
    /* access modifiers changed from: private */
    public OnItemClickListenerExtended onItemClickListenerExtended;
    /* access modifiers changed from: private */
    public OnItemLongClickListener onItemLongClickListener;
    /* access modifiers changed from: private */
    public OnItemLongClickListenerExtended onItemLongClickListenerExtended;
    /* access modifiers changed from: private */
    public RecyclerView.OnScrollListener onScrollListener;
    private IntReturnCallback pendingHighlightPosition;
    private View pinnedHeader;
    private float pinnedHeaderShadowAlpha;
    private Drawable pinnedHeaderShadowDrawable;
    private float pinnedHeaderShadowTargetAlpha;
    /* access modifiers changed from: private */
    public Runnable removeHighlighSelectionRunnable;
    private boolean scrollEnabled;
    /* access modifiers changed from: private */
    public boolean scrollingByUser;
    /* access modifiers changed from: private */
    public int sectionOffset;
    private SectionsAdapter sectionsAdapter;
    private int sectionsCount;
    private int sectionsType;
    /* access modifiers changed from: private */
    public Runnable selectChildRunnable;
    /* access modifiers changed from: private */
    public Drawable selectorDrawable;
    /* access modifiers changed from: private */
    public int selectorPosition;
    /* access modifiers changed from: private */
    public Rect selectorRect;
    /* access modifiers changed from: private */
    public boolean selfOnLayout;
    private int startSection;
    private boolean wasPressed;

    public static abstract class FastScrollAdapter extends SelectionAdapter {
        public abstract String getLetter(int i);

        public abstract int getPositionForScrollProgress(float f);
    }

    public interface IntReturnCallback {
        int run();
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemClickListenerExtended {
        void onItemClick(View view, int i, float f, float f2);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    public interface OnItemLongClickListenerExtended {
        boolean onItemClick(View view, int i, float f, float f2);

        void onLongClickRelease();

        void onMove(float f, float f2);
    }

    public static abstract class SelectionAdapter extends RecyclerView.Adapter {
        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder);

        public int getSelectionBottomPadding(View view) {
            return 0;
        }
    }

    public static abstract class SectionsAdapter extends FastScrollAdapter {
        private int count;
        private SparseIntArray sectionCache;
        private int sectionCount;
        private SparseIntArray sectionCountCache;
        private SparseIntArray sectionPositionCache;

        public abstract int getCountForSection(int i);

        public abstract Object getItem(int i, int i2);

        public abstract int getItemViewType(int i, int i2);

        public abstract int getSectionCount();

        public abstract View getSectionHeaderView(int i, View view);

        public abstract boolean isEnabled(int i, int i2);

        public abstract void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder);

        private void cleanupCache() {
            SparseIntArray sparseIntArray = this.sectionCache;
            if (sparseIntArray == null) {
                this.sectionCache = new SparseIntArray();
                this.sectionPositionCache = new SparseIntArray();
                this.sectionCountCache = new SparseIntArray();
            } else {
                sparseIntArray.clear();
                this.sectionPositionCache.clear();
                this.sectionCountCache.clear();
            }
            this.count = -1;
            this.sectionCount = -1;
        }

        public void notifySectionsChanged() {
            cleanupCache();
        }

        public SectionsAdapter() {
            cleanupCache();
        }

        public void notifyDataSetChanged() {
            cleanupCache();
            super.notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return isEnabled(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        public int getItemCount() {
            int i = this.count;
            if (i >= 0) {
                return i;
            }
            this.count = 0;
            int N = internalGetSectionCount();
            for (int i2 = 0; i2 < N; i2++) {
                this.count += internalGetCountForSection(i2);
            }
            return this.count;
        }

        public final Object getItem(int position) {
            return getItem(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        public int getItemViewType(int position) {
            return getItemViewType(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            onBindViewHolder(getSectionForPosition(position), getPositionInSectionForPosition(position), holder);
        }

        private int internalGetCountForSection(int section) {
            int cachedSectionCount = this.sectionCountCache.get(section, Integer.MAX_VALUE);
            if (cachedSectionCount != Integer.MAX_VALUE) {
                return cachedSectionCount;
            }
            int sectionCount2 = getCountForSection(section);
            this.sectionCountCache.put(section, sectionCount2);
            return sectionCount2;
        }

        private int internalGetSectionCount() {
            int i = this.sectionCount;
            if (i >= 0) {
                return i;
            }
            int sectionCount2 = getSectionCount();
            this.sectionCount = sectionCount2;
            return sectionCount2;
        }

        public final int getSectionForPosition(int position) {
            int cachedSection = this.sectionCache.get(position, Integer.MAX_VALUE);
            if (cachedSection != Integer.MAX_VALUE) {
                return cachedSection;
            }
            int sectionStart = 0;
            int i = 0;
            int N = internalGetSectionCount();
            while (i < N) {
                int sectionEnd = sectionStart + internalGetCountForSection(i);
                if (position < sectionStart || position >= sectionEnd) {
                    sectionStart = sectionEnd;
                    i++;
                } else {
                    this.sectionCache.put(position, i);
                    return i;
                }
            }
            return -1;
        }

        public int getPositionInSectionForPosition(int position) {
            int cachedPosition = this.sectionPositionCache.get(position, Integer.MAX_VALUE);
            if (cachedPosition != Integer.MAX_VALUE) {
                return cachedPosition;
            }
            int sectionStart = 0;
            int i = 0;
            int N = internalGetSectionCount();
            while (i < N) {
                int sectionEnd = sectionStart + internalGetCountForSection(i);
                if (position < sectionStart || position >= sectionEnd) {
                    sectionStart = sectionEnd;
                    i++;
                } else {
                    int positionInSection = position - sectionStart;
                    this.sectionPositionCache.put(position, positionInSection);
                    return positionInSection;
                }
            }
            return -1;
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    private class FastScroll extends View {
        private float bubbleProgress;
        private int[] colors = new int[6];
        private String currentLetter;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private TextPaint letterPaint = new TextPaint(1);
        private StaticLayout oldLetterLayout;
        private Paint paint = new Paint(1);
        private Path path = new Path();
        private boolean pressed;
        private float progress;
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private int scrollX;
        private float startDy;
        private float textX;
        private float textY;

        public FastScroll(Context context) {
            super(context);
            this.letterPaint.setTextSize((float) AndroidUtilities.dp(45.0f));
            for (int a = 0; a < 8; a++) {
                this.radii[a] = (float) AndroidUtilities.dp(44.0f);
            }
            this.scrollX = AndroidUtilities.dp(LocaleController.isRTL ? 10.0f : 117.0f);
            updateColors();
        }

        /* access modifiers changed from: private */
        public void updateColors() {
            int inactive = Theme.getColor(Theme.key_fastScrollInactive);
            int active = Theme.getColor(Theme.key_fastScrollActive);
            this.paint.setColor(inactive);
            this.letterPaint.setColor(Theme.getColor(Theme.key_fastScrollText));
            this.colors[0] = Color.red(inactive);
            this.colors[1] = Color.red(active);
            this.colors[2] = Color.green(inactive);
            this.colors[3] = Color.green(active);
            this.colors[4] = Color.blue(inactive);
            this.colors[5] = Color.blue(active);
            invalidate();
        }

        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action != 2) {
                        if (action != 3) {
                            return super.onTouchEvent(event);
                        }
                    } else if (!this.pressed) {
                        return true;
                    } else {
                        float newY = event.getY();
                        float minY = ((float) AndroidUtilities.dp(12.0f)) + this.startDy;
                        float maxY = ((float) (getMeasuredHeight() - AndroidUtilities.dp(42.0f))) + this.startDy;
                        if (newY < minY) {
                            newY = minY;
                        } else if (newY > maxY) {
                            newY = maxY;
                        }
                        this.lastY = newY;
                        float measuredHeight = this.progress + ((newY - this.lastY) / ((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))));
                        this.progress = measuredHeight;
                        if (measuredHeight < 0.0f) {
                            this.progress = 0.0f;
                        } else if (measuredHeight > 1.0f) {
                            this.progress = 1.0f;
                        }
                        getCurrentLetter();
                        invalidate();
                        return true;
                    }
                }
                this.pressed = false;
                this.lastUpdateTime = System.currentTimeMillis();
                invalidate();
                return true;
            }
            float x = event.getX();
            this.lastY = event.getY();
            float currentY = ((float) Math.ceil((double) (((float) (getMeasuredHeight() - AndroidUtilities.dp(54.0f))) * this.progress))) + ((float) AndroidUtilities.dp(12.0f));
            if ((!LocaleController.isRTL || x <= ((float) AndroidUtilities.dp(25.0f))) && (LocaleController.isRTL || x >= ((float) AndroidUtilities.dp(107.0f)))) {
                float f = this.lastY;
                if (f >= currentY && f <= ((float) AndroidUtilities.dp(30.0f)) + currentY) {
                    this.startDy = this.lastY - currentY;
                    this.pressed = true;
                    this.lastUpdateTime = System.currentTimeMillis();
                    getCurrentLetter();
                    invalidate();
                    return true;
                }
            }
            return false;
        }

        private void getCurrentLetter() {
            RecyclerView.LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    RecyclerView.Adapter adapter = RecyclerListView.this.getAdapter();
                    if (adapter instanceof FastScrollAdapter) {
                        FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                        int position = fastScrollAdapter.getPositionForScrollProgress(this.progress);
                        linearLayoutManager.scrollToPositionWithOffset(position, RecyclerListView.this.sectionOffset);
                        String newLetter = fastScrollAdapter.getLetter(position);
                        if (newLetter == null) {
                            StaticLayout staticLayout = this.letterLayout;
                            if (staticLayout != null) {
                                this.oldLetterLayout = staticLayout;
                            }
                            this.letterLayout = null;
                        } else if (!newLetter.equals(this.currentLetter)) {
                            StaticLayout staticLayout2 = r7;
                            StaticLayout staticLayout3 = new StaticLayout(newLetter, this.letterPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            this.letterLayout = staticLayout2;
                            this.oldLetterLayout = null;
                            if (staticLayout2.getLineCount() > 0) {
                                float lineWidth = this.letterLayout.getLineWidth(0);
                                float lineLeft = this.letterLayout.getLineLeft(0);
                                if (LocaleController.isRTL) {
                                    this.textX = (((float) AndroidUtilities.dp(10.0f)) + ((((float) AndroidUtilities.dp(88.0f)) - this.letterLayout.getLineWidth(0)) / 2.0f)) - this.letterLayout.getLineLeft(0);
                                } else {
                                    this.textX = ((((float) AndroidUtilities.dp(88.0f)) - this.letterLayout.getLineWidth(0)) / 2.0f) - this.letterLayout.getLineLeft(0);
                                }
                                this.textY = (float) ((AndroidUtilities.dp(88.0f) - this.letterLayout.getHeight()) / 2);
                            }
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(132.0f), View.MeasureSpec.getSize(heightMeasureSpec));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0129, code lost:
            if (r8[6] == r9) goto L_0x012b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x013b, code lost:
            if (r8[4] == r9) goto L_0x0194;
         */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0141  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x014d  */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x0162  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x016a  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x0171  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0174  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x0199  */
        /* JADX WARNING: Removed duplicated region for block: B:40:0x019d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r20) {
            /*
                r19 = this;
                r0 = r19
                r1 = r20
                android.graphics.Paint r2 = r0.paint
                int[] r3 = r0.colors
                r4 = 0
                r5 = r3[r4]
                r6 = 1
                r7 = r3[r6]
                r8 = r3[r4]
                int r7 = r7 - r8
                float r7 = (float) r7
                float r8 = r0.bubbleProgress
                float r7 = r7 * r8
                int r7 = (int) r7
                int r5 = r5 + r7
                r7 = 2
                r9 = r3[r7]
                r10 = 3
                r11 = r3[r10]
                r12 = r3[r7]
                int r11 = r11 - r12
                float r11 = (float) r11
                float r11 = r11 * r8
                int r11 = (int) r11
                int r9 = r9 + r11
                r11 = 4
                r12 = r3[r11]
                r13 = 5
                r14 = r3[r13]
                r3 = r3[r11]
                int r14 = r14 - r3
                float r3 = (float) r14
                float r3 = r3 * r8
                int r3 = (int) r3
                int r12 = r12 + r3
                r3 = 255(0xff, float:3.57E-43)
                int r3 = android.graphics.Color.argb(r3, r5, r9, r12)
                r2.setColor(r3)
                int r2 = r19.getMeasuredHeight()
                r3 = 1113063424(0x42580000, float:54.0)
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                int r2 = r2 - r3
                float r2 = (float) r2
                float r3 = r0.progress
                float r2 = r2 * r3
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r2 = (int) r2
                android.graphics.RectF r3 = r0.rect
                int r5 = r0.scrollX
                float r5 = (float) r5
                r8 = 1094713344(0x41400000, float:12.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r9 = r9 + r2
                float r9 = (float) r9
                int r12 = r0.scrollX
                r14 = 1084227584(0x40a00000, float:5.0)
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                int r12 = r12 + r14
                float r12 = (float) r12
                r14 = 1109917696(0x42280000, float:42.0)
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                int r14 = r14 + r2
                float r14 = (float) r14
                r3.set(r5, r9, r12, r14)
                android.graphics.RectF r3 = r0.rect
                r5 = 1073741824(0x40000000, float:2.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                float r9 = (float) r9
                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                android.graphics.Paint r12 = r0.paint
                r1.drawRoundRect(r3, r9, r5, r12)
                boolean r3 = r0.pressed
                r5 = 1065353216(0x3f800000, float:1.0)
                r9 = 0
                if (r3 != 0) goto L_0x0095
                float r3 = r0.bubbleProgress
                int r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r3 == 0) goto L_0x01bf
            L_0x0095:
                android.graphics.Paint r3 = r0.paint
                r12 = 1132396544(0x437f0000, float:255.0)
                float r14 = r0.bubbleProgress
                float r14 = r14 * r12
                int r12 = (int) r14
                r3.setAlpha(r12)
                r3 = 1106247680(0x41f00000, float:30.0)
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                int r3 = r3 + r2
                r12 = 1110966272(0x42380000, float:46.0)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
                int r2 = r2 - r12
                r12 = 0
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                if (r2 > r14) goto L_0x00c0
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                int r14 = r14 - r2
                float r12 = (float) r14
                int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            L_0x00c0:
                r8 = 1092616192(0x41200000, float:10.0)
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r14 = (float) r14
                float r15 = (float) r2
                r1.translate(r14, r15)
                r14 = 1105723392(0x41e80000, float:29.0)
                int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                float r15 = (float) r15
                r16 = 1109393408(0x42200000, float:40.0)
                r17 = 1082130432(0x40800000, float:4.0)
                r18 = 1110441984(0x42300000, float:44.0)
                int r15 = (r12 > r15 ? 1 : (r12 == r15 ? 0 : -1))
                if (r15 > 0) goto L_0x00f6
                int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
                float r15 = (float) r15
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
                float r9 = (float) r9
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                float r14 = (float) r14
                float r14 = r12 / r14
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
                float r8 = (float) r8
                float r14 = r14 * r8
                float r9 = r9 + r14
                goto L_0x0118
            L_0x00f6:
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                float r8 = (float) r8
                float r12 = r12 - r8
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
                float r9 = (float) r8
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
                float r8 = (float) r8
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                float r14 = (float) r14
                float r14 = r12 / r14
                float r14 = r5 - r14
                int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
                float r15 = (float) r15
                float r14 = r14 * r15
                float r15 = r8 + r14
            L_0x0118:
                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r14 = 6
                if (r8 == 0) goto L_0x012b
                float[] r8 = r0.radii
                r16 = r8[r4]
                int r16 = (r16 > r15 ? 1 : (r16 == r15 ? 0 : -1))
                if (r16 != 0) goto L_0x013d
                r8 = r8[r14]
                int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r8 != 0) goto L_0x013d
            L_0x012b:
                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r8 != 0) goto L_0x0194
                float[] r8 = r0.radii
                r16 = r8[r7]
                int r16 = (r16 > r15 ? 1 : (r16 == r15 ? 0 : -1))
                if (r16 != 0) goto L_0x013d
                r8 = r8[r11]
                int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r8 == 0) goto L_0x0194
            L_0x013d:
                boolean r8 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r8 == 0) goto L_0x014d
                float[] r7 = r0.radii
                r7[r6] = r15
                r7[r4] = r15
                r4 = 7
                r7[r4] = r9
                r7[r14] = r9
                goto L_0x0157
            L_0x014d:
                float[] r4 = r0.radii
                r4[r10] = r15
                r4[r7] = r15
                r4[r13] = r9
                r4[r11] = r9
            L_0x0157:
                android.graphics.Path r4 = r0.path
                r4.reset()
                android.graphics.RectF r4 = r0.rect
                boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x016a
                r6 = 1092616192(0x41200000, float:10.0)
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                goto L_0x016b
            L_0x016a:
                r6 = 0
            L_0x016b:
                boolean r7 = im.bclpbkiauv.messenger.LocaleController.isRTL
                r8 = 1118830592(0x42b00000, float:88.0)
                if (r7 == 0) goto L_0x0174
                r7 = 1120141312(0x42c40000, float:98.0)
                goto L_0x0176
            L_0x0174:
                r7 = 1118830592(0x42b00000, float:88.0)
            L_0x0176:
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r7
                int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                r10 = 0
                r4.set(r6, r10, r7, r8)
                android.graphics.Path r4 = r0.path
                android.graphics.RectF r6 = r0.rect
                float[] r7 = r0.radii
                android.graphics.Path$Direction r8 = android.graphics.Path.Direction.CW
                r4.addRoundRect(r6, r7, r8)
                android.graphics.Path r4 = r0.path
                r4.close()
            L_0x0194:
                android.text.StaticLayout r4 = r0.letterLayout
                if (r4 == 0) goto L_0x0199
                goto L_0x019b
            L_0x0199:
                android.text.StaticLayout r4 = r0.oldLetterLayout
            L_0x019b:
                if (r4 == 0) goto L_0x01bf
                r20.save()
                float r6 = r0.bubbleProgress
                int r7 = r0.scrollX
                float r7 = (float) r7
                int r8 = r3 - r2
                float r8 = (float) r8
                r1.scale(r6, r6, r7, r8)
                android.graphics.Path r6 = r0.path
                android.graphics.Paint r7 = r0.paint
                r1.drawPath(r6, r7)
                float r6 = r0.textX
                float r7 = r0.textY
                r1.translate(r6, r7)
                r4.draw(r1)
                r20.restore()
            L_0x01bf:
                boolean r3 = r0.pressed
                if (r3 == 0) goto L_0x01cd
                android.text.StaticLayout r3 = r0.letterLayout
                if (r3 == 0) goto L_0x01cd
                float r3 = r0.bubbleProgress
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 < 0) goto L_0x01dc
            L_0x01cd:
                boolean r3 = r0.pressed
                if (r3 == 0) goto L_0x01d5
                android.text.StaticLayout r3 = r0.letterLayout
                if (r3 != 0) goto L_0x021d
            L_0x01d5:
                float r3 = r0.bubbleProgress
                r4 = 0
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 <= 0) goto L_0x021d
            L_0x01dc:
                long r3 = java.lang.System.currentTimeMillis()
                long r6 = r0.lastUpdateTime
                long r6 = r3 - r6
                r8 = 0
                int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r10 < 0) goto L_0x01f0
                r8 = 17
                int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r10 <= 0) goto L_0x01f2
            L_0x01f0:
                r6 = 17
            L_0x01f2:
                r0.lastUpdateTime = r3
                r19.invalidate()
                boolean r8 = r0.pressed
                r9 = 1123024896(0x42f00000, float:120.0)
                if (r8 == 0) goto L_0x020f
                android.text.StaticLayout r8 = r0.letterLayout
                if (r8 == 0) goto L_0x020f
                float r8 = r0.bubbleProgress
                float r10 = (float) r6
                float r10 = r10 / r9
                float r8 = r8 + r10
                r0.bubbleProgress = r8
                int r8 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
                if (r8 <= 0) goto L_0x021d
                r0.bubbleProgress = r5
                goto L_0x021d
            L_0x020f:
                float r5 = r0.bubbleProgress
                float r8 = (float) r6
                float r8 = r8 / r9
                float r5 = r5 - r8
                r0.bubbleProgress = r5
                r8 = 0
                int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
                if (r5 >= 0) goto L_0x021d
                r0.bubbleProgress = r8
            L_0x021d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.RecyclerListView.FastScroll.onDraw(android.graphics.Canvas):void");
        }

        public void layout(int l, int t, int r, int b) {
            if (RecyclerListView.this.selfOnLayout) {
                super.layout(l, t, r, b);
            }
        }

        /* access modifiers changed from: private */
        public void setProgress(float value) {
            this.progress = value;
            invalidate();
        }
    }

    public boolean getLongPressCalled() {
        return this.longPressCalled;
    }

    private class RecyclerListViewItemClickListener implements RecyclerView.OnItemTouchListener {
        public RecyclerListViewItemClickListener(Context context) {
            GestureDetector unused = RecyclerListView.this.gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener(RecyclerListView.this) {
                public boolean onSingleTapUp(MotionEvent e) {
                    if (!(RecyclerListView.this.currentChildView == null || (RecyclerListView.this.onItemClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null))) {
                        RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, true);
                        View view = RecyclerListView.this.currentChildView;
                        int position = RecyclerListView.this.currentChildPosition;
                        float x = e.getX();
                        float y = e.getY();
                        if (RecyclerListView.this.instantClick && position != -1) {
                            view.playSoundEffect(0);
                            view.sendAccessibilityEvent(1);
                            if (RecyclerListView.this.onItemClickListener != null) {
                                RecyclerListView.this.onItemClickListener.onItemClick(view, position);
                            } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                RecyclerListView.this.onItemClickListenerExtended.onItemClick(view, position, x - view.getX(), y - view.getY());
                            }
                        }
                        final View view2 = view;
                        final int i = position;
                        final float f = x;
                        final float f2 = y;
                        AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new Runnable() {
                            public void run() {
                                if (this == RecyclerListView.this.clickRunnable) {
                                    Runnable unused = RecyclerListView.this.clickRunnable = null;
                                }
                                if (view2 != null) {
                                    RecyclerListView.this.onChildPressed(view2, false);
                                    if (!RecyclerListView.this.instantClick) {
                                        view2.playSoundEffect(0);
                                        view2.sendAccessibilityEvent(1);
                                        if (i == -1) {
                                            return;
                                        }
                                        if (RecyclerListView.this.onItemClickListener != null) {
                                            RecyclerListView.this.onItemClickListener.onItemClick(view2, i);
                                        } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                            OnItemClickListenerExtended access$500 = RecyclerListView.this.onItemClickListenerExtended;
                                            View view = view2;
                                            access$500.onItemClick(view, i, f - view.getX(), f2 - view2.getY());
                                        }
                                    }
                                }
                            }
                        }, (long) ViewConfiguration.getPressedStateDuration());
                        if (RecyclerListView.this.selectChildRunnable != null) {
                            View pressedChild = RecyclerListView.this.currentChildView;
                            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                            Runnable unused = RecyclerListView.this.selectChildRunnable = null;
                            View unused2 = RecyclerListView.this.currentChildView = null;
                            boolean unused3 = RecyclerListView.this.interceptedByChild = false;
                            RecyclerListView.this.removeSelection(pressedChild, e);
                        }
                    }
                    return true;
                }

                public void onLongPress(MotionEvent event) {
                    if (RecyclerListView.this.currentChildView != null && RecyclerListView.this.currentChildPosition != -1) {
                        if (RecyclerListView.this.onItemLongClickListener != null || RecyclerListView.this.onItemLongClickListenerExtended != null) {
                            View child = RecyclerListView.this.currentChildView;
                            if (RecyclerListView.this.onItemLongClickListener != null) {
                                if (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                                    child.performHapticFeedback(0);
                                    child.sendAccessibilityEvent(2);
                                }
                            } else if (RecyclerListView.this.onItemLongClickListenerExtended != null && RecyclerListView.this.onItemLongClickListenerExtended.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition, event.getX() - RecyclerListView.this.currentChildView.getX(), event.getY() - RecyclerListView.this.currentChildView.getY())) {
                                child.performHapticFeedback(0);
                                child.sendAccessibilityEvent(2);
                                boolean unused = RecyclerListView.this.longPressCalled = true;
                            }
                        }
                    }
                }

                public boolean onDown(MotionEvent e) {
                    return false;
                }

                public void onShowPress(MotionEvent e) {
                }

                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });
            RecyclerListView.this.gestureDetector.setIsLongpressEnabled(false);
        }

        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
            MotionEvent motionEvent = event;
            int action = event.getActionMasked();
            boolean isScrollIdle = RecyclerListView.this.getScrollState() == 0;
            if (action != 0 && action != 5) {
                RecyclerView recyclerView = view;
            } else if (RecyclerListView.this.currentChildView != null || !isScrollIdle) {
                RecyclerView recyclerView2 = view;
            } else {
                float ex = event.getX();
                float ey = event.getY();
                boolean unused = RecyclerListView.this.longPressCalled = false;
                if (RecyclerListView.this.allowSelectChildAtPosition(ex, ey)) {
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    View unused2 = recyclerListView.currentChildView = recyclerListView.findChildViewUnder(ex, ey);
                }
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    float x = event.getX() - ((float) RecyclerListView.this.currentChildView.getLeft());
                    float y = event.getY() - ((float) RecyclerListView.this.currentChildView.getTop());
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    int i = viewGroup.getChildCount() - 1;
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        View child = viewGroup.getChildAt(i);
                        if (x >= ((float) child.getLeft()) && x <= ((float) child.getRight()) && y >= ((float) child.getTop()) && y <= ((float) child.getBottom()) && child.isClickable()) {
                            View unused3 = RecyclerListView.this.currentChildView = null;
                            break;
                        }
                        i--;
                    }
                }
                int unused4 = RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    RecyclerListView recyclerListView2 = RecyclerListView.this;
                    int unused5 = recyclerListView2.currentChildPosition = view.getChildPosition(recyclerListView2.currentChildView);
                    MotionEvent childEvent = MotionEvent.obtain(0, 0, event.getActionMasked(), event.getX() - ((float) RecyclerListView.this.currentChildView.getLeft()), event.getY() - ((float) RecyclerListView.this.currentChildView.getTop()), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(childEvent)) {
                        boolean unused6 = RecyclerListView.this.interceptedByChild = true;
                    }
                    childEvent.recycle();
                } else {
                    RecyclerView recyclerView3 = view;
                }
            }
            if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.interceptedByChild || motionEvent == null)) {
                try {
                    RecyclerListView.this.gestureDetector.onTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            if (action == 0 || action == 5) {
                if (RecyclerListView.this.interceptedByChild || RecyclerListView.this.currentChildView == null) {
                    return false;
                }
                Runnable unused7 = RecyclerListView.this.selectChildRunnable = new Runnable() {
                    public final void run() {
                        RecyclerListView.RecyclerListViewItemClickListener.this.lambda$onInterceptTouchEvent$0$RecyclerListView$RecyclerListViewItemClickListener();
                    }
                };
                AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, (long) ViewConfiguration.getTapTimeout());
                if (RecyclerListView.this.currentChildView.isEnabled()) {
                    RecyclerListView recyclerListView3 = RecyclerListView.this;
                    recyclerListView3.positionSelector(recyclerListView3.currentChildPosition, RecyclerListView.this.currentChildView);
                    if (RecyclerListView.this.selectorDrawable != null) {
                        Drawable d = RecyclerListView.this.selectorDrawable.getCurrent();
                        if (d instanceof TransitionDrawable) {
                            if (RecyclerListView.this.onItemLongClickListener == null && RecyclerListView.this.onItemClickListenerExtended == null) {
                                ((TransitionDrawable) d).resetTransition();
                            } else {
                                ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                            }
                        }
                        if (Build.VERSION.SDK_INT >= 21) {
                            RecyclerListView.this.selectorDrawable.setHotspot(event.getX(), event.getY());
                        }
                    }
                    RecyclerListView.this.updateSelectorState();
                    return false;
                }
                RecyclerListView.this.selectorRect.setEmpty();
                return false;
            } else if ((action != 1 && action != 6 && action != 3 && isScrollIdle) || RecyclerListView.this.currentChildView == null) {
                return false;
            } else {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    Runnable unused8 = RecyclerListView.this.selectChildRunnable = null;
                }
                View pressedChild = RecyclerListView.this.currentChildView;
                RecyclerListView recyclerListView4 = RecyclerListView.this;
                recyclerListView4.onChildPressed(recyclerListView4.currentChildView, false);
                View unused9 = RecyclerListView.this.currentChildView = null;
                boolean unused10 = RecyclerListView.this.interceptedByChild = false;
                RecyclerListView.this.removeSelection(pressedChild, motionEvent);
                if ((action != 1 && action != 6 && action != 3) || RecyclerListView.this.onItemLongClickListenerExtended == null || !RecyclerListView.this.longPressCalled) {
                    return false;
                }
                RecyclerListView.this.onItemLongClickListenerExtended.onLongClickRelease();
                boolean unused11 = RecyclerListView.this.longPressCalled = false;
                return false;
            }
        }

        public /* synthetic */ void lambda$onInterceptTouchEvent$0$RecyclerListView$RecyclerListViewItemClickListener() {
            if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                RecyclerListView recyclerListView = RecyclerListView.this;
                recyclerListView.onChildPressed(recyclerListView.currentChildView, true);
                Runnable unused = RecyclerListView.this.selectChildRunnable = null;
            }
        }

        public void onTouchEvent(RecyclerView view, MotionEvent event) {
        }

        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

    public View findChildViewUnder(float x, float y) {
        int count = getChildCount();
        int a = 0;
        while (a < 2) {
            for (int i = count - 1; i >= 0; i--) {
                View child = getChildAt(i);
                float translationY = 0.0f;
                float translationX = a == 0 ? child.getTranslationX() : 0.0f;
                if (a == 0) {
                    translationY = child.getTranslationY();
                }
                if (x >= ((float) child.getLeft()) + translationX && x <= ((float) child.getRight()) + translationX && y >= ((float) child.getTop()) + translationY && y <= ((float) child.getBottom()) + translationY) {
                    return child;
                }
            }
            a++;
        }
        return null;
    }

    public void setDisableHighlightState(boolean value) {
        this.disableHighlightState = value;
    }

    /* access modifiers changed from: protected */
    public View getPressedChildView() {
        return this.currentChildView;
    }

    /* access modifiers changed from: protected */
    public void onChildPressed(View child, boolean pressed) {
        if (!this.disableHighlightState) {
            child.setPressed(pressed);
        }
    }

    /* access modifiers changed from: protected */
    public boolean allowSelectChildAtPosition(float x, float y) {
        return true;
    }

    /* access modifiers changed from: private */
    public void removeSelection(View pressedChild, MotionEvent event) {
        if (pressedChild != null) {
            if (pressedChild == null || !pressedChild.isEnabled()) {
                this.selectorRect.setEmpty();
            } else {
                positionSelector(this.currentChildPosition, pressedChild);
                Drawable drawable = this.selectorDrawable;
                if (drawable != null) {
                    Drawable d = drawable.getCurrent();
                    if (d instanceof TransitionDrawable) {
                        ((TransitionDrawable) d).resetTransition();
                    }
                    if (event != null && Build.VERSION.SDK_INT >= 21) {
                        this.selectorDrawable.setHotspot(event.getX(), event.getY());
                    }
                }
            }
            updateSelectorState();
        }
    }

    public void cancelClickRunnables(boolean uncheck) {
        Runnable runnable = this.selectChildRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.selectChildRunnable = null;
        }
        View view = this.currentChildView;
        if (view != null) {
            View child = this.currentChildView;
            if (uncheck) {
                onChildPressed(view, false);
            }
            this.currentChildView = null;
            removeSelection(child, (MotionEvent) null);
        }
        Runnable runnable2 = this.clickRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public int[] getResourceDeclareStyleableIntArray(String packageName, String name) {
        try {
            Field f = Class.forName(packageName + ".R$styleable").getField(name);
            if (f != null) {
                return (int[]) f.get((Object) null);
            }
        } catch (Throwable th) {
        }
        return null;
    }

    public RecyclerListView(Context context) {
        this(context, (AttributeSet) null);
    }

    public RecyclerListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.currentFirst = -1;
        this.currentVisible = -1;
        this.selectorRect = new Rect();
        this.scrollEnabled = true;
        this.observer = new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                RecyclerListView.this.checkIfEmpty();
                int unused = RecyclerListView.this.currentFirst = -1;
                if (RecyclerListView.this.removeHighlighSelectionRunnable == null) {
                    RecyclerListView.this.selectorRect.setEmpty();
                }
                RecyclerListView.this.invalidate();
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                RecyclerListView.this.checkIfEmpty();
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                RecyclerListView.this.checkIfEmpty();
            }
        };
        this.foreIntercept = false;
        setGlowColor(Theme.getColor(Theme.key_actionBarDefault));
        Drawable selectorDrawable2 = Theme.getSelectorDrawable(false);
        this.selectorDrawable = selectorDrawable2;
        selectorDrawable2.setCallback(this);
        try {
            if (!gotAttributes) {
                attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                gotAttributes = true;
            }
            TypedArray a = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", new Class[]{TypedArray.class}).invoke(this, new Object[]{a});
            a.recycle();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        super.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = false;
                if (!(newState == 0 || RecyclerListView.this.currentChildView == null)) {
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        Runnable unused = RecyclerListView.this.selectChildRunnable = null;
                    }
                    MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    try {
                        RecyclerListView.this.gestureDetector.onTouchEvent(event);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    RecyclerListView.this.currentChildView.onTouchEvent(event);
                    event.recycle();
                    View child = RecyclerListView.this.currentChildView;
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    recyclerListView.onChildPressed(recyclerListView.currentChildView, false);
                    View unused2 = RecyclerListView.this.currentChildView = null;
                    RecyclerListView.this.removeSelection(child, (MotionEvent) null);
                    boolean unused3 = RecyclerListView.this.interceptedByChild = false;
                }
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
                RecyclerListView recyclerListView2 = RecyclerListView.this;
                if (newState == 1 || newState == 2) {
                    z = true;
                }
                boolean unused4 = recyclerListView2.scrollingByUser = z;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrolled(recyclerView, dx, dy);
                }
                if (RecyclerListView.this.selectorPosition != -1) {
                    RecyclerListView.this.selectorRect.offset(-dx, -dy);
                    RecyclerListView.this.selectorDrawable.setBounds(RecyclerListView.this.selectorRect);
                    RecyclerListView.this.invalidate();
                } else {
                    RecyclerListView.this.selectorRect.setEmpty();
                }
                RecyclerListView.this.checkSection();
            }
        });
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (this.fastScroll != null) {
            int height = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            this.fastScroll.getLayoutParams().height = height;
            this.fastScroll.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.fastScroll != null) {
            this.selfOnLayout = true;
            int t2 = t + getPaddingTop();
            if (LocaleController.isRTL) {
                FastScroll fastScroll2 = this.fastScroll;
                fastScroll2.layout(0, t2, fastScroll2.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + t2);
            } else {
                int x = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
                FastScroll fastScroll3 = this.fastScroll;
                fastScroll3.layout(x, t2, fastScroll3.getMeasuredWidth() + x, this.fastScroll.getMeasuredHeight() + t2);
            }
            this.selfOnLayout = false;
        }
        checkSection();
        IntReturnCallback intReturnCallback = this.pendingHighlightPosition;
        if (intReturnCallback != null) {
            highlightRowInternal(intReturnCallback, false);
        }
    }

    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public void setSelectorDrawableColor(int color) {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.setCallback((Drawable.Callback) null);
        }
        Drawable selectorDrawable2 = Theme.getSelectorDrawable(color, false);
        this.selectorDrawable = selectorDrawable2;
        selectorDrawable2.setCallback(this);
    }

    public void checkSection() {
        RecyclerView.ViewHolder holder;
        int firstVisibleItem;
        int startSection2;
        RecyclerView.ViewHolder holder2;
        int childCount;
        int lastVisibleItem;
        RecyclerView.ViewHolder holder3;
        int headerTop;
        if ((this.scrollingByUser && this.fastScroll != null) || (this.sectionsType != 0 && this.sectionsAdapter != null)) {
            RecyclerView.LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() != 1) {
                } else if (this.sectionsAdapter != null) {
                    int paddingTop = getPaddingTop();
                    int i = this.sectionsType;
                    if (i == 1) {
                        int childCount2 = getChildCount();
                        int maxBottom = 0;
                        int minBottom = Integer.MAX_VALUE;
                        View minChild = null;
                        int minBottomSection = Integer.MAX_VALUE;
                        for (int a = 0; a < childCount2; a++) {
                            View child = getChildAt(a);
                            int bottom = child.getBottom();
                            if (bottom > this.sectionOffset + paddingTop) {
                                if (bottom < minBottom) {
                                    minBottom = bottom;
                                    minChild = child;
                                }
                                int maxBottom2 = Math.max(maxBottom, bottom);
                                if (bottom >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom < minBottomSection) {
                                    minBottomSection = bottom;
                                    maxBottom = maxBottom2;
                                } else {
                                    maxBottom = maxBottom2;
                                }
                            }
                        }
                        if (minChild != null && (holder2 = getChildViewHolder(minChild)) != null) {
                            int firstVisibleItem2 = holder2.getAdapterPosition();
                            int lastVisibleItem2 = linearLayoutManager.findLastVisibleItemPosition();
                            int visibleItemCount = Math.abs(lastVisibleItem2 - firstVisibleItem2) + 1;
                            if (!this.scrollingByUser || this.fastScroll == null) {
                            } else {
                                RecyclerView.Adapter adapter = getAdapter();
                                if (adapter instanceof FastScrollAdapter) {
                                    RecyclerView.LayoutManager layoutManager2 = layoutManager;
                                    this.fastScroll.setProgress(Math.min(1.0f, ((float) firstVisibleItem2) / ((float) ((adapter.getItemCount() - visibleItemCount) + 1))));
                                }
                            }
                            this.headersCache.addAll(this.headers);
                            this.headers.clear();
                            if (this.sectionsAdapter.getItemCount() != 0) {
                                if (!(this.currentFirst == firstVisibleItem2 && this.currentVisible == visibleItemCount)) {
                                    this.currentFirst = firstVisibleItem2;
                                    this.currentVisible = visibleItemCount;
                                    this.sectionsCount = 1;
                                    int sectionForPosition = this.sectionsAdapter.getSectionForPosition(firstVisibleItem2);
                                    this.startSection = sectionForPosition;
                                    int itemNum = (this.sectionsAdapter.getCountForSection(sectionForPosition) + firstVisibleItem2) - this.sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem2);
                                    while (itemNum < firstVisibleItem2 + visibleItemCount) {
                                        itemNum += this.sectionsAdapter.getCountForSection(this.startSection + this.sectionsCount);
                                        this.sectionsCount++;
                                    }
                                }
                                int itemNum2 = firstVisibleItem2;
                                int a2 = this.startSection;
                                while (a2 < this.startSection + this.sectionsCount) {
                                    View header = null;
                                    if (!this.headersCache.isEmpty()) {
                                        childCount = childCount2;
                                        this.headersCache.remove(0);
                                        header = this.headersCache.get(0);
                                    } else {
                                        childCount = childCount2;
                                    }
                                    View header2 = getSectionHeaderView(a2, header);
                                    this.headers.add(header2);
                                    int count = this.sectionsAdapter.getCountForSection(a2);
                                    if (a2 == this.startSection) {
                                        int pos = this.sectionsAdapter.getPositionInSectionForPosition(itemNum2);
                                        holder3 = holder2;
                                        if (pos == count - 1) {
                                            header2.setTag(Integer.valueOf((-header2.getHeight()) + paddingTop));
                                            lastVisibleItem = lastVisibleItem2;
                                        } else if (pos == count - 2) {
                                            View child2 = getChildAt(itemNum2 - firstVisibleItem2);
                                            if (child2 != null) {
                                                View view = child2;
                                                headerTop = child2.getTop() + paddingTop;
                                            } else {
                                                View view2 = child2;
                                                headerTop = -AndroidUtilities.dp(100.0f);
                                            }
                                            if (headerTop < 0) {
                                                lastVisibleItem = lastVisibleItem2;
                                                header2.setTag(Integer.valueOf(headerTop));
                                            } else {
                                                lastVisibleItem = lastVisibleItem2;
                                                int i2 = headerTop;
                                                header2.setTag(0);
                                            }
                                        } else {
                                            lastVisibleItem = lastVisibleItem2;
                                            header2.setTag(0);
                                        }
                                        itemNum2 += count - this.sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem2);
                                    } else {
                                        holder3 = holder2;
                                        lastVisibleItem = lastVisibleItem2;
                                        View child3 = getChildAt(itemNum2 - firstVisibleItem2);
                                        if (child3 != null) {
                                            header2.setTag(Integer.valueOf(child3.getTop() + paddingTop));
                                        } else {
                                            header2.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0f)));
                                        }
                                        itemNum2 += count;
                                    }
                                    a2++;
                                    holder2 = holder3;
                                    childCount2 = childCount;
                                    lastVisibleItem2 = lastVisibleItem;
                                }
                                RecyclerView.ViewHolder viewHolder = holder2;
                                int i3 = lastVisibleItem2;
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    if (i == 2) {
                        this.pinnedHeaderShadowTargetAlpha = 0.0f;
                        if (this.sectionsAdapter.getItemCount() != 0) {
                            int childCount3 = getChildCount();
                            int maxBottom3 = 0;
                            int minBottom2 = Integer.MAX_VALUE;
                            View minChild2 = null;
                            int minBottomSection2 = Integer.MAX_VALUE;
                            View minChildSection = null;
                            for (int a3 = 0; a3 < childCount3; a3++) {
                                View child4 = getChildAt(a3);
                                int bottom2 = child4.getBottom();
                                if (bottom2 > this.sectionOffset + paddingTop) {
                                    if (bottom2 < minBottom2) {
                                        minBottom2 = bottom2;
                                        minChild2 = child4;
                                    }
                                    maxBottom3 = Math.max(maxBottom3, bottom2);
                                    if (bottom2 >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom2 < minBottomSection2) {
                                        minBottomSection2 = bottom2;
                                        minChildSection = child4;
                                    }
                                }
                            }
                            if (minChild2 != null && (holder = getChildViewHolder(minChild2)) != null && (startSection2 = this.sectionsAdapter.getSectionForPosition(firstVisibleItem)) >= 0) {
                                if (this.currentFirst != startSection2 || this.pinnedHeader == null) {
                                    this.pinnedHeader = getSectionHeaderView(startSection2, this.pinnedHeader);
                                    this.currentFirst = startSection2;
                                }
                                if (!(this.pinnedHeader == null || minChildSection == null || minChildSection.getClass() == this.pinnedHeader.getClass())) {
                                    this.pinnedHeaderShadowTargetAlpha = 1.0f;
                                }
                                int count2 = this.sectionsAdapter.getCountForSection(startSection2);
                                int pos2 = this.sectionsAdapter.getPositionInSectionForPosition((firstVisibleItem = holder.getAdapterPosition()));
                                int sectionOffsetY = (maxBottom3 == 0 || maxBottom3 >= getMeasuredHeight() - getPaddingBottom()) ? this.sectionOffset : 0;
                                if (pos2 == count2 - 1) {
                                    int headerHeight = this.pinnedHeader.getHeight();
                                    int headerTop2 = paddingTop;
                                    if (minChild2 != null) {
                                        int i4 = childCount3;
                                        int available = ((minChild2.getTop() - paddingTop) - this.sectionOffset) + minChild2.getHeight();
                                        if (available < headerHeight) {
                                            headerTop2 = available - headerHeight;
                                        }
                                    } else {
                                        headerTop2 = -AndroidUtilities.dp(100.0f);
                                    }
                                    if (headerTop2 < 0) {
                                        int i5 = maxBottom3;
                                        this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY + headerTop2));
                                    } else {
                                        this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY));
                                    }
                                } else {
                                    int i6 = maxBottom3;
                                    this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY));
                                }
                                invalidate();
                            }
                        }
                    }
                } else {
                    int firstVisibleItem3 = linearLayoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount2 = Math.abs(linearLayoutManager.findLastVisibleItemPosition() - firstVisibleItem3) + 1;
                    if (firstVisibleItem3 != -1 && this.scrollingByUser && this.fastScroll != null) {
                        RecyclerView.Adapter adapter2 = getAdapter();
                        if (adapter2 instanceof FastScrollAdapter) {
                            this.fastScroll.setProgress(Math.min(1.0f, ((float) firstVisibleItem3) / ((float) ((adapter2.getItemCount() - visibleItemCount2) + 1))));
                        }
                    }
                }
            }
        }
    }

    public void setListSelectorColor(int color) {
        Theme.setSelectorDrawableColor(this.selectorDrawable, color, true);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListenerExtended listener) {
        this.onItemClickListenerExtended = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        this.gestureDetector.setIsLongpressEnabled(listener != null);
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended listener) {
        this.onItemLongClickListenerExtended = listener;
        this.gestureDetector.setIsLongpressEnabled(listener != null);
    }

    public void setEmptyView(View view) {
        if (this.emptyView != view) {
            this.emptyView = view;
            checkIfEmpty();
        }
    }

    public View getEmptyView() {
        return this.emptyView;
    }

    public void invalidateViews() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).invalidate();
        }
    }

    public void updateFastScrollColors() {
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null) {
            fastScroll2.updateColors();
        }
    }

    public void setPinnedHeaderShadowDrawable(Drawable drawable) {
        this.pinnedHeaderShadowDrawable = drawable;
    }

    public boolean canScrollVertically(int direction) {
        return this.scrollEnabled && super.canScrollVertically(direction);
    }

    public void setScrollEnabled(boolean value) {
        this.scrollEnabled = value;
    }

    public void highlightRow(IntReturnCallback callback) {
        highlightRowInternal(callback, true);
    }

    private void highlightRowInternal(IntReturnCallback callback, boolean canHighlightLater) {
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
        }
        RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(callback.run());
        if (holder != null) {
            positionSelector(holder.getLayoutPosition(), holder.itemView);
            Drawable drawable = this.selectorDrawable;
            if (drawable != null) {
                Drawable d = drawable.getCurrent();
                if (d instanceof TransitionDrawable) {
                    if (this.onItemLongClickListener == null && this.onItemClickListenerExtended == null) {
                        ((TransitionDrawable) d).resetTransition();
                    } else {
                        ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                    }
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    this.selectorDrawable.setHotspot((float) (holder.itemView.getMeasuredWidth() / 2), (float) (holder.itemView.getMeasuredHeight() / 2));
                }
            }
            Drawable d2 = this.selectorDrawable;
            if (d2 != null && d2.isStateful() && this.selectorDrawable.setState(getDrawableStateForSelector())) {
                invalidateDrawable(this.selectorDrawable);
            }
            $$Lambda$RecyclerListView$e1qgmumORFN_38rTxTBzZMU4cjU r1 = new Runnable() {
                public final void run() {
                    RecyclerListView.this.lambda$highlightRowInternal$0$RecyclerListView();
                }
            };
            this.removeHighlighSelectionRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 700);
        } else if (canHighlightLater) {
            this.pendingHighlightPosition = callback;
        }
    }

    public /* synthetic */ void lambda$highlightRowInternal$0$RecyclerListView() {
        this.removeHighlighSelectionRunnable = null;
        this.pendingHighlightPosition = null;
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            Drawable d = drawable.getCurrent();
            if (d instanceof TransitionDrawable) {
                ((TransitionDrawable) d).resetTransition();
            }
        }
        Drawable d2 = this.selectorDrawable;
        if (d2 != null && d2.isStateful()) {
            this.selectorDrawable.setState(StateSet.NOTHING);
        }
    }

    public void setForeIntercept(boolean force) {
        this.foreIntercept = false;
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!isEnabled() || this.foreIntercept) {
            return false;
        }
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        OnInterceptTouchListener onInterceptTouchListener2 = this.onInterceptTouchListener;
        if ((onInterceptTouchListener2 == null || !onInterceptTouchListener2.onInterceptTouchEvent(e)) && !super.onInterceptTouchEvent(e)) {
            return false;
        }
        return true;
    }

    public void checkIfEmpty() {
        int i = 0;
        if (getAdapter() != null && this.emptyView != null) {
            boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            this.emptyView.setVisibility(emptyViewVisible ? 0 : 8);
            if (emptyViewVisible) {
                i = 4;
            }
            setVisibility(i);
            this.hiddenByEmptyView = true;
        } else if (this.hiddenByEmptyView && getVisibility() != 0) {
            setVisibility(0);
            this.hiddenByEmptyView = false;
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 0) {
            this.hiddenByEmptyView = false;
        }
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public RecyclerView.OnScrollListener getOnScrollListener() {
        return this.onScrollListener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener listener) {
        this.onInterceptTouchListener = listener;
    }

    public void setInstantClick(boolean value) {
        this.instantClick = value;
    }

    public void setDisallowInterceptTouchEvents(boolean value) {
        this.disallowInterceptTouchEvents = value;
    }

    public void setFastScrollEnabled() {
        this.fastScroll = new FastScroll(getContext());
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setFastScrollVisible(boolean value) {
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null) {
            fastScroll2.setVisibility(value ? 0 : 8);
        }
    }

    public void setSectionsType(int type) {
        this.sectionsType = type;
        if (type == 1) {
            this.headers = new ArrayList<>();
            this.headersCache = new ArrayList<>();
        }
    }

    public void setPinnedSectionOffsetY(int offset) {
        this.sectionOffset = offset;
        invalidate();
    }

    /* access modifiers changed from: private */
    public void positionSelector(int position, View sel) {
        positionSelector(position, sel, false, -1.0f, -1.0f);
    }

    private void positionSelector(int position, View sel, boolean manageHotspot, float x, float y) {
        int bottomPadding;
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
            this.pendingHighlightPosition = null;
        }
        if (this.selectorDrawable != null) {
            boolean positionChanged = position != this.selectorPosition;
            if (getAdapter() instanceof SelectionAdapter) {
                bottomPadding = ((SelectionAdapter) getAdapter()).getSelectionBottomPadding(sel);
            } else {
                bottomPadding = 0;
            }
            if (position != -1) {
                this.selectorPosition = position;
            }
            this.selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom() - bottomPadding);
            boolean enabled = sel.isEnabled();
            if (this.isChildViewEnabled != enabled) {
                this.isChildViewEnabled = enabled;
            }
            if (positionChanged) {
                this.selectorDrawable.setVisible(false, false);
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
            this.selectorDrawable.setBounds(this.selectorRect);
            if (positionChanged && getVisibility() == 0) {
                this.selectorDrawable.setVisible(true, false);
            }
            if (Build.VERSION.SDK_INT >= 21 && manageHotspot) {
                this.selectorDrawable.setHotspot(x, y);
            }
        }
    }

    public void hideSelector() {
        View view = this.currentChildView;
        if (view != null) {
            View child = this.currentChildView;
            onChildPressed(view, false);
            this.currentChildView = null;
            removeSelection(child, (MotionEvent) null);
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectorState() {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null && drawable.isStateful()) {
            if (this.currentChildView != null) {
                if (this.selectorDrawable.setState(getDrawableStateForSelector())) {
                    invalidateDrawable(this.selectorDrawable);
                }
            } else if (this.removeHighlighSelectionRunnable == null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
        }
    }

    private int[] getDrawableStateForSelector() {
        int[] state = onCreateDrawableState(1);
        state[state.length - 1] = 16842919;
        return state;
    }

    public void onChildAttachedToWindow(View child) {
        if (getAdapter() instanceof SelectionAdapter) {
            RecyclerView.ViewHolder holder = findContainingViewHolder(child);
            if (holder != null) {
                child.setEnabled(((SelectionAdapter) getAdapter()).isEnabled(holder));
            }
        } else {
            child.setEnabled(false);
        }
        super.onChildAttachedToWindow(child);
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    public boolean verifyDrawable(Drawable drawable) {
        return this.selectorDrawable == drawable || super.verifyDrawable(drawable);
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FastScroll fastScroll2 = this.fastScroll;
        if (fastScroll2 != null && fastScroll2.getParent() != getParent()) {
            ViewGroup parent = (ViewGroup) this.fastScroll.getParent();
            if (parent != null) {
                parent.removeView(this.fastScroll);
            }
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(this.observer);
        }
        ArrayList<View> arrayList = this.headers;
        if (arrayList != null) {
            arrayList.clear();
            this.headersCache.clear();
        }
        this.currentFirst = -1;
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
        this.pinnedHeader = null;
        if (adapter instanceof SectionsAdapter) {
            this.sectionsAdapter = (SectionsAdapter) adapter;
        } else {
            this.sectionsAdapter = null;
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.observer);
        }
        checkIfEmpty();
    }

    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException e) {
        }
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (!this.longPressCalled) {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        }
        OnItemLongClickListenerExtended onItemLongClickListenerExtended2 = this.onItemLongClickListenerExtended;
        if (onItemLongClickListenerExtended2 != null) {
            onItemLongClickListenerExtended2.onMove((float) dx, (float) dy);
        }
        consumed[0] = dx;
        consumed[1] = dy;
        return true;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = oldView == null;
        View view = this.sectionsAdapter.getSectionHeaderView(section, oldView);
        if (shouldLayout) {
            ensurePinnedHeaderLayout(view, false);
        }
        return view;
    }

    private void ensurePinnedHeaderLayout(View header, boolean forceLayout) {
        if (header.isLayoutRequested() || forceLayout) {
            int i = this.sectionsType;
            if (i == 1) {
                ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                try {
                    header.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i == 2) {
                try {
                    header.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        View view;
        super.onSizeChanged(w, h, oldw, oldh);
        int i = this.sectionsType;
        if (i == 1) {
            if (this.sectionsAdapter != null && !this.headers.isEmpty()) {
                for (int a = 0; a < this.headers.size(); a++) {
                    ensurePinnedHeaderLayout(this.headers.get(a), true);
                }
            }
        } else if (i == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null) {
            ensurePinnedHeaderLayout(view, true);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int i = this.sectionsType;
        float f = 0.0f;
        if (i == 1) {
            if (this.sectionsAdapter != null && !this.headers.isEmpty()) {
                for (int a = 0; a < this.headers.size(); a++) {
                    View header = this.headers.get(a);
                    int saveCount = canvas.save();
                    canvas.translate(LocaleController.isRTL ? (float) (getWidth() - header.getWidth()) : 0.0f, (float) ((Integer) header.getTag()).intValue());
                    canvas.clipRect(0, 0, getWidth(), header.getMeasuredHeight());
                    header.draw(canvas);
                    canvas.restoreToCount(saveCount);
                }
            } else {
                return;
            }
        } else if (i == 2) {
            if (this.sectionsAdapter != null && this.pinnedHeader != null) {
                int saveCount2 = canvas.save();
                int top = ((Integer) this.pinnedHeader.getTag()).intValue();
                if (LocaleController.isRTL) {
                    f = (float) (getWidth() - this.pinnedHeader.getWidth());
                }
                canvas.translate(f, (float) top);
                Drawable drawable = this.pinnedHeaderShadowDrawable;
                if (drawable != null) {
                    drawable.setBounds(0, this.pinnedHeader.getMeasuredHeight(), getWidth(), this.pinnedHeader.getMeasuredHeight() + this.pinnedHeaderShadowDrawable.getIntrinsicHeight());
                    this.pinnedHeaderShadowDrawable.setAlpha((int) (this.pinnedHeaderShadowAlpha * 255.0f));
                    this.pinnedHeaderShadowDrawable.draw(canvas);
                    long newTime = SystemClock.uptimeMillis();
                    long dt = Math.min(20, newTime - this.lastAlphaAnimationTime);
                    this.lastAlphaAnimationTime = newTime;
                    float f2 = this.pinnedHeaderShadowAlpha;
                    float f3 = this.pinnedHeaderShadowTargetAlpha;
                    if (f2 < f3) {
                        float f4 = f2 + (((float) dt) / 180.0f);
                        this.pinnedHeaderShadowAlpha = f4;
                        if (f4 > f3) {
                            this.pinnedHeaderShadowAlpha = f3;
                        }
                        invalidate();
                    } else if (f2 > f3) {
                        float f5 = f2 - (((float) dt) / 180.0f);
                        this.pinnedHeaderShadowAlpha = f5;
                        if (f5 < f3) {
                            this.pinnedHeaderShadowAlpha = f3;
                        }
                        invalidate();
                    }
                }
                canvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
                this.pinnedHeader.draw(canvas);
                canvas.restoreToCount(saveCount2);
            } else {
                return;
            }
        }
        if (!this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            this.selectorDrawable.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
    }

    public ArrayList<View> getHeaders() {
        return this.headers;
    }

    public ArrayList<View> getHeadersCache() {
        return this.headersCache;
    }

    public View getPinnedHeader() {
        return this.pinnedHeader;
    }
}

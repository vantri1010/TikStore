package im.bclpbkiauv.ui.components.paint.views;

import android.content.Context;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.components.Point;
import im.bclpbkiauv.ui.components.Rect;
import java.util.UUID;

public class EntityView extends FrameLayout {
    private boolean announcedSelection = false;
    /* access modifiers changed from: private */
    public EntityViewDelegate delegate;
    /* access modifiers changed from: private */
    public GestureDetector gestureDetector;
    /* access modifiers changed from: private */
    public boolean hasPanned = false;
    /* access modifiers changed from: private */
    public boolean hasReleased = false;
    /* access modifiers changed from: private */
    public boolean hasTransformed = false;
    /* access modifiers changed from: private */
    public int offsetX;
    /* access modifiers changed from: private */
    public int offsetY;
    protected Point position = new Point();
    /* access modifiers changed from: private */
    public float previousLocationX;
    /* access modifiers changed from: private */
    public float previousLocationY;
    /* access modifiers changed from: private */
    public boolean recognizedLongPress = false;
    protected SelectionView selectionView;
    private UUID uuid = UUID.randomUUID();

    public interface EntityViewDelegate {
        boolean allowInteraction(EntityView entityView);

        boolean onEntityLongClicked(EntityView entityView);

        boolean onEntitySelected(EntityView entityView);
    }

    public EntityView(Context context, Point pos) {
        super(context);
        this.position = pos;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
                if (!EntityView.this.hasPanned && !EntityView.this.hasTransformed && !EntityView.this.hasReleased) {
                    boolean unused = EntityView.this.recognizedLongPress = true;
                    if (EntityView.this.delegate != null) {
                        EntityView.this.performHapticFeedback(0);
                        EntityView.this.delegate.onEntityLongClicked(EntityView.this);
                    }
                }
            }
        });
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point value) {
        this.position = value;
        updatePosition();
    }

    public float getScale() {
        return getScaleX();
    }

    public void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    public void setDelegate(EntityViewDelegate entityViewDelegate) {
        this.delegate = entityViewDelegate;
    }

    public void setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.delegate.allowInteraction(this);
    }

    /* access modifiers changed from: private */
    public boolean onTouchMove(float x, float y) {
        float scale = ((View) getParent()).getScaleX();
        Point translation = new Point((x - this.previousLocationX) / scale, (y - this.previousLocationY) / scale);
        if (((float) Math.hypot((double) translation.x, (double) translation.y)) <= (this.hasPanned ? 6.0f : 16.0f)) {
            return false;
        }
        pan(translation);
        this.previousLocationX = x;
        this.previousLocationY = y;
        this.hasPanned = true;
        return true;
    }

    /* access modifiers changed from: private */
    public void onTouchUp() {
        EntityViewDelegate entityViewDelegate;
        if (!this.recognizedLongPress && !this.hasPanned && !this.hasTransformed && !this.announcedSelection && (entityViewDelegate = this.delegate) != null) {
            entityViewDelegate.onEntitySelected(this);
        }
        this.recognizedLongPress = false;
        this.hasPanned = false;
        this.hasTransformed = false;
        this.hasReleased = true;
        this.announcedSelection = false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002c, code lost:
        if (r4 != 6) goto L_0x0050;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            int r0 = r8.getPointerCount()
            r1 = 0
            r2 = 1
            if (r0 > r2) goto L_0x0056
            im.bclpbkiauv.ui.components.paint.views.EntityView$EntityViewDelegate r0 = r7.delegate
            boolean r0 = r0.allowInteraction(r7)
            if (r0 != 0) goto L_0x0011
            goto L_0x0056
        L_0x0011:
            float r0 = r8.getRawX()
            float r3 = r8.getRawY()
            int r4 = r8.getActionMasked()
            r5 = 0
            if (r4 == 0) goto L_0x0039
            if (r4 == r2) goto L_0x0034
            r6 = 2
            if (r4 == r6) goto L_0x002f
            r6 = 3
            if (r4 == r6) goto L_0x0034
            r6 = 5
            if (r4 == r6) goto L_0x0039
            r1 = 6
            if (r4 == r1) goto L_0x0034
            goto L_0x0050
        L_0x002f:
            boolean r5 = r7.onTouchMove(r0, r3)
            goto L_0x0050
        L_0x0034:
            r7.onTouchUp()
            r5 = 1
            goto L_0x0050
        L_0x0039:
            boolean r6 = r7.isSelected()
            if (r6 != 0) goto L_0x0048
            im.bclpbkiauv.ui.components.paint.views.EntityView$EntityViewDelegate r6 = r7.delegate
            if (r6 == 0) goto L_0x0048
            r6.onEntitySelected(r7)
            r7.announcedSelection = r2
        L_0x0048:
            r7.previousLocationX = r0
            r7.previousLocationY = r3
            r5 = 1
            r7.hasReleased = r1
        L_0x0050:
            android.view.GestureDetector r1 = r7.gestureDetector
            r1.onTouchEvent(r8)
            return r5
        L_0x0056:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.paint.views.EntityView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void pan(Point translation) {
        this.position.x += translation.x;
        this.position.y += translation.y;
        updatePosition();
    }

    /* access modifiers changed from: protected */
    public void updatePosition() {
        setX(this.position.x - (((float) getWidth()) / 2.0f));
        setY(this.position.y - (((float) getHeight()) / 2.0f));
        updateSelectionView();
    }

    public void scale(float scale) {
        setScale(Math.max(getScale() * scale, 0.1f));
        updateSelectionView();
    }

    public void rotate(float angle) {
        setRotation(angle);
        updateSelectionView();
    }

    /* access modifiers changed from: protected */
    public Rect getSelectionBounds() {
        return new Rect(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public boolean isSelected() {
        return this.selectionView != null;
    }

    /* access modifiers changed from: protected */
    public SelectionView createSelectionView() {
        return null;
    }

    public void updateSelectionView() {
        SelectionView selectionView2 = this.selectionView;
        if (selectionView2 != null) {
            selectionView2.updatePosition();
        }
    }

    public void select(ViewGroup selectionContainer) {
        SelectionView selectionView2 = createSelectionView();
        this.selectionView = selectionView2;
        selectionContainer.addView(selectionView2);
        selectionView2.updatePosition();
    }

    public void deselect() {
        SelectionView selectionView2 = this.selectionView;
        if (selectionView2 != null) {
            if (selectionView2.getParent() != null) {
                ((ViewGroup) this.selectionView.getParent()).removeView(this.selectionView);
            }
            this.selectionView = null;
        }
    }

    public void setSelectionVisibility(boolean visible) {
        SelectionView selectionView2 = this.selectionView;
        if (selectionView2 != null) {
            selectionView2.setVisibility(visible ? 0 : 8);
        }
    }

    public class SelectionView extends FrameLayout {
        public static final int SELECTION_LEFT_HANDLE = 1;
        public static final int SELECTION_RIGHT_HANDLE = 2;
        public static final int SELECTION_WHOLE_HANDLE = 3;
        private int currentHandle;
        protected Paint dotPaint = new Paint(1);
        protected Paint dotStrokePaint = new Paint(1);
        protected Paint paint = new Paint(1);

        public SelectionView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.paint.setColor(-1);
            this.dotPaint.setColor(-12793105);
            this.dotStrokePaint.setColor(-1);
            this.dotStrokePaint.setStyle(Paint.Style.STROKE);
            this.dotStrokePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        }

        /* access modifiers changed from: protected */
        public void updatePosition() {
            Rect bounds = EntityView.this.getSelectionBounds();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            layoutParams.leftMargin = ((int) bounds.x) + EntityView.this.offsetX;
            layoutParams.topMargin = ((int) bounds.y) + EntityView.this.offsetY;
            layoutParams.width = (int) bounds.width;
            layoutParams.height = (int) bounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }

        /* access modifiers changed from: protected */
        public int pointInsideHandle(float x, float y) {
            return 0;
        }

        /* JADX WARNING: Removed duplicated region for block: B:39:0x0164  */
        /* JADX WARNING: Removed duplicated region for block: B:40:0x0170  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r19) {
            /*
                r18 = this;
                r0 = r18
                int r1 = r19.getActionMasked()
                r2 = 0
                r3 = 0
                r4 = 3
                if (r1 == 0) goto L_0x0130
                r5 = 1
                if (r1 == r5) goto L_0x0123
                r6 = 2
                if (r1 == r6) goto L_0x002b
                if (r1 == r4) goto L_0x001f
                r5 = 5
                if (r1 == r5) goto L_0x0025
                r5 = 6
                if (r1 == r5) goto L_0x001f
                r16 = r1
                r17 = r2
                goto L_0x0120
            L_0x001f:
                r16 = r1
                r17 = r2
                goto L_0x0127
            L_0x0025:
                r16 = r1
                r17 = r2
                goto L_0x0134
            L_0x002b:
                int r3 = r0.currentHandle
                if (r3 != r4) goto L_0x0041
                float r3 = r19.getRawX()
                float r5 = r19.getRawY()
                im.bclpbkiauv.ui.components.paint.views.EntityView r6 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                boolean r2 = r6.onTouchMove(r3, r5)
                r16 = r1
                goto L_0x0160
            L_0x0041:
                if (r3 == 0) goto L_0x011c
                im.bclpbkiauv.ui.components.paint.views.EntityView r3 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                boolean unused = r3.hasTransformed = r5
                im.bclpbkiauv.ui.components.Point r3 = new im.bclpbkiauv.ui.components.Point
                float r7 = r19.getRawX()
                im.bclpbkiauv.ui.components.paint.views.EntityView r8 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                float r8 = r8.previousLocationX
                float r7 = r7 - r8
                float r8 = r19.getRawY()
                im.bclpbkiauv.ui.components.paint.views.EntityView r9 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                float r9 = r9.previousLocationY
                float r8 = r8 - r9
                r3.<init>(r7, r8)
                float r7 = r18.getRotation()
                double r7 = (double) r7
                double r7 = java.lang.Math.toRadians(r7)
                float r7 = (float) r7
                float r8 = r3.x
                double r8 = (double) r8
                double r10 = (double) r7
                double r10 = java.lang.Math.cos(r10)
                double r8 = r8 * r10
                float r10 = r3.y
                double r10 = (double) r10
                double r12 = (double) r7
                double r12 = java.lang.Math.sin(r12)
                double r10 = r10 * r12
                double r8 = r8 + r10
                float r8 = (float) r8
                int r9 = r0.currentHandle
                if (r9 != r5) goto L_0x008b
                r9 = -1082130432(0xffffffffbf800000, float:-1.0)
                float r8 = r8 * r9
            L_0x008b:
                r9 = 1065353216(0x3f800000, float:1.0)
                r10 = 1073741824(0x40000000, float:2.0)
                float r10 = r10 * r8
                int r11 = r18.getWidth()
                float r11 = (float) r11
                float r10 = r10 / r11
                float r10 = r10 + r9
                im.bclpbkiauv.ui.components.paint.views.EntityView r9 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                r9.scale(r10)
                int r9 = r18.getLeft()
                int r11 = r18.getWidth()
                int r11 = r11 / r6
                int r9 = r9 + r11
                float r9 = (float) r9
                int r11 = r18.getTop()
                int r12 = r18.getHeight()
                int r12 = r12 / r6
                int r11 = r11 + r12
                float r11 = (float) r11
                float r12 = r19.getRawX()
                android.view.ViewParent r13 = r18.getParent()
                android.view.View r13 = (android.view.View) r13
                int r13 = r13.getLeft()
                float r13 = (float) r13
                float r12 = r12 - r13
                float r13 = r19.getRawY()
                android.view.ViewParent r14 = r18.getParent()
                android.view.View r14 = (android.view.View) r14
                int r14 = r14.getTop()
                float r14 = (float) r14
                float r13 = r13 - r14
                int r14 = im.bclpbkiauv.messenger.AndroidUtilities.statusBarHeight
                float r14 = (float) r14
                float r13 = r13 - r14
                r14 = 0
                int r15 = r0.currentHandle
                if (r15 != r5) goto L_0x00ec
                float r5 = r11 - r13
                double r5 = (double) r5
                float r15 = r9 - r12
                r16 = r1
                r17 = r2
                double r1 = (double) r15
                double r1 = java.lang.Math.atan2(r5, r1)
                float r14 = (float) r1
                goto L_0x00fd
            L_0x00ec:
                r16 = r1
                r17 = r2
                if (r15 != r6) goto L_0x00fd
                float r1 = r13 - r11
                double r1 = (double) r1
                float r5 = r12 - r9
                double r5 = (double) r5
                double r1 = java.lang.Math.atan2(r1, r5)
                float r14 = (float) r1
            L_0x00fd:
                im.bclpbkiauv.ui.components.paint.views.EntityView r1 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                double r5 = (double) r14
                double r5 = java.lang.Math.toDegrees(r5)
                float r2 = (float) r5
                r1.rotate(r2)
                im.bclpbkiauv.ui.components.paint.views.EntityView r1 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                float r2 = r19.getRawX()
                float unused = r1.previousLocationX = r2
                im.bclpbkiauv.ui.components.paint.views.EntityView r1 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                float r2 = r19.getRawY()
                float unused = r1.previousLocationY = r2
                r2 = 1
                goto L_0x0160
            L_0x011c:
                r16 = r1
                r17 = r2
            L_0x0120:
                r2 = r17
                goto L_0x0160
            L_0x0123:
                r16 = r1
                r17 = r2
            L_0x0127:
                im.bclpbkiauv.ui.components.paint.views.EntityView r1 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                r1.onTouchUp()
                r0.currentHandle = r3
                r2 = 1
                goto L_0x0160
            L_0x0130:
                r16 = r1
                r17 = r2
            L_0x0134:
                float r1 = r19.getX()
                float r2 = r19.getY()
                int r1 = r0.pointInsideHandle(r1, r2)
                if (r1 == 0) goto L_0x015d
                r0.currentHandle = r1
                im.bclpbkiauv.ui.components.paint.views.EntityView r2 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                float r5 = r19.getRawX()
                float unused = r2.previousLocationX = r5
                im.bclpbkiauv.ui.components.paint.views.EntityView r2 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                float r5 = r19.getRawY()
                float unused = r2.previousLocationY = r5
                im.bclpbkiauv.ui.components.paint.views.EntityView r2 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                boolean unused = r2.hasReleased = r3
                r2 = 1
                goto L_0x015f
            L_0x015d:
                r2 = r17
            L_0x015f:
            L_0x0160:
                int r1 = r0.currentHandle
                if (r1 != r4) goto L_0x0170
                im.bclpbkiauv.ui.components.paint.views.EntityView r1 = im.bclpbkiauv.ui.components.paint.views.EntityView.this
                android.view.GestureDetector r1 = r1.gestureDetector
                r3 = r19
                r1.onTouchEvent(r3)
                goto L_0x0172
            L_0x0170:
                r3 = r19
            L_0x0172:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.paint.views.EntityView.SelectionView.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }
}

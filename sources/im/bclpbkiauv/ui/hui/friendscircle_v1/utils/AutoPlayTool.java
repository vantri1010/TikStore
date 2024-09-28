package im.bclpbkiauv.ui.hui.friendscircle_v1.utils;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ScreenUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface;

public class AutoPlayTool {
    public static int MODE_PLAY_CENTER = 1;
    public static int MODE_PLAY_FIRST = 0;
    private AutoPlayItemInterface mHolder;
    private int mode = MODE_PLAY_FIRST;
    private int visiblePercent = 60;

    public AutoPlayTool() {
    }

    public AutoPlayTool(int visiblePercent2) {
        this.visiblePercent = visiblePercent2;
    }

    public AutoPlayTool(int visiblePercent2, int mode2) {
        this.visiblePercent = visiblePercent2;
        this.mode = mode2;
    }

    public void setMode(int mode2) {
        this.mode = mode2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface} */
    /* JADX WARNING: type inference failed for: r1v7, types: [androidx.recyclerview.widget.RecyclerView$LayoutManager] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onActiveWhenNoScrolling(androidx.recyclerview.widget.RecyclerView r12) {
        /*
            r11 = this;
            r0 = 0
            androidx.recyclerview.widget.RecyclerView$LayoutManager r1 = r12.getLayoutManager()
            boolean r1 = r1 instanceof androidx.recyclerview.widget.LinearLayoutManager
            if (r1 == 0) goto L_0x0010
            androidx.recyclerview.widget.RecyclerView$LayoutManager r1 = r12.getLayoutManager()
            r0 = r1
            androidx.recyclerview.widget.LinearLayoutManager r0 = (androidx.recyclerview.widget.LinearLayoutManager) r0
        L_0x0010:
            if (r0 == 0) goto L_0x00a9
            int r1 = r0.findFirstVisibleItemPosition()
            int r2 = r0.findLastVisibleItemPosition()
            java.util.LinkedHashMap r3 = new java.util.LinkedHashMap
            r3.<init>()
        L_0x001f:
            if (r1 > r2) goto L_0x005a
            androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r12.findViewHolderForLayoutPosition(r1)
            boolean r5 = r4 instanceof im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface
            if (r5 == 0) goto L_0x0056
            r5 = r4
            im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface r5 = (im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface) r5
            android.view.View r5 = r5.getAutoPlayView()
            if (r5 == 0) goto L_0x0056
            int r6 = r11.visiblePercent
            boolean r6 = r11.getVisible(r5, r6)
            if (r6 == 0) goto L_0x0056
            int r6 = r11.mode
            int r7 = MODE_PLAY_FIRST
            if (r6 != r7) goto L_0x004c
            r6 = r4
            im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface r6 = (im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface) r6
            r6.setActive()
            r6 = r4
            im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface r6 = (im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface) r6
            r11.mHolder = r6
            return r1
        L_0x004c:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r1)
            r7 = r4
            im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface r7 = (im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface) r7
            r3.put(r6, r7)
        L_0x0056:
            int r1 = r1 + 1
            goto L_0x001f
        L_0x005a:
            r4 = 2147483647(0x7fffffff, float:NaN)
            r5 = 0
            r6 = -1
            java.util.Set r7 = r3.entrySet()
            java.util.Iterator r7 = r7.iterator()
        L_0x0067:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x0096
            java.lang.Object r8 = r7.next()
            java.util.Map$Entry r8 = (java.util.Map.Entry) r8
            java.lang.Object r9 = r8.getValue()
            im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface r9 = (im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface) r9
            android.view.View r9 = r9.getAutoPlayView()
            int r9 = r11.getDistanceFromCenter(r9)
            if (r9 >= r4) goto L_0x0095
            java.lang.Object r10 = r8.getValue()
            r5 = r10
            im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface r5 = (im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface) r5
            r4 = r9
            java.lang.Object r10 = r8.getKey()
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r6 = r10.intValue()
        L_0x0095:
            goto L_0x0067
        L_0x0096:
            im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface r7 = r11.mHolder
            if (r7 == r5) goto L_0x00a1
            if (r7 == 0) goto L_0x009f
            r7.deactivate()
        L_0x009f:
            r11.mHolder = r5
        L_0x00a1:
            im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface r7 = r11.mHolder
            if (r7 == 0) goto L_0x00a9
            r7.setActive()
            return r6
        L_0x00a9:
            r1 = -1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.utils.AutoPlayTool.onActiveWhenNoScrolling(androidx.recyclerview.widget.RecyclerView):int");
    }

    public void onScrolledAndDeactivate(RecyclerView recyclerView) {
        AutoPlayItemInterface autoPlayItemInterface = this.mHolder;
        if (autoPlayItemInterface != null && autoPlayItemInterface.getAutoPlayView() != null && !getVisible(this.mHolder.getAutoPlayView(), this.visiblePercent)) {
            this.mHolder.deactivate();
        }
    }

    public void onScrolledAndDeactivate() {
        AutoPlayItemInterface autoPlayItemInterface = this.mHolder;
        if (autoPlayItemInterface != null && autoPlayItemInterface.getAutoPlayView() != null && !getVisible(this.mHolder.getAutoPlayView(), this.visiblePercent)) {
            this.mHolder.deactivate();
        }
    }

    public void onRefreshDeactivate() {
        AutoPlayItemInterface autoPlayItemInterface = this.mHolder;
        if (autoPlayItemInterface != null && autoPlayItemInterface.getAutoPlayView() != null) {
            this.mHolder.deactivate();
            this.mHolder = null;
        }
    }

    public void onDeactivate() {
        AutoPlayItemInterface autoPlayItemInterface = this.mHolder;
        if (autoPlayItemInterface != null && autoPlayItemInterface.getAutoPlayView() != null) {
            this.mHolder.deactivate();
        }
    }

    public void setVisiblePercent(int visiblePercent2) {
        this.visiblePercent = visiblePercent2;
    }

    private int getVisiblePercent(View v) {
        Rect r = new Rect();
        if (!v.getLocalVisibleRect(r) || v.getMeasuredHeight() <= 0) {
            return -1;
        }
        return (r.height() * 100) / v.getMeasuredHeight();
    }

    private boolean getVisible(View v, int value) {
        if (!v.getLocalVisibleRect(new Rect()) || v.getVisibility() != 0 || getVisiblePercent(v) < value) {
            return false;
        }
        return true;
    }

    private int getDistanceFromCenter(View view) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        return Math.abs((viewLocation[1] + (view.getHeight() / 2)) - ((int) (((double) ScreenUtils.getScreenHeight()) / 2.3d)));
    }
}

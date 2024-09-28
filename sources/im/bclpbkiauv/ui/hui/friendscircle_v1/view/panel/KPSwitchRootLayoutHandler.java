package im.bclpbkiauv.ui.hui.friendscircle_v1.view.panel;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import cn.dreamtobe.kpswitch.IPanelConflictLayout;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.KeyboardUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.StatusBarHeightUtil;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.ViewUtil;

public class KPSwitchRootLayoutHandler {
    private static final String TAG = "KPSRootLayoutHandler";
    private final boolean mIsTranslucentStatus;
    private int mOldHeight = -1;
    private IPanelConflictLayout mPanelLayout;
    private final int mStatusBarHeight;
    private final View mTargetRootView;

    public KPSwitchRootLayoutHandler(View rootView) {
        this.mTargetRootView = rootView;
        this.mStatusBarHeight = StatusBarHeightUtil.getStatusBarHeight(rootView.getContext());
        this.mIsTranslucentStatus = ViewUtil.isTranslucentStatus((Activity) rootView.getContext());
    }

    public void handleBeforeMeasure(int width, int height) {
        if (this.mIsTranslucentStatus && Build.VERSION.SDK_INT >= 16 && this.mTargetRootView.getFitsSystemWindows()) {
            Rect rect = new Rect();
            this.mTargetRootView.getWindowVisibleDisplayFrame(rect);
            height = rect.bottom - rect.top;
        }
        Log.d(TAG, "onMeasure, width: " + width + " height: " + height);
        if (height >= 0) {
            int i = this.mOldHeight;
            if (i < 0) {
                this.mOldHeight = height;
                return;
            }
            int offset = i - height;
            if (offset == 0) {
                Log.d(TAG, "" + offset + " == 0 break;");
            } else if (Math.abs(offset) == this.mStatusBarHeight) {
                Log.w(TAG, String.format("offset just equal statusBar height %d", new Object[]{Integer.valueOf(offset)}));
            } else {
                this.mOldHeight = height;
                IPanelConflictLayout panel = getPanelLayout(this.mTargetRootView);
                if (panel == null) {
                    Log.w(TAG, "can't find the valid panel conflict layout, give up!");
                } else if (Math.abs(offset) < KeyboardUtils.getMinKeyboardHeight(this.mTargetRootView.getContext())) {
                    Log.w(TAG, "system bottom-menu-bar(such as HuaWei Mate7) causes layout changed");
                } else if (offset > 0) {
                    panel.handleHide();
                } else if (panel.isKeyboardShowing() && panel.isVisible()) {
                    panel.handleShow();
                }
            }
        }
    }

    private IPanelConflictLayout getPanelLayout(View view) {
        IPanelConflictLayout iPanelConflictLayout = this.mPanelLayout;
        if (iPanelConflictLayout != null) {
            return iPanelConflictLayout;
        }
        if (view instanceof IPanelConflictLayout) {
            IPanelConflictLayout iPanelConflictLayout2 = (IPanelConflictLayout) view;
            this.mPanelLayout = iPanelConflictLayout2;
            return iPanelConflictLayout2;
        } else if (!(view instanceof ViewGroup)) {
            return null;
        } else {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                IPanelConflictLayout v = getPanelLayout(((ViewGroup) view).getChildAt(i));
                if (v != null) {
                    this.mPanelLayout = v;
                    return v;
                }
            }
            return null;
        }
    }
}

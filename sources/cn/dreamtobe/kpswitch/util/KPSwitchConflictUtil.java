package cn.dreamtobe.kpswitch.util;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

public class KPSwitchConflictUtil {

    public interface SwitchClickListener {
        void onClickSwitch(boolean z);
    }

    public static void attach(View panelLayout, View switchPanelKeyboardBtn, View focusView) {
        attach(panelLayout, switchPanelKeyboardBtn, focusView, (SwitchClickListener) null);
    }

    public static void attach(final View panelLayout, View switchPanelKeyboardBtn, final View focusView, final SwitchClickListener switchClickListener) {
        Activity activity = (Activity) panelLayout.getContext();
        if (switchPanelKeyboardBtn != null) {
            switchPanelKeyboardBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean switchToPanel = KPSwitchConflictUtil.switchPanelAndKeyboard(panelLayout, focusView);
                    SwitchClickListener switchClickListener = switchClickListener;
                    if (switchClickListener != null) {
                        switchClickListener.onClickSwitch(switchToPanel);
                    }
                }
            });
        }
        if (isHandleByPlaceholder(activity)) {
            focusView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() != 1) {
                        return false;
                    }
                    panelLayout.setVisibility(4);
                    return false;
                }
            });
        }
    }

    public static void attach(View panelLayout, View focusView, SubPanelAndTrigger... subPanelAndTriggers) {
        attach(panelLayout, focusView, (SwitchClickListener) null, subPanelAndTriggers);
    }

    public static void attach(final View panelLayout, View focusView, SwitchClickListener switchClickListener, SubPanelAndTrigger... subPanelAndTriggers) {
        Activity activity = (Activity) panelLayout.getContext();
        for (SubPanelAndTrigger subPanelAndTrigger : subPanelAndTriggers) {
            bindSubPanel(subPanelAndTrigger, subPanelAndTriggers, focusView, panelLayout, switchClickListener);
        }
        if (isHandleByPlaceholder(activity)) {
            focusView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() != 1) {
                        return false;
                    }
                    panelLayout.setVisibility(4);
                    return false;
                }
            });
        }
    }

    public static class SubPanelAndTrigger {
        final View subPanelView;
        final View triggerView;

        public SubPanelAndTrigger(View subPanelView2, View triggerView2) {
            this.subPanelView = subPanelView2;
            this.triggerView = triggerView2;
        }
    }

    public static void showPanel(View panelLayout) {
        Activity activity = (Activity) panelLayout.getContext();
        panelLayout.setVisibility(0);
        if (activity.getCurrentFocus() != null) {
            KeyboardUtil.hideKeyboard(activity.getCurrentFocus());
        }
    }

    public static void showKeyboard(View panelLayout, View focusView) {
        KeyboardUtil.showKeyboard(focusView);
        if (isHandleByPlaceholder((Activity) panelLayout.getContext())) {
            panelLayout.setVisibility(4);
        }
    }

    public static boolean switchPanelAndKeyboard(View panelLayout, View focusView) {
        boolean switchToPanel = panelLayout.getVisibility() != 0;
        if (!switchToPanel) {
            showKeyboard(panelLayout, focusView);
        } else {
            showPanel(panelLayout);
        }
        return switchToPanel;
    }

    public static void hidePanelAndKeyboard(View panelLayout) {
        Activity activity = (Activity) panelLayout.getContext();
        View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            KeyboardUtil.hideKeyboard(activity.getCurrentFocus());
            focusView.clearFocus();
        }
        panelLayout.setVisibility(8);
    }

    public static boolean isHandleByPlaceholder(boolean isFullScreen, boolean isTranslucentStatus, boolean isFitsSystem) {
        return isFullScreen || (isTranslucentStatus && !isFitsSystem);
    }

    static boolean isHandleByPlaceholder(Activity activity) {
        return isHandleByPlaceholder(ViewUtil.isFullScreen(activity), ViewUtil.isTranslucentStatus(activity), ViewUtil.isFitsSystemWindows(activity));
    }

    private static void bindSubPanel(SubPanelAndTrigger subPanelAndTrigger, SubPanelAndTrigger[] subPanelAndTriggers, View focusView, View panelLayout, SwitchClickListener switchClickListener) {
        View triggerView = subPanelAndTrigger.triggerView;
        final View view = panelLayout;
        final View view2 = subPanelAndTrigger.subPanelView;
        final View view3 = focusView;
        final SubPanelAndTrigger[] subPanelAndTriggerArr = subPanelAndTriggers;
        final SwitchClickListener switchClickListener2 = switchClickListener;
        triggerView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Boolean switchToPanel = null;
                if (view.getVisibility() != 0) {
                    KPSwitchConflictUtil.showPanel(view);
                    switchToPanel = true;
                    KPSwitchConflictUtil.showBoundTriggerSubPanel(view2, subPanelAndTriggerArr);
                } else if (view2.getVisibility() == 0) {
                    KPSwitchConflictUtil.showKeyboard(view, view3);
                    switchToPanel = false;
                } else {
                    KPSwitchConflictUtil.showBoundTriggerSubPanel(view2, subPanelAndTriggerArr);
                }
                SwitchClickListener switchClickListener = switchClickListener2;
                if (switchClickListener != null && switchToPanel != null) {
                    switchClickListener.onClickSwitch(switchToPanel.booleanValue());
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public static void showBoundTriggerSubPanel(View boundTriggerSubPanelView, SubPanelAndTrigger[] subPanelAndTriggers) {
        for (SubPanelAndTrigger panelAndTrigger : subPanelAndTriggers) {
            if (panelAndTrigger.subPanelView != boundTriggerSubPanelView) {
                panelAndTrigger.subPanelView.setVisibility(8);
            }
        }
        boundTriggerSubPanelView.setVisibility(0);
    }
}

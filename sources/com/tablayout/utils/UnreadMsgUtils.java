package com.tablayout.utils;

import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import com.tablayout.widget.MsgView;

public class UnreadMsgUtils {
    public static void show(MsgView msgView, int num) {
        if (msgView != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) msgView.getLayoutParams();
            DisplayMetrics dm = msgView.getResources().getDisplayMetrics();
            msgView.setVisibility(0);
            if (num <= 0) {
                msgView.setStrokeWidth(0);
                msgView.setText("");
                lp.width = (int) (dm.density * 5.0f);
                lp.height = (int) (dm.density * 5.0f);
                msgView.setLayoutParams(lp);
                return;
            }
            lp.height = (int) (dm.density * 18.0f);
            if (num > 0 && num < 10) {
                lp.width = (int) (dm.density * 18.0f);
                msgView.setText(num + "");
            } else if (num <= 9 || num >= 100) {
                lp.width = -2;
                msgView.setPadding((int) (dm.density * 6.0f), 0, (int) (dm.density * 6.0f), 0);
                msgView.setText("99+");
            } else {
                lp.width = -2;
                msgView.setPadding((int) (dm.density * 6.0f), 0, (int) (dm.density * 6.0f), 0);
                msgView.setText(num + "");
            }
            msgView.setLayoutParams(lp);
        }
    }

    public static void setSize(MsgView rtv, int size) {
        if (rtv != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rtv.getLayoutParams();
            lp.width = size;
            lp.height = size;
            rtv.setLayoutParams(lp);
        }
    }
}

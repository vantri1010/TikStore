package com.bigkoo.pickerview.utils;

import com.bigkoo.pickerview.R;

public class PickerViewAnimateUtil {
    private static final int INVALID = -1;

    public static int getAnimationResource(int gravity, boolean isInAnimation) {
        if (gravity != 80) {
            return -1;
        }
        return isInAnimation ? R.anim.pickerview_slide_in_bottom : R.anim.pickerview_slide_out_bottom;
    }
}

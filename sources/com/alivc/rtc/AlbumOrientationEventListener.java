package com.alivc.rtc;

import android.content.Context;
import android.view.OrientationEventListener;

public class AlbumOrientationEventListener extends OrientationEventListener {
    private int mOrientation;

    public AlbumOrientationEventListener(Context context) {
        super(context);
    }

    public AlbumOrientationEventListener(Context context, int rate) {
        super(context, rate);
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void onOrientationChanged(int orientation) {
        int newOrientation;
        if (orientation != -1 && (newOrientation = (((orientation + 45) / 90) * 90) % 360) != this.mOrientation) {
            this.mOrientation = newOrientation;
        }
    }
}

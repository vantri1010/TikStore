package org.webrtc.sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import org.webrtc.model.SophonViewStatus;
import org.webrtc.utils.AlivcLog;

public class SophonSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "AliRTCEngine";
    private boolean isCreate;
    private SophonSurfaceChange listener;
    private SophonViewStatus sophonViewStatus;

    public interface SophonSurfaceChange {
        void onSurfaceChange(SurfaceHolder surfaceHolder, int i, int i2, SophonViewStatus sophonViewStatus);

        void onSurfaceDestroyed(SurfaceHolder surfaceHolder, SophonViewStatus sophonViewStatus);

        void onsurfaceCreated(SurfaceHolder surfaceHolder, int i, int i2, SophonViewStatus sophonViewStatus);
    }

    public SophonViewStatus getSophonViewStatus() {
        return this.sophonViewStatus;
    }

    public void setSophonViewStatus(SophonViewStatus sophonViewStatus2) {
        this.sophonViewStatus = sophonViewStatus2;
    }

    public boolean isCreate() {
        Log.e(TAG, "isCreate:" + this.isCreate);
        return this.isCreate;
    }

    public SophonSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public SophonSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    public void setListener(SophonSurfaceChange listener2) {
        this.listener = listener2;
    }

    public void removeListener() {
        this.listener = null;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        AlivcLog.d(TAG, "surfaceCreated " + holder.getSurface() + " listener is " + this.listener);
        this.isCreate = true;
        SophonSurfaceChange sophonSurfaceChange = this.listener;
        if (sophonSurfaceChange != null) {
            sophonSurfaceChange.onsurfaceCreated(holder, getWidth(), getHeight(), this.sophonViewStatus);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        AlivcLog.d(TAG, "surfaceChanged listener is " + this.listener);
        SophonSurfaceChange sophonSurfaceChange = this.listener;
        if (sophonSurfaceChange != null) {
            sophonSurfaceChange.onSurfaceChange(holder, width, height, this.sophonViewStatus);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        AlivcLog.d(TAG, "surfaceDestroyed listener is " + this.listener);
        this.isCreate = false;
        SophonSurfaceChange sophonSurfaceChange = this.listener;
        if (sophonSurfaceChange != null) {
            sophonSurfaceChange.onSurfaceDestroyed(holder, this.sophonViewStatus);
        }
    }
}

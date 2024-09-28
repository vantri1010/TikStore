package com.king.zxing;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.king.zxing.camera.CameraManager;

public class CaptureActivity extends AppCompatActivity implements OnCaptureCallback {
    public static final String KEY_RESULT = "SCAN_RESULT";
    private View ivTorch;
    private CaptureHelper mCaptureHelper;
    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (isContentView(layoutId)) {
            setContentView(layoutId);
        }
        initUI();
        this.mCaptureHelper.onCreate();
    }

    public void initUI() {
        this.surfaceView = (SurfaceView) findViewById(getSurfaceViewId());
        int viewfinderViewId = getViewfinderViewId();
        if (viewfinderViewId != 0) {
            this.viewfinderView = (ViewfinderView) findViewById(viewfinderViewId);
        }
        int ivTorchId = getIvTorchId();
        if (ivTorchId != 0) {
            View findViewById = findViewById(ivTorchId);
            this.ivTorch = findViewById;
            findViewById.setVisibility(4);
        }
        initCaptureHelper();
    }

    public void initCaptureHelper() {
        CaptureHelper captureHelper = new CaptureHelper((Activity) this, this.surfaceView, this.viewfinderView, this.ivTorch);
        this.mCaptureHelper = captureHelper;
        captureHelper.setOnCaptureCallback(this);
    }

    public boolean isContentView(int layoutId) {
        return true;
    }

    public int getLayoutId() {
        return R.layout.zxl_capture;
    }

    public int getViewfinderViewId() {
        return R.id.viewfinderView;
    }

    public int getSurfaceViewId() {
        return R.id.surfaceView;
    }

    public int getIvTorchId() {
        return R.id.ivTorch;
    }

    public CaptureHelper getCaptureHelper() {
        return this.mCaptureHelper;
    }

    @Deprecated
    public CameraManager getCameraManager() {
        return this.mCaptureHelper.getCameraManager();
    }

    public void onResume() {
        super.onResume();
        this.mCaptureHelper.onResume();
    }

    public void onPause() {
        super.onPause();
        this.mCaptureHelper.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mCaptureHelper.onDestroy();
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.mCaptureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public boolean onResultCallback(String result) {
        return false;
    }
}

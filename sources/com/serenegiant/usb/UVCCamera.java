package com.serenegiant.usb;

import android.hardware.usb.UsbDevice;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import com.serenegiant.usb.USBMonitor;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UVCCamera {
    public static final int CTRL_AE = 2;
    public static final int CTRL_AE_ABS = 8;
    public static final int CTRL_AE_PRIORITY = 4;
    public static final int CTRL_AR_REL = 16;
    public static final int CTRL_FOCUS_ABS = 32;
    public static final int CTRL_FOCUS_AUTO = 131072;
    public static final int CTRL_FOCUS_REL = 64;
    public static final int CTRL_FOCUS_SIMPLE = 524288;
    public static final int CTRL_IRIS_ABS = 128;
    public static final int CTRL_IRIS_REL = 256;
    public static final int CTRL_PANTILT_ABS = 2048;
    public static final int CTRL_PANTILT_REL = 4096;
    public static final int CTRL_PRIVACY = 262144;
    public static final int CTRL_ROLL_ABS = 8192;
    public static final int CTRL_ROLL_REL = 16384;
    public static final int CTRL_SCANNING = 1;
    public static final int CTRL_WINDOW = 1048576;
    public static final int CTRL_ZOOM_ABS = 512;
    public static final int CTRL_ZOOM_REL = 1024;
    private static final boolean DEBUG = false;
    public static final float DEFAULT_BANDWIDTH = 1.0f;
    public static final int DEFAULT_PREVIEW_HEIGHT = 480;
    public static final int DEFAULT_PREVIEW_MAX_FPS = 30;
    public static final int DEFAULT_PREVIEW_MIN_FPS = 1;
    public static final int DEFAULT_PREVIEW_MODE = 0;
    public static final int DEFAULT_PREVIEW_WIDTH = 640;
    private static final String DEFAULT_USBFS = "/dev/bus/usb";
    public static final int FRAME_FORMAT_MJPEG = 1;
    public static final int FRAME_FORMAT_YUYV = 0;
    public static final int PIXEL_FORMAT_NV21 = 5;
    public static final int PIXEL_FORMAT_RAW = 0;
    public static final int PIXEL_FORMAT_RGB565 = 2;
    public static final int PIXEL_FORMAT_RGBX = 3;
    public static final int PIXEL_FORMAT_YUV = 1;
    public static final int PIXEL_FORMAT_YUV420SP = 4;
    public static final int PU_AVIDEO_LOCK = -2147352576;
    public static final int PU_AVIDEO_STD = -2147418112;
    public static final int PU_BACKLIGHT = -2147483392;
    public static final int PU_BRIGHTNESS = -2147483647;
    public static final int PU_CONTRAST = -2147483646;
    public static final int PU_CONTRAST_AUTO = -2147221504;
    public static final int PU_DIGITAL_LIMIT = -2147450880;
    public static final int PU_DIGITAL_MULT = -2147467264;
    public static final int PU_GAIN = -2147483136;
    public static final int PU_GAMMA = -2147483616;
    public static final int PU_HUE = -2147483644;
    public static final int PU_HUE_AUTO = -2147481600;
    public static final int PU_POWER_LF = -2147482624;
    public static final int PU_SATURATION = -2147483640;
    public static final int PU_SHARPNESS = -2147483632;
    public static final int PU_WB_COMPO = -2147483520;
    public static final int PU_WB_COMPO_AUTO = -2147475456;
    public static final int PU_WB_TEMP = -2147483584;
    public static final int PU_WB_TEMP_AUTO = -2147479552;
    public static final int STATUS_ATTRIBUTE_FAILURE_CHANGE = 2;
    public static final int STATUS_ATTRIBUTE_INFO_CHANGE = 1;
    public static final int STATUS_ATTRIBUTE_UNKNOWN = 255;
    public static final int STATUS_ATTRIBUTE_VALUE_CHANGE = 0;
    public static final int STATUS_CLASS_CONTROL = 16;
    public static final int STATUS_CLASS_CONTROL_CAMERA = 17;
    public static final int STATUS_CLASS_CONTROL_PROCESSING = 18;
    private static final String[] SUPPORTS_CTRL = {"D0:  Scanning Mode", "D1:  Auto-Exposure Mode", "D2:  Auto-Exposure Priority", "D3:  Exposure Time (Absolute)", "D4:  Exposure Time (Relative)", "D5:  Focus (Absolute)", "D6:  Focus (Relative)", "D7:  Iris (Absolute)", "D8:  Iris (Relative)", "D9:  Zoom (Absolute)", "D10: Zoom (Relative)", "D11: PanTilt (Absolute)", "D12: PanTilt (Relative)", "D13: Roll (Absolute)", "D14: Roll (Relative)", "D15: Reserved", "D16: Reserved", "D17: Focus, Auto", "D18: Privacy", "D19: Focus, Simple", "D20: Window", "D21: Region of Interest", "D22: Reserved, set to zero", "D23: Reserved, set to zero"};
    private static final String[] SUPPORTS_PROC = {"D0: Brightness", "D1: Contrast", "D2: Hue", "D3: Saturation", "D4: Sharpness", "D5: Gamma", "D6: White Balance Temperature", "D7: White Balance Component", "D8: Backlight Compensation", "D9: Gain", "D10: Power Line Frequency", "D11: Hue, Auto", "D12: White Balance Temperature, Auto", "D13: White Balance Component, Auto", "D14: Digital Multiplier", "D15: Digital Multiplier Limit", "D16: Analog Video Standard", "D17: Analog Video Lock Status", "D18: Contrast, Auto", "D19: Reserved. Set to zero", "D20: Reserved. Set to zero", "D21: Reserved. Set to zero", "D22: Reserved. Set to zero", "D23: Reserved. Set to zero"};
    private static final String TAG = UVCCamera.class.getSimpleName();
    private static boolean isLoaded = true;
    protected int mAnalogVideoLockStateDef;
    protected int mAnalogVideoLockStateMax;
    protected int mAnalogVideoLockStateMin;
    protected int mAnalogVideoStandardDef;
    protected int mAnalogVideoStandardMax;
    protected int mAnalogVideoStandardMin;
    protected int mAutoFocusDef;
    protected int mAutoFocusMax;
    protected int mAutoFocusMin;
    protected int mAutoWhiteBlanceCompoDef;
    protected int mAutoWhiteBlanceCompoMax;
    protected int mAutoWhiteBlanceCompoMin;
    protected int mAutoWhiteBlanceDef;
    protected int mAutoWhiteBlanceMax;
    protected int mAutoWhiteBlanceMin;
    protected int mBacklightCompDef;
    protected int mBacklightCompMax;
    protected int mBacklightCompMin;
    protected int mBrightnessDef;
    protected int mBrightnessMax;
    protected int mBrightnessMin;
    protected int mContrastDef;
    protected int mContrastMax;
    protected int mContrastMin;
    protected long mControlSupports;
    private USBMonitor.UsbControlBlock mCtrlBlock;
    protected float mCurrentBandwidthFactor = 1.0f;
    protected int mCurrentFrameFormat = 1;
    protected int mCurrentHeight = DEFAULT_PREVIEW_HEIGHT;
    protected List<Size> mCurrentSizeList;
    protected int mCurrentWidth = DEFAULT_PREVIEW_WIDTH;
    protected int mExposureDef;
    protected int mExposureMax;
    protected int mExposureMin;
    protected int mExposureModeDef;
    protected int mExposureModeMax;
    protected int mExposureModeMin;
    protected int mExposurePriorityDef;
    protected int mExposurePriorityMax;
    protected int mExposurePriorityMin;
    protected int mFocusDef;
    protected int mFocusMax;
    protected int mFocusMin;
    protected int mFocusRelDef;
    protected int mFocusRelMax;
    protected int mFocusRelMin;
    protected int mFocusSimpleDef;
    protected int mFocusSimpleMax;
    protected int mFocusSimpleMin;
    protected int mGainDef;
    protected int mGainMax;
    protected int mGainMin;
    protected int mGammaDef;
    protected int mGammaMax;
    protected int mGammaMin;
    protected int mHueDef;
    protected int mHueMax;
    protected int mHueMin;
    protected int mIrisDef;
    protected int mIrisMax;
    protected int mIrisMin;
    protected int mIrisRelDef;
    protected int mIrisRelMax;
    protected int mIrisRelMin;
    protected int mMultiplierDef;
    protected int mMultiplierLimitDef;
    protected int mMultiplierLimitMax;
    protected int mMultiplierLimitMin;
    protected int mMultiplierMax;
    protected int mMultiplierMin;
    protected long mNativePtr = nativeCreate();
    protected int mPanDef;
    protected int mPanMax;
    protected int mPanMin;
    protected int mPanRelDef;
    protected int mPanRelMax;
    protected int mPanRelMin;
    protected int mPowerlineFrequencyDef;
    protected int mPowerlineFrequencyMax;
    protected int mPowerlineFrequencyMin;
    protected int mPrivacyDef;
    protected int mPrivacyMax;
    protected int mPrivacyMin;
    protected long mProcSupports;
    protected int mRollDef;
    protected int mRollMax;
    protected int mRollMin;
    protected int mRollRelDef;
    protected int mRollRelMax;
    protected int mRollRelMin;
    protected int mSaturationDef;
    protected int mSaturationMax;
    protected int mSaturationMin;
    protected int mScanningModeDef;
    protected int mScanningModeMax;
    protected int mScanningModeMin;
    protected int mSharpnessDef;
    protected int mSharpnessMax;
    protected int mSharpnessMin;
    protected String mSupportedSize = null;
    protected int mTiltDef;
    protected int mTiltMax;
    protected int mTiltMin;
    protected int mTiltRelDef;
    protected int mTiltRelMax;
    protected int mTiltRelMin;
    protected int mWhiteBlanceCompoDef;
    protected int mWhiteBlanceCompoMax;
    protected int mWhiteBlanceCompoMin;
    protected int mWhiteBlanceDef;
    protected int mWhiteBlanceMax;
    protected int mWhiteBlanceMin;
    protected int mWhiteBlanceRelDef;
    protected int mWhiteBlanceRelMax;
    protected int mWhiteBlanceRelMin;
    protected int mZoomDef;
    protected int mZoomMax;
    protected int mZoomMin;
    protected int mZoomRelDef;
    protected int mZoomRelMax;
    protected int mZoomRelMin;

    private final native int nativeConnect(long j, int i, int i2, int i3, int i4, int i5, String str);

    private final native long nativeCreate();

    private final native void nativeDestroy(long j);

    private static final native int nativeGetAnalogVideoLoackState(long j);

    private static final native int nativeGetAnalogVideoStandard(long j);

    private static final native int nativeGetAutoContrast(long j);

    private static final native int nativeGetAutoFocus(long j);

    private static final native int nativeGetAutoHue(long j);

    private static final native int nativeGetAutoWhiteBlance(long j);

    private static final native int nativeGetAutoWhiteBlanceCompo(long j);

    private static final native int nativeGetBacklightComp(long j);

    private static final native int nativeGetBrightness(long j);

    private static final native int nativeGetContrast(long j);

    private static final native long nativeGetCtrlSupports(long j);

    private static final native int nativeGetDigitalMultiplier(long j);

    private static final native int nativeGetDigitalMultiplierLimit(long j);

    private static final native int nativeGetExposure(long j);

    private static final native int nativeGetExposureMode(long j);

    private static final native int nativeGetExposurePriority(long j);

    private static final native int nativeGetExposureRel(long j);

    private static final native int nativeGetFocus(long j);

    private static final native int nativeGetFocusRel(long j);

    private static final native int nativeGetGain(long j);

    private static final native int nativeGetGamma(long j);

    private static final native int nativeGetHue(long j);

    private static final native int nativeGetIris(long j);

    private static final native int nativeGetIrisRel(long j);

    private static final native int nativeGetPan(long j);

    private static final native int nativeGetPanRel(long j);

    private static final native int nativeGetPowerlineFrequency(long j);

    private static final native int nativeGetPrivacy(long j);

    private static final native long nativeGetProcSupports(long j);

    private static final native int nativeGetRoll(long j);

    private static final native int nativeGetRollRel(long j);

    private static final native int nativeGetSaturation(long j);

    private static final native int nativeGetScanningMode(long j);

    private static final native int nativeGetSharpness(long j);

    private static final native String nativeGetSupportedSize(long j);

    private static final native int nativeGetTilt(long j);

    private static final native int nativeGetTiltRel(long j);

    private static final native int nativeGetWhiteBlance(long j);

    private static final native int nativeGetWhiteBlanceCompo(long j);

    private static final native int nativeGetZoom(long j);

    private static final native int nativeGetZoomRel(long j);

    private static final native int nativeRelease(long j);

    private static final native int nativeSetAnalogVideoLoackState(long j, int i);

    private static final native int nativeSetAnalogVideoStandard(long j, int i);

    private static final native int nativeSetAutoContrast(long j, boolean z);

    private static final native int nativeSetAutoFocus(long j, boolean z);

    private static final native int nativeSetAutoHue(long j, boolean z);

    private static final native int nativeSetAutoWhiteBlance(long j, boolean z);

    private static final native int nativeSetAutoWhiteBlanceCompo(long j, boolean z);

    private static final native int nativeSetBacklightComp(long j, int i);

    private static final native int nativeSetBrightness(long j, int i);

    private static final native int nativeSetButtonCallback(long j, IButtonCallback iButtonCallback);

    private static final native int nativeSetCaptureDisplay(long j, Surface surface);

    private static final native int nativeSetContrast(long j, int i);

    private static final native int nativeSetDigitalMultiplier(long j, int i);

    private static final native int nativeSetDigitalMultiplierLimit(long j, int i);

    private static final native int nativeSetExposure(long j, int i);

    private static final native int nativeSetExposureMode(long j, int i);

    private static final native int nativeSetExposurePriority(long j, int i);

    private static final native int nativeSetExposureRel(long j, int i);

    private static final native int nativeSetFocus(long j, int i);

    private static final native int nativeSetFocusRel(long j, int i);

    private static final native int nativeSetFrameCallback(long j, IFrameCallback iFrameCallback, int i);

    private static final native int nativeSetGain(long j, int i);

    private static final native int nativeSetGamma(long j, int i);

    private static final native int nativeSetHue(long j, int i);

    private static final native int nativeSetIris(long j, int i);

    private static final native int nativeSetIrisRel(long j, int i);

    private static final native int nativeSetPan(long j, int i);

    private static final native int nativeSetPanRel(long j, int i);

    private static final native int nativeSetPowerlineFrequency(long j, int i);

    private static final native int nativeSetPreviewDisplay(long j, Surface surface);

    private static final native int nativeSetPreviewSize(long j, int i, int i2, int i3, int i4, int i5, float f);

    private static final native int nativeSetPrivacy(long j, boolean z);

    private static final native int nativeSetRoll(long j, int i);

    private static final native int nativeSetRollRel(long j, int i);

    private static final native int nativeSetSaturation(long j, int i);

    private static final native int nativeSetScanningMode(long j, int i);

    private static final native int nativeSetSharpness(long j, int i);

    private static final native int nativeSetStatusCallback(long j, IStatusCallback iStatusCallback);

    private static final native int nativeSetTilt(long j, int i);

    private static final native int nativeSetTiltRel(long j, int i);

    private static final native int nativeSetWhiteBlance(long j, int i);

    private static final native int nativeSetWhiteBlanceCompo(long j, int i);

    private static final native int nativeSetZoom(long j, int i);

    private static final native int nativeSetZoomRel(long j, int i);

    private static final native int nativeStartPreview(long j);

    private static final native int nativeStopPreview(long j);

    private final native int nativeUpdateAnalogVideoLockStateLimit(long j);

    private final native int nativeUpdateAnalogVideoStandardLimit(long j);

    private final native int nativeUpdateAutoContrastLimit(long j);

    private final native int nativeUpdateAutoFocusLimit(long j);

    private final native int nativeUpdateAutoHueLimit(long j);

    private final native int nativeUpdateAutoWhiteBlanceCompoLimit(long j);

    private final native int nativeUpdateAutoWhiteBlanceLimit(long j);

    private final native int nativeUpdateBacklightCompLimit(long j);

    private final native int nativeUpdateBrightnessLimit(long j);

    private final native int nativeUpdateContrastLimit(long j);

    private final native int nativeUpdateDigitalMultiplierLimit(long j);

    private final native int nativeUpdateDigitalMultiplierLimitLimit(long j);

    private final native int nativeUpdateExposureLimit(long j);

    private final native int nativeUpdateExposureModeLimit(long j);

    private final native int nativeUpdateExposurePriorityLimit(long j);

    private final native int nativeUpdateExposureRelLimit(long j);

    private final native int nativeUpdateFocusLimit(long j);

    private final native int nativeUpdateFocusRelLimit(long j);

    private final native int nativeUpdateGainLimit(long j);

    private final native int nativeUpdateGammaLimit(long j);

    private final native int nativeUpdateHueLimit(long j);

    private final native int nativeUpdateIrisLimit(long j);

    private final native int nativeUpdateIrisRelLimit(long j);

    private final native int nativeUpdatePanLimit(long j);

    private final native int nativeUpdatePanRelLimit(long j);

    private final native int nativeUpdatePowerlineFrequencyLimit(long j);

    private final native int nativeUpdatePrivacyLimit(long j);

    private final native int nativeUpdateRollLimit(long j);

    private final native int nativeUpdateRollRelLimit(long j);

    private final native int nativeUpdateSaturationLimit(long j);

    private final native int nativeUpdateScanningModeLimit(long j);

    private final native int nativeUpdateSharpnessLimit(long j);

    private final native int nativeUpdateTiltLimit(long j);

    private final native int nativeUpdateTiltRelLimit(long j);

    private final native int nativeUpdateWhiteBlanceCompoLimit(long j);

    private final native int nativeUpdateWhiteBlanceLimit(long j);

    private final native int nativeUpdateZoomLimit(long j);

    private final native int nativeUpdateZoomRelLimit(long j);

    static {
        if (!isLoaded) {
            System.loadLibrary("jpeg-turbo1500");
            System.loadLibrary("usb100");
            System.loadLibrary("uvc");
            System.loadLibrary("UVCCamera");
        }
    }

    public long getHandle() {
        return this.mNativePtr;
    }

    public synchronized void open(USBMonitor.UsbControlBlock ctrlBlock) {
        int result;
        try {
            USBMonitor.UsbControlBlock clone = ctrlBlock.clone();
            this.mCtrlBlock = clone;
            result = nativeConnect(this.mNativePtr, clone.getVenderId(), this.mCtrlBlock.getProductId(), this.mCtrlBlock.getFileDescriptor(), this.mCtrlBlock.getBusNum(), this.mCtrlBlock.getDevNum(), getUSBFSName(this.mCtrlBlock));
        } catch (Exception e) {
            Log.w(TAG, e);
            result = -1;
        }
        if (result == 0) {
            if (this.mNativePtr != 0 && TextUtils.isEmpty(this.mSupportedSize)) {
                this.mSupportedSize = nativeGetSupportedSize(this.mNativePtr);
            }
            nativeSetPreviewSize(this.mNativePtr, DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT, 1, 30, 0, 1.0f);
        } else {
            throw new UnsupportedOperationException("open failed:result=" + result);
        }
    }

    public void setStatusCallback(IStatusCallback callback) {
        long j = this.mNativePtr;
        if (j != 0) {
            nativeSetStatusCallback(j, callback);
        }
    }

    public void setButtonCallback(IButtonCallback callback) {
        long j = this.mNativePtr;
        if (j != 0) {
            nativeSetButtonCallback(j, callback);
        }
    }

    public synchronized void close() {
        stopPreview();
        if (this.mNativePtr != 0) {
            nativeRelease(this.mNativePtr);
        }
        if (this.mCtrlBlock != null) {
            this.mCtrlBlock.close();
            this.mCtrlBlock = null;
        }
        this.mProcSupports = 0;
        this.mControlSupports = 0;
        this.mCurrentFrameFormat = -1;
        this.mCurrentBandwidthFactor = 0.0f;
        this.mSupportedSize = null;
        this.mCurrentSizeList = null;
    }

    public UsbDevice getDevice() {
        USBMonitor.UsbControlBlock usbControlBlock = this.mCtrlBlock;
        if (usbControlBlock != null) {
            return usbControlBlock.getDevice();
        }
        return null;
    }

    public String getDeviceName() {
        USBMonitor.UsbControlBlock usbControlBlock = this.mCtrlBlock;
        if (usbControlBlock != null) {
            return usbControlBlock.getDeviceName();
        }
        return null;
    }

    public USBMonitor.UsbControlBlock getUsbControlBlock() {
        return this.mCtrlBlock;
    }

    public synchronized String getSupportedSize() {
        String str;
        if (!TextUtils.isEmpty(this.mSupportedSize)) {
            str = this.mSupportedSize;
        } else {
            str = nativeGetSupportedSize(this.mNativePtr);
            this.mSupportedSize = str;
        }
        return str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:3:0x000f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.serenegiant.usb.Size getPreviewSize() {
        /*
            r6 = this;
            r0 = 0
            java.util.List r1 = r6.getSupportedSizeList()
            java.util.Iterator r2 = r1.iterator()
        L_0x0009:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0025
            java.lang.Object r3 = r2.next()
            com.serenegiant.usb.Size r3 = (com.serenegiant.usb.Size) r3
            int r4 = r3.width
            int r5 = r6.mCurrentWidth
            if (r4 == r5) goto L_0x0023
            int r4 = r3.height
            int r5 = r6.mCurrentHeight
            if (r4 != r5) goto L_0x0022
            goto L_0x0023
        L_0x0022:
            goto L_0x0009
        L_0x0023:
            r0 = r3
        L_0x0025:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.serenegiant.usb.UVCCamera.getPreviewSize():com.serenegiant.usb.Size");
    }

    public void setPreviewSize(int width, int height) {
        setPreviewSize(width, height, 1, 30, this.mCurrentFrameFormat, this.mCurrentBandwidthFactor);
    }

    public void setPreviewSize(int width, int height, int frameFormat) {
        setPreviewSize(width, height, 1, 30, frameFormat, this.mCurrentBandwidthFactor);
    }

    public void setPreviewSize(int width, int height, int frameFormat, float bandwidth) {
        setPreviewSize(width, height, 1, 30, frameFormat, bandwidth);
    }

    public void setPreviewSize(int width, int height, int min_fps, int max_fps, int frameFormat, float bandwidthFactor) {
        if (width == 0 || height == 0) {
            throw new IllegalArgumentException("invalid preview size");
        }
        long j = this.mNativePtr;
        if (j == 0) {
            return;
        }
        if (nativeSetPreviewSize(j, width, height, min_fps, max_fps, frameFormat, bandwidthFactor) == 0) {
            this.mCurrentFrameFormat = frameFormat;
            this.mCurrentWidth = width;
            this.mCurrentHeight = height;
            this.mCurrentBandwidthFactor = bandwidthFactor;
            return;
        }
        throw new IllegalArgumentException("Failed to set preview size");
    }

    public List<Size> getSupportedSizeList() {
        return getSupportedSize(this.mCurrentFrameFormat > 0 ? 6 : 4, this.mSupportedSize);
    }

    public static List<Size> getSupportedSize(int type, String supportedSize) {
        int format_type;
        List<Size> result = new ArrayList<>();
        if (!TextUtils.isEmpty(supportedSize)) {
            try {
                JSONArray formats = new JSONObject(supportedSize).getJSONArray("formats");
                int format_nums = formats.length();
                for (int i = 0; i < format_nums; i++) {
                    JSONObject format = formats.getJSONObject(i);
                    if (format.has("type") && format.has("size") && ((format_type = format.getInt("type")) == type || type == -1)) {
                        addSize(format, format_type, 0, result);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static final void addSize(JSONObject format, int formatType, int frameType, List<Size> size_list) throws JSONException {
        JSONArray size = format.getJSONArray("size");
        int size_nums = size.length();
        int j = 0;
        while (j < size_nums) {
            String[] sz = size.getString(j).split("x");
            try {
                size_list.add(new Size(formatType, frameType, j, Integer.parseInt(sz[0]), Integer.parseInt(sz[1])));
                j++;
            } catch (Exception e) {
                return;
            }
        }
    }

    public synchronized void startPreview() {
        if (this.mCtrlBlock != null) {
            nativeStartPreview(this.mNativePtr);
        }
    }

    public synchronized void stopPreview() {
        if (this.mCtrlBlock != null) {
            nativeStopPreview(this.mNativePtr);
        }
    }

    public synchronized void destroy() {
        close();
        if (this.mNativePtr != 0) {
            nativeDestroy(this.mNativePtr);
            this.mNativePtr = 0;
        }
    }

    public boolean checkSupportFlag(long flag) {
        updateCameraParams();
        if ((flag & -2147483648L) == -2147483648L) {
            if ((this.mProcSupports & flag) == (2147483647L & flag)) {
                return true;
            }
            return false;
        } else if ((this.mControlSupports & flag) == flag) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized void setAutoFocus(boolean autoFocus) {
        if (this.mNativePtr != 0) {
            nativeSetAutoFocus(this.mNativePtr, autoFocus);
        }
    }

    public synchronized boolean getAutoFocus() {
        boolean result;
        result = true;
        if (this.mNativePtr != 0) {
            result = nativeGetAutoFocus(this.mNativePtr) > 0;
        }
        return result;
    }

    public synchronized void setFocus(int focus) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mFocusMax - this.mFocusMin);
            if (range > 0.0f) {
                nativeSetFocus(this.mNativePtr, ((int) ((((float) focus) / 100.0f) * range)) + this.mFocusMin);
            }
        }
    }

    public synchronized int getFocus(int focus_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateFocusLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mFocusMax - this.mFocusMin);
            if (range > 0.0f) {
                result = (int) ((((float) (focus_abs - this.mFocusMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getFocus() {
        return getFocus(nativeGetFocus(this.mNativePtr));
    }

    public synchronized void resetFocus() {
        if (this.mNativePtr != 0) {
            nativeSetFocus(this.mNativePtr, this.mFocusDef);
        }
    }

    public synchronized void setAutoWhiteBlance(boolean autoWhiteBlance) {
        if (this.mNativePtr != 0) {
            nativeSetAutoWhiteBlance(this.mNativePtr, autoWhiteBlance);
        }
    }

    public synchronized boolean getAutoWhiteBlance() {
        boolean result;
        result = true;
        if (this.mNativePtr != 0) {
            result = nativeGetAutoWhiteBlance(this.mNativePtr) > 0;
        }
        return result;
    }

    public synchronized void setWhiteBlance(int whiteBlance) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mWhiteBlanceMax - this.mWhiteBlanceMin);
            if (range > 0.0f) {
                nativeSetWhiteBlance(this.mNativePtr, ((int) ((((float) whiteBlance) / 100.0f) * range)) + this.mWhiteBlanceMin);
            }
        }
    }

    public synchronized int getWhiteBlance(int whiteBlance_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateWhiteBlanceLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mWhiteBlanceMax - this.mWhiteBlanceMin);
            if (range > 0.0f) {
                result = (int) ((((float) (whiteBlance_abs - this.mWhiteBlanceMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getWhiteBlance() {
        return getFocus(nativeGetWhiteBlance(this.mNativePtr));
    }

    public synchronized void resetWhiteBlance() {
        if (this.mNativePtr != 0) {
            nativeSetWhiteBlance(this.mNativePtr, this.mWhiteBlanceDef);
        }
    }

    public synchronized void setBrightness(int brightness) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mBrightnessMax - this.mBrightnessMin);
            if (range > 0.0f) {
                nativeSetBrightness(this.mNativePtr, ((int) ((((float) brightness) / 100.0f) * range)) + this.mBrightnessMin);
            }
        }
    }

    public synchronized int getBrightness(int brightness_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateBrightnessLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mBrightnessMax - this.mBrightnessMin);
            if (range > 0.0f) {
                result = (int) ((((float) (brightness_abs - this.mBrightnessMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getBrightness() {
        return getBrightness(nativeGetBrightness(this.mNativePtr));
    }

    public synchronized void resetBrightness() {
        if (this.mNativePtr != 0) {
            nativeSetBrightness(this.mNativePtr, this.mBrightnessDef);
        }
    }

    public synchronized void setContrast(int contrast) {
        if (this.mNativePtr != 0) {
            nativeUpdateContrastLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mContrastMax - this.mContrastMin);
            if (range > 0.0f) {
                nativeSetContrast(this.mNativePtr, ((int) ((((float) contrast) / 100.0f) * range)) + this.mContrastMin);
            }
        }
    }

    public synchronized int getContrast(int contrast_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mContrastMax - this.mContrastMin);
            if (range > 0.0f) {
                result = (int) ((((float) (contrast_abs - this.mContrastMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getContrast() {
        return getContrast(nativeGetContrast(this.mNativePtr));
    }

    public synchronized void resetContrast() {
        if (this.mNativePtr != 0) {
            nativeSetContrast(this.mNativePtr, this.mContrastDef);
        }
    }

    public synchronized void setSharpness(int sharpness) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mSharpnessMax - this.mSharpnessMin);
            if (range > 0.0f) {
                nativeSetSharpness(this.mNativePtr, ((int) ((((float) sharpness) / 100.0f) * range)) + this.mSharpnessMin);
            }
        }
    }

    public synchronized int getSharpness(int sharpness_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateSharpnessLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mSharpnessMax - this.mSharpnessMin);
            if (range > 0.0f) {
                result = (int) ((((float) (sharpness_abs - this.mSharpnessMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getSharpness() {
        return getSharpness(nativeGetSharpness(this.mNativePtr));
    }

    public synchronized void resetSharpness() {
        if (this.mNativePtr != 0) {
            nativeSetSharpness(this.mNativePtr, this.mSharpnessDef);
        }
    }

    public synchronized void setGain(int gain) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mGainMax - this.mGainMin);
            if (range > 0.0f) {
                nativeSetGain(this.mNativePtr, ((int) ((((float) gain) / 100.0f) * range)) + this.mGainMin);
            }
        }
    }

    public synchronized int getGain(int gain_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateGainLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mGainMax - this.mGainMin);
            if (range > 0.0f) {
                result = (int) ((((float) (gain_abs - this.mGainMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getGain() {
        return getGain(nativeGetGain(this.mNativePtr));
    }

    public synchronized void resetGain() {
        if (this.mNativePtr != 0) {
            nativeSetGain(this.mNativePtr, this.mGainDef);
        }
    }

    public synchronized void setGamma(int gamma) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mGammaMax - this.mGammaMin);
            if (range > 0.0f) {
                nativeSetGamma(this.mNativePtr, ((int) ((((float) gamma) / 100.0f) * range)) + this.mGammaMin);
            }
        }
    }

    public synchronized int getGamma(int gamma_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateGammaLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mGammaMax - this.mGammaMin);
            if (range > 0.0f) {
                result = (int) ((((float) (gamma_abs - this.mGammaMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getGamma() {
        return getGamma(nativeGetGamma(this.mNativePtr));
    }

    public synchronized void resetGamma() {
        if (this.mNativePtr != 0) {
            nativeSetGamma(this.mNativePtr, this.mGammaDef);
        }
    }

    public synchronized void setSaturation(int saturation) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mSaturationMax - this.mSaturationMin);
            if (range > 0.0f) {
                nativeSetSaturation(this.mNativePtr, ((int) ((((float) saturation) / 100.0f) * range)) + this.mSaturationMin);
            }
        }
    }

    public synchronized int getSaturation(int saturation_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateSaturationLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mSaturationMax - this.mSaturationMin);
            if (range > 0.0f) {
                result = (int) ((((float) (saturation_abs - this.mSaturationMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getSaturation() {
        return getSaturation(nativeGetSaturation(this.mNativePtr));
    }

    public synchronized void resetSaturation() {
        if (this.mNativePtr != 0) {
            nativeSetSaturation(this.mNativePtr, this.mSaturationDef);
        }
    }

    public synchronized void setHue(int hue) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mHueMax - this.mHueMin);
            if (range > 0.0f) {
                nativeSetHue(this.mNativePtr, ((int) ((((float) hue) / 100.0f) * range)) + this.mHueMin);
            }
        }
    }

    public synchronized int getHue(int hue_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateHueLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mHueMax - this.mHueMin);
            if (range > 0.0f) {
                result = (int) ((((float) (hue_abs - this.mHueMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getHue() {
        return getHue(nativeGetHue(this.mNativePtr));
    }

    public synchronized void resetHue() {
        if (this.mNativePtr != 0) {
            nativeSetHue(this.mNativePtr, this.mSaturationDef);
        }
    }

    public void setPowerlineFrequency(int frequency) {
        long j = this.mNativePtr;
        if (j != 0) {
            nativeSetPowerlineFrequency(j, frequency);
        }
    }

    public int getPowerlineFrequency() {
        return nativeGetPowerlineFrequency(this.mNativePtr);
    }

    public synchronized void setZoom(int zoom) {
        if (this.mNativePtr != 0) {
            float range = (float) Math.abs(this.mZoomMax - this.mZoomMin);
            if (range > 0.0f) {
                nativeSetZoom(this.mNativePtr, ((int) ((((float) zoom) / 100.0f) * range)) + this.mZoomMin);
            }
        }
    }

    public synchronized int getZoom(int zoom_abs) {
        int result;
        result = 0;
        if (this.mNativePtr != 0) {
            nativeUpdateZoomLimit(this.mNativePtr);
            float range = (float) Math.abs(this.mZoomMax - this.mZoomMin);
            if (range > 0.0f) {
                result = (int) ((((float) (zoom_abs - this.mZoomMin)) * 100.0f) / range);
            }
        }
        return result;
    }

    public synchronized int getZoom() {
        return getZoom(nativeGetZoom(this.mNativePtr));
    }

    public synchronized void resetZoom() {
        if (this.mNativePtr != 0) {
            nativeSetZoom(this.mNativePtr, this.mZoomDef);
        }
    }

    public synchronized void updateCameraParams() {
        if (this.mNativePtr == 0) {
            this.mProcSupports = 0;
            this.mControlSupports = 0;
        } else if (this.mControlSupports == 0 || this.mProcSupports == 0) {
            if (this.mControlSupports == 0) {
                this.mControlSupports = nativeGetCtrlSupports(this.mNativePtr);
            }
            if (this.mProcSupports == 0) {
                this.mProcSupports = nativeGetProcSupports(this.mNativePtr);
            }
            if (!(this.mControlSupports == 0 || this.mProcSupports == 0)) {
                nativeUpdateBrightnessLimit(this.mNativePtr);
                nativeUpdateContrastLimit(this.mNativePtr);
                nativeUpdateSharpnessLimit(this.mNativePtr);
                nativeUpdateGainLimit(this.mNativePtr);
                nativeUpdateGammaLimit(this.mNativePtr);
                nativeUpdateSaturationLimit(this.mNativePtr);
                nativeUpdateHueLimit(this.mNativePtr);
                nativeUpdateZoomLimit(this.mNativePtr);
                nativeUpdateWhiteBlanceLimit(this.mNativePtr);
                nativeUpdateFocusLimit(this.mNativePtr);
            }
        }
    }

    private static final void dumpControls(long controlSupports) {
        Log.i(TAG, String.format("controlSupports=%x", new Object[]{Long.valueOf(controlSupports)}));
        for (int i = 0; i < SUPPORTS_CTRL.length; i++) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(SUPPORTS_CTRL[i]);
            sb.append((((long) (1 << i)) & controlSupports) != 0 ? "=enabled" : "=disabled");
            Log.i(str, sb.toString());
        }
    }

    private static final void dumpProc(long procSupports) {
        Log.i(TAG, String.format("procSupports=%x", new Object[]{Long.valueOf(procSupports)}));
        for (int i = 0; i < SUPPORTS_PROC.length; i++) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(SUPPORTS_PROC[i]);
            sb.append((((long) (1 << i)) & procSupports) != 0 ? "=enabled" : "=disabled");
            Log.i(str, sb.toString());
        }
    }

    private final String getUSBFSName(USBMonitor.UsbControlBlock ctrlBlock) {
        String result = null;
        String name = ctrlBlock.getDeviceName();
        String[] v = !TextUtils.isEmpty(name) ? name.split("/") : null;
        if (v != null && v.length > 2) {
            StringBuilder sb = new StringBuilder(v[0]);
            for (int i = 1; i < v.length - 2; i++) {
                sb.append("/");
                sb.append(v[i]);
            }
            result = sb.toString();
        }
        if (!TextUtils.isEmpty(result)) {
            return result;
        }
        String str = TAG;
        Log.w(str, "failed to get USBFS path, try to use default path:" + name);
        return DEFAULT_USBFS;
    }

    public void startCapture(Surface surface) {
        if (this.mCtrlBlock == null || surface == null) {
            throw new NullPointerException("startCapture");
        }
        nativeSetCaptureDisplay(this.mNativePtr, surface);
    }

    public void stopCapture() {
        if (this.mCtrlBlock != null) {
            nativeSetCaptureDisplay(this.mNativePtr, (Surface) null);
        }
    }
}

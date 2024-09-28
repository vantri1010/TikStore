package org.webrtc.ali;

import android.text.TextUtils;
import android.util.Log;
import com.serenegiant.usb.USBMonitor;

public class USBAudioDevice {
    private static final String DEFAULT_USBFS = "/dev/bus/usb";
    static final int LOG_DEBUG = 2;
    static final int LOG_ERROR = 5;
    static final int LOG_FATAL = 6;
    static final int LOG_INFO = 3;
    static final int LOG_SILENT = 7;
    static final int LOG_VERBOSE = 1;
    static final int LOG_WARN = 4;
    private final String TAG;
    private long audioDevicePlayerHandle_;
    private long audioDeviceRecordHandle_;
    private int audioPlayerDeviceCount_;
    private int audioRecordDeviceCount_;
    private USBMonitor.UsbControlBlock usbControlPlayBlock_;
    private USBMonitor.UsbControlBlock usbControlRecordBlock_;

    private static final native int nativeCloseUSBPlayer(long j);

    private static final native int nativeCloseUSBRecord(long j);

    private final native int nativeConnectUSBPlayer(long j, int i, int i2, int i3, int i4, int i5, String str);

    private final native int nativeConnectUSBRecord(long j, int i, int i2, int i3, int i4, int i5, String str);

    private static final native int nativeCreateUSBLog(String str, int i);

    private final native long nativeCreateUSBPlayer();

    private final native long nativeCreateUSBRecord();

    private static final native int nativeDestroyUSBLog(int i);

    private final native void nativeDestroyUSBPlayer(long j);

    private final native void nativeDestroyUSBRecord(long j);

    private static final native int nativeStopUSBPlayer(long j);

    private static final native int nativeStopUSBRecord(long j);

    private static final native int nativeWriteUSBLog(int i, String str);

    static {
        System.loadLibrary("usb100");
        System.loadLibrary("USBAudioDevice");
    }

    private final String getUSBFSName(String deviceName) {
        String result = null;
        String[] v = !TextUtils.isEmpty(deviceName) ? deviceName.split("/") : null;
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
        Log.w("USBAudioDevice", "failed to get USBFS path, try to use default path:" + deviceName);
        return DEFAULT_USBFS;
    }

    public USBAudioDevice() {
        this.TAG = "USBAudioDevice";
        this.audioDeviceRecordHandle_ = 0;
        this.audioRecordDeviceCount_ = 0;
        this.audioDevicePlayerHandle_ = 0;
        this.audioPlayerDeviceCount_ = 0;
        this.audioDeviceRecordHandle_ = nativeCreateUSBRecord();
        this.audioDevicePlayerHandle_ = nativeCreateUSBPlayer();
    }

    public synchronized void release() {
        releaseUSBRecord();
        releaseUSBPlayer();
        releaseLogFile(0);
    }

    public synchronized long getAudioRecordHandle() {
        return this.audioDeviceRecordHandle_;
    }

    public int getAudioRecordDeviceCount() {
        return this.audioRecordDeviceCount_;
    }

    public synchronized int connectUSBRecord(USBMonitor.UsbControlBlock usbControlBlock) {
        if (this.audioDeviceRecordHandle_ == 0) {
            return -1;
        }
        try {
            USBMonitor.UsbControlBlock clone = usbControlBlock.clone();
            this.usbControlRecordBlock_ = clone;
            int rc = nativeConnectUSBRecord(this.audioDeviceRecordHandle_, clone.getVenderId(), this.usbControlRecordBlock_.getProductId(), this.usbControlRecordBlock_.getFileDescriptor(), this.usbControlRecordBlock_.getBusNum(), this.usbControlRecordBlock_.getDevNum(), getUSBFSName(this.usbControlRecordBlock_.getDeviceName()));
            if (rc <= 0) {
                return -2;
            }
            this.audioRecordDeviceCount_ = rc;
            return 0;
        } catch (Exception e) {
            writeLog(5, "info:" + e.getMessage());
            return -3;
        }
    }

    public synchronized int closeUSBRecord() {
        if (this.audioDeviceRecordHandle_ == 0) {
            return -1;
        }
        this.audioRecordDeviceCount_ = 0;
        return nativeCloseUSBRecord(this.audioDeviceRecordHandle_);
    }

    public synchronized int stopUSBRecord() {
        if (this.audioDeviceRecordHandle_ == 0) {
            return -1;
        }
        return nativeStopUSBRecord(this.audioDeviceRecordHandle_);
    }

    public synchronized void releaseUSBRecord() {
        if (this.audioDeviceRecordHandle_ != 0) {
            nativeDestroyUSBRecord(this.audioDeviceRecordHandle_);
            this.audioDeviceRecordHandle_ = 0;
            this.audioRecordDeviceCount_ = 0;
        }
    }

    public synchronized long getAudioPlayerHandle() {
        return this.audioDevicePlayerHandle_;
    }

    public synchronized int getAudioPlayerDeviceCount() {
        return this.audioPlayerDeviceCount_;
    }

    public synchronized int connectUSBPlayer(USBMonitor.UsbControlBlock usbControlBlock) {
        if (this.audioDevicePlayerHandle_ == 0) {
            return -1;
        }
        try {
            USBMonitor.UsbControlBlock clone = usbControlBlock.clone();
            this.usbControlPlayBlock_ = clone;
            int rc = nativeConnectUSBPlayer(this.audioDevicePlayerHandle_, clone.getVenderId(), this.usbControlPlayBlock_.getProductId(), this.usbControlPlayBlock_.getFileDescriptor(), this.usbControlPlayBlock_.getBusNum(), this.usbControlPlayBlock_.getDevNum(), getUSBFSName(this.usbControlPlayBlock_.getDeviceName()));
            if (rc <= 0) {
                return -2;
            }
            this.audioPlayerDeviceCount_ = rc;
            return 0;
        } catch (Exception e) {
            writeLog(5, "info:" + e.getMessage());
            return -3;
        }
    }

    public synchronized int closeUSBPlayer() {
        if (this.audioDevicePlayerHandle_ == 0) {
            return -1;
        }
        this.audioPlayerDeviceCount_ = 0;
        return nativeCloseUSBPlayer(this.audioDevicePlayerHandle_);
    }

    public synchronized int stopUSBPlayer() {
        if (this.audioDevicePlayerHandle_ == 0) {
            return -1;
        }
        return nativeStopUSBPlayer(this.audioDevicePlayerHandle_);
    }

    public synchronized void releaseUSBPlayer() {
        if (this.audioDevicePlayerHandle_ != 0) {
            nativeDestroyUSBPlayer(this.audioDevicePlayerHandle_);
            this.audioDevicePlayerHandle_ = 0;
            this.audioPlayerDeviceCount_ = 0;
        }
    }

    public static synchronized int createLogFile(String strPath, int level) {
        int nativeCreateUSBLog;
        synchronized (USBAudioDevice.class) {
            nativeCreateUSBLog = nativeCreateUSBLog(strPath, level);
        }
        return nativeCreateUSBLog;
    }

    public static int writeLog(int tag, String strLog) {
        return nativeWriteUSBLog(tag, strLog);
    }

    public static int writeLog(String strLog) {
        return nativeWriteUSBLog(3, strLog);
    }

    public static synchronized int releaseLogFile(int param) {
        int nativeDestroyUSBLog;
        synchronized (USBAudioDevice.class) {
            nativeDestroyUSBLog = nativeDestroyUSBLog(param);
        }
        return nativeDestroyUSBLog;
    }
}

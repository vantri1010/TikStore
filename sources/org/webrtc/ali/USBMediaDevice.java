package org.webrtc.ali;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.os.Handler;
import android.os.Looper;
import com.serenegiant.usb.IButtonCallback;
import com.serenegiant.usb.IStatusCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import java.nio.ByteBuffer;
import java.util.List;

public class USBMediaDevice implements USBMonitor.OnDeviceConnectListener, IStatusCallback, IButtonCallback {
    private static long hAudioPlayerHandle_ = 0;
    private static long hAudioRecordHandle_ = 0;
    private static long hCameraHandle_ = 0;
    static final int kMaxRetryConnectAudioCount = 256;
    static final int kMaxRetryConnectCameraCount = 256;
    private final String TAG = "USBMediaDevice";
    protected final int USB_AUDIO_IN_TYPE = 128;
    protected final int USB_DEVICE_NONE = 0;
    protected final int USB_FLAG_AUDIO_TYPE_PLAYER = 4;
    protected final int USB_FLAG_AUDIO_TYPE_RECORD = 2;
    protected final int USB_FLAG_CAMERA_TYPE = 1;
    protected boolean mAttachUSBDevice = false;
    private USBAudioDevice mAudioDevice;
    protected boolean mAudioPlayerConnectFail = false;
    protected boolean mAudioPlayerConnected = false;
    private int mAudioPlayerEndPointCount;
    protected int mAudioPlayerRetryConnectCount = 0;
    protected boolean mAudioRecordConnectFail = false;
    protected boolean mAudioRecordConnected = false;
    private int mAudioRecordEndPointCount;
    protected int mAudioRecordRetryConnectCount = 0;
    private String mAudioUsbPlayerDevice = "";
    private String mAudioUsbRecordDevice = "";
    protected boolean mCameraConnectFail = false;
    protected boolean mCameraConnected = false;
    protected int mCameraRetryCount = 0;
    private String mCameraUsbDevice = "";
    protected USBMediaDeviceEvent mMediaDeviceEvent;
    private USBMonitor mUSBMonitor;
    private UVCCamera mUVCCamera;

    public interface USBMediaDeviceEvent {
        void onUSBDeviceCancel();

        int onUSBDeviceConnect();

        void onUSBDeviceDisconnect();
    }

    public static long getAudioRecordHandle() {
        return hAudioRecordHandle_;
    }

    public static long getAudioPlayerHandle() {
        return hAudioPlayerHandle_;
    }

    public static long getCameraHandle() {
        return hCameraHandle_;
    }

    /* access modifiers changed from: protected */
    public int EnumUsbMediaDeviceType(UsbDevice device, int index) {
        int nDeviceFlag = 0;
        for (int i = index; i < device.getInterfaceCount(); i++) {
            UsbInterface inf = device.getInterface(i);
            if (inf.getInterfaceClass() == 1) {
                for (int j = 0; j < inf.getEndpointCount(); j++) {
                    writeLog("end point addr:" + inf.getEndpoint(j).getAddress());
                    if (inf.getEndpoint(j).getAddress() >= 128) {
                        nDeviceFlag |= 2;
                        writeLog("find record device");
                    } else {
                        nDeviceFlag |= 4;
                        writeLog("find player device");
                    }
                }
            } else if (inf.getInterfaceClass() == 14) {
                writeLog("find camera device");
                nDeviceFlag |= 1;
            }
        }
        return nDeviceFlag;
    }

    public int getAudioRecordDevCount() {
        return this.mAudioRecordEndPointCount;
    }

    public int getAudioPlayerDevCount() {
        return this.mAudioPlayerEndPointCount;
    }

    public boolean getCameraConnected() {
        return this.mCameraConnected;
    }

    public boolean attachUSBDevice() {
        return this.mAttachUSBDevice;
    }

    public String getCameraName() {
        UVCCamera uVCCamera = this.mUVCCamera;
        if (uVCCamera == null) {
            return null;
        }
        return uVCCamera.getDeviceName();
    }

    public void onStatus(int var1, int var2, int var3, int var4, ByteBuffer var5) {
    }

    public void onButton(int var1, int var2) {
    }

    public void onAttach(UsbDevice device) {
        int deviceFlag = EnumUsbMediaDeviceType(device, 0);
        writeLog("usb device name:" + device.getDeviceName() + " attach flag:" + deviceFlag);
        boolean toRequestPermission = false;
        if (this.mCameraUsbDevice.isEmpty() && (deviceFlag & 1) > 0) {
            this.mCameraUsbDevice = device.getDeviceName();
            toRequestPermission = true;
            writeLog("usb device has camera device name:" + device.getDeviceName());
        }
        if (this.mAudioUsbPlayerDevice.isEmpty() && (deviceFlag & 4) > 0) {
            this.mAudioUsbPlayerDevice = device.getDeviceName();
            toRequestPermission = true;
            writeLog("usb device has audio player device name:" + device.getDeviceName());
        }
        if (this.mAudioUsbRecordDevice.isEmpty() && (deviceFlag & 2) > 0) {
            this.mAudioUsbRecordDevice = device.getDeviceName();
            toRequestPermission = true;
            writeLog("usb device has audio record device name:" + device.getDeviceName());
        }
        if (toRequestPermission) {
            this.mUSBMonitor.requestPermission(device);
        }
    }

    public void onDettach(UsbDevice device) {
        writeLog("usb device name:" + device.getDeviceName() + " dettach...");
        if (device.getDeviceName().equals(this.mAudioUsbPlayerDevice)) {
            this.mAudioUsbPlayerDevice = "";
            this.mAudioPlayerConnected = false;
            writeLog("usb player device name:" + device.getDeviceName() + " dettach...");
        }
        if (device.getDeviceName().equals(this.mAudioUsbRecordDevice)) {
            this.mAudioUsbRecordDevice = "";
            this.mAudioRecordConnected = false;
            writeLog("usb record device name:" + device.getDeviceName() + " dettach...");
        }
        if (device.getDeviceName().equals(this.mCameraUsbDevice)) {
            this.mCameraUsbDevice = "";
            this.mCameraConnected = false;
            writeLog("usb camera device name:" + device.getDeviceName() + " dettach...");
        }
    }

    private void doUSBConnectEvent(UsbDevice device) {
        if ((this.mCameraConnected || this.mCameraConnectFail || this.mCameraUsbDevice.isEmpty()) && ((this.mAudioRecordConnected || this.mAudioRecordConnectFail || this.mAudioUsbRecordDevice.isEmpty()) && (this.mAudioPlayerConnected || this.mAudioPlayerConnectFail || this.mAudioUsbPlayerDevice.isEmpty()))) {
            writeLog("usb device name:" + device.getDeviceName() + " onConnect... count:" + this.mAudioRecordEndPointCount);
            if (!this.mAttachUSBDevice) {
                this.mAttachUSBDevice = true;
                writeLog("do device connect event...");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        USBMediaDevice.this.mMediaDeviceEvent.onUSBDeviceConnect();
                    }
                });
            }
        } else if (this.mCameraConnectFail) {
            writeLog("usb device name:" + device.getDeviceName() + " onConnect failed... count:" + this.mAudioRecordEndPointCount);
            if (!this.mAttachUSBDevice) {
                this.mAttachUSBDevice = true;
                writeLog("do device cancel event...");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        USBMediaDevice.this.mMediaDeviceEvent.onUSBDeviceCancel();
                    }
                });
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean connectCameraDevice(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
        if (this.mCameraConnected || !device.getDeviceName().equals(this.mCameraUsbDevice)) {
            return false;
        }
        try {
            writeLog("try connect camera device :" + device.getDeviceName());
            this.mUVCCamera.open(ctrlBlock);
            this.mUVCCamera.setStatusCallback(this);
            this.mUVCCamera.setButtonCallback(this);
            hCameraHandle_ = this.mUVCCamera.getHandle();
            this.mCameraConnected = true;
            this.mCameraRetryCount = 0;
            writeLog(" connect camera device :" + device.getDeviceName() + " succ!");
        } catch (Exception ex) {
            writeLog("failed to connect camera device :" + device.getDeviceName() + "!");
            writeLog("camera open ex:" + ex.getMessage() + " retrycount:" + this.mCameraRetryCount);
            this.mCameraRetryCount = this.mCameraRetryCount + 1;
        }
        if (this.mCameraRetryCount > 256) {
            this.mCameraConnectFail = true;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean connectAudioDevice(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
        USBMonitor.UsbControlBlock usbControlBlock = ctrlBlock;
        boolean bEvent = false;
        if (!this.mAudioRecordConnected && device.getDeviceName().equals(this.mAudioUsbRecordDevice)) {
            writeLog("try connect audio record device :" + device.getDeviceName());
            boolean connectFail = true;
            try {
                this.mAudioDevice.connectUSBRecord(usbControlBlock);
                this.mAudioRecordEndPointCount = this.mAudioDevice.getAudioRecordDeviceCount();
                connectFail = false;
            } catch (Exception ex) {
                this.mAudioRecordEndPointCount = 0;
                writeLog("failed to connect audio record device :" + device.getDeviceName() + "!");
                writeLog("audio record device open ex:" + ex.getMessage() + " retrycount:" + this.mAudioRecordRetryConnectCount);
            }
            if (connectFail || this.mAudioRecordEndPointCount == 0) {
                this.mAudioDevice.closeUSBRecord();
                this.mAudioRecordRetryConnectCount++;
                writeLog("failed to connect audio record device :" + device.getDeviceName() + " ref:" + this.mAudioRecordRetryConnectCount + " !");
            } else {
                hAudioRecordHandle_ = this.mAudioDevice.getAudioRecordHandle();
                this.mAudioRecordConnected = true;
                this.mAudioRecordRetryConnectCount = 0;
                writeLog(" connect audio record device :" + device.getDeviceName() + " succ!");
            }
            if (this.mAudioRecordRetryConnectCount >= 256) {
                this.mAudioRecordConnectFail = true;
            }
            bEvent = true;
        }
        if (!this.mAudioPlayerConnected && device.getDeviceName().equals(this.mAudioUsbPlayerDevice)) {
            writeLog("try connect audio device :" + device.getDeviceName());
            boolean connectFail2 = true;
            try {
                this.mAudioDevice.connectUSBPlayer(usbControlBlock);
                this.mAudioPlayerEndPointCount = this.mAudioDevice.getAudioPlayerDeviceCount();
                connectFail2 = false;
            } catch (Exception ex2) {
                this.mAudioPlayerEndPointCount = 0;
                writeLog("failed to connect audio device :" + device.getDeviceName() + "!");
                writeLog("audio device open ex:" + ex2.getMessage() + " retrycount:" + this.mAudioPlayerRetryConnectCount);
            }
            if (connectFail2 || this.mAudioPlayerEndPointCount == 0) {
                this.mAudioDevice.closeUSBPlayer();
                this.mAudioPlayerRetryConnectCount++;
                writeLog("failed to connect audio device :" + device.getDeviceName() + " ref:" + this.mAudioPlayerRetryConnectCount + " !");
            } else {
                hAudioPlayerHandle_ = this.mAudioDevice.getAudioPlayerHandle();
                bEvent = true;
                this.mAudioPlayerConnected = true;
                this.mAudioPlayerRetryConnectCount = 0;
                writeLog(" connect audio device :" + device.getDeviceName() + " succ!");
            }
            if (this.mAudioPlayerRetryConnectCount >= 256) {
                this.mAudioPlayerConnectFail = true;
            }
        }
        return bEvent;
    }

    public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
        writeLog("connect device name:" + device.getDeviceName() + " status camera:" + this.mCameraConnected + " audio record:" + this.mAudioRecordConnected + " audio player:" + this.mAudioPlayerConnected);
        boolean bCameraEvent = connectCameraDevice(device, ctrlBlock);
        boolean bAudioDeviceEvent = connectAudioDevice(device, ctrlBlock);
        if (bCameraEvent || bAudioDeviceEvent) {
            doUSBConnectEvent(device);
        }
    }

    /* access modifiers changed from: protected */
    public void closeAll(UsbDevice device) {
        if (device.getDeviceName().equals(this.mCameraUsbDevice) && this.mUVCCamera != null) {
            writeLog("stop camera enter name:" + device.getDeviceName());
            this.mUVCCamera.stopPreview();
            this.mUVCCamera.setStatusCallback((IStatusCallback) null);
            this.mUVCCamera.setButtonCallback((IButtonCallback) null);
            this.mUVCCamera.close();
            this.mCameraConnected = false;
            writeLog("stop camera leave!");
        }
        if (device.getDeviceName().equals(this.mAudioUsbRecordDevice) && this.mAudioDevice != null) {
            writeLog("stop audio record enter name:" + device.getDeviceName());
            this.mAudioDevice.closeUSBRecord();
            this.mAudioRecordConnected = false;
            this.mAudioRecordEndPointCount = 0;
            writeLog("stop audio record leave!");
        }
        if (device.getDeviceName().equals(this.mAudioUsbPlayerDevice) && this.mAudioDevice != null) {
            writeLog("stop audio player enter name:" + device.getDeviceName());
            this.mAudioDevice.closeUSBPlayer();
            this.mAudioPlayerConnected = false;
            this.mAudioPlayerEndPointCount = 0;
            writeLog("stop audio player leave!");
        }
    }

    public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
        writeLog("usb device name:" + device.getDeviceName() + " onDisConnect ...enter");
        synchronized (this) {
            closeAll(device);
        }
        if (this.mAttachUSBDevice && !this.mCameraConnected && !this.mAudioPlayerConnected && !this.mAudioRecordConnected) {
            this.mAttachUSBDevice = false;
            writeLog("all usb device disconnect!");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    USBMediaDevice.this.mMediaDeviceEvent.onUSBDeviceDisconnect();
                }
            });
        }
        writeLog("usb device name:" + device.getDeviceName() + " onDisConnect...leave");
    }

    public void onCancel(UsbDevice device) {
        if (!this.mAttachUSBDevice) {
            this.mAttachUSBDevice = true;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    USBMediaDevice.this.mMediaDeviceEvent.onUSBDeviceCancel();
                }
            });
        }
        writeLog("usb device name:" + device.getDeviceName() + " onCancel");
    }

    public USBMediaDevice(Context context, USBMediaDeviceEvent MediaDeviceEvent) {
        USBAudioDevice.createLogFile("sdcard/usblog/", 2);
        writeLog("create usb device...");
        this.mUSBMonitor = new USBMonitor(context, this);
        this.mUVCCamera = new UVCCamera();
        this.mAudioDevice = new USBAudioDevice();
        this.mMediaDeviceEvent = MediaDeviceEvent;
        this.mUSBMonitor.register();
    }

    public int setupDevice() {
        List<UsbDevice> deviceList = this.mUSBMonitor.getDeviceList();
        writeLog("usb list count: " + deviceList.size());
        int nDeviceCount = 0;
        for (UsbDevice item : deviceList) {
            if (item.getInterfaceCount() >= 0) {
                writeLog("usb productid:" + item.getProductId() + " devclass:" + item.getDeviceClass() + " incount:" + item.getInterfaceCount() + " inclass:" + item.getInterface(0).getInterfaceClass() + " name:" + item.getDeviceName());
            }
            int nDeviceFlag = EnumUsbMediaDeviceType(item, 0);
            if (nDeviceFlag != 0) {
                writeLog("device " + item.getDeviceName() + " flag:" + nDeviceFlag);
                boolean toRequestPermission = false;
                if (this.mCameraUsbDevice.isEmpty() && (nDeviceFlag & 1) != 0) {
                    this.mCameraUsbDevice = item.getDeviceName();
                    toRequestPermission = true;
                    nDeviceCount++;
                }
                if (this.mAudioUsbRecordDevice.isEmpty() && (nDeviceFlag & 2) != 0) {
                    this.mAudioUsbRecordDevice = item.getDeviceName();
                    toRequestPermission = true;
                    nDeviceCount++;
                }
                if (this.mAudioUsbPlayerDevice.isEmpty() && (nDeviceFlag & 4) != 0) {
                    this.mAudioUsbPlayerDevice = item.getDeviceName();
                    toRequestPermission = true;
                    nDeviceCount++;
                }
                if (toRequestPermission) {
                    this.mUSBMonitor.requestPermission(item);
                    writeLog("usb permission " + item.getDeviceName() + " succ");
                }
            }
        }
        writeLog("setup usb device count:" + nDeviceCount);
        return nDeviceCount;
    }

    public void release() {
        if (this.mUSBMonitor != null) {
            writeLog("USBMediaDevice release...");
            synchronized (this) {
                if (this.mUSBMonitor != null) {
                    this.mUSBMonitor.unregister();
                    hCameraHandle_ = 0;
                    hAudioRecordHandle_ = 0;
                    hAudioPlayerHandle_ = 0;
                    if (this.mAudioDevice != null) {
                        this.mAudioDevice.closeUSBRecord();
                        this.mAudioDevice.closeUSBPlayer();
                    }
                    if (this.mUVCCamera != null) {
                        this.mUVCCamera.stopPreview();
                    }
                    if (this.mUVCCamera != null) {
                        this.mUVCCamera.close();
                        this.mUVCCamera.destroy();
                        this.mUVCCamera = null;
                    }
                    if (this.mAudioDevice != null) {
                        this.mAudioDevice.release();
                        this.mAudioDevice = null;
                    }
                    this.mUSBMonitor.destroy();
                    this.mUSBMonitor = null;
                    this.mCameraUsbDevice = "";
                    this.mAudioUsbPlayerDevice = "";
                    this.mAudioUsbRecordDevice = "";
                }
            }
        }
    }

    public void writeLog(String strLog) {
        USBAudioDevice.writeLog(3, strLog);
    }
}

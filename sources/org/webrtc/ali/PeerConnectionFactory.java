package org.webrtc.ali;

import android.content.Context;
import java.util.List;
import org.webrtc.ali.EglBase;
import org.webrtc.ali.PeerConnection;

public class PeerConnectionFactory {
    private static final String TAG = "PeerConnectionFactory";
    private static final String VIDEO_CAPTURER_THREAD_NAME = "VideoCapturerThread";
    private static Context applicationContext;
    private static volatile boolean nativeLibLoaded;
    private static Thread networkThread;
    private static Thread signalingThread;
    private static Thread workerThread;
    private EglBase localEglbase;
    private final long nativeFactory;
    private EglBase remoteEglbase;

    public static class Options {
        static final int ADAPTER_TYPE_CELLULAR = 4;
        static final int ADAPTER_TYPE_ETHERNET = 1;
        static final int ADAPTER_TYPE_LOOPBACK = 16;
        static final int ADAPTER_TYPE_UNKNOWN = 0;
        static final int ADAPTER_TYPE_VPN = 8;
        static final int ADAPTER_TYPE_WIFI = 2;
        public boolean disableEncryption;
        public boolean disableNetworkMonitor;
        public int networkIgnoreMask;
    }

    public static native void initializeFieldTrials(String str);

    public static native void initializeInternalTracer();

    private static native long nativeCreateAudioSource(long j, MediaConstraints mediaConstraints);

    private static native long nativeCreateAudioTrack(long j, String str, long j2);

    private static native long nativeCreateLocalMediaStream(long j, String str);

    private static native long nativeCreateObserver(PeerConnection.Observer observer);

    private static native long nativeCreatePeerConnection(long j, PeerConnection.RTCConfiguration rTCConfiguration, MediaConstraints mediaConstraints, long j2);

    private static native long nativeCreatePeerConnectionFactory(Options options, VideoEncoderFactory videoEncoderFactory, VideoDecoderFactory videoDecoderFactory);

    private static native long nativeCreateVideoSource(long j, SurfaceTextureHelper surfaceTextureHelper, boolean z);

    private static native long nativeCreateVideoTrack(long j, String str, long j2);

    private static native String nativeFieldTrialsFindFullName(String str);

    private static native void nativeFreeFactory(long j);

    public static native void nativeInitializeAndroidGlobals(Context context, boolean z);

    private static native void nativeSetVideoHwAccelerationOptions(long j, Object obj, Object obj2);

    private static native boolean nativeStartAecDump(long j, int i, int i2);

    private static native void nativeStopAecDump(long j);

    private static native void nativeThreadsCallbacks(long j);

    public static native void shutdownInternalTracer();

    public static native boolean startInternalTracingCapture(String str);

    public static native void stopInternalTracingCapture();

    @Deprecated
    public native void nativeSetOptions(long j, Options options);

    static {
        try {
            nativeLibLoaded = false;
        } catch (UnsatisfiedLinkError e) {
            nativeLibLoaded = false;
        }
    }

    public static void initializeAndroidGlobals(Context context, boolean videoHwAcceleration) {
        ContextUtils.initialize(context);
        nativeInitializeAndroidGlobals(context, videoHwAcceleration);
    }

    @Deprecated
    public static boolean initializeAndroidGlobals(Object context, boolean initializeAudio, boolean initializeVideo, boolean videoHwAcceleration) {
        initializeAndroidGlobals((Context) context, videoHwAcceleration);
        return true;
    }

    public static String fieldTrialsFindFullName(String name) {
        return nativeLibLoaded ? nativeFieldTrialsFindFullName(name) : "";
    }

    @Deprecated
    public PeerConnectionFactory() {
        this((Options) null);
    }

    public PeerConnectionFactory(Options options) {
        this(options, (VideoEncoderFactory) null, (VideoDecoderFactory) null);
    }

    public PeerConnectionFactory(Options options, VideoEncoderFactory encoderFactory, VideoDecoderFactory decoderFactory) {
        long nativeCreatePeerConnectionFactory = nativeCreatePeerConnectionFactory(options, encoderFactory, decoderFactory);
        this.nativeFactory = nativeCreatePeerConnectionFactory;
        if (nativeCreatePeerConnectionFactory == 0) {
            throw new RuntimeException("Failed to initialize PeerConnectionFactory!");
        }
    }

    public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, MediaConstraints constraints, PeerConnection.Observer observer) {
        long nativeObserver = nativeCreateObserver(observer);
        if (nativeObserver == 0) {
            return null;
        }
        long nativePeerConnection = nativeCreatePeerConnection(this.nativeFactory, rtcConfig, constraints, nativeObserver);
        if (nativePeerConnection == 0) {
            return null;
        }
        return new PeerConnection(nativePeerConnection, nativeObserver);
    }

    public PeerConnection createPeerConnection(List<PeerConnection.IceServer> iceServers, MediaConstraints constraints, PeerConnection.Observer observer) {
        return createPeerConnection(new PeerConnection.RTCConfiguration(iceServers), constraints, observer);
    }

    public MediaStream createLocalMediaStream(String label) {
        return new MediaStream(nativeCreateLocalMediaStream(this.nativeFactory, label));
    }

    public VideoSource createVideoSource(VideoCapturer capturer) {
        EglBase.Context eglContext;
        EglBase eglBase = this.localEglbase;
        if (eglBase == null) {
            eglContext = null;
        } else {
            eglContext = eglBase.getEglBaseContext();
        }
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create(VIDEO_CAPTURER_THREAD_NAME, eglContext);
        long nativeAndroidVideoTrackSource = nativeCreateVideoSource(this.nativeFactory, surfaceTextureHelper, capturer.isScreencast());
        capturer.initialize(surfaceTextureHelper, ContextUtils.getApplicationContext(), new AndroidVideoTrackSourceObserver(nativeAndroidVideoTrackSource));
        return new VideoSource(nativeAndroidVideoTrackSource);
    }

    public VideoTrack createVideoTrack(String id, VideoSource source) {
        return new VideoTrack(nativeCreateVideoTrack(this.nativeFactory, id, source.nativeSource));
    }

    public AudioSource createAudioSource(MediaConstraints constraints) {
        return new AudioSource(nativeCreateAudioSource(this.nativeFactory, constraints));
    }

    public AudioTrack createAudioTrack(String id, AudioSource source) {
        return new AudioTrack(nativeCreateAudioTrack(this.nativeFactory, id, source.nativeSource));
    }

    public boolean startAecDump(int file_descriptor, int filesize_limit_bytes) {
        return nativeStartAecDump(this.nativeFactory, file_descriptor, filesize_limit_bytes);
    }

    public void stopAecDump() {
        nativeStopAecDump(this.nativeFactory);
    }

    @Deprecated
    public void setOptions(Options options) {
        nativeSetOptions(this.nativeFactory, options);
    }

    public void setVideoHwAccelerationOptions(EglBase.Context localEglContext, EglBase.Context remoteEglContext) {
        if (this.localEglbase != null) {
            Logging.w(TAG, "Egl context already set.");
            this.localEglbase.release();
        }
        if (this.remoteEglbase != null) {
            Logging.w(TAG, "Egl context already set.");
            this.remoteEglbase.release();
        }
        this.localEglbase = EglBase.create(localEglContext);
        this.remoteEglbase = EglBase.create(remoteEglContext);
        nativeSetVideoHwAccelerationOptions(this.nativeFactory, this.localEglbase.getEglBaseContext(), this.remoteEglbase.getEglBaseContext());
    }

    public void dispose() {
        nativeFreeFactory(this.nativeFactory);
        networkThread = null;
        workerThread = null;
        signalingThread = null;
        EglBase eglBase = this.localEglbase;
        if (eglBase != null) {
            eglBase.release();
        }
        EglBase eglBase2 = this.remoteEglbase;
        if (eglBase2 != null) {
            eglBase2.release();
        }
    }

    public void threadsCallbacks() {
        nativeThreadsCallbacks(this.nativeFactory);
    }

    private static void printStackTrace(Thread thread, String threadName) {
        if (thread != null) {
            StackTraceElement[] stackTraces = thread.getStackTrace();
            if (stackTraces.length > 0) {
                Logging.d(TAG, threadName + " stacks trace:");
                for (StackTraceElement stackTrace : stackTraces) {
                    Logging.d(TAG, stackTrace.toString());
                }
            }
        }
    }

    public static void printStackTraces() {
        printStackTrace(networkThread, "Network thread");
        printStackTrace(workerThread, "Worker thread");
        printStackTrace(signalingThread, "Signaling thread");
    }

    private static void onNetworkThreadReady() {
        networkThread = Thread.currentThread();
        Logging.d(TAG, "onNetworkThreadReady");
    }

    private static void onWorkerThreadReady() {
        workerThread = Thread.currentThread();
        Logging.d(TAG, "onWorkerThreadReady");
    }

    private static void onSignalingThreadReady() {
        signalingThread = Thread.currentThread();
        Logging.d(TAG, "onSignalingThreadReady");
    }
}

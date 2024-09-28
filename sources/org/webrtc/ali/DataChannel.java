package org.webrtc.ali;

import java.nio.ByteBuffer;

public class DataChannel {
    private final long nativeDataChannel;
    private long nativeObserver;

    public interface Observer {
        void onBufferedAmountChange(long j);

        void onMessage(Buffer buffer);

        void onStateChange();
    }

    public enum State {
        CONNECTING,
        OPEN,
        CLOSING,
        CLOSED
    }

    private native long registerObserverNative(Observer observer);

    private native boolean sendNative(byte[] bArr, boolean z);

    private native void unregisterObserverNative(long j);

    public native long bufferedAmount();

    public native void close();

    public native void dispose();

    public native int id();

    public native String label();

    public native State state();

    public static class Init {
        public int id = -1;
        public int maxRetransmitTimeMs = -1;
        public int maxRetransmits = -1;
        public boolean negotiated = false;
        public boolean ordered = true;
        public String protocol = "";

        public Init() {
        }

        private Init(boolean ordered2, int maxRetransmitTimeMs2, int maxRetransmits2, String protocol2, boolean negotiated2, int id2) {
            this.ordered = ordered2;
            this.maxRetransmitTimeMs = maxRetransmitTimeMs2;
            this.maxRetransmits = maxRetransmits2;
            this.protocol = protocol2;
            this.negotiated = negotiated2;
            this.id = id2;
        }
    }

    public static class Buffer {
        public final boolean binary;
        public final ByteBuffer data;

        public Buffer(ByteBuffer data2, boolean binary2) {
            this.data = data2;
            this.binary = binary2;
        }
    }

    public DataChannel(long nativeDataChannel2) {
        this.nativeDataChannel = nativeDataChannel2;
    }

    public void registerObserver(Observer observer) {
        long j = this.nativeObserver;
        if (j != 0) {
            unregisterObserverNative(j);
        }
        this.nativeObserver = registerObserverNative(observer);
    }

    public void unregisterObserver() {
        unregisterObserverNative(this.nativeObserver);
    }

    public boolean send(Buffer buffer) {
        byte[] data = new byte[buffer.data.remaining()];
        buffer.data.get(data);
        return sendNative(data, buffer.binary);
    }
}

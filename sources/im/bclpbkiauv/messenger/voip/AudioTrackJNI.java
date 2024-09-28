package im.bclpbkiauv.messenger.voip;

import android.media.AudioTrack;
import java.nio.ByteBuffer;

public class AudioTrackJNI {
    /* access modifiers changed from: private */
    public AudioTrack audioTrack;
    /* access modifiers changed from: private */
    public byte[] buffer = new byte[1920];
    private int bufferSize;
    private long nativeInst;
    /* access modifiers changed from: private */
    public boolean needResampling;
    /* access modifiers changed from: private */
    public boolean running;
    private Thread thread;

    /* access modifiers changed from: private */
    public native void nativeCallback(byte[] bArr);

    public AudioTrackJNI(long nativeInst2) {
        this.nativeInst = nativeInst2;
    }

    private int getBufferSize(int min, int sampleRate) {
        return Math.max(AudioTrack.getMinBufferSize(sampleRate, 4, 2), min);
    }

    public void init(int sampleRate, int bitsPerSample, int channels, int bufferSize2) {
        int i = channels;
        int i2 = bufferSize2;
        if (this.audioTrack == null) {
            int size = getBufferSize(i2, 48000);
            this.bufferSize = i2;
            AudioTrack audioTrack2 = new AudioTrack(0, 48000, i == 1 ? 4 : 12, 2, size, 1);
            this.audioTrack = audioTrack2;
            if (audioTrack2.getState() != 1) {
                VLog.w("Error initializing AudioTrack with 48k, trying 44.1k with resampling");
                try {
                    this.audioTrack.release();
                } catch (Throwable th) {
                }
                int size2 = getBufferSize(i2 * 6, 44100);
                VLog.d("buffer size: " + size2);
                this.audioTrack = new AudioTrack(0, 44100, i == 1 ? 4 : 12, 2, size2, 1);
                this.needResampling = true;
                return;
            }
            return;
        }
        throw new IllegalStateException("already inited");
    }

    public void stop() {
        AudioTrack audioTrack2 = this.audioTrack;
        if (audioTrack2 != null) {
            try {
                audioTrack2.stop();
            } catch (Exception e) {
            }
        }
    }

    public void release() {
        this.running = false;
        Thread thread2 = this.thread;
        if (thread2 != null) {
            try {
                thread2.join();
            } catch (InterruptedException e) {
                VLog.e((Throwable) e);
            }
            this.thread = null;
        }
        AudioTrack audioTrack2 = this.audioTrack;
        if (audioTrack2 != null) {
            audioTrack2.release();
            this.audioTrack = null;
        }
    }

    public void start() {
        if (this.thread == null) {
            startThread();
        } else {
            this.audioTrack.play();
        }
    }

    private void startThread() {
        if (this.thread == null) {
            this.running = true;
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        AudioTrackJNI.this.audioTrack.play();
                        ByteBuffer tmp44 = null;
                        ByteBuffer tmp48 = AudioTrackJNI.this.needResampling ? ByteBuffer.allocateDirect(1920) : null;
                        if (AudioTrackJNI.this.needResampling) {
                            tmp44 = ByteBuffer.allocateDirect(1764);
                        }
                        while (true) {
                            if (!AudioTrackJNI.this.running) {
                                break;
                            }
                            try {
                                if (AudioTrackJNI.this.needResampling) {
                                    AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
                                    tmp48.rewind();
                                    tmp48.put(AudioTrackJNI.this.buffer);
                                    Resampler.convert48to44(tmp48, tmp44);
                                    tmp44.rewind();
                                    tmp44.get(AudioTrackJNI.this.buffer, 0, 1764);
                                    AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1764);
                                } else {
                                    AudioTrackJNI.this.nativeCallback(AudioTrackJNI.this.buffer);
                                    AudioTrackJNI.this.audioTrack.write(AudioTrackJNI.this.buffer, 0, 1920);
                                }
                                if (!AudioTrackJNI.this.running) {
                                    AudioTrackJNI.this.audioTrack.stop();
                                    break;
                                }
                            } catch (Exception e) {
                                VLog.e((Throwable) e);
                            }
                        }
                        VLog.i("audiotrack thread exits");
                    } catch (Exception x) {
                        VLog.e("error starting AudioTrack", x);
                    }
                }
            });
            this.thread = thread2;
            thread2.start();
            return;
        }
        throw new IllegalStateException("thread already started");
    }
}

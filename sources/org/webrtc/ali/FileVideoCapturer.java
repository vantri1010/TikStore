package org.webrtc.ali;

import android.content.Context;
import android.os.SystemClock;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.webrtc.ali.VideoCapturer;

public class FileVideoCapturer implements VideoCapturer {
    private static final String TAG = "FileVideoCapturer";
    private VideoCapturer.CapturerObserver capturerObserver;
    private final TimerTask tickTask = new TimerTask() {
        public void run() {
            FileVideoCapturer.this.tick();
        }
    };
    private final Timer timer = new Timer();
    private final VideoReader videoReader;

    private interface VideoReader {
        void close();

        int getFrameHeight();

        int getFrameWidth();

        byte[] getNextFrame();
    }

    public static native void nativeI420ToNV21(byte[] bArr, int i, int i2, byte[] bArr2);

    private static class VideoReaderY4M implements VideoReader {
        private static final String TAG = "VideoReaderY4M";
        private static final String Y4M_FRAME_DELIMETER = "FRAME";
        private final int frameHeight;
        private final int frameSize;
        private final int frameWidth;
        private final RandomAccessFile mediaFileStream;
        private final long videoStart;

        public int getFrameWidth() {
            return this.frameWidth;
        }

        public int getFrameHeight() {
            return this.frameHeight;
        }

        public VideoReaderY4M(String file) throws IOException {
            this.mediaFileStream = new RandomAccessFile(file, "r");
            StringBuilder builder = new StringBuilder();
            while (true) {
                int c = this.mediaFileStream.read();
                if (c == -1) {
                    throw new RuntimeException("Found end of file before end of header for file: " + file);
                } else if (c == 10) {
                    this.videoStart = this.mediaFileStream.getFilePointer();
                    int w = 0;
                    int h = 0;
                    String colorSpace = "";
                    for (String tok : builder.toString().split("[ ]")) {
                        char c2 = tok.charAt(0);
                        if (c2 == 'C') {
                            colorSpace = tok.substring(1);
                        } else if (c2 == 'H') {
                            h = Integer.parseInt(tok.substring(1));
                        } else if (c2 == 'W') {
                            w = Integer.parseInt(tok.substring(1));
                        }
                    }
                    Logging.d(TAG, "Color space: " + colorSpace);
                    if (!colorSpace.equals("420") && !colorSpace.equals("420mpeg2")) {
                        throw new IllegalArgumentException("Does not support any other color space than I420 or I420mpeg2");
                    } else if (w % 2 == 1 || h % 2 == 1) {
                        throw new IllegalArgumentException("Does not support odd width or height");
                    } else {
                        this.frameWidth = w;
                        this.frameHeight = h;
                        this.frameSize = ((w * h) * 3) / 2;
                        Logging.d(TAG, "frame dim: (" + w + ", " + h + ") frameSize: " + this.frameSize);
                        return;
                    }
                } else {
                    builder.append((char) c);
                }
            }
        }

        public byte[] getNextFrame() {
            byte[] frame = new byte[this.frameSize];
            try {
                byte[] frameDelim = new byte[(Y4M_FRAME_DELIMETER.length() + 1)];
                if (this.mediaFileStream.read(frameDelim) < frameDelim.length) {
                    this.mediaFileStream.seek(this.videoStart);
                    if (this.mediaFileStream.read(frameDelim) < frameDelim.length) {
                        throw new RuntimeException("Error looping video");
                    }
                }
                String frameDelimStr = new String(frameDelim);
                if (frameDelimStr.equals("FRAME\n")) {
                    this.mediaFileStream.readFully(frame);
                    byte[] nv21Frame = new byte[this.frameSize];
                    FileVideoCapturer.nativeI420ToNV21(frame, this.frameWidth, this.frameHeight, nv21Frame);
                    return nv21Frame;
                }
                throw new RuntimeException("Frames should be delimited by FRAME plus newline, found delimter was: '" + frameDelimStr + "'");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            try {
                this.mediaFileStream.close();
            } catch (IOException e) {
                Logging.e(TAG, "Problem closing file", e);
            }
        }
    }

    private int getFrameWidth() {
        return this.videoReader.getFrameWidth();
    }

    private int getFrameHeight() {
        return this.videoReader.getFrameHeight();
    }

    public FileVideoCapturer(String inputFile) throws IOException {
        try {
            this.videoReader = new VideoReaderY4M(inputFile);
        } catch (IOException e) {
            Logging.d(TAG, "Could not open video file: " + inputFile);
            throw e;
        }
    }

    private byte[] getNextFrame() {
        return this.videoReader.getNextFrame();
    }

    public void tick() {
        long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
        this.capturerObserver.onByteBufferFrameCaptured(getNextFrame(), getFrameWidth(), getFrameHeight(), 0, captureTimeNs);
    }

    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, VideoCapturer.CapturerObserver capturerObserver2) {
        this.capturerObserver = capturerObserver2;
    }

    public void startCapture(int width, int height, int framerate) {
        this.timer.schedule(this.tickTask, 0, (long) (1000 / framerate));
    }

    public void stopCapture() throws InterruptedException {
        this.timer.cancel();
    }

    public void changeCaptureFormat(int width, int height, int framerate) {
    }

    public void dispose() {
        this.videoReader.close();
    }

    public boolean isScreencast() {
        return false;
    }
}

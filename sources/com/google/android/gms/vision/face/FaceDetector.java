package com.google.android.gms.vision.face;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.internal.vision.zzm;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.internal.client.zza;
import com.google.android.gms.vision.zzc;
import java.nio.ByteBuffer;
import java.util.HashSet;

public final class FaceDetector extends Detector<Face> {
    public static final int ACCURATE_MODE = 1;
    public static final int ALL_CLASSIFICATIONS = 1;
    public static final int ALL_LANDMARKS = 1;
    public static final int FAST_MODE = 0;
    public static final int NO_CLASSIFICATIONS = 0;
    public static final int NO_LANDMARKS = 0;
    private final Object lock;
    private final zzc zzbv;
    private final zza zzbw;
    private boolean zzbx;

    public static class Builder {
        private int mode = 0;
        private int zzby = 0;
        private boolean zzbz = false;
        private int zzca = 0;
        private boolean zzcb = true;
        private float zzcc = -1.0f;
        private final Context zze;

        public Builder(Context context) {
            this.zze = context;
        }

        public FaceDetector build() {
            com.google.android.gms.vision.face.internal.client.zzc zzc = new com.google.android.gms.vision.face.internal.client.zzc();
            zzc.mode = this.mode;
            zzc.zzby = this.zzby;
            zzc.zzca = this.zzca;
            zzc.zzbz = this.zzbz;
            zzc.zzcb = this.zzcb;
            zzc.zzcc = this.zzcc;
            return new FaceDetector(new zza(this.zze, zzc));
        }

        public Builder setClassificationType(int i) {
            if (i == 0 || i == 1) {
                this.zzca = i;
                return this;
            }
            StringBuilder sb = new StringBuilder(40);
            sb.append("Invalid classification type: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }

        public Builder setLandmarkType(int i) {
            if (i == 0 || i == 1) {
                this.zzby = i;
                return this;
            }
            StringBuilder sb = new StringBuilder(34);
            sb.append("Invalid landmark type: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }

        public Builder setMinFaceSize(float f) {
            if (f < 0.0f || f > 1.0f) {
                StringBuilder sb = new StringBuilder(47);
                sb.append("Invalid proportional face size: ");
                sb.append(f);
                throw new IllegalArgumentException(sb.toString());
            }
            this.zzcc = f;
            return this;
        }

        public Builder setMode(int i) {
            if (i == 0 || i == 1) {
                this.mode = i;
                return this;
            }
            StringBuilder sb = new StringBuilder(25);
            sb.append("Invalid mode: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }

        public Builder setProminentFaceOnly(boolean z) {
            this.zzbz = z;
            return this;
        }

        public Builder setTrackingEnabled(boolean z) {
            this.zzcb = z;
            return this;
        }
    }

    private FaceDetector() {
        this.zzbv = new zzc();
        this.lock = new Object();
        this.zzbx = true;
        throw new IllegalStateException("Default constructor called");
    }

    private FaceDetector(zza zza) {
        this.zzbv = new zzc();
        this.lock = new Object();
        this.zzbx = true;
        this.zzbw = zza;
    }

    public final SparseArray<Face> detect(Frame frame) {
        Face[] zzb;
        if (frame != null) {
            ByteBuffer grayscaleImageData = frame.getGrayscaleImageData();
            synchronized (this.lock) {
                if (this.zzbx) {
                    zzb = this.zzbw.zzb(grayscaleImageData, zzm.zzc(frame));
                } else {
                    throw new RuntimeException("Cannot use detector after release()");
                }
            }
            HashSet hashSet = new HashSet();
            SparseArray<Face> sparseArray = new SparseArray<>(zzb.length);
            int i = 0;
            for (Face face : zzb) {
                int id = face.getId();
                i = Math.max(i, id);
                if (hashSet.contains(Integer.valueOf(id))) {
                    id = i + 1;
                    i = id;
                }
                hashSet.add(Integer.valueOf(id));
                sparseArray.append(this.zzbv.zzb(id), face);
            }
            return sparseArray;
        }
        throw new IllegalArgumentException("No frame supplied.");
    }

    /* access modifiers changed from: protected */
    public final void finalize() throws Throwable {
        try {
            synchronized (this.lock) {
                if (this.zzbx) {
                    Log.w("FaceDetector", "FaceDetector was not released with FaceDetector.release()");
                    release();
                }
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public final boolean isOperational() {
        return this.zzbw.isOperational();
    }

    public final void release() {
        super.release();
        synchronized (this.lock) {
            if (this.zzbx) {
                this.zzbw.zzo();
                this.zzbx = false;
            }
        }
    }

    public final boolean setFocus(int i) {
        boolean zzd;
        int zzc = this.zzbv.zzc(i);
        synchronized (this.lock) {
            if (this.zzbx) {
                zzd = this.zzbw.zzd(zzc);
            } else {
                throw new RuntimeException("Cannot use detector after release()");
            }
        }
        return zzd;
    }
}

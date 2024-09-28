package com.serenegiant.usb;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;

public class Size implements Parcelable {
    public static final Parcelable.Creator<Size> CREATOR = new Parcelable.Creator<Size>() {
        public Size createFromParcel(Parcel source) {
            return new Size(source);
        }

        public Size[] newArray(int size) {
            return new Size[size];
        }
    };
    public float[] fps;
    public int frameIntervalIndex;
    public int frameIntervalType;
    private String frameRates;
    public int frame_type;
    public int height;
    public int index;
    public int[] intervals;
    public int type;
    public int width;

    public Size(int _type, int _frame_type, int _index, int _width, int _height) {
        this.type = _type;
        this.frame_type = _frame_type;
        this.index = _index;
        this.width = _width;
        this.height = _height;
        this.frameIntervalType = -1;
        this.frameIntervalIndex = 0;
        this.intervals = null;
        updateFrameRate();
    }

    public Size(int _type, int _frame_type, int _index, int _width, int _height, int _min_intervals, int _max_intervals, int _step) {
        this.type = _type;
        this.frame_type = _frame_type;
        this.index = _index;
        this.width = _width;
        this.height = _height;
        this.frameIntervalType = 0;
        this.frameIntervalIndex = 0;
        int[] iArr = new int[3];
        this.intervals = iArr;
        iArr[0] = _min_intervals;
        iArr[1] = _max_intervals;
        iArr[2] = _step;
        updateFrameRate();
    }

    public Size(int _type, int _frame_type, int _index, int _width, int _height, int[] _intervals) {
        this.type = _type;
        this.frame_type = _frame_type;
        this.index = _index;
        this.width = _width;
        this.height = _height;
        int n = _intervals != null ? _intervals.length : -1;
        if (n > 0) {
            this.frameIntervalType = n;
            int[] iArr = new int[n];
            this.intervals = iArr;
            System.arraycopy(_intervals, 0, iArr, 0, n);
        } else {
            this.frameIntervalType = -1;
            this.intervals = null;
        }
        this.frameIntervalIndex = 0;
        updateFrameRate();
    }

    public Size(Size other) {
        this.type = other.type;
        this.frame_type = other.frame_type;
        this.index = other.index;
        this.width = other.width;
        this.height = other.height;
        this.frameIntervalType = other.frameIntervalType;
        this.frameIntervalIndex = other.frameIntervalIndex;
        int[] iArr = other.intervals;
        int n = iArr != null ? iArr.length : -1;
        if (n > 0) {
            int[] iArr2 = new int[n];
            this.intervals = iArr2;
            System.arraycopy(other.intervals, 0, iArr2, 0, n);
        } else {
            this.intervals = null;
        }
        updateFrameRate();
    }

    private Size(Parcel source) {
        this.type = source.readInt();
        this.frame_type = source.readInt();
        this.index = source.readInt();
        this.width = source.readInt();
        this.height = source.readInt();
        this.frameIntervalType = source.readInt();
        this.frameIntervalIndex = source.readInt();
        int i = this.frameIntervalType;
        if (i >= 0) {
            if (i > 0) {
                this.intervals = new int[i];
            } else {
                this.intervals = new int[3];
            }
            source.readIntArray(this.intervals);
        } else {
            this.intervals = null;
        }
        updateFrameRate();
    }

    public Size set(Size other) {
        if (other != null) {
            this.type = other.type;
            this.frame_type = other.frame_type;
            this.index = other.index;
            this.width = other.width;
            this.height = other.height;
            this.frameIntervalType = other.frameIntervalType;
            this.frameIntervalIndex = other.frameIntervalIndex;
            int[] iArr = other.intervals;
            int n = iArr != null ? iArr.length : -1;
            if (n > 0) {
                int[] iArr2 = new int[n];
                this.intervals = iArr2;
                System.arraycopy(other.intervals, 0, iArr2, 0, n);
            } else {
                this.intervals = null;
            }
            updateFrameRate();
        }
        return this;
    }

    public float getCurrentFrameRate() throws IllegalStateException {
        float[] fArr = this.fps;
        int n = fArr != null ? fArr.length : 0;
        int i = this.frameIntervalIndex;
        if (i >= 0 && i < n) {
            return this.fps[i];
        }
        throw new IllegalStateException("unknown frame rate or not ready");
    }

    public void setCurrentFrameRate(float frameRate) {
        int index2 = -1;
        float[] fArr = this.fps;
        int n = fArr != null ? fArr.length : 0;
        int i = 0;
        while (true) {
            if (i >= n) {
                break;
            } else if (this.fps[i] <= frameRate) {
                index2 = i;
                break;
            } else {
                i++;
            }
        }
        this.frameIntervalIndex = index2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.frame_type);
        dest.writeInt(this.index);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.frameIntervalType);
        dest.writeInt(this.frameIntervalIndex);
        int[] iArr = this.intervals;
        if (iArr != null) {
            dest.writeIntArray(iArr);
        }
    }

    public void updateFrameRate() {
        int n = this.frameIntervalType;
        if (n > 0) {
            this.fps = new float[n];
            for (int i = 0; i < n; i++) {
                this.fps[i] = 1.0E7f / ((float) this.intervals[i]);
            }
        } else if (n == 0) {
            try {
                int min = Math.min(this.intervals[0], this.intervals[1]);
                int max = Math.max(this.intervals[0], this.intervals[1]);
                int step = this.intervals[2];
                if (step > 0) {
                    int m = 0;
                    for (int i2 = min; i2 <= max; i2 += step) {
                        m++;
                    }
                    this.fps = new float[m];
                    int m2 = 0;
                    int i3 = min;
                    while (i3 <= max) {
                        this.fps[m2] = 1.0E7f / ((float) i3);
                        i3 += step;
                        m2++;
                    }
                } else {
                    float max_fps = 1.0E7f / ((float) min);
                    int m3 = 0;
                    for (float fps2 = 1.0E7f / ((float) min); fps2 <= max_fps; fps2 += 1.0f) {
                        m3++;
                    }
                    this.fps = new float[m3];
                    int m4 = 0;
                    float fps3 = 1.0E7f / ((float) min);
                    while (fps3 <= max_fps) {
                        int m5 = m4 + 1;
                        this.fps[m4] = fps3;
                        fps3 += 1.0f;
                        m4 = m5;
                    }
                }
            } catch (Exception e) {
                this.fps = null;
            }
        }
        float[] fArr = this.fps;
        int m6 = fArr != null ? fArr.length : 0;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i4 = 0; i4 < m6; i4++) {
            sb.append(String.format(Locale.US, "%4.1f", new Object[]{Float.valueOf(this.fps[i4])}));
            if (i4 < m6 - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        this.frameRates = sb.toString();
        if (this.frameIntervalIndex > m6) {
            this.frameIntervalIndex = 0;
        }
    }

    public String toString() {
        float frame_rate = 0.0f;
        try {
            frame_rate = getCurrentFrameRate();
        } catch (Exception e) {
        }
        return String.format(Locale.US, "Size(%dx%d@%4.1f,type:%d,frame:%d,index:%d,%s)", new Object[]{Integer.valueOf(this.width), Integer.valueOf(this.height), Float.valueOf(frame_rate), Integer.valueOf(this.type), Integer.valueOf(this.frame_type), Integer.valueOf(this.index), this.frameRates});
    }
}

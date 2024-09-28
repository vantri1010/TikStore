package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ApicFrame extends Id3Frame {
    public static final Parcelable.Creator<ApicFrame> CREATOR = new Parcelable.Creator<ApicFrame>() {
        public ApicFrame createFromParcel(Parcel in) {
            return new ApicFrame(in);
        }

        public ApicFrame[] newArray(int size) {
            return new ApicFrame[size];
        }
    };
    public static final String ID = "APIC";
    public final String description;
    public final String mimeType;
    public final byte[] pictureData;
    public final int pictureType;

    public ApicFrame(String mimeType2, String description2, int pictureType2, byte[] pictureData2) {
        super(ID);
        this.mimeType = mimeType2;
        this.description = description2;
        this.pictureType = pictureType2;
        this.pictureData = pictureData2;
    }

    ApicFrame(Parcel in) {
        super(ID);
        this.mimeType = (String) Util.castNonNull(in.readString());
        this.description = (String) Util.castNonNull(in.readString());
        this.pictureType = in.readInt();
        this.pictureData = (byte[]) Util.castNonNull(in.createByteArray());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ApicFrame other = (ApicFrame) obj;
        if (this.pictureType != other.pictureType || !Util.areEqual(this.mimeType, other.mimeType) || !Util.areEqual(this.description, other.description) || !Arrays.equals(this.pictureData, other.pictureData)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = ((17 * 31) + this.pictureType) * 31;
        String str = this.mimeType;
        int i = 0;
        int result2 = (result + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.description;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return ((result2 + i) * 31) + Arrays.hashCode(this.pictureData);
    }

    public String toString() {
        return this.id + ": mimeType=" + this.mimeType + ", description=" + this.description;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mimeType);
        dest.writeString(this.description);
        dest.writeInt(this.pictureType);
        dest.writeByteArray(this.pictureData);
    }
}

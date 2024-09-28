package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Util;

public final class TextInformationFrame extends Id3Frame {
    public static final Parcelable.Creator<TextInformationFrame> CREATOR = new Parcelable.Creator<TextInformationFrame>() {
        public TextInformationFrame createFromParcel(Parcel in) {
            return new TextInformationFrame(in);
        }

        public TextInformationFrame[] newArray(int size) {
            return new TextInformationFrame[size];
        }
    };
    public final String description;
    public final String value;

    public TextInformationFrame(String id, String description2, String value2) {
        super(id);
        this.description = description2;
        this.value = value2;
    }

    TextInformationFrame(Parcel in) {
        super((String) Util.castNonNull(in.readString()));
        this.description = in.readString();
        this.value = (String) Util.castNonNull(in.readString());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TextInformationFrame other = (TextInformationFrame) obj;
        if (!this.id.equals(other.id) || !Util.areEqual(this.description, other.description) || !Util.areEqual(this.value, other.value)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = ((17 * 31) + this.id.hashCode()) * 31;
        String str = this.description;
        int i = 0;
        int result2 = (result + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.value;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return result2 + i;
    }

    public String toString() {
        return this.id + ": value=" + this.value;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.value);
    }
}

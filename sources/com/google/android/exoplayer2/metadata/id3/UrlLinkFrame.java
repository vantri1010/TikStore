package com.google.android.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.util.Util;

public final class UrlLinkFrame extends Id3Frame {
    public static final Parcelable.Creator<UrlLinkFrame> CREATOR = new Parcelable.Creator<UrlLinkFrame>() {
        public UrlLinkFrame createFromParcel(Parcel in) {
            return new UrlLinkFrame(in);
        }

        public UrlLinkFrame[] newArray(int size) {
            return new UrlLinkFrame[size];
        }
    };
    public final String description;
    public final String url;

    public UrlLinkFrame(String id, String description2, String url2) {
        super(id);
        this.description = description2;
        this.url = url2;
    }

    UrlLinkFrame(Parcel in) {
        super((String) Util.castNonNull(in.readString()));
        this.description = in.readString();
        this.url = (String) Util.castNonNull(in.readString());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UrlLinkFrame other = (UrlLinkFrame) obj;
        if (!this.id.equals(other.id) || !Util.areEqual(this.description, other.description) || !Util.areEqual(this.url, other.url)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = ((17 * 31) + this.id.hashCode()) * 31;
        String str = this.description;
        int i = 0;
        int result2 = (result + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.url;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return result2 + i;
    }

    public String toString() {
        return this.id + ": url=" + this.url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.url);
    }
}

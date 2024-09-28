package com.google.android.exoplayer2.metadata.icy;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Util;

public final class IcyInfo implements Metadata.Entry {
    public static final Parcelable.Creator<IcyInfo> CREATOR = new Parcelable.Creator<IcyInfo>() {
        public IcyInfo createFromParcel(Parcel in) {
            return new IcyInfo(in);
        }

        public IcyInfo[] newArray(int size) {
            return new IcyInfo[size];
        }
    };
    public final String title;
    public final String url;

    public IcyInfo(String title2, String url2) {
        this.title = title2;
        this.url = url2;
    }

    IcyInfo(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IcyInfo other = (IcyInfo) obj;
        if (!Util.areEqual(this.title, other.title) || !Util.areEqual(this.url, other.url)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = 17 * 31;
        String str = this.title;
        int i2 = 0;
        int result = (i + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.url;
        if (str2 != null) {
            i2 = str2.hashCode();
        }
        return result + i2;
    }

    public String toString() {
        return "ICY: title=\"" + this.title + "\", url=\"" + this.url + "\"";
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    public int describeContents() {
        return 0;
    }
}

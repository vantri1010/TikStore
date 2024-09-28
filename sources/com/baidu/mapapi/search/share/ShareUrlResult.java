package com.baidu.mapapi.search.share;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.core.SearchResult;

public class ShareUrlResult extends SearchResult implements Parcelable {
    public static final Parcelable.Creator<ShareUrlResult> CREATOR = new a();
    private String a;
    private int b;

    public ShareUrlResult() {
    }

    protected ShareUrlResult(Parcel parcel) {
        this.a = parcel.readString();
        this.b = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public String getUrl() {
        return this.a;
    }

    public void setType(int i) {
        this.b = i;
    }

    public void setUrl(String str) {
        this.a = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.a);
        parcel.writeInt(this.b);
    }
}

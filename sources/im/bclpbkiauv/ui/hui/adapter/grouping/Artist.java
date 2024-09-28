package im.bclpbkiauv.ui.hui.adapter.grouping;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {
    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
    private int userId;

    public Artist(int userId2) {
        this.userId = userId2;
    }

    protected Artist(Parcel in) {
        this.userId = in.readInt();
    }

    public int getUserId() {
        return this.userId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof Artist) && this.userId == ((Artist) o).getUserId()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.userId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
    }

    public int describeContents() {
        return 0;
    }
}

package im.bclpbkiauv.ui.expand.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class ExpandableGroup<T extends Parcelable> implements Parcelable {
    public static final Parcelable.Creator<ExpandableGroup> CREATOR = new Parcelable.Creator<ExpandableGroup>() {
        public ExpandableGroup createFromParcel(Parcel in) {
            return new ExpandableGroup(in);
        }

        public ExpandableGroup[] newArray(int size) {
            return new ExpandableGroup[size];
        }
    };
    private List<T> items;
    private String title;

    public ExpandableGroup(String title2, List<T> items2) {
        this.title = title2;
        this.items = items2;
    }

    public String getTitle() {
        return this.title;
    }

    public List<T> getItems() {
        return this.items;
    }

    public int getItemCount() {
        List<T> list = this.items;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String toString() {
        return "ExpandableGroup{title='" + this.title + '\'' + ", items=" + this.items + '}';
    }

    protected ExpandableGroup(Parcel in) {
        this.title = in.readString();
        byte hasItems = in.readByte();
        int size = in.readInt();
        if (hasItems == 1) {
            this.items = new ArrayList(size);
            in.readList(this.items, ((Class) in.readSerializable()).getClassLoader());
            return;
        }
        this.items = null;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        if (this.items == null) {
            dest.writeByte((byte) 0);
            dest.writeInt(0);
            return;
        }
        dest.writeByte((byte) 1);
        dest.writeInt(this.items.size());
        dest.writeSerializable(((Parcelable) this.items.get(0)).getClass());
        dest.writeList(this.items);
    }
}

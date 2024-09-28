package im.bclpbkiauv.javaBean.fc;

import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.search.core.PoiInfo;
import com.bjz.comm.net.bean.FcEntitysBean;
import com.bjz.comm.net.bean.RespTopicBean;
import im.bclpbkiauv.messenger.MediaController;
import java.util.ArrayList;
import java.util.HashMap;

public class PublishFcBean implements Parcelable {
    public static final Parcelable.Creator<PublishFcBean> CREATOR = new Parcelable.Creator<PublishFcBean>() {
        public PublishFcBean createFromParcel(Parcel in) {
            return new PublishFcBean(in);
        }

        public PublishFcBean[] newArray(int size) {
            return new PublishFcBean[size];
        }
    };
    private String Content;
    private int ContentType;
    private ArrayList<FcEntitysBean> Entitys = new ArrayList<>();
    private int Permission;
    private HashMap<String, RespTopicBean.Item> Topic;
    private int currentSelectMediaType;
    private long id;
    private PoiInfo locationInfo;
    private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos;
    private ArrayList<Integer> selectedPhotosOrder;

    public PublishFcBean() {
    }

    protected PublishFcBean(Parcel in) {
        this.id = in.readLong();
        this.ContentType = in.readInt();
        this.Content = in.readString();
        this.Permission = in.readInt();
        this.locationInfo = (PoiInfo) in.readParcelable(PoiInfo.class.getClassLoader());
        this.currentSelectMediaType = in.readInt();
    }

    public int getContentType() {
        return this.ContentType;
    }

    public void setContentType(int contentType) {
        this.ContentType = contentType;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }

    public int getPermission() {
        return this.Permission;
    }

    public void setPermission(int permission) {
        this.Permission = permission;
    }

    public PoiInfo getLocationInfo() {
        return this.locationInfo;
    }

    public void setLocationInfo(PoiInfo locationInfo2) {
        this.locationInfo = locationInfo2;
    }

    public HashMap<String, RespTopicBean.Item> getTopic() {
        return this.Topic;
    }

    public void setTopic(HashMap<String, RespTopicBean.Item> topic) {
        this.Topic = topic;
    }

    public HashMap<Integer, MediaController.PhotoEntry> getSelectedPhotos() {
        return this.selectedPhotos;
    }

    public void setSelectedPhotos(HashMap<Integer, MediaController.PhotoEntry> selectedPhotos2) {
        this.selectedPhotos = selectedPhotos2;
    }

    public ArrayList<Integer> getSelectedPhotosOrder() {
        return this.selectedPhotosOrder;
    }

    public void setSelectedPhotosOrder(ArrayList<Integer> selectedPhotosOrder2) {
        this.selectedPhotosOrder = selectedPhotosOrder2;
    }

    public int getCurrentSelectMediaType() {
        return this.currentSelectMediaType;
    }

    public void setCurrentSelectMediaType(int currentSelectMediaType2) {
        this.currentSelectMediaType = currentSelectMediaType2;
    }

    public ArrayList<FcEntitysBean> getEntitys() {
        return this.Entitys;
    }

    public void setEntitys(ArrayList<FcEntitysBean> entitys) {
        this.Entitys = entitys;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.ContentType);
        dest.writeString(this.Content);
        dest.writeInt(this.Permission);
        dest.writeParcelable(this.locationInfo, flags);
        dest.writeInt(this.currentSelectMediaType);
    }
}

package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.List;

public class AvatarPhotoBean implements Parcelable, Serializable {
    public static final Parcelable.Creator<AvatarPhotoBean> CREATOR = new Parcelable.Creator<AvatarPhotoBean>() {
        public AvatarPhotoBean createFromParcel(Parcel in) {
            return new AvatarPhotoBean(in);
        }

        public AvatarPhotoBean[] newArray(int size) {
            return new AvatarPhotoBean[size];
        }
    };
    private static final long serialVersionUID = 50065690207094374L;
    private long access_hash;
    private BigBean big;
    private int date;
    private long id;
    private int needSize;
    private List<SizesBean> sizes;
    private SmallBean small;

    protected AvatarPhotoBean(Parcel in) {
        this.id = in.readLong();
        this.date = in.readInt();
        this.access_hash = in.readLong();
        this.needSize = in.readInt();
    }

    public long getSmallVolumeId() {
        SmallBean smallBean = this.small;
        if (smallBean != null) {
            return smallBean.getVolume_id();
        }
        return 0;
    }

    public int getSmallLocalId() {
        SmallBean smallBean = this.small;
        if (smallBean != null) {
            return smallBean.getLocal_id();
        }
        return 0;
    }

    public int getSmallDcId() {
        SmallBean smallBean = this.small;
        if (smallBean != null) {
            return smallBean.getDc_id();
        }
        return 0;
    }

    public int getSmallPhotoSize() {
        int i = this.needSize;
        if (i != 0) {
            return i;
        }
        List<SizesBean> list = this.sizes;
        if (list == null || list.size() == 0) {
            return 0;
        }
        for (SizesBean size : this.sizes) {
            if (size.location.getVolume_id() == this.small.getVolume_id()) {
                int size2 = size.getSize();
                this.needSize = size2;
                return size2;
            }
        }
        return 0;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id2) {
        this.id = id2;
    }

    public int getDate() {
        return this.date;
    }

    public void setDate(int date2) {
        this.date = date2;
    }

    public long getAccess_hash() {
        return this.access_hash;
    }

    public void setAccess_hash(long access_hash2) {
        this.access_hash = access_hash2;
    }

    public SmallBean getSmall() {
        return this.small;
    }

    public void setSmall(SmallBean small2) {
        this.small = small2;
    }

    public BigBean getBig() {
        return this.big;
    }

    public void setBig(BigBean big2) {
        this.big = big2;
    }

    public List<SizesBean> getSizes() {
        return this.sizes;
    }

    public void setSizes(List<SizesBean> sizes2) {
        this.sizes = sizes2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.date);
        dest.writeLong(this.access_hash);
        dest.writeInt(this.needSize);
    }

    public static class SmallBean implements Parcelable, Serializable {
        public static final Parcelable.Creator<SmallBean> CREATOR = new Parcelable.Creator<SmallBean>() {
            public SmallBean createFromParcel(Parcel in) {
                return new SmallBean(in);
            }

            public SmallBean[] newArray(int size) {
                return new SmallBean[size];
            }
        };
        private static final long serialVersionUID = 2251692648208752379L;
        private int dc_id;
        private int local_id;
        private int secret;
        private long volume_id;

        protected SmallBean(Parcel in) {
            this.volume_id = in.readLong();
            this.secret = in.readInt();
            this.local_id = in.readInt();
            this.dc_id = in.readInt();
        }

        public long getVolume_id() {
            return this.volume_id;
        }

        public void setVolume_id(long volume_id2) {
            this.volume_id = volume_id2;
        }

        public int getSecret() {
            return this.secret;
        }

        public void setSecret(int secret2) {
            this.secret = secret2;
        }

        public int getLocal_id() {
            return this.local_id;
        }

        public void setLocal_id(int local_id2) {
            this.local_id = local_id2;
        }

        public int getDc_id() {
            return this.dc_id;
        }

        public void setDc_id(int dc_id2) {
            this.dc_id = dc_id2;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.volume_id);
            dest.writeInt(this.secret);
            dest.writeInt(this.local_id);
            dest.writeInt(this.dc_id);
        }
    }

    public static class BigBean implements Parcelable, Serializable {
        public static final Parcelable.Creator<BigBean> CREATOR = new Parcelable.Creator<BigBean>() {
            public BigBean createFromParcel(Parcel in) {
                return new BigBean(in);
            }

            public BigBean[] newArray(int size) {
                return new BigBean[size];
            }
        };
        private static final long serialVersionUID = -316645208024667851L;
        private int dc_id;
        private int local_id;
        private int secret;
        private long volume_id;

        protected BigBean(Parcel in) {
            this.volume_id = in.readLong();
            this.secret = in.readInt();
            this.local_id = in.readInt();
            this.dc_id = in.readInt();
        }

        public long getVolume_id() {
            return this.volume_id;
        }

        public void setVolume_id(long volume_id2) {
            this.volume_id = volume_id2;
        }

        public int getSecret() {
            return this.secret;
        }

        public void setSecret(int secret2) {
            this.secret = secret2;
        }

        public int getLocal_id() {
            return this.local_id;
        }

        public void setLocal_id(int local_id2) {
            this.local_id = local_id2;
        }

        public int getDc_id() {
            return this.dc_id;
        }

        public void setDc_id(int dc_id2) {
            this.dc_id = dc_id2;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.volume_id);
            dest.writeInt(this.secret);
            dest.writeInt(this.local_id);
            dest.writeInt(this.dc_id);
        }
    }

    public static class SizesBean implements Parcelable, Serializable {
        public static final Parcelable.Creator<SizesBean> CREATOR = new Parcelable.Creator<SizesBean>() {
            public SizesBean createFromParcel(Parcel in) {
                return new SizesBean(in);
            }

            public SizesBean[] newArray(int size) {
                return new SizesBean[size];
            }
        };
        private static final long serialVersionUID = 8183310654572381066L;
        private int h;
        /* access modifiers changed from: private */
        public LocationBean location;
        private String photoSize_type;
        private int size;
        private String type;
        private int w;

        protected SizesBean(Parcel in) {
            this.w = in.readInt();
            this.h = in.readInt();
            this.type = in.readString();
            this.size = in.readInt();
            this.photoSize_type = in.readString();
        }

        public int getW() {
            return this.w;
        }

        public void setW(int w2) {
            this.w = w2;
        }

        public int getH() {
            return this.h;
        }

        public void setH(int h2) {
            this.h = h2;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type2) {
            this.type = type2;
        }

        public int getSize() {
            return this.size;
        }

        public void setSize(int size2) {
            this.size = size2;
        }

        public String getPhotoSize_type() {
            return this.photoSize_type;
        }

        public void setPhotoSize_type(String photoSize_type2) {
            this.photoSize_type = photoSize_type2;
        }

        public LocationBean getLocation() {
            return this.location;
        }

        public void setLocation(LocationBean location2) {
            this.location = location2;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.w);
            dest.writeInt(this.h);
            dest.writeString(this.type);
            dest.writeInt(this.size);
            dest.writeString(this.photoSize_type);
        }

        public static class LocationBean implements Parcelable, Serializable {
            public static final Parcelable.Creator<LocationBean> CREATOR = new Parcelable.Creator<LocationBean>() {
                public LocationBean createFromParcel(Parcel in) {
                    return new LocationBean(in);
                }

                public LocationBean[] newArray(int size) {
                    return new LocationBean[size];
                }
            };
            private static final long serialVersionUID = -3354812303345372675L;
            private int dc_id;
            private int local_id;
            private int secret;
            private long volume_id;

            protected LocationBean(Parcel in) {
                this.volume_id = in.readLong();
                this.secret = in.readInt();
                this.local_id = in.readInt();
                this.dc_id = in.readInt();
            }

            public long getVolume_id() {
                return this.volume_id;
            }

            public void setVolume_id(long volume_id2) {
                this.volume_id = volume_id2;
            }

            public int getSecret() {
                return this.secret;
            }

            public void setSecret(int secret2) {
                this.secret = secret2;
            }

            public int getLocal_id() {
                return this.local_id;
            }

            public void setLocal_id(int local_id2) {
                this.local_id = local_id2;
            }

            public int getDc_id() {
                return this.dc_id;
            }

            public void setDc_id(int dc_id2) {
                this.dc_id = dc_id2;
            }

            public int describeContents() {
                return 0;
            }

            public void writeToParcel(Parcel dest, int flags) {
                dest.writeLong(this.volume_id);
                dest.writeInt(this.secret);
                dest.writeInt(this.local_id);
                dest.writeInt(this.dc_id);
            }
        }
    }
}

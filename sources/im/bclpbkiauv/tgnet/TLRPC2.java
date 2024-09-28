package im.bclpbkiauv.tgnet;

import java.util.ArrayList;

public class TLRPC2 {

    public static class TL_CarouselMapEmpty extends TL_CarouselMapAbs {
        public static int constructor = 1227065030;
    }

    public static class TL_GetCarouselMap extends TLObject {
        public static int constructor = -964979949;
        public int version;

        public TL_CarouselMapAbs deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_CarouselMapAbs.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.version);
        }
    }

    public static class TL_AdPicture extends TLObject {
        public static int constructor = -560199523;
        public String inner_url;
        public String outer_url;
        public String url;

        public static TL_AdPicture TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_AdPicture ad = new TL_AdPicture();
                ad.readParams(stream, exception);
                return ad;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("------->can't parse magic %x in TL_AdPicture", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.url = stream.readString(exception);
            this.outer_url = stream.readString(exception);
            this.inner_url = stream.readString(exception);
        }
    }

    public static abstract class TL_CarouselMapAbs extends TLObject {
        public static TL_CarouselMapAbs TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            TL_CarouselMapAbs result = null;
            if (constructor == -1727557460) {
                result = new TL_CarouselMapNotModify();
            } else if (constructor == -851199700) {
                result = new TL_CarouselMap();
            } else if (constructor == 1227065030) {
                result = new TL_CarouselMapEmpty();
            }
            if (result != null || !exception) {
                result.readParams(stream, exception);
                return result;
            }
            throw new RuntimeException(String.format("can't parse magic %x in TL_CarouselMapAbs", new Object[]{Integer.valueOf(constructor)}));
        }
    }

    public static class TL_CarouselMapNotModify extends TL_CarouselMapAbs {
        public static int constructor = -1727557460;
        public int version;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.version = stream.readInt32(exception);
        }
    }

    public static class TL_CarouselMap extends TL_CarouselMapAbs {
        public static int constructor = -851199700;
        public ArrayList<TL_AdPicture> sources;
        public int version;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.version = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_AdPicture object = TL_AdPicture.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        if (this.sources == null) {
                            this.sources = new ArrayList<>();
                        }
                        this.sources.add(object);
                        a++;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }
    }

    public static class TL_GetLoginUrl extends TLObject {
        public static int constructor = -804757904;
        public String app_code;

        public TL_LoginUrlInfo deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_LoginUrlInfo.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.app_code);
        }
    }

    public static class TL_LoginUrlInfo extends TLObject {
        public static int constructor = 1702926018;
        public String url;

        public static TL_LoginUrlInfo TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_LoginUrlInfo ad = new TL_LoginUrlInfo();
                ad.readParams(stream, exception);
                return ad;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("------->can't parse magic %x in TL_LoginUrlInfo", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.url = stream.readString(exception);
        }
    }

    public static class TL_GetDiscoveryPageSetting extends TLObject {
        public static int constructor = -851365782;
        public String tag;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_DiscoveryPageSetting.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.tag);
        }
    }

    public static class TL_DiscoveryPageSetting extends TLObject {
        public static int constructor = 611296802;
        private ArrayList<TL_DiscoveryPageSetting_GM> g;
        private ArrayList<TL_DiscoveryPageSetting_SM> s;

        public static TL_DiscoveryPageSetting TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_DiscoveryPageSetting result = new TL_DiscoveryPageSetting();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_DiscoveryPageSetting", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a++) {
                    TL_DiscoveryPageSetting_GM object = TL_DiscoveryPageSetting_GM.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object == null) {
                        break;
                    }
                    if (this.g == null) {
                        this.g = new ArrayList<>();
                    }
                    this.g.add(object);
                }
                int magic2 = stream.readInt32(exception);
                if (magic2 == 481674261) {
                    int count2 = stream.readInt32(exception);
                    int a2 = 0;
                    while (a2 < count2) {
                        TL_DiscoveryPageSetting_SM object2 = TL_DiscoveryPageSetting_SM.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 != null) {
                            if (this.s == null) {
                                this.s = new ArrayList<>();
                            }
                            this.s.add(object2);
                            a2++;
                        } else {
                            return;
                        }
                    }
                } else if (exception) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic2)}));
                }
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }

        public ArrayList<TL_DiscoveryPageSetting_GM> getG() {
            ArrayList<TL_DiscoveryPageSetting_GM> arrayList = this.g;
            if (arrayList != null) {
                return arrayList;
            }
            ArrayList<TL_DiscoveryPageSetting_GM> arrayList2 = new ArrayList<>();
            this.g = arrayList2;
            return arrayList2;
        }

        public ArrayList<TL_DiscoveryPageSetting_SM> getS() {
            ArrayList<TL_DiscoveryPageSetting_SM> arrayList = this.s;
            if (arrayList != null) {
                return arrayList;
            }
            ArrayList<TL_DiscoveryPageSetting_SM> arrayList2 = new ArrayList<>();
            this.s = arrayList2;
            return arrayList2;
        }
    }

    public static class TL_DiscoveryPageSetting_GM extends TLObject {
        public static int constructor = 1178854756;
        public int no;
        private String pic;
        private String url;

        public static TL_DiscoveryPageSetting_GM TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_DiscoveryPageSetting_GM result = new TL_DiscoveryPageSetting_GM();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_DiscoveryPageSetting_GM", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.no = stream.readInt32(exception);
            this.pic = stream.readString(exception);
            this.url = stream.readString(exception);
        }

        public String getPic() {
            String str = this.pic;
            if (str != null) {
                return str;
            }
            this.pic = "";
            return "";
        }

        public String getUrl() {
            String str = this.url;
            if (str != null) {
                return str;
            }
            this.url = "";
            return "";
        }
    }

    public static class TL_DiscoveryPageSetting_SM extends TLObject {
        public static int constructor = -259528521;
        private String logo;
        public int no;
        private String title;
        private String url;

        public static TL_DiscoveryPageSetting_SM TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_DiscoveryPageSetting_SM result = new TL_DiscoveryPageSetting_SM();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_DiscoveryPageSetting_SM", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.no = stream.readInt32(exception);
            this.title = stream.readString(exception);
            this.logo = stream.readString(exception);
            this.url = stream.readString(exception);
        }

        public String getTitle() {
            String str = this.title;
            if (str != null) {
                return str;
            }
            this.title = "";
            return "";
        }

        public String getLogo() {
            String str = this.logo;
            if (str != null) {
                return str;
            }
            this.logo = "";
            return "";
        }

        public String getUrl() {
            String str = this.url;
            if (str != null) {
                return str;
            }
            this.url = "";
            return "";
        }
    }
}

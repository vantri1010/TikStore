package im.bclpbkiauv.tgnet;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;

public class TLRPCHotChannel {

    public static class TL_HotGroupsNotModify extends TLObject {
        public static int constructor = 506285024;
    }

    public static class TL_GetHotGroups extends TLObject {
        public static int constructor = 2145420150;
        public int hash;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_HotGroups.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.hash);
        }
    }

    public static class TL_HotGroups extends TLObject {
        public static int constructor = 921323449;
        private ArrayList<TLRPC.Chat> chats;
        public int hash;
        private ArrayList<TL_HotGroupAbout> peers;

        public static TL_HotGroups TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_HotGroups order = new TL_HotGroups();
                order.readParams(stream, exception);
                return order;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("------->can't parse magic %x in TL_HotGroups", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a++) {
                    TLRPC.Chat object = TLRPC.Chat.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object == null) {
                        break;
                    }
                    if (this.chats == null) {
                        this.chats = new ArrayList<>();
                    }
                    this.chats.add(object);
                }
                int magic2 = stream.readInt32(exception);
                if (magic2 == 481674261) {
                    int count2 = stream.readInt32(exception);
                    for (int a2 = 0; a2 < count2; a2++) {
                        TL_HotGroupAbout object2 = TL_HotGroupAbout.TLdeserialize(stream, stream.readInt32(exception), exception);
                        if (object2 == null) {
                            break;
                        }
                        if (this.peers == null) {
                            this.peers = new ArrayList<>();
                        }
                        this.peers.add(object2);
                    }
                    this.hash = stream.readInt32(exception);
                } else if (exception) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic2)}));
                }
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }

        public ArrayList<TLRPC.Chat> getChats() {
            ArrayList<TLRPC.Chat> arrayList = this.chats;
            if (arrayList != null) {
                return arrayList;
            }
            ArrayList<TLRPC.Chat> arrayList2 = new ArrayList<>();
            this.chats = arrayList2;
            return arrayList2;
        }

        public ArrayList<TL_HotGroupAbout> getPeers() {
            ArrayList<TL_HotGroupAbout> arrayList = this.peers;
            if (arrayList != null) {
                return arrayList;
            }
            ArrayList<TL_HotGroupAbout> arrayList2 = new ArrayList<>();
            this.peers = arrayList2;
            return arrayList2;
        }
    }

    public static class TL_HotGroupAbout extends TLObject {
        public static int constructor = 1898118865;
        public String about;
        public int channelId;
        public String groupType;

        public static TL_HotGroupAbout TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_HotGroupAbout order = new TL_HotGroupAbout();
                order.readParams(stream, exception);
                return order;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("------->can't parse magic %x in TL_HotGroupAbout", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.channelId = stream.readInt32(exception);
            this.about = stream.readString(exception);
            this.groupType = stream.readString(exception);
        }
    }
}

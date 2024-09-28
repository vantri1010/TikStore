package im.bclpbkiauv.tgnet;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;

public class TLRPCFriendsHub {

    public static class TL_GetOtherConfig extends TLObject {
        public static int constructor = 2017161851;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_OtherConfig.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_OtherConfig extends TLObject {
        public static int constructor = 1929982835;
        public ArrayList<String> addrs = new ArrayList<>();
        public TLRPC.TL_dataJSON data;

        public static TL_OtherConfig TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_OtherConfig order = new TL_OtherConfig();
                order.readParams(stream, exception);
                return order;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("------->can't parse magic %x in TL_PaymentOrderList", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int size = stream.readInt32(exception);
                for (int i = 0; i < size; i++) {
                    this.addrs.add(stream.readString(exception));
                }
                this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }
    }
}

package im.bclpbkiauv.tgnet;

import im.bclpbkiauv.tgnet.TLRPC;

public class TLRPCBasic {

    public static class TL_GetToken extends TLObject {
        public static int constructor = 1533137608;
        public int flags;
        public boolean friendCircle;
        public boolean game;
        public boolean other;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_AllToken.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.friendCircle ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            int i2 = this.game ? i | 2 : i & -3;
            this.flags = i2;
            int i3 = this.other ? i2 | 4 : i2 & -5;
            this.flags = i3;
            stream.writeInt32(i3);
        }
    }

    public static class TL_AllToken extends TLObject {
        public static int constructor = -1388443551;
        public TLRPC.TL_dataJSON tokens;

        public static TL_AllToken TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_AllToken result = new TL_AllToken();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_AllToken", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.tokens = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }
    }
}

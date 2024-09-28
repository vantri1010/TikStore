package im.bclpbkiauv.tgnet;

import im.bclpbkiauv.tgnet.TLRPC;

public class TLRPCChats {

    public static class CL_channel_setParticipantBanMode extends TLObject {
        public static final int constructor = -1346817130;
        public boolean ban_add_contact;
        public boolean ban_send_message;
        public int flags;
        public TLRPC.InputPeer peer;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.ban_add_contact ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            int i2 = this.ban_send_message ? i | 2 : i & -3;
            this.flags = i2;
            stream.writeInt32(i2);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_channels_createChannel_v1 extends TLObject {
        public static int constructor = 992002528;
        public String about;
        public String address;
        public boolean ban_add_contact;
        public boolean ban_send_message;
        public boolean broadcast;
        public int flags;
        public TLRPC.InputGeoPoint geo_point;
        public boolean megagroup;
        public String title;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.broadcast ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            int i2 = this.megagroup ? i | 2 : i & -3;
            this.flags = i2;
            int i3 = this.ban_add_contact ? i2 | 8 : i2 & -9;
            this.flags = i3;
            int i4 = this.ban_send_message ? i3 | 16 : i3 & -17;
            this.flags = i4;
            stream.writeInt32(i4);
            stream.writeString(this.title);
            stream.writeString(this.about);
            if ((this.flags & 4) != 0) {
                this.geo_point.serializeToStream(stream);
            }
            if ((this.flags & 4) != 0) {
                stream.writeString(this.address);
            }
        }
    }
}

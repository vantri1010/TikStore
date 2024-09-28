package im.bclpbkiauv.tgnet;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;

public class TLRPCRedpacket {

    public static class CL_messages_sendRptTransfer extends TLObject {
        public static int constructor = -1599833265;
        public int flags;
        public ArrayList<TLRPC.InputPeer> orients = new ArrayList<>();
        public TLRPC.InputPeer peer;
        public long random_id;
        public TLRPC.TL_dataJSON redPkg;
        public int trans;
        public int type;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.type);
            }
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.trans);
            }
            if ((this.flags & 4) != 0) {
                stream.writeInt32(481674261);
                int count = this.orients.size();
                for (int i = 0; i < count; i++) {
                    this.orients.get(i).serializeToStream(stream);
                }
            }
            this.peer.serializeToStream(stream);
            this.redPkg.serializeToStream(stream);
            stream.writeInt64(this.random_id);
        }
    }

    public static class CL_updateRpkTransfer extends TLRPC.Update {
        public static int constructor = -687629886;
        public TLRPC.TL_dataJSON data;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.data.serializeToStream(stream);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }
    }

    public static class CL_messagesRpkTransferMedia extends TLRPC.MessageMedia {
        public static int constructor = -2124421581;
        public boolean all_received;
        public boolean been_received;
        public String caption;
        public int code;
        public TLRPC.TL_dataJSON data;
        public boolean expired;
        public int flags;
        public boolean received;
        public int trans;
        public int ttl_seconds;
        public int type;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            boolean z = true;
            if ((readInt32 & 1) != 0) {
                this.code = stream.readInt32(exception);
            }
            this.received = (this.flags & 2) != 0;
            this.been_received = (this.flags & 4) != 0;
            this.all_received = (this.flags & 8) != 0;
            if ((this.flags & 64) == 0) {
                z = false;
            }
            this.expired = z;
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
            if ((this.flags & 16) != 0) {
                this.caption = stream.readString(exception);
            }
            if ((this.flags & 32) != 0) {
                this.ttl_seconds = stream.readInt32(exception);
            }
            if ((this.flags & 128) != 0) {
                this.type = stream.readInt32(exception);
            }
            this.trans = stream.readInt32(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.code);
            }
            this.data.serializeToStream(stream);
            if ((this.flags & 16) != 0) {
                stream.writeString(this.caption);
            }
            if ((this.flags & 32) != 0) {
                stream.writeInt32(this.ttl_seconds);
            }
            if ((this.flags & 128) != 0) {
                stream.writeInt32(this.type);
            }
            stream.writeInt32(this.trans);
        }
    }

    public static class CL_message_rpkTransferCheck extends TLObject {
        public static int constructor = 771162175;
        public TLRPC.TL_dataJSON data;
        public int flags;
        public int id;
        public TLRPC.InputPeer peer;
        public int trans;
        public int type;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.type);
            }
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.trans);
            }
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.id);
            this.data.serializeToStream(stream);
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }
    }

    public static class CL_messages_rpkTransferReceive extends TLObject {
        public static int constructor = 1239819848;
        public TLRPC.TL_dataJSON data;
        public int flags;
        public int id;
        public TLRPC.InputPeer peer;
        public int trans;
        public int type;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.type);
            }
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.trans);
            }
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.id);
            this.data.serializeToStream(stream);
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }
    }

    public static class CL_messages_rpkTransferRefuse extends TLObject {
        public static int constructor = -156250301;
        public TLRPC.TL_dataJSON data;
        public int flags;
        public int id;
        public TLRPC.InputPeer peer;
        public int trans;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.trans);
            }
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.id);
            this.data.serializeToStream(stream);
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }
    }

    public static class CL_message_rpkTransferQuery extends TLObject {
        public static int constructor = 1654314729;
        public TLRPC.TL_dataJSON data;
        public int flags;
        public int trans;
        public int type;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.type);
            }
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.trans);
            }
            this.data.serializeToStream(stream);
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }
    }

    public static class CL_message_rpkTransferHistory extends TLObject {
        public static int constructor = 1380617728;
        public TLRPC.TL_dataJSON data;
        public int flags;
        public int trans;
        public int type;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.type);
            }
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.trans);
            }
            this.data.serializeToStream(stream);
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }
    }

    public static class CL_messagesActionReceivedRpkTransfer extends TLRPC.MessageAction {
        public static int constructor = -1047777236;
        public TLRPC.TL_dataJSON data;
        public int flags;
        public TLRPC.Peer receiver;
        public TLRPC.Peer sender;
        public int trans;
        public int type;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.type);
            }
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.trans);
            }
            this.receiver.serializeToStream(stream);
            this.sender.serializeToStream(stream);
            this.data.serializeToStream(stream);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.type = stream.readInt32(exception);
            }
            if ((this.flags & 2) != 0) {
                this.trans = stream.readInt32(exception);
            }
            this.receiver = TLRPC.Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.sender = TLRPC.Peer.TLdeserialize(stream, stream.readInt32(exception), exception);
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }
    }

    public static class CL_messagesPayBillOverMedia extends TLRPC.MessageMedia {
        public static int constructor = -1715619219;
        public TLRPC.TL_dataJSON data;
        public int deal_code;
        public int wallet_type;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.wallet_type);
            stream.writeInt32(this.deal_code);
            this.data.serializeToStream(stream);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.wallet_type = stream.readInt32(exception);
            this.deal_code = stream.readInt32(exception);
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }
    }

    public static class CL_messagesRpkTransferPaymentExpireMedia extends TLRPC.MessageMedia {
        public static int constructor = -819141235;
        public TLRPC.TL_dataJSON data;
        public int flags;
        public int trans;
        public int type;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.type);
            }
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.trans);
            }
            this.data.serializeToStream(stream);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.type = stream.readInt32(exception);
            }
            if ((this.flags & 2) != 0) {
                this.trans = stream.readInt32(exception);
            }
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }
    }

    public static class CL_messagesRpkTransferPaymentNotificationMedia extends TLRPC.MessageMedia {
        public static int constructor = 1099106768;
        public TLRPC.TL_dataJSON data;
        public int flags;
        public int trans;
        public int type;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.type);
            }
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.trans);
            }
            this.data.serializeToStream(stream);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.type = stream.readInt32(exception);
            }
            if ((this.flags & 2) != 0) {
                this.trans = stream.readInt32(exception);
            }
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }
    }
}

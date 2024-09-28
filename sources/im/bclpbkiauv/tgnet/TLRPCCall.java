package im.bclpbkiauv.tgnet;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;

public class TLRPCCall {

    public static class TL_UpdateMeetCallEmpty extends TLObject {
        public static int constructor = 1261461260;
        public String id;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Update.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.id);
        }
    }

    public static class TL_UpdateMeetCall extends TLRPC.Update {
        public static int constructor = -392332264;
        public int admin_id;
        public int date;
        public int flags;
        public String id;
        public boolean isPc;
        public long key_fingerPrint;
        public ArrayList<TLRPC.InputPeer> participant_id = new ArrayList<>();
        public boolean video;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Update.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            stream.writeInt32(i);
            stream.writeString(this.id);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(481674261);
            int count = this.participant_id.size();
            for (int i2 = 0; i2 < count; i2++) {
                this.participant_id.get(i2).serializeToStream(stream);
            }
            stream.writeInt64(this.key_fingerPrint);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            this.video = (readInt32 & 1) != 0;
            this.isPc = (this.flags & 2) != 0;
            this.id = stream.readString(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TLRPC.InputPeer inputPeer = TLRPC.InputPeer.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (inputPeer != null) {
                        this.participant_id.add(inputPeer);
                        a++;
                    } else {
                        return;
                    }
                }
                this.key_fingerPrint = stream.readInt64(exception);
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }
    }

    public static class TL_UpdateMeetCallWaiting extends TLRPC.Update {
        public static int constructor = -1773961835;
        public int admin_id;
        public String appid;
        public TLRPC.TL_dataJSON data;
        public int date;
        public int flags;
        public ArrayList<String> gslb = new ArrayList<>();
        public String id;
        public ArrayList<TLRPC.InputPeer> participant_id = new ArrayList<>();
        public int receive_date;
        public String token;
        public boolean video;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 32 : this.flags & -33;
            this.flags = i;
            stream.writeInt32(i);
            stream.writeString(this.id);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(481674261);
            int count = this.participant_id.size();
            for (int i2 = 0; i2 < count; i2++) {
                this.participant_id.get(i2).serializeToStream(stream);
            }
            if ((this.flags & 1) != 0) {
                stream.writeInt32(this.receive_date);
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            this.video = (readInt32 & 32) != 0;
            this.id = stream.readString(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TLRPC.InputPeer inputPeer = TLRPC.InputPeer.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (inputPeer != null) {
                        this.participant_id.add(inputPeer);
                        a++;
                    } else {
                        return;
                    }
                }
                if ((this.flags & 1) != 0) {
                    this.receive_date = stream.readInt32(exception);
                }
                if ((this.flags & 2) != 0) {
                    this.token = stream.readString(exception);
                }
                if ((this.flags & 4) != 0) {
                    this.appid = stream.readString(exception);
                }
                if ((this.flags & 8) != 0) {
                    int imagic = stream.readInt32(exception);
                    if (imagic == 481674261) {
                        int icount = stream.readInt32(exception);
                        int a2 = 0;
                        while (a2 < icount) {
                            String strTmp = stream.readString(exception);
                            if (strTmp != null) {
                                this.gslb.add(strTmp);
                                a2++;
                            } else {
                                return;
                            }
                        }
                    } else if (exception) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(imagic)}));
                    } else {
                        return;
                    }
                }
                if ((this.flags & 16) != 0) {
                    this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
                }
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }
    }

    public static class TL_UpdateMeetCallRequested extends TLRPC.Update {
        public static int constructor = 241392820;
        public int admin_id;
        public String appid;
        public TLRPC.TL_dataJSON data;
        public int date;
        public int flags;
        public ArrayList<String> gslb = new ArrayList<>();
        public String id;
        public ArrayList<TLRPC.InputPeer> participant_id = new ArrayList<>();
        public String token;
        public boolean video;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Update.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 32 : this.flags & -33;
            this.flags = i;
            stream.writeInt32(i);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(481674261);
            int count = this.participant_id.size();
            for (int i2 = 0; i2 < count; i2++) {
                this.participant_id.get(i2).serializeToStream(stream);
            }
            stream.writeString(this.token);
            stream.writeString(this.appid);
            stream.writeInt32(481674261);
            int count2 = this.gslb.size();
            for (int i3 = 0; i3 < count2; i3++) {
                stream.writeString(this.gslb.get(i3));
            }
            this.data.serializeToStream(stream);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            this.video = (readInt32 & 32) != 0;
            this.id = stream.readString(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TLRPC.InputPeer inputPeer = TLRPC.InputPeer.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (inputPeer != null) {
                        this.participant_id.add(inputPeer);
                        a++;
                    } else {
                        return;
                    }
                }
                this.token = stream.readString(exception);
                this.appid = stream.readString(exception);
                int imagic = stream.readInt32(exception);
                if (imagic == 481674261) {
                    int icount = stream.readInt32(exception);
                    int a2 = 0;
                    while (a2 < icount) {
                        String strTmp = stream.readString(exception);
                        if (strTmp != null) {
                            this.gslb.add(strTmp);
                            a2++;
                        } else {
                            return;
                        }
                    }
                    this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
                } else if (exception) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(imagic)}));
                }
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }
    }

    public static class TL_UpdateMeetCallAccepted extends TLRPC.Update {
        public static int constructor = 117720172;
        public int admin_id;
        public int date;
        public int flags;
        public String id;
        public ArrayList<TLRPC.InputPeer> participant_id = new ArrayList<>();
        public boolean video;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Update.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 32 : this.flags & -33;
            this.flags = i;
            stream.writeInt32(i);
            stream.writeString(this.id);
            stream.writeInt32(this.date);
            stream.writeInt32(this.admin_id);
            stream.writeInt32(481674261);
            int count = this.participant_id.size();
            for (int i2 = 0; i2 < count; i2++) {
                this.participant_id.get(i2).serializeToStream(stream);
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            this.video = (readInt32 & 32) != 0;
            this.id = stream.readString(exception);
            this.date = stream.readInt32(exception);
            this.admin_id = stream.readInt32(exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TLRPC.InputPeer inputPeer = TLRPC.InputPeer.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (inputPeer != null) {
                        this.participant_id.add(inputPeer);
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

    public static class TL_UpdateMeetCallDiscarded extends TLRPC.Update {
        public static int constructor = 1975844770;
        public int duration;
        public int flags;
        public String id;
        public boolean video;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Update.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 32 : this.flags & -33;
            this.flags = i;
            stream.writeInt32(i);
            stream.writeString(this.id);
            if ((this.flags & 2) != 0) {
                stream.writeInt32(this.duration);
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            this.video = (readInt32 & 32) != 0;
            this.id = stream.readString(exception);
            if ((this.flags & 2) != 0) {
                this.duration = stream.readInt32(exception);
            }
        }
    }

    public static class TL_UpdateMeetCallHistory extends TLRPC.TL_updates {
        public static int constructor = -1140700830;
        public TLRPC.TL_dataJSON data;
        public int flags;

        public TLRPC.Update deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Update.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.data.serializeToStream(stream);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }
    }

    public static class TL_InputMeetCall extends TLObject {
        public static int constructor = -1472010869;
        public String id;

        public TL_InputMeetCall deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_InputMeetCall result = new TL_InputMeetCall();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in InputmeetCall", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.id);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.id = stream.readString(exception);
        }
    }

    public static class TL_VideoConfig extends TLObject {
        public static int constructor = 1430593449;

        public TLRPC.TL_dataJSON deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.TL_dataJSON.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_MeetRequestCall extends TLObject {
        public static int constructor = 761572648;
        public TLRPC.InputPeer channel_id;
        public int flags;
        public long random_id;
        public ArrayList<TLRPC.InputPeer> userIdList = new ArrayList<>();
        public boolean video;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            stream.writeInt32(i);
            if ((this.flags & 2) != 0) {
                stream.writeInt32(481674261);
                int count = this.userIdList.size();
                stream.writeInt32(count);
                for (int i2 = 0; i2 < count; i2++) {
                    this.userIdList.get(i2).serializeToStream(stream);
                }
            }
            if ((this.flags & 4) != 0) {
                this.channel_id.serializeToStream(stream);
            }
            stream.writeInt64(this.random_id);
        }
    }

    public static class TL_MeetAcceptCall extends TLObject {
        public static int constructor = -1711386009;
        public TL_InputMeetCall peer;

        public TLRPC.Updates deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_MeetConfirmCall extends TLObject {
        public static int constructor = 2037433910;
        public long key_fingerprint;
        public TL_InputMeetCall peer;

        public TLRPC.Updates deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
            stream.writeInt64(this.key_fingerprint);
        }
    }

    public static class TL_MeetKeepCallV1 extends TLObject {
        public static int constructor = 172728919;
        public TL_InputMeetCall peer;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_MeetModel extends TLRPC.Updates {
        public static int constructor = -534468978;
        public int flags;
        public String id;
        public boolean video;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            stream.writeInt32(i);
            stream.writeString(this.id);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.flags = stream.readInt32(exception);
            this.id = stream.readString(exception);
            boolean z = true;
            if ((this.flags & 1) == 0) {
                z = false;
            }
            this.video = z;
        }
    }

    public static class TL_MeetReceivedCall extends TLObject {
        public static int constructor = -2143433199;
        public TL_InputMeetCall peer;

        public TLRPC.Bool deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Bool.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_MeetDiscardCall extends TLObject {
        public static int constructor = -934946139;
        public int duration;
        public int flags;
        public TL_InputMeetCall peer;
        public TLRPC.PhoneCallDiscardReason reason;
        public boolean video;

        public TLRPC.Updates deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            stream.writeInt32(i);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.duration);
            this.reason.serializeToStream(stream);
        }
    }

    public static class TL_MeetGetCallHistory extends TLObject {
        public static int constructor = -521451147;
        public int add_offset;
        public int flags;
        public int hash;
        public int limit;
        public int max_id;
        public int min_id;
        public int offset_date;
        public int offset_id;
        public TLRPC.InputPeer peer;
        public boolean video;

        public TLRPC.Updates deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Updates.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 32 : this.flags & -33;
            this.flags = i;
            stream.writeInt32(i);
            this.peer.serializeToStream(stream);
            stream.writeInt32(this.offset_id);
            stream.writeInt32(this.offset_date);
            stream.writeInt32(this.add_offset);
            stream.writeInt32(this.limit);
            stream.writeInt32(this.max_id);
            stream.writeInt32(this.min_id);
            stream.writeInt32(this.hash);
        }
    }

    public static class TL_hub_getOtherConfig extends TLObject {
        public static int constructor = 307107699;

        public TL_OtherConfig deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_OtherConfig.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_OtherConfig extends TLObject {
        public static int constructor = 1929982835;
        public ArrayList addrs = new ArrayList();
        public TLRPC.TL_dataJSON data;

        public static TL_OtherConfig TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_OtherConfig result = new TL_OtherConfig();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_OtherConfig", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                for (int a = 0; a < count; a++) {
                    this.addrs.add(stream.readString(exception));
                }
                this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }
    }

    public static class TL_MeetChangeCall extends TLObject {
        public static int constructor = 1239003785;
        public int flags;
        public TL_InputMeetCall peer;
        public boolean video;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Update.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            stream.writeInt32(i);
            this.peer.serializeToStream(stream);
        }
    }

    public static class TL_UpdateMeetChangeCall extends TLRPC.Update {
        public static int constructor = -268456246;
        public int flags;
        public String id;
        public boolean video;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TLRPC.Update.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.video ? this.flags | 1 : this.flags & -2;
            this.flags = i;
            stream.writeInt32(i);
            stream.writeString(this.id);
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            int readInt32 = stream.readInt32(exception);
            this.flags = readInt32;
            boolean z = true;
            if ((readInt32 & 1) == 0) {
                z = false;
            }
            this.video = z;
            this.id = stream.readString(exception);
        }
    }
}

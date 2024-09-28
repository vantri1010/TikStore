package im.bclpbkiauv.ui.imtoken;

import im.bclpbkiauv.tgnet.AbstractSerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import java.util.ArrayList;

public class TLRPCImToken {

    public static class TL_GetTokensV1 extends TLObject {
        public static int constructor = -945605970;
        public boolean digitalAndordinaryWallet;
        public boolean digitalWallet;
        public ArrayList<String> old_tokens = new ArrayList<>();
        public boolean ordinaryWallet;
        public int refresh;
        private int types;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_AllToken.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            int i = this.digitalWallet ? this.types | 1 : this.types & -2;
            this.types = i;
            int i2 = this.ordinaryWallet ? i | 2 : i & -3;
            this.types = i2;
            int i3 = this.digitalAndordinaryWallet ? i2 | 4 : i2 & -5;
            this.types = i3;
            stream.writeInt32(i3);
            stream.writeInt32(this.refresh);
            stream.writeInt32(481674261);
            int count = this.old_tokens.size();
            stream.writeInt32(count);
            for (int i4 = 0; i4 < count; i4++) {
                stream.writeString(this.old_tokens.get(i4));
            }
        }
    }

    public static class TL_AllToken extends TLObject {
        public static int constructor = 610385568;
        public ArrayList<TL_Token> tokens = new ArrayList<>();

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
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int count = stream.readInt32(exception);
                int a = 0;
                while (a < count) {
                    TL_Token object = TL_Token.TLdeserialize(stream, stream.readInt32(exception), exception);
                    if (object != null) {
                        this.tokens.add(object);
                        a++;
                    } else {
                        return;
                    }
                }
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }

        public ArrayList<TL_Token> getTokenBeans() {
            return this.tokens;
        }
    }

    public static class TL_Token extends TLObject {
        public static int constructor = 1227249459;
        public int expire;
        public String token;
        public int type;

        public static TL_Token TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_Token result = new TL_Token();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_Token", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.type = stream.readInt32(exception);
            this.token = stream.readString(exception);
            this.expire = stream.readInt32(exception);
        }
    }

    @Deprecated
    public static class TL_DigtalReqPqV1 extends TLObject {
        public static int constructor = 2043024112;
        public String nonce;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_DigtalRepPqV1.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.nonce);
        }
    }

    @Deprecated
    public static class TL_DigtalRepPqV1 extends TLObject {
        public static int constructor = -1087383368;
        public boolean digital_wallet;
        public int expire;
        public String server_nonce;
        public String token;

        public static TL_DigtalRepPqV1 TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_DigtalRepPqV1 result = new TL_DigtalRepPqV1();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_Token", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.token = stream.readString(exception);
            this.server_nonce = stream.readString(exception);
            this.expire = stream.readInt32(exception);
            this.digital_wallet = stream.readBool(exception);
        }
    }
}

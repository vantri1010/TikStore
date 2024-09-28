package im.bclpbkiauv.tgnet;

import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.constants.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;

public class TLRPCWallet implements Constants {

    public static abstract class TL_paymentAccount extends TLObject {
        public static TL_paymentAccount TLdeserialize(AbstractSerializedData stream, int constructor, boolean exception) {
            TL_paymentAccount result = null;
            if (constructor == -1754715375) {
                result = new TL_paymentAccountInfo();
            } else if (constructor == 1425351789) {
                result = new TL_paymentAccountInfoNotExist();
            }
            if (result != null || !exception) {
                if (result != null) {
                    result.readParams(stream, exception);
                }
                return result;
            }
            throw new RuntimeException(String.format("can't parse magic %x in DraftMessage", new Object[]{Integer.valueOf(constructor)}));
        }
    }

    public static class TL_paymentAccountInfo extends TL_paymentAccount {
        public static int constructor = -1754715375;
        public TLRPC.TL_dataJSON dataJSON;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            super.readParams(stream, exception);
            this.dataJSON = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.dataJSON.serializeToStream(stream);
        }
    }

    public static class TL_paymentAccountInfoNotExist extends TL_paymentAccount {
        public static int constructor = 1425351789;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            super.readParams(stream, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_getPaymentAccountInfo extends TLObject {
        public static int constructor = 369827653;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_paymentAccount.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
        }
    }

    public static class TL_createAccount extends TLObject {
        public static int constructor = -1575232465;
        public TLRPC.TL_dataJSON data;

        public TL_createAccount() {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            this.data = tL_dataJSON;
            tL_dataJSON.data = "";
        }

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_paymentAccount.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.data.serializeToStream(stream);
        }
    }

    public static class TL_PaymentOrderList extends TLObject {
        public static int constructor = 844418445;
        public TLRPC.TL_dataJSON order_info;
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_PaymentOrderList TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_PaymentOrderList order = new TL_PaymentOrderList();
                order.readParams(stream, exception);
                return order;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("------->can't parse magic %x in TL_PaymentOrderList", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.order_info.serializeToStream(stream);
            stream.writeInt32(this.users.size());
            Iterator<TLRPC.User> it = this.users.iterator();
            while (it.hasNext()) {
                it.next().serializeToStream(stream);
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.order_info = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int size = stream.readInt32(exception);
                for (int i = 0; i < size; i++) {
                    this.users.add(TLRPC.User.TLdeserialize(stream, stream.readInt32(exception), exception));
                }
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }
    }

    public static class TL_GetOrderList<T> extends TLObject {
        public static int constructor = 2052892843;
        public TLRPC.TL_dataJSON param = new TLRPC.TL_dataJSON();
        public T requestModel;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_PaymentOrderList.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            TLRPC.TL_dataJSON tL_dataJSON;
            stream.writeInt32(constructor);
            if (this.requestModel != null && ((tL_dataJSON = this.param) == null || tL_dataJSON.data == null)) {
                if (this.param == null) {
                    this.param = new TLRPC.TL_dataJSON();
                }
                if (this.param.data == null) {
                    this.param.data = ParamsUtil.toJson((Object) this.requestModel);
                }
            }
            this.param.serializeToStream(stream);
        }
    }

    public static class TL_paymentTransResult extends TLObject {
        public static int constructor = -749965841;
        public TLRPC.TL_dataJSON data;
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_paymentTransResult TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_paymentTransResult result = new TL_paymentTransResult();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_paymentTransResult", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
            int magic = stream.readInt32(exception);
            if (magic == 481674261) {
                int size = stream.readInt32(exception);
                for (int i = 0; i < size; i++) {
                    this.users.add(TLRPC.User.TLdeserialize(stream, stream.readInt32(exception), exception));
                }
            } else if (exception) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(magic)}));
            }
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.data.serializeToStream(stream);
            stream.writeInt32(this.users.size());
            Iterator<TLRPC.User> it = this.users.iterator();
            while (it.hasNext()) {
                it.next().serializeToStream(stream);
            }
        }

        public String getData() {
            TLRPC.TL_dataJSON tL_dataJSON = this.data;
            return tL_dataJSON != null ? tL_dataJSON.data : "";
        }
    }

    public static class TL_paymentTrans<T> extends TLObject {
        public static int constructor = -150222217;
        public TLRPC.TL_dataJSON data = new TLRPC.TL_dataJSON();
        public T requestModel;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_paymentTransResult.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            if (this.requestModel != null) {
                TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
                this.data = tL_dataJSON;
                tL_dataJSON.data = ParamsUtil.toJson((Object) this.requestModel);
            }
            this.data.serializeToStream(stream);
        }
    }

    public static class Builder {
        private static final String VERSION = "0.0.1";
        private Map<String, Object> mParams = new HashMap();
        private TL_paymentTrans req = new TL_paymentTrans();

        public Builder setBusinessKey(String key) {
            this.mParams.put("businessKey", key);
            return this;
        }

        public Builder setBusinessKeyAndId(String key) {
            this.mParams.put("businessKey", key);
            this.mParams.put("userId", Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id));
            return this;
        }

        public Builder setVersion(String version) {
            this.mParams.put("version", version);
            return this;
        }

        public Builder addParam(String key, Object value) {
            this.mParams.put(key, value);
            return this;
        }

        public Builder addParams(Map<String, Object> params) {
            this.mParams.putAll(params);
            return this;
        }

        public Builder addParams(String[] keys, Object[] values) {
            if (!(keys == null || values == null || keys.length != values.length)) {
                for (int i = 0; i < keys.length; i++) {
                    this.mParams.put(keys[i], values[i]);
                }
            }
            return this;
        }

        public TL_paymentTrans build() {
            if (!this.mParams.containsKey("version")) {
                this.mParams.put("version", "0.0.1");
            }
            JSONObject object = new JSONObject(this.mParams);
            this.req.data.data = object.toString();
            return this.req;
        }
    }

    public static class TL_IconDetail extends TLObject {
        public static int constructor = -1059648005;
        public TLRPC.TL_dataJSON data;

        public static TL_IconDetail TLdeserialize(AbstractSerializedData stream, int constructor2, boolean exception) {
            if (constructor == constructor2) {
                TL_IconDetail result = new TL_IconDetail();
                result.readParams(stream, exception);
                return result;
            } else if (!exception) {
                return null;
            } else {
                throw new RuntimeException(String.format("can't parse magic %x in TL_IconDetail", new Object[]{Integer.valueOf(constructor2)}));
            }
        }

        public void readParams(AbstractSerializedData stream, boolean exception) {
            this.data = TLRPC.TL_dataJSON.TLdeserialize(stream, stream.readInt32(exception), exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.data.serializeToStream(stream);
        }
    }

    public static class TL_saveIconInfo extends TLObject {
        public static int constructor = 482741217;
        public TLRPC.TL_dataJSON data = new TLRPC.TL_dataJSON();
        public TLRPC.InputFile file;

        public TLObject deserializeResponse(AbstractSerializedData stream, int constructor2, boolean exception) {
            return TL_IconDetail.TLdeserialize(stream, constructor2, exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            this.file.serializeToStream(stream);
            this.data.serializeToStream(stream);
        }
    }
}

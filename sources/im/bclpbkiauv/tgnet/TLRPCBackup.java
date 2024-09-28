package im.bclpbkiauv.tgnet;

import java.util.ArrayList;
import java.util.Iterator;

public class TLRPCBackup {

    public static class CL_java_simpleConfig extends TLObject {
        public static final int constructor = 1515793004;
        public int date;
        public int expires;
        public ArrayList<CL_java_ipPortRule> rules = new ArrayList<>();

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.date);
            stream.writeInt32(this.expires);
            stream.writeInt32(this.rules.size());
            Iterator<CL_java_ipPortRule> it = this.rules.iterator();
            while (it.hasNext()) {
                it.next().serializeToStream(stream);
            }
        }
    }

    public static final class CL_java_ipPortRule extends TLObject {
        public static final int constructor = 1182381663;
        public int dc_id;
        public ArrayList<CL_java_ipPort> ips = new ArrayList<>();
        public String phone_prefix_rules;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeString(this.phone_prefix_rules);
            stream.writeInt32(this.dc_id);
            stream.writeInt32(this.ips.size());
            Iterator<CL_java_ipPort> it = this.ips.iterator();
            while (it.hasNext()) {
                it.next().serializeToStream(stream);
            }
        }
    }

    public static final class CL_java_ipPort extends TLObject {
        public static final int constructor = -734810765;
        public int ipv4;
        public int port;

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt32(this.ipv4);
            stream.writeInt32(this.port);
        }
    }
}

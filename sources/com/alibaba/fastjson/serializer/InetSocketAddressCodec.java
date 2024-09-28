package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class InetSocketAddressCodec implements ObjectSerializer, ObjectDeserializer {
    public static InetSocketAddressCodec instance = new InetSocketAddressCodec();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        if (object == null) {
            serializer.writeNull();
            return;
        }
        SerializeWriter out = serializer.getWriter();
        InetSocketAddress address = (InetSocketAddress) object;
        InetAddress inetAddress = address.getAddress();
        out.write('{');
        if (inetAddress != null) {
            out.writeFieldName("address");
            serializer.write((Object) inetAddress);
            out.write(',');
        }
        out.writeFieldName("port");
        out.writeInt(address.getPort());
        out.write('}');
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: java.net.InetAddress} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r7, java.lang.reflect.Type r8, java.lang.Object r9) {
        /*
            r6 = this;
            com.alibaba.fastjson.parser.JSONLexer r0 = r7.getLexer()
            int r1 = r0.token()
            r2 = 8
            if (r1 != r2) goto L_0x0011
            r0.nextToken()
            r1 = 0
            return r1
        L_0x0011:
            r1 = 12
            r7.accept(r1)
            r1 = 0
            r2 = 0
        L_0x0018:
            java.lang.String r3 = r0.stringVal()
            r4 = 17
            r0.nextToken(r4)
            java.lang.String r5 = "address"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x0036
            r7.accept(r4)
            java.lang.Class<java.net.InetAddress> r4 = java.net.InetAddress.class
            java.lang.Object r4 = r7.parseObject(r4)
            r1 = r4
            java.net.InetAddress r1 = (java.net.InetAddress) r1
            goto L_0x005e
        L_0x0036:
            java.lang.String r5 = "port"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x0058
            r7.accept(r4)
            int r4 = r0.token()
            r5 = 2
            if (r4 != r5) goto L_0x0050
            int r2 = r0.intValue()
            r0.nextToken()
            goto L_0x005e
        L_0x0050:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException
            java.lang.String r5 = "port is not int"
            r4.<init>(r5)
            throw r4
        L_0x0058:
            r7.accept(r4)
            r7.parse()
        L_0x005e:
            int r4 = r0.token()
            r5 = 16
            if (r4 != r5) goto L_0x006a
            r0.nextToken()
            goto L_0x0018
        L_0x006a:
            r3 = 13
            r7.accept(r3)
            java.net.InetSocketAddress r3 = new java.net.InetSocketAddress
            r3.<init>(r1, r2)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.serializer.InetSocketAddressCodec.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object):java.lang.Object");
    }

    public int getFastMatchToken() {
        return 12;
    }
}

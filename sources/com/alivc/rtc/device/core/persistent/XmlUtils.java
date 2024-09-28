package com.alivc.rtc.device.core.persistent;

import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class XmlUtils {
    XmlUtils() {
    }

    public static final void writeMapXml(Map val, OutputStream out) throws XmlPullParserException, IOException {
        XmlSerializer serializer = new FastXmlSerializer();
        serializer.setOutput(out, "utf-8");
        String str = null;
        serializer.startDocument(str, true);
        serializer.setFeature(FastXmlSerializer.getFeatureUrl(), true);
        writeMapXml(val, str, serializer);
        serializer.endDocument();
    }

    public static final void writeMapXml(Map val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            String str = null;
            out.startTag(str, "null");
            out.endTag(str, "null");
            return;
        }
        String str2 = null;
        out.startTag(str2, "map");
        if (name != null) {
            out.attribute(str2, "name", name);
        }
        for (Map.Entry e : val.entrySet()) {
            writeValueXml(e.getValue(), (String) e.getKey(), out);
        }
        out.endTag(str2, "map");
    }

    public static final void writeListXml(List val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            String str = null;
            out.startTag(str, "null");
            out.endTag(str, "null");
            return;
        }
        String str2 = null;
        out.startTag(str2, "list");
        if (name != null) {
            out.attribute(str2, "name", name);
        }
        int N = val.size();
        for (int i = 0; i < N; i++) {
            writeValueXml(val.get(i), str2, out);
        }
        out.endTag(str2, "list");
    }

    public static final void writeByteArrayXml(byte[] val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            String str = null;
            out.startTag(str, "null");
            out.endTag(str, "null");
            return;
        }
        String str2 = null;
        out.startTag(str2, "byte-array");
        if (name != null) {
            out.attribute(str2, "name", name);
        }
        out.attribute(str2, "num", Integer.toString(N));
        StringBuilder sb = new StringBuilder(val.length * 2);
        for (byte b : val) {
            int h = b >> 4;
            sb.append(h >= 10 ? (h + 97) - 10 : h + 48);
            int h2 = b & 255;
            sb.append(h2 >= 10 ? (h2 + 97) - 10 : h2 + 48);
        }
        out.text(sb.toString());
        out.endTag(str2, "byte-array");
    }

    public static final void writeIntArrayXml(int[] val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            String str = null;
            out.startTag(str, "null");
            out.endTag(str, "null");
            return;
        }
        String str2 = null;
        out.startTag(str2, "int-array");
        if (name != null) {
            out.attribute(str2, "name", name);
        }
        out.attribute(str2, "num", Integer.toString(N));
        for (int num : val) {
            out.startTag(str2, "item");
            out.attribute(str2, "value", Integer.toString(num));
            out.endTag(str2, "item");
        }
        out.endTag(str2, "int-array");
    }

    public static final void writeValueXml(Object v, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        String typeStr;
        if (v == null) {
            String str = null;
            out.startTag(str, "null");
            if (name != null) {
                out.attribute(str, "name", name);
            }
            out.endTag(str, "null");
        } else if (v instanceof String) {
            String str2 = null;
            out.startTag(str2, "string");
            if (name != null) {
                out.attribute(str2, "name", name);
            }
            out.text(v.toString());
            out.endTag(str2, "string");
        } else {
            if (v instanceof Integer) {
                typeStr = "int";
            } else if (v instanceof Long) {
                typeStr = "long";
            } else if (v instanceof Float) {
                typeStr = "float";
            } else if (v instanceof Double) {
                typeStr = "double";
            } else if (v instanceof Boolean) {
                typeStr = "boolean";
            } else if (v instanceof byte[]) {
                writeByteArrayXml((byte[]) v, name, out);
                return;
            } else if (v instanceof int[]) {
                writeIntArrayXml((int[]) v, name, out);
                return;
            } else if (v instanceof Map) {
                writeMapXml((Map) v, name, out);
                return;
            } else if (v instanceof List) {
                writeListXml((List) v, name, out);
                return;
            } else if (v instanceof CharSequence) {
                String str3 = null;
                out.startTag(str3, "string");
                if (name != null) {
                    out.attribute(str3, "name", name);
                }
                out.text(v.toString());
                out.endTag(str3, "string");
                return;
            } else {
                throw new RuntimeException("writeValueXml: unable to write value " + v);
            }
            String str4 = null;
            out.startTag(str4, typeStr);
            if (name != null) {
                out.attribute(str4, "name", name);
            }
            out.attribute(str4, "value", v.toString());
            out.endTag(str4, typeStr);
        }
    }

    public static final HashMap readMapXml(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, (String) null);
        return (HashMap) readValueXml(parser, new String[1]);
    }

    public static final HashMap readThisMapXml(XmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        HashMap map = new HashMap();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                Object val = readThisValueXml(parser, name);
                if (name[0] != null) {
                    map.put(name[0], val);
                } else {
                    throw new XmlPullParserException("Map value without name attribute: " + parser.getName());
                }
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return map;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final ArrayList readThisListXml(XmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        ArrayList list = new ArrayList();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                list.add(readThisValueXml(parser, name));
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return list;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final int[] readThisIntArrayXml(XmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        try {
            int[] array = new int[Integer.parseInt(parser.getAttributeValue((String) null, "num"))];
            int i = 0;
            int eventType = parser.getEventType();
            do {
                if (eventType == 2) {
                    if (parser.getName().equals("item")) {
                        try {
                            array[i] = Integer.parseInt(parser.getAttributeValue((String) null, "value"));
                        } catch (NullPointerException e) {
                            throw new XmlPullParserException("Need value attribute in item");
                        } catch (NumberFormatException e2) {
                            throw new XmlPullParserException("Not a number in value attribute in item");
                        }
                    } else {
                        throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                    }
                } else if (eventType == 3) {
                    if (parser.getName().equals(endTag)) {
                        return array;
                    }
                    if (parser.getName().equals("item")) {
                        i++;
                    } else {
                        throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
                    }
                }
                eventType = parser.next();
            } while (eventType != 1);
            throw new XmlPullParserException("Document ended before " + endTag + " end tag");
        } catch (NullPointerException e3) {
            throw new XmlPullParserException("Need num attribute in byte-array");
        } catch (NumberFormatException e4) {
            throw new XmlPullParserException("Not a number in num attribute in byte-array");
        }
    }

    public static final Object readValueXml(XmlPullParser parser, String[] name) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        while (eventType != 2) {
            if (eventType == 3) {
                throw new XmlPullParserException("Unexpected end tag at: " + parser.getName());
            } else if (eventType != 4) {
                try {
                    eventType = parser.next();
                    if (eventType == 1) {
                        throw new XmlPullParserException("Unexpected end of document");
                    }
                } catch (Exception e) {
                    throw new XmlPullParserException("Unexpected call next(): " + parser.getName());
                }
            } else {
                throw new XmlPullParserException("Unexpected text: " + parser.getText());
            }
        }
        return readThisValueXml(parser, name);
    }

    private static Object readThisValueXml(XmlPullParser parser, String[] name) throws XmlPullParserException, IOException {
        Object res;
        int eventType;
        String str = null;
        String valueName = parser.getAttributeValue(str, "name");
        String tagName = parser.getName();
        if (tagName.equals("null")) {
            res = null;
        } else if (tagName.equals("string")) {
            String value = "";
            while (true) {
                int next = parser.next();
                int eventType2 = next;
                if (next == 1) {
                    throw new XmlPullParserException("Unexpected end of document in <string>");
                } else if (eventType2 == 3) {
                    if (parser.getName().equals("string")) {
                        name[0] = valueName;
                        return value;
                    }
                    throw new XmlPullParserException("Unexpected end tag in <string>: " + parser.getName());
                } else if (eventType2 == 4) {
                    value = value + parser.getText();
                } else if (eventType2 == 2) {
                    throw new XmlPullParserException("Unexpected start tag in <string>: " + parser.getName());
                }
            }
        } else if (tagName.equals("int")) {
            res = Integer.valueOf(Integer.parseInt(parser.getAttributeValue(str, "value")));
        } else if (tagName.equals("long")) {
            res = Long.valueOf(parser.getAttributeValue(str, "value"));
        } else if (tagName.equals("float")) {
            res = Float.valueOf(parser.getAttributeValue(str, "value"));
        } else if (tagName.equals("double")) {
            res = Double.valueOf(parser.getAttributeValue(str, "value"));
        } else if (tagName.equals("boolean")) {
            res = Boolean.valueOf(parser.getAttributeValue(str, "value"));
        } else if (tagName.equals("int-array")) {
            parser.next();
            Object res2 = readThisIntArrayXml(parser, "int-array", name);
            name[0] = valueName;
            return res2;
        } else if (tagName.equals("map")) {
            parser.next();
            Object res3 = readThisMapXml(parser, "map", name);
            name[0] = valueName;
            return res3;
        } else if (tagName.equals("list")) {
            parser.next();
            Object res4 = readThisListXml(parser, "list", name);
            name[0] = valueName;
            return res4;
        } else {
            throw new XmlPullParserException("Unknown tag: " + tagName);
        }
        do {
            int next2 = parser.next();
            eventType = next2;
            if (next2 == 1) {
                throw new XmlPullParserException("Unexpected end of document in <" + tagName + ">");
            } else if (eventType == 3) {
                if (parser.getName().equals(tagName)) {
                    name[0] = valueName;
                    return res;
                }
                throw new XmlPullParserException("Unexpected end tag in <" + tagName + ">: " + parser.getName());
            } else if (eventType == 4) {
                throw new XmlPullParserException("Unexpected text in <" + tagName + ">: " + parser.getName());
            }
        } while (eventType != 2);
        throw new XmlPullParserException("Unexpected start tag in <" + tagName + ">: " + parser.getName());
    }
}

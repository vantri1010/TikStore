package com.google.protobuf;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.protobuf.GeneratedMessageLite;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import kotlin.text.Typography;

final class MessageLiteToString {
    private static final String BUILDER_LIST_SUFFIX = "OrBuilderList";
    private static final String BYTES_SUFFIX = "Bytes";
    private static final String LIST_SUFFIX = "List";

    MessageLiteToString() {
    }

    static String toString(MessageLite messageLite, String commentString) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("# ");
        buffer.append(commentString);
        reflectivePrintWithIndent(messageLite, buffer, 0);
        return buffer.toString();
    }

    private static void reflectivePrintWithIndent(MessageLite messageLite, StringBuilder buffer, int indent) {
        Map<String, Method> nameToNoArgMethod;
        boolean hasValue;
        MessageLite messageLite2 = messageLite;
        StringBuilder sb = buffer;
        int i = indent;
        Map<String, Method> hashMap = new HashMap<>();
        Map<String, Method> nameToMethod = new HashMap<>();
        Set<String> getters = new TreeSet<>();
        int i2 = 0;
        for (Method method : messageLite.getClass().getDeclaredMethods()) {
            nameToMethod.put(method.getName(), method);
            if (method.getParameterTypes().length == 0) {
                hashMap.put(method.getName(), method);
                if (method.getName().startsWith("get")) {
                    getters.add(method.getName());
                }
            }
        }
        for (String getter : getters) {
            String suffix = getter.replaceFirst("get", "");
            if (suffix.endsWith(LIST_SUFFIX) && !suffix.endsWith(BUILDER_LIST_SUFFIX)) {
                String camelCase = suffix.substring(i2, 1).toLowerCase() + suffix.substring(1, suffix.length() - LIST_SUFFIX.length());
                Method listMethod = (Method) hashMap.get("get" + suffix);
                if (listMethod != null) {
                    printField(sb, i, camelCaseToSnakeCase(camelCase), GeneratedMessageLite.invokeOrDie(listMethod, messageLite2, new Object[i2]));
                }
            }
            if (nameToMethod.get("set" + suffix) != null) {
                if (suffix.endsWith(BYTES_SUFFIX)) {
                    if (hashMap.containsKey("get" + suffix.substring(i2, suffix.length() - BYTES_SUFFIX.length()))) {
                    }
                }
                String camelCase2 = suffix.substring(i2, 1).toLowerCase() + suffix.substring(1);
                Method getMethod = hashMap.get("get" + suffix);
                Method hasMethod = hashMap.get("has" + suffix);
                if (getMethod != null) {
                    Object value = GeneratedMessageLite.invokeOrDie(getMethod, messageLite2, new Object[i2]);
                    if (hasMethod == null) {
                        nameToNoArgMethod = hashMap;
                        hasValue = !isDefaultValue(value);
                    } else {
                        nameToNoArgMethod = hashMap;
                        hasValue = ((Boolean) GeneratedMessageLite.invokeOrDie(hasMethod, messageLite2, new Object[i2])).booleanValue();
                    }
                    if (hasValue) {
                        printField(sb, i, camelCaseToSnakeCase(camelCase2), value);
                        hashMap = nameToNoArgMethod;
                        i2 = 0;
                    } else {
                        hashMap = nameToNoArgMethod;
                        i2 = 0;
                    }
                } else {
                    Map<String, Method> nameToNoArgMethod2 = hashMap;
                    i2 = 0;
                }
            }
        }
        Map<String, Method> nameToNoArgMethod3 = hashMap;
        if (messageLite2 instanceof GeneratedMessageLite.ExtendableMessage) {
            Iterator<Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object>> iter = ((GeneratedMessageLite.ExtendableMessage) messageLite2).extensions.iterator();
            while (iter.hasNext()) {
                Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object> entry = iter.next();
                printField(sb, i, "[" + entry.getKey().getNumber() + "]", entry.getValue());
            }
        }
        if (((GeneratedMessageLite) messageLite2).unknownFields != null) {
            ((GeneratedMessageLite) messageLite2).unknownFields.printWithIndent(sb, i);
        }
    }

    private static boolean isDefaultValue(Object o) {
        if (o instanceof Boolean) {
            return !((Boolean) o).booleanValue();
        }
        if (o instanceof Integer) {
            if (((Integer) o).intValue() == 0) {
                return true;
            }
            return false;
        } else if (o instanceof Float) {
            if (((Float) o).floatValue() == 0.0f) {
                return true;
            }
            return false;
        } else if (o instanceof Double) {
            if (((Double) o).doubleValue() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                return true;
            }
            return false;
        } else if (o instanceof String) {
            return o.equals("");
        } else {
            if (o instanceof ByteString) {
                return o.equals(ByteString.EMPTY);
            }
            if (o instanceof MessageLite) {
                if (o == ((MessageLite) o).getDefaultInstanceForType()) {
                    return true;
                }
                return false;
            } else if (!(o instanceof Enum) || ((Enum) o).ordinal() != 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    static final void printField(StringBuilder buffer, int indent, String name, Object object) {
        if (object instanceof List) {
            for (Object entry : (List) object) {
                printField(buffer, indent, name, entry);
            }
            return;
        }
        buffer.append(10);
        for (int i = 0; i < indent; i++) {
            buffer.append(' ');
        }
        buffer.append(name);
        if (object instanceof String) {
            buffer.append(": \"");
            buffer.append(TextFormatEscaper.escapeText((String) object));
            buffer.append(Typography.quote);
        } else if (object instanceof ByteString) {
            buffer.append(": \"");
            buffer.append(TextFormatEscaper.escapeBytes((ByteString) object));
            buffer.append(Typography.quote);
        } else if (object instanceof GeneratedMessageLite) {
            buffer.append(" {");
            reflectivePrintWithIndent((GeneratedMessageLite) object, buffer, indent + 2);
            buffer.append("\n");
            for (int i2 = 0; i2 < indent; i2++) {
                buffer.append(' ');
            }
            buffer.append("}");
        } else {
            buffer.append(": ");
            buffer.append(object.toString());
        }
    }

    private static final String camelCaseToSnakeCase(String camelCase) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append("_");
            }
            builder.append(Character.toLowerCase(ch));
        }
        return builder.toString();
    }
}

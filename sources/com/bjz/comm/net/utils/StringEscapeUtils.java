package com.bjz.comm.net.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class StringEscapeUtils {
    public static String unescapeJavaScript(String str) {
        return unescapeJava(str);
    }

    public static void unescapeJavaScript(Writer out, String str) throws IOException {
        unescapeJava(out, str);
    }

    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            throw new UnhandledException(ioe);
        }
    }

    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if (str != null) {
            int sz = str.length();
            StringBuffer unicode = new StringBuffer(4);
            boolean hadSlash = false;
            boolean inUnicode = false;
            for (int i = 0; i < sz; i++) {
                char ch = str.charAt(i);
                if (inUnicode) {
                    unicode.append(ch);
                    if (unicode.length() == 4) {
                        try {
                            out.write((char) Integer.parseInt(unicode.toString(), 16));
                            unicode.setLength(0);
                            inUnicode = false;
                            hadSlash = false;
                        } catch (NumberFormatException nfe) {
                            throw new NestableRuntimeException("Unable to parse unicode value: " + unicode, nfe);
                        }
                    }
                } else if (hadSlash) {
                    hadSlash = false;
                    if (ch == '\"') {
                        out.write(34);
                    } else if (ch == '\'') {
                        out.write(39);
                    } else if (ch == '\\') {
                        out.write(92);
                    } else if (ch == 'b') {
                        out.write(8);
                    } else if (ch == 'f') {
                        out.write(12);
                    } else if (ch == 'n') {
                        out.write(10);
                    } else if (ch == 'r') {
                        out.write(13);
                    } else if (ch == 't') {
                        out.write(9);
                    } else if (ch != 'u') {
                        out.write(ch);
                    } else {
                        inUnicode = true;
                    }
                } else if (ch == '\\') {
                    hadSlash = true;
                } else {
                    out.write(ch);
                }
            }
            if (hadSlash) {
                out.write(92);
            }
        }
    }
}

package com.google.firebase.remoteconfig.internal;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class DefaultsXmlParser {
    private static final String XML_TAG_ENTRY = "entry";
    private static final String XML_TAG_KEY = "key";
    private static final String XML_TAG_VALUE = "value";

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0074 A[Catch:{ IOException | XmlPullParserException -> 0x008e }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0082 A[Catch:{ IOException | XmlPullParserException -> 0x008e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Map<java.lang.String, java.lang.String> getDefaultsFromXml(android.content.Context r12, int r13) {
        /*
            java.lang.String r0 = "FirebaseRemoteConfig"
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            android.content.res.Resources r2 = r12.getResources()     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            if (r2 != 0) goto L_0x0013
            java.lang.String r3 = "Could not find the resources of the current context while trying to set defaults from an XML."
            android.util.Log.e(r0, r3)     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            return r1
        L_0x0013:
            android.content.res.XmlResourceParser r3 = r2.getXml(r13)     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            r4 = 0
            r5 = 0
            r6 = 0
            int r7 = r3.getEventType()     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
        L_0x001e:
            r8 = 1
            if (r7 == r8) goto L_0x008d
            r9 = 2
            if (r7 != r9) goto L_0x002a
            java.lang.String r8 = r3.getName()     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            r4 = r8
            goto L_0x0087
        L_0x002a:
            r9 = 3
            if (r7 != r9) goto L_0x004a
            java.lang.String r8 = r3.getName()     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            java.lang.String r9 = "entry"
            boolean r8 = r8.equals(r9)     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            if (r8 == 0) goto L_0x0048
            if (r5 == 0) goto L_0x0041
            if (r6 == 0) goto L_0x0041
            r1.put(r5, r6)     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            goto L_0x0046
        L_0x0041:
            java.lang.String r8 = "An entry in the defaults XML has an invalid key and/or value tag."
            android.util.Log.w(r0, r8)     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
        L_0x0046:
            r5 = 0
            r6 = 0
        L_0x0048:
            r4 = 0
            goto L_0x0087
        L_0x004a:
            r9 = 4
            if (r7 != r9) goto L_0x0087
            if (r4 == 0) goto L_0x0087
            r9 = -1
            int r10 = r4.hashCode()     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            r11 = 106079(0x19e5f, float:1.48648E-40)
            if (r10 == r11) goto L_0x0069
            r11 = 111972721(0x6ac9171, float:6.4912916E-35)
            if (r10 == r11) goto L_0x005f
        L_0x005e:
            goto L_0x0072
        L_0x005f:
            java.lang.String r10 = "value"
            boolean r10 = r4.equals(r10)     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            if (r10 == 0) goto L_0x005e
            r9 = 1
            goto L_0x0072
        L_0x0069:
            java.lang.String r10 = "key"
            boolean r10 = r4.equals(r10)     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            if (r10 == 0) goto L_0x005e
            r9 = 0
        L_0x0072:
            if (r9 == 0) goto L_0x0082
            if (r9 == r8) goto L_0x007c
            java.lang.String r8 = "Encountered an unexpected tag while parsing the defaults XML."
            android.util.Log.w(r0, r8)     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            goto L_0x0087
        L_0x007c:
            java.lang.String r8 = r3.getText()     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            r6 = r8
            goto L_0x0087
        L_0x0082:
            java.lang.String r8 = r3.getText()     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            r5 = r8
        L_0x0087:
            int r8 = r3.next()     // Catch:{ XmlPullParserException -> 0x0090, IOException -> 0x008e }
            r7 = r8
            goto L_0x001e
        L_0x008d:
            goto L_0x0096
        L_0x008e:
            r2 = move-exception
            goto L_0x0091
        L_0x0090:
            r2 = move-exception
        L_0x0091:
            java.lang.String r3 = "Encountered an error while parsing the defaults XML file."
            android.util.Log.e(r0, r3, r2)
        L_0x0096:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.remoteconfig.internal.DefaultsXmlParser.getDefaultsFromXml(android.content.Context, int):java.util.Map");
    }
}
